package com.epam.jamp2.service.impl;

import com.epam.jamp2.model.*;
import com.epam.jamp2.rest.FixerioServiceProxy;
import okhttp3.Request;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CalculationCommandExecutionServiceTest {

    @Mock
    private FixerioServiceProxy fixerioServiceProxy;

    @InjectMocks
    private FxRatesServiceImpl fxRatesServiceImpl;

    private CalculationCommandExecutionServiceImpl calculationCommandExecutionService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Read rates from rates.ser and mock remote api
     *
     * @throws Exception reading rates data from disk failed exception
     */
    @Before
    public void setupMocks() throws Exception {
        //Sample currency converting rates from rates.ser
        FileInputStream fis = new FileInputStream("src/test/resources/rates.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        Map<String, Map<String, BigDecimal>> testRates = (Map<String, Map<String, BigDecimal>>) ois.readObject();
        ois.close();

        //Mock the getRates method of fixerioServiceProxy
        when(fixerioServiceProxy.getRates(any(String.class))).thenAnswer(invocationOnMock ->
                new Call<FixerioResponse>() {
                    @Override
                    public Response<FixerioResponse> execute() throws IOException {
                        FixerioResponse response = new FixerioResponse();
                        String currency = (String) invocationOnMock.getArguments()[0];
                        response.setRates(testRates.get(currency.toUpperCase()));
                        return Response.success(response);
                    }

                    @Override
                    public void enqueue(Callback<FixerioResponse> callback) {
                    }

                    @Override
                    public boolean isExecuted() {
                        return false;
                    }

                    @Override
                    public void cancel() {
                    }

                    @Override
                    public boolean isCanceled() {
                        return false;
                    }

                    @Override
                    public Call<FixerioResponse> clone() {
                        return null;
                    }

                    @Override
                    public Request request() {
                        return null;
                    }
                });

        //Inject mocked service to related class
        calculationCommandExecutionService = new CalculationCommandExecutionServiceImpl();
        calculationCommandExecutionService.fxRatesService = fxRatesServiceImpl;
    }

    @Test
    public void testConversionUnnecessary() throws IOException, UnknownCurrencyException {
        CalculationCommand calculationCommand = new CalculationCommand(
                new Value("usd", BigDecimal.valueOf(100)),
                new Value("usd", BigDecimal.valueOf(5)),
                Operation.ADD, "usd");
        assertThat(calculationCommandExecutionService.calculate(calculationCommand).getValue(),
                greaterThan(BigDecimal.ZERO));
        calculationCommand = new CalculationCommand(
                new Value("cny", BigDecimal.valueOf(100)),
                new Value("cny", BigDecimal.valueOf(5)),
                Operation.SUBTRACT, "cny");
        assertThat(calculationCommandExecutionService.calculate(calculationCommand).getValue(),
                greaterThan(BigDecimal.ZERO));
        calculationCommand = new CalculationCommand(
                new Value("hkd", BigDecimal.valueOf(100)),
                new Value("hkd", BigDecimal.valueOf(5)),
                Operation.MULTIPLY, "hkd");
        assertThat(calculationCommandExecutionService.calculate(calculationCommand).getValue(),
                greaterThan(BigDecimal.ZERO));
        calculationCommand = new CalculationCommand(
                new Value("jpy", BigDecimal.valueOf(100)),
                new Value("jpy", BigDecimal.valueOf(5)),
                Operation.DIVIDE, "jpy");
        assertThat(calculationCommandExecutionService.calculate(calculationCommand).getValue(),
                greaterThan(BigDecimal.ZERO));
        verify(fixerioServiceProxy, never()).getRates(any(String.class));
    }

    @Test
    public void testPositiveNumbers() throws IOException, UnknownCurrencyException {
        CalculationCommand calculationCommand = new CalculationCommand(
                new Value("usd", BigDecimal.valueOf(100)), new Value("cny", BigDecimal.valueOf(5)), Operation.ADD, "hkd");
        assertThat(calculationCommandExecutionService.calculate(calculationCommand).getValue(), greaterThan(BigDecimal.ZERO));
        verify(fixerioServiceProxy, times(2)).getRates(any(String.class));
    }

    @Test
    public void testNegativeNumbers() throws Exception {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Division by zero");
        CalculationCommand calculationCommand = new CalculationCommand(
                new Value("usd", BigDecimal.valueOf(-100)), new Value("cny", BigDecimal.valueOf(0)), Operation.DIVIDE, "hkd");
        calculationCommandExecutionService.calculate(calculationCommand);
    }

    @Test
    public void testMissingCurrency() throws IOException, UnknownCurrencyException {
        CalculationCommand calculationCommand = new CalculationCommand(
                new Value("usd", BigDecimal.valueOf(100)), new Value("cny", BigDecimal.valueOf(5)), Operation.MULTIPLY, null);
        assertThat(calculationCommandExecutionService.calculate(calculationCommand).getCurrencyCode().get(), equalTo("usd"));
        calculationCommand = new CalculationCommand(
                new Value(null, BigDecimal.valueOf(100)), new Value("cny", BigDecimal.valueOf(5)), Operation.ADD, null);
        assertThat(calculationCommandExecutionService.calculate(calculationCommand).getCurrencyCode().get(), equalTo("cny"));
    }

}
