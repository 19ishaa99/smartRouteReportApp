package com.example.smartroute.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartroute.data.entity.User;

import java.util.List;

@Dao
public interface UserDao {

    /**
     * Inserts a new user.
     *
     * IGNORE prevents Room from replacing an existing user
     * when a unique value, such as email, already exists.
     *
     * Returns:
     * - the newly created user ID when successful
     * - -1 when the insert is ignored
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertUser(User user);

    /**
     * Updates all fields of an existing user.
     */
    @Update
    int updateUser(User user);

    /**
     * Retrieves a user by email.
     *
     * The comparison is case-insensitive.
     */
    @Query(
            "SELECT * FROM users " +
                    "WHERE LOWER(email) = LOWER(:email) " +
                    "LIMIT 1"
    )
    User getUserByEmail(String email);

    /**
     * Checks whether an email address already exists.
     */
    @Query(
            "SELECT EXISTS(" +
                    "SELECT 1 FROM users " +
                    "WHERE LOWER(email) = LOWER(:email)" +
                    ")"
    )
    boolean emailExists(String email);

    /**
     * Counts users registered with a particular email.
     *
     * This can be used before inserting the default admin.
     */
    @Query(
            "SELECT COUNT(*) FROM users " +
                    "WHERE LOWER(email) = LOWER(:email)"
    )
    int countUsersByEmail(String email);

    /**
     * Checks whether the database already contains an admin.
     */
    @Query(
            "SELECT EXISTS(" +
                    "SELECT 1 FROM users " +
                    "WHERE LOWER(role) = LOWER('Admin')" +
                    ")"
    )
    boolean adminExists();

    /**
     * Retrieves the first administrator account.
     */
    @Query(
            "SELECT * FROM users " +
                    "WHERE LOWER(role) = LOWER('Admin') " +
                    "LIMIT 1"
    )
    User getAdminUser();

    /**
     * Retrieves all users.
     *
     * This can later be used on an admin user-management page.
     */
    @Query(
            "SELECT * FROM users " +
                    "ORDER BY fullName ASC"
    )
    List<User> getAllUsers();

    /**
     * Retrieves all users with a particular role.
     */
    @Query(
            "SELECT * FROM users " +
                    "WHERE LOWER(role) = LOWER(:role) " +
                    "ORDER BY fullName ASC"
    )
    List<User> getUsersByRole(String role);

    /**
     * Retrieves a user immediately by ID.
     *
     * Call this method from a repository background thread.
     */
    @Query(
            "SELECT * FROM users " +
                    "WHERE id = :userId " +
                    "LIMIT 1"
    )
    User getUserByIdSync(int userId);

    /**
     * Observes a user by ID.
     *
     * Room sends a new User object whenever that user's
     * database information changes.
     */
    @Query(
            "SELECT * FROM users " +
                    "WHERE id = :userId " +
                    "LIMIT 1"
    )
    LiveData<User> observeUserById(int userId);

    /**
     * Updates the user's full name and phone number.
     */
    @Query(
            "UPDATE users SET " +
                    "fullName = :fullName, " +
                    "phone = :phone " +
                    "WHERE id = :userId"
    )
    int updateProfile(
            int userId,
            String fullName,
            String phone
    );

    /**
     * Saves the selected profile-image URI.
     */
    @Query(
            "UPDATE users SET " +
                    "profileImageUri = :profileImageUri " +
                    "WHERE id = :userId"
    )
    int updateProfileImage(
            int userId,
            String profileImageUri
    );

    /**
     * Updates the user's password information.
     */
    @Query(
            "UPDATE users SET " +
                    "passwordHash = :passwordHash, " +
                    "passwordSalt = :passwordSalt " +
                    "WHERE id = :userId"
    )
    int updatePassword(
            int userId,
            String passwordHash,
            String passwordSalt
    );

    /**
     * Updates a user's role.
     *
     * This should only be called from an authorized
     * administrator workflow.
     */
    @Query(
            "UPDATE users SET " +
                    "role = :role " +
                    "WHERE id = :userId"
    )
    int updateUserRole(
            int userId,
            String role
    );

    /**
     * Deletes a user by ID.
     */
    @Query(
            "DELETE FROM users " +
                    "WHERE id = :userId"
    )
    int deleteUserById(int userId);
}