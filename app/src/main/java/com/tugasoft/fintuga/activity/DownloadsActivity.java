package com.tugasoft.fintuga.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tugasoft.fintuga.adapters.DownloadedImageAdapter;
import com.tugasoft.fintuga.adapters.DownloadedTransactionsAdapter;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;

import java.io.File;
import java.util.ArrayList;

public class DownloadsActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_download);
        setToolBar();
        init();
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle((CharSequence) "Downloads");
        toolbar.setContentInsetStartWithNavigation(getResources().getInteger(R.integer.dimen_spacing_between_toolbar_icon_and_title));


        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void init() {
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_downloaded_image);
        final LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.ll_downloaded_transactions);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_image);
        RecyclerView recyclerView2 = (RecyclerView) findViewById(R.id.rv_pdf);
        final DownloadedImageAdapter downloadedImageAdapter = new DownloadedImageAdapter(this, new ArrayList());
        recyclerView.setLayoutManager(new GridLayoutManager((Context) this, 3, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(downloadedImageAdapter);
        final DownloadedTransactionsAdapter downloadedTransactionsAdapter = new DownloadedTransactionsAdapter(this, new ArrayList());
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2.setAdapter(downloadedTransactionsAdapter);
        AsyncTask.execute(new Runnable() {


            public void run() {
                final ArrayList<File> imagesListFiles = CommonMethod.getImagesListFiles(CommonMethod.getApplicationDirectory(CommonMethod.SubDirectory.APP_DOWNLOAD_IMAGE, DownloadsActivity.this, true));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList arrayList = imagesListFiles;
                        if (arrayList == null || arrayList.size() == 0) {
                            linearLayout.setVisibility(View.GONE);
                        } else {
                            downloadedImageAdapter.addData(imagesListFiles);
                        }
                    }
                });
                final ArrayList<File> imagesListFiles2 = CommonMethod.getImagesListFiles(CommonMethod.getApplicationDirectory(CommonMethod.SubDirectory.APP_PDF_DATA, DownloadsActivity.this, true));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList arrayList = imagesListFiles2;
                        if (arrayList == null || arrayList.size() == 0) {
                            linearLayout2.setVisibility(View.GONE);
                        } else {
                            downloadedTransactionsAdapter.addData(imagesListFiles2);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}
