package com.warewoof.shanghai.file;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.warewoof.shanghai.GlVar;
import com.warewoof.shanghai.function.LogFx;




public class DownloadCatalog {
	private Context mContext;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private ProgressDialog mProgressDialog;
	final private String localDirectory = GlVar.CATALOG_LOCAL_DIRECTORY;
	//final private String localFileName = XmlProp.CATALOG_LOCAL_FILE_NAME;
	final private String remoteDirectory = GlVar.CATALOG_DOWNLOAD_URL;
	final private String remoteFileName = GlVar.CATALOG_DOWNLOAD_FILE_NAME;
	final private String tempFileName = "temp";
	final private static String TAG = "DownloadCatalog";

	public DownloadCatalog(Context c) {
		mContext = c;
	}

	public void startDownload() {
		try {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setOwnerActivity((Activity)mContext);
			mProgressDialog.setMessage("Downloading catalog...");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);       
	
			new DownloadFileAsync().execute(remoteDirectory+remoteFileName);
		} catch (Exception e) {
			LogFx.warn(TAG, "Exception: " + e.toString());
		}
	}


	class DownloadFileAsync extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			try {
				mProgressDialog.show(); 
			} catch (Exception e) {
				LogFx.debug("DownloadFileAsync", "onPreExecute exception: " + e.toString());
			}
		}

		@Override
		protected String doInBackground(String... aurl) {
			int count;

			try {
				DLPref.setSPCatalogDownloadingStatus(mContext, true);
				LogFx.debug("DownloadFileAsync", "Trying to connect ");
				URL url = new URL(aurl[0]);
				URLConnection conexion = url.openConnection();
				conexion.connect();


				int lenghtOfFile = conexion.getContentLength();
				LogFx.debug("DownloadFileAsync", "Lenght of file: " + lenghtOfFile);
				if (lenghtOfFile < 1) {
					return "NO FILE";
				}
				long lastMod = conexion.getLastModified();
				Date date = new Date(lastMod);
				LogFx.debug("DownloadFileAsync", "Server last modified date: " + date.toLocaleString());
				
				/*
				if (((DLPref.getSPCatalogDownloadTimestamp(mContext) != 0 ) &&
					 (DLPref.getSPCatalogDownloadTimestamp(mContext) >= lastMod))) {

					return "ALREADY UPDATED";
				}
				*/
				if (((DLPref.getSPCatalogDownloadTimestamp(mContext) != 0 ) &&
						(DLPref.getSPCatalogDownloadTimestamp(mContext) >= lastMod)) &&
						(!DLPref.getSPCatalogNeverDownloaded(mContext))) {

					return "ALREADY_UPDATED";
				}
				



				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream(localDirectory + tempFileName);

				byte data[] = new byte[1024];

				long total = 0;

				while ((count = input.read(data)) != -1) {
					total += count;
					try {
						publishProgress(""+(int)((total*100)/lenghtOfFile));
					} catch (Exception e) {
						LogFx.debug("DownloadFileAsync", "publishProgress exception: " + e.toString());
					}
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();


				CheckDirectory("");
				Unzip();

				File tempFile = new File(localDirectory + tempFileName);                
				long localLastMod = tempFile.lastModified();
				date = new Date(localLastMod);
				LogFx.debug("DownloadFileAsync", "Local Downloaded File Last modified : " + date.toLocaleString());

				//DLPref.setSPCatalogLastMod(mContext, localLastMod);
				DLPref.setSPCatalogDownloadTimestamp(mContext, localLastMod);

				tempFile.delete();


				return "Complete";

			} catch (Exception e) {
				DLPref.setSPCatalogDownloadingStatus(mContext, false);
				LogFx.warn("DownloadFileAsync", "Exception: " + e.toString());
				return e.toString();
			}


		}
		protected void onProgressUpdate(String... progress) {
			mProgressDialog.setProgress(Integer.parseInt(progress[0]));
		}

		@Override
		protected void onPostExecute(String result) {

			try {
				/* Successfully downloaded, handle this case and set SPs */
				if (result.equals("Complete")) {
					LogFx.debug("onPostExecute", remoteFileName + " downloaded");
					Toast.makeText(mContext, "Catalog Updated", Toast.LENGTH_SHORT).show();
					DLPref.setSPCatalogDownloadingStatus(mContext, false);
					DLPref.setSPCatalogUpdated(mContext, true);			
					if (DLPref.getSPCatalogNeverDownloaded(mContext)) {  // done download update, so no longer allow the manual update
						DLPref.setSPCatalogNeverDownloaded(mContext, false);
					}
					
				} else {
					LogFx.debug("onPostExecute", result + " downloaded");
					if (result.equals("ALREADY_UPDATED")) {
						Toast.makeText(mContext, "Your catalog is already up to date", Toast.LENGTH_SHORT).show();
					} else {
						if (mContext != null) {
							Toast.makeText(mContext, "No updated catalogs found", Toast.LENGTH_SHORT).show();
						}
					}
					DLPref.setSPCatalogDownloadingStatus(mContext, false);
				}

				NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.cancel(0);


				mProgressDialog.dismiss();

			} catch (Exception e) {
				DLPref.setSPCatalogDownloadingStatus(mContext, false);
				LogFx.debug("onPostExecute", remoteFileName + " download exception");
				LogFx.debug("onPostExecute", "onPostExecute exception: "+ e.toString());
			}
			mProgressDialog = null;

		}
	}



	public void Unzip() { 
		try  { 
			LogFx.debug("Decompress", "Begin decompress..."); 
			FileInputStream fin = new FileInputStream(localDirectory + tempFileName); 
			ZipInputStream zin = new ZipInputStream(new BufferedInputStream(fin));

			ZipEntry ze = null; 

			long startTime = android.os.SystemClock.uptimeMillis();
			while ((ze = zin.getNextEntry()) != null) {
				if(ze.isDirectory()) { 
					CheckDirectory(ze.getName()); 
				} else { 

					if (ze.getName().toLowerCase().endsWith("xml")) {
						if (GlVar.CATALOG_USE_INTERNAL_MEMORY) {
							String[] path = ze.getName().split("\\/");
							String fileName = path[path.length-1];

							// catalog will be saved to internal (hidden) memory
							LogFx.debug(TAG, fileName + " being written to internal memory");
							FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
							int size;
							byte[] buffer = new byte[2048];

							BufferedOutputStream bufferOut = new BufferedOutputStream(fos, buffer.length);

							while((size = zin.read(buffer, 0, buffer.length)) != -1) {
								bufferOut.write(buffer, 0, size);
							}

							bufferOut.flush();
							bufferOut.close();			

						} else {
							String[] path = ze.getName().split("\\/");
							String fileName = path[path.length-1];
							LogFx.debug(TAG, fileName + " being written to external memory");

							int size;
							byte[] buffer = new byte[2048];

							FileOutputStream outStream = new FileOutputStream(localDirectory + fileName);
							BufferedOutputStream bufferOut = new BufferedOutputStream(outStream, buffer.length);

							while((size = zin.read(buffer, 0, buffer.length)) != -1) {
								bufferOut.write(buffer, 0, size);
							}

							bufferOut.flush();
							bufferOut.close();
						}

					} else {
						int size;
						byte[] buffer = new byte[2048];

						FileOutputStream outStream = new FileOutputStream(localDirectory + ze.getName());
						BufferedOutputStream bufferOut = new BufferedOutputStream(outStream, buffer.length);

						while((size = zin.read(buffer, 0, buffer.length)) != -1) {
							bufferOut.write(buffer, 0, size);
						}

						bufferOut.flush();
						bufferOut.close();
					}
				}

			} 

			zin.close(); 
			long endTime = android.os.SystemClock.uptimeMillis();
			LogFx.debug("Decompress", "Excution time: "+(endTime-startTime)+" ms");
		} catch(Exception e) { 
			DLPref.setSPCatalogDownloadingStatus(mContext, false);
			LogFx.debug("Decompress", "unzip exception:" + e); 
		} 
	} 

	private void CheckDirectory(String dir) { 
		File f = new File(localDirectory + dir); 

		if(!f.isDirectory()) { 
			LogFx.debug("Decompress", "Creating directory " + dir); 
			f.mkdirs(); 
		} 
	} 

}