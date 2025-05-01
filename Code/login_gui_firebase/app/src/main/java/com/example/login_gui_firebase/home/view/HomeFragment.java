package com.example.login_gui_firebase.home.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.login_gui_firebase.R;
import com.example.login_gui_firebase.home.presenter.IPresenter;
import com.example.login_gui_firebase.home.presenter.Presenter;
import com.example.login_gui_firebase.model.local.ILocalDataSource;
import com.example.login_gui_firebase.model.local.LocalDataSource;
import com.example.login_gui_firebase.model.pojo.FilteredMeal;
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.model.remote.retrofit.client.Client;
import com.example.login_gui_firebase.model.remote.retrofit.client.IClient;
import com.example.login_gui_firebase.model.repo.IRepo;
import com.example.login_gui_firebase.model.repo.Repo;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements IView {
    private IPresenter presenter;
    private ImageView mainMealImage;
    private TextView mainMealName, mainMealCategory, mainMealArea;
    private RecyclerView mealRecyclerView;
    private mealsAdapter mealsAdapter;
    private Handler handler;
    private Runnable autoScrollRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        // Initialize views and setup
        initializeViews(view);
        setupRecyclerView();
        setupPresenter();
        loadData();
        setupAutoScrolling();


        return view;
    }

    private void initializeViews(View view) {
        mainMealImage = view.findViewById(R.id.mainMealImage);
        mainMealName = view.findViewById(R.id.mainMealName);
        mainMealCategory = view.findViewById(R.id.mainMealCategory);
        mainMealArea = view.findViewById(R.id.mainMealArea);
        mealRecyclerView = view.findViewById(R.id.mealRecyclerView);
    }

    private void setupRecyclerView() {
        mealsAdapter = new mealsAdapter(getContext(), new ArrayList<>());
        mealRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));
        mealRecyclerView.setAdapter(mealsAdapter);
    }

    private void setupPresenter() {
        ILocalDataSource localDataSource = new LocalDataSource(getContext());
        IClient client = Client.getInstance();
        IRepo repository = Repo.getInstance(localDataSource, client);
        presenter = new Presenter(this, repository);
    }

    private void setupAutoScrolling() {
        handler = new Handler();
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                mealRecyclerView.smoothScrollBy(10, 0);
                handler.postDelayed(this, 20);
            }
        };

        mealRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    handler.postDelayed(autoScrollRunnable, 1000);
                }
            }
        });
        handler.postDelayed(autoScrollRunnable, 500);
    }



    private void loadData() {
        presenter.getRandomMeal();
        presenter.getTenRandomMeals();
    }

    @Override
    public void showRandomMeal(Meal meal) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                Glide.with(this).load(meal.getStrMealThumb()).into(mainMealImage);
                mainMealName.setText(meal.getStrMeal());
                mainMealCategory.setText("Category: " + meal.getStrCategory());
                mainMealArea.setText("Area: " + meal.getStrArea());
            });
        }
    }

    @Override
    public void showTenRandomMeals(List<Meal> meals) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> mealsAdapter.updateMeals(meals));
        }
    }

    @Override
    public void showFilteredMeals(List<FilteredMeal> meals) {

    }

    @Override
    public void showError(String errorMessage) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(autoScrollRunnable);
    }
}