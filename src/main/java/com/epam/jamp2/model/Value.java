package com.epam.jamp2.model;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by Alexey on 05.12.2016.
 */
public class Value {
    private Optional<String> currencyCode;
    private BigDecimal value;

    public Value(String currencyCode, BigDecimal value) {
        this.currencyCode = Optional.ofNullable(currencyCode);
        this.value = value;
    }

    public Optional<String> getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(Optional<String> currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
