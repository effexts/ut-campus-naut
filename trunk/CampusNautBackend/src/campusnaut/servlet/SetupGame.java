package campusnaut.servlet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import campusnaut.util.MysqlConnection;

public class SetupGame extends HttpServlet {

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

			if (authentic.equals("<SETUP>")) {
				Connection con = null;
				Statement stmt = null;

				try {
					// execute SQL
					MysqlConnection myconn = new MysqlConnection();
					con = myconn.getConnection();
					stmt = con.createStatement();

					StringTokenizer token = new StringTokenizer(data, DELIMITER);
					while (token.hasMoreTokens()) {
						int gid = Integer.parseInt(token.nextToken());
						String sql = "INSERT INTO t_user_games (user_id , goal_id) "
								+ "VALUES (" + uid + " , " + gid + ")";
						stmt.execute(sql);
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
