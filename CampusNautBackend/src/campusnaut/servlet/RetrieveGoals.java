package campusnaut.servlet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import campusnaut.util.MysqlConnection;

public class RetrieveGoals extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			// read parameters from request stream.
			InputStream is = request.getInputStream();
			DataInputStream in = new DataInputStream(is);

			// Compare the local database count vs online one to see if
			// downloading is necessary
			String localCount = in.readUTF();

			int lCount = 0;
			if (localCount != null)
				lCount = Integer.parseInt(localCount);

			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			// generate response stream
			DataOutputStream out = new DataOutputStream(
					response.getOutputStream());

			try {
				// execute SQL
				MysqlConnection myconn = new MysqlConnection();
				con = myconn.getConnection();
				stmt = con.createStatement();
				String sql = "SELECT count(goal_id) FROM t_goals";
				rs = stmt.executeQuery(sql);
				int count = 0;
				while (rs.next()) {
					count = rs.getInt(1);
				}
				// Android need to sync with DB for goals
				if (count > 0 && count != lCount) {
					// Only need record not available from app db
					sql = "SELECT * FROM t_goals where goal_id >="
							+ (20000 + lCount);
					rs = stmt.executeQuery(sql);
					// out.writeUTF(""+count);/*
					// goal_id title description latitude longitude category
					out.writeUTF("<BEGIN>");
					while (rs.next()) {
						out.writeUTF(rs.getInt(1) + "%" + rs.getString(2) + "%"
								+ rs.getString(3) + "%" + rs.getDouble(4) + "%"
								+ rs.getDouble(5) + "%" + rs.getString(6));
					}
					out.writeUTF("<DONE>");
				} else if (count == lCount) { // tell android app u got nothing
												// new from me
					out.writeUTF("<NOTHING>");
				} else { // tell android i fail
					out.writeUTF("<FAILED>");
				}

				// close db connection
				if (con != null)
					con.close();
			} catch (Exception e) {
				e.printStackTrace();
				out.writeUTF("<FAILED>");
			}

		} catch (IOException e) {

			e.printStackTrace();

		}

	}
}
