<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*"%>
<%@ page import="campusnaut.util.MysqlConnection" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/stylesheet.css"
	media="screen">
<title>CampusNaut</title>
</head>

<body>
<!-- Layout code start from here -->
	<div id="layout">
        <%@ include file="WEB-INF/jspf/banner.jspf" %>
        <%@ include file="WEB-INF/jspf/menu.jspf" %>
		<%@ include file="WEB-INF/jspf/startcontent.jspf" %>
				
					<!-- Content start from here(replace it with new graph or table) -->
					<table border="1" cellspacing="4" cellpadding="4">
						<tr>
							<th>Username</th>
							<th>Gender</th>
							<th>Age</th>
							<th>Ethnic Categories</th>
						</tr>
						<%
						/**
						 * Retrieves the user data from MySQL database and show them 
						 * on the table.
						 */
						try{
							MysqlConnection myconn = new MysqlConnection();
						    Connection con = myconn.getConnection();
						    Statement stmt = con.createStatement();
						    ResultSet rs=stmt.executeQuery("select username, gender, age,ethnicity from t_user ");
							while(rs.next())
							{
								String data = "<td>"+rs.getString("username") + "</td><td>" + rs.getString("gender")+
										"</td><td>"+Integer.parseInt(rs.getString("age"))+"</td><td>"+rs.getString("ethnicity")+"</td>";
								out.println("<tr>"+data+"</tr>");
							}
							if(con != null)			
								con.close();
						} catch (Exception e) {
							System.err.println(e);
						}
						%>
					</table>
					<!-- Content ends here -->
					
					<%@ include file="WEB-INF/jspf/endcontent.jspf" %>

		<%@ include file="WEB-INF/jspf/footer.jspf" %>
	</div>
</body>
</html>