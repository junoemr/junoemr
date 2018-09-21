<%--
  Created by IntelliJ IDEA.
  User: jordan
  Date: 12/09/18
  Time: 1:10 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
	<title>Title</title>
</head>
<body>

<%--<div>
	The URL IS: <%= request.getParameter("clinicaidUrl") %>
</div>
<div>
</div>
<div>
	The URL IS: <s:textfield name="clinicaidUrl" />
</div>--%>


<%--<script>
	var windowName = 'oscarClinicaidWindow';
	//var clinicaidWindow = window.open('<%= request.getParameter("clinicaidUrl") %>', windowName);

	var winref = window.open('', windowName, '', true);
	//if(winref.location.href === 'about:blank'){
		winref.location.href = '<%= request.getParameter("clinicaidUrl") %>';
	//}


/*	var existingWindow = window.open('', windowName);

	if(existingWindow)
	{
		existingWindow.open('<%= request.getParameter("clinicaidUrl") %>');
	}
	else
	{
		var clinicaidWindow = window.open('<%= request.getParameter("clinicaidUrl") %>', windowName);
	}*/

</script>--%>


<iframe src="<%= request.getParameter("clinicaidUrl") %>" frameborder="0" style="overflow:hidden;height:100%;width:100%" height="100%" width="100%"></iframe>
<%--<iframe src="https://google.ca/" frameborder="0" style="overflow:hidden;height:100%;width:100%" height="100%" width="100%"></iframe>--%>



</body>
</html>
