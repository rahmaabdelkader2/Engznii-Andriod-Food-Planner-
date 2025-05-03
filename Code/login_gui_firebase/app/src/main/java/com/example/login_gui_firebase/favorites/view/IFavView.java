package com.example.login_gui_firebase.favorites.view;

import com.example.login_gui_firebase.model.pojo.Meal;

import java.util.List;

public interface IFavView {
    void showFavouriteMeals(List<Meal> meals);
    void showError(String errorMessage);

}
