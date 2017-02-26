package com.epam.jamp2.service;

import com.epam.jamp2.model.CalculationCommand;
import com.epam.jamp2.model.CommandFormatException;
import com.epam.jamp2.model.UnknownCurrencyException;
import com.epam.jamp2.model.Value;
import com.epam.jamp2.mock.FxRateMockData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.BigDecimalCloseTo.closeTo;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CalculationCommandExecutionServiceIT {

    @Autowired
    private CalculationCommandExecutionService calculationCommandExecutionService;

    // 1 USD ~= 6.866 CNY
    private static final String ADD_DIDD_CCY = "USD100.0+CNY5.0=CNY";
    private static final String DIVIDE_SAME_CCY = "CNY100.0/CNY5.0=";
    private static final String MULTI_RIGHT_CCY_PROVIDED = "100.0*USD5.0=";

    @Test
    public void testCalcWithDiffCcy() throws CommandFormatException, IOException {
        CalculationCommand cmd = CalculationCommand.parseFromString(ADD_DIDD_CCY);
        Value result = calculationCommandExecutionService.calculate(cmd);
        validateCalculatedValue(result, FxRateMockData.CNY, 691.6, 5.0);
    }

    @Test
    public void testCalcWithSameCcy()
            throws CommandFormatException, IOException, UnknownCurrencyException {

        CalculationCommand cmd = CalculationCommand.parseFromString(DIVIDE_SAME_CCY);

        Value result = calculationCommandExecutionService.calculate(cmd);
        validateCalculatedValue(result, FxRateMockData.CNY, 20.0, 0.1);
    }

    @Test
    public void testCalcWithOnlyOneCcyProvided()
            throws CommandFormatException, IOException, UnknownCurrencyException {

        CalculationCommand cmd = CalculationCommand.parseFromString(MULTI_RIGHT_CCY_PROVIDED);

        Value result = calculationCommandExecutionService.calculate(cmd);
        validateCalculatedValue(result, FxRateMockData.USD, 500.0, 0.1);
    }

    private void validateCalculatedValue(Value result, String expectedCcy, double expectedValue,
                                         double acceptableDiff) {
        Assert.assertNotNull(result);
        Assert.assertThat(result.getCurrencyCode().get(), is(expectedCcy));
        Assert.assertThat(result.getValue(),
                is(closeTo(new BigDecimal(expectedValue), new BigDecimal(acceptableDiff))));

    }
}
