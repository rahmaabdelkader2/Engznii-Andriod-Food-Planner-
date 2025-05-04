package com.example.login_gui_firebase.search.view;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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

import com.airbnb.lottie.LottieAnimationView;
import com.example.login_gui_firebase.meal_fragment.view.MealFragment;
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

public class SearchFragment extends Fragment implements SearchIview ,OnItemClickListener,OnFilteredMealClickListener{
    private ISearchPresenter searchPresenter;
    private SearchAdaptor adapter;
    private FilteredMealAdaptor filteredMealAdapter;
    private List<Object> currentItems = new ArrayList<>();
    private String currentMode = "categories";
    private RecyclerView recyclerView;
    private FrameLayout fragmentContainer;

    private LottieAnimationView connectionLostAnimation;
    private View connectionLostContainer;

    private List<FilteredMeal> originalFilteredMeals = new ArrayList<>(); // Store original list

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_activty, container, false);

        initializeViews(view);
        setupPresenter();
        setupAdapters();
        setupButtonListeners(view);
        setupSearchView(view);
        checkConnection();
        setupConnectionRetryListener();
        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.search_recycler_view);
        fragmentContainer = view.findViewById(R.id.fragment_containerfav);
        fragmentContainer.setVisibility(View.GONE);

        // Initialize connection lost container
        connectionLostContainer = view.findViewById(R.id.connection_lost_container);
        connectionLostAnimation = connectionLostContainer.findViewById(R.id.animationView4);

        // Make sure it's initially hidden
        connectionLostContainer.setVisibility(View.GONE);
    }
    private void setupPresenter() {
        ILocalDataSource localDataSource = new LocalDataSource(getContext());
        IClient client = Client.getInstance();
        IRepo repository = Repo.getInstance(localDataSource, client);
        searchPresenter = new SearchPresenter(this, repository);
    }
    private void setupAdapters() {
        adapter = new SearchAdaptor(new ArrayList<>(), this);
        filteredMealAdapter = new FilteredMealAdaptor(new ArrayList<>(), this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
    private void setupButtonListeners(View view) {
        Button btnCategories = view.findViewById(R.id.btn_categories);
        Button btnAreas = view.findViewById(R.id.btn_areas);
        Button btnIngredients = view.findViewById(R.id.btn_ingredients);

        btnCategories.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                currentMode = "categories";
                recyclerView.setAdapter(adapter);
                searchPresenter.listAllCategories();
            } else {
                connectionLostContainer.setVisibility(View.VISIBLE);
                connectionLostAnimation.playAnimation();
            }
        });

        btnAreas.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                currentMode = "areas";
                recyclerView.setAdapter(adapter);
                searchPresenter.listAllAreas();
            } else {
                connectionLostContainer.setVisibility(View.VISIBLE);
                connectionLostAnimation.playAnimation();
            }
        });

        btnIngredients.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                currentMode = "ingredients";
                recyclerView.setAdapter(adapter);
                searchPresenter.listAllIngredients();
            } else {
                connectionLostContainer.setVisibility(View.VISIBLE);
                connectionLostAnimation.playAnimation();
            }
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
                .replace(R.id.fragment_containerfav, mealFragment)
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
        currentItems = new ArrayList<>(categories);
        adapter.setItems(currentItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);
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
        originalFilteredMeals = new ArrayList<>(meals);
        filteredMealAdapter.setMeals(meals);
        recyclerView.setAdapter(filteredMealAdapter);
        recyclerView.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);
    }
    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    public boolean onBackSearchPressed() {
        if (fragmentContainer.getVisibility() == View.VISIBLE) {
            getChildFragmentManager().popBackStack();
            fragmentContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }
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
    @Override
    public void onFilteredMealClick(String mealId) {
        showMealFragment(mealId);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
    private void checkConnection() {
        if (!isNetworkAvailable()) {
            connectionLostContainer.setVisibility(View.VISIBLE);
            connectionLostAnimation.playAnimation();
            recyclerView.setVisibility(View.GONE);
        } else {
            connectionLostContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            loadInitialData();
        }
    }
    private void loadInitialData() {
        if (isNetworkAvailable()) {
            switch (currentMode) {
                case "categories":
                    searchPresenter.listAllCategories();
                    break;
                case "areas":
                    searchPresenter.listAllAreas();
                    break;
                case "ingredients":
                    searchPresenter.listAllIngredients();
                    break;
            }
        }
    }
    private void setupConnectionRetryListener() {
        connectionLostContainer.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                connectionLostContainer.setVisibility(View.GONE);
                connectionLostAnimation.cancelAnimation();
                loadInitialData();
            } else {
                recyclerView.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Still offline. Please check your connection",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}