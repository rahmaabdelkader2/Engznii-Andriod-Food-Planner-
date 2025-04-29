package com.example.login_gui_firebase;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 101;

    private ImageView imageView7;
    private TextView fullNameTextView, emailTextView, phoneTextView, countryTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activityy);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize views
        imageView7 = findViewById(R.id.imageView7);
        fullNameTextView = findViewById(R.id.firstnameDisplay);
        emailTextView = findViewById(R.id.emailDisplay);
        phoneTextView = findViewById(R.id.phoneDisplay);
        countryTextView = findViewById(R.id.countryDisplay);

        // Load user data
        loadUserData();

        // Set click listener for the ImageView
        imageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermissionAndPickImage();
            }
        });
    }

    private void loadUserData() {
        if (currentUser != null) {
            // Use UID instead of email as document ID
            String userId = currentUser.getEmail();

            DocumentReference docRef = db.collection("users").document(userId);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Get user data
                        String firstName = document.getString("firstName");
                        String surname = document.getString("surname");
                        String phone = document.getString("phone");
                        String email = document.getString("email"); // This can be different from auth email
                        String countryCode = document.getString("countryCode");

                        // Update UI
                        fullNameTextView.setText(firstName + " " + surname);
                        emailTextView.setText(email != null ? email : currentUser.getEmail());
                        phoneTextView.setText(phone);
                        countryTextView.setText(getCountryName(countryCode));
                    } else {
                        Log.e("ProfileActivity", "No data for UID: " + userId);
                        Toast.makeText(this, "Please complete your profile", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e("ProfileActivity", "Firestore error", task.getException());
                    Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }
    private String getCountryName(String countryCode) {
        // This should match the countries array in your SignUp activity
        String[][] countries = {
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

        for (String[] country : countries) {
            if (country[1].equals(countryCode)) {
                return country[0];
            }
        }
        return "Unknown";
    }

    private void checkStoragePermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission Needed")
                        .setMessage("This permission is needed to select images from your device")
                        .setPositiveButton("OK", (dialog, which) -> {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    STORAGE_PERMISSION_CODE);
                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE);
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showPermissionSettingsDialog();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showPermissionSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("You have permanently denied storage permission. " +
                        "Please enable it in app settings to select images.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    openAppSettings();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView7.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}