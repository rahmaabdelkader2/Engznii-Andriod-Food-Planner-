package com.example.login_gui_firebase.model.remote.retrofit.response;


import com.example.login_gui_firebase.model.pojo.Categories;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoriesResponse {
    @SerializedName("meals")
    private List<Categories> categories;
    public List<Categories> getCategories(){
        return categories;
    }
}
