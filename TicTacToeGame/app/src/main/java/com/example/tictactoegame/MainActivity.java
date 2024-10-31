package com.example.tictactoegame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import com.example.tictactoegame.ButtonAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // check whether the app should be in dark or light mode and apply the change
        manageDarkMode();

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ListView listView = findViewById(R.id.listView);

        // Create an ArrayList and add strings from strings.xml
        ArrayList<String> buttonLabels = new ArrayList<>();
        buttonLabels.add(getString(R.string.EnterNameButton));
        buttonLabels.add(getString(R.string.PlayGameButton));
        buttonLabels.add(getString(R.string.StandingsButton));

        // Corresponding activities
        ArrayList<Class<?>> activities = new ArrayList<>();
        activities.add(EnterNamesActivity.class);
        activities.add(PlayGameActivity.class);
        activities.add(ShowStandingsActivity.class);


        // Create and set the adapter
        ButtonAdapter adapter = new ButtonAdapter(this, buttonLabels, activities);
        listView.setAdapter(adapter);

        // manage the settings button
        ImageButton settingButton = findViewById(R.id.settings_button);
        settingButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // creates the android
        if(!androidExists(this, "Android"))
        {
            Player android = new Player("Android", 0, 0);
            saveAndroid(this, android);
        }
    }

    // checks the user prefs and determines whether or not the app should be in dark mode. Applies any changes
    private void manageDarkMode()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false); // Default is false if not set

        // Apply the theme based on the preference
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);
    }

    // checks if the android has already been created
    private boolean androidExists(Context context, String name)
    {
        SharedPreferences prefs = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        return prefs.contains(name);
    }

    // creates a new android player (will only run on first launch or when the app data is reset)
    private void saveAndroid(Context context, Player player)
    {
        SharedPreferences prefs = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(player);
        editor.putString(player.getName(), json);
        editor.apply();
    }
}