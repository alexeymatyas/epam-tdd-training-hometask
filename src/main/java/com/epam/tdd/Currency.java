package com.epam.tdd;

public enum Currency {
	AUD("AUD"),
	BGN("BGN"),
	BRL("BRL"),
	CAD("CAD"),
	CHF("CHF"),
	CNY("CNY"),
	CZK("CZK"),
	DKK("DKK"),
	GBP("GBP"),
	HKD("HKD"),
	HRK("HRK"),
	HUF("HUF"),
	IDR("IDR"),
	ILS("ILS"),
	INR("INR"),
	JPY("JPY"),
	KRW("KRW"),
	MXN("MXN"),
	MYR("MYR"),
	NOK("NOK"),
	NZD("NZD"),
	PHP("PHP"),
	PLN("PLN"),
	RON("RON"),
	RUB("RUB"),
	SEK("SEK"),
	SGD("SGD"),
	THB("THB"),
	TRY("TRY"),
	ZAR("ZAR"),
	EUR("EUR"),
	USD("USD"),
	RMB("CNY");
	
	private String name;
	
	private Currency(String name){
		this.name = name;
	}
	
}
