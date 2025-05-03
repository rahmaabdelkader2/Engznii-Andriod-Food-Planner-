package com.example.login_gui_firebase.calender.presenter;

import android.icu.util.IndianCalendar;
import android.util.Log;

import com.example.login_gui_firebase.calender.view.CalenderFragment;
import com.example.login_gui_firebase.calender.view.ICalenderView;
import com.example.login_gui_firebase.model.local.MealDatabase;
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.model.repo.IRepo;

public class CalenderPresenter implements ICalenderPresenter {
    private ICalenderView view;
    private IRepo repository;

    public CalenderPresenter(ICalenderView view, IRepo repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void getMealsForDate(String date) {
        MealDatabase.getInstance(((CalenderFragment) view).requireContext())
                .MealDAO()
                .getMealsForDate(date)
                .observe(((CalenderFragment) view).getViewLifecycleOwner(), meals -> {
                    if (meals != null) {
                        view.showMealsForDate(meals);
                    } else {
                        view.showError("Failed to load meals for this date");
                    }
                });
    }

    @Override
    public void removeMealFromCalendar(Meal meal) {
        new Thread(() -> {
            try {
                MealDatabase.getInstance(((CalenderFragment) view).requireContext())
                        .MealDAO()
                        .deleteMeal(meal.getIdMeal());
            } catch (Exception e) {
                Log.e("CalPresenter", "Error removing meal: " + e.getMessage());
                ((CalenderFragment) view).requireActivity().runOnUiThread(() ->
                        view.showError("Failed to remove meal"));
            }
        }).start();
    }

    public void detachView() {
        this.view = null;
    }
}