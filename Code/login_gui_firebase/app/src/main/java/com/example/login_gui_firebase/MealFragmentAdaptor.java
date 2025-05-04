//package com.example.login_gui_firebase;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.login_gui_firebase.model.pojo.Meal;
//
//import java.util.List;
//
//public class MealFragmentAdaptor extends RecyclerView.Adapter<MealFragmentAdaptor.MealViewHolder> {
//
//    private List<Meal> mealList;
//    private Context context;
//    private OnMealClickListener listener;
//
//    public interface OnMealClickListener {
//        void onMealClick(Meal meal);
//    }
//
//    public MealFragmentAdaptor(List<Meal> mealList, Context context, OnMealClickListener listener) {
//        this.mealList = mealList;
//        this.context = context;
//        this.listener = listener;
//    }
//
//    @NonNull
//    @Override
//    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_meal_card, parent, false);
//        return new MealViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
//        Meal meal = mealList.get(position);
//
//        holder.itemName.setText(meal.getStrMeal());
//        holder.itemCategory.setText(meal.getStrCategory());
//        holder.itemArea.setText(meal.getStrArea());
//
//        Glide.with(context)
//                .load(meal.getStrMealThumb())
//                .placeholder(R.drawable.image_placeholder)
//                .into(holder.mealImage);
//
//        holder.itemView.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onMealClick(meal);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return mealList.size();
//    }
//
//    public void updateMeals(List<Meal> newMeals) {
//        mealList = newMeals;
//        notifyDataSetChanged();
//    }
//
//    public static class MealViewHolder extends RecyclerView.ViewHolder {
//        ImageView mealImage;
//        TextView itemName, itemCategory, itemArea;
//
//        public MealViewHolder(@NonNull View itemView) {
//            super(itemView);
//            mealImage = itemView.findViewById(R.id.mealImage);
//            itemName = itemView.findViewById(R.id.itemName);
//            itemCategory = itemView.findViewById(R.id.itemCategory);
//            itemArea = itemView.findViewById(R.id.itemArea);
//        }
//    }
//}