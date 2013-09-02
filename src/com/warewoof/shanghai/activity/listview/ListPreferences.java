package com.warewoof.shanghai.activity.listview;

import java.util.Date;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import com.warewoof.shanghai.function.LogFx;
import android.widget.Toast;

import com.warewoof.shanghai.R;
import com.warewoof.shanghai.file.DLPref;
import com.warewoof.shanghai.file.DownloadCatalog;


public class ListPreferences extends PreferenceActivity {

	private final String TAG = "ListPreferences";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.list_preferences);
		
		/* Update the Last Modified date in the "Update Now" menu option */
        long lastModLong = DLPref.getSPCatalogDownloadTimestamp(ListPreferences.this);
        
        Preference lastModMenu = (Preference) findPreference("updateNow");
        String tempText = lastModMenu.getSummary().toString();
        Date lastModDate = new Date(lastModLong);
        tempText = tempText + "\nLast update: " + (DLPref.getSPCatalogNeverDownloaded(this) ? "Never updated":lastModDate.toLocaleString());
        LogFx.debug(TAG, "Shared setting lastMod " + lastModDate.toLocaleString() + " " + String.valueOf(lastModLong));
        lastModMenu.setSummary(tempText);
        
		Preference customPref = (Preference) findPreference("updateNow");
		customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {

				if (isOnline()) {
					DownloadCatalog dc = new DownloadCatalog(ListPreferences.this);
					//mProgressDialog = new ProgressDialog(Preferences.this);
					dc.startDownload();
					
				} else {
					Toast.makeText(ListPreferences.this, "Please enable internet connection", Toast.LENGTH_SHORT).show();
					
				}
				return true;
			}
			

		} );

	}

	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

	    return cm.getActiveNetworkInfo() != null && 
	       cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
	
	
            
}