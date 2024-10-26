package com.example.tictactoegame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.gson.Gson;

import java.util.List;

public class EnterNamesActivity extends AppCompatActivity {
    private ListView listView;
    private PlayerManager manager;
    private EditText nameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enter_names);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // initialize the edittext
        nameInput = findViewById(R.id.enterNameEditText);

        // back to menu button
        ImageButton returnButton = findViewById(R.id.returnToMainButton);
        returnButton.setOnClickListener(v ->{
            Intent intent = new Intent(EnterNamesActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // save name button
        Button saveButton = findViewById(R.id.saveNameButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String name = nameInput.getText().toString().trim();
                if(!name.isEmpty())
                {
                    int games = 0;
                    int wins = 0;
                    addNewPlayer(name, games, wins);
                } else {
                    Toast.makeText(EnterNamesActivity.this, "Please enter a player name to save", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // set up the listview
        updateListView();
    }

    // creates a new player if there is not already an existing player of the same name
    private void addNewPlayer(String name, int totalGames, int totalWins)
    {
        if(!playerExists(this, name))
        {
            Player player = new Player(name, totalGames, totalWins);
            savePlayer(this, player);
            updateListView();
            nameInput.setText("");
            Toast.makeText(EnterNamesActivity.this, "Name successfully saved!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(EnterNamesActivity.this, "That name is already taken. Please chose another.", Toast.LENGTH_SHORT).show();
        }
    }

    // checks if there is already a player with that name
    private boolean playerExists(Context context, String name)
    {
        SharedPreferences prefs = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        return prefs.contains(name);
    }

    // saves the player data to the prefs
    private void savePlayer(Context context, Player player)
    {
        SharedPreferences prefs = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(player);
        editor.putString(player.getName(), json);
        editor.apply();
    }

    // update the listview to show all names
    private void updateListView()
    {
        listView = findViewById(R.id.existingPlayersListView);
        manager = new PlayerManager(getSharedPreferences("PlayerData", MODE_PRIVATE));

        List<String> playerNames = manager.getPlayerNames();

        // set up an array adapter for the listview
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playerNames);
        listView.setAdapter(adapter);
    }

}