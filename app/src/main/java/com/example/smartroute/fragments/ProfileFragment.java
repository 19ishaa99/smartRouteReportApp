package com.example.smartroute.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.example.smartroute.data.entity.User;
import com.example.smartroute.data.repository.UserRepository;

public class ProfileFragment extends Fragment {

    private static final String SESSION_NAME =
            "smart_route_session";

    private static final String KEY_USER_ID =
            "user_id";

    private ImageView imgProfile;
    private ImageButton btnChangePhoto;

    private TextView txtProfileName;
    private TextView txtProfileEmail;
    private TextView txtProfilePhone;

    private Button btnEditProfile;
    private Button btnLogout;

    private View itemChangePassword;
    private View itemNotifications;
    private View itemHelpSupport;
    private View itemAbout;

    private UserRepository userRepository;
    private User currentUser;

    private Uri selectedProfileImageUri;

    private final ActivityResultLauncher<PickVisualMediaRequest>
            profilePhotoPickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.PickVisualMedia(),
                    uri -> {

                        if (!isAdded()) {
                            return;
                        }

                        if (uri == null) {

                            Toast.makeText(
                                    requireContext(),
                                    "No photo selected",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        selectedProfileImageUri = uri;

                        imgProfile.setImageURI(uri);

                        saveProfilePhoto(uri);
                    }
            );

    public ProfileFragment() {
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
                R.layout.fragment_profile,
                container,
                false
        );

        initializeViews(view);

        userRepository =
                new UserRepository(
                        requireContext()
                                .getApplicationContext()
                );

        setupClickListeners();

        loadUserProfile();

        return view;
    }

    private void initializeViews(
            @NonNull View view
    ) {

        imgProfile =
                view.findViewById(
                        R.id.imgProfile
                );

        btnChangePhoto =
                view.findViewById(
                        R.id.btnChangePhoto
                );

        txtProfileName =
                view.findViewById(
                        R.id.txtProfileName
                );

        txtProfileEmail =
                view.findViewById(
                        R.id.txtProfileEmail
                );

        txtProfilePhone =
                view.findViewById(
                        R.id.txtProfilePhone
                );

        btnEditProfile =
                view.findViewById(
                        R.id.btnEditProfile
                );

        btnLogout =
                view.findViewById(
                        R.id.btnLogout
                );

        itemChangePassword =
                view.findViewById(
                        R.id.itemChangePassword
                );

        itemNotifications =
                view.findViewById(
                        R.id.itemNotifications
                );

        itemHelpSupport =
                view.findViewById(
                        R.id.itemHelpSupport
                );

        itemAbout =
                view.findViewById(
                        R.id.itemAbout
                );
    }

    private void setupClickListeners() {

        btnChangePhoto.setOnClickListener(
                view -> openProfilePhotoPicker()
        );

        imgProfile.setOnClickListener(
                view -> openProfilePhotoPicker()
        );

        btnEditProfile.setOnClickListener(
                view -> showEditProfileDialog()
        );

        itemChangePassword.setOnClickListener(
                view -> showChangePasswordMessage()
        );

        itemNotifications.setOnClickListener(
                view -> Toast.makeText(
                        requireContext(),
                        "Notification settings selected",
                        Toast.LENGTH_SHORT
                ).show()
        );

        itemHelpSupport.setOnClickListener(
                view -> showHelpDialog()
        );

        itemAbout.setOnClickListener(
                view -> showAboutDialog()
        );

        btnLogout.setOnClickListener(
                view -> showLogoutDialog()
        );
    }

    private void loadUserProfile() {

        int userId = getLoggedInUserId();

        if (userId == -1) {

            Toast.makeText(
                    requireContext(),
                    "Login session not found",
                    Toast.LENGTH_LONG
            ).show();

            logoutUser();

            return;
        }

        userRepository
                .getUserById(userId)
                .observe(
                        getViewLifecycleOwner(),
                        user -> {

                            if (user == null) {

                                Toast.makeText(
                                        requireContext(),
                                        "User profile not found",
                                        Toast.LENGTH_LONG
                                ).show();

                                return;
                            }

                            currentUser = user;

                            displayUserProfile(user);
                        }
                );
    }

