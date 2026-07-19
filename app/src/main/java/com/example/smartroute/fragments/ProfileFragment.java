package com.example.smartroute.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.smartroute.AuthActivity;
import com.example.smartroute.R;

public class ProfileFragment extends Fragment {

    private Button btnEditProfile;
    private Button btnLogout;

    private ImageButton btnChangePhoto;
    private ImageView imgProfile;

    private View itemChangePassword;
    private View itemNotifications;
    private View itemHelpSupport;
    private View itemAbout;

    private Uri selectedProfileImageUri;

    private final ActivityResultLauncher<PickVisualMediaRequest>
            profilePhotoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(),
            uri -> {

                if (uri != null) {

                    selectedProfileImageUri = uri;

                    imgProfile.setImageURI(uri);

                    Toast.makeText(
                            requireContext(),
                            "Profile photo updated",
                            Toast.LENGTH_SHORT
                    ).show();

                } else {

                    Toast.makeText(
                            requireContext(),
                            "No photo selected",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
    );

    public ProfileFragment() {
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
                R.layout.fragment_profile,
                container,
                false
        );

        imgProfile =
                view.findViewById(R.id.imgProfile);

        btnChangePhoto =
                view.findViewById(R.id.btnChangePhoto);

        btnEditProfile =
                view.findViewById(R.id.btnEditProfile);

        btnLogout =
                view.findViewById(R.id.btnLogout);

        itemChangePassword =
                view.findViewById(R.id.itemChangePassword);

        itemNotifications =
                view.findViewById(R.id.itemNotifications);

        itemHelpSupport =
                view.findViewById(R.id.itemHelpSupport);

        itemAbout =
                view.findViewById(R.id.itemAbout);

        btnChangePhoto.setOnClickListener(v ->
                openProfilePhotoPicker()
        );

        imgProfile.setOnClickListener(v ->
                openProfilePhotoPicker()
        );

        btnEditProfile.setOnClickListener(v ->
                Toast.makeText(
                        requireContext(),
                        "Edit profile will be added next",
                        Toast.LENGTH_SHORT
                ).show()
        );

        itemChangePassword.setOnClickListener(v ->
                Toast.makeText(
                        requireContext(),
                        "Change password selected",
                        Toast.LENGTH_SHORT
                ).show()
        );

        itemNotifications.setOnClickListener(v ->
                Toast.makeText(
                        requireContext(),
                        "Notification settings selected",
                        Toast.LENGTH_SHORT
                ).show()
        );

        itemHelpSupport.setOnClickListener(v ->
                showHelpDialog()
        );

        itemAbout.setOnClickListener(v ->
                showAboutDialog()
        );

        btnLogout.setOnClickListener(v ->
                showLogoutDialog()
        );

        return view;
    }

    private void openProfilePhotoPicker() {

        PickVisualMediaRequest request =
                new PickVisualMediaRequest.Builder()
                        .setMediaType(
                                ActivityResultContracts
                                        .PickVisualMedia
                                        .ImageOnly.INSTANCE
                        )
                        .build();

        profilePhotoPickerLauncher.launch(request);
    }

    private void showHelpDialog() {

        new AlertDialog.Builder(requireContext())
                .setTitle("Help and Support")
                .setMessage(
                        "For assistance, contact the Smart Route support team.\n\n" +
                                "Email: support@smartroute.com\n" +
                                "Phone: +255 700 000 000"
                )
                .setPositiveButton("OK", null)
                .show();
    }

    private void showAboutDialog() {

        new AlertDialog.Builder(requireContext())
                .setTitle("About Smart Route")
                .setMessage(
                        "Smart Route helps citizens report public issues, " +
                                "select their location and track the progress " +
                                "of submitted reports.\n\nVersion 1.0"
                )
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLogoutDialog() {

        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton(
                        "Logout",
                        (dialog, which) -> logoutUser()
                )
                .show();
    }

    private void logoutUser() {

        Intent intent = new Intent(
                requireContext(),
                AuthActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
    }
}