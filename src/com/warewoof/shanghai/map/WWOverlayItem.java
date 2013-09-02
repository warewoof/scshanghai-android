package com.warewoof.shanghai.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class WWOverlayItem extends OverlayItem {
	private String mChild;
	private String mParent;
	private int mColor;
	
	public WWOverlayItem(GeoPoint point, String title, String snippet, String child, String parent, int color) {
		
		super(point, title, snippet);
		this.mChild = child;
		this.mParent = parent;
		this.mColor = color;
	}
	
	public String getChildName() {
		return mChild;
	}
	
	public String getParentName() {
		return mParent;
	}
	
	public int getColor() {
		return mColor;
	}
	
	public String getSnippet() {
		return super.mSnippet;
	}
}