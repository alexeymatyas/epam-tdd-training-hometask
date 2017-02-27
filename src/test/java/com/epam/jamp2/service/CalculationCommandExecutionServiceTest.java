package com.epam.jamp2.service;

import com.epam.jamp2.model.CalculationCommand;
import com.epam.jamp2.model.CommandFormatException;
import com.epam.jamp2.model.UnknownCurrencyException;
import com.epam.jamp2.model.Value;
import com.epam.jamp2.mock.FxRateMockData;
import com.epam.jamp2.service.impl.CalculationCommandExecutionServiceImpl;
import com.epam.jamp2.service.impl.FxRatesServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.BigDecimalCloseTo.closeTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CalculationCommandExecutionServiceTest {

    @Mock
    private FxRatesServiceImpl fxRatesService;

    @InjectMocks
    private CalculationCommandExecutionServiceImpl calculationCommandExecutionServiceImpl = new CalculationCommandExecutionServiceImpl();

    private static final String ADD_DIFF_CCY = "USD100.0+CNY5.0=CNY";
    private static final String ADD_DIFF_CCY_WITHOUT_TARGET_CCY = "CNY100.0+USD5.0=";
    private static final String DIVIDE_SAME_CCY = "CNY100.0/CNY5.0=";
    private static final String MULTI_RIGHT_CCY_PROVIDED = "100.0*USD5.0=";
    private static final String INCORRECT_CMD_FORMAT = "1.0+2.9";

    @Test
    public void should_pass_4_different_ccy()
            throws CommandFormatException, IOException, UnknownCurrencyException {

        CalculationCommand cmd = mockCalculationCommand(ADD_DIFF_CCY, 686.6, 5.0, FxRateMockData.CNY);

        Value result = calculationCommandExecutionServiceImpl.calculate(cmd);

        Assert.assertNotNull(result);
        Assert.assertThat(result.getCurrencyCode().get(), is(FxRateMockData.CNY));
        Assert.assertThat(result.getValue(),
                is(closeTo(new BigDecimal(691.6), new BigDecimal(0.1))));

    }

    @Test
    public void should_pass_4_same_ccy()
            throws CommandFormatException, IOException, UnknownCurrencyException {

        CalculationCommand cmd = mockCalculationCommand(DIVIDE_SAME_CCY, 100.0, 5.0, FxRateMockData.CNY);

        Value result = calculationCommandExecutionServiceImpl.calculate(cmd);
        verify(fxRatesService, never()).convert(anyString(), anyString(), any(BigDecimal.class));

        Assert.assertNotNull(result);
        Assert.assertThat(result.getCurrencyCode().get(), is(FxRateMockData.CNY));
        Assert.assertThat(result.getValue(),
                is(closeTo(new BigDecimal(20.0), new BigDecimal(0.1))));

    }

    @Test
    public void should_pass_without_target_ccy()
            throws CommandFormatException, IOException, UnknownCurrencyException {

        CalculationCommand cmd = mockCalculationCommand(ADD_DIFF_CCY_WITHOUT_TARGET_CCY, 100.0,
                34.33, FxRateMockData.CNY);

        Value result = calculationCommandExecutionServiceImpl.calculate(cmd);

        Assert.assertNotNull(result);
        Assert.assertThat(result.getCurrencyCode().get(), is(FxRateMockData.CNY));
        Assert.assertThat(result.getValue(),
                is(closeTo(new BigDecimal(134.33), new BigDecimal(0.1))));

    }

    @Test
    public void should_pass_ifOnly_right_ccy_provided()
            throws CommandFormatException, IOException, UnknownCurrencyException {

        CalculationCommand cmd = mockCalculationCommand(MULTI_RIGHT_CCY_PROVIDED, 100.0, 5.0, FxRateMockData.USD);

        Value result = calculationCommandExecutionServiceImpl.calculate(cmd);

        Assert.assertNotNull(result);
        Assert.assertThat(result.getCurrencyCode().get(), is(FxRateMockData.USD));
        Assert.assertThat(result.getValue(),
                is(closeTo(new BigDecimal(500.0), new BigDecimal(0.1))));

    }

    @Test(expected = CommandFormatException.class)
    public void should_throw_cmdFormat_error()
            throws CommandFormatException, IOException, UnknownCurrencyException {
        CalculationCommand cmd = mockCalculationCommand(INCORRECT_CMD_FORMAT, 1.0, 2.9, FxRateMockData.CNY);
        calculationCommandExecutionServiceImpl.calculate(cmd);
    }

    private CalculationCommand mockCalculationCommand(String calFormula, double
            expectedLeftVal, double expectedRightVal, String expectedCcy)
            throws CommandFormatException, IOException, UnknownCurrencyException {
        CalculationCommand cmd = CalculationCommand.parseFromString(calFormula);
        mockConvertOperations(cmd, expectedLeftVal, expectedRightVal, expectedCcy);
        return cmd;
    }

    private void mockConvertOperations(CalculationCommand cmd, double expectedLeftVal,
                                       double expectedRightVal, String resultCcy)
            throws IOException, UnknownCurrencyException {
        Value leftValue = cmd.getLeftOperand();
        Value rightValue = cmd.getRightOperand();
        Optional<String> resultCurrencyCode = Optional.of(resultCcy);

        BigDecimal leftOps = new BigDecimal(expectedLeftVal);
        BigDecimal rightOps = new BigDecimal(expectedRightVal);

        if (leftValue.getCurrencyCode().isPresent()) {
            when(fxRatesService.convert(leftValue.getCurrencyCode().get(), resultCurrencyCode.get(),
                    leftValue.getValue())).thenReturn(leftOps);
        }
        if (rightValue.getCurrencyCode().isPresent()) {
            when(fxRatesService.convert(rightValue.getCurrencyCode().get(), resultCurrencyCode.get(),
                    rightValue.getValue())).thenReturn(rightOps);
        }
    }

}
