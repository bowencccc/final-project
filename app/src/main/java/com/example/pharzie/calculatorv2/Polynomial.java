package com.example.pharzie.calculatorv2;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

/**
 * A polynomial.
 */
public class Polynomial {
    /**
     * The complete expression of the polynomial, not simplified.
     */
    protected String fullExpr;
    /**
     * The list of split expression of the polynomial.
     */
    protected ArrayList<String> expression;
    /**
     * The list of types.
     */
    protected ArrayList<String> type;
    /**
     * All the variables in the expression. Empty if there is no variable.
     */
    protected ArrayList<String> variables;
    /**
     * The set of valid characters.
     */
    private final static String VALID_CHARS = "\\d\\[a-z]\\[A-Z]+-*/%^!()";
    /**
     * The first list of the whole list.
     */
    protected final static int VALUE_LIST = 0;
    /**
     * The second list of the whole list.
     */
    protected final static int TYPE_LIST = 1;
    /**
     * Empty constructor.
     */
    public Polynomial() {
    }
    /**
     * Constructor with a String. Set the expression.
     * @param expr The expression.
     * @throws Exception In case the expression is not valid.
     */
    public Polynomial(final String expr) throws Exception {
        this.fullExpr = expr;
        this.clearSpace();
        expression = splitTerms(fullExpr).get(VALUE_LIST);
        type = splitTerms(fullExpr).get(TYPE_LIST);
        variables = new ArrayList<>();
        for (String str: expression) {
            if (isValidVariable(str) && !variables.contains(str)) {
                variables.add(str);
            }
        }
        if (!isValid()) {
            throw new Exception("Invalid Expression");
        }
    }
    public void clearSpace() {
        StringBuilder nonSpace = new StringBuilder();
        for (char c: fullExpr.toCharArray()) {
            if (c != ' ') {
                nonSpace.append(c);
            }
        }
        fullExpr = nonSpace.toString();
    }
    /**
     * Factor the polynomial.
     */
    public void factor() {

    }
    /**
     * Get all the variables.
     * @return The list of variables.
     */
    public ArrayList<String> getVariables() {
        return variables;
    }

