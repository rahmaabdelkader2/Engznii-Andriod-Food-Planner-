package com.example.login_gui_firebase.model.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.login_gui_firebase.model.pojo.Meal;

@Database(entities = {Meal.class}, version = 3)  // Incremented version from 2 to 3
public abstract class MealDatabase extends RoomDatabase {
    private static volatile MealDatabase instance = null;

    public abstract MealDao MealDAO();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE mealdb ADD COLUMN scheduledDate TEXT");
        }
    };

    // Add new migration for version 2 to 3
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add isFavorite column with default value 0 (false)
            database.execSQL("ALTER TABLE mealdb ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static synchronized MealDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            MealDatabase.class, "meals.db")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)  // Add both migrations
                    .build();
        }
        return instance;
    }
}