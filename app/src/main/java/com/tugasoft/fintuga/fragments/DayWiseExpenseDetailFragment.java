package com.tugasoft.fintuga.fragments;

import android.animation.Animator;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tugasoft.fintuga.activity.DashboardActivity;
import com.tugasoft.fintuga.adapters.CalenderDayWiseHistoryAdapter;
import com.tugasoft.fintuga.adapters.MyViewPagerAdapter;
import com.tugasoft.fintuga.models.Expense;
import com.tugasoft.fintuga.models.MasterExpenseModel;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;
import com.tugasoft.fintuga.utils.MySharedPreferences;
import com.tugasoft.fintuga.ads.AdsProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;



public class DayWiseExpenseDetailFragment extends DialogFragment {
    private MasterExpenseModel mExpenseDetailModel;
    private String mSelectedDate;

    public static DayWiseExpenseDetailFragment newInstance(Bundle bundle) {
        DayWiseExpenseDetailFragment dayWiseExpenseDetailFragment = new DayWiseExpenseDetailFragment();
        dayWiseExpenseDetailFragment.setArguments(bundle);
        return dayWiseExpenseDetailFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(-1, -1);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View mView = layoutInflater.inflate(R.layout.fragment_day_wise_expense_detail, viewGroup, false);
        if (getArguments() != null) {
            this.mSelectedDate = getArguments().getString("SelectedDate");
            this.mExpenseDetailModel = (MasterExpenseModel) getArguments().getSerializable("ExpenseDetailModel");
        } else {
            getActivity().finish();
        }
        mView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorLightGray));
        mView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                view.removeOnLayoutChangeListener(this);
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(view, DayWiseExpenseDetailFragment.this.getArguments().getInt("cx"), DayWiseExpenseDetailFragment.this.getArguments().getInt("cy"), 0.0f, (float) ((int) Math.hypot((double) i3, (double) i4)));
                createCircularReveal.setInterpolator(new DecelerateInterpolator(2.0f));
                createCircularReveal.setDuration(500);
                createCircularReveal.start();
            }
        });
        setToolbar(mView);
        loadAllViews(mView);
        return mView;
    }

    private void setToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(this.mSelectedDate);
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));
        toolbar.setNavigationIcon(R.drawable.ic_cancle);
        toolbar.setNavigationOnClickListener(view1 -> {
            if (DayWiseExpenseDetailFragment.this.getActivity() != null && (DayWiseExpenseDetailFragment.this.getActivity() instanceof DashboardActivity)) {
                ((DashboardActivity) DayWiseExpenseDetailFragment.this.getActivity()).dismissViewTransactionEntryDetailsDialog(view1.getX(), view1.getY());
            }
        });
    }

    private void loadAllViews(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rvTimesheetHistory);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab_view_proof);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ((TextView) view.findViewById(R.id.tv_day_wise_total_income)).setText(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" ").concat(CommonMethod.formatPrice(this.mExpenseDetailModel.getTotalIncome())));
        ((TextView) view.findViewById(R.id.tv_day_wise_total_expense)).setText(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" ").concat(CommonMethod.formatPrice(this.mExpenseDetailModel.getTotalExpense())));
        ((TextView) view.findViewById(R.id.tv_day_wise_total_saving)).setText(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" ").concat(CommonMethod.formatPrice(this.mExpenseDetailModel.getTotalIncome() - this.mExpenseDetailModel.getTotalExpense())));
        recyclerView.setAdapter(new CalenderDayWiseHistoryAdapter(getActivity(), this.mExpenseDetailModel.getExpenses()));
        if (!this.mExpenseDetailModel.isHavingProofImage()) {
            floatingActionButton.hide();
        }
        ArrayList<Expense> detailList = this.mExpenseDetailModel.getExpenses();
        final ArrayList arrayList = new ArrayList();
        Iterator<Expense> it = detailList.iterator();
        while (it.hasNext()) {
            Expense next = it.next();
            if (next.getProofUri() != null && next.getProofUri().trim().length() > 0) {
                arrayList.add(next);
            }
        }
        floatingActionButton.setOnClickListener(view1 -> {
            View inflate = LayoutInflater.from(DayWiseExpenseDetailFragment.this.getActivity()).inflate(R.layout.dialog_display_proof_image, (ViewGroup) DayWiseExpenseDetailFragment.this.getActivity().findViewById(android.R.id.content), false);
            ((ViewPager) inflate.findViewById(R.id.masterViewPager)).setAdapter(new MyViewPagerAdapter(DayWiseExpenseDetailFragment.this.getActivity(), arrayList));
            AlertDialog.Builder builder = new AlertDialog.Builder(DayWiseExpenseDetailFragment.this.getActivity());
            builder.setView(inflate);
            AlertDialog create = builder.create();
            create.setCancelable(true);
            create.setCanceledOnTouchOutside(true);
            create.show();
        });
        FrameLayout frameLayout = view.findViewById(R.id.ad_view_container);
        AdsProvider.getInstance().addBanner(getActivity(), frameLayout);
    }

    public Animator prepareUnrevealAnimator(float f, float f2) {
        int i = (int) f;
        int i2 = (int) f2;
        Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(getView(), i, i2, (float) getEnclosingCircleRadius(getView(), i, i2), 0.0f);
        createCircularReveal.setInterpolator(new AccelerateInterpolator(2.0f));
        createCircularReveal.setDuration(500);
        return createCircularReveal;
    }

    private int getEnclosingCircleRadius(View view, int i, int i2) {
        int left = i + view.getLeft();
        int top = i2 + view.getTop();
        return Collections.max(Arrays.asList((int) Math.hypot(left - view.getLeft(), top - view.getTop()), (int) Math.hypot(view.getRight() - left, top - view.getTop()), (int) Math.hypot(left - view.getLeft(), view.getBottom() - top), (int) Math.hypot(view.getRight() - left, view.getBottom() - top)));
    }
}
