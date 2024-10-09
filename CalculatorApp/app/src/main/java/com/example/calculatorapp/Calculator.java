package com.example.calculatorapp;

import android.util.Log;

import java.util.Arrays;
import java.util.Stack;

public class Calculator {
    // global variables
    private Stack<String> numberStack;  // stores the user's current input
    private Stack<String> operatorStack;    // stores the operators used
    private StringBuilder currentInput; // stores previous input from the user after an operator is selected
    private boolean isNegative;
    private double lastResult = 0.0;
    private boolean hasResult = false;
    private StringBuilder orderOfInputs;  // will track the order of the inputs where 1 is a number and 0 is an operator.


    //---------------------------------------------------------------------------
    // Calculator() constructor to initialize each of the stacks and the currentInput
    //---------------------------------------------------------------------------
    public Calculator() {
        numberStack = new Stack<>();
        operatorStack = new Stack<>();
        currentInput = new StringBuilder();
        isNegative = false;
        orderOfInputs = new StringBuilder();
    }

    //---------------------------------------------------------------------------
    // method to input a digit or decimal point
    //---------------------------------------------------------------------------
    public void inputDigit(String digit) {
        if (hasResult) {
            numberStack.clear(); // Clear the number stack if starting fresh
            hasResult = false; // Reset the flag
        }
        if(currentInput.length() == 0)
        {
            isNegative = false;
        } 
//        else if(digit.equals("-"))
//        {
//            isNegative = !isNegative;
//            Log.d("inputDigit", "Received a negative; the current sign is: " + (isNegative? "positive" : "negative"));
//        }
        if(digit.equals("."))
        {
            if(!currentInput.toString().contains("."))
            {
                currentInput.append(digit);
            }
        } else {
            currentInput.append(digit);
        }
    }

    //---------------------------------------------------------------------------
    // handles the sign button
    //---------------------------------------------------------------------------
    public void toggleSign() {
        if (currentInput.length() > 0) {
            isNegative = !isNegative; // Toggle sign
        }
        if(hasResult)
        {
            lastResult = lastResult * -1;
        }
    }

    //---------------------------------------------------------------------------
    // submitting a number copies it from the input to the numberStack where it is stored until it is used in a calculation
    //---------------------------------------------------------------------------
    public void submitNumber() {
        if (currentInput.length() > 0)
        {
            for(int i = 0; i < currentInput.length(); i++)
            {
                if(currentInput.charAt(i) == '-')
                {
                    currentInput.deleteCharAt(i);
                    isNegative = !isNegative;
                    Log.d("submitNumber", "Found a negative; the current sign is: " + (isNegative? "negative" : "positive"));
                }
            }
            Log.d("submitNumber", "Final value to submit is : " + (isNegative? "negative" : "positive"));
            String numberToSubmit = isNegative ? "-" + currentInput.toString() : currentInput.toString();
            numberStack.push(numberToSubmit); // Store the number
            currentInput.setLength(0);  // Clear current input
            orderOfInputs.append("1");
            Log.d("orderOfInputs", "Appended 1, num was " + numberToSubmit);
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
            operatorStack.push(operator);
            orderOfInputs.append("0");
            return;
        }

        // handle negative with the minus sign
        if(operator.equals("-") && (currentInput.length() == 0 || currentInput.charAt(currentInput.length() - 1) == '-'))
        {
            Log.d("pushOp", "Sending negative instead of minus");
            inputDigit("-");
            return;
        }
        // otherwise, if there is something to submit, then submit it and push the operator
        if(currentInput.length() > 0)
        {
            submitNumber(); // This submits the current input if exists
            operatorStack.push(operator);
            orderOfInputs.append("0");
            Log.d("orderOfInputs", "Appended 0, op was: " + operator);
            return;
        } else if(isLeftParen(operator))
        {
            operatorStack.push(operator);
            orderOfInputs.append("0");
            Log.d("orderOfInputs", "Appended 0, op was: " + operator);
            return;
        } else if(!operatorStack.isEmpty())
        {
            if(isRightParen(operatorStack.peek()))
            {
                operatorStack.push(operator);
                orderOfInputs.append("0");
                Log.d("orderOfInputs", "Appended 0, op was: " + operator);
                return;
            }
            if (!isOperator(operatorStack.peek()))
            {
                operatorStack.push(operator);
                orderOfInputs.append("0");
                Log.d("orderOfInputs", "Appended 0, op was: " + operator);
                return;
            } else {
                String prevOp = operatorStack.pop();
                operatorStack.push(operator);
                Log.d("orderOfInputs", "Appended 0, op was: " + prevOp + ", replaced with: " + operator);
                return;
            }
        }
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

        Log.d("delete", "Asked to delete from: " + orderOfInputs);

        if(currentInput.length() > 0)
        {
            currentInput.deleteCharAt(currentInput.length() - 1);
            Log.d("delete", "Deleted the last digit of current input");
            return;
        }

        Log.d("delete", "The last char in orderOfInputs was " + orderOfInputs.charAt(orderOfInputs.length() - 1));
        boolean lastOpIs0 = orderOfInputs.charAt(orderOfInputs.length() - 1) == '0';
        Log.d("delete", "The last op is 0? " + lastOpIs0);

        if(orderOfInputs.charAt(orderOfInputs.length() - 1) == '0') {
            Log.d("delete", "The last input is an op");
            if (!operatorStack.isEmpty()) {
                operatorStack.pop();
                orderOfInputs.deleteCharAt(orderOfInputs.length() - 1);
                Log.d("delete", "Deleted the last operator and shortened the list");
                if (orderOfInputs.charAt(orderOfInputs.length() - 1) == '1') {
                    String lastNumber = numberStack.pop();
                    currentInput.append(lastNumber);
                    orderOfInputs.deleteCharAt(orderOfInputs.length() - 1);
                    Log.d("delete", "Next input was a num, so moved it over to currentInput");
                }
            }
        }
    }

