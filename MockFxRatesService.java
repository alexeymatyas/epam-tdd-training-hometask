package com.epam.jamp2.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.epam.jamp2.model.FixerioResponse;
import com.epam.jamp2.model.UnknownCurrencyException;
import com.epam.jamp2.rest.FixerioServiceProxy;
import com.epam.jamp2.service.FxRatesService;

import retrofit2.Response;

public class MockFxRatesService implements FxRatesService {
	
	public Map<String, BigDecimal> rates = new HashMap<String, BigDecimal>();

    @Override
    public Map<String, BigDecimal> getRates(String baseCurrencyCode) throws IOException, UnknownCurrencyException {

        return null;
    }

    @Override
    public BigDecimal convert(String fromCcy, String toCcy, BigDecimal value) throws IOException, UnknownCurrencyException {
        //Map<String, BigDecimal> rates = getRates(fromCcy);
    	

        return value.multiply(rates.get(toCcy.toUpperCase()));
    }
}
