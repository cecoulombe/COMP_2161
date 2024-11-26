package com.example.financemanagerapp;

//Objects representing an individual bank account with a name, balance, and flag indicating if it is used in the goal tracking
public class Accounts {

    // Atrtibutes
    String accountName;
    int accountBalance; // using an integer to represent the actual balance * 100 so that it doens't lose precision
    boolean useInGoal;
    boolean useInNetWorth;

    // constructor for firebase
    public Accounts() {}

    // constructor to create an account with a specific name and balance if passed an integer
    public Accounts(String accountName, int accountBalance)
    {
        this.accountName = accountName;
        this.accountBalance = accountBalance;
        this.useInGoal = false;
        this.useInNetWorth = false;
    }

    // returns the account name
    public String getAccountName()
    {
        return accountName;
    }

    // returns the account balance
    public int getAccountBalance()
    {
        return accountBalance;
    }

    // returns the goal flag
    public boolean getUseInGoal()
    {
        return useInGoal;
    }

    // returns the net worth flag
    public boolean getUseInNetWorth()
    {
        return useInNetWorth;
    }

    // sets the account name
    public void setAccountName(String newName)
    {
        accountName = newName;
    }

    // sets the account balance (from an int)
    public void setAccountBalance(int newBalance)
    {
        accountBalance = newBalance;
    }

    // sets the goal flag
    public void setUseInGoal(boolean flag)
    {
        useInGoal = flag;
    }

    // sets the net worth flag
    public void setUseInNetWorth(boolean flag)
    {
        useInNetWorth = flag;
    }
}
