package com.tugasoft.fintuga.currencyConverter.Ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tugasoft.fintuga.currencyConverter.Api.ExchangeRatesResponse;
import com.tugasoft.fintuga.currencyConverter.adapter.RateRecyclerAdapter;
import com.tugasoft.fintuga.currencyConverter.viewmodel.LatestRateViewModel;
import com.tugasoft.fintuga.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RateListActivity extends AppCompatActivity {
    public ProgressDialog progressDialog;
    String baseCurrency = "";
    List<Integer> currencyImageList = new ArrayList();
    List<String> currencyNameList = new ArrayList();
    List<String> currencyRateList = new ArrayList();
    List<String> currencySymbolList = new ArrayList();
    List<String> currencyValueList = new ArrayList();
    HashMap<String, String> symbol_and_name;
    TextView textView;
    String userInputAmount = "";
    private LatestRateViewModel latestRateViewModel;
    private RecyclerView mRecyclerView;
    private RateRecyclerAdapter rateRecylerAdapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_rate_list);
        this.mRecyclerView = (RecyclerView) findViewById(R.id.currency_list);
        TextView textView2 = (TextView) findViewById(R.id.text_view);
        this.textView = textView2;
        textView2.setVisibility(View.GONE);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Bundle extras = getIntent().getExtras();
        this.progressDialog = ProgressDialog.show(this, "Please Wait", "Loading currency Data");
        if (extras != null) {
            this.baseCurrency = extras.getString("currencyBase");
            this.userInputAmount = extras.getString("userInputAmount");
        }
        LatestRateViewModel latestRateViewModel2 = (LatestRateViewModel) ViewModelProviders.of((FragmentActivity) this).get(LatestRateViewModel.class);
        this.latestRateViewModel = latestRateViewModel2;
        latestRateViewModel2.init();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                RateListActivity.this.init();
            }
        }, 2000);
    }

    public void init() {
        this.latestRateViewModel.getLatestRates(this.baseCurrency).observe(this, new Observer<ExchangeRatesResponse>() {
            public void onChanged(ExchangeRatesResponse exchangeRatesResponse) {
                if (exchangeRatesResponse != null && exchangeRatesResponse.getRates() != null) {
                    RateListActivity.this.populateRecyclerView(exchangeRatesResponse.getRates());
                    RateListActivity.this.progressDialog.dismiss();
                    RateListActivity.this.textView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void populateRecyclerView(HashMap<String, String> hashMap) {
        this.symbol_and_name = new HashMap<>();
        String[] stringArray = getApplicationContext().getResources().getStringArray(R.array.currency_symbol_array);
        String[] stringArray2 = getApplicationContext().getResources().getStringArray(R.array.Currency_full_Name);
        for (int i = 0; i < stringArray.length; i++) {
            this.symbol_and_name.put(stringArray[i], stringArray2[i]);
        }
        for (Map.Entry entry : new TreeMap<String, String>(hashMap).entrySet()) {
            if (!((String) entry.getKey()).equals(this.baseCurrency)) {
                this.currencySymbolList.add(String.valueOf(entry.getKey()));
                this.currencyValueList.add(String.valueOf(entry.getValue()));
                if (this.symbol_and_name.containsKey(entry.getKey())) {
                    this.currencyNameList.add(this.symbol_and_name.get(entry.getKey()));
                    boolean equals = ((String) entry.getKey()).toLowerCase().equals("try");
                }
            }
            double parseDouble = Double.parseDouble((String) entry.getValue()) * Double.parseDouble(this.userInputAmount);
            this.currencyRateList.add(new DecimalFormat("#,###.00").format(parseDouble));
        }
        RateRecyclerAdapter rateRecyclerAdapter = new RateRecyclerAdapter(this, this.baseCurrency, this.currencySymbolList, this.currencyNameList, this.currencyRateList, this.currencyValueList, this.currencyImageList);
        this.rateRecylerAdapter = rateRecyclerAdapter;
        this.mRecyclerView.setAdapter(rateRecyclerAdapter);
    }

    private void setFilterData(String text) {
        List<String> thisCurrencyNameList = new ArrayList();
        for (String currency : currencyNameList) {
            if (currency.toLowerCase().contains(text)) {
                thisCurrencyNameList.add(currency);
            }
        }

        rateRecylerAdapter.FilterDataByCurrencyName(thisCurrencyNameList);
    }
}
