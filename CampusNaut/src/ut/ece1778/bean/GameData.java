package ut.ece1778.bean;

import java.util.ArrayList;

import android.widget.TextView;
/**
 * Temporary Datastore for all the Game data.
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public final class GameData {
	private static ArrayList<Game> gamesList = new ArrayList<Game>();
	private static Goal nearbyGoal = new Goal();
	private static TextView curGoalHeader = null; // Closest goal 
	private static boolean updateGoal = false; // If check-in, we should update nearby goal
	private static int scores = 0;
	private static int detector = 0;

	public static int getDetector() {
		return detector;
	}

	public static void setDetector(int detector) {
		GameData.detector = detector;
	}

	public static ArrayList<Game> getGameList() {
		return gamesList;
	}
	
	public static int getScores() {
		return scores;
	}

	public static void setScores(int scores) {
		GameData.scores = scores;
	}

	public static void add(Game game) {
		gamesList.add(game);
	}
	
	public static void remove(Game game) {
		gamesList.remove(game);
	}
	// Clear the memory
	public static void clear() {
		gamesList.clear();
		nearbyGoal = new Goal();
		curGoalHeader = null;
		updateGoal = false;
	}
	
	public static void setNearbyGoal(Goal goal) {
		nearbyGoal = goal;
	}
	public static Goal getNearbyGoal() {
		return nearbyGoal;
	}
	public static void setUpdateGoal(boolean toUpdate) {
		updateGoal = toUpdate;
	}
	public static boolean getUpdateGoal () {
		return updateGoal;
	}
	
	public static void setCurGoalHeader (TextView header) {
		curGoalHeader = header;
	}
	
	public static TextView getCurGoalHeader() {
		return curGoalHeader;
	}
	
	
	
}
