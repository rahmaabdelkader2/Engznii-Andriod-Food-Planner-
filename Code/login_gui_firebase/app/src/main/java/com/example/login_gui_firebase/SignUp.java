package com.example.login_gui_firebase;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.login_gui_firebase.MainActivity;
import com.example.login_gui_firebase.model.remote.authentication.Firebase;
import com.example.login_gui_firebase.model.remote.authentication.Firebase.AuthCallback;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private Firebase firebaseHelper;
    private EditText firstName, surname, phone, email, pass;
    private ImageView googleBtn;
    private Spinner countrySpinner;

    private final String[][] countries = {
            {"American", "US"},
            {"British", "GB"},
            // ... rest of the countries array
    };

    // Patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.try_sigup);

        initializeViews();
        setupFirebaseHelper();
        setupSignUpButton();
        setupLoginRedirect();
        setupGoogleButton();
        setupCountrySpinner();
    }

    private void setupFirebaseHelper() {
        firebaseHelper = new Firebase(this);
    }

    private void initializeViews() {
        firstName = findViewById(R.id.firstnamefield);
        surname = findViewById(R.id.surnamefield);
        phone = findViewById(R.id.phoneField);
        email = findViewById(R.id.emailfield);
        pass = findViewById(R.id.passField);
        googleBtn = findViewById(R.id.googleIcon);
        countrySpinner = findViewById(R.id.countrySpinner);
    }

    private void setupCountrySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                getCountryNames()
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                firebaseHelper.setSelectedCountryCode(countries[position][1]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private String[] getCountryNames() {
        String[] names = new String[countries.length];
        for (int i = 0; i < countries.length; i++) {
            names[i] = countries[i][0];
        }
        return names;
    }

    private void setupSignUpButton() {
        Button signUpButton = findViewById(R.id.loginbtn2);
        signUpButton.setOnClickListener(v -> attemptRegistration());
    }

    private void setupLoginRedirect() {
        TextView loginRedirect = findViewById(R.id.loginclick);
        loginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(SignUp.this, Login.class));
            finish();
        });
    }

    private void setupGoogleButton() {
        googleBtn.setOnClickListener(v -> firebaseHelper.signInWithGoogle(new AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                redirectToHome();
            }

            @Override
            public void onFailure(String errorMessage) {
                showErrorMessage(errorMessage);
            }
        }));
    }

    private void attemptRegistration() {
        String userFirstName = firstName.getText().toString().trim();
        String userSurname = surname.getText().toString().trim();
        String userPhone = phone.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPass = pass.getText().toString().trim();

        if (!validateInputs(userFirstName, userSurname, userPhone, userEmail, userPass)) {
            return;
        }

        firebaseHelper.registerUser(userFirstName, userSurname, userPhone, userEmail, userPass, new AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                showSuccessMessage();
                clearForm();
            }

            @Override
            public void onFailure(String errorMessage) {
                showErrorMessage(errorMessage);
            }
        });
    }

    private boolean validateInputs(String firstName, String surname, String phone, String email, String password) {
        // Validate first name
        if (TextUtils.isEmpty(firstName)) {
            this.firstName.setError("First name is required");
            this.firstName.requestFocus();
            return false;
        }

        if (!NAME_PATTERN.matcher(firstName).matches()) {
            this.firstName.setError("First name can only contain letters");
            this.firstName.requestFocus();
            return false;
        }

        // Validate surname
        if (TextUtils.isEmpty(surname)) {
            this.surname.setError("Surname is required");
            this.surname.requestFocus();
            return false;
        }

        if (!NAME_PATTERN.matcher(surname).matches()) {
            this.surname.setError("Surname can only contain letters");
            this.surname.requestFocus();
            return false;
        }

        // Validate phone
        if (TextUtils.isEmpty(phone)) {
            this.phone.setError("Phone number is required");
            this.phone.requestFocus();
            return false;
        }

        // Validate email
        if (TextUtils.isEmpty(email)) {
            this.email.setError("Email is required");
            this.email.requestFocus();
            return false;     }

        if (!isValidEmail(email)) {
           this.email.setError("Please enter a valid email");
            this.email.requestFocus();
            return false;
        }

        // Validate password
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Firebase.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(data);
            firebaseHelper.handleGoogleSignInResult(task, new AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    redirectToHome();
                }

                @Override
                public void onFailure(String errorMessage) {
                    showErrorMessage(errorMessage);
                }
            });
        }
    }

    private void showSuccessMessage() {
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
    }

    private void clearForm() {
        firstName.setText("");
        surname.setText("");
        phone.setText("");
        email.setText("");
        pass.setText("");
        countrySpinner.setSelection(0);
    }

    private void redirectToHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showErrorMessage(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}