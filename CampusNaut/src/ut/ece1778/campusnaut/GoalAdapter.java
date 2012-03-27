package ut.ece1778.campusnaut;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;

import ut.ece1778.bean.Goal;

/**
 * Custom BaseExpandableLIstAdapter for the goalpicker.
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class GoalAdapter extends BaseExpandableListAdapter {

	@SuppressWarnings("unused")
	private Context context;
	private ArrayList<String> categories;
	private ArrayList<ArrayList<Goal>> goals;
	private LayoutInflater inflater;
	// Used to enforce memorizing the checkbox states.
	private int checkBoxCounter = 0;
	private int checkBoxInitialized = 0;
	static ViewHolder holder;
	static ParentViewHolder parentHolder;
	/**
	 * Constructor for GoalAdapter
	 */
	public GoalAdapter(Context context, ArrayList<String> categories,
			ArrayList<ArrayList<Goal>> goals) {
		this.context = context;
		this.categories = categories;
		this.goals = goals;
		inflater = LayoutInflater.from(context);
	}

	/**
	 * Add a new category to the categories list.
	 */
	public void addToGroup(String category) {
		categories.add(category);
	}

	/**
	 * Add a new goal to a category.
	 */
	public void addToGoal(ArrayList<Goal> goal) {
		goals.add(goal);
	}

	/**
	 * Return the goal based on category index and goal index
	 */
	public Object getChild(int groupPosition, int childPosition) {
		return goals.get(groupPosition).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return (long) (groupPosition * 1024 + childPosition);
	}

	/**
	 * Used to save the ChildView state
	 */
	static class ViewHolder {
		protected TextView text;
		protected CheckBox checkbox;
		//protected TextView selectedText;
	}

	/**
	 * Used to save the ParentView state
	 */
	static class ParentViewHolder {
		protected TextView categoryText;
		protected TextView selectedText;
	}
	/**
	 * Define the view for the 2nd level goals
	 */
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		checkBoxCounter = 0;
		checkBoxInitialized = 0;
		// Current view is empty, inflat and create the view
		if (convertView == null) {
			final ViewHolder viewHolder = new ViewHolder();
			convertView = inflater
					.inflate(R.layout.location_row, parent, false);
			viewHolder.text = (TextView) convertView
					.findViewById(R.id.location);
			viewHolder.checkbox = (CheckBox) convertView
					.findViewById(R.id.checkbox);
			/*viewHolder.selectedText = (TextView) convertView
					.findViewById(R.id.selected);*/
			viewHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						// Update the goal selected state after user click on check
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							Goal element = (Goal) viewHolder.checkbox.getTag();
							element.setSelected(buttonView.isChecked());

							if (checkBoxCounter <= checkBoxInitialized) {
								// increment counter, when we scroll the List it
								// execute onCheckedChanged everytime so by
								// using this stuff we can maintain the state
								checkBoxCounter++;
							} else {
								Goal element2 = (Goal) viewHolder.checkbox
										.getTag();
								element2.setSelected(buttonView.isChecked());

							}
						}
					});
			if (goals.get(groupPosition).get(childPosition).getState() == 1) {
				viewHolder.checkbox.setButtonDrawable(android.R.drawable.btn_star_big_off);
			} else if (goals.get(groupPosition).get(childPosition).getState() == 2) {
				viewHolder.checkbox.setButtonDrawable(android.R.drawable.btn_star_big_on);
			} else {
				viewHolder.checkbox.setButtonDrawable(R.drawable.checkbox);
			}
			convertView.setTag(viewHolder);
			viewHolder.checkbox.setTag(goals.get(groupPosition).get(
					childPosition));
			
		} else {// View already exist, restore the state.
			((ViewHolder) convertView.getTag()).checkbox.setTag(goals.get(
					groupPosition).get(childPosition));
		}

		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		if (goals.get(groupPosition).get(childPosition).getState() == 1) {
			viewHolder.checkbox.setButtonDrawable(android.R.drawable.btn_star_big_off);
		} else if (goals.get(groupPosition).get(childPosition).getState() == 2) {
			viewHolder.checkbox.setButtonDrawable(android.R.drawable.btn_star_big_on);
		} else {
			viewHolder.checkbox.setButtonDrawable(R.drawable.checkbox);
		}
		viewHolder.text.setText(goals.get(groupPosition).get(childPosition)
				.getTitle());
		viewHolder.checkbox.setChecked(goals.get(groupPosition)
				.get(childPosition).getSelected());
		return convertView;
	}

	public int getChildrenCount(int groupPosition) {
		return goals.get(groupPosition).size();
	}

	/**
	 * Count number of goal selected in each category.
	 */
	public int getChildrenSelectedCount(int groupPosition) {
		int count = 0;
		for (int i = 0; i < goals.get(groupPosition).size(); i++) {
			if (goals.get(groupPosition).get(i).getSelected() && goals.get(groupPosition).get(i).getState() == 0)
				count++;
		}
		return count;
	}
	public Object getGroup(int groupPosition) {
		return categories.get(groupPosition);
	}

	public int getGroupCount() {
		return categories.size();
	}

	public long getGroupId(int groupPosition) {
		return (long) (groupPosition * 1024);
	}

	/**
	 * Define the view for the 1st level category
	 */
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) { // Create a new view and remember state
			final ParentViewHolder viewHolder = new ParentViewHolder();
			convertView = inflater.inflate(R.layout.category_row, parent, false);
			viewHolder.categoryText = (TextView) convertView.findViewById(R.id.category);
			viewHolder.selectedText =  (TextView) convertView.findViewById(R.id.selected);
			convertView.setTag(viewHolder);
			viewHolder.categoryText.setText((String) getGroup(groupPosition));
			
			viewHolder.selectedText.setTag(getChildrenSelectedCount(groupPosition));
			String categoryTitle = (String) getGroup(groupPosition);
			
			if (categoryTitle != null) {
				// Only display selected count if it is greater than 0
				if (getChildrenSelectedCount(groupPosition) > 0) {
					viewHolder.categoryText.setText(categoryTitle);
					viewHolder.selectedText.setText(" ("+ getChildrenSelectedCount(groupPosition) + " selected) " );
				}
				else {
					viewHolder.categoryText.setText(categoryTitle);
					viewHolder.selectedText.setText("");
				}
			}
		}
		else { // retrieve state from previous view
			((ParentViewHolder) convertView.getTag()).selectedText.setTag(getChildrenSelectedCount(groupPosition));
		}
		ParentViewHolder viewHolder = (ParentViewHolder) convertView.getTag();
		String categoryTitle = (String) getGroup(groupPosition);
		
		if (categoryTitle != null) {
			// Only display selected count if it is greater than 0
			if (getChildrenSelectedCount(groupPosition) > 0) {
				viewHolder.categoryText.setText(categoryTitle);
				viewHolder.selectedText.setText(" ("+ getChildrenSelectedCount(groupPosition) + " selected) " );
			}
			else {
				viewHolder.categoryText.setText(categoryTitle);
				viewHolder.selectedText.setText("" );
			}
		}

		return convertView;
	}
	
	/**
	 * Define the view for the 1st level category
	 */
	public View getGroupView2(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View v = null;
		if (convertView != null)
			v = convertView;
		else
			v = inflater.inflate(R.layout.category_row, parent, false);
		String categoryTitle = (String) getGroup(groupPosition);
		TextView textCategory = (TextView) v.findViewById(R.id.category);
		TextView textSelected = (TextView) v.findViewById(R.id.selected);
		if (categoryTitle != null) {
			if (getChildrenSelectedCount(groupPosition) > 0) {
				textCategory.setText(categoryTitle);
				textSelected.setText(" ("+ getChildrenSelectedCount(groupPosition) + " selected)" );
			}
			else
				textCategory.setText(categoryTitle);
		}

		return v;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void onGroupCollapsed(int groupPosition) {
	}

	public void onGroupExpanded(int groupPosition) {
	}

}
