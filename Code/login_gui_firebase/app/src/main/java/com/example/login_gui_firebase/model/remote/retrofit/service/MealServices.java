package com.example.login_gui_firebase.model.remote.retrofit.service;

import com.example.login_gui_firebase.model.remote.retrofit.response.AreasResponse;
import com.example.login_gui_firebase.model.remote.retrofit.response.CategoriesResponse;
import com.example.login_gui_firebase.model.remote.retrofit.response.IngredientsResponse;
import com.example.login_gui_firebase.model.remote.retrofit.response.MealResponse;
import com.example.login_gui_firebase.model.remote.retrofit.response.MealsResponseFiltered;

import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.Query;

public interface MealServices {

    // show Random meal each time
    @GET("random.php")
    Call<MealResponse> getRandomMeal();


    @GET("categories.php")
    Call<CategoriesResponse> getAllCategories();

    @GET("list.php?a=list")
    Call<AreasResponse> getAllAreas();

    @GET("list.php?i=list")
    Call<IngredientsResponse> getAllIngredients();

     //filtering by category
    @GET("filter.php")
    Call<MealsResponseFiltered> filterByCategory(@Query("c") String category);

    // filtering by area ( country )
    @GET("filter.php")
    Call<MealsResponseFiltered> filterByArea(@Query("a") String area);

    // filtering by Ingredients
    @GET("filter.php")
    Call<MealsResponseFiltered> filterByIngredient(@Query("i") String ingredient);


    @GET("lookup.php")
    Call<MealResponse> getMealDetails(@Query("i") String mealId);

}

