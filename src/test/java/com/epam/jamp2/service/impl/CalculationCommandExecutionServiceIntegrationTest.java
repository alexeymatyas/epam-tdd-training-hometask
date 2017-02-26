package com.epam.jamp2.service.impl;

import com.epam.jamp2.model.CalculationCommand;
import com.epam.jamp2.model.Operation;
import com.epam.jamp2.model.Value;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableConfigurationProperties

public class CalculationCommandExecutionServiceIntegrationTest {

    @Autowired
    private CalculationCommandExecutionServiceImpl calculationCommandExecutionServiceImpl;

    @Test
    public void testLoadData() throws IOException {
        CalculationCommand calculationCommand = new CalculationCommand(
                new Value("usd", BigDecimal.valueOf(100)), new Value("cny", BigDecimal.valueOf(5)), Operation.ADD, "hkd");
        assert (calculationCommandExecutionServiceImpl.calculate(calculationCommand).getValue().doubleValue() > 0);
    }
}