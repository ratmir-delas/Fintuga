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
import androidx.fragment.app.Fragment;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Statement extends Fragment {
    Button Share;
    String fmonth, tmonth, appLink;
    Button cancelButton;
    int fomonth, fyear;
    OnMessageReadListner onMessageReadListner;
    double monthlyemi, principal, principia, interestPerMonth, balance, time, totalInterest, actualInterest;
    int tdate, tomonth, tyear;

    public Statement(double d, double d2, double d3, double d4, double d5, int i, int i2, int i3) {
        double d6 = d;
        double d7 = d3;
        int i4 = i2;
        this.time = d6;
        this.interestPerMonth = d2;
        this.principal = d7;
        this.principia = d7;
        this.monthlyemi = d4;
        this.totalInterest = d5;
        this.tdate = i;
        this.tomonth = i4;
        this.tyear = i3;

        tmonth = CommonMethod.getShortMonthByNumber(getActivity(), i4);

        int i5 = (int) (((double) i4) + d6);
        int i6 = (i5 - 1) / 12;
        this.fyear = i3 + i6;
        int i7 = i5 - (i6 * 12);
        this.fomonth = i7;
        fmonth = CommonMethod.getShortMonthByNumber(getActivity(), i7);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        final View inflate = layoutInflater.inflate(R.layout.statistics, viewGroup, false);
        this.cancelButton = (Button) inflate.findViewById(R.id.cancel_action);
        this.Share = (Button) inflate.findViewById(R.id.Share);
        LinearLayout linearLayout = (LinearLayout) inflate.findViewById(R.id.months);
        LinearLayout linearLayout2 = (LinearLayout) inflate.findViewById(R.id.Interest);
        LinearLayout linearLayout3 = (LinearLayout) inflate.findViewById(R.id.Balance);
        LinearLayout linearLayout4 = (LinearLayout) inflate.findViewById(R.id.principal);

        int i = 1;
        while (((double) i) <= this.time) {
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(-1, 60));
            textView.setText(String.valueOf(i));
            textView.setGravity(17);
            linearLayout.addView(textView);
            this.actualInterest = this.interestPerMonth * this.principal;
            TextView textView2 = new TextView(getContext());
            textView2.setLayoutParams(new ViewGroup.LayoutParams(-1, 60));
            textView2.setGravity(17);
            linearLayout2.addView(textView2);
            int i2 = i;
            this.balance = this.monthlyemi - this.actualInterest;
            TextView textView3 = new TextView(getContext());
            textView3.setLayoutParams(new ViewGroup.LayoutParams(-1, 60));
            textView3.setGravity(17);
            linearLayout4.addView(textView3);
            this.principal -= this.balance;
            TextView textView4 = new TextView(getContext());
            textView4.setLayoutParams(new ViewGroup.LayoutParams(-1, 60));
            textView4.setGravity(17);
            linearLayout3.addView(textView4);
            if (i2 % 2 == 0) {
                textView.setBackgroundColor(Color.parseColor("#F1DED0"));
                textView4.setBackgroundColor(Color.parseColor("#F1DED0"));
                textView3.setBackgroundColor(Color.parseColor("#F1DED0"));
                textView2.setBackgroundColor(Color.parseColor("#F1DED0"));
            } else {
                textView.setBackgroundColor(Color.parseColor("#F8F4F4"));
                textView4.setBackgroundColor(Color.parseColor("#F8F4F4"));
                textView3.setBackgroundColor(Color.parseColor("#F8F4F4"));
                textView2.setBackgroundColor(Color.parseColor("#F8F4F4"));
            }
            double d = this.actualInterest;
            if (d == ((double) ((int) d))) {
                textView2.setText(String.valueOf(Integer.valueOf((int) d)));
            } else {
                double doubleValue = new BigDecimal(this.actualInterest).setScale(1, RoundingMode.HALF_UP).doubleValue();
                this.actualInterest = doubleValue;
                textView2.setText(String.valueOf(doubleValue));
            }
            double d2 = this.balance;
            if (d2 == ((double) ((int) d2))) {
                textView3.setText(String.valueOf(Integer.valueOf((int) d2)));
            } else {
                double doubleValue2 = new BigDecimal(this.balance).setScale(1, RoundingMode.HALF_UP).doubleValue();
                this.balance = doubleValue2;
                textView3.setText(String.valueOf(doubleValue2));
            }
            double d3 = this.principal;
            if (d3 == ((double) ((int) d3))) {
                textView4.setText(String.valueOf(Integer.valueOf((int) d3)));
            } else {
                double doubleValue3 = new BigDecimal(this.principal).setScale(1, RoundingMode.HALF_UP).doubleValue();
                this.principal = doubleValue3;
                textView4.setText(String.valueOf(doubleValue3));
            }
            i = i2 + 1;
        }
        this.cancelButton.setOnClickListener(view -> {
            Statement.this.onMessageReadListner.onMessage();
            Statement.this.getFragmentManager().popBackStack();
        });
        this.Share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.SUBJECT", "EMI- A Financial Calculator");
                intent.putExtra("android.intent.extra.TEXT", "EMI Details-\n \nPrincipal Loan Amount: " + Statement.this.principia + "\nLoan term: " + Statement.this.time + "\nFirst EMI at: " + Statement.this.tdate + " " + Statement.this.tmonth + " " + Statement.this.tyear + "\n\nMonthly EMI: " + Statement.this.monthlyemi + "\nTotal Interest: " + Statement.this.totalInterest + "\nTotal payment: " + (Statement.this.totalInterest + Statement.this.principal) + "\nLast Loan Date: " + Statement.this.tdate + " " + Statement.this.fmonth + " " + Statement.this.fyear + "\n\nCalculate by EMI\n" + Statement.this.appLink);
                Statement.this.startActivity(Intent.createChooser(intent, "Share Using"));
            }
        });
        return inflate;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.onMessageReadListner = (OnMessageReadListner) ((Activity) context);
    }

    public interface OnMessageReadListner {
        void onMessage();
    }
}