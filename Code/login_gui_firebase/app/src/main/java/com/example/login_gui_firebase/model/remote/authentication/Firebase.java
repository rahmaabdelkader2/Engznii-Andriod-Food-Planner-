package com.example.login_gui_firebase.model.remote.authentication;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.login_gui_firebase.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Firebase {
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private final Context context;
    private String selectedCountryCode;
    public static final int RC_SIGN_IN = 9001;

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String errorMessage);
    }

    public Firebase(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        setupGoogleSignIn();
    }

    public void setSelectedCountryCode(String countryCode) {
        this.selectedCountryCode = countryCode;
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public void registerUser(String firstName, String surname, String phone, String email, String password, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser == null) {
                            callback.onFailure("Registration error - please try again");
                            return;
                        }

                        Map<String, Object> user = new HashMap<>();
                        user.put("firstName", firstName);
                        user.put("surname", surname);
                        user.put("fullName", firstName + " " + surname);
                        user.put("phone", phone);
                        user.put("email", email);
                        user.put("countryCode", selectedCountryCode);
                        user.put("password", password);

                        db.collection("users")
                                .document(email)
                                .set(user)
                                .addOnSuccessListener(aVoid -> callback.onSuccess(firebaseUser))
                                .addOnFailureListener(e -> {
                                    callback.onFailure("Failed to store user data: " + e.getMessage());
                                    firebaseUser.delete();
                                });
                    } else {
                        Exception exception = task.getException();
                        callback.onFailure("Registration failed: " + (exception != null ? exception.getMessage() : "Unknown error"));
                    }
                });
    }

    public void signInWithGoogle(AuthCallback callback) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(task -> {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    if (context instanceof Activity) {
                        ((Activity) context).startActivityForResult(signInIntent, RC_SIGN_IN);
                    }
                });
    }

    public void handleGoogleSignInResult(Task<GoogleSignInAccount> task, AuthCallback callback) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Save Google user data to Firestore
                            saveGoogleUserData(account, user.getUid());

                            callback.onSuccess(user);
                        } else {
                            callback.onFailure(task1.getException().getMessage());
                        }
                    });
        } catch (ApiException e) {
            callback.onFailure(e.getMessage());
        }
    }

    private void saveGoogleUserData(GoogleSignInAccount account, String uid) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName", account.getGivenName());
        userData.put("lastName", account.getFamilyName());
        userData.put("email", account.getEmail());
        userData.put("photoUrl", account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "");

        // Save to Firestore
        db.collection("users").document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Google user data saved"))
                .addOnFailureListener(e -> Log.w(TAG, "Error saving Google user data", e));
    }
//    private void firebaseAuthWithGoogle(String idToken, AuthCallback callback) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        FirebaseUser user = mAuth.getCurrentUser();
//                        if (user != null && task.getResult() != null && task.getResult().getAdditionalUserInfo() != null) {
//                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
//                                storeGoogleUserData(user, callback);
//                            } else {
//                                callback.onSuccess(user);
//                            }
//                        } else {
//                            callback.onFailure("Authentication failed: user information not available");
//                        }
//                    } else {
//                        callback.onFailure("Authentication failed.");
//                    }
//                });
//    }
//
//    private void storeGoogleUserData(FirebaseUser user, AuthCallback callback) {
//        Map<String, Object> userData = new HashMap<>();
//        String fullName = user.getDisplayName();
//        String[] names = fullName != null ? fullName.split(" ") : new String[]{"", ""};
//
//        userData.put("firstName", names.length > 0 ? names[0] : "");
//        userData.put("surname", names.length > 1 ? names[1] : "");
//        userData.put("fullName", fullName != null ? fullName : "");
//        userData.put("email", user.getEmail());
//        userData.put("countryCode", selectedCountryCode);
//
//        db.collection("users")
//                .document(user.getUid())
//                .set(userData)
//                .addOnSuccessListener(aVoid -> callback.onSuccess(user))
//                .addOnFailureListener(e -> callback.onFailure("Error saving user data"));
//    }
}