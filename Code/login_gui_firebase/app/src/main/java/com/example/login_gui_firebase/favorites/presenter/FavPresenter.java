package com.example.login_gui_firebase.favorites.presenter;


import android.util.Log;

import com.example.login_gui_firebase.favorites.view.FavFragment;
import com.example.login_gui_firebase.favorites.view.IFavView;
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.model.repo.IRepo;

public class FavPresenter implements IFavPresenter {
        private IFavView view;
        private IRepo repository;

        public FavPresenter(IFavView view, IRepo repository) {
            this.view = view;
            this.repository = repository;
        }

    @Override
    public void getFavouriteMeals() {
        repository.getFavoriteMeals().observe(((FavFragment) view).getViewLifecycleOwner(), meals -> {
            if (meals != null) {
                view.showFavouriteMeals(meals); // Always show meals, whether empty or not
            } else {
                view.showError("Failed to load favourites");
            }
        });
    }

        @Override
        public void removeMealFromFavorites(Meal meal) {
            new Thread(() -> {
                try {
                    repository.deleteMeal(meal);
                } catch (Exception e) {
                    Log.e("FavouritesPresenter", "Error removing meal: " + e.getMessage());
                    ((FavFragment) view).requireActivity().runOnUiThread(() ->
                            view.showError("Failed to remove meal"));
                }
            }).start();
        }

        public void detachView() {
            this.view = null;
        }
    }

