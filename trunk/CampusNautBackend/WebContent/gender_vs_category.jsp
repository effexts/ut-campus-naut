<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page import="campusnaut.util.MysqlConnection" %>
<%@ page import="campusnaut.bean.DataRow" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<link rel="stylesheet" type="text/css" href="css/stylesheet.css"
	media="screen">
<title>Gender & Category</title>
</head>
<!-- include javascript library -->
<%@ include file="WEB-INF/jspf/jslibrary.jspf" %>
<!-- custom graph script starts here -->
<script type='text/javascript'>
window.onload = function draw(){
	<%
		List<DataRow> drow = new ArrayList<DataRow>();
		try{
			final String QUERY = "SELECT COUNT(gender) AS countg , gender, category " +
					"FROM t_user_games, t_user, t_goals " +
					"WHERE t_user.user_id = t_user_games.user_id " +
					"AND t_user_games.goal_id = t_goals.goal_id " +
					"AND state = 2 " +
					"GROUP BY gender, category " +
					"ORDER BY category ASC,gender DESC";
			
			MysqlConnection myconn = new MysqlConnection();
		    Connection con = myconn.getConnection();
		    Statement stmt = con.createStatement();
		    ResultSet rs=stmt.executeQuery(QUERY);
			while(rs.next())
			{
				if ((drow.size() % 2) == 0) { // even row
					int count = rs.getInt("countg");
					String gender = rs.getString("gender");
					String category = rs.getString("category");
					drow.add(new DataRow(count, gender, category));
				} else { // odd row
					int count = rs.getInt("countg");
					String gender = rs.getString("gender");
					String category = rs.getString("category");
					// Have to make sure gender for each category is considered even if there is no row for it(when count = 0)
					int i = drow.size();
					if (!category.equals(drow.get(i-1).getCategory())) {
						drow.add(new DataRow(0, drow.get(i-1).getReverseGender(), drow.get(i-1).getCategory()));
					}
					drow.add(new DataRow(count, gender, category));
				}
			}
			// Have to make sure there are even number of rows to draw graph so add one if not
			if (drow.size()> 0 && drow.size() % 2 != 0) {
				drow.add(new DataRow(0, drow.get(drow.size()-1).getReverseGender(), drow.get(drow.size()-1).getCategory()));
			}
			if(con != null)			
				con.close();
		} catch (Exception e) {
			System.err.println(e);
		}
		
		StringBuffer dataStr = new StringBuffer();
		for(int i = 0; i < drow.size(); i += 2){
			dataStr.append("["+drow.get(i).getCount()+","+drow.get(i+1).getCount()+"],");
		}
		out.println("var bar1 = new RGraph.Bar('bar1', ["+dataStr.substring(0, dataStr.length()-1)+"]);");
		//Set label of bars
		StringBuffer labelStr = new StringBuffer();
		for(int i = 0; i < drow.size(); i+=2){
			labelStr.append("'"+drow.get(i).getCategory()+"',");
		}
		out.println("bar1.Set('chart.labels', ["+labelStr.substring(0, labelStr.length()-1)+"]);");
		//Set Key's value
		out.println("bar1.Set('chart.key', ['"+drow.get(0).getGender()+"', '"+drow.get(1).getGender()+"']);");
	%>
	
	bar1.Set('chart.background.barcolor1', 'white');
	bar1.Set('chart.background.barcolor2', 'white');
	bar1.Set('chart.key.position.y', 5);
	bar1.Set('chart.key.position', 'gutter');
	bar1.Set('chart.key.background', 'rgb(255,255,255)');
	bar1.Set('chart.colors', ['#0055bb', '#ff5533']);
	bar1.Set('chart.shadow', true);
	bar1.Set('chart.shadow.blur', 25);
	bar1.Set('chart.shadow.offsetx', 0);
	bar1.Set('chart.shadow.offsety', 0);
	bar1.Set('chart.shadow.color', '#aaa');
	bar1.Set('chart.strokestyle', 'rgba(0,0,0,0)');
	bar1.Set('chart.gutter.left', 50);
	bar1.Set('chart.gutter.right', 50);
	bar1.Set('chart.labels.above', true);
	bar1.Set('chart.title.yaxis', '# of Users');
	bar1.Set('chart.title.yaxis.pos', 0.3);
	bar1.Set('chart.text.size', 11);
	//bar1.Draw();
	RGraph.Effects.Bar.Wave(bar1);
	
}
</script>
<body>
<!-- Layout code start from here -->
<div id="layout">
<%@ include file="WEB-INF/jspf/banner.jspf" %>
<%@ include file="WEB-INF/jspf/menu.jspf" %>
<%@ include file="WEB-INF/jspf/startcontent.jspf" %>
	<div style='text-align: center'>
		<h1>Gender & Category</h1>
		<canvas id='bar1' width='800' height='400' >[No canvas support]</canvas>
		<p> </p>
		<table border="1" cellspacing="4" cellpadding="4" align="center">
		<tr>
			<th>Category</th>
			<th>Gender</th>
			<th># of Users</th>
		</tr>
		<%
			for (int i = 0; i < drow.size(); i++ ) {
				out.println(drow.get(i).toString());
			}
		%>
		</table>
	</div>


<!-- Content ends here -->
<%@ include file="WEB-INF/jspf/endcontent.jspf" %>
<%@ include file="WEB-INF/jspf/footer.jspf" %>
</div>
</body>
</html>