package ut.ece1778.campusnaut;

import java.util.ArrayList;

import ut.ece1778.bean.GameData;
import ut.ece1778.bean.Goal;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import android.graphics.Point;
import android.location.Location;
import android.os.Vibrator;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
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
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Paint mBitmapPaint;
	private Paint mPaint;
	// private GeoPoint myGeoPoint = null;
	//private List<GeoPoint> gpList = new ArrayList<GeoPoint>();

	public MyCustomLocationOverlay(Context context, MapView mapView,
			Double left, Double top, Double right, Double bottom) {
		super(context, mapView);
		this.context = context;
		this.mapView = mapView;
		this.gpTopLeft = new GeoPoint(top.intValue(), left.intValue());
		this.gpBottomRight = new GeoPoint(bottom.intValue(), right.intValue());
		mBitmapPaint = new Paint();
		mPaint = new Paint();

		mPaint.setColor(0x30000000);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		mBitmap = Bitmap.createBitmap(800, 1280, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);

	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
			long when) {
		if (shadow == false) {
			Projection projection = mapView.getProjection();
			// Top left boundary
			Point point = new Point();
			projection.toPixels(gpTopLeft, point);
			float zoomlevel = point.x;
			projection.toPixels(gpBottomRight, point);
			// Resize the radius of transparent circle according to the zoom
			zoomlevel = (point.x - zoomlevel) / 40.00f;
			// Radius of blur edge
			float blursize = zoomlevel / 2f;

			mCanvas.drawColor(Color.BLACK);
			MaskFilter mBlur = new BlurMaskFilter(blursize,
					BlurMaskFilter.Blur.NORMAL);
			mPaint.setMaskFilter(mBlur);
			// Retrieve visited Geopoints
			for (int i = 0; i < GameData.getGpList().size(); i++) {
				mapView.getProjection().toPixels(GameData.getGpList().get(i), point);
				mCanvas.drawCircle((float) point.x, (float) point.y, zoomlevel,
						mPaint);
			}
			canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
			super.draw(canvas, mapView, false, when);
		}
		return false;
	}

	/**
	 * Automatically re-locate the user location on the center of the map if
	 * user location is lost after panning away.
	 */
	public void onLocationChanged(Location loc) {
		super.onLocationChanged(loc);
		int curLat = (int) (loc.getLatitude() * 1E6);
		int curLong = (int) (loc.getLongitude() * 1E6);
		GeoPoint myGeoPoint = new GeoPoint(curLat, curLong);
		//GPSCoordiante gc = new GPSCoordinate(curLat, curLong);
		mapView.getController().animateTo(myGeoPoint);
		// record the GPS coordinate if the person move out of certain range.
		int curGPListSize = GameData.getGpList().size();
		if (curGPListSize == 0) {
			GameData.getGpList().add(myGeoPoint);
		} else if (curGPListSize > 0
				&& ((Math.abs(curLat
						- GameData.getGpList().get(curGPListSize - 1).getLatitudeE6()) >= DIMENSION) || (Math
						.abs(curLong
								- GameData.getGpList().get(curGPListSize - 1)
										.getLongitudeE6()) >= DIMENSION))) {
			GameData.getGpList().add(myGeoPoint);
		}

		// it's necessary to set update when my location is jumping around
		GameData.setUpdateGoal(false);

		try {
			// auto detect if there's new goals nearby
			GameOverlayOperation.getGameOverlay().loadItem(myGeoPoint);
			// acquire size of discoverdList, if size changed then new goal
			// found
			int newSize = GameData.getDiscoveredList().size();
			if (GameData.getDetector() < newSize) {
				// when new goal found ,inform with alert
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Congratulations!")
						.setMessage(
								"You just found: \n"
										+ GameData.getDiscoveredList()
												.get(newSize - 1).getTitle()
										+ " !!!")
						.setIcon(R.drawable.goal_icon)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).setCancelable(true);

				AlertDialog alert = builder.create();
				alert.show();
				// call vibrate service.
				Vibrator vibrator = (Vibrator) context
						.getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(500);
				// refresh new goal detector
				GameData.setDetector(newSize);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Override the built-in blue pin with a PNG user_icon and set the rotation.
	 */
	@Override
	protected void drawMyLocation(Canvas canvas, MapView mapView,
			Location lastFix, GeoPoint myLocation, long when) {

		// Only update nearby Goal in the beginning or after checkedin goal
		if (!GameData.getUpdateGoal()) {
			GameData.setUpdateGoal(true); // disable update until next check in

			// Game curGame = GameData.getGameList().get(0);
			ArrayList<Goal> curGoals = (ArrayList<Goal>) GameData.getTempList();
			// Keep track of minimum distance goal
			double minDistance = Double.MAX_VALUE;
			for (int i = 0; i < curGoals.size(); i++) {
				Goal curGoal = curGoals.get(i);
				curGoal.calculateDistance(lastFix);
				if (curGoal.getDistance() < minDistance) {
					minDistance = curGoal.getDistance();
					GameData.setNearbyGoal(curGoal);
					GameData.getCurGoalHeader().setText(curGoal.getTitle());
					GameData.getCurGoalHeader().setTextColor(Color.parseColor("#00aeff"));
					GameData.getCurGoalDistance().setText(Integer.toString((int)(curGoal.getDistance()))+"m");
				}
			}
			// If no more goals, game over
			if (curGoals.size() == 0) {
				GameData.getCurGoalHeader().setText("Mission Accomplished");
				GameData.getCurGoalHeader().setTextColor(Color.GREEN);
				GameData.getCurGoalDistance().setText("0m");
				
			}
		}
		Point screenPts = mapView.getProjection().toPixels(myLocation, null);
		Point goalPts = mapView.getProjection().toPixels(
				GameData.getNearbyGoal().getGeoPoint(), null);
		double dlon = goalPts.x - screenPts.x;
		double dlat = goalPts.y - screenPts.y;
		float angle = (float) (Math.atan2(dlat, dlon) * 180.00 / Math.PI);
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
		int newHeight = (int) Math.round((double) userBitmap.getHeight()
				/ IMAGE_SCALE);
		int newWidth = (int) Math.round((double) userBitmap.getWidth()
				/ IMAGE_SCALE);
		userBitmap = Bitmap.createScaledBitmap(userBitmap, newWidth, newHeight,
				true);

		// rotated bitmap with stretched size
		Bitmap rotatedBitmap = Bitmap.createBitmap(userBitmap, 0, 0,
				userBitmap.getWidth(), userBitmap.getHeight(), matrix, true);

		// Draw user_icon on the map
		canvas.drawBitmap(rotatedBitmap, screenPts.x
				- (userBitmap.getWidth() / 2),
				screenPts.y - (userBitmap.getHeight() / 2), null);
	}

	/**
	 * 
	 * @return DIMENSION of square around user
	 */
	public static int getDimension() {
		return DIMENSION;
	}
}
