package com.example.my_timer_app;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtils {
    private static final String PREFS_FILE_NAME = "com.example.my_timer_app";

    public final static String START_DATE_TIME    = "start_dt";
    public final static String END_DATE_TIME      = "end_dt";

    public static void setVal(Context context, String key, long value){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getValue(Context context, String key){
        long val = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE).getLong(key,0);
        return val;
    }

}