package ut.ece1778.GPSController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * Test harness which allows the user to modify the Mock GPS coordinate to
 * move the user icon in the CampusNaut application.
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class GPSControllerActivity extends Activity {
	// Servlet URL for modifying the Mock GPS coordinate on the backend.
	private final String OUR_URL = "http://ec2-184-73-31-146.compute-1.amazonaws.com:8080/CampusNaut/servlet/GPSInjection";
	// Start location for doing presentation
	private static double longitude = 43.6612420;
	private static double laptitude = -79.3979140;
	
	// Moving distance for one click
	private static final double RANGE = 0.000500;
	// Default user
	private static String user="steve";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button leftButton = (Button) findViewById(R.id.left);
		leftButton.setOnClickListener(onLeftClick);
		Button rightButton = (Button) findViewById(R.id.right);
		rightButton.setOnClickListener(onRightClick);
		Button topButton = (Button) findViewById(R.id.top);
		topButton.setOnClickListener(onTopClick);
		Button downButton = (Button) findViewById(R.id.down);
		downButton.setOnClickListener(onDownClick);
		Button resetButton = (Button) findViewById(R.id.reset);
		resetButton.setOnClickListener(onResetClick);
		RadioGroup userRadio = (RadioGroup) findViewById(R.id.user);
		userRadio.setOnCheckedChangeListener(onUserChec);
	}
    /**
     * Supports two individual users to control two different mock location.
     */
    private OnCheckedChangeListener onUserChec = new OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.steve:
                    user = "steve";
                    break;
                case R.id.leo:
                    user = "leo";
                    break;
            }
        }
    };

	private OnClickListener onLeftClick = new OnClickListener() {
		public void onClick(View v) {
			NetAsyncTask task = new NetAsyncTask();
			task.execute(new String[] { "left" });
		}
	};
	private OnClickListener onRightClick = new OnClickListener() {
		public void onClick(View v) {
			NetAsyncTask task = new NetAsyncTask();
			task.execute(new String[] { "right" });
		}
	};
	private OnClickListener onDownClick = new OnClickListener() {
		public void onClick(View v) {
			NetAsyncTask task = new NetAsyncTask();
			task.execute(new String[] { "down" });
		}
	};
	private OnClickListener onTopClick = new OnClickListener() {
		public void onClick(View v) {
			NetAsyncTask task = new NetAsyncTask();
			task.execute(new String[] { "top" });
		}
	};
	private OnClickListener onResetClick = new OnClickListener() {
		public void onClick(View v) {

			NetAsyncTask task = new NetAsyncTask();
			task.execute(new String[] { "reset" });
		}
	};

	/**
	 * Create a background thread to upload the Mock GPS coordinate 
	 * after user pressed any button.
	 */
	private class NetAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... u) {
			URL url = null;
			HttpURLConnection httpConn = null;
			String returnStr = "null";
			try {
				url = new URL(OUR_URL);

				httpConn = (HttpURLConnection) url.openConnection();
				httpConn.setDoOutput(true);
				httpConn.setRequestMethod("POST");
				if (u[0].equals("right")) {
					laptitude += RANGE;
				} else if (u[0].equals("left")) {
					laptitude -= RANGE;
				} else if (u[0].equals("top")) {
					longitude += RANGE;
				} else if (u[0].equals("down")) {
					longitude -= RANGE;
				} else {// Move user icon to default location if reset is pressed
					longitude = 43.6612420;
					laptitude = -79.3979140;
				}
				// Format the GPS coordinate to 7 decimal place
				DecimalFormat myFormatter = new DecimalFormat("#0.0000000");
				String output = myFormatter.format(longitude) + ","
						+ myFormatter.format(laptitude);
				// Do post request to backend.
				DataOutputStream out = new DataOutputStream(
						httpConn.getOutputStream());
				out.writeUTF(user);
				out.writeUTF(output);
				out.flush();
				out.close();
				// Get the response msg from backend.
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