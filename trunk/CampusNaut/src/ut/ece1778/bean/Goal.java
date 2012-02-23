package ut.ece1778.bean;

/**
 * Entity Class Goal
 *@author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class Goal {

	private String goalTitle;	//Goal Title
	private int goalLat;	//Goal's Latitude (eight digit Integer)
	private int goalLon;	//Goal's Longitude (eight digit Integer)
	private String picDir;	//picture directory of goal
	
	/**
	 * Goal constructor
	 * @param goalTitle
	 * @param goalLat
	 * @param goalLon
	 */
	public Goal(String goalTitle, int goalLat, int goalLon) {
		super();
		this.goalTitle = goalTitle;
		this.goalLat = goalLat;
		this.goalLon = goalLon;
	}
	
	/**
	 * Goal constructor without params
	 */
	public Goal(){
		
	}
	
	//Getters & Setters
	public String getGoalTitle() {
		return goalTitle;
	}
	public void setGoalTitle(String goalTitle) {
		this.goalTitle = goalTitle;
	}
	public int getGoalLat() {
		return goalLat;
	}
	public void setGoalLat(int goalLat) {
		this.goalLat = goalLat;
	}
	public int getGoalLon() {
		return goalLon;
	}
	public void setGoalLon(int goalLon) {
		this.goalLon = goalLon;
	}
	public String getPicDir() {
		return picDir;
	}
	public void setPicDir(String picDir) {
		this.picDir = picDir;
	}

	
}
