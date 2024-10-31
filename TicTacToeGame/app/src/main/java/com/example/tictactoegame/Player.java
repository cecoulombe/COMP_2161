package com.example.tictactoegame;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

// class to store the player's name, total games, and wins
public class Player {
    // global variables
    String name;
    int totalGames;
    int totalWins;
    String time;

    // create a new player and initialize all values
    public Player(String name, int totalGames, int totalWins)
    {
        this.name = name;
        this.totalGames = totalGames;
        if(totalWins <= totalGames)
        {
            this.totalWins = totalWins;
        } else {
            this.totalWins = totalGames;
        }
        setTime();
    }

    // sets the number of games played
    public void setGames(int games)
    {
        totalGames = games;
        setTime();
    }

    // sets the number of games won
    public void setWins(int wins)
    {
        totalWins = wins;
    }

    // returns the players name
    public String getName()
    {
        return name;
    }

    // returns the number of games played
    public int getGames()
    {
        return totalGames;
    }

    // returns the number of wins
    public int getWins()
    {
        return totalWins;
    }

    // increment the games played
    public void addGame()
    {
        totalGames++;
        setTime();
    }

    // increment the total wins
    public void addWin()
    {
        totalWins++;
    }

    // setts the time of the most recently played game
    public void setTime()
    {
        // Get the current date and time
        Date date = new Date();

        // Format it
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        time = formatter.format(date);
    }

    // returns the time of the most recently played game
    public String getTime()
    {
        return time;
    }
}
