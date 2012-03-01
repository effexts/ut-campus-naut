package ut.ece1778.bean;

import android.location.Location;

import com.google.android.maps.GeoPoint;


/**
 * Entity Class Goal
 *@author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class Goal {

	private String title;	//Goal Title
	private double latitude;	//Goal's Latitude (eight digit Integer)
	private double longitude;	//Goal's Longitude (eight digit Integer)
	private int state = 1; //finished goal or not(0,1)

	private String picDir;	//picture directory of goal
	private GeoPoint geoPoint; //Geopoint
	private double distance = 0; // Distance to the user
	private Location location; // Location for calculating distance

	public Goal(String title, double latitude, double longitude) {
		this.title = title;
		this.latitude = latitude;
		this.longitude = longitude;
		this.geoPoint = new GeoPoint((int)(latitude*1E6), (int)(longitude*1E6));
		this.location = new Location(title);
		this.location.setLatitude(latitude);
		this.location.setLongitude(longitude);
		
	}
	
	/**
	 * Goal constructor without params
	 */
	public Goal(){
		
	}
	
	public int getStates() {
		return state;
	}
	public void toFinish(){
		this.state = 0;
	}
	
	public GeoPoint getGeoPoint() {
		return this.geoPoint;
	}
	
	//Getters & Setters
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getPicDir() {
		return picDir;
	}
	public void setPicDir(String picDir) {
		this.picDir = picDir;
	}

	public double getDistance() {
		return distance;
	}
	public void calculateDistance(Location loc) {
		distance = loc.distanceTo(location);
		
	}
	
	
}
