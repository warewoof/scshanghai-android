package com.warewoof.shanghai.activity.childview;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.warewoof.shanghai.GlVar;
import com.warewoof.shanghai.R;
import com.warewoof.shanghai.SPVar;
import com.warewoof.shanghai.function.ActivityFx;
import com.warewoof.shanghai.function.LogFx;
import com.warewoof.shanghai.function.StringFx;
import com.warewoof.shanghai.object.ChildAttribute;
import com.warewoof.shanghai.object.MapItem;

public class ChildTextLayout {
	private static String TAG = "ChildTextLayout Class";
	private String mName;
	private String mValue;
	private String mLink;
	private Context mContext;
	private String mTitle;
	private int mTabColor;

	public ChildTextLayout(Context context) {
		mContext = context;
	}

	public LinearLayout CreateLayout(ChildAttribute childAtt, String activityTitle, int tabColor) {
		mName = childAtt.getName();
		mValue = StringFx.unescape(childAtt.getValue());
		mLink = childAtt.getLink();
		mTitle = activityTitle;
		mTabColor = tabColor;

		final Typeface myTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/ubuntu.ttf");
		
		try {			
			if ((mName.equals("")) && (mValue.equals(""))) {
				return new LinearLayout(mContext);
			}

			final LinearLayout childTextItem = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.child_textminimal, null);

			TextView childName =  (TextView) childTextItem.findViewById(R.id.childName);
			if (GlVar.CHILD_SUBSECTION_TITLE_TEXT_ALLCAPS) {
				childName.setText(mName.toUpperCase(Locale.US));
			} else {
				childName.setText(mName);
			}
			childName.setTypeface(myTypeface);
			childName.setTextSize(SPVar.getPrefTextTitleSize());
			childName.setTextColor(GlVar.CHILD_SUBSECTION_TITLE_TEXT_COLOR);
			TextView childValue =  (TextView) childTextItem.findViewById(R.id.childValue);
			childValue.setTypeface(myTypeface);
			childValue.setText(mValue);
			//childValue.setTextSize(SPVar.getPrefTextValueSize());
			childValue.setTextSize(childAtt.getValueTextSize());
			childValue.setTextColor(GlVar.CHILD_SUBSECTION_VALUE_TEXT_COLOR);


			//childValue.setOnLongClickListener(new CopyOnLongClickListener());
			childValue.setOnLongClickListener(new OptionsOnLongClickListener());

			if (!mLink.equals("")){  // set Link listeners
				if (mLink.toLowerCase(Locale.US).startsWith("tel:")) {
					childValue.setOnClickListener(new CallOnClickListener());
				} else if (mLink.toLowerCase(Locale.US).startsWith("geo:")) {
					childValue.setOnClickListener(new MapOnClickListener());
				} else if (mLink.toLowerCase(Locale.US).startsWith("http")) {
					childValue.setOnClickListener(new WebOnClickListener());
				} else if (StringFx.isEmail(mLink)) {
					childValue.setOnClickListener(new MailOnClickListener());
				} else {
					// do nothing
				}
				childValue.setTextColor(GlVar.CHILD_LINK_COLOR);			 		        
			}
			
			//ImageView textDivider = (ImageView) childTextItem.findViewById(R.id.childTextDivider);
			if (childName.getText().equals("")) {
				childName.setVisibility(TextView.GONE);
				//textDivider.setVisibility(ImageView.GONE);
				childValue.setTypeface(null, Typeface.ITALIC);
				childValue.setTextColor(GlVar.CHILD_SUBSECTION_ORPHAN_TEXT_COLOR);
			}
			
			if (childValue.getText().equals("")) {
				childValue.setVisibility(TextView.GONE);
				//textDivider.setVisibility(ImageView.GONE);
				childName.setTypeface(null, Typeface.BOLD_ITALIC);
				childName.setTextColor(GlVar.CHILD_SUBSECTION_ORPHAN_TEXT_COLOR);
			}


			return childTextItem;

		} catch (Exception e) {
			LogFx.debug("RvBasicText", "Exception " + e.toString());
			return new LinearLayout(mContext);  // return empty layout on error
		}
	}


	public class CopyOnLongClickListener implements OnLongClickListener{
		@Override
		public boolean onLongClick(View v) {
			Toast.makeText(mContext, "Text Copied to Clipboard", Toast.LENGTH_SHORT).show();
			ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Activity.CLIPBOARD_SERVICE);
			//clipboard.setText(mName+"\n"+mValue);
			clipboard.setText(mValue);
			return true;
		}
	}
	
	public class OptionsOnLongClickListener implements OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			final String[] choices = {"Share with...", "Taxi Card", "Copy to Clipboard"};
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle("Options");
			builder.setNegativeButton("Cancel", null);
			builder.setItems(choices, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					LogFx.debug(TAG, "Selected " + Integer.toString(which));
					switch (which) {
					case 0:		// Send Message
						ActivityFx.openShareText(mContext, mTitle + "\n" + mValue );
						break;
					case 1:		// Show Taxi Card
						ActivityFx.openTaxiCard(mContext, mTitle + " " + mName, mValue, mTabColor);
						break;
					case 2:		// Copy to Clipboard
						Toast.makeText(mContext, "Text Copied to Clipboard", Toast.LENGTH_SHORT).show();
						ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Activity.CLIPBOARD_SERVICE);
						clipboard.setText(mValue);
						break;
					default:
						//do nothing
						break;
					}
				}
			});
			
			AlertDialog alert = builder.create();
			alert.show();
			
			return true;
		}
		
	}

	public class CallOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			ActivityFx.openDial(mContext, mLink.toLowerCase(Locale.US));

		}
	}

	public class MapOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {

			if (SPVar.getNativeMapOption(mContext)) {  // launch native google map
				String[] tempArray = mLink.split("[,:?]"); 			
				String parseString = "geo:0,0?q=" + tempArray[1] + "," + tempArray[2] + "(" + mTitle + ")";
				LogFx.debug(TAG, parseString);
				parseString = StringFx.urlEncode(parseString);
				LogFx.debug(TAG, parseString);
				Uri uri = Uri.parse(parseString);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				mContext.startActivity(intent);
			} else {  // launch the pug map
				ArrayList<MapItem> mapItems = new ArrayList<MapItem>();
				MapItem mi = new MapItem(mLink, mName, mValue);
				mapItems.add(mi);
				ActivityFx.openMap(mContext, mTitle, mTabColor, mapItems, true);
			}
		}
	}

	public class WebOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(mLink));
			try{	
				mContext.startActivity(browser);
			} catch (Exception e) {
				Toast.makeText(mContext, "Unable to launch browser " + e.toString(), Toast.LENGTH_SHORT).show();
			}
		}
	}

	public class MailOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Intent email = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + mLink));
			try {
				mContext.startActivity(email);
			} catch (Exception e) {
				Toast.makeText(mContext, "Unable to send email " + e.toString(), Toast.LENGTH_SHORT).show();
			}
		}
	}
}