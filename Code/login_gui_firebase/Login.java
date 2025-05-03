package com.example.login_gui_firebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login_gui_firebase.MainActivity;
import com.example.login_gui_firebase.model.remote.authentication.Firebase;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    FirebaseFirestore db;
    EditText email, password;
    Button login;
    ImageView googlebtn;
    private Firebase firebaseHelper;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.try_login);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Check if user is already logged in
        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            redirectToHome();
            return;
        }

        db = FirebaseFirestore.getInstance();
        firebaseHelper = new Firebase(this);

        email = findViewById(R.id.firstnamefield);
        password = findViewById(R.id.emailfiled);
        googlebtn = findViewById(R.id.imageView);
        login = findViewById(R.id.loginbtn2);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString().trim();
                String userPassword = password.getText().toString().trim();

                if (userEmail.isEmpty() || userPassword.isEmpty()) {
                    Toast.makeText(Login.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check user credentials in Firestore
                db.collection("users")
                        .document(userEmail)  // Using email as document ID
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // User exists, verify password
                                    String storedPassword = document.getString("password");
                                    if (storedPassword != null && storedPassword.equals(userPassword)) {
                                        // Login successful
                                        // Save login state
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean(KEY_IS_LOGGED_IN, true);
                                        editor.apply();

                                        Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Wrong password
                                        Toast.makeText(Login.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // User doesn't exist
                                    Toast.makeText(Login.this, "User not found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Error accessing database
                                Toast.makeText(Login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        TextView backText = findViewById(R.id.loginclick);
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        setupGoogleButton();
    }

    private void setupGoogleButton() {
        googlebtn.setOnClickListener(v -> firebaseHelper.signInWithGoogle(new Firebase.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                // Save login state for Google sign-in
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_IS_LOGGED_IN, true);
                editor.apply();
                redirectToHome();
            }

            @Override
            public void onFailure(String errorMessage) {
                showErrorMessage(errorMessage);
            }
        }));
    }

    private void redirectToHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showErrorMessage(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Firebase.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(data);
            firebaseHelper.handleGoogleSignInResult(task, new Firebase.AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    // Save login state for Google sign-in
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(KEY_IS_LOGGED_IN, true);
                    editor.apply();
                    redirectToHome();
                }

                @Override
                public void onFailure(String errorMessage) {
                    showErrorMessage(errorMessage);
                }
            });
        }
    }
}