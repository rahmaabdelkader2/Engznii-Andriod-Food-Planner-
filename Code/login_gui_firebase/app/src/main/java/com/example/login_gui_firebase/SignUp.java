package com.example.login_gui_firebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText name, phone, email, pass;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.try_sigup);

        initializeViews();
        setupFirestore();
        setupSignUpButton();
        setupLoginRedirect();
    }

    private void initializeViews() {
        name = findViewById(R.id.emailField2);
        phone = findViewById(R.id.phoneField);
        email = findViewById(R.id.passField2);
        pass = findViewById(R.id.passField);
    }

    private void setupFirestore() {
        db = FirebaseFirestore.getInstance();
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
                    redirectToLogin();
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
}