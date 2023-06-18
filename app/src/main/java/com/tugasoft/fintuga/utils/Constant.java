package com.tugasoft.fintuga.utils;

import java.text.DecimalFormat;

public class Constant {
    public static final String CONST_IMAGE_STORE_CONTAINER = "PROOFS";
    public static final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    public static int ACount = 0;
    public static int BCount = 0;
    public static int CCount = 0;
    public static String CURRENT_PAGE = "carrentpage";
    public static String CURRENT_TAB = "currenttab";
    public static String DAY_FORMAT = "dd";
    public static String FIREBASE_NODE_EXPENSE = "expenses";
    public static String FIREBASE_NODE_CATEGORY = "categories";
    public static String FIREBASE_NODE_USER = "expenses";
    public static String FIREBASE_NOTIFICATION_MESSAGE_TOPIC = "Userssss";
    public static String MONTH_FORMAT = "MMM";
    public static String MONTH_YEAR_FORMAT = null;
    public static int REMINDER_HOUR = 21;
    public static int REMINDER_MIN = 0;
    public static String TAB_REPORT = "tab_report";
    public static boolean TYPE_EXPENSE = true;
    public static boolean TYPE_INCOME = false;
    public static String YEAR_FORMAT = "yyyy";
    public static String DATE_FORMAT = (DAY_FORMAT + " " + MONTH_FORMAT + " " + YEAR_FORMAT);
    public static String[] ITEM_HALF_YEAR = {"Jan-June", "July-December"};
    public static String[] ITEM_QUARTERS = {"Jan-March", "April-June", "July-September", "October-December"};

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(MONTH_FORMAT);
        sb.append(" ");
        sb.append(YEAR_FORMAT);
        MONTH_YEAR_FORMAT = sb.toString();
    }
}