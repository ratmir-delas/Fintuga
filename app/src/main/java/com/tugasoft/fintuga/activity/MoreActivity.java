package com.tugasoft.fintuga.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.tugasoft.fintuga.models.CategoryListModel;
import com.tugasoft.fintuga.models.Category;
import com.tugasoft.fintuga.models.User;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;
import com.tugasoft.fintuga.utils.Constant;
import com.tugasoft.fintuga.utils.NotificationScheduler;
import com.tugasoft.fintuga.utils.MySharedPreferences;
import com.tugasoft.fintuga.ads.AdsProvider;
import com.tugasoft.fintuga.receiver.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;


public class MoreActivity extends AppCompatActivity implements View.OnClickListener {

    public CardView admobcard;
    public TextView mTvCurrencySymbol;
    public TextView mTvCurrencySymbolSubTitle;
    public TextView mTvReminderTime;
    int hour;
    int min;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_more);
        this.admobcard = findViewById(R.id.admobcard);

        AdsProvider.getInstance().addNativeView(this, findViewById(R.id.customeventnative_framelayout));

        setTooBar();
        init();
    }

    private void setTooBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_more));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }


    public void setCurrencySymbolAndSubTitle() {
        if (MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").trim().length() == 0) {
            this.mTvCurrencySymbolSubTitle.setText("Set currency symbol");
        } else {
            this.mTvCurrencySymbolSubTitle.setText("Update currency symbol");
        }
        this.mTvCurrencySymbol.setText(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, ""));
    }

    private void init() {
        findViewById(R.id.cv_add_category).setOnClickListener(this);
        findViewById(R.id.cv_currency_symbol).setOnClickListener(this);
        mTvCurrencySymbolSubTitle = findViewById(R.id.tv_currency_symbol_sub_title);
        mTvCurrencySymbol = findViewById(R.id.tv_currency_symbol);
        SwitchCompat switchCompat = findViewById(R.id.switch_reminder);
        final RelativeLayout relativeLayout = findViewById(R.id.rl_choose_reminder_time);
        mTvReminderTime = findViewById(R.id.tv_reminder_time);
        hour = MySharedPreferences.get_hour(MySharedPreferences.KEY_REMINDER_HOUR);
        min = MySharedPreferences.get_min(MySharedPreferences.KEY_REMINDER_MIN);
        setCurrencySymbolAndSubTitle();
        switchCompat.setChecked(MySharedPreferences.getBool(MySharedPreferences.KEY_IS_REMINDER_ENABLE));
        relativeLayout.setAlpha(switchCompat.isChecked() ? 1.0f : 0.4f);
        mTvReminderTime.setText(getFormattedTime(hour, min));
        switchCompat.setOnCheckedChangeListener((compoundButton, checked) -> {
            MySharedPreferences.setBool(MySharedPreferences.KEY_IS_REMINDER_ENABLE, checked);
            if (checked) {
                NotificationScheduler.setReminder(getApplicationContext(), AlarmReceiver.class,
                        MySharedPreferences.get_hour(MySharedPreferences.KEY_REMINDER_HOUR),
                        MySharedPreferences.get_min(MySharedPreferences.KEY_REMINDER_MIN));
                relativeLayout.setAlpha(1.0f);
            } else {
                NotificationScheduler.cancelReminder(getApplicationContext(), AlarmReceiver.class);
                relativeLayout.setAlpha(0.4f);
            }
        });
        relativeLayout.setOnClickListener(this);
        if (!MySharedPreferences.getBool(MySharedPreferences.KEY_IS_USER_PROFILE_FETCHED)) {
            fetchUserProfileData();
        }
    }

    private String getFormattedTime(int hour, int minute) {
        String timeString = hour + ":" + minute;
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(timeString);
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.more_options, menu);
        if (!MySharedPreferences.getBool(MySharedPreferences.KEY_IS_WELCOME_SCREEN_SHOWN)) {
            return true;
        }
        menu.findItem(R.id.item_move_next).setVisible(false);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.item_move_next) {
            if (IsCurrencySelected()) {
                MySharedPreferences.setBool(MySharedPreferences.KEY_IS_WELCOME_SCREEN_SHOWN, true);
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Set your currency symbol first.", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id != R.id.rl_choose_reminder_time) {
            switch (id) {
                case R.id.cv_add_category:
                    startActivity(new Intent(this, AddTransactionCategoryActivity.class));
                    return;
                case R.id.cv_currency_symbol:
                    showDialogToChooseCurrency();
                    return;
                default:
                    return;
            }
        } else if (MySharedPreferences.getBool(MySharedPreferences.KEY_IS_REMINDER_ENABLE)) {
            showTimePickerDialog(MySharedPreferences.get_hour(MySharedPreferences.KEY_REMINDER_HOUR), MySharedPreferences.get_min(MySharedPreferences.KEY_REMINDER_MIN));
        }
    }


    public void fetchUserProfileData() {
        if (CommonMethod.isNetworkConnected(this)) {
            final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            CommonMethod.showProgressDialog(this);
            FirebaseDatabase.getInstance().getReference().child(Constant.FIREBASE_NODE_USER).child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        MySharedPreferences.setBool(MySharedPreferences.KEY_IS_USER_PROFILE_FETCHED, true);
                        User user = dataSnapshot.getValue(User.class);

                        CategoryListModel categoryListModel = new Gson().fromJson(user.getCategories(), CategoryListModel.class);
                        if (categoryListModel != null) {
                            MySharedPreferences.setStr(MySharedPreferences.KEY_EXPENSE_CATEGORY, new Gson().toJson(categoryListModel.getExpenseCategoryList()));
                            MySharedPreferences.setStr(MySharedPreferences.KEY_INCOME_CATEGORY, new Gson().toJson(categoryListModel.getIncomeCategoryList()));
                        }
                        MySharedPreferences.setBool(MySharedPreferences.KEY_IS_ADMIN_USER, user.isAdminUser());
                        MySharedPreferences.setStr(MySharedPreferences.KEY_USER_NAME, user.getFullName());
                        MySharedPreferences.setStr(MySharedPreferences.KEY_CURRENCY_TYPE, user.getCurrencySymbol());
                        setCurrencySymbolAndSubTitle();
                        CommonMethod.cancelProgressDialog();
                        return;
                    }

                    User user = new User();
                    user.setId(currentUser.getUid());
                    user.setFullName(currentUser.getDisplayName());
                    user.setCategories(storeDefaultTransactionCategory());
                    FirebaseDatabase.getInstance().getReference().child(Constant.FIREBASE_NODE_USER).child(currentUser.getUid()).setValue(user).addOnSuccessListener(v -> {
                        MySharedPreferences.setBool(MySharedPreferences.KEY_IS_USER_PROFILE_FETCHED, true);
                        MySharedPreferences.setStr(MySharedPreferences.KEY_BANK_NAME_LIST, "");
                        CategoryListModel categoryListModel = new Gson().fromJson(user.getCategories(), CategoryListModel.class);
                        if (categoryListModel != null) {
                            MySharedPreferences.setStr(MySharedPreferences.KEY_EXPENSE_CATEGORY, new Gson().toJson(categoryListModel.getExpenseCategoryList()));
                            MySharedPreferences.setStr(MySharedPreferences.KEY_INCOME_CATEGORY, new Gson().toJson(categoryListModel.getIncomeCategoryList()));
                        }
                        MySharedPreferences.setBool(MySharedPreferences.KEY_IS_ADMIN_USER, false);
                        MySharedPreferences.setStr(MySharedPreferences.KEY_USER_NAME, user.getFullName());
                        MySharedPreferences.setStr(MySharedPreferences.KEY_CURRENCY_TYPE, "");
                        setCurrencySymbolAndSubTitle();
                        CommonMethod.cancelProgressDialog();
                    }).addOnFailureListener(exc -> {
                        CommonMethod.cancelProgressDialog();
                        showDialogForUserProfileFetchRetry(exc.getMessage());
                    });
                }

                public void onCancelled(@NonNull DatabaseError databaseError) {
                    CommonMethod.cancelProgressDialog();
                    showDialogForUserProfileFetchRetry(databaseError.getMessage());
                }
            });
            return;
        }
        showDialogForUserProfileFetchRetry("Internet connection");
    }


    public void showDialogForUserProfileFetchRetry(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User Profile");
        builder.setMessage("Fetching user profile failed due to ' ".concat(str).concat(" '. Please try again.")).setCancelable(true).setPositiveButton(getString(R.string.action_retry), (dialogInterface, i) -> fetchUserProfileData());
        AlertDialog create = builder.create();
        create.setCancelable(false);
        create.setCanceledOnTouchOutside(false);
        create.show();
    }

    private void showTimePickerDialog(int i, int i2) {
        View inflate = getLayoutInflater().inflate(R.layout.timepicker_header, null);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.DialogTheme, (timePicker, i1, i21) -> {
            MySharedPreferences.setInt(MySharedPreferences.KEY_REMINDER_HOUR, i1);
            MySharedPreferences.setInt(MySharedPreferences.KEY_REMINDER_MIN, i21);
            mTvReminderTime.setText(getFormatedTime(i1, i21));
            NotificationScheduler.setReminder(getApplicationContext(), AlarmReceiver.class, MySharedPreferences.get_hour(MySharedPreferences.KEY_REMINDER_HOUR), MySharedPreferences.get_hour(MySharedPreferences.KEY_REMINDER_MIN));
        }, i, i2, false);
        timePickerDialog.setCustomTitle(inflate);
        timePickerDialog.show();
    }

    private void showDialogToChooseCurrency() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_choose_currency, findViewById(android.R.id.content), false);
        final EditText editText = inflate.findViewById(R.id.et_currency);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
        builder.setTitle(getString(R.string.label_choose_currency));
        builder.setView(inflate);
        builder.setPositiveButton(IsCurrencySelected() ? "Update" : "Set", null).setNegativeButton(getString(R.string.action_cancel), null);
        final AlertDialog create = builder.create();
        create.setOnShowListener(dialogInterface -> {
            create.getButton(-1).setOnClickListener(view -> {
                final String trim = editText.getText().toString().trim();
                if (trim.length() <= 0) {
                    Toast.makeText(MoreActivity.this, "Enter your currency first.", Toast.LENGTH_SHORT).show();
                } else if (CommonMethod.isNetworkConnected(MoreActivity.this)) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("currencySymbol", trim);
                    CommonMethod.showProgressDialog(MoreActivity.this);
                    DatabaseReference child = FirebaseDatabase.getInstance().getReference().child(Constant.FIREBASE_NODE_USER);
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    Objects.requireNonNull(currentUser);
                    child.child(currentUser.getUid()).updateChildren(hashMap).addOnSuccessListener((OnSuccessListener<Void>) voidR -> {
                        CommonMethod.cancelProgressDialog();
                        create.dismiss();
                        mTvCurrencySymbol.setText(trim);
                        MySharedPreferences.setStr(MySharedPreferences.KEY_CURRENCY_TYPE, trim);
                        if (!MySharedPreferences.getBool(MySharedPreferences.KEY_IS_WELCOME_SCREEN_SHOWN)) {
                            mTvCurrencySymbolSubTitle.setText("Update currency symbol");
                            Toast.makeText(MoreActivity.this, "Your currency symbol has been set successfully.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MoreActivity.this);
                        builder1.setTitle("Success");
                        builder1.setMessage("Your currency symbol has been updated successfully. Please login again.").setPositiveButton(getString(R.string.action_re_login), (dialogInterface1, i) -> {
                            dialogInterface1.cancel();
                            FirebaseAuth.getInstance().signOut();
                            MySharedPreferences.clearSP();
                            finish();
                            Intent intent = new Intent(MoreActivity.this, FirebaseAuthenticationActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        });
                        AlertDialog create1 = builder1.create();
                        create1.setCancelable(false);
                        create1.show();
                    }).addOnFailureListener(exc -> {
                        CommonMethod.cancelProgressDialog();
                        Toast.makeText(MoreActivity.this, exc.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } else {
                    CommonMethod.showConnectionAlert(MoreActivity.this);
                }
            });
            create.getButton(-2).setOnClickListener(view -> dialogInterface.dismiss());
        });
        create.setCancelable(false);
        create.setCanceledOnTouchOutside(false);
        create.show();
    }

    public String getFormatedTime(int hours, int minutes) {
        String time = hours + ":" + minutes;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", getCurrentLocale());
            Date parse = simpleDateFormat.parse(time);
            simpleDateFormat.applyPattern("hh:mm a");
            assert parse != null;
            return simpleDateFormat.format(parse);
        } catch (Exception e) {
            e.printStackTrace();
            return "More Activity time parse error (getFormatedTime): " + e.getMessage();
        }
    }

    private boolean IsCurrencySelected() {
        return MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").trim().length() != 0;
    }


    public String storeDefaultTransactionCategory() {
        CategoryListModel categoryListModel = new CategoryListModel();
        ArrayList expense = new ArrayList<>();
        expense.add(new Category(getString(R.string.label_expense_type_groceries), getString(R.string.color_expens_groceries)));
        expense.add(new Category(getString(R.string.label_expense_type_rent), getString(R.string.color_expens_rent)));
        expense.add(new Category(getString(R.string.label_expense_type_transportation), getString(R.string.color_expens_transportation)));
        expense.add(new Category(getString(R.string.label_expense_type_shopping), getString(R.string.color_expens_shopping)));
        expense.add(new Category(getString(R.string.label_expense_type_fees), getString(R.string.color_expens_fees)));
        expense.add(new Category(getString(R.string.label_expense_type_donation), getString(R.string.color_expens_donation)));
        expense.add(new Category(getString(R.string.label_expense_type_food), getString(R.string.color_expens_food)));
        expense.add(new Category(getString(R.string.label_expense_type_loan), getString(R.string.color_expens_acc_loan)));
        expense.add(new Category(getString(R.string.label_expense_type_medical), getString(R.string.color_expens_medical)));
        ArrayList income = new ArrayList<>();
        income.add(new Category(getString(R.string.label_income_type_salary), getString(R.string.color_income_salary)));
        income.add(new Category(getString(R.string.label_income_type_income_from_rent), getString(R.string.color_income_income_from_rent)));
        income.add(new Category(getString(R.string.label_income_type_interest), getString(R.string.color_income_interest)));
        income.add(new Category(getString(R.string.label_income_type_pension), getString(R.string.color_income_pension)));
        income.add(new Category(getString(R.string.label_income_type_work_on_demand), getString(R.string.color_income_work_on_demand)));
        income.add(new Category(getString(R.string.label_income_type_tax_return), getString(R.string.color_income_tax_return)));
        income.add(new Category(getString(R.string.label_income_type_coupon), getString(R.string.color_income_coupon)));
        categoryListModel.setExpenseCategoryList(expense);
        categoryListModel.setIncomeCategoryList(income);
        return new Gson().toJson(categoryListModel);
    }

    public Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= 24) {
            return getResources().getConfiguration().getLocales().get(0);
        }
        return getResources().getConfiguration().locale;
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}
