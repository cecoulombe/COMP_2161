package com.example.calculatorapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements NumPad_Fragment.OnFragmentInteractionListener, ScientificFragment.OnFragmentInteractionListener {

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

        // Load the fragments
        if (findViewById(R.id.scientificFragCont) != null) {
            // Landscape layout detected, add both fragments
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.numPadFragCont, new NumPad_Fragment())
                    .replace(R.id.scientificFragCont, new ScientificFragment())
                    .commit();
        } else {
            // Portrait layout, only add the main fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.numPadFragCont, new NumPad_Fragment())
                    .commit();
        }
    }

    // global variables for calculator
    String operand1 = "";   // stores the first number
    boolean op1hasDecimal = false; // sets flag if there is already a decimal point so that it doesn't allow another
    String operand2 = "";   // stores the second number
    boolean op2hasDecimal = false;  // sets flag if there is already a decimal point so that it doesn't allow another
    String operator = "";   // stores the operator symbol
    String computedValue = "";

    public void onFragmentInteraction(String buttonId)
    {
        Log.d("Tag", "Fragment button was interacted with: " + buttonId);

        // determine the action of the button
        switch (buttonId){
            case "one":
                if(operator == "")
                {
                    operand1 = operand1 + "1";
                }
                else {
                    operand2 = operand2 + "1";
                }
                break;
            case "two":
                if(operator == "")
                {
                    operand1 = operand1 + "2";
                }
                else {
                    operand2 = operand2 + "2";
                }
                break;
            case "three":
                break;
            case "four":
                break;
            case "five":
                break;
            case "six":
                break;
            case "seven":
                break;
            case "eight":
                break;
            case "nine":
                break;
            case "zero":
                break;
            case "delete":
                break;
            case "changeSign":
                break;
            case "percent":
                break;
            case "divide":
                break;
            case "multiply":
                break;
            case "minus":
                break;
            case "plus":
                break;
            case "equal":
                break;
            case "decimalPoint":
                break;
            case "clear":
                break;
            default:
                break;
        }
        updateOutput();
    }

    // updates the textview to show the current operation or result on pressing equal
    private void updateOutput() {
        TextView output  = findViewById(R.id.outputTextview);

        String outMsg = operand1 + operator + operand2;

        output.setText(outMsg);
    }
}