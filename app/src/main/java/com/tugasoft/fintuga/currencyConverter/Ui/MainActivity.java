package com.tugasoft.fintuga.currencyConverter.Ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.tugasoft.fintuga.currencyConverter.adapter.CustomSpinnerAdapter;

import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.ads.AdsProvider;

import java.util.Objects;



public class MainActivity extends AppCompatActivity {

    public CardView admobcard;
    Button button;
    String[] currency_full_Name;
    String[] currency_symbols;
    CustomSpinnerAdapter customSpinnerAdapter;
    EditText editText;
    String nativeAds;
    SharedPreferences sharedPreferences;
    Spinner spinner;
    private int clickcount = 0;
    private SharedPreferences.Editor editor;
    private MenuItem menuDoneItem;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundContentColor));
        getWindow().setFlags(1024, 1024);
        setContentView((int) R.layout.currency_converter);
        SharedPreferences sharedPreferences2 = getSharedPreferences("mypref", 0);
        this.sharedPreferences = sharedPreferences2;
        this.editor = sharedPreferences2.edit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle((int) R.string.currency_converter);
        this.admobcard = (CardView) findViewById(R.id.admobcard);

            AdmobUnified();

        this.editText = (EditText) findViewById(R.id.editText);
        this.button = (Button) findViewById(R.id.button);
        this.spinner = (Spinner) findViewById(R.id.spinner);
        this.currency_symbols = getApplicationContext().getResources().getStringArray(R.array.currency_symbol_array);
        this.currency_full_Name = getApplicationContext().getResources().getStringArray(R.array.Currency_full_Name);
        CustomSpinnerAdapter customSpinnerAdapter2 = new CustomSpinnerAdapter(this, this.currency_symbols, this.currency_full_Name);
        this.customSpinnerAdapter = customSpinnerAdapter2;
        this.spinner.setAdapter(customSpinnerAdapter2);
        this.editText.setInputType(8194);
        this.button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.checkUserInput();
            }
        });
    }


    public void checkUserInput() {
        if (!this.editText.getText().toString().matches("^[0-9]\\d*(\\.\\d+)?$")) {
            Toast.makeText(this, "Please Enter CurrencyConverterActivity Amount ", 0).show();
            return;
        }
        Intent intent = new Intent(getBaseContext(), RateListActivity.class);
        intent.putExtra("currencyBase", this.spinner.getSelectedItem().toString());
        intent.putExtra("userInputAmount", this.editText.getText().toString());
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pro, menu);
        this.menuDoneItem = menu.findItem(R.id.action_settings);

            this.menuDoneItem.setVisible(false);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
        }
        if (menuItem.getItemId() != R.id.action_settings) {
            return super.onOptionsItemSelected(menuItem);
        }

        return true;
    }

    private void AdmobUnified() {
        AdsProvider.getInstance().addBigNativeView(this, findViewById(R.id.fl_adplaceholder));
    }
}
