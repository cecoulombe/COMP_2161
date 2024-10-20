package com.example.securitytokenapp;

public class PasscodeEntry {
    private String timestamp;
    private String password;

    public PasscodeEntry(String timestamp, String password) {
        this.timestamp = timestamp;
        this.password = password;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getPassword() {
        return password;
    }
}
