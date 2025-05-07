package com.example.login_gui_firebase.favorites.presenter;


import androidx.lifecycle.LiveData;
import com.example.login_gui_firebase.favorites.view.IFavView;
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.model.repo.IRepo;

import java.util.List;

public class FavPresenter implements IFavPresenter {
    private IFavView view;
    private IRepo repository;

    public FavPresenter(IFavView view, IRepo repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public LiveData<List<Meal>> getFavouriteMeals(String userId) {
            return repository.getFavouriteMeals(userId);
    }
    @Override
    public LiveData<Boolean> isMealScheduled(String mealId, String date) {
        return repository.isMealScheduled(mealId, date);
    }
    @Override
    public LiveData<String> getScheduledDate(String mealId, String userId) {
        return repository.getScheduledDate(mealId, userId);
    }
}

