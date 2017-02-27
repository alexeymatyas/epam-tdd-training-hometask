package com.epam.jamp2.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created by Northzzn on 2017/2/27.
 */
@RunWith(JUnit4.class)
public class CalculationCommandTest {

    @Test
    public void testParseFromStringWithAdd() throws CommandFormatException {
        CalculationCommand calculationCommand = CalculationCommand.parseFromString("CNY10.0+USD20.01=CFH");

        assertThat(calculationCommand.getLeftOperand().getCurrencyCode().get(), is(equalTo("CNY")));
        assertThat(calculationCommand.getLeftOperand().getValue(), is(equalTo(new BigDecimal("10.0"))));

        assertEquals(Operation.ADD, calculationCommand.getOperation());

        assertEquals("USD", calculationCommand.getRightOperand().getCurrencyCode().get());
        assertEquals(new BigDecimal("20.01"), calculationCommand.getRightOperand().getValue());

        assertEquals("CFH", calculationCommand.getResultCurrencyCode().get());
    }

    @Test(expected = CommandFormatException.class)
    public void testParseFromStringWithMissingOperator() throws CommandFormatException {
        CalculationCommand calculationCommand = CalculationCommand.parseFromString("CNY10.0USD20.01=CFH");
    }

    //TODO: other test cases
}
