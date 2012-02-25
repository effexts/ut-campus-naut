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
    private Paint paint = new Paint();
    private Point point = new Point();
    private RectF rectangle = new RectF();
    private GeoPoint gpTopLeft = null; 
    private GeoPoint gpBottomRight = null; 

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
        this.gpTopLeft = new GeoPoint(left.intValue(), top.intValue());
        this.gpBottomRight = new GeoPoint(right.intValue(), bottom.intValue());
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        // Convert the GPS coordinates to screen pixels 
        Projection projection = mapView.getProjection();


        // Top left boundary
        //Point point_topleft = new Point();
        projection.toPixels(gpTopLeft, point);
        
        rectangle.set(point.x, point.y, 0, 0);
        // Bottom right boundary
        //Point point_bottomright = new Point();
        
        projection.toPixels(gpBottomRight, point);

        //RectF rectangle = new RectF(point_topleft.x, point_topleft.y,
               // point_bottomright.x, point_bottomright.y);
        rectangle.set(rectangle.left, rectangle.top, point.x, point.y);
        // Set the rectangle drawing property
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
