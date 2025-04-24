package com.example.login_gui_firebase.search.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.login_gui_firebase.R;
import com.example.login_gui_firebase.model.pojo.FilteredMeal;
import java.util.ArrayList;
import java.util.List;

public class FilteredMealAdaptor extends RecyclerView.Adapter<FilteredMealAdaptor.FilteredMealViewHolder> {
    private List<FilteredMeal> meals = new ArrayList<>();
    private OnFilteredMealClickListener listener;

    public interface OnFilteredMealClickListener {
        void onFilteredMealClick(String mealId); // Changed to pass mealId directly
    }

    public void setMeals(List<FilteredMeal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    public void setOnFilteredMealClickListener(OnFilteredMealClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FilteredMealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filtered_meals, parent, false);
        return new FilteredMealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilteredMealViewHolder holder, int position) {
        FilteredMeal meal = meals.get(position);
        holder.mealName.setText(meal.getStrMeal());

        Glide.with(holder.itemView.getContext())
                .load(meal.getStrMealThumb())
                .placeholder(R.drawable.image_placeholder)
                .into(holder.mealImage);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && meal.getIdMeal() != null) {
                listener.onFilteredMealClick(meal.getIdMeal());
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    static class FilteredMealViewHolder extends RecyclerView.ViewHolder {
        TextView mealName;
        ImageView mealImage;

        public FilteredMealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealName = itemView.findViewById(R.id.mealName);
            mealImage = itemView.findViewById(R.id.mealImage);
        }
    }
}