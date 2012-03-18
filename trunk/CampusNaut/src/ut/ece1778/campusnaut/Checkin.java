package ut.ece1778.campusnaut;

import ut.ece1778.bean.GameData;
import ut.ece1778.bean.Goal;
import android.app.Activity;
import android.os.Bundle;
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

	private Button checkin;
	private Button back;
	private TextView checkinTitle;
	private ImageView checkinPic;
	private int gid;
	private int inRange;
	private Goal tGoal = new Goal();

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

		// Get values passed from Main Activity
		Bundle bundle = getIntent().getExtras();
		gid = bundle.getInt("focus");
		inRange = bundle.getInt("inRange");

		// Get selected Goal Object
		tGoal = GameData.findGoalById(gid, GameData.getDiscoveredList());
		// Check the goal's state and whether in range to be able to check-in
		if (inRange == 0 && !tGoal.getState()) {
			// Out of range ,disable the check in button
			checkin.setEnabled(false);
		} else if (inRange == 1 && !tGoal.getState()) {
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
		checkinPic.setImageResource(R.drawable.user_icon);
	}

	/**
	 * On Click to Check in
	 */
	OnClickListener onCheckin = new OnClickListener() {

		public void onClick(View v) {

			try {
				// update Goal Lists in GameData
				GameData.updateState(gid);
				Toast.makeText(Checkin.this, tGoal.getTitle() + " visited.",
						Toast.LENGTH_LONG).show();

				finish();
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

}
