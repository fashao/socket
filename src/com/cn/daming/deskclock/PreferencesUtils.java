package com.cn.daming.deskclock;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * PreferencesUtils, easy to get or put data
 * <ul>
 * <strong>Preference Name</strong>
 * <li>you can change preference name by {@link #PREFERENCE_NAME}</li>
 * </ul>
 * <ul>
 * <strong>Put Value</strong>
 * <li>put string {@link #putString(Context, String, String)}</li>
 * <li>put int {@link #putInt(Context, String, int)}</li>
 * <li>put long {@link #putLong(Context, String, long)}</li>
 * <li>put float {@link #putFloat(Context, String, float)}</li>
 * <li>put boolean {@link #putBoolean(Context, String, boolean)}</li>
 * </ul>
 * <ul>
 * <strong>Get Value</strong>
 * <li>get string {@link #getString(Context, String)}, {@link #getString(Context, String, String)}</li>
 * <li>get int {@link #getInt(Context, String)}, {@link #getInt(Context, String, int)}</li>
 * <li>get long {@link #getLong(Context, String)}, {@link #getLong(Context, String, long)}</li>
 * <li>get float {@link #getFloat(Context, String)}, {@link #getFloat(Context, String, float)}</li>
 * <li>get boolean {@link #getBoolean(Context, String)}, {@link #getBoolean(Context, String, boolean)}</li>
 * </ul>
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-3-6
 */
public class PreferencesUtils extends Application{

    public static String PREFERENCE_NAME = "config";
    public static String PHONE = "phone";
    
    public static final String BASEURL="http://1.arduinosocket.sinaapp.com/yang/";
//    private PreferencesUtils() {
//        throw new AssertionError();
//    }

    /**
     * put string preferences
     * 
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    static PreferencesUtils preferencesUtils ; 
//    public void onCreate() {
//    	// TODO Auto-generated method stub
//    	super.onCreate();
//    	preferencesUtils=this;
//    	}
    @Override
    public void onCreate() {
    	super.onCreate();
    	
    	preferencesUtils=this;

    }
    public static PreferencesUtils getinstance() {
			return preferencesUtils;
		
	}
    public static boolean putString( String key, String value) {
        SharedPreferences settings = getinstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }


    /**
     * get string preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a string
     */
    public static String getString( String key, String defaultValue) {
        SharedPreferences settings = getinstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    /**
     * put int preferences
     * 
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putInt(String key, int value) {
        SharedPreferences settings = getinstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        return editor.commit();
    }


    /**
     * get int preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a int
     */
    public static int getInt( String key, int defaultValue) {
        SharedPreferences settings = getinstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getInt(key, defaultValue);
    }

    /**
     * put long preferences
     * 
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putLong( String key, long value) {
        SharedPreferences settings =getinstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    /**

    /**
     * get long preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a long
     */
    public static long getLong( String key, long defaultValue) {
        SharedPreferences settings = getinstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getLong(key, defaultValue);
    }

    /**
     * put float preferences
     * 
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putFloat( String key, float value) {
        SharedPreferences settings = getinstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }


    /**
     * get float preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a float
     */
    public static float getFloat(String key, float defaultValue) {
        SharedPreferences settings = getinstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getFloat(key, defaultValue);
    }

    /**
     * put boolean preferences
     * 
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putBoolean(String key, boolean value) {
        SharedPreferences settings = getinstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }


    /**
     * get boolean preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     *         this name that is not a boolean
     */
    public static boolean getBoolean( String key, boolean defaultValue) {
        SharedPreferences settings = getinstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(key, defaultValue);
    }
    public static boolean isWifiConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getinstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }
     
        return false ;
    }
    /**
     * 检测网络是否可用
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
}
