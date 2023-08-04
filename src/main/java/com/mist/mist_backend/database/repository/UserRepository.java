package com.mist.mist_backend.database.repository;

import com.mist.mist_backend.database.entities.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User repository to access the database
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Function that retrieves the user
     *
     * @param email the user email
     * @return the user
     */
    @NotNull
    Optional<User> findUserByEmail(@NotNull String email);


}
