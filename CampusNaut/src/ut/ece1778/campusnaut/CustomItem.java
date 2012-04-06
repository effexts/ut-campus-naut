package ut.ece1778.campusnaut;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
/**
 * Override com.google.android.maps.OverlayItem
 * @author LeoMan
 *
 */
public class CustomItem extends OverlayItem{
	Drawable marker=null;
    
	/**
	 * Override Constructor
	 * @param pt
	 * @param name
	 * @param snippet
	 * @param marker
	 */
    CustomItem(GeoPoint pt, String name, String snippet,
               Drawable marker) {
      super(pt, name, snippet);
      
      this.marker=marker;
    }
    
    /**
     *Get marker by resource id 
     */
    @Override
    public Drawable getMarker(int stateBitset) {
      
      setState(marker, stateBitset);
    
      return(marker);
    }

    /**
     * Set a drawable as current marker 
     */
	public void setMarker(Drawable marker) {
		// TODO Auto-generated method stub
		 
		this.marker = marker;
		
		
	}
}
