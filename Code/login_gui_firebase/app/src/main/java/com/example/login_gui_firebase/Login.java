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

import com.example.login_gui_firebase.home.view.HomeActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    FirebaseFirestore db;
    EditText email,password;
    Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.try_login);

        db= FirebaseFirestore.getInstance();
        email=findViewById(R.id.emailField2);
        password=findViewById(R.id.passField2);
        login=findViewById(R.id.loginbtn2);
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
                                        Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Login.this, HomeActivity.class);
                                        startActivity(intent);

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
    }
}