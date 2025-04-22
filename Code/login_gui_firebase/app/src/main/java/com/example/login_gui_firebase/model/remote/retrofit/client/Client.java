package com.example.login_gui_firebase.model.remote.retrofit.client;

import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealFilteredCallback;
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
        service.getRandomMeal().enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getSingleMeal() != null) {
                    callback.onSuccess_meal(response.body().getSingleMeal());
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
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    callback.onSuccessFilteredMeal(response.body().getMeals());
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
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
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
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    callback.onSuccessFilteredMeal(response.body().getMeals());
                }
            }

            @Override
            public void onFailure(Call<MealsResponseFiltered> call, Throwable t) {
                callback.onFailureFilteredMeal("Network error: " + t.getMessage());
            }
        });
    }

    public void searchMealByName(String query, MealCallback callback) {
        service.searchMealByName(query).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getSingleMeal() != null) {
                    callback.onSuccess_meal(response.body().getSingleMeal());
                } else {
                    callback.onFailure_meal("No meal found with this name");
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                callback.onFailure_meal("Network error: " + t.getMessage());
            }
        });
    }
}