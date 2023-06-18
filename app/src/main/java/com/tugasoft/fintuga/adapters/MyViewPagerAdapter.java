package com.tugasoft.fintuga.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.tugasoft.fintuga.application.AppCore;
import com.tugasoft.fintuga.models.Category;
import com.tugasoft.fintuga.models.Expense;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;
import com.tugasoft.fintuga.utils.MySharedPreferences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MyViewPagerAdapter extends PagerAdapter {

    private final FirebaseStorage storageRef = FirebaseStorage.getInstance();
    public Activity mActivity;
    private final List<Expense> mList;

    public MyViewPagerAdapter(Activity activity, ArrayList<Expense> arrayList) {
        this.mActivity = activity;
        this.mList = arrayList;
    }

    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public Object instantiateItem(ViewGroup viewGroup, int i) {
        ViewGroup viewGroup2 = (ViewGroup) LayoutInflater.from(this.mActivity).inflate(R.layout.row_details_image, viewGroup, false);
        LinearLayout linearLayout = viewGroup2.findViewById(R.id.ll_details);
        RelativeLayout relativeLayout = viewGroup2.findViewById(R.id.rl_root);
        ImageView imageView = viewGroup2.findViewById(R.id.iv_download);
        ImageView imageView2 = viewGroup2.findViewById(R.id.pagerImage);
        ImageView imageView3 = viewGroup2.findViewById(R.id.iv);
        TextView textView = viewGroup2.findViewById(R.id.tv_exp_category);
        TextView textView2 = viewGroup2.findViewById(R.id.tv_exp_amount);
        TextView textView3 = viewGroup2.findViewById(R.id.tv_exp_descr);
        ProgressBar progressBar = viewGroup2.findViewById(R.id.myProgress);
        final Expense expense = mList.get(i);
        try {
            Glide.with(AppCore.getAppContext())
                    .load(expense.getProofUri())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                           progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .error(ContextCompat.getDrawable(this.mActivity, R.drawable.error_downloading_icon))
                    .into(imageView2);


        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!expense.isExpense()) {
            imageView3.setImageResource(R.drawable.img_income);
        } else if (expense.isExpense()) {
            imageView3.setImageResource(R.drawable.img_expense);
        }

        //get category of expense
        //String category = expense.getCategory();
        int cut = expense.getColor().lastIndexOf('#');
        if (cut != -1) {
            String a = "#80" + expense.getColor().substring(cut + 1);
            relativeLayout.setBackgroundColor(Color.parseColor(a));
        }

        linearLayout.setBackgroundColor(Color.parseColor(expense.getColor()));

        textView.setText(expense.getCategory());
        String str = MySharedPreferences.getStr(MySharedPreferences.KEY_CURRENCY_TYPE, "");
        textView2.setText(str.concat(" " + CommonMethod.formatPrice((double) expense.getAmount())));
        if (expense.getDescription().trim().length() > 0) {
            textView3.setText(expense.getDescription());
        } else {
            textView3.setVisibility(View.GONE);
        }
        imageView.setOnClickListener(view -> MyViewPagerAdapter.this.downloadToLocalFile(expense));
        viewGroup.addView(viewGroup2);
        return viewGroup2;
    }

    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        viewGroup.removeView((View) obj);
    }

    public int getCount() {
        return this.mList.size();
    }


    public void downloadToLocalFile(Expense expense) {
        File applicationDirectory = CommonMethod.getApplicationDirectory(CommonMethod.SubDirectory.APP_DOWNLOAD_IMAGE, this.mActivity, true);
        File file = new File(applicationDirectory, UUID.randomUUID().toString() + ".png");
        try {
            if (!applicationDirectory.exists()) {
                applicationDirectory.mkdir();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StorageReference referenceFromUrl = this.storageRef.getReferenceFromUrl(expense.getProofUri());
        final ProgressDialog progressDialog = new ProgressDialog(this.mActivity);
        progressDialog.setTitle("Downloading...");
        progressDialog.setMessage((CharSequence) null);
        progressDialog.show();
        try {
            referenceFromUrl.getFile(file).addOnSuccessListener((OnSuccessListener) new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MyViewPagerAdapter.this.mActivity, "Image downloaded successfully.", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }).addOnFailureListener((OnFailureListener) new OnFailureListener() {
                @Override
                public void onFailure(Exception exc) {
                    progressDialog.dismiss();
                    Toast.makeText(MyViewPagerAdapter.this.mActivity, exc.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener((OnProgressListener) new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    double bytesTransferred = (((double) taskSnapshot.getBytesTransferred()) * 100.0d) / ((double) taskSnapshot.getTotalByteCount());

                    progressDialog.setMessage("Downloaded " + ((int) bytesTransferred) + "%...");
                }
            });
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
