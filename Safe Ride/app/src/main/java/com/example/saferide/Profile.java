package com.example.saferide;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class Profile extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button button;
    private TextView textView;
    private FirebaseUser user;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);
        profileImage = findViewById(R.id.profile_image);

        if (user == null) {
            redirectToLogin();
        } else {
            // Display user email with "Welcome" message
            textView.setText("Welcome, " + user.getEmail());

            // Load profile picture if available, otherwise use a placeholder
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .apply(new RequestOptions().placeholder(R.drawable.profileicon)) // Fallback image
                        .into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.profileicon);
            }
        }

        // Logout function
        button.setOnClickListener(view -> signOut());

        // Bottom Navigation Handling
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.Profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.Home){
                startActivity(new Intent(this, Homepage.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (item.getItemId() == R.id.Alert) {
                startActivity(new Intent(this, Alerts.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }else if (item.getItemId() == R.id.Footage) {
                startActivity(new Intent(this, Footage.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            }else if (item.getItemId() == R.id.Profile) {
                return true;
            }
            return false;
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }

    private void signOut() {
        auth.signOut();
        Toast.makeText(Profile.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}
