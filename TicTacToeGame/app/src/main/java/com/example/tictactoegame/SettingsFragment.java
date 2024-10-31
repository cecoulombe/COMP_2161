package com.example.tictactoegame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private EditTextPreference countdownPreference;
    private SwitchPreferenceCompat enableCountdownSwitch;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Manage dark mode toggle
        SwitchPreferenceCompat darkModeSwitch = findPreference("dark_mode");
        if (darkModeSwitch != null) {
            darkModeSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference pref, Object newValue) {
                    boolean isDarkMode = (boolean) newValue;
                    applyTheme(isDarkMode);
                    return true;
                }
            });
        }

        // Manage show_stats toggle
        SwitchPreferenceCompat showStatsSwitch = findPreference("show_stats");
        if (showStatsSwitch != null) {
            showStatsSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference pref, Object newValue) {
                    boolean showStats = (boolean) newValue;
                    Log.d("SettingsFragment", "show_stats changed to: " + showStats);
                    return true; // Update the preference state
                }
            });
        }

        // Manage AI delay toggle
        SwitchPreferenceCompat aiDelaySwitch = findPreference("ai_delay");
        if (aiDelaySwitch != null) {
            aiDelaySwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference pref, Object newValue) {
                    boolean aiDelayEnabled = (boolean) newValue;
                    Log.d("SettingsFragment", "AI Delay enabled: " + aiDelayEnabled);
                    return true; // Update the preference state
                }
            });
        }
    }


    // changes the theme based on the switch in the settings menu
    private void applyTheme(boolean isDarkMode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("dark_mode", isDarkMode);
        editor.apply();

        // Set the theme
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Restart the activity to apply the theme change
        getActivity().recreate();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String s) {
        if ("show_stats".equals(s)) {
            boolean showStatsEnabled = sharedPreferences.getBoolean(s, true); // Default is true
            Log.d("SettingsFragment", "Show Stats changed to: " + showStatsEnabled);
        }
    }
}
