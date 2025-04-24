package com.example.login_gui_firebase.model.remote.retrofit.client;

import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.AreaCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.CategoriesCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.IngredientsCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealFilteredCallback;

public interface IClient {
     void getRandomMeal(MealCallback callback);
    void filterByCategory(String category, MealFilteredCallback callback);
    void filterByArea(String area, MealFilteredCallback callback);
    void filterByIngredient(String ingredient, MealFilteredCallback callback);
    void searchMealByName(String query, MealCallback callback);
    void listAllCategories(CategoriesCallback categoriesCallback);
    void listAllAreas(AreaCallback areaCallback);
    void listAllIngredients(IngredientsCallback ingredientsCallback);



}
