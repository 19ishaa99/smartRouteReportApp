package com.example.smartroute.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "users",
        indices = {
                @Index(
                        value = {"email"},
                        unique = true
                )
        }
)
public class User {

    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_CITIZEN = "Citizen";

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String fullName;

    @ColumnInfo(name = "role")
    @NonNull
    private String role;

    @NonNull
    private String email;

    private String phone;

    @NonNull
    private String passwordHash;

    @NonNull
    private String passwordSalt;

    private String profileImageUri;

    private long createdAt;

    public User(
            @NonNull String fullName,
            @NonNull String role,
            @NonNull String email,
            String phone,
            @NonNull String passwordHash,
            @NonNull String passwordSalt,
            String profileImageUri,
            long createdAt
    ) {

        this.fullName = fullName;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.profileImageUri = profileImageUri;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getFullName() {
        return fullName;
    }

    public void setFullName(
            @NonNull String fullName
    ) {
        this.fullName = fullName;
    }

    @NonNull
    public String getRole() {
        return role;
    }

    public void setRole(
            @NonNull String role
    ) {
        this.role = role;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(
            @NonNull String email
    ) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(
            String phone
    ) {
        this.phone = phone;
    }

    @NonNull
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(
            @NonNull String passwordHash
    ) {
        this.passwordHash = passwordHash;
    }

    @NonNull
    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(
            @NonNull String passwordSalt
    ) {
        this.passwordSalt = passwordSalt;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(
            String profileImageUri
    ) {
        this.profileImageUri = profileImageUri;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            long createdAt
    ) {
        this.createdAt = createdAt;
    }

    public boolean isAdmin() {
        return ROLE_ADMIN.equalsIgnoreCase(role);
    }

    public boolean isCitizen() {
        return ROLE_CITIZEN.equalsIgnoreCase(role);
    }
}