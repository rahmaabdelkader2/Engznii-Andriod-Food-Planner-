//package com.example.login_gui_firebase;
//
//import android.content.Intent;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.login_gui_firebase.home.view.HomeActivity;
//import com.example.login_gui_firebase.search.view.SearchActivity;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//public class NavClass {
//    private final AppCompatActivity activity;
//    private BottomNavigationView bottomNavigationView;
//    private static int lastSelectedItemId = R.id.homeID; // Default item
//
//    public NavClass(AppCompatActivity activity) {
//        this.activity = activity;
//    }
//
//        public void setupBottomNavigation() {
//            bottomNavigationView = activity.findViewById(R.id.bottom_navigation);
//
//            // Restore last selected item
//            bottomNavigationView.setSelectedItemId(lastSelectedItemId);
//
//            bottomNavigationView.setOnItemSelectedListener(item -> {
//                int itemId = item.getItemId();
//                lastSelectedItemId = itemId; // Remember selection
//
//                if (itemId == R.id.homeID && !activity.getClass().equals(HomeActivity.class)) {
//                    activity.startActivity(new Intent(activity, HomeActivity.class));
//                    activity.finish();
//                    return true;
//                }
//                // Other cases...
//                return false;
//            });
//        }
//
//
//    public void setSelectedItem(int itemId) {
//        if (bottomNavigationView != null) {
//            bottomNavigationView.setSelectedItemId(itemId);
//        }
//    }
//}