package com.warewoof.shanghai.map;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.warewoof.shanghai.GlVar;
import com.warewoof.shanghai.R;
import com.warewoof.shanghai.activity.DisplayMap;
import com.warewoof.shanghai.function.GeoFx;
import com.warewoof.shanghai.function.LogFx;
import com.warewoof.shanghai.object.MapItem;




public class AsyncOverlay {
	private DisplayMap mContext;
	//private ProgressDialog mProgressDialog;
	private ProgressDialog mProgressDialog;
	private ArrayList<MapItem> mMapItems;
	private String xmlStreetAddress;
	private String xmlAddressName;
	private GeoPoint point;
	private GeoPoint northEast;
	private GeoPoint southWest;
	//private GeoFunc GeoFx;
	private WWItemizedOverlay itemizedoverlay;
	//private Drawable mMarker;
	private List<Overlay> mMapOverlays;
	long startTime;
	long endTime;
	
	//public AsyncOverlay(MapViewActivity c, ArrayList<MapItem> mapItems, Drawable marker, List<Overlay> mapOverlays) {
	public AsyncOverlay(DisplayMap c, ArrayList<MapItem> mapItems) {
		mContext = c;
		this.mMapItems = mapItems;
		//this.mMarker = marker;
		this.itemizedoverlay = mContext.itemizedoverlay;
		this.mMapOverlays = mContext.mapView.getOverlays();
	}
	 
    //public WWItemizedOverlay drawOverlay() {
	public boolean drawOverlay() {
		try {
	    	mProgressDialog = new ProgressDialog(mContext, R.style.NewDialog);
	    	mProgressDialog.setOwnerActivity((Activity)mContext);
	        mProgressDialog.setIndeterminate(true);
	        mProgressDialog.setCancelable(false);
	        
	        startTime = android.os.SystemClock.uptimeMillis(); 
	        new DrawOverlay().execute();
	        return true;
		} catch (Exception e) {
			LogFx.debug("drawOverlay", "Exception e:" + e.toString());
			return false;
		}
    }
    
    public GeoPoint updateCenterPoint(GeoPoint myLocation) {
        return GeoFx.CalculateCenter(GeoFx.CheckNorthEast(northEast, myLocation), GeoFx.CheckSouthWest(southWest, myLocation));
    }

    public GeoPoint getCenterPoint() {
    	return GeoFx.CalculateCenter(northEast, southWest);
    }
    
    public int getLatSpanE6() {
    	return Math.abs(northEast.getLatitudeE6() - southWest.getLatitudeE6());
    }
    
    public int getLatSpanE6(GeoPoint myLocation) {  
    	GeoPoint tempNE = northEast;
    	GeoPoint tempSW = southWest;
    	tempNE = GeoFx.CheckNorthEast(tempNE, myLocation);
    	tempSW = GeoFx.CheckSouthWest(tempSW, myLocation);
    	return Math.abs(tempNE.getLatitudeE6() - tempSW.getLatitudeE6());  // returning with my location computed
    }
    
    public int getLonSpanE6() {
    	return Math.abs(northEast.getLongitudeE6() - southWest.getLongitudeE6());
    }
    
    public int getLonSpanE6(GeoPoint myLocation) {
    	GeoPoint tempNE = northEast;
    	GeoPoint tempSW = southWest;
    	tempNE = GeoFx.CheckNorthEast(tempNE, myLocation);
    	tempSW = GeoFx.CheckSouthWest(tempSW, myLocation);
    	return Math.abs(tempNE.getLongitudeE6() - tempSW.getLongitudeE6());  // returning with my location computed
    }

    class DrawOverlay extends AsyncTask<Void, Void, String> {
        private String TAG = "DrawOverlay";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();           
            
            try {
	            mProgressDialog.show();
	            mProgressDialog.setContentView(R.layout.map_customdialog);
            } catch (Exception e) {
            	LogFx.error(TAG, "Exception onPreExecute: " + e.toString());
            }
            
            
        }

