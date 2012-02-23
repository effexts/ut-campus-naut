package ut.ece1778.campusnaut;

import android.os.Bundle;

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
	private static final int ZOOM_LEVEL = 19;
    private static final Double INITIAL_LATITUDE = 43.668663 * 1E6;
    private static final Double INITIAL_LONGITUDE = -79.404459 * 1E6;
    private static final Double END_LATITUDE = 43.658729 * 1E6;
    private static final Double END_LONGITUDE = -79.384868 * 1E6;
    private static final Double DIMENSION = 1000.00;
    private MapView mapView = null;
    private MapController mapController = null;
    private MyLocationOverlay myLocation = null;

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

        // Must call this to show user location overlay on map
        mapView.postInvalidate();
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
}