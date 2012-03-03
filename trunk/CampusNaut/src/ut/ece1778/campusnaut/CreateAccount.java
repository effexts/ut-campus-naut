package ut.ece1778.campusnaut;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

    private static final String[] AGE_GROUP = {"1 - 10", "11 - 17", "18 - 23",
        "24 - 30", "31 - 40", "41 - 50", "50 - 64", "65+"};
    private String selectedAge = AGE_GROUP[0];
    private String gender = "Male";
    private SharedPreferences prefs = null;
    private Editor editor = null;

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

        // Create a spinner for age group
        Spinner spinAge = (Spinner) findViewById(R.id.agespinner);
        spinAge.setOnItemSelectedListener(onAgeChose);

        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                AGE_GROUP);

        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAge.setAdapter(ageAdapter);

        // Create account button
        Button creatAccButton = (Button) findViewById(R.id.buttonCreate);
        creatAccButton.setOnClickListener(onBtnClick);

        RadioGroup genderRadio = (RadioGroup) findViewById(R.id.gender);
        genderRadio.setOnCheckedChangeListener(onGenderChec);
    }
    /**
     * Handle the event after user click on Create button
     */
    private OnClickListener onBtnClick = new OnClickListener() {

        public void onClick(View v) {
            EditText name = (EditText) findViewById(R.id.accnameinput);
            EditText email = (EditText) findViewById(R.id.emailinput);
            EditText passwd = (EditText) findViewById(R.id.pwinput);

            editor.putString("username", name.getText().toString());
            editor.putString("email", email.getText().toString());
            editor.putString("password", passwd.getText().toString());
            editor.putString("age", selectedAge);
            editor.putString("gender", gender);
            editor.putBoolean("loggedin", true);
            editor.commit();
            
            // ***NOTE*** Upload the account information to MySQL database here.
            Toast.makeText(CreateAccount.this, "Account Created Successfully", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), GameMapView.class));
        }
    };
    /**
     * Listener for age drop down spinner
     */
    private OnItemSelectedListener onAgeChose = new OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent,
                View v, int position, long id) {
            selectedAge = AGE_GROUP[position];
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
     * Since we disable history for LoginScreen, need to
     * override the back button to start the LoginScreen activity again
     */
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), LoginScreen.class));
    }
}
