package com.example.calculatorapp;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.EmptyStackException;

public class CalculatorTest {
    private Calculator calculator;

    @Before
    public void setUp()
    {
        Context context = ApplicationProvider.getApplicationContext(); // Get application context
        calculator = new Calculator(context);
    }

    @Test   //specifically tests the functionality of the sumbitNumber method
    public void testSubmitNumber()
    {
        // single digit number
        calculator.inputDigit("1");
        calculator.submitNumber();
        assertEquals("1", calculator.getNumberStack().peek());

    }

    @Test
    public void testSubmitNegs()
    {
        // single digit number, negative
        calculator.pushOperator("-");
        calculator.inputDigit("1");
        calculator.submitNumber();
        assertEquals("-1", calculator.getNumberStack().peek());

    }

    @Test
    public void testSubmitExtraNegs()
    {
        // stacked negatives
        calculator.pushOperator("-");
        calculator.pushOperator("-");
        calculator.inputDigit("1");
        calculator.submitNumber();
        assertEquals("--1", calculator.getNumberStack().peek());

    }

    @Test
    public void testSubmitInput()
    {
        // multi-digit number
        calculator.inputDigit("2");
        calculator.inputDigit("3");
        calculator.submitNumber();
        assertEquals("23", calculator.getNumberStack().peek());

    }

    @Test
    public void testIsEmptyAfterSubmit()
    {
        // check that currentInput is empty after submission
        calculator.inputDigit("4");
        calculator.submitNumber();
        assertEquals("", calculator.getCurrentInput());

        calculator.clear();

    }

    @Test
    public void testSubmitNothing()
    {
        // no input, make sure numberStack stays empty
        calculator.submitNumber();
        assertEquals("[]", calculator.getNumberStack().toString());

    }

    @Test
    public void testLongNumber()
    {
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

    }

    @Test
    public void testNegDecimal()
    {
        // negative decimal
        calculator.pushOperator("-");
        calculator.inputDigit("1");
        calculator.inputDigit(".");
        calculator.inputDigit("1");
        calculator.submitNumber();
        assertEquals("-1.1", calculator.getNumberStack().peek());

    }

    @Test
    public void testDecimalFirst()
    {
        calculator.inputDigit(".");
        calculator.inputDigit("1");
        calculator.submitNumber();
        assertEquals(".1", calculator.getNumberStack().peek());

    }

    @Test
    public void testExtraDecimal()
    {
        calculator.inputDigit("4");
        calculator.inputDigit("5");
        calculator.inputDigit(".");
        calculator.inputDigit("1");
        calculator.inputDigit(".");
        calculator.inputDigit("5");
        calculator.submitNumber();
        assertEquals("45.15", calculator.getNumberStack().peek());

        calculator.clear();
    }

