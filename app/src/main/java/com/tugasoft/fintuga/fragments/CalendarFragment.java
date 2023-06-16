package com.tugasoft.fintuga.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tugasoft.fintuga.activity.DashboardActivity;
import com.tugasoft.fintuga.models.Expense;
import com.tugasoft.fintuga.models.MasterExpenseModel;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;
import com.tugasoft.fintuga.utils.Constant;
import com.tugasoft.fintuga.utils.MaterialCalendar;
import com.tugasoft.fintuga.utils.MaterialCalendarAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static int mNumEventsOnDay;

    public String TAG = CalendarFragment.class.getCanonicalName();
    private FirebaseAuth mAuth;
    private GridView mCalendar;
    private MaterialCalendarAdapter mMaterialCalendarAdapter;
    private String mUid;
    private SimpleDateFormat monthParser = new SimpleDateFormat("MM", Locale.US);
    private SimpleDateFormat monthYearParser = new SimpleDateFormat(Constant.MONTH_YEAR_FORMAT, Locale.US);
    private DatabaseReference ref;
    private View view;
    private SimpleDateFormat yearParser = new SimpleDateFormat(Constant.YEAR_FORMAT, Locale.US);

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_calendar, (ViewGroup) null);
        this.view = inflate;
        return inflate;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (getActivity() != null && (getActivity() instanceof DashboardActivity)) {
            this.ref = ((DashboardActivity) getActivity()).ref;
            this.mAuth = ((DashboardActivity) getActivity()).mAuth;
            this.mUid = ((DashboardActivity) getActivity()).uid;
        }
        GridView gridView = (GridView) this.view.findViewById(R.id.material_calendar_gridView);
        this.mCalendar = gridView;
        if (gridView != null) {
            gridView.setOnItemClickListener(this);
            MaterialCalendarAdapter materialCalendarAdapter = new MaterialCalendarAdapter(getActivity());
            this.mMaterialCalendarAdapter = materialCalendarAdapter;
            this.mCalendar.setAdapter(materialCalendarAdapter);
            if (!(MaterialCalendar.mCurrentDay == -1 || MaterialCalendar.mFirstDay == -1)) {
                this.mCalendar.setItemChecked(MaterialCalendar.mFirstDay + 6 + MaterialCalendar.mCurrentDay, true);
                MaterialCalendarAdapter materialCalendarAdapter2 = this.mMaterialCalendarAdapter;
                if (materialCalendarAdapter2 != null) {
                    materialCalendarAdapter2.notifyDataSetChanged();
                }
            }
        }
        Date date = null;
        try {
            date = this.monthYearParser.parse(((DashboardActivity) getActivity()).mTvSelectDate.getText().toString().trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            MaterialCalendar.getInitialCalendarInfo(this, Integer.valueOf(this.monthParser.format(date)).intValue() - 1, Integer.valueOf(this.yearParser.format(date)).intValue());
        }
    }

    public void onItemClick(AdapterView<?> adapterView, View view2, int i, long j) {
        int i2;
        if (adapterView.getId() == R.id.material_calendar_gridView) {
            MaterialCalendar.selectCalendarDay(this.mMaterialCalendarAdapter, i);
            mNumEventsOnDay = -1;
            Boolean bool = false;
            ArrayList<MasterExpenseModel> arrayList = ((DashboardActivity) getActivity()).mTransactionList;
            if (MaterialCalendar.mFirstDay == -1 || arrayList == null || arrayList.size() <= 0) {
                i2 = -1;
            } else {
                i2 = i - (MaterialCalendar.mFirstDay + 6);
                Log.d("SELECTED_SAVED_DATE", String.valueOf(i2));
                int size = arrayList.size();
                for (int i3 = 0; i3 < size; i3++) {
                    if (i2 == arrayList.get(i3).getDay() && arrayList.get(i3).isTransactionAdded()) {
                        bool = true;
                    }
                }
            }
            if (bool.booleanValue()) {
                Log.d("POS", String.valueOf(i2));
                if (CommonMethod.isNetworkConnected(getActivity())) {
                    fetchTimeSheetEntryHistoryListForSelectedDate();
                } else {
                    CommonMethod.showConnectionAlert(getActivity());
                }
            } else {
                Toast.makeText(getActivity(), "No transaction entry available", Toast.LENGTH_SHORT).show();
                mNumEventsOnDay = -1;
            }
        }
    }

    public void changeCalendarMonthWise(String str, String str2) {
        Date date;
        Date date2 = null;
        try {
            date = this.monthYearParser.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            date = null;
        }
        try {
            date2 = this.monthYearParser.parse(str2);
        } catch (ParseException e2) {
            e2.printStackTrace();
        }
        if (date.before(date2)) {
            MaterialCalendar.previousOnClick(Integer.valueOf(this.monthParser.format(date)).intValue() - 1, Integer.valueOf(this.yearParser.format(date)).intValue(), this.mCalendar, this.mMaterialCalendarAdapter);
        } else if (date.after(date2)) {
            MaterialCalendar.nextOnClick(Integer.valueOf(this.monthParser.format(date)).intValue() - 1, Integer.valueOf(this.yearParser.format(date)).intValue(), this.mCalendar, this.mMaterialCalendarAdapter);
        }
    }

    public void displayData() {
        showExpenseTable();
    }

    public void resetData() {
        MaterialCalendarAdapter materialCalendarAdapter = this.mMaterialCalendarAdapter;
        if (materialCalendarAdapter != null) {
            materialCalendarAdapter.notifyDataSetChanged();
        }
    }

    public void fetchTimeSheetEntryHistoryListForSelectedDate() {
        String str;
        CommonMethod.showProgressDialog(getActivity());
        final Calendar instance = Calendar.getInstance();
        instance.set(MaterialCalendar.mYear, MaterialCalendar.mMonth, this.mCalendar.getCheckedItemPosition() - (MaterialCalendar.mFirstDay + 6));
        String str2 = null;
        try {
            str = this.monthYearParser.format(instance.getTime());
            try {
                str2 = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.US).format(instance.getTime());
            } catch (Exception e) {
                e = e;
                e.printStackTrace();
                this.ref.child(Constant.FIREBASE_NODE_EXPENSE).child(this.mUid).child(str).child(str2).addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        CommonMethod.cancelProgressDialog();
                        MasterExpenseModel masterExpenseModel = new MasterExpenseModel();
                        boolean z = false;
                        if (dataSnapshot.exists()) {
                            ArrayList arrayList = new ArrayList();
                            long j = 0;
                            long j2 = 0;
                            for (DataSnapshot next : dataSnapshot.getChildren()) {
                                Expense expense = (Expense) next.getValue(Expense.class);
                                expense.setId(next.getKey());
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
                            String str = null;
                            try {
                                str = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.US).format(instance.getTime());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ((DashboardActivity) CalendarFragment.this.getActivity()).openViewTransactionEntryDetailsDialog(str, masterExpenseModel);
                            return;
                        }
                        Toast.makeText(CalendarFragment.this.getActivity(), "No transaction entry available", Toast.LENGTH_SHORT).show();
                    }

                    public void onCancelled(DatabaseError databaseError) {
                        CommonMethod.cancelProgressDialog();
                        Log.d(CalendarFragment.this.TAG, databaseError.getMessage());
                        Toast.makeText(CalendarFragment.this.getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e2) {

            str = null;
            e2.printStackTrace();
            this.ref.child(Constant.FIREBASE_NODE_EXPENSE).child(this.mUid).child(str).child(str2).addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    CommonMethod.cancelProgressDialog();
                    MasterExpenseModel masterExpenseModel = new MasterExpenseModel();
                    boolean z = false;
                    if (dataSnapshot.exists()) {
                        ArrayList arrayList = new ArrayList();
                        long j = 0;
                        long j2 = 0;
                        for (DataSnapshot next : dataSnapshot.getChildren()) {
                            Expense expense = (Expense) next.getValue(Expense.class);
                            expense.setId(next.getKey());
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
                        String str = null;
                        try {
                            str = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.US).format(instance.getTime());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ((DashboardActivity) CalendarFragment.this.getActivity()).openViewTransactionEntryDetailsDialog(str, masterExpenseModel);
                        return;
                    }
                    Toast.makeText(CalendarFragment.this.getActivity(), "No transaction entry available", Toast.LENGTH_SHORT).show();
                }

                public void onCancelled(DatabaseError databaseError) {
                    CommonMethod.cancelProgressDialog();
                    Log.d(CalendarFragment.this.TAG, databaseError.getMessage());
                    Toast.makeText(CalendarFragment.this.getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        this.ref.child(Constant.FIREBASE_NODE_EXPENSE).child(this.mUid).child(str).child(str2).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                CommonMethod.cancelProgressDialog();
                MasterExpenseModel masterExpenseModel = new MasterExpenseModel();
                boolean z = false;
                if (dataSnapshot.exists()) {
                    ArrayList arrayList = new ArrayList();
                    long j = 0;
                    long j2 = 0;
                    for (DataSnapshot next : dataSnapshot.getChildren()) {
                        Expense expense = (Expense) next.getValue(Expense.class);
                        expense.setId(next.getKey());
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
                    String str = null;
                    try {
                        str = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.US).format(instance.getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ((DashboardActivity) CalendarFragment.this.getActivity()).openViewTransactionEntryDetailsDialog(str, masterExpenseModel);
                    return;
                }
                Toast.makeText(CalendarFragment.this.getActivity(), "No transaction entry available", Toast.LENGTH_SHORT).show();
            }

            public void onCancelled(DatabaseError databaseError) {
                CommonMethod.cancelProgressDialog();
                Log.d(CalendarFragment.this.TAG, databaseError.getMessage());
                Toast.makeText(CalendarFragment.this.getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showExpenseTable() {
        MaterialCalendarAdapter materialCalendarAdapter = this.mMaterialCalendarAdapter;
        if (materialCalendarAdapter != null) {
            materialCalendarAdapter.notifyDataSetChanged();
        }
    }

    public void onDetach() {
        super.onDetach();
        System.gc();
    }
}
