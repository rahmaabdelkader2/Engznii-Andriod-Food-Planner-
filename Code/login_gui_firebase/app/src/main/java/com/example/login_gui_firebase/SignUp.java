package com.example.login_gui_firebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login_gui_firebase.home.view.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private EditText name, phone, email, pass;
    ImageView googleBtn;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
    );

    private static final int RC_SIGN_IN = 9001; // Request code for Google Sign-In

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.try_sigup);

        initializeViews();
        setupFirestore();
        setupFirebaseAuth();
        setupGoogleSignIn();
        setupSignUpButton();
        setupLoginRedirect();
        setupGoogleButton();
    }

    private void initializeViews() {
        name = findViewById(R.id.emailField2);
        phone = findViewById(R.id.phoneField);
        email = findViewById(R.id.passField2);
        pass = findViewById(R.id.passField);
        googleBtn = findViewById(R.id.googleIcon);
    }

    private void setupFirestore() {
        db = FirebaseFirestore.getInstance();
    }

    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void setupGoogleSignIn() {
        // Configure Google Sign-In
       GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Get this from Firebase Console
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupSignUpButton() {
        Button signUpButton = findViewById(R.id.loginbtn2);
        signUpButton.setOnClickListener(v -> attemptRegistration());
    }

    private void setupLoginRedirect() {
        TextView loginRedirect = findViewById(R.id.loginclick);
        loginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(SignUp.this, Login.class));
            finish(); // Optional: close this activity to prevent going back
        });
    }

    private void setupGoogleButton() {
        googleBtn.setOnClickListener(v -> signInWithGoogle());
    }

    private void attemptRegistration() {
        String userName = name.getText().toString().trim();
        String userPhone = phone.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPass = pass.getText().toString().trim();

        if (!validateInputs(userName, userPhone, userEmail, userPass)) {
            return;
        }

        registerUser(userName, userPhone, userEmail, userPass);
    }

    private boolean validateInputs(String name, String phone, String email, String password) {
        if (TextUtils.isEmpty(name)) {
            this.name.setError("Name is required");
            this.name.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            this.phone.setError("Phone number is required");
            this.phone.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            this.email.setError("Email is required");
            this.email.requestFocus();
            return false;
        }

        if (!isValidEmail(email)) {
            this.email.setError("Please enter a valid email");
            this.email.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            this.pass.setError("Password is required");
            this.pass.requestFocus();
            return false;
        }

        if (password.length() < 8) {
            this.pass.setError("Password must be at least 8 characters");
            this.pass.requestFocus();
            return false;
        }

        return true;
    }

    private void registerUser(String name, String phone, String email, String password) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("phone", phone);
        user.put("email", email);
        user.put("password", password); // Note: In production, hash this password

        db.collection("users")
                .document(email)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    showSuccessMessage();
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    showErrorMessage(e.getMessage());
                });
    }

    private void showSuccessMessage() {
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
    }

    private void clearForm() {
        name.setText("");
        phone.setText("");
        email.setText("");
        pass.setText("");
    }

    private void redirectToLogin() {
        startActivity(new Intent(this, Login.class));
        finish(); // Optional: close this activity
    }

    private void showErrorMessage(String error) {
        Toast.makeText(this, "Registration failed: " + error, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    // Google Sign-In Method
    private void signInWithGoogle() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                showErrorMessage("Google Sign-In failed: " + e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
//                        registerUser(user.getDisplayName(), "", user.getEmail(), "");
                        Intent intent = new Intent(SignUp.this, HomeActivity.class);
                        startActivity(intent);
                        finish(); // Close SignUp activity to prevent back navigation

                    } else {
                        showErrorMessage("Authentication failed.");
                    }
                });
    }
}

