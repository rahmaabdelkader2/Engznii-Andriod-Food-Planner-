package com.example.login_gui_firebase.profile.presenter;


import android.net.Uri;

public interface IProfilePresenter {
    void loadUserData();
    void checkStoragePermission();
    void handlePermissionResult(int requestCode, int[] grantResults);
    void handleImageSelection(Uri imageUri);
    void detachView();
}
