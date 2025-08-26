package com.example.saferide;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.Home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.Home){
                return true;
            } else if (item.getItemId() == R.id.Alert) {
                startActivity(new Intent(this, Alerts.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            }else if (item.getItemId() == R.id.Footage) {
                startActivity(new Intent(this, Footage.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }else if (item.getItemId() == R.id.Profile) {
                startActivity(new Intent(this, Profile.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}