    private void displayUserProfile(
            @NonNull User user
    ) {

        String fullName = user.getFullName();

        if (fullName == null ||
                fullName.trim().isEmpty()) {

            txtProfileName.setText(
                    "Smart Route User"
            );

        } else {

            txtProfileName.setText(fullName);
        }

        String email = user.getEmail();

        if (email == null ||
                email.trim().isEmpty()) {

            txtProfileEmail.setText(
                    "No email address"
            );

        } else {

            txtProfileEmail.setText(email);
        }

        String phone = user.getPhone();

        if (phone == null ||
                phone.trim().isEmpty()) {

            txtProfilePhone.setText(
                    "No phone number"
            );

        } else {

            txtProfilePhone.setText(phone);
        }

        String imageUri =
                user.getProfileImageUri();

        if (imageUri == null ||
                imageUri.trim().isEmpty()) {

            imgProfile.setImageResource(
                    R.drawable.ic_person
            );

            return;
        }

        try {

            selectedProfileImageUri =
                    Uri.parse(imageUri);

            imgProfile.setImageURI(
                    selectedProfileImageUri
            );

        } catch (Exception exception) {

            imgProfile.setImageResource(
                    R.drawable.ic_person
            );
        }
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

    private void saveProfilePhoto(
            @NonNull Uri imageUri
    ) {

        int userId = getLoggedInUserId();

        if (userId == -1) {

            Toast.makeText(
                    requireContext(),
                    "Login session not found",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        btnChangePhoto.setEnabled(false);
        imgProfile.setEnabled(false);

        userRepository.updateProfileImage(
                userId,
                imageUri.toString(),
                new UserRepository.OperationCallback() {

                    @Override
                    public void onSuccess() {

                        if (!isAdded()) {
                            return;
                        }

                        requireActivity().runOnUiThread(() -> {

                            if (!isAdded()) {
                                return;
                            }

                            btnChangePhoto.setEnabled(true);
                            imgProfile.setEnabled(true);

                            Toast.makeText(
                                    requireContext(),
                                    "Profile photo updated",
                                    Toast.LENGTH_SHORT
                            ).show();
                        });
                    }

                    @Override
                    public void onError(String message) {

                        if (!isAdded()) {
                            return;
                        }

                        requireActivity().runOnUiThread(() -> {

                            if (!isAdded()) {
                                return;
                            }

                            btnChangePhoto.setEnabled(true);
                            imgProfile.setEnabled(true);

                            Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        });
                    }
                }
        );
    }

    private void showEditProfileDialog() {

        if (currentUser == null) {

            Toast.makeText(
                    requireContext(),
                    "Profile is still loading",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        View dialogView =
                LayoutInflater.from(
                        requireContext()
                ).inflate(
                        R.layout.dialog_edit_profile,
                        null
                );

        EditText etEditFullName =
                dialogView.findViewById(
                        R.id.etEditFullName
                );

        EditText etEditPhone =
                dialogView.findViewById(
                        R.id.etEditPhone
                );

        etEditFullName.setText(
                currentUser.getFullName()
        );

        etEditPhone.setText(
                currentUser.getPhone()
        );

        AlertDialog editDialog =
                new AlertDialog.Builder(
                        requireContext()
                )
                        .setTitle("Edit Profile")
                        .setView(dialogView)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Save",
                                null
                        )
                        .create();

        editDialog.setOnShowListener(dialog -> {

            Button saveButton =
                    editDialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    );

            saveButton.setOnClickListener(view -> {

                String fullName =
                        etEditFullName
                                .getText()
                                .toString()
                                .trim();

                String phone =
                        etEditPhone
                                .getText()
                                .toString()
                                .trim();

                if (fullName.isEmpty()) {

                    etEditFullName.setError(
                            "Full name is required"
                    );

                    etEditFullName.requestFocus();

                    return;
                }

                saveButton.setEnabled(false);

                updateProfile(
                        fullName,
                        phone,
                        editDialog,
                        saveButton
                );
            });
        });

        editDialog.show();
    }

    private void updateProfile(
            @NonNull String fullName,
            @NonNull String phone,
            @NonNull AlertDialog dialog,
            @NonNull Button saveButton
    ) {

        int userId = getLoggedInUserId();

        if (userId == -1) {

            saveButton.setEnabled(true);

            Toast.makeText(
                    requireContext(),
                    "Login session not found",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        userRepository.updateProfile(
                userId,
                fullName,
                phone,
                new UserRepository.OperationCallback() {

                    @Override
                    public void onSuccess() {

                        if (!isAdded()) {
                            return;
                        }

                        requireActivity().runOnUiThread(() -> {

                            if (!isAdded()) {
                                return;
                            }

                            dialog.dismiss();

                            Toast.makeText(
                                    requireContext(),
                                    "Profile updated successfully",
                                    Toast.LENGTH_SHORT
                            ).show();
                        });
                    }

                    @Override
                    public void onError(String message) {

                        if (!isAdded()) {
                            return;
                        }

                        requireActivity().runOnUiThread(() -> {

                            if (!isAdded()) {
                                return;
                            }

                            saveButton.setEnabled(true);

                            Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        });
                    }
                }
        );
    }

    private void showChangePasswordMessage() {

        Toast.makeText(
                requireContext(),
                "Change password will be connected next",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void showHelpDialog() {

        new AlertDialog.Builder(
                requireContext()
        )
                .setTitle("Help and Support")
                .setMessage(
                        "For assistance, contact the Smart Route support team.\n\n" +
                                "Email: support@smartroute.com\n" +
                                "Phone: +255 700 000 000"
                )
                .setPositiveButton(
                        "OK",
                        null
                )
                .show();
    }

    private void showAboutDialog() {

        new AlertDialog.Builder(
                requireContext()
        )
                .setTitle("About Smart Route")
                .setMessage(
                        "Smart Route helps citizens report public issues, " +
                                "select their location and track the progress " +
                                "of submitted reports.\n\n" +
                                "Version 1.0"
                )
                .setPositiveButton(
                        "OK",
                        null
                )
                .show();
    }

    private void showLogoutDialog() {

        new AlertDialog.Builder(
                requireContext()
        )
                .setTitle("Logout")
                .setMessage(
                        "Are you sure you want to logout?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Logout",
                        (dialog, which) -> logoutUser()
                )
                .show();
    }

    private void logoutUser() {

        SharedPreferences preferences =
                requireContext()
                        .getSharedPreferences(
                                SESSION_NAME,
                                Context.MODE_PRIVATE
                        );

        preferences.edit()
                .clear()
                .apply();

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        imgProfile = null;
        btnChangePhoto = null;

        txtProfileName = null;
        txtProfileEmail = null;
        txtProfilePhone = null;

        btnEditProfile = null;
        btnLogout = null;

        itemChangePassword = null;
        itemNotifications = null;
        itemHelpSupport = null;
        itemAbout = null;

        currentUser = null;
    }
}