package com.tugasoft.fintuga.tools;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
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
import java.nio.file.Files;
import java.util.Objects;


public class CompareLoanActivity extends AppCompatActivity {
    public CardView admobcard;
    double amount1;
    double amount2;
    Bitmap bmp;
    TextView difEmi;
    TextView difInter;
    TextView difTotal;
    double diffEmiVal;
    double diffInterest;
    double diffPayment;
    Bitmap dusrabmp;
    TextView emi1;
    TextView emi2;
    double emiVal1;
    double emiVal2;
    RadioGroup group;
    TextView interest1;
    TextView interest2;
    EditText principal1;
    EditText principal2;
    RadioButton radioButton;
    EditText rate1;
    EditText rate2;
    double rateVal1;
    double rateVal2;
    Bitmap scalebmp;
    Bitmap scaledusrabmp;
    double tenure1;
    double tenure2;
    EditText time1;
    EditText time2;
    TextView total1;
    TextView total2;
    double totalInterest1;
    double totalInterest2;
    double totalPayment1;
    double totalPayment2;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundContentColor));
        getWindow().setFlags(1024, 1024);
        setContentView((int) R.layout.activity_compare_loan);
        SharedPreferences sharedPreferences2 = getSharedPreferences("mypref", 0);
        SharedPreferences.Editor editor = sharedPreferences2.edit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle((int) R.string.compare);
        this.admobcard = (CardView) findViewById(R.id.admobcard);

        if (!sharedPreferences2.getBoolean("isPurchased", false)) {
            AdsProvider.getInstance().addBanner(this, findViewById(R.id.customeventnative_framelayout));
        }
        this.principal1 = (EditText) findViewById(R.id.principalamoount1);
        this.principal2 = (EditText) findViewById(R.id.principalamoount2);
        this.rate1 = (EditText) findViewById(R.id.interestamoount1);
        this.rate2 = (EditText) findViewById(R.id.interestamoount2);
        this.time1 = (EditText) findViewById(R.id.tenure1);
        this.time2 = (EditText) findViewById(R.id.tenure2);
        this.emi1 = (TextView) findViewById(R.id.emi1);
        this.emi2 = (TextView) findViewById(R.id.emi2);
        this.interest1 = (TextView) findViewById(R.id.totalinterest1);
        this.interest2 = (TextView) findViewById(R.id.totalinterest2);
        this.total1 = (TextView) findViewById(R.id.totalamount1);
        this.total2 = (TextView) findViewById(R.id.totalamount2);
        this.difEmi = (TextView) findViewById(R.id.emidifference);
        this.difInter = (TextView) findViewById(R.id.interestdifference);
        this.difTotal = (TextView) findViewById(R.id.paymentdifference);
        this.group = (RadioGroup) findViewById(R.id.togle);
        Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), R.drawable.compareone);
        this.bmp = decodeResource;
        this.scalebmp = Bitmap.createScaledBitmap(decodeResource, 1200, 2010, false);
        Bitmap decodeResource2 = BitmapFactory.decodeResource(getResources(), R.drawable.comparetwo);
        this.dusrabmp = decodeResource2;
        this.scaledusrabmp = Bitmap.createScaledBitmap(decodeResource2, 1200, 2010, false);
        this.group.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.month) {
                CompareLoanActivity compareloan = CompareLoanActivity.this;
                compareloan.radioButton = (RadioButton) compareloan.findViewById(R.id.year);
                CompareLoanActivity.this.radioButton.setTextColor(CompareLoanActivity.this.getResources().getColor(R.color.fontBlackDisable));
                CompareLoanActivity compareloan2 = CompareLoanActivity.this;
                compareloan2.radioButton = (RadioButton) compareloan2.findViewById(R.id.month);
                CompareLoanActivity.this.radioButton.setTextColor(CompareLoanActivity.this.getResources().getColor(R.color.fontBlackEnable));
            } else if (i == R.id.year) {
                CompareLoanActivity compareloan3 = CompareLoanActivity.this;
                compareloan3.radioButton = (RadioButton) compareloan3.findViewById(R.id.year);
                CompareLoanActivity.this.radioButton.setTextColor(CompareLoanActivity.this.getResources().getColor(R.color.fontBlackEnable));
                CompareLoanActivity compareloan4 = CompareLoanActivity.this;
                compareloan4.radioButton = (RadioButton) compareloan4.findViewById(R.id.month);
                CompareLoanActivity.this.radioButton.setTextColor(CompareLoanActivity.this.getResources().getColor(R.color.fontBlackDisable));
            }
        });
    }

    public void calculate(View view) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (this.principal1.getText().toString().isEmpty() || this.principal2.getText().toString().isEmpty() || this.rate1.getText().toString().isEmpty() || this.rate2.getText().toString().isEmpty() || this.time1.getText().toString().isEmpty() || this.time2.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter the * value", Toast.LENGTH_SHORT).show();
            return;
        }
        this.tenure1 = Double.parseDouble(this.time1.getText().toString());
        this.tenure2 = Double.parseDouble(this.time2.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(this.group.getCheckedRadioButtonId());
        this.radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            this.tenure1 = Double.parseDouble(this.time1.getText().toString()) * 12.0d;
            this.tenure2 = Double.parseDouble(this.time2.getText().toString()) * 12.0d;
        } else {
            this.tenure1 = Double.parseDouble(this.time1.getText().toString());
            this.tenure2 = Double.parseDouble(this.time2.getText().toString());
        }
        if (this.tenure1 <= 30.0d || this.tenure2 <= 30.0d || Double.parseDouble(this.rate1.getText().toString()) <= 50.0d || Double.parseDouble(this.rate2.getText().toString()) <= 50.0d) {
            calculation();
        } else if (this.tenure1 > 30.0d || this.tenure2 > 30.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void calculation() {
        ((ScrollView) findViewById(R.id.scroll)).fullScroll(130);
        this.amount1 = Double.parseDouble(this.principal1.getText().toString());
        this.amount2 = Double.parseDouble(this.principal2.getText().toString());
        this.rateVal1 = Double.parseDouble(this.rate1.getText().toString()) / 1200.0d;
        this.rateVal2 = Double.parseDouble(this.rate2.getText().toString()) / 1200.0d;
        double d = this.amount1;
        double d2 = this.rateVal1;
        double pow = (d * d2) / (1.0d - Math.pow(d2 + 1.0d, -this.tenure1));
        double d3 = this.tenure1;
        double d4 = this.amount1;
        this.totalInterest1 = (pow * d3) - d4;
        double d5 = this.rateVal1;
        this.totalPayment1 = d4 + d5;
        double pow2 = Math.pow(d5 + 1.0d, d3);
        this.emiVal1 = ((this.amount1 * this.rateVal1) * pow2) / (pow2 - 1.0d);
        double d6 = this.amount2;
        double d7 = this.rateVal2;
        double pow3 = (d6 * d7) / (1.0d - Math.pow(d7 + 1.0d, -this.tenure2));
        double d8 = this.tenure2;
        double d9 = this.amount2;
        this.totalInterest2 = (pow3 * d8) - d9;
        double d10 = this.rateVal2;
        this.totalPayment2 = d9 + d10;
        double pow4 = Math.pow(d10 + 1.0d, d8);
        this.emiVal2 = ((this.amount2 * this.rateVal2) * pow4) / (pow4 - 1.0d);
        double d11 = this.totalPayment2;
        if (((double) ((int) d11)) == d11) {
            this.total2.setText(String.valueOf((int) d11));
        } else {
            this.totalPayment2 = new BigDecimal(this.totalPayment2).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView = this.total2;
            textView.setText(this.totalPayment2 + " €");
        }
        double d12 = this.totalPayment1;
        if (((double) ((int) d12)) == d12) {
            this.total1.setText(String.valueOf((int) d12));
        } else {
            this.totalPayment1 = new BigDecimal(this.totalPayment1).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView2 = this.total1;
            textView2.setText(this.totalPayment1 + " €");
        }
        double d13 = this.totalPayment1;
        double d14 = this.totalPayment2;
        if (d13 > d14) {
            // Green 26CA6A
            // Red   F80606
            this.total1.setTextColor(Color.parseColor("#F80606"));
            this.total2.setTextColor(Color.parseColor("#26CA6A"));
            this.diffPayment = this.totalPayment1 - this.totalPayment2;
        } else if (d13 == d14) {
            this.total1.setTextColor(Color.parseColor("#000000"));
            this.total2.setTextColor(Color.parseColor("#000000"));
            this.diffPayment = 0.0;
        } else {
            this.total1.setTextColor(Color.parseColor("#26CA6A"));
            this.total2.setTextColor(Color.parseColor("#F80606"));
            this.diffPayment = this.totalPayment2 - this.totalPayment1;
        }
        this.diffPayment = new BigDecimal(this.diffPayment).setScale(1, RoundingMode.HALF_UP).doubleValue();
        TextView textView3 = this.difTotal;
        textView3.setText(this.diffPayment + " €");
        double d15 = this.totalInterest2;
        if (((double) ((int) d15)) == d15) {
            this.interest2.setText(String.valueOf((int) d15));
        } else {
            this.totalInterest2 = new BigDecimal(this.totalInterest2).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView4 = this.interest2;
            textView4.setText(this.totalInterest2 + " €");
        }
        double d16 = this.totalInterest1;
        if (((double) ((int) d16)) == d16) {
            this.interest1.setText(String.valueOf((int) d16));
        } else {
            this.totalInterest1 = new BigDecimal(this.totalInterest1).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView5 = this.interest1;
            textView5.setText(this.totalInterest1 + " €");
        }
        double d17 = this.totalInterest1;
        double d18 = this.totalInterest2;
        if (d17 > d18) {
            // Green 26CA6A
            // Red   F80606
            this.diffInterest = d17 - d18;
            this.interest1.setTextColor(Color.parseColor("#F80606"));
            this.interest2.setTextColor(Color.parseColor("#26CA6A"));
        } else if (d17 == d18) {
            this.diffInterest = 0.0;
            this.interest1.setTextColor(Color.parseColor("#000000"));
            this.interest2.setTextColor(Color.parseColor("#000000"));
        } else {
            this.diffInterest = d18 - d17;
            this.interest1.setTextColor(Color.parseColor("#26CA6A"));
            this.interest2.setTextColor(Color.parseColor("#F80606"));
        }
        this.diffInterest = new BigDecimal(this.diffInterest).setScale(1, RoundingMode.HALF_UP).doubleValue();
        TextView textView6 = this.difInter;
        textView6.setText(this.diffInterest + " €");
        double d19 = this.emiVal2;
        if (((double) ((int) d19)) == d19) {
            this.emi2.setText(String.valueOf((int) d19));
        } else {
            this.emiVal2 = new BigDecimal(this.emiVal2).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView7 = this.emi2;
            textView7.setText(this.emiVal2 + " €");
        }
        double d20 = this.emiVal1;
        if (((double) ((int) d20)) == d20) {
            this.emi1.setText(String.valueOf((int) d20));
        } else {
            this.emiVal1 = new BigDecimal(this.emiVal1).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView8 = this.emi1;
            textView8.setText(this.emiVal1 + " €");
        }
        double d21 = this.emiVal1;
        double d22 = this.emiVal2;
        if (d21 > d22) {
            this.diffEmiVal = d21 - d22;
            // Green 26CA6A
            // Red   F80606
            this.emi1.setTextColor(Color.parseColor("#F80606"));
            this.emi2.setTextColor(Color.parseColor("#26CA6A"));
        } else if (d21 == d22) {
            this.diffEmiVal = 0.0;
            this.emi1.setTextColor(Color.parseColor("#000000"));
            this.emi2.setTextColor(Color.parseColor("#000000"));
        } else {
            this.diffEmiVal = d22 - d21;
            this.emi1.setTextColor(Color.parseColor("#26CA6A"));
            this.emi2.setTextColor(Color.parseColor("#F80606"));
        }
        this.diffEmiVal = new BigDecimal(this.diffEmiVal).setScale(1, RoundingMode.HALF_UP).doubleValue();
        TextView textView9 = this.difEmi;
        textView9.setText(this.diffEmiVal + " €");
    }

    public void pdf(View view) {
        File file;
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (this.principal1.getText().toString().isEmpty() || this.principal2.getText().toString().isEmpty() || this.rate1.getText().toString().isEmpty() || this.rate2.getText().toString().isEmpty() || this.time1.getText().toString().isEmpty() || this.time2.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter the * value", Toast.LENGTH_SHORT).show();
            return;
        }
        this.tenure1 = Double.parseDouble(this.time1.getText().toString());
        this.tenure2 = Double.parseDouble(this.time2.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(this.group.getCheckedRadioButtonId());
        this.radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            this.tenure1 = Double.parseDouble(this.time1.getText().toString()) * 12.0d;
            this.tenure2 = Double.parseDouble(this.time2.getText().toString()) * 12.0d;
        } else {
            this.tenure1 = Double.parseDouble(this.time1.getText().toString());
            this.tenure2 = Double.parseDouble(this.time2.getText().toString());
        }
        if (this.tenure1 <= 30.0d || this.tenure2 <= 30.0d || Double.parseDouble(this.rate1.getText().toString()) <= 50.0d || Double.parseDouble(this.rate2.getText().toString()) <= 50.0d) {
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
            canvas.drawText(this.amount1 + " €", 300.0f, 735.0f, paint);
            canvas.drawText(this.amount2 + " €", 900.0f, 735.0f, paint);
            canvas.drawText((this.rateVal1 * 1200.0d) + " €", 300.0f, 985.0f, paint);
            canvas.drawText((this.rateVal2 * 1200.0d) + " €", 900.0f, 985.0f, paint);
            canvas.drawText(this.tenure1 + " months", 300.0f, 1235.0f, paint);
            canvas.drawText(this.tenure2 + " months", 900.0f, 1235.0f, paint);
            canvas.drawText(this.emiVal1 + " €", 300.0f, 1550.0f, paint);
            canvas.drawText(this.emiVal2 + " €", 900.0f, 1550.0f, paint);
            canvas.drawText(String.valueOf(this.diffEmiVal), 750.0f, 1695.0f, paint);
            pdfDocument.finishPage(startPage);
            PdfDocument.Page startPage2 = pdfDocument.startPage(new PdfDocument.PageInfo.Builder(1200, 2010, 2).create());
            Canvas canvas2 = startPage2.getCanvas();
            canvas2.drawBitmap(this.scaledusrabmp, 0.0f, 0.0f, paint);
            canvas2.drawText(this.totalInterest1 + " €", 300.0f, 340.0f, paint);
            canvas2.drawText(this.totalInterest2 + " €", 900.0f, 340.0f, paint);
            canvas2.drawText(String.valueOf(this.diffInterest), 750.0f, 480.0f, paint);
            canvas2.drawText(this.totalPayment1 + " €", 300.0f, 825.0f, paint);
            canvas2.drawText(this.totalPayment2 + " €", 900.0f, 825.0f, paint);
            canvas2.drawText(String.valueOf(this.diffInterest), 750.0f, 960.0f, paint);
            pdfDocument.finishPage(startPage2);
            if (Build.VERSION.SDK_INT >= 29) {
                file = new File(getExternalCacheDir(), "/CompareLoan" + System.currentTimeMillis() + ".pdf");
            } else {
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/CompareLoan" + System.currentTimeMillis() + ".pdf");
            }
            File file2 = file;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    pdfDocument.writeTo(Files.newOutputStream(file2.toPath()));
                } else {
                    pdfDocument.writeTo(new FileOutputStream(file2));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            pdfDocument.close();

            if (file2.exists()) {
                CommonMethod.viewPDF(CompareLoanActivity.this, file);
            }
        } else if (this.tenure1 > 30.0d || this.tenure2 > 30.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
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
        }
        if (menuItem.getItemId() != R.id.action_settings) {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
}