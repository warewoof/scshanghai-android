package com.warewoof.shanghai.object;
import java.io.Serializable;
import java.util.Locale;
import java.util.StringTokenizer;

import com.warewoof.shanghai.GlVar;
import com.warewoof.shanghai.SPVar;

@SuppressWarnings("serial")
public class ChildAttribute implements Serializable {

	private String atName;
	private String atValue;
	private String atLink = "";
	private String atOptions;
	private String[] atOptionsArray;
	private String Parent;
	private String Child;	
	private boolean atLaunchAndroidViewer = false;
	private float atValueFontSize = SPVar.getPrefTextValueSize();
	//private String atNodeType;
	

	public void setChildName(String name) {
		this.Child = name;
	}
	
	public String getChildName() {
		return this.Child;
	}
	
	public void setParentName(String name) {
		this.Parent = name;		
	}
	
	public String getParentName() {
		return this.Parent;
	}

	public void setName(String name) {
		atName = name.trim();
	}
	
	public String getName() {
		if (atName == null) {
			atName = "";
		}
		return atName;
	}
	
	public void setValue(String value) {
		atValue = value.trim();
	}
	
	public String getValue() {
		if (atValue == null) {
    		atValue = "";
    	}
		return atValue;
	}
		
	public boolean optionCopy() {
		if (atOptions != null) {
			//LogFx.debug("ChildAtt", "atOptions: " + atOptions);
			if (atOptions.toLowerCase(Locale.US).contains("copy")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public void setLink(String link) {
		atLink = link.trim();
	}
	
	public String getLink() {
		if (atLink == null) {
			atLink = "";
		} 
		return atLink;
	}
	
	public String getOptions() {
		if (atOptions == null) {
			atOptions = "";
		}
		return atOptions;
	}
	
	public void setOptions(String options) {
		atOptions = options;
		atOptionsArray = options.split(",");
		for (int i=0; i<atOptionsArray.length; i++) {
			if (atOptionsArray[i].equalsIgnoreCase(GlVar.OPTION_IMAGEVIEWER)) {
				atLaunchAndroidViewer = true;
			}
			if (atOptionsArray[i].toLowerCase(Locale.US).startsWith(GlVar.OPTION_FONT)) {
				StringTokenizer tokens = new StringTokenizer(atOptionsArray[i].toLowerCase(Locale.US), ":");
				tokens.nextToken(); //should be "font"
				atValueFontSize = Integer.valueOf(tokens.nextToken()) * SPVar.getPrefTextValueModifier();
			}
		}
	}
	
	public boolean launchAndroidViewer() {
		if (atLaunchAndroidViewer) {
			return true;
		} else {
			return false;
		}		
	}
	
	public float getValueTextSize() {
		return atValueFontSize;
	}
	
	
	
	/*
	public void setNodeType(String nodeType) {
		atNodeType = nodeType.trim();
	}
	
	public String getNodeType() {
		return atNodeType;
	}
	*/
	
}