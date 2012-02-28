package ut.ece1778.campusnaut;

import java.util.ArrayList;

import ut.ece1778.bean.Game;
import ut.ece1778.bean.GameData;
import ut.ece1778.bean.Goal;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;

import android.graphics.Point;
import android.location.Location;


import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

/**
 * Custom location overlay to display user location with a external image
 * instead of blue dot on the map.
 *
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class MyCustomLocationOverlay extends MyLocationOverlay {

    private static final double IMAGE_SCALE = 5.0;
    private static final int DIMENSION = 500;
    private MapView mapView = null;
    private Context context = null;
    private Matrix matrix = new Matrix();
    public MyCustomLocationOverlay(Context context, MapView mapView) {
        super(context, mapView);
        this.context = context;
        this.mapView = mapView;
    }

    /**
     * Automatically re-locate the user location on the center of the map if
     * user location is lost after panning away.
     */
    public void onLocationChanged(Location loc) {
        super.onLocationChanged(loc);
        int curLat = (int) (loc.getLatitude() * 1E6);
        int curLong = (int) (loc.getLongitude() * 1E6);
        GeoPoint myGeoPoint = new GeoPoint(curLat,
                curLong);
        mapView.getController().animateTo(myGeoPoint);

        // Remove the blackoverlay rectangle if user is within it
        for (int i = 0; i < mapView.getOverlays().size(); i++) {
            Overlay curOverlay = mapView.getOverlays().get(i);
            if (curOverlay instanceof BlackOverlay) {
            	if (curLong >= ((BlackOverlay) curOverlay).getLeft() && 
            			curLong <= ((BlackOverlay) curOverlay).getRight() &&
            			curLat >= ((BlackOverlay) curOverlay).getBottom() &&
            					curLat	<= ((BlackOverlay) curOverlay).getTop()) {
                    mapView.getOverlays().remove(curOverlay);
                    mapView.postInvalidate();
                }
            }
        }
        
        //it's necessary to set update when my location is jumping around
        GameData.setUpdateGoal(false);
        
    }

    /**
     * Override the built-in blue pin with a PNG user_icon and set the rotation.
     */
    @Override
    protected void drawMyLocation(Canvas canvas, MapView mapView, Location lastFix,
            GeoPoint myLocation, long when) {
    	
    	// Only update nearby Goal in the beginning or after checkedin goal
    	if (!GameData.getUpdateGoal()) {
    		GameData.setUpdateGoal(true); // disable update until next check in
	    	
    		Game curGame = GameData.getGameList().get(0);
	    	ArrayList<Goal> curGoals = curGame.getGoals();
	    	// Keep track of minimum distance goal
	    	double minDistance = Double.MAX_VALUE;
	    	for (int i = 0; i < curGoals.size(); i ++) {
	    		Goal curGoal = curGoals.get(i);
	    		curGoal.calculateDistance(lastFix);
	    		if (curGoal.getDistance() < minDistance) {
	    			minDistance = curGoal.getDistance();
	    			GameData.setNearbyGoal(curGoal);
	    			GameData.getCurGoalHeader().setText(curGoal.getTitle());
	    		}
	    	}
	    	// If no more goals, game over
	    	if (curGoals.size() == 0) {
	    		GameData.getCurGoalHeader().setText("Mission Accomplished");
	    		GameData.getCurGoalHeader().setBackgroundColor(Color.RED);
	    	}
    	}
        Point screenPts = mapView.getProjection().toPixels(myLocation, null);
        Point goalPts =  mapView.getProjection().toPixels(GameData.getNearbyGoal().getGeoPoint(), null);
        double dlon = goalPts.x -  screenPts.x;
        double dlat = goalPts.y -  screenPts.y;
        float angle = (float) (Math.atan2(dlat, dlon)* 180.00/Math.PI);
        // Set up the rotation matrix to point user at the goal

        matrix.reset();
        matrix.postTranslate(-screenPts.x, -screenPts.y);
        matrix.postRotate(angle);
        matrix.postRotate(90);
        matrix.postTranslate(screenPts.x, screenPts.y);       
        // Load the user icon
        Bitmap userBitmap = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.user_icon);

        // Make the user_icon image 5 times smaller 
        int newHeight = (int) Math.round((double) userBitmap.getHeight() / IMAGE_SCALE);
        int newWidth = (int) Math.round((double) userBitmap.getWidth() / IMAGE_SCALE);
        userBitmap = Bitmap.createScaledBitmap(userBitmap, newWidth, newHeight, true);

        // rotated bitmap with stretched size
        Bitmap rotatedBitmap = Bitmap.createBitmap(userBitmap, 0, 0, userBitmap.getWidth(), userBitmap.getHeight(), matrix, true);

        // Draw user_icon on the map
        canvas.drawBitmap(rotatedBitmap,
                screenPts.x - (userBitmap.getWidth() / 2),
                screenPts.y - (userBitmap.getHeight() / 2),
                null);
    }
    
    /**
     * 
     * @return DIMENSION of square around user
     */
    public static int getDimension() {
    	return DIMENSION;
    }
}
