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
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import com.warewoof.shanghai.function.LogFx;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.warewoof.shanghai.R;
import com.warewoof.shanghai.GlVar;
import com.warewoof.shanghai.function.XmlFx;
import com.warewoof.shanghai.object.ListItem;

public class DisplaySearch extends Activity {
	private String sectionName = "";
	private String parentName = "";
  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search);
		
		Intent i = getIntent();		
		setTitle(i.getStringExtra(GlVar.INTENT_EXTRA_TITLE_VAR));
		sectionName = i.getStringExtra(GlVar.INTENT_EXTRA_SECTION_NAME_VAR);
		parentName = i.getStringExtra(GlVar.INTENT_EXTRA_PARENT_NAME_VAR);
		
		TextView tvName = (TextView) findViewById(R.id.custom_list_name_frame_text);
		tvName.setText("Search " + sectionName);
	
		if (getResources().getConfiguration().keyboard != Configuration.KEYBOARD_QWERTY) {
			getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}
		
		EditText editText = (EditText) findViewById(R.id.searchEntry);

		editText.setOnEditorActionListener(new OnEditorActionListener() {
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_DONE) {
		        	LogFx.debug("ParseXml", "Enter pressed");
		    	    searchButtonClick(null);
		        }
		        return false;
		    }
		});
		
	}
	
	public void searchButtonClick(View target) {
		EditText searchBox = (EditText) findViewById(R.id.searchEntry);
		
		Editable searchString = searchBox.getText();
		if (searchString.length() < 1) {
			Toast.makeText(getApplicationContext(), "Enter a valid search", Toast.LENGTH_SHORT).show();
		} else {
			
			//XmlFx xfn = new XmlFx(SearchActivity.this);   
			ArrayList<ListItem> listItems = new ArrayList<ListItem>();
	    	listItems = XmlFx.searchAll(DisplaySearch.this, searchString.toString());  // find all children with matching string
	    	listItems = XmlFx.filterSearchResults(DisplaySearch.this, parentName, sectionName, listItems);  // filter children that are found within this section
	    	Intent i;

			i = new Intent(this, DisplayList.class);

	    		
		    i.putExtra(GlVar.INTENT_EXTRA_OBJECT_VAR, listItems);
		    i.putExtra(GlVar.INTENT_EXTRA_TITLE_VAR, "Search Results");
		    i.putExtra(GlVar.INTENT_EXTRA_NO_BACK_BOOL, false);
		    i.putExtra(GlVar.INTENT_EXTRA_SEARCH_STRING_VAR, searchString.toString());
		    //i.putExtra(XmlProp.INTENT_EXTRA_SECTION_NAME_VAR, parentName);
		    //i.putExtra(XmlProp.INTENT_EXTRA_PARENT_NAME_VAR, sectionName);
		    startActivity(i);
			
		    finish();
			
			/*
			Intent i = new Intent(SearchQuery.this, SearchXml.class);
	
	        i.putExtra(XmlProp.INTENT_EXTRA_SEARCH_STRING_VAR, searchString.toString());
	        i.putExtra(XmlProp.INTENT_EXTRA_SEARCH_SECTION_PARENT_VAR, parentName);
	        i.putExtra(XmlProp.INTENT_EXTRA_SEARCH_SECTION_VAR, sectionName);
	        i.putExtra(XmlProp.INTENT_EXTRA_NO_BACK_BOOL, false);
	        startActivity(i);
	        
	        finish();*/
		}
    }
}