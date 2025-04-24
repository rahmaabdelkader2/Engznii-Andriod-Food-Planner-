package com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks;

import com.example.login_gui_firebase.model.pojo.Meal;

import java.util.List;

public interface MealCallback {
    void onSuccess_meal(List<Meal> singleMeal);

    void onFailure_meal(String errorMsg);
}
