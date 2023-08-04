package com.mist.mist_backend.constants;

import org.springframework.beans.factory.annotation.Value;

/**
 * Authentication constants
 */
public class AuthenticationConstants {

    // Value for the otp longevity
    public final static Integer OTP_LONGEVITY = 60;
    // Jwt
    public static String JWT_TOKEN_SECRET_KEY = "a578ca941109363dff29882af80d0a4d28754a106ac96dc65c36092399175844";
    public static Long JWT_TOKEN_LONGEVITY = 3600L;
    public static Long JWT_REFRESH_TOKEN_LONGEVITY = 86400L;
}
