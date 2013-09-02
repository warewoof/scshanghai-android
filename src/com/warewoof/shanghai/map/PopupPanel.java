package com.warewoof.shanghai.map;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.maps.MapView;
import com.warewoof.shanghai.R;
import com.warewoof.shanghai.function.LogFx;

class PopupPanel {
	View popup;
	boolean isVisible = false;
	private MapView map = null;
	private Activity mContext = null;
	private int screenWidth;
	private int screenHeight;
	
	PopupPanel(Activity context, int layout) {
		this.mContext = context;
		map = (MapView) mContext.findViewById(R.id.mapview);
		ViewGroup parent = (ViewGroup)map.getParent();

		popup = mContext.getLayoutInflater().inflate(layout, parent, false);
		
		popup.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				hide();
			}
		});
		/*
		DisplayMetrics metrics = new DisplayMetrics();
		mContext.getSystemService(Context.WINDOW_SERVICE);
		((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
		screenHeight = metrics.heightPixels;
		screenWidth = metrics.widthPixels;
		
		screenHeight = map.getHeight();
		screenWidth = map.getWidth();
		LogFx.debug("Init Popup Panel", "Screen metrics height:" + screenHeight + " width:" + screenWidth);*/
	}

	View getView() {
		return(popup);
	}

	void show(boolean alignTop) {
		RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
				);

		if (alignTop) {
			lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			//lp.setMargins(0, MathFx.dpToPx(mContext, 20), 0, 0);
			lp.setMargins(0, 20, 0, 0);
		}
		else {
			lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			//lp.setMargins(0, 0, 0, MathFx.dpToPx(mContext, 90));
			lp.setMargins(0, 0, 0, 90);
		}

		hide();

		((ViewGroup)map.getParent()).addView(popup, lp);
		isVisible=true;
	}
	
	void showCoord(int x, int y) {
		RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		LogFx.debug("showCoord", "Screen metrics height:" + screenHeight + " width:" + screenWidth);
		LogFx.debug("showCoord", "X:"+x+" Y:"+y);
		lp.topMargin = y;
		lp.leftMargin = x;
		hide();

		((ViewGroup)map.getParent()).addView(popup, lp);
		isVisible=true;
	}

	void hide() {
		if (isVisible) {
			isVisible=false;
			((ViewGroup)popup.getParent()).removeView(popup);
		}
	}
}