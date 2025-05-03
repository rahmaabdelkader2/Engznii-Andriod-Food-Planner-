package com.example.login_gui_firebase.home.view;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.login_gui_firebase.MealFragment;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;
import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements IView, OnMealClickListener {
    private IPresenter presenter;
    private ImageView randomMealImage;
    private TextView randomMealName, randomMealCategory, randomMealArea;
    private RecyclerView countryMealsRecyclerView, userCountryMealsRecyclerView;
    private TextView countryTitle, userCountryTitle;
    private mealsAdapter countryMealsAdapter, userCountryMealsAdapter;
    private Handler countryHandler, userCountryHandler;
    private Runnable countryAutoScrollRunnable, userCountryAutoScrollRunnable;
    private String currentArea;
    private String userCountryCode;
    private Meal currentRandomMeal;
    private LottieAnimationView connectionLostAnimation;
    private View connectionLostContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(view);
        setupConnectionRetryListener();

        setupRecyclerViews();
        setupPresenter();
        loadUserCountry();
        loadData();
        setupAutoScrolling();

        return view;
    }

    private void initializeViews(View view) {
        // Random meal card views
        randomMealImage = view.findViewById(R.id.mainMealImage);
        randomMealName = view.findViewById(R.id.mainMealName);
        randomMealCategory = view.findViewById(R.id.mainMealCategory);
        randomMealArea = view.findViewById(R.id.mainMealArea);

        // Country meals section
        countryMealsRecyclerView = view.findViewById(R.id.countryMealsRecyclerView);
        countryTitle = view.findViewById(R.id.countryTitle);

        // User country meals section
        userCountryMealsRecyclerView = view.findViewById(R.id.userCountryMealsRecyclerView);
        userCountryTitle = view.findViewById(R.id.userCountryTitle);

        // Set click listener for random meal card
        View randomMealCard = view.findViewById(R.id.randommealcard);
        randomMealCard.setOnClickListener(v -> {
            if (currentRandomMeal != null) {
                showMealDetails(currentRandomMeal);
            }
        });

        // Initialize connection lost container
        connectionLostContainer = view.findViewById(R.id.connection_lost_container);
        connectionLostAnimation = connectionLostContainer.findViewById(R.id.animationView4);

        // Make sure it's initially hidden
        connectionLostContainer.setVisibility(View.GONE);
    }



    private void setupRecyclerViews() {
        // Setup for random meal's country meals

        countryMealsAdapter = new mealsAdapter( new ArrayList<>(), this);
        countryMealsRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));
        countryMealsRecyclerView.setAdapter(countryMealsAdapter);

        // Setup for user's country meals
        userCountryMealsAdapter = new mealsAdapter(new ArrayList<>(), this);
        userCountryMealsRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));
        userCountryMealsRecyclerView.setAdapter(userCountryMealsAdapter);
    }

    private void setupPresenter() {
        ILocalDataSource localDataSource = new LocalDataSource(getContext());
        IClient client = Client.getInstance();
        IRepo repository = Repo.getInstance(localDataSource, client);
        presenter = new Presenter(this, repository);
    }

    private void loadUserCountry() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userEmail = user.getEmail();
            if (userEmail != null) {
                FirebaseFirestore.getInstance().collection("users")
                        .document(userEmail)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    userCountryCode = document.getString("countryCode");
                                    Log.d("HomeFragment", "Raw countryCode: " + userCountryCode);

                                    if (userCountryCode != null && !userCountryCode.isEmpty()) {
                                        String mealArea = mapCountryCodeToArea(userCountryCode);
                                        Log.d("HomeFragment", "Mapped meal area: " + mealArea);
                                        presenter.getMealsByArea(mealArea);
                                    } else {
                                        userCountryTitle.setText("Set your country in profile");
                                    }
                                }
                            }
                        });
            }
        }
    }

    private String mapCountryCodeToArea(String countryCode) {
        switch (countryCode.toUpperCase()) {
            case "US": return "American";
            case "GB": return "British";
            case "CA": return "Canadian";
            case "CN": return "Chinese";
            case "HR": return "Croatian";
            case "NL": return "Dutch";
            case "EG": return "Egyptian";
            case "PH": return "Filipino";
            case "FR": return "French";
            case "GR": return "Greek";
            case "IN": return "Indian";
            case "IE": return "Irish";
            case "IT": return "Italian";
            case "JM": return "Jamaican";
            case "JP": return "Japanese";
            case "KE": return "Kenyan";
            case "MY": return "Malaysian";
            case "MX": return "Mexican";
            case "MA": return "Moroccan";
            case "PL": return "Polish";
            case "PT": return "Portuguese";
            case "RU": return "Russian";
            case "ES": return "Spanish";
            case "TH": return "Thai";
            case "TN": return "Tunisian";
            case "TR": return "Turkish";
            case "UA": return "Ukrainian";
            case "UY": return "Uruguayan";
            case "VN": return "Vietnamese";
            default: return countryCode;
        }
    }

    private void loadData() {
        presenter.getRandomMeal();
        if (isNetworkAvailable()) {
            connectionLostContainer.setVisibility(View.GONE);
            showContentViews(true);
            presenter.getRandomMeal();
        } else {
            connectionLostContainer.setVisibility(View.VISIBLE);
            connectionLostAnimation.playAnimation();
            showContentViews(false);
        }
    }
    // Helper method to show/hide content views
    private void showContentViews(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        randomMealImage.setVisibility(visibility);
        randomMealName.setVisibility(visibility);
        randomMealCategory.setVisibility(visibility);
        randomMealArea.setVisibility(visibility);
        countryMealsRecyclerView.setVisibility(visibility);
        countryTitle.setVisibility(visibility);
        userCountryMealsRecyclerView.setVisibility(visibility);
        userCountryTitle.setVisibility(visibility);
    }

    @Override
    public void showRandomMeal(Meal meal) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                currentRandomMeal = meal;
                Glide.with(this).load(meal.getStrMealThumb()).into(randomMealImage);
                randomMealName.setText(meal.getStrMeal());
                randomMealCategory.setText("Category: " + meal.getStrCategory());
                randomMealArea.setText("Area: " + meal.getStrArea());

                currentArea = meal.getStrArea();
                countryTitle.setText("Meals from " + currentArea);
                presenter.getMealsByArea(currentArea);
            });
        }
    }

    @Override
    public void showMealsByArea(List<Meal> meals) {
        Log.d("HomeFragment", "Received showMealsByArea with " + (meals != null ? meals.size() : 0) + " meals");
        if (meals != null && !meals.isEmpty()) {
            Log.d("HomeFragment", "First meal area: " + meals.get(0).getStrArea());
        }

        if (getActivity() != null && meals != null && !meals.isEmpty()) {
            getActivity().runOnUiThread(() -> {
                String mealArea = meals.get(0).getStrArea();
                Log.d("HomeFragment", "Processing meals for area: " + mealArea);

                if (currentArea != null && mealArea.equalsIgnoreCase(currentArea)) {
                    countryMealsAdapter.updateMeals(meals);
                } else if (userCountryCode != null &&
                        mealArea.equalsIgnoreCase(mapCountryCodeToArea(userCountryCode))) {
                    Log.d("HomeFragment", "Updating user country meals: " + meals.size());
                    userCountryMealsAdapter.updateMeals(meals);
                }
            });
        }
    }

    @Override
    public void onMealClick(Meal meal) {

        showMealDetails(meal);
    }

    private void showMealDetails(Meal meal) {
        if (getActivity() == null) {
            Log.e("HomeFragment", "Activity is null");
            return;
        }

        FrameLayout fragmentContainer = getActivity().findViewById(R.id.fragment_container);
        if (fragmentContainer == null) {
            Log.e("HomeFragment", "Fragment container not found");
            return;
        }

        // Remove layout params modification - not needed with proper XML structure
        fragmentContainer.bringToFront();
        fragmentContainer.setVisibility(View.VISIBLE);

        // Create and show the fragment
        MealFragment mealFragment = MealFragment.newInstance(meal.getIdMeal(), null);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, mealFragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupAutoScrolling() {
        // Setup auto-scrolling for country meals
        countryHandler = new Handler();
        countryAutoScrollRunnable = new Runnable() {
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
                    countryHandler.postDelayed(this, 50);
                }
            }
        };

        // Setup auto-scrolling for user country meals
        userCountryHandler = new Handler();
        userCountryAutoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                LinearLayoutManager layoutManager = (LinearLayoutManager) userCountryMealsRecyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
                    int totalItems = userCountryMealsRecyclerView.getAdapter().getItemCount();

                    if (lastVisiblePosition == totalItems - 1) {
                        userCountryMealsRecyclerView.smoothScrollToPosition(0);
                    } else {
                        userCountryMealsRecyclerView.smoothScrollBy(15, 0);
                    }
                    userCountryHandler.postDelayed(this, 50);
                }
            }
        };

        // Start auto-scrolling when data is loaded for country meals
        countryMealsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (itemCount > 0) {
                    countryHandler.removeCallbacks(countryAutoScrollRunnable);
                    countryHandler.postDelayed(countryAutoScrollRunnable, 1000);
                }
            }
        });

        // Start auto-scrolling when data is loaded for user country meals
        userCountryMealsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (itemCount > 0) {
                    userCountryHandler.removeCallbacks(userCountryAutoScrollRunnable);
                    userCountryHandler.postDelayed(userCountryAutoScrollRunnable, 1000);
                }
            }
        });

        // Pause auto-scroll when user interacts with country meals
        countryMealsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    countryHandler.removeCallbacks(countryAutoScrollRunnable);
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    countryHandler.postDelayed(countryAutoScrollRunnable, 1000);
                }
            }
        });

        // Pause auto-scroll when user interacts with user country meals
        userCountryMealsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    userCountryHandler.removeCallbacks(userCountryAutoScrollRunnable);
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    userCountryHandler.postDelayed(userCountryAutoScrollRunnable, 1000);
                }
            }
        });
    }
    // Add this method to check connectivity
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
        // Check connectivity when fragment resumes
        if (!isNetworkAvailable()) {
            connectionLostAnimation.setVisibility(View.VISIBLE);
            connectionLostAnimation.playAnimation();
            showContentViews(false);
        } else {
            connectionLostAnimation.setVisibility(View.GONE);
            showContentViews(true);
        }


        countryHandler.postDelayed(countryAutoScrollRunnable, 1000);
        userCountryHandler.postDelayed(userCountryAutoScrollRunnable, 1000);
    }

    private void setupConnectionRetryListener() {
        connectionLostContainer.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                connectionLostContainer.setVisibility(View.GONE);
                connectionLostAnimation.cancelAnimation();
                showContentViews(true);
                loadData();
            } else {
                Toast.makeText(getContext(), "Still offline. Please check your connection",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        countryHandler.removeCallbacks(countryAutoScrollRunnable);
        userCountryHandler.removeCallbacks(userCountryAutoScrollRunnable);
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
        if (countryHandler != null) {
            countryHandler.removeCallbacks(countryAutoScrollRunnable);
        }
        if (userCountryHandler != null) {
            userCountryHandler.removeCallbacks(userCountryAutoScrollRunnable);
        }
    }
}