package com.tugasoft.fintuga.fragments;

import android.animation.Animator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tugasoft.fintuga.activity.ReportActivity;
import com.tugasoft.fintuga.adapters.ExpenseHeaderAdapter;
import com.tugasoft.fintuga.models.MasterExpenseModel;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;
import com.tugasoft.fintuga.utils.MySharedPreferences;
import com.tugasoft.fintuga.ads.AdsProvider;

import java.util.ArrayList;



public class ReportsInDetailsFragment extends Fragment {
    private FrameLayout adContainerView;

    private long mTotalBalance = 0;
    private long mTotalExpense = 0;
    private long mTotalIncome = 0;
    private ArrayList<MasterExpenseModel> mTransactionList;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_reports_in_details, (ViewGroup) null);
        inflate.setOnTouchListener((view, motionEvent) -> true);
        inflate.setBackgroundColor(ContextCompat.getColor(requireActivity(), android.R.color.white));
        inflate.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                view.removeOnLayoutChangeListener(this);
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(view, 20, 20, 0.0f, (float) ((int) Math.hypot((double) i3, (double) i4)));
                createCircularReveal.setInterpolator(new DecelerateInterpolator(2.0f));
                createCircularReveal.setDuration(1000);
                createCircularReveal.start();
            }
        });

            this.adContainerView = (FrameLayout) inflate.findViewById(R.id.ad_view_container);

        return inflate;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (getArguments() != null) {
            Bundle arguments = getArguments();
            this.mTotalIncome = arguments.getLong("Income");
            this.mTotalExpense = arguments.getLong("Expense");
            this.mTotalBalance = arguments.getLong("Balance");
            this.mTransactionList = (ArrayList) arguments.getSerializable("TransactionList");
        } else {
            ((ReportActivity) getActivity()).onBackPressed();
        }
        init();

        AdsProvider.getInstance().addBanner(getActivity(), adContainerView);

    }

    private void init() {
        TextView textView = (TextView) getView().findViewById(R.id.tv_total_income);
        TextView textView2 = (TextView) getView().findViewById(R.id.tv_total_expense);
        TextView textView3 = (TextView) getView().findViewById(R.id.tv_total_saving);
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.expensesListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new ExpenseHeaderAdapter(this, R.layout.row_layout, this.mTransactionList, new ExpenseHeaderAdapter.OnItemClickListener() {
            public void onItemLongClick(MasterExpenseModel masterExpenseModel, int i) {
            }
        }));
        textView.setText(String.valueOf(this.mTotalIncome));
        textView2.setText(String.valueOf(this.mTotalExpense));
        textView3.setText(String.valueOf(this.mTotalBalance));
        textView.setText(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" ").concat(CommonMethod.formatPrice((double) this.mTotalIncome)));
        textView2.setText(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" ").concat(CommonMethod.formatPrice((double) this.mTotalExpense)));
        textView3.setText(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" ").concat(CommonMethod.formatPrice((double) this.mTotalBalance)));
    }

}
