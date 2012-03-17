package ut.ece1778.bean;
/**
 * Entity Class User
 *@author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class User {

	private int uID;		//User's ID
	private String userName;	// User name
	private int level;		//User's current level
	private int score;		//User's current total score
	
	/**
	 * User constructor without params
	 */
	public User(){
		
	}
	
	/**
	 * User constructor
	 * @param uID
	 * @param userName
	 * @param level
	 * @param score
	 */
	public User(int uID, String userName, int level, int score){
		this.uID = uID;
		this.userName = userName;
		this.level = level;
		this.score = score;
	}
	
	public int getuID() {
		return uID;
	}

	public void setuID(int uID) {
		this.uID = uID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	
}
