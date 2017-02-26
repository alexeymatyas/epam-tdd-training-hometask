package com.epam.tdd;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CommandExecuterTest {

	private CommandExecuter executer;
	private CommandParser parser;
	@InjectMocks
	private CurrencyMarket market;

	@Before
	public void setup(){
		executer = new CommandExecuter();
		parser = new CommandParser();
	}
	
	@Test
	public void testWithoutMarketSupport() throws Exception{
		String line = "1USD + 3USD=";
		
		Command c = parser.parse(line);
		assertThat(c, anything());
		
		Money m = executer.executeCommand(c);
		assertThat(m, anything()); 
		assertThat(m.getVlaue(), equalTo(4f));
		assertThat(m.getCurrency(), sameInstance(Currency.USD));
		
		System.out.println(c.getQuestionStatement());
	}

	@Test(expected=Exception.class)
	public void testMandatoryMarketSupport_throwException() throws Exception{
		String line = "1USD + 3RMB=";
		
		Command c = parser.parse(line);
		assertThat(c, anything());
		
		executer.executeCommand(c);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullCommand_throwException() throws Exception{
		executer.executeCommand(null);
	}
}
