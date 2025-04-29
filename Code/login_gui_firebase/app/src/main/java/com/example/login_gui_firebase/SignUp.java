//package com.example.login_gui_firebase;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.login_gui_firebase.home.view.HomeActivity;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.GoogleAuthProvider;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.auth.AuthCredential;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
//import com.google.android.gms.common.api.ApiException;
//import com.google.android.gms.tasks.Task;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.regex.Pattern;
//
//public class SignUp extends AppCompatActivity {
//
//    private FirebaseFirestore db;
//    private FirebaseAuth mAuth;
//    private GoogleSignInClient mGoogleSignInClient;
//
//    private EditText firstName, surname, phone, email, pass;
//    private ImageView googleBtn;
//    private Spinner countrySpinner;
//
//    private final String[][] countries = {
//            {"American", "US"},
//            {"British", "GB"},
//            {"Canadian", "CA"},
//            {"Chinese", "CN"},
//            {"Croatian", "HR"},
//            {"Dutch", "NL"},
//            {"Egyptian", "EG"},
//            {"Filipino", "PH"},
//            {"French", "FR"},
//            {"Greek", "GR"},
//            {"Indian", "IN"},
//            {"Irish", "IE"},
//            {"Italian", "IT"},
//            {"Jamaican", "JM"},
//            {"Japanese", "JP"},
//            {"Kenyan", "KE"},
//            {"Malaysian", "MY"},
//            {"Mexican", "MX"},
//            {"Moroccan", "MA"},
//            {"Polish", "PL"},
//            {"Portuguese", "PT"},
//            {"Russian", "RU"},
//            {"Spanish", "ES"},
//            {"Thai", "TH"},
//            {"Tunisian", "TN"},
//            {"Turkish", "TR"},
//            {"Ukrainian", "UA"},
//            {"Uruguayan", "UY"},
//            {"Vietnamese", "VN"},
//    };
//
//    private String selectedCountryCode = "EG"; // Default
//
//    // Email validation pattern
//    private static final Pattern EMAIL_PATTERN = Pattern.compile(
//            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
//            Pattern.CASE_INSENSITIVE
//    );
//
//    // Name validation pattern (letters and spaces only)
//    private static final Pattern NAME_PATTERN = Pattern.compile(
//            "^[a-zA-Z\\s]+$"
//    );
//
//    private static final int RC_SIGN_IN = 9001; // Request code for Google Sign-In
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.try_sigup);
//
//        initializeViews();
//        setupFirestore();
//        setupFirebaseAuth();
//        setupGoogleSignIn();
//        setupSignUpButton();
//        setupLoginRedirect();
//        setupGoogleButton();
//        setupCountrySpinner();
//    }
//
//    private void setupCountrySpinner() {
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_spinner_item,
//                getCountryNames()
//        );
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        countrySpinner.setAdapter(adapter);
//
//        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                selectedCountryCode = countries[position][1];
//                Log.d("Country", countries[position][0] + " (" + selectedCountryCode + ")");
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }
//
//    private String[] getCountryNames() {
//        String[] names = new String[countries.length];
//        for (int i = 0; i < countries.length; i++) {
//            names[i] = countries[i][0];
//        }
//        return names;
//    }
//
//    private void initializeViews() {
//        firstName = findViewById(R.id.firstnamefield);
//        surname = findViewById(R.id.surnamefield);
//        phone = findViewById(R.id.phoneField);
//        email = findViewById(R.id.emailfield);
//        pass = findViewById(R.id.passField);
//        googleBtn = findViewById(R.id.googleIcon);
//        countrySpinner = findViewById(R.id.countrySpinner);
//    }
//
//    private void setupFirestore() {
//        db = FirebaseFirestore.getInstance();
//    }
//
//    private void setupFirebaseAuth() {
//        mAuth = FirebaseAuth.getInstance();
//    }
//
//    private void setupGoogleSignIn() {
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
//    private void setupGoogleButton() {
//        googleBtn.setOnClickListener(v -> signInWithGoogle());
//    }
//
//    private void attemptRegistration() {
//        String userFirstName = firstName.getText().toString().trim();
//        String userSurname = surname.getText().toString().trim();
//        String userPhone = phone.getText().toString().trim();
//        String userEmail = email.getText().toString().trim();
//        String userPass = pass.getText().toString().trim();
//
//        if (!validateInputs(userFirstName, userSurname, userPhone, userEmail, userPass)) {
//            return;
//        }
//
//        registerUser(userFirstName, userSurname, userPhone, userEmail, userPass);
//    }
//
//    private boolean validateInputs(String firstName, String surname, String phone, String email, String password) {
//        // Validate first name
//        if (TextUtils.isEmpty(firstName)) {
//            this.firstName.setError("First name is required");
//            this.firstName.requestFocus();
//            return false;
//        }
//
//        if (!NAME_PATTERN.matcher(firstName).matches()) {
//            this.firstName.setError("First name can only contain letters");
//            this.firstName.requestFocus();
//            return false;
//        }
//
//        // Validate surname
//        if (TextUtils.isEmpty(surname)) {
//            this.surname.setError("Surname is required");
//            this.surname.requestFocus();
//            return false;
//        }
//
//        if (!NAME_PATTERN.matcher(surname).matches()) {
//            this.surname.setError("Surname can only contain letters");
//            this.surname.requestFocus();
//            return false;
//        }
//
//        // Validate phone
//        if (TextUtils.isEmpty(phone)) {
//            this.phone.setError("Phone number is required");
//            this.phone.requestFocus();
//            return false;
//        }
//
//        // Validate email
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
//        // Validate password
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
//    private void registerUser(String firstName, String surname, String phone, String email, String password) {
//        // Add debug logs
//        Log.d("FirebaseDebug", "Attempting to register user: " + email);
//
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
//                        if (firebaseUser == null) {
//                            Log.e("FirebaseError", "FirebaseUser is null after successful registration");
//                            showErrorMessage("Registration error - please try again");
//                            return;
//                        }
//
//                        Log.d("FirebaseDebug", "Auth success, UID: " + firebaseUser.getUid());
//
//                        Map<String, Object> user = new HashMap<>();
//                        user.put("firstName", firstName);
//                        user.put("surname", surname);
//                        user.put("fullName", firstName + " " + surname);
//                        user.put("phone", phone);
//                        user.put("email", email);
//                        user.put("countryCode", selectedCountryCode);
//                        user.put("password",password);
//
//                        // Add debug log for Firestore data
//                        Log.d("FirebaseDebug", "Attempting to save to Firestore: " + user.toString());
//
//                        db.collection("users")
//                                .document(email)
//                                .set(user)
//                                .addOnSuccessListener(aVoid -> {
//                                    Log.d("FirebaseDebug", "Firestore write successful");
//                                    showSuccessMessage();
//                                    clearForm();
//                                })
//                                .addOnFailureListener(e -> {
//                                    Log.e("FirebaseError", "Firestore write failed", e);
//                                    showErrorMessage("Failed to store user data: " + e.getMessage());
//                                    // Attempt to delete the auth user if Firestore fails
//                                    firebaseUser.delete().addOnCompleteListener(deleteTask -> {
//                                        if (deleteTask.isSuccessful()) {
//                                            Log.d("FirebaseDebug", "Rollback: Auth user deleted");
//                                        } else {
//                                            Log.e("FirebaseError", "Failed to delete auth user after Firestore failure");
//                                        }
//                                    });
//                                });
//                    } else {
//                        Exception exception = task.getException();
//                        Log.e("FirebaseError", "Registration failed", exception);
//                        showErrorMessage("Registration failed: " + (exception != null ? exception.getMessage() : "Unknown error"));
//                    }
//                });
//    }
//
//    private void showSuccessMessage() {
//        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
//    }
//
//    private void clearForm() {
//        firstName.setText("");
//        surname.setText("");
//        phone.setText("");
//        email.setText("");
//        pass.setText("");
//        countrySpinner.setSelection(0); // Reset to first item
//    }
//
//    private void redirectToHome() {
//        startActivity(new Intent(this, HomeActivity.class));
//        finish(); // Close SignUp activity
//    }
//
//    private void showErrorMessage(String error) {
//        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
//    }
//
//    private boolean isValidEmail(String email) {
//        return EMAIL_PATTERN.matcher(email).matches();
//    }
//
//    private void signInWithGoogle() {
//        mGoogleSignInClient.signOut()
//                .addOnCompleteListener(this, task -> {
//                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//                    startActivityForResult(signInIntent, RC_SIGN_IN);
//                });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuthWithGoogle(account.getIdToken());
//            } catch (ApiException e) {
//                showErrorMessage("Google Sign-In failed: " + e.getMessage());
//            }
//        }
//    }
//
//    private void firebaseAuthWithGoogle(String idToken) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        FirebaseUser user = mAuth.getCurrentUser();
//
//                        // Check if this is a new user
//                        if (task.getResult().getAdditionalUserInfo().isNewUser()) {
//                            // Store basic user info in Firestore
//                            Map<String, Object> userData = new HashMap<>();
//                            String fullName = user.getDisplayName();
//                            String[] names = fullName != null ? fullName.split(" ") : new String[]{"", ""};
//
//                            userData.put("firstName", names.length > 0 ? names[0] : "");
//                            userData.put("surname", names.length > 1 ? names[1] : "");
//                            userData.put("fullName", fullName != null ? fullName : "");
//                            userData.put("email", user.getEmail());
//                            userData.put("countryCode", selectedCountryCode);
//
//                            db.collection("users")
//                                    .document(user.getUid())
//                                    .set(userData)
//                                    .addOnFailureListener(e -> {
//                                        Log.e("GoogleSignIn", "Error saving user data", e);
//                                    });
//                        }
//
//                        redirectToHome();
//                    } else {
//                        showErrorMessage("Authentication failed.");
//                    }
//                });
//    }
//}