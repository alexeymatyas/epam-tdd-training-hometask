package com.epam.tdd;

public class Money {
	private float vlaue;
	
	private Currency currency;
	
	public Money(Currency currency){
		this.currency = currency;
	}

	public float getVlaue() {
		return vlaue;
	}

	public void setVlaue(float vlaue) {
		this.vlaue = vlaue;
	}

	public Currency getCurrency() {
		return currency;
	}
	
}
