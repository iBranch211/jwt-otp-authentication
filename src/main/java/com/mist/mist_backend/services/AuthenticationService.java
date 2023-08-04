package com.mist.mist_backend.services;

import com.mist.mist_backend.authentications.OtpAuthentication;
import com.mist.mist_backend.authentications.UsernamePasswordAuthentication;
import com.mist.mist_backend.database.entities.Token;
import com.mist.mist_backend.database.entities.User;
import com.mist.mist_backend.database.repository.TokenRepository;
import com.mist.mist_backend.utils.TokenType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;

    /**
     * Function that authenticates the user
     *
     * @param user the user to authenticate
     * @return the authentication contract
     */
    public Authentication authenticate(@NotNull User user) {
        if (user.getOtp().getOtpCode() == null && !user.getPassword().isEmpty()) {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthentication(user.getEmail(), user.getPassword())
            );
        }

        if (user.getPassword() == null && !user.getOtp().getOtpCode().isEmpty()) {
            return authenticationManager.authenticate(
                    new OtpAuthentication(user.getEmail(), user.getOtp().getOtpCode())
            );
        }

        return null;
    }

    /**
     * Function that save the user token to the database
     *
     * @param user     the user
     * @param jwtToken the token
     */
    public void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        tokenRepository.save(token);
    }

    /**
     * Function that revokes all user tokens
     *
     * @param user the user
     */
    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
