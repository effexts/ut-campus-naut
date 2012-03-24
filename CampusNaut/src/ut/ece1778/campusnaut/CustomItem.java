package ut.ece1778.campusnaut;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class CustomItem extends OverlayItem{
	Drawable marker=null;
    
    CustomItem(GeoPoint pt, String name, String snippet,
               Drawable marker) {
      super(pt, name, snippet);
      
      this.marker=marker;
    }
    
    @Override
    public Drawable getMarker(int stateBitset) {
      
      setState(marker, stateBitset);
    
      return(marker);
    }

	public void setMarker(Drawable marker) {
		// TODO Auto-generated method stub
		 
		this.marker = marker;
		
		
	}
}
