package com.example.saferide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Frontpage extends Activity {
    Button signUpButton;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontpage);

        // Find the buttons by their IDs
        signUpButton = findViewById(R.id.signup_but);
        loginButton = findViewById(R.id.login_but);

        // Set click event listeners for the buttons
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Frontpage.this, Signup.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Frontpage.this, Login.class);
                startActivity(intent);
            }
        });
    }
}