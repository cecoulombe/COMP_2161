package com.example.tictactoegame;

import android.content.SharedPreferences;
import android.os.Bundle;

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

        countdownPreference = findPreference("turn_countdown_seconds");
        enableCountdownSwitch = findPreference("enable_countdown");

        // Initialize countdown preference state based on the switch
        if (enableCountdownSwitch != null && countdownPreference != null) {
            countdownPreference.setEnabled(enableCountdownSwitch.isChecked());
        }

        // Manage dark mode toggle
        SwitchPreferenceCompat darkModeSwitch = findPreference("dark_mode");
        if (darkModeSwitch != null) {
            darkModeSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference pref, Object newValue) {
                    boolean isDarkMode = (boolean) newValue;

                    // Apply the theme change
                    applyTheme(isDarkMode);
                    return true;
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ("enable_countdown".equals(key)) {
            boolean isEnabled = sharedPreferences.getBoolean(key, false);
            if (countdownPreference != null) {
                countdownPreference.setEnabled(isEnabled);
            }
        }
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
}
