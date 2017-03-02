package com.epam.tdd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class CurrencyMarket {
	
	private static String URL_BASE = "http://api.fixer.io/latest?base=";
	
	
	public float getExchangeRate(Currency c1, Currency c2) throws RuntimeException{
		if(c1 == Currency.RMB){
			c1 = Currency.CNY;
		}
		
		if(c2 == Currency.RMB){
			c2 = Currency.CNY;
		}
		
		if(c1 == c2){
			return 1.0f;
		}
		
		float v = 0.0f;
		InputStream in = null;
		try {
			URL aURL = new URL(URL_BASE + c1.name());
			
			in = aURL.openStream();
			
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			
			byte[] b = new byte[1024];
			int read = 0;
			while((read = in.read(b)) > 0){
				buf.write(b, 0, read);
			}
			
			String buffer = buf.toString();
			int start = buffer.indexOf(c2.name());
			int end = buffer.indexOf(",", start);
			if(start != -1 && end != -1){
				String vv = buffer.substring(start + 3, end);
				vv = vv.replaceAll("\"|:", "");
				v = Float.parseFloat(vv); 
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch(IOException ioe){
			ioe.printStackTrace();
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return v;
	}
}
