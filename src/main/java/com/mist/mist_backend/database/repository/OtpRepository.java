package com.mist.mist_backend.database.repository;

import com.mist.mist_backend.database.entities.Otp;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Otp repository to access the database
 */
public interface OtpRepository extends JpaRepository<Otp, Integer> {

    /**
     * Function that retrieves the user's otp
     *
     * @param userId the user id
     * @return the user's otp
     */
    @NotNull
    Optional<Otp> findOtpByUserId(@NotNull Integer userId);
}
