package com.mist.mist_backend.authentications.providers;

import com.mist.mist_backend.authentications.OtpAuthentication;
import com.mist.mist_backend.database.entities.User;
import com.mist.mist_backend.database.repository.UserRepository;
import com.mist.mist_backend.services.OtpService;
import com.mist.mist_backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Otp authentication logic
 */
@Component
@RequiredArgsConstructor
public class OtpAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

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
        Optional<User> dbUser = userRepository.findUserByEmail(username);

        if (dbUser.isPresent()) {
            User user = (User) userDetails;

            if (otpService.checkOtpIsValid(user.getOtp().getExpiresAt())) {
                if (passwordEncoder.matches(password, user.getOtp().getOtpCode())) {
                    return new OtpAuthentication(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                }
            }

            throw new BadCredentialsException("Bad credentials!");
        } else {
            throw new UsernameNotFoundException("Username not found: " + username);
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
        return authentication.equals(OtpAuthentication.class);
    }
}
