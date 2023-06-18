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

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundContentColor));
        getWindow().setFlags(1024, 1024);
        setContentView((int) R.layout.activity_simple_and_compound);
        SharedPreferences sharedPreferences2 = getSharedPreferences("mypref", 0);
        sharedPreferences = sharedPreferences2;
        editor = sharedPreferences2.edit();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.simple_compound);
        admobcard = findViewById(R.id.admobcard);

        if (!sharedPreferences.getBoolean("isPurchased", false)) {
            AdmobUnified();
        }
        investmentEdit = findViewById(R.id.amount);
        rateEdit = findViewById(R.id.rateoftax);
        time = findViewById(R.id.tenure);
        Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), R.drawable.simpledetails);
        bmp = decodeResource;
        scalebmp = Bitmap.createScaledBitmap(decodeResource, 1200, 2010, false);
        interest = findViewById(R.id.interestValue);
        netAmount = findViewById(R.id.netamount);
        maturity = findViewById(R.id.maturityAmount);
        RadioGroup radioGroup = findViewById(R.id.togle);
        group = radioGroup;
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
        spiner = (Spinner) findViewById(R.id.spinner);
        spinner.add("Compound interest");
        spinner.add("Simple interest");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinner);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiner.setAdapter(arrayAdapter);
        spiner.setOnItemSelectedListener(this);
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void calculate(View view) {
        ((ScrollView) findViewById(R.id.scroll)).fullScroll(130);
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (investmentEdit.getText().toString().isEmpty() || rateEdit.getText().toString().isEmpty() || time.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        Tenure = Double.parseDouble(time.getText().toString());
        RadioButton radioButton2 = findViewById(group.getCheckedRadioButtonId());
        radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            Tenure = Double.parseDouble(time.getText().toString()) * 12.0d;
        } else {
            Tenure = Double.parseDouble(time.getText().toString());
        }
        if (Tenure <= 360.0d && Double.parseDouble(rateEdit.getText().toString()) <= 50.0d) {
            calculation();
        } else if (Tenure > 360.0d) {
            Toast.makeText(this, "Tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void share(View view) {
        if (investmentEdit.getText().toString().isEmpty() || rateEdit.getText().toString().isEmpty() || time.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        Tenure = Double.parseDouble(time.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(group.getCheckedRadioButtonId());
        radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            Tenure = Double.parseDouble(time.getText().toString()) * 12.0d;
        } else {
            Tenure = Double.parseDouble(time.getText().toString());
        }
        if (Tenure <= 360.0d && Double.parseDouble(rateEdit.getText().toString()) <= 50.0d) {
            calculation();
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.SUBJECT", "EMI- A Calculator app");
            intent.putExtra("android.intent.extra.TEXT", "Simple & Compound interest Details-\n\ninvestmentEdit Amount : " + investmentAmount + "\nTenure : " + Tenure + "months\n\nTotal investmentEdit Amount: " + totalInvestment + "\ninterest Value: " + totalInterest + "\nmaturity Value: " + maturityValue + "\n\nCalculate by EMI\n" + appLink);
            startActivity(Intent.createChooser(intent, "Share Using"));
        } else if (Tenure > 360.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void pdf(View view) {
        File file;
        if (investmentEdit.getText().toString().isEmpty() || rateEdit.getText().toString().isEmpty() || time.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        Tenure = Double.parseDouble(time.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(group.getCheckedRadioButtonId());
        radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            Tenure = Double.parseDouble(time.getText().toString()) * 12.0d;
        } else {
            Tenure = Double.parseDouble(time.getText().toString());
        }
        if (Tenure <= 360.0d && Double.parseDouble(rateEdit.getText().toString()) <= 50.0d) {
            calculation();

            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE"}, 0);
            PdfDocument pdfDocument = new PdfDocument();
            Paint paint = new Paint();
            PdfDocument.Page startPage = pdfDocument.startPage(new PdfDocument.PageInfo.Builder(1200, 2010, 1).create());
            Canvas canvas = startPage.getCanvas();
            canvas.drawBitmap(scalebmp, 0.0f, 0.0f, paint);
            paint.setTextSize(33.0f);
            paint.setTextAlign(Paint.Align.CENTER);
            radioButton = (RadioButton) findViewById(group.getCheckedRadioButtonId());
            canvas.drawText(investmentAmount + " €", 900.0f, 575.0f, paint);
            canvas.drawText((expectedRate * 1200.0d) + " %", 900.0f, 700.0f, paint);
            canvas.drawText(Tenure + "  months", 900.0f, 850.0f, paint);
            canvas.drawText(totalInvestment + " €", 300.0f, 1150.0f, paint);
            canvas.drawText(totalInterest + " €", 900.0f, 1150.0f, paint);
            canvas.drawText(maturityValue + " €", 600.0f, 1450.0f, paint);
            canvas.drawText(choosen, 900.0f, 480.0f, paint);
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
        } else if (Tenure > 360.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void calculation() {
        investmentAmount = Double.parseDouble(investmentEdit.getText().toString());
        Tenure = Double.parseDouble(time.getText().toString());
        RadioButton radioButton2 = findViewById(group.getCheckedRadioButtonId());
        radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            Tenure = Double.parseDouble(time.getText().toString()) * 12.0d;
        } else {
            Tenure = Double.parseDouble(time.getText().toString());
        }
        double parseDouble = Double.parseDouble(rateEdit.getText().toString());
        expectedRate = parseDouble / 1200.0d;
        if (choosen.equals("Compound interest")) {
            maturityValue = investmentAmount * Math.pow(expectedRate + 1.0d, Tenure);
        } else {
            double d = investmentAmount;
            maturityValue = d + (expectedRate * d * Tenure);
        }
        double d2 = investmentAmount;
        totalInvestment = d2;
        double d3 = maturityValue - d2;
        totalInterest = d3;
        if (((double) ((int) d3)) == d3) {
            interest.setText(String.valueOf((int) d3));
        } else {
            totalInterest = new BigDecimal(totalInterest).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView = interest;
            textView.setText(totalInterest + " €");
        }
        double d4 = totalInvestment;
        if (((double) ((int) d4)) == d4) {
            TextView textView2 = netAmount;
            textView2.setText(((int) d4) + " €");
        } else {
            totalInvestment = new BigDecimal(totalInvestment).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView3 = netAmount;
            textView3.setText(totalInvestment + " €");
        }
        double d5 = maturityValue;
        if (((double) ((int) d5)) == d5) {
            maturity.setText(String.valueOf((int) d5));
            return;
        }
        maturityValue = new BigDecimal(maturityValue).setScale(1, RoundingMode.HALF_UP).doubleValue();
        TextView textView4 = maturity;
        textView4.setText(maturityValue + " €");
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        choosen = adapterView.getItemAtPosition(i).toString();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    private void AdmobUnified() {
        AdsProvider.getInstance().addBanner(this, findViewById(R.id.customeventnative_framelayout));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pro, menu);
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