package com.unicef.dreamapp2.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.yariksoffice.lingver.Lingver;

import java.util.Locale;

/**
 * @author Iman Augustine
 *
 * MyApplication extends MultiDexApplication.
 *
 * */

public class MyApplication extends MultiDexApplication {

    // Application instance
    private static MyApplication instance;
    private static SharedPreferences preferenceManager;

    // On creation
    @Override
    public void onCreate() {

        preferenceManager = MyPreferenceManager.getMySharedPreferences(this.getApplicationContext()); // Shared preferences
        String currentLanguage = preferenceManager.getString("locale", "ru"); // Getting app language
        assert currentLanguage != null; // If current language is not null
        Lingver.init(this, currentLanguage); // Initialize application language

        super.onCreate();
        instance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    // On configuration changed
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        preferenceManager = MyPreferenceManager.getMySharedPreferences(this.getApplicationContext());
        String currentLanguage = preferenceManager.getString("locale", "ru");
        // Create a new Locale object
        assert currentLanguage != null;
        Locale locale = new Locale(currentLanguage);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        super.onConfigurationChanged(newConfig);
    }

    // Returns the instance
    public static MyApplication getInstance() {
        return instance;
    }
}
