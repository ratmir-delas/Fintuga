package com.tugasoft.fintuga.currencyConverter.Api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class ExchangeRatesResponse {
    @SerializedName("base")
    @Expose
    private String base;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("rates")
    @Expose
    private HashMap<String, String> rates;

    public String getBase() {
        return this.base;
    }

    public String getDate() {
        return this.date;
    }

    public HashMap<String, String> getRates() {
        return this.rates;
    }
}
