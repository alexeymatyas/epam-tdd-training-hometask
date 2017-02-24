package com.epam.jamp2.service.impl;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.HashMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import javax.annotation.Resource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epam.jamp2.model.CalculationCommand;
import com.epam.jamp2.model.Operation;
import com.epam.jamp2.model.Value;
import com.epam.jamp2.service.CalculationCommandExecutionService;
import com.epam.jamp2.service.FxRatesService;

public class CalculationCommandExecutionServiceImplTest {
	
	@InjectMocks  
    @Resource(name= "calculateService")  
	CalculationCommandExecutionServiceImpl calculateService; 
	
	@Mock  
	FxRatesService mockFxRatesService;
	
	//private CalculationCommandExecutionServiceImpl calculateService = new CalculationCommandExecutionServiceImpl();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		 MockitoAnnotations.initMocks(this);
	}

	@Test
	//normal test
	public void testCalculate() {
		
		//set inputs
		Value valueLeft = new Value("USD",new BigDecimal("100.10"));
		Value valueRight = new Value("USD",new BigDecimal("210.31"));
		Operation operation = Operation.ADD;
		String resultCurrency = "USD";
		CalculationCommand calCommand = new CalculationCommand(valueLeft,valueRight,operation,resultCurrency);
		Value assertValue = new Value("USD",new BigDecimal("310.41"));
		
		//get result
		Value result = calculateService.calculate(calCommand);
		
		//assert
		assertEquals(result.getCurrencyCode(),assertValue.getCurrencyCode());
		assertEquals(result.getValue().compareTo(assertValue.getValue()),0);
		
	}

	@Test
	//normal test USD&RMB
	public void testCalculateUSDRMB() {
		
		//set inputs
		Value valueLeft = new Value("USD",new BigDecimal("100.10"));
		Value valueRight = new Value("RMB",new BigDecimal("210.31"));
		Operation operation = Operation.ADD;
		String resultCurrency = "USD";
		CalculationCommand calCommand = new CalculationCommand(valueLeft,valueRight,operation,resultCurrency);
		Value assertValue = new Value("USD",new BigDecimal("310.41"));
		
		//get result
		Value result = calculateService.calculate(calCommand);
		
		//assert
		assertEquals(result.getCurrencyCode(),assertValue.getCurrencyCode());
		//assertEquals(result.getValue().compareTo(assertValue.getValue()),0);
		
	}
	
	@Test
	//normal test
	public void testCalculateRMB() {
		
		//set inputs
		Value valueLeft = new Value("USD",new BigDecimal("100.10"));
		Value valueRight = new Value("RMB",new BigDecimal("210.31"));
		Operation operation = Operation.ADD;
		String resultCurrency = "RMB";
		CalculationCommand calCommand = new CalculationCommand(valueLeft,valueRight,operation,resultCurrency);
		Value assertValue = new Value("USD",new BigDecimal("310.41"));
		
		//get result
		Value result = calculateService.calculate(calCommand);
		
		//assert
		assertThat("correct result",result,is(equalTo(assertValue)));
	}
}
