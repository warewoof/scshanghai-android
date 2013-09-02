
package com.warewoof.shanghai.file;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.warewoof.shanghai.SPVar;
import com.warewoof.shanghai.function.LogFx;




public class DLPref {

	public final static int DL_NO = 0;  						// do not know why user cannot download
	public final static int DL_OK_INTERNET = 1;  				// auto download, internet connected
	public final static int DL_OK_WIFI = 2;  					// auto download, wifi connected
	public final static int DL_NOTIFY_WIFI_CONNECT = 3;  		// auto download, but wifi not connected
	public final static int DL_NOTIFY_INTERNET_CONNECT = 4;  	// auto download, but no internet connection
	public final static int DL_ONLINE = 5;  					// internet connected
	public final static int DL_OFFLINE = 6;  					// no internet connection



	public static int Download(Context mContext) {
		final String TAG = "DLPref.Download";

		SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		boolean autoDownload = defaultPreferences.getBoolean("autoDownloadCheckBox", false);
		LogFx.debug(TAG, "Auto Download preference: " + String.valueOf(autoDownload));

		if (autoDownload) {					
			String downloadMethod = defaultPreferences.getString("connectionType", "none");
			LogFx.debug(TAG, "Download type preference: " + downloadMethod);

			ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo;
			if (downloadMethod.equalsIgnoreCase("download_wifiOnly")) {  // these settins are found in res/values/array.xml
				activeNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if (activeNetworkInfo.isConnectedOrConnecting()) {
					LogFx.debug(TAG, "Network is connected by WIFI");
					return DL_OK_WIFI;
				} else {
					return DL_NOTIFY_WIFI_CONNECT;
				}
			} else if (downloadMethod.equalsIgnoreCase("download_cellWifi")) {
				activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
				if (activeNetworkInfo.isConnectedOrConnecting()) {
					LogFx.debug(TAG, "Network is connected by MOBILE (possibly WIFI)");
					return DL_OK_INTERNET;
				} else {
					return DL_NOTIFY_INTERNET_CONNECT;					
				}
			}					
		} else {
			return DL_NOTIFY_INTERNET_CONNECT;
		}
		LogFx.debug(TAG, "Auto Download preference: " + String.valueOf(autoDownload));
		return DL_NO;

	}

	public static long getSPLastRunAppVersion(Context c) {
		SharedPreferences settings = c.getSharedPreferences(SPVar.SP_FILE_NAME, Context.MODE_PRIVATE);
        return settings.getLong(SPVar.SP_KEY_LAST_RUN_APP_VERSION, 0);
	}
	
	public static void setSPCurrentRunAppVersion(Context c, long appVersion) {
		SharedPreferences settings = c.getSharedPreferences(SPVar.SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(SPVar.SP_KEY_LAST_RUN_APP_VERSION, appVersion);
        editor.commit();
	}
	
	public static boolean getSPCatalogUpdated(Context c) {
		SharedPreferences settings = c.getSharedPreferences(SPVar.SP_FILE_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(SPVar.SP_KEY_UPDATED_CATALOG_BOOLEAN, false);
	}
	public static void setSPCatalogUpdated(Context c, boolean status) {
		SharedPreferences settings = c.getSharedPreferences(SPVar.SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SPVar.SP_KEY_UPDATED_CATALOG_BOOLEAN, status);
        editor.commit();
	}		
	
	public static long getSPCatalogDownloadTimestamp(Context c) {
		SharedPreferences settings = c.getSharedPreferences(SPVar.SP_FILE_NAME, Context.MODE_PRIVATE);
		return settings.getLong(SPVar.SP_KEY_CATALOG_DL_TIMESTAMP_LONG, 0);
	}
	
	public static void setSPCatalogDownloadTimestamp(Context c, long time) {
		SharedPreferences settings = c.getSharedPreferences(SPVar.SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(SPVar.SP_KEY_CATALOG_DL_TIMESTAMP_LONG, time);
        editor.commit();
	}
	
	public static boolean getSPCatalogNeverDownloaded(Context c) {
		SharedPreferences settings = c.getSharedPreferences(SPVar.SP_FILE_NAME, Context.MODE_PRIVATE);	
		return settings.getBoolean(SPVar.SP_KEY_CATALOG_DL_MANUAL_ALLOWED_BOOLEAN, false);
	}
	
	public static void setSPCatalogNeverDownloaded(Context c, boolean allowed) {
		SharedPreferences settings = c.getSharedPreferences(SPVar.SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SPVar.SP_KEY_CATALOG_DL_MANUAL_ALLOWED_BOOLEAN, allowed);
        editor.commit();
	}
	/*
	public static long getSPCatalogLastMod(Context c) {
		SharedPreferences settings = c.getSharedPreferences(SPVar.SP_FILE_NAME, Context.MODE_PRIVATE);
		//String lastModString = settings.getString(SPVar.SP_KEY_CATALOG_LAST_MOD_STRING, "");
		return settings.getLong(SPVar.SP_KEY_CATALOG_LAST_MOD_LONG, 0);
	}
	
	public static void setSPCatalogLastMod(Context c, long lastMod) {
		SharedPreferences settings = c.getSharedPreferences(SPVar.SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(SPVar.SP_KEY_CATALOG_LAST_MOD_LONG, lastMod);
        editor.commit();
	}
	*/
	
	public static void setSPCalendarLastMod(Context c, long lastMod) {
        SharedPreferences settings = c.getSharedPreferences(SPVar.SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(SPVar.SP_KEY_CALENDAR_LAST_MOD_STRING, lastMod);
        editor.commit();
	}
	
	public static long getSPCalendarLastMod(Context c) {
		SharedPreferences settings = c.getSharedPreferences(SPVar.SP_FILE_NAME, Context.MODE_PRIVATE);
		return settings.getLong(SPVar.SP_KEY_CALENDAR_LAST_MOD_LONG, 0);
	}
	
	public static void setSPCalendarUpdated(Context c, boolean status) {
    	SharedPreferences settings = c.getSharedPreferences(SPVar.SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SPVar.SP_KEY_UPDATED_CALENDAR_BOOLEAN, status);
        editor.commit();
	}
	
	public static void setSPCatalogDownloadingStatus(Context c, boolean status) {
		SharedPreferences settings = c.getSharedPreferences(SPVar.SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SPVar.SP_KEY_DOWNLOADING_CATALOG_BOOLEAN, status);
        editor.commit();
	}
	
	public static boolean getSPCatalogDownloadingStatus(Context c) {
		SharedPreferences settings = c.getSharedPreferences(SPVar.SP_FILE_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(SPVar.SP_KEY_DOWNLOADING_CATALOG_BOOLEAN, false);
	}
	
	
	public static int ConnectionOk(Context mContext) {
		final String TAG = "DLPref.ConnectionOk";
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo;
			activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
			if (activeNetworkInfo.isConnectedOrConnecting()) {
				return DL_ONLINE;
			} else {
				return DL_OFFLINE;					
			}
		} catch (Exception e) {
			LogFx.debug(TAG, "Exception:" + e.toString());
			return DL_NO;
		}
	}
}