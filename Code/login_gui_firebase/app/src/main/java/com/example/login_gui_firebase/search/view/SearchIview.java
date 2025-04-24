package com.example.login_gui_firebase.search.view;

import com.example.login_gui_firebase.model.pojo.Area;
import com.example.login_gui_firebase.model.pojo.Categories;
import com.example.login_gui_firebase.model.pojo.FilteredMeal;
import com.example.login_gui_firebase.model.pojo.Ingredients;
import com.example.login_gui_firebase.model.pojo.Meal;

import java.util.List;

public interface SearchIview {
    void showCategories(List<Categories> categories);
    void showAreas(List<Area> areas);
    void showIngredients(List<Ingredients> ingredients);

    void showFilteredMeals(List<FilteredMeal> meals);
    void showError(String message);

}
