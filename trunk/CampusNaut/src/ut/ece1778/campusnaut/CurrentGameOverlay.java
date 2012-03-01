package ut.ece1778.campusnaut;

import java.util.ArrayList;
import java.util.List;

import ut.ece1778.bean.Game;
import ut.ece1778.bean.GameData;
import ut.ece1778.bean.Goal;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * Customized ItemizedOverlay<OverlayItem>
 * Set VISUAL_FIELD to determine the maximum distance of visible goals
 *@author  Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class CurrentGameOverlay extends ItemizedOverlay<OverlayItem>{

	private Context context;
	private LinearLayout checkinLayout;
	private TextView goalTitle;
	private Animation myAnimation_Alpha;
	private List<OverlayItem> items = new ArrayList<OverlayItem>();
	ArrayList<Goal> goals = new ArrayList<Goal>();
	Location location;
	//private Game game;
	public static final double VISUAL_FIELD = 100.0; // unit meter. any goal out of this range will not be shown.
	
	/**
	 * CurrentGameOverlay constructor
	 * create a new layer contains visible goals
	 * @param context
	 * @param marker
	 * @param checkinLayout
	 * @param goalTitle
	 * @param game
	 * @param myLocation
	 */
	public CurrentGameOverlay(Context context ,Drawable marker,
			LinearLayout checkinLayout,TextView goalTitle, Game game , GeoPoint myLocation) {
		super(marker);
		this.context = context;
		this.checkinLayout = checkinLayout;
		this.goalTitle = goalTitle;
		//this.game = game;
		boundCenterBottom(marker);
		goals = game.getGoals();
		location = new Location("myLocation");
		location.setLatitude(myLocation.getLatitudeE6()*1E-6);
		location.setLongitude(myLocation.getLongitudeE6()*1E-6);
		//add nearby goals(within VISUAL_FIELD) into layer items
		for(int i = 0;i < goals.size();i++){	
			
			goals.get(i).calculateDistance(location);
			if(goals.get(i).getDistance() < VISUAL_FIELD ){
				items.add(new OverlayItem(goals.get(i).getGeoPoint(),goals.get(i).getTitle(),""+i));
			}
		}
		populate();
	}

	public void loadItem( GeoPoint myLocation){
		for(int i = 0;i < goals.size();i++){	
			location.setLatitude(myLocation.getLatitudeE6()*1E-6);
			location.setLongitude(myLocation.getLongitudeE6()*1E-6);
			goals.get(i).calculateDistance(location);
			if(goals.get(i).getDistance() < VISUAL_FIELD &&
					goals.get(i).getStates() == 1){
				items.add(new OverlayItem(goals.get(i).getGeoPoint(),goals.get(i).getTitle(),""+i));
			}
			
		}
		populate();
	}
	//remove all items
	public void removeAll(){
		
		if( this.size()>0 ){
				items.clear();			
			
		}
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
	
	/**
	 * show check-in panel when user tap on a goal's marker
	 * @see com.google.android.maps.ItemizedOverlay#onTap(int)
	 */
    protected boolean onTap(int i) {
/*
    	if(checkinLayout.getVisibility() == 4){
			//show panel
    		checkinLayout.setVisibility(0);
    		//load alpha and scale animations
			myAnimation_Alpha = AnimationUtils.loadAnimation(this.context,R.anim.my_alpha_action);
			checkinLayout.startAnimation(myAnimation_Alpha);
			//set panel title
			goalTitle.setText(" "+items.get(i).getTitle());
		}else{
			checkinLayout.setVisibility(4);
		}*/
    	//GameData.getGameList().get(0).getGoals().remove(Integer.parseInt(items.get(i).getSnippet()));
    	Intent intent = new Intent(context, Checkin.class);
    	intent.putExtra("focus", Integer.parseInt(items.get(i).getSnippet()));
    	context.startActivity(intent);
    	
      return( true);
    }
    
    public List<OverlayItem> getItems() {
		return items;
	}

	public void setItems(List<OverlayItem> items) {
		this.items = items;
	}
	
}
