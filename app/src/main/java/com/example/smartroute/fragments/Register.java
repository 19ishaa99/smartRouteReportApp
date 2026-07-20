package com.example.smartroute.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartroute.R;
import com.example.smartroute.data.entity.User;
import com.example.smartroute.data.repository.UserRepository;
import com.example.smartroute.utils.PasswordUtils;

import java.util.Locale;

public class Register extends Fragment {

    private EditText etFullName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etPassword;
    private EditText etConfirmPassword;

    private Button btnRegister;
    private TextView txtLogin;

    private UserRepository userRepository;

    public Register() {
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
                R.layout.fragment_register,
                container,
                false
        );

        initializeViews(view);

        userRepository = new UserRepository(
                requireContext().getApplicationContext()
        );

        btnRegister.setOnClickListener(v -> registerUser());

        txtLogin.setOnClickListener(v -> openLoginFragment(null));

        return view;
    }

    private void initializeViews(@NonNull View view) {

        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(
                R.id.etConfirmPassword
        );

        btnRegister = view.findViewById(R.id.btnRegister);
        txtLogin = view.findViewById(R.id.txtLogin);
    }

    private void registerUser() {

        clearErrors();

        String fullName = etFullName
                .getText()
                .toString()
                .trim();

        String email = etEmail
                .getText()
                .toString()
                .trim()
                .toLowerCase(Locale.ROOT);

        String phone = etPhone
                .getText()
                .toString()
                .trim();

        String password = etPassword
                .getText()
                .toString();

        String confirmPassword = etConfirmPassword
                .getText()
                .toString();

        if (!validateInputs(
                fullName,
                email,
                phone,
                password,
                confirmPassword
        )) {
            return;
        }

        setLoading(true);

        try {

            String passwordSalt =
                    PasswordUtils.generateSalt();

            String passwordHash =
                    PasswordUtils.hashPassword(
                            password,
                            passwordSalt
                    );

            User user = new User(
                    fullName,
                    email,
                    phone,
                    passwordHash,
                    passwordSalt,
                    null,
                    null,
                    System.currentTimeMillis()
            );

            saveUser(user, email);

        } catch (Exception exception) {

            setLoading(false);

            Toast.makeText(
                    requireContext(),
                    "Unable to secure the password. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void saveUser(
            @NonNull User user,
            @NonNull String email
    ) {

        userRepository.registerUser(
                user,
                new UserRepository.RegistrationCallback() {

                    @Override
                    public void onSuccess(long userId) {

                        if (!isAdded()) {
                            return;
                        }

                        requireActivity().runOnUiThread(() -> {

                            if (!isAdded()) {
                                return;
                            }

                            setLoading(false);

                            Toast.makeText(
                                    requireContext(),
                                    "Account created successfully. Please sign in.",
                                    Toast.LENGTH_LONG
                            ).show();

                            openLoginFragment(email);
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

                            setLoading(false);

                            if (message.toLowerCase(Locale.ROOT)
                                    .contains("email")) {

                                etEmail.setError(message);
                                etEmail.requestFocus();

                            } else {

                                Toast.makeText(
                                        requireContext(),
                                        message,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        });
                    }
                }
        );
    }

    private boolean validateInputs(
            @NonNull String fullName,
            @NonNull String email,
            @NonNull String phone,
            @NonNull String password,
            @NonNull String confirmPassword
    ) {

        if (TextUtils.isEmpty(fullName)) {

            etFullName.setError(
                    "Full name is required"
            );

            etFullName.requestFocus();
            return false;
        }

        if (fullName.length() < 3) {

            etFullName.setError(
                    "Enter your complete name"
            );

            etFullName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {

            etEmail.setError(
                    "Email is required"
            );

            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            etEmail.setError(
                    "Enter a valid email address"
            );

            etEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(phone)) {

            etPhone.setError(
                    "Phone number is required"
            );

            etPhone.requestFocus();
            return false;
        }

        String normalizedPhone =
                phone.replaceAll("[\\s-]", "");

        if (!normalizedPhone.matches("\\+?[0-9]{9,15}")) {

            etPhone.setError(
                    "Enter a valid phone number"
            );

            etPhone.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {

            etPassword.setError(
                    "Password is required"
            );

            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {

            etPassword.setError(
                    "Password must contain at least 6 characters"
            );

            etPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {

            etConfirmPassword.setError(
                    "Confirm your password"
            );

            etConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {

            etConfirmPassword.setError(
                    "Passwords do not match"
            );

            etConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void clearErrors() {

        etFullName.setError(null);
        etEmail.setError(null);
        etPhone.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
    }

    private void setLoading(boolean loading) {

        btnRegister.setEnabled(!loading);
        txtLogin.setEnabled(!loading);

        etFullName.setEnabled(!loading);
        etEmail.setEnabled(!loading);
        etPhone.setEnabled(!loading);
        etPassword.setEnabled(!loading);
        etConfirmPassword.setEnabled(!loading);

        if (loading) {
            btnRegister.setText("Creating account...");
        } else {
            btnRegister.setText("Create Account");
        }
    }

    private void openLoginFragment(
            @Nullable String registeredEmail
    ) {

        if (!isAdded()) {
            return;
        }

        LoginFragment loginFragment =
                new LoginFragment();

        if (registeredEmail != null) {

            Bundle bundle = new Bundle();

            bundle.putString(
                    "registered_email",
                    registeredEmail
            );

            loginFragment.setArguments(bundle);
        }

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(
                        R.id.authContainer,
                        loginFragment
                )
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        etFullName = null;
        etEmail = null;
        etPhone = null;
        etPassword = null;
        etConfirmPassword = null;
        btnRegister = null;
        txtLogin = null;
    }
}