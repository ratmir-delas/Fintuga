package com.tugasoft.fintuga.utils;

import android.content.SharedPreferences;

import com.tugasoft.fintuga.application.AppCore;

public class MySharedPreferences {
    public static final String KEY_BANK_NAME_LIST = "KEY_BANK_NAME_LIST";
    public static final String KEY_CURRENCY_TYPE = "KEY_CURRENCY_TYPE";
    public static final String KEY_EXPENSE_CATEGORY = "KEY_EXPENSE_CATEGORY";
    public static final String KEY_INCOME_CATEGORY = "KEY_INCOME_CATEGORY";
    public static final String KEY_IS_ADMIN_USER = "KEY_IS_ADMIN_USER";
    public static final String KEY_IS_REMINDER_ENABLE = "KEY_IS_REMINDER_ENABLE";
    public static final String KEY_IS_REMINDER_SET_ON_APP_LAUNCH = "KEY_IS_REMINDER_SET_ON_APP_LAUNCH";
    public static final String KEY_IS_USER_PROFILE_FETCHED = "KEY_IS_USER_PROFILE_FETCHED";
    public static final String KEY_IS_WELCOME_SCREEN_SHOWN = "KEY_IS_WELCOME_SCREEN_SHOWN";
    public static final String KEY_REMINDER_HOUR = "KEY_REMINDER_HOUR";
    public static final String KEY_REMINDER_MIN = "KEY_REMINDER_MIN";
    public static final String KEY_USER_NAME = "KEY_USER_NAME";
    public static final String PREFS_NAME = "income_expense_prefs";
    private static SharedPreferences PREF_APP;
    private static SharedPreferences.Editor editor;

    private MySharedPreferences() {
    }

    public static void getInstance() {
        if (PREF_APP == null) {
            SharedPreferences sharedPreferences = AppCore.getAppContext().getSharedPreferences(PREFS_NAME, 0);
            PREF_APP = sharedPreferences;
            editor = sharedPreferences.edit();
        }
    }

    public static void setInt(String str, int i) {
        editor.putInt(str, i);
        editor.commit();
    }

    public static int getInt(String str, int i) {
        return PREF_APP.getInt(str, i);
    }

    public static void setStr(String str, String str2) {
        editor.putString(str, str2);
        editor.commit();
    }

    public static String getStr(String str, String str2) {
        return PREF_APP.getString(str, str2);
    }

    public static void setBool(String str, boolean z) {
        editor.putBoolean(str, z);
        editor.commit();
    }

    public static boolean getBool(String str) {
        return PREF_APP.getBoolean(str, false);
    }

    public static int get_hour(String str) {
        return PREF_APP.getInt(str, Constant.REMINDER_HOUR);
    }

    public static int get_min(String str) {
        return PREF_APP.getInt(str, Constant.REMINDER_MIN);
    }

    public static void clearSP() {
        boolean bool = getBool(KEY_IS_REMINDER_SET_ON_APP_LAUNCH);
        boolean bool2 = getBool(KEY_IS_REMINDER_ENABLE);
        int i = get_hour(KEY_REMINDER_HOUR);
        int i2 = get_min(KEY_REMINDER_MIN);
        editor.clear();
        editor.commit();
        setBool(KEY_IS_REMINDER_SET_ON_APP_LAUNCH, bool);
        setBool(KEY_IS_REMINDER_ENABLE, bool2);
        setInt(KEY_REMINDER_HOUR, i);
        setInt(KEY_REMINDER_MIN, i2);
    }
}