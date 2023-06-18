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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
    double amount1, amount2;
    Bitmap bmp;
    TextView difEmi, difInter, difTotal;
    double diffEmiVal, diffInterest, diffPayment;
    Bitmap dusrabmp;
    TextView emi1, emi2;
    double emiVal1, emiVal2;
    RadioGroup group;
    TextView interest1, interest2;
    EditText principal1, principal2;
    RadioButton radioButton;
    EditText rate1, rate2;
    double rateVal1, rateVal2;
    Bitmap scalebmp, scaledusrabmp;
    double tenure1, tenure2;
    EditText time1, time2;
    TextView total1, total2;
    double totalInterest1, totalInterest2, totalPayment1, totalPayment2;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundContentColor));
        getWindow().setFlags(1024, 1024);
        setContentView((int) R.layout.activity_compare_loan);
        SharedPreferences sharedPreferences2 = getSharedPreferences("mypref", 0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle((int) R.string.compare);
        admobcard = (CardView) findViewById(R.id.admobcard);

        if (!sharedPreferences2.getBoolean("isPurchased", false)) {
            AdsProvider.getInstance().addBanner(this, findViewById(R.id.customeventnative_framelayout));
        }
        principal1 = (EditText) findViewById(R.id.principalamoount1);
        principal2 = (EditText) findViewById(R.id.principalamoount2);
        rate1 = (EditText) findViewById(R.id.interestamoount1);
        rate2 = (EditText) findViewById(R.id.interestamoount2);
        time1 = (EditText) findViewById(R.id.tenure1);
        time2 = (EditText) findViewById(R.id.tenure2);
        emi1 = (TextView) findViewById(R.id.emi1);
        emi2 = (TextView) findViewById(R.id.emi2);
        interest1 = (TextView) findViewById(R.id.totalinterest1);
        interest2 = (TextView) findViewById(R.id.totalinterest2);
        total1 = (TextView) findViewById(R.id.totalamount1);
        total2 = (TextView) findViewById(R.id.totalamount2);
        difEmi = (TextView) findViewById(R.id.emidifference);
        difInter = (TextView) findViewById(R.id.interestdifference);
        difTotal = (TextView) findViewById(R.id.paymentdifference);
        group = (RadioGroup) findViewById(R.id.togle);
        Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), R.drawable.compareone);
        bmp = decodeResource;
        scalebmp = Bitmap.createScaledBitmap(decodeResource, 1200, 2010, false);
        Bitmap decodeResource2 = BitmapFactory.decodeResource(getResources(), R.drawable.comparetwo);
        dusrabmp = decodeResource2;
        scaledusrabmp = Bitmap.createScaledBitmap(decodeResource2, 1200, 2010, false);
        group.setOnCheckedChangeListener((radioGroup, i) -> {
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
        if (principal1.getText().toString().isEmpty() || principal2.getText().toString().isEmpty() || rate1.getText().toString().isEmpty() || rate2.getText().toString().isEmpty() || time1.getText().toString().isEmpty() || time2.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter the * value", Toast.LENGTH_SHORT).show();
            return;
        }
        tenure1 = Double.parseDouble(time1.getText().toString());
        tenure2 = Double.parseDouble(time2.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(group.getCheckedRadioButtonId());
        radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            tenure1 = Double.parseDouble(time1.getText().toString()) * 12.0d;
            tenure2 = Double.parseDouble(time2.getText().toString()) * 12.0d;
        } else {
            tenure1 = Double.parseDouble(time1.getText().toString());
            tenure2 = Double.parseDouble(time2.getText().toString());
        }
        if (tenure1 <= 30.0d || tenure2 <= 30.0d || Double.parseDouble(rate1.getText().toString()) <= 50.0d || Double.parseDouble(rate2.getText().toString()) <= 50.0d) {
            calculation();
            showResult();
        } else if (tenure1 > 30.0d || tenure2 > 30.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    private void showResult() {
        LinearLayout result = findViewById(R.id.result_layout);
        result.setVisibility(View.VISIBLE);
        ScrollView scrollView = findViewById(R.id.scroll);
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    public void calculation() {
        ((ScrollView) findViewById(R.id.scroll)).fullScroll(130);
        amount1 = Double.parseDouble(principal1.getText().toString());
        amount2 = Double.parseDouble(principal2.getText().toString());
        rateVal1 = Double.parseDouble(rate1.getText().toString()) / 1200.0d;
        rateVal2 = Double.parseDouble(rate2.getText().toString()) / 1200.0d;
        double d = amount1;
        double d2 = rateVal1;
        double pow = (d * d2) / (1.0d - Math.pow(d2 + 1.0d, -tenure1));
        double d3 = tenure1;
        double d4 = amount1;
        totalInterest1 = (pow * d3) - d4;
        double d5 = rateVal1;
        totalPayment1 = d4 + d5;
        double pow2 = Math.pow(d5 + 1.0d, d3);
        emiVal1 = ((amount1 * rateVal1) * pow2) / (pow2 - 1.0d);
        double d6 = amount2;
        double d7 = rateVal2;
        double pow3 = (d6 * d7) / (1.0d - Math.pow(d7 + 1.0d, -tenure2));
        double d8 = tenure2;
        double d9 = amount2;
        totalInterest2 = (pow3 * d8) - d9;
        double d10 = rateVal2;
        totalPayment2 = d9 + d10;
        double pow4 = Math.pow(d10 + 1.0d, d8);
        emiVal2 = ((amount2 * rateVal2) * pow4) / (pow4 - 1.0d);
        double d11 = totalPayment2;
        if (((double) ((int) d11)) == d11) {
            total2.setText(String.valueOf((int) d11));
        } else {
            totalPayment2 = new BigDecimal(totalPayment2).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView = total2;
            textView.setText(totalPayment2 + " €");
        }
        double d12 = totalPayment1;
        if (((double) ((int) d12)) == d12) {
            total1.setText(String.valueOf((int) d12));
        } else {
            totalPayment1 = new BigDecimal(totalPayment1).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView2 = total1;
            textView2.setText(totalPayment1 + " €");
        }
        double d13 = totalPayment1;
        double d14 = totalPayment2;
        if (d13 > d14) {
            // Green 26CA6A
            // Red   F80606
            total1.setTextColor(Color.parseColor("#F80606"));
            total2.setTextColor(Color.parseColor("#26CA6A"));
            diffPayment = totalPayment1 - totalPayment2;
        } else if (d13 == d14) {
            total1.setTextColor(Color.parseColor("#000000"));
            total2.setTextColor(Color.parseColor("#000000"));
            diffPayment = 0.0;
        } else {
            total1.setTextColor(Color.parseColor("#26CA6A"));
            total2.setTextColor(Color.parseColor("#F80606"));
            diffPayment = totalPayment2 - totalPayment1;
        }
        diffPayment = new BigDecimal(diffPayment).setScale(1, RoundingMode.HALF_UP).doubleValue();
        TextView textView3 = difTotal;
        textView3.setText(diffPayment + " €");
        double d15 = totalInterest2;
        if (((double) ((int) d15)) == d15) {
            interest2.setText(String.valueOf((int) d15));
        } else {
            totalInterest2 = new BigDecimal(totalInterest2).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView4 = interest2;
            textView4.setText(totalInterest2 + " €");
        }
        double d16 = totalInterest1;
        if (((double) ((int) d16)) == d16) {
            interest1.setText(String.valueOf((int) d16));
        } else {
            totalInterest1 = new BigDecimal(totalInterest1).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView5 = interest1;
            textView5.setText(totalInterest1 + " €");
        }
        double d17 = totalInterest1;
        double d18 = totalInterest2;
        if (d17 > d18) {
            // Green 26CA6A
            // Red   F80606
            diffInterest = d17 - d18;
            interest1.setTextColor(Color.parseColor("#F80606"));
            interest2.setTextColor(Color.parseColor("#26CA6A"));
        } else if (d17 == d18) {
            diffInterest = 0.0;
            interest1.setTextColor(Color.parseColor("#000000"));
            interest2.setTextColor(Color.parseColor("#000000"));
        } else {
            diffInterest = d18 - d17;
            interest1.setTextColor(Color.parseColor("#26CA6A"));
            interest2.setTextColor(Color.parseColor("#F80606"));
        }
        diffInterest = new BigDecimal(diffInterest).setScale(1, RoundingMode.HALF_UP).doubleValue();
        TextView textView6 = difInter;
        textView6.setText(diffInterest + " €");
        double d19 = emiVal2;
        if (((double) ((int) d19)) == d19) {
            emi2.setText(String.valueOf((int) d19));
        } else {
            emiVal2 = new BigDecimal(emiVal2).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView7 = emi2;
            textView7.setText(emiVal2 + " €");
        }
        double d20 = emiVal1;
        if (((double) ((int) d20)) == d20) {
            emi1.setText(String.valueOf((int) d20));
        } else {
            emiVal1 = new BigDecimal(emiVal1).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView8 = emi1;
            textView8.setText(emiVal1 + " €");
        }
        double d21 = emiVal1;
        double d22 = emiVal2;
        if (d21 > d22) {
            diffEmiVal = d21 - d22;
            // Green 26CA6A
            // Red   F80606
            emi1.setTextColor(Color.parseColor("#F80606"));
            emi2.setTextColor(Color.parseColor("#26CA6A"));
        } else if (d21 == d22) {
            diffEmiVal = 0.0;
            emi1.setTextColor(Color.parseColor("#000000"));
            emi2.setTextColor(Color.parseColor("#000000"));
        } else {
            diffEmiVal = d22 - d21;
            emi1.setTextColor(Color.parseColor("#26CA6A"));
            emi2.setTextColor(Color.parseColor("#F80606"));
        }
        diffEmiVal = new BigDecimal(diffEmiVal).setScale(1, RoundingMode.HALF_UP).doubleValue();
        TextView textView9 = difEmi;
        textView9.setText(diffEmiVal + " €");
    }

    public void pdf(View view) {
        File file;
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (principal1.getText().toString().isEmpty() || principal2.getText().toString().isEmpty() || rate1.getText().toString().isEmpty() || rate2.getText().toString().isEmpty() || time1.getText().toString().isEmpty() || time2.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter the * value", Toast.LENGTH_SHORT).show();
            return;
        }
        tenure1 = Double.parseDouble(time1.getText().toString());
        tenure2 = Double.parseDouble(time2.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(group.getCheckedRadioButtonId());
        radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            tenure1 = Double.parseDouble(time1.getText().toString()) * 12.0d;
            tenure2 = Double.parseDouble(time2.getText().toString()) * 12.0d;
        } else {
            tenure1 = Double.parseDouble(time1.getText().toString());
            tenure2 = Double.parseDouble(time2.getText().toString());
        }
        if (tenure1 <= 30.0d || tenure2 <= 30.0d || Double.parseDouble(rate1.getText().toString()) <= 50.0d || Double.parseDouble(rate2.getText().toString()) <= 50.0d) {
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
            canvas.drawText(amount1 + " €", 300.0f, 735.0f, paint);
            canvas.drawText(amount2 + " €", 900.0f, 735.0f, paint);
            canvas.drawText((rateVal1 * 1200.0d) + " €", 300.0f, 985.0f, paint);
            canvas.drawText((rateVal2 * 1200.0d) + " €", 900.0f, 985.0f, paint);
            canvas.drawText(tenure1 + " months", 300.0f, 1235.0f, paint);
            canvas.drawText(tenure2 + " months", 900.0f, 1235.0f, paint);
            canvas.drawText(emiVal1 + " €", 300.0f, 1550.0f, paint);
            canvas.drawText(emiVal2 + " €", 900.0f, 1550.0f, paint);
            canvas.drawText(String.valueOf(diffEmiVal), 750.0f, 1695.0f, paint);
            pdfDocument.finishPage(startPage);
            PdfDocument.Page startPage2 = pdfDocument.startPage(new PdfDocument.PageInfo.Builder(1200, 2010, 2).create());
            Canvas canvas2 = startPage2.getCanvas();
            canvas2.drawBitmap(scaledusrabmp, 0.0f, 0.0f, paint);
            canvas2.drawText(totalInterest1 + " €", 300.0f, 340.0f, paint);
            canvas2.drawText(totalInterest2 + " €", 900.0f, 340.0f, paint);
            canvas2.drawText(String.valueOf(diffInterest), 750.0f, 480.0f, paint);
            canvas2.drawText(totalPayment1 + " €", 300.0f, 825.0f, paint);
            canvas2.drawText(totalPayment2 + " €", 900.0f, 825.0f, paint);
            canvas2.drawText(String.valueOf(diffInterest), 750.0f, 960.0f, paint);
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
        } else if (tenure1 > 30.0d || tenure2 > 30.0d) {
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