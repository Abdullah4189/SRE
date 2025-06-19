package com.example.sre.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpScreen extends AppCompatActivity {

    TextView signup_to_login;
    EditText username_tb;
    EditText email_tb;
    EditText password_tb;
    EditText c_password_tb;
    Button signup_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });

        signup_to_login =findViewById(R.id.signup_to_login);
        username_tb = findViewById(R.id.username);
        email_tb = findViewById(R.id.email);
        password_tb = findViewById(R.id.password);
        c_password_tb = findViewById(R.id.confirm_password);
        signup_btn = findViewById(R.id.signup_btn);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email_tb.getText().toString().isEmpty() ||
                        password_tb.getText().toString().isEmpty() ||
                        c_password_tb.getText().toString().isEmpty() ||
                        username_tb.getText().toString().isEmpty() )
                {
                    Toast.makeText(SignUpScreen.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                }
                else if(!(password_tb.getText().toString().equals(c_password_tb.getText().toString())))
                {
                    Toast.makeText(SignUpScreen.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                }
                else{

                    String email = email_tb.getText().toString();
                    String password = password_tb.getText().toString();

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpScreen.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        Toast.makeText(SignUpScreen.this, "Sign-up Successful!", Toast.LENGTH_SHORT).show();

                                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(user.getEmail(), username_tb.getText().toString());
                                        editor.apply();

                                        Intent intent = new Intent(SignUpScreen.this, LoginScreen.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignUpScreen.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }

            }
        });

        signup_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpScreen.this,LoginScreen.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });



    }


}