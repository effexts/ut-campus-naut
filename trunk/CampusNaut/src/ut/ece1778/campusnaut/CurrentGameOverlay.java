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
	
	private List<OverlayItem> items = new ArrayList<OverlayItem>();
	private ArrayList<Goal> goals = new ArrayList<Goal>();
	private Location location;
	//private Game game;
	public static final double VISUAL_FIELD = 80.0; // unit meter. any goal out of this range will not be shown.
	
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
	public CurrentGameOverlay(Context context ,Drawable marker,Game game) {
		super(marker);
		this.context = context;
		boundCenterBottom(marker);
		goals = game.getGoals();

		populate();
	}

	/**
	 * Unused for now
	 * @param myLocation
	 */
	public void setMyLocation(GeoPoint myLocation){
	
		location = new Location("myLocation");
		location.setLatitude(myLocation.getLatitudeE6()*1E-6);
		location.setLongitude(myLocation.getLongitudeE6()*1E-6);
		
	}
	
	/**
	 * Load new goals into overlay items
	 * as well as discoveredList  
	 * @param myLocation
	 */
	public void loadItem( GeoPoint myLocation){
		location = new Location("myLocation");
		location.setLatitude(myLocation.getLatitudeE6()*1E-6);
		location.setLongitude(myLocation.getLongitudeE6()*1E-6);
		
		//System.out.println("SSSSSSSIIIIIIIIIIIIIIIZZZZZZZZZZZZEEEEEEEE-----"+goals.size());
		boolean flag = false;
		for(Goal sGoal : goals){
			//calculate distance between the goal and my location
			sGoal.calculateDistance(location);
			//if goal in range and never been added to discovered list then add onto map
			if(sGoal.getDistance() < VISUAL_FIELD){
				
				//if discovered list is empty, add goal directly
				if(GameData.getDiscoveredList().size() ==0){
					flag = true;
				}else{
					flag = true;
					for(Goal dGoal : GameData.getDiscoveredList()){
						//find if goal already been added to discovered list
						if(dGoal.getgID() == sGoal.getgID()){
							flag = false;
						}
					}
				}
			}else{
				flag = false;
			}
			if(flag){
				GameData.getDiscoveredList().add(sGoal);
				items.add(new OverlayItem(sGoal.getGeoPoint(),sGoal.getTitle(),""+sGoal.getgID()));
				System.out.println("DISCOVERED-------"+GameData.getDiscoveredList().size());
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

    	Goal tapGoal = GameData.getDiscoveredList().get(i);
    	tapGoal.calculateDistance(location);
    	
    	Intent intent = new Intent(context, Checkin.class);
    	intent.putExtra("focus", Integer.parseInt(items.get(i).getSnippet()));   	
    	if(tapGoal.getDistance() < VISUAL_FIELD){
    		intent.putExtra("inRange", 1);
    	}else{
    		intent.putExtra("inRange", 0);
    	}
    	
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
