package campusnaut.servlet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;

import java.sql.Statement;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import campusnaut.util.MysqlConnection;

public class UpdateProgress extends HttpServlet {

	/**
	 * 
	 */
	private static final String DELIMITER = "%";

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		this.doPost(request, response);

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {

		try {
			// read parameters from request stream.
			InputStream is = request.getInputStream();
			DataInputStream in = new DataInputStream(is);

			String authentic = in.readUTF();
			int uid = Integer.parseInt(in.readUTF());
			String data = in.readUTF();

			System.out.println(uid);
			System.out.println(data);
			DataOutputStream out = new DataOutputStream(
					response.getOutputStream());

			if (authentic.equals("<UPDATE>")) {
				Connection con = null;
				Statement stmt = null;
				ResultSet rs = null;

				try {
					// execute SQL
					MysqlConnection myconn = new MysqlConnection();
					con = myconn.getConnection();
					stmt = con.createStatement();

					StringTokenizer token = new StringTokenizer(data, DELIMITER);
					while (token.hasMoreTokens()) {
						int gid = Integer.parseInt(token.nextToken());
						String search = "SELECT COUNT(user_id) FROM t_user_games "
								+ "WHERE user_id = "
								+ uid
								+ " AND goal_id = "
								+ gid;
						rs = stmt.executeQuery(search);
						int count = 0;
						while (rs.next()) {
							count = rs.getInt(1);
						}
						if (count == 1) {
							String sql = "UPDATE t_user_games SET state = 2 "
									+ "WHERE user_id = " + uid
									+ " AND goal_id = " + gid + ";";
							stmt.execute(sql);
						} else {
							String insert = "INSERT INTO t_user_games (user_id, goal_id, state)"
									+ "VALUES (" + uid + ", " + gid + ", 2);";
							stmt.execute(insert);
						}
					}
					out.writeUTF("Success");
				} catch (Exception e) {
					e.printStackTrace();
					out.writeUTF("Success");
				} finally {
					con.close();
				}
			} else {
				out.writeUTF("Invalid");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
