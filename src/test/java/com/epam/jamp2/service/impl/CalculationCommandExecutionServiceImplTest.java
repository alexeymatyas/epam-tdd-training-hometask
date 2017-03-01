package com.epam.jamp2.service.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.epam.jamp2.model.CalculationCommand;
import com.epam.jamp2.model.CommandFormatException;
import com.epam.jamp2.model.FixerioResponse;
import com.epam.jamp2.model.Value;
import com.epam.jamp2.rest.FixerioServiceProxy;
import com.epam.jamp2.service.CalculationCommandExecutionService;
import com.epam.jamp2.service.FxRatesService;

import retrofit2.Call;
import retrofit2.Response;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CalculationCommandExecutionServiceImplTest {

	@Mock
	private FixerioServiceProxy fixerioServiceProxy;
	
	@InjectMocks
	@Autowired
	private FxRatesService fxRatesService;

	@Autowired
	private CalculationCommandExecutionService calculationCommandExecutionService;

	@Before
	public void setUp() throws IOException {
		FixerioResponse response = new FixerioResponse();
		Map<String, BigDecimal> USD2CNYRate = new HashMap<String, BigDecimal>();
		USD2CNYRate.put("CNY", new BigDecimal("7.00"));
		response.setRates(USD2CNYRate);
		@SuppressWarnings("unchecked")
		Call<FixerioResponse> call = mock(Call.class);
	    given(call.execute()).willReturn(Response.success(response));
		given(fixerioServiceProxy.getRates("USD")).willReturn(call);
	}

	@Test
	public void testShouldNotCallForRateWhenInputCcysAreSameAsTargetCcy() throws CommandFormatException, IOException {

		 CalculationCommand calculationCommand = CalculationCommand.parseFromString("CNY7.00+CNY1.00=CNY");
		 calculationCommandExecutionService.calculate(calculationCommand);
		 verify(fixerioServiceProxy, times(0)).getRates(anyObject());

	}
	
	@Test
	public void testShouldCallForRateForOneTimeWhenInputCcysHasOneSameAsTargetCcy() throws CommandFormatException, IOException {

		 CalculationCommand calculationCommand = CalculationCommand.parseFromString("CNY7.00+USD1.00=CNY");
		 calculationCommandExecutionService.calculate(calculationCommand);
		 verify(fixerioServiceProxy, times(1)).getRates(anyObject());

	}
	
	@Test
	public void testShouldCallForRateForTwoTimesWhenInputCcysAreNotSameAsTargetCcy() throws CommandFormatException, IOException {

		 CalculationCommand calculationCommand = CalculationCommand.parseFromString("USD1.00+USD2.00=CNY");
		 calculationCommandExecutionService.calculate(calculationCommand);
		 verify(fixerioServiceProxy, times(2)).getRates(anyObject());

	}
	
	@Test
	public void testShouldReturnCorrectResultWhenDoAdd() throws CommandFormatException, IOException {

		 CalculationCommand calculationCommand = CalculationCommand.parseFromString("CNY7.00+USD1.00=CNY");
		 Value value = calculationCommandExecutionService.calculate(calculationCommand);
		 assertThat(value.getValue(), is(equalTo(new BigDecimal("14.0000"))));
		 assertEquals(Optional.of("CNY"), value.getCurrencyCode());

	}
	
	@Test
	public void testShouldReturnCorrectResultWhenDoMultiply() throws CommandFormatException, IOException {

		 CalculationCommand calculationCommand = CalculationCommand.parseFromString("CNY7.00*USD1.00=CNY");
		 Value value = calculationCommandExecutionService.calculate(calculationCommand);
		 assertThat(value.getValue(), is(equalTo(new BigDecimal("49.000000"))));
		 assertEquals(Optional.of("CNY"), value.getCurrencyCode());

	}
	
	@Test
	public void testShouldReturnCorrectResultWhenDoDivide() throws CommandFormatException, IOException {

		 CalculationCommand calculationCommand = CalculationCommand.parseFromString("CNY7.00/USD1.00=CNY");
		 Value value = calculationCommandExecutionService.calculate(calculationCommand);
		 assertThat(value.getValue(), is(equalTo(new BigDecimal("1"))));
		 assertEquals(Optional.of("CNY"), value.getCurrencyCode());

	}
	
	@Test(expected = CommandFormatException.class)
	public void testShouldThrowCommandFormatExceptionWhenFormatInvalid() throws CommandFormatException, IOException {
		 CalculationCommand.parseFromString("CNY7.00&USD1.00=CNY");
	}

}
