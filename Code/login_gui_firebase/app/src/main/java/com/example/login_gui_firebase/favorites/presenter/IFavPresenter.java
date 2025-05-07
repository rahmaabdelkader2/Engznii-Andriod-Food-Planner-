package com.example.login_gui_firebase.favorites.presenter;

import androidx.lifecycle.LiveData;

import com.example.login_gui_firebase.model.pojo.Meal;

import java.util.List;

public interface IFavPresenter {
    LiveData<List<Meal>> getFavouriteMeals(String userId);
    LiveData<Boolean> isMealScheduled(String mealId, String date);
    LiveData<String> getScheduledDate(String mealId, String userId);
}