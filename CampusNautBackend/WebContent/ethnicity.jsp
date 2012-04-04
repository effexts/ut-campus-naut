<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<%@ page import="java.util.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page import="campusnaut.util.MysqlConnection" %>
<%@ page import="campusnaut.bean.DataRow" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<link rel="stylesheet" type="text/css" href="css/stylesheet.css"
	media="screen">
<title>Ethnicity</title>
</head>
<!-- include javascript library -->
<%@ include file="WEB-INF/jspf/jslibrary.jspf" %>
<!-- custom graph script starts here -->

<script type='text/javascript'>
var xmlHttp;
var xmlHttpa;

window.onload = function draw(){
	drawBar1();
	drawBar2();
}

// Top Graph initialization 
function drawBar1(){
	if (window.XMLHttpRequest)
	{// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlHttp=new XMLHttpRequest();
	}
	else
	{// code for IE6, IE5
		xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	//Set XMLHttpRequest callback function
	xmlHttp.onreadystatechange = callback;
	//Set parameters
	var eth = document.getElementById("ethSelectbar1").value;
	var ip = "http://<%=request.getServerName()+":"+request.getServerPort() %>/CampusNaut/servlet/GetJsonByEth?eth="+eth;
	//Sending request
	xmlHttp.open("GET",ip,true);
	xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
	xmlHttp.send();
	
	document.getElementById("info").innerHTML = "Loading.....";
	}

function callback(){

	var title = document.getElementById("ethSelectbar1")[document.getElementById("ethSelectbar1").selectedIndex].innerHTML;
	if(xmlHttp.readyState==4&&xmlHttp.status==200)
	{	
		var jsonText;
		//Acquire respons String
		jsonText = xmlHttp.responseText;
		
		//Parse String to JSON object
		jsonObj = eval(jsonText);

		var data = new Array();
		//Read value from JSON object
		for(var i = 0; i < jsonObj.length; i++){
			data[i] = jsonObj[i].countg;
		}
		document.getElementById("info").innerHTML="&nbsp;";
		//Drawing frist graph
		RGraph.Clear(document.getElementById("bar1"));
	 	if (typeof(bar1) == 'object') RGraph.ObjectRegistry.Remove(bar1);

		var bar1 = new RGraph.Bar('bar1',
				[[data[0],data[1]],[data[2],data[3]],[data[4],data[5]],[data[6],data[7]],[data[8],data[9]]]);
				
		bar1.Set('chart.labels', ['Architecture','Art','Libraries','Scientific Discoveries','Sports']);
		bar1.Set('chart.key', ['Male','Female']);
		bar1.Set('chart.variant','3d');
		bar1.Set('chart.background.barcolor1', 'white');
		bar1.Set('chart.background.barcolor2', 'white');
		bar1.Set('chart.title', 'Gender & Category for ' + title);
		bar1.Set('chart.title.vpos', 0.4);
		bar1.Set('chart.colors', ['#0055bb', '#ff5533']);
		bar1.Set('chart.shadow', true);
		bar1.Set('chart.shadow.blur', 25);
		bar1.Set('chart.shadow.offsetx', 0);
		bar1.Set('chart.shadow.offsety', 0);
		bar1.Set('chart.shadow.color', '#aaa');
		bar1.Set('chart.strokestyle', 'rgba(0,0,0,0)');
		bar1.Set('chart.gutter.top', 45);
		bar1.Set('chart.gutter.bottom', 45);
		bar1.Set('chart.gutter.left', 50);
		bar1.Set('chart.gutter.right', 50);
		bar1.Set('chart.labels.above', true);
		bar1.Set('chart.title.yaxis', '# of Users');
		bar1.Set('chart.title.yaxis.pos', 0.1);
		bar1.Set('chart.text.size', 11);
		RGraph.Effects.jQuery.Slide.In(bar1,{'from': 'bottom'});
	}
}

// Bottom Graph initialization
function drawBar2(){
	
	
	if (window.XMLHttpRequest)
	{// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlHttpa=new XMLHttpRequest();
	}
	else
	{// code for IE6, IE5
		xmlHttpa=new ActiveXObject("Microsoft.XMLHTTP");
	}
	
	xmlHttpa.onreadystatechange = callbacka;
	var eth = document.getElementById("ethSelectbar2").value;
	var ip = "http://<%=request.getServerName()+":"+request.getServerPort() %>/CampusNaut/servlet/GetJsonByEth?eth="+eth;

	xmlHttpa.open("GET",ip,true);
	xmlHttpa.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
	xmlHttpa.send();
	
	document.getElementById("info").innerHTML = "Loading....";
	}
	
//Same to previous callback	
function callbacka(){

	var title = document.getElementById("ethSelectbar2")[document.getElementById("ethSelectbar2").selectedIndex].innerHTML;
	if(xmlHttpa.readyState==4&&xmlHttpa.status==200)
	{	
		var jsonText;
		jsonText = xmlHttpa.responseText;

		jsonObj = eval(jsonText);

		var data = new Array();
		
		for(var i = 0; i < jsonObj.length; i++){
			data[i] = jsonObj[i].countg;
		}
		document.getElementById("info").innerHTML="&nbsp;";
		RGraph.Clear(document.getElementById("bar2"));
	 	if (typeof(bar2) == 'object') RGraph.ObjectRegistry.Remove(bar2);
		var bar2 = new RGraph.Bar('bar2',
				[[data[0],data[1]],[data[2],data[3]],[data[4],data[5]],[data[6],data[7]],[data[8],data[9]]]);
		bar2.Set('chart.labels', ['Architecture','Art','Libraries','Scientific Discoveries','Sports']);
		bar2.Set('chart.key', ['Male','Female']);
		bar2.Set('chart.variant','3d');
		bar2.Set('chart.background.barcolor1', 'white');
		bar2.Set('chart.background.barcolor2', 'white');
		bar2.Set('chart.title', 'Gender & Category for ' + title);
		bar2.Set('chart.title.vpos', 0.4);
		bar2.Set('chart.colors', ['#0055bb', '#ff5533']);
		bar2.Set('chart.shadow', true);
		bar2.Set('chart.shadow.blur', 25);
		bar2.Set('chart.shadow.offsetx', 0);
		bar2.Set('chart.shadow.offsety', 0);
		bar2.Set('chart.shadow.color', '#aaa');
		bar2.Set('chart.strokestyle', 'rgba(0,0,0,0)');
		bar2.Set('chart.gutter.top', 45);
		bar2.Set('chart.gutter.bottom', 45);
		bar2.Set('chart.gutter.left', 50);
		bar2.Set('chart.gutter.right', 50);
		bar2.Set('chart.labels.above', true);
		bar2.Set('chart.title.yaxis', '# of Users');
		bar2.Set('chart.title.yaxis.pos', 0.1);
		bar2.Set('chart.text.size', 11);
		RGraph.Effects.jQuery.Slide.In(bar2,{'from': 'bottom'});
	 
	}
}
</script>

<body>
	<!-- Layout code start from here -->
	<div id="layout">
	<%@ include file="WEB-INF/jspf/banner.jspf" %>
	<%@ include file="WEB-INF/jspf/menu.jspf" %>
	<%@ include file="WEB-INF/jspf/startcontent.jspf" %>
		<div style='text-align: center'>
		<h1>Ethnic Group Comparison</h1>
					<p>
			Top Graph: <select id = "ethSelectbar1"  onChange="drawBar1()">
			<option value="0">Aboriginal origins</option>
			<option value="1">African origins</option>
			<option value="2">Arab origins</option>
			<option value="3">British Isles origins</option>
			<option value="4">Caribbean origins</option>
			<option value="5">East and Southeast Asian origins</option>
			<option value="6">European origins</option>
			<option value="7">French origins</option>
			<option value="8">Latin, Central and South American origins</option>
			<option value="9">Oceania origins</option>
			<option value="10">South Asian origins</option>
			<option value="11">West Asian origins</option>	
			</select> 
		    Bottom Graph: <select id = "ethSelectbar2"  onChange="drawBar2()">
			<option value="0">Aboriginal origins</option>
			<option value="1" selected="selected">African origins</option>
			<option value="2">Arab origins</option>
			<option value="3">British Isles origins</option>
			<option value="4">Caribbean origins</option>
			<option value="5">East and Southeast Asian origins</option>
			<option value="6">European origins</option>
			<option value="7">French origins</option>
			<option value="8">Latin, Central and South American origins</option>
			<option value="9">Oceania origins</option>
			<option value="10">South Asian origins</option>
			<option value="11">West Asian origins</option>	
			</select>
			<div id = "info" style='clear:both'>&nbsp;</div>
			<canvas id='bar1' width='680' height='300' style='margin-top:20px;'>[No canvas
			support]</canvas><p></p>
			<canvas id='bar2' width='680' height='300' style='margin-top:20px;'>[No canvas
			support]</canvas>

		</div>
		

		<!-- Content ends here -->
<div class="baktotop">
				<p>
					<a href="ethnicity.jsp#layout">Back to top</a>
				</p>
			</div>
		</div>
	</div>
</div>
	<%@ include file="WEB-INF/jspf/footer.jspf" %>
	</div>
</body>
</html>