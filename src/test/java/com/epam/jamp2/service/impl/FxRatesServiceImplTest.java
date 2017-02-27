package com.epam.jamp2.service.impl;

import com.epam.jamp2.model.FixerioResponse;
import com.epam.jamp2.model.UnknownCurrencyException;
import com.epam.jamp2.rest.FixerioServiceProxy;
import com.epam.jamp2.service.FxRatesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FxRatesServiceImplTest {

  @Mock
  FixerioServiceProxy fixerioServiceProxy;

  @InjectMocks
  FxRatesService fxRatesService = new FxRatesServiceImpl();

  private BigDecimal fromValue;
  private BigDecimal expectedValue;


  @Test(expected = UnknownCurrencyException.class)
  public void should_throw_exception_when_response_body_null() throws IOException, UnknownCurrencyException {
    Response<FixerioResponse> outterResponse = Response.success(null);
    Call<FixerioResponse> ratesCall = mock(Call.class);
    when(ratesCall.execute()).thenReturn(outterResponse);
    when(fixerioServiceProxy.getRates(anyString())).thenReturn(ratesCall);

    fxRatesService.getRates("dummy");
  }

  @Test
  public void should_calculate_correct() throws Exception {
    String dummFrom = "dummFrom";
    String dummToLowerCase = "dummTo";
    String dummToUpperCase = "DUMMTO";

    Map<String, BigDecimal> rateMap = new HashMap<>();
    rateMap.put(dummToUpperCase, new BigDecimal("5.43"));

    FixerioResponse fixerioResponse = new FixerioResponse();
    fixerioResponse.setRates(rateMap);

    Response<FixerioResponse> outterResponse = Response.success(fixerioResponse);
    Call<FixerioResponse> ratesCall = mock(Call.class);
    when(ratesCall.execute()).thenReturn(outterResponse);
    when(fixerioServiceProxy.getRates(anyString())).thenReturn(ratesCall);

    assertThat(fxRatesService.getRates(dummFrom), equalTo(rateMap));
    assertThat(fxRatesService.convert(dummFrom, dummToLowerCase, new BigDecimal("2")),
            equalTo(new BigDecimal("10.86")));

    verify(fixerioServiceProxy, times(2)).getRates(dummFrom);
  }
}
