package com.epam.jamp2.service.impl;

import com.epam.jamp2.model.CalculationCommand;
import com.epam.jamp2.model.CommandFormatException;
import com.epam.jamp2.model.UnknownCurrencyException;
import com.epam.jamp2.model.Value;
import com.epam.jamp2.service.CalculationCommandExecutionService;
import com.epam.jamp2.service.FxRatesService;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CalculationCommandExecutionServiceImplTest {
  private static CalculationCommandExecutionService calculationService;

  @Mock
  FxRatesService mockedFXRatesService;

  @InjectMocks
  private CalculationCommandExecutionService mockedCalculationService = new CalculationCommandExecutionServiceImpl();

  @BeforeClass
  public static void setup(){
    calculationService = new CalculationCommandExecutionServiceImpl();
  }

  @AfterClass
  public static void teardown() {
    calculationService = null;
  }

  @Test
  public void shouldCalculateCorrectWithUSDCurrency() {
    try {
      CalculationCommand command = CalculationCommand.parseFromString("usd54.13+usd25.96=usd");
      Value result = calculationService.calculate(command);
      assertThat(result.getCurrencyCode(), equalTo(Optional.of("usd")));
      assertThat(result.getValue(), equalTo(new BigDecimal("80.09")));

    } catch (CommandFormatException e) {
      e.printStackTrace();
      Assert.fail("Calculation failed, CommandFormatException");
    } catch (IOException e) {
      e.printStackTrace();
      Assert.fail("Calculation failed, IOException");
    }
  }

  @Test(expected = CommandFormatException.class)
  public void shouldThrowFormatExceptionWhenWrongExpression() throws CommandFormatException{
    CalculationCommand.parseFromString("dummy1+dummy2=dummy");
  }

  @Test(expected = RuntimeException.class)
  public void should_throw_runtime_exception_when_rate_service_ioexception() throws IOException,UnknownCurrencyException{
    try {
      CalculationCommand command = CalculationCommand.parseFromString("abc54.13+usd25.96=usd");
      doThrow(IOException.class).when(mockedFXRatesService).convert(anyString(),anyString(),any(BigDecimal.class));

      Value result = mockedCalculationService.calculate(command);
      Assert.assertEquals("Currency is not correct.", Optional.of("usd"), result.getCurrencyCode());
      Assert.assertEquals("Value is not correct.", new BigDecimal("80.09"), result.getValue());
    } catch (CommandFormatException e) {
      e.printStackTrace();
      Assert.fail("Calculation failed, CommandFormatException");
    }
  }

  @Test
  public void should_not_invoke_rate_service_when_currency_all_the_same() throws CommandFormatException, IOException, UnknownCurrencyException {
    CalculationCommand command = CalculationCommand.parseFromString("ddd54.13+ddd25.96=ddd");
    Value result = mockedCalculationService.calculate(command);

    verify(mockedFXRatesService, times(0)).convert(anyString(),anyString(),any(BigDecimal.class));
  }

}
