package com.tugasoft.fintuga.tools;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.measurement.api.AppMeasurementSdk;
import com.itextpdf.text.xml.xmp.DublinCoreProperties;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;
import com.tugasoft.fintuga.ads.AdsProvider;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;



public class SwpCalculatorActivity extends AppCompatActivity implements Statementsip.OnMessageReadListener {
    public CardView admobcard;
    String Mmonth, tmonth, name, appLink;
    Bitmap bmp;
    Button day;
    double expectedRate;
    FrameLayout frameLayout;
    RadioGroup group;
    EditText investment, Time, rate;
    TextView maturity, maturityDate;
    SQLiteDatabase myDatabase;
    int myear, year, month, date;
    RadioButton radioButton;
    Bitmap scalebmp;
    DatePickerDialog.OnDateSetListener setListener;
    TextView totalInterest, totalInvestment, withdrawal;
    double totalInterestAmount, totalInvestmentAmount, withdrawalAmount, maturityValue, investmentAmount, Tenure;
    //private String JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundContentColor));
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_swp);
        SharedPreferences sharedPreferences = getSharedPreferences("mypref", 0);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.sys_withdraw);
        this.admobcard = findViewById(R.id.admobcard);

        Log.d("emicalculator", "passed admob");

        if (!sharedPreferences.getBoolean("isPurchased", false)) {
            AdsProvider.getInstance().addBanner(this, findViewById(R.id.customeventnative_framelayout));
        }
        this.investment = findViewById(R.id.InvestmentAmoount);
        this.rate = findViewById(R.id.interestAmount);
        this.Time = findViewById(R.id.tenure);
        this.withdrawal = findViewById(R.id.withdrawalAmoount);
        this.totalInvestment = findViewById(R.id.totalInvestment);
        this.totalInterest = findViewById(R.id.totalInterest);
        this.maturityDate = findViewById(R.id.Matuirtydate);
        this.maturity = findViewById(R.id.MaturityValue);
        SQLiteDatabase openOrCreateDatabase = openOrCreateDatabase("EMI", 0, null);
        this.myDatabase = openOrCreateDatabase;
        openOrCreateDatabase.execSQL("CREATE TABLE IF NOT EXISTS swpTable(name TEXT,principalAmount DOUBLE,interest DOUBLE,tenure DOUBLE,date TEXT,withdrawal DOUBLE,id INTEGER PRIMARY KEY)");
        this.day = findViewById(R.id.date);
        Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), R.drawable.swpdetail);
        this.bmp = decodeResource;
        this.scalebmp = Bitmap.createScaledBitmap(decodeResource, 1200, 2010, false);
        this.frameLayout = findViewById(R.id.blur);
        this.group = findViewById(R.id.togle);
        group.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.month) {
                radioButton = findViewById(R.id.year);
                SwpCalculatorActivity.this.radioButton.setTextColor(SwpCalculatorActivity.this.getResources().getColor(R.color.fontBlackDisable));
                radioButton = findViewById(R.id.month);
                SwpCalculatorActivity.this.radioButton.setTextColor(SwpCalculatorActivity.this.getResources().getColor(R.color.fontBlackEnable
                ));
            } else if (i == R.id.year) {

                radioButton = findViewById(R.id.year);
                radioButton.setTextColor(getResources().getColor(R.color.fontBlackEnable));
                radioButton = findViewById(R.id.month);
                radioButton.setTextColor(getResources().getColor(R.color.fontBlackDisable));
            }
        });
        Calendar instance = Calendar.getInstance();
        this.year = instance.get(1);
        this.month = instance.get(2) + 1;
        this.date = instance.get(5);

        CommonMethod.getShortMonthByNumber(this, month);

        Button button = this.day;
        button.setText("First EMI: " + this.date + " " + this.tmonth + " " + this.year);

        this.setListener = (datePicker, i, i2, i3) -> {
            SwpCalculatorActivity.this.date = i3;
            SwpCalculatorActivity.this.month = i2 + 1;
            SwpCalculatorActivity.this.year = i;

            SwpCalculatorActivity.this.tmonth = CommonMethod.getShortMonthByNumber(this, month);

            Button button1 = SwpCalculatorActivity.this.day;
            button1.setText("First EMI: " + SwpCalculatorActivity.this.date + " " + SwpCalculatorActivity.this.tmonth + " " + SwpCalculatorActivity.this.year);
        };
        String stringExtra = getIntent().getStringExtra("Open");
        if (stringExtra != null) {
            openingSaved(Integer.parseInt(stringExtra));
        }
        this.day.setOnClickListener(view -> {
            SwpCalculatorActivity swp = SwpCalculatorActivity.this;
            DatePickerDialog datePickerDialog = new DatePickerDialog(swp, android.R.style.Theme_Holo_Dialog_MinWidth, swp.setListener, SwpCalculatorActivity.this.year, SwpCalculatorActivity.this.month - 1, SwpCalculatorActivity.this.date);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            datePickerDialog.show();
        });

        Log.d("emicalculator", "passed JANUARY");


        // Initialize month strings
