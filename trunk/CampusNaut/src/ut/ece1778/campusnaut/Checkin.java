package ut.ece1778.campusnaut;

import ut.ece1778.bean.GameData;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;



public class Checkin extends Activity{

	private Button ck;
	private Button bk;

	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.checkin);
	        
	        ck = (Button)findViewById(R.id.CK);
	        bk = (Button)findViewById(R.id.BK);
	        
	        ck.setOnClickListener(onCheckin);
	        bk.setOnClickListener(onBack);
	        
	        
	        
	 }
	 
	 OnClickListener onCheckin = new OnClickListener(){

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Bundle bundle = getIntent().getExtras();
	        int gid = bundle.getInt("focus");
	        Toast.makeText(Checkin.this,
	        		GameData.getGameList().get(0).getGoals().get(gid).getTitle()+" visited." , Toast.LENGTH_LONG).show();
	        GameData.getGameList().get(0).getGoals().remove(gid);
			
			finish();
		}
		 
	 };
	 
	 OnClickListener onBack = new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			 
		 };
	 
	
}
