package com.example.financialmanagementapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// sign in page which uses firebase services
public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge instead of using the import
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // get the textedit values
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);

        // set up buttons
        Button signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = getEmail();
                String password = getPassword();
                if(!email.matches("") && !password.matches(""))
                {
                    signIn(email, password);
                } else {
                    Toast.makeText(MainActivity.this, "Enter an email and password.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = getEmail();
                String password = getPassword();
                if(!email.matches("") && !password.matches(""))
                {
                    createAccount(email, password);
                } else {
                    Toast.makeText(MainActivity.this, "Enter an email and password.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // on app start, check if the user is signed in and redirect as needed
    @Override
    public void onStart()
    {
        super.onStart();

        // check if the user is signed in (non-null) and redirect
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            // open the homepage activity
        }
    }

    // returns the entered email
    private String getEmail()
    {
        return emailInput.getText().toString();
    }

    // returns the entered password
    private String getPassword()
    {
        return passwordInput.getText().toString();
    }

    // sign in an existing user
    private void signIn(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful())
            {
                // sign in success
                FirebaseUser user = mAuth.getCurrentUser();
                // load homepage activity
            } else {
                //sign in failed
                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // create an account for a new user
    private void createAccount(String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()) {
                // Successfully created an account
                FirebaseUser user = mAuth.getCurrentUser();
                // open the main activity
            } else {
                // Account creation failed
                Toast.makeText(MainActivity.this, "Account Creation Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // sign out user (called from pressing the sign out button on the other pages?)
    private void signOut()
    {
        mAuth.signOut();
    }
}