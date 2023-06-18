package com.tugasoft.fintuga.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.tugasoft.fintuga.BuildConfig;
import com.tugasoft.fintuga.R;

import java.io.File;
import java.util.Objects;

public class ShowPDFActivity extends AppCompatActivity implements OnPageChangeListener {

    String TAG="ShowPDFActivity";
    PDFView pdfView;
    String pdfPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pdfactivity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        pdfPath = getIntent().getStringExtra("pdfFile");
        pdfView = findViewById(R.id.pdfView);
        pdfView.fromFile(new File(this.pdfPath)).defaultPage(0).enableSwipe(true).swipeHorizontal(false).onPageChange(this ).enableAnnotationRendering(true).scrollHandle(new DefaultScrollHandle(this)).pageFitPolicy(FitPolicy.BOTH).spacing(10).load();
        pdfView.setMaxZoom(1.0f);
        pdfView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    public void saveBtn(View view) {
        Toast.makeText(this, "Saved PDF File At: "+pdfPath, Toast.LENGTH_SHORT).show();
    }

    public void shareBtn(View view) {


        Uri uri = Uri.fromFile(new File(pdfPath));//FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider",new File(pdfPath));

        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("*/*");
        intent.putExtra("android.intent.extra.SUBJECT", "njduhnf");
//        StringBuilder stringBuilder2 = new StringBuilder();
//        stringBuilder2.append(getApplicationContext().getPackageName());
//        stringBuilder2.append(".provider");
        intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".provider", new File(pdfPath)));
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Select Application."));

//        Intent share = new Intent();
//        share.setAction(Intent.ACTION_SEND);
//        share.setType("application/pdf");
//        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        share.putExtra(Intent.EXTRA_STREAM, uri);
////        share.setPackage("com.whatsapp");
//
//        startActivity(Intent.createChooser(share,"Share...."));
    }
}