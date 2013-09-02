package com.warewoof.shanghai.function;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.warewoof.shanghai.GlVar;
import com.warewoof.shanghai.SPVar;
import com.warewoof.shanghai.activity.DisplayChild;
import com.warewoof.shanghai.activity.DisplayList;
import com.warewoof.shanghai.activity.DisplayMap;
import com.warewoof.shanghai.activity.DisplayMapV2;
import com.warewoof.shanghai.activity.DisplaySearch;
import com.warewoof.shanghai.activity.DisplayTaxiCard;
import com.warewoof.shanghai.activity.childview.ChildPreferences;
import com.warewoof.shanghai.activity.listview.ListPreferences;
import com.warewoof.shanghai.object.ChildAttribute;
import com.warewoof.shanghai.object.ListItem;
import com.warewoof.shanghai.object.MapItem;


public class ActivityFx {
	//private static final String TAG = "ActivityFx";

	public static void openChild(Context c,
			String parentName,
			String childName,
			int color,
			boolean disableSearch,
			boolean disableShare,
			boolean disableMap,
			boolean noBack) {

		ArrayList<ChildAttribute> childAtts = new ArrayList<ChildAttribute>();
		childAtts = XmlFx.parseChild(c, parentName, childName);

		Intent i = new Intent(c, DisplayChild.class);
		i.putExtra(GlVar.INTENT_EXTRA_OBJECT_VAR, childAtts);	    
		i.putExtra(GlVar.INTENT_EXTRA_TITLE_VAR, parentName);
		i.putExtra(GlVar.INTENT_EXTRA_SEARCH_NAME_VAR, childName);
		i.putExtra(GlVar.INTENT_EXTRA_SECTION_NAME_VAR, childName);
		i.putExtra(GlVar.INTENT_EXTRA_NO_BACK_BOOL, noBack);
		i.putExtra(GlVar.INTENT_EXTRA_IS_CHILD_BOOL, true);
		i.putExtra(GlVar.INTENT_EXTRA_CHILD_TAB_COLOR, color);
		i.putExtra(GlVar.INTENT_EXTRA_SEARCH_DISABLE_BOOL, disableSearch);
		i.putExtra(GlVar.INTENT_EXTRA_SHARE_DISABLE_BOOL, disableShare);
		i.putExtra(GlVar.INTENT_EXTRA_MAP_DISABLE_BOOL, disableMap);

		c.startActivity(i);
	}
	
	public static void openChildWithHighlightAOI(Context c,
			String parentName,
			String childName,
			int color,
			boolean disableSearch,
			boolean disableShare,
			boolean disableMap,
			boolean noBack,
			String AOI) {

		ArrayList<ChildAttribute> childAtts = new ArrayList<ChildAttribute>();
		childAtts = XmlFx.parseChild(c, parentName, childName);

		Intent i = new Intent(c, DisplayChild.class);
		i.putExtra(GlVar.INTENT_EXTRA_OBJECT_VAR, childAtts);	    
		i.putExtra(GlVar.INTENT_EXTRA_TITLE_VAR, parentName);
		i.putExtra(GlVar.INTENT_EXTRA_SEARCH_NAME_VAR, childName);
		i.putExtra(GlVar.INTENT_EXTRA_SECTION_NAME_VAR, childName);
		i.putExtra(GlVar.INTENT_EXTRA_NO_BACK_BOOL, noBack);
		i.putExtra(GlVar.INTENT_EXTRA_IS_CHILD_BOOL, true);
		i.putExtra(GlVar.INTENT_EXTRA_CHILD_TAB_COLOR, color);
		i.putExtra(GlVar.INTENT_EXTRA_SEARCH_DISABLE_BOOL, disableSearch);
		i.putExtra(GlVar.INTENT_EXTRA_SHARE_DISABLE_BOOL, disableShare);
		i.putExtra(GlVar.INTENT_EXTRA_MAP_DISABLE_BOOL, disableMap);
		i.putExtra(GlVar.INTENT_EXTRA_HIGHLIGHT_AOI, AOI);

		c.startActivity(i);
	}

	public static void openList(Context c, String parentName, String searchName) {

		openList(c, parentName, searchName, false, false, false, false);

	}

	public static void openList(Context c, String parentName, String searchName, 
			boolean disableSearch, boolean disableShare, boolean disableMap) {

		openList(c, parentName, searchName, disableSearch, disableShare, disableMap, false);

	}

