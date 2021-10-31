package com.echat.chat.usecases;

import com.echat.chat.exception.RegisterException;
import com.echat.chat.models.entities.User;
import com.echat.chat.models.entities.UserAuthentication;
import com.echat.chat.models.requests.SignUpRequest;
import com.echat.chat.repositories.AuthRepository;
import com.echat.chat.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
public class RegisterUseCase {
    private static final Logger log = LoggerFactory.getLogger(RegisterUseCase.class);

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SignUpRequest request;

    public String execute() throws RegisterException {
        UserAuthentication userAuthentication = authRepository.findByEmail(request.getEmail());

        if (userAuthentication != null) {
            log.error("User Already exist with same email: {}", request.getEmail());
            throw new RegisterException("User already exist with same email");
        }

        User user = User
                .builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .build();
        userRepository.save(user);

        UserAuthentication newEntity = UserAuthentication
                .builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .loginAttempts(0)
                .user(user)
                .build();
        authRepository.save(newEntity);

        return "Registered successfully";
    }
}