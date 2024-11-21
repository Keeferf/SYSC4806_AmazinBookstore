package com.bookstore.dto;

import jakarta.validation.constraints.*;

/**
 * Data Transfer Object (DTO) that represents the user registration request body.
 * Contains all necessary information for creating a new user account including
 * username, password, email, and personal details. All fields are validated
 * using Jakarta validation constraints to ensure data integrity.
 */
public class RegistrationDTO {
    @NotNull
    @NotBlank
    //@Size(min = 2, max = 50)
    private String username;
    @NotNull
    @NotBlank
    //@Size(min = 6, max = 32)
    private String password;
    @NotNull
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @NotNull
    private String firstName;
    @NotBlank
    @NotNull
    private String lastName;

    /**
     * Constructs a new RegistrationBody with all required user information.
     *
     * @param username  the desired username for the new account (must not be null or blank)
     * @param password  the password for the new account (must not be null or blank)
     * @param email     the email address for the account (must be a valid email format)
     * @param firstName the user's first name (must not be null or blank)
     * @param lastName  the user's last name (must not be null or blank)
     */
    public RegistrationDTO(String username, String password, String email, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Returns the username specified in the registration request.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password specified in the registration request.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the email address specified in the registration request.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the first name specified in the registration request.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name specified in the registration request.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }
}
