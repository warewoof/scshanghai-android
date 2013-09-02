package com.warewoof.shanghai.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;

import com.warewoof.shanghai.GlVar;
import com.warewoof.shanghai.R;
import com.warewoof.shanghai.SPVar;
import com.warewoof.shanghai.file.DLPref;
import com.warewoof.shanghai.file.NotifyCatalogUpdate;
import com.warewoof.shanghai.function.ActivityFx;
import com.warewoof.shanghai.function.LogFx;

public class DisplaySplashScreen extends Activity {
	private final String TAG = "DisplaySplashScreen";

	private RelativeLayout llPointer;
	private Thread splashTimer;
	private boolean appStarted = false; 
	private boolean backPressed = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.splash);

		/* Local directory is required for downloading and storage */
		File f = new File(GlVar.CATALOG_LOCAL_DIRECTORY);  
		if (!f.isDirectory()) {  // something went seriously wrong in the previous creation of the directory, delete this and start over
			LogFx.debug(TAG, GlVar.CATALOG_LOCAL_DIRECTORY + " is not a directory, attempt delete");
			try {
				f.delete();
			} catch (Exception e) {
				LogFx.debug(TAG, "Catalog directory deletion exception: " + e.toString());
			}
		}
		if (!f.exists()) {
			if (f.mkdirs()) {
				LogFx.debug(TAG, "Directory creation succeeded " + GlVar.CATALOG_LOCAL_DIRECTORY);
			} else {
				LogFx.debug(TAG, "Directory creation failed " + GlVar.CATALOG_LOCAL_DIRECTORY);
			}
		}

		/* Check if app was updated, if so then the catalog should be using the internal version on first run
		 * delete all existing downloaded .XML files
		 * but manual download is still allowed (at least until it's been manually updated) */
		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);

			/* List internal files and delete all that end in .XML  */
			if ( DLPref.getSPLastRunAppVersion(this) != pInfo.versionCode ) {
				try{
					LogFx.warn(TAG, "This app does not match previous run version, upgrade detected");
					SPVar.setFontUpdated(this, false);  // because I saw it jump once, not fully explained
					if (GlVar.CATALOG_USE_INTERNAL_MEMORY) {
						final File dir = getFilesDir();
						for (final File file : dir.listFiles()) {
							LogFx.debug(TAG, "Listing internal files: " + file.toString());
							if (file.toString().endsWith(".xml")) {
								try {
									String[] filePath = file.toString().split("\\/");
									String tempFile = filePath[filePath.length-1];
									deleteFile(tempFile);
									LogFx.debug(TAG, "Deleting internal file " + file.toString());
								} catch (Exception e) { 
									LogFx.debug(TAG, "Failed: Deleting internal file " + file.toString() + e.toString());
								}

							}
						}
					} else {
						final File dir = new File(GlVar.CATALOG_LOCAL_DIRECTORY); 
						for(final File file : dir.listFiles()){ 
							LogFx.debug(TAG, "Listing external files: " + file.toString());
							if (file.toString().endsWith(".xml")) {
								file.delete();
								LogFx.debug(TAG, "Deleting external file " + file.toString());
							}
						}
					}
				} catch (Exception e) {
					LogFx.error(TAG, "Handling app update exception: " + e.toString());
				}


				/* Set catalog mod times */
				DLPref.setSPCurrentRunAppVersion(this, pInfo.versionCode);
				DLPref.setSPCatalogNeverDownloaded(this, true);
				DLPref.setSPCatalogDownloadingStatus(this, false);  // reset prior install shared preference, bug found in 1.0.1

				//Calendar now = Calendar.getInstance();				
				/*
				DLPref.setSPCatalogDownloadTimestamp(this, now.getTimeInMillis());
				LogFx.debug(TAG, "File does not exist, setting setSPCatalogDownloadTimestamp stamps to " + 
									Long.valueOf(now.getTimeInMillis()));
				 */
				SimpleDateFormat lastUpdateSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				Calendar lastUpdateCal = Calendar.getInstance();
				lastUpdateCal.setTime(lastUpdateSDF.parse(GlVar.CATALOG_LAST_UPDATE));
				//Date dt = lastUpdateSDF.parse(GlVar.CATALOG_LAST_UPDATE);  
				//DLPref.setSPCatalogLastMod(this, lastUpdateCal.getTimeInMillis());
				//LogFx.debug(TAG, "File does not exist, setting setSPCatalogLastMod stamps to " + 
					//	Long.valueOf(lastUpdateCal.getTimeInMillis()));

				
				DLPref.setSPCatalogDownloadTimestamp(this, lastUpdateCal.getTimeInMillis());
				LogFx.debug(TAG, "File does not exist, setting setSPCatalogDownloadTimestamp stamps to " + 
						Long.valueOf(lastUpdateCal.getTimeInMillis()) + " or " + lastUpdateCal.toString());
				
			}
		} catch (Exception e) {
			LogFx.debug(TAG, "Detecting app version failed: " + e.toString());
		}


		/* Make sure that the app isn't already download.  It could happen if user opens the app to auto download
		 * then quickly closes and reopens  */
		if (!DLPref.getSPCatalogDownloadingStatus(this)) {
			NotifyCatalogUpdate catalog = new NotifyCatalogUpdate();        
			catalog.CheckUpdated(this);
		}

		/* layout and pointer set for onclicklistener */
		RelativeLayout ll = (RelativeLayout) findViewById(R.id.splashLayout);
		llPointer = ll;

		
		splashTimer = new Thread() {
			public void run() {
				try {
					int splashTimer = 0;
					int breakOut = 0;
					boolean clickListenerSet = false;
					while (splashTimer < GlVar.SPLASH_SCREEN_DELAY) {
						sleep(100);						
						if (!DLPref.getSPCatalogDownloadingStatus(DisplaySplashScreen.this)) {							
							splashTimer = splashTimer + 100;							
						} else {  // if downloading then keep splash screen up
							breakOut += 100;
							if ((breakOut > GlVar.SPLASH_SCREEN_TIMEOUT) && !clickListenerSet) { // timeout in case something went wrong with the download
								LogFx.debug(TAG, "Thread timed out. Download SP bool equals " + (DLPref.getSPCatalogDownloadingStatus(DisplaySplashScreen.this) ? "TRUE" : "FALSE"));
								setOnClickListener();
								
								//break;
							}
						}
					}

				} catch (Exception e) {
					LogFx.error(TAG, "splashTimer exception: " + e.toString());
					setOnClickListener();
					// something went wrong with Splash delay, now we rely OnClickListener by user tap?
				} finally {
					if ((!appStarted) && (!backPressed)) {
						ActivityFx.openList(DisplaySplashScreen.this, GlVar.CATALOG_PARENT_NAME, GlVar.CATALOG_TOC_NAME, 
								false, false, false, true);
					}
				}
			}
		};

	}    

	public void setOnClickListener() {
		LogFx.debug(TAG, "Clicked to exit");
		
		LogFx.debug("Splash", "Layout pointer set");
		llPointer.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityFx.openList(DisplaySplashScreen.this, GlVar.CATALOG_PARENT_NAME, GlVar.CATALOG_TOC_NAME, 
						false, false, false, true);
				appStarted = true;
			}


		});
	}
	
	@Override
	public void onBackPressed() {		
		super.onBackPressed();
		backPressed = true;

	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		llPointer.setBackgroundColor(GlVar.SPLASH_BACKGROUND_COLOR);  // dirty fix for background color changing on app restart
		try {
			splashTimer.start();
		} catch (Exception e) {
			LogFx.error(TAG, "splashTimer exception: " + e.toString());
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}


}
