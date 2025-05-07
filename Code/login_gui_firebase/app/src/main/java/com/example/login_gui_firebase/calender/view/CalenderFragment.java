package com.example.login_gui_firebase.calender.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login_gui_firebase.meal_fragment.view.MealFragment;
import com.example.login_gui_firebase.R;
import com.example.login_gui_firebase.calender.presenter.CalenderPresenter;
import com.example.login_gui_firebase.calender.presenter.ICalenderPresenter;
import com.example.login_gui_firebase.model.local.ILocalDataSource;
import com.example.login_gui_firebase.model.local.LocalDataSource;
import com.example.login_gui_firebase.model.remote.retrofit.client.Client;
import com.example.login_gui_firebase.model.remote.retrofit.client.IClient;
import com.example.login_gui_firebase.model.repo.IRepo;
import com.example.login_gui_firebase.model.repo.Repo;
import com.example.login_gui_firebase.model.pojo.Meal;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalenderFragment extends Fragment implements ICalenderView,OnMealClickListener {
    private ICalenderPresenter presenter;
    private CalendarView calendarView;
    private TextView selectedDateTextView;
    private RecyclerView mealsRecyclerView;
    private MealCalenderAdaptor mealAdapter;
    private String currentSelectedDate;
    private SharedPreferences   sharedPreferences;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calender, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        selectedDateTextView = view.findViewById(R.id.selectedDateTextView);
        mealsRecyclerView = view.findViewById(R.id.mealsRecyclerView);
        sharedPreferences = requireActivity().getSharedPreferences("UserPref", getContext().MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "def");


        setupRecyclerView();
        setupPresenter();
        setupCalendar();
        loadInitialData();

        return view;
    }

    private void setupRecyclerView() {
        mealAdapter = new MealCalenderAdaptor(new ArrayList<>(), this);

        mealsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mealsRecyclerView.setAdapter(mealAdapter);
    }

    private void setupPresenter() {
        ILocalDataSource localDataSource = LocalDataSource.getInstance(requireContext());
        IClient client = Client.getInstance();
        IRepo repository = Repo.getInstance(getContext(),localDataSource, client);
        presenter = new CalenderPresenter(this, repository);
    }

    private void setupCalendar() {
        long today = System.currentTimeMillis();
        long oneWeekLater = today + (7 * 24 * 60 * 60 * 1000); // Today + 7 days in milliseconds

        calendarView.setMinDate(today);
        calendarView.setMaxDate(oneWeekLater); // Set max date to 7 days from today

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            currentSelectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            updateDateDisplay(currentSelectedDate);

            // Refresh data when date changes
            LiveData<List<Meal>> mealsLiveData = presenter.getMealsForDate(currentSelectedDate, userId);
            mealsLiveData.observe(getViewLifecycleOwner(), meals -> {
                if (meals != null && !meals.isEmpty()) {
                    mealAdapter.updateMeals(meals);
                    mealsRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    mealAdapter.updateMeals(new ArrayList<>());
                    mealsRecyclerView.setVisibility(View.GONE);
                }
            });
        });
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

    private void loadInitialData() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        currentSelectedDate = today;
        updateDateDisplay(today);
        presenter.getMealsForDate(today, userId);

        LiveData<List<Meal>> mealsLiveData = presenter.getMealsForDate(currentSelectedDate, userId);
        mealsLiveData.observe(getViewLifecycleOwner(), meals -> {
            if (meals != null && !meals.isEmpty()) {
                mealAdapter.updateMeals(meals);
                mealsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mealAdapter.updateMeals(new ArrayList<>());
                mealsRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void updateDateDisplay(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());

        try {
            Date parsedDate = inputFormat.parse(date);
            String formattedDate = outputFormat.format(parsedDate);

            if (!isDateWithinAllowedRange(date)) {
                selectedDateTextView.setText(formattedDate + " (Not available for planning)");
            } else {
                selectedDateTextView.setText(formattedDate);
            }
        } catch (Exception e) {
            selectedDateTextView.setText(date);
        }
    }

    private void showMealFragment(String mealId) {

        try {
            if (!isDateWithinAllowedRange(currentSelectedDate)) {
                return;
            }
            View rootView = getView();
            if (rootView == null) {
                return;
            }

            // Find views with null checks
            View mainContent = rootView.findViewById(R.id.main_content);
            View fragmentContainer = rootView.findViewById(R.id.fragment_containerfav);

            if (mainContent == null || fragmentContainer == null) {
                return;
            }

            // Update UI
            mainContent.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);

            MealFragment mealFragment = MealFragment.newInstance(mealId, currentSelectedDate);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_containerfav, mealFragment)
                    .addToBackStack(null)
                    .commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void showError(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                View rootView = getView();
                if (rootView != null && rootView.findViewById(R.id.fragment_containerfav).getVisibility() == View.VISIBLE) {
                    // Show the main content again
                    rootView.findViewById(R.id.main_content).setVisibility(View.VISIBLE);
                    // Hide the fragment container
                    rootView.findViewById(R.id.fragment_containerfav).setVisibility(View.GONE);
                    getParentFragmentManager().popBackStack();
                } else {
                    requireActivity().onBackPressed();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            ((CalenderPresenter) presenter).detachView();
        }
    }

    @Override
    public void onMealClick(Meal meal) {
        if (isDateWithinAllowedRange(currentSelectedDate)) {
            showMealFragment(meal.getIdMeal());
        } else {
        }
    }

    public boolean onBackCalenderPressed() {
        View fragmentContainer = getView().findViewById(R.id.fragment_containerfav);
        if (fragmentContainer != null && fragmentContainer.getVisibility() == View.VISIBLE) {
            getParentFragmentManager().popBackStack();
            fragmentContainer.setVisibility(View.GONE);
            getView().findViewById(R.id.main_content).setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }
}