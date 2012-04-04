package campusnaut.servlet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet handle request from Mock GPS Controller to
 * update the mock GPS coordinate to be read by the client.
 * 
 * @author Steve Chun-Hao Hu, Leo ChenLiang Man
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
			// Read parameters from request stream.
			InputStream is = request.getInputStream();
			DataInputStream in = new DataInputStream(is);
			// There are two user files, one Steve.txt and one Leo.txt
			String user = in.readUTF();
			String coordinate = in.readUTF();
			ServletContext context = getServletContext();
			// Update the GPS coordinate
			out = new OutputStreamWriter(new FileOutputStream(
					getServletContext().getRealPath("/") + user + ".txt"));
			out.write(coordinate + "\n");
			out.close();
			// Send a response msg back to client
			out2.writeUTF("Success.");
			out2.close();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}
}
