package com.example.securitytokenapp;

import java.util.ArrayList;
import java.util.List;

public class PasswordManager {
    private static PasswordManager instance;
    private List<String> passwordEntries;
    private boolean hasLoadedSavedEntry = false;

    private PasswordManager() {
        passwordEntries = new ArrayList<>();
    }

    public static synchronized PasswordManager getInstance() {
        if (instance == null) {
            instance = new PasswordManager();
        }
        return instance;
    }

    public List<String> getEntries() {
        return passwordEntries;
    }

    public void addEntry(String entry) {
        passwordEntries.add(entry);
    }

    public void setLoadFlag()
    {
        hasLoadedSavedEntry = !hasLoadedSavedEntry;
    }

    public boolean getLoadFLag()
    {
        return hasLoadedSavedEntry;
    }

    public void clearEntries() {
        passwordEntries.clear();
    }
}
