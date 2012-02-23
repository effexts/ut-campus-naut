package ut.ece1778.campusnaut;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * Black rectangle overlay to create 'fog of war' effects on the map.
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class BlackOverlay extends Overlay {

    private Double left;
    private Double right;
    private Double top;
    private Double bottom;

    /**
     * Constructor which takes in starting top-left GPS coordinate and
     * bottom-right GPS coordinate.
     *
     * @param left starting latitude
     * @param top starting longitude
     * @param right end latitude
     * @param bottom end longitude
     */
    public BlackOverlay(Double left, Double top, Double right, Double bottom) {
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        // Convert the GPS coordinates to screen pixels 
        Projection projection = mapView.getProjection();
        GeoPoint geoPoint = new GeoPoint(left.intValue(), top.intValue());

        // Top left boundary
        Point point_topleft = new Point();
        projection.toPixels(geoPoint, point_topleft);
        geoPoint = new GeoPoint(right.intValue(), bottom.intValue());

        // Bottom right boundary
        Point point_bottomright = new Point();
        projection.toPixels(geoPoint, point_bottomright);

        RectF rectangle = new RectF(point_topleft.x, point_topleft.y,
                point_bottomright.x, point_bottomright.y);

        // Set the rectangle drawing property
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);

        canvas.drawRect(rectangle, paint);
        super.draw(canvas, mapView, shadow);
    }

    public Double getLeft() {
        return this.left;
    }

    public Double getRight() {
        return this.right;
    }

    public Double getTop() {
        return this.top;
    }

    public Double getBottom() {
        return this.bottom;
    }

    @Override
    public boolean onTap(GeoPoint point, MapView mapView) {
        return false;
    }
}
