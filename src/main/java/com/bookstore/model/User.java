package com.bookstore.model;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;



@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String username;
 
    private String password;

    private String firstName;

    private String lastName;

    private String email;
    private Role role;

    @DBRef
    private List<Checkout> purchases;

    /**
     * Default constructor for User.
     */
    public User() {
    }

    /**
     * Constructor for User with username.
     * @param username the username of the user
     */
    public User(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    /**
     * Gets the unique identifier of the user.
     * @return the unique identifier of the user
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the user.
     * @param id the unique identifier to set for the user
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the username of the user.
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     * @param username the username to set for the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the role of the user.
     * @return the role of the user
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the role of the user.
     * @param role the role to set for the user
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Gets the list of purchases made by the user.
     * @return the list of purchases
     */
    public List<Checkout> getPurchases() {
        return purchases;
    }

    /**
     * Sets the list of purchases made by the user.
     * @param purchases the list of purchases to set
     */
    public void setPurchases(List<Checkout> purchases) {
        this.purchases = purchases;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
