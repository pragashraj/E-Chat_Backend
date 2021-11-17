package com.echat.chat.usecases;

import com.echat.chat.models.entities.User;
import com.echat.chat.models.responses.SearchUserResponse;
import com.echat.chat.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class SearchUserByUsernameUseCase {
    private static final Logger log = LoggerFactory.getLogger(SearchUserByUsernameUseCase.class);

    private final UserRepository userRepository;
    private final String username;

    public SearchUserResponse execute() {
        List<User> users = userRepository.findAllByUsernameContaining(username);

        log.info("No of users found : {}, for username: {}", users.size(), username);

        return new SearchUserResponse(
                users.size(),
                users.stream().map(user -> new SearchUserResponse.User(
                        user.getId(),
                        user.getEmail(),
                        user.getUsername()
                )).collect(Collectors.toList())
        );
    }
}