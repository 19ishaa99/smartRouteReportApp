package com.example.smartroute.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "reports",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index("userId")
        }
)
public class Report {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int userId;

    @NonNull
    private String category;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    private String location;

    private String imageUri;

    @NonNull
    private String urgency;

    @NonNull
    private String status;

    private long createdAt;

    private long updatedAt;

    public Report(
            int userId,
            @NonNull String category,
            @NonNull String title,
            @NonNull String description,
            @NonNull String location,
            String imageUri,
            @NonNull String urgency,
            @NonNull String status,
            long createdAt,
            long updatedAt
    ) {
        this.userId = userId;
        this.category = category;
        this.title = title;
        this.description = description;
        this.location = location;
        this.imageUri = imageUri;
        this.urgency = urgency;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @NonNull
    public String getCategory() {
        return category;
    }

    public void setCategory(@NonNull String category) {
        this.category = category;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    @NonNull
    public String getLocation() {
        return location;
    }

    public void setLocation(@NonNull String location) {
        this.location = location;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    @NonNull
    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(@NonNull String urgency) {
        this.urgency = urgency;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}