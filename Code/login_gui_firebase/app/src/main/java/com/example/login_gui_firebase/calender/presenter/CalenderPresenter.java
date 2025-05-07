package com.example.login_gui_firebase.calender.presenter;

import androidx.lifecycle.LiveData;
import com.example.login_gui_firebase.calender.view.ICalenderView;
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.model.repo.IRepo;
import java.util.List;

public class CalenderPresenter implements ICalenderPresenter {
    private ICalenderView view;
    private final IRepo repository;

    public CalenderPresenter(ICalenderView view, IRepo repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public LiveData<List<Meal>> getMealsForDate(String date, String userId) {
        return repository.getMealsForDate(date, userId);
    }

//    @Override
//    public void scheduleMeal(String mealId, String date, String userId) {
//        repository.scheduleMeal(mealId, date, userId);
//    }
//
//    @Override
//    public void unscheduleMeal(String mealId, String userId) {
//        repository.unscheduleMeal(mealId, userId);
//    }
//
//    @Override
//    public LiveData<Boolean> isMealScheduled(String mealId, String date) {
//        return repository.isMealScheduled(mealId, date);
//    }

    public void detachView() {
        this.view = null;
    }
}