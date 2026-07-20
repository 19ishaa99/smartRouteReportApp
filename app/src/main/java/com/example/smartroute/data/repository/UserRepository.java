package com.example.smartroute.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.smartroute.data.dao.UserDao;
import com.example.smartroute.data.database.AppDatabase;
import com.example.smartroute.data.entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {

    private final UserDao userDao;
    private final ExecutorService executorService;

    public UserRepository(Context context) {

        AppDatabase database =
                AppDatabase.getInstance(
                        context.getApplicationContext()
                );

        userDao = database.userDao();

        executorService =
                Executors.newSingleThreadExecutor();
    }

    /*
     * ==========================
     * REGISTER
     * ==========================
     */

    public void registerUser(
            @NonNull User user,
            @NonNull RegistrationCallback callback
    ) {

        executorService.execute(() -> {

            try {

                boolean alreadyExists =
                        userDao.emailExists(
                                user.getEmail()
                        );

                if (alreadyExists) {

                    callback.onError(
                            "An account with this email already exists"
                    );

                    return;
                }

                long userId =
                        userDao.insertUser(user);

                callback.onSuccess(userId);

            } catch (Exception exception) {

                callback.onError(
                        "Registration failed. Please try again."
                );
            }
        });
    }

    /*
     * ==========================
     * LOGIN
     * ==========================
     */

    public void loginUser(
            @NonNull String email,
            @NonNull LoginCallback callback
    ) {

        executorService.execute(() -> {

            try {

                User user =
                        userDao.getUserByEmail(email);

                if (user == null) {

                    callback.onError(
                            "No account found with this email"
                    );

                    return;
                }

                callback.onSuccess(user);

            } catch (Exception exception) {

                callback.onError(
                        "Login failed. Please try again."
                );
            }
        });
    }

    /*
     * ==========================
     * PROFILE
     * ==========================
     */

    public LiveData<User> getUserById(int userId) {

        return userDao.observeUserById(userId);
    }

    public void updateProfile(
            int userId,
            @NonNull String fullName,
            String phone,
            @NonNull OperationCallback callback
    ) {

        executorService.execute(() -> {

            try {

                userDao.updateProfile(
                        userId,
                        fullName,
                        phone
                );

                callback.onSuccess();

            } catch (Exception exception) {

                callback.onError(
                        "Unable to update profile."
                );
            }
        });
    }

    public void updateProfileImage(
            int userId,
            @NonNull String imageUri,
            @NonNull OperationCallback callback
    ) {

        executorService.execute(() -> {

            try {

                userDao.updateProfileImage(
                        userId,
                        imageUri
                );

                callback.onSuccess();

            } catch (Exception exception) {

                callback.onError(
                        "Unable to update profile photo."
                );
            }
        });
    }

    public void updatePassword(
            int userId,
            @NonNull String passwordHash,
            @NonNull String passwordSalt,
            @NonNull OperationCallback callback
    ) {

        executorService.execute(() -> {

            try {

                userDao.updatePassword(
                        userId,
                        passwordHash,
                        passwordSalt
                );

                callback.onSuccess();

            } catch (Exception exception) {

                callback.onError(
                        "Unable to update password."
                );
            }
        });
    }

    /*
     * ==========================
     * CALLBACKS
     * ==========================
     */

    public interface RegistrationCallback {

        void onSuccess(long userId);

        void onError(String message);
    }

    public interface LoginCallback {

        void onSuccess(User user);

        void onError(String message);
    }

    public interface OperationCallback {

        void onSuccess();

        void onError(String message);
    }
}