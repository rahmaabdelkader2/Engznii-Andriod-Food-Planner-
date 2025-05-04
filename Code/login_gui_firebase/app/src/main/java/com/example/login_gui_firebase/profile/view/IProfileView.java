package com.example.login_gui_firebase.profile.view;

public interface IProfileView {
    void showUserData(String fullName, String email, String phone, String country);
    void showError(String message);
}