    @Test
    public void testToggleSignForSingleNumber()
    {
        calculator.inputDigit("4");
        calculator.pushFunction("-");
        assertEquals("- ( 4 )", calculator.getFullExpression());
        assertEquals("-4.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testToggleSignForExpression()
    {
        // toggle negative of a whole expression
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("4");
        calculator.pushFunction("-");
        assertEquals("- ( 1 + 4 )", calculator.getFullExpression());
        assertEquals("-5.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testNestedToggleSignForExpression()
    {
        // toggle negative of a whole expression
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("4");
        calculator.pushFunction("-");
        calculator.pushFunction("-");
        assertEquals("- ( - ( 1 + 4 ) )", calculator.getFullExpression());
        assertEquals("5.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testToggleSignWithExpressionAfter()
    {
        // toggle negative of a whole expression
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("4");
        calculator.pushFunction("-");
        calculator.pushOperator("*");
        calculator.inputDigit("3");
        assertEquals("- ( 1 + 4 ) * 3", calculator.getFullExpression());
        assertEquals("-15.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testEmbeddedToggleSignWithExpressionAfter()
    {
        // toggle negative of a whole expression
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("4");
        calculator.pushFunction("-");
        calculator.pushOperator("*");
        calculator.inputDigit("3");
        calculator.pushFunction("-");
        assertEquals("- ( - ( 1 + 4 ) * 3 )", calculator.getFullExpression());
        assertEquals("15.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test   //specifically tests the functionality of the isEmpty method and that an empty value is not pushed
    public void testIsEmpty() {
        calculator.submitNumber();
        assertTrue(calculator.getNumberStack().isEmpty()); // Assuming submitNumber does not push empty values
    }

    @Test   //specifically tests the operators (they submit the values and save the operator)
    public void testSimpleOp()
    {
        // simple operator
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        assertEquals("1", calculator.getNumberStack().peek());
        assertEquals("+", calculator.getOperatorStack().peek());

    }

    @Test
    public void testNegAndOp()
    {
        // simple operator with negative before operator
        calculator.pushOperator("-");
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        assertEquals("-1", calculator.getNumberStack().peek());
        assertEquals("+", calculator.getOperatorStack().peek());

    }

    @Test
    public void testOpOnly()
    {
        // operator only
        calculator.clear();
        calculator.pushOperator("+");
        assertEquals("[]", calculator.getNumberStack().toString());
        try{
            assertEquals("", calculator.getOperatorStack().peek());
        } catch (EmptyStackException e)
        {
            // do nothing about it.
        }

        calculator.clear();
    }

    @Test
    public void testStackedNegs()
    {
        // stacked negatives
        calculator.pushOperator("-");
        calculator.inputDigit("1");
        calculator.pushOperator("-");
        calculator.pushOperator("-");
        calculator.pushOperator("-");
        calculator.inputDigit("1");
        calculator.submitNumber();
        assertEquals("-1 - --1", calculator.getFullExpression());
        assertEquals("-2.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testDecimal()
    {
        // decimal operator
        calculator.inputDigit("1");
        calculator.inputDigit(".");
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        assertEquals("1.1", calculator.getNumberStack().peek());
        assertEquals("+", calculator.getOperatorStack().peek());

    }

    @Test
    public void testDecimaFirstWithOp()
    {
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
    }

    @Test
    public void testMultipleOpsPlusMinus()
    {
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

    }

    @Test
    public void testMinusAsNegAndOp()
    {
        // minus as negative operators
        calculator.pushOperator("-");
        calculator.inputDigit("4");
        assertEquals("-4", calculator.getCurrentInput());
        calculator.pushOperator("-");
        calculator.inputDigit("2");
        calculator.submitNumber();
        assertEquals("[-4, 2]", calculator.getNumberStack().toString());
        assertEquals("[-]", calculator.getOperatorStack().toString());

        calculator.clear();
    }

    @Test
    public void testSubNeg()
    {
        // multiple operators with negatives
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.pushOperator("-");
        calculator.inputDigit("4");
        calculator.pushOperator("-");
        calculator.pushOperator("-");
        calculator.inputDigit("2");
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

        calculator.clear();
    }

    @Test
    public void testDecimals()
    {
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

    }

    @Test
    public void testAllOps()
    {
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

    }

    @Test
    public void testOpFirst()
    {

        // operator first
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        assertEquals("1", calculator.getFullExpression());

        calculator.clear();

    }

    @Test
    public void testEmptyExpression()
    {
        // empty expression
        assertEquals("", calculator.getFullExpression());

        calculator.clear();

    }

    @Test
    public void testMultipleOpsAndNegs()
    {
        // multiple operators with negatives
        calculator.pushOperator("-");
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("2");
        calculator.pushOperator("-");
        calculator.pushOperator("-");
        calculator.inputDigit("3");
        calculator.pushOperator("*");
        calculator.inputDigit("4");
        calculator.pushOperator("/");
        calculator.pushOperator("-");
        calculator.inputDigit("5");
        assertEquals("-1 + 2 - -3 * 4 / -5", calculator.getFullExpression());

    }

    @Test   //specifically tests the functionality of each operator implemented
    public void testAddPos() {
        // addition
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        assertEquals("2.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testAddNegSecond()
    {
        // addition with one negative
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("-1");
        assertEquals("0.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testAddNegFirst()
    {
        // addition with one negative
        calculator.inputDigit("-1");
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        assertEquals("0.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testAddTwoNeg()
    {
        // addition with two negatives
        calculator.inputDigit("-1");
        calculator.pushOperator("+");
        calculator.inputDigit("-1");
        assertEquals("-2.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test   //specifically tests the functionality of each operator implemented
    public void testSubPos() {
        // subtraction
        calculator.inputDigit("1");
        calculator.pushOperator("-");
        calculator.inputDigit("1");
        assertEquals("0.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testSubNegSecond()
    {
        // subtraction with one negative
        calculator.inputDigit("1");
        calculator.pushOperator("-");
        calculator.inputDigit("-1");
        assertEquals("2.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testSubNegFirst()
    {
        // subtraction with one negative
        calculator.inputDigit("-1");
        calculator.pushOperator("-");
        calculator.inputDigit("1");
        assertEquals("-2.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testSubTwoNeg()
    {
        // subtraction with two negatives
        calculator.inputDigit("-1");
        calculator.pushOperator("-");
        calculator.inputDigit("-1");
        assertEquals("0.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testMultPos() {
        calculator.inputDigit("1");
        calculator.pushOperator("*");
        calculator.inputDigit("2");
        assertEquals("2.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testMultNegSecond()
    {
        // multiplication with one negative
        calculator.inputDigit("1");
        calculator.pushOperator("*");
        calculator.inputDigit("-2");
        assertEquals("-2.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testMultNegFirst()
    {
        // multiplication with one negative
        calculator.inputDigit("-1");
        calculator.pushOperator("*");
        calculator.inputDigit("2");
        assertEquals("-2.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testMultTwoNeg()
    {
        // multiplication with two negatives
        calculator.inputDigit("-1");
        calculator.pushOperator("*");
        calculator.inputDigit("-2");
        assertEquals("2.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test   //specifically tests the functionality of each operator implemented
    public void testDivPos() {
        // division
        calculator.inputDigit("1");
        calculator.pushOperator("/");
        calculator.inputDigit("2");
        assertEquals("0.5", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testDivWithNegBot()
    {
        // division with one negative
        calculator.inputDigit("1");
        calculator.pushOperator("/");
        calculator.inputDigit("-2");
        assertEquals("-0.5", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testDivWithNegTop()
    {
        // division with one negative
        calculator.inputDigit("-1");
        calculator.pushOperator("/");
        calculator.inputDigit("2");
        assertEquals("-0.5", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testDivWithTwoNegs()
    {
        // division with two negatives
        calculator.inputDigit("-1");
        calculator.pushOperator("/");
        calculator.inputDigit("-2");
        assertEquals("0.5", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testDivisionOfZeroPos() {
        // division of 0
        calculator.inputDigit("0");
        calculator.pushOperator("/");
        calculator.inputDigit("2");
        assertEquals("0.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testDivOfZeroNeg()
    {
        // division with one negative
        calculator.inputDigit("0");
        calculator.pushOperator("/");
        calculator.inputDigit("-2");
        assertEquals("0.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test   //specifically tests the functionality of each operator implemented
    public void testDivisionByZeroPositive() {
        // division by 0
        calculator.inputDigit("1");
        calculator.pushOperator("/");
        calculator.inputDigit("0");
        assertEquals("NaN", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testDivByZeroNegative()
    {
        // division with one negative
        calculator.inputDigit("-1");
        calculator.pushOperator("/");
        calculator.inputDigit("0");
        assertEquals("NaN", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test   //specifically tests the functionality of each operator implemented
    public void testMultipleSamePriorPlus() {
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

    }

    @Test
    public void testMultipleSamePriorSub()
    {
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

    }

    @Test
    public void testMultipleSamePriorMult()
    {
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

    }

    @Test
    public void testMultipleSamePriorDiv()
    {
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

    }

    @Test
    public void testMixedPriority()
    {
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

    }

    @Test
    public void testMixedPriorityAgain()
    {
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

    @Test   //specifically tests the functionality of each operator implemented
    public void testSimplePercent() {
        // simple percent
        calculator.inputDigit("10");
        calculator.pushOperator("%");
        assertEquals("0.1", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testPercentWithPrecedingPlus()
    {
        // percent with preceding operation (+)
        calculator.inputDigit("2");
        calculator.pushOperator("+");
        calculator.inputDigit("10");
        calculator.pushOperator("%");
        assertEquals("[2, 10]", calculator.getNumberStack().toString());
        assertEquals("[+, %]", calculator.getOperatorStack().toString());
        assertEquals("2.2", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testPercentProcedingOp()
    {
        // percent with operation after (+)   - test failed, figure out why
        calculator.inputDigit("10");
        calculator.pushOperator("%");
        calculator.pushOperator("+");
        calculator.inputDigit("2");
        assertEquals("2.1", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testPercentWithPrecedingMult()
    {
        // percent with preceding operation (*)
        calculator.inputDigit("2");
        calculator.pushOperator("*");
        calculator.inputDigit("10");
        calculator.pushOperator("%");
        assertEquals("0.2", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test   //specifically tests the functionality of each operator implemented
    public void testParenthesis() {
        // parenthesis
        calculator.pushOperator("(");
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        calculator.pushOperator(")");
        assertEquals("( 1 + 1 + 1 )", calculator.getFullExpression());
        assertEquals("3.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test   //specifically tests the functionality of each operator implemented
    public void testExtraLeftParen() {
        // parenthesis
        calculator.pushOperator("(");
        calculator.pushOperator("(");
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        calculator.pushOperator(")");
        assertEquals("NaN", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test   //specifically tests the functionality of each operator implemented
    public void testExtraRightParen() {
        // parenthesis
        calculator.pushOperator("(");
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        calculator.pushOperator(")");
        calculator.pushOperator(")");
        assertEquals("NaN", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testParenthesesAtEnd()
    {
        // parenthesis after an expression
        calculator.inputDigit("2");
        calculator.pushOperator("*");
        calculator.pushOperator("(");
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        calculator.pushOperator(")");
        assertEquals("4.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testParenthesesAtStart()
    {
        // parenthesis before an expression
        calculator.pushOperator("(");
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        calculator.pushOperator(")");
        calculator.pushOperator("*");
        calculator.inputDigit("2");
        assertEquals("4.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testParenthesesInMiddle()
    {
        // parenthesis between expressions
        calculator.inputDigit("2");
        calculator.pushOperator("*");
        calculator.pushOperator("(");
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        calculator.pushOperator(")");
        calculator.pushOperator("+");
        calculator.inputDigit("2");
        assertEquals("6.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testNestedParentheses()
    {
        // nested parenthesis
        calculator.inputDigit("2");
        calculator.pushOperator("*");
        calculator.pushOperator("(");
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.pushOperator("(");
        calculator.inputDigit("2");
        calculator.pushOperator("+");
        calculator.inputDigit("2");
        calculator.pushOperator(")");
        calculator.pushOperator("*");
        calculator.inputDigit("2");
        calculator.pushOperator(")");
        calculator.pushOperator("+");
        calculator.inputDigit("2");
        assertEquals("20.0", calculator.evaluateExpression());
    }


    @Test   //specifically tests the delete functionality
    public void testDeleteANum() {
        // delete a num
        calculator.inputDigit("1");
        calculator.inputDigit("1");
        assertEquals("11", calculator.getCurrentInput());
        calculator.delete();
        assertEquals("1", calculator.getCurrentInput());

        calculator.clear();

    }

    @Test
    public void testDeleteAnOp()
    {
        // delete an operator
        calculator.inputDigit("1");
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.pushOperator("/");
        assertEquals("[/]", calculator.getOperatorStack().toString());
        calculator.delete();
        assertEquals("[]", calculator.getOperatorStack().toString());

        calculator.clear();

    }

    @Test
    public void testDeletePercent()
    {
        // deleting the percent sign
        calculator.inputDigit("1");
        calculator.pushOperator("%");
        assertEquals("1 %", calculator.getFullExpression());
        calculator.delete();
        assertEquals("1", calculator.getFullExpression());

        calculator.clear();

    }

    @Test
    public void testMultipleDeletions()
    {
        // multiple deletes
        calculator.inputDigit("1");
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("2");
        assertEquals("11 + 2", calculator.getFullExpression());
        calculator.delete();
        calculator.delete();
        calculator.delete();
        calculator.delete();
        assertEquals("", calculator.getFullExpression());

        calculator.clear();

    }

    @Test
    public void testInputAfterDeletion()
    {
        // continue typing after a deletion
        calculator.inputDigit("1");
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("2");
        assertEquals("11 + 2", calculator.getFullExpression());
        calculator.delete();
        calculator.delete();
        calculator.pushOperator("/");
        calculator.inputDigit("2");
        assertEquals("11 / 2", calculator.getFullExpression());

        calculator.clear();

    }

    @Test
    public void testDeleteParenthesis()
    {
        calculator.inputDigit("2");
        calculator.pushOperator("*");
        calculator.pushOperator("(");
        calculator.pushOperator("(");
        calculator.delete();
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.pushOperator("(");
        calculator.inputDigit("2");
        calculator.pushOperator("+");
        calculator.inputDigit("2");
        calculator.pushOperator(")");
        calculator.pushOperator("*");
        calculator.inputDigit("2");
        calculator.pushOperator(")");
        calculator.pushOperator(")");
        calculator.delete();
        calculator.pushOperator("+");
        calculator.inputDigit("2");
        assertEquals("20.0", calculator.evaluateExpression());
    }

    @Test
    public void testUseStoredResult() {
        // use the stored result
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        calculator.evaluateExpression();
        calculator.pushOperator("*");
        calculator.inputDigit("2");
        assertEquals("4.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testIgnoreStoredResult()
    {
        calculator.inputDigit("9");
        calculator.pushOperator("+");
        calculator.inputDigit("8");
        calculator.evaluateExpression();
        calculator.inputDigit("2");
        calculator.pushOperator("+");
        calculator.inputDigit("3");
        assertEquals("5.0", calculator.evaluateExpression());

        calculator.clear();

    }

    @Test
    public void testMultipleOperators()
    {
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

//    @Test
//    public void testMemSave_WithLastResult() {
//        calculator.lastResult = 10; // Set last result
//        calculator.hasResult = true; // Indicate there's a valid result
//        calculator.memSave(); // Save it
//        assertEquals("10.0", calculator.memVar); // Verify memVar holds the result
//    }

//    @Test
//    public void testMemSave_WithCurrentInput() {
//        calculator.currentInput.append("5"); // Simulate user input
//        calculator.hasResult = false; // Indicate there's no valid result
//        calculator.memSave(); // Save current input
//        assertEquals("5.0", calculator.memVar); // Verify memVar holds the current input
//    }

//    @Test
//    public void testMemSave_NoActionWhenNoResultOrInput() {
//        calculator.hasResult = false; // No result
//        calculator.currentInput.setLength(0); // Clear current input
//        String originalMemVar = calculator.memVar;
//        calculator.memSave(); // Attempt to save
//        assertEquals(originalMemVar, calculator.memVar); // Verify memVar is unchanged
//    }

    @Test
    public void testMemRecall_NonEmptyMemory() {
        calculator.memVar = "5"; // Set a value in memory
        calculator.memRecall(); // Recall it
        assertEquals("5", calculator.currentInput.toString()); // Verify currentInput is updated
    }

    @Test
    public void testMemRecall_EmptyMemory() {
        calculator.memVar = ""; // Clear memory
        calculator.memRecall(); // Recall
        assertEquals("", calculator.currentInput.toString()); // Verify currentInput remains unchanged
    }

//    @Test
//    public void testMemAdd_NonEmptyMemory() {
//        calculator.memVar = "5"; // Set memory
//        calculator.lastResult = 3; // Set last result
//        calculator.hasResult = true; // Indicate there's a valid result
//        calculator.memAdd(); // Add to memory
//        assertEquals("8.0", calculator.memVar); // Verify memVar is updated
//    }

    @Test
    public void testMemAdd_EmptyMemory() {
        calculator.memVar = ""; // Clear memory
        calculator.lastResult = 3; // Set last result
        calculator.hasResult = true; // Indicate there's a valid result
        calculator.memAdd(); // Attempt to add
        assertEquals("", calculator.memVar); // Verify memVar remains unchanged
    }

//    @Test
//    public void testMemSub_NonEmptyMemory() {
//        calculator.memVar = "5"; // Set memory
//        calculator.lastResult = 2; // Set last result
//        calculator.hasResult = true; // Indicate there's a valid result
//        calculator.memSub(); // Subtract from memory
//        assertEquals("3.0", calculator.memVar); // Verify memVar is updated
//    }

    @Test
    public void testMemSub_EmptyMemory() {
        calculator.memVar = ""; // Clear memory
        calculator.lastResult = 2; // Set last result
        calculator.hasResult = true; // Indicate there's a valid result
        calculator.memSub(); // Attempt to subtract
        assertEquals("", calculator.memVar); // Verify memVar remains unchanged
    }

//    @Test
//    public void testMemClear_NonEmptyMemory() {
//        calculator.memVar = "5"; // Set memory
//        calculator.memClear(); // Clear memory
//        assertEquals("", calculator.memVar); // Verify memVar is empty
//    }
//
//    @Test
//    public void testMemClear_AlreadyEmpty() {
//        calculator.memVar = ""; // Ensure memory is empty
//        String originalMemVar = calculator.memVar;
//        calculator.memClear(); // Attempt to clear
//        assertEquals(originalMemVar, calculator.memVar); // Verify memVar remains unchanged
//    }

    @Test
    public void testE() {
        calculator.pushE();
        assertEquals(String.valueOf(Math.E), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testEWithCI() {
        calculator.inputDigit("5");
        calculator.pushE();
        calculator.delete();
        calculator.delete();
        assertEquals("5.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testPI() {
        calculator.pushPi();
        assertEquals(String.valueOf(Math.PI), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testPIWithCI() {
        calculator.inputDigit("5");
        calculator.pushPi();
        assertEquals(String.valueOf(5 * Math.PI), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testDeletePi() {
        calculator.inputDigit("5");
        calculator.pushPi();
        calculator.delete();
        calculator.delete();
        assertEquals("5.0", calculator.evaluateExpression());

        calculator.clear();
    }

    // cant really test rand cause there is no way to know what number was chosen

    @Test
    public void testSquaredSignForSingleNumber()
    {
        calculator.inputDigit("4");
        calculator.pushFunction("square");
        assertEquals("square ( 4 )", calculator.getFullExpression());
        assertEquals("16.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testCubedSignForSingleNumber()
    {
        calculator.inputDigit("4");
        calculator.pushFunction("cube");
        assertEquals("cube ( 4 )", calculator.getFullExpression());
        assertEquals("64.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testERaisedXSignForSingleNumber()
    {
        calculator.inputDigit("4");
        calculator.pushFunction("e^");
        assertEquals("e^ ( 4 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.pow(Math.E, 4)), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void test10RaisedXSignForSingleNumber()
    {
        calculator.inputDigit("4");
        calculator.pushFunction("10^");
        assertEquals("10^ ( 4 )", calculator.getFullExpression());
        assertEquals("10000.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testSinSignForSingleNumber()
    {
        calculator.inputDigit("4");
        calculator.pushFunction("sin");
        assertEquals("sin ( 4 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.sin(Math.toRadians(4))), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testSinhSignForSingleNumber()
    {
        calculator.inputDigit("4");
        calculator.pushFunction("sinh");
        assertEquals("sinh ( 4 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.sinh(Math.toRadians(4))), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testCosSignForSingleNumber()
    {
        calculator.inputDigit("4");
        calculator.pushFunction("cos");
        assertEquals("cos ( 4 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.cos(Math.toRadians(4))), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testCoshSignForSingleNumber()
    {
        calculator.inputDigit("4");
        calculator.pushFunction("cosh");
        assertEquals("cosh ( 4 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.cosh(Math.toRadians(4))), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testTanSignForSingleNumber()
    {
        calculator.inputDigit("4");
        calculator.pushFunction("tan");
        assertEquals("tan ( 4 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.tan(Math.toRadians(4))), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testTanhSignForSingleNumber()
    {
        calculator.inputDigit("4");
        calculator.pushFunction("tanh");
        assertEquals("tanh ( 4 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.tanh(Math.toRadians(4))), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testSqrtSignForSingleNumber()
    {
        calculator.inputDigit("4");
        calculator.pushFunction("sqrt");
        assertEquals("sqrt ( 4 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.sqrt(4)), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testCbrtSignForSingleNumber()
    {
        calculator.inputDigit("4");
        calculator.pushFunction("cbrt");
        assertEquals("cbrt ( 4 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.cbrt(4)), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testLogSignForSingleNumber()
    {
        calculator.inputDigit("4");
        calculator.pushFunction("log");
        assertEquals("log ( 4 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.log10(4)), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testLnSignForSingleNumber()
    {
        calculator.inputDigit("4");
        calculator.pushFunction("ln");
        assertEquals("ln ( 4 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.log(4)), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testInverseSignForSingleNumber()
    {
        calculator.inputDigit("4");
        calculator.inverse();
        assertEquals("1 / 4", calculator.getFullExpression());
        assertEquals("0.25", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testSquaredSignForResult()
    {
        calculator.lastResult = 4;
        calculator.hasResult = true;
        calculator.pushFunction("square");
        assertEquals("square ( 4.0 )", calculator.getFullExpression());
        assertEquals("16.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testCubedSignForResult()
    {
        calculator.lastResult = 4;
        calculator.hasResult = true;
        calculator.pushFunction("cube");
        assertEquals("cube ( 4.0 )", calculator.getFullExpression());
        assertEquals("64.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testERaisedXSignForResult()
    {
        calculator.lastResult = 4;
        calculator.hasResult = true;
        calculator.pushFunction("e^");
        assertEquals("e^ ( 4.0 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.pow(Math.E, 4)), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void test10RaisedXSignForResult()
    {
        calculator.lastResult = 4;
        calculator.hasResult = true;
        calculator.pushFunction("10^");
        assertEquals("10^ ( 4.0 )", calculator.getFullExpression());
        assertEquals("10000.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testSinSignForResult()
    {
        calculator.lastResult = 4;
        calculator.hasResult = true;
        calculator.pushFunction("sin");
        assertEquals("sin ( 4.0 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.sin(Math.toRadians(4))), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testSinhSignForResult()
    {
        calculator.lastResult = 4;
        calculator.hasResult = true;
        calculator.pushFunction("sinh");
        assertEquals("sinh ( 4.0 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.sinh(Math.toRadians(4))), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testCosSignForResult()
    {
        calculator.lastResult = 4;
        calculator.hasResult = true;
        calculator.pushFunction("cos");
        assertEquals("cos ( 4.0 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.cos(Math.toRadians(4))), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testCoshSignForResult()
    {
        calculator.lastResult = 4;
        calculator.hasResult = true;
        calculator.pushFunction("cosh");
        assertEquals("cosh ( 4.0 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.cosh(Math.toRadians(4))), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testTanSignForResult()
    {
        calculator.lastResult = 4;
        calculator.hasResult = true;
        calculator.pushFunction("tan");
        assertEquals("tan ( 4.0 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.tan(Math.toRadians(4))), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testTanhSignForResult()
    {
        calculator.lastResult = 4;
        calculator.hasResult = true;
        calculator.pushFunction("tanh");
        assertEquals("tanh ( 4.0 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.tanh(Math.toRadians(4))), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testSqrtSignForResult()
    {
        calculator.lastResult = 4;
        calculator.hasResult = true;
        calculator.pushFunction("sqrt");
        assertEquals("sqrt ( 4.0 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.sqrt(4)), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testCbrtSignForResult()
    {
        calculator.lastResult = 4;
        calculator.hasResult = true;
        calculator.pushFunction("cbrt");
        assertEquals("cbrt ( 4.0 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.cbrt(4)), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testLogSignForResult()
    {
        calculator.lastResult = 4;
        calculator.hasResult = true;
        calculator.pushFunction("log");
        assertEquals("log ( 4.0 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.log10(4)), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testLnSignForResult()
    {
        calculator.lastResult = 4;
        calculator.hasResult = true;
        calculator.pushFunction("ln");
        assertEquals("ln ( 4.0 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.log(4)), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testInverseSignForResult()
    {
        calculator.lastResult = 4;
        calculator.hasResult = true;
        calculator.inverse();
        assertEquals("1 / 4.0", calculator.getFullExpression());
        assertEquals("0.25", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testNestedFunctions()
    {
        calculator.inputDigit("4");
        calculator.pushFunction("ln");
        calculator.pushFunction("square");
        calculator.pushFunction("cos");
        assertEquals("cos ( square ( ln ( 4 ) ) )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.cos(Math.toRadians(Math.pow(Math.log(4), 2)))), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testFunctionBeforeOp()
    {
        calculator.inputDigit("4");
        calculator.pushFunction("ln");
        calculator.pushOperator("*");
        calculator.inputDigit("10");
        assertEquals("ln ( 4 ) * 10", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.log(4) * 10), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testInverseBeforeOp()
    {
        calculator.inputDigit("4");
        calculator.inverse();
        calculator.pushOperator("*");
        calculator.inputDigit("10");
        assertEquals("1 / 4 * 10", calculator.getFullExpression());
        assertEquals("2.5", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testFunctionWithOp()
    {
        calculator.inputDigit("4");
        calculator.pushOperator("*");
        calculator.inputDigit("10");
        calculator.pushFunction("ln");
        assertEquals("ln ( 4 * 10 )", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.log(4 * 10)), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testInverseAfterOpPlus()
    {
        calculator.inputDigit("4");
        calculator.pushOperator("+");
        calculator.inputDigit("10");
        calculator.inverse();
        assertEquals("4 + 1 / 10", calculator.getFullExpression());
        assertEquals("4.1", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testInverseAfterOpMult()
    {
        calculator.inputDigit("4");
        calculator.pushOperator("*");
        calculator.inputDigit("10");
        calculator.inverse();
        assertEquals("4 * 1 / 10", calculator.getFullExpression());
        assertEquals("0.4", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testSqrtInvalidInput()
    {
        calculator.pushOperator("-");
        calculator.inputDigit("4");
        calculator.pushFunction("sqrt");
        assertEquals("sqrt ( -4 )", calculator.getFullExpression());
        assertEquals("NaN", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testFactorialForSingleNumber()
    {
        calculator.inputDigit("4");
        calculator.pushOperator("!");
        assertEquals("4 !", calculator.getFullExpression());
        assertEquals("24.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testFactorialForInvalidInputNeg()
    {
        calculator.inputDigit("-4");
        calculator.pushOperator("!");
        assertEquals("-4 !", calculator.getFullExpression());
        assertEquals("NaN", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testFactorialForInvalidInputDecimal()
    {
        calculator.inputDigit("4");
        calculator.inputDigit(".");
        calculator.inputDigit("4");
        calculator.pushOperator("!");
        assertEquals("4.4 !", calculator.getFullExpression());
        assertEquals("NaN", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testFactorialAfterOpPlus()
    {
        calculator.inputDigit("1");
        calculator.pushOperator("+");
        calculator.inputDigit("4");
        calculator.pushOperator("!");
        assertEquals("1 + 4 !", calculator.getFullExpression());
        assertEquals("25.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testFactorialAfterOpMult()
    {
        calculator.inputDigit("2");
        calculator.pushOperator("*");
        calculator.inputDigit("4");
        calculator.pushOperator("!");
        assertEquals("2 * 4 !", calculator.getFullExpression());
        assertEquals("48.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testFactorialBeforeOpPlus()
    {
        calculator.inputDigit("4");
        calculator.pushOperator("!");
        calculator.pushOperator("+");
        calculator.inputDigit("1");
        assertEquals("4 ! + 1", calculator.getFullExpression());
        assertEquals("25.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testFactorialBeforeOpMult()
    {
        calculator.inputDigit("4");
        calculator.pushOperator("!");
        calculator.pushOperator("*");
        calculator.inputDigit("2");
        assertEquals("4 ! * 2", calculator.getFullExpression());
        assertEquals("48.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testXRaisedYSimple()
    {
        calculator.inputDigit("2");
        calculator.pushOperator("^");
        calculator.inputDigit("4");
        assertEquals("2 ^ 4", calculator.getFullExpression());
        assertEquals("16.0", calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testXRaisedYDecimal()
    {
        calculator.inputDigit("2.2");
        calculator.pushOperator("^");
        calculator.inputDigit("4.2");
        assertEquals("2.2 ^ 4.2", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.pow(2.2, 4.2)), calculator.evaluateExpression());

        calculator.clear();
    }

    @Test
    public void testXRaisedYNegative()
    {
        calculator.pushOperator("-");
        calculator.inputDigit("2");
        calculator.pushOperator("^");
        calculator.pushOperator("-");
        calculator.inputDigit("4");
        assertEquals("-2 ^ -4", calculator.getFullExpression());
        assertEquals(String.valueOf(Math.pow(-2, -4)), calculator.evaluateExpression());

        calculator.clear();
    }




}
