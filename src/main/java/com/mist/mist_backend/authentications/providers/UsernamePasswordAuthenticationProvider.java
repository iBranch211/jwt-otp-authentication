package com.mist.mist_backend.authentications.providers;

import com.mist.mist_backend.authentications.UsernamePasswordAuthentication;
import com.mist.mist_backend.database.entities.User;
import com.mist.mist_backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Username/password authentication logic
 */
@Component
@RequiredArgsConstructor
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Function that decides to authenticate the user
     *
     * @param authentication the authentication contract
     * @return the authentication contract
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        UserDetails userDetails = userService.loadUserByUsername(username);

        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            User user = (User) userDetails;

            userService.renewUserOtp(user);

            return new UsernamePasswordAuthentication(userDetails.getUsername(), userDetails.getPassword());
        } else {
            throw new BadCredentialsException("Bad credentials!");
        }
    }

    /**
     * Function that checks if the authentication is supported by our logic
     *
     * @param authentication the authentication class
     * @return true if the authentication is supported, false otherwise
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthentication.class);
    }
}
