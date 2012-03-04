package ut.ece1778.campusnaut;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This activity allows the user to login with account. The activity is skipped
 * if the user has previously logged in.
 *
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class LoginScreen extends Activity {

    private SharedPreferences prefs = null; //Used for local datastore
    private Editor editor = null;
    private String email = null;
    private String passwd = null;
    private EditText emailinput;
    private EditText passwdinput;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve local datastore
        prefs = getSharedPreferences("UserDataStorage", MODE_PRIVATE);
        
        // Skip to map view if user already login
        final boolean loggedin = prefs.getBoolean("loggedin", false);
        if (loggedin) {
            startActivity(new Intent(LoginScreen.this, GameMapView.class));
        }
        setContentView(R.layout.login);
        
        // editor is used to modify the loggedin state later
        editor = prefs.edit();
        // load email and password from data store
        email = prefs.getString("email", null);
        passwd = prefs.getString("password", null);
        
        Button loginButton = (Button) findViewById(R.id.buttonlogin);
        loginButton.setOnClickListener(onLoginClick);

        Button createAccButton = (Button) findViewById(R.id.buttonsignup);
        createAccButton.setOnClickListener(onCreateClick);
        
        // Automatically fill out the email for user
        if (email != null) {
        	// Don show the create account button if user datastore is not empty
            createAccButton.setVisibility(View.INVISIBLE);
            EditText emailinput = (EditText) findViewById(R.id.emailinput);
            emailinput.setText(email);
        }

    }
    /**
     * Handle the event after user click on Login button
     */
    private OnClickListener onLoginClick = new OnClickListener() {
        public void onClick(View v) {
            if (email != null && passwd != null) {// email and password exists in local datastore 
                emailinput = (EditText) findViewById(R.id.emailinput);
                passwdinput = (EditText) findViewById(R.id.pwinput);
                // perform account validation and modify logged in state to true
                if (email.equals(emailinput.getText().toString()) && passwd.equals(passwdinput.getText().toString())) {
                    editor.putBoolean("loggedin", true);
                    editor.commit();
                    startActivity(new Intent(LoginScreen.this, GameMapView.class));
                } else if (!email.equals(emailinput.getText().toString())) { // Email does not match local stored one 
                	// ***NOTE*** can validate email with MySQL database here.
                    Toast.makeText(LoginScreen.this, "Email address is not registered!", Toast.LENGTH_SHORT).show();
                } else if (!passwd.equals(passwdinput.getText().toString())) { // Invalid password
                    Toast.makeText(LoginScreen.this, "Wrong password!", Toast.LENGTH_SHORT).show();
                }
            } else { // ***NOTE*** Add the account validation with MySQL database feature here.
            	
            	emailinput = (EditText) findViewById(R.id.emailinput);
                passwdinput = (EditText) findViewById(R.id.pwinput);
            	LoginAsyncTask task = new LoginAsyncTask();
                task.execute(new String[]{"http://ec2-184-73-31-146.compute-1.amazonaws.com:8080/CampusNaut/servlet/AccountValidation"});
                         	
            }

        }
    };
    
    /**
     * AsyncTask to sent create account request to server.
     *
     */
    private class LoginAsyncTask extends AsyncTask<String, Void, String>
    {
        ProgressDialog mProgressDialog;
        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.dismiss();
            if(result.equals(null)){
            	Toast.makeText(LoginScreen.this, "Cannot access to server.", Toast.LENGTH_LONG).show();
            }else if(result.equals("Failed") ){
            	Toast.makeText(LoginScreen.this, "Please create an account first!", Toast.LENGTH_SHORT).show();   
            }else{
            	Toast.makeText(LoginScreen.this, result, Toast.LENGTH_LONG).show();     
            	startActivity(new Intent(getApplicationContext(), GameMapView.class));
            }
        }
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(LoginScreen.this, "Loading...", "Data is Loading...");
        }
        @Override
        protected String doInBackground(String... u) {
        	URL url = null;
            HttpURLConnection httpConn = null;
            String returnStr = "null";
            try{
            	url = new URL(u[0]);
            	httpConn = (HttpURLConnection)url.openConnection();
            	httpConn.setDoOutput(true);
            	httpConn.setRequestMethod("POST");
            	//Do post request.
            	DataOutputStream out = new DataOutputStream(httpConn.getOutputStream());         	
            	out.writeUTF(emailinput.getText().toString());
            	out.writeUTF(passwdinput.getText().toString());          	
            	out.flush();
            	out.close();

            	//Receiving response from server
            	DataInputStream in = new DataInputStream(httpConn.getInputStream());  
            	     returnStr = in.readUTF();
         	     
            }catch(Exception e){
            	e.printStackTrace();
            }
            return returnStr;
        }
    }
    
    /**
     * Handle the event after user click on Create button
     */
    private OnClickListener onCreateClick = new OnClickListener() {

        public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(), CreateAccount.class));
        }
    };
}