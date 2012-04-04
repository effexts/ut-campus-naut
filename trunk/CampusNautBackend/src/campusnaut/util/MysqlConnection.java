package campusnaut.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class MysqlConnection {

	private Connection con = null;

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
