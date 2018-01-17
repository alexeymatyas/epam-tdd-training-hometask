package com.epam.jamp2.service;

import com.epam.jamp2.model.Operation;
import com.epam.jamp2.model.Value;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

/*
 * Parameterized data for unit tests. Also used for ITCalculationCommandExecutionService.
 */
public class CalculationCommandExecutionServiceImplScenario {

    public static Collection<Object[]> data() {
        Object[][] objects = {
                test("gbp", 1.0, Operation.ADD, "gbp", 2.0, "gbp",  new Value("gbp", dec(3.0))),
                test("gbp", 3.0, Operation.SUBTRACT, "gbp", 2.0, "gbp", new Value("gbp",  dec(1.0))),
                test("gbp", 1.0, Operation.ADD, "cny", 2.0, "cny", new Value("cny",  dec(12.0))),
                test("gbp", 10000000000000000001.0, Operation.ADD, "cny",  20000000000000000000.0,
                        "cny", new Value("cny", dec(120000000000000000000.0))),
                test("gbp", 0.00001, Operation.ADD, "gbp", 0.0, "cny", new Value( "cny", dec(0.0001))),
                test("gbp", 1.0, Operation.ADD, "gbp", 2.0, null, new Value("gbp",  dec(3.0))),
                test("gbp", 1.0, Operation.ADD, "cny", 2.0, null, new Value( "gbp",  dec(1.2))),
                test("gbp", 1.0, Operation.ADD, null, 2.0, null, new Value("gbp",  dec(3.0))),
                test(null, 1.0, Operation.ADD, null, 2.0, "gbp", new Value("gbp",  dec(3.0)))
        };
        return Arrays.asList(objects);
    }

    private static Object[] test(
            String leftCurrency,
            double leftVal,
            Operation operation,
            String rightCurrency,
            double rightVal,
            String resultCurrencyCode,
            Value expected) {
        Value left = new Value(leftCurrency, dec(leftVal));
        Value right = new Value(rightCurrency, dec(rightVal));
        return new Object[]{left, right, operation, resultCurrencyCode, expected};
    }

    public static BigDecimal dec(Double dec)
    {
        return new BigDecimal(dec);
    }

}