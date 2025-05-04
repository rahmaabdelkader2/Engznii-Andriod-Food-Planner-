package com.example.login_gui_firebase.model.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.login_gui_firebase.model.local.ILocalDataSource;
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.model.remote.retrofit.client.IClient;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.AreaCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.CategoriesCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.IngredientsCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealFilteredCallback;

import java.util.List;

public class Repo implements IRepo {
    private static Repo instance;
    private final ILocalDataSource localDataSource;
    private final IClient client;
    private final MutableLiveData<List<Meal>> mealsLiveData = new MutableLiveData<>();

    private Repo(ILocalDataSource localDataSource, IClient client) {
        this.localDataSource = localDataSource;
        this.client = client;
    }

    public static synchronized Repo getInstance(ILocalDataSource localDataSource, IClient client) {
        if (instance == null) {
            instance = new Repo(localDataSource, client);
        }
        return instance;
    }

    // Remote data operations
    @Override
    public void getRandomMeal(MealCallback callback) {
        client.getRandomMeal(callback);
    }

    @Override
    public void listAllCategories(CategoriesCallback callback) {
        client.listAllCategories(callback);
    }

    @Override
    public void listAllAreas(AreaCallback callback) {
        client.listAllAreas(callback);
    }

    @Override
    public void listAllIngredients(IngredientsCallback callback) {
        client.listAllIngredients(callback);
    }

    @Override
    public void filterByCategory(String category, MealFilteredCallback callback) {
        client.filterByCategory(category, callback);
    }

    @Override
    public void filterByArea(String area, MealFilteredCallback callback) {
        client.filterByArea(area, callback);
    }

    @Override
    public void filterByIngredient(String ingredient, MealFilteredCallback callback) {
        client.filterByIngredient(ingredient, callback);
    }

    @Override
    public void getMealDetails(String mealId, MealCallback callback) {
        client.getMealDetails(mealId, callback);
    }

    // Local data operations
    @Override
    public LiveData<List<Meal>> getFavouriteMeals(String userId) {
        return localDataSource.getFavouriteMeals(userId);
    }

    @Override
    public void setFavoriteStatus(String mealId, boolean isFavorite, String userId) {
        localDataSource.setFavoriteStatus(mealId, isFavorite, userId);
    }

    @Override
    public LiveData<Boolean> isFavorite(String mealId, String userId) {
        return localDataSource.isFavorite(mealId, userId);
    }

    @Override
    public void insertMeal(Meal meal, String userId) {
        meal.setUserId(userId);
        localDataSource.insertMeal(meal);
    }

    @Override
    public void deleteMeal(Meal meal, String userId) {
        localDataSource.deleteMeal(meal, userId);
    }

    // Meal scheduling operations
    @Override
    public void scheduleMeal(String mealId, String date, String userId) {
        localDataSource.scheduleMeal(mealId, date, userId);
    }

    @Override
    public void unscheduleMeal(String mealId, String userId) {
        localDataSource.unscheduleMeal(mealId, userId);
    }

    @Override
    public LiveData<List<Meal>> getMealsForDate(String date, String userId) {
        return localDataSource.getMealsForDate(date, userId);
    }

    @Override
    public LiveData<Boolean> isMealScheduled(String mealId, String date) {
        return localDataSource.isMealScheduled(mealId, date);
    }
}