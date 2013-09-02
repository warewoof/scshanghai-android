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
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.maps.MapView.LayoutParams;
import com.warewoof.shanghai.GlVar;
import com.warewoof.shanghai.R;
import com.warewoof.shanghai.SPVar;
import com.warewoof.shanghai.activity.childview.ChildImageLayout;
import com.warewoof.shanghai.activity.childview.ChildTextFrameLayout;
import com.warewoof.shanghai.activity.childview.ChildTextLayout;
import com.warewoof.shanghai.file.DLPref;
import com.warewoof.shanghai.function.ActivityFx;
import com.warewoof.shanghai.function.LogFx;
import com.warewoof.shanghai.function.MathFx;
import com.warewoof.shanghai.function.StringFx;
import com.warewoof.shanghai.object.ChildAttribute;
import com.warewoof.shanghai.object.MapItem;

public class DisplayChild extends Activity {
	private static String TAG = "DisplayChildActivity";
	private String mChildName;
	private String mFrameName;
	private String mTitle;
	private int mTabColor;
	private LinearLayout mFramePointer;
	private ArrayList<ChildAttribute> mChildAtts;
	private ChildAttribute mNameAtt;
	private List<List<String>> mShareList = new ArrayList<List<String>>();
	private boolean disableShare;
	private boolean disableMap;
	private boolean noBack;
	private boolean reset = false;
	private boolean highlightAOI = false;  // highlight Attirbute of Interest
	private String mAOI;  // Attribute of Interest
	private ScrollView scrView;
	private LinearLayout scrollPosition;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		Intent intent = getIntent();
		InitVars(intent);
		setTitle(mTitle);

		long startTime = android.os.SystemClock.uptimeMillis();    

		LinearLayout pageLayout = new LinearLayout(this);  			// Creating a new LinearLayout
		pageLayout.setOrientation(LinearLayout.VERTICAL);
		pageLayout.setId(500);
		RelativeLayout frameNameLayout = new RelativeLayout(this);  // Creating a new Top Frame Container

		int attCount = mChildAtts.size();        
		boolean blankName = true;
		boolean isImageAtt = false;
		for (int i=0; i<attCount; i++) {

			String attName = mChildAtts.get(i).getName();

			/* This is a sanity check that the Name field is defined. Otherwise this display method will not know how to interpret the Value */
			/* also ignore meta data, it will be used for search and other features */
			if ((attName == null) || (attName.equals(GlVar.CHILD_META_TAG))) { 
				continue; 
			}        	// This needs to be fully tested         	

			String attValue = mChildAtts.get(i).getValue();        	

			/* this condition will be checked later, when inserting the value
			 * since image will be inserted differently from text */
			isImageAtt = attName.equalsIgnoreCase(GlVar.CHILD_IMAGE_TAG) ? true: false;

			if (!attName.equals("Name")) {  // everything except for the frame name will be handled in this section
				if (isImageAtt) {
					ChildImageLayout childImageLayout = new ChildImageLayout(this);
					pageLayout.addView(childImageLayout.CreateLayout(mChildAtts.get(i)));
				} else {        			
					ChildTextLayout childTextLayout = new ChildTextLayout(this);
					LinearLayout attributeLayout = childTextLayout.CreateLayout(mChildAtts.get(i),mFrameName,mTabColor);
					if (highlightAOI) {
						if (mChildAtts.get(i).getName().trim().equalsIgnoreCase(mAOI.trim())) {
							attributeLayout.setBackgroundResource(R.drawable.rounded_child_border_highlight);
							scrollPosition = attributeLayout;  // set pointer to scroll to this position onWindowFocusChanged
						}
					}
					pageLayout.addView(attributeLayout);	
					
					
					/* store text lists for later OnShareIconClick handling */
					ArrayList<String> mShareItem = new ArrayList<String>();
					mShareItem.add(attName);
					mShareItem.add(StringFx.unescape(attValue));
					mShareList.add(mShareItem);

				}

			} else { // tag is "Name" attribute, set as frame title
				mNameAtt = mChildAtts.get(i); // hold for later
				mFrameName = mNameAtt.getValue();
				blankName = false;	
			}
		}

		LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		rlp.setMargins(12, 0, 12, 25);
		pageLayout.setLayoutParams(rlp);

		//pageLayout.setPadding(12, 0, 12, 25);
		//pageLayout.setBackgroundResource(R.drawable.list_gradient_background);

		scrView = new ScrollView(this);
		scrView.addView(pageLayout);
		scrView.setBackgroundResource(R.drawable.list_gradient_background);


		ChildTextFrameLayout frameLayout = new ChildTextFrameLayout(this);


