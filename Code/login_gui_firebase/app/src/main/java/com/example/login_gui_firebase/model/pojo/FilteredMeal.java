package com.example.login_gui_firebase.model.pojo;


import com.google.gson.annotations.SerializedName;

public class FilteredMeal {
    @SerializedName("strMeal")
    private String strMeal;

    @SerializedName("strMealThumb")
    private String strMealThumb;

    @SerializedName("idMeal")
    private String idMeal;

    // Constructor
    public FilteredMeal(String strMeal, String strMealThumb, String idMeal) {
        this.strMeal = strMeal;
        this.strMealThumb = strMealThumb;
        this.idMeal = idMeal;
    }

    // Getters and Setters
    public String getStrMeal() {
        return strMeal;
    }

    public void setStrMeal(String strMeal) {
        this.strMeal = strMeal;
    }

    public String getStrMealThumb() {
        return strMealThumb;
    }

    public void setStrMealThumb(String strMealThumb) {
        this.strMealThumb = strMealThumb;
    }

    public String getIdMeal() {
        return idMeal;
    }

    public void setIdMeal(String idMeal) {
        this.idMeal = idMeal;
    }
}