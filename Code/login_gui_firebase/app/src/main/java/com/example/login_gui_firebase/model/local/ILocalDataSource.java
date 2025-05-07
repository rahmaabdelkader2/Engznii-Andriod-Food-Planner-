package com.example.login_gui_firebase.model.local;

import androidx.lifecycle.LiveData;

import com.example.login_gui_firebase.model.pojo.Meal;

import java.util.List;

public interface ILocalDataSource {
    void insertMeal(Meal meal);
    void deleteMeal(Meal meal, String userId);
    void scheduleMeal(String mealId, String date,String userId);
    void unscheduleMeal(String mealId,String userId);
    LiveData<List<Meal>> getMealsForDate(String date,String userId);
    LiveData<Boolean> isMealScheduled(String mealId, String date);

    LiveData<List<Meal>> getFavouriteMeals(String userId);
    void setFavoriteStatus(String mealId, boolean isFavorite, String userId);
    LiveData<Boolean> isFavorite(String mealId, String userId);

    LiveData<Integer> mealExists(String mealId, String userId);

    LiveData<String> getScheduledDate(String mealId, String userId);

    Meal getMealById(String mealId);

}
