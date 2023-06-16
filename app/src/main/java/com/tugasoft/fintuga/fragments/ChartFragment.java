package com.tugasoft.fintuga.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tugasoft.fintuga.activity.DashboardActivity;
import com.tugasoft.fintuga.adapters.ChartAdapter;
import com.tugasoft.fintuga.models.Category;
import com.tugasoft.fintuga.models.Expense;
import com.tugasoft.fintuga.models.MasterExpenseModel;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;
import com.tugasoft.fintuga.utils.MySharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class ChartFragment extends Fragment {
    public static int ie = 0;
    public PieChartData pieChartData;
    public PieChartView pieChartView_;
    Map<String, Double> hashMap = new HashMap();
    LinearLayout mLlDataFound;
    LinearLayout mLlNoDataFound;
    private ChartAdapter mAdapter;
    private View view;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_chart, (ViewGroup) null);
        view = inflate;
        return inflate;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        mLlDataFound = view.findViewById(R.id.ll_data_found);
        mLlNoDataFound = view.findViewById(R.id.ll_no_data_found);
        pieChartView_ = view.findViewById(R.id.piechart_);
        RecyclerView recyclerView = view.findViewById(R.id.chartListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ChartAdapter chartAdapter = new ChartAdapter(getActivity(), R.drawable.img_expense, hashMap);
        mAdapter = chartAdapter;
        recyclerView.setAdapter(chartAdapter);
        ((DashboardActivity) requireActivity()).switchToggleBackGround(true);
    }

    public void resetData() {
        hashMap.clear();
        ArrayList arrayList = new ArrayList();
        pieChartView_.setValueSelectionEnabled(false);
        PieChartData pieChartData2 = new PieChartData((List<SliceValue>) arrayList);
        pieChartData = pieChartData2;
        pieChartData2.setHasLabels(true);
        pieChartData.setValueLabelTextSize(10);
        pieChartData.setHasLabelsOutside(true);
        pieChartData.setHasCenterCircle(true);
        pieChartData.setCenterText1FontSize(16);
        pieChartData.setCenterText1Color(ContextCompat.getColor(requireActivity(), android.R.color.black));
        pieChartView_.setPieChartData(pieChartData);
        ChartAdapter chartAdapter = mAdapter;
        if (chartAdapter != null) {
            chartAdapter.clearData();
        }
        if (hashMap.isEmpty()) {
            mLlNoDataFound.setVisibility(View.VISIBLE);
            mLlDataFound.setVisibility(View.GONE);
        }
    }

    public void getData(boolean isExpanse) {
        Log.e("TAG", "getData: " + isExpanse);
        hashMap.clear();
        ArrayList<MasterExpenseModel> arrayList = ((DashboardActivity) requireActivity()).mTransactionList;
        if (arrayList != null && arrayList.size() > 0) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                Iterator<Expense> iterator = arrayList.get(i).getExpenses().iterator();
                while (iterator.hasNext()) {
                    Expense expense = iterator.next();
                    //Category category = expense.getCategory();

                    if (expense.isExpense() == isExpanse) {
                        if (!hashMap.containsKey(expense.getCategory() + "," + expense.getColor())) {
                            hashMap.put(expense.getCategory() + "," + expense.getColor(), expense.getAmount());
                        } else {
                            hashMap.put(expense.getCategory() + "," + expense.getColor(), hashMap.get(expense.getCategory() + "," + expense.getColor()) + expense.getAmount());
                        }
                    }
                }
            }
        }
        if (mAdapter != null) {
            hashMap = CommonMethod.sortByComparator(hashMap, false);
            mAdapter.addData(!isExpanse ? R.drawable.img_income : R.drawable.img_expense, hashMap);
        }
        if (hashMap.isEmpty()) {
            mLlNoDataFound.setVisibility(View.VISIBLE);
            mLlDataFound.setVisibility(View.GONE);
            return;
        }
        mLlNoDataFound.setVisibility(View.GONE);
        mLlDataFound.setVisibility(View.VISIBLE);
        displayPieChart_(isExpanse);
    }

    private void displayPieChart_(boolean isExpanse) {
        ArrayList arrayList = new ArrayList<>();
        for (String next : hashMap.keySet()) {
            String[] split = next.split(",");
            arrayList.add(new SliceValue((float) hashMap.get(next).longValue(), Color.parseColor(split[1])).setLabel(split[0]));
        }
        pieChartView_.setValueSelectionEnabled(false);
        PieChartData pieChartData2 = new PieChartData((List<SliceValue>) arrayList);
        pieChartData = pieChartData2;
        pieChartData2.setHasLabels(true);
        pieChartData.setValueLabelTextSize(10);
        pieChartData.setHasLabelsOutside(true);
        pieChartData.setHasCenterCircle(true);
        pieChartData.setCenterText1FontSize(16);
        pieChartData.setCenterText1Color(ContextCompat.getColor(requireActivity(), android.R.color.black));
        if (!isExpanse) {
            PieChartData pieChartData3 = pieChartData;
            pieChartData3.setCenterText1(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "") + " " + CommonMethod.formatPrice((double) ((DashboardActivity) getActivity()).totalIncome));
        } else {
            PieChartData pieChartData4 = pieChartData;
            pieChartData4.setCenterText1(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "") + " " + CommonMethod.formatPrice((double) ((DashboardActivity) getActivity()).totalExpense));
        }
        pieChartView_.setPieChartData(pieChartData);
    }

    public void onDetach() {
        super.onDetach();
        System.gc();
    }
}
