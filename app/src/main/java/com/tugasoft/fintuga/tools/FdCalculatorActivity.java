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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import androidx.fragment.app.Fragment;
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
import java.util.List;
import java.util.Objects;



public class FdCalculatorActivity extends AppCompatActivity implements Statementsip.OnMessageReadListener {

    public CardView admobcard;
    TextView ansInvestment;
    String appLink;
    Bitmap bmp;
    int date;
    Button day;
    double expectedRate;
    FrameLayout frameLayout;
    RadioGroup group;
    TextView interestText;
    double investmentAmount;
    EditText investmentEdit;
    int mDate, mMonth, mYear;
    TextView maturityDate;
    double maturityValue;
    int month;
    String msMonth;
    SQLiteDatabase myDatabase;
    String name;
    RadioButton radioButton;
    EditText rateEdit;
    String sMonth;
    Bitmap scalebmp;
    DatePickerDialog.OnDateSetListener setListener;
    double tenure;
    EditText timeEdit;
    double totalInterest;
    TextView valueMaturity;
    int year;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(512, 512);
        getWindow().setStatusBarColor(0);
        setContentView((int) R.layout.activity_fd);
        SharedPreferences sharedPreferences2 = getSharedPreferences("mypref", 0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle((int) R.string.fd);
        admobcard = (CardView) findViewById(R.id.admobcard);

        if (!sharedPreferences2.getBoolean("isPurchased", false)) {
            AdmobUnified();
        }
        SQLiteDatabase openOrCreateDatabase = openOrCreateDatabase("EMI", 0, (SQLiteDatabase.CursorFactory) null);
        myDatabase = openOrCreateDatabase;
        openOrCreateDatabase.execSQL("CREATE TABLE IF NOT EXISTS fdTable(name TEXT,principalAmount DOUBLE,interest DOUBLE,tenure DOUBLE,date TEXT,id INTEGER PRIMARY KEY)");
        investmentEdit = (EditText) findViewById(R.id.InvestmentAmoount);
        rateEdit = (EditText) findViewById(R.id.interestAmount);
        timeEdit = (EditText) findViewById(R.id.tenure);
        day = (Button) findViewById(R.id.date);
        ansInvestment = (TextView) findViewById(R.id.totalInvestment);
        interestText = (TextView) findViewById(R.id.totalInterest);
        valueMaturity = (TextView) findViewById(R.id.MaturityValue);
        maturityDate = (TextView) findViewById(R.id.Matuirtydate);
        frameLayout = (FrameLayout) findViewById(R.id.blur);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.togle);
        group = radioGroup;
        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            if (i == R.id.month) {
                FdCalculatorActivity fd = FdCalculatorActivity.this;
                fd.radioButton = (RadioButton) fd.findViewById(R.id.year);
                FdCalculatorActivity.this.radioButton.setTextColor(FdCalculatorActivity.this.getResources().getColor(R.color.fontBlackDisable));
                FdCalculatorActivity fd2 = FdCalculatorActivity.this;
                fd2.radioButton = (RadioButton) fd2.findViewById(R.id.month);
                FdCalculatorActivity.this.radioButton.setTextColor(FdCalculatorActivity.this.getResources().getColor(R.color.fontBlackEnable));
            } else if (i == R.id.year) {
                FdCalculatorActivity fd3 = FdCalculatorActivity.this;
                fd3.radioButton = (RadioButton) fd3.findViewById(R.id.year);
                FdCalculatorActivity.this.radioButton.setTextColor(FdCalculatorActivity.this.getResources().getColor(R.color.fontBlackEnable));
                FdCalculatorActivity fd4 = FdCalculatorActivity.this;
                fd4.radioButton = (RadioButton) fd4.findViewById(R.id.month);
                FdCalculatorActivity.this.radioButton.setTextColor(FdCalculatorActivity.this.getResources().getColor(R.color.fontBlackDisable));
            }
        });
        Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), R.drawable.fddetails);
        bmp = decodeResource;
        scalebmp = Bitmap.createScaledBitmap(decodeResource, 1200, 2010, false);
        Calendar instance = Calendar.getInstance();
        year = instance.get(1);
        month = instance.get(2) + 1;
        date = instance.get(5);
        sMonth = CommonMethod.getShortMonthByNumber(this, month);
        Button button = day;
        button.setText("First EMI: " + date + " " + sMonth + " " + year);
        setListener = (datePicker, i, i2, i3) -> {
            FdCalculatorActivity.this.date = i3;
            FdCalculatorActivity.this.month = i2 + 1;
            FdCalculatorActivity.this.year = i;
            FdCalculatorActivity.this.sMonth = CommonMethod.getShortMonthByNumber(FdCalculatorActivity.this, FdCalculatorActivity.this.month);
            Button button1 = FdCalculatorActivity.this.day;
            button1.setText("First EMI: " + FdCalculatorActivity.this.date + " " + FdCalculatorActivity.this.sMonth + " " + FdCalculatorActivity.this.year);
        };
        String stringExtra = getIntent().getStringExtra("Open");
        if (stringExtra != null) {
            openingSaved(Integer.parseInt(stringExtra));
        }
        this.day.setOnClickListener(view -> {
            FdCalculatorActivity fd = FdCalculatorActivity.this;
            DatePickerDialog datePickerDialog = new DatePickerDialog(fd, android.R.style.Theme_Holo_Dialog_MinWidth, fd.setListener, FdCalculatorActivity.this.year, FdCalculatorActivity.this.month - 1, FdCalculatorActivity.this.date);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            datePickerDialog.show();
        });
    }

    public void calculate(View view) {
        ((ScrollView) findViewById(R.id.scroll)).fullScroll(130);
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (investmentEdit.getText().toString().isEmpty() || rateEdit.getText().toString().isEmpty() || timeEdit.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        tenure = Double.parseDouble(timeEdit.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(group.getCheckedRadioButtonId());
        radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            tenure = Double.parseDouble(timeEdit.getText().toString()) * 12.0d;
        } else {
            tenure = Double.parseDouble(timeEdit.getText().toString());
        }
        if (tenure <= 360.0d && Double.parseDouble(rateEdit.getText().toString()) <= 50.0d) {
            calculation();
            showResult();
        } else if (tenure > 360.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "interestText rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    private void showResult() {
        LinearLayout result = findViewById(R.id.result_layout);
        result.setVisibility(View.VISIBLE);
        ScrollView scrollView = findViewById(R.id.scroll);
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    public void share(View view) {
        if (investmentEdit.getText().toString().isEmpty() || rateEdit.getText().toString().isEmpty() || timeEdit.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        tenure = Double.parseDouble(timeEdit.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(group.getCheckedRadioButtonId());
        radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            tenure = Double.parseDouble(timeEdit.getText().toString()) * 12.0d;
        } else {
            tenure = Double.parseDouble(timeEdit.getText().toString());
        }
        if (tenure <= 360.0d && Double.parseDouble(rateEdit.getText().toString()) <= 50.0d) {
            calculation();
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.SUBJECT", "EMI- A Calculator app");
            intent.putExtra("android.intent.extra.TEXT", "FD Details-\n\ninvestmentEdit Amount : " + investmentAmount + "\ntenure : " + tenure + "months\nFirst SIP: " + date + " " + msMonth + " " + year + "\n\nTotal investmentEdit Amount: " + investmentAmount + "\nTotal interestText: " + totalInterest + "\nMaturity Value: " + maturityValue + "\nMaturity Date: " + date + " " + mMonth + " " + mYear + "\n\nCalculate by EMI\n" + appLink);
            startActivity(Intent.createChooser(intent, "Share Using"));
        } else if (tenure > 360.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "interestText rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void pdf(View view) {
        File file;
        if (investmentEdit.getText().toString().isEmpty() || rateEdit.getText().toString().isEmpty() || timeEdit.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        tenure = Double.parseDouble(timeEdit.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(group.getCheckedRadioButtonId());
        radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            tenure = Double.parseDouble(timeEdit.getText().toString()) * 12.0d;
        } else {
            tenure = Double.parseDouble(timeEdit.getText().toString());
        }
        if (tenure <= 360.0d && Double.parseDouble(rateEdit.getText().toString()) <= 50.0d) {
            calculation();
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE"}, 0);
            PdfDocument pdfDocument = new PdfDocument();
            Paint paint = new Paint();
            PdfDocument.Page startPage = pdfDocument.startPage(new PdfDocument.PageInfo.Builder(1200, 2010, 1).create());
            Canvas canvas = startPage.getCanvas();
            canvas.drawBitmap(scalebmp, 0.0f, 0.0f, paint);
            paint.setTextSize(33.0f);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(investmentAmount + " €", 900.0f, 575.0f, paint);
            canvas.drawText((expectedRate * 1200.0d) + " %", 900.0f, 700.0f, paint);
            canvas.drawText(tenure + "  months", 900.0f, 850.0f, paint);
            canvas.drawText(investmentAmount + " €", 300.0f, 1150.0f, paint);
            canvas.drawText(totalInterest + " €", 900.0f, 1150.0f, paint);
            canvas.drawText(maturityValue + " €", 600.0f, 1450.0f, paint);
            canvas.drawText(date + " " + msMonth + " " + mYear, 600.0f, 1660.0f, paint);
            canvas.drawText(date + " " + mMonth + " " + year, 900.0f, 480.0f, paint);
            pdfDocument.finishPage(startPage);
            if (Build.VERSION.SDK_INT >= 29) {
                file = new File(getExternalCacheDir(), "/FD"+ System.currentTimeMillis() + ".pdf");
            } else {
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/FD"+ System.currentTimeMillis() + ".pdf");
            }
            try {
                pdfDocument.writeTo(new FileOutputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            pdfDocument.close();
            if (file.exists()) {
                CommonMethod.viewPDF(FdCalculatorActivity.this, file);
            }
        } else if (tenure > 360.0d) {
            Toast.makeText(this, "Tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void calculation() {
        investmentAmount = Double.parseDouble(investmentEdit.getText().toString());
        tenure = Double.parseDouble(timeEdit.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(group.getCheckedRadioButtonId());
        radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            tenure = Double.parseDouble(timeEdit.getText().toString()) * 12.0d;
        } else {
            tenure = Double.parseDouble(timeEdit.getText().toString());
        }
        double parseDouble = Double.parseDouble(rateEdit.getText().toString());
        double d = parseDouble / 1200.0d;
        expectedRate = d;
        double pow = investmentAmount * Math.pow(d + 1.0d, tenure);
        maturityValue = pow;
        double d2 = pow - investmentAmount;
        totalInterest = d2;
        if (((double) ((int) d2)) == d2) {
            interestText.setText(String.valueOf((int) d2));
        } else {
            totalInterest = new BigDecimal(totalInterest).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView = interestText;
            textView.setText(totalInterest + " €");
        }
        double d3 = investmentAmount;
        if (((double) ((int) d3)) == d3) {
            TextView textView2 = ansInvestment;
            textView2.setText(((int) d3) + " €");
        } else {
            investmentAmount = new BigDecimal(investmentAmount).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView3 = ansInvestment;
            textView3.setText(investmentAmount + " €");
        }
        double d4 = maturityValue;
        if (((double) ((int) d4)) == d4) {
            valueMaturity.setText(String.valueOf((int) d4));
        } else {
            maturityValue = new BigDecimal(maturityValue).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView4 = valueMaturity;
            textView4.setText(maturityValue + " €");
        }
        int i = (int) (((double) month) + tenure);
        int i2 = (i - 1) / 12;
        mYear = year + i2;
        int i3 = i - (i2 * 12);
        mMonth = i3;
        msMonth = CommonMethod.getShortMonthByNumber(this, i3);
        TextView textView5 = maturityDate;
        textView5.setText(date + " " + msMonth + " " + mYear);
    }

    public void statistic(View view) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (investmentEdit.getText().toString().isEmpty() || rateEdit.getText().toString().isEmpty() || timeEdit.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        tenure = Double.parseDouble(timeEdit.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(group.getCheckedRadioButtonId());
        radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            tenure = Double.parseDouble(timeEdit.getText().toString()) * 12.0d;
        } else {
            tenure = Double.parseDouble(timeEdit.getText().toString());
        }
        if (tenure <= 360.0d && Double.parseDouble(rateEdit.getText().toString()) <= 50.0d) {
            calculation();
            frameLayout.setBackgroundColor(Color.parseColor("#59000000"));
            Statementsip statementsip = new Statementsip(0.0, tenure, expectedRate, investmentAmount, maturityValue, totalInterest, date, month, year, "FD", this);
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.addToBackStack((String) null);
            beginTransaction.setCustomAnimations(R.anim.segmentup, R.anim.segmentdown);
            beginTransaction.add((int) R.id.frame, (Fragment) statementsip, "Fragment").commit();
        } else if (tenure > 360.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "interestText rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void onMessage() {
        frameLayout.setBackgroundColor(Color.parseColor("#00FFFFFF"));
    }

    public void onBackPressed() {
        Statement statement = (Statement) getSupportFragmentManager().findFragmentByTag("TAG_FRAGMENT");
        frameLayout.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        super.onBackPressed();
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
        ListView listView = (ListView) dialog.findViewById(R.id.historyList);
        TextView textView = (TextView) dialog.findViewById(R.id.no_history);
        Cursor rawQuery = myDatabase.rawQuery("SELECT * FROM fdTable", (String[]) null);
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
            int columnIndex = rawQuery.getColumnIndex("id");
            int columnIndex2 = rawQuery.getColumnIndex(AppMeasurementSdk.ConditionalUserProperty.NAME);
            int columnIndex3 = rawQuery.getColumnIndex("principalAmount");
            int columnIndex4 = rawQuery.getColumnIndex("interest");
            int columnIndex5 = rawQuery.getColumnIndex("tenure");
            int columnIndex6 = rawQuery.getColumnIndex(DublinCoreProperties.DATE);
            rawQuery.moveToFirst();
            while (!rawQuery.isAfterLast()) {
                names.add(rawQuery.getString(columnIndex2));
                id.add(rawQuery.getInt(columnIndex));
                principle.add(rawQuery.getDouble(columnIndex3));
                interests.add(rawQuery.getDouble(columnIndex4));
                time.add(rawQuery.getDouble(columnIndex5));
                dateHistory.add(rawQuery.getString(columnIndex6));
                rawQuery.moveToNext();
            }
            noHistory = false;
            HistoryListAdapter historyListAdapter = new HistoryListAdapter(this, names, principle, dateHistory, id, time, (List<Double>) null);
            listView.setAdapter(historyListAdapter);
        }
        if (noHistory) {
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        ((ImageView) dialog.findViewById(R.id.canceling)).setOnClickListener(view -> dialog.dismiss());
    }

    public void save(View view) {
        double d;
        if (investmentEdit.getText().toString().isEmpty() || rateEdit.getText().toString().isEmpty() || timeEdit.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        tenure = Double.parseDouble(timeEdit.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(group.getCheckedRadioButtonId());
        radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            tenure = Double.parseDouble(timeEdit.getText().toString()) * 12.0d;
        } else {
            tenure = Double.parseDouble(timeEdit.getText().toString());
        }
        if (tenure <= 360.0d && Double.parseDouble(rateEdit.getText().toString()) <= 50.0d) {
            calculation();
            double parseDouble = Double.parseDouble(investmentEdit.getText().toString());
            double parseDouble2 = Double.parseDouble(rateEdit.getText().toString());
            final String str = date + " " + month + " " + year;
            if (radioButton.getText().toString().equals("year")) {
                d = Double.parseDouble(timeEdit.getText().toString());
            } else {
                d = Double.parseDouble(timeEdit.getText().toString()) / 12.0d;
            }
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.file_name);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setCanceledOnTouchOutside(true);
            dialog.getWindow().setLayout(-2, -2);
            dialog.getWindow().getAttributes().windowAnimations = 16973826;
            dialog.show();
            final EditText editText = (EditText) dialog.findViewById(R.id.naming);
            ((ImageView) dialog.findViewById(R.id.canceling)).setOnClickListener(v -> dialog.dismiss());
            final double d2 = parseDouble;
            final double d3 = parseDouble2;
            final double d4 = d;
            ((Button) dialog.findViewById(R.id.save)).setOnClickListener(v -> {
                FdCalculatorActivity.this.name = editText.getText().toString();
                if (FdCalculatorActivity.this.name.isEmpty()) {
                    FdCalculatorActivity.this.name = "Untitled";
                }
                SQLiteDatabase sQLiteDatabase = FdCalculatorActivity.this.myDatabase;
                sQLiteDatabase.execSQL("INSERT INTO fdTable(name,principalAmount,interest,tenure,date) VALUES ('" + FdCalculatorActivity.this.name + "'," + d2 + "," + d3 + "," + d4 + ",'" + str + "')");
                dialog.dismiss();
            });
        } else if (tenure > 360.0d) {
            Toast.makeText(this, "Tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void openingSaved(int i) {
        int i2 = i;
        Cursor rawQuery = myDatabase.rawQuery("SELECT * FROM fdTable", (String[]) null);
        ArrayList arrayList = new ArrayList<>();
        ArrayList arrayList2 = new ArrayList<>();
        ArrayList arrayList3 = new ArrayList<>();
        ArrayList arrayList4 = new ArrayList<>();
        ArrayList arrayList5 = new ArrayList<>();
        ArrayList arrayList6 = new ArrayList<>();
        int columnIndex = rawQuery.getColumnIndex("id");
        int columnIndex2 = rawQuery.getColumnIndex(AppMeasurementSdk.ConditionalUserProperty.NAME);
        int columnIndex3 = rawQuery.getColumnIndex("principalAmount");
        int columnIndex4 = rawQuery.getColumnIndex("interest");
        int columnIndex5 = rawQuery.getColumnIndex("tenure");
        int columnIndex6 = rawQuery.getColumnIndex(DublinCoreProperties.DATE);
        rawQuery.moveToFirst();
        while (!rawQuery.isAfterLast()) {
            arrayList3.add(rawQuery.getString(columnIndex2));
            arrayList2.add(rawQuery.getInt(columnIndex));
            arrayList.add(rawQuery.getDouble(columnIndex3));
            arrayList4.add(rawQuery.getDouble(columnIndex4));
            arrayList5.add(rawQuery.getDouble(columnIndex5));
            arrayList6.add(rawQuery.getString(columnIndex6));
            rawQuery.moveToNext();
        }
        investmentEdit.setText(String.valueOf(arrayList.get(i2)));
        timeEdit.setText(String.valueOf(arrayList5.get(i2)));
        rateEdit.setText(String.valueOf(arrayList4.get(i2)));
        day.setText(String.valueOf(arrayList6.get(i2)));
        String[] split = ((String) arrayList6.get(i2)).split(" ");
        date = Integer.parseInt(split[0]);
        String str = split[1];
        sMonth = str;
        month = CommonMethod.getNumberOfShortMonth(this, str);
        year = Integer.parseInt(split[2]);
        tenure = Double.parseDouble(timeEdit.getText().toString()) * 12.0d;
        calculation();
    }

    private void AdmobUnified() {
        AdsProvider.getInstance().addBanner(this,findViewById(R.id.customeventnative_framelayout));
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
        }
        if (menuItem.getItemId() == R.id.history) {
            historyFunction();
        }
        if (menuItem.getItemId() != R.id.action_settings) {
            return super.onOptionsItemSelected(menuItem);
        }

        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history, menu);
        return true;
    }
}