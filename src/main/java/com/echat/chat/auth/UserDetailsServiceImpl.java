package com.echat.chat.auth;

import com.echat.chat.models.entities.UserAuthentication;
import com.echat.chat.repositories.AuthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final AuthRepository authRepository;

    @Autowired
    public UserDetailsServiceImpl(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAuthentication userAuthentication = authRepository.findByEmail(email);
        if (userAuthentication == null) {
            log.error("User not found for email: {}", email);
            throw new UsernameNotFoundException("User not found");
        }

        log.info("User found for email: {} and with id : {}", email, userAuthentication.getId());
        return new UserDetailsImpl(
                userAuthentication.getId(),
                userAuthentication.getEmail(),
                userAuthentication.getPassword()
        );
    }
}