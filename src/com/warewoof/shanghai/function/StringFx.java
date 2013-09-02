package com.warewoof.shanghai.function;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.warewoof.shanghai.GlVar;


public class StringFx {
	//private static final String TAG = "StringFx";
	
	public static String PrintArray(ArrayList<String> strs) {
		String tempStr="";
		for (int i=0; i < strs.size(); i++) {
			tempStr = tempStr + " " + strs.get(i);
		}
		return tempStr;
	}
	
	public static String getLocalChildDataPath(String parent, String child) {
		String sub1 = parent.replaceAll("[^A-Za-z0-9]", "").trim().toLowerCase() + "/";
		String sub2 = child.replaceAll("[^A-Za-z0-9]", "").trim().toLowerCase() + "/";
		return GlVar.CATALOG_LOCAL_DATA_DIRECTORY + sub1 + sub2;
	}
	
	public static String getLocalChildDataPath(String parent, String child, String file) {
		return getLocalChildDataPath(parent, child) + file;
	}
	
	public static String unescape(String description) {
	    return description.replaceAll("\\\\n", "\\\n");
	}
	
	public static boolean isEmail(String inputMail)
    {   
		Pattern pattern= Pattern.compile("^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+");
	    return pattern.matcher(inputMail).matches();
    }
	
	public static String urlEncode(String url) {
		return url.replace("&", "%26");
	}
}



