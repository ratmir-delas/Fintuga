package com.tugasoft.fintuga.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;
import com.tugasoft.fintuga.utils.MySharedPreferences;

import java.util.ArrayList;
import java.util.Map;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.MainViewHolder> {
    private final ArrayList modelList;
    private final LayoutInflater inflater;
    private final Context mContext;
    private int mTransactionTypeImage;

    public ChartAdapter(Context context, int i, Map<String, Double> map) {
        this.inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mTransactionTypeImage = i;
        ArrayList arrayList = new ArrayList<>();
        this.modelList = arrayList;
        arrayList.addAll(map.entrySet());
    }

    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MainViewHolder(this.inflater.inflate(R.layout.row_chart_transaction_detail, viewGroup, false));
    }

    public void onBindViewHolder(MainViewHolder mainViewHolder, int i) {
        int i2;
        Map.Entry entry = (Map.Entry) this.modelList.get(i);
        double doubleValue = (Double) entry.getValue();
        String[] split = ((String) entry.getKey()).split(",");
        String str = split[0];
        String str2 = split[1];
        GradientDrawable gradientDrawable = (GradientDrawable) ((LayerDrawable) mainViewHolder.ll_root.getBackground()).findDrawableByLayerId(R.id.outerRectangle);
        if (str2 != null) {
            i2 = Color.parseColor(str2);
        } else {
            i2 = this.mContext.getResources().getColor(R.color.colorAccent);
        }

        //mTransactionTypeImage == Constant.TYPE_INCOME

        gradientDrawable.setColor(i2);
        gradientDrawable.setStroke(3, i2);
        gradientDrawable.setColor(Color.parseColor("#FFFFFF"));
        mainViewHolder.iv.setImageTintMode(PorterDuff.Mode.SRC_IN);
        mainViewHolder.iv.setColorFilter(i2);
        mainViewHolder.tv_exp_category.setTextColor(i2);
        mainViewHolder.tv_exp_amount.setTextColor(i2);

        mainViewHolder.iv.setImageResource(this.mTransactionTypeImage);
        mainViewHolder.tv_exp_category.setText(str);
        mainViewHolder.tv_exp_amount.setText(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" ").concat(CommonMethod.formatPrice((double) doubleValue)));
    }

    public int getItemCount() {
        return this.modelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addData(int i, Map<String, Double> map) {
        ArrayList arrayList = this.modelList;
        if (arrayList != null) {
            arrayList.clear();
        }
        this.modelList.addAll(map.entrySet());
        this.mTransactionTypeImage = i;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearData() {
        ArrayList arrayList = this.modelList;
        if (arrayList != null) {
            arrayList.clear();
        }
        notifyDataSetChanged();
    }

    class MainViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        LinearLayout ll_root;
        TextView tv_exp_amount;
        TextView tv_exp_category;

        MainViewHolder(View view) {
            super(view);
            this.ll_root = (LinearLayout) view.findViewById(R.id.rl_root);
            this.iv = (ImageView) view.findViewById(R.id.iv);
            this.tv_exp_category = (TextView) view.findViewById(R.id.tv_exp_category);
            this.tv_exp_amount = (TextView) view.findViewById(R.id.tv_exp_amount);
        }
    }
}