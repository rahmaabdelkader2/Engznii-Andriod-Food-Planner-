package com.example.login_gui_firebase.home.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.login_gui_firebase.NavClass;
import com.bumptech.glide.Glide;
import com.example.login_gui_firebase.Calender;
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
import com.example.login_gui_firebase.search.view.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements IView {
    private IPresenter presenter;
    private ImageView mainMealImage;
    private TextView mainMealName, mainMealCategory, mainMealArea;
    private RecyclerView mealRecyclerView;
    private mealsAdapter mealsAdapter;
  //  private NavClass nav;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        nav = new NavClass(this);
//        nav.setupBottomNavigation();
//        nav.setSelectedItem(R.id.homeID);


        // Initialize views
        mainMealImage = findViewById(R.id.mainMealImage);
        mainMealName = findViewById(R.id.mainMealName);
        mainMealCategory = findViewById(R.id.mainMealCategory);
        mainMealArea = findViewById(R.id.mainMealArea);

        // Setup RecyclerView
        mealRecyclerView = findViewById(R.id.mealRecyclerView);
        mealsAdapter = new mealsAdapter(this, new ArrayList<>());
        mealRecyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        mealRecyclerView.setAdapter(mealsAdapter);

        // Initialize dependencies
        ILocalDataSource localDataSource = new LocalDataSource(this);
        IClient client = Client.getInstance();
        IRepo repository = Repo.getInstance(localDataSource, client);
        presenter = new Presenter(this, repository);

        // Load data automatically
        loadData();

        // Auto-scrolling setup
        final Handler handler = new Handler();
        final Runnable autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                // Scroll by 2 pixels every 20ms (adjust these values as needed)
                mealRecyclerView.smoothScrollBy(10, 0);
                handler.postDelayed(this, 20);
            }
        };

        // Start auto-scrolling when data is loaded
        mealRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Restart auto-scrolling when user stops interacting
                    handler.postDelayed(autoScrollRunnable, 1000);
                }
            }
        });

        // Start auto-scrolling after a delay
        handler.postDelayed(autoScrollRunnable, 500);
        Button gotosearch=findViewById(R.id.gotosearch);

    gotosearch.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
        }
    });

        Button gotocalender=findViewById(R.id.gotocalender);

        gotocalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, Calender.class);
                startActivity(intent);
            }
        });
    }

    private void loadData() {
        // Load single random meal for top section
        presenter.getRandomMeal();

        // Load 10 random meals for bottom section
        presenter.getTenRandomMeals();
    }

    @Override
    public void showRandomMeal(Meal meal) {
        runOnUiThread(() -> {
            Glide.with(this).load(meal.getStrMealThumb()).into(mainMealImage);
            mainMealName.setText(meal.getStrMeal());
            mainMealCategory.setText("Category: " + meal.getStrCategory());
            mainMealArea.setText("Area: " + meal.getStrArea());
        });
    }

    @Override
    public void showTenRandomMeals(List<Meal> meals) {
        runOnUiThread(() -> {
            mealsAdapter.updateMeals(meals);
        });
    }
    @Override
    public void showFilteredMeals(List<FilteredMeal> meals) {
        // Implement if needed
    }

    @Override
    public void showError(String errorMessage) {
        runOnUiThread(() -> Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up if needed
    }
}