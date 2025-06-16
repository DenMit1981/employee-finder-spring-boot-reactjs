package com.empire.employeefinder.security.service;

import com.empire.employeefinder.security.model.CustomUserDetails;
import com.empire.employeefinder.exception.UserNotFoundException;
import com.empire.employeefinder.model.User;
import com.empire.employeefinder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private static final String USER_NOT_FOUND = "User with login %s not found";

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) {
        return new CustomUserDetails(getByLogin(login));
    }

    private User getByLogin(String login) {
        return userRepository.findByEmail(login)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, login)));
    }
}
