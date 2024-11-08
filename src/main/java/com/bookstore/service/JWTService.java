package com.bookstore.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.bookstore.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * JWTService is a Spring service that provides methods for generating and
 * validating JSON Web Tokens (JWT). It uses HMAC256 for token signing.
 * This service initializes the signing algorithm with a key provided through
 * application properties and allows for token generation with claims,
 * issuer information, and expiration settings. It also includes a method for
 * extracting a username claim from a given token.
 */
@Service
public class JWTService {
    @Value("${jwt.algorithm.key}")
    private String key;
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.expiryInSeconds}")
    private long expiryInSeconds;
    private Algorithm algorithm;
    private static final String USERNAME_KEY = "USERNAME";

    /**
     * Initializes the algorithm used for signing JWT tokens using the HMAC256 algorithm
     * with a key specified in the application properties.
     */
    @PostConstruct
    public void postConstruct() {
        algorithm = Algorithm.HMAC256(key);
    }

    /**
     * Generates a JSON Web Token (JWT) for the given user.
     * @param user the user for whom the token is to be generated
     * @return a JWT as a String containing the username claim, expiration time, and issuer information
     */
    public String generateToken(User user) {
        return JWT.create()
                .withClaim(USERNAME_KEY, user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiryInSeconds * 1000))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    /**
     * Extracts the username from the given JWT token.
     * @param token the JWT token from which the username is to be extracted
     * @return the username extracted from the token
     */
    public String getUsername(String token) {
        return JWT.decode(token).getClaim(USERNAME_KEY).asString();
    }

}
