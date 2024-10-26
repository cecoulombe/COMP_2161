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
}
