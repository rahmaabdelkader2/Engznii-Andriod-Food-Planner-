package com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks;

import com.example.login_gui_firebase.model.pojo.Categories;

import java.util.List;

public interface CategoriesCallback {
    void onSuccessCategories(List<Categories> categoriesList);
    void onFailureCategories(String errorMsg);
}
