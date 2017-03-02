package com.epam.tdd;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class CommandParserTest {
	
	private CommandParser parser;

	@Before
	public void setup(){
		this.parser = new CommandParser();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullCommandLine(){
		parser.parse(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidCurrency(){
		String line = "12UUU+2.3USD";
		parser.parse(line);
	}
	
	@Test
	public void testValidCurrency(){
		String line = "12RMB+2.3USD";
		Command c = parser.parse(line);
		assertThat(c, anything());
		
		assertThat(c.first().getCurrency(), sameInstance(Currency.RMB));
		assertThat(c.second().getCurrency(), sameInstance(Currency.USD));
		
		assertThat(c.first().getVlaue(), equalTo(12f));
		assertThat(c.second().getVlaue(), equalTo(2.3f));
		
		assertTrue(c.isAddOperation());
		assertTrue(c.isSourceCurrDifferent());
		assertFalse(c.isTargetCurrDifferent());
		assertNull(c.result());
		
	}
	
	@Test
	public void testValidCase1(){
		String line = "12RMB+2.3USD";
		Command c = parser.parse(line);
		assertThat(c, anything());
		
		assertThat(c.first().getVlaue(), equalTo(12f));
		assertThat(c.second().getVlaue(), equalTo(2.3f));
		
		assertTrue(c.isAddOperation());
		assertTrue(c.isSourceCurrDifferent());
		assertFalse(c.isTargetCurrDifferent());
		assertNull(c.result());
		
	}
	
	@Test
	public void testValidCase2(){
		String line = "12RMB+2.3USD=HUF";
		Command c = parser.parse(line);
		assertThat(c, anything());
		
		assertThat(c.first().getCurrency(), sameInstance(Currency.RMB));
		assertThat(c.second().getCurrency(), sameInstance(Currency.USD));
		
		assertThat(c.result(), anything());
		assertThat(c.result().getCurrency(), sameInstance(Currency.HUF));
		
		assertThat(c.first().getVlaue(), equalTo(12f));
		assertThat(c.second().getVlaue(), equalTo(2.3f));
		
		assertTrue(c.isAddOperation());
		assertTrue(c.isSourceCurrDifferent());
		assertTrue(c.isTargetCurrDifferent());
		
	}
	
	@Test
	public void testValidCaseWithEqulsOnly(){
		String line = "12RMB+2.3USD=";
		Command c = parser.parse(line);
		assertThat(c, anything());
		assertNull(c.result());
	}
	
	@Test
	public void testValidCaseWithEqulsAndSpace(){
		String line = "12RMB+2.3USD= \t";
		Command c = parser.parse(line);
		assertThat(c, anything());
		assertNull(c.result());
	}
	
	@Test
	public void testCommandQuestionString(){
		String line = "12RMB+2.3USD= \t";
		Command c = parser.parse(line);
		assertThat(c.getQuestionStatement(), anything());
		assertThat(c.getQuestionStatement(), equalTo("12.0RMB + 2.3USD = "));
	}

	@Test
	public void testNoCasesentive(){
		String line = "12Rmb+2.3Usd= \t";
		Command c = parser.parse(line);
		assertThat(c.getQuestionStatement(), anything());
		assertThat(c.getQuestionStatement(), equalTo("12.0RMB + 2.3USD = "));
	}

}
