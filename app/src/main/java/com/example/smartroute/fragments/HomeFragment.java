package com.example.smartroute.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartroute.R;

public class HomeFragment extends Fragment {

    private View cardReportIssue;
    private View cardLocation;
    private View cardMyReports;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        cardReportIssue = view.findViewById(R.id.cardReportIssue);
        cardLocation = view.findViewById(R.id.cardLocation);
        cardMyReports = view.findViewById(R.id.cardMyReports);

        cardReportIssue.setOnClickListener(v ->
                openFragment(new ReportFragment()));

        cardLocation.setOnClickListener(v ->
                openFragment(new LocationFragment()));

        cardMyReports.setOnClickListener(v ->
                openFragment(new MyReportsFragment()));

        return view;
    }

    private void openFragment(Fragment fragment) {

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, fragment)
                .commit();
    }
}