	public static void openList(Context c, String parentName, String searchName, 
			boolean disableSearch, boolean disableShare, boolean disableMap,
			boolean noBack) {

		ArrayList<ListItem> listItems = new ArrayList<ListItem>();		
		listItems = XmlFx.parseList(c, parentName, searchName);		
		Intent i = new Intent(c, DisplayList.class);		
		i.putExtra(GlVar.INTENT_EXTRA_OBJECT_VAR, listItems);
		i.putExtra(GlVar.INTENT_EXTRA_TITLE_VAR, searchName);
		i.putExtra(GlVar.INTENT_EXTRA_NO_BACK_BOOL, false);
		i.putExtra(GlVar.INTENT_EXTRA_SECTION_NAME_VAR, searchName);
		i.putExtra(GlVar.INTENT_EXTRA_PARENT_NAME_VAR, parentName);
		i.putExtra(GlVar.INTENT_EXTRA_SEARCH_DISABLE_BOOL, disableSearch);
		i.putExtra(GlVar.INTENT_EXTRA_SHARE_DISABLE_BOOL, disableShare);
		i.putExtra(GlVar.INTENT_EXTRA_MAP_DISABLE_BOOL, disableMap);
		i.putExtra(GlVar.INTENT_EXTRA_NO_BACK_BOOL, noBack);
		c.startActivity(i);		
	}	

	public static void openMap(Context c, String sectionName, int spacerColorHideCode, ArrayList<MapItem> mapItems) {		
		openMap(c, sectionName, spacerColorHideCode, mapItems, false);		
	}

	public static void openMap(Context c, String sectionName, int spacerColorHideCode, ArrayList<MapItem> mapItems, boolean requestedByChild) {
		SPVar.setMeasureTime(c);
		Intent mv;
		if (isGoogleMapsInstalled(c)) {
			mv = new Intent(c, DisplayMapV2.class);
		} else {
			mv = new Intent(c, DisplayMap.class);
		}
		
		mv.putExtra(GlVar.INTENT_EXTRA_TITLE_VAR, sectionName);  //using private here since Name attribute could come after address attribute
		mv.putExtra(GlVar.INTENT_EXTRA_CHILD_TAB_COLOR, spacerColorHideCode);
		//mv.putExtra(GlVar.INTENT_EXTRA_OBJECT_VAR, mapItems);
		String mapKey = sectionName + String.valueOf(mapItems.size()) + String.valueOf(android.os.SystemClock.uptimeMillis());
		SPVar.setMapItems(mapKey, mapItems);
		mv.putExtra(GlVar.INTENT_EXTRA_OBJECT_KEY, mapKey);
		mv.putExtra(GlVar.INTENT_EXTRA_MAP_REQUEST_FROM_CHILD, requestedByChild);		  
		c.startActivity(mv);
	}


	public static void openTaxiCard(Context c, String title, String displayText, int mTabColor) {

		Intent i = new Intent(c, DisplayTaxiCard.class);
		i.putExtra(GlVar.INTENT_EXTRA_TITLE_VAR, title);
		i.putExtra(GlVar.INTENT_EXTRA_TAXI_CARD_STRING, displayText);	    
		i.putExtra(GlVar.INTENT_EXTRA_CHILD_TAB_COLOR, mTabColor);
		i.putExtra(GlVar.INTENT_EXTRA_NO_BACK_BOOL, false);
		i.putExtra(GlVar.INTENT_EXTRA_IS_CHILD_BOOL, true);		

		c.startActivity(i);
	}

	public static void openSearch(Context c, String parentName, String searchName) {

		Intent i = new Intent(c, DisplaySearch.class);
		i.putExtra(GlVar.INTENT_EXTRA_PARENT_NAME_VAR, parentName);
		i.putExtra(GlVar.INTENT_EXTRA_SECTION_NAME_VAR, searchName);
		c.startActivity(i);

	}

	public static void openListPreferences(Context c) {
		Intent i = new Intent(c, ListPreferences.class);
		c.startActivity(i);

	}

	public static void openChildPreferences(Context c) {
		Intent i = new Intent(c, ChildPreferences.class);
		c.startActivity(i);

	}

	public static void openDial(Context c, String dial) {
		Intent dialIntent=new Intent(Intent.ACTION_DIAL,Uri.parse(dial));
		c.startActivity(dialIntent);	  
	}

	public static void openShareText(Context c, String shareText) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);        
		sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);        
		sendIntent.setType("text/plain");
		c.startActivity(Intent.createChooser(sendIntent, "Share with"));
	}

	public static void openImage(Context c, String path) {
		Intent newIntent = new Intent(android.content.Intent.ACTION_VIEW);
		Uri hacked_uri = Uri.parse("file://" + path);
		newIntent.setDataAndType(hacked_uri, "image/*");
		c.startActivity(newIntent);
	}

	public static boolean isGoogleMapsInstalled(Context c)
	{
	    try
	    {
	        ApplicationInfo info = c.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
	        return true;
	    } 
	    catch(PackageManager.NameNotFoundException e)
	    {
	        return false;
	    }
	}
	
}