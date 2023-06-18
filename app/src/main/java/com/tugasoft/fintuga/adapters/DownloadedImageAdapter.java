package com.tugasoft.fintuga.adapters;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;

import java.io.File;
import java.util.ArrayList;

public class DownloadedImageAdapter extends RecyclerView.Adapter<DownloadedImageAdapter.MyViewHolder> {
    static final boolean $assertionsDisabled = false;

    public Activity mActivity;
    private ArrayList<File> movieList;

    public DownloadedImageAdapter(Activity activity, ArrayList<File> arrayList) {
        this.movieList = arrayList;
        this.mActivity = activity;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_downloaded_images, viewGroup, false));
    }

    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        final File file = this.movieList.get(i);
        myViewHolder.ivShare.setOnClickListener(shareMediaItem(file));
        myViewHolder.imageViewImageMedia.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        myViewHolder.imageViewImageMedia.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (file.getName().toString().endsWith(".png")) {
                    CommonMethod.viewImage(DownloadedImageAdapter.this.mActivity, file);
                }
            }
        });
    }


    public View.OnClickListener shareMediaItem(final File file) {
        return new View.OnClickListener() {
            public void onClick(View view) {
                new Runnable() {
                    public void run() {
                        CommonMethod.ShareImageFile(DownloadedImageAdapter.this.mActivity, file);
                    }
                }.run();
            }
        };
    }

    public int getItemCount() {
        return this.movieList.size();
    }

    public void addData(ArrayList<File> arrayList) {
        ArrayList<File> arrayList2 = this.movieList;
        if (arrayList2 != null) {
            arrayList2.clear();
        }
        this.movieList.addAll(arrayList);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageViewImageMedia;
        ImageView ivShare;

        MyViewHolder(View view) {
            super(view);
            this.imageViewImageMedia = (ImageView) view.findViewById(R.id.thumbnail);
            this.ivShare = (ImageView) view.findViewById(R.id.iv_share);
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
        }
    }
}
