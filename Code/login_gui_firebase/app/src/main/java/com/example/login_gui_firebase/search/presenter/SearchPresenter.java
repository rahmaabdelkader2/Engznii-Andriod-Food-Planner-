package com.example.login_gui_firebase.search.presenter;

import com.example.login_gui_firebase.model.pojo.*;
import com.example.login_gui_firebase.model.repo.IRepo;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.*;
import com.example.login_gui_firebase.search.view.SearchIview;

import java.util.List;


public class SearchPresenter implements ISearchPresenter, CategoriesCallback,
        AreaCallback, IngredientsCallback, MealFilteredCallback {
    private final IRepo repository;
    private final SearchIview view;

    public SearchPresenter(SearchIview view, IRepo repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void listAllCategories() {
        repository.listAllCategories(this);
    }

    @Override
    public void listAllAreas() {
        repository.listAllAreas(this);
    }

    @Override
    public void listAllIngredients() {
        repository.listAllIngredients(this);
    }

    @Override
    public void filterByCategory(String category) {
        repository.filterByCategory(category,this);
    }

    @Override
    public void filterByIngredients(String ingredients) {
        repository.filterByIngredient(ingredients, this);
    }

    @Override
    public void filterByAreas(String areas) {
        repository.filterByArea(areas,this);
    }

    @Override
    public void onSuccessArea(List<Area> areaList) {
        view.showAreas(areaList);
    }

    @Override
    public void onFailureArea(String errorMsg) {
        view.showError(errorMsg);
    }

    @Override
    public void onSuccessCategories(List<Categories> categoriesList) {
        view.showCategories(categoriesList);
    }

    @Override
    public void onFailureCategories(String errorMsg) {
        view.showError(errorMsg);
    }

    @Override
    public void onSuccessIngredients(List<Ingredients> ingredientsList) {
        view.showIngredients(ingredientsList);
    }

    @Override
    public void onFailureIngredients(String errorMsg) {
        view.showError(errorMsg);
    }

    @Override
    public void onSuccessFilteredMeal(List<FilteredMeal> filteredMeals) {
        view.showFilteredMeals(filteredMeals);
    }

    @Override
    public void onFailureFilteredMeal(String errorMsg) {
        view.showError(errorMsg);
    }
}
