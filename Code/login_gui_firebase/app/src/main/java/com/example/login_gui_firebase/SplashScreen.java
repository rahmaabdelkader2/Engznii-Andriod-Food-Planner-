package com.example.login_gui_firebase;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        LottieAnimationView animationView = findViewById(R.id.animationView3);

        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                checkFirstTimeLaunch();
            }
        });
    }

    private void checkFirstTimeLaunch() {
        ScreenManagers manager = new ScreenManagers(this);

        Intent intent;
        if (manager.isFirstTimeLaunch()) {
            intent = new Intent(SplashScreen.this, OnBoardScreen1.class);
            manager.setFirstTimeLaunch(false);
        } else {
            intent = new Intent(SplashScreen.this, Login.class);
        }

        startActivity(intent);
        finish();
    }
}