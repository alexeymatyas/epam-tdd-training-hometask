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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(value = Parameterized.class)
public class TestCalculationCommandExecutionService {

    private CalculationCommandExecutionServiceImpl target;

    private Value value;
    private Optional<String> targetCurrencyCode;
    private BigDecimal expected;

    // Parameterized data for tests
    @Parameters(name = "{index}: test getConvertedValue({0}, {1}) = {2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new Value("gbp", BigDecimal.valueOf(1.0)), Optional.of("cny")
                        , BigDecimal.valueOf(10.0)},
                {new Value("gbp", BigDecimal.valueOf(1.0)), Optional.of("cny")
                        , BigDecimal.valueOf(10.0)}
        });
    }

    // constructor
    public TestCalculationCommandExecutionService(Value value, Optional<String> targetCurrencyCode, BigDecimal expected) {
        this.value = value;
        this.targetCurrencyCode = targetCurrencyCode;
        this.expected = expected;
    }

    @Before
    public void beforeTest() throws IOException, UnknownCurrencyException {
        this.target = new CalculationCommandExecutionServiceImpl();

        FxRatesService fxRatesService = mock(FxRatesService.class);
        when(fxRatesService.convert(value.getCurrencyCode().get(), targetCurrencyCode.get(), value.getValue())).thenReturn(expected);
        this.target.setFxRatesService(fxRatesService);
    }

    @Test
    public void test_getConvertedValue() throws IOException, UnknownCurrencyException {
        BigDecimal result = target.getConvertedValue(value, targetCurrencyCode);
        assertThat((result), is(expected));
    }
    @Test
    public void test_calculate() throws IOException, UnknownCurrencyException {

        CalculationCommand hmm = new CalculationCommand(new Value("gbp", BigDecimal.valueOf(1.0)),
                new Value("gbp", BigDecimal.valueOf(2.0)), Operation.ADD, "gbp");
        Value resultTwo = target.calculate(hmm);
        Value expectedTwo = new Value("gbp", BigDecimal.valueOf(3.0));
        assertThat((resultTwo), samePropertyValuesAs(expectedTwo));    // Note: could use the hasProperty method.
        assertThat((resultTwo), hasProperty("value", equalTo(expectedTwo.getValue())));

    }

}