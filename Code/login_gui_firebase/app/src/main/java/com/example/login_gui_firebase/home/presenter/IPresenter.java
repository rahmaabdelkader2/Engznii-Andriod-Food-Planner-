package com.example.login_gui_firebase.home.presenter;


import com.example.login_gui_firebase.model.pojo.Meal;

public interface IPresenter {
    void getRandomMeal();
    void filterByCategory(String category);
    void filterByArea(String area);
    void filterByIngredient(String ingredient);
    void searchMealByName(String query);
    void addMealToFavorites(Meal meal);
    void removeMealFromFavorites(Meal meal);

}