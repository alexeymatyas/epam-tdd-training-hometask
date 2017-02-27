package com.epam.jamp2.service.impl;

import com.epam.jamp2.ApplicationTestConfig;
import com.epam.jamp2.model.CalculationCommand;
import com.epam.jamp2.model.FixerioResponse;
import com.epam.jamp2.rest.FixerioServiceProxy;
import com.epam.jamp2.service.CalculationCommandExecutionService;
import com.epam.jamp2.service.FxRatesService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

/**
 * Created by Northzzn on 2017/2/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationTestConfig.class)
public class CalculationCommandExecutionServiceImplTest {

    @Mock
    private FixerioServiceProxy fixerioServiceProxy;

    @InjectMocks
    @Autowired
    FxRatesService fxRatesService;

    @Autowired
    private CalculationCommandExecutionService calculationCommandExecutionService;

    @Before
    public void setUp() throws IOException {
        FixerioResponse usdResp = new FixerioResponse();
        Map usdRates = new HashMap();
        usdRates.put("CNY", new BigDecimal("6.90"));
        usdResp.setRates(usdRates);

        Call<FixerioResponse> usdCall = mock(Call.class);
        when(usdCall.execute()).thenReturn(Response.success(usdResp));
        when(fixerioServiceProxy.getRates("USD")).thenReturn(usdCall);
    }

    @Test
    public void testGetRatesCallingTimesWithAllSameInputCcy() throws Exception {
        CalculationCommand calculationCommand = CalculationCommand.parseFromString("CNY10.0+CNY20.01=CNY");
        calculationCommandExecutionService.calculate(calculationCommand);
        verify(fixerioServiceProxy, times(0)).getRates(anyObject());
    }

    @Test
    public void testGetRatesCallingTimesWithOneSameInputCcy() throws Exception {
        CalculationCommand calculationCommand = CalculationCommand.parseFromString("CNY10.0+USD20.01=CNY");
        calculationCommandExecutionService.calculate(calculationCommand);
        verify(fixerioServiceProxy, times(1)).getRates(anyObject());
    }

    @Test
    public void testGetRatesCallingTimesWithNoneSameInputCcy() throws Exception {
        CalculationCommand calculationCommand = CalculationCommand.parseFromString("USD10.0+USD20.01=CNY");
        calculationCommandExecutionService.calculate(calculationCommand);
        verify(fixerioServiceProxy, times(2)).getRates(anyObject());
    }

    //TODO: other test cases
}
