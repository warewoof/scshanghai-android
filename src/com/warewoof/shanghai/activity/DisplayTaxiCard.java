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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
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
import android.widget.TextView;

import com.google.android.maps.MapView.LayoutParams;
import com.warewoof.shanghai.GlVar;
import com.warewoof.shanghai.R;
import com.warewoof.shanghai.SPVar;
import com.warewoof.shanghai.activity.childview.ChildTextFrameLayout;
import com.warewoof.shanghai.file.DLPref;
import com.warewoof.shanghai.function.LogFx;

public class DisplayTaxiCard extends Activity {
	private static String TAG = "DisplayTaxiCardActivity";
	private String mTitle;
	private String mDisplayText;
	private int mTabColor;
	private LinearLayout mFramePointer;
	private boolean reset = false;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		Intent intent = getIntent();
		InitVars(intent);
		setTitle(mTitle);

		long startTime = android.os.SystemClock.uptimeMillis();    

		TextView displayText = new TextView(this);
		displayText.setText(mDisplayText);
		displayText.setTextSize(GlVar.TAXI_CARD_TEXT_DEFAULT_SIZE);
		displayText.setTextColor(GlVar.TAXI_CARD_TEXT_COLOR);
		displayText.setGravity(Gravity.CENTER);

		ScrollView scrView = new ScrollView(this);
		scrView.addView(displayText);
		scrView.setBackgroundResource(R.drawable.list_gradient_background);
		scrView.setFillViewport(true);

		ChildTextFrameLayout frameLayout = new ChildTextFrameLayout(this);
		mFramePointer = frameLayout.CreateLayout(mTitle, mTabColor);
		frameLayout.DisableShare();
		frameLayout.DisableMap();
		
		RelativeLayout frameNameLayout = new RelativeLayout(this);  // Creating a new Top Frame Container
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
		LogFx.debug("DisplayTaxiCard", "Excution time: "+(endTime-startTime)+" ms, for display");


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

		if (SPVar.getFontUpdated(DisplayTaxiCard.this)) {
			SPVar.setFontUpdated(DisplayTaxiCard.this, false);
			finish(); startActivity(getIntent()); 
		}
	}




	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//No call for super(). Bug on API Level > 11.
		LogFx.debug("DisplayTaxiCard", "onSaveInstanceState called");
	}

	

	//@SuppressWarnings("unchecked")
	private void InitVars(Intent intent) {
		mTitle = intent.getStringExtra(GlVar.INTENT_EXTRA_TITLE_VAR);
		mDisplayText = intent.getStringExtra(GlVar.INTENT_EXTRA_TAXI_CARD_STRING);
		mTabColor = intent.getIntExtra(GlVar.INTENT_EXTRA_CHILD_TAB_COLOR, GlVar.SPACER_COLOR);
		LogFx.debug("DisplayTaxiCard", "Color returned " + Integer.toString(mTabColor));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.taxi_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int itemId = item.getItemId();
		if (itemId == R.id.menuReport) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			String subject = "Reporting error on \"" + mTitle.toUpperCase() + "\" taxi card page";
			Uri data = Uri.parse("mailto:scshanghai@warewoof.com?subject=" + subject + "&body=" + "(please describe)\n");
			intent.setData(data);
			startActivity(intent);
			return true;
		} else if (itemId == R.id.menuMain) {
			DLPref.setSPCatalogUpdated(DisplayTaxiCard.this, true);
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
			boolean updated = DLPref.getSPCatalogUpdated(DisplayTaxiCard.this);
			if (updated) {
	        		LogFx.debug(TAG, "OnResume killing activity");
	        		finish();
			}
		} catch (Exception e) {
			LogFx.error(TAG, "OnResume exception: " + e.toString());
		}

	}
	
	public class OnShareIconClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			LogFx.debug("DisplayTaxiCard", "OnShareIconClickListener should not be called!!!");
		}		
	}

	public class OnMapIconClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			LogFx.debug("DisplayTaxiCard", "OnMapIconClickListener should not be called!!!");
		}
	}

}