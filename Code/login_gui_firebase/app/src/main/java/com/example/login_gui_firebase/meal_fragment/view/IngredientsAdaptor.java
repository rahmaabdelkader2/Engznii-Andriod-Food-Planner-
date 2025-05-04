package com.example.login_gui_firebase.meal_fragment.view;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.login_gui_firebase.R;

import java.util.List;
public class IngredientsAdaptor extends RecyclerView.Adapter<IngredientsAdaptor.IngredientViewHolder> {

    private List<IngredientItem> ingredientItems;

    public IngredientsAdaptor(List<IngredientItem> ingredientItems) {
        this.ingredientItems = ingredientItems;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_card, parent, false); // Using your existing meal card layout
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        IngredientItem item = ingredientItems.get(position);

        // Set the ingredient name as the main text
        holder.itemName.setText(item.getName());


        // Use area field for the measurement
        holder.itemArea.setText(item.getMeasure());

        // Load ingredient image
        Glide.with(holder.itemView.getContext())
                .load("https://www.themealdb.com/images/ingredients/" + item.getName() + "-Small.png")
                .placeholder(R.drawable.image_placeholder)
                .into(holder.mealImage);
    }

    @Override
    public int getItemCount() {
        return ingredientItems.size();
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        ImageView mealImage;
        TextView itemName, itemCategory, itemArea;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.mealImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemCategory = itemView.findViewById(R.id.itemCategory);
            itemArea = itemView.findViewById(R.id.itemArea);
        }
    }

    public static class IngredientItem {
        private String name;
        private String measure;

        public IngredientItem(String name, String measure) {
            this.name = name;
            this.measure = measure;
        }

        public String getName() { return name; }
        public String getMeasure() { return measure; }
    }
}