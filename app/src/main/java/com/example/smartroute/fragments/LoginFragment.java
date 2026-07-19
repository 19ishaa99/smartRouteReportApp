package com.example.smartroute.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class LoginFragment extends Fragment {

    private EditText etEmail;
    private EditText etPassword;
    private AppCompatButton btnLogin;
    private TextView txtRegister;

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

        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        txtRegister = view.findViewById(R.id.txtRegister);

        btnLogin.setOnClickListener(v -> loginUser());

        txtRegister.setOnClickListener(v -> openRegisterFragment());

        return view;
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must contain at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        Toast.makeText(
                requireContext(),
                "Login successful",
                Toast.LENGTH_SHORT
        ).show();

        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);

        // Prevent returning to AuthActivity when pressing Back
        requireActivity().finish();
    }

    private void openRegisterFragment() {

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(R.id.authContainer, new Register())
                .addToBackStack(null)
                .commit();
    }
}