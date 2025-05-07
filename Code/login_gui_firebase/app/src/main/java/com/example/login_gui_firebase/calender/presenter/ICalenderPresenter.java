package com.example.login_gui_firebase.calender.presenter;

import androidx.lifecycle.LiveData;
import com.example.login_gui_firebase.model.pojo.Meal;
import java.util.List;

public interface ICalenderPresenter {
    LiveData<List<Meal>> getMealsForDate(String date, String userId);
//    void scheduleMeal(String mealId, String date, String userId);
//    void unscheduleMeal(String mealId, String userId);
//    LiveData<Boolean> isMealScheduled(String mealId, String date);
}