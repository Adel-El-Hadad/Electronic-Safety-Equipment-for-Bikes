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

public class Footage extends AppCompatActivity {

    private ListView footageListView;
    private SimpleAdapter adapter;
    private List<Map<String, String>> footageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_footage);

        footageListView = findViewById(R.id.tripfootListView);

        // Sample data
        footageList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Map<String, String> item = new HashMap<>();
            item.put("tripName", "View Trip " + i + " Footage");
            footageList.add(item);
        }

        // Create adapter using custom layout
        adapter = new SimpleAdapter(
                this,
                footageList,
                R.layout.trip_list_item,
                new String[]{"tripName"},
                new int[]{R.id.tripName}
        );

        // Set adapter to ListView
        footageListView.setAdapter(adapter);

        // Handle item click
        footageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFootage = footageList.get(position).get("tripName");
                Toast.makeText(Footage.this, "Clicked: " + selectedFootage, Toast.LENGTH_SHORT).show();
            }
        });

        // Bottom Navigation Handling
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.Footage);

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
            } else if (item.getItemId() == R.id.Footage) {
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
