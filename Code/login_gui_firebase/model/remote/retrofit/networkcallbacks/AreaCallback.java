package com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks;

import com.example.login_gui_firebase.model.pojo.Area;

import java.util.List;

public interface AreaCallback {
    void onSuccessArea(List<Area> areaList);
    void onFailureArea(String errorMsg);
}
