package com.example.login_gui_firebase.search.view;

import com.example.login_gui_firebase.model.pojo.Area;
import com.example.login_gui_firebase.model.pojo.Categories;
import com.example.login_gui_firebase.model.pojo.Ingredients;

public interface OnItemClickListener {
    void onCategoryClick(Categories category);
    void onAreaClick(Area area);
    void onIngredientClick(Ingredients ingredient);
}
