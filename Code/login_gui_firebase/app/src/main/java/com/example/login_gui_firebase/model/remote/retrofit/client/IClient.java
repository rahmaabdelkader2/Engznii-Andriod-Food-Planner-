package com.example.login_gui_firebase.model.remote.retrofit.client;

import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealFilteredCallback;

public interface IClient {
     void getRandomMeal(MealCallback callback);
    void filterByCategory(String category, MealFilteredCallback callback);
    void filterByArea(String area, MealFilteredCallback callback);
    void filterByIngredient(String ingredient, MealFilteredCallback callback);
    void searchMealByName(String query, MealCallback callback);


}
