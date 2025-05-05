package com.example.login_gui_firebase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class OnBoardScreen1 extends AppCompatActivity {
    private LottieAnimationView animationView;
    private Button nextButton;
    private TextView onboardText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.onboard1);

        onboardText = findViewById(R.id.txtboard3);

        animationView = findViewById(R.id.animationView2);
        animationView.playAnimation();

        nextButton = findViewById(R.id.next1);
        nextButton.setOnClickListener(view -> {

            Intent intent = new Intent(OnBoardScreen1.this, OnBoardScreen2.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationView != null) {
            animationView.pauseAnimation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animationView != null) {
            animationView.resumeAnimation();
        }
    }
}