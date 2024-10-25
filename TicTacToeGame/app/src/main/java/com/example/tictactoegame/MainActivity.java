package com.example.tictactoegame;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tictactoegame.ButtonAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

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

        ListView listView = findViewById(R.id.listView);

        // Create an ArrayList and add strings from strings.xml
        ArrayList<String> buttonLabels = new ArrayList<>();
        buttonLabels.add(getString(R.string.EnterNameButton));
        buttonLabels.add(getString(R.string.PlayGameButton));
        buttonLabels.add(getString(R.string.StandingsButton));

        // Corresponding activities
        ArrayList<Class<?>> activities = new ArrayList<>();
        activities.add(EnterNamesActivity.class);
        activities.add(PlayGameActivity.class);
        activities.add(ShowStandingsActivity.class);


        // Create and set the adapter
        ButtonAdapter adapter = new ButtonAdapter(this, buttonLabels, activities);
        listView.setAdapter(adapter);
    }
}