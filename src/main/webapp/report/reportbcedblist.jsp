<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
      String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
      boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_report,_admin.reporting" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_report&type=_admin.reporting");%>
</security:oscarSec>
<%
if(!authed) {
	return;
}
%>

<%

String deepcolor = "#CCCCFF", weakcolor = "#EEEEFF";

String limit = request.getParameter("limit") == null ? "100" : request.getParameter("limit");
String offset = request.getParameter("offset") == null ? "0" : request.getParameter("offset");

if(request.getParameter("resultLimit")!=null)
{
	limit = request.getParameter("resultLimit");
}
String startDate =null, endDate=null;
if(request.getParameter("startDate")!=null) startDate = request.getParameter("startDate");  
if(request.getParameter("endDate")!=null) endDate = request.getParameter("endDate");
%>

<%@ page import="java.util.*, java.sql.*" errorPage="../errorpage.jsp"%>

<jsp:useBean id="providerNameBean" class="java.util.Properties" scope="page" />

<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.PMmodule.dao.ProviderDao" %>
<%@ page import="org.oscarehr.common.model.Provider" %>
<%@ page import="org.oscarehr.common.dao.forms.FormsDao" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DateFormat" %>
<%
	ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
	FormsDao formsDao = SpringUtils.getBean(FormsDao.class);
%>


<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title><bean:message key="report.reportnewdblist.title" /></title>
<link rel="stylesheet" href="../css/receptionistapptstyle.css">
<script language="JavaScript">

function onResultLimitChange()
{
	var resultLimit = document.getElementById("resultLimit");
	document.location.href='reportbcedblist.jsp?startDate=<%=request.getParameter("startDate")%>&endDate=<%=request.getParameter("endDate")%>&limit=' + resultLimit.options[resultLimit.selectedIndex].value + '&offset=<%=offset%>';
}

</script>
<!--base target="pt_srch_main"-->
</head>
<body onLoad="setfocus()" topmargin="0" leftmargin="0" rightmargin="0">

<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr bgcolor="<%=deepcolor%>">
		<th colspan="2">
			<font face="Helvetica"><bean:message key="report.reportnewdblist.msgEDDList" /></font>
		</th>
	</tr>
	<tr>
		<td>
			<div class="pageLimiter">
				<label>Result Limit:</label>
				<select name="resultLimit" id="resultLimit" onchange="onResultLimitChange();">
					<option value="100" <%= limit.equals("100") ? "selected='selected'" : "" %>>100</option>
					<option value="250" <%= limit.equals("250") ? "selected='selected'" : "" %>>250</option>
					<option value="500" <%= limit.equals("500") ? "selected='selected'" : "" %>>500</option>
					<option value="1000"<%= limit.equals("1000") ? "selected='selected'" : "" %>>1000</option>
				</select>
			</div>
		</td>
		<td align="right"><input type="button" name="Button"
			value="<bean:message key="global.btnPrint"/>"
			onClick="window.print()"> <input type="button" name="Button"
			value="<bean:message key="global.btnCancel" />"
			onClick="window.close()">
		</th>
	</tr>
</table>

<CENTER>
<table width="100%" border="0" bgcolor="silver" cellspacing="2"
	cellpadding="2">
	<tr bgcolor='<%=deepcolor%>'>
		<TH align="center" width="6%" nowrap><b><bean:message
			key="report.reportnewdblist.msgEDD" /></b></TH>
		<TH align="center" width="20%"><b><bean:message
			key="report.reportnewdblist.msgName" /> </b></TH>
		<!--TH align="center" width="20%"><b>Demog' No </b></TH-->
		<TH align="center" width="9%"><b><bean:message
			key="report.reportnewdblist.msgDOB" /></b></TH>
		<TH align="center" width="5%"><b>G</b><font size="-2">ravida</font></TH>
		<TH align="center" width="5%"><b><bean:message
			key="report.reportnewdblist.msgTerm" /></b></TH>
		<TH align="center" width="10%"><b><bean:message
			key="report.reportnewdblist.msgPhone" /></b></TH>
		<TH align="center" width="10%"><b><bean:message
			key="report.reportnewdblist.msLanguage" /></b></TH>
		<TH align="center" width="8%"><b><bean:message
			key="report.reportnewdblist.msPHN" /></b></TH>
		<TH align="center" width="20%"><b>Doula</b></TH>
		<TH align="center"><b>Doula#</b></TH>
	</tr>
	<%
	for(Provider p : providerDao.getActiveProviders()) {
    providerNameBean.setProperty(p.getProviderNo(), new String( p.getFormattedName()));
  }
  
  Properties demoProp = new Properties();
    
  boolean bodd=false;
  int nItems=0;

  for(Object[] result : formsDao.selectBcFormAr(startDate, endDate, Integer.parseInt(limit), Integer.parseInt(offset))) {
 	String demographicNo = ((Integer)result[0]).toString();
 	Date cEDD = (Date)result[1];
 	String surname = (String)result[2];
 	String givenName  = (String)result[3];
 	String pg1_ageAtEDD = (String)result[4];
 	Date dob = (Date)result[5];
 	String langPref = (String)result[6];
 	String phn = (String)result[7];
 	String gravida = (String)result[8];
 	String term = (String)result[9];
 	String phone = (String)result[10];
 	String doula = (String)result[12];
 	String doulaNo = (String)result[13];


	DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	String cEddString = df.format(cEDD);
    if (demoProp.containsKey(demographicNo) ) continue;
    else demoProp.setProperty(demographicNo, "1");
    bodd=bodd?false:true; //for the color of rows
    nItems++; 
%>
	<tr bgcolor="<%=bodd?weakcolor:"white"%>">
		<td align="center" nowrap><%=cEddString!=null?cEddString:"----/--/--"%></td>
		<td><%=surname + ", " + givenName%></td>
		<!--td align="center" ><%=demographicNo%> </td-->
		<td><%=dob!=null?dob:""%></td>
		<td><%=gravida!=null?gravida:""%></td>
		<td><%=term!=null?term:""%></td>
		<td nowrap><%=phone%></td>
		<td><%=langPref%></td>
		<td><%=phn%></td>
		<td><%=doula%></td>
		<td><%=doulaNo%></td>
	</tr>
	<%
  }
%>

</table>
<br>
<%
  int nextOffset = Integer.parseInt(offset) + Integer.parseInt(limit);
  int prevOffset = Integer.parseInt(offset) - Integer.parseInt(limit);
  if(prevOffset>=0) {
%> <a
		href="reportbcedblist.jsp?startDate=<%=request.getParameter("startDate")%>&endDate=<%=request.getParameter("endDate")%>&limit=<%=limit%>&offset=<%=prevOffset%>"><bean:message
		key="report.reportnewdblist.msgLastPage" /></a> | <%
  }

  if(nItems==Integer.parseInt(limit)) {
%> <a
		href="reportbcedblist.jsp?startDate=<%=request.getParameter("startDate")%>&endDate=<%=request.getParameter("endDate")%>&limit=<%=limit%>&offset=<%=nextOffset%>">
	<bean:message key="report.reportnewdblist.msgNextPage" /></a> <%
}
%>
</body>
</html:html>
