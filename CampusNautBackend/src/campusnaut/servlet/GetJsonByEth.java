package campusnaut.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import campusnaut.bean.DataRow;
import campusnaut.util.MysqlConnection;

/**
 * This Servlet generates a Json buffer for a certain ethnicity 
 * from the request of generating graph data in ethnicity.jsp 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class GetJsonByEth extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// Get parameters from request
		int num = Integer.parseInt(request.getParameter("eth"));

		List<DataRow> drow = new ArrayList<DataRow>();
		List<String> hashDR = new ArrayList<String>();
		drow.add(new DataRow(0, "Male", "Architecture"));
		hashDR.add("Male" + "Architecture");
		drow.add(new DataRow(0, "Female", "Architecture"));
		hashDR.add("Female" + "Architecture");
		drow.add(new DataRow(0, "Male", "Art"));
		hashDR.add("Male" + "Art");
		drow.add(new DataRow(0, "Female", "Art"));
		hashDR.add("Female" + "Art");
		drow.add(new DataRow(0, "Male", "Libraries"));
		hashDR.add("Male" + "Libraries");
		drow.add(new DataRow(0, "Female", "Libraries"));
		hashDR.add("Female" + "Libraries");
		drow.add(new DataRow(0, "Male", "Scientific Discoveries"));
		hashDR.add("Male" + "Scientific Discoveries");
		drow.add(new DataRow(0, "Female", "Scientific Discoveries"));
		hashDR.add("Female" + "Scientific Discoveries");
		drow.add(new DataRow(0, "Male", "Sports"));
		hashDR.add("Male" + "Sports");
		drow.add(new DataRow(0, "Female", "Sports"));
		hashDR.add("Female" + "Sports");

		// Set ethnicity parameter for query
		String ethnicity = "Aboriginal origins";
		switch (num) {
		case 0:
			ethnicity = "Aboriginal origins";
			break;
		case 1:
			ethnicity = "African origins";
			break;
		case 2:
			ethnicity = "Arab origins";
			break;
		case 3:
			ethnicity = "British Isles origins";
			break;
		case 4:
			ethnicity = "Caribbean origins";
			break;
		case 5:
			ethnicity = "East and Southeast Asian origins";
			break;
		case 6:
			ethnicity = "European origins";
			break;
		case 7:
			ethnicity = "French origins";
			break;
		case 8:
			ethnicity = "Latin, Central and South American origins";
			break;
		case 9:
			ethnicity = "Oceania origins";
			break;
		case 10:
			ethnicity = "South Asian origins";
			break;
		case 11:
			ethnicity = "West Asian origins";
			break;
		}

		final String QUERY = "SELECT COUNT(gender) AS countg , gender, category "
				+ "FROM t_user_games, t_user, t_goals "
				+ "WHERE t_user.user_id = t_user_games.user_id "
				+ "AND t_user_games.goal_id = t_goals.goal_id "
				+ "AND state = 2 AND ethnicity = '"
				+ ethnicity
				+ "' "
				+ "GROUP BY gender, category "
				+ "ORDER BY category ASC,gender DESC";
		try {

			// Start DB query
			MysqlConnection myconn = new MysqlConnection();
			Connection con = myconn.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);
			while (rs.next()) {
				int count = rs.getInt("countg");
				String gender = rs.getString("gender");
				String category = rs.getString("category");

				int index = hashDR.indexOf(gender + category);
				if (index != -1) {
					drow.add(index, new DataRow(count, gender, category));
					drow.remove(index + 1);
				}
			}
			if (con != null)
				con.close();
		} catch (Exception e) {
			System.err.println(e);
		}
		hashDR.clear();

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		// Generate JSON buffer
		StringBuffer sb = new StringBuffer("{origin:[");

		for (int i = 0; i < drow.size(); i++) {
			sb.append("{countg:" + drow.get(i).getCount() + "," + "gender:'"
					+ drow.get(i).getGender() + "'," + "category:'"
					+ drow.get(i).getCategory() + "'},");

		}
		sb.delete(sb.length() - 1, sb.length());
		sb.append("]}");
		// Send response text back to client
		out.println(sb);
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doGet(request, response);
	}

}
