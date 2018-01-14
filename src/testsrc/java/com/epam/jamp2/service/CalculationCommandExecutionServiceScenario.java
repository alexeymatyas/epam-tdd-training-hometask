package com.epam.jamp2.service;

import com.epam.jamp2.model.CalculationCommand;
import com.epam.jamp2.model.Operation;
import com.epam.jamp2.model.UnknownCurrencyException;
import com.epam.jamp2.model.Value;
import com.epam.jamp2.service.impl.CalculationCommandExecutionServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.number.BigDecimalCloseTo.closeTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(value = Parameterized.class)
public class CalculationCommandExecutionServiceScenario {

    private CalculationCommandExecutionServiceImpl target;

    @Mock
    private FxRatesService fxRatesService;

    private CalculationCommand parameter;
    private Value expected;

    public CalculationCommandExecutionServiceScenario(
            Value leftOperand,
            Value rightOperand,
            Operation operation,
            String resultCurrencyCode,
            Value expected)
    {
        this.parameter = new CalculationCommand(leftOperand, rightOperand, operation, resultCurrencyCode);
        this.expected = expected;
    }

    private final BigDecimal zero = dec(0.0);

    // Parameterized data for unit tests. Also used for ITCalculationCommandExecutionService.
    @Parameters(name = "{index}: test calculate (parameter: {0}, {1}, {2}, {3}) should return = {4}")
    public static Collection<Object[]> data()
    {
        return Arrays.asList(
                test("gbp", 1.0, "gbp", 2.0, Operation.ADD, "gbp", "gbp", 3.0),
                test("gbp", 3.0, "gbp", 2.0, Operation.SUBTRACT, "gbp", "gbp", 1.0),
                test("gbp", 1.0, "cny", 2.0, Operation.ADD, "cny", "cny", 12.0),
                test("gbp", 10000000000000000001.0, "cny", 20000000000000000000.0,
                        Operation.ADD, "cny", "cny", 120000000000000000000.0),
                test("gbp", 0.00001, "gbp", 0.0, Operation.ADD, "cny", "cny", 0.0001),
                test("gbp", 1.0, "gbp", 2.0, Operation.ADD, null, "gbp", 3.0),
                test("gbp", 1.0, "cny", 2.0, Operation.ADD, null, "gbp", 1.2),
                test("gbp", 1.0, null, 2.0, Operation.ADD, null, "gbp", 3.0),
                test(null, 1.0, null, 2.0, Operation.ADD, "gbp", "gbp", 3.0));
    }

    private static Object[] test(
            String leftCurrency,
            double leftVal,
            String rightCurrency,
            double rightVal,
            Operation operation,
            String wantedCurrency,
            String expectedCurrency,
            double expectedVal)
    {
        Value left = new Value(leftCurrency, dec(leftVal));
        Value right = new Value(rightCurrency, dec(rightVal));
        Value expected = new Value(expectedCurrency, dec(expectedVal));
        return new Object[]{left, right, operation, wantedCurrency, expected};
    }

    private static BigDecimal dec(Double dec)
    {
        return new BigDecimal(dec);
    }


    private static final Map<String, BigDecimal> mockedRates = new HashMap<>();
    static {
        mockedRates.put("gbp => cny", dec(10.0));
        mockedRates.put("cny => gbp", dec(0.1));
        mockedRates.put("cny => cny", dec(1.0));
        mockedRates.put("gbp => gbp", dec(1.0));
    }

    @Before
    public void before()
    {
        this.target = new CalculationCommandExecutionServiceImpl();

        fxRatesService = mock(FxRatesService.class);
        String resultCurrency = parameter.getResultCurrencyCode().get();

        mockConvertCall(fxRatesService, parameter.getLeftOperand(), resultCurrency);
        mockConvertCall(fxRatesService, parameter.getRightOperand(), resultCurrency);

        this.target.setFxRatesService(fxRatesService);
    }

    private void mockConvertCall(FxRatesService fxRates, Value operand, String toCcy)
    {
        BigDecimal expected = calculateExpected(operand, toCcy);
        String fromCcy = operand.getCurrencyCode().get();
        try {
            when(fxRates.convert(fromCcy, toCcy, operand.getValue())).thenReturn(expected);
        } catch (IOException | UnknownCurrencyException e) { e.printStackTrace(); }
    }

    private BigDecimal calculateExpected(Value operand, String resultCurrency) {
        String conversion = operand.getCurrencyCode().get() + " => " + resultCurrency;
        BigDecimal rate = mockedRates.get(conversion);
        rate = rate == null ? zero : rate;
        return operand.getValue().multiply(rate);
    }

    @Test
    public void test_getConvertedValue()
    {

        try {
            BigDecimal leftInTarget = target.getConvertedValue(parameter.getLeftOperand(), parameter.getResultCurrencyCode());
            BigDecimal rightInTarget = target.getConvertedValue(parameter.getRightOperand(), parameter.getResultCurrencyCode());
            BigDecimal errorTolerance = expected.getValue().multiply(dec(0.000001)); // i.e. ±0.00001% for float precision

            if(parameter.getOperation().equals(Operation.ADD)) {
                assertThat(leftInTarget.add(rightInTarget), closeTo(expected.getValue(), errorTolerance));
            } else if(parameter.getOperation().equals(Operation.SUBTRACT)) {
                assertThat((leftInTarget.subtract(rightInTarget)), closeTo(expected.getValue(), errorTolerance));
            }
        } catch (IOException | UnknownCurrencyException e) { assert false; }
    }

    @Test
    public void test_calculate()
    {
        Value result = target.calculate(parameter);
        BigDecimal errorTolerance = expected.getValue().multiply(dec(0.000001)); // i.e. ±0.00001% for float precision
        assertThat((result), hasProperty("value", closeTo(expected.getValue(), errorTolerance)));
        assertThat(result.getCurrencyCode(), equalTo(expected.getCurrencyCode()));
    }

}