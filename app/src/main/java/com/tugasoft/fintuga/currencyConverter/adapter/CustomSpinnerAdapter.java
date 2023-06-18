package com.tugasoft.fintuga.currencyConverter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tugasoft.fintuga.R;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    String[] currency_full_name;
    private Context context;
    private String[] currency_symbols;

    public CustomSpinnerAdapter(Context context2, String[] strArr, String[] strArr2) {
        super(context2, R.layout.layout_spinner_items, strArr);
        this.context = context2;
        this.currency_symbols = strArr;
        this.currency_full_name = strArr2;
    }

    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        View inflate = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_spinner_items, (ViewGroup) null);
        ((TextView) inflate.findViewById(R.id.spinner_symbol)).setText(this.currency_symbols[i] + " - " + this.currency_full_name[i]);
        return inflate;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflate = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_spinner_items, (ViewGroup) null);
        ((TextView) inflate.findViewById(R.id.spinner_symbol)).setText(this.currency_symbols[i] + " - " + this.currency_full_name[i]);
        return inflate;
    }
}
