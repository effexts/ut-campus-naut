package campusnaut.bean;

/**
 * This class is used to store the data read from the MySQL database
 * and as a data source feed for the JavaScript RGraph.
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class DataRow {
	private int count = 0;
	private String gender = "";
	private String category = "";
	private String ethnicity = "";

	/**
	 * Constructor for gender vs category
	 */
	public DataRow(int count, String gender, String category) {
		this.count = count;
		this.gender = gender;
		this.category = category;
	}

	/**
	 * Constructor for gender vs category and ethnicity
	 */
	public DataRow(int count, String gender, String category, String ethnicity) {
		this.count = count;
		this.gender = gender;
		this.category = category;
		this.ethnicity = ethnicity;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getGender() {
		return gender;
	}

	public String getReverseGender() {
		if (gender.equals("Male")) {
			return "Female";
		} else {
			return "Male";
		}
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getEthnicity() {
		return ethnicity;
	}

	public void setEthnicity(String ethnicity) {
		this.ethnicity = ethnicity;
	}

	/**
	 * Define the HTML table row data be shown on the page.
	 */
	public String toString() {
		String result;
		if (ethnicity.isEmpty()) {
			result = "<tr><td>" + category + "</td>" + "<td>" + gender
					+ "</td>" + "<td>" + count + "</td></tr>";
		} else {
			result = "<tr>" + ethnicity + "</td>" + "<td>" + category + "</td>"
					+ "<td>" + gender + "</td>" + "<td>" + count + "</td></tr>";
		}
		return result;
	}
}
