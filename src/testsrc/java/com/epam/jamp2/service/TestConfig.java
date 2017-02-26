package com.epam.jamp2.service;

import com.epam.jamp2.service.impl.CalculationCommandExecutionServiceImpl;
import org.junit.Before;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.epam.jamp2"})
public class TestConfig {

    @Bean
    CalculationCommandExecutionServiceImpl calculationCommandExecutionServiceImpl() {
        return new CalculationCommandExecutionServiceImpl();
    }
}