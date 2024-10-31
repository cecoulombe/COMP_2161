package com.example.tictactoegame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Load the SettingsFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();

        // Set up the back button
        ImageButton returnButton = findViewById(R.id.returnToMainButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the settings activity
            }
        });

        // set up the reset stats button
        Button resetStatsButton = findViewById(R.id.resetStatsButton);
        resetStatsButton.setOnClickListener(v -> showResetStatsConfirmationDialog());

        // set up the clear player list button
        Button clearPlayerButton = findViewById(R.id.clearPlayersButton);
        clearPlayerButton.setOnClickListener(v -> showClearPlayersConfirmationDialog());
    }

    // popup to confirm that the player wants to delete all stats
    private void showResetStatsConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.resetStatsTitle)
                .setMessage(R.string.resetStatsSummary)
                .setPositiveButton(R.string.yes, (dialog, which) -> resetPlayerList())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    // popup to confirm that the user wants to delete all players and their data
    private void showClearPlayersConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.resetPlayerListTitle))
                .setMessage(R.string.resetPlayerListSummary)
                .setPositiveButton(R.string.yes, (dialog, which) -> clearPlayerList())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    // clears the player statistics but keeps the player names
    private void resetPlayerList() {
        // Clear players from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("PlayerData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // load the current list of players
        Gson gson = new Gson();
        List<Player> players = loadPlayers(); // Load the current list of players

        // Loop through each player and reset their stats while keeping their names
        for (Player player : players) {
            player.setGames(0); // Reset games played
            player.setWins(0); // Reset wins
            String playerJson = gson.toJson(player);
            editor.putString(player.getName(), playerJson); // Save updated player data
        }

        editor.apply(); // Commit changes
    }

    // clears all players and their data
    private void clearPlayerList() {
        // Clear players from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("PlayerData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Removes all player data
        editor.apply();
    }

    // creates a list of all of the existing players
    private List<Player> loadPlayers() {
        SharedPreferences prefs = getSharedPreferences("PlayerData", MODE_PRIVATE);
        List<Player> players = new ArrayList<>();
        for (String name : prefs.getAll().keySet()) {
            String json = prefs.getString(name, "");
            if (!json.isEmpty()) {
                Player player = new Gson().fromJson(json, Player.class);
                players.add(player);
            }
        }
        return players;
    }
}
