package com.mist.mist_backend.services;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Class that contains all otp actions
 */
@Service
@Transactional
@RequiredArgsConstructor
public class OtpService {

    /**
     * Function that generates a custom Otp
     * Generate a random value between 0 and 9999
     *
     * @return the generated code
     */
    public String generateOtp() {
        String code;

        try {
            SecureRandom random = SecureRandom.getInstanceStrong();

            code = String.valueOf(random.nextInt(9000) + 1000);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Problem when generating the otp");
        }

        return code;
    }

    /**
     * Function that checks if the otp is valid
     * The otp duration is 60 sec
     *
     * @param expDate Otp expiration date
     * @return true if the expiration date is larger than today's date
     */
    public boolean checkOtpIsValid(Long expDate) {
        return expDate > System.currentTimeMillis();
    }
}
