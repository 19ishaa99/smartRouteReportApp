package com.example.smartroute.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartroute.R;
import com.example.smartroute.data.entity.Report;
import com.example.smartroute.data.entity.SavedLocation;
import com.example.smartroute.data.entity.User;
import com.example.smartroute.data.repository.ReportRepository;
import com.example.smartroute.data.repository.SavedLocationRepository;
import com.example.smartroute.data.repository.UserRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final String SESSION_NAME =
            "smart_route_session";

    private static final String KEY_USER_ID =
            "user_id";

    /*
     * Navigation cards
     */
    private View cardReportIssue;
    private View cardLocation;
    private View cardMyReports;

    /*
     * Header
     */
    private TextView txtGreeting;
    private TextView txtHomeLocation;

    /*
     * Statistics
     */
    private TextView txtTotalReports;
    private TextView txtPendingReports;
    private TextView txtResolvedReports;

    /*
     * Recent activity
     */
    private View cardRecentActivity;
    private View layoutNoRecentActivity;

    private TextView txtRecentTitle;
    private TextView txtRecentDetails;
    private TextView txtRecentStatus;

    /*
     * Repositories
     */
    private UserRepository userRepository;
    private ReportRepository reportRepository;
    private SavedLocationRepository locationRepository;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(
                R.layout.fragment_home,
                container,
                false
        );

        initializeViews(view);

        initializeRepositories();

        setupClickListeners();

        loadHomeData();

        return view;
    }

    private void initializeViews(
            @NonNull View view
    ) {

        cardReportIssue =
                view.findViewById(
                        R.id.cardReportIssue
                );

        cardLocation =
                view.findViewById(
                        R.id.cardLocation
                );

        cardMyReports =
                view.findViewById(
                        R.id.cardMyReports
                );

        txtGreeting =
                view.findViewById(
                        R.id.txtGreeting
                );

        txtHomeLocation =
                view.findViewById(
                        R.id.txtHomeLocation
                );

        txtTotalReports =
                view.findViewById(
                        R.id.txtTotalReports
                );

        txtPendingReports =
                view.findViewById(
                        R.id.txtPendingReports
                );

        txtResolvedReports =
                view.findViewById(
                        R.id.txtResolvedReports
                );

        cardRecentActivity =
                view.findViewById(
                        R.id.cardRecentActivity
                );

        layoutNoRecentActivity =
                view.findViewById(
                        R.id.layoutNoRecentActivity
                );

        txtRecentTitle =
                view.findViewById(
                        R.id.txtRecentTitle
                );

        txtRecentDetails =
                view.findViewById(
                        R.id.txtRecentDetails
                );

        txtRecentStatus =
                view.findViewById(
                        R.id.txtRecentStatus
                );
    }

    private void initializeRepositories() {

        Context applicationContext =
                requireContext()
                        .getApplicationContext();

        userRepository =
                new UserRepository(
                        applicationContext
                );

        reportRepository =
                new ReportRepository(
                        applicationContext
                );

        locationRepository =
                new SavedLocationRepository(
                        applicationContext
                );
    }

    private void setupClickListeners() {

        cardReportIssue.setOnClickListener(
                view -> openFragment(
                        new ReportFragment()
                )
        );

        cardLocation.setOnClickListener(
                view -> openFragment(
                        new LocationFragment()
                )
        );

        cardMyReports.setOnClickListener(
                view -> openFragment(
                        new MyReportsFragment()
                )
        );

        cardRecentActivity.setOnClickListener(
                view -> openFragment(
                        new MyReportsFragment()
                )
        );
    }

    private void loadHomeData() {

        int userId = getLoggedInUserId();

        if (userId == -1) {

            showSessionError();

            return;
        }

        loadUser(userId);

        loadLocation(userId);

        loadReportStatistics(userId);

        loadLatestReport(userId);
    }

    /*
     * Load the logged-in user and display their first name.
     */
    private void loadUser(
            int userId
    ) {

        userRepository
                .getUserById(userId)
                .observe(
                        getViewLifecycleOwner(),
                        this::displayGreeting
                );
    }

    private void displayGreeting(
            @Nullable User user
    ) {

        if (user == null) {

            txtGreeting.setText(
                    "Hello, Citizen!"
            );

            return;
        }

        String fullName =
                user.getFullName();

        if (fullName == null ||
                fullName.trim().isEmpty()) {

            txtGreeting.setText(
                    "Hello, Citizen!"
            );

            return;
        }

        String firstName =
                getFirstName(fullName);

        txtGreeting.setText(
                "Hello, " + firstName + "!"
        );
    }

    private String getFirstName(
            @NonNull String fullName
    ) {

        String cleanedName =
                fullName.trim();

        int firstSpace =
                cleanedName.indexOf(" ");

        if (firstSpace == -1) {
            return cleanedName;
        }

        return cleanedName.substring(
                0,
                firstSpace
        );
    }

    /*
     * Load the newest saved location.
     */
    private void loadLocation(
            int userId
    ) {

        locationRepository
                .getLatestLocation(userId)
                .observe(
                        getViewLifecycleOwner(),
                        this::displayLocation
                );
    }

    private void displayLocation(
            @Nullable SavedLocation location
    ) {

        if (location == null) {

            txtHomeLocation.setText(
                    "No location selected"
            );

            return;
        }

        String description =
                location.getDescription();

        if (description == null ||
                description.trim().isEmpty()) {

            txtHomeLocation.setText(
                    formatCoordinates(
                            location.getLatitude(),
                            location.getLongitude()
                    )
            );

            return;
        }

        txtHomeLocation.setText(
                description
        );
    }

    private String formatCoordinates(
            double latitude,
            double longitude
    ) {

        return String.format(
                Locale.getDefault(),
                "%.5f, %.5f",
                latitude,
                longitude
        );
    }

    /*
     * Observe report numbers.
     */
    private void loadReportStatistics(
            int userId
    ) {

        reportRepository
                .getTotalReports(userId)
                .observe(
                        getViewLifecycleOwner(),
                        count -> txtTotalReports.setText(
                                String.valueOf(
                                        count == null ? 0 : count
                                )
                        )
                );

        reportRepository
                .getActiveReports(userId)
                .observe(
                        getViewLifecycleOwner(),
                        count -> txtPendingReports.setText(
                                String.valueOf(
                                        count == null ? 0 : count
                                )
                        )
                );

        reportRepository
                .getReportsByStatus(
                        userId,
                        "Resolved"
                )
                .observe(
                        getViewLifecycleOwner(),
                        count -> txtResolvedReports.setText(
                                String.valueOf(
                                        count == null ? 0 : count
                                )
                        )
                );
    }

    /*
     * Observe and display the newest report.
     */
    private void loadLatestReport(
            int userId
    ) {

        reportRepository
                .getLatestReport(userId)
                .observe(
                        getViewLifecycleOwner(),
                        this::displayLatestReport
                );
    }

    private void displayLatestReport(
            @Nullable Report report
    ) {

        if (report == null) {

            cardRecentActivity.setVisibility(
                    View.GONE
            );

            layoutNoRecentActivity.setVisibility(
                    View.VISIBLE
            );

            return;
        }

        layoutNoRecentActivity.setVisibility(
                View.GONE
        );

        cardRecentActivity.setVisibility(
                View.VISIBLE
        );

        String title =
                report.getTitle();

        if (title == null ||
                title.trim().isEmpty()) {

            title = report.getCategory();
        }

        if (title == null ||
                title.trim().isEmpty()) {

            title = "Submitted report";
        }

        txtRecentTitle.setText(title);

        txtRecentDetails.setText(
                buildRecentDetails(report)
        );

        String status =
                report.getStatus();

        if (status == null ||
                status.trim().isEmpty()) {

            status = "Submitted";
        }

        txtRecentStatus.setText(status);

        applyStatusAppearance(status);
    }

    private String buildRecentDetails(
            @NonNull Report report
    ) {

        String location =
                report.getLocation();

        if (location == null ||
                location.trim().isEmpty()) {

            location = "Location not provided";
        }

        return location +
                " • " +
                formatReportDate(
                        report.getCreatedAt()
                );
    }

    private String formatReportDate(
            long createdAt
    ) {

        if (createdAt <= 0) {
            return "Recently submitted";
        }

        long currentTime =
                System.currentTimeMillis();

        long difference =
                currentTime - createdAt;

        long minute =
                60L * 1000L;

        long hour =
                60L * minute;

        long day =
                24L * hour;

        if (difference < minute) {
            return "Just now";
        }

        if (difference < hour) {

            long minutes =
                    difference / minute;

            return minutes == 1
                    ? "1 minute ago"
                    : minutes + " minutes ago";
        }

        if (difference < day) {

            long hours =
                    difference / hour;

            return hours == 1
                    ? "1 hour ago"
                    : hours + " hours ago";
        }

        if (difference < 7L * day) {

            long days =
                    difference / day;

            return days == 1
                    ? "Yesterday"
                    : days + " days ago";
        }

        SimpleDateFormat formatter =
                new SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                );

        return formatter.format(
                new Date(createdAt)
        );
    }

    private void applyStatusAppearance(
            @NonNull String status
    ) {

        String normalizedStatus =
                status.trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        switch (normalizedStatus) {

            case "resolved":

                txtRecentStatus.setTextColor(
                        requireContext().getColor(
                                R.color.status_resolved_text
                        )
                );

                txtRecentStatus.setBackgroundResource(
                        R.drawable.status_resolved_background
                );

                break;

            case "rejected":

                txtRecentStatus.setTextColor(
                        requireContext().getColor(
                                R.color.status_rejected_text
                        )
                );

                txtRecentStatus.setBackgroundResource(
                        R.drawable.status_rejected_background
                );

                break;

            case "in progress":

                txtRecentStatus.setTextColor(
                        requireContext().getColor(
                                R.color.status_progress_text
                        )
                );

                txtRecentStatus.setBackgroundResource(
                        R.drawable.status_progress_background
                );

                break;

            default:

                txtRecentStatus.setTextColor(
                        requireContext().getColor(
                                R.color.status_pending_text
                        )
                );

                txtRecentStatus.setBackgroundResource(
                        R.drawable.status_pending_background
                );

                break;
        }
    }

    private int getLoggedInUserId() {

        SharedPreferences preferences =
                requireContext()
                        .getSharedPreferences(
                                SESSION_NAME,
                                Context.MODE_PRIVATE
                        );

        return preferences.getInt(
                KEY_USER_ID,
                -1
        );
    }

    private void showSessionError() {

        txtGreeting.setText(
                "Hello, Citizen!"
        );

        txtHomeLocation.setText(
                "Login required"
        );

        txtTotalReports.setText("0");
        txtPendingReports.setText("0");
        txtResolvedReports.setText("0");

        cardRecentActivity.setVisibility(
                View.GONE
        );

        layoutNoRecentActivity.setVisibility(
                View.VISIBLE
        );

        Toast.makeText(
                requireContext(),
                "Unable to load your account. Please log in again.",
                Toast.LENGTH_LONG
        ).show();
    }

    private void openFragment(
            @NonNull Fragment fragment
    ) {

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.mainFragmentContainer,
                        fragment
                )
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        cardReportIssue = null;
        cardLocation = null;
        cardMyReports = null;

        txtGreeting = null;
        txtHomeLocation = null;

        txtTotalReports = null;
        txtPendingReports = null;
        txtResolvedReports = null;

        cardRecentActivity = null;
        layoutNoRecentActivity = null;

        txtRecentTitle = null;
        txtRecentDetails = null;
        txtRecentStatus = null;
    }
}