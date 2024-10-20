package com.example.securitytokenapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PasscodeAdapter extends ArrayAdapter<String> {
    private final List<String> entries;

    public PasscodeAdapter(Context context, List<String> entries) {
        super(context, 0, entries);
        this.entries = entries;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String entry = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_passcode, parent, false);
        }

        // Lookup view for data population
        TextView timestampTextView = convertView.findViewById(R.id.timestampTextView);
        TextView passcodeTextView = convertView.findViewById(R.id.passcodeTextview_Listview);

        // Split the entry into timestamp and passcode
        String[] parts = entry.split("\n\t"); // Assuming your format is "timestamp\n\tpasscode"
        if (parts.length == 2) {
            timestampTextView.setText(parts[0]);
            passcodeTextView.setText(parts[1]);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}

