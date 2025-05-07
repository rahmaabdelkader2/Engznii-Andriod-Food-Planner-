package com.example.login_gui_firebase;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.login_gui_firebase.calender.view.CalenderFragment;
import com.example.login_gui_firebase.favorites.view.FavFragment;
import com.example.login_gui_firebase.home.view.HomeFragment;
import com.example.login_gui_firebase.profile.view.ProfileFragment;
import com.example.login_gui_firebase.search.view.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_bar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.homeID) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.SearchID) {
                selectedFragment = new SearchFragment();
            }
            else if (itemId == R.id.FavID) {
                selectedFragment = new FavFragment();
            }else if (itemId == R.id.CalenderID) {
                selectedFragment = new CalenderFragment();
            }
            else if (itemId == R.id.profileID) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, selectedFragment)
                        .commit();
                return true;
            }

            return false;
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.homeID);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (currentFragment instanceof SearchFragment) {
            if (((SearchFragment) currentFragment).onBackSearchPressed()) {
                return;
            }
        }
        if (currentFragment instanceof FavFragment) {
            if (((FavFragment) currentFragment).onBackFavPressed()) {
                return;
            }
        }
        if (currentFragment instanceof CalenderFragment) {
            if (((CalenderFragment) currentFragment).onBackCalenderPressed()) {
                return;
            }
        }

        super.onBackPressed();
    }
}