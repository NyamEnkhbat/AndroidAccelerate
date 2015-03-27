package eu.isawsm.accelerate.ax.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import eu.isawsm.accelerate.Model.Car;
import eu.isawsm.accelerate.Model.Driver;

public class AxPreferences {

    private static final String DRIVER = "driver";
    public static Gson gson;
    private static final String AX_SERVER_ADDRESS="AxServerAddress";
    private static final String DRIVER_NAME = "DriverName";
    private static final String CARS = "cars";

    static {
        gson = new Gson();
    }

    private static void putSharedPreferencesString(Context context, String key, String val){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit=preferences.edit();
        edit.putString(key, val);
        edit.apply();
    }

    private static String getSharedPreferencesString(Context context, String key, String _default){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, _default);
    }

    public static void setDriver(Context context, Driver driver) {
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit=preferences.edit();
        edit.putString(DRIVER, gson.toJson(driver));
        edit.apply();
    }

    public static Driver getDriver(Context context) {
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        return gson.fromJson(preferences.getString(DRIVER, null),Driver.class);
    }

    public static void putServerAddress(Context context, String address){
        putSharedPreferencesString(context, AxPreferences.AX_SERVER_ADDRESS, address);
    }

    public static String getServerAddress(Context context) {
        return getSharedPreferencesString(context, AxPreferences.AX_SERVER_ADDRESS, "");
    }
}