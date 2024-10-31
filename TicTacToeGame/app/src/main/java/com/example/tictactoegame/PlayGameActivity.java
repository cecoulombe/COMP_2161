package com.example.tictactoegame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public class PlayGameActivity extends AppCompatActivity {
    // global variables
    private Button startGameButton;
    private Spinner player1Spinner;
    private Spinner player2Spinner;
    private PlayerManager playerManager;
    private List<String> allPlayerNames;
    private Button[] boardButtons;
    private TextView player1StatsTextView;
    private TextView player2StatsTextView;
    private boolean showStats;
    private boolean player1Turn;
    private String player1Name;
    private String player2Name;
    private TextView playerIndicator;
    private Button newGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // back to menu button
        ImageButton returnButton = findViewById(R.id.returnToMainButton);
        returnButton.setOnClickListener(v ->{
            Intent intent = new Intent(PlayGameActivity.this, MainActivity.class);
            startActivity(intent);
        });

        newGameButton = findViewById(R.id.playNewGameButton);
        newGameButton.setEnabled(false);
        newGameButton.setVisibility(View.GONE);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });

        // set up the spinners
        // Initialize spinners and PlayerManager
        player1Spinner = findViewById(R.id.player1Spin);
        player2Spinner = findViewById(R.id.player2Spin);

        SharedPreferences prefs = getSharedPreferences("PlayerData", MODE_PRIVATE);
        playerManager = new PlayerManager(prefs);
        allPlayerNames = playerManager.getPlayerNames();

        // start game button
        // initialize start button
        startGameButton = findViewById(R.id.startGameButton);
        player1Name = "";
        player2Name = "";
        player1Turn = true;
        playerIndicator = findViewById(R.id.currentPlayerTextView);

        // Start game button click listener
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });

        // get the settings to know if the stats should be shown
        SharedPreferences settingsPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        showStats = settingsPrefs.getBoolean("show_stats", true);
        Log.d("PlayGameActivity", "Show Stats: " + showStats);

        // initialize the text view in case showStats is on
        player1StatsTextView = findViewById(R.id.player1TextView);
        player2StatsTextView = findViewById(R.id.player2TextView);
        if(!showStats)
        {
            player1StatsTextView.setVisibility(View.GONE);
            player2StatsTextView.setVisibility(View.GONE);
        } else {
            player1StatsTextView.setVisibility(View.VISIBLE);
            player2StatsTextView.setVisibility(View.VISIBLE);
        }

        // Populate the first spinner which will set up the second
        setupPlayer1Spinner();

        // initialize board buttons and disable them initially
        boardButtons = new Button[]{
                findViewById(R.id.button_00), findViewById(R.id.button_01), findViewById(R.id.button_02),
                findViewById(R.id.button_03), findViewById(R.id.button_04), findViewById(R.id.button_05),
                findViewById(R.id.button_06), findViewById(R.id.button_07), findViewById(R.id.button_08),
        };
        for (Button boardButton : boardButtons) {
            boardButton.setEnabled(false); // Disable board buttons initially
            boardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    pressButton(v);
                }
            });
        }

    }

    // creates the array for spinner 1
    private void setupPlayer1Spinner() {
        // Create a filtered list of player names excluding "Android"
        List<String> player1Names = new ArrayList<>(allPlayerNames);
        player1Names.remove("Android");

        // Add "Guest" as an additional player option
        if (!player1Names.contains("Guest")) {
            player1Names.add("Guest");
        }

        // Create an adapter for player 1 with the filtered names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, player1Names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        player1Spinner.setAdapter(adapter);

        // Update the stats display for player 1 for the default player
        String defaultPlayer = player1Spinner.getSelectedItem().toString();
        updatePlayerStats(defaultPlayer, player1StatsTextView);

        // Set listener for player 1 spinner selection to update player 2 spinner
        player1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlayer1 = player1Spinner.getSelectedItem().toString();

                // Update the stats display for player 1 if enabled in settings
                updatePlayerStats(selectedPlayer1, player1StatsTextView);

                setupPlayer2Spinner(selectedPlayer1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setupPlayer2Spinner(null);
                player1StatsTextView.setText("");
            }
        });
    }


    // creates the array for spinner 2
    private void setupPlayer2Spinner(String selectedPlayer1) {
        // Prepare player 2 list excluding the player 1 selection and adding "Guest"
        List<String> player2Options = new ArrayList<>(allPlayerNames);
        player2Options.remove(selectedPlayer1);  // Remove the selected player from player 1
        player2Options.add(player2Options.size(), "Guest");  // Add "Guest" as the last option

        // Set up the adapter for player 2 spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, player2Options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        player2Spinner.setAdapter(adapter);

        // Set "Android" as the default if it exists in the list
        int defaultPosition = player2Options.indexOf("Android");
        if (defaultPosition != -1) {  // Ensure "Android" exists in the list
            player2Spinner.setSelection(defaultPosition);
        } else {
            player2Spinner.setSelection(0); // Default to "Guest" if "Android" isn't found
        }

        // Update the stats display for player 1 for the default player
        String defaultPlayer = player1Spinner.getSelectedItem().toString();
        updatePlayerStats(defaultPlayer, player1StatsTextView);

        // Set listener for player 2 spinner selection to display stats if enabled
        player2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlayer2 = player2Spinner.getSelectedItem().toString();

                // Only update stats if the setting is enabled and player 2 isn't "Guest"
                updatePlayerStats(selectedPlayer2, player2StatsTextView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                player2StatsTextView.setText("");
            }
        });
    }

    // start the game and enable all buttons, disable spinners for player selection, increment the number of games for each player, and update the turn indicator
    private void startGame()
    {
        // Disable and hide the start button
        startGameButton.setEnabled(false);
        startGameButton.setVisibility(View.GONE);

        // Lock the spinners
        player1Spinner.setEnabled(false);
        player2Spinner.setEnabled(false);

        // Enable the board buttons
        for (Button boardButton : boardButtons) {
            boardButton.setEnabled(true);
            boardButton.setText(""); // Clear the button text
            boardButton.setTextColor(getColor(R.color.textColor));  // reset the color of all buttons
        }

        // increment the games played for each player
        // Retrieve selected player names from the spinners
        player1Name = player1Spinner.getSelectedItem().toString();
        player2Name = player2Spinner.getSelectedItem().toString();

        // Increment games played for both players
        incrementGamesPlayed(player1Name, player1StatsTextView);
        incrementGamesPlayed(player2Name, player2StatsTextView);

        // set the current turn
        player1Turn = true;
        playerIndicator.setText(getString(R.string.playerTurnPrompt, player1Name));
        
    }

    // add a game played for the player in question
    private void incrementGamesPlayed(String playerName, TextView textView) {
        SharedPreferences prefs = getSharedPreferences("PlayerData", MODE_PRIVATE);
        PlayerManager manager = new PlayerManager(prefs);

        // Retrieve the player by name
        Player player = manager.getPlayerByName(playerName);

        if (player != null) {
            player.addGame(); // Increment the games played
            manager.savePlayer(player); // Save the updated player back to SharedPreferences
            updatePlayerStats(playerName, textView); // Update the stats display
        }
    }

    // helper method to update stats for a player
    private void updatePlayerStats(String playerName, TextView statsTextView) {
        SharedPreferences prefs = getSharedPreferences("PlayerData", MODE_PRIVATE);
        PlayerManager manager = new PlayerManager(prefs);
        Player player = manager.getPlayerByName(playerName);

        if (player != null) {
            String stats = player.getWins() + " / " + player.getGames();
            statsTextView.setText(stats);
        } else {
            statsTextView.setText(""); // Clear if player data is unavailable
        }
    }

    // handle button presses and turn management
    private void pressButton(View v)
    {
        // check if its valid
        if(checkValid(v))
        {
            // change the text
            setSymbol(v);

            // check if it won
            if(isWinner())
            {
                disableButtons();
                incrementGamesWon(currentPlayer());
                newGameButton.setEnabled(true);
                newGameButton.setVisibility(View.VISIBLE);
                playerIndicator.setText(getString(R.string.winnerMsg, currentPlayer()));
            } else {
                if(isDraw())
                {
                    disableButtons();
                    newGameButton.setEnabled(true);
                    newGameButton.setVisibility(View.VISIBLE);
                    playerIndicator.setText(getString(R.string.drawMsg));
                } else {
                    changeTurn();
                    // if current turn is android, manage that, otherwise just continue on
                    if(currentPlayer().equals("Android"))
                    {
                        androidTurn();
                    }
                }
            }
        } else {
            Toast.makeText(this, "Must choose an empty spot!", Toast.LENGTH_SHORT).show();
        }
    }

    // change the turn indicator's text
    private void changeTurn()
    {
        player1Turn = !player1Turn;

        playerIndicator.setText(getString(R.string.playerTurnPrompt, currentPlayer()));
    }

    // returns the current player
    private String currentPlayer()
    {
        return player1Turn ? player1Name : player2Name;
    }

    // check if the current square has already been marked
    private boolean checkValid(View v)
    {
        Button button = (Button) v; // Cast the View to a Button

        // Check the current text of the button
        String buttonText = button.getText().toString();

        return buttonText.isEmpty();
    }

    // sets the symbol according to the player
    private void setSymbol(View v)
    {
        Button button = (Button) v; // Cast the View to a Button

        if(player1Turn)
        {
            button.setText("X");
        } else {
            button.setText("O");
        }
    }

    // check if the last move won the game
    private boolean isWinner()
    {
        // create all possible combinations for a win
        int [][] winningCombinations = {
                {0, 1, 2}, // Row 1
                {3, 4, 5}, // Row 2
                {6, 7, 8}, // Row 3
                {0, 3, 6}, // Column 1
                {1, 4, 7}, // Column 2
                {2, 5, 8}, // Column 3
                {0, 4, 8}, // Diagonal \
                {2, 4, 6}  // Diagonal /
        };

        // Check each winning combination
        for (int[] combination : winningCombinations) {
            String text1 = boardButtons[combination[0]].getText().toString();
            String text2 = boardButtons[combination[1]].getText().toString();
            String text3 = boardButtons[combination[2]].getText().toString();

            if (!text1.isEmpty() && text1.equals(text2) && text1.equals(text3)) {
                // Change the color of the winning buttons to green
                for (int index : combination) {
                    boardButtons[index].setTextColor(getColor(R.color.winColor));
                }
                return true; // There is a winner
            }
        }

        // no winner found
        return false;
    }

    // check if the last move caused a draw
    private boolean isDraw()
    {
        for (Button boardButton : boardButtons) {
            if(boardButton.getText().toString().isEmpty())
            {
                // not a draw
                return false;
            }
        }
        // is a draw
        return true;
    }

    // disable all buttons
    private void disableButtons()
    {
        for (Button boardButton : boardButtons) {
            boardButton.setEnabled(false);
        }
    }

    // enables all buttons
    private void enableButtons()
    {
        for (Button boardButton : boardButtons) {
            boardButton.setEnabled(true);
        }
    }

    // add a game played for the player in question
    private void incrementGamesWon(String playerName) {
        SharedPreferences prefs = getSharedPreferences("PlayerData", MODE_PRIVATE);
        PlayerManager manager = new PlayerManager(prefs);

        // Retrieve the player by name
        Player player = manager.getPlayerByName(playerName);

        TextView textView;
        if(playerName.equals("player1Name"))
        {
            textView = player1StatsTextView;
        } else {
            textView = player2StatsTextView;
        }
        
        if (player != null) {
            player.addWin(); // Increment the games played
            manager.savePlayer(player); // Save the updated player back to SharedPreferences
            updatePlayerStats(playerName, textView); // Update the stats display
        }
    }

    // calls the asyncTask thread for the android turn
    private void androidTurn()
    {
        // do the android turn logic
        disableButtons();
        new AIMoveTask().execute();
    }

    // Add a method to reset the game state
    private void resetGame() {
        newGameButton.setEnabled(false);
        newGameButton.setVisibility(View.GONE);

        // will try resetting the board in the startGame method
        for (Button boardButton : boardButtons) {
            boardButton.setEnabled(false);
        }

        player1Turn = true; // Reset to player 1's turn
        playerIndicator.setText(getString(R.string.playerTurnPrompt, player1Name));
        startGameButton.setEnabled(true); // Re-enable the start button
        startGameButton.setVisibility(View.VISIBLE);
        player1Spinner.setEnabled(true); // Re-enable spinners
        player2Spinner.setEnabled(true);
    }
    
    // android turn logic in AsyncTask
    private class AIMoveTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            // Introduce a delay to simulate thinking if enabled
            if(giveAIDelay())
            {
                try {
                    Thread.sleep(1000); // Delay for 1.5 seconds (1500 milliseconds)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            int position;
            boolean moveMade = false;

            // Find an empty spot
            do {
                position = (int) (Math.random() * 9); // Random position between 0 and 8
                if (boardButtons[position].getText().toString().isEmpty()) {
                    moveMade = true;
                }
            } while (!moveMade);

            return position; // Return the position where the AI will move
        }

        @Override
        protected void onPostExecute(Integer position) {
            // Update the UI with the AI's move
            if (position != null) {
                boardButtons[position].setText("O"); // AI's symbol
            }

            // check winner and draw conditions
            if (isWinner()) {
                disableButtons();
                incrementGamesWon(currentPlayer());
                newGameButton.setEnabled(true);
                newGameButton.setVisibility(View.VISIBLE);
                playerIndicator.setText(getString(R.string.winnerMsg, currentPlayer()));
            } else {
                if (isDraw()) {
                    disableButtons();
                    newGameButton.setEnabled(true);
                    newGameButton.setVisibility(View.VISIBLE);
                    playerIndicator.setText(getString(R.string.drawMsg));
                } else {
                    changeTurn();
                    enableButtons();
                    // will not be an AI next, so no need to put that here
                }
            }
        }
    }

    // returns if the win percentage is visible
    private boolean giveAIDelay() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("ai_delay", true);
    }
}