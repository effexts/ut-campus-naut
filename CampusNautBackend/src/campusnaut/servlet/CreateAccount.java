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

/**
 * This servlet handles account creation requests.
 * 
 * @author LeoMan
 * 
 */
public class CreateAccount extends HttpServlet {

	/**
	 * serialized class
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		this.doGet(request, response);

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {

		try {
			// read parameters from request stream.
			InputStream is = request.getInputStream();
			DataInputStream in = new DataInputStream(is);
			String username = in.readUTF();
			String email = in.readUTF();
			String password = in.readUTF();
			int age = Integer.parseInt(in.readUTF());
			String gender = in.readUTF();
			String ethnicity = in.readUTF();
			System.out.println(username);

			Connection con = null;
			Statement stmt = null;
			// generate response stream
			DataOutputStream out = new DataOutputStream(
					response.getOutputStream());

			try {
				// execute SQL
				MysqlConnection myconn = new MysqlConnection();
				con = myconn.getConnection();
				stmt = con.createStatement();
				String sql = "INSERT INTO t_user "
						+ "(username,password,email,age,gender,ethnicity) "
						+ "VALUES ('" + username + "','" + password + "','"
						+ email + "','" + age + "','" + gender + "','"
						+ ethnicity + "')";
				stmt.execute(sql);
				// Get new user's ID
				sql = "SELECT user_id FROM t_user WHERE username = '"
						+ username + "'";
				ResultSet rs = stmt.executeQuery(sql);
				int user_id = 0;
				while (rs.next()) {
					user_id = rs.getInt("user_id");
				}
				// write result into response stream
				out.writeUTF("Welcome " + username);
				out.writeUTF(Integer.toString(user_id));
				if (con != null)
					con.close();
			} catch (Exception e) {
				e.printStackTrace();
				out.writeUTF("Failed");
			} finally {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (IOException e) {

			e.printStackTrace();

		}

	}
}
