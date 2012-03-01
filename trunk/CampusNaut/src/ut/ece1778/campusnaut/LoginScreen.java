package ut.ece1778.campusnaut;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
/**
 * This activity allows the user to login with account. 
 * The activity is skipped if the user has previously
 * logged in.
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class LoginScreen extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        Button loginButton = (Button)findViewById(R.id.buttonlogin);
        loginButton.setOnClickListener(onLoginClick);
        
        Button creatAccButton = (Button)findViewById(R.id.buttonsignup);
        creatAccButton.setOnClickListener(onCreateClick);
    }
    
    /**
     * Handle the event after user click on Login button
     */
    private OnClickListener onLoginClick = new OnClickListener() {
        public void onClick(View v) {
        	startActivity(new Intent(LoginScreen.this,GameMapView.class));
        }
    };
    
    /**
     * Handle the event after user click on Create button
     */
    private OnClickListener onCreateClick = new OnClickListener() {
        public void onClick(View v) {
        	startActivity(new Intent(LoginScreen.this,CreateAccount.class));
        }
    };
    
    
}