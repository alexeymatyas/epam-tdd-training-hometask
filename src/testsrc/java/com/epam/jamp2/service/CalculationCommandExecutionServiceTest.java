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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.epam.jamp2.service.CalculationCommandExecutionServiceImplScenario.dec;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.number.BigDecimalCloseTo.closeTo;
import static org.junit.Assert.assertThat;


@RunWith(Parameterized.class)
public class CalculationCommandExecutionServiceTest {

    @InjectMocks
    private CalculationCommandExecutionServiceImpl target;

    @Mock
    private FxRatesService fxRatesService;

    private CalculationCommand command;
    private Value expected;

    private static final Map<String, BigDecimal> mockedRates = new HashMap<>(); static {
        mockedRates.put("gbp => cny", dec(10.0));
        mockedRates.put("cny => gbp", dec(0.1));
        mockedRates.put("cny => cny", dec(1.0));
        mockedRates.put("gbp => gbp", dec(1.0));
    }
    private static final BigDecimal zero = dec(0.0);

    public CalculationCommandExecutionServiceTest(
            Value leftOperand,
            Value rightOperand,
            Operation operation,
            String resultCurrencyCode,
            Value expected)
    {
        this.command = new CalculationCommand(leftOperand, rightOperand, operation, resultCurrencyCode);
        this.expected = expected;
    }

    @Parameters(name = "{index}: test calculate (parameter: {0}, {1}, {2}, {3}) should return = {4}")
    public static Collection<Object[]> data()
    {
        return CalculationCommandExecutionServiceImplScenario.data();
    }

    @Before
    public void before()
    {
        MockitoAnnotations.initMocks(this);

        target.setFxRatesService(fxRatesService);
        String resultCurrency = guessResultCcy();
        mockConvertCall(fxRatesService, resultCurrency, command.getLeftOperand());
        mockConvertCall(fxRatesService, resultCurrency, command.getRightOperand());
    }

    private void mockConvertCall(FxRatesService fxRates, String toCcy, Value operand)
    {
        try {
            BigDecimal expected = calculateExpected(operand, toCcy);
            String fromCcy = guessFromCcy(operand, toCcy);
            Mockito.when(
                    fxRates.convert(fromCcy, toCcy, operand.getValue())
            ).thenReturn(expected);
        }
        catch (IOException | UnknownCurrencyException e) { e.printStackTrace(); }
    }

    private String guessResultCcy() {
        return command.getResultCurrencyCode()
                .orElse(command.getLeftOperand().getCurrencyCode()
                        //Obfuscated meaning is, the left op defines the target currency before the right.
                        .orElse(command.getRightOperand().getCurrencyCode()
                                .orElse("unspecified")));
    }

    private String guessFromCcy(Value operand, String toCcy) {
        return operand.getCurrencyCode().orElse(toCcy);
    }

    private BigDecimal calculateExpected(Value operand, String toCcy) {
        String from = guessFromCcy(operand, toCcy);
        String conversion = from + " => " + toCcy;
        BigDecimal rate = mockedRates.get(conversion);
        rate = rate == null ? zero : rate;
        return operand.getValue().multiply(rate);
    }

    @Test
    public void test_calculate()
    {
        Value result = target.calculate(command);
        BigDecimal errorTolerance = expected.getValue().multiply(dec(0.000001));
        assertThat((result), hasProperty("value", closeTo(expected.getValue(), errorTolerance)));
        assertThat(result.getCurrencyCode(), equalTo(expected.getCurrencyCode()));
    }

}