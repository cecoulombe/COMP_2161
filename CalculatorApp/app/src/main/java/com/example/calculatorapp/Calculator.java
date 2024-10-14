package com.example.calculatorapp;

import android.util.Log;
import android.widget.Toast;
import android.content.Context;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;

public class Calculator {
    // global variables
    Stack<String> numberStack;  // stores the user's current input
    Stack<String> operatorStack;    // stores the operators used
    StringBuilder currentInput; // stores previous input from the user after an operator is selected
    boolean isNegative;
    String lastResult = "";
    boolean hasResult = false;
    StringBuilder orderOfInputs;  // will track the order of the inputs where 1 is a number and 0 is an operator.
    String memVar = "";
    private Context context; // Declare a Context variable
    private String spinnerChoice;


    //---------------------------------------------------------------------------
    // Calculator() constructor to initialize each of the stacks and the currentInput
    //---------------------------------------------------------------------------
    public Calculator(Context context) {
        this.context = context;
        numberStack = new Stack<>();
        operatorStack = new Stack<>();
        currentInput = new StringBuilder();
        isNegative = false;
        orderOfInputs = new StringBuilder();
    }

    //---------------------------------------------------------------------------
    // gets the spinner option from the main activity (setter)
    //---------------------------------------------------------------------------
    public void setSpinnerChoice(String spinnerChoice) {
        this.spinnerChoice = spinnerChoice;
        Log.d("setSelectedOption", "Spinner updated to be: " + getSpinnerChoice());
    }

