package com.epam.jamp2.service.impl;

import com.epam.jamp2.model.CalculationCommand;
import com.epam.jamp2.model.Operation;
import com.epam.jamp2.model.Value;
import com.epam.jamp2.rest.FixerioServiceProxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CalculationCommandExecutionServiceTest {
    @Mock
    FixerioServiceProxy fixerioServiceProxy;

    @InjectMocks
    CalculationCommandExecutionServiceImpl calculationCommandExecutionService;

    @Before
    public void setupMocks() {
        //when(fixerioServiceProxy.getRates("cny")).then(doReturn(10));
        calculationCommandExecutionService = new CalculationCommandExecutionServiceImpl();
    }

    @Test
    public void testCalculateCommand() {
        CalculationCommand calculationCommand = new CalculationCommand(
                new Value("usd", BigDecimal.valueOf(100)), new Value("cny", BigDecimal.valueOf(5)), Operation.ADD, "cfh");
        System.out.println(calculationCommandExecutionService.calculate(calculationCommand));
    }
}
