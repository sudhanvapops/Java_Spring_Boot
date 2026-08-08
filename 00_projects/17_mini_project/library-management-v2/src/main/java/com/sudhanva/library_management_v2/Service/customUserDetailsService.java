package com.sudhanva.library_management_v2.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.sudhanva.library_management_v2.Model.User;
import com.sudhanva.library_management_v2.exceptions.UserExceptions.UserNotFoundException;
import com.sudhanva.library_management_v2.repo.UserRepo;
import com.sudhanva.library_management_v2.security.UserPrincipal;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) {

        User user = userRepo.findByEmail(email).orElseThrow(
            () -> UserNotFoundException.byEmail(email)
        );

        return new UserPrincipal(user);

    }
    
}
