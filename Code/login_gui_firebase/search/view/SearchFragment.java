package com.example.login_gui_firebase.search.view;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login_gui_firebase.MealFragment;
import com.example.login_gui_firebase.R;
import com.example.login_gui_firebase.model.local.ILocalDataSource;
import com.example.login_gui_firebase.model.local.LocalDataSource;
import com.example.login_gui_firebase.model.pojo.Area;
import com.example.login_gui_firebase.model.pojo.Categories;
import com.example.login_gui_firebase.model.pojo.FilteredMeal;
import com.example.login_gui_firebase.model.pojo.Ingredients;
import com.example.login_gui_firebase.model.remote.retrofit.client.Client;
import com.example.login_gui_firebase.model.remote.retrofit.client.IClient;
import com.example.login_gui_firebase.model.repo.IRepo;
import com.example.login_gui_firebase.model.repo.Repo;
import com.example.login_gui_firebase.search.presenter.ISearchPresenter;
import com.example.login_gui_firebase.search.presenter.SearchPresenter;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SearchIview {
    private ISearchPresenter searchPresenter;
    private SearchAdaptor adapter;
    private FilteredMealAdaptor filteredMealAdapter;
    private List<Object> currentItems = new ArrayList<>();
    private String currentMode = "categories";
    private RecyclerView recyclerView;
    private FrameLayout fragmentContainer;

    private List<FilteredMeal> originalFilteredMeals = new ArrayList<>(); // Store original list

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search_activty, container, false);

        // Initialize dependencies
        ILocalDataSource localDataSource = new LocalDataSource(requireContext());
        IClient client = Client.getInstance();
        IRepo repository = Repo.getInstance(localDataSource, client);
        searchPresenter = new SearchPresenter(this, repository);

        // Initialize views
        initializeViews(view);
        setupAdapters();
        setupButtonListeners(view);
        setupSearchView(view);

        // Load initial data
        //searchPresenter.listAllCategories();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.search_recycler_view);
        fragmentContainer = view.findViewById(R.id.fragment_container);
        fragmentContainer.setVisibility(View.GONE);
    }

    private void setupAdapters() {
        adapter = new SearchAdaptor();
        filteredMealAdapter = new FilteredMealAdaptor();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter); // Set default adapter

        adapter.setOnItemClickListener(new SearchAdaptor.OnItemClickListener() {
            @Override
            public void onCategoryClick(Categories category) {
                searchPresenter.filterByCategory(category.getStrCategory());
            }

            @Override
            public void onAreaClick(Area area) {
                searchPresenter.filterByAreas(area.getStrArea());
            }

            @Override
            public void onIngredientClick(Ingredients ingredient) {
                searchPresenter.filterByIngredients(ingredient.getStrIngredient());
            }
        });

        filteredMealAdapter.setOnFilteredMealClickListener(this::showMealFragment);
    }

    private void setupButtonListeners(View view) {
        Button btnCategories = view.findViewById(R.id.btn_categories);
        Button btnAreas = view.findViewById(R.id.btn_areas);
        Button btnIngredients = view.findViewById(R.id.btn_ingredients);

        btnCategories.setOnClickListener(v -> {
            currentMode = "categories";
            recyclerView.setAdapter(adapter);
            searchPresenter.listAllCategories();
        });

        btnAreas.setOnClickListener(v -> {
            currentMode = "areas";
            recyclerView.setAdapter(adapter);
            searchPresenter.listAllAreas();
        });

        btnIngredients.setOnClickListener(v -> {
            currentMode = "ingredients";
            recyclerView.setAdapter(adapter);
            searchPresenter.listAllIngredients();
        });
    }

    private void setupSearchView(View view) {
        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterItems(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Clear search when text is empty
                if (newText.isEmpty()) {
                    if (recyclerView.getAdapter() == filteredMealAdapter) {
                        filteredMealAdapter.setMeals(new ArrayList<>(originalFilteredMeals));
                    } else {
                        adapter.setItems(currentItems);
                    }
                } else {
                    filterItems(newText);
                }
                return true;
            }
        });
    }

    private void showMealFragment(String mealId) {
        recyclerView.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);

        MealFragment mealFragment = MealFragment.newInstance(mealId, null);

        getChildFragmentManager().beginTransaction()
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

    private void filterItems(String query) {
        if (recyclerView.getAdapter() == adapter) {
            // Search within categories/areas/ingredients (existing code)
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
        } else if (recyclerView.getAdapter() == filteredMealAdapter) {
            // Search within filtered meals
            if (query.isEmpty()) {
                // If search is empty, show all original filtered meals
                filteredMealAdapter.setMeals(new ArrayList<>(originalFilteredMeals));
            } else {
                // Filter based on search query
                List<FilteredMeal> filtered = new ArrayList<>();
                for (FilteredMeal meal : originalFilteredMeals) {
                    if (meal.getStrMeal().toLowerCase().contains(query.toLowerCase())) {
                        filtered.add(meal);
                    }
                }
                filteredMealAdapter.setMeals(filtered);
            }
        }
    }

    @Override
    public void showCategories(List<Categories> categories) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            currentItems = new ArrayList<>(categories);
            adapter.setItems(currentItems);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.GONE);


        });
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
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            Log.d("SearchFragment", "Received " + meals.size() + " filtered meals");
            originalFilteredMeals = new ArrayList<>(meals); // Store the original list
            filteredMealAdapter.setMeals(meals);
            recyclerView.setAdapter(filteredMealAdapter);
            recyclerView.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.GONE);
        });
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public boolean onBackPressed() {
        if (fragmentContainer.getVisibility() == View.VISIBLE) {
            getChildFragmentManager().popBackStack();
            fragmentContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }
}