package ut.ece1778.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.widget.TextView;

/**
 * Temporary Datastore for all the Game data.
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public final class GameData {
	private static final String GP_FILE_NAME = "geopoint.log"; // GPSCoordinate Serialized file
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
	private static TreeMap<String, ArrayList<Goal>> allGoals = new TreeMap<String, ArrayList<Goal>>(); // used
																										// for
																										// mapping
																										// the
																										// category
																										// and
																										// goals
																										// inside
																										// the
																										// goal
																										// picker
	private static ArrayList<Goal> selectedGoals = new ArrayList<Goal>(); // SelectedGoals
																			// from
																			// goal
																			// picker
	private static Goal nearbyGoal = new Goal();
	private static List<GeoPoint> gpList = new ArrayList<GeoPoint>();// List of
												// GP
												// that
												// the
												// user
												// has
												// worked
												// thru
												// for
												// drawing
												// circle

	private static TextView curGoalHeader = null; // Closest goal
	private static TextView curGoalDistance = null;

	private static boolean updateGoal = false; // If check-in, we should update
												// nearby goal
	private static boolean firstTime = true; // Check if first time running the
												// game
	private static int scores = 0;
	private static int detector = 0;
	private static int allGoalCount = 0;

	// Clear the memory
	public static void clear() {
		gpList.clear();
		gamesList.clear();
		tempList.clear();
		discoveredList.clear();
		selectedGoals.clear();
		allGoals.clear();
		allGoalCount = 0;
		curUser = null;
		nearbyGoal = new Goal();
		curGoalHeader = null;
		updateGoal = false;
		scores = 0;
		detector = 0;

	}

	/**
	 * Serialize GeoPoint coordinate for next load
	 * 
	 * @param context
	 */
	public static void saveGpList(Context context) {
		List<GPSCoordinate> gcList = new ArrayList<GPSCoordinate>();
		// Generate a list of GPSCoordinate to be saved from geopoint list
		for (int i = 0; i < gpList.size(); i++) {
			gcList.add(new GPSCoordinate(gpList.get(i).getLatitudeE6(), gpList
					.get(i).getLongitudeE6()));
		}
		if (gcList.size() > 0) {
			ObjectOutputStream oos = null;
			try {
				OutputStream os = context.openFileOutput(GP_FILE_NAME, 0);
				// Serialize the coordinate list into file
				oos = new ObjectOutputStream(os);
				oos.writeObject(gcList);
				oos.flush();
				oos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static List<GeoPoint> getGpList() {
		return gpList;
	}

	/**
	 * Deserialize GeoPoint coordinate to load them to the map
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public static void loadGpList(Context context) {
		List<GPSCoordinate> gcList = null;
		try {
			InputStream is;
			is = context.openFileInput(GP_FILE_NAME);
			// Deserialize the coordinate list object from file
			ObjectInputStream ois = new ObjectInputStream(is);
			gcList = (List<GPSCoordinate>) ois.readObject();
			ois.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Check if the coordinate is empty
		if (gcList.size()>0) {
			// Add the last saved coordinate to the new GeoPoint list
			for (int i = 0; i < gcList.size(); i++) {
				gpList.add(new GeoPoint(gcList.get(i).getLatitude(), gcList
						.get(i).getLongitude()));
			}
			gcList.clear();
		}


	}

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

	public static TextView getCurGoalDistance() {
		return curGoalDistance;
	}

	public static void setCurGoalDistance(TextView curGoalDistance) {
		GameData.curGoalDistance = curGoalDistance;
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
					.setState(2);
			discoveredList.get(getIndex(goal_id, discoveredList))
					.setState(2);
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