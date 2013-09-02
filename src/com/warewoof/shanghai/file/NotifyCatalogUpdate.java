package com.warewoof.shanghai.file;

import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.warewoof.shanghai.GlVar;
import com.warewoof.shanghai.R;
import com.warewoof.shanghai.activity.listview.ListPreferences;
import com.warewoof.shanghai.function.LogFx;

public class NotifyCatalogUpdate {
	private String TAG = "NotifyCatalogUpdate";
	Context mContext;
	private int lengthOfFile;

	public void CheckUpdated(Context c) {
		try {
			mContext = c;
			new CompareFileHeaderAsync().execute(GlVar.CATALOG_DOWNLOAD_URL + GlVar.CATALOG_DOWNLOAD_FILE_NAME);
		}
		catch (Exception e) {
			LogFx.debug("Catalog", "Exception: " + e.toString());
		}
	}	

	class CompareFileHeaderAsync extends AsyncTask<String, String, Date> {
		@Override
		protected Date doInBackground(String... aurl) {
			Date d = new Date();
			try {
				LogFx.debug(TAG, "Trying to connect...");
				URL url = new URL(aurl[0]);
				URLConnection conexion = url.openConnection();
				conexion.connect();
				long lastMod = conexion.getLastModified();
				LogFx.debug(TAG, "Current time " + d.toLocaleString());
				Date tempDate = new Date(lastMod);
				LogFx.debug(TAG, "Server file last mod time " + tempDate.toLocaleString());
				lengthOfFile = conexion.getContentLength();
				LogFx.debug(TAG, "Server file length: " + lengthOfFile);

				if (lengthOfFile < 0) { // file is missing, not found
					d.setTime(0); 
					LogFx.debug(TAG, "File not found, setting last mod time to " + d.toLocaleString());
				} else {
					d.setTime(lastMod);
				}




			} catch (Exception e) {
				LogFx.debug(TAG, "Exception: " + e.toString());
				d.setTime(0); 
			}

			return d;
		}

		@Override
		protected void onPostExecute(Date serverDate) {

			try {
				/* Get last downloaded date */
				long lastModLong = DLPref.getSPCatalogDownloadTimestamp(mContext);
				Date localDate = new Date(lastModLong);
				//LogFx.debug(TAG, "Shared setting lastMod " + lastModString + " " + String.valueOf(lastModLong));
				LogFx.debug(TAG, "onPostExecute lastMod result" + serverDate.toLocaleString() + " " + String.valueOf(serverDate.getTime()));
				LogFx.debug(TAG, "onPostExecute localDate is " + localDate.toLocaleString() + " " + String.valueOf(localDate.getTime()));
	
	
	
				/* Compare last downloaded with current server version */
				if (localDate.before(serverDate)) {
					//DLPref.setSPCatalogNeverDownloaded(mContext, false);  // since server catalog is newer no longer allow image only download
	
					boolean downloadOK = false;
					boolean notifyDownload = false;
	
					switch (DLPref.Download(mContext)) {
					case DLPref.DL_OK_WIFI:
						downloadOK = true;
					case DLPref.DL_OK_INTERNET:
						downloadOK = true;
					case DLPref.DL_NOTIFY_WIFI_CONNECT:
						notifyDownload = true;
					case DLPref.DL_NOTIFY_INTERNET_CONNECT:
						notifyDownload = true;					
					}
	
					if (downloadOK) {
						LogFx.debug(TAG, "Start catalog download...");
						DownloadCatalog dc = new DownloadCatalog(mContext);
						dc.startDownload();
					}
					if (notifyDownload) {
						//prompt download new catalog
						LogFx.debug(TAG, "Update new catalog");
						NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
						Notification notification = new Notification(R.drawable.ab_silhouette, "SCS updates avalable", System.currentTimeMillis());
						notification.flags |= Notification.FLAG_AUTO_CANCEL;
						notification.number += 1;
						Intent intent = new Intent(mContext, ListPreferences.class);
	
						double dLengthOfFile = (double) lengthOfFile / 1000;
						String fileSize;
						if (dLengthOfFile < 1028) {		// less then 1 MB
							fileSize = String.format("%.1f", dLengthOfFile);
							fileSize =  "(" + fileSize + "KB)";
						} else {
							dLengthOfFile = dLengthOfFile / 1000;
							fileSize = String.format("%.1f", dLengthOfFile);
							fileSize =  "(" + fileSize + "MB)";
						}
						
						PendingIntent activity = PendingIntent.getActivity(mContext, 0, intent, 0);
						notification.setLatestEventInfo(mContext, "SCS update "+ fileSize,
								"Choose 'Update Now' to download", activity);
						notificationManager.notify(0, notification);
					}
	
	
	
	
				} else {
					//do nothing
					LogFx.debug(TAG, "Do not update new catalog");
				}
			} catch (Exception e) {
				LogFx.warn(TAG, "Exception: " + e.toString());
			}


		}
		
	}




}