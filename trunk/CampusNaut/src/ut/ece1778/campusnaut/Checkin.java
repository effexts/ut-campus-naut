package ut.ece1778.campusnaut;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.net.URL;

import ut.ece1778.bean.DBHelper;
import ut.ece1778.bean.GameData;
import ut.ece1778.bean.Goal;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Check in Screen. Shows information of a selected goal.
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */

public class Checkin extends Activity {

	private static final String IMAGE_FOLDER_URL = "http://ec2-184-73-31-146.compute-1.amazonaws.com/photos/";
	private static final String UPD_PROG_URL = "http://ec2-184-73-31-146.compute-1.amazonaws.com:8080/CampusNaut/servlet/UpdateProgress";
	private Button checkin;
	private Button back;
	private TextView checkinTitle;
	private TextView goalDescription;
	private ImageView checkinPic;
	private int gid;
	private int inRange;
	private int counter;
	private Goal tGoal = new Goal();
	private DBHelper dbHelper = null;
	private Cursor constantsCursor = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkin);

		// Init widget
		checkin = (Button) findViewById(R.id.CK);
		back = (Button) findViewById(R.id.BK);
		checkin.setOnClickListener(onCheckin);
		back.setOnClickListener(onBack);
		checkinTitle = (TextView) findViewById(R.id.checkinTitle);
		checkinPic = (ImageView) findViewById(R.id.checkinPic);
		goalDescription = (TextView) findViewById(R.id.checkinIntro);
		// Set up connection to local SQLLite
		dbHelper = new DBHelper(this);

		// Get values passed from Main Activity
		Bundle bundle = getIntent().getExtras();
		gid = bundle.getInt("focus");
		counter = bundle.getInt("counter");
		inRange = bundle.getInt("inRange");
		// Load the image from web
		try {
			new LoadImage(checkinPic).execute(IMAGE_FOLDER_URL + gid + ".jpg");
		} catch (Exception e) {
			checkinPic.setImageResource(R.drawable.p20019);
		}
		// Load the description from SQLite Database
		new LoadDescription().execute("");

		// Get selected Goal Object
		tGoal = GameData.findGoalById(gid, GameData.getDiscoveredList());
		//Toast.makeText(this, inRange+":"+tGoal.getState(), Toast.LENGTH_LONG).show();
		// Check the goal's state and whether in range to be able to check-in
		if (inRange == 0 && tGoal.getState() != 2) {
			// Out of range ,disable the check in button
			checkin.setEnabled(false);
		} else if (inRange == 1 && tGoal.getState() != 2 ) {
			// Unchecked goal in range
			// Enable the check in button
			checkin.setEnabled(true);
		} else { // Goal already checked in
			checkin.setEnabled(false);
			checkin.setText("CheckED");
			checkinTitle.setBackgroundColor(0xff00ff00);
		}
		// Show informations
		checkinTitle.setText(tGoal.getTitle());
	}

	/**
	 * On Click to Check in
	 */
	OnClickListener onCheckin = new OnClickListener() {

		public void onClick(View v) {

			// Update Goal Lists in GameData
			GameData.updateState(gid);
			//Update state in local DB
			DBHelper helper = new DBHelper(Checkin.this);		
			try {
				
				ContentValues cv  = new ContentValues();
				cv.put("state", 2);
				helper.getWritableDatabase().update("t_goals", cv, "goal_id = ?", new String[]{String.valueOf(gid)});
				helper.getWritableDatabase().close();
				helper.close();
				
				GameOverlayOperation.getGameOverlay().getItem(counter)
					.setMarker(GameOverlayOperation.getGameOverlay().getMarker(R.drawable.goal_gold));
				//Make a request data string
				String updateData = "";				
				for (Goal goal : GameData.getDiscoveredList()) {
					if (goal.getState()==2) {
						updateData += goal.getgID() + "%";
					}
				}
				// Update check-in state with server DB
				UpdateProgressAsyncTask task = new UpdateProgressAsyncTask();
				task.execute(new String[] { updateData.substring(0,
							updateData.length() - 1) });
								
				Toast.makeText(Checkin.this, tGoal.getTitle() + " visited.",
						Toast.LENGTH_LONG).show();

				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	};

	OnClickListener onBack = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}

	};

	private class LoadImage extends AsyncTask<String, Void, Bitmap> {
		ProgressDialog mProgressDialog;
		private String url;
		private final WeakReference<ImageView> imageViewReference;

		public LoadImage(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		protected Bitmap doInBackground(String... params) {
			url = params[0];
			try {
				if (isInternetOn()) {
					// Retrieve image from auto-generated URL
					Bitmap bitmap = BitmapFactory.decodeStream(new URL(url)
							.openConnection().getInputStream());
					// Scale original photo according to screen and maintain
					// original ratio
					Bitmap scaledBitmap = null;
					if (bitmap != null) {
						double oldy = bitmap.getHeight();
						double oldx = bitmap.getWidth();
						double ratio = oldy / oldx;

						// find the width and height of the screen:
						Display d = getWindowManager().getDefaultDisplay();
						int y = d.getWidth();
						int x = (int) (Math.round((double) y * ratio));

						scaledBitmap = Bitmap.createScaledBitmap(bitmap, y, x,
								true);
					}
					return scaledBitmap;

				} else { // No internet so cannot get image from web
					return null;
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		protected void onPostExecute(Bitmap result) {
			mProgressDialog.dismiss();
			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					imageView.setImageBitmap(result);
				}
			}
		}

		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(Checkin.this, "Loading...",
					"Please wait while loading image from Database...");
			// The default image
			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					imageView.setImageResource(R.drawable.trans);
				}
			}
		}
	}

	/**
	 * Load the description from SQLite database
	 * 
	 * @author SteveWho
	 * 
	 */
	private class LoadDescription extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... params) {
			try {
				String description = null;
				if (constantsCursor != null) {
					constantsCursor.moveToFirst();
					// First item should be description according to the sql
					// query
					description = constantsCursor.getString(0);
				}
				return description;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		protected void onPostExecute(String result) {
			if (result != null && !result.isEmpty()) {
				goalDescription.setText(result);
			} else {
				goalDescription.setText("Oops! No description available!");
			}
		}

		protected void onPreExecute() {
			// Create a cursor to execute the database and store resultset
			constantsCursor = dbHelper.getReadableDatabase().rawQuery(
					"SELECT description FROM t_goals where goal_id = " + gid,
					null);
		}

	}

	public void onPanuse() {
		super.onPause();

	}

	/**
	 * Close database connection
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (constantsCursor != null)
			constantsCursor.close();
		if (dbHelper != null)
			dbHelper.close();
	}

	/**
	 * Check both wifi and 3G connection
	 */
	private boolean isInternetOn() {
		ConnectivityManager connMgr = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return (wifi.isConnected() || mobile.isConnected());
	}

	
	/**
	 * Update goal's state with DB on server
	 * 
	 * @author LeoMan
	 */
	private class UpdateProgressAsyncTask extends
			AsyncTask<String, Void, String> {
		ProgressDialog mProgressDialog;

		@Override
		protected void onPostExecute(String result) {
			mProgressDialog.dismiss();
			if (result == null) {
				Toast.makeText(
						Checkin.this,
						"Cannot access the server. \nPlease make sure your WI-FI is on.",
						Toast.LENGTH_LONG).show();
			} else if (result.equals("Invalid")) {
				Toast.makeText(Checkin.this, "Invalid Request.",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(Checkin.this, "Update successfully!", Toast.LENGTH_LONG)
						.show();
				finish();
			}
		}
		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog
					.show(Checkin.this, "Loading...",
							"Please wait while connecting to Database...");
		}
		@Override
		protected String doInBackground(String... u) {
			URL url = null;
			HttpURLConnection httpConn = null;
			String returnStr = "null";
			try {
				url = new URL(UPD_PROG_URL);
				httpConn = (HttpURLConnection) url.openConnection();
				httpConn.setDoOutput(true);
				httpConn.setRequestMethod("POST");
				// Do post request.
				DataOutputStream out = new DataOutputStream(
						httpConn.getOutputStream());
				out.writeUTF("<UPDATE>");
				out.writeUTF(Integer.toString(GameData.getCurUser().getuID()));
				//System.out.println(Integer.toString(GameData.getCurUser()
				//		.getuID()));
				out.writeUTF(u[0]);
				out.flush();
				out.close();
				// Receiving response from server
				DataInputStream in = new DataInputStream(
						httpConn.getInputStream());
				returnStr = in.readUTF();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return returnStr;
		}
	}
}
