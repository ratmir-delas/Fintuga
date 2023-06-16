package com.tugasoft.fintuga.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.polyak.iconswitch.IconSwitch;
import com.tugasoft.fintuga.adapters.AddExpenseCategoryAdapter;
import com.tugasoft.fintuga.adapters.AddIncomeCategoryAdapter;
import com.tugasoft.fintuga.adapters.ColorPickerAdapter;
import com.tugasoft.fintuga.models.CategoryListModel;
import com.tugasoft.fintuga.models.Category;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;
import com.tugasoft.fintuga.utils.Constant;
import com.tugasoft.fintuga.utils.MySharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddTransactionCategoryActivity extends AppCompatActivity implements View.OnClickListener {
    public AddExpenseCategoryAdapter mAdapterForExpenseCategory;
    public AddIncomeCategoryAdapter mAdapterForIncomeCategory;
    DatabaseReference expensesRef;
    public static boolean isExpanse = true;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_add_category);
        setFireBase();
        setToobar();
        init();
    }

    private void setFireBase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        this.expensesRef = reference.child(Constant.FIREBASE_NODE_EXPENSE).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("categories");
    }

    private void setToobar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Category");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.black));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> AddTransactionCategoryActivity.this.onBackPressed());
    }

    private void init() {
        ArrayList arrayList = new ArrayList();
        String str = MySharedPreferences.getStr(MySharedPreferences.KEY_EXPENSE_CATEGORY, "");
        if (str.trim().length() > 0) {
            try {
                arrayList = (ArrayList) new Gson().fromJson(str, new TypeToken<List<Category>>() {
                }.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ArrayList arrayList2 = new ArrayList();
        String str2 = MySharedPreferences.getStr(MySharedPreferences.KEY_INCOME_CATEGORY, "");
        if (str2.trim().length() > 0) {
            try {
                arrayList2 = (ArrayList) new Gson().fromJson(str2, new TypeToken<List<Category>>() {
                }.getType());
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_expense_category);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), 1));
        AddExpenseCategoryAdapter addExpenseCategoryAdapter = new AddExpenseCategoryAdapter(this, arrayList);
        this.mAdapterForExpenseCategory = addExpenseCategoryAdapter;
        recyclerView.setAdapter(addExpenseCategoryAdapter);
        RecyclerView recyclerView2 = (RecyclerView) findViewById(R.id.rv_income_category);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2.addItemDecoration(new DividerItemDecoration(recyclerView2.getContext(), 1));
        AddIncomeCategoryAdapter addIncomeCategoryAdapter = new AddIncomeCategoryAdapter(this, arrayList2);
        this.mAdapterForIncomeCategory = addIncomeCategoryAdapter;
        recyclerView2.setAdapter(addIncomeCategoryAdapter);
        ((FloatingActionButton) findViewById(R.id.fab_add_transaction_category)).setOnClickListener(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_category, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.item_submit) {
            return true;
        }
        if ((this.mAdapterForExpenseCategory.getCategoryList() == null || this.mAdapterForExpenseCategory.getCategoryList().size() <= 0) && (this.mAdapterForIncomeCategory.getCategoryList() == null || this.mAdapterForIncomeCategory.getCategoryList().size() <= 0)) {
            Toast.makeText(this, "No new category has been added yet.", 0).show();
            return true;
        } else if (CommonMethod.isNetworkConnected(this)) {
            CommonMethod.showProgressDialog(this);
            CategoryListModel categoryListModel = new CategoryListModel();
            AddExpenseCategoryAdapter addExpenseCategoryAdapter = this.mAdapterForExpenseCategory;
            if (addExpenseCategoryAdapter != null) {
                categoryListModel.setExpenseCategoryList(addExpenseCategoryAdapter.getCategoryList());
            }
            AddIncomeCategoryAdapter addIncomeCategoryAdapter = this.mAdapterForIncomeCategory;
            if (addIncomeCategoryAdapter != null) {
                categoryListModel.setIncomeCategoryList(addIncomeCategoryAdapter.getCategoryList());
            }
            this.expensesRef.setValue(new Gson().toJson((Object) categoryListModel)).addOnSuccessListener(new OnSuccessListener<Void>() {
                public void onSuccess(Void voidR) {
                    MySharedPreferences.setStr(MySharedPreferences.KEY_EXPENSE_CATEGORY, new Gson().toJson((Object) AddTransactionCategoryActivity.this.mAdapterForExpenseCategory.getCategoryList()));
                    MySharedPreferences.setStr(MySharedPreferences.KEY_INCOME_CATEGORY, new Gson().toJson((Object) AddTransactionCategoryActivity.this.mAdapterForIncomeCategory.getCategoryList()));
                    Toast.makeText(AddTransactionCategoryActivity.this, "Categories updated successfully.", Toast.LENGTH_SHORT).show();
                    CommonMethod.cancelProgressDialog();
                    AddTransactionCategoryActivity.this.onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                public void onFailure(Exception exc) {
                    exc.printStackTrace();
                    AddTransactionCategoryActivity addTransactionCategoryActivity = AddTransactionCategoryActivity.this;
                    Toast.makeText(addTransactionCategoryActivity, "Failed to submit : " + exc.getMessage(), 0).show();
                    CommonMethod.cancelProgressDialog();
                }
            });
            return true;
        } else {
            CommonMethod.showConnectionAlert(this);
            return true;
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.fab_add_transaction_category) {
            openCategoryDialog();
        }
    }


    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    private void openCategoryDialog() {
        isExpanse = true;
        final String[] categoryColor = {"#000000"};
        Dialog dialog = new Dialog(AddTransactionCategoryActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.color_palette_layout);
        dialog.setCancelable(false);
        dialog.show();

        RecyclerView recyclerView = dialog.findViewById(R.id.color_palette);
        TextView mTvBody = dialog.findViewById(R.id.tv_body);
        TextView mTvTitle = dialog.findViewById(R.id.tv_title);
        EditText editText = dialog.findViewById(R.id.et_category);
        AppCompatButton positiveButtion = dialog.findViewById(R.id.positive);
        AppCompatButton negativeButton = dialog.findViewById(R.id.negative);
        editText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        IconSwitch iconSwitch = dialog.findViewById(R.id.icon_switch);
        //initColors();

        String[] colorList = new String[]{"#f44336", "#03a9f4", "#00bcd4", "#009688", "#4caf50", "#8bc34a", "#cddc39", "#ffeb3b", "#ffc107", "#ff9800", "#ff5722", "#795548", "#9e9e9e", "#607d8b"};
        ColorPickerAdapter adapter = new ColorPickerAdapter(AddTransactionCategoryActivity.this, colorList);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        adapter.setOnColorChooser(new ColorPickerAdapter.OnChooseColorListener() {
            @Override
            public void onChoose(String color) {
                categoryColor[0] = color;
            }

            @Override
            public void onCancel() {

            }
        });

        positiveButtion.setOnClickListener(v -> {
            if (editText.getText().toString().isEmpty()) {
                editText.setError("Category Field Required");
                editText.requestFocus();
            } else {
                addCategoryInList(editText.getText().toString(), categoryColor[0]);
            }
            dialog.dismiss();
        });
        negativeButton.setOnClickListener(v -> dialog.dismiss());
        iconSwitch.setCheckedChangeListener(checked -> {
            switch (checked) {
                case LEFT:
                    isExpanse = true;
                    mTvTitle.setText("Add Expense Category");
                    mTvBody.setText("Please enter expense category.\nExe. Food & Drink,Transportation which are responsible for expenses are comes under EXPENSE.");
                    break;
                case RIGHT:
                    isExpanse = false;
                    mTvTitle.setText("Add Income Category");
                    mTvBody.setText("Please enter income category.\nExe. Work On Demand,Salary which are source of incomes are comes under INCOME.");
                    break;
            }
        });
    }


    public void addCategoryInList(String categroryName, String categoryColor) {
        Category category = new Category(categroryName, categoryColor);
        boolean z = true;
        if (isExpanse) {
            if (AddTransactionCategoryActivity.this.mAdapterForExpenseCategory.getCategoryList() == null || AddTransactionCategoryActivity.this.mAdapterForExpenseCategory.getCategoryList().size() <= 0) {
                AddTransactionCategoryActivity.this.mAdapterForExpenseCategory.addItem(category);
                return;
            }
            int size2 = AddTransactionCategoryActivity.this.mAdapterForExpenseCategory.getCategoryList().size();
            int i4 = 0;
            while (true) {
                if (i4 >= size2) {
                    z = false;
                    break;
                } else if (AddTransactionCategoryActivity.this.mAdapterForExpenseCategory.getCategoryList().get(i4).getCategory().equalsIgnoreCase(categroryName)) {
                    break;
                } else {
                    i4++;
                }
            }
            if (!z) {
                AddTransactionCategoryActivity.this.mAdapterForExpenseCategory.addItem(category);
            } else {
                Toast.makeText(AddTransactionCategoryActivity.this, "Duplicate category.", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (AddTransactionCategoryActivity.this.mAdapterForIncomeCategory.getCategoryList() == null || AddTransactionCategoryActivity.this.mAdapterForIncomeCategory.getCategoryList().size() <= 0) {
                AddTransactionCategoryActivity.this.mAdapterForIncomeCategory.addItem(category);
                return;
            }
            int size = AddTransactionCategoryActivity.this.mAdapterForIncomeCategory.getCategoryList().size();
            int i3 = 0;
            while (true) {
                if (i3 >= size) {
                    z = false;
                    break;
                } else if (AddTransactionCategoryActivity.this.mAdapterForIncomeCategory.getCategoryList().get(i3).getCategory().equalsIgnoreCase(categroryName)) {
                    break;
                } else {
                    i3++;
                }
            }
            if (!z) {
                AddTransactionCategoryActivity.this.mAdapterForIncomeCategory.addItem(category);
            } else {
                Toast.makeText(AddTransactionCategoryActivity.this, "Duplicate category.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
