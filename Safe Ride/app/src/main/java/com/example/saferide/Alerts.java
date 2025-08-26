package com.example.saferide;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Alerts extends AppCompatActivity {

    private ListView alertsListView;
    private SimpleAdapter adapter;
    private List<Map<String, String>> alertsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        alertsListView = findViewById(R.id.tripsListView);

        // Sample data
        alertsList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Map<String, String> item = new HashMap<>();
            item.put("tripName", "View Trip " + i + " Alerts");
            alertsList.add(item);
        }

        // Create adapter using custom layout
        adapter = new SimpleAdapter(
                this,
                alertsList,
                R.layout.trip_list_item,
                new String[]{"tripName"},
                new int[]{R.id.tripName}
        );

        // Set adapter to ListView
        alertsListView.setAdapter(adapter);

        // Handle item click
        alertsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedAlert = alertsList.get(position).get("tripName");
                Toast.makeText(Alerts.this, "Clicked: " + selectedAlert, Toast.LENGTH_SHORT).show();
            }
        });

        // Bottom Navigation Handling
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.Alert);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.Home){
                startActivity(new Intent(this, Homepage.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (item.getItemId() == R.id.Alert) {
                return true;
            } else if (item.getItemId() == R.id.Footage) {
                startActivity(new Intent(this, Footage.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (item.getItemId() == R.id.Profile) {
                startActivity(new Intent(this, Profile.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}
