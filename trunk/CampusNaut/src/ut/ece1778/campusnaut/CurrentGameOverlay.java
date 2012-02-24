package ut.ece1778.campusnaut;

import java.util.ArrayList;
import java.util.List;

import ut.ece1778.bean.Game;
import ut.ece1778.bean.Goal;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class CurrentGameOverlay extends ItemizedOverlay<OverlayItem>{

	private Context context;
	private LinearLayout checkinLayout;
	private TextView goalTitle;
	private Animation myAnimation_Alpha;
	private List<OverlayItem> items = new ArrayList<OverlayItem>();
	//private Game game;
	
	

	public CurrentGameOverlay(Context context ,Drawable marker,
			LinearLayout checkinLayout,TextView goalTitle, Game game) {
		super(marker);
		this.context = context;
		this.checkinLayout = checkinLayout;
		this.goalTitle = goalTitle;
		//this.game = game;
		// TODO Auto-generated constructor stub
		boundCenterBottom(marker);
		ArrayList<Goal> goals = game.getGoals();
		
		for(int i = 0;i < goals.size();i++){
			items.add(new OverlayItem(new GeoPoint(goals.get(i).getGoalLat(),goals.get(i).getGoalLon())
			,goals.get(i).getGoalTitle(),""));
		}
		/*
		items.add(new OverlayItem(new GeoPoint(43664300,-79399400)
			,"Roberts Library","ALable2"));
		items.add(new OverlayItem(new GeoPoint(43662700,-79401200)
			,"Athlete Centre","BLable2"));
		items.add(new OverlayItem(new GeoPoint(43663500,-79401500)
			,"Grad House","CLable2"));
		*/
		
		populate();
		
		
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return (items.get(i));
	}

	@Override
	public int size() {
		return(items.size());
	}
	
	
    protected boolean onTap(int i) {

    	if(checkinLayout.getVisibility() == 4){
			
    		checkinLayout.setVisibility(0);
			myAnimation_Alpha = AnimationUtils.loadAnimation(this.context,R.anim.my_alpha_action);
			checkinLayout.startAnimation(myAnimation_Alpha);
			
			goalTitle.setText(" "+items.get(i).getTitle());
		}else{
			checkinLayout.setVisibility(4);
		}
      return( true);
    }
    
    public List<OverlayItem> getItems() {
		return items;
	}

	public void setItems(List<OverlayItem> items) {
		this.items = items;
	}
	
}
