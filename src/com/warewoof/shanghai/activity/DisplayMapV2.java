package com.warewoof.shanghai.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.MapView.LayoutParams;
import com.warewoof.shanghai.GlVar;
import com.warewoof.shanghai.R;
import com.warewoof.shanghai.SPVar;
import com.warewoof.shanghai.function.ActivityFx;
import com.warewoof.shanghai.function.GeoFx;
import com.warewoof.shanghai.function.LogFx;
import com.warewoof.shanghai.function.MathFx;
import com.warewoof.shanghai.google.Google;
import com.warewoof.shanghai.map.WWItemizedOverlay;
import com.warewoof.shanghai.object.MapItem;

/**
 * This shows how to retain a map across activity restarts (e.g., from screen rotations), which can
 * be faster than relying on state serialization.
 */
public class DisplayMapV2 extends FragmentActivity implements
			ConnectionCallbacks,
			OnConnectionFailedListener,
			OnMyLocationButtonClickListener, LocationListener {

	private final String TAG = "DisplayMapV2";
    private GoogleMap mMap;
    private LocationClient mLocationClient;
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	private LatLng point;
	private String xmlAddressName;
	private String xmlStreetAddress;
	private String mMapTitle;
	private int tabColor;
	private String mMapLink;
	private int mFrameHeight;
	private ArrayList<MapItem> mMapItems;
	public GoogleMap mapView;
	public WWItemizedOverlay itemizedoverlay;
	private boolean requestFromChild = false;
	private String mapKey = "";
	private LatLngBounds.Builder mBuilder = new LatLngBounds.Builder();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	try {
	    	LogFx.debug(TAG, "onCreate");
	    	super.onCreate(savedInstanceState);
	        setContentView(R.layout.mapview_v2);
	
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
			final Typeface myTypeface = Typeface.createFromAsset(this.getAssets(), "fonts/ubuntu.ttf");
			title.setTypeface(myTypeface);
			title.setText(mMapTitle);
			title.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();				
				}			
			});
	        
	        SupportMapFragment mapFragment =
	                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfrag);
	
	        if (savedInstanceState == null) {
	            // First incarnation of this activity.
	            mapFragment.setRetainInstance(true);
	        } else {
	            // Reincarnated activity. The obtained map is the same map instance in the previous
	            // activity life cycle. There is no need to reinitialize it.
	            mMap = mapFragment.getMap();
	        }
	        setUpMapIfNeeded();
	        
	        if ((!requestFromChild) || (mMapItems.size() > 1)) {       	
				DisableMapShareIcon();

			} else {
				RelativeLayout.LayoutParams ftlp = (RelativeLayout.LayoutParams) title.getLayoutParams();
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
					point = mapItem.GetLatLng();
					if (point == null) {
						Toast.makeText(DisplayMapV2.this, "Map was not found, bad map data", Toast.LENGTH_SHORT).show();
						finish();
					} else {    	        
						mMapLink = GeoFx.LatLngToLink(point);        
						GetShortUrl task = new GetShortUrl();
						task.execute(mMapLink);
					}
				}

			}

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
    protected void onResume() {
    	try {
	    	LogFx.debug(TAG, "onResume");
	        super.onResume();
	        setUpMapIfNeeded();
	        setUpLocationClientIfNeeded();
	        mLocationClient.connect();
    	} catch (Exception e) {
			LogFx.error(TAG, "setUpMapIfNeeded exception: " + e.toString());
			
		}
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }

    private void setUpMapIfNeeded() {
    	
    	try {
	        if (mMap == null) {
	            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfrag))
	                    .getMap();
	            
	            if (mMap != null) {
	                setUpMap();
	            }
	        }
    	} catch (Exception e) {
			LogFx.error(TAG, "setUpMapIfNeeded exception: " + e.toString());
			
		}
    }
    

    private void setUpMap() {
    	
    	addMarkersToMap();

    	mMap.setOnMarkerClickListener(new MarkerClickListener());
    	
    	if (!requestFromChild) {
    		mMap.setOnInfoWindowClickListener(new InfoWindowClickListener());
    	}
        mMap.setMyLocationEnabled(true);

        LatLng centerPoint = GeoFx.CalculateCenterLatLng(mMapItems);
        CameraPosition cameraPosition = new CameraPosition.Builder()
									        .target(centerPoint)
									        .zoom(16)  
									        .build();  
        //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        
        mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition arg0) {
            	if (mMapItems.size() != 1) {
            		mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mBuilder.build(), 20));
            	}
            	mMap.setOnCameraChangeListener(null);
            }
        });
    }
    
    
    private void addMarkersToMap() {
    	
    	for (int i = 0; i < mMapItems.size(); i++) {
    		MapItem mapItem = mMapItems.get(i);
    		
    		xmlStreetAddress = mapItem.GetStreetAddress();
	        xmlAddressName = mapItem.GetAddressName();
	        String displayName = mapItem.GetChildName();
	        String snippet = xmlAddressName;
	        if (displayName == "") { //this intent came from DisplayList, since child name was not included    
	        	//this intent came from DisplayChild, so we want the "Address Name" rather than the child/section name
	        	if (mMapItems.size() ==1) {  //in the case of single POI, then we want the detailed street address to show instead
	        		displayName = xmlStreetAddress;
	        		snippet = "";
	        	} else {
	        		displayName = xmlAddressName;
	        		snippet = "";
	        	}
	        }
    		Marker marker = mMap.addMarker(new MarkerOptions()
    				.position(mapItem.GetLatLng())
    				.title(displayName)
    				.snippet(snippet)
    				.icon(BitmapDescriptorFactory.defaultMarker(mapItem.GetTabHue())));
    		
    		mapItem.SetMarkerId(marker.getId());

            LatLng position = new LatLng(mapItem.GetLat(), mapItem.GetLng());
            
            mBuilder.include(position);

            
    	}
    	
    
    }
    
    private class InfoWindowClickListener implements OnInfoWindowClickListener {
    	@Override
    	public void onInfoWindowClick(Marker arg0) {
    		LogFx.debug(TAG, "onInfoWindowClick");
    		String searchId = arg0.getId();
    		for (int i = 0; i < mMapItems.size(); i++) {
    			if (mMapItems.get(i).GetMarkerId().equals(searchId)) {
    				ActivityFx.openChildWithHighlightAOI(DisplayMapV2.this, mMapItems.get(i).GetParentName(), mMapItems.get(i).GetChildName(), mMapItems.get(i).GetTabColor(), 
							false, false, false, false, arg0.getSnippet());
    				break;
    			}
    		}
    		
    	}
    }
    
    private class MarkerClickListener implements OnMarkerClickListener {

		@Override
		public boolean onMarkerClick(Marker arg0) {
			if (mLocationClient != null && mLocationClient.isConnected()) {
				if (arg0.getSnippet().equalsIgnoreCase("")) {
		            Location markerLocation = new Location("");
		            markerLocation.setLatitude(arg0.getPosition().latitude);
		            markerLocation.setLongitude(arg0.getPosition().longitude);
		            float markerDistance = mLocationClient.getLastLocation().distanceTo(markerLocation);	            
		            String msg;
		            LogFx.debug(TAG, "MarkerClickListener distance: "+ Float.toString(markerDistance));
		            
		            if (markerDistance > 1000) {
		            	msg = String.format("%.2f", markerDistance / 1000) + " km";
		            } else {
		            	msg = String.format("%.0f", markerDistance) + " meter";
		            }		            
		            arg0.setSnippet(msg);
	            } 
	        }
			return false;
		}
    	
    }
    
    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
                    getApplicationContext(),
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }
    
    @Override
	public void onWindowFocusChanged(boolean hasFocus) {
		try {
			super.onWindowFocusChanged(hasFocus);
			RelativeLayout nameFrame = (RelativeLayout) findViewById(R.id.custom_list_name_frame);
			if (mFrameHeight != nameFrame.getHeight()) {
				mFrameHeight = nameFrame.getHeight() - GlVar.FRAME_DIVIDER_WIDTH;
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

			}
		} catch (Exception e) {
			LogFx.error(TAG, "onWindowFocusChanged exception: " + e.toString());
		}
	}
    
    public void shareIconClick(View target) {
		final String newLine = "\n";

		LogFx.debug("MapView", "Share requested");
		ActivityFx.openShareText(DisplayMapV2.this, 
				mMapTitle + newLine + xmlAddressName + newLine + xmlStreetAddress + newLine + mMapLink);
	}

	@Override
	public boolean onMyLocationButtonClick() {
		// this is currently bugged https://code.google.com/p/gmaps-api-issues/issues/detail?id=4789&q=apitype%3DAndroid2&colspec=ID%20Type%20Status%20Introduced%20Fixed%20Summary%20Stars%20ApiType%20Internal
		LogFx.debug(TAG, "onMyLocationButtonClick");
		return false;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		LogFx.debug(TAG, "onConnectionFailed arg0 = " + arg0.toString());
		
	}

	@Override
    public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(
                REQUEST,
                this);  // LocationListener
    }

	@Override
	public void onDisconnected() {
		LogFx.debug(TAG, "onDisconnected");
		
	}

	@Override
	public void onLocationChanged(Location arg0) {
		LogFx.debug(TAG,"onLocationChanged Location = " + arg0);
		
	}


	
}
