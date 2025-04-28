package com.example.login_gui_firebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.login_gui_firebase.model.pojo.Meal;

import java.util.List;

public class MealCalenderAdaptor extends RecyclerView.Adapter<MealCalenderAdaptor.MealViewHolder> {
    private List<Meal> meals;
    private final OnMealClickListener listener;

    public interface OnMealClickListener {
        void onRemoveClick(Meal meal);
        void onMealClick(Meal meal);
    }

    public MealCalenderAdaptor(OnMealClickListener listener) {
        this.listener = listener;
    }

    public void updateMeals(List<Meal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filtered_meals, parent, false);
        return new MealViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        if (meals != null && position < meals.size()) {
            holder.bind(meals.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return meals == null ? 0 : meals.size();
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mealImage;
        private final TextView mealName;
        private final TextView mealCategory;
        private final MealCalenderAdaptor adapter;

        public MealViewHolder(@NonNull View itemView, MealCalenderAdaptor adapter) {
            super(itemView);
            this.adapter = adapter;
            mealImage = itemView.findViewById(R.id.mealImage);
            mealName = itemView.findViewById(R.id.mealName);
            mealCategory = itemView.findViewById(R.id.mealCategory);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && adapter.listener != null) {
                    adapter.listener.onMealClick(adapter.meals.get(position));
                }
            });


        }

        public void bind(Meal meal) {
            mealName.setText(meal.getStrMeal());
            mealCategory.setText(meal.getStrCategory());

            Glide.with(itemView.getContext())
                    .load(meal.getStrMealThumb())
                    .into(mealImage);
        }
    }
}