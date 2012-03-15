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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
	private static final String GPS_URL = "http://ec2-184-73-31-146.compute-1.amazonaws.com:8080/CampusNaut/gps.txt";
	private static final int ZOOM_LEVEL = 19;
	private static final Double INITIAL_LATITUDE = 43.669858 * 1E6;
	private static final Double INITIAL_LONGITUDE = -79.40727 * 1E6;
	private static final Double END_LATITUDE = 43.657859 * 1E6;
	private static final Double END_LONGITUDE = -79.381928 * 1E6;
	private static final Double DIMENSION = (Double) (MyCustomLocationOverlay
			.getDimension() * 2.0);
	private static final Double LAT_D = DIMENSION * 1.5;
	private static final Double LONG_D = DIMENSION * 2;
	private MapView mapView = null;
	private MapController mapController = null;
	private MyLocationOverlay myLocation = null;
	private LinearLayout checkinLayout;
	private TextView goalTitle;
	// private Button checkin;
	// private Button cancelCheckin;
	private CurrentGameOverlay gameOverlay;
	private Game game;
	private ArrayList<Goal> goals;
	private Drawable goalMarker;
	private Button trigger;
	private TextView scoreBoard;
	private LocationManager locMngr;
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
		// Draw multiple black overlays on top of map
		for (Double curLatitude = INITIAL_LATITUDE; curLatitude > END_LATITUDE; curLatitude -= LAT_D) {
			for (Double curLongitude = INITIAL_LONGITUDE; curLongitude < END_LONGITUDE; curLongitude += LONG_D) {
				//mapView.getOverlays().add(new BlackOverlay(curLongitude, curLatitude,curLongitude + LONG_D, curLatitude - LAT_D));
			}
		}

		// User location overlay
		
		myLocation = new MyCustomLocationOverlay(this, mapView,INITIAL_LONGITUDE, INITIAL_LATITUDE, END_LONGITUDE, END_LATITUDE);
		mapView.getOverlays().add(myLocation);
		myLocation.enableMyLocation();
		
		// *Mock GPS* Code for mock Location provider
		mocLocationProvider = LocationManager.NETWORK_PROVIDER;
		mockLocMgr = (LocationManager) getBaseContext().getSystemService(
				Context.LOCATION_SERVICE);
		mockLocMgr.clearTestProviderEnabled(mocLocationProvider);
		mockLocMgr.requestLocationUpdates(mocLocationProvider, 0, 0,
				locListener);

		mockLocMgr.addTestProvider(mocLocationProvider, false, false, false,
				false, true, true, true, 0, 5);
		mockLocMgr.setTestProviderEnabled(mocLocationProvider, true);
		// Pull the Location every 1 second
		Timer timer = new Timer();
		timer.schedule(new MockGPSUpdateTimeTask(), 100, 1000);
		// End of *Mock GPS* code
		
		// Must call this to show user location overlay on map
		mapView.postInvalidate();

		// message panel
		/*
		 * checkinLayout = (LinearLayout)findViewById(R.id.checkinLayout);
		 * goalTitle = (TextView)findViewById(R.id.goalTitile);
		 * 
		 * checkin = (Button)findViewById(R.id.checkin);
		 * checkin.setOnClickListener(onCheckin); cancelCheckin =
		 * (Button)findViewById(R.id.cancelCheckin);
		 * cancelCheckin.setOnClickListener(onCancelCheckin);
		 */
		scoreBoard = (TextView) findViewById(R.id.scoreBoard);
		trigger = (Button) findViewById(R.id.trigge);
		trigger.setOnClickListener(onTrigger);
		// Set the current widget
		GameData.setCurGoalHeader((TextView) findViewById(R.id.header));
		GameOverlayOperation.setCheckinLayout(checkinLayout);
		GameOverlayOperation.setGoalTitle(goalTitle);

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
		Goal goal = new Goal("Becca's H, Robert Murray (1973)", 43.659955,
				-79.396584);
		goals.add(goal);
		goal = new Goal("Helix of Life, Ted Bieler (1967)", 43.660747,
				-79.393537);
		goals.add(goal);
		goal = new Goal("Cedars, Walter Yarwood (1962)", 43.660000, -79.398500);
		goals.add(goal);
		goal = new Goal("Untitled, Ron Bard (1964)", 43.658387, -79.393516);
		goals.add(goal);
		game.setGoals(goals);

		// Add to temporary data store
		GameData.add(game);

	}

	/**
	 * Enable location tracker upon enter map.
	 */
	@Override
	public void onResume() {
		super.onResume();
		myLocation.enableMyLocation();
		scoreBoard.setText("Score: " + GameData.getScores());
		locMngr = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		locMngr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
				locListener);
		// Always center the user location on the map
		myLocation.runOnFirstFix(new Runnable() {

			public void run() {
				try {
					mapController.setCenter(myLocation.getMyLocation());
					mapView.getOverlays().remove(gameOverlay);
					gameOverlay = new CurrentGameOverlay(GameMapView.this,
							goalMarker, checkinLayout, goalTitle, GameData
									.getGameList().get(0), myLocation
									.getMyLocation());
					// mapView.getOverlays().add(gameOverlay );
					GameOverlayOperation.setMyLocation(myLocation
							.getMyLocation());
					GameOverlayOperation.setGameOverlay(gameOverlay);
					GameOverlayOperation.addGameOverlay(mapView);
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
	 * do check-in a goal
	 */
	OnClickListener onCheckin = new OnClickListener() {

		public void onClick(View v) {
			// Update the nearby goal
			GameData.setUpdateGoal(false);
			checkinLayout.setVisibility(4);

			Toast.makeText(
					GameMapView.this,
					GameOverlayOperation.getGameOverlay().getFocus().getTitle()
							+ " visited.", Toast.LENGTH_LONG).show();
			// remove checked goal from GameData's games list
			/*
			 * GameData.getGameList().get(0).getGoals().remove(
			 * Integer.parseInt(
			 * GameOverlayOperation.getGameOverlay().getFocus().getSnippet()));
			 */
			GameData.getGameList()
					.get(0)
					.getGoals()
					.get(GameOverlayOperation.getGameOverlay()
							.getLastFocusedIndex());
			System.out.println(GameData.getGameList().get(0).toString());
			// reload goals list into CurrentGameOverlay
			GameOverlayOperation.updateGameOverlay(getApplicationContext(),
					mapView, myLocation.getMyLocation());
		}
	};
	/**
	 * disappear check-in panel
	 */
	OnClickListener onCancelCheckin = new OnClickListener() {

		public void onClick(View v) {
			checkinLayout.setVisibility(4);
		}
	};
	/**
	 * search nearby goals , if there are , show their markers. center user's
	 * location
	 */
	OnClickListener onTrigger = new OnClickListener() {

		public void onClick(View v) {
			GameOverlayOperation.updateGameOverlay(getApplicationContext(),
					mapView, myLocation.getMyLocation());
			// System.out.println(GameOverlayOperation.getGameOverlay().getItems().size());
			// mapController.setCenter(myLocation.getMyLocation());
			if (myLocation.getMyLocation() != null) { // make sure location is
														// available before
														// calling method
				mapController.setZoom(19);
				mapController.animateTo(myLocation.getMyLocation());
			}

		}
	};

	/**
	 * set new goal detector
	 */
	LocationListener locListener = new LocationListener() {

		public void onLocationChanged(Location location) {
			try {
				if (GameData.getDetector() < gameOverlay.getItems().size()) {
					/*
					 * call vibrate service Vibrator vibrator = (Vibrator)
					 * getSystemService(VIBRATOR_SERVICE);
					 * vibrator.vibrate(1000);
					 */
					// show alert dialog
					AlertDialog.Builder builder = new AlertDialog.Builder(
							GameMapView.this);
					builder.setTitle("New goal discovered!")
							.setMessage(
									gameOverlay.getItem(
											gameOverlay.getItems().size() - 1)
											.getTitle())
							.setIcon(R.drawable.goal_icon)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {

										}
									}).setCancelable(true);

					AlertDialog alert = builder.create();
					alert.show();

					GameData.setDetector(gameOverlay.getItems().size());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
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