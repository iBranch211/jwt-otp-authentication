package com.mist.mist_backend.authentications;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Class that represents the authentication with username and password,
 * in out case email and code
 */
public class OtpAuthentication extends UsernamePasswordAuthenticationToken {

    /**
     * Constructor of the authentication method with username and password
     * This constructor will not authenticate the principal because it doesn't have the granted authorities
     *
     * @param principal   the principal
     * @param credentials the credentials, in our case none
     */
    public OtpAuthentication(Object principal, Object credentials) {
        super(principal, credentials);
    }

    /**
     * Constructor of the authentication method with username and password
     * This constructor will authenticate the principal
     *
     * @param principal   the principal
     * @param credentials the credentials, in our case none
     * @param authorities the granted authorities, in out case the user roles
     */
    public OtpAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
