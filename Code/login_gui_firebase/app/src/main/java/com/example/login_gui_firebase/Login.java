//package com.example.login_gui_firebase;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.text.TextUtils;
//
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.login_gui_firebase.model.remote.authentication.Firebase;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//public class Login extends AppCompatActivity {
//    FirebaseFirestore db;
//    EditText email, password;
//    Button login;
//    ImageView googlebtn;
//    private Firebase firebaseHelper;
//    private FirebaseAuth mAuth;
//    private SharedPreferences sharedPreferences;
//    private static final String PREFS_NAME = "UserPref";
//    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
//    private TextView Skip_Login;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.login_activity);
//
//        initializeSharedPreferences();
//        checkAutoLogin();
//        initializeFirebase();
//        initializeViews();
//        skipLogin();
//
//    }
//
//    private void initializeSharedPreferences() {
//        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//    }
//    private void checkAutoLogin() {
//        boolean fromLogout = getIntent().getBooleanExtra("FROM_LOGOUT", false);
//        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) && !fromLogout) {
//            redirectToMain();
//        }
//    }
//    private void initializeFirebase() {
//        db = FirebaseFirestore.getInstance();
//        mAuth = FirebaseAuth.getInstance();
//        firebaseHelper = new Firebase(this);
//    }
//    private void initializeViews() {
//        email = findViewById(R.id.firstnamefield);
//        password = findViewById(R.id.emailfiled);
//        googlebtn = findViewById(R.id.imageView);
//        login = findViewById(R.id.loginbtn2);
//        TextView backText = findViewById(R.id.loginclick);
//        Skip_Login = findViewById(R.id.guest_login);
//
//        login.setOnClickListener(v -> handleLogin());
//        backText.setOnClickListener(v -> navigateToSignUp());
//        setupGoogleButton();
//    }
//    private void skipLogin(){
//        Skip_Login.setOnClickListener(v -> {
//            sharedPreferences.edit()
//                            .putString("userId", "guest")
//                            .putBoolean("isSignedIn", false)
//                            .putBoolean("isGuest", true)
//                            .apply();
//            startActivity(new Intent(Login.this, MainActivity.class));
//            finish();
//        });
//    }
//    private void handleLogin() {
//        String userEmail = email.getText().toString().trim();
//        String userPassword = password.getText().toString().trim();
//
//        if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
//            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        saveLoginState();
//                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
//                        redirectToMain();
//                    } else {
//                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//    private void saveLoginState() {
//        sharedPreferences.edit()
//                .putBoolean("isSignedIn", true)
//                .putBoolean("isGuest", false)
//                .putString("userId", mAuth.getCurrentUser().getUid())
//                .apply();
//    }
//    private void navigateToSignUp() {
//        startActivity(new Intent(this, SignUp.class));
//    }
//    private void setupGoogleButton() {
//        googlebtn.setOnClickListener(v -> firebaseHelper.signInWithGoogle(new Firebase.AuthCallback() {
//            @Override
//            public void onSuccess(FirebaseUser user) {
//                // Save login state for Google sign-in
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putBoolean(KEY_IS_LOGGED_IN, true);
//                editor.apply();
//                redirectToMain();
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                showErrorMessage(errorMessage);
//            }
//        }));
//    }
//    private void redirectToMain() {
//        startActivity(new Intent(this, MainActivity.class));
//        finish();
//    }
//    private void showErrorMessage(String error) {
//        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == Firebase.RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(data);
//            firebaseHelper.handleGoogleSignInResult(task, new Firebase.AuthCallback() {
//                @Override
//                public void onSuccess(FirebaseUser user) {
//                    // Save login state for Google sign-in
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putBoolean(KEY_IS_LOGGED_IN, true);
//                    editor.apply();
//                    redirectToMain();
//                }
//
//                @Override
//                public void onFailure(String errorMessage) {
//
//                    showErrorMessage(errorMessage);
//                }
//            });
//        }
//    }
//}
package com.example.login_gui_firebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login_gui_firebase.model.remote.authentication.Firebase;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    FirebaseFirestore db;
    EditText email, password;
    Button login;
    ImageView googlebtn;
    private Firebase firebaseHelper;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "UserPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private TextView Skip_Login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activity);

        initializeSharedPreferences();
        checkAutoLogin();
        initializeFirebase();
        initializeViews();
        skipLogin();
    }

    private void initializeSharedPreferences() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    private void checkAutoLogin() {
        boolean fromLogout = getIntent().getBooleanExtra("FROM_LOGOUT", false);
        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) && !fromLogout) {
            redirectToMain();
        }
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseHelper = new Firebase(this);
    }

    private void initializeViews() {
        email = findViewById(R.id.firstnamefield);
        password = findViewById(R.id.emailfiled);
        googlebtn = findViewById(R.id.imageView);
        login = findViewById(R.id.loginbtn2);
        TextView backText = findViewById(R.id.loginclick);
        Skip_Login = findViewById(R.id.guest_login);

        login.setOnClickListener(v -> handleLogin());
        backText.setOnClickListener(v -> navigateToSignUp());
        setupGoogleButton();
    }

    private void skipLogin() {
        Skip_Login.setOnClickListener(v -> {
            sharedPreferences.edit()
                    .putString("userId", "guest")
                    .putBoolean("isSignedIn", false)
                    .putBoolean("isGuest", true)
                    .apply();
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        });
    }

    private void handleLogin() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveLoginState();
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                        redirectToMain();
                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveLoginState() {
        sharedPreferences.edit()
                .putBoolean("isSignedIn", true)
                .putBoolean("isGuest", false)
                .putString("userId", mAuth.getCurrentUser().getUid())
                .apply();
    }

    private void navigateToSignUp() {
        startActivity(new Intent(this, SignUp.class));
    }

    private void setupGoogleButton() {
        googlebtn.setOnClickListener(v -> firebaseHelper.signInWithGoogle(new Firebase.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                String userId = user.getUid();
                Log.d("Login", "signInWithGoogle Success, userId: " + userId);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_IS_LOGGED_IN, true);
                editor.putString("userId", userId);
                editor.putBoolean("isGuest", false);
                editor.apply();
                redirectToMain();
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("Login", "signInWithGoogle Failure: " + errorMessage);
                showErrorMessage(errorMessage);
            }
        }));
    }

    private void redirectToMain() {
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
                    String userId = user.getUid();
                    Log.d("Login", "Google Sign-In Success, userId: " + userId);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(KEY_IS_LOGGED_IN, true);
                    editor.putString("userId", userId);
                    editor.putBoolean("isGuest", false);
                    editor.apply();
                    redirectToMain();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("Login", "Google Sign-In Failure: " + errorMessage);
                    showErrorMessage(errorMessage);
                }
            });
        }
    }
}