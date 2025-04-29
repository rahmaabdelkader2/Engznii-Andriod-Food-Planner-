package com.example.login_gui_firebase.model.local;


import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.login_gui_firebase.model.pojo.Meal;

import java.util.List;

public class LocalDataSource implements ILocalDataSource {
    private MealDao mealDao;  // Changed from mealDAO to mealDao for consistency
    private static LocalDataSource instance = null;

    public LocalDataSource(Context context) {
        MealDatabase mealDatabase = MealDatabase.getInstance(context);
        mealDao = mealDatabase.MealDAO();
    }

    public static synchronized LocalDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new LocalDataSource(context);
        }
        return instance;
    }

    @Override
    public void insertMeal(Meal meal) {
        new Thread(() -> mealDao.insertMeal(meal)).start();
    }

    @Override
    public void deleteMeal(Meal meal) {
        new Thread(() -> mealDao.deleteMeal(meal.getIdMeal())).start();  // Fixed to use idMeal
    }

    @Override
    public LiveData<List<Meal>> getAllMeals() {
        return mealDao.getAllMeals();
    }

    // Add these methods
    @Override
    public LiveData<List<Meal>> getFavoriteMeals() {
        return mealDao.getFavoriteMeals();
    }

    @Override
    public void setFavoriteStatus(Meal meal, boolean isFavorite) {
        new Thread(() -> {
            meal.setFavorite(isFavorite);
            mealDao.setFavoriteStatus(meal.getIdMeal(), isFavorite);
        }).start();
    }

    @Override
    public boolean isFavorite(String mealId) {
        return mealDao.isFavorite(mealId);
    }
}