package ut.ece1778.campusnaut;

import java.util.ArrayList;
import java.util.List;

import ut.ece1778.bean.Game;
import ut.ece1778.bean.GameData;
import ut.ece1778.bean.Goal;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import android.graphics.Point;
import android.location.Location;


import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * Custom location overlay to display user location with a external image
 * instead of blue dot on the map.
 *
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class MyCustomLocationOverlay extends MyLocationOverlay {

    private static final double IMAGE_SCALE = 5.0;
    private static final int DIMENSION = 400;
    private MapView mapView = null;
    private Context context = null;
    private Matrix matrix = new Matrix();
    private GeoPoint gpTopLeft = null; 
    private GeoPoint gpBottomRight = null;
    private Double left;
    private Double right;
    private Double top;
    private Double bottom;
    private Bitmap  mBitmap;
    private Canvas  mCanvas;
    private Path    mPath;
    private Paint   mBitmapPaint;
    private Paint       mPaint;
   // private GeoPoint myGeoPoint = null;
    private List<GeoPoint> gpList = new ArrayList<GeoPoint>();
    public MyCustomLocationOverlay(Context context, MapView mapView,Double left, Double top, Double right, Double bottom) {
        super(context, mapView);
        this.context = context;
        this.mapView = mapView;
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
        this.gpTopLeft = new GeoPoint(top.intValue(),left.intValue());
        this.gpBottomRight = new GeoPoint(bottom.intValue(),right.intValue());
       
        
    }
    @Override
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
    	//canvas.drawColor(0xFFAAAAAA);
    	mBitmap = Bitmap.createBitmap(800, 1280, Bitmap.Config.ARGB_8888);
        //mCanvas = new Canvas(mBitmap);
        mCanvas = new Canvas();
        mCanvas.setBitmap(mBitmap);
        RectF rectangle= new RectF();
        Projection projection = mapView.getProjection();


        // Top left boundary
        Point point = new Point();
        projection.toPixels(gpTopLeft, point);
        
        rectangle.set(point.x, point.y, 0, 0);
        projection.toPixels(gpBottomRight, point);
        rectangle.set(rectangle.left, rectangle.top, point.x, point.y);
        float zoomlevel =  (float) ((point.x - rectangle.left)/ 40.00);
        // Set the rectangle drawing property
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        mCanvas.drawRect(rectangle, paint);
        //mCanvas
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
		mPaint.setXfermode(null);
		mPaint.setAlpha(0xFF);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        for (int i = 0; i < gpList.size(); i++) {
        	MaskFilter mBlur = null;
        	//if (i == gpList.size()-1) {
        	mBlur = new BlurMaskFilter( (float) (zoomlevel/2.5), BlurMaskFilter.Blur.NORMAL);
        	//} else {
        		//mBlur =  new BlurMaskFilter( (float) (zoomlevel), BlurMaskFilter.Blur.NORMAL);
        	//}
        	mapView.getProjection().toPixels(gpList.get(i), point);
        	//System.err.println("WWTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
        	mPaint.setMaskFilter(mBlur);
        	mCanvas.drawCircle((float) point.x, (float) point.y, zoomlevel, mPaint);
        }
    	canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
    	super.draw(canvas, mapView, shadow, when);
        return true;
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
        Point point = new Point();
        if (gpList.size()==0) {
        	gpList.add(myGeoPoint);
        } else if (gpList.size() > 0) {

        if (Math.abs(curLat - gpList.get(gpList.size()-1).getLatitudeE6()) >= 500 || Math.abs(curLong - gpList.get(gpList.size()-1).getLongitudeE6()) >= 500)
        	gpList.add(myGeoPoint);
        }
    
        //it's necessary to set update when my location is jumping around
        GameData.setUpdateGoal(false);
       
        
        try{
        	GameOverlayOperation.updateGameOverlay(context, mapView, loc);
        	
        }catch(Exception e){
        	e.printStackTrace();
        }
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
