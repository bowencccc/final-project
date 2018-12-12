package com.example.pharzie.calculatorv2;

import java.util.List;

/**
 * A tool class to operate numbers.
 */
public class Operator {
    /**
     * Get the result of a certain step of calculation.
     * @param str1 The first number as a string.
     * @param str2 The second number as a string.
     * @param operator The operator.
     * @return The answer of this opertion as a Number object.
     * @throws Exception In case the operator isn't identified.
     */
    public static String calculate(final String str1, final String str2, final String operator) throws Exception {
        if (str1 == null || str2 == null) {
            return new Number(-1, "Null numbers").toString();
        }
        Number n1 = new Number(str1);
        Number n2 = new Number(str2);
        switch (operator) {
            case "+": return plus(n1, n2).toString();
            case "-": return plus(n1, new Number(-n2.getReal(), -n2.getImag())).toString();
            case "*": return multiply(n1, n2).toString();
            case "/": return divide(n1, n2).toString();
            case "%": return mod(n1, n2).toString();
            case "^": return power(n1, n2).toString();
            case "log": return log(n1, n2).toString();
            default: break;
        }
        throw new Exception("Illegal Operator");
    }
    /**
     * Get the result of a certain step of calculation.
     * @param str The number to be operated as a string.
     * @param operator The operator.
     * @return The answer of this operation as a Number object.
     * @throws Exception In case the operator isn"t identified.
     */
    public static String calculate(final String str, final String operator) throws Exception {
        if (str == null) {
            return new Number(-1, "Null numbers").toString();
        }
        Number n = new Number(str);
        switch (operator) {
            case "!": return factorial(n).toString();
            case "sin": return sin(n).toString();
            case "cos": return cos(n).toString();
            case "tan": return tan(n).toString();
            case "sqrt": return sqrt(n).toString();
            case "arcsin": return asin(n).toString();
            case "arccos": return acos(n).toString();
            case "arctan": return atan(n).toString();
            default: break;
        }
        throw new Exception("Illegal Operator.");
    }

    /**
     * The arccos() function.
     * @param n The number to be operated.
     * @return The arccosine of the number.
     */
    public static Number acos(final Number n) {
        if (!n.isReal()) {
            return new Number(-1, "Unreal parameter in trig operation.");
        } else if (n.getAbsValue() > Number.Constants.MAX_X_ATRIG) {
            return new Number(-1, "Invalid parameter for arccos()");
        }
        return new Number(Math.acos(n.getReal()));
    }

    /**
     * The arcsin() function.
     * @param n The number to be operated.
     * @return The arcsine of the number.
     */
    public static Number asin(final Number n) {
        if (!n.isReal()) {
            return new Number(-1, "Unreal parameter in trig operation.");
        } else if (n.getAbsValue() > Number.Constants.MAX_X_ATRIG) {
            return new Number(-1, "Invalid parameter for arccos()");
        }
        return new Number(Math.asin(n.getReal()));
    }

    /**
     * The arctan() function.
     * @param n The number to be operated.
     * @return The arctangent of the number.
     */
    public static Number atan(final Number n) {
        if (!n.isReal()) {
            return new Number(-1, "Unreal parameter in tirg operation.");
        }
        return new Number(Math.atan(n.getReal()));
    }

    /**
     * The cos() function.
     * @param n The number to be operated.
     * @return The cosine of the number.
     */
    public static Number cos(final Number n) {
        if (!n.isReal()) {
            return new Number(-1, "Unreal parameter in trig operation.");
        }
        return new Number(Math.cos(n.getReal()));
    }

