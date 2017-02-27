package com.epam.jamp2.service;

import com.epam.jamp2.model.CalculationCommand;
import com.epam.jamp2.model.Operation;
import com.epam.jamp2.model.UnknownCurrencyException;
import com.epam.jamp2.model.Value;
import com.epam.jamp2.rest.FixerioServiceProxyConfiguration;
import com.epam.jamp2.service.impl.CalculationCommandExecutionServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
//@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings("InstanceMethodNamingConvention")
@ContextConfiguration(classes = {TestConfig.class, FixerioServiceProxyConfiguration.class })
public class ITCalculationCommandExecutionService {

    // Performs the same job as @RunWith(SpringJUnit4ClassRunner.class)
    private TestContextManager testContextManager;
    @Before
    public void setUpStringContext() throws Exception {
        testContextManager = new TestContextManager(getClass());
        testContextManager.prepareTestInstance(this);
    }

    @Autowired
    private CalculationCommandExecutionServiceImpl target;

    private CalculationCommand command;
    private Value expected;

    // Parameterized data for tests
    @Parameters(name = "{index}: test calculate({0}, {1}) = {2}")
    public static Collection<Object[]> data() {

        return Arrays.asList(new Object[][]{
                {new Value("gbp", BigDecimal.valueOf(1.0)), new Value("gbp", BigDecimal.valueOf(2.0))
                        , Operation.ADD, "gbp", new Value("gbp", BigDecimal.valueOf(3.0))}
                                        ,
                {new Value("gbp", BigDecimal.valueOf(1.0)), new Value("cny", BigDecimal.valueOf(2.0))
                        , Operation.ADD, "cny", new Value("cny", BigDecimal.valueOf(30.0))}
        });
    }

    // constructor
    public ITCalculationCommandExecutionService(Value leftOperand, Value rightOperand, Operation operation
                , String resultCurrencyCode, Value expected) {
        this.command = new CalculationCommand(leftOperand, rightOperand, operation, resultCurrencyCode);
        this.expected = expected;
    }

    // constructor
//    public ITCalculationCommandExecutionService() {
//    }

    @Test
    public void test_calculate() throws IOException, UnknownCurrencyException {
        Value result = target.calculate(command);
        assertThat((result), samePropertyValuesAs(expected));
    }

}