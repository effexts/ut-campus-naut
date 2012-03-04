package ut.ece1778.campusnaut;

import ut.ece1778.bean.GameData;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class Checkin extends Activity{

	private Button checkin;
	private Button back;
	private TextView checkinTitle;
	private int gid;

	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.checkin);
	        
	        checkin = (Button)findViewById(R.id.CK);
	        back = (Button)findViewById(R.id.BK);
	         checkin.setOnClickListener(onCheckin);
	        back.setOnClickListener(onBack);
	        checkinTitle = (TextView)findViewById(R.id.checkinTitle);
	        
	        Bundle bundle = getIntent().getExtras();
	        gid = bundle.getInt("focus");
	        
	        checkinTitle.setText(GameData.getGameList().get(0).getGoals().get(gid).getTitle());
	 }
	 
	 OnClickListener onCheckin = new OnClickListener(){

		public void onClick(View v) {
			// TODO Auto-generated method stub
			try{
		        Toast.makeText(Checkin.this,
		        		GameData.getGameList().get(0).getGoals().get(gid).getTitle()+" visited." , Toast.LENGTH_LONG).show();
		        GameData.getGameList().get(0).getGoals().remove(gid);
				GameData.setScores(GameData.getScores()+50);
				GameData.setDetector(GameData.getDetector()-1);
				finish();
				}catch(Exception e){
					e.printStackTrace();
				}
			
		}
		 
	 };
	 
	 OnClickListener onBack = new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			 
		 };
	 
	
}
