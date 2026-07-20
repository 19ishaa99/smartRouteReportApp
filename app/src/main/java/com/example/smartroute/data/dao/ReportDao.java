package com.example.smartroute.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartroute.data.entity.Report;

import java.util.List;

@Dao
public interface ReportDao {

    /*
     * Insert a new report.
     *
     * ABORT stops the insert if a database constraint is violated.
     *
     * Returns the generated report ID.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertReport(Report report);

    /*
     * Update all editable fields of an existing report.
     *
     * Returns the number of affected rows.
     */
    @Update
    int updateReport(Report report);

    /*
     * Delete a report object.
     *
     * Returns the number of affected rows.
     */
    @Delete
    int deleteReport(Report report);

    /*
     * Observe every report submitted by one user.
     *
     * Room automatically updates the result whenever the user's
     * reports change. The newest report appears first.
     */
    @Query(
            "SELECT * FROM reports " +
                    "WHERE userId = :userId " +
                    "ORDER BY createdAt DESC"
    )
    LiveData<List<Report>> observeReportsByUser(
            int userId
    );

    /*
     * Retrieve a user's reports immediately.
     *
     * This method must be called from a background thread.
     */
    @Query(
            "SELECT * FROM reports " +
                    "WHERE userId = :userId " +
                    "ORDER BY createdAt DESC"
    )
    List<Report> getReportsByUserSync(
            int userId
    );

    /*
     * Observe the newest report submitted by one user.
     */
    @Query(
            "SELECT * FROM reports " +
                    "WHERE userId = :userId " +
                    "ORDER BY createdAt DESC " +
                    "LIMIT 1"
    )
    LiveData<Report> observeLatestReport(
            int userId
    );

    /*
     * Observe one report by its ID.
     */
    @Query(
            "SELECT * FROM reports " +
                    "WHERE id = :reportId " +
                    "LIMIT 1"
    )
    LiveData<Report> observeReportById(
            int reportId
    );

    /*
     * Retrieve one report immediately by its ID.
     *
     * This method must be called from a background thread.
     */
    @Query(
            "SELECT * FROM reports " +
                    "WHERE id = :reportId " +
                    "LIMIT 1"
    )
    Report getReportByIdSync(
            int reportId
    );

    /*
     * Observe the total number of reports submitted by one user.
     */
    @Query(
            "SELECT COUNT(*) FROM reports " +
                    "WHERE userId = :userId"
    )
    LiveData<Integer> observeTotalReports(
            int userId
    );

    /*
     * Observe the number of a user's reports with a specific status.
     *
     * The comparison is case-insensitive.
     */
    @Query(
            "SELECT COUNT(*) FROM reports " +
                    "WHERE userId = :userId " +
                    "AND LOWER(status) = LOWER(:status)"
    )
    LiveData<Integer> observeReportsByStatus(
            int userId,
            String status
    );

    /*
     * Observe reports that are still active.
     *
     * Active statuses include:
     * - Pending
     * - Submitted
     * - Under Review
     * - In Progress
     */
    @Query(
            "SELECT COUNT(*) FROM reports " +
                    "WHERE userId = :userId " +
                    "AND (" +
                    "LOWER(status) = 'pending' " +
                    "OR LOWER(status) = 'submitted' " +
                    "OR LOWER(status) = 'under review' " +
                    "OR LOWER(status) = 'in progress'" +
                    ")"
    )
    LiveData<Integer> observeActiveReports(
            int userId
    );

    /*
     * Retrieve all reports in the system.
     *
     * This may later be used by the administrator to view
     * submitted, approved, rejected, or resolved reports.
     *
     * Call this method from a background thread.
     */
    @Query(
            "SELECT * FROM reports " +
                    "ORDER BY createdAt DESC"
    )
    List<Report> getAllReportsSync();

    /*
     * Observe all reports in the system.
     *
     * Room automatically updates the list whenever any report changes.
     */
    @Query(
            "SELECT * FROM reports " +
                    "ORDER BY createdAt DESC"
    )
    LiveData<List<Report>> observeAllReports();

    /*
     * Retrieve all reports with a particular status.
     *
     * This is used by the administrator to retrieve reports
     * whose status is Submitted.
     *
     * Call this method from a background thread.
     */
    @Query(
            "SELECT * FROM reports " +
                    "WHERE LOWER(status) = LOWER(:status) " +
                    "ORDER BY createdAt DESC"
    )
    List<Report> getReportsByStatusSync(
            String status
    );

    /*
     * Observe all reports with a particular status.
     *
     * For example, the administrator can observe all reports
     * whose status is Submitted.
     */
    @Query(
            "SELECT * FROM reports " +
                    "WHERE LOWER(status) = LOWER(:status) " +
                    "ORDER BY createdAt DESC"
    )
    LiveData<List<Report>> observeAllReportsByStatus(
            String status
    );

    /*
     * Retrieve all submitted reports for the administrator.
     *
     * This is a convenient method specifically for the
     * admin approval page.
     *
     * Call this method from a background thread.
     */
    @Query(
            "SELECT * FROM reports " +
                    "WHERE LOWER(status) = 'submitted' " +
                    "ORDER BY createdAt DESC"
    )
    List<Report> getSubmittedReportsSync();

    /*
     * Observe all submitted reports.
     *
     * The admin page will automatically update when:
     * - a citizen submits a new report;
     * - an admin approves a report;
     * - a submitted report changes status.
     */
    @Query(
            "SELECT * FROM reports " +
                    "WHERE LOWER(status) = 'submitted' " +
                    "ORDER BY createdAt DESC"
    )
    LiveData<List<Report>> observeSubmittedReports();

    /*
     * Update a report's status.
     *
     * updatedAt stores the time at which the status was changed.
     *
     * Returns the number of affected rows:
     * - 1 means the report was updated;
     * - 0 means no matching report was found.
     */
    @Query(
            "UPDATE reports SET " +
                    "status = :newStatus, " +
                    "updatedAt = :updatedAt " +
                    "WHERE id = :reportId"
    )
    int updateReportStatus(
            int reportId,
            String newStatus,
            long updatedAt
    );

    /*
     * Approve one report.
     *
     * This method directly changes the report status to Approved.
     *
     * Returns the number of affected rows.
     */
    @Query(
            "UPDATE reports SET " +
                    "status = 'Approved', " +
                    "updatedAt = :updatedAt " +
                    "WHERE id = :reportId"
    )
    int approveReport(
            int reportId,
            long updatedAt
    );

    /*
     * Delete one report by its ID.
     *
     * Returns the number of affected rows.
     */
    @Query(
            "DELETE FROM reports " +
                    "WHERE id = :reportId"
    )
    int deleteReportById(
            int reportId
    );
}