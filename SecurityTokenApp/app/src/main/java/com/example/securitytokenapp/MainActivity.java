package com.example.securitytokenapp;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TimeTickReceiver.OnTimeTickListener {
    // declare the receiver as a member variable
    private BroadcastReceiver timeTickReceiver;
    private TextView passcodeTextview;
    private TextView countdownTextview;
    private int passcode;

    // for the list view
    private List<String> passwordEntries;

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

        // initialize the textview
        passcodeTextview = findViewById(R.id.passcodeTextview);
        countdownTextview = findViewById(R.id.countdownTextview);

        // initialize the passwordEntries list
        passwordEntries = new ArrayList<>();

        // register the receiver to listen for ACTION_TIME_TICK
        timeTickReceiver = new TimeTickReceiver(this);
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(timeTickReceiver, filter);

        // update the passcode
        triggerPasscodeUpdate();

        // set up the button so that it automatically opens the secondary activity
        Button verifyButton = findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(v -> {
           Intent intent = new Intent(MainActivity.this, SecondActivity.class);
           intent.putExtra("passwordEntries", new ArrayList<>(passwordEntries));
           startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timeTickReceiver);
    }

    // Override the interface method to receive the current minute
    @Override
    public void onMinuteUpdated(int currentMinute)
    {
        // generate the passcode based on the current minute
        passcode = (currentMinute * 1245) + 10000;
        String passcodeMsg = String.valueOf(passcode);
        passcodeTextview.setText(passcodeMsg);

        // restart the countdown for the next minute
        Calendar calendar = Calendar.getInstance();
        long millisUntilNextMinute = 60000 - (calendar.get(Calendar.SECOND) * 1000);
        startCountdown(millisUntilNextMinute);

        addPasscodeEntry();
    }

    // manually sets the correct passcode when the app is launched
    private void triggerPasscodeUpdate()
    {
        Calendar calendar = Calendar.getInstance();
        int currentMinute = calendar.get(Calendar.MINUTE);

        passcode = (currentMinute * 1245) + 10000;
        String msg = String.valueOf(passcode);
        passcodeTextview.setText(msg);

        // restart the countdown for the next minute
        long millisUntilNextMinute = 60000 - (calendar.get(Calendar.SECOND) * 1000);
        startCountdown(millisUntilNextMinute);

        addPasscodeEntry();
    }

    private void startCountdown(long millisUntilNextMinute)
    {
        new android.os.CountDownTimer(millisUntilNextMinute, 1)
        {

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

    private void addPasscodeEntry()
    {
        String timestamp = new SimpleDateFormat("yyyy=MM-dd HH:mm", Locale.getDefault()).format(new Date());
        passwordEntries.add(timestamp + "\n\t" + passcode);
    }

}