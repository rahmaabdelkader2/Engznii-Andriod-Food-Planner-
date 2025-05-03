package com.example.login_gui_firebase.calender.view;

import com.example.login_gui_firebase.model.pojo.Meal;

import java.util.List;

public interface ICalenderView {

    void showMealsForDate(List<Meal> meals);
    void showError(String errorMessage);
}