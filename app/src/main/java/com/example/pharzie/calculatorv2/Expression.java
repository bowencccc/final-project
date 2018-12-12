package com.example.pharzie.calculatorv2;
import java.util.ArrayList;
import java.util.List;
public class Expression extends Polynomial {
    /**
     * List of operators.
     */
    private ArrayList<String> operators = new ArrayList<>();
    /**
     * Constructor with no input.
     */
    Expression() {
    }
    /**
     * Constructor with a list of list of string.
     * @param input The expression as a list.
     */
    Expression(final ArrayList<ArrayList<String>> input) {
        this.expression = input.get(VALUE_LIST);
        this.operators = input.get(TYPE_LIST);
    }

    /**
     * Constructor with a string.
     * @param setFullExpr The full expression as a string.
     * @throws Exception In case the expression is not valid.
     */
    Expression(final String setFullExpr) throws Exception {
        super(setFullExpr);
        this.operators = splitTerms(fullExpr).get(TYPE_LIST);
    }

    /**
     * Get the post-fix form of this expression.
     * @return The post-fix expression.
     * @throws Exception In case the operators aren't recognized.
     */
    public List<String> getpostExpr() throws Exception {
        /**
         * postfix expression
         */
        List<String> postExpr = new ArrayList<>();
        /**
         * s2 operator list
         */
        List<String> operList = new ArrayList<>();
        for (int i = 0; i < expression.size(); i++) {
            if (operators.get(i).equals("Number")) {
                postExpr.add(expression.get(i));
            } else if (operators.get(i).equals("LeftParenthesis")) {
                operList.add(expression.get(i));
            } else if (operators.get(i).equals("RightParenthesis")) {
                for (int j = operList.size() - 1; j >= 0; j--) {
                    if (operList.get(j).equals("(")) {
                        operList.remove(j);
                        break;
                    }
                    postExpr.add(operList.get(j));
                    operList.remove(j);
                }
            } else {
                if (operList.size() == 0) {
                    operList.add(expression.get(i));
                    continue;
                }
                int last = operList.size() - 1;
                if (operList.get(last).equals("(")) {
                    operList.add(expression.get(i));
                    continue;
                }
                if (Operator.getPriorityOf(operList.get(last)) < Operator.getPriorityOf(expression.get(i))) {
                    operList.add(expression.get(i));
                } else {
                    postExpr.add(operList.get(last));
                    operList.remove(operList.size() - 1);
                    operList.add(expression.get(i));
                }
            }
        }
        if (operList.size() != 0) {
            for (int i = operList.size() - 1; i >= 0; i--) {
                postExpr.add(operList.get(i));
            }
        }
        return postExpr;
    }

