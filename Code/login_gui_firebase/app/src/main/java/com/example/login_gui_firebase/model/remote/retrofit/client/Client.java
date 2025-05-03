package com.example.login_gui_firebase.model.remote.retrofit.client;

import android.util.Log;

import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.AreaCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.CategoriesCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.IngredientsCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealFilteredCallback;
import com.example.login_gui_firebase.model.remote.retrofit.response.AreasResponse;
import com.example.login_gui_firebase.model.remote.retrofit.response.CategoriesResponse;
import com.example.login_gui_firebase.model.remote.retrofit.response.IngredientsResponse;
import com.example.login_gui_firebase.model.remote.retrofit.response.MealResponse;
import com.example.login_gui_firebase.model.remote.retrofit.response.MealsResponseFiltered;
import com.example.login_gui_firebase.model.remote.retrofit.service.MealServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client implements IClient {

    private static final String TAG = "Client";
    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";

    private static Client clientInstance = null;

    private static MealServices service;

    public static synchronized Client getInstance() {
        if (clientInstance == null) {
            clientInstance = new Client();
        }
        return clientInstance;
    }

    private Client() {
        service = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MealServices.class);
    }

    public void getRandomMeal(MealCallback callback) {
        // enqueue a network call to get a random meal , function ghza mn el api
        service.getRandomMeal().enqueue(new Callback<MealResponse>() { // aknoo anonymous inner class
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) { // call request mn el net // response dah natigt el acall
                if (response.isSuccessful() && response.body() != null ) {
                    callback.onSuccess_meal(response.body().getMeals()); // response.body dah el object elly feh el data , meal response array mn elmeals
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                callback.onFailure_meal("Network error: " + t.getMessage());
            }
        });
    }

    public void filterByCategory(String category, MealFilteredCallback callback) {
        service.filterByCategory(category).enqueue(new Callback<MealsResponseFiltered>() {
            @Override
            public void onResponse(Call<MealsResponseFiltered> call, Response<MealsResponseFiltered> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccessFilteredMeal(response.body().getMeals());
                    Log.d("+++++++++++++", response.toString());
                }
            }

            @Override
            public void onFailure(Call<MealsResponseFiltered> call, Throwable t) {
                callback.onFailureFilteredMeal("Network error: " + t.getMessage());
            }
        });
    }

    public void filterByArea(String area, MealFilteredCallback callback) {
        service.filterByArea(area).enqueue(new Callback<MealsResponseFiltered>() {
            @Override
            public void onResponse(Call<MealsResponseFiltered> call, Response<MealsResponseFiltered> response) {
                if (response.isSuccessful() && response.body() != null ) {
                    callback.onSuccessFilteredMeal(response.body().getMeals());
                }
            }

            @Override
            public void onFailure(Call<MealsResponseFiltered> call, Throwable t) {
                callback.onFailureFilteredMeal("Network error: " + t.getMessage());
            }
        });
    }

    public void filterByIngredient(String ingredient, MealFilteredCallback callback) {
        service.filterByIngredient(ingredient).enqueue(new Callback<MealsResponseFiltered>() {
            @Override
            public void onResponse(Call<MealsResponseFiltered> call, Response<MealsResponseFiltered> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccessFilteredMeal(response.body().getMeals());
                }
            }

            @Override
            public void onFailure(Call<MealsResponseFiltered> call, Throwable t) {
                callback.onFailureFilteredMeal("Network error: " + t.getMessage());
            }
        });
    }


    @Override
    public void listAllCategories(CategoriesCallback categoriesCallback) {
        service.getAllCategories().enqueue(new Callback<CategoriesResponse>() {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoriesCallback.onSuccessCategories(response.body().getCategories());
                } else {
                    categoriesCallback.onFailureCategories("No categories found or empty response");
                }
            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                categoriesCallback.onFailureCategories("Network error: " + t.getMessage());
            }
        });
    }

    @Override
    public void listAllAreas(AreaCallback areaCallback) {
        service.getAllAreas().enqueue(new Callback<AreasResponse>() {
            @Override
            public void onResponse(Call<AreasResponse> call, Response<AreasResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    areaCallback.onSuccessArea(response.body().getAreas());
                } else {
                    areaCallback.onFailureArea("No areas found or empty response");
                }
            }

            @Override
            public void onFailure(Call<AreasResponse> call, Throwable t) {
                areaCallback.onFailureArea("Network error: " + t.getMessage());
            }
        });
    }

    @Override
    public void listAllIngredients(IngredientsCallback ingredientsCallback) {
        service.getAllIngredients().enqueue(new Callback<IngredientsResponse>() {
            @Override
            public void onResponse(Call<IngredientsResponse> call, Response<IngredientsResponse> response) {
                if (response.isSuccessful() && response.body() != null ) {
                    ingredientsCallback.onSuccessIngredients(response.body().getIngredients());
                } else {
                    ingredientsCallback.onFailureIngredients("No ingredients found or empty response");
                }
            }

            @Override
            public void onFailure(Call<IngredientsResponse> call, Throwable t) {
                ingredientsCallback.onFailureIngredients("Network error: " + t.getMessage());
            }
        });
    }

    public void getMealDetails(String mealId, MealCallback callback) {
        service.getMealDetails(mealId).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess_meal(response.body().getMeals());
                } else {
                    callback.onFailure_meal("Meal not found");
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                callback.onFailure_meal("Network error: " + t.getMessage());
            }
        });
    }
}