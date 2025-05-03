package com.example.login_gui_firebase.model.remote.retrofit.response;

import com.example.login_gui_firebase.model.pojo.Meal;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MealResponse {
    @SerializedName("meals")
    private List<Meal> meals;

    public List<Meal> getMeals() {
        return meals;
    }
    public Meal getFirstMeal() {
        return (meals != null && !meals.isEmpty()) ? meals.get(0) : null;
    }
}