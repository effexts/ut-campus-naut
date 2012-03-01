package ut.ece1778.campusnaut;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
/**
 * This activity allows the user to create a account on local datastore
 * and MySQL database.
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class CreateAccount extends Activity {
    private static final String[] AGE_GROUP = {"1 - 10", "11 - 17", "18 - 23",
        "24 - 30", "31 - 40", "41 - 50", "50 - 64", "65+"};
    private static final String[] SALARY_RANGE = {"No Income", "$10,000 - $30,000",
        "$30,000 - $50,000", "$50,000 - $80,000",
        "$80,000 - $100,000", "$100,000+"};
    private String selectedAge = AGE_GROUP[0];
    private String selectedSalary = SALARY_RANGE[0];

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        // Create a spinner for age group
        Spinner spinAge = (Spinner) findViewById(R.id.agespinner);
        spinAge.setOnItemSelectedListener(onAgeChose);

        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                AGE_GROUP);

        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAge.setAdapter(ageAdapter);

        // Create a spinner for salary range
        Spinner spinSalary = (Spinner) findViewById(R.id.salaryspinner);
        spinSalary.setOnItemSelectedListener(onSalaryChose);

        ArrayAdapter<String> salaryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                SALARY_RANGE);

        salaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinSalary.setAdapter(salaryAdapter);

        // Create account button
        Button creatAccButton = (Button) findViewById(R.id.buttonCreate);
        creatAccButton.setOnClickListener(onBtnClick);
    }
    
    /**
     * Handle the event after user click on Create button
     */
    private OnClickListener onBtnClick = new OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(CreateAccount.this, "Account Created Successfully", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    };
    
    /**
     * Listener for age drop down spinner
     */
    private OnItemSelectedListener onAgeChose = new OnItemSelectedListener() {

        public void onItemSelected(AdapterView<?> parent,
                View v, int position, long id) {
            selectedAge = AGE_GROUP[position];
            selectedAge = selectedAge + ""; // to get rid of warning lol
        }

        public void onNothingSelected(AdapterView<?> parent) {
            //
        }
    };
    
    /**
     * Listener for salary drop down spinner
     */
    private OnItemSelectedListener onSalaryChose = new OnItemSelectedListener() {

        public void onItemSelected(AdapterView<?> parent,
                View v, int position, long id) {
            selectedSalary = SALARY_RANGE[position];
            selectedAge = selectedSalary + ""; // to get rid of warning lol
        }

        public void onNothingSelected(AdapterView<?> parent) {
            //
        }
    };
}
