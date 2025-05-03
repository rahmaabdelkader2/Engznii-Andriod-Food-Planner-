package com.example.login_gui_firebase.favorites.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.example.login_gui_firebase.MealFragment;

import java.util.ArrayList;
import java.util.List;

public class FavFragment extends Fragment implements IFavView,OnFavouriteMealClick {
    private IFavPresenter presenter;
    private RecyclerView recyclerView;
    private FavMealAdaptor mealAdapter;
    private View emptyView;
    private FrameLayout fragmentContainer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupPresenter();

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
    }

    private void setupPresenter() {
        ILocalDataSource localDataSource = LocalDataSource.getInstance(requireContext());
        IClient client = Client.getInstance();
        IRepo repository = Repo.getInstance(localDataSource, client);
        presenter = new FavPresenter(this, repository);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFavorites();
    }

    public void refreshFavorites() {
        presenter.getFavouriteMeals();
    }

    @Override
    public void showFavouriteMeals(List<Meal> meals) {
        if (meals != null && !meals.isEmpty()) {
            mealAdapter.updateMeals(meals);

        }
    }


    @Override
    public void showError(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMealClick(Meal meal) {

        recyclerView.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);

        // Open MealFragment when a meal is clicked
        MealFragment mealFragment = MealFragment.newInstance(meal.getIdMeal(), null);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_containerfav, mealFragment)
                .addToBackStack(null)
                .commit();


    }
    public boolean onBackFavPressed(){
        if (fragmentContainer.getVisibility() == View.VISIBLE) {
            getChildFragmentManager().popBackStack();
            fragmentContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);


            return true;
        }
        return false;
    }
}
