package com.example.smartroute.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartroute.R;

public class ReportFragment extends Fragment {

    private Spinner spinnerCategory;

    private EditText etReportTitle;
    private EditText etDescription;
    private EditText etReportLocation;

    private TextView txtUseCurrentLocation;

    private View cardAddPhoto;
    private ImageView imgPreview;

    private RadioGroup radioUrgency;

    private Button btnSubmitReport;

    private Uri selectedImageUri;

    private final ActivityResultLauncher<PickVisualMediaRequest>
            photoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(),
            uri -> {

                if (uri != null) {

                    selectedImageUri = uri;

                    imgPreview.setImageURI(uri);
                    imgPreview.setVisibility(View.VISIBLE);

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
                R.layout.fragment_report,
                container,
                false
        );

        spinnerCategory =
                view.findViewById(R.id.spinnerCategory);

        etReportTitle =
                view.findViewById(R.id.etReportTitle);

        etDescription =
                view.findViewById(R.id.etDescription);

        etReportLocation =
                view.findViewById(R.id.etReportLocation);

        txtUseCurrentLocation =
                view.findViewById(R.id.txtUseCurrentLocation);

        cardAddPhoto =
                view.findViewById(R.id.cardAddPhoto);

        imgPreview =
                view.findViewById(R.id.imgPreview);

        radioUrgency =
                view.findViewById(R.id.radioUrgency);

        btnSubmitReport =
                view.findViewById(R.id.btnSubmitReport);

        cardAddPhoto.setOnClickListener(v ->
                openPhotoPicker()
        );

        imgPreview.setOnClickListener(v ->
                openPhotoPicker()
        );

        txtUseCurrentLocation.setOnClickListener(v ->
                Toast.makeText(
                        requireContext(),
                        "GPS location will be added later",
                        Toast.LENGTH_SHORT
                ).show()
        );

        btnSubmitReport.setOnClickListener(v ->
                submitReport()
        );

        return view;
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

        photoPickerLauncher.launch(request);
    }

    private void submitReport() {

        String title =
                etReportTitle.getText().toString().trim();

        String description =
                etDescription.getText().toString().trim();

        String location =
                etReportLocation.getText().toString().trim();

        if (spinnerCategory.getSelectedItemPosition() == 0) {

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

        if (description.isEmpty()) {

            etDescription.setError(
                    "Enter issue description"
            );

            etDescription.requestFocus();
            return;
        }

        if (location.isEmpty()) {

            etReportLocation.setError(
                    "Enter issue location"
            );

            etReportLocation.requestFocus();
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

        String urgency = getSelectedUrgency();

        Toast.makeText(
                requireContext(),
                "Report submitted successfully\nUrgency: " + urgency,
                Toast.LENGTH_LONG
        ).show();

        resetForm();
    }

    private String getSelectedUrgency() {

        int selectedUrgencyId =
                radioUrgency.getCheckedRadioButtonId();

        if (selectedUrgencyId == R.id.radioUrgent) {
            return "Urgent";
        }

        if (selectedUrgencyId == R.id.radioEmergency) {
            return "Emergency";
        }

        return "Normal";
    }

    private void resetForm() {

        spinnerCategory.setSelection(0);

        etReportTitle.setText("");
        etDescription.setText("");
        etReportLocation.setText("");

        radioUrgency.check(R.id.radioNormal);

        selectedImageUri = null;

        imgPreview.setImageDrawable(null);
        imgPreview.setVisibility(View.GONE);
    }
}