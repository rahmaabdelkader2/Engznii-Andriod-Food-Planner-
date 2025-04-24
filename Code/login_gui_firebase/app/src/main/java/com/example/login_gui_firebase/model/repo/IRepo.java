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
    //LiveData<List<Meal>> getStoredMeals();
    void getRandomMeal(MealCallback callback);
    void listAllCategories(CategoriesCallback callback);
    void listAllAreas(AreaCallback callback);
    void listAllIngredients(IngredientsCallback callback);



    void filterByCategory(String category, MealFilteredCallback callback);
    void filterByArea(String area, MealFilteredCallback callback);
    void filterByIngredient(String ingredient, MealFilteredCallback callback);

    void getMealDetails(String mealId, MealCallback callback);

    void insertMeal(Meal meal);
    void deleteMeal(Meal meal);
}