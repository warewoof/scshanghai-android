package com.warewoof.shanghai.activity.listview;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.warewoof.shanghai.GlVar;
import com.warewoof.shanghai.R;
import com.warewoof.shanghai.object.ListItem;

public class CustomListAdapter extends BaseAdapter {
	//private static String TAG = "CustomListAdapter";
	private ArrayList<ListItem> listItems = null;
	private Context context;
	

	public CustomListAdapter(Context context, ArrayList<ListItem> listObj) {
		this.listItems = listObj;
		this.context = context;
	}


	public int getCount() {
		return listItems.size();
	}

	public Object getItem(int position) {
		return listItems;
	}

	public long getItemId(int position) {
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		final Typeface myTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/ubuntu.ttf");
		LinearLayout rowLayout = null;
		if (convertView == null) {
			rowLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.listitem, parent, false);
		} else {
			rowLayout = (LinearLayout) convertView;
		}
		TextView name = (TextView) rowLayout.findViewById(R.id.name);
		name.setTypeface(myTypeface);
		name.setText(listItems.get(position).getItemName());
		name.setTextColor(GlVar.LIST_TEXT_COLOR);

		TextView nextArrow = (TextView) rowLayout.findViewById(R.id.next_arrow);
		nextArrow.setTextColor(GlVar.LIST_NEXT_ARROW_COLOR);
		//nextArrow.setTextSize(XmlProp.LIST_NEXT_ARROW_SIZE);
		int childrenCount = listItems.get(position).getNumChildren();
		if ((childrenCount > 0) && 
				((!listItems.get(position).getItemNode().equals(GlVar.CHILD_NODE_TAG)))) {
			nextArrow.setText(Integer.toString(childrenCount));
		}

		TextView spacer = (TextView) rowLayout.findViewById(R.id.spacer);
		
		if (listItems.get(position).getItemColor() == GlVar.SPACER_COLOR_HIDE_CODE) { // no indent because spacer is purposely hidden
			spacer.setVisibility(View.GONE);
		} else {
			spacer.setBackgroundColor(listItems.get(position).getItemColor());  // set custom spacer color
			//name.setPadding(15, 5, 15, 5);
		}
		//ImageView spacer = (ImageView) rowLayout.findViewById(R.id.spacer);
		//ImageView spacer = (ImageView) rowLayout.findViewById(R.id.spacer);
		
		
		

		return rowLayout;
	}
}