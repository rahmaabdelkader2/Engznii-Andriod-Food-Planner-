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

//    @Override
//    public LiveData<List<Meal>> getStoredMeals() {
//        return localDataSource.getAllMeals();
//    }

    @Override
    public void getRandomMeal(MealCallback callback) {
        client.getRandomMeal(new MealCallback() {
            @Override
            public void onSuccess_meal(List<Meal> meals) {
                callback.onSuccess_meal(meals);
                if (meals != null && !meals.isEmpty()) {
                    mealsLiveData.postValue(meals);
                    // Optionally save to local database
                    for (Meal meal : meals) {
                        localDataSource.insertMeal(meal);
                    }
                }
            }

            @Override
            public void onFailure_meal(String errorMsg) {
                callback.onFailure_meal(errorMsg);
            }
        });
    }

    @Override
    public void listAllCategories(CategoriesCallback callback) {
        client.listAllCategories(new CategoriesCallback() {
            @Override
            public void onSuccessCategories(List<Categories> categoriesList) {
                callback.onSuccessCategories(categoriesList);
            }

            @Override
            public void onFailureCategories(String errorMsg) {
                callback.onFailureCategories(errorMsg);
            }
        });
    }

    @Override
    public void listAllAreas(AreaCallback callback) {
        client.listAllAreas(new AreaCallback() {
            @Override
            public void onSuccessArea(List<Area> areaList) {
                callback.onSuccessArea(areaList);
            }

            @Override
            public void onFailureArea(String errorMsg) {
                callback.onFailureArea(errorMsg);
            }
        });
    }

    @Override
    public void listAllIngredients(IngredientsCallback callback) {
        client.listAllIngredients(new IngredientsCallback() {
            @Override
            public void onSuccessIngredients(List<Ingredients> ingredientsList) {
                callback.onSuccessIngredients(ingredientsList);
            }

            @Override
            public void onFailureIngredients(String errorMsg) {
                callback.onFailureIngredients(errorMsg);
            }
        });
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
    public void searchMealByName(String query, MealCallback callback) {
        client.searchMealByName(query, callback);
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