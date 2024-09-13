package com.example.unitconverterapp;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import java.util.Locale;

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

        // initialize all objects
        Spinner spinFrom = findViewById(R.id.spinner1);
        Spinner spinTo = findViewById(R.id.spinner2);

        Button button = findViewById(R.id.convertButton);

        EditText inputText = findViewById(R.id.enterValue);

        TextView results = findViewById(R.id.results);

        int duration = Toast.LENGTH_SHORT;

        Toast noConversion = Toast.makeText(this, getResources().getString(R.string.toast_noCon), duration);
        Toast noValue = Toast.makeText(this, getResources().getString(R.string.toast_noVal), duration);

        //-----------------------------------------------------------------------------------------------
        // listens for the button click and responds accordingly when one occurs
        //-----------------------------------------------------------------------------------------------
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // get values of spinners
                String fromUnit = spinFrom.getSelectedItem().toString();
                String toUnit = spinTo.getSelectedItem().toString();

                // get value of editText
                String input = inputText.getText().toString();

                // variables for the conversion
                double inputTemp;

                if (fromUnit.equals(toUnit)) {
                    // nothing happens
                    results.setText("");
                    noConversion.show();
                } else {
                    try {
                        inputTemp = Double.parseDouble(input);
                        if (fromUnit.equals("Fahrenheit")) {
                            double celsius = (inputTemp - 32) * (5.0 / 9.0);
                            results.setText(String.format(Locale.CANADA, "%.2f", celsius));
                        } else {
                            double fahrenheit = (inputTemp * 9.0 / 5.0) + 32;
                            results.setText(String.format(Locale.CANADA, "%.2f", fahrenheit));
                        }
                    } catch (NumberFormatException e)
                    {
                        results.setText(getResources().getString(R.string.noInput));
                        noValue.show();
                    }
                }
            }
        });
    }
}