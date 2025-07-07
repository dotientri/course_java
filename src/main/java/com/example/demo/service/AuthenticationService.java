package com.example.demo.service;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.AuthenticationResponse;
import com.example.demo.dto.response.IntrospectResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.InvalidatedToken;
import com.example.demo.entity.User;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.InvalidatedTokenRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
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

    // PHƯƠNG THỨC ĐƯỢC THÊM LẠI ĐỂ SỬA LỖI
    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false); // isLogout = false, chỉ kiểm tra tính hợp lệ
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    public UserResponse createUser(UserCreationRequest request) {
        // 1. Kiểm tra username và email đã tồn tại
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        // 2. Map DTO sang Entity và mã hóa mật khẩu
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 3. THAY ĐỔI: Tạo mã OTP 6 chữ số
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(999999)); // Tạo số ngẫu nhiên 6 chữ số

        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now()); // Ghi lại thời gian tạo
        user.setEmailVerified(false); // Đặt trạng thái chưa xác thực

        // 4. Gán vai trò (Role) mặc định
        com.example.demo.entity.Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION, "Default role USER not found in database"));
        user.setRoles(Set.of(userRole));

        // 5. Lưu user vào database
        User savedUser = userRepository.save(user);

        // 6. Tự động tạo giỏ hàng (Cart) mới
        Cart newCart = new Cart();
        newCart.setUser(savedUser);
        cartRepository.save(newCart);
        log.info("Cart created for user {}", savedUser.getUsername());

        // 7. THAY ĐỔI: Gửi email chứa mã OTP
        emailService.sendVerificationOtp(savedUser.getEmail(), otp);
        log.info("User {} created and verification OTP sent.", savedUser.getUsername());

        return userMapper.toUserResponse(savedUser);
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

    // =================================================================
    // CHỨC NĂNG MỚI: GỬI LẠI MÃ OTP
    // =================================================================
    @Transactional
    public void resendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Để chống spam, chỉ cho phép gửi lại OTP sau 60 giây
        if (user.getOtpGeneratedTime() != null &&
                Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).toSeconds() < 60) {
            throw new AppException(ErrorCode.OTP_COOLDOWN);
        }

        // Tạo mã OTP mới
        SecureRandom random = new SecureRandom();
        String newOtp = String.format("%06d", random.nextInt(1_000_000));

        user.setOtp(newOtp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);

        // Logic thông minh:
        // - Nếu tài khoản chưa xác thực -> gửi email xác thực.
        // - Nếu tài khoản đã xác thực (trường hợp quên mật khẩu) -> gửi email đặt lại mật khẩu.
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

    // --- LOGIC MỚI CHO VIỆC QUÊN MẬT KHẨU ---

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(999999));

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

    // --- CÁC PHƯƠNG THỨC HỖ TRỢ TOKEN ĐƯỢC THÊM LẠI ---

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signedToken = verifyToken(request.getToken(), true); // isLogout = true, cần token hợp lệ để logout
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

        // Khi logout, không cần check token đã bị logout chưa
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
                .jwtID(UUID.randomUUID().toString()) // Quan trọng cho việc logout
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