package com.example.login_gui_firebase.model.remote.retrofit.response;

import com.example.login_gui_firebase.model.pojo.FilteredMeal;

import java.util.List;

public class MealsResponseFiltered {

    private List<FilteredMeal> filteredMeals;

    public List<FilteredMeal> getMeals() {
        return filteredMeals;
    }
}
