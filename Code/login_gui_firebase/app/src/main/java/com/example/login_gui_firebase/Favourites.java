package com.example.login_gui_firebase;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login_gui_firebase.model.local.MealDatabase;
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.search.view.MealFragment;

public class Favourites extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MealCalenderAdaptor mealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        recyclerView = findViewById(R.id.recyclerViewfav);
        setupRecyclerView();
        loadFavorites();
    }

    private void setupRecyclerView() {
        mealAdapter = new MealCalenderAdaptor(new MealCalenderAdaptor.OnMealClickListener() {
            @Override
            public void onRemoveClick(Meal meal) {
                removeFromFavorites(meal);
            }

            @Override
            public void onMealClick(Meal meal) {
                // Show meal details fragment when a meal is clicked
                showMealFragment(meal.getIdMeal(), null);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mealAdapter);
    }

    private void showMealFragment(String mealId, String selectedDate) {
        // Hide the RecyclerView and title
        recyclerView.setVisibility(View.GONE);
        findViewById(R.id.favtitle).setVisibility(View.GONE);

        MealFragment mealFragment = MealFragment.newInstance(mealId, selectedDate);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the entire content with the fragment
        transaction.replace(R.id.main, mealFragment);  // Using the ConstraintLayout's ID
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadFavorites() {
        MealDatabase database = MealDatabase.getInstance(this);
        database.MealDAO().getFavoriteMeals().observe(this, meals -> {
            if (meals != null) {
                mealAdapter.updateMeals(meals);
            }
        });
    }

    private void removeFromFavorites(Meal meal) {
        MealDatabase database = MealDatabase.getInstance(this);
        new Thread(() -> {
            meal.setFavorite(false);
            database.MealDAO().updateMeal(meal);
            runOnUiThread(() -> loadFavorites());
        }).start();
    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            // Show the RecyclerView and title again
            recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.favtitle).setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }
}