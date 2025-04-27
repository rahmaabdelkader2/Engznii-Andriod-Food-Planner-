package com.example.login_gui_firebase;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.example.login_gui_firebase.OnBoardScreen1;
import com.example.login_gui_firebase.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen); // Your XML file name

        LottieAnimationView animationView = findViewById(R.id.animationView3);

        // Listener for when the animation ends
        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Go to MainActivity (replace with your target activity)
                Intent intent = new Intent(SplashScreen.this, OnBoardScreen1.class);
                startActivity(intent);
                finish(); // Close splash screen so user can't go back
            }
        });
    }
}