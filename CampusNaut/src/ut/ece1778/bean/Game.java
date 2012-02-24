package ut.ece1778.bean;

import java.util.ArrayList;

/**
 * Entity Class Game
 *@author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class Game {

	private String gameTitle; 	//Game Title
	private ArrayList<Goal> goals;	//A list store goals of the game
	
	/**
	 * Game constructor
	 * @param title
	 * @param goals
	 */
	public Game(String gameTitle, ArrayList<Goal> goals) {
		super();
		this.gameTitle = gameTitle;
		this.goals = goals;
	}
	
	/**
	 * Game constructor without params
	 */
	public Game(){
		
	}


	//Getters & Setters
	public String getTitle() {
		return gameTitle;
	}


	public void setTitle(String title) {
		this.gameTitle = title;
	}


	public ArrayList<Goal> getGoals() {
		return goals;
	}


	public void setGoals(ArrayList<Goal> goals) {
		this.goals = goals;
	}
	
	@Override
	public String toString(){
		String str = this.getGoals().size() + " :";
		for(Goal goal : this.getGoals()){
			str += goal.getTitle();
		}
		return  str;
	}

	
}
