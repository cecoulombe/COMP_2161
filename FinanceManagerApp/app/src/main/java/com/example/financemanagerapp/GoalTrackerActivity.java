package com.example.financemanagerapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class GoalTrackerActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_goal_tracker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        loadUser(); // should happen in the welcome activity, failsafe
        if(GlobalUser.getUser().getGoalName().equals(""))
        {
            createGoal_Popup();
        }
        setUpChart();

        // set up the buttons across the top to save whatever is currently on the screen and to redirect to the appropriate activity
        // instantiate all buttons and disable the button for the current activity. For the rest, set up the change activity
        ImageButton homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> {
            savePage();
            Intent intent = new Intent(GoalTrackerActivity.this, WelcomePageActivity.class);
            startActivity(intent);
        });
        Button accountManagerButton = findViewById(R.id.accountManagerButton);
        accountManagerButton.setOnClickListener(v -> {
            savePage();
            Intent intent = new Intent(GoalTrackerActivity.this, AccountManagerActivity.class);
            startActivity(intent);
        });
        Button netWorthButton = findViewById(R.id.netWorthButton);
        netWorthButton.setOnClickListener(v -> {
            savePage();
            Intent intent = new Intent(GoalTrackerActivity.this, NetWorthCalculator.class);
            startActivity(intent);
        });

        Button goalTrackerButton = findViewById(R.id.goalTrackerButton);
        goalTrackerButton.setEnabled(false);

        ImageButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            savePage();
            Intent intent = new Intent(GoalTrackerActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        Button signOutButton = findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(v -> {
            savePage();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(GoalTrackerActivity.this, MainActivity.class);
            startActivity(intent);
        });

        ImageButton helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(v -> {
            helpPopup();
        });
    }

    // populates the pie chart to display current goal progress
    private void setUpChart()
    {
        PieChart pieChart = findViewById(R.id.pieChart);

        // calculate the percent of goal completed
        float currentAmount = (int) (GlobalUser.getUser().getCurrentAmount() / 100f);
        float goalAmount = (int) (GlobalUser.getUser().getGoalAmount() / 100f);
        float currentPercent = (currentAmount / goalAmount) * 100f;
        float remainingPercent = 100f - currentPercent;

        // populate the data
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(currentPercent, "Saved"));
        entries.add(new PieEntry(remainingPercent, "Remaining"));

        PieDataSet dataSet = new PieDataSet(entries, "Goal Progress");
        dataSet.setColors(new int[]{getResources().getColor(R.color.tableLayoutBackground), getResources().getColor(R.color.headerBackground)});
        dataSet.setValueTextColor(getResources().getColor(R.color.textColor));
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        // make it a donut chart
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setHoleColor(getResources().getColor(R.color.backgroundColor));

        // Customize chart appearance
        pieChart.setDrawEntryLabels(false); // Hide labels outside the pie
        pieChart.getDescription().setEnabled(false); // Remove description label
        pieChart.setCenterText(String.format("%.0f%%", currentPercent)); // Add center text
        pieChart.setCenterTextSize(16f);
        pieChart.setCenterTextColor(getResources().getColor(R.color.textColor));

        // refresh the chart
        pieChart.invalidate();
    }

    // creates a popup which prompts the user to create a new financial goal
    private void createGoal_Popup()
    {
        // Inflate the custom layout for the dialog
        LinearLayout dialogLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_create_goal, null);
        EditText nameEditText = dialogLayout.findViewById(R.id.enterNameEditText);
        EditText balanceEditText = dialogLayout.findViewById(R.id.enterAmountEditText);
        LinearLayout accountsList = dialogLayout.findViewById(R.id.accountsList);
        addCheckboxesToPopup(accountsList);


        // create and show the AlertDialog
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.name_alertBuilderText))
                .setView(dialogLayout)
                .setPositiveButton("Create Goal", (dialog, which) ->
                {
                    // get user input and update the button text
                    String name = nameEditText.getText().toString().trim();
                    String balance = balanceEditText.getText().toString();
                    Log.d("CreateNewAccountPOPUP", "Creating a new account with input name: " + name + " and input balance: " + balance);
                    int balanceInCents;

                    if(!name.isEmpty() && !balance.isEmpty())
                    {
                        double balanceInDollars = Double.parseDouble(balance);
                        balanceInCents = (int) (balanceInDollars * 100);
                        GlobalUser.getUser().setGoalName(name);
                        GlobalUser.getUser().setGoalAmount(balanceInCents);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Goal must have a name and target amount!", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();


    }

    // populates the list of accounts which can be used as a liability
    private void addCheckboxesToPopup(LinearLayout container)
    {
        List<Accounts> accounts = GlobalUser.getUser().getAccountsList();
        // get the list of x from the user, add a checkbox for each

        for(Accounts acc : accounts)
        {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(acc.getAccountName());
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            // if it is a liability, set it to be true
            if(GlobalUser.getUser().getGoalFlag(acc.getAccountName()))
            {
                checkBox.setChecked(true);
            }

            // set a listener for when the box is toggled
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
            {
                String checkBoxText = checkBox.getText().toString();

                GlobalUser.getUser().setGoalFlag(checkBoxText, isChecked);
                savePage();
            });

            container.addView(checkBox);
        }
    }

    // creates a popup with info on how to navigate the current activity
    private void helpPopup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.helpPopupTitle));
        builder.setMessage(getResources().getString(R.string.helpPopupInfo_GoalTracker));

        // set a neutral button to dismiss the message
        builder.setNeutralButton("OK", (dialog, which) -> dialog.dismiss());

        // show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // saves the user data to firebase asynchronously (.set method is async by nature)
    private void savePage()
    {
        // save all user data to the firebase
        FirebaseUser auth = mAuth.getCurrentUser();
        if(auth != null) {
            String userID = auth.getUid();

            db.collection("users").document(userID)
                    .set(GlobalUser.getUser())
                    .addOnSuccessListener(aVoid ->
                    {
                        Log.d("SavePage", "User data saved successfully.");
                    })
                    .addOnFailureListener(e ->
                    {
                        Log.d("SavePage", "Error saving user data: " + e.getMessage());
                    });
        } else {
            Log.e("SavePage", "User is not authenticated");
        }
    }

    // loads the user at the start of the activity to make sure all data is there (failsafe from when the data should have been loaded in the welcome activity)
    // loads the user data for an existing user in the firebase
    private void loadUser()
    {
        FirebaseUser auth = mAuth.getCurrentUser();
        if(auth != null) {
            String userID = auth.getUid();
            DocumentReference docRef = db.collection("users").document(userID);

            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    GlobalUser.setUser(user);
                    Log.d("loadUser", "Loading the user data from the firebase");
                }
            });
        }
    }
}