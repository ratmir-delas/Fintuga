package com.tugasoft.fintuga.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.tugasoft.fintuga.activity.DashboardActivity;
import com.tugasoft.fintuga.fragments.HomeFragment;
import com.tugasoft.fintuga.models.Expense;
import com.tugasoft.fintuga.models.MasterExpenseModel;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;
import com.tugasoft.fintuga.utils.Constant;
import com.tugasoft.fintuga.utils.MySharedPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ExpenseHeaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final OnItemClickListener mListener;
    public Activity mContext;
    public Fragment mFragment;
    public SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.US);
    private final List<MasterExpenseModel> mList;
    private final int mResource;
    private final String TAG = "ExpanseHeaderAdapter";
    public static ExpenseChildAdapter expenseChildAdapter0;

    public ExpenseHeaderAdapter(Fragment fragment, int i, List<MasterExpenseModel> list, OnItemClickListener onItemClickListener) {
        this.mFragment = fragment;
        this.mContext = fragment.getActivity();
        this.mResource = i;
        this.mList = list;
        this.mListener = onItemClickListener;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(this.mResource, viewGroup, false));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ((MyViewHolder) viewHolder).bind(this.mList.get(i), i, this.mListener);
    }

    public int getItemCount() {
        return this.mList.size();
    }

    public int getItemViewType(int i) {
        return super.getItemViewType(i);
    }

    public interface OnItemClickListener {
        void onItemLongClick(MasterExpenseModel masterExpenseModel, int i);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        RecyclerView childRecyclerView;
        ImageView iv_proof;
        LinearLayout rl_header;
        TextView tv_date;
        TextView tv_day;
        TextView tv_month;
        TextView tv_total_expense;
        TextView tv_total_income;

        MyViewHolder(View view) {
            super(view);
            rl_header = view.findViewById(R.id.rl_header);
            tv_date = view.findViewById(R.id.tv_date);
            tv_day = view.findViewById(R.id.tv_day);
            tv_month = view.findViewById(R.id.tv_month);
            tv_total_income = view.findViewById(R.id.tv_total_income);
            tv_total_expense = view.findViewById(R.id.tv_total_expense);
            iv_proof = view.findViewById(R.id.iv_proof);
            RecyclerView recyclerView = view.findViewById(R.id.detailsListView);
            childRecyclerView = recyclerView;
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            childRecyclerView.setLayoutManager(linearLayoutManager);
        }


        public void bind(final MasterExpenseModel masterExpenseModel, final int i, final OnItemClickListener onItemClickListener) {
            Log.e(TAG, "bind: Header" + i);
            try {
                Date parse = simpleDateFormat.parse(masterExpenseModel.getDate());
                assert parse != null;
                tv_date.setText(new SimpleDateFormat("dd", Locale.US).format(parse));
                tv_day.setText(new SimpleDateFormat("EEE", Locale.US).format(parse).concat(","));
                tv_month.setText(new SimpleDateFormat("MMM yyyy", Locale.US).format(parse));
            } catch (ParseException unused) {
                tv_date.setText(masterExpenseModel.getDate());
            }
            if (masterExpenseModel.getTotalIncome() > 0) {
                TextView textView = tv_total_income;
                String str = MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "");
                textView.setText(str.concat(" " + CommonMethod.formatPrice(masterExpenseModel.getTotalIncome())));
                tv_total_income.setVisibility(View.VISIBLE);
            } else {
                tv_total_income.setVisibility(View.GONE);
            }
            if (masterExpenseModel.getTotalExpense() > 0) {
                TextView textView2 = tv_total_expense;
                String str2 = MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "");
                textView2.setText(str2.concat(" " + CommonMethod.formatPrice(masterExpenseModel.getTotalExpense())));
                tv_total_expense.setVisibility(View.VISIBLE);
            } else {
                tv_total_expense.setVisibility(View.GONE);
            }
            this.rl_header.setOnLongClickListener(view -> {
                onItemClickListener.onItemLongClick(masterExpenseModel, i);
                return true;
            });
            if (masterExpenseModel.isHavingProofImage()) {
                iv_proof.setVisibility(View.VISIBLE);
            } else {
                iv_proof.setVisibility(View.GONE);
            }

            iv_proof.setOnClickListener(view -> {
                ArrayList<Expense> detailList = masterExpenseModel.getExpenses();
                ArrayList arrayList = new ArrayList<>();
                Iterator<Expense> it = detailList.iterator();
                while (it.hasNext()) {
                    Expense next = it.next();
                    if (next.getProofUri() != null && next.getProofUri().trim().length() > 0) {
                        arrayList.add(next);
                    }
                }
                View inflate = LayoutInflater.from(mContext).inflate(R.layout.dialog_display_proof_image, mContext.findViewById(android.R.id.content), false);
                ((ViewPager) inflate.findViewById(R.id.masterViewPager)).setAdapter(new MyViewPagerAdapter(mContext, arrayList));
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.CustomDialogTheme);
                builder.setView(inflate);
                AlertDialog create = builder.create();
                create.setCancelable(true);
                create.setCanceledOnTouchOutside(true);
                create.show();
            });

            expenseChildAdapter0 = new ExpenseChildAdapter(R.layout.row_layout_detail, masterExpenseModel.getExpenses(), new ExpenseChildAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Expense expense, int iChild) {
                    if (mContext instanceof DashboardActivity) {
                        ((DashboardActivity) mContext).openTransactionEntryDialog(expense);
                    }
                }

                @Override
                public void onItemLongClick(Expense expense, int iChild) {
                    if (ExpenseHeaderAdapter.this.mFragment instanceof HomeFragment) {
                        ((HomeFragment) ExpenseHeaderAdapter.this.mFragment).deleteTransactionEntry(expense, i, iChild,expenseChildAdapter0);
                        Log.e(TAG, "onItemLongClick: " + i + " " + iChild);
                    }
                }
            });
            childRecyclerView.setAdapter(expenseChildAdapter0);
        }
    }
}