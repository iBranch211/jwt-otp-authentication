package com.mist.mist_backend.database.repository;

import com.mist.mist_backend.database.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Token repository to access the database
 */
public interface TokenRepository extends JpaRepository<Token, Integer> {

    /**
     * Function that retrieves all the valid token of a specific user
     *
     * @param userId the user id
     * @return the token list
     */
    @Query(value = """
            select token from Token token inner join User user\s
            on token.user.id = user.id\s
            where user.id = :id and (token.expired = false or token.revoked = false)\s
            """)
    List<Token> findAllValidTokenByUser(Integer userId);

    /**
     * Function that finds a specific token
     *
     * @param token the token
     * @return the retrieved token
     */
    Optional<Token> findByToken(String token);
}
