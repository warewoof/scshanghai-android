package com.warewoof.shanghai.function;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;
import com.warewoof.shanghai.GlVar;
import com.warewoof.shanghai.object.MapItem;

public class GeoFx {


	public static float DistanceBetweenGeo(GeoPoint a, GeoPoint b) {
		float[] distance = new float[1];
		Location.distanceBetween(a.getLatitudeE6()/1E6, a.getLongitudeE6()/1E6, b.getLatitudeE6()/1E6, b.getLongitudeE6()/1E6, distance);
		return distance[0]; // distance returned in meters
	}

	public static GeoPoint CheckNorthEast(GeoPoint current, GeoPoint compare) {
		int northPoint;
		int eastPoint;		
		if (current.getLatitudeE6() <= compare.getLatitudeE6()) {
			northPoint = compare.getLatitudeE6();
		} else {
			northPoint = current.getLatitudeE6();
		}		
		if (current.getLongitudeE6() <= compare.getLongitudeE6()) {
			eastPoint = compare.getLongitudeE6();
		} else {
			eastPoint = current.getLongitudeE6();
		}
		return new GeoPoint(northPoint, eastPoint);
	}

	public static GeoPoint CheckSouthWest(GeoPoint current, GeoPoint compare) {

		int southPoint;
		int westPoint;
		if (current.getLongitudeE6() >= compare.getLongitudeE6()) {
			westPoint = compare.getLongitudeE6();
		} else {
			westPoint = current.getLongitudeE6();
		}

		if (current.getLatitudeE6() >= compare.getLatitudeE6()) {
			southPoint = compare.getLatitudeE6();
		} else {
			southPoint = current.getLatitudeE6();
		}

		return new GeoPoint(southPoint, westPoint);

	}

	public static GeoPoint CalculateCenter(GeoPoint a, GeoPoint b) {

		try {
			int lat = (a.getLatitudeE6() + b.getLatitudeE6())/2;
			int lon = (a.getLongitudeE6()+ b.getLongitudeE6())/2;

			return new GeoPoint(lat,lon);
		} catch (Exception e) {
			return null;
		}

	}

	public static GeoPoint StringToGeo(String coordString) {
		StringTokenizer tokens = new StringTokenizer(coordString, ":,?");
		int longitude;
		int latitude;
		// this statement should be asserted to catch sanity case errors 
		LogFx.debug("MapView", "First token = " + tokens.nextToken()); // should be "geo"
		longitude = (int) (Float.valueOf(tokens.nextToken()) * 1E6);
		LogFx.debug("MapView", "Second (converted) token = " + Integer.toString(longitude));
		latitude = (int) (Float.valueOf(tokens.nextToken()) * 1E6);
		LogFx.debug("MapView", "Third (converted) token = " + Integer.toString(latitude));
		GeoPoint point = new GeoPoint(longitude,latitude);
		return point;    	
	}

	public static GeoPoint LocationToGeoPoint(Location location) {
		int lat = (int) (location.getLatitude()*1E6);
		int lon = (int) (location.getLongitude()*1E6);
		GeoPoint gp = new GeoPoint(lat, lon);

		return gp;

	}

	public static Location GeoToLocation(GeoPoint gp) {
		Location location = new Location("dummyProvider");
		location.setLatitude(gp.getLatitudeE6()/1E6);
		location.setLongitude(gp.getLongitudeE6()/1E6);
		return location;
	}

	public static String GeoToLink(GeoPoint gp) {
		return GlVar.MAP_SOURCE_PREFIX + gp.getLatitudeE6()/1E6 + "," + gp.getLongitudeE6()/1E6 + "&z=" + GlVar.MAP_INITIAL_ZOOM_LEVEL;
	}
	
	public static LatLng CalculateCenterLatLng(ArrayList<MapItem> mapItems) {

		try {
			double minLat = mapItems.get(0).GetLat();
			double maxLat = mapItems.get(0).GetLat();
			double minLng = mapItems.get(0).GetLng();
			double maxLng = mapItems.get(0).GetLng();
			
			for (int i=1; i<mapItems.size(); i++) {
				if (mapItems.get(i).GetLat() > maxLat) {
					maxLat = mapItems.get(i).GetLat();					
				} else if (mapItems.get(i).GetLat() < minLat) {
					minLat = mapItems.get(i).GetLat();					
				}	
				
				if (mapItems.get(i).GetLng() > maxLng) {
					maxLng = mapItems.get(i).GetLng();					
				} else if (mapItems.get(i).GetLng() < minLng) {
					minLng = mapItems.get(i).GetLng();					
				}
			}
			
			return new LatLng((maxLat+minLat)/2, (maxLng+minLng)/2);
		} catch (Exception e) {
			return null;
		}

	}
	
	public static String LatLngToLink(LatLng gp) {
		return GlVar.MAP_SOURCE_PREFIX + Double.toString(gp.latitude) + "," + Double.toString(gp.longitude) + "&z=" + GlVar.MAP_INITIAL_ZOOM_LEVEL;
	}
}