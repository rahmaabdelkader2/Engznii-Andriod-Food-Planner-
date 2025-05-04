package com.example.login_gui_firebase.favorites.presenter;

import androidx.lifecycle.LiveData;

import com.example.login_gui_firebase.model.pojo.Meal;

import java.util.List;

public interface IFavPresenter {
    LiveData<List<Meal>> getFavouriteMeals(String userId);
}
