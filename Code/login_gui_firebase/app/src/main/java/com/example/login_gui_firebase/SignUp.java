package com.example.login_gui_firebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    FirebaseFirestore db;
    Button signbtn, back;
    EditText name, phone, email, pass;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
    );

    // Password validation pattern (only letters and numbers)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9]+$"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.try_sigup);


        db = FirebaseFirestore.getInstance();

        name = findViewById(R.id.emailField2);
        phone = findViewById(R.id.phoneField);
        email = findViewById(R.id.passField2);
        pass = findViewById(R.id.passField);

        signbtn = findViewById(R.id.loginbtn2);
        signbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String userName = name.getText().toString().trim();
                String userPhone = phone.getText().toString().trim();
                String userEmail = email.getText().toString().trim();
                String userPass = pass.getText().toString().trim();

                // Validate inputs
                if (userName.isEmpty() || userPhone.isEmpty() || userEmail.isEmpty() || userPass.isEmpty()) {
                    Toast.makeText(SignUp.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate email format
                if (!isValidEmail(userEmail)) {
                    email.setError("Please enter a valid email address");
                    email.requestFocus();
                    return;
                }

                // Validate password format
                if (!isValidPassword(userPass)) {
                    pass.setError("Password can only contain letters and numbers");
                    pass.requestFocus();
                    return;
                }

                // Check password length (optional)
                if (userPass.length() < 8) {
                    pass.setError("Password should be at least 6 characters");
                    pass.requestFocus();
                    return;
                }

                // Create a new user with the data
                Map<String, Object> user = new HashMap<>();
                user.put("name", userName);
                user.put("phone", userPhone);
                user.put("email", userEmail);
                user.put("password", userPass); // Note: In production, you should hash passwords

                // Use email as the document ID
                db.collection("users")
                        .document(userEmail) // Set custom document ID here
                        .set(user)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(SignUp.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                            // Clear fields after successful registration
                            name.setText("");
                            phone.setText("");
                            email.setText("");
                            pass.setText("");
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(SignUp.this, "Error registering user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });


        TextView backText = findViewById(R.id.loginclick);
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });
    }

    // Email validation method
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    // Password validation method
    private boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}