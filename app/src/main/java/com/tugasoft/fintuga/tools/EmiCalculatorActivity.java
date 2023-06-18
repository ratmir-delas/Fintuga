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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.measurement.api.AppMeasurementSdk;
import com.google.firebase.messaging.FirebaseMessaging;
import com.itextpdf.text.xml.xmp.DublinCoreProperties;
import com.tugasoft.fintuga.application.AppCore;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;
import com.tugasoft.fintuga.utils.Constant;
import com.tugasoft.fintuga.utils.NotificationScheduler;
import com.tugasoft.fintuga.utils.MySharedPreferences;
import com.tugasoft.fintuga.ads.AdsProvider;

import com.tugasoft.fintuga.receiver.AlarmReceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class EmiCalculatorActivity extends AppCompatActivity implements Statement.OnMessageReadListner {
    public CardView admobcard;
    String appLink;
    Bitmap bmp;
    Button dateButton;
    Bitmap dusrabmp;
    String fmonth;
    FrameLayout frameLayout;
    int fyear;
    RadioGroup group;
    double interest, emi, loanamount, period;
    SQLiteDatabase myDatabase;
    String name;
    RadioButton radioButton;
    Bitmap scaleBitmap, scaledusrabmp;
    DatePickerDialog.OnDateSetListener setListener;
    double tinterest, total;
    String tmonth;
    int todate, tomonth, toyear;
    TextView totalemi, totalinterest, totalpayement, totalprincipal;
    EditText yearEditText, principalAmountTextEdit, interestAmountEditText;
    private final String TAG = "Emi";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_emi);
        SharedPreferences sharedPreferences2 = getSharedPreferences("mypref", 0);
        FirebaseMessaging.getInstance().subscribeToTopic(Constant.FIREBASE_NOTIFICATION_MESSAGE_TOPIC);
        //HashMap<String, Object> hashMap = new HashMap<>();
        //hashMap.put("Latest_Application_Version", 11);
        if (!MySharedPreferences.getBool(MySharedPreferences.KEY_IS_REMINDER_SET_ON_APP_LAUNCH)) {
            MySharedPreferences.setBool(MySharedPreferences.KEY_IS_REMINDER_ENABLE, true);
            NotificationScheduler.setReminder(getApplicationContext(), AlarmReceiver.class, MySharedPreferences.get_hour(MySharedPreferences.KEY_REMINDER_HOUR), MySharedPreferences.get_min(MySharedPreferences.KEY_REMINDER_MIN));
            MySharedPreferences.setBool(MySharedPreferences.KEY_IS_REMINDER_SET_ON_APP_LAUNCH, true);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle((int) R.string.emi_calculator);
        this.admobcard = findViewById(R.id.admobcard);

        if (!sharedPreferences2.getBoolean("isPurchased", false)) {
            AdmobUnified();
        }
        this.bmp = BitmapFactory.decodeResource(getResources(), R.drawable.emidetails);
        this.dateButton = findViewById(R.id.date);
        this.scaleBitmap = Bitmap.createScaledBitmap(this.bmp, 1200, 2010, false);
        Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), R.drawable.emipart);
        this.dusrabmp = decodeResource;
        this.scaledusrabmp = Bitmap.createScaledBitmap(decodeResource, 1200, 2010, false);
        SQLiteDatabase openOrCreateDatabase = openOrCreateDatabase("EMI", 0, (SQLiteDatabase.CursorFactory) null);
        this.myDatabase = openOrCreateDatabase;
        openOrCreateDatabase.execSQL("CREATE TABLE IF NOT EXISTS emiTable(name TEXT,principalAmount DOUBLE,interest DOUBLE,tenure DOUBLE,date TEXT,id INTEGER PRIMARY KEY)");
        this.principalAmountTextEdit = findViewById(R.id.principalamoount);
        this.interestAmountEditText = findViewById(R.id.interestamoount);
        this.yearEditText = findViewById(R.id.tenure);
        this.totalemi = findViewById(R.id.emi);
        this.totalemi = findViewById(R.id.emi);
        this.totalinterest = findViewById(R.id.totalinterest);
        this.totalprincipal = findViewById(R.id.totalprncipal);
        this.totalpayement = findViewById(R.id.totalpayement);
        this.frameLayout = findViewById(R.id.blur);
        RadioGroup radioGroup = findViewById(R.id.togle);
        this.group = radioGroup;
        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            if (i == R.id.month) {
                EmiCalculatorActivity emi = EmiCalculatorActivity.this;
                emi.radioButton = emi.findViewById(R.id.year);
                radioButton.setTextColor(getResources().getColor(R.color.fontBlackDisable));
                EmiCalculatorActivity emi2 = EmiCalculatorActivity.this;
                emi2.radioButton = emi2.findViewById(R.id.month);
                radioButton.setTextColor(getResources().getColor(R.color.fontBlackEnable));
            } else if (i == R.id.year) {
                EmiCalculatorActivity emi3 = EmiCalculatorActivity.this;
                emi3.radioButton = emi3.findViewById(R.id.year);
                radioButton.setTextColor(getResources().getColor(R.color.fontBlackEnable));
                EmiCalculatorActivity emi4 = EmiCalculatorActivity.this;
                emi4.radioButton = emi4.findViewById(R.id.month);
                radioButton.setTextColor(getResources().getColor(R.color.fontBlackDisable));
            }
        });
        Calendar instance = Calendar.getInstance();
        this.toyear = instance.get(1);
        this.tomonth = instance.get(2) + 1;
        this.todate = instance.get(5);
        switch (this.tomonth) {
            case 1:
                this.tmonth = "Jan";
                break;
            case 2:
                this.tmonth = "Feb";
                break;
            case 3:
                this.tmonth = "Mar";
                break;
            case 4:
                this.tmonth = "April";
                break;
            case 5:
                this.tmonth = "May";
                break;
            case 6:
                this.tmonth = "Jun";
                break;
            case 7:
                this.tmonth = "July";
                break;
            case 8:
                this.tmonth = "Aug";
                break;
            case 9:
                this.tmonth = "Sep";
                break;
            case 10:
                this.tmonth = "Oct";
                break;
            case 11:
                this.tmonth = "Nov";
                break;
            case 12:
                this.tmonth = "Dec";
                break;
        }

        Button button = this.dateButton;
        button.setText("First EMI: " + this.todate + " " + this.tmonth + " " + this.toyear);
        this.setListener = (datePicker, i, i2, i3) -> {
            todate = i3;
            tomonth = i2 + 1;
            toyear = i;
            switch (tomonth) {
                case 1:
                    tmonth = "Jan";
                    break;
                case 2:
                    tmonth = "Feb";
                    break;
                case 3:
                    tmonth = "Mar";
                    break;
                case 4:
                    tmonth = "April";
                    break;
                case 5:
                    tmonth = "May";
                    break;
                case 6:
                    tmonth = "Jun";
                    break;
                case 7:
                    tmonth = "July";
                    break;
                case 8:
                    tmonth = "Aug";
                    break;
                case 9:
                    tmonth = "Sep";
                    break;
                case 10:
                    tmonth = "Oct";
                    break;
                case 11:
                    tmonth = "Nov";
                    break;
                case 12:
                    tmonth = "Dec";
                    break;
            }
            Button button1 = dateButton;
            button1.setText("First EMI: " + todate + " " + tmonth + " " + toyear);
        };
        String stringExtra = getIntent().getStringExtra("Open");
        if (stringExtra != null) {
            openingSaved(Integer.parseInt(stringExtra));
        }
        this.dateButton.setOnClickListener(view -> {
            EmiCalculatorActivity emi = EmiCalculatorActivity.this;
            DatePickerDialog datePickerDialog = new DatePickerDialog(emi, android.R.style.Theme_Holo_Dialog_MinWidth, emi.setListener, toyear, tomonth, todate);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            datePickerDialog.show();
        });
    }

    public void calculate(View view) {
        ((ScrollView) findViewById(R.id.scroll)).fullScroll(130);
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (this.principalAmountTextEdit.getText().toString().isEmpty() || this.interestAmountEditText.getText().toString().isEmpty() || this.yearEditText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        this.period = Double.parseDouble(this.yearEditText.getText().toString());
        RadioButton radioButton2 = findViewById(this.group.getCheckedRadioButtonId());
        this.radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            this.period = Double.parseDouble(this.yearEditText.getText().toString()) * 12.0d;
        } else {
            this.period = Double.parseDouble(this.yearEditText.getText().toString());
        }
        if (this.period <= 360.0d && Double.parseDouble(this.interestAmountEditText.getText().toString()) <= 50.0d) {
            calculation();
            showResult();
        } else if (this.period > 360.0d) {
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
        this.loanamount = Double.parseDouble(this.principalAmountTextEdit.getText().toString());
        double parseDouble = Double.parseDouble(this.interestAmountEditText.getText().toString());
        double d = parseDouble / 1200.0d;
        this.interest = d;
        double pow = (this.loanamount * d) / (1.0d - Math.pow(d + 1.0d, -this.period));
        double d2 = this.period;
        double d3 = this.loanamount;
        double d4 = (pow * d2) - d3;
        this.tinterest = d4;
        this.total = d4 + d3;
        double pow2 = Math.pow(this.interest + 1.0d, d2);
        this.emi = ((this.loanamount * this.interest) * pow2) / (pow2 - 1.0d);
        TextView textView = this.totalprincipal;
        textView.setText(this.loanamount + " €");
        double d5 = this.tinterest;
        if (((double) ((int) d5)) == d5) {
            this.totalinterest.setText(String.valueOf((int) d5));
        } else {
            this.tinterest = new BigDecimal(this.tinterest).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView2 = this.totalinterest;
            textView2.setText(this.tinterest + " €");
        }
        double d6 = this.total;
        if (((double) ((int) d6)) == d6) {
            TextView textView3 = this.totalpayement;
            textView3.setText(((int) d6) + " €");
        } else {
            this.total = new BigDecimal(this.total).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView4 = this.totalpayement;
            textView4.setText(this.total + " €");
        }
        double d7 = this.emi;
        if (((double) ((int) d7)) == d7) {
            this.totalemi.setText(String.valueOf((int) d7));
        } else {
            this.emi = new BigDecimal(this.emi).setScale(1, RoundingMode.HALF_UP).doubleValue();
            TextView textView5 = this.totalemi;
            textView5.setText(this.emi + " €");
        }
        int i = (int) (((double) this.tomonth) + this.period);
        int i2 = (i - 1) / 12;
        this.fyear = this.toyear + i2;
        switch (i - (i2 * 12)) {
            case 1:
                this.fmonth = "Jan";
                break;
            case 2:
                this.fmonth = "Feb";
                break;
            case 3:
                this.fmonth = "Mar";
                break;
            case 4:
                this.fmonth = "April";
                break;
            case 5:
                this.fmonth = "May";
                break;
            case 6:
                this.fmonth = "Jun";
                break;
            case 7:
                this.fmonth = "July";
                break;
            case 8:
                this.fmonth = "Aug";
                break;
            case 9:
                this.fmonth = "Sep";
                break;
            case 10:
                this.fmonth = "Oct";
                break;
            case 11:
                this.fmonth = "Nov";
                break;
            case 12:
                this.fmonth = "Dec";
                break;
        }
        ((TextView) findViewById(R.id.finaldate)).setText(this.todate + " " + this.fmonth + " " + this.fyear);
    }

    public void statistic(View view) {
        if (this.principalAmountTextEdit.getText().toString().isEmpty() || this.interestAmountEditText.getText().toString().isEmpty() || this.yearEditText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        this.period = Double.parseDouble(this.yearEditText.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(this.group.getCheckedRadioButtonId());
        this.radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            this.period = Double.parseDouble(this.yearEditText.getText().toString()) * 12.0d;
        } else {
            this.period = Double.parseDouble(this.yearEditText.getText().toString());
        }
        if (this.period <= 360.0d && Double.parseDouble(this.interestAmountEditText.getText().toString()) <= 50.0d) {
            calculation();
            this.frameLayout.setBackgroundColor(Color.parseColor("#59000000"));
            Statement statement = new Statement(this.period, this.interest, this.loanamount, this.emi, this.tinterest, this.todate, this.tomonth, this.toyear);
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.addToBackStack((String) null);
            beginTransaction.setCustomAnimations(R.anim.segmentup, R.anim.segmentdown);
            beginTransaction.add((int) R.id.frame, (Fragment) statement, "Fragment").commit();
        } else if (this.period > 360.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void share(View view) {
        if (this.principalAmountTextEdit.getText().toString().isEmpty() || this.interestAmountEditText.getText().toString().isEmpty() || this.yearEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter the value", Toast.LENGTH_SHORT).show();
            return;
        }
        this.period = Double.parseDouble(this.yearEditText.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(this.group.getCheckedRadioButtonId());
        this.radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            this.period = Double.parseDouble(this.yearEditText.getText().toString()) * 12.0d;
        } else {
            this.period = Double.parseDouble(this.yearEditText.getText().toString());
        }
        if (this.period <= 30.0d || Double.parseDouble(this.interestAmountEditText.getText().toString()) <= 50.0d) {
            calculation();
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.SUBJECT", "EMI- A Calculator app");
            intent.putExtra("android.intent.extra.TEXT", "EMI Details-\n \nPrincipal Loan Amount: " + String.valueOf(this.loanamount) + "\nLoan term: " + String.valueOf(this.period) + "\nFirst EMI at: " + String.valueOf(this.todate) + " " + this.tmonth + " " + String.valueOf(this.toyear) + "\n\nMonthly EMI: " + this.emi + "\nTotal Interest: " + String.valueOf(this.tinterest) + "\nTotal payment: " + String.valueOf(this.tinterest + this.loanamount) + "\nLast Loan Date: " + String.valueOf(this.todate) + " " + this.fmonth + " " + String.valueOf(this.fyear) + "\n\nCalculate by EMI\n" + this.appLink);
            startActivity(Intent.createChooser(intent, "Share Using"));
        } else if (this.period > 30.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    public void pdf(View view) {
        File file;
        if (this.principalAmountTextEdit.getText().toString().isEmpty() || this.interestAmountEditText.getText().toString().isEmpty() || this.yearEditText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }

        this.period = Double.parseDouble(this.yearEditText.getText().toString());
        RadioButton radioButton2 = (RadioButton) findViewById(this.group.getCheckedRadioButtonId());
        this.radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            this.period = Double.parseDouble(this.yearEditText.getText().toString()) * 12.0d;
        } else {
            this.period = Double.parseDouble(this.yearEditText.getText().toString());
        }
        if (this.period <= 30.0d || Double.parseDouble(this.interestAmountEditText.getText().toString()) <= 50.0d) {
            calculation();
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE"}, 0);
            PdfDocument pdfDocument = new PdfDocument();
            Paint paint = new Paint();
            PdfDocument.Page startPage = pdfDocument.startPage(new PdfDocument.PageInfo.Builder(1200, 2010, 1).create());
            Canvas canvas = startPage.getCanvas();
            canvas.drawBitmap(this.scaleBitmap, 0.0f, 0.0f, paint);
            paint.setTextSize(33.0f);
            paint.setTextAlign(Paint.Align.CENTER);
            this.radioButton = (RadioButton) findViewById(this.group.getCheckedRadioButtonId());
            canvas.drawText(this.loanamount + " €", 900.0f, 575.0f, paint);
            canvas.drawText((this.interest * 1200.0d) + " %", 900.0f, 700.0f, paint);
            canvas.drawText(this.period + "  " + this.radioButton.getText().toString(), 900.0f, 850.0f, paint);
            StringBuilder sb = new StringBuilder();
            sb.append(this.tinterest);
            sb.append(" €");
            canvas.drawText(sb.toString(), 300.0f, 1150.0f, paint);
            canvas.drawText(this.loanamount + " €", 900.0f, 1150.0f, paint);
            canvas.drawText(this.total + " €", 600.0f, 1450.0f, paint);
            canvas.drawText(this.todate + " " + this.fmonth + " " + this.fyear, 600.0f, 1660.0f, paint);
            canvas.drawText(this.todate + " " + this.tmonth + " " + this.toyear, 900.0f, 480.0f, paint);
            pdfDocument.finishPage(startPage);
            PdfDocument.Page startPage2 = pdfDocument.startPage(new PdfDocument.PageInfo.Builder(1200, 2010, 2).create());
            Canvas canvas2 = startPage2.getCanvas();
            canvas2.drawBitmap(this.scaledusrabmp, 0.0f, 0.0f, paint);
            canvas2.drawText(String.valueOf(this.emi) + " €", 600.0f, 375.0f, paint);
            pdfDocument.finishPage(startPage2);
            if (Build.VERSION.SDK_INT >= 29) {
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/Emi"+ AppCore.replaceSpecialCharacters(String.valueOf(System.currentTimeMillis())) + ".pdf");
            } else {
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/Emi"+ AppCore.replaceSpecialCharacters(String.valueOf(System.currentTimeMillis())) + ".pdf");
            }
            try {
                pdfDocument.writeTo(new FileOutputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            pdfDocument.close();
            Log.e(TAG, "pdf: file:"+file );
            if (file.exists()) {
                CommonMethod.viewPDF(EmiCalculatorActivity.this, file);

            }else {
                Toast.makeText(this, "Something, wrong", Toast.LENGTH_SHORT).show();
            }
        } else if (this.period > 30.0d) {
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
        Cursor rawQuery = this.myDatabase.rawQuery("SELECT * FROM emiTable", (String[]) null);
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
            Log.e(TAG, "historyFunction: " + names.get(0) );
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

    public void openingSaved(int i) {
        Cursor rawQuery = this.myDatabase.rawQuery("SELECT * FROM emiTable", null);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        ArrayList arrayList5 = new ArrayList();
        ArrayList arrayList6 = new ArrayList();
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
        this.principalAmountTextEdit.setText(String.valueOf(arrayList.get(i)));
        this.yearEditText.setText(String.valueOf(arrayList5.get(i)));
        this.interestAmountEditText.setText(String.valueOf(arrayList4.get(i)));
        this.dateButton.setText(String.valueOf(arrayList6.get(i)));
        String[] split = ((String) arrayList6.get(i)).split(" ");
        this.todate = Integer.parseInt(split[0]);
        String str = split[1];
        this.tmonth = str;
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
                this.tomonth = 8;
                break;
            case 1:
                this.tomonth = 12;
                break;
            case 2:
                this.tomonth = 2;
                break;
            case 3:
                this.tomonth = 1;
                break;
            case 4:
                this.tomonth = 6;
                break;
            case 5:
                this.tomonth = 3;
                break;
            case 6:
                this.tomonth = 5;
                break;
            case 7:
                this.tomonth = 11;
                break;
            case 8:
                this.tomonth = 10;
                break;
            case 9:
                this.tomonth = 9;
                break;
            case 10:
                this.tomonth = 7;
                break;
            case 11:
                this.tomonth = 4;
                break;
        }
        this.toyear = Integer.parseInt(split[2]);
        this.period = Double.parseDouble(this.yearEditText.getText().toString()) * 12.0d;
        calculation();
    }

    public void save(View view) {
        double d;
        if (this.principalAmountTextEdit.getText().toString().isEmpty() || this.interestAmountEditText.getText().toString().isEmpty() || this.yearEditText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        this.period = Double.parseDouble(this.yearEditText.getText().toString());
        RadioButton radioButton2 = findViewById(this.group.getCheckedRadioButtonId());
        this.radioButton = radioButton2;
        if (radioButton2.getText().toString().equals("year")) {
            this.period = Double.parseDouble(this.yearEditText.getText().toString()) * 12.0d;
        } else {
            this.period = Double.parseDouble(this.yearEditText.getText().toString());
        }
        if (this.period <= 360.0d && Double.parseDouble(this.interestAmountEditText.getText().toString()) <= 50.0d) {
            calculation();
            double parseDouble = Double.parseDouble(this.principalAmountTextEdit.getText().toString());
            double parseDouble2 = Double.parseDouble(this.interestAmountEditText.getText().toString());
            final String str = this.todate + " " + this.tmonth + " " + this.toyear;
            if (this.radioButton.getText().toString().equals("year")) {
                d = Double.parseDouble(this.yearEditText.getText().toString());
            } else {
                d = Double.parseDouble(this.yearEditText.getText().toString()) / 12.0d;
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
            ((ImageView) dialog.findViewById(R.id.canceling)).setOnClickListener(view12 -> dialog.dismiss());
            final double d2 = parseDouble;
            final double d3 = parseDouble2;
            final double d4 = d;
            ((Button) dialog.findViewById(R.id.save)).setOnClickListener(view1 -> {
                name = editText.getText().toString();
                if (name.isEmpty()) {
                    name = "Untitled";
                }
                SQLiteDatabase sQLiteDatabase = myDatabase;
                sQLiteDatabase.execSQL("INSERT INTO emiTable(name,principalAmount,interest,tenure,date) VALUES ('" + name + "'," + d2 + "," + d3 + "," + d4 + ",'" + str + "')");
                dialog.dismiss();
            });
        } else if (this.period > 360.0d) {
            Toast.makeText(this, "tenure should be less than 30 years", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Interest rate should be less than 50%", Toast.LENGTH_SHORT).show();
        }
    }

    private void AdmobUnified() {
        AdsProvider.getInstance().addBanner(this, findViewById(R.id.customeventnative_framelayout));
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