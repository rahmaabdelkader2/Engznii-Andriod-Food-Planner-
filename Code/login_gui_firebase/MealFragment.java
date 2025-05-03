package com.example.login_gui_firebase;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.login_gui_firebase.model.local.MealDatabase;
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.model.remote.retrofit.client.Client;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MealFragment extends Fragment {
    private static final String ARG_MEAL_ID = "meal_id";
    private static final String ARG_SELECTED_DATE = "selected_date";

    private ImageView mealImage, calendarIcon, favoriteIcon;
    private TextView mealName, mealCategory, mealArea, mealInstructions;
    private LinearLayout ingredientsContainer;
    private WebView webView;
    private ImageView backButton;
    private String currentMealId;
    private String currentSelectedDate;
    private Meal currentMeal;

    public static MealFragment newInstance(String mealId, String selectedDate) {
        MealFragment fragment = new MealFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEAL_ID, mealId);
        args.putString(ARG_SELECTED_DATE, selectedDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_details, container, false);
        initViews(view);
        setupWebView();
        setupClickListeners();

        if (getArguments() != null) {
            currentMealId = getArguments().getString(ARG_MEAL_ID);
            currentSelectedDate = getArguments().getString(ARG_SELECTED_DATE);
            if (currentMealId != null) {
                fetchMealDetails(currentMealId);
                updateCalendarIcon();
            }
        }

        return view;
    }

    private void initViews(View view) {
        mealImage = view.findViewById(R.id.mealImage);
        mealName = view.findViewById(R.id.mealName);
        mealCategory = view.findViewById(R.id.mealCategory);
        mealArea = view.findViewById(R.id.mealArea);
        mealInstructions = view.findViewById(R.id.mealInstructions);
       // ingredientsContainer = view.findViewById(R.id.ingredientsContainer);
        webView = view.findViewById(R.id.webview);
        backButton = view.findViewById(R.id.btn_back);
        calendarIcon = view.findViewById(R.id.addtocal);
        favoriteIcon = view.findViewById(R.id.addtofav);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        calendarIcon.setOnClickListener(v -> {
            if (currentSelectedDate == null) {
                showDatePickerDialog();
            } else {
                toggleMealInCalendar();
            }
        });

        favoriteIcon.setOnClickListener(v -> {
            if (currentMeal != null) {
                toggleFavoriteStatus();
            }
        });
    }

    private void toggleFavoriteStatus() {
        boolean newFavoriteStatus = !currentMeal.isFavorite();
        currentMeal.setFavorite(newFavoriteStatus);
        updateFavoriteIcon(newFavoriteStatus);

        new Thread(() -> {
            MealDatabase database = MealDatabase.getInstance(requireContext());
            database.MealDAO().setFavoriteStatus(currentMeal.getIdMeal(), newFavoriteStatus);
            requireActivity().runOnUiThread(() -> {
                String message = newFavoriteStatus ? "Added to favorites" : "Removed from favorites";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        // Change both icon and color
        int iconRes = isFavorite ? R.drawable.fav2 : R.drawable.fav;
        int colorRes = isFavorite ? R.color.secondary_color : R.color.secondary_color;

        favoriteIcon.setImageResource(iconRes);
        favoriteIcon.setColorFilter(ContextCompat.getColor(requireContext(), colorRes));
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = formatDate(selectedYear, selectedMonth, selectedDay);
                    if (isDateWithinAllowedRange(selectedDate)) {
                        currentSelectedDate = selectedDate;
                        scheduleMealForDate(currentSelectedDate);
                        updateCalendarIcon();
                        Toast.makeText(getContext(), "Meal saved for " + currentSelectedDate, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "You can only plan meals for today and the next 7 days", Toast.LENGTH_SHORT).show();
                    }
                },
                year, month, day);

        // Set min and max dates (in milliseconds)
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_YEAR, 7); // Today + 7 days

        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private boolean isDateWithinAllowedRange(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date selectedDate = sdf.parse(date);
            Date today = new Date();
            Date oneWeekLater = new Date(today.getTime() + (7 * 24 * 60 * 60 * 1000));

            // Clear time part for accurate comparison
            today = sdf.parse(sdf.format(today));
            oneWeekLater = sdf.parse(sdf.format(oneWeekLater));

            return !selectedDate.before(today) && !selectedDate.after(oneWeekLater);
        } catch (ParseException e) {
            return false;
        }
    }

    private void updateCalendarIcon() {
        if (currentSelectedDate == null) {
            calendarIcon.setImageResource(R.drawable.calender_svgrepo_com);
            calendarIcon.setContentDescription("Add to calendar");
        } else {
            calendarIcon.setImageResource(R.drawable.selected_calender);
            calendarIcon.setContentDescription("Remove from calendar");
        }
    }

    private void toggleMealInCalendar() {
        MealDatabase database = MealDatabase.getInstance(requireContext());
        new Thread(() -> {
            boolean isScheduled = database.MealDAO().isMealScheduled(currentMealId, currentSelectedDate) > 0;

            if (isScheduled) {
                database.MealDAO().unscheduleMeal(currentMealId);
                currentSelectedDate = null;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Meal removed from calendar", Toast.LENGTH_SHORT).show();
                    updateCalendarIcon();
                });
            } else {
                database.MealDAO().scheduleMeal(currentMealId, currentSelectedDate);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Meal added to calendar", Toast.LENGTH_SHORT).show();
                    updateCalendarIcon();
                });
            }
        }).start();
    }

    private void scheduleMealForDate(String date) {
        MealDatabase database = MealDatabase.getInstance(requireContext());
        new Thread(() -> {
            database.MealDAO().scheduleMeal(currentMealId, date);
        }).start();
    }

    private String formatDate(int year, int month, int day) {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
    }

    private void setupWebView() {
        webView.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
    }

    private void fetchMealDetails(String mealId) {
        Client.getInstance().getMealDetails(mealId, new MealCallback() {
            @Override
            public void onSuccess_meal(List<Meal> meals) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (meals != null && !meals.isEmpty()) {
                            currentMeal = meals.get(0);
                            updateUI(currentMeal);
                            checkFavoriteStatus();
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

    private void checkFavoriteStatus() {
        new Thread(() -> {
            MealDatabase database = MealDatabase.getInstance(requireContext());
            boolean isFavorite = database.MealDAO().isFavorite(currentMeal.getIdMeal());
            requireActivity().runOnUiThread(() -> {
                currentMeal.setFavorite(isFavorite);
                updateFavoriteIcon(isFavorite);
            });
        }).start();
    }

    private void updateUI(Meal meal) {
        mealName.setText(meal.getStrMeal());
        mealCategory.setText("Category: " + meal.getStrCategory());
        mealArea.setText("Origin: " + meal.getStrArea());
        mealInstructions.setText(meal.getStrInstructions());

        Glide.with(this)
                .load(meal.getStrMealThumb())
                .into(mealImage);

        setupIngredientsRecyclerView(meal);
        loadYoutubeVideo(meal.getStrYoutube());
    }

    private void loadYoutubeVideo(String youtubeUrl) {
        if (youtubeUrl != null && !youtubeUrl.isEmpty()) {
            String videoId = extractYoutubeVideoId(youtubeUrl);
            if (videoId != null) {
                String videoHtml = "<html><body style='margin:0;padding:0;'><iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" +
                        videoId + "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
                webView.loadData(videoHtml, "text/html", "utf-8");
                webView.setVisibility(View.VISIBLE);
            } else {
                webView.setVisibility(View.GONE);
            }
        } else {
            webView.setVisibility(View.GONE);
        }
    }

    private String extractYoutubeVideoId(String url) {
        String videoId = null;
        if (url != null && url.trim().length() > 0) {
            if (url.contains("youtu.be/")) {
                videoId = url.substring(url.lastIndexOf("/") + 1);
            } else if (url.contains("v=")) {
                videoId = url.substring(url.indexOf("v=") + 2);
                int ampPos = videoId.indexOf('&');
                if (ampPos != -1) {
                    videoId = videoId.substring(0, ampPos);
                }
            } else if (url.contains("youtube.com/embed/")) {
                videoId = url.substring(url.lastIndexOf("/") + 1);
            }
        }
        return videoId;
    }
    private void setupIngredientsRecyclerView(Meal meal) {
        List<IngredientsAdaptor.IngredientItem> ingredientItems = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            try {
                String ingredient = (String) meal.getClass().getMethod("getStrIngredient" + i).invoke(meal);
                String measure = (String) meal.getClass().getMethod("getStrMeasure" + i).invoke(meal);

                if (ingredient != null && !ingredient.trim().isEmpty()) {
                    ingredientItems.add(new IngredientsAdaptor.IngredientItem(ingredient, measure));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        RecyclerView ingredientsRecyclerView = getView().findViewById(R.id.ingredientsRecyclerView);

        // Set horizontal LinearLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        ingredientsRecyclerView.setLayoutManager(layoutManager);


        ingredientsRecyclerView.setAdapter(new IngredientsAdaptor(ingredientItems));
    }

    @Override
    public void onDestroyView() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroyView();
    }
}