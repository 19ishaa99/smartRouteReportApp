package com.example.smartroute.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartroute.R;
import com.example.smartroute.data.entity.Report;
import com.example.smartroute.data.repository.ReportRepository;

import java.util.Locale;

public class ReportFragment extends Fragment {

    /*
     * Fragment Result keys.
     * These same keys must be used by LocationFragment.
     */
    public static final String LOCATION_REQUEST_KEY =
            "location_result";

    public static final String KEY_SELECTED_ADDRESS =
            "selected_address";

    public static final String KEY_SELECTED_LATITUDE =
            "selected_latitude";

    public static final String KEY_SELECTED_LONGITUDE =
            "selected_longitude";

    private AutoCompleteTextView dropdownIssueType;

    private EditText etReportTitle;
    private EditText etDescription;
    private EditText etReportLocation;

    private TextView txtUseCurrentLocation;

    private View layoutSelectLocation;
    private View cardAddPhoto;

    private ImageView imgPreview;

    private RadioGroup radioUrgency;

    private Button btnSubmitReport;

    private Uri selectedImageUri;

    private ReportRepository reportRepository;

    private String selectedCategory = "";

    /*
     * Location selected from LocationFragment.
     */
    private String selectedLocationAddress = "";

    private double selectedLatitude = 0.0;
    private double selectedLongitude = 0.0;

    private boolean locationSelected = false;

    private final String[] issueCategories = {
            "Select an issue category",
            "Electricity Problem",
            "Water Problem",
            "Drainage Problem",
            "Street Light Problem"
    };

