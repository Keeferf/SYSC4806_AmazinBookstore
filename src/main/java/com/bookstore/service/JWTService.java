package com.bookstore.service;

import com.bookstore.model.Role;
import com.bookstore.model.User;
import com.bookstore.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;

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

    /**
     * The base64-encoded secret key used for signing JWTs.
     */
    private String secretkey;

    @Autowired
    UserRepository userRepository;

    /**
     * Constructor that initializes the JWT service with a randomly generated HmacSHA256 secret key.
     * @throws RuntimeException if the HmacSHA256 algorithm is not available
     */
    public JWTService() {

        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretkey = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a JWT token for the specified username with default claims.
     * @param username the username to be included in the token
     * @return a signed JWT token string
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        Optional<User> user = userRepository.findByUsernameIgnoreCase(username);
        if(user.isPresent()) {
            claims.put("role", user.get().getRole());
        } else {
            claims.put("role", Role.CUSTOMER); //default
        }

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 30))
                .and()
                .signWith(getKey())
                .compact();

    }

    /**
     * Retrieves the signing key used for JWT operations.
     * @return SecretKey instance for JWT signing
     */
    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretkey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the username from a JWT token.
     * @param token the JWT token string
     * @return the username stored in the token
     */
    public String extractUserName(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Generic method to extract a specific claim from the token.
     * @param token the JWT token string
     * @param claimResolver function to extract the desired claim
     * @param <T> the type of the claim to be extracted
     * @return the extracted claim value
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    /**
     * Extracts all claims from a JWT token.
     * @param token the JWT token string
     * @return Claims object containing all token claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Validates a token by checking the username and expiration.
     * @param token the JWT token string
     * @param userDetails the UserDetails object to validate against
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Checks if a token has expired.
     * @param token the JWT token string
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from a token.
     * @param token the JWT token string
     * @return Date object representing the token's expiration time
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}

