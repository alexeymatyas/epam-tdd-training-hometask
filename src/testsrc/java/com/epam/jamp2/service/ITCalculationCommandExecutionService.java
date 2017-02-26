package com.epam.jamp2.service;

import com.epam.jamp2.model.CalculationCommand;
import com.epam.jamp2.model.Operation;
import com.epam.jamp2.model.UnknownCurrencyException;
import com.epam.jamp2.model.Value;
import com.epam.jamp2.rest.FixerioServiceProxyConfiguration;
import com.epam.jamp2.service.impl.CalculationCommandExecutionServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, FixerioServiceProxyConfiguration.class })
public class ITCalculationCommandExecutionService {

    @Autowired
    private CalculationCommandExecutionServiceImpl target;

    private Value value;
    private Optional<String> targetCurrencyCode;
    private BigDecimal expected;



    @Test
    public void test_calculate() throws IOException, UnknownCurrencyException {

        CalculationCommand hmm = new CalculationCommand(new Value("gbp", BigDecimal.valueOf(1.0)),
                new Value("gbp", BigDecimal.valueOf(2.0)), Operation.ADD, "gbp");
        Value resultTwo = target.calculate(hmm);
        Value expectedTwo = new Value("gbp", BigDecimal.valueOf(3.0));
//        assertThat((resultTwo), hasProperty("value", equalTo(expectedTwo.getValue())));
        assertThat((resultTwo), samePropertyValuesAs(expectedTwo));

    }

}