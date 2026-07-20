package com.example.smartroute.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.smartroute.data.dao.SavedLocationDao;
import com.example.smartroute.data.database.AppDatabase;
import com.example.smartroute.data.entity.SavedLocation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SavedLocationRepository {

    private final SavedLocationDao savedLocationDao;
    private final ExecutorService executorService;

    public SavedLocationRepository(
            @NonNull Context context
    ) {

        AppDatabase database =
                AppDatabase.getInstance(
                        context.getApplicationContext()
                );

        savedLocationDao =
                database.savedLocationDao();

        executorService =
                Executors.newSingleThreadExecutor();
    }

    public void insertLocation(
            @NonNull SavedLocation location,
            @NonNull InsertCallback callback
    ) {

        executorService.execute(() -> {

            try {

                long locationId =
                        savedLocationDao.insertLocation(
                                location
                        );

                callback.onSuccess(locationId);

            } catch (Exception exception) {

                callback.onError(
                        "Unable to save the location."
                );
            }
        });
    }

    public LiveData<List<SavedLocation>>
    getLocationsByUser(
            int userId
    ) {

        return savedLocationDao
                .observeLocationsByUser(
                        userId
                );
    }

    public LiveData<SavedLocation>
    getLatestLocation(
            int userId
    ) {

        return savedLocationDao
                .observeLatestLocation(
                        userId
                );
    }

    public void deleteLocation(
            int locationId,
            @NonNull OperationCallback callback
    ) {

        executorService.execute(() -> {

            try {

                savedLocationDao.deleteLocationById(
                        locationId
                );

                callback.onSuccess();

            } catch (Exception exception) {

                callback.onError(
                        "Unable to delete the location."
                );
            }
        });
    }

    public interface InsertCallback {

        void onSuccess(long locationId);

        void onError(String message);
    }

    public interface OperationCallback {

        void onSuccess();

        void onError(String message);
    }
}