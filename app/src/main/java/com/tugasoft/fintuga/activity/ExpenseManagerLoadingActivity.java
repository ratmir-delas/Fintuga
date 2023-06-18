package com.tugasoft.fintuga.activity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.CommonMethod;
import com.tugasoft.fintuga.utils.Constant;
import com.tugasoft.fintuga.utils.NotificationScheduler;
import com.tugasoft.fintuga.utils.MySharedPreferences;
import com.tugasoft.fintuga.receiver.AlarmReceiver;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseManagerLoadingActivity extends AppCompatActivity {

    public String TAG = ExpenseManagerLoadingActivity.class.getCanonicalName();
    public FirebaseRemoteConfig mFirebaseRemoteConfig = null;
    public AVLoadingIndicatorView mProgressBar;
    private HashMap<String, Object> firebaseDefaultMap;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_loading_expense_manager);

        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic(Constant.FIREBASE_NOTIFICATION_MESSAGE_TOPIC);
        HashMap<String, Object> hashMap = new HashMap<>();
        this.firebaseDefaultMap = hashMap;
        hashMap.put("Latest_Application_Version", 11);
        if (!MySharedPreferences.getBool(MySharedPreferences.KEY_IS_REMINDER_SET_ON_APP_LAUNCH)) {
            MySharedPreferences.setBool(MySharedPreferences.KEY_IS_REMINDER_ENABLE, true);
            NotificationScheduler.setReminder(getApplicationContext(), AlarmReceiver.class, MySharedPreferences.get_hour(MySharedPreferences.KEY_REMINDER_HOUR), MySharedPreferences.get_min(MySharedPreferences.KEY_REMINDER_MIN));
            MySharedPreferences.setBool(MySharedPreferences.KEY_IS_REMINDER_SET_ON_APP_LAUNCH, true);
        }
        transparentToolbar();
        init();
        handlePermission();
    }

    private void transparentToolbar() {
        getWindow().getDecorView().setSystemUiVisibility(1280);
        CommonMethod.setWindowFlag(this, 1 << 26, false);
        getWindow().setStatusBarColor(0);
    }

    private void init() {
        this.mProgressBar = findViewById(R.id.pb_fetch_captions);
        ((TextView) findViewById(R.id.tv_app_version)).setText("Version ".concat("BuildConfig.VERSION_NAME"));
    }

    private String[] permissionList() {
        return new String[]{"android.permission.WRITE_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE", "android.permission.CAMERA"};
    }

    private void handlePermission() {
        Dexter.withActivity(this).withPermissions(permissionList()).withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                multiplePermissionsReport.getDeniedPermissionResponses();
                if (multiplePermissionsReport.getDeniedPermissionResponses().size() <= 0) {
                    checkForApplicationUpdate();
                } else {
                    showSettingsDialog();
                }
            }

            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(dexterError -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
    }

    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((int) R.string.title_required_permission);
        builder.setMessage((int) R.string.message_required_permission).setPositiveButton((int) R.string.action_go_settings, (DialogInterface.OnClickListener) null).setNegativeButton((int) R.string.action_cancel, (DialogInterface.OnClickListener) null);
        final AlertDialog create = builder.create();
        create.setOnShowListener(dialogInterface -> {
            create.getButton(-1).setOnClickListener(view -> {
                create.dismiss();
                openSettings();
            });
            create.getButton(-2).setOnClickListener(view -> create.dismiss());
        });
        create.setCancelable(false);
        create.setCanceledOnTouchOutside(false);
        create.show();
    }

    public void openSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), (String) null));
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 101) {
            int i3 = 0;
            boolean z = false;
            for (String str : permissionList()) {
                i3 += ContextCompat.checkSelfPermission(this, str);
                z = z || ActivityCompat.shouldShowRequestPermissionRationale(this, str);
            }
            if (i3 == 0) {
                checkForApplicationUpdate();
            } else {
                handlePermission();
            }
        }
    }

    public void checkForApplicationUpdate() {
        if (CommonMethod.isNetworkConnected(this)) {
            if (this.mFirebaseRemoteConfig == null) {
                this.mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            }
            this.mFirebaseRemoteConfig.setDefaultsAsync((Map<String, Object>) this.firebaseDefaultMap);
            this.mFirebaseRemoteConfig.setConfigSettingsAsync(new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600).build());
            this.mProgressBar.setVisibility(View.VISIBLE);
            this.mFirebaseRemoteConfig.fetch().addOnCompleteListener(task -> {
                mProgressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    mFirebaseRemoteConfig.fetchAndActivate();
                    String access$500 = TAG;
                    Log.d(access$500, "Fetched value: " + mFirebaseRemoteConfig.getString("Latest_Application_Version"));
                    checkForUpdate();
                    return;
                }
                Toast.makeText(ExpenseManagerLoadingActivity.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
            });
            return;
        }
        CommonMethod.showConnectionAlertAndRetry(this);
    }

    public void checkForUpdate() {
        if (((int) this.mFirebaseRemoteConfig.getDouble("Latest_Application_Version")) > 11) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle((CharSequence) "Update Available");
            builder.setCancelable(false);
            builder.setMessage((CharSequence) "A new version of this app is available. Please click below to update the latest version.").setCancelable(false).setPositiveButton((int) R.string.action_update, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String packageName = getPackageName();
                    try {

                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + packageName)));
                    } catch (ActivityNotFoundException unused) {

                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                    }
                }
            }).setNegativeButton(R.string.action_cancel, (dialogInterface, i) -> {
                dialogInterface.dismiss();
                finish();
            });
            builder.create().show();
            return;
        }
        callNextScreen();
    }

    private void callNextScreen() {
        new Thread() {
            @Override
            public void run() {
                Intent intent;
                Intent intent2;
                Intent intent3;
                try {
                    synchronized (this) {
                        wait(100);
                    }
                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                        intent3 = new Intent(ExpenseManagerLoadingActivity.this, FirebaseAuthenticationActivity.class);
                        startActivity(intent3);
                        finish();
                    } else if (!MySharedPreferences.getBool(MySharedPreferences.KEY_IS_WELCOME_SCREEN_SHOWN)) {
                        intent2 = new Intent(ExpenseManagerLoadingActivity.this, MoreActivity.class);
                        startActivity(intent2);
                        finish();
                    } else {
                        intent = new Intent(ExpenseManagerLoadingActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (InterruptedException e) {
                    try {
                        e.printStackTrace();
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            intent3 = new Intent(ExpenseManagerLoadingActivity.this, FirebaseAuthenticationActivity.class);
                        } else if (!MySharedPreferences.getBool(MySharedPreferences.KEY_IS_WELCOME_SCREEN_SHOWN)) {
                            intent2 = new Intent(ExpenseManagerLoadingActivity.this, MoreActivity.class);
                        } else {
                            intent = new Intent(ExpenseManagerLoadingActivity.this, DashboardActivity.class);
                        }
                    } catch (Throwable th) {
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            startActivity(new Intent(ExpenseManagerLoadingActivity.this, FirebaseAuthenticationActivity.class));
                            finish();
                        } else if (!MySharedPreferences.getBool(MySharedPreferences.KEY_IS_WELCOME_SCREEN_SHOWN)) {
                            startActivity(new Intent(ExpenseManagerLoadingActivity.this, MoreActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(ExpenseManagerLoadingActivity.this, DashboardActivity.class));
                            finish();
                        }
                        throw th;
                    }
                }
            }
        }.start();
    }

    public void reloadFirebase() {
        checkForApplicationUpdate();
    }

    public void destroyActivity() {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}