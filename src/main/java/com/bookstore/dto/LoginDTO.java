package com.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) that represents the login request body.
 * Contains user credentials (username and password) required for authentication.
 * All fields are validated using Jakarta validation constraints to ensure they are not null or blank.
 */
public class LoginDTO {
    @NotNull
    @NotBlank
    private String username;
    @NotNull
    @NotBlank
    private String password;

    /**
     * Returns the username provided in the login request.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password provided in the login request.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }
}
