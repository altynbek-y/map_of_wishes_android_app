package com.unicef.dreamapp2.application;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferenceManager {

    public final static String USER_TYPE = "user_type";
    public final static String REGULAR_USER = "Customers";
    public final static String VOLUNTEER = "Volunteers";

    private static SharedPreferences sharedPreferences = null;

        public static SharedPreferences getMySharedPreferences(Context context) {
            if (sharedPreferences == null) {
                sharedPreferences = context.getSharedPreferences("dreamappAppPreferences", Context.MODE_PRIVATE);
            }
            return sharedPreferences;
        }
}