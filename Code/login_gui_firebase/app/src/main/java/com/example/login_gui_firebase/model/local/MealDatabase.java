package com.example.login_gui_firebase.model.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.login_gui_firebase.model.pojo.Meal;

@Database(entities = {Meal.class}, version = 2)
public abstract class MealDatabase extends RoomDatabase {
    private static volatile MealDatabase instance = null;

    public abstract MealDao MealDAO();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE mealdb ADD COLUMN scheduledDate TEXT");
        }
    };

    public static synchronized MealDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            MealDatabase.class, "meals.db")
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return instance;
    }
}