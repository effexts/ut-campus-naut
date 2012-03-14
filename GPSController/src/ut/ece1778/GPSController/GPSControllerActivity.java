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

public class GPSControllerActivity extends Activity {
	private final String OUR_URL = "http://ec2-184-73-31-146.compute-1.amazonaws.com:8080/CampusNaut/servlet/GPSInjection";

	// Start location which is near to where we do our presentation
	private static double longitude = 43.660887;
	private static double laptitude = -79.398547;
	private static final double RANGE = 0.000500;

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
	}

	private OnClickListener onLeftClick = new OnClickListener() {

		public void onClick(View v) {
			// ***NOTE*** Upload the account information to MySQL database here.
			NetAsyncTask task = new NetAsyncTask();
			task.execute(new String[] { "left" });
		}
	};
	private OnClickListener onRightClick = new OnClickListener() {

		public void onClick(View v) {
			// ***NOTE*** Upload the account information to MySQL database here.
			NetAsyncTask task = new NetAsyncTask();
			task.execute(new String[] { "right" });
		}
	};
	private OnClickListener onDownClick = new OnClickListener() {

		public void onClick(View v) {
			// ***NOTE*** Upload the account information to MySQL database here.
			NetAsyncTask task = new NetAsyncTask();
			task.execute(new String[] { "down" });
		}
	};
	private OnClickListener onTopClick = new OnClickListener() {

		public void onClick(View v) {
			// ***NOTE*** Upload the account information to MySQL database here.
			NetAsyncTask task = new NetAsyncTask();
			task.execute(new String[] { "top" });
		}
	};

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
				}
				DecimalFormat myFormatter = new DecimalFormat("#0.0000000");
				String output = myFormatter.format(longitude) + ","
						+ myFormatter.format(laptitude);
				// Do post request.
				DataOutputStream out = new DataOutputStream(
						httpConn.getOutputStream());
				out.writeUTF(output);
				out.flush();
				out.close();
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