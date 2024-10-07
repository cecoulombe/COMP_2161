package com.example.calculatorapp;

import android.util.Log;

import java.util.Stack;

public class Calculator {
    // global variables
    private Stack<String> numberStack;  // stores the user's current input
    private Stack<String> operatorStack;    // stores the operators used
    private StringBuilder currentInput; // stores previous input from the user after an operator is selected
    private boolean isNegative;
    private double lastResult = 0.0;
    private boolean hasResult = false;
    private boolean lastInputWasNum = false;

    //---------------------------------------------------------------------------
    // Calculator() constructor to initialize each of the stacks and the currentInput
    //---------------------------------------------------------------------------
    public Calculator() {
        numberStack = new Stack<>();
        operatorStack = new Stack<>();
        currentInput = new StringBuilder();
        isNegative = false;
    }

    //---------------------------------------------------------------------------
    // method to input a digit or decimal point
    //---------------------------------------------------------------------------
    public void inputDigit(String digit) {
        if (hasResult) {
            numberStack.clear(); // Clear the number stack if starting fresh
            hasResult = false; // Reset the flag
        }
        if(digit.equals("."))
        {
            if(!currentInput.toString().contains("."))
            {
                currentInput.append(digit);
            }
        } else {
            currentInput.append(digit);
        }
        lastInputWasNum = true;
    }

    //---------------------------------------------------------------------------
    // mhandles the sign button
    //---------------------------------------------------------------------------
    public void toggleSign() {
        if (currentInput.length() > 0) {
            isNegative = !isNegative; // Toggle sign
        }
    }

    //---------------------------------------------------------------------------
    // submitting a number copies it from the input to the numberStack where it is stored until it is used in a calculation
    //---------------------------------------------------------------------------
    public void submitNumber() {
        if (currentInput.length() > 0)
        {
            String numberToSubmit = isNegative ? "-" + currentInput.toString() : currentInput.toString();
            numberStack.push(numberToSubmit); // Store the number
            currentInput.setLength(0);  // Clear current input
            isNegative = false; // Reset sign
        }
    }

    //---------------------------------------------------------------------------
    // pushes an operator into the operator stack
    //---------------------------------------------------------------------------
    public void pushOperator(String operator) {
        if (hasResult) {
            numberStack.push(String.valueOf(lastResult)); // Push the last result onto the stack
            currentInput.setLength(0); // Clear current input to prepare for next digit
            hasResult = false; // Reset the flag
        }
        submitNumber(); // This submits the current input if exists
        operatorStack.push(operator);
        lastInputWasNum = false;
    }

    //---------------------------------------------------------------------------
    // pops the last entered number
    //---------------------------------------------------------------------------
    public String popNumber() {
        if(!numberStack.isEmpty())
        {
            return numberStack.pop();
        }
        return "";
    }

    //---------------------------------------------------------------------------
    // pops the most recent operator
    //---------------------------------------------------------------------------
    public String popOperator() {
        if(!operatorStack.isEmpty())
        {
            return operatorStack.pop();
        }
        return "";
    }

    //---------------------------------------------------------------------------
    // gets the current input string
    //---------------------------------------------------------------------------
    public String getCurrentInput() {
        if(isNegative)
        {
            return "-"+currentInput.toString();
        }
        return currentInput.toString();
    }

    //---------------------------------------------------------------------------
    // deletes the most recent input
    //---------------------------------------------------------------------------
    public void delete() {
        // if not empty, delete the last char of currentInput and return
        // currentInput is empty so delete an operator. Now pop and save the top of the number stack as it can be edited (deleted or added to) until a new operator is submitted. Continue in a loop like that until the numberStack is empty
        if(currentInput.length() > 0)
        {
            currentInput.deleteCharAt(currentInput.length() - 1);
            return;
        }

        if(!operatorStack.isEmpty())
        {
            operatorStack.pop();
            String lastNumber = numberStack.pop();
            currentInput.append(lastNumber);
        }
    }