//package com.example.login_gui_firebase;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.regex.Pattern;
//
//public class SignUp extends AppCompatActivity {
//
//    private FirebaseFirestore db;
//    private EditText name, phone, email, pass;
//
//    ImageView googleBtn;
//    // Email validation pattern
//    private static final Pattern EMAIL_PATTERN = Pattern.compile(
//            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
//            Pattern.CASE_INSENSITIVE
//    );
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.try_sigup);
//
//        initializeViews();
//        setupFirestore();
//        setupSignUpButton();
//        setupLoginRedirect();
//    }
//
//    private void initializeViews() {
//        name = findViewById(R.id.emailField2);
//        phone = findViewById(R.id.phoneField);
//        email = findViewById(R.id.passField2);
//        pass = findViewById(R.id.passField);
//        googleBtn = findViewById(R.id.googleIcon);
//
//    }
//
//    private void setupFirestore() {
//        db = FirebaseFirestore.getInstance();
//    }
//
//    private void setupSignUpButton() {
//        Button signUpButton = findViewById(R.id.loginbtn2);
//        signUpButton.setOnClickListener(v -> attemptRegistration());
//    }
//
//    private void setupLoginRedirect() {
//        TextView loginRedirect = findViewById(R.id.loginclick);
//        loginRedirect.setOnClickListener(v -> {
//            startActivity(new Intent(SignUp.this, Login.class));
//            finish(); // Optional: close this activity to prevent going back
//        });
//    }
//
//    private void attemptRegistration() {
//        String userName = name.getText().toString().trim();
//        String userPhone = phone.getText().toString().trim();
//        String userEmail = email.getText().toString().trim();
//        String userPass = pass.getText().toString().trim();
//
//        if (!validateInputs(userName, userPhone, userEmail, userPass)) {
//            return;
//        }
//
//        registerUser(userName, userPhone, userEmail, userPass);
//    }
//
//    private boolean validateInputs(String name, String phone, String email, String password) {
//        if (TextUtils.isEmpty(name)) {
//            this.name.setError("Name is required");
//            this.name.requestFocus();
//            return false;
//        }
//
//        if (TextUtils.isEmpty(phone)) {
//            this.phone.setError("Phone number is required");
//            this.phone.requestFocus();
//            return false;
//        }
//
//        if (TextUtils.isEmpty(email)) {
//            this.email.setError("Email is required");
//            this.email.requestFocus();
//            return false;
//        }
//
//        if (!isValidEmail(email)) {
//            this.email.setError("Please enter a valid email");
//            this.email.requestFocus();
//            return false;
//        }
//
//        if (TextUtils.isEmpty(password)) {
//            this.pass.setError("Password is required");
//            this.pass.requestFocus();
//            return false;
//        }
//
//        if (password.length() < 8) {
//            this.pass.setError("Password must be at least 8 characters");
//            this.pass.requestFocus();
//            return false;
//        }
//
//        return true;
//    }
//
//    private void registerUser(String name, String phone, String email, String password) {
//        Map<String, Object> user = new HashMap<>();
//        user.put("name", name);
//        user.put("phone", phone);
//        user.put("email", email);
//        user.put("password", password); // Note: In production, hash this password
//
//        db.collection("users")
//                .document(email)
//                .set(user)
//                .addOnSuccessListener(aVoid -> {
//                    showSuccessMessage();
//                    clearForm();
//                    redirectToLogin();
//                })
//                .addOnFailureListener(e -> {
//                    showErrorMessage(e.getMessage());
//                });
//    }
//
//    private void showSuccessMessage() {
//        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
//    }
//
//    private void clearForm() {
//        name.setText("");
//        phone.setText("");
//        email.setText("");
//        pass.setText("");
//    }
//
//    private void redirectToLogin() {
//        startActivity(new Intent(this, Login.class));
//        finish(); // Optional: close this activity
//    }
//
//    private void showErrorMessage(String error) {
//        Toast.makeText(this, "Registration failed: " + error, Toast.LENGTH_SHORT).show();
//    }
//
//    private boolean isValidEmail(String email) {
//        return EMAIL_PATTERN.matcher(email).matches();
//    }
//}