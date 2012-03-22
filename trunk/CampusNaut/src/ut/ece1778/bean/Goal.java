package ut.ece1778.bean;

import android.location.Location;

import com.google.android.maps.GeoPoint;

/**
 * Entity Class Goal
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class Goal {

	private int gID; // Goal ID

	private String title; // Goal Title
	private String category; // Goal Category
	private double latitude; // Goal's Latitude (eight digit Integer)
	private double longitude; // Goal's Longitude (eight digit Integer)
	private int state = 0; //undiscovered(0),uncheck-in(1),checked-in(2)
	private boolean selected = false; // selected goal in goalpicker

	private String picDir; // picture directory of goal
	private GeoPoint geoPoint; // Geopoint
	private double distance = 0; // Distance to the user
	private Location location; // Location for calculating distance

	public Goal(int gID, String title, double latitude, double longitude) {
		this.gID = gID;
		this.title = title;
		this.latitude = latitude;
		this.longitude = longitude;
		this.geoPoint = new GeoPoint((int) (latitude * 1E6),
				(int) (longitude * 1E6));
		this.location = new Location(title);
		this.location.setLatitude(latitude);
		this.location.setLongitude(longitude);
	}

	/**
	 * Goal constructor without params
	 */
	public Goal() {

	}

	public int getgID() {
		return gID;
	}

	public void setgID(int gID) {
		this.gID = gID;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void toFinish() {
		this.state = 2;
	}

	public GeoPoint getGeoPoint() {
		return this.geoPoint;
	}

	// Getters & Setters
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public boolean getSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public double getDistance() {
		return distance;
	}

	public void calculateDistance(Location loc) {
		distance = loc.distanceTo(location);

	}

}
