package com.example.login_gui_firebase.home.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.login_gui_firebase.R;
import com.example.login_gui_firebase.model.pojo.Meal;

import java.util.List;

public class mealsAdapter extends RecyclerView.Adapter<mealsAdapter.MealViewHolder> {
    private List<Meal> meals;
    private Context context;
    private OnMealClickListener onMealClickListener;

    public interface OnMealClickListener {
        void onMealClick(Meal meal);
    }

    public mealsAdapter(Context context, List<Meal> meals, OnMealClickListener listener) {
        this.context = context;
        this.meals = meals;
        this.onMealClickListener = listener;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal_card, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = meals.get(position);

        Glide.with(context)
                .load(meal.getStrMealThumb())
                .into(holder.mealImage);

        holder.mealName.setText(meal.getStrMeal());
        holder.mealCategory.setText("Category: " + meal.getStrCategory());
        holder.mealArea.setText("Area: " + meal.getStrArea());

        holder.itemView.setOnClickListener(v -> {
            if (onMealClickListener != null) {
                onMealClickListener.onMealClick(meal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public void updateMeals(List<Meal> newMeals) {
        meals = newMeals;
        notifyDataSetChanged();
    }

    public List<Meal> getMeals() {
        return meals;
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView mealImage;
        TextView mealName, mealCategory, mealArea;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.mealImage);
            mealName = itemView.findViewById(R.id.itemName);
            mealCategory = itemView.findViewById(R.id.itemCategory);
            mealArea = itemView.findViewById(R.id.itemArea);
        }
    }
}