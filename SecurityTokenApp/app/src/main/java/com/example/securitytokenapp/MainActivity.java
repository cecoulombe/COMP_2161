package com.example.securitytokenapp;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TimeTickReceiver.OnTimeTickListener {
    private BroadcastReceiver timeTickReceiver;
    private boolean isReceiverRegistered = false;
    private TextView passcodeTextview;
    private TextView countdownTextview;
    private static int passcode;
    private static String timestamp;
    private boolean isSwitchingActivities = false;
    private boolean hasSentPasscode = false;

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

        // Register the receiver to listen for ACTION_TIME_TICK
        timeTickReceiver = new TimeTickReceiver(this);
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(timeTickReceiver, filter);
        isReceiverRegistered = true;

        // Update the passcode initially
        updatePasscode();

        // Set up the button to automatically open the secondary activity
        Button verifyButton = findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(v -> {
            hasSentPasscode = true;
            isSwitchingActivities = true;
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra("passcode", passcode);
            intent.putExtra("timestamp", timestamp);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isReceiverRegistered) {
            unregisterReceiver(timeTickReceiver); // Unregister here
            isReceiverRegistered = false; // Reset the flag
        }
    }

    private void updatePasscode() {
        // Update passcode based on the current minute
        Calendar calendar = Calendar.getInstance();
        int currentMinute = calendar.get(Calendar.MINUTE);
        passcode = (currentMinute * 1245) + 10000;
        timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        // Update the UI with the new passcode
        passcodeTextview.setText(String.valueOf(passcode));

        // Restart the countdown for the next minute
        long millisUntilNextMinute = 60000 - (calendar.get(Calendar.SECOND) * 1000);
        startCountdown(millisUntilNextMinute);
    }

    @Override
    public void onMinuteUpdated(int currentMinute) {
        updatePasscode(); // Call updatePasscode directly to update
    }

    @Override
    public void checkBeforeUpdate()
    {
        Log.d("checkBeforeUpdate", "checking if the passcode has been sent");
        if(!hasSentPasscode)
        {
            String entry = timestamp + "\n\t" + passcode;
            Log.d("checkBeforeUpdate", "the passcode has NOT been sent, saving the following first: " + entry);
            saveEntry(entry);
        }
        else
        {
            Log.d("checkBeforeUpdate", "the passcode has been sent");
        }
    }

    private void startCountdown(long millisUntilNextMinute) {
        new android.os.CountDownTimer(millisUntilNextMinute, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) (millisUntilFinished / 1000);
                countdownTextview.setText(getString(R.string.CountdownMsg, secondsLeft));
            }

            @Override
            public void onFinish() {
                countdownTextview.setText(getString(R.string.DefaultCountdown));
                // After finishing, update the passcode again
                Calendar calendar = Calendar.getInstance();
                onMinuteUpdated(calendar.get(Calendar.MINUTE));
            }
        }.start();
    }


    private void saveEntry(String entry)
    {
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("entry", entry);
        editor.apply();
    }

    private String getEntry()
    {
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPref.getString("entry", "null");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        Log.d("configChange_act1", "Changed the orientation of the device");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isSwitchingActivities)
        {
            // changing activities, save the data
            Log.d("saving_act1", "App is NOT exiting, just changing activities. Save the data");
        }
        else
        {
            // app is exiting, clear the data but store entry
            Log.d("saving_act1", "App is exiting, need to clear and save the final piece of data");
            saveEntry(timestamp + "\n\t" + passcode);
        }
    }
}
