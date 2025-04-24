package com.example.login_gui_firebase.home.presenter;

import android.util.Log;

import com.example.login_gui_firebase.home.view.IView;
import com.example.login_gui_firebase.model.pojo.FilteredMeal;
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.model.repo.IRepo;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealCallback;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealFilteredCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Presenter implements IPresenter {
    private IView view;
    private IRepo repository;

    public Presenter(IView view, IRepo repository) {
        this.view = view;
        this.repository = repository;
    }

    public void getRandomMeal() {
        repository.getRandomMeal(new MealCallback() {
            @Override
            public void onSuccess_meal(List<Meal> meals) {
                if (meals != null && !meals.isEmpty()) {
                    view.showRandomMeal(meals.get(0));
                } else {
                    view.showError("No meals found");
                }
            }




            @Override
            public void onFailure_meal(String errorMsg) {
                Log.e("Presenter", "Error: " + errorMsg);
                view.showError(errorMsg);
            }
        });
    }

    public void getTenRandomMeals() {
        List<Meal> meals = new ArrayList<>();
        AtomicInteger remaining = new AtomicInteger(10);

        for (int i = 0; i < 10; i++) {
            repository.getRandomMeal(new MealCallback() {
                @Override
                public void onSuccess_meal(List<Meal> mealList) {
                    synchronized (meals) {
                        if (mealList != null && !mealList.isEmpty()) {
                            meals.add(mealList.get(0));
                        }
                    }
                    checkCompletion(remaining.decrementAndGet());
                }





                @Override
                public void onFailure_meal(String errorMsg) {
                    checkCompletion(remaining.decrementAndGet());
                }

                private void checkCompletion(int count) {
                    if (count == 0) {
                        if (!meals.isEmpty()) {
                            view.showTenRandomMeals(meals);
                        } else {
                            view.showError("Failed to load meals");
                        }
                    }
                }
            });
        }
    }


    @Override
    public void addMealToFavorites(Meal meal) {
        repository.insertMeal(meal);
    }

    @Override
    public void removeMealFromFavorites(Meal meal) {
        repository.deleteMeal(meal);
    }
}