package com.echat.chat.controllers;

import com.echat.chat.models.responses.SearchUserResponse;
import com.echat.chat.repositories.UserRepository;
import com.echat.chat.usecases.SearchUserByUsernameUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1.0/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/searchUserByUserName")
    private ResponseEntity<?> searchUserByUserName(@RequestParam String username) {
        try {
            SearchUserByUsernameUseCase useCase = new SearchUserByUsernameUseCase(userRepository, username);
            SearchUserResponse response = useCase.execute();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Unable to search user by username : {}, cause: {}", username, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server error, please try again");
        }
    }
}