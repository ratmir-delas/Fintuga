package com.tugasoft.fintuga.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.ads.AdsProvider;
import com.tugasoft.fintuga.utils.CommonMethod;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;


public class Statementsip extends Fragment {
    Button Share;
    TextView title;
    Button cancel;
    OnMessageReadListener onMessageReadListener;
    String appLink;
    String finalMonth;
    String thisMonth;
    String what;
    int fomonth;
    int finalYear;
    int thisDate;
    int tomonth;
    int thisYear;
    double balance;
    double actualInterest;
    double interestPerMonth;
    double maturityValue;
    double principal;
    double principia;
    double totalInterest;
    double time;

    public Statementsip(double d, double tenture, double expectedRate, double investmentAmount, double maturityValue, double totalInterest, int day, int month, int year, String str, Context context) {
        this.time = tenture;
        this.what = str;
        this.interestPerMonth = expectedRate;
        this.principal = investmentAmount;
        if (Objects.equals(str, "SIP") || Objects.equals(str, "RD")) {
            this.principia = investmentAmount;
            this.balance = investmentAmount;
        } else if (Objects.equals(str, "SWP")) {
            this.principia = 0.0 - d;
            this.balance = investmentAmount;
        } else {
            this.balance = investmentAmount;
            this.principia = 0.0;
        }
        this.maturityValue = maturityValue;
        this.totalInterest = totalInterest;
        this.thisDate = day;
        this.tomonth = month;
        this.thisYear = year;
        thisMonth = CommonMethod.getShortMonthByNumber(context, month);
        int i6 = (int) (((double) month) + tenture);
        int i7 = (i6 - 1) / 12;
        this.finalYear = year + i7;
        int i8 = i6 - (i7 * 12);
        this.fomonth = i8;
        finalMonth = CommonMethod.getShortMonthByNumber(context, i8);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        String str;
        final View inflate = layoutInflater.inflate(R.layout.statementsip, viewGroup, false);
        this.cancel = (Button) inflate.findViewById(R.id.cancel_action);
        this.Share = (Button) inflate.findViewById(R.id.Share);
        TextView textView = (TextView) inflate.findViewById(R.id.which);
        this.title = textView;
        textView.setText(this.what);

        LinearLayout linearLayout = (LinearLayout) inflate.findViewById(R.id.months);
        LinearLayout linearLayout2 = (LinearLayout) inflate.findViewById(R.id.Balance);
        LinearLayout linearLayout3 = (LinearLayout) inflate.findViewById(R.id.interest);
        AdsProvider.getInstance().addBanner(getActivity(), linearLayout);
        AdsProvider.getInstance().addBanner(getActivity(), linearLayout2);
        AdsProvider.getInstance().addBanner(getActivity(), linearLayout3);

        int i = 1;
        while (true) {
            double d = i;
            if (d <= this.time) {
                TextView textView2 = new TextView(getContext());
                textView2.setLayoutParams(new ViewGroup.LayoutParams(-1, 60));
                textView2.setText(String.valueOf(i));
                textView2.setGravity(17);
                linearLayout.addView(textView2);
                int i2 = i;
                this.actualInterest = this.interestPerMonth * this.balance;
                TextView textView3 = new TextView(getContext());
                textView3.setLayoutParams(new ViewGroup.LayoutParams(-1, 60));
                textView3.setGravity(17);
                double d2 = this.actualInterest;
                if (d2 == ((double) ((int) d2))) {
                    textView3.setText(String.valueOf(Integer.valueOf((int) d2)));
                } else {
                    textView3.setText(String.valueOf(new BigDecimal(this.actualInterest).setScale(1, RoundingMode.HALF_UP).doubleValue()));
                }
                linearLayout3.addView(textView3);
                if (d == this.time && ((str = this.what) == "SIP" || str == "RD")) {
                    this.principia = 0.0;
                }
                this.balance = this.balance + this.actualInterest + this.principia;
                TextView textView4 = new TextView(getContext());
                textView4.setLayoutParams(new ViewGroup.LayoutParams(-1, 60));
                textView4.setGravity(17);
                linearLayout2.addView(textView4);
                double d3 = this.balance;
                if (d3 == ((double) ((int) d3))) {
                    textView4.setText(String.valueOf(Integer.valueOf(((int) d3) + ((int) this.principia))));
                } else {
                    textView4.setText(String.valueOf(new BigDecimal(this.balance).setScale(1, RoundingMode.HALF_UP).doubleValue()));
                }
                if (i2 % 2 == 0) {
                    textView2.setBackgroundColor(Color.parseColor("#F1DED0"));
                    textView4.setBackgroundColor(Color.parseColor("#F1DED0"));
                    textView3.setBackgroundColor(Color.parseColor("#F1DED0"));
                } else {
                    textView2.setBackgroundColor(Color.parseColor("#F8F4F4"));
                    textView4.setBackgroundColor(Color.parseColor("#F8F4F4"));
                    textView3.setBackgroundColor(Color.parseColor("#F8F4F4"));
                }
                i = i2 + 1;
            } else {
                this.cancel.setOnClickListener(view -> {
                    onMessageReadListener.onMessage();
                    assert getFragmentManager() != null;
                    getFragmentManager().popBackStack();
                });
                this.Share.setOnClickListener(view -> {
                    Double d1;
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("text/plain");
                    if (Objects.equals(what, "SIP") || Objects.equals(what, "RD")) {
                        d1 = principal * time;
                    } else {
                        d1 = principal;
                    }
                    intent.putExtra("android.intent.extra.SUBJECT", "EMI- A Calculator app");
                    intent.putExtra("android.intent.extra.TEXT", what + "Details-\n\nInvestment Amount : " + principal + "\nTenure : " + time + "months\nFirst " + what + ": " + thisDate + " " + thisMonth + " " + thisYear + "\n\nTotal Investment Amount: " + d1 + "\nTotal Interest: " + totalInterest + "\nMaturity Value: " + (totalInterest + principal) + "\nMaturity Date: " + thisDate + " " + finalMonth + " " + finalYear + "\n\nCalculate by EMI\n" + appLink);
                    startActivity(Intent.createChooser(intent, "Share Using"));
                });
                return inflate;
            }
        }
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.onMessageReadListener = (OnMessageReadListener) context;
    }

    public interface OnMessageReadListener {
        void onMessage();
    }
}
