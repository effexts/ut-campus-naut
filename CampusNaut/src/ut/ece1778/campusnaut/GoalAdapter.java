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

	private Context context;
	private ArrayList<String> categories;
	private ArrayList<ArrayList<Goal>> goals;
	private LayoutInflater inflater;
	private int checkBoxCounter = 0;
	private int checkBoxInitialized = 0;
	static ViewHolder holder;

	public GoalAdapter(Context context, ArrayList<String> categories,
			ArrayList<ArrayList<Goal>> goals) {
		this.context = context;
		this.categories = categories;
		this.goals = goals;
		inflater = LayoutInflater.from(context);
	}

	public void addToGroup(String category) {
		categories.add(category);
	}

	public void addToGoal(ArrayList<Goal> goal) {
		goals.add(goal);
	}

	public Object getChild(int groupPosition, int childPosition) {
		return goals.get(groupPosition).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return (long) (groupPosition * 1024 + childPosition);
	}

	static class ViewHolder {
		protected TextView text;
		protected CheckBox checkbox;
	}

	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		checkBoxCounter = 0;
		checkBoxInitialized = 0;
		if (convertView == null) {
			final ViewHolder viewHolder = new ViewHolder();
			convertView = inflater
					.inflate(R.layout.location_row, parent, false);
			viewHolder.text = (TextView) convertView
					.findViewById(R.id.location);
			viewHolder.checkbox = (CheckBox) convertView
					.findViewById(R.id.checkbox);

			viewHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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
			convertView.setTag(viewHolder);
			viewHolder.checkbox.setTag(goals.get(groupPosition).get(
					childPosition));
		} else {
			((ViewHolder) convertView.getTag()).checkbox.setTag(goals.get(
					groupPosition).get(childPosition));
		}

		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.text.setText(goals.get(groupPosition).get(childPosition)
				.getTitle());
		viewHolder.checkbox.setChecked(goals.get(groupPosition)
				.get(childPosition).getSelected());
		return convertView;
	}

	public int getChildrenCount(int groupPosition) {
		return goals.get(groupPosition).size();
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

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View v = null;
		if (convertView != null)
			v = convertView;
		else
			v = inflater.inflate(R.layout.category_row, parent, false);
		String gt = (String) getGroup(groupPosition);
		TextView colorGroup = (TextView) v.findViewById(R.id.category);
		if (gt != null)
			colorGroup.setText(gt);
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
