package com.example.login_gui_firebase.favorites.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login_gui_firebase.R;
import com.example.login_gui_firebase.favorites.presenter.FavPresenter;
import com.example.login_gui_firebase.favorites.presenter.IFavPresenter;
import com.example.login_gui_firebase.model.local.ILocalDataSource;
import com.example.login_gui_firebase.model.local.LocalDataSource;
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.model.remote.retrofit.client.Client;
import com.example.login_gui_firebase.model.remote.retrofit.client.IClient;
import com.example.login_gui_firebase.model.repo.IRepo;
import com.example.login_gui_firebase.model.repo.Repo;
import com.example.login_gui_firebase.meal_fragment.view.MealFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FavFragment extends Fragment implements IFavView,OnFavouriteMealClick {
    private IFavPresenter presenter;
    private RecyclerView recyclerView;
    private FavMealAdaptor mealAdapter;
    private View emptyView;
    private FrameLayout fragmentContainer;

    private SharedPreferences  sharedPreferences;
    private String userId;
    private boolean isGuest;

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_favourites, container, false);
//
//        sharedPreferences = requireActivity().getSharedPreferences("UserPref", getContext().MODE_PRIVATE);
//        userId = sharedPreferences.getString("userId", "def");
//
//        initializeViews(view);
//        setupRecyclerView();
//        setupPresenter();
//
//        LiveData<List<Meal>> mealsLiveData = presenter.getFavouriteMeals(userId);
//        mealsLiveData.observe(getViewLifecycleOwner(), meals -> {
//            if (meals != null && !meals.isEmpty()) {
//                mealAdapter.updateMeals(meals);
//                emptyView.setVisibility(View.GONE);
//                recyclerView.setVisibility(View.VISIBLE);
//                fragmentContainer.setVisibility(View.GONE);
//            } else {
//                emptyView.setVisibility(View.VISIBLE);
//                fragmentContainer.setVisibility(View.GONE);
//                recyclerView.setVisibility(View.GONE);
//            }
//        });
//        return view;
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("UserPref", getContext().MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "def");
        isGuest =sharedPreferences.getBoolean("isGuest", false);
        initializeViews(view);
        setupRecyclerView();
        setupPresenter();

        LiveData<List<Meal>> mealsLiveData = presenter.getFavouriteMeals(userId);
        mealsLiveData.observe(getViewLifecycleOwner(), meals -> {
            // Always update the adapter with the latest meals
            mealAdapter.updateMeals(meals != null ? meals : new ArrayList<>());

            // Only update visibility if MealFragment is not being displayed
            if (fragmentContainer.getVisibility() != View.VISIBLE) {
                if (meals != null && !meals.isEmpty()) {
                    emptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    fragmentContainer.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    fragmentContainer.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }
    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewfav);
        emptyView = view.findViewById(R.id.emptyView);
        fragmentContainer = view.findViewById(R.id.fragment_containerfav);
        fragmentContainer.setVisibility(View.GONE);
    }
    private void setupRecyclerView() {
        mealAdapter = new FavMealAdaptor(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mealAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private void setupPresenter() {
        ILocalDataSource localDataSource = LocalDataSource.getInstance(requireContext());
        IClient client = Client.getInstance();
        IRepo repository = Repo.getInstance(requireContext(), localDataSource, client);
        presenter = new FavPresenter(this, repository);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFavorites();
    }

    public void refreshFavorites() {
        presenter.getFavouriteMeals(userId);
    }


    @Override
    public void showError(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }
//    public boolean onBackFavPressed(){
//        if (fragmentContainer.getVisibility() == View.VISIBLE) {
//            getChildFragmentManager().popBackStack();
//            fragmentContainer.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.VISIBLE);
//            emptyView.setVisibility(View.GONE);
//
//
//            return true;
//        }
//
//        return false;
//    }
    public boolean onBackFavPressed() {
        if (fragmentContainer.getVisibility() == View.VISIBLE) {
            getChildFragmentManager().popBackStack();
            fragmentContainer.setVisibility(View.GONE);

            // Refresh UI based on current meals list
            List<Meal> currentMeals = mealAdapter.getMeals(); // Add getMeals() to FavMealAdaptor
            if (currentMeals != null && !currentMeals.isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
            return true;
        }
        return false;
    }
    @Override
    public void onMealClick(Meal meal) {
        recyclerView.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        LiveData<Boolean> isScheduledLiveData = presenter.isMealScheduled(meal.getIdMeal(), currentDate);
        isScheduledLiveData.observe(getViewLifecycleOwner(), isScheduled -> {
            if (isScheduled != null && isScheduled) {
                LiveData<String> scheduledDateLiveData = presenter.getScheduledDate(meal.getIdMeal(), userId);
                scheduledDateLiveData.observe(getViewLifecycleOwner(), scheduledDate -> {
                    if (scheduledDate != null) {
                        MealFragment mealFragment = MealFragment.newInstance(meal.getIdMeal(), scheduledDate);
                        showMealFragment(mealFragment);
                    } else {
                        MealFragment mealFragment = MealFragment.newInstance(meal.getIdMeal(), null);
                        showMealFragment(mealFragment);
                    }
                    scheduledDateLiveData.removeObservers(getViewLifecycleOwner());
                });
            } else {
                MealFragment mealFragment = MealFragment.newInstance(meal.getIdMeal(), null);
                showMealFragment(mealFragment);
            }
            isScheduledLiveData.removeObservers(getViewLifecycleOwner());
        });
    }
    private void showMealFragment(MealFragment mealFragment) {
        getParentFragmentManager().beginTransaction()
                .add(R.id.fragment_containerfav, mealFragment)
                .addToBackStack(null)
                .commit();
    }
}
