package com.tugasoft.fintuga.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tugasoft.fintuga.models.Category;
import com.tugasoft.fintuga.R;

import java.util.ArrayList;

public class AddIncomeCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final int EMPTY_VIEW = 77777;
    private final Activity mActivity;
    ArrayList<Category> mList;

    public AddIncomeCategoryAdapter(Activity activity, ArrayList<Category> arrayList) {
        this.mActivity = activity;
        this.mList = arrayList;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater from = LayoutInflater.from(this.mActivity);
        if (i == 77777) {
            return new EmptyViewHolder(from.inflate(R.layout.nothing_yet, viewGroup, false));
        }
        return new MyViewHolder(from.inflate(R.layout.row_add_transaction_category, viewGroup, false));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int i2;
        if (getItemViewType(i) == 77777) {
            ((EmptyViewHolder) viewHolder).tvAlertMessage.setText("No categories added yet.Please add some category by tapping on bottom ICON.");
            return;
        }
        MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
        Category category = this.mList.get(i);
        myViewHolder.tvDesc.setText(category.getCategory());
        String color = category.getColor();
        if (color != null) {
            i2 = Color.parseColor(color);
        } else {
            i2 = this.mActivity.getResources().getColor(R.color.colorAccent);
        }
        myViewHolder.ivColor.setColorFilter(i2);
        myViewHolder.ivDelete.setTag(Integer.valueOf(i));
        myViewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AddIncomeCategoryAdapter.this.mList.remove(((Integer) view.getTag()).intValue());
                AddIncomeCategoryAdapter.this.notifyDataSetChanged();
            }
        });
    }

    public int getItemCount() {
        if (this.mList.size() > 0) {
            return this.mList.size();
        }
        return 1;
    }

    public int getItemViewType(int i) {
        if (this.mList.size() == 0) {
            return 77777;
        }
        return super.getItemViewType(i);
    }

    public void addItem(Category category) {
        this.mList.add(category);
        notifyDataSetChanged();
    }

    public ArrayList<Category> getCategoryList() {
        return this.mList;
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvAlertMessage;

        public EmptyViewHolder(View view) {
            super(view);
            this.tvAlertMessage = (TextView) view.findViewById(R.id.tvAlertMessage);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivColor = ((ImageView) this.itemView.findViewById(R.id.iv_color));
        ImageView ivDelete = ((ImageView) this.itemView.findViewById(R.id.iv_delete));
        TextView tvDesc = ((TextView) this.itemView.findViewById(R.id.tv_desc));

        public MyViewHolder(View view) {
            super(view);
        }
    }
}
