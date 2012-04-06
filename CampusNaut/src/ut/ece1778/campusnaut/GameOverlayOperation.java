package ut.ece1778.campusnaut;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

/**
 * CurrentGameOverlay Operator.
 * This class Maintains the overlay which draws goal markers on the map.
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public final class GameOverlayOperation {

	private static CurrentGameOverlay gameOverlay = null;
	private static Drawable goalMarker = null;
	private static TextView goalTitle = null;
	private static LinearLayout checkinLayout = null;
	private static GeoPoint myLocation = null;

	/**
	 * Add an ItemizedOverlay into MapView
	 * @param mapView
	 */
	public static void addGameOverlay(MapView mapView) {
		mapView.getOverlays().add(gameOverlay);
	}
	

	/**
	 * used for onLocationChange, but it produce exceptions .abandoned
	 * temporarily.
	 * 
	 * @param context
	 * @param mapView
	 * @param loc
	 */
	public static void updateGameOverlay(Context context, MapView mapView,
			Location loc) {

		
		GeoPoint myGeoPoint = new GeoPoint((int) (loc.getLatitude() * 1E6),
				(int) (loc.getLongitude() * 1E6));
		
		if (gameOverlay != null) {
			
			gameOverlay.loadItem(myGeoPoint);

			if (gameOverlay.getItems().size() > 0) {
				
			}
		}

	}

	/**
	 * update visible goals on game layer, when check-in or triggered.
	 * 
	 * @param context
	 * @param mapView
	 * @param myLocation
	 */
	public static void updateGameOverlay(Context context, MapView mapView,
			GeoPoint myLocation) {

		
		if (gameOverlay != null) {
			gameOverlay.removeAll();
			gameOverlay.loadItem(myLocation);
		}

	}

	/**
	 * clear the memory
	 */
	public static void clear() {
		gameOverlay = null;
		goalMarker = null;
		goalTitle = null;
		checkinLayout = null;
		myLocation = null;
	}

	public static CurrentGameOverlay getGameOverlay() {
		return gameOverlay;
	}

	public static void setGameOverlay(CurrentGameOverlay gameOverlay) {
		GameOverlayOperation.gameOverlay = gameOverlay;
	}

	public static Drawable getGoalMarker() {
		return goalMarker;
	}

	public static void setGoalMarker(Drawable goalMarker) {
		GameOverlayOperation.goalMarker = goalMarker;
	}

	public static TextView getGoalTitle() {
		return goalTitle;
	}

	public static void setGoalTitle(TextView goalTitle) {
		GameOverlayOperation.goalTitle = goalTitle;
	}

	public static LinearLayout getCheckinLayout() {
		return checkinLayout;
	}

	public static void setCheckinLayout(LinearLayout checkinLayout) {
		GameOverlayOperation.checkinLayout = checkinLayout;
	}

	public static GeoPoint getMyLocation() {
		return myLocation;
	}

	public static void setMyLocation(GeoPoint myLocation) {
		GameOverlayOperation.myLocation = myLocation;
	}

}
