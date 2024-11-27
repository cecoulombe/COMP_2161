package com.example.financemanagerapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class User {

    // attributes
    private String name;
    private List<Accounts> accountsList;

    // public constructor for firebase
    public User() {}

    // constructor to create a new user
    public User(String name)
    {
        this.name = name;
        accountsList = new ArrayList<>();
    }

    // returns the user's name
    public String getName()
    {
        return name;
    }

    // returns the list of accounts
    public List<Accounts> getAccountsList()
    {
        return accountsList;
    }

    // sets the user's name
    public void setName(String newName)
    {
        name = newName;
    }

    // adds an account to the list
    public void addAccount(String accountName, int accountBalance)
    {
        if(!accountExists(accountName))
        {
            accountsList.add(new Accounts(accountName, accountBalance));
        }
        Log.d("addAccount", "An account with that name already exists");
    }

    // modifies an account name
    public void modifyAccountName(String oldName, String newName)
    {
        for(Accounts acc : accountsList)
        {
            if(acc.getAccountName().equals(oldName))
            {
                acc.setAccountName(newName);
                return;
            }
        }
        Log.d("modifyAccountName", "No such account exists");
    }

    // modifies an account balance
    public void modifyAccountBalance(String name, int newBalance)
    {
        for(Accounts acc : accountsList)
        {
            if(acc.getAccountName().equals(name))
            {
                acc.setAccountBalance(newBalance);
                return;
            }
        }
        Log.d("modifyAccountBalance", "No such account exists");
    }

    // returns a specific account reference
    public int getAccountBalance(String name)
    {
        for(Accounts acc : accountsList)
        {
            if(acc.getAccountName().equals(name))
            {
                return acc.getAccountBalance();
            }
        }
        Log.d("modifyAccountBalance", "No such account exists");
        return 0;
    }

    // sets the flag to indicate whether or not the account is to be used in the goal tracking
    public void setGoalFlag(String name, boolean flag)
    {
        for(Accounts acc : accountsList)
        {
            if(acc.getAccountName().equals(name))
            {
                acc.setUseInGoal(flag);
                return;
            }
        }
        Log.d("setGoalFlag", "No such account exists");
    }

    // sets the flag to indicate whether or not the account is to be used in the net worth calculations
    public void setNetWorthFlag(String name, boolean flag)
    {
        for(Accounts acc : accountsList)
        {
            if(acc.getAccountName().equals(name))
            {
                acc.setUseInNetWorth(flag);
                return;
            }
        }
        Log.d("setNetWorthFlag", "No such account exists");
    }

    // returns true if there exists an account with that name
    public boolean accountExists(String name)
    {
        for(Accounts acc : accountsList)
        {
            if(acc.getAccountName().equals(name))
            {
                return true;
            }
        }
        return false;
    }

    // deletes an account from the list
    public void deleteAccount(String name)
    {
        for(Accounts acc : accountsList)
        {
            if(acc.getAccountName().equals(name))
            {
                accountsList.remove(acc);
                return;
            }
        }
        Log.d("deleteAccount", "No such account exists");

    }


}
