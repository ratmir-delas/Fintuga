package com.tugasoft.fintuga.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.ads.AdsProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyConverterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ArrayAdapter<String> adapter;
    private int chosenIndex = 0;
    private final double[] displayAnswer = new double[6];
    private final double[] conversionRates = new double[6];
    private final String[] finalAnswer = new String[6];
    private ListView listView;
    private String measurement = "0";
    private SharedPreferences sharedPreferences;
    private Spinner spinner;
    private final List<String> units = new ArrayList<>();
    private EditText valueEditText;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundContentColor));
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_currency);

        Log.d("CurrencyConverterAct", "onCreate: passed setContentView");

        sharedPreferences = getSharedPreferences("mypref", 0);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(-1);
        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.currency_converter);

        if (internetConnection()) {
            AdsProvider.getInstance().addNativeView(this, findViewById(R.id.customeventnative_framelayout));
        } else {
            CardView admobCardView = findViewById(R.id.admobcard);
            admobCardView.setVisibility(View.GONE);
        }

        this.valueEditText = findViewById(R.id.amount);
        this.spinner = findViewById(R.id.spinner);
        this.sharedPreferences = getApplicationContext().getSharedPreferences("com.sampledata.EmiCalculatorActivity", 0);
        this.listView = findViewById(R.id.currency);

        setupAdapter();
        setupSpinner();
    }

    private void setupAdapter() {
        for (int i = 0; i < 6; i++) {
            this.displayAnswer[i] = 0.0d;
        }

        this.units.add("EURO (€)");
        this.units.add("DOLLAR ($)");
        this.units.add("POUND (£)");
        this.units.add("AUSTRALIAN D. ($)");
        this.units.add("DIRHAM (د.إ)");
        this.units.add("YEN (¥)");

        for (int i2 = 0; i2 < 6; i2++) {
            this.finalAnswer[i2] = "0  " + this.units.get(i2);
        }

        this.adapter = new ArrayAdapter<>(this, R.layout.align_right, this.finalAnswer);
        this.listView.setAdapter(this.adapter);
    }

    private void setupSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, this.units);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner.setAdapter(spinnerAdapter);
        this.spinner.setOnItemSelectedListener(this);
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        this.chosenIndex = i;
    }

    public void calculate(View view) {
        ((ScrollView) findViewById(R.id.scroll)).fullScroll(130);
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);

        if (!this.valueEditText.getText().toString().isEmpty()) {
            this.measurement = this.valueEditText.getText().toString();
        } else {
            this.valueEditText.setText("0");
            this.measurement = "0";
        }

        if (internetConnection()) {
            new DownloadTask().execute();
            //performConversion();
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void performConversion() {
        try {
            double measurementValue = Double.parseDouble(measurement);

            for (int i = 0; i <= 5; i++) {
                double[] resultArray = displayAnswer;
                double[] conversionArray = conversionRates;
                resultArray[i] = measurementValue * (conversionArray[i] / conversionArray[chosenIndex]);
            }

            for (int i2 = 0; i2 <= 5; i2++) {
                double[] resultArray = displayAnswer;
                if (resultArray[i2] == ((double) ((int) resultArray[i2]))) {
                    resultArray[i2] = (int) resultArray[i2];
                } else {
                    displayAnswer[i2] = BigDecimal.valueOf(resultArray[i2]).setScale(2, RoundingMode.HALF_UP).doubleValue();
                }
            }

            for (int i3 = 0; i3 <= 5; i3++) {
                finalAnswer[i3] = displayAnswer[i3] + "  " + units.get(i3);
            }

            adapter.notifyDataSetChanged();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // Handle the error, e.g., display an error message to the user
            Toast.makeText(this, "Conversation error", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean internetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected() && activeNetworkInfo.isAvailable();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pro, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
        } else
        if (menuItem.getItemId() != R.id.action_settings) {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    public class DownloadTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient().newBuilder().build();
                Request request = new Request.Builder()
                        .url("https://api.apilayer.com/exchangerates_data/latest?symbols=USD%2CEUR%2CAED%2CJPY%2CGBP%2CAUD&base=EUR")
                        .addHeader("apikey", "4FuFOLAS16CUSzFepaE9vXvgeOqHuWcg")
                        .build();
                Response response = client.newCall(request).execute();
                if (response.body() != null) {
                    String responseBody = response.body().string();
                    Log.d("CurrencyConverterAct", responseBody);
                    return new JSONObject(responseBody).optJSONObject("rates");
                } else {
                    Log.d("CurrencyConverterAct", "failed try");
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject ratesObject) {
            super.onPostExecute(ratesObject);
            if (ratesObject != null) {
                try {
                    conversionRates[0] = 1.0d;
                    conversionRates[1] = ratesObject.optDouble("USD");
                    conversionRates[2] = ratesObject.optDouble("GBP");
                    conversionRates[3] = ratesObject.optDouble("AED");
                    conversionRates[4] = ratesObject.optDouble("AUD");
                    conversionRates[5] = ratesObject.optDouble("JPY");
                    //Toast.makeText(CurrencyConverterActivity.this, "Real time", Toast.LENGTH_SHORT).show();
                    saveConversionRates();

                } catch (Exception e) {
                    e.printStackTrace();
                    showOffline();
                    Toast.makeText(CurrencyConverterActivity.this, "Problem...", Toast.LENGTH_SHORT).show();
                }
            } else {
                showOffline();
                Toast.makeText(CurrencyConverterActivity.this, "Problem...", Toast.LENGTH_SHORT).show();
            }

            performConversion();
        }

        private void saveConversionRates() {
            //SharedPreferences.Editor editor = CurrencyConverterActivity.this.sharedPreferences.edit();
            sharedPreferences.edit()
                    .putFloat("conversionRate0", (float) conversionRates[0])
                    .putFloat("conversionRate1", (float) conversionRates[1])
                    .putFloat("conversionRate2", (float) conversionRates[2])
                    .putFloat("conversionRate3", (float) conversionRates[3])
                    .putFloat("conversionRate4", (float) conversionRates[4])
                    .putFloat("conversionRate5", (float) conversionRates[5])
                    .apply();
        }

        void showOffline() {
            if (0 == sharedPreferences.getFloat("conversionRate0", 0)) {
                conversionRates[0] = sharedPreferences.getFloat("conversionRate0", 0);
                conversionRates[1] = sharedPreferences.getFloat("conversionRate1", 0);
                conversionRates[2] = sharedPreferences.getFloat("conversionRate2", 0);
                conversionRates[3] = sharedPreferences.getFloat("conversionRate3", 0);
                conversionRates[4] = sharedPreferences.getFloat("conversionRate4", 0);
                conversionRates[5] = sharedPreferences.getFloat("conversionRate5", 0);
                Toast.makeText(CurrencyConverterActivity.this, "Offline values", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
