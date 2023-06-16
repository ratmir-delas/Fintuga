package com.tugasoft.fintuga.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatePickerHelper {
    public static final String dateFormat = Constant.DATE_FORMAT;

    public final Calendar calendar = Calendar.getInstance();
    private final int mSelectedMonth;

    public TextView textView = null;

    public DatePickerHelper(final Context context, TextView textView2, int i) {
        this.textView = textView2;
        this.mSelectedMonth = i;
        if (textView2 != null) {
            textView2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    DatePickerHelper.this.getDatePickerDialog(context).show();
                }
            });
        }
    }

    private DatePickerDialog.OnDateSetListener getOnDateSetListener() {
        return new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                DatePickerHelper.this.calendar.set(1, i);
                DatePickerHelper.this.calendar.set(2, i2);
                DatePickerHelper.this.calendar.set(5, i3);
                DatePickerHelper.this.textView.setText(new SimpleDateFormat(DatePickerHelper.dateFormat, Locale.US).format(DatePickerHelper.this.calendar.getTime()));
            }
        };
    }


    public DatePickerDialog getDatePickerDialog(Context context) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, getOnDateSetListener(), this.calendar.get(1), this.mSelectedMonth, this.calendar.get(5));
        Calendar instance = Calendar.getInstance();
        int i = instance.get(2);
        int i2 = this.mSelectedMonth;
        if (i == i2) {
            datePickerDialog.getDatePicker().setMaxDate(instance.getTimeInMillis());
            instance.set(5, 1);
            datePickerDialog.getDatePicker().setMinDate(instance.getTimeInMillis());
        } else {
            instance.set(2, i2);
            instance.set(5, 1);
            datePickerDialog.getDatePicker().setMinDate(instance.getTimeInMillis());
            instance.set(5, instance.getActualMaximum(5));
            datePickerDialog.getDatePicker().setMaxDate(instance.getTimeInMillis());
        }
        return datePickerDialog;
    }
}
