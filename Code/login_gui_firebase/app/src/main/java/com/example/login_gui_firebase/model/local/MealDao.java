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

    @Query("SELECT * FROM mealdb WHERE idMeal = :mealId LIMIT 1")
    Meal getMealById(String mealId);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMeal(Meal meal);

    @Query("DELETE FROM mealdb WHERE idMeal = :idMeal AND userId = :userId")
    void deleteMeal(String idMeal, String userId);


    @Query("UPDATE mealdb SET scheduledDate = NULL WHERE idMeal = :mealId AND userId = :userId")
    void unscheduleMeal(String mealId,String userId);

    @Query("SELECT * FROM mealdb WHERE scheduledDate = :date AND userId = :userId")
    LiveData<List<Meal>> getMealsForDate(String date,String userId);

    @Query("SELECT COUNT(*) FROM mealdb WHERE idMeal = :mealId AND scheduledDate = :date")
    LiveData<Boolean>  isMealScheduled(String mealId, String date);

    @Query("SELECT * FROM mealdb WHERE isFavorite = 1 AND userId = :userId")
    LiveData<List<Meal>> getFavoriteMeals(String userId);


    @Query("SELECT isFavorite FROM mealdb WHERE idMeal = :mealId AND userId = :userId")
    LiveData<Boolean>  isFavorite(String mealId, String userId);

    @Query("SELECT COUNT(*) FROM mealdb WHERE idMeal = :mealId AND userId = :userId")
    LiveData<Integer> mealExists(String mealId, String userId);

    @Query("SELECT scheduledDate FROM mealdb WHERE idMeal = :mealId AND userId = :userId")
    LiveData<String> getScheduledDate(String mealId, String userId);


    @Query("UPDATE mealdb SET isFavorite = :isFavorite WHERE idMeal = :mealId AND userId = :userId")
    void setFavoriteStatus(String mealId, boolean isFavorite, String userId);

    @Query("UPDATE mealdb SET scheduledDate = :date WHERE idMeal = :mealId AND userId = :userId")
    void scheduleMeal(String mealId, String date, String userId);

}