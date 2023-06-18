package com.tugasoft.fintuga.ads;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AdsPreferences {

    private Activity activity;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public AdsPreferences(Activity activity) {
        this.activity = activity;
        this.sharedPreferences = activity.getSharedPreferences("AudiobookAds", Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public void addCount() {
        editor.putInt("counter", getCount() + 1);
        editor.apply();
    }

    public void resetCount() {
        editor.putInt("counter", 0);
        editor.apply();
    }

    public int getCount() {
        return sharedPreferences.getInt("counter", 0);
    }

}
