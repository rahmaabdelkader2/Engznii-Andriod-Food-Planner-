package com.example.login_gui_firebase.search.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login_gui_firebase.R;
import com.example.login_gui_firebase.model.local.ILocalDataSource;
import com.example.login_gui_firebase.model.local.LocalDataSource;
import com.example.login_gui_firebase.model.pojo.*;
import com.example.login_gui_firebase.model.remote.retrofit.client.Client;
import com.example.login_gui_firebase.model.remote.retrofit.client.IClient;
import com.example.login_gui_firebase.model.repo.IRepo;
import com.example.login_gui_firebase.model.repo.Repo;
import com.example.login_gui_firebase.search.presenter.ISearchPresenter;
import com.example.login_gui_firebase.search.presenter.SearchPresenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements SearchIview {
    private ISearchPresenter Searchpresenter;
    private SearchAdaptor adapter;
    private FilteredMealAdaptor filteredMealAdapter;
    private List<Object> currentItems = new ArrayList<>();
    private String currentMode = "categories";
    private RecyclerView recyclerView;
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_activty);

        ILocalDataSource localDataSource = new LocalDataSource(this);
        IClient client = Client.getInstance();
        IRepo repository = Repo.getInstance(localDataSource, client);

        Searchpresenter = new SearchPresenter(this, repository);

        recyclerView = findViewById(R.id.search_recycler_view);
        SearchView searchView = findViewById(R.id.search_view);
        Button btnCategories = findViewById(R.id.btn_categories);
        Button btnAreas = findViewById(R.id.btn_areas);
        Button btnIngredients = findViewById(R.id.btn_ingredients);
        fragmentContainer = findViewById(R.id.fragment_container);

        adapter = new SearchAdaptor();
        filteredMealAdapter = new FilteredMealAdaptor();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fragmentContainer.setVisibility(View.GONE);

        adapter.setOnItemClickListener(new SearchAdaptor.OnItemClickListener() {
            @Override
            public void onCategoryClick(Categories category) {
                Searchpresenter.filterByCategory(category.getStrCategory());
            }

            @Override
            public void onAreaClick(Area area) {
                Searchpresenter.filterByAreas(area.getStrArea());
            }

            @Override
            public void onIngredientClick(Ingredients ingredient) {
                Searchpresenter.filterByIngredients(ingredient.getStrIngredient());
            }
        });

        filteredMealAdapter.setOnFilteredMealClickListener(this::showMealFragment);

        btnCategories.setOnClickListener(v -> {
            currentMode = "categories";
            recyclerView.setAdapter(adapter);
            Searchpresenter.listAllCategories();
        });

        btnAreas.setOnClickListener(v -> {
            currentMode = "areas";
            recyclerView.setAdapter(adapter);
            Searchpresenter.listAllAreas();
        });

        btnIngredients.setOnClickListener(v -> {
            currentMode = "ingredients";
            recyclerView.setAdapter(adapter);
            Searchpresenter.listAllIngredients();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterItems(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterItems(newText);
                return true;
            }
        });
    }

    private void showMealFragment(String mealId) {
        recyclerView.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);

        // Pass null for date since we'll show date picker when user clicks calendar icon
        MealFragment mealFragment = MealFragment.newInstance(mealId, null);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, mealFragment)
                .addToBackStack("meal_details")
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (fragmentContainer.getVisibility() == View.VISIBLE) {
            getSupportFragmentManager().popBackStack();
            fragmentContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    private void filterItems(String query) {
        List<Object> filtered = new ArrayList<>();
        for (Object item : currentItems) {
            String name = "";
            if (item instanceof Categories) {
                name = ((Categories) item).getStrCategory().toLowerCase();
            } else if (item instanceof Area) {
                name = ((Area) item).getStrArea().toLowerCase();
            } else if (item instanceof Ingredients) {
                name = ((Ingredients) item).getStrIngredient().toLowerCase();
            }

            if (name.contains(query.toLowerCase())) {
                filtered.add(item);
            }
        }
        adapter.setItems(filtered);
    }

    @Override
    public void showCategories(List<Categories> categories) {
        currentItems = new ArrayList<>(categories);
        adapter.setItems(currentItems);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showAreas(List<Area> areas) {
        currentItems = new ArrayList<>(areas);
        adapter.setItems(currentItems);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showIngredients(List<Ingredients> ingredients) {
        currentItems = new ArrayList<>(ingredients);
        adapter.setItems(currentItems);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showFilteredMeals(List<FilteredMeal> meals) {
        filteredMealAdapter.setMeals(meals);
        recyclerView.setAdapter(filteredMealAdapter);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}