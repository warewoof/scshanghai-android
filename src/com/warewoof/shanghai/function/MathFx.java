package com.warewoof.shanghai.function;

import java.math.BigDecimal;

import android.content.Context;


public class MathFx {
	//private static final String TAG = "MathFx";
	
	public static int dpToPx(Context mContext, int dp) {
	    final float scale = mContext.getResources().getDisplayMetrics().density;
	    return (int) (dp * scale + 0.5f);
	}
	
	public static int pxToDp(Context mContext, int px) {
		final float scale = mContext.getResources().getDisplayMetrics().density;
		return (int) ((px - 0.5f) / scale);
	}
	
	public static double round(double unrounded, int precision, int roundingMode)
	{
	    BigDecimal bd = new BigDecimal(unrounded);
	    BigDecimal rounded = bd.setScale(precision, roundingMode);
	    return rounded.doubleValue();
	}

	public static int dpToPx(Context mContext, double px) {
		final float scale = mContext.getResources().getDisplayMetrics().density;
		return (int) ((px - 0.5f) / scale);
	}
}



