package com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks;

import com.example.login_gui_firebase.model.pojo.FilteredMeal;

import java.util.List;

public interface MealFilteredCallback {
    void onSuccessFilteredMeal(List<FilteredMeal>filteredMeals );
    void onFailureFilteredMeal(String errorMsg);

}
