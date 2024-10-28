package com.example.tictactoegame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import android.graphics.Typeface;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShowStandingsActivity extends AppCompatActivity {
    private TableLayout standingsTableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_standings);

        standingsTableLayout = findViewById(R.id.standingsTableLayout);
        List<Player> playerList = loadPlayers();

        // Populate the table with player data
        populateTable(playerList);

        // Get SharedPreferences to register the listener
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.equals("show_percentage")) {
                // Refresh the table when the win percentage setting changes
                populateTable(loadPlayers());
            }
        });

        // Back to menu button
        ImageButton returnButton = findViewById(R.id.returnToMainButton);
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(ShowStandingsActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // returns if the win percentage is visible
    private boolean isWinPercentageVisible() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("win_percentage", false);
    }

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

    // fills the table with all player stats
    private void populateTable(List<Player> playerList) {
        standingsTableLayout.removeAllViews(); // Clear existing rows

        // Create table header
        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        headerRow.addView(createTextView(getResources().getString(R.string.playerNameHeader), true, TableRow.LayoutParams.WRAP_CONTENT));
        headerRow.addView(createTextView(getResources().getString(R.string.gamesPlayedHeader), true, TableRow.LayoutParams.WRAP_CONTENT));
        headerRow.addView(createTextView(getResources().getString(R.string.gamesWonHeader), true, TableRow.LayoutParams.WRAP_CONTENT));

        // Check if win percentage should be shown
        if (isWinPercentageVisible()) {
            headerRow.addView(createTextView(getResources().getString(R.string.winPercentHeader), true, TableRow.LayoutParams.WRAP_CONTENT));
        }
        standingsTableLayout.addView(headerRow);

        // Create rows for each player
        for (Player player : playerList) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            row.addView(createTextView(player.getName(), false, TableRow.LayoutParams.WRAP_CONTENT));
            row.addView(createTextView(String.valueOf(player.getGames()), false, TableRow.LayoutParams.WRAP_CONTENT));
            row.addView(createTextView(String.valueOf(player.getWins()), false, TableRow.LayoutParams.WRAP_CONTENT));

            // Only add win percentage if the switch is enabled
            if (isWinPercentageVisible()) {
                double winPercentage = (player.getGames() > 0)
                        ? ((double) player.getWins() / player.getGames()) * 100
                        : 0;
                row.addView(createTextView(String.format(Locale.CANADA,"%.2f%%", winPercentage), false, TableRow.LayoutParams.WRAP_CONTENT));
            }

            standingsTableLayout.addView(row);
        }
    }

    private TextView createTextView(String text, boolean isHeader, int width) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setLayoutParams(new TableRow.LayoutParams(width, TableRow.LayoutParams.WRAP_CONTENT));
        textView.setTextColor(ContextCompat.getColor(this, R.color.textColor)); // Replace 'your_color' with your desired color
        textView.setPadding(16, 16, 16, 16);
        if (isHeader) {
            textView.setTypeface(null, Typeface.BOLD);
        }
        return textView;
    }

}
