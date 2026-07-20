package com.example.smartroute.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.smartroute.MainActivity;
import com.example.smartroute.R;
import com.example.smartroute.data.entity.User;
import com.example.smartroute.data.repository.UserRepository;
import com.example.smartroute.utils.PasswordUtils;

import java.util.Locale;

public class LoginFragment extends Fragment {

    private EditText etEmail;
    private EditText etPassword;
    private AppCompatButton btnLogin;
    private TextView txtRegister;

    private UserRepository userRepository;

    public LoginFragment() {
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
                R.layout.fragment_login,
                container,
                false
        );

        initializeViews(view);

        userRepository = new UserRepository(
                requireContext().getApplicationContext()
        );

        fillRegisteredEmail();

        btnLogin.setOnClickListener(v -> loginUser());

        txtRegister.setOnClickListener(
                v -> openRegisterFragment()
        );

        return view;
    }

    private void initializeViews(@NonNull View view) {

        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        txtRegister = view.findViewById(R.id.txtRegister);
    }

    private void fillRegisteredEmail() {

        Bundle arguments = getArguments();

        if (arguments == null) {
            return;
        }

        String registeredEmail = arguments.getString(
                "registered_email"
        );

        if (registeredEmail != null
                && !registeredEmail.trim().isEmpty()) {

            etEmail.setText(registeredEmail);
            etPassword.requestFocus();
        }
    }

    private void loginUser() {

        clearErrors();

        String email = etEmail
                .getText()
                .toString()
                .trim()
                .toLowerCase(Locale.ROOT);

        String password = etPassword
                .getText()
                .toString();

        if (!validateInputs(email, password)) {
            return;
        }

        setLoading(true);

        userRepository.loginUser(
                email,
                new UserRepository.LoginCallback() {

                    @Override
                    public void onSuccess(User user) {

                        if (!isAdded()) {
                            return;
                        }

                        requireActivity().runOnUiThread(() -> {

                            if (!isAdded()) {
                                return;
                            }

                            boolean passwordCorrect =
                                    PasswordUtils.verifyPassword(
                                            password,
                                            user.getPasswordSalt(),
                                            user.getPasswordHash()
                                    );

                            if (!passwordCorrect) {

                                setLoading(false);

                                etPassword.setError(
                                        "Incorrect password"
                                );

                                etPassword.requestFocus();
                                return;
                            }

                            saveLoginSession(user);

                            Toast.makeText(
                                    requireContext(),
                                    "Welcome, "
                                            + user.getFullName(),
                                    Toast.LENGTH_SHORT
                            ).show();

                            openMainActivity(user);
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

                            etEmail.setError(message);
                            etEmail.requestFocus();
                        });
                    }
                }
        );
    }

    private boolean validateInputs(
            @NonNull String email,
            @NonNull String password
    ) {

        if (TextUtils.isEmpty(email)) {

            etEmail.setError("Email is required");
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

        return true;
    }

    private void saveLoginSession(@NonNull User user) {

        requireContext()
                .getSharedPreferences(
                        "smart_route_session",
                        android.content.Context.MODE_PRIVATE
                )
                .edit()
                .putBoolean("is_logged_in", true)
                .putInt("user_id", user.getId())
                .putString(
                        "user_name",
                        user.getFullName()
                )
                .putString(
                        "user_email",
                        user.getEmail()
                )
                .apply();
    }

    private void openMainActivity(@NonNull User user) {

        Intent intent = new Intent(
                requireActivity(),
                MainActivity.class
        );

        intent.putExtra("user_id", user.getId());
        intent.putExtra(
                "user_name",
                user.getFullName()
        );

        intent.putExtra(
                "user_email",
                user.getEmail()
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        requireActivity().finish();
    }

    private void clearErrors() {

        etEmail.setError(null);
        etPassword.setError(null);
    }

    private void setLoading(boolean loading) {

        btnLogin.setEnabled(!loading);
        txtRegister.setEnabled(!loading);

        etEmail.setEnabled(!loading);
        etPassword.setEnabled(!loading);

        if (loading) {
            btnLogin.setText("Signing in...");
        } else {
            btnLogin.setText("Login");
        }
    }

    private void openRegisterFragment() {

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(
                        R.id.authContainer,
                        new Register()
                )
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        etEmail = null;
        etPassword = null;
        btnLogin = null;
        txtRegister = null;
    }
}