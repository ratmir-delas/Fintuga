package com.tugasoft.fintuga.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tugasoft.fintuga.fragments.AddExpenseFragment;
import com.tugasoft.fintuga.fragments.CalendarFragment;
import com.tugasoft.fintuga.fragments.ChartFragment;
import com.tugasoft.fintuga.fragments.DayWiseExpenseDetailFragment;
import com.tugasoft.fintuga.fragments.HomeFragment;
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
import java.util.Locale;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public String TAG = DashboardActivity.class.getCanonicalName();
    public DatabaseReference expensesRef;
    public FirebaseAuth mAuth;
    public String mSelectedMonthYear;
    public LinearLayout mToggleSwitchChart;
    public Fragment mTopFragment;
    public ArrayList<MasterExpenseModel> mTransactionList = new ArrayList<>();
    public TextView mTvSelectDate;
    public DatabaseReference ref;
    public double totalExpense = 0;
    public double totalIncome = 0;
    public TextView tv_reports;
    public String uid;
    TextView toggle_a, toggle_b;
    ImageView homeNav, calendarNav, analyticsNav;
    SimpleDateFormat monthYearParser = new SimpleDateFormat(Constant.MONTH_YEAR_FORMAT, Locale.US);
    private FloatingActionButton mFabAddTransaction;
    private TextView mTvTotalExpense;
    private TextView mTvTotalIncome;
    private TextView mTvTotalSaving;
    private final SimpleDateFormat monthParser = new SimpleDateFormat("MM", Locale.US);
    private final SimpleDateFormat yearParser = new SimpleDateFormat(Constant.YEAR_FORMAT, Locale.US);

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_dashboard);
        toggle_a = findViewById(R.id.toggle_1);
        toggle_b = findViewById(R.id.toggle_b);
        SharedPreferences sharedPreferences = getSharedPreferences("mypref", 0);
        setToolbar();
        if (MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").trim().length() == 0) {
            showDialogToChooseCurrency();
        } else {
            init();
        }
        int intExtra = getIntent().getIntExtra("tab_value", 11);
        if (intExtra != 11) {
            Fragment fragment = null;
            if (intExtra == 1) {
                fragment = new HomeFragment();
            } else if (intExtra == 2) {
                fragment = new CalendarFragment();
            } else if (intExtra == 3) {
                fragment = new ChartFragment();
            } else if (intExtra == 4) {
                startActivity(new Intent(this, ReportActivity.class));
            }
            loadFragment(fragment);
        }

        homeNav.setOnClickListener(this);
        calendarNav.setOnClickListener(this);
        analyticsNav.setOnClickListener(this);
        toggle_a.setOnClickListener(this);
        toggle_b.setOnClickListener(this);

        ifAdSuccess();
    }

    private void ifAdSuccess() {
        openTransactionEntryDialog(null);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setContentInsetStartWithNavigation(getResources().getInteger(R.integer.dimen_spacing_between_toolbar_icon_and_title));
        setSupportActionBar(toolbar);
    }


    public void init() {
        this.ref = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth instance = FirebaseAuth.getInstance();
        this.mAuth = instance;
        this.uid = Objects.requireNonNull(instance.getCurrentUser()).getUid();
        homeNav = findViewById(R.id.homeNavigationIcon);
        calendarNav = findViewById(R.id.calendarNavigationIcon);
        analyticsNav = findViewById(R.id.analyticsNavigationIcon);
        RelativeLayout rlAdContainer = findViewById(R.id.rl_add_container);
        this.mFabAddTransaction = findViewById(R.id.fab_add_transaction);
        this.mToggleSwitchChart = findViewById(R.id.toggle_switch_chart);
        this.mTvSelectDate = findViewById(R.id.tv_select_date);
        this.tv_reports = findViewById(R.id.tv_reports);
        this.mTvSelectDate.setText(this.monthYearParser.format(new Date()));
        this.tv_reports.setText("Reports");
        this.mSelectedMonthYear = this.mTvSelectDate.getText().toString().trim();
        loadFragment(new HomeFragment());
        this.mTvTotalIncome = findViewById(R.id.tv_total_income);
        this.mTvTotalExpense = findViewById(R.id.tv_total_expense);
        this.mTvTotalSaving = findViewById(R.id.tv_total_saving);
        this.mTvSelectDate.setOnClickListener(this);
        this.tv_reports.setOnClickListener(this);
        this.mFabAddTransaction.setOnClickListener(this);
        getDetails();
    }

    public boolean switchToggleBackGround(boolean selectedLeft) {
        GradientDrawable toggle_A = (GradientDrawable) ((LayerDrawable) toggle_a.getBackground()).findDrawableByLayerId(R.id.myToggle);
        GradientDrawable toggle_B = (GradientDrawable) ((LayerDrawable) toggle_b.getBackground()).findDrawableByLayerId(R.id.myToggle);
        if (DashboardActivity.this.mTopFragment != null && (DashboardActivity.this.mTopFragment instanceof ChartFragment)) {
            if (selectedLeft) {
                ChartFragment.ie = 0;
                ((ChartFragment) DashboardActivity.this.mTopFragment).getData(Constant.TYPE_EXPENSE);
                toggle_A.setColor(Color.parseColor("#FC495B"));
                toggle_B.setColor(Color.parseColor("#F2CFFF"));
                toggle_a.setTextColor(Color.parseColor("#FFFFFF"));
                toggle_b.setTextColor(Color.parseColor("#000000"));
                selectedLeft = true;
            } else {
                ChartFragment.ie = 1;
                ((ChartFragment) DashboardActivity.this.mTopFragment).getData(Constant.TYPE_INCOME);
                toggle_B.setColor(Color.parseColor("#09B838"));
                toggle_A.setColor(Color.parseColor("#F2CFFF"));
                toggle_b.setTextColor(Color.parseColor("#FFFFFF"));
                toggle_a.setTextColor(Color.parseColor("#000000"));
                selectedLeft = false;
            }
        }

        return selectedLeft;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!CommonMethod.isAutoDateTimeEnabled(this)) {
            CommonMethod.showAlertForChangeDate(this);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.item_download) {
            ArrayList<MasterExpenseModel> arrayList = this.mTransactionList;
            if (arrayList == null || arrayList.size() <= 0) {
                Toast.makeText(this, "No transaction found for selected month.", Toast.LENGTH_SHORT).show();
                return true;
            }
            new CommonMethod().downloadSelectedMonthDataIntoPDF(this, this.mTvSelectDate.getText().toString().trim().concat(" ( Monthly )"), this.mTransactionList);
            return true;
        } else if (menuItem.getItemId() != R.id.item_more) {
            return true;
        } else {
            startActivity(new Intent(this, MoreActivity.class));
            return true;
        }
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.homeNavigationIcon:
                myClicks(1);
                break;
            case R.id.calendarNavigationIcon:
                myClicks(2);
                break;
            case R.id.analyticsNavigationIcon:
                myClicks(3);
                break;
            case R.id.toggle_1:
                switchToggleBackGround(true);
                break;
            case R.id.toggle_b:
                switchToggleBackGround(false);
                break;
        }


        Date date = null;
        if (view.getId() == R.id.fab_add_transaction) {

            openTransactionEntryDialog((Expense) null);

        } else if (view.getId() == R.id.tv_select_date) {
            try {
                date = this.monthYearParser.parse(this.mTvSelectDate.getText().toString().trim());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar instance = Calendar.getInstance();
            int i2 = instance.get(2);
            int i3 = instance.get(1);
            if (date != null) {
                i2 = Integer.parseInt(this.monthParser.format(date)) - 1;
                i3 = Integer.parseInt(this.yearParser.format(date));
            }
            new MonthPickerDialog.Builder(this, (i, i21) -> {
                Date date1;
                try {
                    date1 = new SimpleDateFormat("MM-yyyy", Locale.US).parse(String.valueOf(i + 1).concat("-").concat(String.valueOf(i21)));
                } catch (ParseException e) {
                    e.printStackTrace();
                    date1 = null;
                }
                String format = DashboardActivity.this.monthYearParser.format(date1);
                DashboardActivity.this.mTvSelectDate.setText(format);
                if (DashboardActivity.this.mTopFragment == null) {
                    return;
                }
                if ((DashboardActivity.this.mTopFragment instanceof HomeFragment) || (DashboardActivity.this.mTopFragment instanceof ChartFragment)) {
                    DashboardActivity.this.mSelectedMonthYear = format;
                    DashboardActivity.this.getDetails();
                } else if (DashboardActivity.this.mTopFragment instanceof CalendarFragment) {
                    ((CalendarFragment) DashboardActivity.this.mTopFragment).changeCalendarMonthWise(format, DashboardActivity.this.mSelectedMonthYear);
                    DashboardActivity.this.mSelectedMonthYear = format;
                }
            }, instance.get(1), instance.get(2)).setActivatedMonth(i2).setMinYear(2000).setActivatedYear(i3).setMaxYear(instance.get(1)).setMinMonth(0).setTitle("Select month").setMonthRange(0, 11).setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                public void onMonthChanged(int i) {
                }
            }).setOnYearChangedListener(i -> {
            }).build().show();
        } else if (view.getId() == R.id.tv_reports) {
            startActivity(new Intent(this, ReportActivity.class));
        }
    }

    public void insertTransactionEntry() {
        getDetails();
        dismissDialog();
    }

    public void getDetails() {
        resetAmountData();
        this.mTransactionList.clear();
        Fragment fragment = this.mTopFragment;
        if (fragment != null) {
            if (fragment instanceof HomeFragment) {
                ((HomeFragment) fragment).resetData();
            } else if (fragment instanceof CalendarFragment) {
                ((CalendarFragment) fragment).resetData();
            } else if (fragment instanceof ChartFragment) {
                ((ChartFragment) fragment).resetData();
            }
        }
        DatabaseReference child = this.ref.child(Constant.FIREBASE_NODE_EXPENSE).child(this.uid).child(this.mTvSelectDate.getText().toString().trim());
        this.expensesRef = child;
        if (child == null) {
            Toast.makeText(this, "No Reference node found", Toast.LENGTH_SHORT).show();
        } else if (CommonMethod.isNetworkConnected(this)) {
            CommonMethod.showProgressDialog(this);
            this.expensesRef.addListenerForSingleValueEvent(new ValueEventListener() {


                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot next : dataSnapshot.getChildren()) {
                            ArrayList expenses = new ArrayList<>();
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
                                masterExpenseModel.setDay(Integer.parseInt(simpleDateFormat2.format(date)));
                            }
                            boolean z = false;
                            long j = 0;
                            long j2 = 0;
                            for (DataSnapshot next2 : next.getChildren()) {
                                Expense expense = next2.getValue(Expense.class);
                                expense.setId(next2.getKey());
                                expense.setDate(masterExpenseModel.getDate());
                                expenses.add(expense);
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
                            masterExpenseModel.setExpenses(expenses);
                            masterExpenseModel.setTransactionAdded(true);
                            DashboardActivity.this.mTransactionList.add(masterExpenseModel);
                            DashboardActivity.this.totalIncome += masterExpenseModel.getTotalIncome();
                            DashboardActivity.this.totalExpense += masterExpenseModel.getTotalExpense();
                        }
                        if (DashboardActivity.this.mTransactionList != null) {
                            Log.d(DashboardActivity.this.TAG, "no of records of the search is " + DashboardActivity.this.mTransactionList.size());
                        }
                        CommonMethod.cancelProgressDialog();
                        DashboardActivity.this.displayAmount();
                        if (DashboardActivity.this.mTopFragment == null) {
                            return;
                        }
                        if (DashboardActivity.this.mTopFragment instanceof HomeFragment) {
                            ((HomeFragment) DashboardActivity.this.mTopFragment).displayData();
                        } else if (DashboardActivity.this.mTopFragment instanceof CalendarFragment) {
                            ((CalendarFragment) DashboardActivity.this.mTopFragment).displayData();
                        } else if (!(DashboardActivity.this.mTopFragment instanceof ChartFragment)) {
                        } else {
                            if (switchToggleBackGround(true)) {
                                ((ChartFragment) DashboardActivity.this.mTopFragment).getData(Constant.TYPE_EXPENSE);
                            } else {
                                ((ChartFragment) DashboardActivity.this.mTopFragment).getData(Constant.TYPE_INCOME);
                            }
                        }
                    } else {
                        CommonMethod.cancelProgressDialog();
                    }
                }

                public void onCancelled(DatabaseError databaseError) {
                    CommonMethod.cancelProgressDialog();
                    Toast.makeText(DashboardActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            CommonMethod.showConnectionAlert(this);
        }
    }

    public void myClicks(int clicks) {
        Fragment fragment;
        switch (clicks) {
            case 1:
                homeNav.setImageResource(R.drawable.ic_a_img_c);
                calendarNav.setImageResource(R.drawable.ic_b_img);
                analyticsNav.setImageResource(R.drawable.ic_analytics_img);
                fragment = new HomeFragment();
                break;
            case 2:
                homeNav.setImageResource(R.drawable.ic_a_img);
                calendarNav.setImageResource(R.drawable.ic_b_img_c);
                analyticsNav.setImageResource(R.drawable.ic_analytics_img);
                fragment = new CalendarFragment();
                break;
            case 3:
                homeNav.setImageResource(R.drawable.ic_a_img);
                calendarNav.setImageResource(R.drawable.ic_b_img);
                analyticsNav.setImageResource(R.drawable.ic_c_img_c);
                fragment = new ChartFragment();
                break;
            default:
                fragment = null;
                break;
        }
        loadFragment(fragment);
    }

    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Fragment fragment;
        switch (menuItem.getItemId()) {
            case R.id.navigation_calendar_purchased:
                fragment = new CalendarFragment();
                break;
            case R.id.navigation_chart_purchased:
                fragment = new ChartFragment();
                break;
            case R.id.navigation_home_purchased:
                fragment = new HomeFragment();
                break;
            default:
                fragment = null;
                break;
        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment == null) {
            return false;
        }
        this.mTopFragment = fragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        if (fragment instanceof ChartFragment) {
            this.mFabAddTransaction.hide();
            this.mToggleSwitchChart.setVisibility(View.VISIBLE);
            return true;
        }
        this.mFabAddTransaction.show();
        this.mToggleSwitchChart.setVisibility(View.GONE);
        return true;
    }

    public void openTransactionEntryDialog(Expense expense) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment findFragmentByTag = supportFragmentManager.findFragmentByTag("fragment_dialog_for_expense_entry");
        if (findFragmentByTag != null) {
            supportFragmentManager.beginTransaction().remove(findFragmentByTag).commit();
        }
        Bundle bundle = new Bundle();
        Date date = null;
        try {
            date = this.monthYearParser.parse(this.mTvSelectDate.getText().toString().trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int i = 0;
        if (date != null) {
            i = Integer.parseInt(this.monthParser.format(date)) - 1;
        }
        bundle.putInt("SelectedMonth", i);
        bundle.putSerializable("ExpenseDetailModel", expense);
        bundle.putInt("cx", 20);
        bundle.putInt("cy", 20);
        AddExpenseFragment newInstance = AddExpenseFragment.newInstance(bundle);
        newInstance.show(supportFragmentManager, "fragment_dialog_for_expense_entry");
        newInstance.setCancelable(true);
    }

    public void dismissDialog() {
        AddExpenseFragment addExpenseFragment;
        Fragment findFragmentByTag = getSupportFragmentManager().findFragmentByTag("fragment_dialog_for_expense_entry");
        if (findFragmentByTag != null && (addExpenseFragment = (AddExpenseFragment) findFragmentByTag) != null && addExpenseFragment.isVisible()) {
            addExpenseFragment.dismiss();
        }
    }

    public void openViewTransactionEntryDetailsDialog(String str, MasterExpenseModel masterExpenseModel) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment findFragmentByTag = supportFragmentManager.findFragmentByTag("fragment_view_detail_dialog");
        if (findFragmentByTag != null) {
            supportFragmentManager.beginTransaction().remove(findFragmentByTag).commit();
        }
        Bundle bundle = new Bundle();
        bundle.putString("SelectedDate", str);
        bundle.putSerializable("ExpenseDetailModel", masterExpenseModel);
        bundle.putInt("cx", 20);
        bundle.putInt("cy", 20);
        DayWiseExpenseDetailFragment newInstance = DayWiseExpenseDetailFragment.newInstance(bundle);
        newInstance.show(supportFragmentManager, "fragment_view_detail_dialog");
        newInstance.setCancelable(true);
    }

    public void dismissViewTransactionEntryDetailsDialog(float f, float f2) {
        DayWiseExpenseDetailFragment dayWiseExpenseDetailFragment;
        Fragment findFragmentByTag = getSupportFragmentManager().findFragmentByTag("fragment_view_detail_dialog");
        if (findFragmentByTag != null && (dayWiseExpenseDetailFragment = (DayWiseExpenseDetailFragment) findFragmentByTag) != null && dayWiseExpenseDetailFragment.isVisible()) {
            dayWiseExpenseDetailFragment.dismiss();
        }
    }

    public void resetAmountData() {
        this.totalIncome = 0;
        this.totalExpense = 0;
        displayAmount();
    }

    public void displayAmount() {
        this.mTvTotalIncome.setText(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" ").concat(CommonMethod.formatPrice((double) this.totalIncome)));
        this.mTvTotalExpense.setText(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" ").concat(CommonMethod.formatPrice((double) this.totalExpense)));
        this.mTvTotalSaving.setText(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" ").concat(CommonMethod.formatPrice((double) (this.totalIncome - this.totalExpense))));
    }

    private void showDialogToChooseCurrency() {

        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_choose_currency, findViewById(android.R.id.content), false);
        final EditText editText = inflate.findViewById(R.id.et_currency);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
        builder.setTitle("CHOOSE CURRENCY");
        builder.setView(inflate);
        builder.setPositiveButton("Set", null);
        final AlertDialog create = builder.create();
        create.setOnShowListener(dialogInterface -> create.getButton(-1).setOnClickListener(view -> {
            final String trim = editText.getText().toString().trim();
            if (trim.length() == 0) {
                Toast.makeText(DashboardActivity.this, "Enter your currency first.", Toast.LENGTH_SHORT).show();
            } else if (CommonMethod.isNetworkConnected(DashboardActivity.this)) {
                HashMap hashMap = new HashMap();
                hashMap.put("currencySymbol", trim);
                CommonMethod.showProgressDialog(DashboardActivity.this);
                DatabaseReference child = FirebaseDatabase.getInstance().getReference().child(Constant.FIREBASE_NODE_USER);
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                Objects.requireNonNull(currentUser);
                child.child(currentUser.getUid()).updateChildren(hashMap).addOnSuccessListener((OnSuccessListener<Void>) v -> {
                    CommonMethod.cancelProgressDialog();
                    create.dismiss();
                    MySharedPreferences.setStr(MySharedPreferences.KEY_CURRENCY_TYPE, trim);
                    DashboardActivity.this.init();
                }).addOnFailureListener(exc -> {
                    CommonMethod.cancelProgressDialog();
                    Toast.makeText(DashboardActivity.this, exc.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                CommonMethod.showConnectionAlert(DashboardActivity.this);
            }
        }));
        create.setCancelable(false);
        create.setCanceledOnTouchOutside(false);
        create.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}