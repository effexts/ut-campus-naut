package ut.ece1778.campusnaut;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ut.ece1778.bean.GameData;
import ut.ece1778.bean.User;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * This activity allows the user to create a account on local datastore and
 * MySQL database.
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class CreateAccount extends Activity {
	private static final String CREATE_ACC_URL = "http://ec2-184-73-31-146.compute-1.amazonaws.com:8080/CampusNaut/servlet/CreateAccount";
	private static final String[] ETHNICITY = {"Aboriginal origins",
		"African origins",
		"Arab origins",
		"British Isles origins",
		"Caribbean origins",
		"East and Southeast Asian origins",
		"European origins",
		"French origins",
		"Latin, Central and South American origins",
		"Oceania origins",
		"South Asian origins",
		"West Asian origins"
 };
	
	private String selectedEthnicity = ETHNICITY[0];
	private String gender = "Male";
	private SharedPreferences prefs = null;
	private Editor editor = null;
	private EditText name;
	private EditText email;
	private EditText passwd;
	private EditText age;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account);
		// Retrieve local datastore
		prefs = getSharedPreferences("UserDataStorage", MODE_PRIVATE);
		// editor is used to modify the local datastore
		editor = prefs.edit();

		// Create a spinner for ethinity group
		Spinner spinEthnicity = (Spinner) findViewById(R.id.ethnicityspinner);
		spinEthnicity.setOnItemSelectedListener(onEthnicityChose);

		ArrayAdapter<String> ethnicityAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, ETHNICITY);

		ethnicityAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinEthnicity.setAdapter(ethnicityAdapter);

		// Create account button
		Button creatAccButton = (Button) findViewById(R.id.buttonCreate);
		creatAccButton.setOnClickListener(onBtnClick);

		RadioGroup genderRadio = (RadioGroup) findViewById(R.id.gender);
		genderRadio.setOnCheckedChangeListener(onGenderChec);

		name = (EditText) findViewById(R.id.accnameinput);
		email = (EditText) findViewById(R.id.emailinput);
		passwd = (EditText) findViewById(R.id.pwinput);
		age = (EditText) findViewById(R.id.ageinput);
	}

	/**
	 * Handle the event after user click on Create button
	 */
	private OnClickListener onBtnClick = new OnClickListener() {

		public void onClick(View v) {
			if (isInternetOn()) {
				editor.putString("username", name.getText().toString());
				editor.putString("email", email.getText().toString());
				editor.putString("password", passwd.getText().toString());
				editor.putString("age", age.getText().toString());
				editor.putString("ethnicity", selectedEthnicity);
				editor.putString("gender", gender);
				editor.putBoolean("loggedin", true);
				editor.commit();

				// ***NOTE*** Upload the account information to MySQL database
				// here.
				AccountAsyncTask task = new AccountAsyncTask();
				task.execute();

				// Toast.makeText(CreateAccount.this,
				// "Account Created Successfully", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(
						CreateAccount.this,
						"Failed to connect to CampusNaut Database. "
								+ "\nPlease check your network settings and try again!",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * AsyncTask to sent create account request to server.
	 * 
	 */
	private class AccountAsyncTask extends AsyncTask<String, Void, String> {
		ProgressDialog mProgressDialog;

		@Override
		protected void onPostExecute(String result) {
			mProgressDialog.dismiss();
			if (result == null) {
				Toast.makeText(
						CreateAccount.this,
						"Cannot access the server. \nPlease make sure your WI-FI is on.",
						Toast.LENGTH_LONG).show();
			} else if (result.equals("Failed")) {
				Toast.makeText(CreateAccount.this,
						"Username is taken!\nPlease enter another username.",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(CreateAccount.this, result, Toast.LENGTH_LONG)
						.show();
				GameData.setFirstTime(true);
				startActivity(new Intent(getApplicationContext(),
						GoalPicker.class));
			}
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog
					.show(CreateAccount.this, "Loading...",
							"Please wait while connecting to Database...");
		}

		@Override
		protected String doInBackground(String... u) {
			URL url = null;
			HttpURLConnection httpConn = null;
			String returnStr = "null";
			try {
				url = new URL(CREATE_ACC_URL);
				httpConn = (HttpURLConnection) url.openConnection();
				httpConn.setDoOutput(true);
				httpConn.setRequestMethod("POST");
				// Do post request.
				DataOutputStream out = new DataOutputStream(
						httpConn.getOutputStream());
				out.writeUTF(name.getText().toString());
				out.writeUTF(email.getText().toString());
				out.writeUTF(passwd.getText().toString());
				out.writeUTF(age.getText().toString());
				out.writeUTF(gender);
				out.writeUTF(selectedEthnicity);
				out.flush();
				out.close();

				// Receiving response from server
				DataInputStream in = new DataInputStream(
						httpConn.getInputStream());
				returnStr = in.readUTF();
				User curUser = new User();
				int uID = Integer.parseInt(in.readUTF());
				System.out.println("CurRRRRRRRRRRRRUUUUUUUUUUUUU"+uID);
				curUser.setuID(uID);
				GameData.setCurUser(curUser);
				
				editor.putInt("user_id",uID );
				editor.commit();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return returnStr;
		}
	}

	/**
	 * Listener for age drop down spinner
	 */
	private OnItemSelectedListener onEthnicityChose = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View v, int position,
				long id) {
			selectedEthnicity = ETHNICITY[position];
		}

		public void onNothingSelected(AdapterView<?> parent) {
			//
		}
	};

	/**
	 * Listener for gender radio button
	 */
	private OnCheckedChangeListener onGenderChec = new OnCheckedChangeListener() {

		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.male:
				gender = "Male";
				break;
			case R.id.female:
				gender = "Female";
				break;
			}
		}
	};

	/**
	 * Since we disable history for LoginScreen, need to override the back
	 * button to start the LoginScreen activity again
	 */
	@Override
	public void onBackPressed() {
		startActivity(new Intent(getApplicationContext(), LoginScreen.class));
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
}
