package com.tugasoft.fintuga.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;

import java.io.File;
import java.util.ArrayList;

public class DownloadedTransactionsAdapter extends RecyclerView.Adapter<DownloadedTransactionsAdapter.MyViewHolder> {
    static final boolean $assertionsDisabled = false;

    public Activity mActivity;

    public ArrayList<File> mList;

    public DownloadedTransactionsAdapter(Activity activity, ArrayList<File> arrayList) {
        this.mList = arrayList;
        this.mActivity = activity;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_downloaded_transactions, viewGroup, false));
    }

    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        final File file = this.mList.get(i);
        myViewHolder.tvFileName.setText(file.getName());
        myViewHolder.rlRoot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (file.getName().endsWith(".pdf")) {
                    CommonMethod.viewPDF(DownloadedTransactionsAdapter.this.mActivity, file);
                }
            }
        });
        myViewHolder.ivDelete.setTag(Integer.valueOf(i));
        myViewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (file.getName().endsWith(".pdf")) {
                    try {
                        file.delete();
                        DownloadedTransactionsAdapter.this.mList.remove(((Integer) view.getTag()).intValue());
                        DownloadedTransactionsAdapter.this.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public int getItemCount() {
        return this.mList.size();
    }

    public void addData(ArrayList<File> arrayList) {
        ArrayList<File> arrayList2 = this.mList;
        if (arrayList2 != null) {
            arrayList2.clear();
        }
        this.mList.addAll(arrayList);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivDelete;
        RelativeLayout rlRoot;
        TextView tvFileName;

        MyViewHolder(View view) {
            super(view);
            this.rlRoot = (RelativeLayout) view.findViewById(R.id.rl_root);
            this.tvFileName = (TextView) view.findViewById(R.id.tv_file_name);
            this.ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
        }
    }
}
