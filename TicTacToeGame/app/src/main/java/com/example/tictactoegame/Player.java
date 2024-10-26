package com.example.tictactoegame;

// class to store the player's name, total games, and wins
public class Player {
    // global variables
    String name;
    int totalGames;
    int totalWins;

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
    }

    // increment the total wins
    public void addWin()
    {
        totalWins++;
    }
}
