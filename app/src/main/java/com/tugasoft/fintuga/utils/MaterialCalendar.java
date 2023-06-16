package com.tugasoft.fintuga.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import com.tugasoft.fintuga.activity.DashboardActivity;
import com.tugasoft.fintuga.fragments.CalendarFragment;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class MaterialCalendar {
    public static Activity mActivity = null;
    public static int mCurrentDay = -1;
    public static int mCurrentMonth = -1;
    public static int mCurrentYear = -1;
    public static int mFirstDay = -1;
    public static int mMonth = -1;
    public static int mNumDaysInMonth = -1;
    public static int mYear = -1;
    private static Fragment mFragment = null;

    public static void getInitialCalendarInfo(Fragment fragment, int i, int i2) {
        mFragment = fragment;
        mActivity = fragment.getActivity();
        Calendar instance = Calendar.getInstance();
        instance.set(2, i);
        instance.set(1, i2);
        mNumDaysInMonth = instance.getActualMaximum(5);
        mMonth = instance.get(2);
        mYear = instance.get(1);
        mCurrentDay = instance.get(5);
        int i3 = mMonth;
        mCurrentMonth = i3;
        int i4 = mYear;
        mCurrentYear = i4;
        getFirstDay(i3, i4);
    }

    private static void getFirstDay(int i, int i2) {
        Calendar instance = Calendar.getInstance();
        if (instance != null) {
            instance.set(2, i);
            instance.set(1, i2);
            instance.set(5, 1);
            switch (instance.get(7)) {
                case 1:
                    Log.d("FIRST_DAY", "Sunday");
                    mFirstDay = 0;
                    return;
                case 2:
                    Log.d("FIRST_DAY", "Monday");
                    mFirstDay = 1;
                    return;
                case 3:
                    Log.d("FIRST_DAY", "Tuesday");
                    mFirstDay = 2;
                    return;
                case 4:
                    Log.d("FIRST_DAY", "Wednesday");
                    mFirstDay = 3;
                    return;
                case 5:
                    Log.d("FIRST_DAY", "Thursday");
                    mFirstDay = 4;
                    return;
                case 6:
                    Log.d("FIRST_DAY", "Friday");
                    mFirstDay = 5;
                    return;
                case 7:
                    Log.d("FIRST_DAY", "Saturday");
                    mFirstDay = 6;
                    return;
                default:
                    return;
            }
        }
    }

    public static void refreshCalendar(GridView gridView, MaterialCalendarAdapter materialCalendarAdapter, int i, int i2) {
        int i3;
        checkCurrentDay(i, i2);
        getNumDayInMonth(i, i2);
        getFirstDay(i, i2);
        Fragment fragment = mFragment;
        if (fragment instanceof CalendarFragment) {
            CalendarFragment calendarFragment = (CalendarFragment) fragment;
            CalendarFragment.mNumEventsOnDay = -1;
            ((DashboardActivity) mActivity).getDetails();
            ((DashboardActivity) mActivity).resetAmountData();
            if (materialCalendarAdapter != null) {
                if (gridView != null) {
                    int i4 = mCurrentDay;
                    if (i4 == -1 || (i3 = mFirstDay) == -1) {
                        gridView.setItemChecked(gridView.getCheckedItemPosition(), false);
                    } else {
                        gridView.setItemChecked(i3 + 6 + i4, true);
                    }
                }
                materialCalendarAdapter.notifyDataSetChanged();
            }
        }
    }

    private static String getMonthName(int i) {
        return new DateFormatSymbols().getMonths()[i];
    }

    private static void checkCurrentDay(int i, int i2) {
        if (i == mCurrentMonth && i2 == mCurrentYear) {
            mCurrentDay = Calendar.getInstance().get(5);
        } else {
            mCurrentDay = -1;
        }
    }

    private static void getNumDayInMonth(int i, int i2) {
        Calendar instance = Calendar.getInstance();
        if (instance != null) {
            instance.set(2, i);
            instance.set(1, i2);
            mNumDaysInMonth = instance.getActualMaximum(5);
            Log.d("MONTH_NUMBER", String.valueOf(instance.getActualMaximum(5)));
        }
    }

    public static void previousOnClick(int i, int i2, GridView gridView, MaterialCalendarAdapter materialCalendarAdapter) {
        if (mMonth != -1 && mYear != -1) {
            previousMonth(i, i2, gridView, materialCalendarAdapter);
        }
    }

    public static void nextOnClick(int i, int i2, GridView gridView, MaterialCalendarAdapter materialCalendarAdapter) {
        if (mMonth != -1 && mYear != -1) {
            nextMonth(i, i2, gridView, materialCalendarAdapter);
        }
    }

    private static void previousMonth(int i, int i2, GridView gridView, MaterialCalendarAdapter materialCalendarAdapter) {
        mMonth = i;
        mYear = i2;
        refreshCalendar(gridView, materialCalendarAdapter, i, i2);
    }

    private static void nextMonth(int i, int i2, GridView gridView, MaterialCalendarAdapter materialCalendarAdapter) {
        mMonth = i;
        mYear = i2;
        refreshCalendar(gridView, materialCalendarAdapter, i, i2);
    }

    public static void selectCalendarDay(MaterialCalendarAdapter materialCalendarAdapter, int i) {
        Log.d("SELECTED_POSITION", String.valueOf(i));
        if (i > mFirstDay + 6) {
            getSelectedDate(i, mMonth, mYear);
            if (materialCalendarAdapter != null) {
                materialCalendarAdapter.notifyDataSetChanged();
            }
        }
    }

    public static void getSelectedDate(int i, int i2, int i3) {
        int i4 = (i - 6) - mFirstDay;
        Log.d("DATE_NUMBER", String.valueOf(i4));
        Log.d("SELECTED_DATE", String.valueOf(i2 + "/" + i4 + "/" + i3));
    }
}
