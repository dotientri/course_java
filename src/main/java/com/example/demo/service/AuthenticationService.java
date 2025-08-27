package com.example.demo.service;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.AuthenticationResponse;
import com.example.demo.dto.response.IntrospectResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.InvalidatedToken;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.InvalidatedTokenRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {

    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    RoleRepository roleRepository;
    CartRepository cartRepository;
    EmailService emailService;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${google.oauth2.client-id}")
    private String googleClientId;

    @Transactional
    public AuthenticationResponse loginWithGoogle(GoogleLoginRequest request) {
        GoogleIdToken.Payload payload = verifyGoogleIdToken(request.getIdToken());

        // Use a flag to track if the user is new.
        // AtomicBoolean is used because its value can be changed from within a lambda.
        final var isNewUser = new java.util.concurrent.atomic.AtomicBoolean(false);

        User user = userRepository.findByEmail(payload.getEmail())
                .orElseGet(() -> {
                    // Mark this as a new user
                    isNewUser.set(true);
                    // Call the existing method to create the user
                    return createUserFromGoogle(payload);
                });

        // After getting the user (either old or new), check the isNewUser flag
        if (isNewUser.get()) {
            // If it's a new user, send the welcome email
            String fullName = (String) payload.get("name");
            emailService.sendWelcomeEmailForGoogleUser(user.getEmail(), fullName);
        }

        // Generate a token and return the response as usual
        String token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    private GoogleIdToken.Payload verifyGoogleIdToken(String idTokenString) {
        log.info("Verifying token with Google Client ID: '{}'", googleClientId);

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                return idToken.getPayload();
            } else {
                throw new AppException(ErrorCode.UNAUTHENTICATED, "Invalid Google ID Token.");
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("Error verifying Google ID Token", e);
            throw new AppException(ErrorCode.UNAUTHENTICATED, "Token verification failed.");
        }
    }

    private User createUserFromGoogle(GoogleIdToken.Payload payload) {
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("FATAL: USER role not found."));

        String baseUsername = payload.getEmail().split("@")[0].replaceAll("[^a-zA-Z0-9]", "");
        String username = baseUsername;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter++;
        }

        String fullName = (String) payload.get("name");
        String firstName = "";
        String lastName = "";
        if (fullName != null && !fullName.trim().isEmpty()) {
            String[] nameParts = fullName.split("\\s+", 2);
            firstName = nameParts[0];
            if (nameParts.length > 1) {
                lastName = nameParts[1];
            }
        }

        User newUser = User.builder()
                .email(payload.getEmail())
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .password(passwordEncoder.encode(java.util.UUID.randomUUID().toString()))
                .emailVerified(true)
                .roles(new HashSet<>(Collections.singletonList(userRole)))
                .build();

        User savedUser = userRepository.save(newUser);
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);
        return savedUser;
    }

    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    @Transactional
    public UserResponse createUser(UserCreationRequest request) {
        Optional<User> userByEmailOpt = userRepository.findByEmail(request.getEmail());

        if (userByEmailOpt.isPresent()) {
            User existingUser = userByEmailOpt.get();
            if (existingUser.isEmailVerified()) {
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }

            userRepository.findByUsername(request.getUsername()).ifPresent(userWithSameUsername -> {
                if (!userWithSameUsername.getId().equals(existingUser.getId())) {
                    throw new AppException(ErrorCode.USER_EXISTED);
                }
            });

            log.info("Email {} exists but is not verified. Overwriting registration info for user ID: {}",
                    request.getEmail(), existingUser.getId());

            existingUser.setUsername(request.getUsername());
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));

            String otp = generateOtp();
            existingUser.setOtp(otp);
            existingUser.setOtpGeneratedTime(LocalDateTime.now());

            User savedUser = userRepository.save(existingUser);
            emailService.sendVerificationOtp(savedUser.getEmail(), otp);

            return userMapper.toUserResponse(savedUser);
        } else {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new AppException(ErrorCode.USER_EXISTED);
            }

            log.info("Email {} is new. Creating a new user.", request.getEmail());

            User newUser = userMapper.toUser(request);
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));

            String otp = generateOtp();
            newUser.setOtp(otp);
            newUser.setOtpGeneratedTime(LocalDateTime.now());
            newUser.setEmailVerified(false);

            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION, "Default role USER not found in database"));
            newUser.setRoles(Set.of(userRole));

            User savedUser = userRepository.save(newUser);

            Cart newCart = new Cart();
            newCart.setUser(savedUser);
            cartRepository.save(newCart);
            log.info("Cart created for user {}", savedUser.getUsername());

            emailService.sendVerificationOtp(savedUser.getEmail(), otp);

            return userMapper.toUserResponse(savedUser);
        }
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1_000_000));
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!user.isEmailVerified()) {
            throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    @Transactional
    public void resendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getOtpGeneratedTime() != null &&
                Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).toSeconds() < 60) {
            throw new AppException(ErrorCode.OTP_COOLDOWN);
        }

        String newOtp = generateOtp();
        user.setOtp(newOtp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);

        if (!user.isEmailVerified()) {
            emailService.sendVerificationOtp(email, newOtp);
            log.info("Resent verification OTP to {}", email);
        } else {
            emailService.sendPasswordResetOtp(email, newOtp);
            log.info("Resent password reset OTP to {}", email);
        }
    }

    public void verifyOtp(OtpVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        if (Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).toMinutes() > 5) {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }

        user.setEmailVerified(true);
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        userRepository.save(user);
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendPasswordResetOtp(email, otp);
        log.info("Password reset OTP sent to {}", email);
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }
        if (Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).toMinutes() > 5) {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        userRepository.save(user);
        log.info("Password for user {} has been reset.", user.getUsername());
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signedToken = verifyToken(request.getToken(), true);
        String jit = signedToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedToken.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
        invalidatedTokenRepository.save(invalidatedToken);
    }

    private SignedJWT verifyToken(String token, boolean isLogout) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (isLogout) return signedJWT;

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("coursejava.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> stringJoiner.add("ROLE_" + role.getName()));
        }
        return stringJoiner.toString();
    }
}