package com.example.smartroute.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartroute.R;
import com.example.smartroute.data.entity.Report;

import java.util.ArrayList;
import java.util.List;

public class AdminReportAdapter
        extends RecyclerView.Adapter<
        AdminReportAdapter.AdminReportViewHolder> {

    /*
     * This interface sends the selected report
     * from the adapter to AdminReportsFragment.
     */
    public interface OnApproveClickListener {

        void onApproveClick(
                Report report,
                int position
        );
    }

    private final List<Report> reportList;

    private final OnApproveClickListener
            onApproveClickListener;

    /*
     * Adapter constructor.
     */
    public AdminReportAdapter(
            @NonNull OnApproveClickListener
                    onApproveClickListener
    ) {

        this.onApproveClickListener =
                onApproveClickListener;

        reportList =
                new ArrayList<>();
    }

    /*
     * Replace the current adapter data
     * with a new list of reports.
     */
    public void setReports(
            List<Report> reports
    ) {

        reportList.clear();

        if (reports != null) {

            reportList.addAll(
                    reports
            );
        }

        notifyDataSetChanged();
    }

    /*
     * Remove an approved report from the list.
     *
     * This method is optional when using LiveData,
     * because Room will automatically refresh the list.
     */
    public void removeReport(
            int position
    ) {

        if (position < 0 ||
                position >= reportList.size()) {

            return;
        }

        reportList.remove(
                position
        );

        notifyItemRemoved(
                position
        );
    }

    /*
     * Return the number of reports currently shown.
     */
    public int getReportCount() {

        return reportList.size();
    }

    @NonNull
    @Override
    public AdminReportViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.item_admin_report,
                        parent,
                        false
                );

        return new AdminReportViewHolder(
                view
        );
    }

    @Override
    public void onBindViewHolder(
            @NonNull AdminReportViewHolder holder,
            int position
    ) {

        Report report =
                reportList.get(
                        position
                );

        holder.bind(
                report,
                onApproveClickListener
        );
    }

    @Override
    public int getItemCount() {

        return reportList.size();
    }

    /*
     * ViewHolder holds all views from
     * item_admin_report.xml.
     */
    static class AdminReportViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView txtReportTitle;
        private final TextView txtReportCategory;
        private final TextView txtReportDescription;
        private final TextView txtReportLocation;
        private final TextView txtReportStatus;

        private final Button btnApproveReport;

        public AdminReportViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            txtReportTitle =
                    itemView.findViewById(
                            R.id.txtReportTitle
                    );

            txtReportCategory =
                    itemView.findViewById(
                            R.id.txtReportCategory
                    );

            txtReportDescription =
                    itemView.findViewById(
                            R.id.txtReportDescription
                    );

            txtReportLocation =
                    itemView.findViewById(
                            R.id.txtReportLocation
                    );

            txtReportStatus =
                    itemView.findViewById(
                            R.id.txtReportStatus
                    );

            btnApproveReport =
                    itemView.findViewById(
                            R.id.btnApprove
                    );
        }

        /*
         * Display one report inside the report card.
         */
        private void bind(
                @NonNull Report report,
                @NonNull OnApproveClickListener listener
        ) {

            /*
             * Report title.
             */
            txtReportTitle.setText(
                    getSafeText(
                            report.getTitle(),
                            "Untitled report"
                    )
            );

            /*
             * Report category.
             */
            txtReportCategory.setText(
                    "Category: " +
                            getSafeText(
                                    report.getCategory(),
                                    "Not specified"
                            )
            );

            /*
             * Report description.
             */
            txtReportDescription.setText(
                    getSafeText(
                            report.getDescription(),
                            "No description provided"
                    )
            );

            /*
             * Report location.
             */
            txtReportLocation.setText(
                    "Location: " +
                            getSafeText(
                                    report.getLocation(),
                                    "Not specified"
                            )
            );

            /*
             * Report status.
             */
            txtReportStatus.setText(
                    getSafeText(
                            report.getStatus(),
                            "Submitted"
                    )
            );

            /*
             * Reset the button whenever a RecyclerView
             * item is reused.
             */
            btnApproveReport.setEnabled(
                    true
            );

            btnApproveReport.setText(
                    "Approve Report"
            );

            /*
             * Send the selected report to the fragment.
             */
            btnApproveReport.setOnClickListener(
                    view -> {

                        int currentPosition =
                                getBindingAdapterPosition();

                        if (currentPosition ==
                                RecyclerView.NO_POSITION) {

                            return;
                        }

                        /*
                         * Prevent repeated clicks while
                         * Room is updating the report.
                         */
                        btnApproveReport.setEnabled(
                                false
                        );

                        btnApproveReport.setText(
                                "Approving..."
                        );

                        listener.onApproveClick(
                                report,
                                currentPosition
                        );
                    }
            );
        }

        /*
         * Prevent null or empty values from appearing.
         */
        @NonNull
        private String getSafeText(
                String value,
                @NonNull String defaultValue
        ) {

            if (value == null ||
                    value.trim().isEmpty()) {

                return defaultValue;
            }

            return value.trim();
        }
    }
}