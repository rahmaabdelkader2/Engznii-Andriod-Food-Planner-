package com.example.login_gui_firebase.search.presenter;

import com.example.login_gui_firebase.model.pojo.*;
import com.example.login_gui_firebase.model.repo.IRepo;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.*;
import com.example.login_gui_firebase.search.view.SearchIview;

import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private final IRepo repository;
    private final SearchIview view;

    public SearchPresenter( SearchIview view,IRepo repository) {
        this.view = view;
        this.repository = repository;

    }

    @Override
    public void listAllCategories() {
        //view.showLoading();
        repository.listAllCategories(new CategoriesCallback() {
            @Override
            public void onSuccessCategories(List<Categories> categories) {
                //view.hideLoading();
                view.showCategories(categories);
            }

            @Override
            public void onFailureCategories(String error) {
                // view.hideLoading();
                //view.showError(error);
            }
        });
    }

    @Override
    public void listAllAreas() {
        //view.showLoading();
        repository.listAllAreas(new AreaCallback() {
            @Override
            public void onSuccessArea(List<Area> areas) {
                // view.hideLoading();
                view.showAreas(areas);
            }

            @Override
            public void onFailureArea(String error) {
                //view.hideLoading();
                view.showError(error);
            }
        });
    }

    @Override
    public void listAllIngredients() {
        //view.showLoading();
        repository.listAllIngredients(new IngredientsCallback() {
            @Override
            public void onSuccessIngredients(List<Ingredients> ingredients) {
                //view.hideLoading();
                view.showIngredients(ingredients);
            }

            @Override
            public void onFailureIngredients(String error) {
                //view.hideLoading();
                view.showError(error);
            }
        });
    }

}
