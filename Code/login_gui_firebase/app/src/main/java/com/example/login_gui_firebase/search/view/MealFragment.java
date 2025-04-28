package com.example.login_gui_firebase.search.view;

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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.login_gui_firebase.R;
import com.example.login_gui_firebase.model.local.MealDatabase;
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.model.remote.retrofit.client.Client;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealCallback;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MealFragment extends Fragment {
    private static final String ARG_MEAL_ID = "meal_id";
    private static final String ARG_SELECTED_DATE = "selected_date";

    private ImageView mealImage, calendarIcon;
    private TextView mealName, mealCategory, mealArea, mealInstructions;
    private LinearLayout ingredientsContainer;
    private WebView webView;
    private ImageView backButton;
    private String currentMealId;
    private String currentSelectedDate;

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
        ingredientsContainer = view.findViewById(R.id.ingredientsContainer);
        webView = view.findViewById(R.id.webview);
        backButton = view.findViewById(R.id.btn_back);
        calendarIcon = view.findViewById(R.id.addtocal);
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
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    currentSelectedDate = formatDate(selectedYear, selectedMonth, selectedDay);
                    scheduleMealForDate(currentSelectedDate);
                    updateCalendarIcon();
                    Toast.makeText(getContext(), "Meal saved for " + currentSelectedDate, Toast.LENGTH_SHORT).show();
                },
                year, month, day);
        datePickerDialog.show();
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
                            updateUI(meals.get(0));
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
        mealName.setText(meal.getStrMeal());
        mealCategory.setText("Category: " + meal.getStrCategory());
        mealArea.setText("Origin: " + meal.getStrArea());
        mealInstructions.setText(meal.getStrInstructions());

        Glide.with(this)
                .load(meal.getStrMealThumb())
                .into(mealImage);

        addIngredientsToView(meal);
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

    @Override
    public void onDestroyView() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroyView();
    }
}