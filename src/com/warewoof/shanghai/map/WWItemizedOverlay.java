package com.warewoof.shanghai.map;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.warewoof.shanghai.R;
import com.warewoof.shanghai.function.ActivityFx;
import com.warewoof.shanghai.function.GeoFx;
import com.warewoof.shanghai.function.LogFx;
import com.warewoof.shanghai.function.MathFx;

public class WWItemizedOverlay extends ItemizedOverlay<WWOverlayItem> {
	private static String TAG = "WWItemizedOverlay";
	private ArrayList<WWOverlayItem> mOverlays = new ArrayList<WWOverlayItem>();
	private PopupPanel panel;
	private MapView map = null;
	private GeoPoint myLocation = null;
	private Context mContext;

	public WWItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public WWItemizedOverlay(Drawable defaultMarker, Context context, MapView mapView) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		panel = new PopupPanel((Activity) context, R.layout.popup);
		map = mapView;
		
	}

	@Override
	protected WWOverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	public void addOverlay(WWOverlayItem overlay) {
		mOverlays.add(overlay);
	}
	
	public void fastPopulate() {
		try {
			populate();  // populate once after all overlay items add is several orders faster
			setLastFocusedIndex(-1);
		} catch (Exception e) {
			LogFx.debug(TAG, "fastPopulate exception:" +e.toString());
		}
	}



	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		//super.draw(canvas, mapView, shadow);
		
		//if (!shadow)
	        //super.draw(canvas, mapView, shadow);
		super.draw(canvas, mapView, false);
		//boundCenterBottom(marker);
	}
	@Override
	protected boolean onTap(int i) {
		
		final WWOverlayItem item = (WWOverlayItem) getItem(i);
		GeoPoint geo = item.getPoint();
		Point pt = map.getProjection().toPixels(geo, null);

		View view = panel.getView();
		
		if (myLocation != null) {
			String distance = String.valueOf(MathFx.round(GeoFx.DistanceBetweenGeo(geo, myLocation)/1000, 2, BigDecimal.ROUND_HALF_UP)) + "km";
			((TextView)view.findViewById(R.id.title)).setText(item.getTitle() + "\n" + distance);
		} else {
			((TextView)view.findViewById(R.id.title)).setText(item.getTitle());
		}
		if ((item.getParentName() != "") && (item.getChildName() != "")) {  // this map was drawn from a List section
			
			view.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.button_white));
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//ActivityFx.openChild(mContext, item.getParentName(), item.getChildName(), item.getColor(), 
						//	false, false, false, false);
					ActivityFx.openChildWithHighlightAOI(mContext, item.getParentName(), item.getChildName(), item.getColor(), 
							false, false, false, false, item.getSnippet());
				}
				
			});
		
			panel.showCoord(pt.x - MathFx.dpToPx(mContext, 60), pt.y - MathFx.dpToPx(mContext, 120));
		} else {  // this map was drawn from a Child page
			panel.show(pt.y*2>map.getHeight());
		}
		//panel.show(pt.y*2>map.getHeight());
		
		return(true);
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		
		panel.hide();		
		
		return super.onTouchEvent(event, mapView);
		
	}
	
	public void setMyLocation(GeoPoint mLoc) {
		myLocation = mLoc;
	}

	
}


