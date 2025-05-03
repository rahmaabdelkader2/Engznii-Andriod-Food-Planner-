package com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks;

import com.example.login_gui_firebase.model.pojo.Ingredients;

import java.util.List;

public interface IngredientsCallback {
    void onSuccessIngredients(List<Ingredients> ingredientsList);
    void onFailureIngredients(String errorMsg);
}
