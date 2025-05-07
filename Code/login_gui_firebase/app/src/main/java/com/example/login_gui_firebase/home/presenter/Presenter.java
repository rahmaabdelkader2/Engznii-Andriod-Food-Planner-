package com.example.login_gui_firebase.home.presenter;

import android.util.Log;

import com.example.login_gui_firebase.home.view.IView;
import com.example.login_gui_firebase.model.pojo.FilteredMeal;
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealFilteredCallback;
import com.example.login_gui_firebase.model.repo.IRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Presenter implements IPresenter ,MealCallback, MealFilteredCallback{
    private IView view;
    private IRepo repository;

    public Presenter(IView view, IRepo repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void getRandomMeal() {
        repository.getRandomMeal(this);
    }

    @Override
    public void getMealsByArea(String area) {
        repository.filterByArea(area,this);
    }

    private void fetchMealDetails(List<FilteredMeal> filteredMeals) {
        List<Meal> meals = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);

        for (FilteredMeal filteredMeal : filteredMeals) {
            repository.getMealDetails(filteredMeal.getIdMeal(), new MealCallback() {
                @Override
                public void onSuccess_meal(List<Meal> mealDetails) {
                    if (mealDetails != null && !mealDetails.isEmpty()) {
                        meals.add(mealDetails.get(0));
                    }


                    if (counter.incrementAndGet() == filteredMeals.size()) {
                        if (!meals.isEmpty()) {
                            view.showMealsByArea(meals);
                        } else {
                            view.showError("Could not load meal details");
                        }
                    }
                }

                @Override
                public void onFailure_meal(String errorMsg) {
                    Log.e("Presenter", "Error loading meal details: " + errorMsg);
                    if (counter.incrementAndGet() == filteredMeals.size() && !meals.isEmpty()) {
                        view.showMealsByArea(meals);
                    }
                }
            });
        }
    }

    @Override
    public void onSuccess_meal(List<Meal> singleMeal) {
        if (singleMeal != null && !singleMeal.isEmpty()) {
            view.showRandomMeal(singleMeal.get(0));
        } else {
            view.showError("No meals found");
        }
    }

    @Override
    public void onFailure_meal(String errorMsg) {
        Log.e("Presenter", "Error: " + errorMsg);
        view.showError(errorMsg);
    }

    @Override
    public void onSuccessFilteredMeal(List<FilteredMeal> filteredMeals) {
        if (filteredMeals != null && !filteredMeals.isEmpty()) {
            fetchMealDetails(filteredMeals);
        } else {
            view.showError("No meals found for this area ");
        }
    }

    @Override
    public void onFailureFilteredMeal(String errorMsg) {
        view.showError(errorMsg);
    }
}