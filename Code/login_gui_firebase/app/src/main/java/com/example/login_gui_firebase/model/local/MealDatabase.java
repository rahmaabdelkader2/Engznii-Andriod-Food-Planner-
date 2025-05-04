package com.example.login_gui_firebase.model.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.login_gui_firebase.model.pojo.Meal;

@Database(entities = {Meal.class}, version = 4)
public abstract class MealDatabase extends RoomDatabase {
    private static volatile MealDatabase instance = null;

    public abstract MealDao MealDAO();

    public static synchronized MealDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            MealDatabase.class, "meals.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}