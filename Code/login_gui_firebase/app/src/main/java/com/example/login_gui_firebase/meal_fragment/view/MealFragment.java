package com.example.login_gui_firebase.meal_fragment.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.login_gui_firebase.R;
import com.example.login_gui_firebase.meal_fragment.presenter.IMealFragmentPresenter;
import com.example.login_gui_firebase.meal_fragment.presenter.MealFragmentPresenter;
import com.example.login_gui_firebase.model.local.ILocalDataSource;
import com.example.login_gui_firebase.model.local.LocalDataSource;
import com.example.login_gui_firebase.model.local.MealDatabase;
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.model.remote.retrofit.client.Client;
import com.example.login_gui_firebase.model.remote.retrofit.networkcallbacks.MealCallback;
import com.example.login_gui_firebase.model.repo.Repo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MealFragment extends Fragment implements IMealFragmentView{
    private static final String ARG_MEAL_ID = "meal_id";
    private static final String ARG_SELECTED_DATE = "selected_date";
    private ImageView mealImage, calendarIcon, favoriteIcon;
    private TextView mealName, mealCategory, mealArea, mealInstructions;

    private WebView webView;
    private ImageView backButton;
    private String currentMealId;
    private String currentSelectedDate;
    private Meal currentMeal;
    private SharedPreferences sharedPreferences;
    private String userId;
    private Boolean isGuest = false;
    private IMealFragmentPresenter presenter;
    private Context context ;
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

        sharedPreferences = requireActivity().getSharedPreferences("UserPref", getContext().MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "def");
        isGuest= sharedPreferences.getBoolean("isGuest", false);

        initViews(view);
        setupWebView();
        setupClickListeners();


        presenter = new MealFragmentPresenter(this, Repo.getInstance(context,LocalDataSource.getInstance(this.getContext()), Client.getInstance()));presenter = new MealFragmentPresenter(this,
                Repo.getInstance(requireContext(),
                        LocalDataSource.getInstance(this.getContext()),
                        Client.getInstance()));

        if (getArguments() != null) {
            currentMealId = getArguments().getString(ARG_MEAL_ID);
            currentSelectedDate = getArguments().getString(ARG_SELECTED_DATE);
            if (currentMealId != null) {
                presenter.getMealDetails(currentMealId);
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
            if (isGuest == true) {
                Toast.makeText(getContext(), "Please login to use this feature", Toast.LENGTH_SHORT).show();
            } else {
                if (currentSelectedDate == null) {
                    showDatePickerDialog();
                } else {
                    toggleMealInCalendar();
                }
            }
        });



        favoriteIcon.setOnClickListener(v -> {
            if (isGuest == true) {
                Toast.makeText(getContext(), "Please login to use this feature", Toast.LENGTH_SHORT).show();

            } else {
                if (currentMeal != null) {
                    toggleFavoriteStatus();
                }}
        });
        }


    private void toggleFavoriteStatus() {
        boolean newFavoriteStatus = !currentMeal.isFavorite();
        currentMeal.setFavorite(newFavoriteStatus);
        updateFavoriteIcon(newFavoriteStatus);

        // Check if meal exists in database
        LiveData<Integer> existsLiveData = presenter.mealExists(currentMeal.getIdMeal(), userId);
        existsLiveData.observe(getViewLifecycleOwner(), existsCount -> {
            if (existsCount != null) {
                if (existsCount > 0) {
                    // Meal exists, just update favorite status
                    presenter.setFavoriteStatus(currentMeal.getIdMeal(), newFavoriteStatus, userId);
                } else {
                    // Meal doesn't exist, insert it
                    presenter.insertMeal(currentMeal, userId);
                }
                String message = newFavoriteStatus ? "Added to favorites" : "Removed from favorites";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                existsLiveData.removeObservers(getViewLifecycleOwner());
            }
        });
    }

    private void updateFavoriteIcon(boolean isFavorite) {
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
        // Remove any existing observers first
        LiveData<Boolean> isScheduled = presenter.isMealScheduled(currentMealId, currentSelectedDate);
        //LiveData<Boolean> isScheduled = presenter.isMealScheduled(currentMealId);

        isScheduled.removeObservers(getViewLifecycleOwner());

        isScheduled.observe(getViewLifecycleOwner(), scheduled -> {
            if (scheduled != null) {
                if (scheduled) {
                    presenter.unscheduleMeal(currentMealId, userId);
                    currentSelectedDate = null;
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Meal removed from calendar", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    presenter.scheduleMeal(currentMealId, currentSelectedDate, userId);
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Meal added to calendar", Toast.LENGTH_SHORT).show();
                    }
                }
                updateCalendarIcon();
                isScheduled.removeObservers(getViewLifecycleOwner());
            }
        });
    }
    private void scheduleMealForDate(String date) {
        if (currentMeal != null) {
            presenter.scheduleMeal(currentMeal.getIdMeal(), date, userId);
        }
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
            webView = null;
        }
        mealImage = null;
        mealName = null;
        // Nullify other views
        presenter = null; // Consider if this is appropriate for your architecture
        super.onDestroyView();
    }


    public void updateUI(Meal meal) {
        currentMeal = meal;
        currentMeal.setUserId(userId);
        mealName.setText(meal.getStrMeal());
        mealCategory.setText("Category: " + meal.getStrCategory());
        mealArea.setText("Origin: " + meal.getStrArea());
        mealInstructions.setText(meal.getStrInstructions());

        // Observe favorite status
        LiveData<Boolean> isFavoriteLiveData = presenter.isFavorite(meal.getIdMeal(), userId);
        isFavoriteLiveData.observe(getViewLifecycleOwner(), isFavorite -> {
            if (isFavorite != null) {
                meal.setFavorite(isFavorite);
                updateFavoriteIcon(isFavorite);
            } else {
                meal.setFavorite(false);
                updateFavoriteIcon(false);
            }
        });

        // Observe scheduled status if no date was passed in arguments
        if (currentSelectedDate == null) {
            LiveData<String> scheduledDateLiveData = presenter.getScheduledDate(meal.getIdMeal(), userId);
            scheduledDateLiveData.observe(getViewLifecycleOwner(), scheduledDate -> {
                if (scheduledDate != null) {
                    currentSelectedDate = scheduledDate;
                    updateCalendarIcon();
                }
                scheduledDateLiveData.removeObservers(getViewLifecycleOwner());
            });
        }

        Glide.with(this)
                .load(meal.getStrMealThumb())
                .into(mealImage);

        setupIngredientsRecyclerView(meal);
        loadYoutubeVideo(meal.getStrYoutube());
    }
    @Override
    public void showError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }
}