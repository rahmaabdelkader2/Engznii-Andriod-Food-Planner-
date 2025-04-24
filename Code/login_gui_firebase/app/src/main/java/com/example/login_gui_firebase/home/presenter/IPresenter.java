package com.example.login_gui_firebase.home.presenter;

import com.example.login_gui_firebase.model.pojo.Meal;

public interface IPresenter {
    void getRandomMeal();

    void getTenRandomMeals();

    void addMealToFavorites(Meal meal);
    void removeMealFromFavorites(Meal meal);
}