package com.example.login_gui_firebase.model.repo;

import androidx.lifecycle.LiveData;

import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.AreaCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.CategoriesCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.IngredientsCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealFilteredCallback;

import java.util.List;

public interface IRepo {
    // Remote data operations
    void getRandomMeal(MealCallback callback);
    void listAllCategories(CategoriesCallback callback);
    void listAllAreas(AreaCallback callback);
    void listAllIngredients(IngredientsCallback callback);
    void filterByCategory(String category, MealFilteredCallback callback);
    void filterByArea(String area, MealFilteredCallback callback);
    void filterByIngredient(String ingredient, MealFilteredCallback callback);
    void getMealDetails(String mealId, MealCallback callback);

    // Local data operations (updated to include userId parameters)
    LiveData<List<Meal>> getFavouriteMeals(String userId);
    void setFavoriteStatus(String mealId, boolean isFavorite, String userId);
    LiveData<Boolean> isFavorite(String mealId, String userId);

    void insertMeal(Meal meal, String userId);
//    void deleteMeal(Meal meal, String userId);

    // Meal scheduling operations
    void scheduleMeal(String mealId, String date, String userId);
    void unscheduleMeal(String mealId, String userId);
    LiveData<List<Meal>> getMealsForDate(String date, String userId);

    LiveData<Integer> mealExists(String mealId, String userId);


    LiveData<Boolean> isMealScheduled(String mealId,String date);
    LiveData<String> getScheduledDate(String mealId, String userId);

    void getMealDetailsOffline(String mealId, MealCallback callback);
}
