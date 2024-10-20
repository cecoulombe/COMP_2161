package com.example.securitytokenapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
                clearPasswordEntries(); // Call the method to clear entries
            }
        });
    }

    private void clearPasswordEntries()
    {
        entriesList.clear();
        adapter.notifyDataSetChanged();
    }
}

