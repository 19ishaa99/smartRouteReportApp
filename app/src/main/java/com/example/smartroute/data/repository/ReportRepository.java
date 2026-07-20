package com.example.smartroute.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.smartroute.data.dao.ReportDao;
import com.example.smartroute.data.database.AppDatabase;
import com.example.smartroute.data.entity.Report;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReportRepository {

    private final ReportDao reportDao;
    private final ExecutorService executorService;

    public ReportRepository(
            @NonNull Context context
    ) {

        AppDatabase database =
                AppDatabase.getInstance(
                        context.getApplicationContext()
                );

        reportDao =
                database.reportDao();

        executorService =
                Executors.newSingleThreadExecutor();
    }

    /*
     * Insert a new citizen report.
     */
    public void insertReport(
            @NonNull Report report,
            @NonNull InsertCallback callback
    ) {

        executorService.execute(() -> {

            try {

                long reportId =
                        reportDao.insertReport(
                                report
                        );

                callback.onSuccess(
                        reportId
                );

            } catch (Exception exception) {

                callback.onError(
                        getErrorMessage(
                                exception,
                                "Unable to submit the report."
                        )
                );
            }
        });
    }

    /*
     * Update an existing report object.
     */
    public void updateReport(
            @NonNull Report report,
            @NonNull OperationCallback callback
    ) {

        executorService.execute(() -> {

            try {

                int affectedRows =
                        reportDao.updateReport(
                                report
                        );

                if (affectedRows > 0) {

                    callback.onSuccess();

                } else {

                    callback.onError(
                            "Report was not found."
                    );
                }

            } catch (Exception exception) {

                callback.onError(
                        getErrorMessage(
                                exception,
                                "Unable to update the report."
                        )
                );
            }
        });
    }

    /*
     * Observe all reports belonging to one citizen.
     *
     * Room automatically refreshes the list whenever
     * the user's reports change.
     */
    public LiveData<List<Report>> getReportsByUser(
            int userId
    ) {

        return reportDao.observeReportsByUser(
                userId
        );
    }

    /*
     * Retrieve a citizen's reports once.
     *
     * The result is returned using a callback because
     * Room database work must happen off the main thread.
     */
    public void getReportsByUserOnce(
            int userId,
            @NonNull ReportsCallback callback
    ) {

        executorService.execute(() -> {

            try {

                List<Report> reports =
                        reportDao.getReportsByUserSync(
                                userId
                        );

                callback.onSuccess(
                        reports
                );

            } catch (Exception exception) {

                callback.onError(
                        getErrorMessage(
                                exception,
                                "Unable to load reports."
                        )
                );
            }
        });
    }

    /*
     * Observe the citizen's newest report.
     */
    public LiveData<Report> getLatestReport(
            int userId
    ) {

        return reportDao.observeLatestReport(
                userId
        );
    }

    /*
     * Observe one report by its ID.
     */
    public LiveData<Report> getReportById(
            int reportId
    ) {

        return reportDao.observeReportById(
                reportId
        );
    }

    /*
     * Retrieve one report once by its ID.
     */
    public void getReportByIdOnce(
            int reportId,
            @NonNull ReportCallback callback
    ) {

        executorService.execute(() -> {

            try {

                Report report =
                        reportDao.getReportByIdSync(
                                reportId
                        );

                if (report != null) {

                    callback.onSuccess(
                            report
                    );

                } else {

                    callback.onError(
                            "Report was not found."
                    );
                }

            } catch (Exception exception) {

                callback.onError(
                        getErrorMessage(
                                exception,
                                "Unable to load the report."
                        )
                );
            }
        });
    }

    /*
     * Observe the total number of reports belonging
     * to one citizen.
     */
    public LiveData<Integer> getTotalReports(
            int userId
    ) {

        return reportDao.observeTotalReports(
                userId
        );
    }

    /*
     * Observe the number of active reports.
     *
     * Active statuses:
     * Pending
     * Submitted
     * Under Review
     * In Progress
     */
    public LiveData<Integer> getActiveReports(
            int userId
    ) {

        return reportDao.observeActiveReports(
                userId
        );
    }

    /*
     * Observe the number of reports belonging to one
     * citizen that have a particular status.
     */
    public LiveData<Integer> getReportsByStatus(
            int userId,
            @NonNull String status
    ) {

        return reportDao.observeReportsByStatus(
                userId,
                status
        );
    }

    /*
     * Observe all reports in the application.
     *
     * This is mainly intended for administrator use.
     */
    public LiveData<List<Report>> getAllReports() {

        return reportDao.observeAllReports();
    }

    /*
     * Retrieve all reports once.
     */
    public void getAllReportsOnce(
            @NonNull ReportsCallback callback
    ) {

        executorService.execute(() -> {

            try {

                List<Report> reports =
                        reportDao.getAllReportsSync();

                callback.onSuccess(
                        reports
                );

            } catch (Exception exception) {

                callback.onError(
                        getErrorMessage(
                                exception,
                                "Unable to load reports."
                        )
                );
            }
        });
    }

    /*
     * Observe all reports having a particular status.
     *
     * Example:
     * Submitted
     * Approved
     * Rejected
     * Resolved
     */
    public LiveData<List<Report>> observeAllReportsByStatus(
            @NonNull String status
    ) {

        return reportDao.observeAllReportsByStatus(
                status
        );
    }

    /*
     * Retrieve all reports having a particular status once.
     */
    public void getAllReportsByStatusOnce(
            @NonNull String status,
            @NonNull ReportsCallback callback
    ) {

        executorService.execute(() -> {

            try {

                List<Report> reports =
                        reportDao.getReportsByStatusSync(
                                status
                        );

                callback.onSuccess(
                        reports
                );

            } catch (Exception exception) {

                callback.onError(
                        getErrorMessage(
                                exception,
                                "Unable to load reports."
                        )
                );
            }
        });
    }

    /*
     * Observe only reports waiting for administrator approval.
     *
     * Because this returns LiveData, the admin screen will
     * automatically update after a report is approved.
     */
    public LiveData<List<Report>> getSubmittedReports() {

        return reportDao.observeSubmittedReports();
    }

    /*
     * Retrieve submitted reports once.
     *
     * This can be used when you do not want LiveData.
     */
    public void getSubmittedReportsOnce(
            @NonNull ReportsCallback callback
    ) {

        executorService.execute(() -> {

            try {

                List<Report> reports =
                        reportDao.getSubmittedReportsSync();

                callback.onSuccess(
                        reports
                );

            } catch (Exception exception) {

                callback.onError(
                        getErrorMessage(
                                exception,
                                "Unable to load submitted reports."
                        )
                );
            }
        });
    }

    /*
     * Change a report to any status.
     *
     * Examples:
     * Approved
     * Rejected
     * In Progress
     * Resolved
     */
    public void updateReportStatus(
            int reportId,
            @NonNull String newStatus,
            @NonNull OperationCallback callback
    ) {

        executorService.execute(() -> {

            try {

                int affectedRows =
                        reportDao.updateReportStatus(
                                reportId,
                                newStatus,
                                System.currentTimeMillis()
                        );

                if (affectedRows > 0) {

                    callback.onSuccess();

                } else {

                    callback.onError(
                            "Report was not found."
                    );
                }

            } catch (Exception exception) {

                callback.onError(
                        getErrorMessage(
                                exception,
                                "Unable to update the report status."
                        )
                );
            }
        });
    }

    /*
     * Approve one report.
     *
     * This changes its status directly to Approved.
     */
    public void approveReport(
            int reportId,
            @NonNull OperationCallback callback
    ) {

        executorService.execute(() -> {

            try {

                int affectedRows =
                        reportDao.approveReport(
                                reportId,
                                System.currentTimeMillis()
                        );

                if (affectedRows > 0) {

                    callback.onSuccess();

                } else {

                    callback.onError(
                            "Report was not found."
                    );
                }

            } catch (Exception exception) {

                callback.onError(
                        getErrorMessage(
                                exception,
                                "Unable to approve the report."
                        )
                );
            }
        });
    }

    /*
     * Delete a report using the Report object.
     */
    public void deleteReport(
            @NonNull Report report,
            @NonNull OperationCallback callback
    ) {

        executorService.execute(() -> {

            try {

                int affectedRows =
                        reportDao.deleteReport(
                                report
                        );

                if (affectedRows > 0) {

                    callback.onSuccess();

                } else {

                    callback.onError(
                            "Report was not found."
                    );
                }

            } catch (Exception exception) {

                callback.onError(
                        getErrorMessage(
                                exception,
                                "Unable to delete the report."
                        )
                );
            }
        });
    }

    /*
     * Delete a report using its ID.
     */
    public void deleteReport(
            int reportId,
            @NonNull OperationCallback callback
    ) {

        executorService.execute(() -> {

            try {

                int affectedRows =
                        reportDao.deleteReportById(
                                reportId
                        );

                if (affectedRows > 0) {

                    callback.onSuccess();

                } else {

                    callback.onError(
                            "Report was not found."
                    );
                }

            } catch (Exception exception) {

                callback.onError(
                        getErrorMessage(
                                exception,
                                "Unable to delete the report."
                        )
                );
            }
        });
    }

    /*
     * Return a useful database error message.
     */
    @NonNull
    private String getErrorMessage(
            @NonNull Exception exception,
            @NonNull String defaultMessage
    ) {

        String message =
                exception.getMessage();

        if (message == null ||
                message.trim().isEmpty()) {

            return defaultMessage;
        }

        return message;
    }

    /*
     * Callback used when inserting a report.
     */
    public interface InsertCallback {

        void onSuccess(long reportId);

        void onError(String message);
    }

    /*
     * Callback used when returning several reports.
     */
    public interface ReportsCallback {

        void onSuccess(
                List<Report> reports
        );

        void onError(
                String message
        );
    }

    /*
     * Callback used when returning one report.
     */
    public interface ReportCallback {

        void onSuccess(
                Report report
        );

        void onError(
                String message
        );
    }

    /*
     * Callback used for update, approval and deletion operations.
     */
    public interface OperationCallback {

        void onSuccess();

        void onError(
                String message
        );
    }

    /*
     * Shut down the repository executor when it is no longer needed.
     *
     * This method is optional. Do not call it if the same repository
     * instance will still be used.
     */
    public void shutdown() {

        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}