    //---------------------------------------------------------------------------
    // gets the entire expression for evaluation (i.e. 12 + 34)
    //---------------------------------------------------------------------------
    public String getFullExpression()
    {
        Log.d("getFullExpression", "Order of inputs is: " + orderOfInputs);
        // display results if there are any
        if(hasResult)
        {
            return String.valueOf(lastResult);
        }

        // build the expression of inputs
        StringBuilder expression = new StringBuilder();
        int numberCount = numberStack.size();
        int operatorCount = operatorStack.size();
        int numIndex = 0;
        int opIndex = 0;

        for(int i = 0; i < orderOfInputs.length(); i++)
        {
            if(orderOfInputs.charAt(i) == '1')
            {
                if(numIndex < numberCount)
                {
                    expression.append(numberStack.get(numIndex)).append(" ");
                    numIndex++;
                }
            } else if(orderOfInputs.charAt(i) == '0')
            {
                if(opIndex < operatorCount)
                {
                    expression.append(operatorStack.get(opIndex)).append(" ");
                    opIndex++;
                }
            }
        }

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
        lastResult = 0;
        hasResult = false;
        orderOfInputs.setLength(0);
        Log.d("Clear", "Cleared all stacks, input, and results");
    }

    //---------------------------------------------------------------------------
    // split the expression into tokens (called on pressing equal)
    //---------------------------------------------------------------------------
    public String evaluateExpression() {
        String fullExpression = getFullExpression();   // Get the full expression as a string
        Log.d("evaluateExpression", "Full expression before tokenizing: " + fullExpression);
        String[] tokens = tokenizeExpression(fullExpression); // Tokenize it
        Log.d("evaluateExpression", "Tokens after splitting: " + Arrays.toString(tokens));

        // scan tokens to eliminate double negatives?
        for (int j = 0; j < tokens.length; j++) {
            StringBuilder tokenBuilder = new StringBuilder(tokens[j]);

            // Iterate through the string with index `i` and `i + 1`
            for (int i = 0; i < tokenBuilder.length() - 1; i++) {
                if (tokenBuilder.charAt(i) == '-' && tokenBuilder.charAt(i + 1) == '-') {
                    // Delete both consecutive hyphens
                    tokenBuilder.delete(i, i + 2);

                    // Adjust the index since we removed two characters
                    i -= 1;
                }
            }

            // Replace the modified token in the array
            tokens[j] = tokenBuilder.toString();
        }

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
    public double evaluate(String[] tokens) {
        Stack<Double> numbers = new Stack<>();
        Stack<String> operators = new Stack<>();

        // to evaluate parentheses, make a copy of the tokens, go through it and look for parentheses, pushing elements onto the new list , keep doing that until you've evaluated all of them, then evaluate the outdated expression
        String[] newTokens = evaluateParens(tokens);

        Log.d("evaluate", "The tokens passed to evaluate are: " + Arrays.toString(newTokens));


        for (int i = 0; i < newTokens.length; i++) {
            Log.d("EvaluateLoop", "Token at position " + i + ": " + newTokens[i]);
                if (isNumber(newTokens[i])) {
                numbers.push(Double.parseDouble(newTokens[i]));
            } else if (isOperator(newTokens[i])) {
                // Handle operator precedence
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(newTokens[i])) {
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
                operators.push(newTokens[i]);
            } else if (isFunction(newTokens[i])) {
                double value = numbers.pop();
                // Check if it's % and follows +/-, treat it as a percentage of the previous number
                if ("%".equals(newTokens[i]) && !operators.isEmpty() && (operators.peek().equals("+") || operators.peek().equals("-"))) {
                    double baseValue = numbers.pop();
                    value = baseValue * (value / 100);
                    if(baseValue == -0.0)
                    {
                        baseValue = 0.0;
                    }
                    numbers.push(baseValue);  // Push baseValue back for addition/subtraction
                } else {
                    value = applyFunction(newTokens[i], value);
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
    // Go through the entire method, evaluate any parentheses, then return the updated list of tokens
    //---------------------------------------------------------------------------
    private String[] evaluateParens(String[] tokens) {
        StringBuilder newExpression = new StringBuilder();

        // iterate through the list

        // if its not a left paren, submit it to the newTokens
        // if its a left paren, then find the right paren
        // submit the range from left to right paren into evaluate, which will call this recursively
        // push the result of evaluate onto the tokens
        // return the newTokens
        for (int i = 0; i < tokens.length; i++) {
            Log.d("evaluateParens", "Looking at the position: " + i + "which is token " + tokens[i]);
            if (isLeftParen(tokens[i])) {
                Log.d("evaluateParens", "Found left parens at pos " + i);
                // Find the corresponding right parenthesis
                int rightParenPos = findRightParen(tokens, i);
                Log.d("evaluateParens", "Found right parens at pos " + rightParenPos);
                if (rightParenPos == -1) {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }

                // Extract the sub-expression between the parentheses
                String[] subTokens = Arrays.copyOfRange(tokens, i + 1, rightParenPos);
                newExpression.append(String.valueOf(evaluate(subTokens))).append(" "); // Recursively evaluate the sub-expression

                i = rightParenPos; // Update the index to the position of the right parenthesis
                Log.d("evaluateParens", "Jumping to pos of right parens: " + i);
            } else {
                Log.d("evaluateParens", "i : " + i + ", symbol = : " + tokens[i]);

                newExpression.append(tokens[i]).append(" ");
            }
        }

        String[] newTokens = tokenizeExpression(String.valueOf(newExpression));
        Log.d("evaluateParens", "Returning the tokens: " + Arrays.toString(newTokens));
        return newTokens;
    }

    //---------------------------------------------------------------------------
    // Helper method to find the corresponding right parenthesis
    //---------------------------------------------------------------------------
    private int findRightParen(String[] tokens, int leftParenPos) {
        Log.d("ParenthesisBalance", "Received the following tokens: " + Arrays.toString(tokens));
        Log.d("ParenthesisBalance", "Starting parenthesis search at position: " + leftParenPos);
        Log.d("ParenthesisBalance", "Total number of tokens: " + tokens.length);

        int balance = 1; // Start with a balance of 1 for the initial left parenthesis
        for (int i = leftParenPos + 1; i < tokens.length; i++) {
            if (isLeftParen(tokens[i])) {
                balance++;
            } else if (isRightParen(tokens[i])) {
                balance--;
            }

            Log.d("ParenthesisBalance", "Current balance at position " + i + ", which has value " + tokens[i] + ": " + balance);

            if (balance == 0) {
                Log.d("ParenthesisBalance", "Right parenthesis found at position: " + i);
                return i; // Matching right parenthesis found
            }
        }
        Log.d("ParenthesisBalance", "Balance at fail is: " + balance);
        return -1; // No matching right parenthesis found
    }

    //---------------------------------------------------------------------------
    // checks if the token is the left bracket
    //---------------------------------------------------------------------------
    private boolean isLeftParen(String token) {
        return token.equals("(");
    }

    //---------------------------------------------------------------------------
    // checks if the token is the right bracket
    //---------------------------------------------------------------------------
    private boolean isRightParen(String token) {
        return token.equals(")");
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
