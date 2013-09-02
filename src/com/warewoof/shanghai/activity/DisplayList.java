/***
  Copyright (c) 2012 XLocal
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
	
  
*/

package com.warewoof.shanghai.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.warewoof.shanghai.GlVar;
import com.warewoof.shanghai.R;
import com.warewoof.shanghai.activity.listview.CustomListAdapter;
import com.warewoof.shanghai.file.DLPref;
import com.warewoof.shanghai.function.ActivityFx;
import com.warewoof.shanghai.function.LogFx;
import com.warewoof.shanghai.function.MathFx;
import com.warewoof.shanghai.function.XmlFx;
import com.warewoof.shanghai.object.ListItem;
import com.warewoof.shanghai.object.MapItem;

public class DisplayList extends Activity {
	private final String TAG = "DisplayListActivity";
	private ArrayList<ListItem> listItems;
    private OnItemClickListener onClickListener;    
    private String sectionName = "";
    private String parentName = "";
    private String titleBar ="";
    private boolean noBack;
    private boolean disableSearch;
    private boolean disableShare;
    private boolean disableMap;
    
    
    
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listview);

		RelativeLayout frameRl = (RelativeLayout) findViewById(R.id.custom_list_name_frame);
		frameRl.setBackgroundDrawable(getResources().getDrawable(GlVar.LIST_FRAME_BG));
		
		Intent i = getIntent();		
		noBack = i.getBooleanExtra(GlVar.INTENT_EXTRA_NO_BACK_BOOL, false);		
		listItems = (ArrayList<ListItem>) i.getSerializableExtra(GlVar.INTENT_EXTRA_OBJECT_VAR);
		titleBar = i.getStringExtra(GlVar.INTENT_EXTRA_TITLE_VAR);
		sectionName = i.getStringExtra(GlVar.INTENT_EXTRA_SECTION_NAME_VAR);
		parentName = i.getStringExtra(GlVar.INTENT_EXTRA_PARENT_NAME_VAR);
		disableSearch = i.getBooleanExtra(GlVar.INTENT_EXTRA_SEARCH_DISABLE_BOOL, false);
		disableShare = i.getBooleanExtra(GlVar.INTENT_EXTRA_SHARE_DISABLE_BOOL, false);
		disableMap = i.getBooleanExtra(GlVar.INTENT_EXTRA_MAP_DISABLE_BOOL, false);
		
		LogFx.debug("DisplayList", "Section " + sectionName + ", parent " + parentName);
		
		setTitle(titleBar);
		final Typeface myTypeface = Typeface.createFromAsset(this.getAssets(), "fonts/ubuntu.ttf");
		TextView tvName = (TextView) findViewById(R.id.custom_list_name_frame_text);
		tvName.setTypeface(myTypeface);
		tvName.setText(titleBar);
		tvName.setTextColor(GlVar.LIST_FRAME_TEXT_COLOR);
		if (!noBack) {
			tvName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}			
			});
		}
		ListView list = (ListView)findViewById(R.id.custom_list);
		
		RelativeLayout.LayoutParams ftlp = (RelativeLayout.LayoutParams) tvName.getLayoutParams();
		int dividers = 0;
		if (getResources().getDisplayMetrics().densityDpi <= DisplayMetrics.DENSITY_HIGH) {
			dividers = 4;  // don't know why this is, but notice it happening on N1 and not NG
		} else {
			dividers = 2;
		}
    	int totalIconWidth = getResources().getDrawable(R.drawable.ic_menu_mapmode).getIntrinsicWidth()
    						+ getResources().getDrawable(R.drawable.ic_menu_search).getIntrinsicWidth()
    						+ MathFx.dpToPx(this, dividers) // for the dividers
    						+ MathFx.dpToPx(this, 20);  // for the icon padding
    	
    	LogFx.debug(TAG, "totalIconWidth="+String.valueOf(totalIconWidth));
    	ftlp.setMargins(0, 0, totalIconWidth, 0);
    	
		/* if section name and parent name is null then this could be a search result list
		 * do not display search options (icon/menu)
		 */
		if ((sectionName == null) || (parentName == null)) {
			ImageView searchIcon = (ImageView) findViewById(R.id.listNameSearchIcon);
			searchIcon.setVisibility(ImageView.GONE);
			ImageView searchDivIcon = (ImageView) findViewById(R.id.ic_sep_search);
			searchDivIcon.setVisibility(ImageView.GONE);
			ImageView mapIcon = (ImageView) findViewById(R.id.listNameMapIcon);
			
			
        	
			if (listItems.isEmpty()) {
				mapIcon.setVisibility(ImageView.GONE);
				ImageView mapDivIcon = (ImageView) findViewById(R.id.ic_sep_map);
				mapDivIcon.setVisibility(ImageView.GONE);
				
		    	ftlp = (RelativeLayout.LayoutParams) tvName.getLayoutParams();
		    	ftlp.setMargins(0, 0, 0, 0);
		    	
			} else {  // search may return empty list, but so does 
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				mapIcon.setLayoutParams(lp);

				
	        	ftlp = (RelativeLayout.LayoutParams) tvName.getLayoutParams();
	        	totalIconWidth = getResources().getDrawable(R.drawable.ic_menu_mapmode).getIntrinsicWidth()
	        						+ MathFx.dpToPx(this, 1) // for the divider
	        						+ MathFx.dpToPx(this, 10);  // for the icon padding
	        	
	        	LogFx.debug(TAG, "totalIconWidth="+String.valueOf(totalIconWidth));
	        	ftlp.setMargins(0, 0, totalIconWidth, 0);
		    	
			}
		}
		
		if (disableSearch) {
			ImageView searchIcon = (ImageView) findViewById(R.id.listNameSearchIcon);		
			searchIcon.setVisibility(ImageView.GONE);
			ImageView searchDivIcon = (ImageView) findViewById(R.id.ic_sep_search);
			searchDivIcon.setVisibility(ImageView.GONE);

			ImageView mapIcon = (ImageView) findViewById(R.id.listNameMapIcon);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			mapIcon.setLayoutParams(lp);
		}
		
		if (disableMap) {
			ImageView mapIcon = (ImageView) findViewById(R.id.listNameMapIcon);
			mapIcon.setVisibility(ImageView.GONE);
			ImageView mapDivIcon = (ImageView) findViewById(R.id.ic_sep_map);
			mapDivIcon.setVisibility(ImageView.GONE);
		}
			
		
		/* Check if list is empty, which should only be empty if either
		 *   1) corresponding child node was not defined in the children section, or 
		 *   2) Search results found no matching strings
		 */
		if (listItems.isEmpty()) {
			listItems.add(new ListItem("No information found", "noParent", "nonNode", 0x00000000));
			list.setClickable(false);
			list.setFocusable(false);
		} else {
			list.setClickable(true);
			onClickListener = new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				
				LinearLayout listItem = (LinearLayout) v;
				TextView clickedItemView = (TextView) listItem.findViewById(R.id.name);
				String clickedItemString = clickedItemView.getText().toString();
				LogFx.debug("DisplayList", "Click detected " + clickedItemString + ", position " + Integer.toString(position));
				
				
				if (listItems.get(position).getItemNode().equals(GlVar.CHILD_NODE_TAG)) {
					LogFx.debug("DisplayList", "Preparing to display child " + clickedItemString);
					
					String itemParent = listItems.get(position).getItemParent();
					int itemColor = listItems.get(position).getItemColor();
					ActivityFx.openChild(DisplayList.this, itemParent, clickedItemString, 
							itemColor, disableSearch, disableShare, disableMap, noBack);
				} else {
					LogFx.debug("DisplayList", "Preparing to display list " + clickedItemString);
					
					//ActivityFx.openList(DisplayList.this, sectionName, clickedItemString, disableSearch, disableShare, disableMap);
					/* disableMap is set to false to override the splash setting of true
					 * because there are too many geopoints to map from the root node	 */
					ActivityFx.openList(DisplayList.this, sectionName, clickedItemString, disableSearch, disableShare, false);
										
				}				
			}};			
			
	        list.setOnItemClickListener(onClickListener);
		}


		list.setAdapter(new CustomListAdapter(getApplicationContext(), listItems));
		
		
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		ImageView searchIcon = (ImageView) findViewById(R.id.listNameSearchIcon);
		ImageView mapIcon = (ImageView) findViewById(R.id.listNameMapIcon);
		
		RelativeLayout nameFrame = (RelativeLayout) findViewById(R.id.custom_list_name_frame);
		searchIcon.setMinimumHeight(nameFrame.getHeight());
		mapIcon.setMinimumHeight(nameFrame.getHeight());
		
		ImageView divider1 = (ImageView) findViewById(R.id.ic_sep_search);
		divider1.setMinimumHeight(nameFrame.getHeight());
		ImageView divider2 = (ImageView) findViewById(R.id.ic_sep_map);
		divider2.setMinimumHeight(nameFrame.getHeight());
		
	}
	
	@Override
	public void onResume()
    {  
		// After a pause OR at startup
	    super.onResume();
	    //Refresh your stuff here
	    LogFx.debug(TAG, "OnResume called");
	    try {
	    boolean updated = DLPref.getSPCatalogUpdated(DisplayList.this);
        if (updated) {
        	LogFx.debug(TAG, "OnResume SP updated");
        	if (noBack) {
        		LogFx.debug(TAG, "OnResume no back, reset SP, reload root list");
        		DLPref.setSPCatalogUpdated(DisplayList.this, false);

                ActivityFx.openList(DisplayList.this, GlVar.CATALOG_PARENT_NAME, GlVar.CATALOG_TOC_NAME, false, false, false, true);
        		
                finish();
                
        	} else {
        		LogFx.debug(TAG, "OnResume killing activity");
        		finish();
        	}
        }
	    } catch (Exception e) {
	    	LogFx.error(TAG, "OnResume exception: " + e.toString());
	    }
		
    }
	
	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    //No call for super(). Bug on API Level > 11.
		super.onSaveInstanceState(outState);
		LogFx.debug("DisplayListActivity", "onSaveInstanceState called");
	}


	public void searchIconClick(View target) {
		
		ActivityFx.openSearch(DisplayList.this, parentName, sectionName);
    }
	
	public void mapIconClick(View target) {
		
		if ((sectionName != null) && (parentName != null)) {  // this is a general map search on a specific Section with Parent
			LogFx.debug("mapIconClick", "filtering section " + sectionName);
			ArrayList<MapItem> mapItems = null;
			
			long TstartTime = android.os.SystemClock.uptimeMillis();
			long endTime = 0;
			long startTime = 0;
			//for (int i=0; i <10; i++) {
				startTime = android.os.SystemClock.uptimeMillis();
				mapItems = XmlFx.getAllGeoLinks(DisplayList.this);  // find all children with matching string
				endTime = android.os.SystemClock.uptimeMillis();
				LogFx.debug("Iteration Time getAllGeoLinks", "Excution time: "+(endTime-startTime)+" ms");
				startTime = android.os.SystemClock.uptimeMillis();
				mapItems = XmlFx.filterGeoResults(DisplayList.this, parentName, sectionName, mapItems);  // filter children that are found within this section
				endTime = android.os.SystemClock.uptimeMillis();
			    LogFx.debug("Iteration Time filterGeoResults", "Excution time: "+(endTime-startTime)+" ms");
			//}
			long TendTime = android.os.SystemClock.uptimeMillis();
			LogFx.debug("Average Time", "Excution time: "+((TendTime-TstartTime))+" ms");
			

			if (mapItems.size() < 1) {
				Toast.makeText(this, "This section does not contain any maps", Toast.LENGTH_SHORT).show();
			} else {	
				
				//String mapKey = sectionName+parentName+String.valueOf(mapItems.size());
				//GlVar.setMapItems(mapKey, mapItems);
				ActivityFx.openMap(DisplayList.this, sectionName, GlVar.SPACER_COLOR_HIDE_CODE, mapItems);
				//ActivityFx.openMapSP(DisplayList.this, sectionName, GlVar.SPACER_COLOR_HIDE_CODE);
				//ActivityFx.openMapSP(DisplayList.this, sectionName, GlVar.SPACER_COLOR_HIDE_CODE, mapKey);
			}
		} else if (!listItems.isEmpty()) {  // this is a map search for a String Search result list
			LogFx.debug("mapIconClick", "filtering seach against " + String.valueOf(listItems.size())  + " items");

			ArrayList<MapItem> mapItems = XmlFx.getAllGeoLinks(DisplayList.this);
			mapItems = XmlFx.filterGeoResultsFromList(listItems, mapItems);
			
			if (mapItems.size() < 1) {
				Toast.makeText(this, "This section does not contain any maps", Toast.LENGTH_SHORT).show();
			} else {				
				ActivityFx.openMap(DisplayList.this, titleBar, GlVar.SPACER_COLOR_HIDE_CODE, mapItems);				
			}
		}		
    }
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.list_menu, menu);
	    if (noBack) {
	    	/* this is the root list, no main menu option available */
	    	menu.removeItem(R.id.menuMain);
	    }
	    
	    if ((sectionName == null) || (parentName == null)) {  
	    	/* this is a search result list, no search option available */
			menu.removeItem(R.id.menuSearch);
		}
	    
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int itemId = item.getItemId();
		if (itemId == R.id.menuSearch) {
			ActivityFx.openSearch(DisplayList.this, GlVar.CATALOG_PARENT_NAME, GlVar.CATALOG_TOC_NAME);  // search all categories	        	
			return true;
		} else if (itemId == R.id.menuPreferences) {
			ActivityFx.openListPreferences(DisplayList.this);
			return true;
		} else if (itemId == R.id.menuAbout) {
			ActivityFx.openChild(DisplayList.this, "About", "About this app", GlVar.SPACER_COLOR_HIDE_CODE, true, true, true, false);
			return true;
		} else if (itemId == R.id.menuMain) {
			/* this SP was originally intended for catalog updates, but functionally same as traversing to main menu */
			DLPref.setSPCatalogUpdated(DisplayList.this, true);
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	@Override
	public boolean onSearchRequested() {

		ActivityFx.openSearch(DisplayList.this, parentName, sectionName);
		
	    return false;  // don't go ahead and show the search box
	}

	
}