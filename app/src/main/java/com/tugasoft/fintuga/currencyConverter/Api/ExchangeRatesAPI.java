package com.tugasoft.fintuga.currencyConverter.Api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ExchangeRatesAPI {
    @GET("latest?")
    Call<ExchangeRatesResponse> getLatest(@Query("access_key") String str, @Query("base") String str2);
}
