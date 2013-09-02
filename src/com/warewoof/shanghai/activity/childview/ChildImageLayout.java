package com.warewoof.shanghai.activity.childview;

import java.io.File;
import java.io.FileInputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.warewoof.shanghai.R;
import com.warewoof.shanghai.SPVar;
import com.warewoof.shanghai.function.ActivityFx;
import com.warewoof.shanghai.function.LogFx;
import com.warewoof.shanghai.function.StringFx;
import com.warewoof.shanghai.object.ChildAttribute;

public class ChildImageLayout {
	private String mLink;
	private Context mContext;
	private ChildAttribute mChildAtt;
	private float scaleFactor;

	public ChildImageLayout(Context context) {
		mContext = context;
	}

	public RelativeLayout CreateLayout(ChildAttribute childAtt) {

		RelativeLayout childImageItem = null;
		mChildAtt = childAtt;
		try {
			childImageItem = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.child_imageminimal, null);

			mContext.getSystemService(Context.WINDOW_SERVICE);
			ImageView imgView = (ImageView) childImageItem.findViewById(R.id.child_image);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			mLink = StringFx.getLocalChildDataPath(mChildAtt.getParentName(), mChildAtt.getChildName(), mChildAtt.getLink());

			File imageFile = new File (mLink);
			if (imageFile.exists()) {
				LogFx.debug("RlBasicImage", "Drawing ImageView for " + mLink);
				Bitmap bm = decodeFile(mLink);
				imgView.setImageBitmap(bm);
			} else {
				LogFx.debug("RlBasicImage", "Cannot find ImageView for " + mLink);
				imgView.setVisibility(ImageView.GONE);
				childImageItem.findViewById(R.id.child_image_textRight).setVisibility(TextView.GONE);
				childImageItem.findViewById(R.id.child_image_textBelow).setVisibility(TextView.GONE);
				return childImageItem;
			}

			if (mChildAtt.launchAndroidViewer()) {
				imgView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							ActivityFx.openImage(mContext, mLink);
						} catch (android.content.ActivityNotFoundException e) {
							Toast.makeText(mContext, "No handler for this type of file.", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}

			if (scaleFactor < 1.1) {
				TextView tvText = (TextView) childImageItem.findViewById(R.id.child_image_textRight);
				tvText.setText(StringFx.unescape(childAtt.getValue()));
				tvText.setTextSize(SPVar.getPrefImageTextSize());
				tvText = (TextView) childImageItem.findViewById(R.id.child_image_textBelow);
				tvText.setVisibility(TextView.GONE);
			} else {
				TextView tvText = (TextView) childImageItem.findViewById(R.id.child_image_textBelow);
				tvText.setText(StringFx.unescape(childAtt.getValue()));
				tvText.setTextSize(SPVar.getPrefImageTextSize());
				tvText = (TextView) childImageItem.findViewById(R.id.child_image_textRight);
				tvText.setVisibility(TextView.GONE);
			}
		}
		catch (Exception e) {
			LogFx.debug("RlImageMinimalFrame", "Error building ImageView for " + mLink + ", exception " + e.toString());
		}

		return childImageItem;

	}



	private Bitmap decodeFile(String path){
		try {
			File f = new File(path);
			DisplayMetrics metrics = new DisplayMetrics();
			((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
			//Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f),null,o);

			//The new size we want to scale to
			final int REQUIRED_SIZE=200;

			LogFx.debug("decodeBitmap", "output width:"+ String.valueOf(o.outWidth)+" height:"+ String.valueOf(o.outHeight));
			scaleFactor = (float) o.outWidth/o.outHeight;
			LogFx.debug("decodeBitmap", "scaleFactor:"+String.valueOf(scaleFactor));

			//Find the correct scale value. It should be the power of 2.
			int scale=1;
			while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
				scale*=2;

			//Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize=scale;
			Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);



			if (bm.getWidth() > bm.getHeight()) {
				LogFx.debug("decodeBitmap", "width:"+ String.valueOf((int) (metrics.densityDpi * scaleFactor))+" height:"+ String.valueOf(metrics.densityDpi));
				return Bitmap.createScaledBitmap(bm, (int) (metrics.densityDpi * scaleFactor), metrics.densityDpi, true);
			} else {
				LogFx.debug("decodeBitmap", "width:"+ String.valueOf(metrics.densityDpi)+" height:"+ String.valueOf((int) (metrics.densityDpi/scaleFactor)));
				return Bitmap.createScaledBitmap(bm, metrics.densityDpi, (int) (metrics.densityDpi / scaleFactor), true);
			}




		} catch (Exception e) {}
		return null;
	}

}