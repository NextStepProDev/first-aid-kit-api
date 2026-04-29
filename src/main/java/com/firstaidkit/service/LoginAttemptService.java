package com.firstaidkit.service;

import com.firstaidkit.infrastructure.database.entity.UserEntity;
import com.firstaidkit.infrastructure.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAttemptService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    private final UserRepository userRepository;

    public record LoginAttemptResult(boolean locked, long lockoutMinutes) {}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public LoginAttemptResult handleFailedLogin(String email) {
        UserEntity user = userRepository.findByEmail(email);
        if (user != null) {
            user.incrementFailedAttempts();

            if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setLockedUntil(OffsetDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
                userRepository.save(user);
                log.warn("Account locked due to {} failed attempts: {}", MAX_FAILED_ATTEMPTS, email);
                return new LoginAttemptResult(true, LOCKOUT_DURATION_MINUTES);
            }

            userRepository.save(user);
            log.warn("Failed login attempt {} for user: {}", user.getFailedLoginAttempts(), email);
        }
        return new LoginAttemptResult(false, 0);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resetFailedAttempts(Integer userId) {
        userRepository.findById(userId).ifPresent(user -> {
            if (user.getFailedLoginAttempts() > 0 || user.getLockedUntil() != null) {
                user.resetFailedAttempts();
                userRepository.save(user);
            }
        });
    }
}
