package com.example.calculatorapp;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Collections;
import java.util.Stack;

import android.view.View;
import android.widget.AdapterView;

public class MainActivity extends AppCompatActivity implements NumPad_Fragment.OnFragmentInteractionListener, ScientificFragment.OnFragmentInteractionListener {
    private Calculator calculator;
    private Spinner mySpinner;

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
            Log.d("MainActivity", "Landscape layout detected. Loading both fragments.");

            // Landscape layout detected, add both fragments
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.numPadFragCont, new NumPad_Fragment())
                    .replace(R.id.scientificFragCont, new ScientificFragment())
                    .commit();
        } else {
            Log.d("MainActivity", "Portrait layout detected. Loading NumPad fragment only.");

            // Portrait layout, only add the main fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.numPadFragCont, new NumPad_Fragment())
                    .commit();
        }
        calculator = new Calculator(this);

        if (savedInstanceState != null) {
            Log.d("Saving", "Loaded the position saved data.");

            // Restore stacks
            calculator.numberStack = new Stack<>();
            calculator.operatorStack = new Stack<>();
            Collections.addAll(calculator.numberStack, savedInstanceState.getStringArray("numberStack"));
            Collections.addAll(calculator.operatorStack, savedInstanceState.getStringArray("operatorStack"));

            // Restore StringBuilders
            calculator.currentInput = new StringBuilder(savedInstanceState.getString("currentInput"));
            calculator.orderOfInputs = new StringBuilder(savedInstanceState.getString("orderOfInputs"));

            // Restore other variables
//            calculator.memVar = savedInstanceState.getString("memVar");
            calculator.isNegative = savedInstanceState.getBoolean("isNegative");
            calculator.lastResult = savedInstanceState.getString("lastResult");
            calculator.hasResult = savedInstanceState.getBoolean("hasResult");

            updateOutput();
        }

        // Check if the dialog has been shown before
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean hasSeenDialog = prefs.getBoolean("hasSeenDialog", false);

        // Show the dialog if it's the user's first time
        if (!hasSeenDialog) {
            FeatureOutlineDialog dialog = new FeatureOutlineDialog();
            dialog.show(getSupportFragmentManager(), "FeatureOutlineDialog");

            // Update the preference so the dialog isn't shown again
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("hasSeenDialog", true);
            editor.apply();
        }

        // set up drop down spinner
        // Get a reference to the Spinner
        mySpinner = findViewById(R.id.mySpinner);

        // Create the ArrayAdapter using a resource array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.spinner_items,
                R.layout.spinner_item // Use your custom layout here
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(adapter);

        // Set the spinner selection initially based on orientation
        setSpinnerSelectionBasedOnOrientation();

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                String selectedItem = parent.getItemAtPosition(position).toString();
                calculator.setSpinnerChoice(selectedItem); // Send the value to the Calculator
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where nothing is selected
                String defaultItem = ""; // Variable to hold the default item

                // Determine the default item based on orientation
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    defaultItem = parent.getItemAtPosition(1).toString(); // Second item for landscape
                } else {
                    defaultItem = parent.getItemAtPosition(0).toString(); // First item for portrait
                }

                calculator.setSpinnerChoice(defaultItem); // Send the default item to the Calculator
            }
        });

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
                calculator.pushFunction("-");
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
                int orientation = getResources().getConfiguration().orientation;
                calculator.evaluateExpression();
                break;
            case "decimalPoint":
                calculator.inputDigit(".");
                break;
            case "clear":
                calculator.clear();
                break;
            case "memSave":
                calculator.memSave();
                break;
            case "memClear":
                calculator.memClear();
                break;
            case "memRecall":
                calculator.memRecall();
                break;
            case "memSub":
                calculator.memSub();
                break;
            case "memAdd":
                calculator.memAdd();
                break;
            case "leftParen":
                calculator.pushOperator("(");
                break;
            case "rightParen":
                calculator.pushOperator(")");
                break;
            case "squared":
                calculator.pushFunction("square");
                break;
            case "cubed":
                calculator.pushFunction("cube");
                break;
            case "eRaisedX":
                calculator.pushFunction("e ^");
                break;
            case "raisedToY":
                calculator.pushOperator("^");
                break;
            case "tenRaisedX":
                calculator.pushFunction("10 ^");
                break;
            case "enterExponent":
                calculator.pushOperator("E");
                break;
            case "sin":
                calculator.pushFunction("sin");
                break;
            case "cos":
                calculator.pushFunction("cos");
                break;
            case "tan":
                calculator.pushFunction("tan");
                break;
            case "sinh":
                calculator.pushFunction("sinh");
                break;
            case "cosh":
                calculator.pushFunction("cosh");
                break;
            case "tanh":
                calculator.pushFunction("tanh");
                break;
            case "squareRoot":
                calculator.pushFunction("sqrt");
                break;
            case "cubeRoot":
                calculator.pushFunction("cbrt");
                break;
            case "yRoot":
                calculator.pushOperator("root");
                break;
            case "logBase10":
                calculator.pushFunction("log");
                break;
            case "ln":
                calculator.pushFunction("ln");
                break;
            case "e":
                calculator.pushE();
                break;
            case "pi":
                calculator.pushPi();
                break;
            case "rand":
                calculator.pushRand();
                break;
            case "inverse":
                calculator.inverse();
                break;
            case "factorial":
                calculator.pushOperator("!");
                break;
            default:
                break;
        }
        updateOutput();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recheck the orientation and set the spinner selection accordingly
        setSpinnerSelectionBasedOnOrientation();
    }

    // update spinner based on orientation
    private void setSpinnerSelectionBasedOnOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mySpinner.setSelection(1); // Set to the second item for landscape
        } else {
            mySpinner.setSelection(0); // Set to the first item for portrait
        }
    }

    // updates the textview to show the current operation or result on pressing equal
    private void updateOutput() {
        TextView output  = findViewById(R.id.outputTextview);
        HorizontalScrollView horizontalScrollView = findViewById(R.id.horizontalScrollView);

        String outMsg = calculator.getFullExpression();

        output.setText(outMsg);
        // Force the HorizontalScrollView to scroll to the end
        horizontalScrollView.post(new Runnable() {
            @Override
            public void run() {
                horizontalScrollView.scrollTo(horizontalScrollView.getWidth(), 0);  // Scroll to the end
            }
        });
    }

    // save relevant data for when the screen rotates
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d("Saving", "Saving the data.");

        // Convert stacks to arrays for saving
        outState.putStringArray("numberStack", calculator.getNumberStack().toArray(new String[0]));
        outState.putStringArray("operatorStack", calculator.getOperatorStack().toArray(new String[0]));

        // Convert StringBuilders to Strings
        outState.putString("currentInput", calculator.getCurrentInput().toString());
        outState.putString("orderOfInputs", calculator.orderOfInputs.toString());

        // Save other variables
//        outState.putString("memVar", calculator.memVar);
        outState.putBoolean("isNegative", calculator.isNegative);
        outState.putString("lastResult", calculator.lastResult);
        outState.putBoolean("hasResult", calculator.hasResult);
    }
}