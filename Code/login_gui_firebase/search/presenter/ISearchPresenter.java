package com.example.login_gui_firebase.search.presenter;


public interface ISearchPresenter {
    // list by tags
    void listAllCategories();
    void listAllAreas();
    void listAllIngredients();

    // filteration

    void filterByCategory(String categories);
    void filterByIngredients(String ingredients);
    void filterByAreas(String areas);

    // after selecting tag search by meal inside the tag

    //void searchMealByName(String query);


}
