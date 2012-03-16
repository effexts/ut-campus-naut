package ut.ece1778.campusnaut;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ut.ece1778.bean.Game;
import ut.ece1778.bean.GameData;
import ut.ece1778.bean.Goal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

/**
 * In-Game Map view activity that automatically locates the current user on the
 * map and point the user in the direction of the goal.
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class GameMapView extends MapActivity {
	// URL for remote GPS location
	
	private static final String GPS_URL = "http://ec2-184-73-31-146.compute-1.amazonaws.com:8080/CampusNaut/leo.txt";
	private static final int GPS_UPDATE_TIME = 3000;

	private static final int ZOOM_LEVEL = 19;
	private static final Double INITIAL_LATITUDE = 43.669858 * 1E6;
	private static final Double INITIAL_LONGITUDE = -79.40727 * 1E6;
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
	private TextView scoreBoard;
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

		// User location overlay
		myLocation = new MyCustomLocationOverlay(this, mapView,INITIAL_LONGITUDE, INITIAL_LATITUDE, END_LONGITUDE, END_LATITUDE);
		mapView.getOverlays().add(myLocation);
		myLocation.enableMyLocation();
		
		// *Mock GPS* Code for mock Location provider
		mocLocationProvider = LocationManager.NETWORK_PROVIDER;
		mockLocMgr = (LocationManager) getBaseContext().getSystemService(
				Context.LOCATION_SERVICE);
		//mockLocMgr.clearTestProviderEnabled(mocLocationProvider);
		//mockLocMgr.requestLocationUpdates(mocLocationProvider, 0, 0,
				//locListener);

		mockLocMgr.addTestProvider(mocLocationProvider, false, false, false,
				false, true, true, true, 0, 5);
		mockLocMgr.setTestProviderEnabled(mocLocationProvider, true);
		// Pull the Location every 1 second
		Timer timer = new Timer();
		timer.schedule(new MockGPSUpdateTimeTask(), 500, GPS_UPDATE_TIME);
		// End of *Mock GPS* code
		
		// Must call this to show user location overlay on map
		mapView.postInvalidate();

		//tools of map view
		scoreBoard = (TextView) findViewById(R.id.scoreBoard);
		trigger = (Button) findViewById(R.id.trigge);
		trigger.setOnClickListener(onTrigger);
		// Set the current widget
		GameData.setCurGoalHeader((TextView) findViewById(R.id.header));
		

		// load marker resource
		goalMarker = getResources().getDrawable(R.drawable.goal_icon);
		goalMarker.setBounds(0, 0, goalMarker.getIntrinsicWidth(),
				goalMarker.getIntrinsicHeight());
		GameOverlayOperation.setGoalMarker(goalMarker);

		// Instantiate a Game
		game = new Game();
		goals = new ArrayList<Goal>();
		/*
		 * Goal goal = new Goal("Robarts Library", 43.664300, -79.399400);
		 * goals.add(goal); goal = new Goal("Athlete Centre", 43.662700,
		 * -79.401200); goals.add(goal); goal = new Goal("Grad House",
		 * 43.663500, -79.401500); goals.add(goal); goal = new Goal("Cedars",
		 * 43.660000, -79.398500); goals.add(goal);
		 */
		Goal goal = new Goal(101,"Becca's H, Robert Murray (1973)", 43.659955,
				-79.396584);
		goals.add(goal);
		goal = new Goal(102,"Helix of Life, Ted Bieler (1967)", 43.660747,
				-79.393537);
		goals.add(goal);
		goal = new Goal(103,"Cedars, Walter Yarwood (1962)", 43.660000, -79.398500);
		goals.add(goal);
		goal = new Goal(104,"Untitled, Ron Bard (1964)", 43.658387, -79.393516);
		goals.add(goal);
		game.setGoals(goals);

		// Add to temporary data store
		GameData.add(game);

		GameData.setTempList(goals);
		
		gameOverlay = new CurrentGameOverlay(GameMapView.this,
		 		goalMarker, GameData.getGameList().get(0));
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
		scoreBoard.setText("Score: " + GameData.getScores());
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
	 * Disable location tracker upon exit map.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
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

			if (myLocation.getMyLocation() != null) { // make sure location is
														// available before
														// calling method
				mapController.setZoom(19);
				mapController.animateTo(myLocation.getMyLocation());
			}

		}
	};

	/**
	 * Unused
	 */
	LocationListener locListener = new LocationListener() {

		public void onLocationChanged(Location location) {

		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
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
		case R.id.setting:
			// Future implementation
			break;
		case R.id.editor:
			// Future implementation
			break;
		case R.id.achievement:
			// Future implementation
			break;
		}
		return true;
	}

	/**
	 * Mock GPS signal timer task
	 * @author SteveWho
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
}