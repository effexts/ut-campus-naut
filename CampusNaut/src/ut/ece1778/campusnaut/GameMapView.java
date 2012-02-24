package ut.ece1778.campusnaut;

import java.util.ArrayList;

import ut.ece1778.bean.Game;
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
    private static final Double DIMENSION = 1000.00;
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
                //mapView.getOverlays().add(new BlackOverlay(curLatitude, curLongitude, curLatitude - DIMENSION, curLongitude + DIMENSION));
            }
        }

        // User location overlay
        myLocation = new MyCustomLocationOverlay(this, mapView);
        mapView.getOverlays().add(myLocation);

        // Must call this to show user location overlay on map
        mapView.postInvalidate();
        
        //Checkin panel
        checkinLayout = (LinearLayout)findViewById(R.id.checkinLayout);
        goalTitle = (TextView)findViewById(R.id.goalTitile);
        checkin = (Button)findViewById(R.id.checkin);
        checkin.setOnClickListener(onCheckin);
        cancelCheckin = (Button)findViewById(R.id.cancelCheckin);
        cancelCheckin.setOnClickListener(onCancelCheckin);

       
        game = new Game();
        goals = new ArrayList<Goal>();
        Goal goal = new Goal("Roberts Library",43664300,-79399400);
        goals.add(goal);
        goal = new Goal("Athlete Centre",43662700,-79401200);
        goals.add(goal);
        goal = new Goal("Grad House",43663500,-79401500);
        goals.add(goal);
        
        game.setGoals(goals);
        
        //add game layer
        goalMarker = getResources().getDrawable(R.drawable.goal_icon);
        goalMarker.setBounds(0, 0, goalMarker.getIntrinsicWidth(), goalMarker.getIntrinsicHeight());
        gameOverlay = new CurrentGameOverlay(this,goalMarker ,this.checkinLayout,this.goalTitle,game);
        mapView.getOverlays().add(gameOverlay );
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
     * disappear checkin panel
     */
    OnClickListener onCheckin = new OnClickListener(){
		public void onClick(View v) {
			// TODO Auto-generated method stub
			checkinLayout.setVisibility(4);
			Toast.makeText(GameMapView.this,gameOverlay.getFocus().getTitle()+" visited." , Toast.LENGTH_LONG).show();
			game.getGoals().remove(gameOverlay.getLastFocusedIndex());
			mapView.getOverlays().remove(gameOverlay);
			
			gameOverlay = new CurrentGameOverlay(GameMapView.this,goalMarker ,checkinLayout,goalTitle,game);
	        mapView.getOverlays().add(gameOverlay );
			
		}
    };
    /**
     * disappear checkin panel
     */
    OnClickListener onCancelCheckin = new OnClickListener(){
		public void onClick(View v) {
			// TODO Auto-generated method stub
			checkinLayout.setVisibility(4);
		}
    };
}