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
    int mDate;
    int mMonth;
    int mYear;
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

    public void calculate(View view) {
        ((ScrollView) findViewById(R.id.scroll)).fullScroll(130);
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (this.investmentEdit.getText().toString().isEmpty() || this.rateEdit.getText().toString().isEmpty() || this.timeEdit.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        this.tenure = Double.parseDouble(this.timeEdit.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(this.group.getCheckedRadioButtonId());
        this.radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            this.tenure = Double.parseDouble(this.timeEdit.getText().toString()) * 12.0d;
        } else {
            this.tenure = Double.parseDouble(this.timeEdit.getText().toString());
        }
        if (this.tenure <= 360.0d && Double.parseDouble(this.rateEdit.getText().toString()) <= 50.0d) {
            calculation();
        } else if (this.tenure > 360.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "interestText rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void share(View view) {
        if (this.investmentEdit.getText().toString().isEmpty() || this.rateEdit.getText().toString().isEmpty() || this.timeEdit.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        this.tenure = Double.parseDouble(this.timeEdit.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(this.group.getCheckedRadioButtonId());
        this.radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            this.tenure = Double.parseDouble(this.timeEdit.getText().toString()) * 12.0d;
        } else {
            this.tenure = Double.parseDouble(this.timeEdit.getText().toString());
        }
        if (this.tenure <= 360.0d && Double.parseDouble(this.rateEdit.getText().toString()) <= 50.0d) {
            calculation();
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.SUBJECT", "EMI- A Calculator app");
            intent.putExtra("android.intent.extra.TEXT", "FD Details-\n\ninvestmentEdit Amount : " + this.investmentAmount + "\ntenure : " + this.tenure + "months\nFirst SIP: " + this.date + " " + this.msMonth + " " + this.year + "\n\nTotal investmentEdit Amount: " + this.investmentAmount + "\nTotal interestText: " + this.totalInterest + "\nMaturity Value: " + this.maturityValue + "\nMaturity Date: " + this.date + " " + this.mMonth + " " + this.mYear + "\n\nCalculate by EMI\n" + this.appLink);
            startActivity(Intent.createChooser(intent, "Share Using"));
        } else if (this.tenure > 360.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "interestText rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void pdf(View view) {
        File file;
        if (this.investmentEdit.getText().toString().isEmpty() || this.rateEdit.getText().toString().isEmpty() || this.timeEdit.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        this.tenure = Double.parseDouble(this.timeEdit.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(this.group.getCheckedRadioButtonId());
        this.radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            this.tenure = Double.parseDouble(this.timeEdit.getText().toString()) * 12.0d;
        } else {
            this.tenure = Double.parseDouble(this.timeEdit.getText().toString());
        }
        if (this.tenure <= 360.0d && Double.parseDouble(this.rateEdit.getText().toString()) <= 50.0d) {
            calculation();
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE"}, 0);
            PdfDocument pdfDocument = new PdfDocument();
            Paint paint = new Paint();
            PdfDocument.Page startPage = pdfDocument.startPage(new PdfDocument.PageInfo.Builder(1200, 2010, 1).create());
            Canvas canvas = startPage.getCanvas();
            canvas.drawBitmap(this.scalebmp, 0.0f, 0.0f, paint);
            paint.setTextSize(33.0f);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(this.investmentAmount + " €", 900.0f, 575.0f, paint);
            canvas.drawText((this.expectedRate * 1200.0d) + " %", 900.0f, 700.0f, paint);
            canvas.drawText(this.tenure + "  months", 900.0f, 850.0f, paint);
            canvas.drawText(this.investmentAmount + " €", 300.0f, 1150.0f, paint);
            canvas.drawText(this.totalInterest + " €", 900.0f, 1150.0f, paint);
            canvas.drawText(this.maturityValue + " €", 600.0f, 1450.0f, paint);
            canvas.drawText(this.date + " " + this.msMonth + " " + this.mYear, 600.0f, 1660.0f, paint);
            canvas.drawText(this.date + " " + this.mMonth + " " + this.year, 900.0f, 480.0f, paint);
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
        } else if (this.tenure > 360.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "interestText rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void calculation() {
        this.investmentAmount = Double.parseDouble(this.investmentEdit.getText().toString());
        this.tenure = Double.parseDouble(this.timeEdit.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(this.group.getCheckedRadioButtonId());
        this.radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            this.tenure = Double.parseDouble(this.timeEdit.getText().toString()) * 12.0d;
        } else {
            this.tenure = Double.parseDouble(this.timeEdit.getText().toString());
        }
        double parseDouble = Double.parseDouble(this.rateEdit.getText().toString());
        this.expectedRate = parseDouble;
        double d = parseDouble / 1200.0d;
        this.expectedRate = d;
        double pow = this.investmentAmount * Math.pow(d + 1.0d, this.tenure);
        this.maturityValue = pow;
        double d2 = pow - this.investmentAmount;
        this.totalInterest = d2;
        if (((double) ((int) d2)) == d2) {
            this.interestText.setText(String.valueOf((int) d2));
        } else {
            this.totalInterest = new BigDecimal(this.totalInterest).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView = this.interestText;
            textView.setText(this.totalInterest + " €");
        }
        double d3 = this.investmentAmount;
        if (((double) ((int) d3)) == d3) {
            TextView textView2 = this.ansInvestment;
            textView2.setText(((int) d3) + " €");
        } else {
            this.investmentAmount = new BigDecimal(this.investmentAmount).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView3 = this.ansInvestment;
            textView3.setText(this.investmentAmount + " €");
        }
        double d4 = this.maturityValue;
        if (((double) ((int) d4)) == d4) {
            this.valueMaturity.setText(String.valueOf((int) d4));
        } else {
            this.maturityValue = new BigDecimal(this.maturityValue).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView4 = this.valueMaturity;
            textView4.setText(this.maturityValue + " €");
        }
        int i = (int) (((double) this.month) + this.tenure);
        this.mMonth = i;
        int i2 = (i - 1) / 12;
        this.mYear = this.year + i2;
        int i3 = i - (i2 * 12);
        this.mMonth = i3;
        switch (i3) {
            case 1:
                this.msMonth = "Jan";
                break;
            case 2:
                this.msMonth = "Feb";
                break;
            case 3:
                this.msMonth = "Mar";
                break;
            case 4:
                this.msMonth = "April";
                break;
            case 5:
                this.msMonth = "May";
                break;
            case 6:
                this.msMonth = "Jun";
                break;
            case 7:
                this.msMonth = "July";
                break;
            case 8:
                this.msMonth = "Aug";
                break;
            case 9:
                this.msMonth = "Sep";
                break;
            case 10:
                this.msMonth = "Oct";
                break;
            case 11:
                this.msMonth = "Nov";
                break;
            case 12:
                this.msMonth = "Dec";
                break;
        }
        TextView textView5 = this.maturityDate;
        textView5.setText(this.date + " " + this.msMonth + " " + this.mYear);
    }

    public void statistic(View view) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (this.investmentEdit.getText().toString().isEmpty() || this.rateEdit.getText().toString().isEmpty() || this.timeEdit.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        this.tenure = Double.parseDouble(this.timeEdit.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(this.group.getCheckedRadioButtonId());
        this.radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            this.tenure = Double.parseDouble(this.timeEdit.getText().toString()) * 12.0d;
        } else {
            this.tenure = Double.parseDouble(this.timeEdit.getText().toString());
        }
        if (this.tenure <= 360.0d && Double.parseDouble(this.rateEdit.getText().toString()) <= 50.0d) {
            calculation();
            this.frameLayout.setBackgroundColor(Color.parseColor("#59000000"));
            Statementsip statementsip = new Statementsip(0.0, this.tenure, this.expectedRate, this.investmentAmount, this.maturityValue, this.totalInterest, this.date, this.month, this.year, "FD", this);
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.addToBackStack((String) null);
            beginTransaction.setCustomAnimations(R.anim.segmentup, R.anim.segmentdown);
            beginTransaction.add((int) R.id.frame, (Fragment) statementsip, "Fragment").commit();
        } else if (this.tenure > 360.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "interestText rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(512, 512);
        getWindow().setStatusBarColor(0);
        setContentView((int) R.layout.activity_fd);
        SharedPreferences sharedPreferences2 = getSharedPreferences("mypref", 0);
        SharedPreferences.Editor editor = sharedPreferences2.edit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle((int) R.string.fd);
        this.admobcard = (CardView) findViewById(R.id.admobcard);
        
        if (!sharedPreferences2.getBoolean("isPurchased", false)) {
            AdmobUnified();
        }
        SQLiteDatabase openOrCreateDatabase = openOrCreateDatabase("EMI", 0, (SQLiteDatabase.CursorFactory) null);
        this.myDatabase = openOrCreateDatabase;
        openOrCreateDatabase.execSQL("CREATE TABLE IF NOT EXISTS fdTable(name TEXT,principalAmount DOUBLE,interest DOUBLE,tenure DOUBLE,date TEXT,id INTEGER PRIMARY KEY)");
        this.investmentEdit = (EditText) findViewById(R.id.InvestmentAmoount);
        this.rateEdit = (EditText) findViewById(R.id.interestAmount);
        this.timeEdit = (EditText) findViewById(R.id.tenure);
        this.day = (Button) findViewById(R.id.date);
        this.ansInvestment = (TextView) findViewById(R.id.totalInvestment);
        this.interestText = (TextView) findViewById(R.id.totalInterest);
        this.valueMaturity = (TextView) findViewById(R.id.MaturityValue);
        this.maturityDate = (TextView) findViewById(R.id.Matuirtydate);
        this.frameLayout = (FrameLayout) findViewById(R.id.blur);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.togle);
        this.group = radioGroup;
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
        this.bmp = decodeResource;
        this.scalebmp = Bitmap.createScaledBitmap(decodeResource, 1200, 2010, false);
        Calendar instance = Calendar.getInstance();
        this.year = instance.get(1);
        this.month = instance.get(2) + 1;
        this.date = instance.get(5);
        switch (this.month) {
            case 1:
                this.sMonth = "Jan";
                break;
            case 2:
                this.sMonth = "Feb";
                break;
            case 3:
                this.sMonth = "Mar";
                break;
            case 4:
                this.sMonth = "April";
                break;
            case 5:
                this.sMonth = "May";
                break;
            case 6:
                this.sMonth = "Jun";
                break;
            case 7:
                this.sMonth = "July";
                break;
            case 8:
                this.sMonth = "Aug";
                break;
            case 9:
                this.sMonth = "Sep";
                break;
            case 10:
                this.sMonth = "Oct";
                break;
            case 11:
                this.sMonth = "Nov";
                break;
            case 12:
                this.sMonth = "Dec";
                break;
        }
        Button button = this.day;
        button.setText("First EMI: " + this.date + " " + this.sMonth + " " + this.year);
        this.setListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                FdCalculatorActivity.this.date = i3;
                FdCalculatorActivity.this.month = i2 + 1;
                FdCalculatorActivity.this.year = i;
                switch (FdCalculatorActivity.this.month) {
                    case 1:
                        FdCalculatorActivity.this.sMonth = "Jan";
                        break;
                    case 2:
                        FdCalculatorActivity.this.sMonth = "Feb";
                        break;
                    case 3:
                        FdCalculatorActivity.this.sMonth = "Mar";
                        break;
                    case 4:
                        FdCalculatorActivity.this.sMonth = "April";
                        break;
                    case 5:
                        FdCalculatorActivity.this.sMonth = "May";
                        break;
                    case 6:
                        FdCalculatorActivity.this.sMonth = "Jun";
                        break;
                    case 7:
                        FdCalculatorActivity.this.sMonth = "July";
                        break;
                    case 8:
                        FdCalculatorActivity.this.sMonth = "Aug";
                        break;
                    case 9:
                        FdCalculatorActivity.this.sMonth = "Sep";
                        break;
                    case 10:
                        FdCalculatorActivity.this.sMonth = "Oct";
                        break;
                    case 11:
                        FdCalculatorActivity.this.sMonth = "Nov";
                        break;
                    case 12:
                        FdCalculatorActivity.this.sMonth = "Dec";
                        break;
                }
                Button button = FdCalculatorActivity.this.day;
                button.setText("First EMI: " + FdCalculatorActivity.this.date + " " + FdCalculatorActivity.this.sMonth + " " + FdCalculatorActivity.this.year);
            }
        };
        String stringExtra = getIntent().getStringExtra("Open");
        if (stringExtra != null) {
            openingSaved(Integer.parseInt(stringExtra));
        }
        this.day.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FdCalculatorActivity fd = FdCalculatorActivity.this;
                DatePickerDialog datePickerDialog = new DatePickerDialog(fd, android.R.style.Theme_Holo_Dialog_MinWidth, fd.setListener, FdCalculatorActivity.this.year, FdCalculatorActivity.this.month - 1, FdCalculatorActivity.this.date);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                datePickerDialog.show();
            }
        });
    }

    public void onMessage() {
        this.frameLayout.setBackgroundColor(Color.parseColor("#00FFFFFF"));
    }

    public void onBackPressed() {
        Statement statement = (Statement) getSupportFragmentManager().findFragmentByTag("TAG_FRAGMENT");
        this.frameLayout.setBackgroundColor(Color.parseColor("#00FFFFFF"));
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
        Cursor rawQuery = this.myDatabase.rawQuery("SELECT * FROM fdTable", (String[]) null);
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
        ((ImageView) dialog.findViewById(R.id.canceling)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void save(View view) {
        double d;
        if (this.investmentEdit.getText().toString().isEmpty() || this.rateEdit.getText().toString().isEmpty() || this.timeEdit.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        this.tenure = Double.parseDouble(this.timeEdit.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(this.group.getCheckedRadioButtonId());
        this.radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            this.tenure = Double.parseDouble(this.timeEdit.getText().toString()) * 12.0d;
        } else {
            this.tenure = Double.parseDouble(this.timeEdit.getText().toString());
        }
        if (this.tenure <= 360.0d && Double.parseDouble(this.rateEdit.getText().toString()) <= 50.0d) {
            calculation();
            double parseDouble = Double.parseDouble(this.investmentEdit.getText().toString());
            double parseDouble2 = Double.parseDouble(this.rateEdit.getText().toString());
            final String str = this.date + " " + this.month + " " + this.year;
            if (this.radioButton.getText().toString().equals("year")) {
                d = Double.parseDouble(this.timeEdit.getText().toString());
            } else {
                d = Double.parseDouble(this.timeEdit.getText().toString()) / 12.0d;
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
        } else if (this.tenure > 360.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void openingSaved(int i) {
        int i2 = i;
        Cursor rawQuery = this.myDatabase.rawQuery("SELECT * FROM fdTable", (String[]) null);
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
        this.investmentEdit.setText(String.valueOf(arrayList.get(i2)));
        this.timeEdit.setText(String.valueOf(arrayList5.get(i2)));
        this.rateEdit.setText(String.valueOf(arrayList4.get(i2)));
        this.day.setText(String.valueOf(arrayList6.get(i2)));
        String[] split = ((String) arrayList6.get(i2)).split(" ");
        this.date = Integer.parseInt(split[0]);
        String str = split[1];
        this.sMonth = str;
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case 66195:
                if (str.equals("Aug")) {
                    c = 0;
                    break;
                }
                break;
            case 68578:
                if (str.equals("Dec")) {
                    c = 1;
                    break;
                }
                break;
            case 70499:
                if (str.equals("Feb")) {
                    c = 2;
                    break;
                }
                break;
            case 74231:
                if (str.equals("Jan")) {
                    c = 3;
                    break;
                }
                break;
            case 74851:
                if (str.equals("Jun")) {
                    c = 4;
                    break;
                }
                break;
            case 77118:
                if (str.equals("Mar")) {
                    c = 5;
                    break;
                }
                break;
            case 77125:
                if (str.equals("May")) {
                    c = 6;
                    break;
                }
                break;
            case 78517:
                if (str.equals("Nov")) {
                    c = 7;
                    break;
                }
                break;
            case 79104:
                if (str.equals("Oct")) {
                    c = 8;
                    break;
                }
                break;
            case 83006:
                if (str.equals("Sep")) {
                    c = 9;
                    break;
                }
                break;
            case 2320440:
                if (str.equals("July")) {
                    c = 10;
                    break;
                }
                break;
            case 63478374:
                if (str.equals("April")) {
                    c = 11;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                this.month = 8;
                break;
            case 1:
                this.month = 12;
                break;
            case 2:
                this.month = 2;
                break;
            case 3:
                this.month = 1;
                break;
            case 4:
                this.month = 6;
                break;
            case 5:
                this.month = 3;
                break;
            case 6:
                this.month = 5;
                break;
            case 7:
                this.month = 11;
                break;
            case 8:
                this.month = 10;
                break;
            case 9:
                this.month = 9;
                break;
            case 10:
                this.month = 7;
                break;
            case 11:
                this.month = 4;
                break;
        }
        this.year = Integer.parseInt(split[2]);
        this.tenure = Double.parseDouble(this.timeEdit.getText().toString()) * 12.0d;
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
