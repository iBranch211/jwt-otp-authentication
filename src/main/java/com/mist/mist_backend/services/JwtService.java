package com.mist.mist_backend.services;

import com.mist.mist_backend.constants.AuthenticationConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Class that contains all jwt token actions
 */
@Service
public class JwtService {

    /**
     * Function that generate a jwt token without extra claims
     *
     * @param userDetails the user details that we want to add
     * @return the jwt token
     */
    public String generateJwtToken(UserDetails userDetails) {
        return generateJwtToken(new HashMap<>(), userDetails);
    }

    /**
     * Function that generate a jwt token with extra claims
     *
     * @param extraClaims the extra claims that we want to add
     * @param userDetails the user details that we want to add
     * @return the jwt token
     */
    public String generateJwtToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, AuthenticationConstants.JWT_TOKEN_LONGEVITY * 1000);
    }

    /**
     * Function that generate a jwt refresh token
     *
     * @param userDetails the user details that we want to add
     * @return the jwt refresh token
     */
    public String generateJwtRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, AuthenticationConstants.JWT_REFRESH_TOKEN_LONGEVITY * 1000);
    }

    /**
     * Function that builds a jwt token
     *
     * @param extraClaims the extra claims that we want to add
     * @param userDetails the user details that we want to add
     * @param expiration  the token expiration time
     * @return the jwt token
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Function that checks if the token is valid by the subject, in out case the user email
     *
     * @param token       the jwt token
     * @param userDetails the user details
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userEmail = extractJwtSubject(token);

        return (userEmail.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Function that checks if the token is expired
     *
     * @param token the jwt token
     * @return true if the token in expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Function that checks if the jwt token is present into the header
     *
     * @param header the request header
     * @return true if token is present, false otherwise
     */
    public boolean isTokenPresent(String header) {
        return header != null && header.startsWith("Bearer ");
    }

    /**
     * Function that extract the subject claim, user email in out case
     *
     * @param token the jwt token
     * @return the jwt subject, in out case the user email
     */
    public String extractJwtSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Function that extract the expiration date
     *
     * @param token the jwt token
     * @return the jwt expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Function that extract a claim of type T
     *
     * @param token          the jwt token
     * @param claimsResolver functional function that takes in input a Claims object and generates T object
     * @param <T>            the type of object that we want to return
     * @return the T object from the functional function
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    /**
     * Function that get all the jwt token claims
     *
     * @param token is the jwt token the we parse
     * @return the jwt claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Function that gets and decodes the jwt secret key
     *
     * @return the key
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(AuthenticationConstants.JWT_TOKEN_SECRET_KEY);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