    public static ArrayList<String> inputOriginal;
    public static ArrayList<String> getLaTeXpr(ArrayList<String> input) {
        ArrayList<String> toReturn = new ArrayList<>();
        String toCompare = "0123456789+-*^!";
        //This is the count of parentheses. Used for recursion.
        int count = 0;
        //This is the index to put /frac{ modifier. Move with i when it's non-numbers and non-dividers.
        int indexDivide = 0;
        //i is the index of original list. In many cases it's also the index of the final list;
        for (int i = 0; i < input.size(); i++) {
            if (toCompare.contains(input.get(i).substring(0, 1))) {
                toReturn.add(input.get(i));
            } else if (input.get(i).equals("%")) {
                toReturn.add("\\bmod");
            } else if (input.get(i).equals("sin")) {
                toReturn.add("\\sin");
            } else if (input.get(i).equals("cos")) {
                toReturn.add("\\cos");
            } else if (input.get(i).equals("tan")) {
                toReturn.add("\\tan");
            } else if (input.get(i).equals("asin")) {
                toReturn.add("\\arcsin");
            } else if (input.get(i).equals("acos")) {
                toReturn.add("\\arccos");
            } else if (input.get(i).equals("atan")) {
                toReturn.add("\\arctan");
            } else if (input.get(i).equals("ln")) {
                toReturn.add("\\ln");
            } else if (input.get(i).equals("sqrt")) {
                toReturn.add("\\sqrt");
            }
            if (input.get(i).equals("(")) {
                //Add the left parenthesis.
                toReturn.add("(");
                count++;
                int index = 0;
                //Get the corresponding right parenthesis by counting the number.
                for (int b = i + 1; b < input.size(); b++) {
                    if (input.get(b).equals("(")) {
                        count++;
                    }
                    if (input.get(b).equals(")")) {
                        count--;
                        if (count == 0) {
                            index = b;
                            break;
                        }
                    }
                }
                //Get the inner-list(between the corresponding parentheses). (i+=index-1)
                ArrayList<String> array = new ArrayList<>();
                for (int c = i + 1; c < index; c++) {
                    array.add(input.get(c));
                }
                //Recursion to get the final LaTeX-fied inner-list.
                ArrayList<String> anotherArray;
                anotherArray = getLaTeXpr(array);
                for (int d = 0; d < anotherArray.size(); d++) {
                    toReturn.add(anotherArray.get(d));
                }
                //Add the right parenthesis.(i+=1)
                toReturn.add(")");
                i = index - 1;
            }
            if (input.get(i).equals("/")) {
                //Add the \frac{ modifier at the very front of a series of divisions.
                //The case the previous element is a right parenthesis.
                int leftIndex = 0;
                int rightIndex = 0;
                if (toReturn.get(toReturn.size() - 1).equals(")")) {
                    count++;
                    for (int j = toReturn.size() - 2; j >= 0; j--) {
                        if (toReturn.get(j).equals(")")) {
                            count++;
                        }
                        if (toReturn.get(j).equals("(")) {
                            count--;
                            if (count==  0) {
                                if (j > 0 && toReturn.get(j - 1).length() > 1) {
                                    j--;
                                }
                                leftIndex = j;
                            }
                        }
                    }
                } else if (toReturn.get(toReturn.size() - 1).equals("}")) {
                    for (int j = toReturn.size() - 2; j >= 0; j--) {
                        if (toReturn.get(j).equals("\\frac{")) {
                            if (j > 0 && !toReturn.get(j - 1).equals("\\frac{")) {
                                leftIndex = j;
                            }
                        }
                    }
                } else if (toCompare.contains(toReturn.get(toReturn.size() - 1).substring(0, 1))) {
                    for (int j = toReturn.size() - 2; j >= 0; j--) {
                        if (!toReturn.get(j).equals("^")) {
                            leftIndex = j + 1;
                            break;
                        }
                    }
                } else {

                }
                toReturn.add(leftIndex, "\\frac{");
                toReturn.add("}");
                toReturn.add("{");
                //Find the place to add "}";
                //If it's a function, add it.(i+=1)
                if (!toCompare.contains(input.get(i + 1))) {
                    toReturn.add(input.get(i + 1));
                    i++;
                }
                if (input.get(i + 1).equals("(")) {
                    //Add the left parenthesis.(i+=1)
                    toReturn.add("(");
                    count++;
                    int index = 0;
                    //Get the corresponding right parenthesis by counting the number.
                    for (int b = i + 2; b < input.size(); b++) {
                        if (input.get(b).equals("(")) {
                            count++;
                        }
                        if (input.get(b).equals(")")) {
                            count--;
                            if (count == 0) {
                                index = b;
                                break;
                            }
                        }
                    }
                    //Get the inner-list(between the corresponding parentheses). (i+=index-1)
                    ArrayList<String> array = new ArrayList<>();
                    for (int c = i + 2; c < index; c++) {
                        array.add(input.get(c));
                    }
                    //Recursion to get the final LaTeX-fied inner-list.
                    ArrayList<String> anotherArray;
                    anotherArray = getLaTeXpr(array);
                    for (int d = 0; d < anotherArray.size(); d++) {
                        toReturn.add(anotherArray.get(d));
                    }
                    //Add the right parenthesis.(i+=1)
                    toReturn.add(")");
                    i = index - 1;
                } else if (toCompare.contains(input.get(i + 1).substring(0, 1))) {
                    for (int j = i + 2; j < input.size(); j++) {
                        if (!input.get(j).equals("^")) {
                            rightIndex = j;
                        }
                    }
                    for (int k = i + 1; k < rightIndex; k++) {
                        toReturn.add(input.get(k));
                        i++;
                    }
                }
                toReturn.add("}");
                break;
            }
        }
        return toReturn;
    }


    /**
     * Print a list of string.
     * @param input The input.
     */
    public static void print(final List<String> input) {
        String result = "";
        for (String s : input) {
            result += s;
        }
        System.out.println(result);
    }
}

