package com.example.securitytokenapp;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TimeTickReceiver.OnTimeTickListener {
    private BroadcastReceiver timeTickReceiver;
    private TextView passcodeTextview;
    private TextView countdownTextview;
    private int passcode;
    private String timestamp;
    private static boolean hasLoadedSave = false;

    // For the list view
    private List<String> passwordEntries;
    static final String PREFS_NAME = "passcodePrefs";
    static final String KEY_ENTRIES = "passwordEntries";

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

        // Initialize the TextView
        passcodeTextview = findViewById(R.id.passcodeTextview);
        countdownTextview = findViewById(R.id.countdownTextview);

        // Initialize the passwordEntries list
        passwordEntries = new ArrayList<>();

        // Load saved values from SharedPreferences
        if (!hasLoadedSave) {
            loadSavedValues();  // Load entries at the start
            hasLoadedSave = true;  // Prevent loading multiple times
        }

        // Register the receiver to listen for ACTION_TIME_TICK
        timeTickReceiver = new TimeTickReceiver(this);
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(timeTickReceiver, filter);

        // Update the passcode
        triggerPasscodeUpdate();

        // Set up the button to automatically open the secondary activity
        Button verifyButton = findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra("passwordEntries", passwordEntries.toArray(new String[0]));
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
    }

    private void submitSavedCode() {
        // Check if the entry is already present to avoid duplicates
        String entry = timestamp + "\n\t" + passcode;
        if (!passwordEntries.contains(entry)) {
            passwordEntries.add(entry);
            savePasswordEntries();  // Ensure to save whenever a new entry is added
        }
    }

    private void clearPasswordEntries() {
        passwordEntries.clear(); // Clear the list
        // Save the cleared state
        savePasswordEntries();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timeTickReceiver);
        savePasscodeData();  // Save when the app is closed or destroyed
    }

    // Override the interface method to receive the current minute
    @Override
    public void onMinuteUpdated(int currentMinute) {
        // Generate the passcode based on the current minute
        passcode = (currentMinute * 1245) + 10000;
        String passcodeMsg = String.valueOf(passcode);
        passcodeTextview.setText(passcodeMsg);

        // Restart the countdown for the next minute
        Calendar calendar = Calendar.getInstance();
        long millisUntilNextMinute = 60000 - (calendar.get(Calendar.SECOND) * 1000);
        startCountdown(millisUntilNextMinute);

        addPasscodeEntry();
    }

    private void triggerPasscodeUpdate() {
        Calendar calendar = Calendar.getInstance();
        int currentMinute = calendar.get(Calendar.MINUTE);

        passcode = (currentMinute * 1245) + 10000;
        String msg = String.valueOf(passcode);
        passcodeTextview.setText(msg);

        // Restart the countdown for the next minute
        long millisUntilNextMinute = 60000 - (calendar.get(Calendar.SECOND) * 1000);
        startCountdown(millisUntilNextMinute);

        addPasscodeEntry();
    }

    private void startCountdown(long millisUntilNextMinute) {
        new android.os.CountDownTimer(millisUntilNextMinute, 1) {
            @Override
            public void onTick(long millisUntilNextMinute) {
                int secondsLeft = (int) (millisUntilNextMinute / 1000);
                countdownTextview.setText(getString(R.string.CountdownMsg, secondsLeft));
            }

            @Override
            public void onFinish() {
                countdownTextview.setText(getString(R.string.DefaultCountdown));
            }
        }.start();
    }

    private void addPasscodeEntry() {
        timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        passwordEntries.add(timestamp + "\n\t" + passcode);
        savePasswordEntries();  // Save every time an entry is added
    }

    private void savePasswordEntries() {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String json = new Gson().toJson(passwordEntries);
        editor.putString(KEY_ENTRIES, json);
        editor.apply();
        Log.d("Saving", "Saved entries: " + passwordEntries);
    }

    private void loadSavedValues() {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        passcode = sharedPref.getInt("passcode", -1);
        timestamp = sharedPref.getString("timestamp", null);

        // Load the saved passwordEntries list from SharedPreferences
        Gson gson = new Gson();
        String json = sharedPref.getString(KEY_ENTRIES, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();

        if (json != null) {
            passwordEntries = gson.fromJson(json, type);
        }

        if (timestamp != null && passcode != -1) {
            Log.d("MainActivity", "Loaded the saved data from SharedPreferences");
            submitSavedCode();  // Load the saved entry if it's not already added
        }
    }

    // Save entries list when onPause or onDestroy
    @Override
    protected void onPause() {
        super.onPause();
        savePasscodeData();  // Saving the passcode and timestamp
        savePasswordEntries();  // Also save the list of entries
    }

    private void savePasscodeData() {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("passcode", passcode);
        editor.putString("timestamp", timestamp);
        editor.apply();
    }
}
