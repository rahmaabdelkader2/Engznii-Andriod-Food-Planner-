package com.example.login_gui_firebase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class OnBoardScreen3 extends AppCompatActivity {

    private LottieAnimationView animationView;
    private TextView onboardText3;
    private Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.onboard3);
        start = findViewById(R.id.next3);

        onboardText3= findViewById(R.id.txtboard3);

        animationView = findViewById(R.id.animationView4);
        animationView.playAnimation();


        start.setOnClickListener(v -> {
            Intent intent = new Intent(OnBoardScreen3.this, SignUp.class);
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