package com.tugasoft.fintuga.currencyConverter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tugasoft.fintuga.R;

import java.util.List;

public class RateRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    LayoutInflater inflater;
    private String baseCurrency;
    private Context context;
    private List<Integer> currencyImageList;
    private List<String> currencyNameList;
    private List<String> currencyRateList;
    private List<String> currencySymbolList;
    private List<String> currencyValueList;

    public RateRecyclerAdapter(Context context2, String str, List<String> list, List<String> list2, List<String> list3, List<String> list4, List<Integer> list5) {
        this.context = context2;
        this.baseCurrency = str;
        this.currencySymbolList = list;
        this.currencyNameList = list2;
        this.currencyRateList = list3;
        this.currencyValueList = list4;
        this.currencyImageList = list5;
        this.inflater = LayoutInflater.from(context2);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new RateViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_currency_list_item, viewGroup, false));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        RateViewHolder rateViewHolder = (RateViewHolder) viewHolder;
        rateViewHolder.currencySymbol.setText(this.currencySymbolList.get(i));
        rateViewHolder.currencyName.setText(this.currencyNameList.get(i));
        rateViewHolder.currencyRate.setText(this.currencyRateList.get(i));
        rateViewHolder.currencyValue.setText(this.context.getResources().getString(R.string.currency_rate, new Object[]{this.baseCurrency, this.currencyValueList.get(i), this.currencySymbolList.get(i)}));
    }

    public int getItemCount() {
        return this.currencyNameList.size();
    }

    public void FilterDataByCurrencyName(List<String> modelData) {
        currencyNameList = modelData;
        notifyDataSetChanged();
    }

    public void FilterDataByCurrencyRate(List<String> modelData) {
        currencyRateList = modelData;
        notifyDataSetChanged();
    }
}
