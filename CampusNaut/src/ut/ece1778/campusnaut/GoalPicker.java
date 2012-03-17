package ut.ece1778.campusnaut;

import ut.ece1778.bean.DBHelper;
import ut.ece1778.bean.GameData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * This activity loads goal from either server or local db
 * and let user pick goals for the game.
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class GoalPicker extends Activity {
	private static final String SYNC_DB_URL = "http://ec2-184-73-31-146.compute-1.amazonaws.com:8080/CampusNaut/servlet/RetrieveGoals";
	private static final String SERVER_MSG_BEGIN = "<BEGIN>";
	private static final String SERVER_MSG_FAIL = "<FAILED>";
	private static final String SERVER_MSG_DONE = "<DONE>";
	private static final String SERVER_MSG_NOTHING = "<NOTHING>";
	private DBHelper dbHelper = null;
	private Cursor constantsCursor = null;

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
		
		Button goButton = (Button) findViewById(R.id.button1);
		goButton.setOnClickListener(onGoClick);
		Button updateButton = (Button) findViewById(R.id.button2);
		updateButton.setOnClickListener(onUpdateClick);
		// if first time running the game, auto load from server
		if (GameData.getFirstTime()) {
			new DBAsyncTask().execute();
		} else {
			// else load from local db
			new ReadFromDBTask().execute();
		}
	}

	private OnClickListener onGoClick = new OnClickListener() {
		public void onClick(View v) {
			startActivity(new Intent(getApplicationContext(), GameMapView.class));
			finish();
		}
	};
	private OnClickListener onUpdateClick = new OnClickListener() {
		public void onClick(View v) {
			new DBAsyncTask().execute();
		}
	};
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
			} else {
				// No need to autou pdate anymore if user already has data in local db
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
					// Send local Database count to server database count to see if new data is available on server.
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
							// Keep reading record from server until we receive a message <DONE>
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
									dbHelper.getWritableDatabase().insert("t_goals", null, values);
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
	private class ReadFromDBTask extends AsyncTask<String, Void, String> {
		ProgressDialog mProgressDialog;

		protected void onPostExecute(String result) {
			mProgressDialog.dismiss();
			if (result.isEmpty()) {
				Toast.makeText(GoalPicker.this,
						"Failed to populate data from local database.",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(GoalPicker.this, result, Toast.LENGTH_LONG)
						.show();

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

		@Override
		protected String doInBackground(String... u) {
			String responseMsg = "";
			if (constantsCursor != null) {
				constantsCursor.moveToFirst();
				while (constantsCursor.isAfterLast() == false) {

					responseMsg += constantsCursor.getInt(0)
							+ constantsCursor.getString(1) + constantsCursor.getString(5) +"\n";
					constantsCursor.moveToNext();
				}
				constantsCursor.close();
			}
			return responseMsg;
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
}
