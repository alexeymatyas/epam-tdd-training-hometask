package com.epam.jamp2.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.jamp2.model.CalculationCommand;
import com.epam.jamp2.model.UnknownCurrencyException;
import com.epam.jamp2.model.Value;
import com.epam.jamp2.service.CalculationCommandExecutionService;
import com.epam.jamp2.service.FxRatesService;

/**
 * Created by Alexey on 06.12.2016.
 */
@Service
public class CalculationCommandExecutionServiceImpl implements CalculationCommandExecutionService {
    @Autowired
    FxRatesService fxRatesService;

    @Override
    public Value calculate(CalculationCommand command) {
        Optional<String> resultCurrencyCode = command.getResultCurrencyCode();
        if(!resultCurrencyCode.isPresent()) {
            if (command.getLeftOperand().getCurrencyCode().isPresent()) {
                resultCurrencyCode = command.getLeftOperand().getCurrencyCode();
            } else if(command.getRightOperand().getCurrencyCode().isPresent()) {
                resultCurrencyCode = command.getRightOperand().getCurrencyCode();
            }
        }


        try {
            BigDecimal leftValue = getConvertedValue(command.getLeftOperand(), resultCurrencyCode);
            BigDecimal rightValue = getConvertedValue(command.getRightOperand(), resultCurrencyCode);
            BigDecimal resultValue = null;

            switch (command.getOperation()) {
                case ADD:
                    resultValue = leftValue.add(rightValue);
                    break;
                case DIVIDE:
                    resultValue = leftValue.divide(rightValue);
                    break;
                case MULTIPLY:
                    resultValue = leftValue.multiply(rightValue);
                    break;
                case SUBTRACT:
                    resultValue = leftValue.subtract(rightValue);
                    break;
            }

            return new Value(resultCurrencyCode.orElse(null), resultValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BigDecimal getConvertedValue(Value value, Optional<String> targetCurrencyCode)
            throws IOException, UnknownCurrencyException {
        BigDecimal convertedValue;
        if(!value.getCurrencyCode().isPresent() || value.getCurrencyCode().get().equalsIgnoreCase(targetCurrencyCode.get())) {
            convertedValue = value.getValue();
        } else {
            convertedValue = fxRatesService.convert(value.getCurrencyCode().get(), targetCurrencyCode.get(),
                    value.getValue());
        }

        return convertedValue;
    }
}
