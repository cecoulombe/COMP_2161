package com.example.unitconverterapp;

import android.os.Bundle;
import android.widget.AdapterView;

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
            public void onClick(View v)
            {
                // get values of spinners
                String fromUnit = spinFrom.getSelectedItem().toString();
                String toUnit = spinTo.getSelectedItem().toString();

                // get value of editText
                String input = inputText.getText().toString();

                // variables for the conversion
                double inputTemp = Double.parseDouble(input);

                if(fromUnit.equals(toUnit))
                {
                    // nothing happens
                    results.setText("");
                    noConversion.show();
                }
                else if(input.isEmpty())
                {
                    results.setText(getResources().getString(R.string.noInput));
                    noValue.show();
                }
                else if(fromUnit.equals("Fahrenheit"))
                {
                    double celsius = (inputTemp - 32) * (float)(5/9);
                    results.setText(String.format(Locale.CANADA, "%.2f", celsius));
                }
                else
                {
                    double fahr = (inputTemp * 9 / 5) + 32;
                    results.setText(String.format(Locale.CANADA, "%.2f", fahr));
                }
            }
        });


        // pseudocode

        /*
        if spinner 1 is fahrenheit, true (fahrenheit is true, celsius is false)
        if spinner 2 is fahrenheit, true (fahrenheit is true, celsius is false)

        on a button press:
            check if spinner1 = spinner2
                if yes, then nothing to convert
                    update the result to equal the entered value (not converted)
                    push the corresponding toast message
            if the value box is empty
                set the result to null
                push the corresponding toast message
            if spinner 1 is fahrenheit
                convert from fahrenheit to celsius
            convert from celsius to fahrenheit

         */
    }


}