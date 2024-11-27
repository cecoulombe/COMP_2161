package com.example.financemanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GoalTrackerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_goal_tracker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // set up the buttons across the top to save whatever is currently on the screen and to redirect to the appropriate activity
        // instantiate all buttons and disable the button for the current activity. For the rest, set up the change activity
        ImageButton homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> {
            savePage();
            Intent intent = new Intent(GoalTrackerActivity.this, WelcomePageActivity.class);
            startActivity(intent);
        });
        Button accountManagerButton = findViewById(R.id.accountManagerButton);
        accountManagerButton.setOnClickListener(v -> {
            savePage();
            Intent intent = new Intent(GoalTrackerActivity.this, AccountManagerActivity.class);
            startActivity(intent);
        });
        Button netWorthButton = findViewById(R.id.netWorthButton);
        netWorthButton.setOnClickListener(v -> {
            savePage();
            Intent intent = new Intent(GoalTrackerActivity.this, NetWorthCalculator.class);
            startActivity(intent);
        });
        Button goalTrackerButton = findViewById(R.id.goalTrackerButton);
        goalTrackerButton.setEnabled(false);
        ImageButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            savePage();
            Intent intent = new Intent(GoalTrackerActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void savePage()
    {
        // whatever logic needs to happen to save any changes made to the current page
    }
}