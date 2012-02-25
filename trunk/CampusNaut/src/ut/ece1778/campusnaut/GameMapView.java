package ut.ece1778.campusnaut;

import java.util.ArrayList;

import ut.ece1778.bean.Game;
import ut.ece1778.bean.GameData;
import ut.ece1778.bean.Goal;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
	private static final int ZOOM_LEVEL = 18;
    private static final Double INITIAL_LATITUDE = 43.668663 * 1E6;
    private static final Double INITIAL_LONGITUDE = -79.404459 * 1E6;
    private static final Double END_LATITUDE = 43.658729 * 1E6;
    private static final Double END_LONGITUDE = -79.384868 * 1E6;
    private static final Double DIMENSION = (Double) (MyCustomLocationOverlay.getDimension() * 2.0);
    private MapView mapView = null;
    private MapController mapController = null;
    private MyLocationOverlay myLocation = null;
    private LinearLayout checkinLayout;
    private TextView goalTitle;
    private Button checkin;
    private Button cancelCheckin;
    private CurrentGameOverlay gameOverlay;
    private Game game;
    private ArrayList<Goal> goals;
    private Drawable goalMarker;
    private Button trigger;
    private TextView scoreBoard;
    private int score  = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamemap);

        mapView = (MapView) findViewById(R.id.myMap);
        mapView.setBuiltInZoomControls(true);
        
        
        mapController = mapView.getController();
        mapController.setZoom(ZOOM_LEVEL);
        // Draw multiple black overlays on top of map
        for (Double curLatitude = INITIAL_LATITUDE; curLatitude > END_LATITUDE; curLatitude -= DIMENSION) {
            for (Double curLongitude = INITIAL_LONGITUDE; curLongitude < END_LONGITUDE; curLongitude += DIMENSION) {
                mapView.getOverlays().add(new BlackOverlay(curLatitude, curLongitude, curLatitude - DIMENSION, curLongitude + DIMENSION));
            }
        }

        // User location overlay
        myLocation = new MyCustomLocationOverlay(this, mapView);
        mapView.getOverlays().add(myLocation);
        myLocation.enableMyLocation();

        // Must call this to show user location overlay on map
        mapView.postInvalidate();
        
        //Checkin panel
        checkinLayout = (LinearLayout)findViewById(R.id.checkinLayout);
        goalTitle = (TextView)findViewById(R.id.goalTitile);
        scoreBoard = (TextView)findViewById(R.id.scoreBoard);
        checkin = (Button)findViewById(R.id.checkin);
        checkin.setOnClickListener(onCheckin);
        cancelCheckin = (Button)findViewById(R.id.cancelCheckin);
        cancelCheckin.setOnClickListener(onCancelCheckin);
        trigger =  (Button)findViewById(R.id.trigger);
        trigger.setOnClickListener(onTrigger);
        // Set the current widget
        GameData.setCurGoalHeader((TextView)findViewById(R.id.header));
        GameOverlayOperation.setCheckinLayout(checkinLayout);
        GameOverlayOperation.setGoalTitle(goalTitle);
        
      //load marker resource
        goalMarker = getResources().getDrawable(R.drawable.goal_icon);
        goalMarker.setBounds(0, 0, goalMarker.getIntrinsicWidth(), goalMarker.getIntrinsicHeight());
        GameOverlayOperation.setGoalMarker(goalMarker);
        
        //Instantiate a Game
        game = new Game();
        goals = new ArrayList<Goal>();
        Goal goal = new Goal("Robarts Library",43.664300,-79.399400);
        goals.add(goal);
        goal = new Goal("Athlete Centre",43.662700,-79.401200);
        goals.add(goal);
        goal = new Goal("Grad House",43.663500,-79.401500);
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
        
        // Always center the user location on the map
        myLocation.runOnFirstFix(new Runnable() {
            public void run() {
                mapController.setCenter(myLocation.getMyLocation());
                
                gameOverlay = new CurrentGameOverlay(
                		GameMapView.this, goalMarker, checkinLayout, goalTitle, 
                		GameData.getGameList().get(0), myLocation.getMyLocation());
                //mapView.getOverlays().add(gameOverlay );
                GameOverlayOperation.setMyLocation(myLocation.getMyLocation());
                GameOverlayOperation.setGameOverlay(gameOverlay);
                GameOverlayOperation.addGameOverlay(mapView);
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
    OnClickListener onCheckin = new OnClickListener(){
		public void onClick(View v) {
			// Update the nearby goal
			GameData.setUpdateGoal(false);
			checkinLayout.setVisibility(4);
			
			Toast.makeText(GameMapView.this,GameOverlayOperation.getGameOverlay().getFocus().getTitle()+" visited." , Toast.LENGTH_LONG).show();
		    //remove checked goal from GameData's games list
			GameData.getGameList().get(0).getGoals().remove(
							Integer.parseInt(GameOverlayOperation.getGameOverlay().getFocus().getSnippet()));
			System.out.println(GameData.getGameList().get(0).toString());
			//reload goals list into CurrentGameOverlay
			GameOverlayOperation.updateGameOverlay(getApplicationContext(), mapView, myLocation.getMyLocation());
			//update score board
			score += 50;	
			scoreBoard.setText("Score: " + score);
			
			
			
		}
    };
    /**
     * disappear check-in panel
     */
    OnClickListener onCancelCheckin = new OnClickListener(){
		public void onClick(View v) {
			checkinLayout.setVisibility(4);
		}
    };
    
    /**
     * search nearby goals , if there are , show their markers.
     */
    OnClickListener onTrigger = new OnClickListener(){
		public void onClick(View v) {
			GameOverlayOperation.updateGameOverlay(getApplicationContext(), mapView, myLocation.getMyLocation());
			mapController.setCenter(myLocation.getMyLocation());
		}
    };
}