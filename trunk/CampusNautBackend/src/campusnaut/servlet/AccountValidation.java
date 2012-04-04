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

public class AccountValidation extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		this.doPost(request, response);

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {

		try {
			// read parameters from request stream.
			InputStream is = request.getInputStream();
			DataInputStream in = new DataInputStream(is);

			String email = in.readUTF();
			String password = in.readUTF();

			System.out.println(email);

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
				String sql = "SELECT user_id FROM t_user WHERE email='" + email
						+ "' AND password='" + password + "'";
				rs = stmt.executeQuery(sql);
				int user_id = 0;
				while (rs.next()) {
					user_id = rs.getInt(1);
				}
				System.out.println(user_id);
				if (user_id == 0) {
					out.writeUTF("Failed");
				} else {
					out.writeUTF("Success.");
					out.writeUTF(Integer.toString(user_id));
				}
				// write result into response stream
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