    //---------------------------------------------------------------------------
    // gets the entire expression for evaluation (i.e. 12 + 34)
    //---------------------------------------------------------------------------
    public String getFullExpression() {
        StringBuilder expression = new StringBuilder();

        // Get sizes of the stacks
        int numberCount = numberStack.size();
        int operatorCount = operatorStack.size();

        // Iterate over the stacks
        for (int i = 0; i < numberCount; i++) {
            expression.append(numberStack.get(i)).append(" ");
            // Only append an operator if there's a corresponding one
            if (i < operatorCount) {
                expression.append(operatorStack.get(i)).append(" ");
            }
        }

        // Add the current input (if there is one)
        if (currentInput.length() > 0) {
            expression.append(getCurrentInput());
        }

        return expression.toString().trim(); // Trim any trailing spaces
    }

    //---------------------------------------------------------------------------
    // returns the number stack for testing
    //---------------------------------------------------------------------------
    public Stack<String> getNumberStack() {
        return numberStack;
    }

    //---------------------------------------------------------------------------
    // returns the operator stack for testing
    //---------------------------------------------------------------------------
    public Stack<String> getOperatorStack() {
        return operatorStack;
    }

    //---------------------------------------------------------------------------
    // clear the stacks (clear the short term memory of the calculator)
    //---------------------------------------------------------------------------
    public void clear() {
        numberStack.clear();
        operatorStack.clear();
        currentInput.setLength(0);
    }

    //---------------------------------------------------------------------------
    // split the expression into tokens (called on pressing equal)
    //---------------------------------------------------------------------------
    public String evaluateExpression() {
        String fullExpression = getFullExpression();   // Get the full expression as a string
        String[] tokens = tokenizeExpression(fullExpression); // Tokenize it

        String result = "";
        try {
            result = String.valueOf(evaluate(tokens));
            lastResult = Double.parseDouble(result); // Store the result after evaluation
            hasResult = true; // Indicate that we have a result now
        } catch (ArithmeticException e) {
            result = "NaN"; // Handle division by zero, etc.
        }
        resetState(); // Reset the state after evaluation
        return result;
    }

    //---------------------------------------------------------------------------
    // clear everything for a new expression to be formed
    //---------------------------------------------------------------------------
    private void resetState() {
        currentInput.setLength(0); // Clear current input
        operatorStack.clear(); // Clear the operator stack
        numberStack.clear(); // Clear the number stack if needed
    }

    //---------------------------------------------------------------------------
    // split the expression into tokens
    //---------------------------------------------------------------------------
    public String[] tokenizeExpression(String expression) {
        return expression.split(" ");   // divides expression based on spaces meaning operators and numbers will be split
    }

    //---------------------------------------------------------------------------
    // evaluate the expression
    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------
    public double evaluate(String[] tokens) {
        Stack<Double> numbers = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                numbers.push(Double.parseDouble(token));
            } else if (isOperator(token)) {
                // handle % before + or -
                if (!operators.isEmpty() && operators.peek().equals("%")) {
                    operators.pop();  // Remove '%'
                    double percentageValue = numbers.pop() / 100;
                    numbers.push(percentageValue);
                }

                // Handle operator precedence
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    double right = numbers.pop();
                    double left = numbers.pop();
                    String op = operators.pop();
                    double result = applyOperator(op, left, right);
                    if(result == -0.0)
                    {
                        result = 0.0;
                    }
                    numbers.push(result);
                }
                operators.push(token);
            } else if (isFunction(token)) {
                double value = numbers.pop();
                // Check if it's % and follows +/-, treat it as a percentage of the previous number
                if ("%".equals(token) && !operators.isEmpty() && (operators.peek().equals("+") || operators.peek().equals("-"))) {
                    double baseValue = numbers.pop();
                    value = baseValue * (value / 100);
                    if(value == -0.0)
                    {
                        value = 0.0;
                    }
                    numbers.push(baseValue);  // Push baseValue back for addition/subtraction
                } else {
                    value = applyFunction(token, value);
                }
                if(value == -0.0)
                {
                    value = 0.0;
                }
                numbers.push(value);
            }
        }

        // Final evaluation for any remaining operators
        while (!operators.isEmpty()) {
            double right = numbers.pop();
            double left = numbers.pop();
            String op = operators.pop();
            double result = applyOperator(op, left, right);
            if(result == -0.0)
            {
                result = 0.0;
            }
            numbers.push(result);
        }

        return numbers.pop();
    }

    //---------------------------------------------------------------------------
    // checks if the token is a number
    //---------------------------------------------------------------------------
    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e)
        {
            return false;
        }
    }

    //---------------------------------------------------------------------------
    // checks if the token is an operator (binary)
    //---------------------------------------------------------------------------
    private boolean isOperator(String token) {
        return token.matches("[+*/\\-]");
    }

    //---------------------------------------------------------------------------
    // checks if the token is a function (unary)
    //---------------------------------------------------------------------------
    private boolean isFunction(String token) {
        return token.equals("%");   // add other functions as buttons are added
    }

    //---------------------------------------------------------------------------
    // determines the precedence of the operator based on PEMDAS
    //---------------------------------------------------------------------------
    private int precedence(String operator) {
        switch(operator)
        {
            // add more as functionality grows
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            default:
                return 0;
        }
    }

    //---------------------------------------------------------------------------
    // computes binary operations (+, -, *, /, &c.)
    //---------------------------------------------------------------------------
