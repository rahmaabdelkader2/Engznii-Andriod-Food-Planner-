package com.example.login_gui_firebase.profile.view;

import android.content.Intent;
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
        // Initialize views
        profileImage = view.findViewById(R.id.imageView7);
        fullNameDisplay = view.findViewById(R.id.firstnameDisplay);

        emailDisplay = view.findViewById(R.id.emailDisplay);
        phoneDisplay = view.findViewById(R.id.phoneDisplay);
        countryDisplay = view.findViewById(R.id.countryDisplay);

        // Set static profile image
        logout= view.findViewById(R.id.logout);
        logout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }



        });

        // Initialize presenter
        presenter = new ProfilePresenter(this);
        presenter.loadUserData();
    }

    @Override
    public void showUserData(String firstName, String email, String phone, String country) {
        fullNameDisplay.setText(firstName);
        emailDisplay.setText(email);
        phoneDisplay.setText(phone);
        countryDisplay.setText(country);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }
}