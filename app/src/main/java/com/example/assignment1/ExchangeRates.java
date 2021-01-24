package com.example.assignment1;

import java.util.HashMap;

public class ExchangeRates {
    HashMap<String, Double> rates;
    public ExchangeRates() {
        rates = new HashMap<String, Double>();
        rates.put("EUR", 1.0);
        rates.put("SEK", 10.0);
        rates.put("USD", 2.0);
        rates.put("GBP", 2.6);
        rates.put("CNY", 3.6);
        rates.put("JPY", 2.4);
        rates.put("KRW", 5.3);
    }
    public double Exchage(String from, String to, int amount){
        double rateForFirstCurrencie = rates.get(from);
        double rateForSecondCurrencie = rates.get(to);
        double total = (amount/rateForFirstCurrencie)*rateForSecondCurrencie;
        total = Math.round(total* 100.0) / 100.0;
        return  total;


    }
}
