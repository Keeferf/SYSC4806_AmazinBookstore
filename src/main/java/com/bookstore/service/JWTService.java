package com.bookstore.service;

import com.bookstore.model.Role;
import com.bookstore.model.User;
import com.bookstore.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.nio.file.Files;
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

    private static final String SECRETKEY_FILE = "jwt_secret.key";
    private String secretKey;

    @Autowired
    UserRepository userRepository;

    @PostConstruct
    public void init() {
        try {
            File keyFile = new File(SECRETKEY_FILE);
            if (keyFile.exists()) {
                secretKey = Files.readString(keyFile.toPath());
            } else {
                KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
                SecretKey key = keyGen.generateKey();
                secretKey = Base64.getEncoder().encodeToString(key.getEncoded());

                Files.writeString(keyFile.toPath(), secretKey);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize JWT secret key", e);
        }
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent()) {
            claims.put("role", user.get().getRole());
            claims.put("userId", user.get().getId());
        } else {
            claims.put("role", Role.CUSTOMER);
        }

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000 * 24))
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
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

