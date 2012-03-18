package ut.ece1778.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import android.widget.TextView;

/**
 * Temporary Datastore for all the Game data.
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public final class GameData {
	private static User curUser = new User(); // Current login user

	private static ArrayList<Game> gamesList = new ArrayList<Game>();
	private static List<Goal> tempList = new ArrayList<Goal>(); // used for
																// keeping
																// uncheck-in
																// goals
	private static List<Goal> discoveredList = new ArrayList<Goal>(); // used
																		// for
																		// populating
																		// the
																		// CurrentGameOverlay
	private static TreeMap<String, ArrayList<Goal>> allGoals = new TreeMap<String, ArrayList<Goal>>();
	private static ArrayList<Goal> selectedGoals = new ArrayList<Goal>();
	private static Goal nearbyGoal = new Goal();
	private static TextView curGoalHeader = null; // Closest goal
	private static boolean updateGoal = false; // If check-in, we should update
												// nearby goal
	private static boolean firstTime = true; // Check if first time running the
												// game
	private static int scores = 0;
	private static int detector = 0;
	private static int allGoalCount = 0;

	public static TreeMap<String, ArrayList<Goal>> getAllGoals() {
		return allGoals;
	}

	public static List<Goal> getTempList() {
		return tempList;
	}

	public static ArrayList<Goal> getSelectedGoal() {
		return selectedGoals;
	}

	public static int getAllGoalCount() {
		return allGoalCount;
	}

	public static void setAllGoalCount(int count) {
		allGoalCount = count;
	}

	public static void setTempList(List<Goal> tempList) {
		GameData.tempList = tempList;
	}

	public static List<Goal> getDiscoveredList() {
		return discoveredList;
	}

	public static void setDiscoveredList(List<Goal> discoveredList) {
		GameData.discoveredList = discoveredList;
	}

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

	public static boolean getUpdateGoal() {
		return updateGoal;
	}

	public static boolean getFirstTime() {
		return firstTime;
	}

	public static void setFirstTime(boolean tag) {
		firstTime = tag;
	}

	public static void setCurGoalHeader(TextView header) {
		curGoalHeader = header;
	}

	public static TextView getCurGoalHeader() {
		return curGoalHeader;
	}

	public static User getCurUser() {
		return curUser;
	}

	public static void setCurUser(User curUser) {
		GameData.curUser = curUser;
	}

	/**
	 * Get a goal object from goal list by ID
	 * 
	 * @param goal_id
	 * @param goal_list
	 * @return Goal Object
	 */
	public static Goal findGoalById(int goal_id, List<Goal> goal_list) {

		for (Goal goal : goal_list) {
			if (goal.getgID() == goal_id) {
				return goal;
			}
		}
		return null;
	}

	/**
	 * Update goal's check in state in main gameList and discoveredList Remove
	 * goal in tempList by goal ID
	 * 
	 * @param goal_id
	 */
	public static void updateState(int goal_id) {

		try {
			gamesList.get(0).getGoals()
					.get(getIndex(goal_id, gamesList.get(0).getGoals()))
					.setState(true);
			discoveredList.get(getIndex(goal_id, discoveredList))
					.setState(true);
			tempList.remove(getIndex(goal_id, tempList));
			GameData.setScores(GameData.getScores() + 50);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get index of goal in a goal list
	 * 
	 * @param goal_id
	 * @param goal_list
	 * @return index of goal
	 */
	public static int getIndex(int goal_id, List<Goal> goal_list) {
		int index = -1;
		for (Goal goal : goal_list) {
			if (goal.getgID() == goal_id) {
				index = goal_list.indexOf(goal);
			}
		}
		return index;
	}
}
