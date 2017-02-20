package com.epam.jamp2.service.impl;

import com.epam.jamp2.model.FixerioResponse;
import com.epam.jamp2.model.UnknownCurrencyException;
import com.epam.jamp2.service.FxRatesService;
import com.epam.jamp2.rest.FixerioServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Alexey on 05.12.2016.
 */
@Service
public class FxRatesServiceImpl implements FxRatesService {
    @Autowired
    FixerioServiceProxy fixerioServiceProxy;

    @Override
    public Map<String, BigDecimal> getRates(String baseCurrencyCode) throws IOException, UnknownCurrencyException {
        Response<FixerioResponse> response = fixerioServiceProxy.getRates(baseCurrencyCode).execute();
        if(response.body() == null) {
            throw new UnknownCurrencyException();
        }
        return response.body().getRates();
    }

    @Override
    public BigDecimal convert(String fromCcy, String toCcy, BigDecimal value) throws IOException, UnknownCurrencyException {
        Map<String, BigDecimal> rates = getRates(fromCcy);
        return value.multiply(rates.get(toCcy.toUpperCase()));
    }
}
