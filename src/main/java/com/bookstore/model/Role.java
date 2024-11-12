package com.bookstore.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the roles that can be assigned to users.
 */
public enum Role {
    CUSTOMER("customer"),
    ADMIN("admin");

    private String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    /**
     * Gets the name of the role.
     * @return the name of the role
     */
    @JsonValue
    public String getRoleName() {
        return roleName;
    }

    /**
     * Gets the Role enum value from the role name.
     * @param roleName the name of the role
     * @return the Role enum value
     */
    @JsonCreator
    public static Role fromRoleName(String roleName) {
        for (Role role : Role.values()) {
            if (role.roleName.equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role name: " + roleName);
    }
}