    /**
     * Find out whether the expression is valid.
     * @return TURE if is.
     */
    private boolean isValid() {
        if (expression.size() == 0) {
            return false;
        }
        /**
         * Make sure the variables are all valid.
         */
        /*
        for (String str: getVariables()) {
            if (!VALID_CHARS.contains(str)) {
                return false;
            }
        }
        */
        //These operators cannot be placed first.
        if (Operator.Constants.POST_SET.contains(expression.get(0))) {
            return false;
        }
        //These operators cannot be placed right after a '(' bracket.
        for (int i = 0; i < expression.size(); i++) {
            if (expression.get(i).equals("(")) {
                if (Operator.Constants.PRE_SET.contains(expression.get(i + 1))) {
                    return false;
                }
            }
        }
        //These operators cannot be followed by a ')' bracket.
        for (int i = 0; i < expression.size(); i++) {
            if (Operator.Constants.PRE_SET.contains(expression.get(i))) {
                if (i + 1 >= expression.size() || expression.get(i + 1).equals(")")) {
                    return false;
                }
            }
        }
        //These operators must be followed by a '(' bracket.
        for (int i = 0; i < expression.size(); i++) {
            if (Operator.Constants.FUNCTION_SET.contains(expression.get(i))) {
                if (i + 1 >= expression.size() || !expression.get(i + 1).equals("(")) {
                    return false;
                }
            }
        }
        //These operators cannot be put continually.
        for (int i = 0; i < expression.size(); i++) {
            if (Operator.Constants.LONE_SET.contains(expression.get(i))) {
                if (i + 1 < expression.size() && Operator.Constants.LONE_SET.contains(expression.get(i + 1))) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * Decide whether a character can be a variable.
     * @param str The potential character.
     * @return TRUE if can.
     */
    private static boolean isValidVariable(final String str) {
        if (str.length() > 1) {
            return false;
        }
        char c = str.charAt(0);
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }
    public Polynomial plugIn(String variable, double value) throws Exception {
        StringBuilder newExpr = new StringBuilder("");
        for (int i = 0; i < expression.size(); i++) {
            if (type.get(i).equals("Variable") && expression.get(i).equals(variable)) {
                newExpr.append(value);
                continue;
            }
            newExpr.append(expression.get(i));
        }
        return new Polynomial(newExpr.toString());
    }
    /**
     * Simplify the polynomial.
     */
    public void simplify() {

    }
    /**
     * Split the full expression as a string to a list of numbers, variables and operators.
     * @param expr The original expression.
     * @return A list equal to the original expression.
     */
    protected static ArrayList<ArrayList<String>> splitTerms(final String expr) {
        if (expr == null) {
            return null;
        }
        ArrayList<ArrayList<String>> expression = new ArrayList<>();
        //First for the value.
        expression.add(new ArrayList<String>());
        //Second for the type.
        expression.add(new ArrayList<String>());
        int bracketCount = 0;
        for (int i = 0; i < expr.length(); i++) {
            int last = expression.get(VALUE_LIST).size() - 1;
            /**
             * Symbols are added directly.+-*\%^()!
             */
            if (Operator.Constants.BRACKET_SET.contains(expr.substring(i, i + 1))) {
                if (expr.charAt(i) == '(') {
                    //Add the count.
                    bracketCount++;
                    //Add the * if it's omitted in the original expression.
                    if (i > 0 && (expression.get(TYPE_LIST).get(last).equals("Number")
                            || expression.get(TYPE_LIST).get(last).equals("Variable")
                            || expression.get(TYPE_LIST).get(last).equals("RightParenthesis"))) {
                        expression.get(VALUE_LIST).add("*");
                        expression.get(TYPE_LIST).add("Operator");
                    }
                    expression.get(TYPE_LIST).add("LeftParenthesis");
                } else if (expr.charAt(i) == ')') {
                    bracketCount--;
                    expression.get(TYPE_LIST).add("RightParenthesis");
                }
                expression.get(VALUE_LIST).add(expr.substring(i, i + 1));
            } else if (Operator.Constants.SYMBOL_SET.contains(expr.substring(i, i + 1))) {
                //If it's a negative symbol, add a '0' before to make it a subtraction symbol.
                if (expr.charAt(i) == '-') {
                    if (i == 0 || expression.get(TYPE_LIST).get(last).equals("LeftParenthesis")) {
                        expression.get(VALUE_LIST).add("0");
                        expression.get(TYPE_LIST).add("Number");
                    }
                }
                expression.get(VALUE_LIST).add(expr.substring(i, i + 1));
                expression.get(TYPE_LIST).add("Operator");
                /**
                 * Numbers are added as a string.
                 */
            } else if (Number.Constants.NUMBER_SET.contains(expr.substring(i, i + 1))) {
                String number = expr.substring(i, i + 1);
                for (int j = i + 1; j < expr.length(); j++) {
                    if (Number.Constants.NUMBER_SET.contains(expr.substring(j, j + 1))) {
                        number += expr.substring(j, j + 1);
                    } else {
                        break;
                    }
                }
                i += number.length() - 1;
                expression.get(VALUE_LIST).add(number);
                expression.get(TYPE_LIST).add("Number");
                /**
                 * Variables (or functions) are added carefully. Missing * are added.
                 */
            } else {
                //Add the * if it's omitted in the original expression.
                if (i > 0 && (expression.get(TYPE_LIST).get(last).equals("Number")
                        || expression.get(TYPE_LIST).get(last).equals("Variable")
                        || expression.get(TYPE_LIST).get(last).equals("RightParenthesis"))) {
                    expression.get(VALUE_LIST).add("*");
                    expression.get(TYPE_LIST).add("Operator");
                }
                if (i + 1 < expr.length()) {
                    if (expr.substring(i, i + 2).equals("pi")) {
                        expression.get(VALUE_LIST).add(Number.Constants.PI.toString());
                        expression.get(TYPE_LIST).add("Number");
                        i = i + 1;
                    } else if (expr.substring(i, i + 2).equals("ln")) {
                        expression.get(VALUE_LIST).add("ln");
                        expression.get(TYPE_LIST).add("Function");
                        i = i + 1;
                    }
                }
                if (i + 2 < expr.length()) {
                    String sub = expr.substring(i, i + 3);
                    if (sub.equals("sin") || sub.equals("cos") || sub.equals("tan") || sub.equals("log")) {
                        expression.get(VALUE_LIST).add(sub);
                        expression.get(TYPE_LIST).add("Function");
                        i = i + 2;
                        continue;
                    }
                }
                if (i + 3 < expr.length()) {
                    String sub = expr.substring(i, i + 4);
                    if (sub.equals("asin") || sub.equals("acos") || sub.equals("atan") || sub.equals("sqrt")) {
                        expression.get(VALUE_LIST).add(sub);
                        expression.get(TYPE_LIST).add("Function");
                        i = i + 3;
                        continue;
                    }
                }
                expression.get(VALUE_LIST).add(expr.substring(i, i + 1));
                expression.get(TYPE_LIST).add("Variable");
            }
        }
        while (bracketCount > 0) {
            expression.get(VALUE_LIST).add(")");
            expression.get(TYPE_LIST).add("RightParenthesis");
            bracketCount--;
        }
        return expression;
    }
    /**
     * Return the standardized expression as a string.
     * @return
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (String str: expression) {
            s.append(str);
        }
        return s.toString();
    }
}