    //---------------------------------------------------------------------------
    // returns the current spinner options
    //---------------------------------------------------------------------------
    public String getSpinnerChoice() {
        return spinnerChoice;
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
    // handles the various function buttons
    //---------------------------------------------------------------------------
    public void pushFunction(String function) {
        // don't push functions if its in running calculation mode
        if(getSpinnerChoice().equals("Running Calculations")) {
            Toast.makeText(context, "Cannot use functions in Running Calculation mode.", Toast.LENGTH_SHORT).show();
            return;
        }
        // add the function and ( to the stacks
        numberStack.push(function);
        orderOfInputs.append("1");
        if(hasResult && isNumber(lastResult))
        {
            currentInput.append(lastResult);
            hasResult = false;
        }
        else {
            hasResult = false;
        }
        operatorStack.push("(");
        orderOfInputs.append("0");
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
        if (hasResult && isNumber(lastResult)) {
            Log.d("pushOperator", "lastResult is a number");
            numberStack.push(lastResult); // Push the last result onto the stack
            orderOfInputs.append("1");
            currentInput.setLength(0); // Clear current input to prepare for next digit
            Log.d("pushOperator", "The number stack now is: " + getNumberStack());
            hasResult = false; // Reset the flag
            operatorStack.push(operator);
            orderOfInputs.append("0");
            return;
        }

        Log.d("pushOperator", "lastResult is a NOT number");

        // handle negative with the minus sign
        if(operator.equals("-") && (currentInput.length() == 0 || currentInput.charAt(currentInput.length() - 1) == '-'))
        {
            Log.d("pushOp", "Sending negative instead of minus");
            inputDigit("-");
            return;
        }
        // if the user wants the running option, then evaluate whats there and push the result
        if(getSpinnerChoice().equals("Running Calculations"))
        {
            if(isLeftParen(operator) || isRightParen(operator))
            {
                // don't push parens or functions in running calc mode
                Toast.makeText(context, "Cannot use parentheses in Running Calculation mode.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(currentInput.length() > 0 && isOperator(operator))
            {
                submitNumber(); // This submits the current input if exists
                currentInput.append(evaluateExpression());
                numberStack.push(lastResult); // Push the last result onto the stack
                orderOfInputs.append("1");
                currentInput.setLength(0);
                hasResult = false; // Reset the flag
                operatorStack.push(operator);
                orderOfInputs.append("0");
                return;
            }
        }

        // if there is something to submit, then submit it and push the operator
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
    // saves the last result into the memSave function
    //---------------------------------------------------------------------------
    public void memSave()
    {
        if(hasResult && isNumber(lastResult))
        {
            memVar = String.valueOf(lastResult);
            Toast.makeText(context, "Memory saved.", Toast.LENGTH_SHORT).show();
            return;

        } else
        {
            Toast.makeText(context, "No result to save.", Toast.LENGTH_SHORT).show();
        }
    }

    //---------------------------------------------------------------------------
    // returns the current memory function and puts it into currentInput
    //---------------------------------------------------------------------------
    public void memClear()
    {
        // if there is a value being saved, clear it
        memVar = "";
        Toast.makeText(context, "Memory cleared.", Toast.LENGTH_SHORT).show();

    }

    //---------------------------------------------------------------------------
    // clears the value of memVar
    //---------------------------------------------------------------------------
    public void memRecall()
    {
        // if there is a value being saved, then put it in. If not, do nothing
        if(memVar != null && !memVar.isEmpty())
        {
            currentInput.setLength(0);
            currentInput.append(memVar);
            hasResult = false;
            lastResult = "0";
            Toast.makeText(context, "Memory recalled.", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "Nothing to recall.", Toast.LENGTH_SHORT).show();
        }
    }

    //---------------------------------------------------------------------------
    // adds the lastResult to the memVar
    //---------------------------------------------------------------------------
    public void memAdd()
    {
        if(hasResult && isNumber(lastResult) && memVar != null && !memVar.isEmpty())
        {
            Log.d("memAdd", "Adding something memAdd");
            double currentMem = Double.parseDouble(memVar);
            currentMem += Double.parseDouble(lastResult);
            memVar = String.valueOf(currentMem);
            Toast.makeText(context, "Value added to memory.", Toast.LENGTH_SHORT).show();

        }
    }

    //---------------------------------------------------------------------------
    // subtracts the lastResult form the memVar
    //---------------------------------------------------------------------------
    public void memSub()
    {
        if(hasResult && isNumber(lastResult) && memVar != null && !memVar.isEmpty())
        {
            Log.d("memSub", "Subtracting something from memSub");
            double currentMem = Double.parseDouble(memVar);
            currentMem -= Double.parseDouble(lastResult);
            memVar = String.valueOf(currentMem);
            Toast.makeText(context, "Value subtracted from memory.", Toast.LENGTH_SHORT).show();
        }
    }

    //---------------------------------------------------------------------------
    // inverses the currentInput/result
    //---------------------------------------------------------------------------
    public void inverse()
    {
        numberStack.push("1");
        orderOfInputs.append("1");
        operatorStack.push("/");
        orderOfInputs.append("0");

        if (hasResult && isNumber(lastResult)) {
            numberStack.push(String.valueOf(lastResult)); // Push the last result onto the stack
            currentInput.setLength(0); // Clear current input to prepare for next digit
            orderOfInputs.append("1");
            hasResult = false; // Reset the flag
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
    // inputs e
    //---------------------------------------------------------------------------
    public void pushE() {
        if(currentInput.length() > 0)
        {
            // there is a cI, so multiply it by e
            pushOperator("*");
            currentInput.append("e");

        } else {
            // there is not a cI so set cI = e
            currentInput.append("e");
        }
    }

    //---------------------------------------------------------------------------
    // inputs PI
    //---------------------------------------------------------------------------
    public void pushPi() {
        if(currentInput.length() > 0)
        {
            // there is a cI, so multiply it by e
            pushOperator("*");
            currentInput.append("π");

        } else {
            // there is not a cI so set cI = e
            currentInput.append("π");
        }
    }

    //---------------------------------------------------------------------------
    // inputs PI
    //---------------------------------------------------------------------------
    public void pushRand() {
        String randNum = String.valueOf(Math.random());


//        Log.d("pushRand", "Checking the format of the number");
//        String regex = "^\\d{1,15}(\\.\\d{0,9})?$";
//
//        // If the input is valid and doesn't exceed the max length
//        if (randNum.length() <= 15 && randNum.matches(regex)) {
//            // Valid input, proceed with processing
//            // e.g., update the display or save the input
//            Log.d("pushRand", "Length is ok, letting it through");
//        } else {
//            // Invalid input; truncate it to fit the constraints
//            // Initialize a new input to store the truncated value
//            String truncatedInput = randNum;
//
//            Log.d("pushRand", "Too long... truncating value");
//            // Loop to truncate the least significant digit
//            while (truncatedInput.length() > 10) {
//                // Check if there is a decimal point
//                int decimalIndex = truncatedInput.indexOf('.');
//                if (decimalIndex != -1 && truncatedInput.length() - decimalIndex > 10) {
//                    // If there is a decimal and the total length exceeds, remove the last character
//                    Log.d("pushRand", "Too long... removed a decimal");
//                    truncatedInput = truncatedInput.substring(0, truncatedInput.length() - 1);
//                } else {
//                    // If no decimal or total length is still too long, truncate the last character
//                    Log.d("pushRand", "Too long.. removing a value");
//                    truncatedInput = truncatedInput.substring(0, truncatedInput.length() - 1);
//                }
//            }
//            Log.d("pushRand", "Saving the truncated result: " + truncatedInput);
//            randNum = truncatedInput;
//        }

        if(currentInput.length() > 0)
        {
            // there is a cI, so multiply it by e
            pushOperator("*");
            currentInput.append(randNum);
        } else {
            // there is not a cI so set cI = e
            currentInput.append(randNum);
        }
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

        if(orderOfInputs.length() > 0)
        {
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
        else
        {
            Log.d("delete", "Nothing to delete.");
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
            return lastResult;
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
        lastResult = "0";
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
            result = String.format(Locale.US,"%.7g",evaluate(tokens));
            if (result.contains("e")) {
                result = result.replace("e", "E"); // Change to uppercase E
            }
            result = result.replace("E+", "E"); // Remove the '+' sign before the exponent

            if (result.contains(".")) { // Check if it is a decimal number
                result = result.replaceAll("\\.?0*$", ""); // Remove trailing zeros after decimal
            }

        } catch (ArithmeticException e) {
            result = "NaN"; // Handle division by zero, etc.
        } catch (IllegalArgumentException e) {
            if(e.equals("Mismatched Parentheses"))
            {
                result = "Error: Missing ( or )";
            }
            else {
                result = "NaN"; // Handle division by zero, etc.
            }
        } catch (EmptyStackException e) {
            result = "Invalid";
        } finally
        {
            lastResult = result;
            hasResult = true; // There will always be a result, even if its not a good one
        }

        resetState(); // Reset the state after evaluation
        Log.d("evaluateExpression", "Returning the following result: " + result);
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
            if(newTokens[i].equals("e"))
            {
                newTokens[i] = String.valueOf(Math.E);
            } else if(newTokens[i].equals("π"))
            {
                newTokens[i] = String.valueOf(Math.PI);
            } else if ("!".equals(newTokens[i]))
            {
                double value = applyFunction(newTokens[i], numbers.pop());

                if(value == -0.0)
                {
                    value = 0.0;
                }
                numbers.push(value);
            }
            if (isNumber(newTokens[i])) {
                numbers.push(Double.parseDouble(newTokens[i]));
            } else if((i == 0 || isOperator(newTokens[i - 1])) && newTokens[i].equals("-"))   // check that its the first thing (aside from other ignored negatives)
            {
                if (newTokens[i + 1].equals("-"))
                {
                    // ignore both of them (double negative)
                    i = i + 1;
                } else if(isNumber(newTokens[i + 1]))
                {
                    // handle the negative values first
                    double value = Double.parseDouble(newTokens[i + 1]);

                    value = applyFunction("-", value);

                    if(value == -0.0)
                    {
                        value = 0.0;
                    }
                    numbers.push(value);
                    i = i + 1;
                }
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
                double value;
                // Check if it's % and follows +/-, treat it as a percentage of the previous number
                if ("%".equals(newTokens[i]) && !operators.isEmpty() && (operators.peek().equals("+") || operators.peek().equals("-"))) {
                    value = numbers.pop();
                    double baseValue = numbers.pop();
                    value = baseValue * (value / 100);
                    if(baseValue == -0.0)
                    {
                        baseValue = 0.0;
                    }
                    numbers.push(baseValue);  // Push baseValue back for addition/subtraction
                } else {
                    if ("%".equals(newTokens[i]))
                    {
                        value = applyFunction(newTokens[i], numbers.pop());
                    }
                    else
                    {
                        value = applyFunction(newTokens[i], Double.parseDouble(newTokens[i + 1]));
                        i += 1;
                    }
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
            } else if(isRightParen(tokens[i])) {
                throw new IllegalArgumentException("Mismatched parentheses");
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
        return token.matches("[+*E^/\\-]") || token.equals("root");
    }

    //---------------------------------------------------------------------------
    // checks if the token is a function (unary)
    //---------------------------------------------------------------------------
    private boolean isFunction(String token) {
//        return token.equals("%");   // add other functions as buttons are added

        Set<String> validFunctions = new HashSet<>();
        validFunctions.add("%");
        validFunctions.add("sqrt");
        validFunctions.add("cbrt");
        validFunctions.add("square");
        validFunctions.add("cube");
        validFunctions.add("e^");
        validFunctions.add("10^");
        validFunctions.add("sin");
        validFunctions.add("cos");
        validFunctions.add("tan");
        validFunctions.add("sinh");
        validFunctions.add("cosh");
        validFunctions.add("tanh");
        validFunctions.add("log");
        validFunctions.add("ln");

        return validFunctions.contains(token);
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
            case "^":
            case "root":
                return 3;
            case "E":
                return 4;
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
            case "^":
                double powResult = Math.pow(left, right);
                Log.d("applyOp", "Returning result: " + powResult);
                return powResult;
            case "E":
                double EResult = left * Math.pow(10, right);
                Log.d("applyOp", "Returning result: " + EResult);
                return EResult;
            case "root":
                double rootResult = Math.pow(right, 1 / left);
                Log.d("applyOp", "Returning result: " + rootResult);
                return rootResult;
            default:
                throw new UnsupportedOperationException("Operator not supported: " + op);
        }
    }
    //---------------------------------------------------------------------------
    // computes unary operations (%, square, sqrt, &c.)
    //---------------------------------------------------------------------------
    private Double applyFunction(String function, double value) {
        Log.d("applyFunction", "Received function: " + function + " and value: " + value);

        switch(function) {
            case "%":
                return value / 100;
            case "-":   // negative, not minus
                return value * -1;
            case "square":
                return Math.pow(value, 2);
            case "cube":
                return Math.pow(value, 3);
            case "e^":
                return Math.pow(Math.E, value);
            case "10^":
                return Math.pow(10, value);
            case "sqrt":
                return Math.sqrt(value);
            case "cbrt":
                double cbrtResult = Math.cbrt(value);
                Log.d("apply CBRT", "Returning the result: " + cbrtResult);
                return cbrtResult;
            case "sin":
                return Math.sin(Math.toRadians(value));  // Converts degrees to radians if needed
            case "cos":
                return Math.cos(Math.toRadians(value));  // Converts degrees to radians if needed
            case "tan":
                return Math.tan(Math.toRadians(value));  // Converts degrees to radians if needed
            case "sinh":
                return Math.sinh(Math.toRadians(value));  // Converts degrees to radians if needed
            case "cosh":
                return Math.cosh(Math.toRadians(value));  // Converts degrees to radians if needed
            case "tanh":
                return Math.tanh(Math.toRadians(value));  // Converts degrees to radians if needed
            case "log":
                return Math.log10(value);
            case "ln":
                return Math.log(value);
            case "!":
                double factResult = factorial(value);
                Log.d("factorial", "Returning the result " + factResult);
                return factResult;
            default:
                throw new UnsupportedOperationException("Function not supported: " + function);
        }
    }

    //---------------------------------------------------------------------------
    // calculates the factorial of an integer number
    //---------------------------------------------------------------------------
    private Double factorial(double value) {
        if(value != Math.floor(value) || value != Math.abs(value))
        {
            return Double.NaN;
        }

        if(value != 1)
        {
            value = value * factorial(value - 1);
        }

        return value;
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
