package com.epam.jamp2.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Created by Alexey on 05.12.2016.
 */
public class FixerioResponse {
    private String base;
    private Date date;
    private Map<String, BigDecimal> rates;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }

    public void setRates(Map<String, BigDecimal> rates) {
        this.rates = rates;
    }
}
