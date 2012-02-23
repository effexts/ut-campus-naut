package ut.ece1778.campusnaut;

import java.util.ArrayList;
import java.util.List;

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
	
	public CurrentGameOverlay(Context context ,Drawable marker,
			LinearLayout checkinLayout,TextView goalTitle) {
		super(marker);
		this.context = context;
		this.checkinLayout = checkinLayout;
		this.goalTitle = goalTitle;
		// TODO Auto-generated constructor stub
		boundCenterBottom(marker);
		
		items.add(new OverlayItem(new GeoPoint((int)(43663846.65313048),(int)(-79399258.94474051))
			,"ALable1","ALable2"));
		items.add(new OverlayItem(new GeoPoint((int)(43664800.38890235),(int)(-79398200.84819761))
			,"BLable1","BLable2"));
		items.add(new OverlayItem(new GeoPoint(43663347,-79399759)
			,"CLable1","CLable2"));
		
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
	
}
