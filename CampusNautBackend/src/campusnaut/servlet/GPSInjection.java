package campusnaut.servlet;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.Statement;

import javax.servlet.ServletContext;
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
public class GPSInjection extends HttpServlet {

	/**
	 * serialized class
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {

		Writer out = null;

		try {
			DataOutputStream out2 = new DataOutputStream(
					response.getOutputStream());
			// read parameters from request stream.
			InputStream is = request.getInputStream();
			DataInputStream in = new DataInputStream(is);
			String user = in.readUTF();
			String coordinate = in.readUTF();
			ServletContext context = getServletContext();
			out = new OutputStreamWriter(new FileOutputStream(
					getServletContext().getRealPath("/") + user + ".txt"));
			out.write(coordinate + "\n");
			out.close();
			out2.writeUTF("Success.");
			out2.close();
		} catch (IOException e) {

			// e.printStackTrace();

		}
	}
}
