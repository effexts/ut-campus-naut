package campusnaut.util;

import java.sql.Connection;
import java.sql.DriverManager;
/**
 * This class provides a mysql jdbc connection tool
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
 */
public class MysqlConnection {

	private Connection con = null;

	/**
	 * Get JDBC connection with campusnaut database
	 * @return java.sql.Connection
	 */
	public Connection getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			String url = "jdbc:mysql://localhost:3306/campusnautdb";
			String name = "dbuser";
			String pswd = "campusnaut";
			con = DriverManager.getConnection(url, name, pswd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;

	}

}
