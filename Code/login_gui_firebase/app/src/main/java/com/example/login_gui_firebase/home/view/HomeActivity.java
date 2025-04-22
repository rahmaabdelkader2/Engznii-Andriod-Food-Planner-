package com.example.login_gui_firebase.home.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.login_gui_firebase.R;
import com.example.login_gui_firebase.home.presenter.IPresenter;
import com.example.login_gui_firebase.home.presenter.Presenter;
import com.example.login_gui_firebase.model.local.ILocalDataSource;
import com.example.login_gui_firebase.model.local.LocalDataSource;
import com.example.login_gui_firebase.model.local.MealDatabase;
import com.example.login_gui_firebase.model.pojo.FilteredMeal;
import com.example.login_gui_firebase.model.pojo.Meal;
import com.example.login_gui_firebase.model.remote.retrofit.client.Client;
import com.example.login_gui_firebase.model.remote.retrofit.client.IClient;
import com.example.login_gui_firebase.model.repo.IRepo;
import com.example.login_gui_firebase.model.repo.Repo;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements IView {
    private IPresenter presenter;
    private ProgressBar progressBar;
    private ImageView mealImage;
    private TextView mealName, mealCategory, mealArea;
    private Button randomMealButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        progressBar = findViewById(R.id.progressBar);
        mealImage = findViewById(R.id.mealImage);
        mealName = findViewById(R.id.mealName);
        mealCategory = findViewById(R.id.mealCategory);
        mealArea = findViewById(R.id.mealArea);
        randomMealButton = findViewById(R.id.randomMealButton);
        MealDatabase database = MealDatabase.getInstance(this);
        ILocalDataSource localDataSource = new LocalDataSource(this);
        IClient client = Client.getInstance();  // Your existing Client class

        // Initialize dependencies
        IRepo repository = Repo.getInstance(localDataSource, client);
        presenter = new Presenter(this, repository);

        randomMealButton.setOnClickListener(v -> presenter.getRandomMeal());

    }

    @Override
    public void showRandomMeal(Meal meal) {

            Glide.with(this).load(meal.getStrMealThumb()).into(mealImage);
            mealName.setText(meal.getStrMeal());
            mealCategory.setText("Category: " + meal.getStrCategory());
            mealArea.setText("Area: " + meal.getStrArea());

    }

    @Override
    public void showFilteredMeals(List<FilteredMeal> meals) {
        // Implement if you have a list view for filtered meals
    }

    @Override
    public void showError(String errorMessage) {
        runOnUiThread(() -> Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show());
    }

//    @Override
//    public void showLoading() {
//        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
//    }
//
//    @Override
//    public void hideLoading() {
//        runOnUiThread(() -> progressBar.setVisibility(View.GONE));
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up if needed
    }
}