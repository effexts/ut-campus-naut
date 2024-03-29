package ut.ece1778.campusnaut;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import java.util.TimerTask;

import ut.ece1778.bean.DBHelper;
import ut.ece1778.bean.Game;
import ut.ece1778.bean.GameData;
import ut.ece1778.bean.Goal;
import ut.ece1778.bean.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

// Uncomment the following two imports if Mock GPS is used.
//import java.util.Timer;
//import android.content.Context;

/**
 * The main Map view activity that is used to coordinate all components on 
 * top of google map such as current user location, check-in, and fog of war overlay. 
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class GameMapView extends MapActivity {
	// URL for mock GPS location
	private static final String GPS_URL = "http://ec2-50-16-100-246.compute-1.amazonaws.com:8080/CampusNaut/steve.txt";
	// Update time for polling mock GPS (Uncomment if Mock GPS is used)
	// private static final int GPS_UPDATE_TIME = 2000;
	
	private static final String GAME_INIT_URL = "http://ec2-50-16-100-246.compute-1.amazonaws.com:8080/CampusNaut/servlet/SetupGame";
	private static final int ZOOM_LEVEL = 19;
	private static final Double INITIAL_LATITUDE = 43.669858 * 1E6;
	private static final Double INITIAL_LONGITUDE = -79.400727 * 1E6;
	private static final Double END_LATITUDE = 43.657859 * 1E6;
	private static final Double END_LONGITUDE = -79.381928 * 1E6;
	
	private MapView mapView = null;
	private MapController mapController = null;
	private MyLocationOverlay myLocation = null;
	private CurrentGameOverlay gameOverlay;
	private Game game;
	private ArrayList<Goal> goals;
	private Drawable goalMarker;
	private Button trigger;
	private SharedPreferences prefs = null;
	private Editor editor = null;
	private LocationManager mockLocMgr = null;
	private String mocLocationProvider = null;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gamemap);
		// Retrieve local datastore
		prefs = getSharedPreferences("UserDataStorage", MODE_PRIVATE);
		// Use for modifying login state
		editor = prefs.edit();

		mapView = (MapView) findViewById(R.id.myMap);
		mapView.setBuiltInZoomControls(false);

		mapController = mapView.getController();
		mapController.setZoom(ZOOM_LEVEL);
		// Load last saved geopoint list to draw transparent circle on the map
		GameData.loadGpList(this);
		// User location overlay
		myLocation = new MyCustomLocationOverlay(this, mapView,
				INITIAL_LONGITUDE, INITIAL_LATITUDE, END_LONGITUDE,
				END_LATITUDE);
		mapView.getOverlays().add(myLocation);

		
		// Mock GPS* Code for mock Location provider Uncomment the following to use MockGPS Controller
		/*mocLocationProvider = LocationManager.NETWORK_PROVIDER;

		mockLocMgr = (LocationManager) getBaseContext().getSystemService(
				Context.LOCATION_SERVICE);
		// mockLocMgr.clearTestProviderEnabled(mocLocationProvider);
		// mockLocMgr.requestLocationUpdates(mocLocationProvider, 0, 0,
		// locListener);

		mockLocMgr.addTestProvider(mocLocationProvider, false, false, false,
				false, true, true, true, 0, 5);
		mockLocMgr.setTestProviderEnabled(mocLocationProvider, true);
		// Pull the Location every 1 second
		Timer timer = new Timer();
		timer.schedule(new MockGPSUpdateTimeTask(), 500, GPS_UPDATE_TIME);
		// End of *Mock GPS* code
		*/
		
		// Must call this to show user location overlay on map
		mapView.postInvalidate();

		// Tools of map view
		trigger = (Button) findViewById(R.id.trigge);
		trigger.setOnClickListener(onTrigger);
		
		// Set the current widget
		TextView header = (TextView) findViewById(R.id.header);
		TextView distancer = (TextView)findViewById(R.id.distanceBoard);
		
		// header.setMovementMethod(new ScrollingMovementMethod());
		GameData.setCurGoalHeader(header);
		GameData.setCurGoalDistance(distancer);
		
		// load marker resource
		goalMarker = getResources().getDrawable(R.drawable.goal_icon);
		goalMarker.setBounds(0, 0, goalMarker.getIntrinsicWidth(),
				goalMarker.getIntrinsicHeight());
		GameOverlayOperation.setGoalMarker(goalMarker);

		// Instantiate a Game
		game = new Game();
		goals = GameData.getSelectedGoal(); // Add the selected goal to our game
		game.setGoals(goals);

		int uID = prefs.getInt("user_id", 0);
		if(uID != 0){
			User curUser = new User();
			curUser.setuID(uID);
			GameData.setCurUser(curUser);
		}
		// Add to temporary data store
		GameData.add(game);
		GameData.setTempList(goals);
		
		String initData = "";
		// Check if there's checked-in goal
		for (Goal goal : GameData.getGameList().get(0).getGoals()) {
			initData += goal.getgID() +"%";
		}
		
		if (initData.equals("")) {
			Toast.makeText(GameMapView.this,
					"No goals loaded!", 1000).show();
		} else {
			// Update check-in state with server DB
			GameInitAsyncTask task = new GameInitAsyncTask();
			task.execute(new String[] { initData.substring(0,
					initData.length() - 1) });
		}
		
		//Load Overlay of goals on map
		gameOverlay = new CurrentGameOverlay(GameMapView.this, goalMarker,
				GameData.getGameList().get(0));
		GameOverlayOperation.setGameOverlay(gameOverlay);
		GameOverlayOperation.addGameOverlay(mapView);
	}

	/**
	 * Enable location tracker upon enter map.
	 */
	@Override
	public void onResume() {
		super.onResume();
		myLocation.enableMyLocation();
		// Always center the user location on the map
		myLocation.runOnFirstFix(new Runnable() {
			public void run() {
				try {
					mapController.setCenter(myLocation.getMyLocation());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Disable location tracker upon exit map.
	 */
	@Override
	public void onPause() {
		super.onPause();
		myLocation.disableMyLocation();
	}

	/**
	 * Disable location tracker and clear some memory upon exiting map.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		// Save the geopoint list before exit
		myLocation.disableMyLocation();
		GameData.saveGpList(this);
		GameData.clear();
		GameOverlayOperation.clear();
		mapView.getOverlays().remove(gameOverlay);
		mapView.getOverlays().clear();
	}

	/**
	 * Must override this.
	 */
	@Override
	protected boolean isLocationDisplayed() {
		return myLocation.isMyLocationEnabled();
	}

	/**
	 * Must override this.
	 */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * only center user's location
	 */
	OnClickListener onTrigger = new OnClickListener() {
		public void onClick(View v) {
			if (myLocation.getMyLocation() != null) { 
				// Make sure location is available before calling method
				mapController.setZoom(19);
				mapController.animateTo(myLocation.getMyLocation());
			}

		}
	};

	/**
	 * Create the Icon menu from menu/context.xml
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context, menu);
		return true;
	}

	/**
	 * Actions to be done after user click on an option.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.logout:
			// Change the loggedin state to false and go back to login screen
			editor.putBoolean("loggedin", false);
			editor.commit();
			finish();
			break;
		//case R.id.setting:
			// Future implementation
			//break;
		case R.id.editor:
			GameData.setOnPause(true);
			Intent intent = new Intent(this, GoalPicker.class);
			startActivity(intent);
			break;
		//case R.id.achievement:
			// Future implementation
			//break;
		//case R.id.upload: 
			// Future implementation
			//break;
		}
		return true;
	}

	/**
	 * Mock GPS signal timer task to pull GPS coordinate from backend.
	 * 
	 */
	class MockGPSUpdateTimeTask extends TimerTask {
		public void run() {
			try {
				URL url = new URL(GPS_URL);
				URLConnection urlConnection = url.openConnection();
				InputStream in = new BufferedInputStream(
						urlConnection.getInputStream());
				if (in != null) {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					String str;
					while ((str = reader.readLine()) != null) {
						String[] result = str.split(",");
						if (result.length == 2) {
							Location loc = new Location(mocLocationProvider);
							loc.setTime(System.currentTimeMillis());
							loc.setLatitude(Double.parseDouble(result[0]));
							loc.setLongitude(Double.parseDouble(result[1]));
							loc.setAccuracy(10);
							mockLocMgr.setTestProviderStatus(
									mocLocationProvider,
									LocationProvider.AVAILABLE, null,
									System.currentTimeMillis());
							mockLocMgr.setTestProviderLocation(
									mocLocationProvider, loc);
						}

					}

					in.close();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	
	/**
	 * Initialize game,  insert user selections into t_user_games
	 * Load previous check-in state from local DB into objective list
	 * Acquire discovered goal from local DB 
	 */
	private class GameInitAsyncTask extends AsyncTask<String, Void, String>{
		ProgressDialog mProgressDialog;

		@Override
		protected void onPostExecute(String result) {
			mProgressDialog.dismiss();
			if (result == null) {
				Toast.makeText(
						GameMapView.this,
						"Cannot access the server. \nPlease make sure your WI-FI is on.",
						Toast.LENGTH_LONG).show();
			} else if (result.equals("Invalid")) {
				Toast.makeText(GameMapView.this, "Invalid Request.",
						Toast.LENGTH_LONG).show();
			} else {
				DBHelper helper = new DBHelper(GameMapView.this);
				Cursor constantsCursor = null;
				constantsCursor = helper.getReadableDatabase()
						.rawQuery("SELECT * FROM t_goals WHERE state > ?",new String[]{String.valueOf(0)});
				if (constantsCursor != null){
					//clear
					GameData.getDiscoveredList().clear();
					
					constantsCursor.moveToFirst();
					while (constantsCursor.isAfterLast() == false) {

						Goal goal = new Goal(constantsCursor.getInt(0),
								constantsCursor.getString(1),
								Double.parseDouble(constantsCursor.getString(3)),
								Double.parseDouble(constantsCursor.getString(4)));
						goal.setState(constantsCursor.getInt(6));
						
						//Get discovered goal from local DB
						if(goal.getState()>0){
							GameData.getDiscoveredList().add(goal);
						}
						constantsCursor.moveToNext();
					}
					constantsCursor.close();
				}
				helper.getReadableDatabase().close();
				helper.close();
				//Update the goal detector
				GameData.setDetector(GameData.getDiscoveredList().size());
			}
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog
					.show(GameMapView.this, "Initializing...",
							"Please wait for game to be setup...");
		}

		@Override
		protected String doInBackground(String... u) {
			URL url = null;
			HttpURLConnection httpConn = null;
			String returnStr = "null";
			try {
				//Open Http connection
				url = new URL(GAME_INIT_URL);
				httpConn = (HttpURLConnection) url.openConnection();
				httpConn.setDoOutput(true);
				httpConn.setRequestMethod("POST");

				// Do post request.
				DataOutputStream out = new DataOutputStream(
						httpConn.getOutputStream());
				out.writeUTF("<SETUP>");
				out.writeUTF(Integer.toString(GameData.getCurUser().getuID()));
				
				out.writeUTF(u[0]);
				out.flush();
				out.close();

				// Receiving response from server
				DataInputStream in = new DataInputStream(
						httpConn.getInputStream());
				returnStr = in.readUTF();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return returnStr;
		}
	
	}
	
}