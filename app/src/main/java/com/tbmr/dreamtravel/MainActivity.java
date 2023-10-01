package com.tbmr.dreamtravel;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.airbnb.lottie.LottieAnimationView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Context context;
    LottieAnimationView animationView; // Declare as a member variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Link to the XML layout

        // Initialize your context and LottieAnimationView
        context = this;
        animationView = findViewById(R.id.logoLottieAnimation);

        // Do something, for example, start the animation
        animationView.playAnimation();
    }
}
