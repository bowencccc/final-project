package com.example.pharzie.calculatorv2;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        assertEquals("x^2+x*(x+1)", Expression.getLaTeXpr(new Polynomial("1/(2+3)").expression));
    }
    public static void main(String[] unused) throws Exception {
        Polynomial p = new Polynomial("2+1*(2+3)");
        System.out.println(p.toString());
    }
}