    private final ActivityResultLauncher<PickVisualMediaRequest>
            photoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(),
            uri -> {

                if (!isAdded()) {
                    return;
                }

                if (uri != null) {

                    selectedImageUri = uri;

                    if (imgPreview != null) {

                        imgPreview.setImageURI(uri);
                        imgPreview.setVisibility(View.VISIBLE);
                    }

                    Toast.makeText(
                            requireContext(),
                            "Image selected successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                } else {

                    Toast.makeText(
                            requireContext(),
                            "No image selected",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
    );

    public ReportFragment() {
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
                R.layout.fragment_report,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        super.onViewCreated(
                view,
                savedInstanceState
        );

        initializeViews(view);

        setupIssueCategoryDropdown();

        setupLocationResultListener();

        reportRepository =
                new ReportRepository(
                        requireContext()
                                .getApplicationContext()
                );

        cardAddPhoto.setOnClickListener(
                v -> openPhotoPicker()
        );

        imgPreview.setOnClickListener(
                v -> openPhotoPicker()
        );

        View.OnClickListener openLocationListener =
                v -> openLocationPicker();

        layoutSelectLocation.setOnClickListener(
                openLocationListener
        );

        txtUseCurrentLocation.setOnClickListener(
                openLocationListener
        );

        btnSubmitReport.setOnClickListener(
                v -> submitReport()
        );
    }

    /**
     * Connects Java fields to views in fragment_report.xml.
     */
    private void initializeViews(
            @NonNull View view
    ) {

        dropdownIssueType =
                view.findViewById(
                        R.id.dropdownIssueType
                );

        etReportTitle =
                view.findViewById(
                        R.id.etReportTitle
                );

        etDescription =
                view.findViewById(
                        R.id.etDescription
                );

        etReportLocation =
                view.findViewById(
                        R.id.etReportLocation
                );

        txtUseCurrentLocation =
                view.findViewById(
                        R.id.txtUseCurrentLocation
                );

        layoutSelectLocation =
                view.findViewById(
                        R.id.layoutSelectLocation
                );

        cardAddPhoto =
                view.findViewById(
                        R.id.cardAddPhoto
                );

        imgPreview =
                view.findViewById(
                        R.id.imgPreview
                );

        radioUrgency =
                view.findViewById(
                        R.id.radioUrgency
                );

        btnSubmitReport =
                view.findViewById(
                        R.id.btnSubmitReport
                );
    }

    /**
     * Configures the issue category dropdown.
     */
    private void setupIssueCategoryDropdown() {

        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout
                                .simple_dropdown_item_1line,
                        issueCategories
                );

        dropdownIssueType.setAdapter(
                categoryAdapter
        );

        dropdownIssueType.setInputType(0);
        dropdownIssueType.setKeyListener(null);

        /*
         * Initially display the placeholder.
         */
        dropdownIssueType.setText(
                issueCategories[0],
                false
        );

        dropdownIssueType.setOnClickListener(
                v -> dropdownIssueType.showDropDown()
        );

        dropdownIssueType.setOnFocusChangeListener(
                (v, hasFocus) -> {

                    if (hasFocus) {

                        dropdownIssueType
                                .showDropDown();
                    }
                }
        );

        dropdownIssueType.setOnItemClickListener(
                (parent, selectedView, position, id) -> {

                    if (position == 0) {

                        selectedCategory = "";

                    } else {

                        selectedCategory =
                                parent.getItemAtPosition(
                                        position
                                ).toString();

                        dropdownIssueType.setError(
                                null
                        );
                    }
                }
        );
    }

    /**
     * Opens the existing map screen.
     */
    private void openLocationPicker() {

        if (!isAdded()) {
            return;
        }

        getParentFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.mainFragmentContainer,
                        new LocationFragment()
                )
                .addToBackStack(null)
                .commit();
    }

    /**
     * Receives the location selected in LocationFragment.
     */
    private void setupLocationResultListener() {

        getParentFragmentManager()
                .setFragmentResultListener(
                        LOCATION_REQUEST_KEY,
                        getViewLifecycleOwner(),
                        (requestKey, bundle) -> {

                            selectedLocationAddress =
                                    bundle.getString(
                                            KEY_SELECTED_ADDRESS,
                                            ""
                                    );

                            selectedLatitude =
                                    bundle.getDouble(
                                            KEY_SELECTED_LATITUDE,
                                            0.0
                                    );

                            selectedLongitude =
                                    bundle.getDouble(
                                            KEY_SELECTED_LONGITUDE,
                                            0.0
                                    );

                            locationSelected =
                                    bundle.containsKey(
                                            KEY_SELECTED_LATITUDE
                                    )
                                            && bundle.containsKey(
                                            KEY_SELECTED_LONGITUDE
                                    );

                            displaySelectedLocation();
                        }
                );
    }

    /**
     * Displays the selected map address in the location field.
     */
    private void displaySelectedLocation() {

        if (etReportLocation == null) {
            return;
        }

        String address =
                selectedLocationAddress == null
                        ? ""
                        : selectedLocationAddress.trim();

        String coordinates =
                String.format(
                        Locale.getDefault(),
                        "%.6f, %.6f",
                        selectedLatitude,
                        selectedLongitude
                );

        if (address.isEmpty()) {

            etReportLocation.setText(
                    coordinates
            );

        } else {

            etReportLocation.setText(
                    address
            );
        }

        etReportLocation.setError(null);

        Toast.makeText(
                requireContext(),
                "Issue location selected",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void openPhotoPicker() {

        PickVisualMediaRequest request =
                new PickVisualMediaRequest.Builder()
                        .setMediaType(
                                ActivityResultContracts
                                        .PickVisualMedia
                                        .ImageOnly.INSTANCE
                        )
                        .build();

        photoPickerLauncher.launch(
                request
        );
    }

    private void submitReport() {

        clearErrors();

        String category =
                selectedCategory.trim();

        String title =
                etReportTitle
                        .getText()
                        .toString()
                        .trim();

        String description =
                etDescription
                        .getText()
                        .toString()
                        .trim();

        String displayedLocation =
                etReportLocation
                        .getText()
                        .toString()
                        .trim();

        if (category.isEmpty()) {

            dropdownIssueType.setError(
                    "Select an issue category"
            );

            dropdownIssueType.requestFocus();
            dropdownIssueType.showDropDown();

            Toast.makeText(
                    requireContext(),
                    "Please select an issue category",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (title.isEmpty()) {

            etReportTitle.setError(
                    "Enter report title"
            );

            etReportTitle.requestFocus();
            return;
        }

        if (title.length() < 4) {

            etReportTitle.setError(
                    "Report title is too short"
            );

            etReportTitle.requestFocus();
            return;
        }

        if (description.isEmpty()) {

            etDescription.setError(
                    "Enter issue description"
            );

            etDescription.requestFocus();
            return;
        }

        if (description.length() < 10) {

            etDescription.setError(
                    "Provide a clearer issue description"
            );

            etDescription.requestFocus();
            return;
        }

        if (!locationSelected ||
                displayedLocation.isEmpty()) {

            etReportLocation.setError(
                    "Choose the issue location from the map"
            );

            Toast.makeText(
                    requireContext(),
                    "Please choose the issue location from the map",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (selectedImageUri == null) {

            Toast.makeText(
                    requireContext(),
                    "Please add photo evidence",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int userId =
                getLoggedInUserId();

        if (userId == -1) {

            Toast.makeText(
                    requireContext(),
                    "Your login session was not found. Please sign in again.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        String urgency =
                getSelectedUrgency();

        /*
         * Save both the readable address and coordinates.
         *
         * This avoids changing the Report database entity immediately.
         */
        String completeLocation =
                buildCompleteLocation(
                        displayedLocation
                );

        long currentTime =
                System.currentTimeMillis();

        Report report =
                new Report(
                        userId,
                        category,
                        title,
                        description,
                        completeLocation,
                        selectedImageUri.toString(),
                        urgency,
                        "Submitted",
                        currentTime,
                        currentTime
                );

        saveReport(report);
    }

    /**
     * Combines the selected address with its map coordinates.
     */
    @NonNull
    private String buildCompleteLocation(
            @NonNull String displayedLocation
    ) {

        String coordinates =
                String.format(
                        Locale.US,
                        "%.6f, %.6f",
                        selectedLatitude,
                        selectedLongitude
                );

        return displayedLocation
                + " | Coordinates: "
                + coordinates;
    }

    private void saveReport(
            @NonNull Report report
    ) {

        setLoading(true);

        reportRepository.insertReport(
                report,
                new ReportRepository.InsertCallback() {

                    @Override
                    public void onSuccess(
                            long reportId
                    ) {

                        if (!isAdded()) {
                            return;
                        }

                        requireActivity()
                                .runOnUiThread(() -> {

                                    if (!isAdded()) {
                                        return;
                                    }

                                    setLoading(false);

                                    Toast.makeText(
                                            requireContext(),
                                            "Report submitted successfully",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    resetForm();
                                });
                    }

                    @Override
                    public void onError(
                            String message
                    ) {

                        if (!isAdded()) {
                            return;
                        }

                        requireActivity()
                                .runOnUiThread(() -> {

                                    if (!isAdded()) {
                                        return;
                                    }

                                    setLoading(false);

                                    String errorMessage =
                                            message;

                                    if (errorMessage == null ||
                                            errorMessage
                                                    .trim()
                                                    .isEmpty()) {

                                        errorMessage =
                                                "Unable to submit the report";
                                    }

                                    Toast.makeText(
                                            requireContext(),
                                            errorMessage,
                                            Toast.LENGTH_LONG
                                    ).show();
                                });
                    }
                }
        );
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

    @NonNull
    private String getSelectedUrgency() {

        int selectedUrgencyId =
                radioUrgency
                        .getCheckedRadioButtonId();

        if (selectedUrgencyId ==
                R.id.radioUrgent) {

            return "Urgent";
        }

        if (selectedUrgencyId ==
                R.id.radioEmergency) {

            return "Emergency";
        }

        return "Normal";
    }

    private void clearErrors() {

        dropdownIssueType.setError(null);
        etReportTitle.setError(null);
        etDescription.setError(null);
        etReportLocation.setError(null);
    }

    private void setLoading(
            boolean loading
    ) {

        btnSubmitReport.setEnabled(
                !loading
        );

        dropdownIssueType.setEnabled(
                !loading
        );

        etReportTitle.setEnabled(
                !loading
        );

        etDescription.setEnabled(
                !loading
        );

        radioUrgency.setEnabled(
                !loading
        );

        cardAddPhoto.setEnabled(
                !loading
        );

        imgPreview.setEnabled(
                !loading
        );

        layoutSelectLocation.setEnabled(
                !loading
        );

        txtUseCurrentLocation.setEnabled(
                !loading
        );

        if (loading) {

            btnSubmitReport.setText(
                    "Submitting report..."
            );

        } else {

            btnSubmitReport.setText(
                    "Submit Report"
            );
        }
    }

    private void resetForm() {

        selectedCategory = "";

        dropdownIssueType.setText(
                issueCategories[0],
                false
        );

        dropdownIssueType.clearFocus();

        etReportTitle.setText("");
        etDescription.setText("");
        etReportLocation.setText("");

        selectedLocationAddress = "";

        selectedLatitude = 0.0;
        selectedLongitude = 0.0;

        locationSelected = false;

        radioUrgency.check(
                R.id.radioNormal
        );

        selectedImageUri = null;

        imgPreview.setImageDrawable(
                null
        );

        imgPreview.setVisibility(
                View.GONE
        );
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();

        dropdownIssueType = null;

        etReportTitle = null;
        etDescription = null;
        etReportLocation = null;

        txtUseCurrentLocation = null;

        layoutSelectLocation = null;
        cardAddPhoto = null;

        imgPreview = null;

        radioUrgency = null;

        btnSubmitReport = null;
    }
}