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
import org.mockito.stubbing.OngoingStubbing;

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
public class CalculationCommandExecutionServiceTest {

    private CalculationCommandExecutionServiceImpl target;

    private CalculationCommand command;
    private Value expected;

    private FxRatesService fxRatesService;

    // Parameterized data for unit tests. Also used for ITCalculationCommandExecutionService.
    @Parameters(name = "{index}: test calculate (command: {0}, {1}, {2}, {3}) should return = {4}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new Value("gbp", BigDecimal.valueOf(1.0)), new Value("gbp", BigDecimal.valueOf(2.0))
                        , Operation.ADD, "gbp", new Value("gbp", BigDecimal.valueOf(3.0))},
                {new Value("gbp", BigDecimal.valueOf(3.0)), new Value("gbp", BigDecimal.valueOf(2.0))
                        , Operation.SUBTRACT, "gbp", new Value("gbp", BigDecimal.valueOf(1.0))},
                {new Value("gbp", BigDecimal.valueOf(1.0)), new Value("cny", BigDecimal.valueOf(2.0))
                        , Operation.ADD, "cny", new Value("cny", BigDecimal.valueOf(12.0))},
                {new Value("gbp", BigDecimal.valueOf(10000000000000000001.0)),
                        new Value("cny", BigDecimal.valueOf(20000000000000000000.0))
                        , Operation.ADD, "cny", new Value("cny", BigDecimal.valueOf(120000000000000000000.0))},
                {new Value("gbp", BigDecimal.valueOf(0.00001)), new Value("gbp", BigDecimal.valueOf(0.0))
                        , Operation.ADD, "cny", new Value("cny", BigDecimal.valueOf(0.0001))},
                {new Value("gbp", BigDecimal.valueOf(1.0)), new Value("gbp", BigDecimal.valueOf(2.0))
                        , Operation.ADD, null, new Value("gbp", BigDecimal.valueOf(3.0))},
                {new Value("gbp", BigDecimal.valueOf(1.0)), new Value("cny", BigDecimal.valueOf(2.0))
                        , Operation.ADD, null, new Value("gbp", BigDecimal.valueOf(1.2))},
                {new Value("gbp", BigDecimal.valueOf(1.0)), new Value(null, BigDecimal.valueOf(2.0))
                        , Operation.ADD, null, new Value("gbp", BigDecimal.valueOf(3.0))},
                {new Value(null, BigDecimal.valueOf(1.0)), new Value(null, BigDecimal.valueOf(2.0))
                        , Operation.ADD, "gbp", new Value("gbp", BigDecimal.valueOf(3.0))}
        });
    }

    // constructor
    public CalculationCommandExecutionServiceTest(Value leftOperand, Value rightOperand, Operation operation
            , String resultCurrencyCode, Value expected) {
        this.command = new CalculationCommand(leftOperand, rightOperand, operation, resultCurrencyCode);
        this.expected = expected;
    }

    private static final Map<String, BigDecimal> fxRates = new HashMap<>();
    static {
        fxRates.put("gbp => cny", BigDecimal.valueOf(10));
        fxRates.put("cny => gbp", BigDecimal.valueOf(0.1));
        fxRates.put("cny => cny", BigDecimal.valueOf(1));
        fxRates.put("gbp => gbp", BigDecimal.valueOf(1));
    }

    @Before
    public void beforeTest() throws IOException, UnknownCurrencyException {
        this.target = new CalculationCommandExecutionServiceImpl();

        try {
            fxRatesService = mock(FxRatesService.class);

            // Mocks the possible calls to the FxRatesService.
            BigDecimal o = BigDecimal.valueOf(0.0);
            Optional<BigDecimal> leftToResultRate = Optional.ofNullable(
                    fxRates.get(command.getLeftOperand().getCurrencyCode().orElse("null") + " => "
                            + command.getResultCurrencyCode().orElse("null")));
            Optional<BigDecimal> rightToResultRate = Optional.ofNullable(
                    fxRates.get(command.getRightOperand().getCurrencyCode().orElse("null") + " => "
                            + command.getResultCurrencyCode().orElse("null")));
            BigDecimal leftInTarget = command.getLeftOperand().getValue().multiply(leftToResultRate.orElse(o));
            BigDecimal rightInTarget = command.getRightOperand().getValue().multiply(rightToResultRate.orElse(o));
            OngoingStubbing leftCall = when(fxRatesService.convert(command.getLeftOperand().getCurrencyCode().get()
                    , command.getResultCurrencyCode().get()
                    , command.getLeftOperand().getValue()));
            leftCall.thenReturn(leftInTarget);
            OngoingStubbing rightCall = when(fxRatesService.convert(command.getRightOperand().getCurrencyCode().get()
                    , command.getResultCurrencyCode().get()
                    , command.getRightOperand().getValue()));
            rightCall.thenReturn(rightInTarget);

            this.target.setFxRatesService(fxRatesService);
        } catch (java.util.NoSuchElementException e) {
            throw new java.lang.UnsupportedOperationException("My mock it still not smart enough to deal with " +
                    "null values for currencies.");
        }
    }

    @Test
    public void test_getConvertedValue() throws IOException, UnknownCurrencyException {

        BigDecimal leftInTarget = target.getConvertedValue(command.getLeftOperand(), command.getResultCurrencyCode());
        BigDecimal rightInTarget = target.getConvertedValue(command.getRightOperand(), command.getResultCurrencyCode());
        BigDecimal errorTolerance = expected.getValue().multiply(BigDecimal.valueOf(0.000001)); // i.e. ±0.00001% for float precision

        if(command.getOperation().equals(Operation.ADD)) {
            assertThat("mmm",(leftInTarget.add(rightInTarget)), closeTo(expected.getValue(), errorTolerance));
        } else if(command.getOperation().equals(Operation.SUBTRACT)) {
            assertThat((leftInTarget.subtract(rightInTarget)), closeTo(expected.getValue(), errorTolerance));
        }
    }

    @Test
    public void test_calculate() throws IOException, UnknownCurrencyException {
        Value result = target.calculate(command);
        BigDecimal errorTolerance = expected.getValue().multiply(BigDecimal.valueOf(0.000001)); // i.e. ±0.00001% for float precision
        assertThat((result), hasProperty("value", closeTo(expected.getValue(), errorTolerance)));
        assertThat(result.getCurrencyCode(), equalTo(expected.getCurrencyCode()));
    }

}