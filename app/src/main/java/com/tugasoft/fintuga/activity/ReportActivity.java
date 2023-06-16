package com.tugasoft.fintuga.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tugasoft.fintuga.fragments.ReportsInDetailsFragment;
import com.tugasoft.fintuga.models.Expense;
import com.tugasoft.fintuga.models.MasterExpenseModel;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;
import com.tugasoft.fintuga.utils.Constant;
import com.tugasoft.fintuga.utils.MySharedPreferences;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import info.hoang8f.android.segmented.SegmentedGroup;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class ReportActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    static final boolean $assertionsDisabled = false;
    private static final String TAG = "Report pie chart screen";
    private static View rootView;
    public SimpleDateFormat dateFormat;
    public String date_1;
    public String date_2;
    public HashMap<String, String> hashMap;
    public long mTotalBalance = 0;
    public long mTotalExpense = 0;
    public long mTotalIncome = 0;
    public ArrayList<MasterExpenseModel> mTransactionList = new ArrayList<>();
    public PieChartData pieChartData;
    public PieChartView pieChartView;
    public String qurter_count;
    public String selected_quarter_year;
    public Button summary_report;
    public TextView text_from_date;
    public TextView text_to_date;
    CardView mCvEndDate;
    CardView mCvStartDate;
    SimpleDateFormat monthYearParser = new SimpleDateFormat(Constant.MONTH_YEAR_FORMAT, Locale.US);
    RadioButton rbFull;
    RadioButton rbHalf;
    RadioButton rbMonthly;
    RadioButton rbQuarter;
    TextView textHalfYearly;
    TextView textMonthly;
    TextView textQuarterly;
    TextView textYearly;
    TextView txt_balance;
    TextView txt_expense;
    TextView txt_income;
    String type = null;
    View vHalfYearly;
    View vMonthly;
    View vQuarterly;
    View vYearly;
    LinearLayout yearly_view;
    private Bundle bundle;
    private Calendar calendar;
    private FrameLayout mContainer;
    private Menu mMenu;
    private SimpleDateFormat monthParser = new SimpleDateFormat("MM", Locale.US);
    private TableRow monthly_view;
    private SegmentedGroup segmented2;
    private Button show_report;
    private SimpleDateFormat yearParser = new SimpleDateFormat(Constant.YEAR_FORMAT, Locale.US);

    @Override
    public void onCreate(Bundle bundle2) {
        super.onCreate(bundle2);
        setContentView((int) R.layout.activity_reports);
        setTooBar();
        init();
    }

    private void setTooBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle((CharSequence) "Reports");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.black));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(ContextCompat.getColor(this, android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ReportActivity.this.onBackPressed();
            }
        });
    }

    private void init() {
        this.mContainer = (FrameLayout) findViewById(R.id.fragment_container);
        this.mCvStartDate = (CardView) findViewById(R.id.cv_start_date);
        this.mCvEndDate = (CardView) findViewById(R.id.cv_end_date);
        this.textMonthly = (TextView) findViewById(R.id.textMonthly);
        this.textQuarterly = (TextView) findViewById(R.id.textQuarterly);
        this.textHalfYearly = (TextView) findViewById(R.id.textHalfYearly);
        this.textYearly = (TextView) findViewById(R.id.textYearly);
        this.vMonthly = findViewById(R.id.vMonthly);
        this.vQuarterly = findViewById(R.id.vQuarterly);
        this.vHalfYearly = findViewById(R.id.vHalfYearly);
        this.vYearly = findViewById(R.id.vYearly);
        String str = Constant.TAB_REPORT;
        Constant.CURRENT_TAB = str;
        Constant.CURRENT_PAGE = str;
        this.rbMonthly = (RadioButton) findViewById(R.id.monthly);
        this.rbQuarter = (RadioButton) findViewById(R.id.quarterly);
        this.rbHalf = (RadioButton) findViewById(R.id.half_yearly);
        this.rbFull = (RadioButton) findViewById(R.id.yearly);
        this.calendar = Calendar.getInstance();
        this.segmented2 = (SegmentedGroup) findViewById(R.id.segmentGroup);
        this.txt_income = (TextView) findViewById(R.id.income);
        this.txt_expense = (TextView) findViewById(R.id.expense);
        this.txt_balance = (TextView) findViewById(R.id.balance);
        this.segmented2.setTintColor(getResources().getColor(R.color.colorPrimaryDark));
        this.segmented2.setOnCheckedChangeListener(this);
        this.mTotalIncome = 0;
        this.mTotalExpense = 0;
        this.mTotalBalance = 0;
        this.dateFormat = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.US);
        this.pieChartView = (PieChartView) findViewById(R.id.piechart);
        this.show_report = (Button) findViewById(R.id.button_show_report);
        Button button = (Button) findViewById(R.id.button_summary_report);
        this.summary_report = button;
        button.setTextColor(getResources().getColor(android.R.color.black));
        this.text_from_date = (TextView) findViewById(R.id.text_from_date);
        this.text_to_date = (TextView) findViewById(R.id.text_to_date);
        this.text_from_date.setText("");
        this.text_to_date.setText("");
        this.monthly_view = (TableRow) findViewById(R.id.monthly_view);
        this.yearly_view = (LinearLayout) findViewById(R.id.yearly_view);
        stubChart();
        this.show_report.setOnClickListener(this);
        this.summary_report.setOnClickListener(this);
        this.mCvStartDate.setOnClickListener(this);
        this.mCvEndDate.setOnClickListener(this);
        this.textMonthly.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ReportActivity.this.rbMonthly.setChecked(true);
                ReportActivity.this.ResetTextcolor();
                ReportActivity.this.textMonthly.setTextColor(ContextCompat.getColor(ReportActivity.this, R.color.colorReportTab));
                ReportActivity.this.vMonthly.setBackgroundColor(ContextCompat.getColor(ReportActivity.this, R.color.colorReportTab));
                if (Constant.CCount == 25) {
                    Constant.ACount = 0;
                    Constant.BCount = 0;
                    Constant.CCount = 0;
                } else if (Constant.BCount == 20) {
                    Constant.ACount = 0;
                    Constant.BCount = 0;
                } else if (Constant.ACount == 10) {
                    Constant.ACount = 0;
                } else {
                    Constant.ACount++;
                    Constant.BCount++;
                    Constant.CCount++;
                }
            }
        });
        this.textQuarterly.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ReportActivity.this.rbQuarter.setChecked(true);
                ReportActivity.this.ResetTextcolor();
                ReportActivity.this.textQuarterly.setTextColor(ContextCompat.getColor(ReportActivity.this, R.color.colorReportTab));
                ReportActivity.this.vQuarterly.setBackgroundColor(ContextCompat.getColor(ReportActivity.this, R.color.colorReportTab));
                if (Constant.CCount == 25) {
                    Constant.ACount = 0;
                    Constant.BCount = 0;
                    Constant.CCount = 0;
                } else if (Constant.BCount == 20) {
                    Constant.ACount = 0;
                    Constant.BCount = 0;
                } else if (Constant.ACount == 10) {
                    Constant.ACount = 0;
                } else {
                    Constant.ACount++;
                    Constant.BCount++;
                    Constant.CCount++;
                }
            }
        });
        this.textHalfYearly.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ReportActivity.this.rbHalf.setChecked(true);
                ReportActivity.this.ResetTextcolor();
                ReportActivity.this.textHalfYearly.setTextColor(ContextCompat.getColor(ReportActivity.this, R.color.colorReportTab));
                ReportActivity.this.vHalfYearly.setBackgroundColor(ContextCompat.getColor(ReportActivity.this, R.color.colorReportTab));
                if (Constant.CCount == 25) {
                    Constant.ACount = 0;
                    Constant.BCount = 0;
                    Constant.CCount = 0;
                } else if (Constant.BCount == 20) {
                    Constant.ACount = 0;
                    Constant.BCount = 0;
                } else if (Constant.ACount == 10) {
                    Constant.ACount = 0;
                } else {
                    Constant.ACount++;
                    Constant.BCount++;
                    Constant.CCount++;
                }
            }
        });
        this.textYearly.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ReportActivity.this.rbFull.setChecked(true);
                ReportActivity.this.ResetTextcolor();
                ReportActivity.this.textYearly.setTextColor(ContextCompat.getColor(ReportActivity.this, R.color.colorReportTab));
                ReportActivity.this.vYearly.setBackgroundColor(ContextCompat.getColor(ReportActivity.this, R.color.colorReportTab));
                if (Constant.CCount == 25) {
                    Constant.ACount = 0;
                    Constant.BCount = 0;
                    Constant.CCount = 0;
                } else if (Constant.BCount == 20) {
                    Constant.ACount = 0;
                    Constant.BCount = 0;
                } else if (Constant.ACount == 10) {
                    Constant.ACount = 0;
                } else {
                    Constant.ACount++;
                    Constant.BCount++;
                    Constant.CCount++;
                }
            }
        });
        if (this.rbMonthly.isChecked()) {
            this.text_to_date.setHint("Select Month");
            this.type = "monthly";
            this.mCvStartDate.setVisibility(View.GONE);
            this.yearly_view.setVisibility(View.GONE);
            this.monthly_view.setVisibility(View.GONE);
        } else if (this.rbQuarter.isChecked()) {
            this.text_from_date.setHint("Select Quarter");
            this.text_to_date.setHint("Select Year");
            this.type = "quarterly";
            this.mCvStartDate.setVisibility(View.VISIBLE);
            this.yearly_view.setVisibility(View.GONE);
            this.monthly_view.setVisibility(View.GONE);
        } else if (this.rbHalf.isChecked()) {
            this.text_from_date.setHint("Select Months");
            this.text_to_date.setHint("Select Year");
            this.type = "half_yearly";
            this.mCvStartDate.setVisibility(View.VISIBLE);
            this.yearly_view.setVisibility(View.GONE);
            this.monthly_view.setVisibility(View.GONE);
        } else {
            this.text_to_date.setHint("Select Year");
            this.type = "yearly";
            this.mCvStartDate.setVisibility(View.VISIBLE);
            this.yearly_view.setVisibility(View.GONE);
            this.monthly_view.setVisibility(View.GONE);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        getMenuInflater().inflate(R.menu.reports, menu);
        menu.findItem(R.id.item_report_download).setVisible(false);
        return true;
    }


    public boolean onOptionsItemSelected(android.view.MenuItem r7) {

        ArrayList<MasterExpenseModel> r8 = mTransactionList;

        int r70;
        String term_section;

        if (r8.size() <= 0){
            Toast.makeText(this, "No transaction found for selected criteria.", Toast.LENGTH_SHORT).show();

        }




        switch (r7.getItemId()) {
            case R.id.item_report_download:



                char c;
                String str;
                char c2;

                this.mTotalIncome = 0;
                this.mTotalExpense = 0;
                this.mTotalBalance = 0;
                displayAmount();
                ArrayList<String> arrayList = new ArrayList<>();
                String str2 = this.type;
                str2.hashCode();
                switch (str2.hashCode()) {
                    case -1066027719:
                        if (str2.equals("quarterly")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case -734561654:
                        if (str2.equals("yearly")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1236635661:
                        if (str2.equals("monthly")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1929029654:
                        if (str2.equals("half_yearly")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                        String str3 = this.qurter_count;
                        str3.hashCode();
                        switch (str3.hashCode()) {
                            case -906279820:
                                str = "Jun";
                                if (str3.equals("second")) {
                                    c2 = 0;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 97440432:
                                str = "Jun";
                                if (str3.equals("first")) {
                                    c2 = 1;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            case 110331239:
                                str = "Jun";
                                if (str3.equals("third")) {
                                    c2 = 2;
                                    break;
                                }
                                c2 = 65535;
                                break;
                            default:
                                str = "Jun";
                                c2 = 65535;
                                break;
                        }
                        switch (c2) {
                            case 0:

                                term_section = "April - June ".concat(" ").concat(selected_quarter_year).concat(qurter_count);

                                break;
                            case 1:

                                term_section = "January - March ".concat(" ").concat(selected_quarter_year).concat(qurter_count);

                                break;
                            case 2:

                                term_section = "July - September ".concat(" ").concat(selected_quarter_year).concat(qurter_count);

                                break;
                            default:

                                term_section = "October - December ".concat(" ").concat(selected_quarter_year).concat(qurter_count);

                                break;
                        }
                        new CommonMethod().downloadSelectedMonthDataIntoPDF(this,term_section,mTransactionList);
                        break;
                    case 1:



                        term_section   = selected_quarter_year.concat(" ( Yearly )");




                        new CommonMethod().downloadSelectedMonthDataIntoPDF(this,term_section,mTransactionList);
                        break;
                    case 2:



                        term_section   = selected_quarter_year.concat(" ( Monthly )");

                        new CommonMethod().downloadSelectedMonthDataIntoPDF(this,term_section,mTransactionList);
                        break;
                    case 3:
                        if (this.qurter_count.equals("first_half")) {


                            term_section = "January - June ".concat(" ").concat(selected_quarter_year).concat("first_half");



                        } else {



                            term_section = "July - December ".concat(" ").concat(selected_quarter_year).concat(" ( Half Yearly )");

                        }
                        new CommonMethod().downloadSelectedMonthDataIntoPDF(this,term_section,mTransactionList);
                        break;
                    default:
                        break;
                }


                break;
            default:
                break;
        }

        return true;

        /*
            r6 = this;
            int r7 = r7.getItemId()
            r0 = 1
            r1 = 2131362169(0x7f0a0179, float:1.834411E38)
            if (r7 != r1) goto L_0x012b
            java.util.ArrayList<com.moneyverse.money.manager.income.expense.tracker.Model.MasterExpenseModel> r7 = r6.mTransactionList
            r1 = 0
            if (r7 == 0) goto L_0x0122
            int r7 = r7.size()
            if (r7 <= 0) goto L_0x0122
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            java.lang.String r7 = r6.type
            r7.hashCode()
            int r2 = r7.hashCode()
            r3 = 2
            r4 = -1
            switch(r2) {
                case -1066027719: goto L_0x004b;
                case -734561654: goto L_0x0040;
                case 1236635661: goto L_0x0035;
                case 1929029654: goto L_0x002a;
                default: goto L_0x0028;
            }
        L_0x0028:
            r7 = -1
            goto L_0x0055
        L_0x002a:
            java.lang.String r2 = "half_yearly"
            boolean r7 = r7.equals(r2)
            if (r7 != 0) goto L_0x0033
            goto L_0x0028
        L_0x0033:
            r7 = 3
            goto L_0x0055
        L_0x0035:
            java.lang.String r2 = "monthly"
            boolean r7 = r7.equals(r2)
            if (r7 != 0) goto L_0x003e
            goto L_0x0028
        L_0x003e:
            r7 = 2
            goto L_0x0055
        L_0x0040:
            java.lang.String r2 = "yearly"
            boolean r7 = r7.equals(r2)
            if (r7 != 0) goto L_0x0049
            goto L_0x0028
        L_0x0049:
            r7 = 1
            goto L_0x0055
        L_0x004b:
            java.lang.String r2 = "quarterly"
            boolean r7 = r7.equals(r2)
            if (r7 != 0) goto L_0x0054
            goto L_0x0028
        L_0x0054:
            r7 = 0
        L_0x0055:
            java.lang.String r2 = " "
            switch(r7) {
                case 0: goto L_0x00a2;
                case 1: goto L_0x0098;
                case 2: goto L_0x008e;
                case 3: goto L_0x005e;
                default: goto L_0x005a;
            }
        L_0x005a:
            java.lang.String r7 = ""
            goto L_0x0117
        L_0x005e:
            java.lang.String r7 = r6.qurter_count
            java.lang.String r1 = "first_half"
            boolean r7 = r7.equals(r1)
            java.lang.String r1 = " ( Half Yearly )"
            if (r7 == 0) goto L_0x007c
            java.lang.String r7 = "January - June "
            java.lang.String r7 = r7.concat(r2)
            java.lang.String r2 = r6.selected_quarter_year
            java.lang.String r7 = r7.concat(r2)
            java.lang.String r7 = r7.concat(r1)
            goto L_0x0117
        L_0x007c:
            java.lang.String r7 = "July - December "
            java.lang.String r7 = r7.concat(r2)
            java.lang.String r2 = r6.selected_quarter_year
            java.lang.String r7 = r7.concat(r2)
            java.lang.String r7 = r7.concat(r1)
            goto L_0x0117
        L_0x008e:
            java.lang.String r7 = r6.selected_quarter_year
            java.lang.String r1 = " ( Monthly )"
            java.lang.String r7 = r7.concat(r1)
            goto L_0x0117
        L_0x0098:
            java.lang.String r7 = r6.selected_quarter_year
            java.lang.String r1 = " ( Yearly )"
            java.lang.String r7 = r7.concat(r1)
            goto L_0x0117
        L_0x00a2:
            java.lang.String r7 = r6.qurter_count
            r7.hashCode()
            int r5 = r7.hashCode()
            switch(r5) {
                case -906279820: goto L_0x00c6;
                case 97440432: goto L_0x00bb;
                case 110331239: goto L_0x00b0;
                default: goto L_0x00ae;
            }
        L_0x00ae:
            r1 = -1
            goto L_0x00cf
        L_0x00b0:
            java.lang.String r1 = "third"
            boolean r7 = r7.equals(r1)
            if (r7 != 0) goto L_0x00b9
            goto L_0x00ae
        L_0x00b9:
            r1 = 2
            goto L_0x00cf
        L_0x00bb:
            java.lang.String r1 = "first"
            boolean r7 = r7.equals(r1)
            if (r7 != 0) goto L_0x00c4
            goto L_0x00ae
        L_0x00c4:
            r1 = 1
            goto L_0x00cf
        L_0x00c6:
            java.lang.String r3 = "second"
            boolean r7 = r7.equals(r3)
            if (r7 != 0) goto L_0x00cf
            goto L_0x00ae
        L_0x00cf:
            java.lang.String r7 = " ( Quarterly )"
            switch(r1) {
                case 0: goto L_0x0107;
                case 1: goto L_0x00f6;
                case 2: goto L_0x00e5;
                default: goto L_0x00d4;
            }
        L_0x00d4:
            java.lang.String r1 = "October - December "
            java.lang.String r1 = r1.concat(r2)
            java.lang.String r2 = r6.selected_quarter_year
            java.lang.String r1 = r1.concat(r2)
            java.lang.String r7 = r1.concat(r7)
            goto L_0x0117
        L_0x00e5:
            java.lang.String r1 = "July - September "
            java.lang.String r1 = r1.concat(r2)
            java.lang.String r2 = r6.selected_quarter_year
            java.lang.String r1 = r1.concat(r2)
            java.lang.String r7 = r1.concat(r7)
            goto L_0x0117
        L_0x00f6:
            java.lang.String r1 = "January - March "
            java.lang.String r1 = r1.concat(r2)
            java.lang.String r2 = r6.selected_quarter_year
            java.lang.String r1 = r1.concat(r2)
            java.lang.String r7 = r1.concat(r7)
            goto L_0x0117
        L_0x0107:
            java.lang.String r1 = "April - June "
            java.lang.String r1 = r1.concat(r2)
            java.lang.String r2 = r6.selected_quarter_year
            java.lang.String r1 = r1.concat(r2)
            java.lang.String r7 = r1.concat(r7)
        L_0x0117:
            com.moneyverse.money.manager.income.expense.tracker.Utils.CommonMethod r1 = new com.moneyverse.money.manager.income.expense.tracker.Utils.CommonMethod
            r1.<init>()
            java.util.ArrayList<com.moneyverse.money.manager.income.expense.tracker.Model.MasterExpenseModel> r2 = r6.mTransactionList
            r1.downloadSelectedMonthDataIntoPDF(r6, r7, r2)
            goto L_0x012b
        L_0x0122:
            java.lang.String r7 = "No transaction found for selected criteria."
            android.widget.Toast r7 = android.widget.Toast.makeText(r6, r7, r1)
            r7.show()
        L_0x012b:
            return r0
        */
        //throw new UnsupportedOperationException("Method not decompiled: com.moneyverse.money.manager.income.expense.tracker.Activity.ReportActivity.onOptionsItemSelected(android.view.MenuItem):boolean");
    }

    public void ResetTextcolor() {
        this.textMonthly.setTextColor(Color.parseColor("#000000"));
        this.textQuarterly.setTextColor(Color.parseColor("#000000"));
        this.textHalfYearly.setTextColor(Color.parseColor("#000000"));
        this.textYearly.setTextColor(Color.parseColor("#000000"));
        this.vMonthly.setBackgroundColor(Color.parseColor("#00000000"));
        this.vQuarterly.setBackgroundColor(Color.parseColor("#00000000"));
        this.vHalfYearly.setBackgroundColor(Color.parseColor("#00000000"));
        this.vYearly.setBackgroundColor(Color.parseColor("#00000000"));
    }

    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        this.mTransactionList.clear();
        this.mTotalIncome = 0;
        this.mTotalExpense = 0;
        this.mTotalBalance = 0;
        this.txt_income.setText("");
        this.txt_expense.setText("");
        this.txt_balance.setText("");
        this.text_from_date.setText("");
        this.text_to_date.setText("");
        Menu menu = this.mMenu;
        if (menu != null) {
            menu.findItem(R.id.item_report_download).setVisible(false);
        }
        stubChart();
        if (i == R.id.monthly) {
            this.mCvStartDate.setVisibility(View.GONE);
            this.yearly_view.setVisibility(View.GONE);
            this.monthly_view.setVisibility(View.GONE);
            this.type = "monthly";
            this.text_to_date.setHint("Select Month");
        } else if (i == R.id.half_yearly) {
            this.text_from_date.setHint("Select Months");
            this.text_to_date.setHint("Select Year");
            this.mCvStartDate.setVisibility(View.VISIBLE);
            this.yearly_view.setVisibility(View.GONE);
            this.monthly_view.setVisibility(View.GONE);
            this.type = "half_yearly";
        } else if (i == R.id.quarterly) {
            this.type = "quarterly";
            this.text_from_date.setHint("Select Quarter");
            this.text_to_date.setHint("Select Year");
            this.mCvStartDate.setVisibility(View.VISIBLE);
            this.yearly_view.setVisibility(View.GONE);
            this.monthly_view.setVisibility(View.GONE);
        } else if (i == R.id.yearly) {
            this.mCvStartDate.setVisibility(View.GONE);
            this.yearly_view.setVisibility(View.GONE);
            this.monthly_view.setVisibility(View.GONE);
            this.type = "yearly";
            this.text_to_date.setHint("Select Year");
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_show_report /*{ENCODED_INT: 2131361934}*/:
                try {
                    if (this.type.equals("monthly")) {
                        if (this.text_to_date.getText().toString().equals("")) {
                            CommonMethod.showAlertWithOk(this, "Alert", "Please select Month, Quarter and Year.", getString(R.string.action_ok));
                        } else {
                            getTransactionData();
                        }
                    } else if (this.type.equals("monthly")) {
                        if (this.text_from_date.getText().toString().equals("") && this.text_to_date.getText().toString().equals("")) {
                            CommonMethod.showAlertWithOk(this, "Alert", "Please select Start Date.", getString(R.string.action_ok));
                        } else if (!this.text_from_date.getText().toString().equals("") && this.text_to_date.getText().toString().equals("")) {
                            CommonMethod.showAlertWithOk(this, "Alert", "Please select End Date.", getString(R.string.action_ok));
                        } else if (!this.text_from_date.getText().toString().equals("") || this.text_to_date.getText().toString().equals("")) {
                            try {
                                if (!this.dateFormat.parse(this.date_2).after(this.dateFormat.parse(this.date_1)) && !this.dateFormat.parse(this.date_2).equals(this.dateFormat.parse(this.date_1))) {
                                    CommonMethod.showAlertWithOk(this, "Alert", "Invalid Date.", getString(R.string.action_ok));
                                    this.txt_income.setText("");
                                    this.txt_expense.setText("");
                                    this.txt_balance.setText("");
                                    stubChart();
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            getTransactionData();
                        } else {
                            CommonMethod.showAlertWithOk(this, "Alert", "Please select Start Date.", getString(R.string.action_ok));
                        }
                    } else if (this.type.equals("quarterly")) {
                        if (this.text_from_date.getText().toString().equals("") && this.text_to_date.getText().toString().equals("")) {
                            CommonMethod.showAlertWithOk(this, "Alert", "Please select Quarter and Year.", getString(R.string.action_ok));
                        } else if (!this.text_from_date.getText().toString().equals("") && this.text_to_date.getText().toString().equals("")) {
                            CommonMethod.showAlertWithOk(this, "Alert", "Please select Year.", getString(R.string.action_ok));
                        } else if (!this.text_from_date.getText().toString().equals("") || this.text_to_date.getText().toString().equals("")) {
                            getTransactionData();
                        } else {
                            CommonMethod.showAlertWithOk(this, "Alert", "Please select Quarter.", getString(R.string.action_ok));
                        }
                    } else if (this.type.equals("half_yearly")) {
                        if (this.text_from_date.getText().toString().equals("") && this.text_to_date.getText().toString().equals("")) {
                            CommonMethod.showAlertWithOk(this, "Alert", "Please select Months and Year.", getString(R.string.action_ok));
                        } else if (!this.text_from_date.getText().toString().equals("") && this.text_to_date.getText().toString().equals("")) {
                            CommonMethod.showAlertWithOk(this, "Alert", "Please select Year.", getString(R.string.action_ok));
                        } else if (!this.text_from_date.getText().toString().equals("") || this.text_to_date.getText().toString().equals("")) {
                            getTransactionData();
                        } else {
                            CommonMethod.showAlertWithOk(this, "Alert", "Please select Months.", getString(R.string.action_ok));
                        }
                    } else if (this.text_to_date.getText().toString().equals("")) {
                        CommonMethod.showAlertWithOk(this, "Alert", "Please select Quarter and Year.", getString(R.string.action_ok));
                    } else {
                        getTransactionData();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                if (Constant.CCount == 25) {
                    Constant.ACount = 0;
                    Constant.BCount = 0;
                    Constant.CCount = 0;
                    return;
                } else if (Constant.BCount == 20) {
                    Constant.ACount = 0;
                    Constant.BCount = 0;
                    return;
                } else if (Constant.ACount == 10) {
                    Constant.ACount = 0;
                    return;
                } else {
                    Constant.ACount++;
                    Constant.BCount++;
                    Constant.CCount++;
                    return;
                }
            case R.id.button_summary_report /*{ENCODED_INT: 2131361936}*/:
                this.summary_report.setTextColor(getResources().getColor(R.color.black));
                if (this.mTotalIncome == 0 && this.mTotalExpense == 0 && this.mTotalBalance == 0) {
                    CommonMethod.showAlertWithOk(this, "Alert", "No data found.\nSelect Month Or Year.\nThen Show Report\nThen Click View in Detail", getString(R.string.action_ok));
                } else {
                    Bundle bundle2 = new Bundle();
                    bundle2.putSerializable("TransactionList", this.mTransactionList);
                    bundle2.putLong("Income", this.mTotalIncome);
                    bundle2.putLong("Expense", this.mTotalExpense);
                    bundle2.putLong("Balance", this.mTotalBalance);
                    loadFragment(new ReportsInDetailsFragment(), bundle2);
                }
                if (Constant.CCount == 25) {
                    Constant.ACount = 0;
                    Constant.BCount = 0;
                    Constant.CCount = 0;
                    return;
                } else if (Constant.BCount == 20) {
                    Constant.ACount = 0;
                    Constant.BCount = 0;
                    return;
                } else if (Constant.ACount == 10) {
                    Constant.ACount = 0;
                    return;
                } else {
                    Constant.ACount++;
                    Constant.BCount++;
                    Constant.CCount++;
                    return;
                }
            case R.id.cv_end_date /*{ENCODED_INT: 2131361999}*/:
                String trim = this.text_to_date.getText().toString().trim();
                Calendar instance = Calendar.getInstance();
                int i = instance.get(2);
                int i2 = instance.get(1);
                if (trim.length() > 0) {
                    if (this.type.equals("monthly")) {
                        Date date = null;
                        try {
                            date = this.monthYearParser.parse(trim);
                        } catch (ParseException e3) {
                            e3.printStackTrace();
                        }
                        if (date != null) {
                            i2 = Integer.valueOf(this.yearParser.format(date)).intValue();
                            i = Integer.valueOf(this.monthParser.format(date)).intValue() - 1;
                        }
                    } else {
                        i2 = Integer.valueOf(trim).intValue();
                    }
                }
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(this, new MonthPickerDialog.OnDateSetListener() {


                    @Override // com.whiteelephant.monthpicker.MonthPickerDialog.OnDateSetListener
                    public void onDateSet(int i, int i2) {
                        if (ReportActivity.this.type.equals("monthly")) {
                            Date date = null;
                            try {
                                date = new SimpleDateFormat("MM-yyyy", Locale.US).parse(String.valueOf(i + 1).concat("-").concat(String.valueOf(i2)));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            ReportActivity reportActivity = ReportActivity.this;
                            reportActivity.selected_quarter_year = reportActivity.monthYearParser.format(date);
                            ReportActivity.this.text_to_date.setText(ReportActivity.this.selected_quarter_year);
                            return;
                        }
                        ReportActivity.this.selected_quarter_year = String.valueOf(i2);
                        ReportActivity.this.text_to_date.setText(ReportActivity.this.selected_quarter_year);
                    }
                }, instance.get(1), instance.get(2));
                builder.setActivatedMonth(i).setMinYear(2000).setActivatedYear(i2).setMaxYear(instance.get(1)).setMinMonth(0).setTitle(this.type.equals("monthly") ? "Select Month" : "Select Year").setMonthRange(0, 11).setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {


                    @Override
                    // com.whiteelephant.monthpicker.MonthPickerDialog.OnMonthChangedListener
                    public void onMonthChanged(int i) {
                    }
                }).setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {


                    @Override

                    public void onYearChanged(int i) {
                    }
                });
                if (!this.type.equals("monthly")) {
                    builder.showYearOnly();
                }
                builder.build().show();
                if (Constant.CCount == 25) {
                    Constant.ACount = 0;
                    Constant.BCount = 0;
                    Constant.CCount = 0;
                    return;
                } else if (Constant.BCount == 20) {
                    Constant.ACount = 0;
                    Constant.BCount = 0;
                    return;
                } else if (Constant.ACount == 10) {
                    Constant.ACount = 0;
                    return;
                } else {
                    Constant.ACount++;
                    Constant.BCount++;
                    Constant.CCount++;
                    return;
                }
            case R.id.cv_start_date /*{ENCODED_INT: 2131362000}*/:
                if (this.type.equals("quarterly")) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                    builder2.setTitle("Make your selection");
                    builder2.setItems(Constant.ITEM_QUARTERS, new DialogInterface.OnClickListener() {


                        public void onClick(DialogInterface dialogInterface, int i) {
                            ReportActivity.this.text_from_date.setText(Constant.ITEM_QUARTERS[i]);
                            if (i == 0) {
                                ReportActivity.this.qurter_count = "first";
                            } else if (i == 1) {
                                ReportActivity.this.qurter_count = "second";
                            } else if (i == 2) {
                                ReportActivity.this.qurter_count = "third";
                            } else if (i == 3) {
                                ReportActivity.this.qurter_count = "fourth";
                            }
                        }
                    }).setCancelable(true);
                    builder2.create().show();
                } else if (this.type.equals("half_yearly")) {
                    AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                    builder3.setTitle("Make your selection");
                    builder3.setItems(Constant.ITEM_HALF_YEAR, new DialogInterface.OnClickListener() {


                        public void onClick(DialogInterface dialogInterface, int i) {
                            ReportActivity.this.text_from_date.setText(Constant.ITEM_HALF_YEAR[i]);
                            if (i == 0) {
                                ReportActivity.this.qurter_count = "first_half";
                            } else if (i == 1) {
                                ReportActivity.this.qurter_count = "second_half";
                            }
                        }
                    }).setCancelable(true);
                    builder3.create().show();
                }
                if (Constant.CCount == 25) {
                    Constant.ACount = 0;
                    Constant.BCount = 0;
                    Constant.CCount = 0;
                    return;
                } else if (Constant.BCount == 20) {
                    Constant.ACount = 0;
                    Constant.BCount = 0;
                    return;
                } else if (Constant.ACount == 10) {
                    Constant.ACount = 0;
                    return;
                } else {
                    Constant.ACount++;
                    Constant.BCount++;
                    Constant.CCount++;
                    return;
                }
            default:
                return;
        }
    }


    public void createPieChart(long j, long j2, long j3) {
        ArrayList arrayList = new ArrayList();
        SliceValue sliceValue = new SliceValue();
        sliceValue.setValue((float) j);
        sliceValue.setColor(getResources().getColor(R.color.myGreen));
        sliceValue.setLabel(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" ").concat(CommonMethod.formatPrice((double) j)));
        arrayList.add(new SliceValue(sliceValue));
        SliceValue sliceValue2 = new SliceValue();
        sliceValue2.setValue((float) j2);
        sliceValue2.setColor(getResources().getColor(R.color.myRed));
        sliceValue2.setLabel(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" ").concat(CommonMethod.formatPrice((double) j2)));
        arrayList.add(new SliceValue(sliceValue2));
        SliceValue sliceValue3 = new SliceValue();
        sliceValue3.setValue((float) j3);
        sliceValue3.setColor(getResources().getColor(R.color.pink));
        sliceValue3.setLabel(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" ").concat(CommonMethod.formatPrice((double) j3)));
        arrayList.add(new SliceValue(sliceValue3));
        PieChartData pieChartData2 = new PieChartData((List<SliceValue>) arrayList);
        this.pieChartData = pieChartData2;
        pieChartData2.setHasLabelsOnlyForSelected(false);
        this.pieChartData.setHasLabelsOutside(false);
        this.pieChartData.setValueLabelTextSize(14);
        this.pieChartView.setValueSelectionEnabled(true);
    }

    public void stubChart() {
        createPieChart(1, 1, 1);
        this.pieChartData.setHasLabels(false);
        this.pieChartView.setPieChartData(this.pieChartData);
    }

    private void getTransactionData() {
        char c;
        String str;
        char c2;
        this.mTransactionList.clear();
        this.mTotalIncome = 0;
        this.mTotalExpense = 0;
        this.mTotalBalance = 0;
        displayAmount();
        ArrayList<String> arrayList = new ArrayList<>();
        String str2 = this.type;
        str2.hashCode();
        switch (str2.hashCode()) {
            case -1066027719:
                if (str2.equals("quarterly")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -734561654:
                if (str2.equals("yearly")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1236635661:
                if (str2.equals("monthly")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1929029654:
                if (str2.equals("half_yearly")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                String str3 = this.qurter_count;
                str3.hashCode();
                switch (str3.hashCode()) {
                    case -906279820:
                        str = "Jun";
                        if (str3.equals("second")) {
                            c2 = 0;
                            break;
                        }
                        c2 = 65535;
                        break;
                    case 97440432:
                        str = "Jun";
                        if (str3.equals("first")) {
                            c2 = 1;
                            break;
                        }
                        c2 = 65535;
                        break;
                    case 110331239:
                        str = "Jun";
                        if (str3.equals("third")) {
                            c2 = 2;
                            break;
                        }
                        c2 = 65535;
                        break;
                    default:
                        str = "Jun";
                        c2 = 65535;
                        break;
                }
                switch (c2) {
                    case 0:
                        arrayList.add("Apr".concat(" ").concat(this.selected_quarter_year));
                        arrayList.add("May".concat(" ").concat(this.selected_quarter_year));
                        arrayList.add(str.concat(" ").concat(this.selected_quarter_year));
                        break;
                    case 1:
                        arrayList.add("Jan".concat(" ").concat(this.selected_quarter_year));
                        arrayList.add("Feb".concat(" ").concat(this.selected_quarter_year));
                        arrayList.add("Mar".concat(" ").concat(this.selected_quarter_year));
                        break;
                    case 2:
                        arrayList.add("Jul".concat(" ").concat(this.selected_quarter_year));
                        arrayList.add("Aug".concat(" ").concat(this.selected_quarter_year));
                        arrayList.add("Sep".concat(" ").concat(this.selected_quarter_year));
                        break;
                    default:
                        arrayList.add("Oct".concat(" ").concat(this.selected_quarter_year));
                        arrayList.add("Nov".concat(" ").concat(this.selected_quarter_year));
                        arrayList.add("Dec".concat(" ").concat(this.selected_quarter_year));
                        break;
                }
                callFirebaseAPI(arrayList);
                return;
            case 1:
                arrayList.add("Jan".concat(" ").concat(this.selected_quarter_year));
                arrayList.add("Feb".concat(" ").concat(this.selected_quarter_year));
                arrayList.add("Mar".concat(" ").concat(this.selected_quarter_year));
                arrayList.add("Apr".concat(" ").concat(this.selected_quarter_year));
                arrayList.add("May".concat(" ").concat(this.selected_quarter_year));
                arrayList.add("Jun".concat(" ").concat(this.selected_quarter_year));
                arrayList.add("Jul".concat(" ").concat(this.selected_quarter_year));
                arrayList.add("Aug".concat(" ").concat(this.selected_quarter_year));
                arrayList.add("Sep".concat(" ").concat(this.selected_quarter_year));
                arrayList.add("Oct".concat(" ").concat(this.selected_quarter_year));
                arrayList.add("Nov".concat(" ").concat(this.selected_quarter_year));
                arrayList.add("Dec".concat(" ").concat(this.selected_quarter_year));
                callFirebaseAPI(arrayList);
                return;
            case 2:
                arrayList.add(this.text_to_date.getText().toString().trim());
                callFirebaseAPI(arrayList);
                return;
            case 3:
                if (this.qurter_count.equals("first_half")) {
                    arrayList.add("Jan".concat(" ").concat(this.selected_quarter_year));
                    arrayList.add("Feb".concat(" ").concat(this.selected_quarter_year));
                    arrayList.add("Mar".concat(" ").concat(this.selected_quarter_year));
                    arrayList.add("Apr".concat(" ").concat(this.selected_quarter_year));
                    arrayList.add("May".concat(" ").concat(this.selected_quarter_year));
                    arrayList.add("Jun".concat(" ").concat(this.selected_quarter_year));
                } else {
                    arrayList.add("Jul".concat(" ").concat(this.selected_quarter_year));
                    arrayList.add("Aug".concat(" ").concat(this.selected_quarter_year));
                    arrayList.add("Sep".concat(" ").concat(this.selected_quarter_year));
                    arrayList.add("Oct".concat(" ").concat(this.selected_quarter_year));
                    arrayList.add("Nov".concat(" ").concat(this.selected_quarter_year));
                    arrayList.add("Dec".concat(" ").concat(this.selected_quarter_year));
                }
                callFirebaseAPI(arrayList);
                return;
            default:
                return;
        }
    }


    private void callFirebaseAPI(ArrayList<String> arrayList) {
        if (CommonMethod.isNetworkConnected(this)) {
            CommonMethod.showProgressDialog(this);
            DatabaseReference child = FirebaseDatabase.getInstance().getReference().child(Constant.FIREBASE_NODE_EXPENSE);
            String uid = FirebaseAuth.getInstance().getUid();
            Objects.requireNonNull(uid);
            DatabaseReference child2 = child.child(uid);
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                child2.child(it.next()).addListenerForSingleValueEvent(new ValueEventListener() {


                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot next : dataSnapshot.getChildren()) {
                                MasterExpenseModel masterExpenseModel = new MasterExpenseModel();
                                masterExpenseModel.setId(next.getKey());
                                masterExpenseModel.setDate(next.getKey());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.US);
                                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(Constant.DAY_FORMAT, Locale.US);
                                Date date = null;
                                try {
                                    date = simpleDateFormat.parse(next.getKey());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (date != null) {
                                    masterExpenseModel.setDay(Integer.valueOf(simpleDateFormat2.format(date)).intValue());
                                }
                                ArrayList arrayList = new ArrayList();
                                boolean z = false;
                                long j = 0;
                                long j2 = 0;
                                for (DataSnapshot next2 : next.getChildren()) {
                                    Expense expense = (Expense) next2.getValue(Expense.class);
                                    expense.setId(next2.getKey());
                                    expense.setDate(masterExpenseModel.getDate());
                                    arrayList.add(expense);
                                    if (!expense.isExpense()) {
                                        j += expense.getAmount();
                                    } else if (expense.isExpense()) {
                                        j2 += expense.getAmount();
                                    }
                                    if (expense.getProofUri() != null && expense.getProofUri().trim().length() > 0) {
                                        z = true;
                                    }
                                }
                                masterExpenseModel.setHavingProofImage(z);
                                masterExpenseModel.setTotalIncome(j);
                                masterExpenseModel.setTotalExpense(j2);
                                masterExpenseModel.setExpenses(arrayList);
                                masterExpenseModel.setTransactionAdded(true);
                                ReportActivity.this.mTransactionList.add(masterExpenseModel);
                                ReportActivity.this.mTotalIncome += masterExpenseModel.getTotalIncome();
                                ReportActivity.this.mTotalExpense += masterExpenseModel.getTotalExpense();
                                ReportActivity reportActivity = ReportActivity.this;
                                reportActivity.mTotalBalance = reportActivity.mTotalIncome - ReportActivity.this.mTotalExpense;
                            }
                            CommonMethod.cancelProgressDialog();
                            ReportActivity.this.displayAmount();
                            return;
                        }
                        CommonMethod.cancelProgressDialog();
                    }

                    public void onCancelled(DatabaseError databaseError) {
                        CommonMethod.cancelProgressDialog();
                        Toast.makeText(ReportActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return;
        }
        CommonMethod.showConnectionAlert(this);
    }


    public void displayAmount() {
        long j = this.mTotalIncome;
        if (j == 0 && this.mTotalExpense == 0 && this.mTotalBalance == 0) {
            stubChart();
            this.txt_income.setText("");
            this.txt_expense.setText("");
            this.txt_balance.setText("");
        } else {
            createPieChart(j, this.mTotalExpense, this.mTotalBalance);
            this.pieChartData.setHasLabels(true);
            this.pieChartView.setPieChartData(this.pieChartData);
            this.txt_income.setText(String.valueOf(this.mTotalIncome));
            this.txt_expense.setText(String.valueOf(this.mTotalExpense));
            this.txt_balance.setText(String.valueOf(this.mTotalBalance));
        }
        if (this.mMenu != null) {
            ArrayList<MasterExpenseModel> arrayList = this.mTransactionList;
            if (arrayList == null || arrayList.isEmpty()) {
                this.mMenu.findItem(R.id.item_report_download).setVisible(false);
            } else {
                this.mMenu.findItem(R.id.item_report_download).setVisible(true);
            }
        }
    }

    private void loadFragment(Fragment fragment, Bundle bundle2) {
        if (fragment != null) {
            FrameLayout frameLayout = this.mContainer;
            if (frameLayout != null && frameLayout.getVisibility() == View.GONE) {
                this.mContainer.setVisibility(View.VISIBLE);
            }
            fragment.setArguments(bundle2);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }

    public void onBackPressed() {
        FrameLayout frameLayout = this.mContainer;
        if (frameLayout == null || frameLayout.getVisibility() != View.VISIBLE) {
            super.onBackPressed();
            return;
        }
        this.mContainer.setVisibility(View.GONE);
        this.mContainer.removeAllViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}
