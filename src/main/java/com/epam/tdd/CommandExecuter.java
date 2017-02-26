package com.epam.tdd;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class CommandExecuter {

	private CurrencyMarket market;
	
	public void setMarket(CurrencyMarket market){
		this.market = market;
	}
	
	public Money executeCommand(Command c) throws Exception{
		if(c == null){
			throw new IllegalArgumentException("No command provided yet");
		}
		
		boolean needMarket = (c.isSourceCurrDifferent() || c.isTargetCurrDifferent());
		if(market == null && needMarket){
			throw new Exception("No market for exchage rate enquery.");
		}
		
		Money m1 = c.first();
		Money m2 = c.second();
		Money m3 = c.result();
		
		if(!c.hasTarget()){
			m3 = new Money(m1.getCurrency());
		}
		
		if(needMarket){
			float v1 = m1.getVlaue() * this.market.getExchangeRate(m1.getCurrency(), m3.getCurrency());
			float v2 = m2.getVlaue() * this.market.getExchangeRate(m2.getCurrency(), m3.getCurrency());
			
			float v3 = v1 + v2 * (c.isAddOperation() ? 1:-1);
			m3.setVlaue(v3);
		}else{
			float v = 0.0f;
			v = m1.getVlaue() + m2.getVlaue() * (c.isAddOperation() ? 1:-1);
			m3.setVlaue(v);
		}
		
		return m3;
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(" Usage: type in express like (23cny - 3USD[=CHF] to caculate automatically, or 'exit' to quit.");
		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(System.in));

		String line = null;
		CommandExecuter executer = new CommandExecuter();
		CommandParser parser = new CommandParser();
		CurrencyMarket market = new CurrencyMarket();
		executer.setMarket(market);
		
		while((line = lnr.readLine()) != null){
			if("exit".equalsIgnoreCase(line)){
				System.exit(0);
			}
			
			Command c = null;
			
			try{
				c = parser.parse(line);
				System.out.print(c.getQuestionStatement());
				Money m = executer.executeCommand(c);
				if(m != null){
					System.out.printf("%s%f%s\n\r",(c.hasTarget() ? " ==> ": ""), m.getVlaue(), m.getCurrency().name());
				}
			}catch(IllegalArgumentException iae){
				System.out.println("Error: " + iae.getMessage());
			}catch(Exception e){
				System.out.println("Error: " + e.getMessage());
			}
		}
		
		
	}
}
