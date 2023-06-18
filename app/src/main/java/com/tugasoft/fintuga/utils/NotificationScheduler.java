package com.tugasoft.fintuga.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.tugasoft.fintuga.R;

import java.util.Calendar;

public class NotificationScheduler {
    public static final int DAILY_REMINDER_REQUEST_CODE = 145;

    public static void setReminder(Context context, Class<?> cls, int i, int i2) {
        Calendar instance = Calendar.getInstance();
        Calendar instance2 = Calendar.getInstance();
        instance2.set(11, i);
        instance2.set(12, i2);
        instance2.set(13, 0);
        cancelReminder(context, cls);
        if (instance2.before(instance)) {
            instance2.add(5, 1);
        }
        context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, cls), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setInexactRepeating(AlarmManager.RTC_WAKEUP, instance2.getTimeInMillis(), 86400000, PendingIntent.getBroadcast(context, DAILY_REMINDER_REQUEST_CODE, new Intent(context, cls), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));
    }

    public static void cancelReminder(Context context, Class<?> cls) {
        context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, cls), 2, 1);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, DAILY_REMINDER_REQUEST_CODE, new Intent(context, cls), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(broadcast);
        broadcast.cancel();
    }

    public static void showNotification(Context context, Class<?> cls, String str, String str2) {
        Uri defaultUri = RingtoneManager.getDefaultUri(2);
        Intent intent = new Intent(context, cls);
        intent.setFlags(1 << 26);
        TaskStackBuilder create = TaskStackBuilder.create(context);
        create.addParentStack(cls);
        create.addNextIntent(intent);
        PendingIntent pendingIntent = create.getPendingIntent(DAILY_REMINDER_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("168", "Reminder Notification", NotificationManager.IMPORTANCE_UNSPECIFIED);
            notificationChannel.setDescription("We will reminds you to make entries of your transactions.");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(-16776961);
            notificationChannel.enableVibration(false);
            notificationChannel.setLockscreenVisibility(1);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(str2);
        NotificationCompat.Builder priority = new NotificationCompat.Builder(context, "168").setContentTitle(str).setContentText(str2).setAutoCancel(true).setSound(defaultUri).setContentIntent(pendingIntent).setStyle(bigTextStyle).setWhen(System.currentTimeMillis()).setPriority(2);
        if (Build.VERSION.SDK_INT >= 21) {
            priority.setSmallIcon(R.drawable.img_transparent_notification_icon);
            priority.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        } else {
            priority.setSmallIcon(R.drawable.img_notification_icon);
        }
        notificationManager.notify(DAILY_REMINDER_REQUEST_CODE, priority.build());
    }
}
