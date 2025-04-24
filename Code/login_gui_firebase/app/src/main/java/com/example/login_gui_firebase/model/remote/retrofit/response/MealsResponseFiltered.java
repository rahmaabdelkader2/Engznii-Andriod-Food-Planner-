package com.example.login_gui_firebase.model.remote.retrofit.response;

import com.example.login_gui_firebase.model.pojo.FilteredMeal;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MealsResponseFiltered {

    @SerializedName("meals")
    private List<FilteredMeal> filteredMeals;

    public List<FilteredMeal> getMeals() {
        return filteredMeals;
    }

    public void setMeals(List<FilteredMeal> filteredMeals) {
        this.filteredMeals = filteredMeals;
    }
}

