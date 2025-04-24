package com.example.login_gui_firebase.search.view;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login_gui_firebase.R;
import com.example.login_gui_firebase.model.local.ILocalDataSource;
import com.example.login_gui_firebase.model.local.LocalDataSource;
import com.example.login_gui_firebase.model.pojo.*;
import com.example.login_gui_firebase.model.remote.retrofit.client.Client;
import com.example.login_gui_firebase.model.remote.retrofit.client.IClient;
import com.example.login_gui_firebase.model.repo.IRepo;
import com.example.login_gui_firebase.model.repo.Repo;
import com.example.login_gui_firebase.search.presenter.ISearchPresenter;
import com.example.login_gui_firebase.search.presenter.SearchPresenter;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchIview {

    private ISearchPresenter Searchpresenter;

    private SearchAdaptor adapter;
    private List<Object> currentItems = new ArrayList<>();
    private String currentMode = "categories";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_activty);

        ILocalDataSource localDataSource = new LocalDataSource(this);
        IClient client = Client.getInstance();
        IRepo repository = Repo.getInstance(localDataSource, client);

        Searchpresenter= new SearchPresenter(this, repository);

        // Setup UI
        RecyclerView recyclerView = findViewById(R.id.search_recycler_view);
        SearchView searchView = findViewById(R.id.search_view);
        Button btnCategories = findViewById(R.id.btn_categories);
        Button btnAreas = findViewById(R.id.btn_areas);
        Button btnIngredients = findViewById(R.id.btn_ingredients);

        // Configure RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdaptor();
        recyclerView.setAdapter(adapter);

        // Set click listeners
        btnCategories.setOnClickListener(v -> {
            currentMode = "categories";
            Searchpresenter.listAllCategories();
        });

        btnAreas.setOnClickListener(v -> {
            currentMode = "areas";
            Searchpresenter.listAllAreas();
        });

        btnIngredients.setOnClickListener(v -> {
            currentMode = "ingredients";
            Searchpresenter.listAllIngredients();
        });

        // Search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterItems(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterItems(newText);
                return true;
            }
        });

        // Load initial data
        Searchpresenter.listAllCategories();
    }

    private void filterItems(String query) {
        List<Object> filtered = new ArrayList<>();
        for (Object item : currentItems) {
            String name = "";
            if (item instanceof Categories) {
                name = ((Categories) item).getStrCategory().toLowerCase();
            } else if (item instanceof Area) {
                name = ((Area) item).getStrArea().toLowerCase();
            } else if (item instanceof Ingredients) {
                name = ((Ingredients) item).getStrIngredient().toLowerCase();
            }

            if (name.contains(query.toLowerCase())) {
                filtered.add(item);
            }
        }
        adapter.setItems(filtered);
    }

    @Override
    public void showCategories(List<Categories> categories) {
        currentItems = new ArrayList<>(categories);
        adapter.setItems(currentItems);
    }

    @Override
    public void showAreas(List<Area> areas) {
        currentItems = new ArrayList<>(areas);
        adapter.setItems(currentItems);
    }

    @Override
    public void showIngredients(List<Ingredients> ingredients) {
        currentItems = new ArrayList<>(ingredients);
        adapter.setItems(currentItems);
    }

    @Override
    public void showError(String message) {

    }


}