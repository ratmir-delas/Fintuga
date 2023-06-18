package com.tugasoft.fintuga;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.tugasoft.fintuga.activity.ExpenseManagerLoadingActivity;
import com.tugasoft.fintuga.activity.MoreActivity;
import com.tugasoft.fintuga.ads.AdsProvider;
import com.tugasoft.fintuga.tools.CompareLoanActivity;
import com.tugasoft.fintuga.tools.CurrencyConverterActivity;
import com.tugasoft.fintuga.tools.EmiCalculatorActivity;
import com.tugasoft.fintuga.tools.FdCalculatorActivity;
import com.tugasoft.fintuga.tools.SimpleAndCompoundActivity;
import com.tugasoft.fintuga.tools.SwpCalculatorActivity;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    public TextView ad;
    public CardView cardview_ad;
    public View line;
    public RelativeLayout linearLayout2;
    ImageView burger;
    DrawerLayout drawerLayout;
    RelativeLayout main_Lay, myLay;
    Dialog main_dialog;
    private ReviewManager manager;

    @Override
    public void onCreate(Bundle bundle) {

        //changeLocale("pt");

        super.onCreate(bundle);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundContentColor));
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_main);

        main_Lay = findViewById(R.id.main_Lay);

        drawerLayout = findViewById(R.id.drawer_layout);

        manager = ReviewManagerFactory.create(this);
        cardview_ad = findViewById(R.id.admobcard);
        ad = findViewById(R.id.ad);
        View inflate = LayoutInflater.from(this).inflate(R.layout.exit_dialog, (ViewGroup) null);
        Dialog dialog = new Dialog(this);
        main_dialog = dialog;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        main_dialog.setCanceledOnTouchOutside(true);
        main_dialog.setContentView(inflate);
        main_dialog.setCancelable(false);
        line = main_dialog.findViewById(R.id.line);
        linearLayout2 = main_dialog.findViewById(R.id.linearLayout2);
        main_dialog.findViewById(R.id.yes).setOnClickListener(view -> finishAffinity());
        main_dialog.findViewById(R.id.no).setOnClickListener(view -> main_dialog.dismiss());

        refreshAd_exit();
        AdmobNative();

        burger = findViewById(R.id.burger);
        burger.setOnClickListener(view -> {
            //drawerLayout.openDrawer(GravityCompat.START);
            startActivity(new Intent(this, MoreActivity.class));
        });
    }

    public void changeLocale(String locale) {
        Locale myLocale = new Locale(locale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        res.updateConfiguration(conf, dm);
    }

    private void AdmobNative() {
        AdsProvider.getInstance().addBigNativeView(this, findViewById(R.id.fl_adplaceholder));
    }

    public void onBackPressed() {
        manager.requestReviewFlow().addOnCompleteListener(this::showRate);
    }

    public void showRate(Task task) {
        Dialog dialog;
        if (task.isSuccessful()) {
            manager.launchReviewFlow(this, (ReviewInfo) task.getResult()).addOnCompleteListener(task1 -> {
                if (main_dialog != null) {
                    main_dialog.show();
                }
            });
        } else if (!isFinishing() && (dialog = main_dialog) != null) {
            dialog.show();
        }
    }

    private void refreshAd_exit() {
        AdsProvider.getInstance().addBigNativeView(this, findViewById(R.id.fl_adplaceholder));
    }

    @Override
    public void onDestroy() {
        main_dialog.dismiss();
        super.onDestroy();
    }

    public void startExpenseManager(View view) {
        startActivity(new Intent(this, ExpenseManagerLoadingActivity.class));
    }

    public void startEmi(View view) {
        startActivity(new Intent(this, EmiCalculatorActivity.class));
    }

    public void startCompareLoan(View view) {
        startActivity(new Intent(this, CompareLoanActivity.class));
    }

    public void startCurrencyConvertor(View view) {
        startActivity(new Intent(this, CurrencyConverterActivity.class));
    }

    public void startSimpleAndCompound(View view) {
        startActivity(new Intent(this, SimpleAndCompoundActivity.class));
    }

    public void fd(View view) {
        startActivity(new Intent(this, FdCalculatorActivity.class));
    }

    public void swp(View view) {
        startActivity(new Intent(this, SwpCalculatorActivity.class));
    }
}