package com.tugasoft.fintuga.currencyConverter.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tugasoft.fintuga.currencyConverter.Api.ExchangeRatesResponse;
import com.tugasoft.fintuga.currencyConverter.repository.LatestRateRepository;

public class LatestRateViewModel extends ViewModel {
    private LiveData<ExchangeRatesResponse> mutableLiveData;
    private LatestRateRepository rateRepository;

    public void init() {
        this.rateRepository = LatestRateRepository.getInstance();
    }

    public LiveData<ExchangeRatesResponse> getLatestRates(String str) {
        LiveData<ExchangeRatesResponse> latestRates = this.rateRepository.getLatestRates(str);
        this.mutableLiveData = latestRates;
        return latestRates;
    }
}
