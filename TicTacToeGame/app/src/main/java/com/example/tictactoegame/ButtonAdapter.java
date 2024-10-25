package com.example.tictactoegame;

// MyButtonAdapter.java
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.List;

public class ButtonAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> buttonLabels;
    private final List<Class<?>> activities;

    public ButtonAdapter(Context context, List<String> buttonLabels, List<Class<?>> activities) {
        super(context, R.layout.list_item_button, buttonLabels);
        this.context = context;
        this.buttonLabels = buttonLabels;
        this.activities = activities;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_button, parent, false);

        Button button = rowView.findViewById(R.id.button);
        button.setText(buttonLabels.get(position));

        // Set an OnClickListener for the button to open the corresponding activity
        button.setOnClickListener(v -> {
            Intent intent = new Intent(context, activities.get(position));
            context.startActivity(intent);
        });

        return rowView;
    }
}
