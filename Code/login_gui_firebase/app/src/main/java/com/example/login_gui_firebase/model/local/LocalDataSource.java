package com.example.login_gui_firebase.model.local;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.login_gui_firebase.model.pojo.Meal;

import java.util.List;

public class LocalDataSource implements ILocalDataSource {
    private MealDao mealDao;
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
    public void deleteMeal(Meal meal, String userId) {
        new Thread(() -> mealDao.deleteMeal(meal.getIdMeal(),userId)).start();
    }

    @Override
    public void scheduleMeal(String mealId, String date, String userId) {
        new Thread(() -> mealDao.scheduleMeal(mealId, date, userId)).start();
    }

    @Override
    public void unscheduleMeal(String mealId, String userId) {
        new Thread(() -> mealDao.unscheduleMeal(mealId, userId)).start();
    }

    @Override
    public LiveData<List<Meal>> getMealsForDate(String date, String userId) {
        return mealDao.getMealsForDate(date, userId);
    }

    @Override
    public LiveData<Boolean>  isMealScheduled(String mealId, String date) {
        return mealDao.isMealScheduled(mealId, date);
    }

    @Override
    public LiveData<List<Meal>> getFavouriteMeals(String userId) {
        return mealDao.getFavoriteMeals(userId);
    }

    @Override
    public void setFavoriteStatus(String mealId, boolean isFavorite, String userId) {
        new Thread(() -> mealDao.setFavoriteStatus(mealId, isFavorite, userId)).start();
    }

    @Override
    public LiveData<Boolean>  isFavorite(String mealId, String userId) {
        return mealDao.isFavorite(mealId, userId);
    }

    @Override
    public LiveData<Integer> mealExists(String mealId, String userId) {
        return mealDao.mealExists(mealId, userId);
    }

    @Override
    public LiveData<String> getScheduledDate(String mealId, String userId) {
        return mealDao.getScheduledDate(mealId, userId);
    }

    @Override
    public Meal getMealById(String mealId) {
        return mealDao.getMealById(mealId);
    }
}