//        JANUARY = getString(R.string.jan);
//        FEBRUARY = getString(R.string.feb);
//        MARCH = getString(R.string.mar);
//        APRIL = getString(R.string.apr);
//        MAY = getString(R.string.may);
//        JUNE = getString(R.string.jun);
//        JULY = getString(R.string.jul);
//        AUGUST = getString(R.string.aug);
//        SEPTEMBER = getString(R.string.sep);
//        OCTOBER = getString(R.string.oct);
//        NOVEMBER = getString(R.string.nov);
//        DECEMBER = getString(R.string.dec);
    }

    public void calculate(View view) {
        ((ScrollView) findViewById(R.id.scroll)).fullScroll(130);
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), Toast.LENGTH_SHORT);

        if (investment.getText().toString().isEmpty() || rate.getText().toString().isEmpty() || Time.getText().toString().isEmpty() || withdrawal.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }

        Tenure = Double.parseDouble(Time.getText().toString());
        RadioButton radioButton2 = findViewById(group.getCheckedRadioButtonId());
        radioButton = radioButton2;

        if (radioButton2.getText().toString().equals("year")) {
            Tenure *= 12.0d;
        }

        if (Tenure <= 360.0d && Double.parseDouble(rate.getText().toString()) <= 50.0d) {
            calculation();
        } else if (Tenure > 360.0d) {
            Toast.makeText(this, "Tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }

        //show result layout
        LinearLayout result = findViewById(R.id.result_layout);
        result.setVisibility(View.VISIBLE);

        ScrollView scrollView = findViewById(R.id.scroll);
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    public void share(View view) {
        if (investment.getText().toString().isEmpty() || rate.getText().toString().isEmpty() || Time.getText().toString().isEmpty() || withdrawal.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }

        Tenure = Double.parseDouble(Time.getText().toString());
        RadioButton radioButton2 = findViewById(group.getCheckedRadioButtonId());
        radioButton = radioButton2;

        if (radioButton2.getText().toString().equals("year")) {
            Tenure = Tenure * 12.0d;
        }

        if (Tenure <= 360.0d && Double.parseDouble(rate.getText().toString()) <= 50.0d) {
            calculation();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "EMi- A Calculator app");
            intent.putExtra(Intent.EXTRA_TEXT, "SWP Details-\n\ninvestment Amount : " + investmentAmount + "\nTenure : " + Tenure + "months\nFirst SIP: " + date + " " + tmonth + " " + year + "\n\nTotal investment Amount: " + totalInvestment + "\nTotal Interest: " + totalInterestAmount + "\nmaturity Value: " + maturityValue + "\nmaturity Date: " + date + " " + Mmonth + " " + myear + "\n\nCalculate by EMI\n" + appLink);
            startActivity(Intent.createChooser(intent, "Share Using"));
        } else if (Tenure > 360.0d) {
            Toast.makeText(this, "Tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void pdf(View view) {
        File outputFile;

        if (investment.getText().toString().isEmpty() || rate.getText().toString().isEmpty() || Time.getText().toString().isEmpty() || withdrawal.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }

        double tenure = Double.parseDouble(Time.getText().toString());
        RadioButton tenureRadioButton = findViewById(group.getCheckedRadioButtonId());

        if (tenureRadioButton.getText().toString().equals("year")) {
            tenure *= 12.0d;
        }

        if (tenure <= 360.0d && Double.parseDouble(rate.getText().toString()) <= 50.0d) {
            calculation();

            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"}, Toast.LENGTH_SHORT);

            PdfDocument pdfDocument = new PdfDocument();
            Paint paint = new Paint();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(scalebmp, Toast.LENGTH_SHORT, Toast.LENGTH_SHORT, paint);
            paint.setTextSize(33.0f);
            paint.setTextAlign(Paint.Align.CENTER);

            radioButton = findViewById(group.getCheckedRadioButtonId());
            canvas.drawText(investmentAmount + " €", 900.0f, 575.0f, paint);
            canvas.drawText((expectedRate * 1200.0d) + " %", 900.0f, 700.0f, paint);
            canvas.drawText(tenure + "  " + radioButton.getText().toString(), 900.0f, 850.0f, paint);
            canvas.drawText(totalInvestmentAmount + " €", 300.0f, 1150.0f, paint);
            canvas.drawText(totalInterestAmount + " €", 900.0f, 1150.0f, paint);
            canvas.drawText(maturityValue + " €", 600.0f, 1450.0f, paint);
            canvas.drawText(date + " " + Mmonth + " " + myear, 600.0f, 1660.0f, paint);
            canvas.drawText(date + " " + tmonth + " " + year, 900.0f, 480.0f, paint);

            pdfDocument.finishPage(page);

            if (Build.VERSION.SDK_INT >= 29) {
                outputFile = new File(getExternalCacheDir(), "/SWP" + System.currentTimeMillis() + ".pdf");
            } else {
                outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/SWP" + System.currentTimeMillis() + ".pdf");
            }

            try {
                pdfDocument.writeTo(new FileOutputStream(outputFile));
            } catch (IOException e) {
                e.printStackTrace();
            }

            pdfDocument.close();

            if (outputFile.exists()) {
                CommonMethod.viewPDF(SwpCalculatorActivity.this, outputFile);
            }
        } else if (tenure > 360.0d) {
            Toast.makeText(this, "Tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void calculation() {
        double investmentAmount = Double.parseDouble(investment.getText().toString());
        double tenure = Double.parseDouble(Time.getText().toString());
        double withdrawalAmount = Double.parseDouble(withdrawal.getText().toString());
        RadioButton tenureRadioButton = findViewById(group.getCheckedRadioButtonId());

        if (tenureRadioButton.getText().toString().equals("year")) {
            tenure *= 12.0d;
        }

        double annualInterestRate = Double.parseDouble(rate.getText().toString()) / 1200.0d;
        double sumOfPowers = 0.0;

        for (int monthCount = 1; monthCount <= tenure; monthCount++) {
            sumOfPowers += Math.pow(annualInterestRate + 1.0d, tenure - 1.0d);
        }

        double maturityValue = investmentAmount * Math.pow(annualInterestRate + 1.0d, tenure) - (sumOfPowers * withdrawalAmount);
        double totalInvestmentAmount = investmentAmount;
        double totalInterestAmount = maturityValue - (withdrawalAmount * tenure);

        if (((double) ((int) totalInterestAmount)) == totalInterestAmount) {
            totalInterest.setText(String.valueOf((int) totalInterestAmount));
        } else {
            totalInterestAmount = new BigDecimal(totalInterestAmount).setScale(1, RoundingMode.HALF_UP).doubleValue();
            totalInterest.setText(totalInterestAmount + " €");
        }

        if (((double) ((int) totalInvestmentAmount)) == totalInvestmentAmount) {
            totalInvestment.setText(((int) totalInvestmentAmount) + " €");
        } else {
            totalInvestmentAmount = new BigDecimal(totalInvestmentAmount).setScale(1, RoundingMode.HALF_UP).doubleValue();
            totalInvestment.setText(totalInvestmentAmount + " €");
        }

        if (((double) ((int) maturityValue)) == maturityValue) {
            maturity.setText(String.valueOf((int) maturityValue));
        } else {
            maturityValue = new BigDecimal(maturityValue).setScale(1, RoundingMode.HALF_UP).doubleValue();
            maturity.setText(maturityValue + " €");
        }

        int totalMonths = (int) (month + tenure);
        int totalYears = year + (totalMonths - 1) / 12;
        int remainingMonths = totalMonths - ((totalYears - year) * 12);
        String monthName = CommonMethod.getShortMonthByNumber(this, remainingMonths);
        maturityDate.setText(date + " " + monthName + " " + totalYears);
    }

    public void statistic(View view) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), Toast.LENGTH_SHORT);
        if (this.investment.getText().toString().isEmpty() || this.rate.getText().toString().isEmpty() || this.Time.getText().toString().isEmpty() || this.withdrawal.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        this.Tenure = Double.parseDouble(this.Time.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(this.group.getCheckedRadioButtonId());
        this.radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            this.Tenure = Double.parseDouble(this.Time.getText().toString()) * 12.0d;
        } else {
            this.Tenure = Double.parseDouble(this.Time.getText().toString());
        }
        if (this.Tenure <= 360.0d && Double.parseDouble(this.rate.getText().toString()) <= 50.0d) {
            calculation();
            this.frameLayout.setBackgroundColor(Color.parseColor("#59000000"));
            Statementsip statementsip = new Statementsip(this.withdrawalAmount, this.Tenure, this.expectedRate, this.investmentAmount, this.maturityValue, this.totalInterestAmount, this.date, this.month, this.year, "SWP", this);
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.addToBackStack(null);
            beginTransaction.setCustomAnimations(R.anim.segmentup, R.anim.segmentdown);
            beginTransaction.add((int) R.id.frame, statementsip, "Fragment").commit();
        } else if (this.Tenure > 360.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void onMessage() {
        this.frameLayout.setBackgroundColor(Color.parseColor("#00FFFFFF"));
    }

    public void onBackPressed() {
        this.frameLayout.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        super.onBackPressed();
    }

    public void openingSaved(int i) {
        Cursor rawQuery = myDatabase.rawQuery("SELECT * FROM swpTable", null);

        ArrayList<Double> withdrawalList = new ArrayList<>();
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<Integer> idList = new ArrayList<>();
        ArrayList<Double> principalAmountList = new ArrayList<>();
        ArrayList<Double> interestList = new ArrayList<>();
        ArrayList<Double> tenureList = new ArrayList<>();
        ArrayList<String> dateList = new ArrayList<>();

        int columnIndexWithdrawal = rawQuery.getColumnIndex("withdrawal");
        int columnIndexId = rawQuery.getColumnIndex("id");
        int columnIndexName = rawQuery.getColumnIndex(AppMeasurementSdk.ConditionalUserProperty.NAME);
        int columnIndexPrincipalAmount = rawQuery.getColumnIndex("principalAmount");
        int columnIndexInterest = rawQuery.getColumnIndex("interest");
        int columnIndexTenure = rawQuery.getColumnIndex("tenure");
        int columnIndexDate = rawQuery.getColumnIndex(DublinCoreProperties.DATE);

        rawQuery.moveToFirst();
        while (!rawQuery.isAfterLast()) {
            withdrawalList.add(rawQuery.getDouble(columnIndexWithdrawal));
            nameList.add(rawQuery.getString(columnIndexName));
            idList.add(rawQuery.getInt(columnIndexId));
            principalAmountList.add(rawQuery.getDouble(columnIndexPrincipalAmount));
            interestList.add(rawQuery.getDouble(columnIndexInterest));
            tenureList.add(rawQuery.getDouble(columnIndexTenure));
            dateList.add(rawQuery.getString(columnIndexDate));
            rawQuery.moveToNext();
        }

        withdrawal.setText(String.valueOf(withdrawalList.get(i)));
        investment.setText(String.valueOf(principalAmountList.get(i)));
        Time.setText(String.valueOf(tenureList.get(i)));
        rate.setText(String.valueOf(interestList.get(i)));
        day.setText(String.valueOf(dateList.get(i)));

        String[] split = dateList.get(i).split(" ");
        date = Integer.parseInt(split[0]);
        String monthStr = split[1];
        tmonth = monthStr;

        month = CommonMethod.getNumberOfShortMonth(this, monthStr);
        year = Integer.parseInt(split[2]);
        Tenure = Double.parseDouble(Time.getText().toString()) * 12.0d;

        calculation();
    }

    public void historyFunction() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.history);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setLayout(-2, -1);
        dialog.getWindow().getAttributes().windowAnimations = 16973826;
        dialog.show();
        ListView listView = dialog.findViewById(R.id.historyList);
        TextView textView = dialog.findViewById(R.id.no_history);
        Cursor rawQuery = this.myDatabase.rawQuery("SELECT * FROM swpTable", null);
        boolean noHistory;
        if (rawQuery == null || rawQuery.getCount() <= 0) {
            noHistory = true;
        } else {
            ArrayList<Double> principle = new ArrayList<>();
            ArrayList<Integer> id = new ArrayList<>();
            ArrayList<String> names = new ArrayList<>();
            ArrayList<Double> interests = new ArrayList<>();
            ArrayList<Double> time = new ArrayList<>();
            ArrayList<String> dateHistory = new ArrayList<>();
            ArrayList<Double> withdrawals = new ArrayList<>();
            int columnIndex = rawQuery.getColumnIndex("withdrawal");
            int columnIndex2 = rawQuery.getColumnIndex("id");
            int columnIndex3 = rawQuery.getColumnIndex(AppMeasurementSdk.ConditionalUserProperty.NAME);
            int columnIndex4 = rawQuery.getColumnIndex("principalAmount");
            int columnIndex5 = rawQuery.getColumnIndex("interest");
            int columnIndex6 = rawQuery.getColumnIndex("tenure");
            int columnIndex7 = rawQuery.getColumnIndex(DublinCoreProperties.DATE);
            rawQuery.moveToFirst();
            while (!rawQuery.isAfterLast()) {
                withdrawals.add(rawQuery.getDouble(columnIndex));
                names.add(rawQuery.getString(columnIndex3));
                id.add(rawQuery.getInt(columnIndex2));
                principle.add(rawQuery.getDouble(columnIndex4));
                interests.add(rawQuery.getDouble(columnIndex5));
                time.add(rawQuery.getDouble(columnIndex6));
                dateHistory.add(rawQuery.getString(columnIndex7));
                rawQuery.moveToNext();
            }
            noHistory = false;
            HistoryListAdapter historyListAdapter = new HistoryListAdapter(this, names, principle, dateHistory, id, time, withdrawals);
            listView.setAdapter(historyListAdapter);
        }
        if (noHistory) {
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        dialog.findViewById(R.id.canceling).setOnClickListener(view -> dialog.dismiss());
    }

    public void save(View view) {
        if (investment.getText().toString().isEmpty() || rate.getText().toString().isEmpty() || Time.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }

        double tenureMonths;
        double investmentAmount = Double.parseDouble(investment.getText().toString());
        double interestRate = Double.parseDouble(rate.getText().toString());
        double withdrawalAmount = Double.parseDouble(withdrawal.getText().toString());
        String dateStr = date + " " + month + " " + year;

        RadioButton selectedRadioButton = findViewById(group.getCheckedRadioButtonId());

        if (selectedRadioButton.getText().toString().equals("year")) {
            tenureMonths = Double.parseDouble(Time.getText().toString()) * 12.0d;
        } else {
            tenureMonths = Double.parseDouble(Time.getText().toString());
        }

        if (tenureMonths <= 360.0d && interestRate <= 50.0d) {
            calculation();

            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.file_name);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setCanceledOnTouchOutside(true);
            dialog.getWindow().setLayout(-2, -2);
            dialog.getWindow().getAttributes().windowAnimations = 16973826;
            dialog.show();

            dialog.findViewById(R.id.canceling).setOnClickListener(view12 -> dialog.dismiss());

            EditText editText = dialog.findViewById(R.id.naming);

            dialog.findViewById(R.id.save).setOnClickListener(view1 -> {

                SwpCalculatorActivity.this.name = editText.getText().toString();

                assert SwpCalculatorActivity.this.name.isEmpty();
                SwpCalculatorActivity.this.name = "Untitled";

                SQLiteDatabase database = myDatabase;
                database.execSQL("INSERT INTO swpTable(name,principalAmount,interest,tenure,date,withdrawal) VALUES " +
                        "('" + name + "'," + investmentAmount + "," + interestRate + "," + tenureMonths + ",'" + dateStr + "'," + withdrawalAmount + ")");

                dialog.dismiss();
            });
        } else if (tenureMonths > 360.0d) {
            Toast.makeText(this, "Tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
        } else if (menuItem.getItemId() == R.id.history) {
            historyFunction();
        } else if (menuItem.getItemId() != R.id.action_settings) {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
}