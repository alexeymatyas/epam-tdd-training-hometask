package com.epam.jamp2.model;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

//@RunWith(SpringJUnit4ClassRunner.class)
public class CalculationCommandTest {

	@Test
	public void testCommand1() throws CommandFormatException {
		String cmdStr = "usd100.5+cny5.0=chD";
		CalculationCommand cmd = CalculationCommand.parseFromString(cmdStr);
		Value leftOper = cmd.getLeftOperand();
		Value rightOper = cmd.getRightOperand();
		Operation oper = cmd.getOperation();
		Optional<String> ccy = cmd.getResultCurrencyCode();
		Assert.assertThat(leftOper.getCurrencyCode().get(), equalToIgnoringCase("USD"));
		Assert.assertThat(rightOper.getCurrencyCode().get(), equalToIgnoringCase("CNY"));
		Assert.assertThat(leftOper.getValue(), equalTo(new BigDecimal("100.5")));
		Assert.assertThat(rightOper.getValue(), equalTo(new BigDecimal("5.0")));
		Assert.assertThat(oper, equalTo(Operation.ADD));
		Assert.assertThat(ccy.get(), equalToIgnoringCase("CHD"));
	}
	
	@Test
	public void testCommand2() throws CommandFormatException {
		String cmdStr = "usd100.0/cny5.1=chD";
		CalculationCommand cmd = CalculationCommand.parseFromString(cmdStr);
		Value leftOper = cmd.getLeftOperand();
		Value rightOper = cmd.getRightOperand();
		Operation oper = cmd.getOperation();
		Optional<String> ccy = cmd.getResultCurrencyCode();
		Assert.assertThat(leftOper.getCurrencyCode().get(), equalToIgnoringCase("USD"));
		Assert.assertThat(rightOper.getCurrencyCode().get(), equalToIgnoringCase("CNY"));
		Assert.assertThat(leftOper.getValue(), equalTo(new BigDecimal("100.0")));
		Assert.assertThat(rightOper.getValue(), equalTo(new BigDecimal("5.1")));
		Assert.assertThat(oper, equalTo(Operation.DIVIDE));
		Assert.assertThat(ccy.get(), equalToIgnoringCase("CHD"));
	}
	
	@Test
	public void testCommand3() throws CommandFormatException {
		String cmdStr = "usd100.0*cny5.1=JPY";
		CalculationCommand cmd = CalculationCommand.parseFromString(cmdStr);
		Value leftOper = cmd.getLeftOperand();
		Value rightOper = cmd.getRightOperand();
		Operation oper = cmd.getOperation();
		Optional<String> ccy = cmd.getResultCurrencyCode();
		Assert.assertThat(leftOper.getCurrencyCode().get(), equalToIgnoringCase("USD"));
		Assert.assertThat(rightOper.getCurrencyCode().get(), equalToIgnoringCase("CNY"));
		Assert.assertThat(leftOper.getValue(), equalTo(new BigDecimal("100.0")));
		Assert.assertThat(rightOper.getValue(), equalTo(new BigDecimal("5.1")));
		Assert.assertThat(oper, equalTo(Operation.MULTIPLY));
		Assert.assertThat(ccy.get(), equalToIgnoringCase("JPY"));
		
	}
	
	@Test
	public void testCommand4() throws CommandFormatException {
		String cmdStr = "usd100.0-cny5.1=JPY";
		CalculationCommand cmd = CalculationCommand.parseFromString(cmdStr);
		Value leftOper = cmd.getLeftOperand();
		Value rightOper = cmd.getRightOperand();
		Operation oper = cmd.getOperation();
		Optional<String> ccy = cmd.getResultCurrencyCode();
		Assert.assertThat(leftOper.getCurrencyCode().get(), equalToIgnoringCase("USD"));
		Assert.assertThat(rightOper.getCurrencyCode().get(), equalToIgnoringCase("CNY"));
		Assert.assertThat(leftOper.getValue(), equalTo(new BigDecimal("100.0")));
		Assert.assertThat(rightOper.getValue(), equalTo(new BigDecimal("5.1")));
		Assert.assertThat(oper, equalTo(Operation.SUBTRACT));
		Assert.assertThat(ccy.get(), equalToIgnoringCase("JPY"));
		
	}
	
	@Test(expected=CommandFormatException.class)
	public void testCommand5() throws CommandFormatException {
		String cmdStr = "usd100.0%cny5.1=JPY";
		CalculationCommand cmd = CalculationCommand.parseFromString(cmdStr);
		cmd.getOperation();
	}
}
