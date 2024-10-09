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
    private Calculator calculator;

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
        calculator = new Calculator();
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
                calculator.inputDigit("1");
                break;
            case "two":
                calculator.inputDigit("2");
                break;
            case "three":
                calculator.inputDigit("3");
                break;
            case "four":
                calculator.inputDigit("4");
                break;
            case "five":
                calculator.inputDigit("5");
                break;
            case "six":
                calculator.inputDigit("6");
                break;
            case "seven":
                calculator.inputDigit("7");
                break;
            case "eight":
                calculator.inputDigit("8");
                break;
            case "nine":
                calculator.inputDigit("9");
                break;
            case "zero":
                calculator.inputDigit("0");
                break;
            case "delete":
                calculator.delete();
                break;
            case "changeSign":
                calculator.toggleSign();
                break;
            case "percent":
                calculator.pushOperator("%");
                break;
            case "divide":
                calculator.pushOperator("/");
                break;
            case "multiply":
                calculator.pushOperator("*");
                break;
            case "minus":
                calculator.pushOperator("-");
                break;
            case "plus":
                calculator.pushOperator("+");
                break;
            case "equal":
                calculator.evaluateExpression();
                break;
            case "decimalPoint":
                calculator.inputDigit(".");
                break;
            case "clear":
                calculator.clear();
                break;
            default:
                break;
        }
        updateOutput();
    }

    // updates the textview to show the current operation or result on pressing equal
    private void updateOutput() {
        TextView output  = findViewById(R.id.outputTextview);

        String outMsg = calculator.getFullExpression();

        output.setText(outMsg);
    }
}