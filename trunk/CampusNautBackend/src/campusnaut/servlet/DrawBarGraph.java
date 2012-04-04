package campusnaut.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DrawBarGraph extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Fill DB query results into graph here
		int[] data = new int[] { 12, 16, 23, 20, 14, 8, 3, 10, 23, 15 };
		String[] labels = new String[] { "Art", "Architecture", "Libraries",
				"Scientific Discoveries", "Sports" };
		String[] keys = new String[] { "Male", "Female" };

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("<script type='text/javascript' src='https://apis.google.com/js/plusone.js'></script>");
		// Include libraries
		out.println("<script src='../libraries/RGraph.common.core.js' ></script>");
		out.println("<script src='../libraries/RGraph.common.effects.js' ></script>");
		out.println("<script src='../libraries/RGraph.common.tooltips.js' ></script>");
		out.println("<script src='../libraries/RGraph.common.key.js' ></script>");
		out.println("<script src='../libraries/RGraph.bar.js' ></script>");
		// Start of JavaScript
		out.println("<script type='text/javascript'>");
		out.println("window.onload = function draw(){");
		out.println("alert('Bar Graph');");

		// Set data for bars
		StringBuffer dataStr = new StringBuffer();
		for (int i = 0; i < data.length; i += 2) {
			dataStr.append("[" + data[i] + "," + data[i + 1] + "],");
		}
		out.println("var bar1 = new RGraph.Bar('bar1', ["
				+ dataStr.substring(0, dataStr.length() - 1) + "]);");

		// Set label of bars
		StringBuffer labelStr = new StringBuffer();
		for (int i = 0; i < labels.length; i++) {
			labelStr.append("'" + labels[i] + "',");
		}
		out.println("bar1.Set('chart.labels', ["
				+ labelStr.substring(0, labelStr.length() - 1) + "]);");

		// Set Key's value
		out.println("bar1.Set('chart.key', ['" + keys[0] + "', '" + keys[1]
				+ "']);");

		// Set other attributes of graph
		out.println("bar1.Set('chart.background.barcolor1', 'white');"
				+ "bar1.Set('chart.background.barcolor2', 'white');"
				+ "bar1.Set('chart.key.position.y', 5);"
				+ "bar1.Set('chart.key.position', 'gutter');"
				+ "bar1.Set('chart.key.background', 'rgb(255,255,255)');"
				+ "bar1.Set('chart.colors', ['#447799', '#88dddd']);"
				+ "bar1.Set('chart.shadow', true);"
				+ "bar1.Set('chart.shadow.blur', 25);"
				+ "bar1.Set('chart.shadow.offsetx', 0);"
				+ "bar1.Set('chart.shadow.offsety', 0);"
				+ "bar1.Set('chart.shadow.color', '#aaa');"
				+ "bar1.Set('chart.strokestyle', 'rgba(0,0,0,0)');"
				+ "bar1.Set('chart.gutter.left', 55);"
				+ "bar1.Set('chart.gutter.right', 35);" + "bar1.Draw();");
		out.println("}");
		out.println("</script>");
		out.println("  <BODY>");
		out.println("<div style='text-align: center'><h1>Campusnaut Data Analysis</h1></div>");
		// Canvas DIV to draw current graph
		out.println("<div style='text-align: center'>");
		out.println("<canvas id='bar1' width='600' height='300'>[No canvas support]</canvas>");
		out.println("</div>");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

}
