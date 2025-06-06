package com.example.login_gui_firebase.meal_fragment.presenter;

import androidx.lifecycle.LiveData;
import com.example.login_gui_firebase.meal_fragment.view.IMealFragmentView;
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealCallback;
import com.example.login_gui_firebase.model.repo.IRepo;

import java.util.List;

public class MealFragmentPresenter implements IMealFragmentPresenter, MealCallback {
    IRepo repository;
    IMealFragmentView view;

    public MealFragmentPresenter(IMealFragmentView view, IRepo repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void insertMeal(Meal meal, String userId) {
        repository.insertMeal(meal, userId);
    }

    @Override
    public LiveData<Boolean> isFavorite(String mealId, String userId) {
        return repository.isFavorite(mealId, userId);
    }

    @Override
    public void setFavoriteStatus(String mealId, boolean isFavorite, String userId) {
        repository.setFavoriteStatus(mealId, isFavorite, userId);
    }


    @Override
    public void getMealDetails(String mealId) {
        repository.getMealDetails(mealId, new MealCallback() {
            @Override
            public void onSuccess_meal(List<Meal> meals) {
                if (meals != null && !meals.isEmpty()) {
                    view.updateUI(meals.get(0));
                } else {
                    view.showError("Meal not found");
                }
            }

            @Override
            public void onFailure_meal(String errorMsg) {
                // Check if we're offline and try to get from local storage
                repository.getMealDetailsOffline(mealId, new MealCallback() {
                    @Override
                    public void onSuccess_meal(List<Meal> meals) {
                        if (meals != null && !meals.isEmpty()) {
                            view.updateUI(meals.get(0));
                        } else {
                            view.showError(errorMsg);
                        }
                    }

                    @Override
                    public void onFailure_meal(String offlineErrorMsg) {
                        view.showError(offlineErrorMsg);
                    }
                });
            }
        });
    }

    @Override
    public void scheduleMeal(String mealId, String date, String userId) {
        repository.scheduleMeal(mealId, date, userId);
    }

    @Override
    public void unscheduleMeal(String mealId, String userId) {
        repository.unscheduleMeal(mealId, userId);
    }

    @Override
    public LiveData<Boolean> isMealScheduled(String mealId, String date) {
        return repository.isMealScheduled(mealId, date);
    }


    @Override
    public LiveData<String> getScheduledDate(String mealId, String userId) {
        return repository.getScheduledDate(mealId, userId);
    }

    @Override
    public LiveData<Integer> mealExists(String mealId, String userId) {
        return repository.mealExists(mealId, userId);
    }

    @Override
    public void onSuccess_meal(List<Meal> singleMeal) {
        view.updateUI(singleMeal.get(0));
    }

    @Override
    public void onFailure_meal(String errorMsg) {
        view.showError(errorMsg);
    }

}