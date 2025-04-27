// Calender.java
package com.example.login_gui_firebase;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login_gui_firebase.model.local.MealDatabase;
import com.example.login_gui_firebase.model.pojo.Meal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Calender extends AppCompatActivity {
    private RecyclerView mealsRecyclerView;
    private MealAdapter mealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        CalendarView calendarView = findViewById(R.id.calendarView);
        TextView myDate = findViewById(R.id.myDate);
        mealsRecyclerView = findViewById(R.id.mealsRecyclerView);

        // Setup RecyclerView
        mealAdapter = new MealAdapter(null);
        mealsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mealsRecyclerView.setAdapter(mealAdapter);

        // Set initial date
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        checkMealForDate(today);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            checkMealForDate(selectedDate);
        });
    }

    private void checkMealForDate(String date) {
        MealDatabase database = MealDatabase.getInstance(this);
        database.MealDAO().getMealsForDate(date).observe(this, meals -> {
            if (meals != null && !meals.isEmpty()) {
                myDate.setText("Meals planned for " + date + ": " + meals.size());
                mealAdapter.updateMeals(meals);
                Toast.makeText(this, "You have " + meals.size() + " meals planned", Toast.LENGTH_SHORT).show();
            } else {
                myDate.setText("No meals planned for " + date);
                mealAdapter.updateMeals(null);
                Toast.makeText(this, "No meals planned for " + date, Toast.LENGTH_SHORT).show();
            }
        });
    }
}