package com.example.login_gui_firebase.calender.presenter;

import com.example.login_gui_firebase.model.pojo.Meal;

public interface ICalenderPresenter {

    void getMealsForDate(String date);
    void removeMealFromCalendar(Meal meal);
}