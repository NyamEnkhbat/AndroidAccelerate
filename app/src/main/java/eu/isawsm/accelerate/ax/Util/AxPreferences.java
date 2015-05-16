package eu.isawsm.accelerate.ax.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import eu.isawsm.accelerate.Model.AxUser;

public class AxPreferences {

    private static final String DRIVER = "driver";
    private static final String AX_SERVER_ADDRESS = "AxServerAddress";
    private static final String AXUSER = "AxUser";
    ;
    public static Gson gson;
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

    public static void setAxIUser(Context context, AxUser user) {
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit=preferences.edit();
        edit.putString(AXUSER, gson.toJson(user.getCopy()));
        edit.apply();
    }


    public static AxUser getAxIUser(Context context) {
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        return gson.fromJson(preferences.getString(AXUSER, null), AxUser.class);
    }


    public static void putServerAddress(Context context, String address){
        putSharedPreferencesString(context, AxPreferences.AX_SERVER_ADDRESS, address);
    }

    public static String getServerAddress(Context context) {
        return getSharedPreferencesString(context, AxPreferences.AX_SERVER_ADDRESS, "");
    }
}