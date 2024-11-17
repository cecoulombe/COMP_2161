package com.example.financemanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
                launchWelcomePage();
            } else {
                // Sign in failed, get specific error
                String errorMessage = "Authentication failed.";

                if (task.getException() instanceof FirebaseAuthException) {
                    FirebaseAuthException e = (FirebaseAuthException) task.getException();
                    switch (e.getErrorCode()) {
                        case "ERROR_INVALID_EMAIL":
                            errorMessage = "The email address is badly formatted.";
                            break;
                        case "ERROR_WRONG_PASSWORD":
                            errorMessage = "The password is incorrect.";
                            break;
                        case "ERROR_USER_NOT_FOUND":
                            errorMessage = "No account found with this email.";
                            break;
                        case "ERROR_USER_DISABLED":
                            errorMessage = "This account has been disabled.";
                            break;
                        case "ERROR_TOO_MANY_REQUESTS":
                            errorMessage = "Too many login attempts. Please try again later.";
                            break;
                        case "ERROR_OPERATION_NOT_ALLOWED":
                            errorMessage = "Login with email and password is disabled. Please contact support.";
                            break;
                        // Add more cases as needed
                        default:
                            errorMessage = e.getLocalizedMessage();
                            break;
                    }
                } else if (task.getException() != null) {
                    errorMessage = task.getException().getLocalizedMessage();
                }

                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
                launchWelcomePage();
            } else {
                // Account creation failed, get specific error
                String errorMessage = "Account Creation Failed";

                if (task.getException() instanceof FirebaseAuthException) {
                    FirebaseAuthException e = (FirebaseAuthException) task.getException();
                    switch (e.getErrorCode()) {
                        case "ERROR_INVALID_EMAIL":
                            errorMessage = "The email address is badly formatted.";
                            break;
                        case "ERROR_EMAIL_ALREADY_IN_USE":
                            errorMessage = "This email address is already in use.";
                            break;
                        case "ERROR_WEAK_PASSWORD":
                            errorMessage = "The password is too weak. Please use a stronger password.";
                            break;
                        case "ERROR_OPERATION_NOT_ALLOWED":
                            errorMessage = "Account creation is disabled. Please contact support.";
                            break;
                        // Add more cases as needed
                        default:
                            errorMessage = e.getLocalizedMessage();
                            break;
                    }
                } else if (task.getException() != null) {
                    errorMessage = task.getException().getLocalizedMessage();
                }

                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    // sign out user (called from pressing the sign out button on the other pages?)
    private void signOut()
    {
        mAuth.signOut();
    }
    
    // launch the next activity on sign in
    private void launchWelcomePage()
    {
        Intent intent = new Intent(MainActivity.this, WelcomePageActivity.class);
        startActivity(intent);
    }
}