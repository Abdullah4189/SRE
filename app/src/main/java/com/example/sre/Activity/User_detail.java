package com.example.sre.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sre.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class User_detail extends AppCompatActivity {

    TextView nameTV;
    TextView emailTV;
    CardView tcCV;
    CardView deleteCV;
    CardView logoutCV;
    CardView my_ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nameTV = findViewById(R.id.nameTV);
        emailTV = findViewById(R.id.emailTV);
        tcCV = findViewById(R.id.TC);
        deleteCV = findViewById(R.id.deleteCV);
        logoutCV = findViewById(R.id.logoutCV);




        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String username = sharedPreferences.getString(email, "User");

            nameTV.setText(username);
            emailTV.setText(email);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            Log.e("User_detail", "Firebase user is null");
            // Optional: Redirect to login screen
            Intent intent = new Intent(User_detail.this, LoginScreen.class);
            startActivity(intent);
            finish();
        }

        deleteCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCurrentUserAccount();
            }
        });

        logoutCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = getSharedPreferences("SRE_PREFS", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();

                firebaseAuth.signOut();
                Intent intent = new Intent(User_detail.this, LoginScreen.class);
                startActivity(intent);
                finish();
            }
        });

        tcCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_detail.this, Terms_Conditions.class);
                startActivity(intent);
            }
        });





    }

    public void deleteCurrentUserAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Account deleted successfully
                            Toast.makeText(this, "User account deleted.", Toast.LENGTH_SHORT).show();
                            Log.d("DeleteUser", "User account deleted.");
                            // Optionally, redirect to login screen or close activity
                        } else {
                            // Failed to delete account
                            Log.e("DeleteUser", "Error: " + task.getException().getMessage());
                        }
                    });
        } else {
            Log.e("DeleteUser", "No user is currently signed in.");
        }
    }

}