package com.example.smartroute.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartroute.R;
import com.example.smartroute.adapters.AdminReportAdapter;
import com.example.smartroute.data.entity.Report;
import com.example.smartroute.data.repository.ReportRepository;

import java.util.Collections;
import java.util.List;

public class AdminReportsFragment extends Fragment {

    private RecyclerView recyclerAdminReports;
    private ProgressBar progressAdminReports;
    private TextView txtNoReports;
    private TextView txtAdminReportCount;

    private AdminReportAdapter adminReportAdapter;
    private ReportRepository reportRepository;

    public AdminReportsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_admin_reports,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        initializeRepository();
        setupRecyclerView();
        observeSubmittedReports();
    }

    private void initializeViews(@NonNull View view) {

        recyclerAdminReports =
                view.findViewById(R.id.recyclerAdminReports);

        progressAdminReports =
                view.findViewById(R.id.progressAdminReports);

        txtNoReports =
                view.findViewById(R.id.txtNoReports);

        txtAdminReportCount =
                view.findViewById(R.id.txtAdminReportCount);
    }

    private void initializeRepository() {

        reportRepository = new ReportRepository(
                requireContext().getApplicationContext()
        );
    }

    private void setupRecyclerView() {

        adminReportAdapter = new AdminReportAdapter(
                this::approveReport
        );

        recyclerAdminReports.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recyclerAdminReports.setHasFixedSize(true);
        recyclerAdminReports.setAdapter(adminReportAdapter);
    }

    private void observeSubmittedReports() {

        showLoading(true);

        reportRepository
                .getSubmittedReports()
                .observe(
                        getViewLifecycleOwner(),
                        reports -> {

                            if (!isAdded() || getView() == null) {
                                return;
                            }

                            showLoading(false);

                            List<Report> safeReports =
                                    reports == null
                                            ? Collections.emptyList()
                                            : reports;

                            adminReportAdapter.setReports(safeReports);

                            updateReportListState(safeReports);
                        }
                );
    }

    private void approveReport(
            @NonNull Report report,
            int position
    ) {

        reportRepository.approveReport(
                report.getId(),
                new ReportRepository.OperationCallback() {

                    @Override
                    public void onSuccess() {

                        runOnUiThread(() ->
                                Toast.makeText(
                                        requireContext(),
                                        "Report approved successfully.",
                                        Toast.LENGTH_SHORT
                                ).show()
                        );
                    }

                    @Override
                    public void onError(String message) {

                        runOnUiThread(() -> {

                            Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();

                            /*
                             * Re-enable the button if approval failed.
                             */
                            if (adminReportAdapter != null) {
                                adminReportAdapter.notifyItemChanged(position);
                            }
                        });
                    }
                }
        );
    }

    private void updateReportListState(
            @NonNull List<Report> reports
    ) {

        int reportCount = reports.size();
        boolean hasReports = reportCount > 0;

        recyclerAdminReports.setVisibility(
                hasReports ? View.VISIBLE : View.GONE
        );

        txtNoReports.setVisibility(
                hasReports ? View.GONE : View.VISIBLE
        );

        if (reportCount == 1) {

            txtAdminReportCount.setText(
                    "1 submitted report waiting for approval"
            );

        } else if (reportCount > 1) {

            txtAdminReportCount.setText(
                    reportCount
                            + " submitted reports waiting for approval"
            );

        } else {

            txtAdminReportCount.setText(
                    "No submitted reports waiting for approval"
            );
        }
    }

    private void showLoading(boolean loading) {

        progressAdminReports.setVisibility(
                loading ? View.VISIBLE : View.GONE
        );

        if (loading) {

            recyclerAdminReports.setVisibility(View.GONE);
            txtNoReports.setVisibility(View.GONE);
        }
    }

    private void runOnUiThread(
            @NonNull Runnable action
    ) {

        if (!isAdded()) {
            return;
        }

        requireActivity().runOnUiThread(() -> {

            if (isAdded() && getView() != null) {
                action.run();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        recyclerAdminReports = null;
        progressAdminReports = null;
        txtNoReports = null;
        txtAdminReportCount = null;
        adminReportAdapter = null;
    }
}