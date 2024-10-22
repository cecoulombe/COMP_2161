package com.example.securitytokenapp;

import static com.example.securitytokenapp.MainActivity.KEY_ENTRIES;
import static com.example.securitytokenapp.MainActivity.PREFS_NAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {
    private ListView listView;
    private PasscodeAdapter adapter;
    private List<String> entriesList; // Keep a reference to the list

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

        listView = findViewById(R.id.listview); // Replace with your actual ListView ID

        // Retrieve the string array passed from MainActivity
        String[] passwordEntries = getIntent().getStringArrayExtra("passwordEntries");

        // Convert the array to a List for the adapter
        entriesList = new ArrayList<>();
        if (passwordEntries != null) {
            for (String entry : passwordEntries) {
                entriesList.add(entry);
            }
        }

        // Create an instance of the PasscodeAdapter
        adapter = new PasscodeAdapter(this, entriesList);
        listView.setAdapter(adapter);

        // Button to clear entries
        Button clearButton = findViewById(R.id.clearButton); // Replace with your actual Button ID
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entriesList.clear(); // Clear the list
                adapter.notifyDataSetChanged(); // Notify the adapter about the data change
                clearPasswordEntries(); // Clear the saved entries in SharedPreferences
            }
        });

        // button to return to main
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Save the list of password entries before going back
            savePasswordEntries();

            // Return to MainActivity and finish SecondActivity
            Intent intent = new Intent(SecondActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
//            finish(); // Close SecondActivity
        });
    }

    private void clearPasswordEntries() {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_ENTRIES, null); // Remove the saved entries
        editor.apply();
    }

    // Save entries before finishing the activity
    private void savePasswordEntries() {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String json = new Gson().toJson(entriesList);  // Convert the list to JSON
        editor.putString(KEY_ENTRIES, json);
        editor.apply();
    }
}
