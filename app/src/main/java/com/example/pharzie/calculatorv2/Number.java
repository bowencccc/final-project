package com.example.pharzie.calculatorv2;
import java.util.Random;
/**
 * A number is considered a complex number.
 */
public class Number implements Comparable<Number> {
    /**
     * Real part of the number.
     */
    private double real;
    /**
     * Imaginary part of the number.
     */
    private double imag;
    /**
     * Whether the number is an integer.
     */
    private boolean isInteger;
    /**
     * Whether the number is real.
     */
    private boolean isReal;
    /**
     * The tag of the number in case it's invalid.
     */
    private String tag = "";
    /**
     * Empty-arg constructor.
     */
    Number() {
    }
    /**
     * Set the real only.
     * @param setReal The real part of the number.
     */
    Number(final double setReal) {
        this.real = setReal;
        this.imag = 0.0;
        isReal = true;
        isInteger = (int) Math.abs(setReal) == Math.abs(setReal);
    }
    /**
     * Set the number with real-imag.
     * @param setReal The real part of the number.
     * @param setImag The imaginary part of the number.
     */
    Number(final double setReal, final double setImag) {
        this.real = setReal;
        this.imag = setImag;
        isReal = setImag == 0;
        isInteger = isReal && (int) Math.abs(setReal) == Math.abs(setReal);
    }

    /**
     * Set the number with a string.
     * @param str The number as a string.
     */
    Number(final String str) {
        if (str.contains("+")) {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == '+') {
                    this.real = Double.parseDouble(str.substring(0, i));
                    this.imag = Double.parseDouble(str.substring(i, str.length() - 1));
                    isReal = imag == 0;
                    isInteger = isReal && (int) Math.abs(real) == Math.abs(real);
                }
            }
        } else {
            this.real = Double.parseDouble(str);
            isReal = true;
            isInteger = (int) Math.abs(real) == Math.abs(real);
        }
    }
    /**
     * Set the number with arg-abs.
     * @param setAbs The absolute value of the number.
     * @param setArg The argument of the number.
     * @param b Just to differ from the real-imag constructor.
     */
    Number(final double setAbs, final double setArg, final boolean b) {
        double arg = setArg % Number.Constants.PI.getReal();
        this.real = setAbs * Math.cos(arg);
        this.imag = setAbs * Math.sin(arg);
        isReal = new Number(real, imag).isReal();
        isInteger = new Number(real, imag).isInteger();
    }
    /**
     * Set the number with a warning.
     * @param errorNum Usually -1;
     * @param warning The warning telling what's wrong.
     */
    Number(final double errorNum, final String warning) {
        this.real = errorNum;
        this.tag = warning;
        isReal = false;
        isInteger = false;
    }
    /**
     * Return the trig form of the number.
     * @return The number in form of "acos(x)+aisin(x)".
     */
    public String cis() {
        return getAbsValue() + "cos(" + getArg() + ")+" + getAbsValue() + "isin(" + getArg() + ")";
    }
    /**
     * Get the difference of two numbers.
     * @param n1 The first number.
     * @param n2 The second number.
     * @return The difference.
     */
    private double differ(final Number n1, final Number n2) {
        double r1 = n1.getReal();
        double r2 = n2.getReal();
        double i1 = n1.getImag();
        double i2 = n2.getImag();
        return (Math.sqrt(Math.pow(r1 - r2, 2.0) + Math.pow(i1 - i2, 2.0)));
    }
    /**
     * Return the exponential form of the number.
     * @return The number in form of "ae^i(x)".
     */
    public String eit() {
        return getAbsValue() + "e^i(" + getArg() + ")";
    }
    /**
     * Get the argument of the number.
     * @return From -pi to pi.
     */
    public double getArg() {
        if (real == 0.0) {
            if (imag == 0.0) {
                return 0;
            }
            return Constants.PI.getReal() / 2;
        } else if (imag == 0) {
            if (real < 0) {
                return Constants.PI.getReal();
            }
        }
        return 2 * Math.atan(imag / Math.sqrt(Math.pow(real, 2.0) + Math.pow(imag, 2.0)) + real);
    }
    /**
     * Get the imaginary part of the number.
     * @return imag.
     */
    public double getImag() {
        return imag;
    }
    /**
     * Get the real part of the number.
     * @return real.
     */
    public double getReal() {
        return real;
    }
    /**
     * Get the absolute value of the number.
     * @return The absolute value.
     */
    public double getAbsValue() {
        return Math.sqrt(Math.pow(real, 2.0) + Math.pow(imag, 2.0));
    }
    /**
     * Find out whether the number is an integer.
     * @return TRUE if is.
     */
    public boolean isInteger() {
        return isInteger;
    }
    /**
     * Find out whether the number is a real.
     * @return TRUE if is.
     */
    public boolean isReal() {
        return isReal;
    }

    /**
     * Round the number if it's real.
     * @param d Significant figures.
     * @return The rounded(downward) number.
     */
    public String roundToDigit(int d) {
        if (!isReal) {
            return new Number(-1, "Cannot round a imaginary number").toString();
        }
        return String.valueOf(real).substring(0, d);
    }
    /**
     * Find out whether the number is bigger than another number or not.
     * @param other The other number.
     * @return 1 if is; 0 if same; -1 if opposite.
     */
    public int compareTo(final Number other) {
        if (isReal && other.isReal()) {
            if (real > other.getReal()) {
                return 1;
            } else if (real < other.getReal()) {
                return -1;
            } else {
                return 0;
            }
        }
        if (getAbsValue() > other.getAbsValue()) {
            return 1;
        } else if (getAbsValue() < other.getAbsValue()) {
            return -1;
        } else {
            return 0;
        }
    }
    /**
     * Find out whether a number is approximate equal to another number.
     * @param other The other number.
     * @return TRUE if is.
     */
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof Number)) {
            return false;
        }
        Number otherNumber = (Number) other;
        return differ(this, otherNumber) <= Constants.MAX_ERROR;
    }
    /**
     * Hash code.
     * @return The hash code
     */
    public int hashCode() {
        String s1 = String.valueOf(real);
        String s2 = String.valueOf(imag);
        String critical = s1.substring(0, new Random().nextInt(s1.length()))
                + s2.substring(0, new Random().nextInt(s2.length()));
        return critical.hashCode();
    }
    /**
     * Return the normal version of the number.
     * @return The number in form of "a+bi".
     */
    public String toString() {
        if (!tag.equals("")) {
            return tag;
        }
        if (isReal) {
            return String.valueOf(real);
        }
        return real + "+" + imag + "i";
    }
    /**
     * An inner class of constants.
     */
    public static class Constants {
        /**
         * The max error bigger than which two numbers are regarded as different numbers.
         */
        private final static double MAX_ERROR = 1.0e-10;
        /**
         * The range of x in arctirg functions.
         */
        public final static double MAX_X_ATRIG = 1;
        /**
         * The set of number (dot included).
         */
        public final static String NUMBER_SET = "0123456789.";
        /**
         * E.
         */
        private final static Number E = new Number(Math.E);
        /**
         * Pi.
         */
        protected final static Number PI = new Number(Math.PI);
    }
}
