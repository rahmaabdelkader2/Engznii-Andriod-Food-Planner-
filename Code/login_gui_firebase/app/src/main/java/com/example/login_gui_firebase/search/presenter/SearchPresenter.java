package com.example.login_gui_firebase.search.presenter;

import com.example.login_gui_firebase.model.pojo.*;
import com.example.login_gui_firebase.model.repo.IRepo;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.*;
import com.example.login_gui_firebase.search.view.SearchIview;

import java.util.List;


    public class SearchPresenter implements ISearchPresenter {
        private final IRepo repository;
        private final SearchIview view;

        public SearchPresenter(SearchIview view, IRepo repository) {
            this.view = view;
            this.repository = repository;
        }

        @Override
        public void listAllCategories() {
            repository.listAllCategories(new CategoriesCallback() {
                @Override
                public void onSuccessCategories(List<Categories> categories) {
                    view.showCategories(categories);
                }

                @Override
                public void onFailureCategories(String error) {
                    view.showError(error);
                }
            });
        }

        @Override
        public void listAllAreas() {
            repository.listAllAreas(new AreaCallback() {
                @Override
                public void onSuccessArea(List<Area> areas) {
                    view.showAreas(areas);
                }

                @Override
                public void onFailureArea(String error) {
                    view.showError(error);
                }
            });
        }

        @Override
        public void listAllIngredients() {
            repository.listAllIngredients(new IngredientsCallback() {
                @Override
                public void onSuccessIngredients(List<Ingredients> ingredients) {
                    view.showIngredients(ingredients);
                }

                @Override
                public void onFailureIngredients(String error) {
                    view.showError(error);
                }
            });
        }

        @Override
        public void filterByCategory(String category) {
            repository.filterByCategory(category, new MealFilteredCallback() {
                @Override
                public void onSuccessFilteredMeal(List<FilteredMeal> meals) {
                    view.showFilteredMeals(meals);
                }

                @Override
                public void onFailureFilteredMeal(String errorMsg) {
                    view.showError(errorMsg);
                }
            });
        }

        @Override
        public void filterByIngredients(String ingredients) {
            repository.filterByIngredient(ingredients, new MealFilteredCallback() {
                @Override
                public void onSuccessFilteredMeal(List<FilteredMeal> meals) {
                    view.showFilteredMeals(meals);
                }

                @Override
                public void onFailureFilteredMeal(String errorMsg) {
                    view.showError(errorMsg);
                }
            });
        }

        @Override
        public void filterByAreas(String areas) {
            repository.filterByArea(areas, new MealFilteredCallback() {
                @Override
                public void onSuccessFilteredMeal(List<FilteredMeal> meals) {
                    view.showFilteredMeals(meals);
                }

                @Override
                public void onFailureFilteredMeal(String errorMsg) {
                    view.showError(errorMsg);
                }
            });
        }
    }
    // @Override
//    public void searchMealByName(String query) {
//        repository.searchMealByName(query, new MealCallback() {
//            @Override
//            public void onSuccess_meal(List<Meal> meals) {
//                view.showMeals(meals);
//            }
//
//            @Override
//            public void onFailure_meal(String error) {
//                view.showError(error);
//            }
//        });
//    }

