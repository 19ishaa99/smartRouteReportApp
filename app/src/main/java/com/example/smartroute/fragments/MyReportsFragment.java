package com.example.smartroute.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartroute.R;

public class MyReportsFragment extends Fragment {

    private Button btnViewReportOne;
    private Button btnViewReportTwo;
    private Button btnViewReportThree;

    public MyReportsFragment() {
        // Required empty constructor
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

        btnViewReportOne =
                view.findViewById(R.id.btnViewReportOne);

        btnViewReportTwo =
                view.findViewById(R.id.btnViewReportTwo);

        btnViewReportThree =
                view.findViewById(R.id.btnViewReportThree);

        btnViewReportOne.setOnClickListener(v ->
                showReportDetails(
                        "Broken Street Light",
                        "Pending"
                )
        );

        btnViewReportTwo.setOnClickListener(v ->
                showReportDetails(
                        "Uncollected Waste",
                        "In Progress"
                )
        );

        btnViewReportThree.setOnClickListener(v ->
                showReportDetails(
                        "Water Leakage",
                        "Resolved"
                )
        );

        return view;
    }

    private void showReportDetails(
            String reportTitle,
            String reportStatus
    ) {

        Toast.makeText(
                requireContext(),
                reportTitle + "\nStatus: " + reportStatus,
                Toast.LENGTH_LONG
        ).show();
    }
}