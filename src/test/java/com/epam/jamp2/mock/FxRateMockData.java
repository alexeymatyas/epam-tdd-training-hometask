package com.epam.jamp2.mock;

import com.epam.jamp2.model.FixerioResponse;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class FxRateMockData {

    public static final String USD = "USD";
    public static final String CNY = "CNY";
    public static final String EUR = "EUR";

    public static FixerioResponse mockResponseRates(String baseCurrencyCode) {
        FixerioResponse response = new FixerioResponse();

        response.setBase(baseCurrencyCode);
        switch (baseCurrencyCode) {
            case USD:
                response.setRates(mockUsdMap());
                break;
            case CNY:
                response.setRates(mockCnyMap());
                break;
            default: break;
        }

        return response;
    }

    public static Map<String, BigDecimal> mockUsdMap() {
        Map<String, BigDecimal> ccyMap = new HashMap<>();
        ccyMap.put(CNY, new BigDecimal(0.146));
        ccyMap.put(EUR, new BigDecimal(0.946));
        return ccyMap;
    }

    public static Map<String, BigDecimal> mockCnyMap() {
        Map<String, BigDecimal> ccyMap = new HashMap<>();
        ccyMap.put(USD, new BigDecimal(6.866));
        ccyMap.put(EUR, new BigDecimal(0.138));
        return ccyMap;
    }
}
