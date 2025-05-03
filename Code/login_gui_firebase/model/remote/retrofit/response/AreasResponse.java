package com.example.login_gui_firebase.model.remote.retrofit.response;

import com.example.login_gui_firebase.model.pojo.Area;
import com.google.gson.annotations.SerializedName;



import java.util.List;

public class AreasResponse {
    @SerializedName("meals")
    private List<Area> areas;
    public List<Area> getAreas(){
        return areas;
    }
}
