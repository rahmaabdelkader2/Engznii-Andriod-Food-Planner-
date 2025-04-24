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
    public void filterByCategory(String category) {

        repository.filterByCategory(category, new MealFilteredCallback() {
            @Override
            public void onSuccessFilteredMeal(List<FilteredMeal> filteredMeals) {
                //view.hideLoading();
                // Convert FilteredMeal to Meal if needed or update IView to handle FilteredMeal
                // view.showFilteredMeals(convertToMeals(filteredMeals));
            }

            @Override
            public void onFailureFilteredMeal(String errorMsg) {
                //view.hideLoading();
                view.showError(errorMsg);
            }
        });
    }

    // Implement other methods with similar corrections...
    @Override
    public void filterByArea(String area) {
        //view.showLoading();
        repository.filterByArea(area, new MealFilteredCallback() {
            @Override
            public void onSuccessFilteredMeal(List<FilteredMeal> filteredMeals) {
                //view.hideLoading();
                // Handle filtered meals
            }

            @Override
            public void onFailureFilteredMeal(String errorMsg) {
                //view.hideLoading();
                view.showError(errorMsg);
            }
        });
    }

    @Override
    public void filterByIngredient(String ingredient) {
        //view.showLoading();
        repository.filterByIngredient(ingredient, new MealFilteredCallback() {
            @Override
            public void onSuccessFilteredMeal(List<FilteredMeal> filteredMeals) {
                //view.hideLoading();
                // Handle filtered meals
            }

            @Override
            public void onFailureFilteredMeal(String errorMsg) {
                //view.hideLoading();
                view.showError(errorMsg);
            }
        });
    }

    @Override
    public void searchMealByName(String query) {
        //view.showLoading();
        repository.searchMealByName(query, new MealCallback() {
            @Override
            public void onSuccess_meal(List<Meal> meals) {
                //view.hideLoading();
                if (meals != null && !meals.isEmpty()) {
                    view.showRandomMeal(meals.get(0));
                } else {
                    view.showError("No meals found with this name");
                }
            }

            @Override
            public void onFailure_meal(String errorMsg) {
                //view.hideLoading();
                view.showError(errorMsg);
            }
        });
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