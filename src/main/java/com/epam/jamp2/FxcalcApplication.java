package com.epam.jamp2;

import com.epam.jamp2.model.CalculationCommand;
import com.epam.jamp2.model.CommandFormatException;
import com.epam.jamp2.model.Value;
import com.epam.jamp2.service.CalculationCommandExecutionService;
import com.epam.jamp2.service.impl.CalculationCommandExecutionServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FxcalcApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext ctx = SpringApplication.run(FxcalcApplication.class, args);
		CalculationCommandExecutionServiceImpl service = (CalculationCommandExecutionServiceImpl) ctx.getBean(CalculationCommandExecutionService.class);
		try {
			String validInput = (args.length >= 1)? args[0] : "usd100.0+cny5.0=cny";
			CalculationCommand cmd = CalculationCommand.parseFromString(validInput);
			Value value = service.calculate(cmd);
			System.out.println(validInput + " -> " + value.getValue() + " " + value.getCurrencyCode());
		} catch (CommandFormatException e) {
			e.printStackTrace();
		}
	}
}
