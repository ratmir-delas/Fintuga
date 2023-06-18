package com.tugasoft.fintuga.service;

import android.app.IntentService;
import android.content.Intent;

public class SendFCMNotificationService extends IntentService {
    public SendFCMNotificationService() {
        super("SendFCMNotificationService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        throw new UnsupportedOperationException("Method not decompiled: com.tugasoft.fintuga.service.SentFCMNotification\nMessage: " + intent.getStringExtra("message"));
    }
}
