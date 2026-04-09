package world.ezra.loan_management.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Alex Kiburu
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        if ("ezra".equals(username)) {
            String encodedPassword = "$2a$10$OfYAeGvTzjwjC8HFDfB5NOEzq2CjYWUrHaHRrqEWcUIlP33CCldCa"; // BCrypt("ezra")
            return User.builder()
                    .username("ezra")
                    .password(encodedPassword)
                    .roles("USER")
                    .build();
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }
}