package com.example.securitytokenapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;


public class TimeTickReceiver extends BroadcastReceiver {

    // define an interface for the listener
    public interface OnTimeTickListener {
        void onMinuteUpdated(int currentMinute);
    }

    private OnTimeTickListener listener;

    // constructor to initialize the listener
    public TimeTickReceiver(OnTimeTickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // check that we got the right kind of broadcast
        if(Intent.ACTION_TIME_TICK.equals(intent.getAction()))
        {
            // get the current minute
            Calendar calendar = Calendar.getInstance();
            int currentMinute = calendar.get(Calendar.MINUTE);

            // send the current minute to the listener
            if(listener != null)
            {
                listener.onMinuteUpdated(currentMinute);
            }
        }
    }
}
