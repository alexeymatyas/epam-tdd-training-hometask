package com.epam.jamp2.service;

import com.epam.jamp2.model.UnknownCurrencyException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Alexey on 05.12.2016.
 */
public interface FxRatesService {
    Map<String, BigDecimal> getRates(String baseCurrencyCode) throws IOException, UnknownCurrencyException;
    BigDecimal convert(String fromCcy, String toCcy, BigDecimal value) throws IOException, UnknownCurrencyException;
}
