package com.example.financemanagerapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class WelcomePageActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String displayName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // set up the buttons across the top to save whatever is currently on the screen and to redirect to the appropriate activity
        // instantiate all buttons and disable the button for the current activity. For the rest, set up the change activity
        ImageButton homeButton = findViewById(R.id.homeButton);
        homeButton.setEnabled(false);
        Button accountManagerButton = findViewById(R.id.accountManagerButton);
        accountManagerButton.setOnClickListener(v -> {
            savePage();
            Intent intent = new Intent(WelcomePageActivity.this, AccountManagerActivity.class);
            startActivity(intent);
        });
        Button netWorthButton = findViewById(R.id.netWorthButton);
        netWorthButton.setOnClickListener(v -> {
            savePage();
            Intent intent = new Intent(WelcomePageActivity.this, NetWorthCalculator.class);
            startActivity(intent);
        });
        Button goalTrackerButton = findViewById(R.id.goalTrackerButton);
        goalTrackerButton.setOnClickListener(v -> {
            savePage();
            Intent intent = new Intent(WelcomePageActivity.this, GoalTrackerActivity.class);
            startActivity(intent);
        });
        ImageButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            savePage();
            Intent intent = new Intent(WelcomePageActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // when the app is first opened for a new user, prompt for a nickname
        retrieveNicknameFromFirestore();
        if(displayName.equals(""))
        {
            promptForNickname();
        }

        // load the nickname into the welcome message
    }

    // creates an alertDialog which prompts the user for their desired nickname and saves it to firebase
    private void promptForNickname()
    {
        // create an edittext for user input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Nickname")
                .setMessage("Please enter your nickname")
                .setView(input) // Set the EditText in the dialog
                .setPositiveButton("Submit", null) // Don't close the dialog immediately
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss()); // Optional: cancel button

        // Create the dialog and get reference to it
        AlertDialog dialog = builder.create();

        // Set up validation when the user clicks "Submit"
        dialog.setOnShowListener(d -> {
            Button submitButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            submitButton.setOnClickListener(v -> {
                // Get the entered nickname
                String nickname = input.getText().toString().trim();

                // Perform validation
                if (nickname.isEmpty()) {
                    // If nickname is empty, show a validation message
                    input.setError("Nickname cannot be empty");
                } else if (nickname.length() < 2) {
                    input.setError("Nickname must be at least 2 characters long");
                } else {
                    // If nickname is valid, save it and dismiss the dialog
                    displayName = nickname;
                    saveNicknameToFirestore(nickname);
                    dialog.dismiss();
                }
            });
        });

        // Show the dialog
        dialog.show();
    }

    // saves the users nickname to firestore for future use
    private void saveNicknameToFirestore(String nickname)
    {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null)
        {
            String userID = user.getUid();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("nickname", nickname);

            // Save to Firestore under the 'users' collection
            db.collection("users").document(userID)
                    .set(userMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(WelcomePageActivity.this, "Nickname saved!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(WelcomePageActivity.this, "Error saving nickname", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // retrieves the users nickname from firestore for display
    private void retrieveNicknameFromFirestore()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null)
        {
            String userId = user.getUid();

            // retrieve the user's documents from firestore
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists())
                        {
                            displayName = documentSnapshot.getString("nickname");
                        } else {
                            Toast.makeText(WelcomePageActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(WelcomePageActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private void savePage()
    {
        // whatever logic needs to happen to save any changes made to the current page
    }
}