package com.echat.chat.controllers;

import com.echat.chat.exception.EntityNotFoundException;
import com.echat.chat.exception.RegisterException;
import com.echat.chat.exception.UserLoginException;
import com.echat.chat.models.requests.LoginRequest;
import com.echat.chat.models.requests.SignUpRequest;
import com.echat.chat.models.responses.ApiResponse;
import com.echat.chat.models.responses.AuthenticationResponse;
import com.echat.chat.repositories.AuthRepository;
import com.echat.chat.repositories.UserRepository;
import com.echat.chat.usecases.LoginUseCase;
import com.echat.chat.usecases.RegisterUseCase;
import com.echat.chat.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1.0/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil,
                          AuthRepository authRepository,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authRepository = authRepository;
        this.userRepository = userRepository;
    }

    @Value("${app.jwt.expiration}")
    private int expiration;


    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@RequestBody SignUpRequest request) {
        try {
            RegisterUseCase useCase = new RegisterUseCase(
                    authRepository,
                    userRepository,
                    passwordEncoder,
                    request
            );
            String response = useCase.execute();
            ApiResponse apiResponse = new ApiResponse(true, response);
            return ResponseEntity.ok(apiResponse);
        } catch (RegisterException e) {
            log.error("Unable to register user, cause: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Unable to register user, cause: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server error, please try again");
        }
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginUseCase useCase = new LoginUseCase(
                    authRepository,
                    userRepository,
                    jwtUtil,
                    authenticationManager,
                    request
            );
            AuthenticationResponse response = useCase.execute(expiration);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException | UserLoginException e) {
            log.error("Unable to login, incorrect email or password, cause: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Unable to login, cause: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server error, please try again");
        }
    }
}