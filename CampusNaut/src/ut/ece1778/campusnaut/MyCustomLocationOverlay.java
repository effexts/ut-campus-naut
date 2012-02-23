package ut.ece1778.campusnaut;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
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

    private static final int CIRCLE_RADIUS = 80;
    private static final int CIRCLE_COLOR = 0x30000000;
    private static final int CIRCLE_STROKE_COLOR = 0x99000000;
    private static final double IMAGE_SCALE = 5.0;
    private MapView mapView = null;
    private Context context = null;

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
                if ((curLat < ((BlackOverlay) curOverlay).getLeft())
                        && (curLat > ((BlackOverlay) curOverlay).getRight())
                        && (curLong > ((BlackOverlay) curOverlay).getTop())
                        && (curLong < ((BlackOverlay) curOverlay).getBottom())) {
                    mapView.getOverlays().remove(curOverlay);
                    mapView.postInvalidate();
                }
            }
        }
    }

    /**
     * Override the built-in blue pin with a PNG user_icon and set the rotation.
     */
    @Override
    protected void drawMyLocation(Canvas canvas, MapView mapView, Location lastFix,
            GeoPoint myLocation, long when) {
        // Translate the GeoPoint to screen pixels
        Point screenPts = mapView.getProjection().toPixels(myLocation, null);

        // Load the user icon
        Bitmap userBitmap = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.user_icon);

        // Make the user_icon image 5 times smaller 
        int newHeight = (int) Math.round((double) userBitmap.getHeight() / IMAGE_SCALE);
        int newWidth = (int) Math.round((double) userBitmap.getWidth() / IMAGE_SCALE);
        userBitmap = Bitmap.createScaledBitmap(userBitmap, newWidth, newHeight, true);

        // Draw a circle around the user icon
        Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(CIRCLE_COLOR);
        circlePaint.setStyle(Style.FILL_AND_STROKE);
        canvas.drawCircle(screenPts.x, screenPts.y, CIRCLE_RADIUS, circlePaint);

        circlePaint.setColor(CIRCLE_STROKE_COLOR);
        circlePaint.setStyle(Style.STROKE);
        canvas.drawCircle(screenPts.x, screenPts.y, CIRCLE_RADIUS, circlePaint);

        // Draw user_icon on the map
        canvas.drawBitmap(userBitmap,
                screenPts.x - (userBitmap.getWidth() / 2),
                screenPts.y - (userBitmap.getHeight() / 2),
                null);
    }
}
