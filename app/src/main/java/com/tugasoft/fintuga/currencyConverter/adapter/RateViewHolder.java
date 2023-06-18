package com.tugasoft.fintuga.currencyConverter.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tugasoft.fintuga.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class RateViewHolder extends RecyclerView.ViewHolder {
    CircleImageView currencyImage;
    TextView currencyName;
    TextView currencyRate;
    TextView currencySymbol;
    TextView currencyValue;

    public RateViewHolder(View view) {
        super(view);
        this.currencySymbol = (TextView) view.findViewById(R.id.currency_symbol);
        this.currencyName = (TextView) view.findViewById(R.id.currency_name);
        this.currencyRate = (TextView) view.findViewById(R.id.currency_converted);
        this.currencyValue = (TextView) view.findViewById(R.id.currency_value);
    }
}
