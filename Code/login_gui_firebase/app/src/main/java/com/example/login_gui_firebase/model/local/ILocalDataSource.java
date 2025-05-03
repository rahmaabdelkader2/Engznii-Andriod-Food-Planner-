package com.example.login_gui_firebase.model.local;

import androidx.lifecycle.LiveData;

import com.example.login_gui_firebase.model.pojo.Meal;

import java.util.List;

public interface ILocalDataSource {
    LiveData<List<Meal>> getAllMeals();
    void insertMeal(Meal meal);
    void deleteMeal(Meal meal);

    LiveData<List<Meal>> getFavoriteMeals();
    void setFavoriteStatus(Meal meal, boolean isFavorite);
    boolean isFavorite(String mealId);
}
