package com.tugasoft.fintuga.adapters;

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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.models.Expense;
import com.tugasoft.fintuga.utils.CommonMethod;
import com.tugasoft.fintuga.utils.MySharedPreferences;

import java.util.List;

public class ExpenseChildAdapter extends RecyclerView.Adapter<ExpenseChildAdapter.MyViewHolder> {
    private final OnItemClickListener mListener;
    private final List<Expense> mList;
    private final int mResource;

    public ExpenseChildAdapter(int resource, List<Expense> list, OnItemClickListener onItemClickListener) {
        this.mResource = resource;
        this.mList = list;
        this.mListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mResource, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(mList.get(position), position, mListener);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Expense expense, int position);

        void onItemLongClick(Expense expense, int position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout llRoot;
        private final ImageView iv;
        private final TextView tvExpCategory;
        private final TextView tvExpAmount;
        private final TextView tvExpDescr;

        MyViewHolder(View view) {
            super(view);
            llRoot = view.findViewById(R.id.rl_root);
            iv = view.findViewById(R.id.iv);
            tvExpCategory = view.findViewById(R.id.tv_exp_category);
            tvExpAmount = view.findViewById(R.id.tv_exp_amount);
            tvExpDescr = view.findViewById(R.id.tv_exp_descr);
        }

        public void bind(final Expense expense, final int position, final OnItemClickListener onItemClickListener) {
            GradientDrawable gradientDrawable = (GradientDrawable) ((LayerDrawable) llRoot.getBackground()).findDrawableByLayerId(R.id.outerRectangle);
            GradientDrawable borderDrawable = (GradientDrawable) ((LayerDrawable) tvExpAmount.getBackground()).findDrawableByLayerId(R.id.border_Rect);
            String categoryColor = expense.getColor();
            int backgroundColor = ContextCompat.getColor(itemView.getContext(), R.color.colorAccent);
            int opacityColor = ContextCompat.getColor(itemView.getContext(), R.color.color1);

            if (categoryColor != null) {
                int color = Color.parseColor(categoryColor);
                int cut = categoryColor.lastIndexOf('#');
                if (cut != -1) {
                    String a = "#80" + categoryColor.substring(cut + 1);
                    opacityColor = Color.parseColor(a);
                }
                gradientDrawable.setStroke(3, color);
                gradientDrawable.setColor(opacityColor);
                borderDrawable.setStroke(3, color);
                iv.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            } else {
                gradientDrawable.setStroke(3, backgroundColor);
                gradientDrawable.setColor(opacityColor);
                borderDrawable.setStroke(3, backgroundColor);
                iv.setColorFilter(backgroundColor, PorterDuff.Mode.SRC_IN);
            }

            if (!expense.isExpense()) {
                iv.setImageResource(R.drawable.ic_add_cir);
            } else if (expense.isExpense()) {
                iv.setImageResource(R.drawable.ic_remove_circle);
            }

            tvExpCategory.setText(expense.getCategory());

            String currencyType = MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "");
            String formattedAmount = currencyType + " " + CommonMethod.formatPrice(expense.getAmount());
            tvExpAmount.setText(formattedAmount);

            String description = expense.getDescription();
            if (description.trim().length() > 0) {
                tvExpDescr.setText(description);
                tvExpDescr.setVisibility(View.VISIBLE);
            } else {
                tvExpDescr.setVisibility(View.GONE);
            }

            itemView.setOnLongClickListener(view -> {
                onItemClickListener.onItemLongClick(expense, position);
                return true;
            });

            itemView.setOnClickListener(view -> onItemClickListener.onItemClick(expense, position));
        }
    }
}