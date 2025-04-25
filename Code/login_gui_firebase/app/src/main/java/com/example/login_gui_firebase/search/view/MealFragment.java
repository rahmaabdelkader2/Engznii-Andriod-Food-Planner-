package com.example.login_gui_firebase.search.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.login_gui_firebase.R;
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.model.remote.retrofit.client.Client;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealCallback;

import java.util.List;

public class MealFragment extends Fragment {
    private static final String ARG_MEAL_ID = "meal_id";

    private ImageView mealImage;
    private TextView mealName, mealCategory, mealArea, mealInstructions;
    private LinearLayout ingredientsContainer;

    private ImageView backButton;

    public static MealFragment newInstance(String mealId) {
        MealFragment fragment = new MealFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEAL_ID, mealId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_details, container, false);

        // Initialize views
        mealImage = view.findViewById(R.id.mealImage);
        mealName = view.findViewById(R.id.mealName);
        mealCategory = view.findViewById(R.id.mealCategory);
        mealArea = view.findViewById(R.id.mealArea);
        mealInstructions = view.findViewById(R.id.mealInstructions);
        ingredientsContainer = view.findViewById(R.id.ingredientsContainer);
        backButton = view.findViewById(R.id.btn_back);

        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                ((SearchActivity)getActivity()).onBackPressed();
            }
        });

        String mealId = getArguments().getString(ARG_MEAL_ID);
        fetchMealDetails(mealId);

        return view;
    }

    private void fetchMealDetails(String mealId) {
        Client.getInstance().getMealDetails(mealId, new MealCallback() {
            @Override
            public void onSuccess_meal(List<Meal> meals) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (meals != null && !meals.isEmpty()) {
                            updateUI(meals.get(0)); // Get first meal from list
                        } else {
                            Toast.makeText(getContext(), "No meal details found", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure_meal(String errorMsg) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void updateUI(Meal meal) {
        // Set basic info
        mealName.setText(meal.getStrMeal());
        mealCategory.setText("Category: " + meal.getStrCategory());
        mealArea.setText("Origin: " + meal.getStrArea());
        mealInstructions.setText(meal.getStrInstructions());

        // Load image
        Glide.with(this)
                .load(meal.getStrMealThumb())
                .into(mealImage);

        // Add ingredients and measures
        addIngredientsToView(meal);
    }

    private void addIngredientsToView(Meal meal) {
        ingredientsContainer.removeAllViews();

        for (int i = 1; i <= 20; i++) {
            try {
                String ingredient = (String) meal.getClass().getMethod("getStrIngredient" + i).invoke(meal);
                String measure = (String) meal.getClass().getMethod("getStrMeasure" + i).invoke(meal);

                if (ingredient != null && !ingredient.trim().isEmpty()) {
                    TextView ingredientView = new TextView(getContext());
                    ingredientView.setText(String.format("• %s: %s", ingredient, measure));
                    ingredientView.setTextSize(16);
                    ingredientView.setPadding(0, 8, 0, 8);
                    ingredientsContainer.addView(ingredientView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}