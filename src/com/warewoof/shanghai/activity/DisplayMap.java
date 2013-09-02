package com.warewoof.shanghai.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.MyLocationOverlay;
import com.warewoof.shanghai.GlVar;
import com.warewoof.shanghai.R;
import com.warewoof.shanghai.SPVar;
import com.warewoof.shanghai.file.DLPref;
import com.warewoof.shanghai.function.ActivityFx;
import com.warewoof.shanghai.function.GeoFx;
import com.warewoof.shanghai.function.LogFx;
import com.warewoof.shanghai.function.MathFx;
import com.warewoof.shanghai.google.Google;
import com.warewoof.shanghai.map.AsyncOverlay;
import com.warewoof.shanghai.map.WWItemizedOverlay;
import com.warewoof.shanghai.object.MapItem;

public class DisplayMap extends MapActivity {
	private final String TAG = "MapViewActivity";
	private MyLocationOverlay myLocationOverlay;
	private LocationManager locationManager;
	private LocationListener locationGpsListener;
	private LocationListener locationNetworkListener;	
	private GeoPoint point;
	private GeoPoint myPoint;
	private String xmlAddressName;
	private String xmlStreetAddress;
	private String mMapTitle;
	private int tabColor;
	private String mMapLink;
	private boolean initialLocation = true;
	private int mFrameHeight;
	private ArrayList<MapItem> mMapItems;
	public MapView mapView;
	public WWItemizedOverlay itemizedoverlay;
	private Drawable marker;
	private boolean alreadyDrawn = false;
	private boolean pulledBackOnce = false;
	private boolean requestFromChild = false;
	private AsyncOverlay asyncOverlay;
	private String mapKey = "";
	private boolean rotated = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			setContentView(R.layout.mapview);

			Intent intent = getIntent();
			mMapTitle = intent.getStringExtra(GlVar.INTENT_EXTRA_TITLE_VAR);
			tabColor = intent.getIntExtra(GlVar.INTENT_EXTRA_CHILD_TAB_COLOR, GlVar.SPACER_COLOR);
			mapKey = intent.getStringExtra(GlVar.INTENT_EXTRA_OBJECT_KEY);        
			requestFromChild = intent.getBooleanExtra(GlVar.INTENT_EXTRA_MAP_REQUEST_FROM_CHILD, false);

			mMapItems = SPVar.getMapItems(mapKey);
			if (mMapItems.size() < 1) {
				finish();  // sometimest he hashmap saved map will be deallocated by system
			}
			long measureTime = SPVar.getMeasureTime(this);
			LogFx.debug("Passing MapItems", "Excution time: "+((android.os.SystemClock.uptimeMillis())-measureTime)+" ms");

			//TextView spacer = (TextView) findViewById(R.id.spacer);
			if (tabColor == GlVar.SPACER_COLOR_HIDE_CODE) {  // set to hide if map intent originates from DisplayList
				((TextView) findViewById(R.id.spacer)).setVisibility(TextView.GONE);
			} else {       
				((TextView) findViewById(R.id.spacer)).setBackgroundColor(tabColor);
			}

