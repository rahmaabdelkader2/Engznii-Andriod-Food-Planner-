package com.example.login_gui_firebase.meal_fragment.presenter;

import androidx.lifecycle.LiveData;

import com.example.login_gui_firebase.model.pojo.Meal;

public interface IMealFragmentPresenter {
    void insertMeal(Meal meal, String userId);
    LiveData<Boolean>  isFavorite(String mealId, String userId);
    void setFavoriteStatus(String mealId, boolean isFavorite, String userId);
    void deleteMeal(Meal meal,String userId);
    void getMealDetails(String mealId);

    void scheduleMeal(String mealId, String date, String userId);
    void unscheduleMeal(String mealId, String userId);
    LiveData<Boolean> isMealScheduled(String mealId, String date);



}
