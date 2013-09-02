package com.warewoof.shanghai.activity.childview;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.warewoof.shanghai.GlVar;
import com.warewoof.shanghai.R;
import com.warewoof.shanghai.function.LogFx;
import com.warewoof.shanghai.function.MathFx;

public class ChildTextFrameLayout {
	private String TAG = "ChildTextFrameLayout";
	private String mValue;
	private Activity mContext;
	private int mTabColor;
	private LinearLayout childTextItem;
	private boolean shareIconVisible = true;
	private boolean mapIconVisible = true;
	private ViewTreeObserver vto;
	
	public ChildTextFrameLayout(Activity context) {
		mContext = context;
	}
	
	
	public LinearLayout CreateLayout(String title, int tabColor) {
		
		mValue = title;
		mTabColor = tabColor;
		childTextItem = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.child_frametext, null);
		
		final ImageView spacerTab =  (ImageView) childTextItem.findViewById(R.id.barSpacer);
		final Typeface myTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/ubuntu.ttf");
		spacerTab.setBackgroundColor(mTabColor);
		
		TextView childName =  (TextView) childTextItem.findViewById(R.id.barText);
		childName.setTypeface(myTypeface);
		childName.setText(mValue);
		childName.setOnClickListener(new FinishOnClickListener());		
		
		ImageView divider =  (ImageView) childTextItem.findViewById(R.id.barDividerImage);
		divider.setMinimumHeight(MathFx.dpToPx(mContext, GlVar.FRAME_DIVIDER_WIDTH));
		
		final ImageView mapDiv =  (ImageView) childTextItem.findViewById(R.id.barMapDiv);
		mapDiv.setMinimumWidth(MathFx.dpToPx(mContext, GlVar.FRAME_DIVIDER_WIDTH));
    	final ImageView mapIcon =  (ImageView) childTextItem.findViewById(R.id.barMapIcon);
    	int iconPadding = MathFx.dpToPx(mContext, 5);
    	mapIcon.setPadding(iconPadding, 0, iconPadding, 0);
    	int mapIconWidth = mContext.getResources().getDrawable(R.drawable.ic_menu_mapmode).getIntrinsicWidth();
    	
    	final ImageView shareDiv =  (ImageView) childTextItem.findViewById(R.id.barShareDiv);
    	shareDiv.setMinimumWidth(MathFx.dpToPx(mContext, GlVar.FRAME_DIVIDER_WIDTH));
		final ImageView shareIcon =  (ImageView) childTextItem.findViewById(R.id.barShareIcon);
		shareIcon.setPadding(iconPadding, 0, iconPadding, 0);
    	int shareIconWidth = mContext.getResources().getDrawable(R.drawable.ic_menu_mapmode).getIntrinsicWidth();
    	
    	RelativeLayout barTextHolder = (RelativeLayout)  childTextItem.findViewById(R.id.barTextHolder);
    	int totalIconWidth = (iconPadding * 4) + mapIconWidth + shareIconWidth + (MathFx.dpToPx(mContext, GlVar.FRAME_DIVIDER_WIDTH) * 2);
    	LogFx.debug(TAG, "Padding "+String.valueOf(totalIconWidth));
    	barTextHolder.setPadding(0, 0, totalIconWidth, 0);
    	
    	vto = childTextItem.getViewTreeObserver();
	
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				
				int frameHeight = childTextItem.getHeight() - MathFx.dpToPx(mContext, GlVar.FRAME_DIVIDER_WIDTH);
				spacerTab.setMinimumHeight(frameHeight);				
				mapDiv.setMinimumHeight(frameHeight);
		    	shareDiv.setMinimumHeight(frameHeight);
		    	mapIcon.setMinimumHeight(frameHeight);
		    	shareIcon.setMinimumHeight(frameHeight);
		    	LogFx.debug("color", String.valueOf(mTabColor));
		    	LogFx.debug("hiehgt", String.valueOf(frameHeight));
    	        ViewTreeObserver obs = childTextItem.getViewTreeObserver();
    	        obs.removeGlobalOnLayoutListener(this);				
				
			}
    	});  
		
		
		return childTextItem;	    
       
	}
	
	
	public class FinishOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			mContext.finish();				
		}			
	}

	public void DisableShare() {
		ImageView shareIcon =  (ImageView) childTextItem.findViewById(R.id.barShareIcon);
		shareIcon.setVisibility(ImageView.GONE);
		
		ImageView shareDiv =  (ImageView) childTextItem.findViewById(R.id.barShareDiv);
		shareDiv.setVisibility(ImageView.GONE);
		
		shareIconVisible = false;
		
		int totalIconWidth = 0;
		if (mapIconVisible) {
	    	final ImageView mapIcon =  (ImageView) childTextItem.findViewById(R.id.barMapIcon);
	    	LayoutParams mapIconParams = (LayoutParams) mapIcon.getLayoutParams();
	    	mapIconParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    	
	    	int mapIconWidth = mContext.getResources().getDrawable(R.drawable.ic_menu_mapmode).getIntrinsicWidth();
	    	int iconPadding = MathFx.dpToPx(mContext, 5);
	    	totalIconWidth = (iconPadding * 2) + mapIconWidth + MathFx.dpToPx(mContext, GlVar.FRAME_DIVIDER_WIDTH);
	    	
		}
		
		RelativeLayout barTextHolder = (RelativeLayout)  childTextItem.findViewById(R.id.barTextHolder);
    	barTextHolder.setPadding(0, 0, totalIconWidth, 0);
    	LogFx.debug(TAG, "Padding "+String.valueOf(totalIconWidth));
	}
	
	public ImageView getShareIcon() {  // used to set onClickListeners
		return (ImageView) childTextItem.findViewById(R.id.barShareIcon);
		
	}

	public void DisableMap() {
		ImageView mapIcon =  (ImageView) childTextItem.findViewById(R.id.barMapIcon);
		mapIcon.setVisibility(ImageView.GONE);
		
		ImageView mapDiv =  (ImageView) childTextItem.findViewById(R.id.barMapDiv);
		mapDiv.setVisibility(ImageView.GONE);
		
		int totalIconWidth = 0;
		if (shareIconVisible) {
	    	int shareIconWidth = mContext.getResources().getDrawable(R.drawable.ic_menu_mapmode).getIntrinsicWidth();
	    	int iconPadding = MathFx.dpToPx(mContext, 5);
	    	totalIconWidth = (iconPadding * 2) + shareIconWidth + MathFx.dpToPx(mContext, GlVar.FRAME_DIVIDER_WIDTH);	    	
		}
		
		RelativeLayout barTextHolder = (RelativeLayout)  childTextItem.findViewById(R.id.barTextHolder);
    	barTextHolder.setPadding(0, 0, totalIconWidth, 0);
    	LogFx.debug(TAG, "Padding "+String.valueOf(totalIconWidth));
	}
	
	public ImageView GetMapImageView() {
		return (ImageView) childTextItem.findViewById(R.id.barMapIcon);
		
	}
}