		if (blankName) {
			try {
				LogFx.debug("DisplayChild", "Creating default Name attribute, since none was defined for " + mFrameName);
				mFramePointer = frameLayout.CreateLayout(mFrameName, mTabColor);
				blankName = false;
			} catch (Exception e) {
				LogFx.debug("DisplayChild", "Exception creating name frame: " + e.toString());
			}
		} else {
			LogFx.debug("DisplayChild", "Found Name attribute for " + mFrameName);
			mFramePointer = frameLayout.CreateLayout(mNameAtt.getValue(),mTabColor);

			LogFx.debug(TAG, String.valueOf(mTabColor));
		}

		if (disableShare) {
			frameLayout.DisableShare();
		} else {
			frameLayout.getShareIcon().setOnClickListener(new OnShareIconClickListener());
		}

		if (disableMap) {
			frameLayout.DisableMap();
		} else {
			frameLayout.GetMapImageView().setOnClickListener(new OnMapIconClickListener());
		} 

		frameNameLayout.addView(mFramePointer);



		final ScrollView.LayoutParams svlp = new ScrollView.LayoutParams(ScrollView.LayoutParams.FILL_PARENT,ScrollView.LayoutParams.FILL_PARENT);

		final ViewTreeObserver vto = mFramePointer.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {	    	
			@Override
			public void onGlobalLayout() {
				svlp.topMargin = mFramePointer.getHeight();  //should match XML layout
				ViewTreeObserver obs = mFramePointer.getViewTreeObserver();
				obs.removeGlobalOnLayoutListener(this);
			}
		});

		svlp.gravity = LayoutParams.BOTTOM;
		scrView.setLayoutParams(svlp);
		FrameLayout fl = new FrameLayout(this);  
		FrameLayout.LayoutParams fllp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,FrameLayout.LayoutParams.FILL_PARENT);
		fl.setLayoutParams(fllp);
		scrView.setBackgroundResource(R.drawable.child_gradient_background);
		fl.addView(scrView);
		fl.addView(frameNameLayout);
		setContentView(fl, fllp);

		long endTime = android.os.SystemClock.uptimeMillis();
		LogFx.debug("DisplayChild", "Excution time: "+(endTime-startTime)+" ms, for display");


	}




	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		LogFx.debug(TAG, "onWindowFocusChanged");

		if (!reset) {
			LogFx.debug(TAG, "redrawing");
			/* this section is to force a layout draw, 
			 * a dirty workaround for some devices to properly measure and redraw the text frame */
			LinearLayout frameItem = (LinearLayout) findViewById(R.id.frameItem);
			frameItem.setVisibility(LinearLayout.GONE);
			frameItem.setVisibility(LinearLayout.VISIBLE);
			reset = true;
			//frameItem.invalidate();
		}		

		if (SPVar.getFontUpdated(DisplayChild.this)) {  // restart activity due to font preference changes
			SPVar.setFontUpdated(DisplayChild.this, false);
			finish(); startActivity(getIntent()); 
		}
		if (highlightAOI) {
			int location[] = {0,0};
			scrollPosition.getLocationInWindow(location);
			int position = location[1];	
			
			Rect androidSystemStatusBar = new Rect();
			Window window = getWindow();
			window.getDecorView().getWindowVisibleDisplayFrame(androidSystemStatusBar);
			int androidSystemStatusBarHeight = androidSystemStatusBar.top;
			LogFx.debug(TAG, "scrollPosition:" + String.valueOf(position - androidSystemStatusBar.top));	
		
			int customFrameHeight = mFramePointer.getHeight();		
			//scrView.scrollTo(0, position - androidSystemStatusBarHeight - MathFx.dpToPx(DisplayChild.this, 10));
			scrView.scrollTo(0, position - androidSystemStatusBarHeight - customFrameHeight - MathFx.dpToPx(DisplayChild.this, 5));
		}
	}




	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//No call for super(). Bug on API Level > 11.
		LogFx.debug("DisplayChild", "onSaveInstanceState called");
	}

	public class OnShareIconClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			AlertDialog.Builder shareDialog = new AlertDialog.Builder(DisplayChild.this);
			shareDialog.setIcon(R.drawable.ic_menu_share);
			shareDialog.setTitle("Share Items");

			final String[] shareArray = new String[mShareList.size()];
			final String[] shareValueArray = new String[mShareList.size()];
			final boolean[] optionArray = new boolean[mShareList.size()];
			for (int i=0; i<mShareList.size(); i++) {
				shareArray[i] = mShareList.get(i).get(0); // first item is the name of child attribute
				shareValueArray[i] = mShareList.get(i).get(1); // second item is the value of child attribute
			}

			shareDialog.setMultiChoiceItems(shareArray, optionArray,
					new DialogInterface.OnMultiChoiceClickListener() {
				public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
					optionArray[whichButton] = isChecked; 
				}
			});
			shareDialog.setPositiveButton("Share", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String tempString = mFrameName + "\n\n";
					boolean oneChecked = false;
					for (int j = 0; j < shareArray.length; j++) {
						if (optionArray[j]) {
							tempString = tempString + shareArray[j] + "\n" + shareValueArray[j] + "\n\n";
							oneChecked = true;
						}                		  
					}
					if (oneChecked) {
						ActivityFx.openShareText(DisplayChild.this, tempString);
					} else {
						Toast.makeText(DisplayChild.this, "There is nothing to share", Toast.LENGTH_SHORT).show();
					}
				}
			});
			shareDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) { /* Do nothing */ }
			});
			shareDialog.create();
			shareDialog.show();
		}		
	}

	public class OnMapIconClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			//create map list

			ArrayList<MapItem> mapList = new ArrayList<MapItem>();
			for (int i=0; i < mChildAtts.size(); i++) {
				if (mChildAtts.get(i).getLink().toLowerCase(Locale.US).startsWith("geo:")) {
					LogFx.debug("OnMapIconClick", "Inserting " + mChildAtts.get(i).getLink() + mChildAtts.get(i).getName() + mChildAtts.get(i).getValue());
					MapItem mi = new MapItem(mChildAtts.get(i).getLink(), mChildAtts.get(i).getName(), mChildAtts.get(i).getValue(), mTabColor);
					mapList.add(mi);
				}
			}
			if (mapList.size() < 1) {
				Toast.makeText(DisplayChild.this, "This section does not contain any maps", Toast.LENGTH_SHORT).show();
			} else {
				ActivityFx.openMap(DisplayChild.this, mFrameName, mTabColor, mapList, true);

			}

		}
	}

	@SuppressWarnings("unchecked")
	private void InitVars(Intent intent) {
		mChildName = intent.getStringExtra(GlVar.INTENT_EXTRA_SEARCH_NAME_VAR);
		mChildAtts = (ArrayList<ChildAttribute>) intent.getSerializableExtra(GlVar.INTENT_EXTRA_OBJECT_VAR);
		disableShare = intent.getBooleanExtra(GlVar.INTENT_EXTRA_SHARE_DISABLE_BOOL, false);
		disableMap = intent.getBooleanExtra(GlVar.INTENT_EXTRA_MAP_DISABLE_BOOL, false);
		noBack = intent.getBooleanExtra(GlVar.INTENT_EXTRA_NO_BACK_BOOL, false);		
		mTabColor = intent.getIntExtra(GlVar.INTENT_EXTRA_CHILD_TAB_COLOR, GlVar.SPACER_COLOR);
		LogFx.debug("DisplayChild", "Color returned " + Integer.toString(mTabColor));
		mFrameName = mChildName;
		mTitle = intent.getStringExtra(GlVar.INTENT_EXTRA_TITLE_VAR);
		mAOI = intent.getStringExtra(GlVar.INTENT_EXTRA_HIGHLIGHT_AOI);
		if (mAOI != null) {
			if (mAOI.length() > 0) {
				highlightAOI = true;
				LogFx.debug(TAG, "Highlight: " + mAOI);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.child_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int itemId = item.getItemId();
		if (itemId == R.id.menuPreferences) {
			ActivityFx.openChildPreferences(DisplayChild.this);
			reset = false;
			return true;
		} else if (itemId == R.id.menuReport) {
			//ActivityFx.op.openChild(DisplayChild.this, "About", "About this app", GlVar.SPACER_COLOR_HIDE_CODE, true, true, true, false);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			String subject = "Reporting error on \"" + mChildName.toUpperCase(Locale.US) + "\" page";
			Uri data = Uri.parse("mailto:scshanghai@warewoof.com?subject=" + subject + "&body=" + "(please describe)\n");
			intent.setData(data);
			startActivity(intent);
			return true;
		} else if (itemId == R.id.menuMain) {
			DLPref.setSPCatalogUpdated(DisplayChild.this, true);
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume()
	{  
		// After a pause OR at startup
		super.onResume();
		//Refresh your stuff here
		try {
			LogFx.debug(TAG, "OnResume called");
			boolean updated = DLPref.getSPCatalogUpdated(DisplayChild.this);
			if (updated) {
				if (noBack) {
	        		LogFx.debug(TAG, "OnResume no back, reset SP, reload root list");
	        		DLPref.setSPCatalogUpdated(DisplayChild.this, false);

	                ActivityFx.openList(DisplayChild.this, GlVar.CATALOG_PARENT_NAME, GlVar.CATALOG_TOC_NAME, false, false, false, true);
	        		
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

}