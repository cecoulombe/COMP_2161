package com.example.financemanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AccountManagerActivity extends AppCompatActivity {
    TableLayout table;
            
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_manager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // set up the buttons across the top to save whatever is currently on the screen and to redirect to the appropriate activity
        // instantiate all buttons and disable the button for the current activity. For the rest, set up the change activity
        ImageButton homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> {
            savePage();
            Intent intent = new Intent(AccountManagerActivity.this, WelcomePageActivity.class);
            startActivity(intent);
        });
        Button accountManagerButton = findViewById(R.id.accountManagerButton);
        accountManagerButton.setEnabled(false);
        Button netWorthButton = findViewById(R.id.netWorthButton);
        netWorthButton.setOnClickListener(v -> {
            savePage();
            Intent intent = new Intent(AccountManagerActivity.this, NetWorthCalculator.class);
            startActivity(intent);
        });
        Button goalTrackerButton = findViewById(R.id.goalTrackerButton);
        goalTrackerButton.setOnClickListener(v -> {
            savePage();
            Intent intent = new Intent(AccountManagerActivity.this, GoalTrackerActivity.class);
            startActivity(intent);
        });
        ImageButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            savePage();
            Intent intent = new Intent(AccountManagerActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        Button signOutButton = findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(v -> {
            savePage();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AccountManagerActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // add the headers to the table
        table = findViewById(R.id.accountsTableLayout);

        // add header row
        TableRow headerRow = new TableRow(this);

        TextView nameHeader = new TextView(this);
        nameHeader.setText(getResources().getString(R.string.accountNameHeader));
        nameHeader.setTextSize(18);
        nameHeader.setPadding(5, 5, 5, 5);
        headerRow.addView(nameHeader);

        TextView balanceHeader = new TextView(this);
        balanceHeader.setText(getResources().getString(R.string.accountBalanceHeader));
        balanceHeader.setTextSize(18);
        balanceHeader.setPadding(5, 5, 5, 5);
        headerRow.addView(balanceHeader);

        // populate the table form the saved user data
        populateTableFromSavedData();

        // set up the add new account button
        Button createNewAccountButton = findViewById(R.id.createNewAccountButton);
        createNewAccountButton.setOnClickListener(v -> {
            createNewAccount_Popup();
        });
    }

    // creates a new account within the user object
    private void createNewAccount_Row(String name)
    {
        TableRow tableRow = new TableRow(this);

        // name button
        Button nameButton = new Button(this);
        nameButton.setText(name);
        nameButton.setOnClickListener(v -> editName(v, name));      // handles the popup when the name button is clicked
        tableRow.addView(nameButton);

        // balance button
        Button balanceButton = new Button(this);
        int balanceInCents = GlobalUser.getUser().getAccountBalance(name);
        double balanceInDollars = balanceInCents / 100.0;

        NumberFormat currentFormatter = NumberFormat.getCurrencyInstance(Locale.CANADA);
        String formattedBalance = currentFormatter.format(balanceInDollars);

        balanceButton.setText(formattedBalance);
        balanceButton.setOnClickListener(v -> editBalance(v, name));      // handles the popup when the name button is clicked
        tableRow.addView(balanceButton);
    }

    // creates a popup which allows the user to enter the name and starting balance for a new account
    private void createNewAccount_Popup()
    {
        // test values
        // String name = "";
        // int balance = 0;

        // Inflate the custom layout for the dialog
        LinearLayout dialogLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_new_account, null);
        EditText nameEditText = dialogLayout.findViewById(R.id.editAccountName);
        EditText balanceEditText = dialogLayout.findViewById(R.id.editAccountBalance);

        // create and show the AlertDialog
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.name_alertBuilderText))
                .setView(dialogLayout)
                .setPositiveButton("Create New Account", (dialog, which) ->
                {
                    // get user input and update the button text
                    String name = nameEditText.getText().toString().trim();
                    String balance = balanceEditText.getText().toString();
                    double balanceInDollars = Double.parseDouble(balance);
                    int balanceInCents = (int) balanceInDollars * 100;

                    if(!name.isEmpty() && !balance.isEmpty())
                    {
                        if(!GlobalUser.getUser().accountExists(name))
                        {
                            createNewAccount(name, balanceInCents);
                            Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }
                        Toast.makeText(this, "Account with that name already exists.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, name.isEmpty() ? "Account name must not be empty." : "Account must have an initial balance.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();


    }

    // creates a popup which allows the user to enter the name and starting balance for a new account
    private void createNewAccount(String name, int balance)
    {
        GlobalUser.getUser().addAccount(name, balance);

        createNewAccount_Row(name);
    }

    // creates a popup which allows the user to edit the account name
    private void editName(View v, String oldName)
    {
        // Inflate the custom layout for the dialog
        LinearLayout dialogLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_edit_account, null);
        TextView currentNameTextView = findViewById(R.id.currentAccountName);
        EditText editAccountName = dialogLayout.findViewById(R.id.editAccountName);

        // set the current name of the button in the textview
        currentNameTextView.setText(getResources().getString(R.string.currentAccountNameLabel, oldName));

        // cast the view to a button to update the label
        Button button = (Button) v;

        // create and show the AlertDialog
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.name_alertBuilderText))
                .setView(dialogLayout)
                .setPositiveButton("Save", (dialog, which) ->
                {
                    // get user input and update the button text
                    String newName = editAccountName.getText().toString().trim();
                    if(!newName.isEmpty())
                    {
                        if(!GlobalUser.getUser().accountExists(newName))
                        {
                            GlobalUser.getUser().modifyAccountName(oldName, newName);
                            button.setText(newName);
                            Toast.makeText(this, "Account name updated!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        Toast.makeText(this, "Account with that name already exists. Please try a new name", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Account name cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // creates a popup which allows the user to edit the account balance
    private void editBalance(View view, String name)
    {
        // Inflate the custom layout for the dialog
        LinearLayout dialogLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_edit_balance, null);

        TextView currentBalanceView = dialogLayout.findViewById(R.id.currentBalance);
        EditText editBalanceInput = dialogLayout.findViewById(R.id.editBalance);
        Button cancelButton = dialogLayout.findViewById(R.id.cancelButton);
        Button addButton = dialogLayout.findViewById(R.id.addButton);
        Button subtractButton = dialogLayout.findViewById(R.id.subtractButton);
        Button replaceButton = dialogLayout.findViewById(R.id.replaceButton);

        // Cast the view to a button to get which button was pressed
        Button accountButton = (Button) view;

        // get the current balance of the button from the user
        int balanceInCents = GlobalUser.getUser().getAccountBalance(name);
        double balanceInDollars = balanceInCents / 100.0;
        NumberFormat currentFormatter = NumberFormat.getCurrencyInstance(Locale.CANADA);
        String formattedBalance = currentFormatter.format(balanceInDollars);

        // display the balance in the textview
        currentBalanceView.setText(getResources().getString(R.string.currentAccountBalanceLabel, formattedBalance));

        // Create and show the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.balance_alertBuilderText))
                .setView(dialogLayout)
                .create();

        // button logic
        cancelButton.setOnClickListener(v ->
        {
            dialog.dismiss();
        });

        addButton.setOnClickListener(v ->
        {
            String input = editBalanceInput.getText().toString();
            if(!input.isEmpty())
            {
                // save the updated value
                double inputInDollars = Double.parseDouble(input);
                int inputInCents = (int) inputInDollars * 100;
                int newBalance = balanceInCents + inputInCents;
                GlobalUser.getUser().modifyAccountBalance(name, newBalance);

                // display the updated value
                updateBalanceOnButton(view, name);
                Toast.makeText(this, "Account balance updated.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        subtractButton.setOnClickListener(v ->
        {
            String input = editBalanceInput.getText().toString();
            if(!input.isEmpty())
            {
                // save the updated value
                double inputInDollars = Double.parseDouble(input);
                int inputInCents = (int) inputInDollars * 100;
                int newBalance = balanceInCents - inputInCents;
                GlobalUser.getUser().modifyAccountBalance(name, newBalance);

                // display the updated value
                updateBalanceOnButton(view, name);
                Toast.makeText(this, "Account balance updated.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        addButton.setOnClickListener(v ->
        {
            String input = editBalanceInput.getText().toString();
            if(!input.isEmpty())
            {
                // save the updated value
                double inputInDollars = Double.parseDouble(input);
                int inputInCents = (int) inputInDollars * 100;
                GlobalUser.getUser().modifyAccountBalance(name, inputInCents);

                // display the updated value
                updateBalanceOnButton(view, name);
                Toast.makeText(this, "Account balance updated.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        
        dialog.show();

    }

    // updates the button's displayed balance based on the account
    private void updateBalanceOnButton(View v, String name)
    {
        // Cast the view to a button to get which button was pressed
        Button accountButton = (Button) v;

        // get the current balance of the button from the user
        int balanceInCents = GlobalUser.getUser().getAccountBalance(name);
        double balanceInDollars = balanceInCents / 100.0;
        NumberFormat currentFormatter = NumberFormat.getCurrencyInstance(Locale.CANADA);
        String formattedBalance = currentFormatter.format(balanceInDollars);
        accountButton.setText(formattedBalance);
    }
    
    // looks through each of the accounts and adds a corresponding row
    private void populateTableFromSavedData()
    {
        List<Accounts> accountsList = GlobalUser.getUser().getAccountsList();
        for(Accounts acc : accountsList)
        {
            createNewAccount_Row(acc.getAccountName());
        }
        Log.d("populateTable", "Added all accounts to the table");

    }

    private void savePage()
    {
        // whatever logic needs to happen to save any changes made to the current page
    }
}