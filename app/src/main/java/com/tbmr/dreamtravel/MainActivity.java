package com.tbmr.dreamtravel;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.airbnb.lottie.LottieAnimationView;

import java.util.Timer;
import java.util.TimerTask;

import java.util.Timer;
import java.util.TimerTask;
public class MainActivity extends AppCompatActivity {

    Context context;
    private Timer splashTimer;
    private static final long DELAY = 2600;
    private boolean schedule = false;
    LottieAnimationView animationView; // Declare as a member variable

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Link to the XML layout

        // Initialize your context and LottieAnimationView
        context = this;
        animationView = findViewById(R.id.logoLottieAnimation);

        // Initialize Timer
        splashTimer = new Timer();

        // Do something, for example, start the animation
        animationView.playAnimation();
        splashTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(context, LoginScreen.class);
                startActivity(intent);
            }
        }, DELAY);
        schedule = true ;
    }

}