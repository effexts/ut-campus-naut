package ut.ece1778.campusnaut;

import ut.ece1778.bean.DBHelper;
import ut.ece1778.bean.GameData;
import ut.ece1778.bean.Goal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.Toast;

/**
 * This activity loads goal from either server or local db and let user pick
 * goals for the game.
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class GoalPicker extends ExpandableListActivity {
	private static final String SYNC_DB_URL = "http://ec2-184-73-31-146.compute-1.amazonaws.com:8080/CampusNaut/servlet/RetrieveGoals";
	private static final String SERVER_MSG_BEGIN = "<BEGIN>";
	private static final String SERVER_MSG_FAIL = "<FAILED>";
	private static final String SERVER_MSG_DONE = "<DONE>";
	private static final String SERVER_MSG_NOTHING = "<NOTHING>";
	private DBHelper dbHelper = null;
	private Cursor constantsCursor = null;
	private GoalAdapter expListAdapter;
	ArrayList<ArrayList<Goal>> goals = new ArrayList<ArrayList<Goal>>();
	ArrayList<String> categories = new ArrayList<String>();

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goalpicker);
		// Create a DB helper first
		dbHelper = new DBHelper(this);
		constantsCursor = dbHelper.getReadableDatabase().rawQuery(
				"SELECT * FROM t_goals", null);

		Button goButton = (Button) findViewById(R.id.buttonStart);
		goButton.setOnClickListener(onGoClick);

		// Button updateButton = (Button) findViewById(R.id.button2);
		// updateButton.setOnClickListener(onUpdateClick);
		// if first time running the game, auto load from server
		if (GameData.getFirstTime()) {
			new DBAsyncTask().execute();
		} else {
			// else load from local db
			new ReadFromDBTask().execute();
		}

	}

	/**
	 * When user click on Begin the journey, we upload the
	 * selectedGoals in memory based on goals they checked
	 * in the multi-level goal picker.
	 */
	private OnClickListener onGoClick = new OnClickListener() {
		public void onClick(View v) {
			// Clear the memory first
			GameData.getSelectedGoal().clear();
			@SuppressWarnings("rawtypes")
			Iterator iterator = GameData.getAllGoals().entrySet().iterator();
			while (iterator.hasNext()) {
				@SuppressWarnings("rawtypes")
				TreeMap.Entry entry = (TreeMap.Entry) iterator.next();
				@SuppressWarnings("unchecked")
				ArrayList<Goal> gList = (ArrayList<Goal>) entry.getValue();
				for (int i = 0; i < gList.size(); i++) {
					if (gList.get(i).getSelected()
							&& !GameData.getSelectedGoal().contains(
									gList.get(i)))
						GameData.getSelectedGoal().add(gList.get(i));
				}
			}
			if (GameData.getSelectedGoal().size()<=0) {
				Toast.makeText(GoalPicker.this, "At least one objectives must be selected.",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(GoalPicker.this, "You have " + GameData.getSelectedGoal().size() + " objectives to complete.",
						Toast.LENGTH_LONG).show();
				startActivity(new Intent(getApplicationContext(), GameMapView.class));
				finish();
			}
		}
	};
	/**
	 * This method will be used in the future
	private OnClickListener onUpdateClick = new OnClickListener() {
		public void onClick(View v) {
			new DBAsyncTask().execute();
		}
	};*/

	/**
	 * AsyncTask to sent create account request to server.
	 * 
	 */
	private class DBAsyncTask extends AsyncTask<String, Void, String> {
		ProgressDialog mProgressDialog;

		@Override
		protected void onPostExecute(String result) {
			mProgressDialog.dismiss();
			if (result == null || result.equals(SERVER_MSG_FAIL)) {
				Toast.makeText(
						GoalPicker.this,
						"Cannot access the server. \nPlease make sure your WI-FI is on.",
						Toast.LENGTH_LONG).show();
			} else if (result.equals(SERVER_MSG_NOTHING)) {
				if (GameData.getFirstTime())
					GameData.setFirstTime(false);
				Toast.makeText(GoalPicker.this, "No update available.",
						Toast.LENGTH_LONG).show();
				new ReadFromDBTask().execute();
			} else {
				// No need to autou pdate anymore if user already has data in
				// local db
				if (GameData.getFirstTime())
					GameData.setFirstTime(false);
				// Update is available so let's resync the local database
				new ReadFromDBTask().execute();
			}
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(GoalPicker.this,
					"Loading...",
					"Please wait while updating goals from server...");
		}

		@Override
		protected String doInBackground(String... u) {
			if (isInternetOn()) {
				URL url = null;
				HttpURLConnection httpConn = null;
				String returnStr = "null";
				try {
					url = new URL(SYNC_DB_URL);
					httpConn = (HttpURLConnection) url.openConnection();
					httpConn.setDoOutput(true);
					httpConn.setRequestMethod("POST");
					// Do post request.
					DataOutputStream out = new DataOutputStream(
							httpConn.getOutputStream());
					// Send local Database count to server database count to see
					// if new data is available on server.
					out.writeUTF(constantsCursor.getCount() + "");
					out.flush();
					out.close();

					// Receiving response from server
					DataInputStream in = new DataInputStream(
							httpConn.getInputStream());
					returnStr = in.readUTF();
					if (returnStr != null && returnStr.equals(SERVER_MSG_BEGIN)) {
						do {
							returnStr = in.readUTF();
							// Keep reading record from server until we receive
							// a message <DONE>
							if (returnStr != null
									&& !returnStr.equals(SERVER_MSG_DONE)) {
								String[] parseData = returnStr.split("%");
								if (parseData.length == 6) {
									ContentValues values = new ContentValues();
									values.put("goal_id",
											Integer.parseInt(parseData[0]));
									values.put("title", parseData[1]);
									values.put("description", parseData[2]);
									values.put("latitude", parseData[3]);
									values.put("longitude", parseData[4]);
									values.put("category", parseData[5]);
									// Field to store whether user has already
									// check in and
									// if the image is already on disk.
									values.put("state", 0);
									values.put("ondisk", "false");
									dbHelper.getWritableDatabase().insert(
											"t_goals", null, values);
								}
							}
						} while (!returnStr.equals(SERVER_MSG_DONE));

					}

				} catch (Exception e) {

					e.printStackTrace();
				}
				return returnStr;
			} else {
				return null;
			}
		}

	}

	/**
	 * AsyncTask to load goal data from local SQLITE database.
	 * 
	 */
	@SuppressWarnings("rawtypes")
	private class ReadFromDBTask extends
			AsyncTask<String, TreeMap.Entry, String> {
		ProgressDialog mProgressDialog;

		protected void onPostExecute(String result) {
			mProgressDialog.dismiss();
			// Goal count is zero, nothing found in database
			if (result.equals("0")) {
				Toast.makeText(GoalPicker.this,
						"Failed to populate objectives from local database.",
						Toast.LENGTH_LONG).show();
			} else { // Display multi-level goal checkbox
				expListAdapter = new GoalAdapter(GoalPicker.this, categories,
						goals);
				setListAdapter(expListAdapter);
			}
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(GoalPicker.this,
					"Loading...",
			 "Please wait while populating goals from Database...");
			 
			constantsCursor = dbHelper.getReadableDatabase().rawQuery(
					"SELECT * FROM t_goals ORDER BY category", null);
		}


		@SuppressWarnings("unchecked")
		@Override
		protected String doInBackground(String... u) {
			// Only traverse the local database for goals if the current goalCount
			// in memory is not the same as the goal count in database
			if (constantsCursor != null
					&& constantsCursor.getCount() != GameData.getAllGoalCount()) {
				// Means there is an update on the goals, so we refresh the
				// in-memory allGoalsList
				GameData.getAllGoals().clear();
				GameData.setAllGoalCount(0);
				constantsCursor.moveToFirst();
				int count = 0;
				while (constantsCursor.isAfterLast() == false) {
					String category = constantsCursor.getString(5);
					if (category != null && !category.isEmpty()
							&& !GameData.getAllGoals().containsKey(category)) {
						GameData.getAllGoals().put(category,
								new ArrayList<Goal>());
					}
					Goal g = new Goal(constantsCursor.getInt(0),
							constantsCursor.getString(1),
							Double.parseDouble(constantsCursor.getString(3)),
							Double.parseDouble(constantsCursor.getString(4)));
					if (!GameData.getAllGoals().get(category).contains(g)) {
						GameData.getAllGoals().get(category).add(g);
						GameData.setAllGoalCount(++count);
					}
					constantsCursor.moveToNext();
				}
				constantsCursor.close();
			}
			// If there is goals available, display it on the app
			if (GameData.getAllGoalCount() > 0) {
				Iterator iterator = GameData.getAllGoals().entrySet()
						.iterator();

				while (iterator.hasNext()) {
					TreeMap.Entry entry = (TreeMap.Entry) iterator.next();
					ArrayList<Goal> gList = (ArrayList<Goal>) entry.getValue();
					String category = (String) entry.getKey();
					goals.add(gList);
					categories.add(category);
				}
			}
			return "" + GameData.getAllGoalCount();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (constantsCursor != null)
			constantsCursor.close();
		if (dbHelper != null)
			dbHelper.close();
	}

	/**
	 * Since we disable history for LoginScreen, need to override the back
	 * button to start the LoginScreen activity again
	 */
	@Override
	public void onBackPressed() {
		finish();
	}

	/**
	 * Check both wifi and 3G connection
	 */
	private boolean isInternetOn() {
		ConnectivityManager connMgr = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return (wifi.isConnected() || mobile.isConnected());
	}

	public void onContentChanged() {
		super.onContentChanged();
	}

	/**
	 * Check which checkbox is toggle
	 */
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		CheckBox cb = (CheckBox) v.findViewById(R.id.checkbox);
		if (cb != null)
			cb.toggle();
		return false;
	}
}
