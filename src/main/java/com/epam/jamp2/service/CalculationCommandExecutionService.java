package com.epam.jamp2.service;

import com.epam.jamp2.model.CalculationCommand;
import com.epam.jamp2.model.Value;

import java.io.IOException;

/**
 * Created by Alexey on 06.12.2016.
 */
public interface CalculationCommandExecutionService {
    Value calculate(CalculationCommand command) throws IOException;
}
