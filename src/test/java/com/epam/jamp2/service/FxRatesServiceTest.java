package com.epam.jamp2.service;

import com.epam.jamp2.model.FixerioResponse;
import com.epam.jamp2.model.UnknownCurrencyException;
import com.epam.jamp2.rest.FixerioServiceProxy;
import com.epam.jamp2.mock.FxRateMockData;
import com.epam.jamp2.service.impl.FxRatesServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.BigDecimalCloseTo.closeTo;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FxRatesServiceTest {

    @Mock
    private FixerioServiceProxy fixerioServiceProxy;

    @Mock
    private Call<FixerioResponse> fixerioResponseCall;

    @InjectMocks
    FxRatesService fxRatesService = new FxRatesServiceImpl();

    @Test
    public void test_convert_USD_2_CNY() throws IOException, UnknownCurrencyException {
        mockFxRates(FxRateMockData.USD);
        BigDecimal convertedValue = fxRatesService.convert(FxRateMockData.USD, FxRateMockData.CNY, new BigDecimal(1000));
        Assert.assertThat(convertedValue, is(closeTo(new BigDecimal(146.0), new BigDecimal(0.1))));
    }

    @Test(expected = NullPointerException.class)
    public void test_no_matched_target_ccy() throws IOException, UnknownCurrencyException {
        mockFxRates(FxRateMockData.USD);
        fxRatesService.convert(FxRateMockData.USD, "TES", new BigDecimal(1000));
    }

    @Test(expected = UnknownCurrencyException.class)
    public void test_unknown_ccy() throws IOException, UnknownCurrencyException {
        when(fixerioResponseCall.execute()).thenReturn(Response.success(null));
        when(fixerioServiceProxy.getRates(FxRateMockData.USD)).thenReturn(fixerioResponseCall);

        fxRatesService.convert(FxRateMockData.USD, FxRateMockData.CNY, new BigDecimal(1000));
    }

    private void mockFxRates(String fromCcy) throws
            IOException, UnknownCurrencyException {
        when(fixerioResponseCall.execute()).thenReturn(mockResponse(fromCcy));
        when(fixerioServiceProxy.getRates(fromCcy)).thenReturn(fixerioResponseCall);
    }

    private Response<FixerioResponse> mockResponse(String baseCcy) {
        return Response.success(FxRateMockData.mockResponseRates(baseCcy));
    }
}
