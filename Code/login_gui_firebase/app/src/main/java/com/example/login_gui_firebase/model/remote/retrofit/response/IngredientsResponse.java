package com.example.login_gui_firebase.model.remote.retrofit.response;

import com.example.login_gui_firebase.model.pojo.Ingredients;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IngredientsResponse {
    @SerializedName("meals")
    private List<Ingredients> ingredients;

    public List<Ingredients> getIngredients(){
        return ingredients;
    }
}
