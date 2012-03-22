package ut.ece1778.bean;

import java.io.Serializable;
/**
 * Custom serializable object to store gps coordinate since
 * GeoPoint cannot be serialized.
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class GPSCoordinate implements Serializable {
	
	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -6552113552020726169L;
	private int latitude;
	private int longitude;
	
	public GPSCoordinate(int latitude, int longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public int getLatitude(){
		return this.latitude;
	}
	public int getLongitude(){
		return this.longitude;
	}
}
