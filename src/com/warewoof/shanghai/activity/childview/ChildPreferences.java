package com.warewoof.shanghai.activity.childview;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import com.warewoof.shanghai.function.LogFx;

import com.warewoof.shanghai.R;
import com.warewoof.shanghai.SPVar;


public class ChildPreferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

	private final String TAG = "Preferences";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.child_preferences);
	
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	LogFx.debug(TAG, "onSharedPreferenceChanged " + key);
    	SPVar.setFontUpdated(ChildPreferences.this, true);
    }



	
            
}