package com.tugasoft.fintuga.currencyConverter;

import java.text.SimpleDateFormat;

public class Constants {
    public static final String ACCESS_KEY = "7ac1c509e74afaef85cbe85ddff81a6a";
    public static final String BASE_URL = "http://data.fixer.io/api/";


    public static String unixTimeToFormatTime(long j) {
        return new SimpleDateFormat("MMM dd,yyyy hh:mm:a").format(Long.valueOf(j * 1000));
    }
}
