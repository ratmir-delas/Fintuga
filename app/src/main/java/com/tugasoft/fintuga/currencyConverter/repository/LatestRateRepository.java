package com.tugasoft.fintuga.currencyConverter.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tugasoft.fintuga.currencyConverter.Api.ExchangeRatesAPI;
import com.tugasoft.fintuga.currencyConverter.Api.ExchangeRatesResponse;
import com.tugasoft.fintuga.currencyConverter.Api.Service;
import com.tugasoft.fintuga.currencyConverter.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LatestRateRepository {
    private static LatestRateRepository instance;
    private ExchangeRatesAPI exchangeRatesAPI = Service.getExchangeRateApo();

    public static LatestRateRepository getInstance() {
        if (instance == null) {
            instance = new LatestRateRepository();
        }
        return instance;
    }

    public LiveData<ExchangeRatesResponse> getLatestRates(String str) {
        final MutableLiveData mutableLiveData = new MutableLiveData();
        this.exchangeRatesAPI.getLatest(Constants.ACCESS_KEY, str).enqueue(new Callback<ExchangeRatesResponse>() {
            public void onResponse(Call<ExchangeRatesResponse> call, Response<ExchangeRatesResponse> response) {
                if (response.isSuccessful()) {
                    Log.i("getLatestRates Repo ", "Success!" + response);
                    mutableLiveData.setValue(response.body());
                }
            }

            public void onFailure(Call<ExchangeRatesResponse> call, Throwable th) {
                Log.i("getLatestRates Repo ", "Failure!");
                mutableLiveData.setValue(null);
            }
        });
        return mutableLiveData;
    }
}
