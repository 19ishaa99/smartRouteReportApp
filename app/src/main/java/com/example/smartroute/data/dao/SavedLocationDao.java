package com.example.smartroute.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.smartroute.data.entity.SavedLocation;

import java.util.List;

@Dao
public interface SavedLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertLocation(
            SavedLocation location
    );

    @Delete
    void deleteLocation(
            SavedLocation location
    );

    @Query(
            "SELECT * FROM saved_locations " +
                    "WHERE userId = :userId " +
                    "ORDER BY createdAt DESC"
    )
    LiveData<List<SavedLocation>> observeLocationsByUser(
            int userId
    );

    @Query(
            "SELECT * FROM saved_locations " +
                    "WHERE userId = :userId " +
                    "ORDER BY createdAt DESC " +
                    "LIMIT 1"
    )
    LiveData<SavedLocation> observeLatestLocation(
            int userId
    );

    @Query(
            "SELECT * FROM saved_locations " +
                    "WHERE id = :locationId " +
                    "LIMIT 1"
    )
    LiveData<SavedLocation> observeLocationById(
            int locationId
    );

    @Query(
            "DELETE FROM saved_locations " +
                    "WHERE id = :locationId"
    )
    void deleteLocationById(
            int locationId
    );
}