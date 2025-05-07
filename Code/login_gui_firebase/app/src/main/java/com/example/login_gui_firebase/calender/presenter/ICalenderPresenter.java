package com.example.login_gui_firebase.calender.presenter;

import androidx.lifecycle.LiveData;
import com.example.login_gui_firebase.model.pojo.Meal;
import java.util.List;

public interface ICalenderPresenter {
    LiveData<List<Meal>> getMealsForDate(String date, String userId);
}