package com.warewoof.shanghai.function;

import android.util.Log;

import com.warewoof.shanghai.GlVar;


public class LogFx {
	
	public static void debug(String tag, String text) {
		if (!GlVar.RELEASE_MODE) {			
			Log.d(tag, text);			
		}		
	}
	
	public static void info(String tag, String text) {
		if (!GlVar.RELEASE_MODE) {			
			Log.i(tag, text);			
		}		
	}
	
	public static void warn(String tag, String text) {
		if (!GlVar.RELEASE_MODE) {			
			Log.w(tag, text);			
		}		
	}
	
	public static void error(String tag, String text) {
		if (!GlVar.RELEASE_MODE) {			
			Log.e(tag, text);			
		}		
	}
	
	
	
	
}