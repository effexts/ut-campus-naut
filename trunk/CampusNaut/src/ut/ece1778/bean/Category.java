package ut.ece1778.bean;

import java.util.List;

/**
 * Entity Class Category
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 *
 */
public class Category {

	private String categoryName; //the name of category
	private List<Goal> goals; //list of its goals
	

	public Category(){
		
	}
	
	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public List<Goal> getGoals() {
		return goals;
	}

	public void setGoals(List<Goal> goals) {
		this.goals = goals;
	}
}
