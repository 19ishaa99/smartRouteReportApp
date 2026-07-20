package com.example.smartroute.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartroute.R;
import com.example.smartroute.adapters.ReportAdapter;
import com.example.smartroute.data.entity.Report;
import com.example.smartroute.data.repository.ReportRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyReportsFragment extends Fragment {

    private TextView txtTotalReports;
    private TextView txtPendingReports;
    private TextView txtResolvedReports;
    private TextView txtEmptyReportsMessage;

    private EditText etSearchReports;

    private RecyclerView recyclerReports;

    private LinearLayout layoutEmptyReports;

    private ReportAdapter reportAdapter;
    private ReportRepository reportRepository;

    private final List<Report> currentReports =
            new ArrayList<>();

    public MyReportsFragment() {
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
                R.layout.fragment_my_reports,
                container,
                false
        );

        initializeViews(view);

        reportRepository =
                new ReportRepository(
                        requireContext()
                                .getApplicationContext()
                );

        setupRecyclerView();
        setupSearch();
        loadReports();

        return view;
    }

    private void initializeViews(
            @NonNull View view
    ) {

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

        txtEmptyReportsMessage =
                view.findViewById(
                        R.id.txtEmptyReportsMessage
                );

        etSearchReports =
                view.findViewById(
                        R.id.etSearchReports
                );

        recyclerReports =
                view.findViewById(
                        R.id.recyclerReports
                );

        layoutEmptyReports =
                view.findViewById(
                        R.id.layoutEmptyReports
                );
    }

    private void setupRecyclerView() {

        reportAdapter =
                new ReportAdapter(
                        this::openReportDetails
                );

        recyclerReports.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        recyclerReports.setHasFixedSize(false);

        recyclerReports.setAdapter(reportAdapter);
    }

    private void loadReports() {

        int userId = getLoggedInUserId();

        if (userId == -1) {

            showEmptyState(
                    "Login session not found. " +
                            "Please sign in again."
            );

            Toast.makeText(
                    requireContext(),
                    "Please sign in again",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        reportRepository
                .getReportsByUser(userId)
                .observe(
                        getViewLifecycleOwner(),
                        reports -> {

                            currentReports.clear();

                            if (reports != null) {

                                currentReports.addAll(
                                        reports
                                );
                            }

                            reportAdapter.setReports(
                                    currentReports
                            );

                            updateSummary(
                                    currentReports
                            );

                            updateListVisibility(
                                    currentReports.isEmpty(),
                                    false
                            );
                        }
                );
    }

    private void setupSearch() {

        etSearchReports.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence text,
                            int start,
                            int count,
                            int after
                    ) {
                        // No action required
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence text,
                            int start,
                            int before,
                            int count
                    ) {

                        String query =
                                text == null
                                        ? ""
                                        : text.toString();

                        reportAdapter.filter(query);

                        boolean noResults =
                                reportAdapter.getItemCount() == 0;

                        boolean searching =
                                !query.trim().isEmpty();

                        updateListVisibility(
                                noResults,
                                searching
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable editable
                    ) {
                        // No action required
                    }
                }
        );
    }

    private void updateSummary(
            @NonNull List<Report> reports
    ) {

        int total = reports.size();
        int pending = 0;
        int resolved = 0;

        for (Report report : reports) {

            String status = report.getStatus();

            if (status == null) {
                continue;
            }

            String normalizedStatus =
                    status.trim()
                            .toLowerCase(Locale.ROOT);

            if (normalizedStatus.equals("resolved")) {

                resolved++;

            } else if (
                    normalizedStatus.equals("submitted") ||
                            normalizedStatus.equals("pending")
            ) {

                pending++;
            }
        }

        txtTotalReports.setText(
                String.valueOf(total)
        );

        txtPendingReports.setText(
                String.valueOf(pending)
        );

        txtResolvedReports.setText(
                String.valueOf(resolved)
        );
    }

    private void updateListVisibility(
            boolean showEmpty,
            boolean searching
    ) {

        if (showEmpty) {

            recyclerReports.setVisibility(
                    View.GONE
            );

            layoutEmptyReports.setVisibility(
                    View.VISIBLE
            );

            if (searching) {

                txtEmptyReportsMessage.setText(
                        "No reports match your search."
                );

            } else {

                txtEmptyReportsMessage.setText(
                        "Reports you submit will appear here."
                );
            }

        } else {

            layoutEmptyReports.setVisibility(
                    View.GONE
            );

            recyclerReports.setVisibility(
                    View.VISIBLE
            );
        }
    }

    private void showEmptyState(
            @NonNull String message
    ) {

        recyclerReports.setVisibility(
                View.GONE
        );

        layoutEmptyReports.setVisibility(
                View.VISIBLE
        );

        txtEmptyReportsMessage.setText(message);
    }

    private void openReportDetails(
            @NonNull Report report
    ) {

        String details =
                "Category: " + report.getCategory() +
                        "\nTitle: " + report.getTitle() +
                        "\nLocation: " + report.getLocation() +
                        "\nUrgency: " + report.getUrgency() +
                        "\nStatus: " + report.getStatus();

        Toast.makeText(
                requireContext(),
                details,
                Toast.LENGTH_LONG
        ).show();
    }

    private int getLoggedInUserId() {

        SharedPreferences preferences =
                requireContext()
                        .getSharedPreferences(
                                "smart_route_session",
                                Context.MODE_PRIVATE
                        );

        return preferences.getInt(
                "user_id",
                -1
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        txtTotalReports = null;
        txtPendingReports = null;
        txtResolvedReports = null;
        txtEmptyReportsMessage = null;

        etSearchReports = null;
        recyclerReports = null;
        layoutEmptyReports = null;

        reportAdapter = null;
    }
}