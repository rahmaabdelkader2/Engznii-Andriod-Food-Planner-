package com.example.login_gui_firebase.profile.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login_gui_firebase.Login;
import com.example.login_gui_firebase.R;
import com.example.login_gui_firebase.profile.presenter.ProfilePresenter;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment implements IProfileView {
    private TextView fullNameDisplay, emailDisplay, phoneDisplay, countryDisplay;
    private ImageView profileImage;
    private ProfilePresenter presenter;
    Button logout;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile_activityy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Initialize views
        profileImage = view.findViewById(R.id.imageView7);
        fullNameDisplay = view.findViewById(R.id.firstnameDisplay);
        emailDisplay = view.findViewById(R.id.emailDisplay);
        phoneDisplay = view.findViewById(R.id.phoneDisplay);
        countryDisplay = view.findViewById(R.id.countryDisplay);
        logout = view.findViewById(R.id.logout);

        // Set up logout button
        logout.setOnClickListener(v -> {
            // Sign out from Firebase
            mAuth.signOut();

            // Clear the login state from SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_IS_LOGGED_IN, false);
            editor.apply();

            // Redirect to Login activity
            Intent intent = new Intent(getActivity(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            // Finish the current activity if it exists
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        // Initialize presenter and load user data
        presenter = new ProfilePresenter(this);
        presenter.loadUserData();
    }

    @Override
    public void showUserData(String firstName, String email, String phone, String country) {
        if (getView() != null) {
            fullNameDisplay.setText(firstName);
            emailDisplay.setText(email);
            phoneDisplay.setText(phone);
            countryDisplay.setText(country);
        }
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.detachView();
        }
    }
}