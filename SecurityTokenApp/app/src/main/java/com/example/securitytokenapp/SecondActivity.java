package com.example.securitytokenapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {
    private ArrayAdapter<String> adapter;
    private boolean isSwitchingActivities = false;
    private String entry;
    private boolean justCleared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_secondary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.secondary), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the passwordEntries list and adapter
        List<String> passwordEntries = PasswordManager.getInstance().getEntries();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, passwordEntries);
        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(adapter);

        String oldEntry = getEntry();
        if(!PasswordManager.getInstance().getLoadFLag() && !oldEntry.equals("null"))
        {
            // haven't loaded yet so load it and set flag
            String entry = getEntry();
            PasswordManager.getInstance().addEntry(entry); // Add the received passcode to the list
            adapter.notifyDataSetChanged(); // Notify the adapter to refresh the ListView
            PasswordManager.getInstance().setLoadFlag();
        }

        // Retrieve the passcode from the Intent
        justCleared = getClearFlag();
        int passcode = getIntent().getIntExtra("passcode", -1);
        String timestamp = getIntent().getStringExtra("timestamp");
        if (passcode != -1 && timestamp != null) {
            entry = timestamp + "\n\t" + passcode;
            Log.d("justClearedTest", "checking if entry is a repeat or it just cleared");
            if(!entry.equals(getEntry()) || justCleared)
            {
                Log.d("justClearedTest", justCleared ? "Just cleared, adding prev entry": "Didn't clear, new code added");
                justCleared = false;
                setClearFlag();
                saveEntry(entry);
                PasswordManager.getInstance().addEntry(entry); // Add the received passcode to the list
                adapter.notifyDataSetChanged(); // Notify the adapter to refresh the ListView
            }
            else
            {
                Log.d("justClearedTest", justCleared ? "Just cleared but entry is a repeat": "Didn't clear and a repeated entry");
            }
        } else {
            Log.d("SecondActivity", "No passcode received.");
        }

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            isSwitchingActivities = true;
            Intent intent = new Intent(SecondActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        Button clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(v -> {
            Log.d("ClearButton", "Button was pressed, clearing the list and adding: " + entry);
            PasswordManager.getInstance().clearEntries();
            saveEntry(entry);
            justCleared = true;
            setClearFlag();
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update the adapter if the password entries have changed
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isSwitchingActivities)
        {
            // changing activities, save the data
            Log.d("saving_act2", "App is NOT exiting, just changing activities. Save the data");
        }
        else
        {
            if (isFinishing())
            {
                // app is exiting, clear the data but store entry
                Log.d("saving_act2", "App is exiting, need to clear and save the final piece of data");
            }
            else
            {
                Log.d("saving_act2", "Act is NOT exiting, might be a config change");
                PasswordManager.getInstance().clearEntries();
            }

        }
    }

    private void saveEntry(String entry)
    {
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("entry", entry);
        editor.apply();
    }

    private void setClearFlag()
    {
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("justCleared", justCleared);
        editor.apply();
    }

    private String getEntry()
    {
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPref.getString("entry", "null");
    }

    private Boolean getClearFlag()
    {
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPref.getBoolean("justCleared", false);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        Log.d("configChange_act2", "Changed the orientation of the device");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isSwitchingActivities)
        {
            // changing activities, save the data
            Log.d("saving_act2", "App is NOT exiting, just changing activities. Save the data");
        }
        else
        {
            // app is exiting, clear the data but store entry
            Log.d("saving_act2", "App is exiting, need to clear and save the final piece of data");
        }
    }

    // No need for BroadcastReceiver or saving/loading functionality
}
