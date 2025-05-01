package com.example.login_gui_firebase.favorites.presenter;

import com.example.login_gui_firebase.model.pojo.Meal;

public interface IFavPresenter {
    void getFavouriteMeals();
    void removeMealFromFavorites(Meal meal);
}