    /**
     * The division.
     * @param n1 The first number.
     * @param n2 The second number.
     * @return The division of two numbers.
     */
    public static Number divide(final Number n1, final Number n2) {
        if (n2.getAbsValue() == 0) {
            return new Number(-1, "Zero as denominator.");
        } else {
            double r = (n1.getReal() * n2.getReal() + n1.getImag() * n2.getImag())
                    / (Math.pow(n2.getReal(), 2.0) + Math.pow(n2.getImag(), 2.0));
            double i = (n2.getReal() * n1.getImag() - n1.getReal() * n2.getImag())
                    / (Math.pow(n2.getReal(), 2.0) + Math.pow(n2.getImag(), 2.0));
            return new Number(r, i);
        }
    }

    /**
     * The factorial.
     * @param n The number to be operated.
     * @return The factorial of the number.
     */
    public static Number factorial(final Number n) {
        if (!n.isInteger()) {
            return new Number(-1, "Factorial of a non-integer.");
        } else {
            if (n.equals(new Number(0)) || n.equals(new Number(1))) {
                return new Number(1);
            }
            return factorial(new Number(n.getReal() - 1));
        }
    }

    /**
     * The natural log
     * @param n The number.
     * @return The natural log of the number.
     */
    public static Number ln(final Number n) {
        if (!n.isReal()) {
            return new Number(-1, "Unreal argument for ln(x)");
        } else if (n.compareTo(new Number(0)) <= 0) {
            return new Number(-1, "Invalid argument.");
        }
        return new Number(Math.log(n.getReal()));
    }
    /**
     * The log.
     * @param n1 The first number.
     * @param n2 The second number.
     * @return The log of two numbers.
     */
    public static Number log(final Number n1, final Number n2) {
        if (!n1.isReal() || !n2.isReal()) {
            return new Number(-1, "Unreal argument for log(a,b).");
        } else if (n1.equals(new Number(1)) || n1.compareTo(new Number(0)) <= 0) {
            return new Number(-1, "Invalid base.");
        }
        return new Number(Math.log(n1.getReal()) / Math.log(n2.getReal()));
    }

    /**
     * The mod.
     * @param n1 The first number.
     * @param n2 The mod.
     * @return The mod of two numbers.
     */
    public static Number mod(final Number n1, final Number n2) {
        if (!n1.isInteger() || !n2.isInteger()) {
            return new Number(-1, "Mod of a non-Integer.");
        } else {
            return new Number(n1.getReal() % n2.getReal());
        }
    }

    /**
     * The multiply.
     * @param n1 The first number.
     * @param n2 The second number.
     * @return The multiply of two numbers.
     */
    public static Number multiply(final Number n1, final Number n2) {
        double r = n1.getReal() * n2.getReal() - n1.getImag() * n2.getImag();
        double i = n1.getReal() * n2.getImag() + n1.getImag() * n2.getReal();
        return new Number(r, i);
    }

    /**
     * The addition.
     * @param n1 The first number.
     * @param n2 The second number.
     * @return The addition of two numbers.
     */
    public static Number plus(final Number n1, final Number n2) {
        return new Number(n1.getReal() + n2.getReal());
    }

    /**
     * The power.
     * @param n1 The base.
     * @param n2 The power.
     * @return The power of two numbers.
     */
    public static Number power(final Number n1, final Number n2) {
        if (!n1.isReal() || !n2.isReal()) {
            return new Number(-1, "Unreal power or base");
        } else if (n1.getReal() < 0 && !n2.isInteger()) {
            return new Number(-1, "Non-integer power of a negative number.");
        } else if (n1.equals(new Number(0)) && n2.equals(new Number(0))) {
            return new Number(-1, "Zero power of zero");
        }
        return new Number(Math.pow(n1.getReal(), n2.getReal()));
    }

    /**
     * The sin() function.
     * @param n The number to be operated.
     * @return The sine of the number.
     */
    public static Number sin(final Number n) {
        if (!n.isReal()) {
            return new Number(-1, "Unreal parameter in trig operation.");
        }
        return new Number(Math.sin(n.getReal()));
    }

