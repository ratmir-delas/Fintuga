package com.tugasoft.fintuga.tools;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;
import com.tugasoft.fintuga.ads.AdsProvider;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class SimpleAndCompoundActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public CardView admobcard;
    double Tenure;
    String appLink;
    Bitmap bmp;
    String choosen = "Compound interest";
    double expectedRate;
    RadioGroup group;
    TextView interest;
    double investmentAmount;
    EditText investmentEdit;
    TextView maturity;
    double maturityValue;
    TextView netAmount;
    RadioButton radioButton;
    EditText rateEdit;
    Bitmap scalebmp;
    Spinner spiner;
    List<String> spinner = new ArrayList();
    EditText time;
    double totalInterest;
    double totalInvestment;
    private SharedPreferences.Editor editor;
    private MenuItem menuDoneItem;
    private SharedPreferences sharedPreferences;
    private String JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundContentColor));
        getWindow().setFlags(1024, 1024);
        setContentView((int) R.layout.activity_simple_and_compound);
        SharedPreferences sharedPreferences2 = getSharedPreferences("mypref", 0);
        this.sharedPreferences = sharedPreferences2;
        this.editor = sharedPreferences2.edit();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.simple_compound);
        this.admobcard = findViewById(R.id.admobcard);

        if (!this.sharedPreferences.getBoolean("isPurchased", false)) {
            AdmobUnified();
        }
        this.investmentEdit = findViewById(R.id.amount);
        this.rateEdit = findViewById(R.id.rateoftax);
        this.time = findViewById(R.id.tenure);
        Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), R.drawable.simpledetails);
        this.bmp = decodeResource;
        this.scalebmp = Bitmap.createScaledBitmap(decodeResource, 1200, 2010, false);
        this.interest = findViewById(R.id.interestValue);
        this.netAmount = findViewById(R.id.netamount);
        this.maturity = findViewById(R.id.maturityAmount);
        RadioGroup radioGroup = findViewById(R.id.togle);
        this.group = radioGroup;
        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            if (i == R.id.month) {
                SimpleAndCompoundActivity simpleAndCompound = SimpleAndCompoundActivity.this;
                simpleAndCompound.radioButton = simpleAndCompound.findViewById(R.id.year);
                SimpleAndCompoundActivity.this.radioButton.setTextColor(SimpleAndCompoundActivity.this.getResources().getColor(R.color.fontBlackDisable));
                SimpleAndCompoundActivity simpleAndCompound2 = SimpleAndCompoundActivity.this;
                simpleAndCompound2.radioButton = simpleAndCompound2.findViewById(R.id.month);
                SimpleAndCompoundActivity.this.radioButton.setTextColor(SimpleAndCompoundActivity.this.getResources().getColor(R.color.fontBlackEnable));
            } else if (i == R.id.year) {
                SimpleAndCompoundActivity simpleAndCompound3 = SimpleAndCompoundActivity.this;
                simpleAndCompound3.radioButton = simpleAndCompound3.findViewById(R.id.year);
                SimpleAndCompoundActivity.this.radioButton.setTextColor(SimpleAndCompoundActivity.this.getResources().getColor(R.color.fontBlackEnable));
                SimpleAndCompoundActivity simpleAndCompound4 = SimpleAndCompoundActivity.this;
                simpleAndCompound4.radioButton = simpleAndCompound4.findViewById(R.id.month);
                SimpleAndCompoundActivity.this.radioButton.setTextColor(SimpleAndCompoundActivity.this.getResources().getColor(R.color.fontBlackDisable));
            }
        });
        this.spiner = (Spinner) findViewById(R.id.spinner);
        this.spinner.add("Compound interest");
        this.spinner.add("Simple interest");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, this.spinner);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spiner.setAdapter(arrayAdapter);
        this.spiner.setOnItemSelectedListener(this);

        // Initialize month strings
        JANUARY = getString(R.string.jan);
        FEBRUARY = getString(R.string.feb);
        MARCH = getString(R.string.mar);
        APRIL = getString(R.string.apr);
        MAY = getString(R.string.may);
        JUNE = getString(R.string.jun);
        JULY = getString(R.string.jul);
        AUGUST = getString(R.string.aug);
        SEPTEMBER = getString(R.string.sep);
        OCTOBER = getString(R.string.oct);
        NOVEMBER = getString(R.string.nov);
        DECEMBER = getString(R.string.dec);
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void calculate(View view) {
        ((ScrollView) findViewById(R.id.scroll)).fullScroll(130);
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (this.investmentEdit.getText().toString().isEmpty() || this.rateEdit.getText().toString().isEmpty() || this.time.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        this.Tenure = Double.parseDouble(this.time.getText().toString());
        RadioButton radioButton2 = findViewById(this.group.getCheckedRadioButtonId());
        this.radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            this.Tenure = Double.parseDouble(this.time.getText().toString()) * 12.0d;
        } else {
            this.Tenure = Double.parseDouble(this.time.getText().toString());
        }
        if (this.Tenure <= 360.0d && Double.parseDouble(this.rateEdit.getText().toString()) <= 50.0d) {
            calculation();
        } else if (this.Tenure > 360.0d) {
            Toast.makeText(this, "Tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void share(View view) {
        if (this.investmentEdit.getText().toString().isEmpty() || this.rateEdit.getText().toString().isEmpty() || this.time.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        this.Tenure = Double.parseDouble(this.time.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(this.group.getCheckedRadioButtonId());
        this.radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            this.Tenure = Double.parseDouble(this.time.getText().toString()) * 12.0d;
        } else {
            this.Tenure = Double.parseDouble(this.time.getText().toString());
        }
        if (this.Tenure <= 360.0d && Double.parseDouble(this.rateEdit.getText().toString()) <= 50.0d) {
            calculation();
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.SUBJECT", "EMI- A Calculator app");
            intent.putExtra("android.intent.extra.TEXT", "Simple & Compound interest Details-\n\ninvestmentEdit Amount : " + this.investmentAmount + "\nTenure : " + this.Tenure + "months\n\nTotal investmentEdit Amount: " + this.totalInvestment + "\ninterest Value: " + this.totalInterest + "\nmaturity Value: " + this.maturityValue + "\n\nCalculate by EMI\n" + this.appLink);
            startActivity(Intent.createChooser(intent, "Share Using"));
        } else if (this.Tenure > 360.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void pdf(View view) {
        File file;
        if (this.investmentEdit.getText().toString().isEmpty() || this.rateEdit.getText().toString().isEmpty() || this.time.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        this.Tenure = Double.parseDouble(this.time.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(this.group.getCheckedRadioButtonId());
        this.radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            this.Tenure = Double.parseDouble(this.time.getText().toString()) * 12.0d;
        } else {
            this.Tenure = Double.parseDouble(this.time.getText().toString());
        }
        if (this.Tenure <= 360.0d && Double.parseDouble(this.rateEdit.getText().toString()) <= 50.0d) {
            calculation();

            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE"}, 0);
            PdfDocument pdfDocument = new PdfDocument();
            Paint paint = new Paint();
            PdfDocument.Page startPage = pdfDocument.startPage(new PdfDocument.PageInfo.Builder(1200, 2010, 1).create());
            Canvas canvas = startPage.getCanvas();
            canvas.drawBitmap(this.scalebmp, 0.0f, 0.0f, paint);
            paint.setTextSize(33.0f);
            paint.setTextAlign(Paint.Align.CENTER);
            this.radioButton = (RadioButton) findViewById(this.group.getCheckedRadioButtonId());
            canvas.drawText(this.investmentAmount + " €", 900.0f, 575.0f, paint);
            canvas.drawText((this.expectedRate * 1200.0d) + " %", 900.0f, 700.0f, paint);
            canvas.drawText(this.Tenure + "  months", 900.0f, 850.0f, paint);
            canvas.drawText(this.totalInvestment + " €", 300.0f, 1150.0f, paint);
            canvas.drawText(this.totalInterest + " €", 900.0f, 1150.0f, paint);
            canvas.drawText(this.maturityValue + " €", 600.0f, 1450.0f, paint);
            canvas.drawText(this.choosen, 900.0f, 480.0f, paint);
            pdfDocument.finishPage(startPage);
            if (Build.VERSION.SDK_INT >= 29) {
                file = new File(getExternalCacheDir(), "/Interest"+ System.currentTimeMillis() + ".pdf");
            } else {
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/Interest"+ System.currentTimeMillis() + ".pdf");
            }
            try {
                pdfDocument.writeTo(new FileOutputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            pdfDocument.close();
            if (file.exists()) {
                CommonMethod.viewPDF(SimpleAndCompoundActivity.this, file);
            }
        } else if (this.Tenure > 360.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void calculation() {
        this.investmentAmount = Double.parseDouble(this.investmentEdit.getText().toString());
        this.Tenure = Double.parseDouble(this.time.getText().toString());
        RadioButton radioButton2 = findViewById(this.group.getCheckedRadioButtonId());
        this.radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            this.Tenure = Double.parseDouble(this.time.getText().toString()) * 12.0d;
        } else {
            this.Tenure = Double.parseDouble(this.time.getText().toString());
        }
        double parseDouble = Double.parseDouble(this.rateEdit.getText().toString());
        this.expectedRate = parseDouble;
        this.expectedRate = parseDouble / 1200.0d;
        if (this.choosen.equals("Compound interest")) {
            this.maturityValue = this.investmentAmount * Math.pow(this.expectedRate + 1.0d, this.Tenure);
        } else {
            double d = this.investmentAmount;
            this.maturityValue = d + (this.expectedRate * d * this.Tenure);
        }
        double d2 = this.investmentAmount;
        this.totalInvestment = d2;
        double d3 = this.maturityValue - d2;
        this.totalInterest = d3;
        if (((double) ((int) d3)) == d3) {
            this.interest.setText(String.valueOf((int) d3));
        } else {
            this.totalInterest = new BigDecimal(this.totalInterest).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView = this.interest;
            textView.setText(this.totalInterest + " €");
        }
        double d4 = this.totalInvestment;
        if (((double) ((int) d4)) == d4) {
            TextView textView2 = this.netAmount;
            textView2.setText(((int) d4) + " €");
        } else {
            this.totalInvestment = new BigDecimal(this.totalInvestment).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView3 = this.netAmount;
            textView3.setText(this.totalInvestment + " €");
        }
        double d5 = this.maturityValue;
        if (((double) ((int) d5)) == d5) {
            this.maturity.setText(String.valueOf((int) d5));
            return;
        }
        this.maturityValue = new BigDecimal(this.maturityValue).setScale(1, RoundingMode.HALF_UP).doubleValue();
        TextView textView4 = this.maturity;
        textView4.setText(this.maturityValue + " €");
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        this.choosen = adapterView.getItemAtPosition(i).toString();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    private void AdmobUnified() {
        AdsProvider.getInstance().addBanner(this, findViewById(R.id.customeventnative_framelayout));
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
        } else if (menuItem.getItemId() != R.id.action_settings) {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
}