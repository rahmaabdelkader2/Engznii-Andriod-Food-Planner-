package com.example.login_gui_firebase.profile.view;

public interface IProfileView {
    void showUserData(String fullName, String email, String phone, String country);
    void loadProfileImage(String imageUrl);
    void showError(String message);
}