package com.example.calculatorapp;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class CalculatorTest {
    private Calculator calculator;

    @Before
    public void setUp()
    {
        calculator = new Calculator();
    }

    @Test   //specifically tests the functionality of the sumbitNumber method
    public void testSubmitNumber()
    {
        // single digit number
        calculator.inputDigit("1");
        calculator.submitNumber();
        assertEquals("1", calculator.getNumberStack().peek());

        // single digit number, negative
        calculator.inputDigit("1");
        calculator.toggleSign();
        calculator.submitNumber();
        assertEquals("-1", calculator.getNumberStack().peek());

        // multi-digit number
        calculator.inputDigit("2");
        calculator.inputDigit("3");
        calculator.submitNumber();
        assertEquals("23", calculator.getNumberStack().peek());

        // check that currentInput is empty after submission
        calculator.inputDigit("4");
        calculator.submitNumber();
        assertEquals("", calculator.getCurrentInput());

        calculator.clear();

        // no input, make sure numberStack stays empty
        calculator.submitNumber();
        assertEquals("[]", calculator.getNumberStack().toString());

        // super long number - checking against overflow and confirming currentInput before and after submission
        calculator.inputDigit("1");
        calculator.inputDigit("2");
        calculator.inputDigit("3");
        calculator.inputDigit("4");
        calculator.inputDigit("5");
        calculator.inputDigit("6");
        calculator.inputDigit("7");
        calculator.inputDigit("8");
        calculator.inputDigit("9");
        calculator.inputDigit("0");
        calculator.inputDigit("1");
        calculator.inputDigit("2");
        calculator.inputDigit("3");
        calculator.inputDigit("4");
        calculator.inputDigit("5");
        calculator.inputDigit("6");
        calculator.inputDigit("7");
        calculator.inputDigit("8");
        calculator.inputDigit("9");
        calculator.inputDigit("0");
        assertEquals("12345678901234567890", calculator.getCurrentInput());
        calculator.submitNumber();
        assertEquals("12345678901234567890", calculator.getNumberStack().peek());
        assertEquals("", calculator.getCurrentInput());
    }

    @Test   //specifically tests the functionality of the sumbitNumber method with decimal values
    public void testDecimalPoints()
    {
        calculator.inputDigit("1");
        calculator.inputDigit(".");
        calculator.inputDigit("1");
        calculator.submitNumber();
        assertEquals("1.1", calculator.getNumberStack().peek());

        // negative decimal
        calculator.inputDigit("1");
        calculator.inputDigit(".");
        calculator.toggleSign();
        calculator.inputDigit("1");
        calculator.submitNumber();
        assertEquals("-1.1", calculator.getNumberStack().peek());

        calculator.inputDigit(".");
        calculator.inputDigit("1");
        calculator.submitNumber();
        assertEquals(".1", calculator.getNumberStack().peek());

        calculator.inputDigit("4");
        calculator.inputDigit("5");
        calculator.inputDigit(".");
        calculator.inputDigit("1");
        calculator.inputDigit(".");
        calculator.inputDigit("5");
        calculator.submitNumber();
        assertEquals("45.15", calculator.getNumberStack().peek());

        // toggle negative in the middle of a number
        calculator.inputDigit("4");
        calculator.inputDigit("5");
        calculator.inputDigit(".");
        calculator.inputDigit("1");
        calculator.toggleSign();
        calculator.inputDigit("5");
        calculator.submitNumber();
        assertEquals("-45.15", calculator.getNumberStack().peek());
    }

    @Test   //specifically tests the functionality of the isEmpty method and that an empty value is not pushed
    public void testIsEmpty() {
        calculator.submitNumber();
        assertTrue(calculator.getNumberStack().isEmpty()); // Assuming submitNumber does not push empty values
    }

    @Test   //specifically tests the operators (they submit the values and save the operator)
    public void testOperators()
    {
        // simple operator
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        assertEquals("1", calculator.getNumberStack().peek());
        assertEquals("+", calculator.getOperatorStack().peek());

        // simple operator with negative before operator
        calculator.inputDigit("1");
        calculator.toggleSign();
        calculator.pushOperator("+");
        assertEquals("-1", calculator.getNumberStack().peek());
        assertEquals("+", calculator.getOperatorStack().peek());

        // operator only
        calculator.clear();
        calculator.pushOperator("+");
        assertEquals("[]", calculator.getNumberStack().toString());
        assertEquals("+", calculator.getOperatorStack().peek());

        // decimal operator
        calculator.inputDigit("1");
        calculator.inputDigit(".");
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        assertEquals("1.1", calculator.getNumberStack().peek());
        assertEquals("+", calculator.getOperatorStack().peek());

        // decimal first with operator
        calculator.inputDigit(".");
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("4");
        calculator.inputDigit("2");
        assertEquals(".1", calculator.getNumberStack().peek());
        assertEquals("+", calculator.getOperatorStack().peek());
        assertEquals("42", calculator.getCurrentInput());

        calculator.clear();

        // multiple operators
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("4");
        calculator.pushOperator("-");
        calculator.inputDigit("2");
        assertEquals("[1, 4]", calculator.getNumberStack().toString());
        assertEquals("[+, -]", calculator.getOperatorStack().toString());
        assertEquals("2", calculator.getCurrentInput());

        calculator.clear();
        // multiple operators with negatives
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("4");
        calculator.toggleSign();
        calculator.pushOperator("-");
        calculator.inputDigit("2");
        calculator.toggleSign();
        assertEquals("[1, -4]", calculator.getNumberStack().toString());
        assertEquals("[+, -]", calculator.getOperatorStack().toString());
        assertEquals("-2", calculator.getCurrentInput());
    }

    @Test   //specifically tests the method to get a full expression
    public void testGetFullExpression()
    {
        calculator.inputDigit("1");
        calculator.inputDigit("2");
        calculator.pushOperator("+");
        calculator.inputDigit("3");
        calculator.inputDigit("4");
        assertEquals("12 + 34", calculator.getFullExpression());

        calculator.clear(); // clear out the calculator memory so that a new expression can be written
        // (this will normally happen when the equal sign is hit, it will compute the results and get rid of all other stored data)

        // decimal numbers
        calculator.inputDigit("1");
        calculator.inputDigit(".");
        calculator.inputDigit("2");
        calculator.pushOperator("/");
        calculator.inputDigit("3");
        calculator.inputDigit(".");
        calculator.inputDigit("4");
        assertEquals("1.2 / 3.4", calculator.getFullExpression());

        calculator.clear();

        // multiple operators
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("2");
        calculator.pushOperator("-");
        calculator.inputDigit("3");
        calculator.pushOperator("*");
        calculator.inputDigit("4");
        calculator.pushOperator("/");
        calculator.inputDigit("5");
        assertEquals("1 + 2 - 3 * 4 / 5", calculator.getFullExpression());

        calculator.clear();

        // operator first
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        assertEquals("1", calculator.getFullExpression());

        calculator.clear();
        // empty expression
        assertEquals("", calculator.getFullExpression());

        calculator.clear();

        // multiple operators with negatives
        calculator.inputDigit("1");
        calculator.toggleSign();
        calculator.pushOperator("+");
        calculator.inputDigit("2");
        calculator.pushOperator("-");
        calculator.inputDigit("3");
        calculator.toggleSign();
        calculator.pushOperator("*");
        calculator.inputDigit("4");
        calculator.pushOperator("/");
        calculator.inputDigit("5");
        calculator.toggleSign();
        assertEquals("-1 + 2 - -3 * 4 / -5", calculator.getFullExpression());

    }

    @Test   //specifically tests the functionality of each operator implemented
    public void testCalculations() {
        //------------------------------------------------------------------------
        // addition
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        assertEquals("2.0", calculator.evaluateExpression());

        calculator.clear();

        // addition with one negative
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("-1");
        assertEquals("0.0", calculator.evaluateExpression());

        calculator.clear();
        // addition with one negative
        calculator.inputDigit("-1");
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        assertEquals("0.0", calculator.evaluateExpression());

        calculator.clear();

        // addition with two negatives
        calculator.inputDigit("-1");
        calculator.pushOperator("+");
        calculator.inputDigit("-1");
        assertEquals("-2.0", calculator.evaluateExpression());

        calculator.clear();

        //------------------------------------------------------------------------
        // subtraction
        calculator.inputDigit("1");
        calculator.pushOperator("-");
        calculator.inputDigit("1");
        assertEquals("0.0", calculator.evaluateExpression());

        calculator.clear();

        // subtraction with one negative
        calculator.inputDigit("1");
        calculator.pushOperator("-");
        calculator.inputDigit("-1");
        assertEquals("2.0", calculator.evaluateExpression());

        calculator.clear();
        // subtraction with one negative
        calculator.inputDigit("-1");
        calculator.pushOperator("-");
        calculator.inputDigit("1");
        assertEquals("-2.0", calculator.evaluateExpression());

        calculator.clear();

        // subtraction with two negatives
        calculator.inputDigit("-1");
        calculator.pushOperator("-");
        calculator.inputDigit("-1");
        assertEquals("0.0", calculator.evaluateExpression());

        calculator.clear();

        //------------------------------------------------------------------------
        // multiplication
        calculator.inputDigit("1");
        calculator.pushOperator("*");
        calculator.inputDigit("2");
        assertEquals("2.0", calculator.evaluateExpression());

        calculator.clear();

        // multiplication with one negative
        calculator.inputDigit("1");
        calculator.pushOperator("*");
        calculator.inputDigit("-2");
        assertEquals("-2.0", calculator.evaluateExpression());

        calculator.clear();

        // multiplication with one negative
        calculator.inputDigit("-1");
        calculator.pushOperator("*");
        calculator.inputDigit("2");
        assertEquals("-2.0", calculator.evaluateExpression());

        calculator.clear();

        // multiplication with two negatives
        calculator.inputDigit("-1");
        calculator.pushOperator("*");
        calculator.inputDigit("-2");
        assertEquals("2.0", calculator.evaluateExpression());

        calculator.clear();

        //------------------------------------------------------------------------
        // division
        calculator.inputDigit("1");
        calculator.pushOperator("/");
        calculator.inputDigit("2");
        assertEquals("0.5", calculator.evaluateExpression());

        calculator.clear();

        // division with one negative
        calculator.inputDigit("1");
        calculator.pushOperator("/");
        calculator.inputDigit("-2");
        assertEquals("-0.5", calculator.evaluateExpression());

        calculator.clear();

        // division with one negative
        calculator.inputDigit("-1");
        calculator.pushOperator("/");
        calculator.inputDigit("2");
        assertEquals("-0.5", calculator.evaluateExpression());

        calculator.clear();

        // division with two negatives
        calculator.inputDigit("-1");
        calculator.pushOperator("/");
        calculator.inputDigit("-2");
        assertEquals("0.5", calculator.evaluateExpression());

        calculator.clear();

        //------------------------------------------------------------------------
        // division of 0
        calculator.inputDigit("0");
        calculator.pushOperator("/");
        calculator.inputDigit("2");
        assertEquals("0.0", calculator.evaluateExpression());

        calculator.clear();

        // division with one negative
        calculator.inputDigit("0");
        calculator.pushOperator("/");
        calculator.inputDigit("-2");
        assertEquals("0.0", calculator.evaluateExpression());

        calculator.clear();

        //------------------------------------------------------------------------
        // division by 0
        calculator.inputDigit("1");
        calculator.pushOperator("/");
        calculator.inputDigit("0");
        assertEquals("NaN", calculator.evaluateExpression());

        calculator.clear();

        // division with one negative
        calculator.inputDigit("-1");
        calculator.pushOperator("/");
        calculator.inputDigit("0");
        assertEquals("NaN", calculator.evaluateExpression());

        calculator.clear();

        //------------------------------------------------------------------------
        // multiple same priority (left to right eval)
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("2");
        calculator.pushOperator("-");
        calculator.inputDigit("3");
        calculator.pushOperator("+");
        calculator.inputDigit("4");
        assertEquals("4.0", calculator.evaluateExpression());

        calculator.clear();

        // multiple same priority (left to right eval)
        calculator.inputDigit("1");
        calculator.pushOperator("-");
        calculator.inputDigit("2");
        calculator.pushOperator("+");
        calculator.inputDigit("3");
        calculator.pushOperator("-");
        calculator.inputDigit("4");
        assertEquals("-2.0", calculator.evaluateExpression());

        calculator.clear();

        // multiple same priority (left to right eval)
        calculator.inputDigit("1");
        calculator.pushOperator("*");
        calculator.inputDigit("2");
        calculator.pushOperator("/");
        calculator.inputDigit("2");
        calculator.pushOperator("*");
        calculator.inputDigit("4");
        assertEquals("4.0", calculator.evaluateExpression());

        calculator.clear();

        // multiple same priority (left to right eval)
        calculator.inputDigit("1");
        calculator.pushOperator("/");
        calculator.inputDigit("2");
        calculator.pushOperator("*");
        calculator.inputDigit("3");
        calculator.pushOperator("/");
        calculator.inputDigit("1");
        assertEquals("1.5", calculator.evaluateExpression());

        calculator.clear();

        // multiple mixed priority (PEMDAS)
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("2");
        calculator.pushOperator("-");
        calculator.inputDigit("3");
        calculator.pushOperator("*");
        calculator.inputDigit("4");
        assertEquals("-9.0", calculator.evaluateExpression());

        calculator.clear();

        // multiple mixed priority (PEMDAS)
        calculator.inputDigit("12");
        calculator.pushOperator("/");
        calculator.inputDigit("2");
        calculator.pushOperator("-");
        calculator.inputDigit("3");
        calculator.pushOperator("*");
        calculator.inputDigit("2");
        assertEquals("0.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test   //specifically tests the ability to store and either use or ignore a result
    public void testStoredResult() {
        // use the stored result
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        calculator.evaluateExpression();
        calculator.pushOperator("*");
        calculator.inputDigit("2");
        assertEquals("4.0", calculator.evaluateExpression());

        calculator.clear();

        // use the stored result
        calculator.inputDigit("9");
        calculator.pushOperator("+");
        calculator.inputDigit("8");
        calculator.evaluateExpression();
        calculator.inputDigit("2");
        calculator.pushOperator("+");
        calculator.inputDigit("3");
        assertEquals("5.0", calculator.evaluateExpression());

        // everything all at once
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("2");
        calculator.evaluateExpression();
        calculator.pushOperator("*");
        calculator.inputDigit("3");
        calculator.evaluateExpression();
        calculator.inputDigit("9");
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        assertEquals("10.0", calculator.evaluateExpression());
    }
}
