package com.epam.jamp2.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.epam.jamp2.model.CalculationCommand;
import com.epam.jamp2.model.CommandFormatException;
import com.epam.jamp2.model.UnknownCurrencyException;
import com.epam.jamp2.model.Value;
import com.epam.jamp2.service.impl.CalculationCommandExecutionServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
public class CalculationCommandExecutionServiceTest {

	@InjectMocks 
	private CalculationCommandExecutionService cmdService = new CalculationCommandExecutionServiceImpl();
	
	@Mock
	private FxRatesService fxRateService;
	
    @Before  
    public void setUp() throws IOException, UnknownCurrencyException{  
        MockitoAnnotations.initMocks(this);  
        
        when(fxRateService.convert(any(String.class), any(String.class), any(BigDecimal.class))).thenReturn(new BigDecimal(10)).thenReturn(new BigDecimal(5));

    }  
    
	@Test
	public void testCommand1() throws CommandFormatException, IOException {
		String cmdStr = "usd100.5+cny5.0=chD";
		CalculationCommand cmd = CalculationCommand.parseFromString(cmdStr);
		Value result = cmdService.calculate(cmd);
		Assert.assertThat(result.getCurrencyCode().get(), equalToIgnoringCase("CHD"));
		Assert.assertThat(result.getValue(), equalTo(new BigDecimal(15)));
	}
	
	@Test
	public void testCommand2() throws CommandFormatException, IOException {
		String cmdStr = "usd100.5*cny5.0=chD";
		CalculationCommand cmd = CalculationCommand.parseFromString(cmdStr);
		Value result = cmdService.calculate(cmd);
		Assert.assertThat(result.getCurrencyCode().get(), equalToIgnoringCase("CHD"));
		Assert.assertThat(result.getValue(), equalTo(new BigDecimal(50)));
	}
	
	@Test(expected=RuntimeException.class)
	public void testCommand3() throws CommandFormatException, IOException {
		String cmdStr = "usd100.5/chd0=chD";
		CalculationCommand cmd = CalculationCommand.parseFromString(cmdStr);
		Value result = cmdService.calculate(cmd);
		Assert.assertThat(result.getCurrencyCode().get(), equalToIgnoringCase("CHD"));
		Assert.assertThat(result.getValue(), equalTo(new BigDecimal(50)));
	}
}
