package com.example.smartroute.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.smartroute.data.dao.ReportDao;
import com.example.smartroute.data.dao.SavedLocationDao;
import com.example.smartroute.data.dao.UserDao;
import com.example.smartroute.data.entity.Report;
import com.example.smartroute.data.entity.SavedLocation;
import com.example.smartroute.data.entity.User;
import com.example.smartroute.utils.PasswordUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                User.class,
                Report.class,
                SavedLocation.class
        },
        version = 4,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME =
            "smart_route_database.db";

    private static volatile AppDatabase INSTANCE;

    private static final ExecutorService DATABASE_EXECUTOR =
            Executors.newSingleThreadExecutor();

    public abstract UserDao userDao();

    public abstract ReportDao reportDao();

    public abstract SavedLocationDao savedLocationDao();

    /*
     * Existing migration:
     * Creates the saved_locations table.
     */
    private static final Migration MIGRATION_2_3 =
            new Migration(2, 3) {

                @Override
                public void migrate(
                        @NonNull SupportSQLiteDatabase database
                ) {

                    database.execSQL(
                            "CREATE TABLE IF NOT EXISTS `saved_locations` (" +
                                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                    "`userId` INTEGER NOT NULL, " +
                                    "`description` TEXT NOT NULL, " +
                                    "`latitude` REAL NOT NULL, " +
                                    "`longitude` REAL NOT NULL, " +
                                    "`createdAt` INTEGER NOT NULL, " +
                                    "FOREIGN KEY(`userId`) " +
                                    "REFERENCES `users`(`id`) " +
                                    "ON UPDATE NO ACTION " +
                                    "ON DELETE CASCADE)"
                    );

                    database.execSQL(
                            "CREATE INDEX IF NOT EXISTS " +
                                    "`index_saved_locations_userId` " +
                                    "ON `saved_locations` (`userId`)"
                    );
                }
            };

    /*
     * New migration:
     * Adds the role column to existing users.
     *
     * Existing accounts automatically become Citizens.
     */
    private static final Migration MIGRATION_3_4 =
            new Migration(3, 4) {

                @Override
                public void migrate(
                        @NonNull SupportSQLiteDatabase database
                ) {

                    database.execSQL(
                            "ALTER TABLE users " +
                                    "ADD COLUMN role TEXT NOT NULL " +
                                    "DEFAULT 'Citizen'"
                    );
                }
            };

    public static AppDatabase getInstance(
            @NonNull Context context
    ) {

        if (INSTANCE == null) {

            synchronized (AppDatabase.class) {

                if (INSTANCE == null) {

                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DATABASE_NAME
                            )
                            .addMigrations(
                                    MIGRATION_2_3,
                                    MIGRATION_3_4
                            )
                            .build();

                    /*
                     * Insert the default admin after Room has
                     * finished creating or opening the database.
                     */
                    createDefaultAdmin();
                }
            }
        }

        return INSTANCE;
    }

    private static void createDefaultAdmin() {

        DATABASE_EXECUTOR.execute(() -> {

            if (INSTANCE == null) {
                return;
            }

            UserDao userDao =
                    INSTANCE.userDao();

            /*
             * Do not create another admin if one already exists.
             */
            if (userDao.adminExists()) {
                return;
            }

            String plainPassword =
                    "Admin@123";

            /*
             * Use the same password hashing logic used
             * during normal user registration.
             */
            String passwordSalt =
                    PasswordUtils.generateSalt();

            String passwordHash =
                    PasswordUtils.hashPassword(
                            plainPassword,
                            passwordSalt
                    );

            User admin = new User(
                    "System Administrator",
                    User.ROLE_ADMIN,
                    "admin@smartroute.com",
                    "0777000000",
                    passwordHash,
                    passwordSalt,
                    null,
                    System.currentTimeMillis()
            );

            userDao.insertUser(admin);
        });
    }
}