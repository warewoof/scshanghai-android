package com.warewoof.shanghai;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.warewoof.shanghai.function.LogFx;
import com.warewoof.shanghai.object.MapItem;



public class SPVar extends Application {
	/* Shared Preference settings */ 
	public static final String SP_FILE_NAME = "test";  //share preference file name
	//public static final String SP_KEY_CATALOG_LAST_MOD_STRING = "catalogLastModString";  //share preference file name
	public static final String SP_KEY_CATALOG_LAST_MOD_LONG = "catalogLastModLong";  //share preference file name
	public static final String SP_KEY_CATALOG_DL_TIMESTAMP_LONG = "catalogDLTimestamp";
	public static final String SP_KEY_CATALOG_DL_MANUAL_ALLOWED_BOOLEAN = "catalogDLManualAllowed";
	public static final String SP_KEY_CALENDAR_LAST_MOD_STRING = "calendarLastModString";  //share preference file name
	public static final String SP_KEY_CALENDAR_LAST_MOD_LONG = "calendarLastModLong";  //share preference file name
	public static final String SP_KEY_UPDATED_CATALOG_BOOLEAN = "updatedCatalogBoolean";  //share preference file name
	public static final String SP_KEY_UPDATED_CALENDAR_BOOLEAN = "updatedCalendarBoolean";  //share preference file name
	public static final String SP_KEY_DOWNLOADING_CATALOG_BOOLEAN = "downloadingCatalogBoolean";  //share preference file name
	public static final String SP_KEY_LAST_RUN_APP_VERSION = "lastRunAppVersion";  //share preference file name
	public static final String SP_KEY_FONT_PREF_UPDATED = "fontPrefUpdated";  //share preference file name
	
	private final static String TAG = "SPVar";	
	public static Context context;
	private static SharedPreferences defaultPreferences;
	public static ArrayList<Object> tempMapItems = null;
	private static HashMap<String, ArrayList<MapItem>> mapHashMap = new HashMap<String, ArrayList<MapItem>>();
	
	@Override
	public void onCreate() {
		super.onCreate();		
		context = this;
		defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	public static void setMapItems(String key, ArrayList<MapItem> mapItems) {
		//tempMapItems = mapItems;
		mapHashMap.put(key, mapItems);		
	}
	
	public static ArrayList<MapItem> getMapItems(String key) {
		return mapHashMap.get(key);
	}
	
	public static void destroyMapItems(String key) {
		mapHashMap.remove(key);
	}
	
	public static void setMeasureTime(Context c) {
		SharedPreferences settings = c.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("measuretimeeee", android.os.SystemClock.uptimeMillis());
        editor.commit();
		
	}
	
	public static long getMeasureTime(Context c) {
		SharedPreferences settings = c.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return settings.getLong("measuretimeeee", 0);
	}
	
	
	public static float getPrefTextTitleSize() {
		//LogFx.debug(TAG, defaultPreferences.getString("fontpref", "1.0"));
		float textPct = Float.valueOf(defaultPreferences.getString("fontpref", "1.0"));
		return (GlVar.CHILD_SUBSECTION_TITLE_TEXT_DEFAULT_SIZE * textPct);		
	}
	
	public static float getPrefTextValueSize() {
		//SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(GlConst.this);
		float textPct = Float.valueOf(defaultPreferences.getString("fontpref", "1.0"));		
		return (GlVar.CHILD_SUBSECTION_VALUE_TEXT_DEFAULT_SIZE * textPct);		
	}
	
	public static float getPrefTextValueModifier() {
		//SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(GlConst.this);
	
		return Float.valueOf(defaultPreferences.getString("fontpref", "1.0"));			
	}
	
	public static float getPrefImageTextSize() {
		//SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(GlConst.this);
		float textPct = Float.valueOf(defaultPreferences.getString("fontpref", "1.0"));		
		return (GlVar.CHILD_SUBSECTION_IMAGE_TEXT_DEFAULT_SIZE * textPct);		
	}
	
	public static void setFontUpdated(Context c, boolean status) {
		SharedPreferences settings = c.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SP_KEY_FONT_PREF_UPDATED, status);
        editor.commit();
		
	}
	
	public static boolean getFontUpdated(Context c) {
		SharedPreferences settings = c.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(SP_KEY_FONT_PREF_UPDATED, false);
	}


	public static boolean getNativeMapOption(Context c) {
		//SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(c);
		boolean option = defaultPreferences.getBoolean("nativeMapOption", false);
		LogFx.debug(TAG, "Checking nativeMapOption preference: " + String.valueOf(option));
		return option;
	}
	
	
	
}