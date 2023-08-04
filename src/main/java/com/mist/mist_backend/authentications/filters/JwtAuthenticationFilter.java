package com.mist.mist_backend.authentications.filters;

import com.mist.mist_backend.authentications.UsernamePasswordAuthentication;
import com.mist.mist_backend.database.repository.TokenRepository;
import com.mist.mist_backend.services.JwtService;
import com.mist.mist_backend.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Custom jwt authentication filter class
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final UserService userService;

    /**
     * Function that creates an additional filter that deal with the jwt authentication
     *
     * @param request     the http request
     * @param response    the http response
     * @param filterChain the filter chain
     */
    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (jwtService.isTokenPresent(authHeader)) {
            final String jwtToken = authHeader.substring(7);
            final String userEmail = jwtService.extractJwtSubject(jwtToken);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(userEmail);

                boolean isTokenValid = tokenRepository.findByToken(jwtToken)
                        .map(token -> !token.isExpired() && !token.isRevoked())
                        .orElse(false);

                if (jwtService.isTokenValid(jwtToken, userDetails) && isTokenValid) {
                    UsernamePasswordAuthentication authentication = new UsernamePasswordAuthentication(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );

                    if (authentication.isAuthenticated() && jwtService.isTokenValid(jwtToken, userDetails)) {
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Configures the filter not to be triggered on request for the provided path/s
     *
     * @param request the http request
     * @return true if the path is /login, false otherwise
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/auth/login") ||
                request.getServletPath().equals("/auth/register");
    }
}
