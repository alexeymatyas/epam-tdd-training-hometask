package com.epam.tdd;

import java.math.BigDecimal;

public class App {
    public static void main(String[] args) {
        System.out.println((new CalculatorService()).divide(BigDecimal.TEN, BigDecimal.ONE));
    }
}
