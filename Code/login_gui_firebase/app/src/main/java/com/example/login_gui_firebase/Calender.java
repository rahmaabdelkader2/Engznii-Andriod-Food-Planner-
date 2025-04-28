package com.example.login_gui_firebase;

import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login_gui_firebase.model.local.MealDatabase;
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.search.view.MealFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Calender extends AppCompatActivity {
    // UI Components
    private CalendarView calendarView;
    private TextView myDate;
    private RecyclerView mealsRecyclerView;
    private LinearLayout mainContent;
    private FrameLayout fragmentContainer;

    // Adapter
    private MealCalenderAdaptor mealAdapter;

    // Current state
    private String currentSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        initializeViews();
        setupRecyclerView();
        setupCalendar();
        loadInitialData();
    }

    private void initializeViews() {
        calendarView = findViewById(R.id.calendarView);
        myDate = findViewById(R.id.myDate);
        mealsRecyclerView = findViewById(R.id.mealsRecyclerView);
        mainContent = findViewById(R.id.main_content);
        fragmentContainer = findViewById(R.id.fragment_container);
    }

    private void setupRecyclerView() {
        mealAdapter = new MealCalenderAdaptor(new MealCalenderAdaptor.OnMealClickListener() {
            @Override
            public void onRemoveClick(Meal meal) {
                removeMealFromCalendar(meal);
            }

            @Override
            public void onMealClick(Meal meal) {
                showMealFragment(meal.getIdMeal());
            }
        });

        mealsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mealsRecyclerView.setAdapter(mealAdapter);
    }

    private void setupCalendar() {
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            currentSelectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            checkMealsForDate(currentSelectedDate);
        });
    }

    private void loadInitialData() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        currentSelectedDate = today;
        checkMealsForDate(today);
    }

    private void showMealFragment(String mealId) {
        // Hide main content
        mainContent.setVisibility(View.GONE);

        // Create and show fragment
        MealFragment mealFragment = MealFragment.newInstance(mealId, currentSelectedDate);

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

        fragmentContainer.setVisibility(View.VISIBLE);
    }

    private void removeMealFromCalendar(Meal meal) {
        MealDatabase database = MealDatabase.getInstance(this);
        new Thread(() -> {
            database.MealDAO().deleteMeal(meal.getIdMeal());
            runOnUiThread(() -> {
                Toast.makeText(this, "Meal removed from calendar", Toast.LENGTH_SHORT).show();
                checkMealsForDate(currentSelectedDate);
            });
        }).start();
    }

    private void checkMealsForDate(String date) {
        MealDatabase database = MealDatabase.getInstance(this);
        database.MealDAO().getMealsForDate(date).observe(this, meals -> {
            if (meals != null && !meals.isEmpty()) {
                myDate.setText(String.format("Meals planned for %s: %d", date, meals.size()));
                mealAdapter.updateMeals(meals);
            } else {
                myDate.setText(String.format("No meals planned for %s", date));
                mealAdapter.updateMeals(null);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (fragmentContainer.getVisibility() == View.VISIBLE) {
            // Return to calendar view
            mainContent.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.GONE);
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to activity
        checkMealsForDate(currentSelectedDate);
    }
}