			TextView title = (TextView) findViewById(R.id.custom_list_name_frame_text);
			title.setText(mMapTitle);
			title.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					rotated = false;
					finish();				
				}			
			});

			// create MapView
			mapView = (MapView) findViewById(R.id.mapview);
			mapView.setBuiltInZoomControls(true);
			mapView.getController().setZoom(GlVar.MAP_INITIAL_ZOOM_LEVEL);

			// create Overlay View
			
			marker = getResources().getDrawable(R.drawable.map_marker);
			itemizedoverlay = new WWItemizedOverlay(marker, this, mapView);
			asyncOverlay = new AsyncOverlay(this, mMapItems);

			if ((!requestFromChild) || (mMapItems.size() > 1)) {       	
				DisableMapShareIcon();


			} else {
				TextView frameText = title;
				RelativeLayout.LayoutParams ftlp = (RelativeLayout.LayoutParams) frameText.getLayoutParams();
				int totalIconWidth = getResources().getDrawable(R.drawable.ic_menu_mapmode).getIntrinsicWidth()
						+ MathFx.dpToPx(this, 1) // for the divider
						+ MathFx.dpToPx(this, 10);  // for the icon padding

				LogFx.debug(TAG, "totalIconWidth="+String.valueOf(totalIconWidth));
				ftlp.setMargins(0, 0, totalIconWidth, 0);


				// if only one item (and intent requested from child link) then prepare the goo.gl shortened URL
				if (mMapItems.size() == 1) {  
					MapItem mapItem = mMapItems.get(0);
					xmlStreetAddress = mapItem.GetStreetAddress();
					xmlAddressName = mapItem.GetAddressName();
					point = mapItem.GetGeoPoint();
					if (point == null) {
						Toast.makeText(DisplayMap.this, "Map cannot be found, bad map data", Toast.LENGTH_SHORT).show();
						finish();
					} else {    	        
						mMapLink = GeoFx.GeoToLink(point);        
						GetShortUrl task = new GetShortUrl();
						task.execute(mMapLink);
					}
				}

			}


			myLocationOverlay = new MyLocationOverlay(this, mapView);
			mapView.getOverlays().add(myLocationOverlay);
			myLocationOverlay.enableMyLocation();        


			// Acquire a reference to the system Location Manager
			locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			// Define a listener that responds to location updates        
			locationGpsListener = new MyLocationListener("GPS");
			locationNetworkListener = new MyLocationListener("network");
			// Register the listener with the Location Manager to receive location updates
			// This is being handled by MyLocationManager now, so these can be disabled until needed for custom location fixes 
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationNetworkListener);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationGpsListener);       

		} catch (Exception e) {
			LogFx.error(TAG, "onCreate exception: " + e.toString());
			finish();
		}

	}

	private void DisableMapShareIcon() {
		ImageView shareIcon = (ImageView) findViewById(R.id.map_share_icon);
		shareIcon.setVisibility(ImageView.GONE);
		ImageView shareIconDiv = (ImageView) findViewById(R.id.map_share_div);
		shareIconDiv.setVisibility(ImageView.GONE);
		TextView frameText = (TextView) findViewById(R.id.custom_list_name_frame_text);
		RelativeLayout.LayoutParams ftlp = (RelativeLayout.LayoutParams) frameText.getLayoutParams();
		ftlp.setMargins(0, 0, 0, 0);

	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		try {
			super.onWindowFocusChanged(hasFocus);
			RelativeLayout nameFrame = (RelativeLayout) findViewById(R.id.custom_list_name_frame);
			if (mFrameHeight != nameFrame.getHeight()) {
				mFrameHeight = nameFrame.getHeight() -GlVar.FRAME_DIVIDER_WIDTH;
				LogFx.debug("MapView", "Map has focus, Frame Name Bar Height = " + Integer.toString(mFrameHeight));
				RelativeLayout mapFrame = (RelativeLayout) findViewById(R.id.map_frame);
				RelativeLayout.LayoutParams rlm = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
				rlm.topMargin = mFrameHeight + GlVar.FRAME_DIVIDER_WIDTH;
				mapFrame.setLayoutParams(rlm);

				TextView rTitle = (TextView) findViewById(R.id.custom_list_name_frame_text);
				rTitle.setHeight(mFrameHeight);

				TextView rSpacer = (TextView) findViewById(R.id.spacer);
				rSpacer.setHeight(mFrameHeight);

				ImageView iconDivider = (ImageView) findViewById(R.id.map_share_div);
				iconDivider.setMinimumHeight(mFrameHeight);

				ImageView shareIcon = (ImageView) findViewById(R.id.map_share_icon);
				shareIcon.setMinimumHeight(mFrameHeight);

				if (!alreadyDrawn) {

					alreadyDrawn = asyncOverlay.drawOverlay();
				}
			}
		} catch (Exception e) {
			LogFx.error(TAG, "onWindowFocusChanged exception: " + e.toString());
		}
	}




	public Object onRetainNonConfigurationInstance() {
		return mMapItems;
	}

	public void shareIconClick(View target) {
		final String newLine = "\n";

		LogFx.debug("MapView", "Share requested");

		ActivityFx.openShareText(DisplayMap.this, 
				mMapTitle + newLine + xmlAddressName + newLine + xmlStreetAddress + newLine + mMapLink);
	}




	private class MyLocationListener implements LocationListener {
		private String mServiceType; 

		public MyLocationListener(String serviceType) {
			mServiceType = serviceType;
		}

		@Override
		public void onLocationChanged(Location location) {
			try {
				// Called when a new location is found by the network location provider.
				myPoint = GeoFx.LocationToGeoPoint(location);
				itemizedoverlay.setMyLocation(myPoint);

				MapView mapView = (MapView) findViewById(R.id.mapview);

				if (initialLocation && alreadyDrawn) {				
					try {
						float myDistance = GeoFx.DistanceBetweenGeo(asyncOverlay.getCenterPoint(), myPoint);

						String geoString = "geo:" + location.getLatitude() + ", " + location.getLongitude();
						LogFx.debug(TAG, mServiceType + " listener onLocationChanged to location: " + geoString);
						LogFx.debug(TAG,  "My location distance " + String.valueOf(GeoFx.DistanceBetweenGeo(mapView.getMapCenter(), myPoint)));

						geoString = "geo:" + GeoFx.GeoToLocation(asyncOverlay.getCenterPoint()).getLatitude() + ", " + GeoFx.GeoToLocation(asyncOverlay.getCenterPoint()).getLongitude();
						LogFx.debug(TAG, mServiceType + " listener onLocationChanged to location: " + geoString);
						LogFx.debug(TAG,  "My location distance " + String.valueOf(GeoFx.DistanceBetweenGeo(asyncOverlay.getCenterPoint(), myPoint)));

						if (myDistance < GlVar.MAP_MAX_DISTANCE_SPAN) {
							LogFx.debug("MapView", "Spanning to my location");
							mapView.getController().animateTo(asyncOverlay.updateCenterPoint(myPoint));
							mapView.getController().zoomToSpan(asyncOverlay.getLatSpanE6(myPoint), asyncOverlay.getLonSpanE6(myPoint));		        		
						} 

						initialLocation = false;
					} catch (Exception e) {
						LogFx.warn(TAG, "Exception: " + e.toString());
					}
				}
				if (!pulledBackOnce) {
					/* If centered on myPoint, and on another continent (>MAX_SPAN_DISTANCE) 
					 * Then pull back to where the POIs are */
					if ((GeoFx.DistanceBetweenGeo(mapView.getMapCenter(), myPoint) == 0) &&
							(GeoFx.DistanceBetweenGeo(asyncOverlay.getCenterPoint(), myPoint) > GlVar.MAP_MAX_DISTANCE_SPAN)) {
						String geoString = "geo:" + GeoFx.GeoToLocation(asyncOverlay.getCenterPoint()).getLatitude() + ", " + GeoFx.GeoToLocation(asyncOverlay.getCenterPoint()).getLongitude();
						LogFx.debug(TAG, mServiceType + " listener onLocationChanged to location: " + geoString);
						LogFx.debug(TAG,  "My location distance " + String.valueOf(GeoFx.DistanceBetweenGeo(asyncOverlay.getCenterPoint(), myPoint)));

						LogFx.debug(TAG, "Pulling back to POIs");
						mapView.getController().animateTo(asyncOverlay.getCenterPoint());
						mapView.getController().zoomToSpan(asyncOverlay.getLatSpanE6(), asyncOverlay.getLonSpanE6());
						pulledBackOnce = true;
					}

				}
			} catch (Exception e) {
				LogFx.error(TAG, "onLocationChanged exception: " + e.toString());
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {LogFx.debug("MapView", mServiceType + " listener onStatusChanged");}

		@Override
		public void onProviderEnabled(String provider) {LogFx.debug("MapView", mServiceType + " listener onProviderEnabled");}

		@Override
		public void onProviderDisabled(String provider) {LogFx.debug("MapView", mServiceType + " listener onProviderDisabled");}

	}

	protected class GetShortUrl extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String urlToShorten = params[0];
			String mshortUrl="";
			LogFx.debug("Google", "Shortening Url " + urlToShorten);
			try {				
				mshortUrl = Google.shorten(urlToShorten);
			} catch (Exception e) {
				e.printStackTrace();
			}			
			return mshortUrl;			
		}    	
		@Override
		protected void onPostExecute(String result) {
			LogFx.debug("Google", "onPostExecute Url " + result);
			mMapLink = result;
		}
	}


	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	
	@Override
	protected void onPause() {
		try {
			super.onPause();
			LogFx.debug(TAG, "onPause");
			myLocationOverlay.disableMyLocation();
			locationManager.removeUpdates(locationNetworkListener);
			locationManager.removeUpdates(locationGpsListener);
		} catch (Exception e) {
			LogFx.debug(TAG, "onPause exception: " + e.toString());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			LogFx.debug("MapView", "onResume");
			myLocationOverlay.enableMyLocation();
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationNetworkListener);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationGpsListener);


			LogFx.debug(TAG, "OnResume called");
			boolean updated = DLPref.getSPCatalogUpdated(DisplayMap.this);
			if (updated) {
				LogFx.debug(TAG, "OnResume killing activity");
				finish();
			}
		} catch (Exception e) {
			LogFx.error(TAG, "OnResume exception: " + e.toString());
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {		
		LogFx.debug("MapView", "Map onConfigurationChanged " + newConfig.toString());
		initialLocation = true;
		rotated = true;
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onBackPressed() { // this requires API level 5 or higher
		super.onBackPressed();
		rotated = false;
	}

	@Override
	protected void onDestroy() {
		if (!rotated) {
			try {
				SPVar.destroyMapItems(mapKey);
				LogFx.warn(TAG, "Destroying key: " + mapKey);
				rotated = false;
			} catch (Exception e) {  // may happen if Android reclaimed memory and this Hash no longer exists
				LogFx.error(TAG, "onDestroy exception: " + e.toString());
				rotated = false;
			}
		}
		super.onDestroy();
	}


}