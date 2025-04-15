package com.example.demo.configuration;

import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                var roles = new HashSet<String>();
                roles.add(Role.ADMIN.name());
                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
//                        .roles(roles)
                        .build();
                userRepository.save(user);
                log.warn("admin user has been created with default password : admin , please change it !");
            }
            for (int i = 1; i <= 10; i++) {
                String staffUsername = "staff" + i;
                if (userRepository.findByUsername(staffUsername).isEmpty()) {
                    var roles = new HashSet<String>();
                    roles.add(Role.STAFF.name());
                    User user = User.builder()
                            .username(staffUsername)
                            .password(passwordEncoder.encode("staff" + i))
//                            .roles(roles)
                            .build();
                    userRepository.save(user);
                    log.info("Staff user {} has been created with default password: staff{}", staffUsername, i);
                }
            }
        };
    }
}
