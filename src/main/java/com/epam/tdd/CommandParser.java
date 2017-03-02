package com.epam.tdd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

	private static java.util.regex.Pattern REG_COMMAND = Pattern.compile("^(\\s*\\d+.?\\d*\\s*[a-zA-Z]{3})(\\s*[+-]\\s*)(\\d+.?\\d*[a-zA-Z]{3}\\s*)(=|=\\s*[a-zA-Z]{3})?\\s*$");
	
	
	
	public static void main(String[] args){
		String v = "";
		
		v = "21.32RMB+1.2USD=HUF";
		
		Matcher m = REG_COMMAND.matcher(v);
		
		
		System.out.println(m.matches());
		
		Command c = new CommandParser().parse(v);
		
		System.out.println("Done");
	}
	
	public Command parse(String input) throws IllegalArgumentException{
		Command c = null;
		
		if(input == null){
			throw new IllegalArgumentException("No command line provided yet.");
		}
		
		Matcher m = REG_COMMAND.matcher(input);
		if(!m.matches()){
			throw new IllegalArgumentException("Invalid expression");
		}
		
		int gcount = m.groupCount();
		
		Money m1=null, m2=null, m3=null; 
		
		boolean isAdd = true;
		
		m1 = parseMoney(m.group(1));
		m2 = parseMoney(m.group(3));
		if("-".equals(m.group(2).trim())){
			isAdd = false;
		}
		
		if(gcount == 4){
			String r = m.group(4);
			if(r != null && !"=".equals((r.trim()))){
				m3 = parseMoney(m.group(4));
			}
		}
		
		c = new Command(m1, m2, m3, isAdd);
		
		return c;
	}
	
	private Money parseMoney(String v) throws IllegalArgumentException{
		v = v.trim().replace("\\s", "");
		String v1 = v.substring(0, v.length() - 3);
		String v2 = v.substring(v.length()-3);
		
		Currency c = Currency.valueOf(v2.toUpperCase());
		
		Money m = new Money(c);
		if(v1.length() > 0 && !"=".equals(v1)){
			m.setVlaue(Float.parseFloat(v1));
		}
		return m;
	}
	
}
