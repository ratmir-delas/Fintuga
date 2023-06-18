package com.tugasoft.fintuga.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.tugasoft.fintuga.activity.DashboardActivity;
import com.tugasoft.fintuga.models.MasterExpenseModel;
import com.tugasoft.fintuga.R;

public class MaterialCalendarAdapter extends BaseAdapter {
    private static ViewHolder mHolder;
    int mGridViewIndexOffset = 1;
    int mWeekDayNames = 7;
    private Context mContext;

    public MaterialCalendarAdapter(Context context) {
        this.mContext = context;
    }

    public Object getItem(int i) {
        return null;
    }

    public long getItemId(int i) {
        return 0;
    }

    public int getCount() {
        if (MaterialCalendar.mFirstDay == -1 || MaterialCalendar.mNumDaysInMonth == -1) {
            return this.mWeekDayNames;
        }
        Log.d("GRID_COUNT", String.valueOf(this.mWeekDayNames + MaterialCalendar.mFirstDay + MaterialCalendar.mNumDaysInMonth));
        return this.mWeekDayNames + MaterialCalendar.mFirstDay + MaterialCalendar.mNumDaysInMonth;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_material_day, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder();
            mHolder = viewHolder;
            if (view != null) {
                viewHolder.mTextView = (TextView) view.findViewById(R.id.material_calendar_day);
                mHolder.mTvIncome = (TextView) view.findViewById(R.id.tv_income);
                mHolder.mTvExpense = (TextView) view.findViewById(R.id.tv_expense);
                view.setTag(mHolder);
            }
        } else {
            mHolder = (ViewHolder) view.getTag();
        }
        if (mHolder.mTextView != null) {
            setCalendarDay(i);
        }
        setSavedEvent(i);
        return view;
    }

    private void setCalendarDay(int i) {
        if (i <= (this.mWeekDayNames - this.mGridViewIndexOffset) + MaterialCalendar.mFirstDay) {
            mHolder.mTextView.setTextColor(this.mContext.getResources().getColor(R.color.calendar_day_text_color));
            Log.d("NO_CLICK_POSITION", String.valueOf(i));
        } else {
            mHolder.mTextView.setTextColor(this.mContext.getResources().getColor(R.color.calendar_number_text_color));
        }
        switch (i) {
            case 0:
                mHolder.mTextView.setText(this.mContext.getResources().getString(R.string.sunday));
                mHolder.mTextView.setTypeface(Typeface.DEFAULT_BOLD);
                return;
            case 1:
                mHolder.mTextView.setText(this.mContext.getResources().getString(R.string.monday));
                mHolder.mTextView.setTypeface(Typeface.DEFAULT_BOLD);
                return;
            case 2:
                mHolder.mTextView.setText(this.mContext.getResources().getString(R.string.tuesday));
                mHolder.mTextView.setTypeface(Typeface.DEFAULT_BOLD);
                return;
            case 3:
                mHolder.mTextView.setText(this.mContext.getResources().getString(R.string.wednesday));
                mHolder.mTextView.setTypeface(Typeface.DEFAULT_BOLD);
                return;
            case 4:
                mHolder.mTextView.setText(this.mContext.getResources().getString(R.string.thursday));
                mHolder.mTextView.setTypeface(Typeface.DEFAULT_BOLD);
                return;
            case 5:
                mHolder.mTextView.setText(this.mContext.getResources().getString(R.string.friday));
                mHolder.mTextView.setTypeface(Typeface.DEFAULT_BOLD);
                return;
            case 6:
                mHolder.mTextView.setText(this.mContext.getResources().getString(R.string.saturday));
                mHolder.mTextView.setTypeface(Typeface.DEFAULT_BOLD);
                return;
            default:
                mHolder.mTextView.setTypeface(Typeface.DEFAULT);
                Log.d("CURRENT_POSITION", String.valueOf(i));
                if (i < this.mWeekDayNames + MaterialCalendar.mFirstDay) {
                    Log.d("BLANK_POSITION", "This is a blank day");
                    mHolder.mTextView.setText("");
                    mHolder.mTextView.setTypeface(Typeface.DEFAULT);
                    return;
                }
                mHolder.mTextView.setText(String.valueOf((i - (this.mWeekDayNames - this.mGridViewIndexOffset)) - MaterialCalendar.mFirstDay));
                if (MaterialCalendar.mCurrentDay != -1) {
                    mHolder.mTextView.setTextColor(this.mContext.getResources().getColor(R.color.calendar_time_sheet_not_filled_color));
                    return;
                } else {
                    mHolder.mTextView.setTextColor(this.mContext.getResources().getColor(R.color.calendar_time_sheet_not_filled_color));
                    return;
                }
        }
    }

    private void setSavedEvent(int i) {
        int i2;
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                mHolder.mTvIncome.setVisibility(View.INVISIBLE);
                mHolder.mTvExpense.setVisibility(View.GONE);
                return;
            default:
                mHolder.mTvIncome.setVisibility(View.INVISIBLE);
                mHolder.mTvExpense.setVisibility(View.INVISIBLE);
                if (MaterialCalendar.mFirstDay != -1 && ((DashboardActivity) this.mContext).mTransactionList != null && ((DashboardActivity) this.mContext).mTransactionList.size() > 0 && i > (i2 = (this.mWeekDayNames - this.mGridViewIndexOffset) + MaterialCalendar.mFirstDay)) {
                    int size = ((DashboardActivity) this.mContext).mTransactionList.size();
                    for (int i3 = 0; i3 < size; i3++) {
                        MasterExpenseModel masterExpenseModel = ((DashboardActivity) this.mContext).mTransactionList.get(i3);
                        int day = masterExpenseModel.getDay() + i2;
                        Log.d("POSITION", String.valueOf(i));
                        Log.d("SAVED_POSITION", String.valueOf(day));
                        if (i == day) {
                            if (i == MaterialCalendar.mCurrentDay + i2) {


                                mHolder.mTextView.setTextColor(ContextCompat.getColor(this.mContext, android.R.color.black));
                                if (masterExpenseModel.isTransactionAdded() && (masterExpenseModel.getTotalIncome() > 0 || masterExpenseModel.getTotalExpense() > 0)) {
                                    mHolder.mTvIncome.setVisibility(View.VISIBLE);
                                    mHolder.mTvExpense.setVisibility(View.VISIBLE);
                                    if (masterExpenseModel.getTotalIncome() > 0) {
                                        mHolder.mTvIncome.setText(CommonMethod.formatPrice((double) masterExpenseModel.getTotalIncome()));
                                    } else {
                                        mHolder.mTvIncome.setText("-");
                                    }
                                    if (masterExpenseModel.getTotalExpense() > 0) {
                                        mHolder.mTvExpense.setText(CommonMethod.formatPrice((double) masterExpenseModel.getTotalExpense()));
                                    } else {
                                        mHolder.mTvExpense.setText("-");
                                    }
                                }
                            } else if (!masterExpenseModel.isTransactionAdded()) {
                                mHolder.mTextView.setTextColor(this.mContext.getResources().getColor(R.color.calendar_time_sheet_not_filled_color));
                            } else if (masterExpenseModel.getTotalIncome() > 0 || masterExpenseModel.getTotalExpense() > 0) {
                                mHolder.mTvIncome.setVisibility(View.VISIBLE);
                                mHolder.mTvExpense.setVisibility(View.VISIBLE);
                                if (masterExpenseModel.getTotalIncome() > 0) {
                                    mHolder.mTvIncome.setText(CommonMethod.formatPrice((double) masterExpenseModel.getTotalIncome()));
                                } else {
                                    mHolder.mTvIncome.setText("-");
                                }
                                if (masterExpenseModel.getTotalExpense() > 0) {
                                    mHolder.mTvExpense.setText(CommonMethod.formatPrice((double) masterExpenseModel.getTotalExpense()));
                                } else {
                                    mHolder.mTvExpense.setText("-");
                                }
                            }
                        }
                    }
                    return;
                }
                return;
        }
    }

    private static class ViewHolder {
        TextView mTextView;
        TextView mTvExpense;
        TextView mTvIncome;

        private ViewHolder() {
        }
    }
}
