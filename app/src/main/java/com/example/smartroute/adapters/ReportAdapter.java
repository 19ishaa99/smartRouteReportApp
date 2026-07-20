package com.example.smartroute.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartroute.R;
import com.example.smartroute.data.entity.Report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends
        RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private final List<Report> allReports =
            new ArrayList<>();

    private final List<Report> displayedReports =
            new ArrayList<>();

    private final OnReportClickListener listener;

    public ReportAdapter(
            @NonNull OnReportClickListener listener
    ) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_report,
                        parent,
                        false
                );

        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ReportViewHolder holder,
            int position
    ) {

        Report report = displayedReports.get(position);

        holder.txtReportTitle.setText(
                report.getTitle()
        );

        holder.txtReportCategory.setText(
                "Category: " + report.getCategory()
        );

        holder.txtReportLocation.setText(
                "Location: " + report.getLocation()
        );

        holder.txtReportUrgency.setText(
                "Urgency: " + report.getUrgency()
        );

        holder.txtReportStatus.setText(
                report.getStatus()
        );

        holder.txtReportDate.setText(
                "Reported: " +
                        formatDate(report.getCreatedAt())
        );

        setStatusColor(
                holder.txtReportStatus,
                report.getStatus()
        );

        holder.btnViewReport.setOnClickListener(
                view -> listener.onReportClick(report)
        );

        holder.itemView.setOnClickListener(
                view -> listener.onReportClick(report)
        );
    }

    @Override
    public int getItemCount() {

        return displayedReports.size();
    }

    public void setReports(List<Report> reports) {

        allReports.clear();
        displayedReports.clear();

        if (reports != null) {

            allReports.addAll(reports);
            displayedReports.addAll(reports);
        }

        notifyDataSetChanged();
    }

    public void filter(String searchText) {

        displayedReports.clear();

        if (searchText == null ||
                searchText.trim().isEmpty()) {

            displayedReports.addAll(allReports);

        } else {

            String query = searchText
                    .trim()
                    .toLowerCase(Locale.ROOT);

            for (Report report : allReports) {

                if (matchesSearch(report, query)) {

                    displayedReports.add(report);
                }
            }
        }

        notifyDataSetChanged();
    }

    private boolean matchesSearch(
            @NonNull Report report,
            @NonNull String query
    ) {

        return containsIgnoreCase(
                report.getTitle(),
                query
        ) || containsIgnoreCase(
                report.getCategory(),
                query
        ) || containsIgnoreCase(
                report.getLocation(),
                query
        ) || containsIgnoreCase(
                report.getStatus(),
                query
        ) || containsIgnoreCase(
                report.getUrgency(),
                query
        );
    }

    private boolean containsIgnoreCase(
            String value,
            String query
    ) {

        return value != null &&
                value.toLowerCase(Locale.ROOT)
                        .contains(query);
    }

    private String formatDate(long timestamp) {

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "dd MMMM yyyy",
                        Locale.getDefault()
                );

        return dateFormat.format(
                new Date(timestamp)
        );
    }

    private void setStatusColor(
            @NonNull TextView statusView,
            String status
    ) {

        if (status == null) {

            statusView.setTextColor(
                    Color.parseColor("#6B7280")
            );

            return;
        }

        switch (status.toLowerCase(Locale.ROOT)) {

            case "resolved":

                statusView.setTextColor(
                        Color.parseColor("#16A34A")
                );

                break;

            case "in progress":

                statusView.setTextColor(
                        Color.parseColor("#2563EB")
                );

                break;

            case "emergency":

                statusView.setTextColor(
                        Color.parseColor("#DC2626")
                );

                break;

            case "rejected":

                statusView.setTextColor(
                        Color.parseColor("#DC2626")
                );

                break;

            case "under review":

                statusView.setTextColor(
                        Color.parseColor("#7C3AED")
                );

                break;

            case "submitted":
            case "pending":
            default:

                statusView.setTextColor(
                        Color.parseColor("#F59E0B")
                );

                break;
        }
    }

    public interface OnReportClickListener {

        void onReportClick(Report report);
    }

    static class ReportViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView txtReportTitle;
        private final TextView txtReportStatus;
        private final TextView txtReportCategory;
        private final TextView txtReportLocation;
        private final TextView txtReportUrgency;
        private final TextView txtReportDate;

        private final Button btnViewReport;

        public ReportViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            txtReportTitle =
                    itemView.findViewById(
                            R.id.txtReportTitle
                    );

            txtReportStatus =
                    itemView.findViewById(
                            R.id.txtReportStatus
                    );

            txtReportCategory =
                    itemView.findViewById(
                            R.id.txtReportCategory
                    );

            txtReportLocation =
                    itemView.findViewById(
                            R.id.txtReportLocation
                    );

            txtReportUrgency =
                    itemView.findViewById(
                            R.id.txtReportUrgency
                    );

            txtReportDate =
                    itemView.findViewById(
                            R.id.txtReportDate
                    );

            btnViewReport =
                    itemView.findViewById(
                            R.id.btnViewReport
                    );
        }
    }
}