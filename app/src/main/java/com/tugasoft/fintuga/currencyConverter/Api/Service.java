package com.tugasoft.fintuga.currencyConverter.Api;

import com.tugasoft.fintuga.currencyConverter.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Service {
    private static OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(2, TimeUnit.SECONDS).writeTimeout(2, TimeUnit.SECONDS).retryOnConnectionFailure(false).addInterceptor(new LoggingInterceptor()).build();
    private static ExchangeRatesAPI exchangeRatesAPI;
    private static Retrofit retrofit;
    private static Retrofit.Builder retrofitBuilder;

    static {
        Retrofit.Builder addConverterFactory = new Retrofit.Builder().baseUrl(Constants.BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create());
        retrofitBuilder = addConverterFactory;
        Retrofit build = addConverterFactory.build();
        retrofit = build;
        exchangeRatesAPI = (ExchangeRatesAPI) build.create(ExchangeRatesAPI.class);
    }

    public static ExchangeRatesAPI getExchangeRateApo() {
        return exchangeRatesAPI;
    }
}
