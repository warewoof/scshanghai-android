package com.warewoof.shanghai.object;

import java.util.StringTokenizer;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;
import com.warewoof.shanghai.GlVar;
import com.warewoof.shanghai.function.LogFx;

public class MapItem {
	private String mGeoString;
	private String mStreetAddress;
	private String mAddressName;
	private String mChild;
	private String mParent;
	private String mMapMarkerId;
	private double mLat;
	private double mLng;
	private int mColor = GlVar.SPACER_COLOR;


	public MapItem(String geoString, String addressName, String streetAddress) {
		this.mGeoString = geoString;
		this.mAddressName = addressName;
		this.mStreetAddress = streetAddress;		
		this.StringToCoords();
	}
	
	public MapItem(String geoString, String addressName, String streetAddress, int color) {
		this.mGeoString = geoString;
		this.mAddressName = addressName;
		this.mStreetAddress = streetAddress;
		this.mColor = color;
		this.StringToCoords();
	}
	
	public MapItem(String geoString, String addressName, String streetAddress, String parent, String child) {
		this.mGeoString = geoString.trim();
		this.mAddressName = addressName.trim();
		this.mStreetAddress = streetAddress.trim();		
		this.mParent = parent.trim();
		this.mChild = child.trim();
		this.StringToCoords();
	}
	
	public String GetChildName() {
		if (mChild == null) {
			return "";
		} else {
			return mChild;
		}
	}
	
	public String GetParentName() {
		if (mParent == null) {
			return "";
		} else {
			return mParent;
		}
	}
	public void SetParentName(String name) {
		mParent = name.trim();
	}
	
	public void SetMarkerId(String mId) {
		mMapMarkerId = mId;		
	}
	
	public String GetMarkerId() {
		return mMapMarkerId;
	}
	
	public int GetTabColor() {
		return mColor;
	}
	
	public void SetTabColor(int color) {
		mColor = color;
	}
	
	public GeoPoint GetGeoPoint() {

		try {
			StringTokenizer tokens = new StringTokenizer(mGeoString, ":,?");
			tokens.nextToken();
			GeoPoint point = new GeoPoint((int) (Float.valueOf(tokens.nextToken()) * 1E6), (int) (Float.valueOf(tokens.nextToken()) * 1E6));
			return point;
		} catch (Exception e) {
			LogFx.debug("MapItem", "Tokenizing Exception for "+mChild+": "+e.toString());
			return null;
		}
	}
	
	private void StringToCoords() {
		try {
			String[] tempArray = mGeoString.split("[,:?]"); 
			mLat = Double.parseDouble(tempArray[1]);
			mLng = Double.parseDouble(tempArray[2]);
		} catch (Exception e) {
			LogFx.error("MapItem", "StringToCoords Exception for "+mChild+": "+e.toString());
		}		
	}
	
	public LatLng GetLatLng() {
		return new LatLng(mLat, mLng);
	}
	
	public double GetLng() {
		return mLng;
	}
	
	public double GetLat() {
		return mLat;
	}
	public String GetLongitudeString() {
		return Double.toString(mLng);
	}
	
	public String GetLatitudeString() {
		return Double.toString(mLat);
	}
	
	
	public String GetStreetAddress() {
		return unescape(mStreetAddress);
	}
	
	public String GetAddressName() {
		return unescape(mAddressName);
	}
	
	private String unescape(String description) {
	    return description.replaceAll("\\\\n", "\\\n");
	}
	
	public float GetTabHue() {
		String hexString = Integer.toHexString(mColor);
		int r = Integer.valueOf(hexString.substring(2,4), 16).intValue();
		int g = Integer.valueOf(hexString.substring(4,6), 16).intValue();
		int b = Integer.valueOf(hexString.substring(6,8), 16).intValue();
		float[] hsv = new float[3];
		Color.RGBToHSV(r,g,b,hsv);
		
		return hsv[0];
	}
}
