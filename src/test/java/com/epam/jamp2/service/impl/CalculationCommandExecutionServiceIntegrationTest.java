package com.epam.jamp2.service.impl;

import com.epam.jamp2.model.CalculationCommand;
import com.epam.jamp2.model.Operation;
import com.epam.jamp2.model.UnknownCurrencyException;
import com.epam.jamp2.model.Value;
import com.epam.jamp2.rest.FixerioServiceProxy;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableConfigurationProperties

public class CalculationCommandExecutionServiceIntegrationTest {

    @Autowired
    private CalculationCommandExecutionServiceImpl calculationCommandExecutionServiceImpl;

    @Autowired
    FixerioServiceProxy fixerioServiceProxy;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Test case to generate the real rates data for unit test cases
     *
     * @throws IOException write rates data to disk failed
     */
    @Test
    public void testRestApiResultsAndSetupForUnitTest() throws IOException {
        Map<String, Map<String, BigDecimal>> testRates = new HashMap<>();
        Map<String, BigDecimal> usdRates = fixerioServiceProxy.getRates("usd").execute().body().getRates();
        assert (usdRates.size() > 0);
        testRates.put("USD", usdRates);
        usdRates.keySet().forEach(key -> {
            try {
                Map<String, BigDecimal> rates = fixerioServiceProxy.getRates(key).execute().body().getRates();
                assert (rates.size() > 0);
                testRates.put(key, rates);
            } catch (IOException e) {
                throw new RuntimeException("IO Exception");
            }
        });
        FileOutputStream fos = new FileOutputStream("src/test/resources/rates.ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(testRates);
        oos.close();
    }

    @Test
    public void testLoadData() throws IOException {
        CalculationCommand calculationCommand = new CalculationCommand(
                new Value("usd", BigDecimal.valueOf(100)),
                new Value("cny", BigDecimal.valueOf(5)),
                Operation.ADD, "hkd");
        assert (calculationCommandExecutionServiceImpl.calculate(calculationCommand).getValue().doubleValue() > 0);
    }

    @Test
    public void testPositiveNumbers() throws IOException, UnknownCurrencyException {
        CalculationCommand calculationCommand = new CalculationCommand(
                new Value("usd", BigDecimal.valueOf(100)), new Value("cny", BigDecimal.valueOf(5)), Operation.ADD, "hkd");
        assertThat(calculationCommandExecutionServiceImpl.calculate(calculationCommand).getValue(), greaterThan(BigDecimal.ZERO));
    }

    @Test
    public void testUnknownCurrency() throws Exception {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("com.epam.jamp2.model.UnknownCurrencyException");
        CalculationCommand calculationCommand = new CalculationCommand(
                new Value("abc", BigDecimal.valueOf(-100)), new Value("def", BigDecimal.valueOf(0)), Operation.DIVIDE, "ghi");
        calculationCommandExecutionServiceImpl.calculate(calculationCommand);
    }
}