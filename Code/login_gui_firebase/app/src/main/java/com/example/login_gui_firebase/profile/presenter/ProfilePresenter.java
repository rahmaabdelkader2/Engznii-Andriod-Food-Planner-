package com.example.login_gui_firebase.profile.presenter;

import com.example.login_gui_firebase.profile.view.IProfileView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfilePresenter {
    private IProfileView view;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    // Country data as a field in the presenter
    private final String[][] countries = {
            {"American", "US"},
            {"British", "GB"},
            {"Canadian", "CA"},
            {"Chinese", "CN"},
            {"Croatian", "HR"},
            {"Dutch", "NL"},
            {"Egyptian", "EG"},
            {"Filipino", "PH"},
            {"French", "FR"},
            {"Greek", "GR"},
            {"Indian", "IN"},
            {"Irish", "IE"},
            {"Italian", "IT"},
            {"Jamaican", "JM"},
            {"Japanese", "JP"},
            {"Kenyan", "KE"},
            {"Malaysian", "MY"},
            {"Mexican", "MX"},
            {"Moroccan", "MA"},
            {"Polish", "PL"},
            {"Portuguese", "PT"},
            {"Russian", "RU"},
            {"Spanish", "ES"},
            {"Thai", "TH"},
            {"Tunisian", "TN"},
            {"Turkish", "TR"},
            {"Ukrainian", "UA"},
            {"Uruguayan", "UY"},
            {"Vietnamese", "VN"},
    };

    public ProfilePresenter(IProfileView view) {
        this.view = view;
    }

    public void loadUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Try to get data from Firestore first
            db.collection("users").document(currentUser.getEmail())
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            // User exists in Firestore (regular sign-up)
                            String fullName = document.getString("firstName") + " " +
                                    document.getString("surname");
                            String countryCode = document.getString("countryCode");
                            String countryName = getCountryName(countryCode);

                            view.showUserData(
                                    fullName,
                                    document.getString("email"),
                                    document.getString("phone"),
                                    countryName
                            );
                        } else {
                            // User doesn't exist in Firestore (Google sign-in)
                            String fullName = currentUser.getDisplayName();
                            String email = currentUser.getEmail();
                            String phone = "Not set"; // Google doesn't provide phone number
                            String country = "Not set";

                            view.showUserData(
                                    fullName != null ? fullName : "Google User",
                                    email != null ? email : "No email",
                                    phone,
                                    country
                            );
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Fallback to Firebase Auth data if Firestore fails
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            view.showUserData(
                                    user.getDisplayName() != null ? user.getDisplayName() : "Google User",
                                    user.getEmail() != null ? user.getEmail() : "No email",
                                    "Not set",
                                    "Not set"
                            );
                        } else {
                            view.showError("User data not available");
                        }
                    });
        }
    }

    private String getCountryName(String countryCode) {
        for (String[] country : countries) {
            if (country[1].equals(countryCode)) {
                return country[0];
            }
        }
        return "Unknown";
    }

    public void detachView() {
        this.view = null;
    }
}