        @Override
        protected String doInBackground(Void... params) {

            try {

            	long startTime = android.os.SystemClock.uptimeMillis();
            	boolean spanInitialized = false;
            	LogFx.debug(TAG, "Number of Locations: " + Integer.toString(mMapItems.size()) );
            	int loopCount = (mMapItems.size() > GlVar.MAP_MAXOVERLAYITEMS ) ? GlVar.MAP_MAXOVERLAYITEMS : mMapItems.size();
            	
            	//for (int i=loopCount; i>0; i--) {
            	for (int i=0; i<loopCount; i++) {
            		try {
            			MapItem mapItem = mMapItems.get(i);
    	    	        xmlStreetAddress = mapItem.GetStreetAddress();
    	    	        xmlAddressName = mapItem.GetAddressName();
    	    	        //String pointName = mapItem.GetChildName();
    	    	        String childName = mapItem.GetChildName();
    	    	        String parentName = mapItem.GetParentName();
    	    	        int childTabColor = mapItem.GetTabColor();
    	    	        // set Overlay Marker
    	    	        point = mapItem.GetGeoPoint();
    	    	        if (!spanInitialized) {
    	    	        	northEast = point;
    	    	        	southWest = point;
    	    	        	spanInitialized = true;
    	    	        }
    	    	        northEast = GeoFx.CheckNorthEast(northEast, point);
    	    	        southWest = GeoFx.CheckSouthWest(southWest, point);
    	    	        
    	    	        WWOverlayItem overlayitem;
    	    	        if (childName != "") { //this intent came from DisplayList, since child name was not included    	    	        	
    	    	        	//overlayitem = new WWOverlayItem(point, childName, "", childName, parentName, childTabColor);
    	    	        	overlayitem = new WWOverlayItem(point, childName, xmlAddressName, childName, parentName, childTabColor);
    	    	        } else { //this intent came from DisplayChild, so we want the "Address Name" rather than the child/section name
    	    	        	if (mMapItems.size() ==1) {  //in the case of single POI, then we want the detailed street address to show instead
    	    	        		overlayitem = new WWOverlayItem(point, xmlStreetAddress, "", childName, parentName, childTabColor);
    	    	        	} else {
    	    	        		overlayitem = new WWOverlayItem(point, xmlAddressName, "", childName, parentName, childTabColor);
    	    	        	}
    	    	        }
    	    	        
    	    	        
    	    	        itemizedoverlay.addOverlay(overlayitem);	    	        
            		} catch (Exception e) {
            			LogFx.debug("Mapping", "Exception: " + e.toString());
            		}
            		
            	}
            	
            	itemizedoverlay.fastPopulate();
            	
            	long endTime = android.os.SystemClock.uptimeMillis();
        	    LogFx.debug(TAG, "Excution time: "+(endTime-startTime)+" ms");
                return "Complete";
                
            } catch (Exception e) {
            	LogFx.error(TAG, "Exception: " + e.toString());            	
            	return e.toString();
            }
            

        }
        protected void onProgressUpdate(Void... params) {
             //mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String result) {

        	try {
        		mProgressDialog.dismiss();
        		//mProgressDialog.hide();
        		LogFx.debug("AsyncOverlay", "doInBackground " + result);
	        	
	        	mMapOverlays.add(itemizedoverlay);
	        	mContext.mapView.getController().animateTo(GeoFx.CalculateCenter(northEast, southWest));
	        	mContext.mapView.getController().zoomToSpan(Math.abs(northEast.getLatitudeE6() - southWest.getLatitudeE6()), Math.abs(northEast.getLongitudeE6() - southWest.getLongitudeE6()));
	        	//mContext.mapView.getController().setZoom(GlVar.MAP_INITIAL_ZOOM_LEVEL);
	        	
	        	mProgressDialog = null;        	
	            
	            endTime = android.os.SystemClock.uptimeMillis();
	            
	    	    LogFx.debug("MapViewActivity", "Creating Layers Excution time: "+(endTime-startTime)+" ms, for display");
	    	    
	    	    /*
	    	    if (mMapItems.size() > 1) {
	    	    	Toast.makeText(mContext, "Showing " + String.valueOf(mMapItems.size()) + " places", Toast.LENGTH_SHORT).show();
	    	    }
	    	    */
	    	    if (mMapItems.size() > GlVar.MAP_MAXOVERLAYITEMS) {
	    	    	Toast.makeText(mContext, "Limiting to " + String.valueOf(GlVar.MAP_MAXOVERLAYITEMS) + " returned locations", Toast.LENGTH_LONG).show();
	    	    }
	    	    
        	} catch (Exception e) {
        		LogFx.error("DownloadCatalog", "Exception: "+ e.toString());
        	}
            
    	    
        }

    }
	
   
    
	
}
