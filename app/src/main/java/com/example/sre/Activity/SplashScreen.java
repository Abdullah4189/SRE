package com.example.sre.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sre.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });

        SharedPreferences preferences = getSharedPreferences("SRE_PREFS", MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean("isFirstTime", true);

        SharedPreferences prefs = getSharedPreferences("SRE_PREFS", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (isFirstTime)
                {
                    startActivity(new Intent(SplashScreen.this,DescriptionScreen1.class));
                    overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                    finish();
                }
                else {

                    if (isLoggedIn)
                    {

                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    }
                    else startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                    overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                    finish();

                }

            }
        },1600);
    }
}