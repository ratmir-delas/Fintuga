package com.tugasoft.fintuga.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tugasoft.fintuga.models.Category;
import com.tugasoft.fintuga.models.Expense;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;
import com.tugasoft.fintuga.utils.MySharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class CalenderDayWiseHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Activity mActivity;
    private List<Expense> mList;

    public CalenderDayWiseHistoryAdapter(Activity activity, ArrayList<Expense> arrayList) {
        mActivity = activity;
        mList = arrayList;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
        if (i == 77777) {
            return new EmptyViewHolder(from.inflate(R.layout.nothing_yet, viewGroup, false));
        }
        return new MyViewHolder(from.inflate(R.layout.row_calender_day_transaction_history_detail, viewGroup, false));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        //get categories
        List<Category> categories = new ArrayList<>();
        for (Expense expense : mList) {
            if (!categories.contains(expense.getCategory())) {
                categories.add(new Category(expense.getCategory(), expense.getColor()));
            }
        }
        //
        int i2;
        int i2Opacity = 0;
        if (getItemViewType(i) != 77777) {
            MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
            Expense expense = mList.get(i);
            GradientDrawable gradientDrawable = (GradientDrawable) ((LayerDrawable) ((MyViewHolder) viewHolder).ll_root.getBackground()).findDrawableByLayerId(R.id.outerRectangle);
            GradientDrawable borderDrawable = (GradientDrawable) ((LayerDrawable) ((MyViewHolder) viewHolder).tv_exp_amount.getBackground()).findDrawableByLayerId(R.id.border_Rect);
//            String categoryColor = null;
//            for (String category : categories) {
//                if (category.equalsIgnoreCase(categoryColor)) {
//                    categoryColor = category;
//                    break;
//                }
//            }
            if (expense.getColor() != null) {
                i2 = Color.parseColor(expense.getColor());
                int cut = expense.getColor().lastIndexOf('#');
                if (cut != -1) {
                    String a = "#80" + expense.getColor().substring(cut + 1);
                    i2Opacity = Color.parseColor(a);
                }
            } else {
                i2 = mActivity.getResources().getColor(R.color.colorAccent);
                i2Opacity = mActivity.getResources().getColor(R.color.color1);
            }
            gradientDrawable.setStroke(3, i2);
            gradientDrawable.setColor(i2Opacity);
            borderDrawable.setStroke(3, i2);
            ((MyViewHolder) viewHolder).iv.setImageTintMode(PorterDuff.Mode.SRC_IN);
            ((MyViewHolder) viewHolder).iv.setColorFilter(i2);
            ((MyViewHolder) viewHolder).tv_exp_category.setText(expense.getCategory());
            //((MyViewHolder) viewHolder).tv_exp_amount.setText(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" ").concat(CommonMethod.formatPrice((double) expense.getAmount())));

            myViewHolder.tv_exp_amount.setText(MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "").concat(" ").concat(CommonMethod.formatPrice((double) expense.getAmount())));
            if (expense.getDescription().trim().length() > 0) {
                myViewHolder.tv_exp_descr.setText(expense.getDescription());
            } else {
                myViewHolder.tv_exp_descr.setVisibility(View.GONE);
            }
        }
    }

    public int getItemCount() {
        if (mList.size() > 0) {
            return mList.size();
        }
        return 1;
    }

    public int getItemViewType(int i) {
        if (mList.size() == 0) {
            return 77777;
        }
        return super.getItemViewType(i);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addData(ArrayList<Expense> arrayList) {
        mList.addAll(arrayList);
        notifyDataSetChanged();
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvAlertMessage;

        public EmptyViewHolder(View view) {
            super(view);
            tvAlertMessage = view.findViewById(R.id.tvAlertMessage);
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv = itemView.findViewById(R.id.iv);
        LinearLayout ll_root = itemView.findViewById(R.id.rl_root);
        TextView tv_exp_amount = itemView.findViewById(R.id.tv_exp_amount);
        TextView tv_exp_category = itemView.findViewById(R.id.tv_exp_category);
        TextView tv_exp_descr = itemView.findViewById(R.id.tv_exp_descr);

        public MyViewHolder(View view) {
            super(view);
        }
    }
}
