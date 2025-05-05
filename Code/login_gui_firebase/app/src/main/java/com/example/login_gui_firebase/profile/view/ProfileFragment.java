package com.example.login_gui_firebase.profile.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.login_gui_firebase.Login;
import com.example.login_gui_firebase.R;
import com.example.login_gui_firebase.profile.presenter.ProfilePresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment implements IProfileView {
    private TextView fullNameDisplay, emailDisplay, phoneDisplay, countryDisplay;
    private ImageView profileImage;
    private ProfilePresenter presenter;
    Button logout;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private View connectionLostContainer;
    private LottieAnimationView connectionLostAnimation;
    private View mainContentContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = requireActivity().getSharedPreferences("UserPref", Context.MODE_PRIVATE);

        initializeViews(view);
        setupLogoutButton();
        checkConnection();
        setupConnectionRetryListener();

        presenter = new ProfilePresenter(this);
        presenter.loadUserData();

        loadFirebaseUserProfileImage();
    }

    private void initializeViews(View view) {
        profileImage = view.findViewById(R.id.imageView7);
        fullNameDisplay = view.findViewById(R.id.firstnameDisplay);
        emailDisplay = view.findViewById(R.id.emailDisplay);
        phoneDisplay = view.findViewById(R.id.phoneDisplay);
        countryDisplay = view.findViewById(R.id.countryDisplay);
        logout = view.findViewById(R.id.logout);

        mainContentContainer = view.findViewById(R.id.main_content_container);
        connectionLostContainer = view.findViewById(R.id.connection_lost_container);
        connectionLostAnimation = connectionLostContainer.findViewById(R.id.animationView4);

        connectionLostContainer.setVisibility(View.GONE);
    }

    private void setupLogoutButton() {
        logout.setOnClickListener(v -> {
            // Sign out from Firebase
            mAuth.signOut();

            // Clear all relevant preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("isGuest");
            editor.remove("isSignedIn");
            editor.remove("userId");
            editor.remove("isLoggedIn");
            editor.apply();

            // Redirect to login with clear flags
            Intent intent = new Intent(requireActivity(), Login.class);
            intent.putExtra("FROM_LOGOUT", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finishAffinity();

            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadFirebaseUserProfileImage() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.default_profile)
                    .into(profileImage);
        }
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void checkConnection() {
        if (!isNetworkAvailable()) {
            connectionLostContainer.setVisibility(View.VISIBLE);
            mainContentContainer.setVisibility(View.GONE);
            connectionLostAnimation.playAnimation();
        } else {
            connectionLostContainer.setVisibility(View.GONE);
            mainContentContainer.setVisibility(View.VISIBLE);
            connectionLostAnimation.cancelAnimation();
        }
    }

    private void setupConnectionRetryListener() {
        connectionLostContainer.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                connectionLostContainer.setVisibility(View.GONE);
                mainContentContainer.setVisibility(View.VISIBLE);
                connectionLostAnimation.cancelAnimation();
                presenter.loadUserData();
            } else {
                Toast.makeText(getContext(), "Still offline. Please check your connection",
                        Toast.LENGTH_SHORT).show();
            }
        });
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