//    private Double applyOperator(String op, double left, double right) {
//        switch(op)
//        {
//            case "+":
//                return left + right;
//            case "-":
//                return left - right;
//            case "*":
//                return left * right;
//            case "/":
//                if (right == 0) {
//                    throw new ArithmeticException();
//                }
//                return left / right;
//            default:
//                throw new UnsupportedOperationException("Operator not supported: " + op);
//        }
//    }

    private Double applyOperator(String op, double left, double right) {
        switch(op) {
            case "+":
                return left + right;
            case "-":
                return left - right;
            case "*":
                return left * right;
            case "/":
                if (right == 0) {
                    throw new ArithmeticException();
                }
                return left / right;
            default:
                throw new UnsupportedOperationException("Operator not supported: " + op);
        }
    }
    //---------------------------------------------------------------------------
    // computes unary operations (%, square, sqrt, &c.)
    //---------------------------------------------------------------------------
//    private Double applyFunction(String function, double value) {
//        switch(function)
//        {
//            case "%":
//                return percent(value);  // Calls the percent method with the current value
//            case "sqrt":
//                return Math.sqrt(value);
//            case "x²":
//                return Math.pow(value, 2);
//            case "x³":
//                return Math.pow(value, 3);
//            default:
//                throw new UnsupportedOperationException("Operator not supported: " + function);
//        }
//    }

    private Double applyFunction(String function, double value) {
        switch(function) {
            case "%":
                return value / 100;
            case "sqrt":
                return Math.sqrt(value);
            case "sin":
                return Math.sin(Math.toRadians(value));  // Converts degrees to radians if needed
            case "cos":
                return Math.cos(Math.toRadians(value));  // Converts degrees to radians if needed
            case "tan":
                return Math.tan(Math.toRadians(value));  // Converts degrees to radians if needed
            case "x²":
                return Math.pow(value, 2);
            case "x³":
                return Math.pow(value, 3);
            default:
                throw new UnsupportedOperationException("Function not supported: " + function);
        }
    }
    //---------------------------------------------------------------------------
    // handles percent and accounts for single or double operators
    //---------------------------------------------------------------------------
    private Double percent(double currentNumber) {
        // Only perform operations if there are values in the stacks
        if (!operatorStack.isEmpty() && !numberStack.isEmpty()) {
            String lastOp = operatorStack.peek(); // Get the last operator
            double baseNum = Double.parseDouble(numberStack.peek()); // Peek the last number (if needed)

            switch (lastOp) {
                case "+":
                case "-":
                    Log.d("Tag", "Last op was + or -");
                    // Calculate the percentage relative to baseNum and return the modified number
                    currentNumber = baseNum * (currentNumber / 100);
                    break;
                case "*":
                case "/":
                    Log.d("Tag", "Last op was * or /");
                    // For * and /, directly apply % on currentNumber
                    currentNumber = currentNumber / 100;
                    break;
                case "%":
                    Log.d("Tag", "Last op was %");
                    // Apply % directly if it is a standalone operator
                    currentNumber = currentNumber / 100;
                    break;
                default:
                    Log.d("Tag", "Unsupported operator for % calculation");
                    break;
            }
        } else {
            Log.d("Tag", "Op stack is empty");
            // If the operator stack is empty, it is likely a standalone %
            currentNumber = currentNumber / 100;
        }

        return currentNumber;
    }



}
