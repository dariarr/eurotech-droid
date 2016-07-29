package com.martoff.esmart;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.martoff.esmart.extras.Constants;

import java.util.Locale;

/**
 * Created by Safeer on 14-May-16.
 */
public class AppController extends Application {

    private static AppController app_instance;
    public static String APP_LANGUAGE;
    public String TAG = "AppController";

    @Override
    public void onCreate() {
        super.onCreate();
        app_instance = this;

        //2 because if language is other than English or Russian then APP_LANGUAGE will be english.
        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
        APP_LANGUAGE = preferenceManager.getString(Constants.LANGUAGE_CODE, "2");

        if (isFirstLaunch()) {
            getLangAndCountry();
        } else {
            Log.e(TAG, "this is NOT first launch of app.");
        }
    }

    private boolean isFirstLaunch() {
        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferenceManager.getBoolean(Constants.IS_FIRST_LAUNCH, true)) {
            Log.e(TAG, "YES FIRST LAUNCH.");
            SharedPreferences.Editor editor = preferenceManager.edit();
            editor.putBoolean(Constants.IS_FIRST_LAUNCH, false);
            editor.apply();
            return true;
        } else {
            Log.e(TAG, "Not first launch.");
            return false;
        }
    }

    //saving language and country in preferences
    private void getLangAndCountry() {

        String language = Locale.getDefault().getLanguage();
        String language_code;
        Log.e(TAG, "DEVICE LANGUAGE :: " + language);

        String country = Locale.getDefault().getCountry();
        String country_code;
        Log.e(TAG, "DEVICE COUNTRY :: " + country);

        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferenceManager.edit();
        switch (language) {
            case "en":
                language_code = "2";
                Log.e(TAG, "LANGUAGE CODE TO SAVE : " + language_code);
                editor.putString(Constants.LANGUAGE_CODE, language_code);
                editor.apply();
                break;

            case "ru":
                language_code = "1";
                Log.e(TAG, "LANGUAGE CODE TO SAVE : " + language_code);
                editor.putString(Constants.LANGUAGE_CODE, language_code);
                editor.apply();
                break;

            default:
                language_code = "2";
                Log.e(TAG, "DEFAULT:: LANGUAGE CODE TO SAVE : " + language_code);
                editor.putString(Constants.LANGUAGE_CODE, language_code);
                editor.apply();
        }

//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//
//        String sim_country, iso3_country;
//        sim_country = telephonyManager.getSimCountryIso();
//        iso3_country = getResources().getConfiguration().locale.getISO3Country();
//
//        Log.e(TAG, "sim_country :: " + sim_country);
//        Log.e(TAG, "iso3_country :: " + iso3_country);
//
//        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = preferenceManager.edit();
//
//        switch (iso3_country) {
//            case "usa":
//                iso3_country = "2";
//                break;
//            case "rus":
//                iso3_country = "1";
//                break;
//            default:
//                iso3_country = "2";
//        }
//        editor.putString(Constants.SIM_COUNTRY, sim_country);
//        editor.putString(Constants.ISO3_COUNTRY, iso3_country);
//        editor.apply();
    }

    public static AppController getAppInstance() {
        return app_instance;
    }

    public static Context getAppContext() {
        return app_instance.getApplicationContext();
    }

    //checking whether internet is available
    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) app_instance.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
