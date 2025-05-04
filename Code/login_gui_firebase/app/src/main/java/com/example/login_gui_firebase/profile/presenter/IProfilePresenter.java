package com.example.login_gui_firebase.profile.presenter;


public interface IProfilePresenter {
    void loadUserData();
    String getCountryName(String countryCode);
    void detachView();
}