    /**
     * The arithmetic square root of the number. Imaginary number is acceptable.
     * @param n The number to be operated on.
     * @return The square root of the number.
     */
    public static Number sqrt(final Number n) {
        if (!n.isReal()) {
            return new Number(-1, "Unreal parameter in square root function.");
        }
        return new Number(Math.sqrt(n.getReal()));
    }

    /**
     * The tan() function.
     * @param n The number to be operated on.
     * @return The tangent of the number.
     */
    public static Number tan(final Number n) {
        if (!n.isReal()) {
            return new Number(-1, "Unreal parameter in trig operation.");
        }
        try {
            return new Number(Math.tan(n.getReal()));
        } catch (Exception e) {
            return new Number(-1, "Invalid parameter for tan()");
        }
    }
    /**
     * Get the digits left of the dot in a real number.
     * @param d The real number.
     * @return The digits left of the dot.
     */
    public static String getLeftDigits(final double d) {
        String str = String.valueOf(d);
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '.') {
                return str.substring(0, i);
            }
        }
        return str;
    }

    /**
     * Find the priority of a given operator.
     * @param operator The operator.
     * @return The priority of the operator. The Bigger the answer, the higher the priority.
     * @throws Exception In case the operator is null or invalid characters.
     */
    public static int getPriorityOf(final String operator) throws Exception {
        if (operator == null) {
            throw new Exception("Null operator");
        }
        if (operator.equals("+") || operator.equals("-")) {
            return 1;
        } else if (operator.equals("*") || operator.equals("/") || operator.equals("%")) {
            return 2;
        } else if (operator.equals("^")) {
            return 3;
        } else if (operator.equals("sin") || operator.equals("cos") || operator.equals("tan")
                || operator.equals("asin") || operator.equals("acos") || operator.equals("atan")
                || operator.equals("ln") || operator.equals("sqrt")) {
            return 4;
        } else if (operator.equals("!")) {
            return 5;
        } else {
            throw new Exception("Invalid operator");
        }
    }
    /**
     * Start the calculation according to the post-fix expression.
     * @param postExpr The post-fix expression.
     * @return The answer.
     * @throws Exception In case there are problems during operations.
     */
    public static String go(final List<String> postExpr) throws Exception {
        for (int i = 0; i < postExpr.size(); i++) {
            if (!(Number.Constants.NUMBER_SET.contains(postExpr.get(i).substring(0, 1)))) {
                if (getPriorityOf(postExpr.get(i)) == 5 || getPriorityOf(postExpr.get(i)) == 4) {
                    postExpr.add(i - 1, calculate(postExpr.get(i - 1), postExpr.get(i)));
                    postExpr.remove(i);
                    postExpr.remove(i);
                    i = i - 1;
                } else {
                    postExpr.add(i - 2, calculate(postExpr.get(i - 2), postExpr.get(i - 1), postExpr.get(i)));
                    postExpr.remove(i - 1);
                    postExpr.remove(i - 1);
                    postExpr.remove(i - 1);
                    i = i - 2;
                }
            }
        }
        return postExpr.get(0);
    }
    /**
     * Some constants.
     */
    public static class Constants {
        /**
         * Unary operators.
         */
        public final static String UNO_SET = "!asinacosatansqrtln";
        /**
         * Binary operators.
         */
        public final static String DUAL_SET = "+-*/%^";
        /**
         * special characters.
         */
        public final static String SP_SET = "()ie";
        /**
         * All the symbols.
         */
        public final static String BRACKET_SET = "()";
        /**
         * Symbols.
         */
        public final static String SYMBOL_SET = "+-*/%^()!";
        /**
         * Operators put post-numbered.
         */
        public final static String POST_SET = "+-*/%^!";
        /**
         * Operators put pre-numbered.
         */
        public final static String PRE_SET = "+*/%^(";
        /**
         * Function operators.
         */
        public final static String FUNCTION_SET = "asinacosatansqrtln";
        /**
         * Oprators cannot be put continually.
         */
        public final static String LONE_SET = "+-*/%^!";
    }
}
