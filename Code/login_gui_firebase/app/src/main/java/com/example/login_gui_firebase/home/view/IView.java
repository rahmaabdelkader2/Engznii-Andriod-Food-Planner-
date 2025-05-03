package com.example.login_gui_firebase.home.view;


import com.example.login_gui_firebase.model.pojo.FilteredMeal;
import com.example.login_gui_firebase.model.pojo.Meal;

import java.util.List;

public interface IView {
    void showRandomMeal(Meal meal);

    void showError(String errorMessage);
    void showMealsByArea(List<Meal> meals);

}
