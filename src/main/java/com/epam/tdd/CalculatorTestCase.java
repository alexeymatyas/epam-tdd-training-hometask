package com.epam.tdd;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

public class CalculatorTestCase {
	
	//private ExpectedException 
	
	private BigDecimal a;
	private BigDecimal b;
	private CalculatorService service;
	
	@Mock 
	UnreliableServiceProxy proxy;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		a = BigDecimal.valueOf(12.0);
		b = BigDecimal.valueOf(2);
		
		service = new CalculatorService();
	}

	@After
	public void tearDown() throws Exception {
	}

	//@Test(expected=NotImplementedException.class)
	@SuppressWarnings("deprecation")
	@Test()
	public void test() {
		BigDecimal c = service.divide(a, b);
		//Assert.assertEquals("Expects 6.0", c.doubleValue(), 6.0);
		assertThat(BigDecimal.valueOf(6.0), is(equalTo(c)));
	}

	
}
