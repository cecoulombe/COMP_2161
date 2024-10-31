package com.example.tictactoegame;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerManager {
    private SharedPreferences prefs;
    private Gson gson = new Gson();


    public PlayerManager(SharedPreferences prefs)
    {
        this.prefs = prefs;
    }

    // retrieve all player names
    public List<String> getPlayerNames()
    {
        List<String> playerNames = new ArrayList();

        Map<String, ?> allEntries = prefs.getAll();

        // loop through all of the entries and add keys (names) to the list
        for(Map.Entry<String, ?> entry : allEntries.entrySet())
        {
            playerNames.add(entry.getKey());
        }

        return playerNames;
    }


    // Retrieve player by name
    public Player getPlayerByName(String name) {
        String json = prefs.getString(name, null);
        if (json != null) {
            return gson.fromJson(json, Player.class);
        }
        return null; // Returns null if player not found
    }

    // Save player to SharedPreferences
    public void savePlayer(Player player) {
        SharedPreferences.Editor editor = prefs.edit();
        String json = gson.toJson(player);
        editor.putString(player.getName(), json);
        editor.apply();
    }
}
