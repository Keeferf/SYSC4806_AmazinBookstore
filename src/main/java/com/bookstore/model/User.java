package com.bookstore.model;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String role = "USER";

    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
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
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the user.
     * @param id the unique identifier to set for the user
     */
    public void setId(Long id) {
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
}
