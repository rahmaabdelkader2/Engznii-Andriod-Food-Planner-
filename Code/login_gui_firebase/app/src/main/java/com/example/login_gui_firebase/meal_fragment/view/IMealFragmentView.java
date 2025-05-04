package com.example.login_gui_firebase.meal_fragment.view;

import com.example.login_gui_firebase.model.pojo.Meal;

public interface IMealFragmentView {
    void updateUI(Meal meal);
    void showError(String error);

}
