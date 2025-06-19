package com.example.sre.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sre.R;

public class DescriptionScreen3 extends AppCompatActivity {

    Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_description_screen3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });


        nextButton = findViewById(R.id.buttonNext);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(DescriptionScreen3.this,LoginScreen.class));
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                finish();
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed(); // Default back behavior
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left); // Add animation
    }

}