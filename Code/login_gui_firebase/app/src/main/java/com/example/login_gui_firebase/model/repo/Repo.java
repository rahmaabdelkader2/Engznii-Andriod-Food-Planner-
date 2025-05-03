package com.example.login_gui_firebase.model.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.login_gui_firebase.model.local.ILocalDataSource;
import com.example.login_gui_firebase.model.pojo.Area;
import com.example.login_gui_firebase.model.pojo.Categories;
import com.example.login_gui_firebase.model.pojo.Ingredients;
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.model.remote.retrofit.client.IClient;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.AreaCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.CategoriesCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.IngredientsCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealFilteredCallback;

import java.util.Collections;
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
    @Override
    public LiveData<List<Meal>> getFavoriteMeals() {
        return localDataSource.getFavoriteMeals();
    }

    @Override
    public void setFavoriteStatus(Meal meal, boolean isFavorite) {
        localDataSource.setFavoriteStatus(meal, isFavorite);
    }

    @Override
    public boolean isFavorite(String mealId) {
        return localDataSource.isFavorite(mealId);
    }

    @Override
    public void insertMeal(Meal meal) {
        localDataSource.insertMeal(meal);
    }

    @Override
    public void deleteMeal(Meal meal) {
        localDataSource.deleteMeal(meal);
    }

}