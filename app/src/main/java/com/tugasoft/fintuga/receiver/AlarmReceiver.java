package com.tugasoft.fintuga.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tugasoft.fintuga.activity.ExpenseManagerLoadingActivity;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.NotificationScheduler;
import com.tugasoft.fintuga.utils.MySharedPreferences;

public class AlarmReceiver extends BroadcastReceiver {
    String TAG = "AlarmReceiver";

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null || context == null || !intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")) {
            Log.d(this.TAG, "onReceive: ");
            NotificationScheduler.showNotification(context, ExpenseManagerLoadingActivity.class, context.getString(R.string.app_name), "Add your today's transaction to keep track of your budget.");
            return;
        }
        Log.d(this.TAG, "onReceive: BOOT_COMPLETED");
        if (MySharedPreferences.getBool(MySharedPreferences.KEY_IS_REMINDER_ENABLE)) {
            NotificationScheduler.setReminder(context, AlarmReceiver.class, MySharedPreferences.get_hour(MySharedPreferences.KEY_REMINDER_HOUR), MySharedPreferences.get_min(MySharedPreferences.KEY_REMINDER_MIN));
        }
    }
}
