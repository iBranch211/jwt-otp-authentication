package com.mist.mist_backend.controllers;

import com.mist.mist_backend.database.entities.Otp;
import com.mist.mist_backend.database.entities.User;
import com.mist.mist_backend.database.repository.UserRepository;
import com.mist.mist_backend.dto.request.LoginUserDto;
import com.mist.mist_backend.dto.request.RegisterUserDto;
import com.mist.mist_backend.dto.response.AuthenticationRes;
import com.mist.mist_backend.services.AuthenticationService;
import com.mist.mist_backend.services.JwtService;
import com.mist.mist_backend.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserService userService;

    @GetMapping(value = "/login")
    public ResponseEntity<AuthenticationRes> login(@Valid LoginUserDto loginUserDto) {
        Authentication authentication = authenticationService.authenticate(
                User.builder()
                        .email(loginUserDto.getEmail())
                        .password(loginUserDto.getPassword())
                        .otp(Otp.builder().otpCode(loginUserDto.getOtp()).build())
                        .build()
        );

        if (authentication == null) {
            throw new BadCredentialsException("Bad credentials!");
        }

        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Optional<User> dbUser = userRepository.findUserByEmail(loginUserDto.getEmail());

            if (dbUser.isPresent()) {
                User currentUser = dbUser.get();
                String jwtToken = jwtService.generateJwtToken(currentUser);
                String jwtRefreshToken = jwtService.generateJwtRefreshToken(currentUser);

                authenticationService.revokeAllUserTokens(currentUser);
                authenticationService.saveUserToken(currentUser, jwtToken);

                return ResponseEntity.ok(AuthenticationRes.builder()
                        .accessToken(jwtToken)
                        .refreshToken(jwtRefreshToken)
                        .build()
                );
            } else {
                throw new BadCredentialsException("User not found!");
            }
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_PARTIAL_CONTENT).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationRes> refresh(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }

        String refreshToken = authHeader.substring(7);
        String userEmail = jwtService.extractJwtSubject(refreshToken);

        if (userEmail != null) {
            User user = userRepository.findUserByEmail(userEmail)
                    .orElseThrow();

            if (jwtService.isTokenValid(refreshToken, user)) {
                String accessToken = jwtService.generateJwtToken(user);

                authenticationService.revokeAllUserTokens(user);
                authenticationService.saveUserToken(user, accessToken);

                AuthenticationRes authResponse = AuthenticationRes.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();

                return ResponseEntity.ok(authResponse);
            } else {
                throw new BadCredentialsException("Token not valid!");
            }
        } else {
            throw new BadCredentialsException("User not found!");
        }
    }

    @PostMapping("/register")
    public void register(@Valid RegisterUserDto registerUserDto) {
        User user = User.builder()
                .firstName(registerUserDto.getFirstName())
                .lastName(registerUserDto.getLastName())
                .email(registerUserDto.getEmail())
                .password(registerUserDto.getPassword())
                .role(registerUserDto.getRole())
                .build();

        userService.register(user);
    }
}