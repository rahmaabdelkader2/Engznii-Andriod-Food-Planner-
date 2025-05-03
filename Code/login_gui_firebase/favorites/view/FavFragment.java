package com.example.login_gui_firebase.favorites.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FavFragment extends Fragment implements IFavView {
    private IFavPresenter presenter;
    private RecyclerView recyclerView;
    private FavMealAdaptor mealAdapter;
    private View emptyView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_favourites, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewfav);
        emptyView = view.findViewById(R.id.emptyView);
        setupRecyclerView();
        setupPresenter();

        return view;
    }

    private void setupRecyclerView() {
        mealAdapter = new FavMealAdaptor(getContext(), new ArrayList<>(), meal -> {

            recyclerView.setVisibility(View.GONE);
            // Open MealFragment when a meal is clicked
            MealFragment mealFragment = MealFragment.newInstance(meal.getIdMeal(), null);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mealFragment)
                    .addToBackStack(null)
                    .commit();
        });

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

}