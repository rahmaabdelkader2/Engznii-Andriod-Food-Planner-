package com.example.login_gui_firebase.model.local;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.login_gui_firebase.model.pojo.Meal;

import java.util.List;

@Dao
public interface MealDao {
    @Query("SELECT * FROM mealdb")
    LiveData<List<Meal>> getAllMeals();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMeal(Meal meal);

    @Query("DELETE FROM mealdb WHERE idMeal = :idMeal")
    void deleteMeal(String idMeal);

    // New queries for scheduled meals
    @Query("UPDATE mealdb SET scheduledDate = :date WHERE idMeal = :mealId")
    void scheduleMeal(String mealId, String date);

    @Query("UPDATE mealdb SET scheduledDate = null WHERE idMeal = :mealId")
    void unscheduleMeal(String mealId);

    @Query("SELECT * FROM mealdb WHERE scheduledDate = :date")
    LiveData<List<Meal>> getMealsForDate(String date);

    @Query("SELECT scheduledDate FROM mealdb WHERE idMeal = :mealId")
    String getScheduledDateForMeal(String mealId);
}