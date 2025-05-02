package com.example.login_gui_firebase.home.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.model.remote.retrofit.client.Client;
import com.example.login_gui_firebase.model.remote.retrofit.client.IClient;
import com.example.login_gui_firebase.model.repo.IRepo;
import com.example.login_gui_firebase.model.repo.Repo;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements IView {
    private IPresenter presenter;
    private ImageView randomMealImage;
    private TextView randomMealName, randomMealCategory, randomMealArea;
    private RecyclerView countryMealsRecyclerView;
    private TextView countryTitle;
    private mealsAdapter countryMealsAdapter;
    private Handler handler;
    private Runnable autoScrollRunnable;
    private String currentArea;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        initializeViews(view);
        setupRecyclerViews();
        setupPresenter();
        loadData();
        setupAutoScrolling();

        return view;
    }

    private void initializeViews(View view) {
        // Random meal card views - use IDs from activity_home.xml
        randomMealImage = view.findViewById(R.id.mainMealImage);
        randomMealName = view.findViewById(R.id.mainMealName);
        randomMealCategory = view.findViewById(R.id.mainMealCategory);
        randomMealArea = view.findViewById(R.id.mainMealArea);

        // Country meals section
        countryMealsRecyclerView = view.findViewById(R.id.countryMealsRecyclerView);
        countryTitle = view.findViewById(R.id.countryTitle);
    }

    private void setupRecyclerViews() {
        // Setup for country meals horizontal recycler view
        countryMealsAdapter = new mealsAdapter(getContext(), new ArrayList<>());
        countryMealsRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));
        countryMealsRecyclerView.setAdapter(countryMealsAdapter);
    }

    private void setupPresenter() {
        ILocalDataSource localDataSource = new LocalDataSource(getContext());
        IClient client = Client.getInstance();
        IRepo repository = Repo.getInstance(localDataSource, client);
        presenter = new Presenter(this, repository);
    }

    private void loadData() {
        presenter.getRandomMeal();
    }

    @Override
    public void showRandomMeal(Meal meal) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                // Set random meal data in the card view
                Glide.with(this).load(meal.getStrMealThumb()).into(randomMealImage);
                randomMealName.setText(meal.getStrMeal());
                randomMealCategory.setText("Category: " + meal.getStrCategory());
                randomMealArea.setText("Area: " + meal.getStrArea());

                // Get meals by country/area
                currentArea = meal.getStrArea();
                countryTitle.setText("Meals from " + currentArea);
                presenter.getMealsByArea(currentArea);
            });
        }
    }

    @Override
    public void showMealsByArea(List<Meal> meals) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                // Add new meals to existing ones
                List<Meal> currentMeals = countryMealsAdapter.getMeals();
                currentMeals.addAll(meals);
                countryMealsAdapter.updateMeals(currentMeals);
            });
        }
    }

    private void setupAutoScrolling() {
        handler = new Handler();
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                LinearLayoutManager layoutManager = (LinearLayoutManager) countryMealsRecyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
                    int totalItems = countryMealsRecyclerView.getAdapter().getItemCount();

                    if (lastVisiblePosition == totalItems - 1) {
                        countryMealsRecyclerView.smoothScrollToPosition(0);
                    } else {
                        countryMealsRecyclerView.smoothScrollBy(15, 0);
                    }
                    handler.postDelayed(this, 50);
                }
            }
        };

        // Start auto-scrolling when data is loaded
        countryMealsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (itemCount > 0) {
                    handler.removeCallbacks(autoScrollRunnable);
                    handler.postDelayed(autoScrollRunnable, 1000);
                }
            }
        });

        // Pause auto-scroll when user interacts
        countryMealsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    handler.removeCallbacks(autoScrollRunnable);
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    handler.postDelayed(autoScrollRunnable, 1000);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(autoScrollRunnable, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(autoScrollRunnable);
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
        if (handler != null) {
            handler.removeCallbacks(autoScrollRunnable);
        }
    }
}