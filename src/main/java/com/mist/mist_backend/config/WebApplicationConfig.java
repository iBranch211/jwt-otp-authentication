package com.mist.mist_backend.config;

import com.mist.mist_backend.authentications.providers.OtpAuthenticationProvider;
import com.mist.mist_backend.authentications.providers.UsernamePasswordAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebApplicationConfig {

    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider,
            OtpAuthenticationProvider otpAuthenticationProvider
    ) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.authenticationProvider(usernamePasswordAuthenticationProvider);
        authenticationManagerBuilder.authenticationProvider(otpAuthenticationProvider);

        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
