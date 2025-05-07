package com.example.login_gui_firebase.calender.view;

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

public class MealCalenderAdaptor extends RecyclerView.Adapter<MealCalenderAdaptor.MealViewHolder> {

    private List<Meal> meals;
    private final OnMealClickListener listener;

    public MealCalenderAdaptor(List<Meal> meals,OnMealClickListener listener ) {
        this.meals = meals;
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
                .inflate(R.layout.item_meal_card, parent, false); // Changed to item_meal_card
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        if (meals != null && position < meals.size()) {
            Meal meal = meals.get(position);
            holder.bind(meal);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onMealClick(meal);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return meals == null ? 0 : meals.size();
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mealImage;
        private final TextView mealName;
        private final TextView mealCategory;
        private final TextView mealArea;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.mealImage);
            mealName = itemView.findViewById(R.id.itemName);
            mealCategory = itemView.findViewById(R.id.itemCategory);
            mealArea = itemView.findViewById(R.id.itemArea);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMealClick(meals.get(position));
                }
            });
        }

        public void bind(Meal meal) {
            mealName.setText(meal.getStrMeal());
            mealCategory.setText(meal.getStrCategory());
            mealArea.setText(meal.getStrArea());

            Glide.with(itemView.getContext())
                    .load(meal.getStrMealThumb())
                    .into(mealImage);
        }
    }
}