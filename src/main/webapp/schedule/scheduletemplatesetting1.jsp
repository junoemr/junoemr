<%--

    Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for the
    Department of Family Medicine
    McMaster University
    Hamilton
    Ontario, Canada

--%>

<%--
	This file (so far) seems to be a non-used, duplicated functionality file of the its
	working version 'scheduletemplateapplying.jsp'. To make oscar work safely,
	We shall modify this file regards to the modifications made at 'scheduletemplateapplying.jsp'

--%>

<%
  
  String weekdaytag[] = {"SUN","MON","TUE","WED","THU","FRI","SAT"};
%>
<%@ page import="org.oscarehr.schedule.dao.RScheduleDao,
                 org.oscarehr.schedule.model.RSchedule,
                 org.oscarehr.util.SpringUtils,
                 oscar.MyDateFormat,
                 oscar.SxmlMisc"
         errorPage="../appointment/errorpage.jsp" %>
<%@page import="oscar.util.ConversionUtils" %>
<%@page import="java.util.Calendar" %>
<%@page import="java.util.GregorianCalendar" %>
<%@page import="java.util.StringTokenizer" %>

<%
	RScheduleDao rScheduleDao = SpringUtils.getBean(RScheduleDao.class);
%>

<jsp:useBean id="scheduleRscheduleBean" class="oscar.RscheduleBean" scope="session" />


<% scheduleRscheduleBean.clear(); %>

<html>
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title>SCHEDULE SETTING</title>
<link rel="stylesheet" href="../web.css" />

<script language="JavaScript">
<!--
function setfocus() {
  this.focus();
  //document.schedule.keyword.focus();
  //document.schedule.keyword.select();
}
function selectrschedule(s) {
  if(self.location.href.lastIndexOf("&sdate=") > 0 ) a = self.location.href.substring(0,self.location.href.lastIndexOf("&sdate="));
  else a = self.location.href;
	self.location.href = a + "&sdate=" +s.options[s.selectedIndex].value ;
}
function upCaseCtrl(ctrl) {
	ctrl.value = ctrl.value.toUpperCase();
}
function addDataString() {
  var str="";
  var str1="";
  if(document.schedule.checksun.checked) {
    str += "1 ";
    str1 += "<SUN>"+document.schedule.sunfrom.value+"-"+document.schedule.sunto.value+"</SUN>"; 
  }
  if(document.schedule.checksun.unchecked) {
    str = str.replace("1 ","");
//	str1 = str1.replace();
  }
  if(document.schedule.checkmon.checked) {
    str += "2 ";
	str1 += "<MON>"+document.schedule.monfrom.value+"-"+document.schedule.monto.value+"</MON>";
  }
  if(document.schedule.checkmon.unchecked) {    str = str.replace("2 ","");  }
  if(document.schedule.checktue.checked) {
    str += "3 ";  
	str1 += "<TUE>"+document.schedule.tuefrom.value+"-"+document.schedule.tueto.value+"</TUE>";
  }
  if(document.schedule.checktue.unchecked) {
    str = str.replace("3 ","");
  }
  if(document.schedule.checkwed.checked) {
    str += "4 ";
	str1 += "<WED>"+document.schedule.wedfrom.value+"-"+document.schedule.wedto.value+"</WED>";
  }
  if(document.schedule.checkwed.unchecked) {    str = str.replace("4 ","");  }
  if(document.schedule.checkthu.checked) {
    str += "5 ";
	str1 += "<THU>"+document.schedule.thufrom.value+"-"+document.schedule.thuto.value+"</THU>";
  }
  if(document.schedule.checkthu.unchecked) {    str = str.replace("5 ","");  }
  if(document.schedule.checkfri.checked) {
    str += "6 ";
	str1 += "<FRI>"+document.schedule.frifrom.value+"-"+document.schedule.frito.value+"</FRI>";
  }
  if(document.schedule.checkfri.unchecked) {    str = str.replace("6 ","");  }
  if(document.schedule.checksat.checked) {
    str += "7 ";
	str1 += "<SAT>"+document.schedule.satfrom.value+"-"+document.schedule.satto.value+"</SAT>";
  }
  if(document.schedule.checksat.unchecked) {    str = str.replace("7 ","");  }

	document.schedule.day_of_week.value = str; 
	document.schedule.avail_hour.value = str1; 
	
	if(document.schedule.syear.value=="" || document.schedule.smonth.value=="" || document.schedule.sday.value=="") {
//	  alert("Please input a Date!!!"); return false;
	} else {
	  return true;
	}
}
function addDataString1() {
  var str="";
  str += document.schedule.day_of_month1.value +" ";
  str += document.schedule.day_of_month2.value +" ";
  str += document.schedule.day_of_month3.value +" ";
  str += document.schedule.day_of_month4.value +" ";
  str += document.schedule.day_of_month5.value +" ";
  str += document.schedule.day_of_month6.value +" ";
  str += document.schedule.day_of_month7.value +" ";
  str += document.schedule.day_of_month8.value +" ";
  str += document.schedule.day_of_month9.value +" ";
  str += document.schedule.day_of_month10.value;

  document.schedule.day_of_month.value = str; 
	if(document.schedule.syear.value=="" || document.schedule.smonth.value=="" || document.schedule.sday.value=="" || document.schedule.eyear.value=="" || document.schedule.emonth.value=="" || document.schedule.eday.value=="") {
	  alert("Please input a Date!!!");
	  return false;
	} else {
	  return true;
	}
}



function inputValidation()
{
	var isInputWeekDaysOK = addDataString();

	var isInputDateRangeOK = addDataString1();

	return isInputWeekDaysOK === true && isInputDateRangeOK === true;

}


function disableSubmitButton()
{
	document.getElementById("submitBTNID").disabled = true;
}

function submission()
{

	if(inputValidation() === true)
	{
		//prevent spam hitting submission, only allow one submission.
		disableSubmitButton();
		return true;
	}

	return false;

}




//-->
</script>
</head>
<body bgcolor="ivory" bgproperties="fixed" onLoad="setfocus()"
	topmargin="0" leftmargin="0" rightmargin="0">
<form method="post" name="schedule" action="schedulecreatedate.jsp"
	onSubmit="submission()">

<table border="0" width="100%">
	<tr>
		<td width="150" bgcolor="#009966"><!--left column-->
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr bgcolor="#486ebd">
				<th align="CENTER" bgcolor="#009966">
				<p>&nbsp;</p>
				<p><font face="Helvetica" color="#FFFFFF">SCHEDULE
				TEMPLATE SETTING</font></p>
				</th>
			</tr>
		</table>
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
				<p>&nbsp;</p>
				<p><font size="-1">1. Use the current R Schedule or
				select a different one from the select field.</font></p>
				<p><font size="-1">2. Type in the start date and end date
				for this R Schedule.</font></p>
				<p><font size="-1">3. Check the day of week which is
				AVAILABLE.</font></p>
				<p><font size="-1">4. Click the 'Next' button.</font></p>
				<p>&nbsp;</p>
				<p>&nbsp;</p>
				</td>
			</tr>
		</table>

		</td>
		<td>

		<center>
		<%
  int rowsAffected = 0;
  GregorianCalendar now = new GregorianCalendar();
  int year = now.get(Calendar.YEAR);
  int month = now.get(Calendar.MONTH)+1;
  int day = now.get(Calendar.DATE);
  String today = now.get(Calendar.YEAR)+"-"+MyDateFormat.getDigitalXX((now.get(Calendar.MONTH)+1))+"-"+MyDateFormat.getDigitalXX(now.get(Calendar.DATE));
  

  RSchedule r = rScheduleDao.search_rschedule_current(request.getParameter("provider_no"), "1", ConversionUtils.fromDateString(request.getParameter("sdate")!=null?request.getParameter("sdate"):today));
  if(r != null) {
  	scheduleRscheduleBean.setRscheduleBean(r.getProviderNo(),ConversionUtils.toDateString(r.getsDate()),ConversionUtils.toDateString(r.geteDate()),r.getAvailable(),r.getDayOfWeek(), null, r.getAvailHour(), r.getCreator());
  } 

  String syear = "",smonth="",sday="",eyear="",emonth="",eday="";
  String[] param2 =new String[7];
  for(int i=0; i<7; i++) {param2[i]="";}
  String[][] param3 =new String[7][2];
  for(int i=0; i<7; i++) {
    for(int j=0; j<2; j++) {
	    param3[i][j]="";
	  }
  }
  if(scheduleRscheduleBean.provider_no!="") {
    syear = ""+MyDateFormat.getYearFromStandardDate(scheduleRscheduleBean.sdate);
    smonth = ""+MyDateFormat.getMonthFromStandardDate(scheduleRscheduleBean.sdate);
    sday = ""+MyDateFormat.getDayFromStandardDate(scheduleRscheduleBean.sdate);
    eyear = ""+MyDateFormat.getYearFromStandardDate(scheduleRscheduleBean.edate);
    emonth = ""+MyDateFormat.getMonthFromStandardDate(scheduleRscheduleBean.edate);
    eday = ""+MyDateFormat.getDayFromStandardDate(scheduleRscheduleBean.edate);

    String availhour = scheduleRscheduleBean.avail_hour;
    StringTokenizer st = new StringTokenizer(scheduleRscheduleBean.day_of_week);
    while (st.hasMoreTokens() ) {
      int j = Integer.parseInt(st.nextToken())-1;
	    int i = j==7?0:j;
      param2[i]="checked";
      if(SxmlMisc.getXmlContent(availhour, ("<"+weekdaytag[i]+">"),"</"+weekdaytag[i]+">") != null) {
	      StringTokenizer sthour = new StringTokenizer(SxmlMisc.getXmlContent(availhour, ("<"+weekdaytag[i]+">"),"</"+weekdaytag[i]+">"), "-");
        j = 0;
		    while (sthour.hasMoreTokens() ) {
          param3[i][j]=sthour.nextToken(); j++;
        }
	    }
    }
  }

%>
		<p>&nbsp;</p>
		<table width="95%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td bgcolor="#CCFFCC"><b><%=request.getParameter("provider_name")%></b>
				<input type="hidden" name="provider_name"
					value="<%=request.getParameter("provider_name")%>"></td>
				<td bgcolor="#CCFFCC">
				<div align="right"><select name="select"
					onChange="selectrschedule(this)">
					<option value="<%=today%>"
						<%=request.getParameter("sdate")!=null?(today.equals(request.getParameter("sdate"))?"selected":""):""%>>Current
					R Schedule</option>
					<%
 
  for(RSchedule rs: rScheduleDao.search_rschedule_future(request.getParameter("provider_no"),"1",ConversionUtils.fromDateString(today))) {
  
%>
					<option value="<%=ConversionUtils.toDateString(rs.getsDate())%>"
						<%=request.getParameter("sdate")!=null?(ConversionUtils.toDateString(rs.getsDate()).equals(request.getParameter("sdate"))?"selected":""):""%>>
					<%=ConversionUtils.toDateString(rs.getsDate())+" ~ "+ConversionUtils.toDateString(rs.geteDate())%></option>
					<%
 	}
%>
				</select></div>
				</td>
			</tr>
			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="2">Date:</td>
				<!--is not available every day of month:-->
			</tr>
			<tr>
				<td bgcolor="#CCFFCC" colspan="2">&nbsp; from<font size="-2">(yyyy-mm-dd)</font>:
				<input type="text" name="syear" size="4" maxlength="4"
					value="<%=syear%>"> - <input type="text" name="smonth"
					size="2" maxlength="2" value="<%=smonth%>"> - <input
					type="text" name="sday" size="2" maxlength="2" value="<%=sday%>">
				&nbsp; &nbsp; to<font size="-2">(yyyy-mm-dd)</font>: <input
					type="text" name="eyear" size="4" maxlength="4" value="<%=eyear%>">
				- <input type="text" name="emonth" size="2" maxlength="2"
					value="<%=emonth%>"> - <input type="text" name="eday"
					size="2" maxlength="2" value="<%=eday%>"> <input
					type="hidden" name="day_of_month1" size="3" maxlength="2"
					onBlur="addDataString1()"> <input type="hidden"
					name="day_of_month2" size="3" maxlength="2"
					onBlur="addDataString1()"> <input type="hidden"
					name="day_of_month3" size="3" maxlength="2"
					onBlur="addDataString1()"> <input type="hidden"
					name="day_of_month4" size="3" maxlength="2"
					onBlur="addDataString1()"> <input type="hidden"
					name="day_of_month5" size="3" maxlength="2"
					onBlur="addDataString1()"> <input type="hidden"
					name="day_of_month6" size="3" maxlength="2"
					onBlur="addDataString1()"> <input type="hidden"
					name="day_of_month7" size="3" maxlength="2"
					onBlur="addDataString1()"> <input type="hidden"
					name="day_of_month8" size="3" maxlength="2"
					onBlur="addDataString1()"> <input type="hidden"
					name="day_of_month9" size="3" maxlength="2"
					onBlur="addDataString1()"> <input type="hidden"
					name="day_of_month10" size="3" maxlength="2"
					onBlur="addDataString1()"> <input type="hidden"
					name="day_of_month" value=""> <input type="hidden"
					name="day_of_year" value=""></td>
			</tr>
			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="2">is available EVERY<font size="-2"> (Day
				of Week)</font>:</td>
			</tr>
			<tr>
				<td nowrap align="right" colspan="2">
				<table border=0 width=80%>
					<tr bgcolor="#CCFFCC">
						<td>
						<p><font size="-1"> <input type="checkbox"
							name="checksun" value="1" onClick="addDataString()"
							<%=param2[0]%>> Sun. </font>
						</td>
						<td><font size="-1">from: <input type="text"
							name="sunfrom" size="5" maxlength="5" value="<%=param3[0][0]%>">
						&nbsp;&nbsp;to: <input type="text" name="sunto" size="5"
							maxlength="5" value="<%=param3[0][1]%>"> </font></td>
					</tr>
					<tr>
						<td><font size="-1"> <input type="checkbox"
							name="checkmon" value="2" onClick="addDataString()"
							<%=param2[1]%>> Mon.</font></td>
						<td><font size="-1">from: <input type="text"
							name="monfrom" size="5" maxlength="5" value="<%=param3[1][0]%>">
						&nbsp;&nbsp;to: <input type="text" name="monto" size="5"
							maxlength="5" value="<%=param3[1][1]%>"> </font></td>
					</tr>
					<tr bgcolor="#CCFFCC">
						<td><font size="-1"> <input type="checkbox"
							name="checktue" value="3" onClick="addDataString()"
							<%=param2[2]%>> Tue.</font></td>
						<td><font size="-1">from: <input type="text"
							name="tuefrom" size="5" maxlength="5" value="<%=param3[2][0]%>">
						&nbsp;&nbsp;to: <input type="text" name="tueto" size="5"
							maxlength="5" value="<%=param3[2][1]%>"> </font></td>
					</tr>
					<tr>
						<td><font size="-1"> <input type="checkbox"
							name="checkwed" value="4" onClick="addDataString()"
							<%=param2[3]%>> Wed.</font></td>
						<td><font size="-1">from: <input type="text"
							name="wedfrom" size="5" maxlength="5" value="<%=param3[3][0]%>">
						&nbsp;&nbsp;to: <input type="text" name="wedto" size="5"
							maxlength="5" value="<%=param3[3][1]%>"> </font></td>
					</tr>
					<tr bgcolor="#CCFFCC">
						<td><font size="-1"> <input type="checkbox"
							name="checkthu" value="5" onClick="addDataString()"
							<%=param2[4]%>> Thu.</font></td>
						<td><font size="-1">from: <input type="text"
							name="thufrom" size="5" maxlength="5" value="<%=param3[4][0]%>">
						&nbsp;&nbsp;to: <input type="text" name="thuto" size="5"
							maxlength="5" value="<%=param3[4][1]%>"> </font></td>
					</tr>
					<tr>
						<td><font size="-1"> <input type="checkbox"
							name="checkfri" value="6" onClick="addDataString()"
							<%=param2[5]%>> Fri.</font></td>
						<td><font size="-1">from: <input type="text"
							name="frifrom" size="5" maxlength="5" value="<%=param3[5][0]%>">
						&nbsp;&nbsp;to: <input type="text" name="frito" size="5"
							maxlength="5" value="<%=param3[5][1]%>"> </font></td>
					</tr>
					<tr bgcolor="#CCFFCC">
						<td><font size="-1"> <input type="checkbox"
							name="checksat" value="7" onClick="addDataString()"
							<%=param2[6]%>> Sat.</font></td>
						<td><font size="-1">from: <input type="text"
							name="satfrom" size="5" maxlength="5" value="<%=param3[6][0]%>">
						&nbsp;&nbsp;to: <input type="text" name="satto" size="5"
							maxlength="5" value="<%=param3[6][1]%>"> </font></td>
					</tr>
				</table>
				</td>
				<input type="hidden" name="day_of_week" value="">
				<input type="hidden" name="avail_hour" value="">
			</tr>
			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td bgcolor="#CCFFCC" colspan="2">
				<div align="right"><input type="hidden" name="provider_no"
					value="<%=request.getParameter("provider_no")%>"> <input
					type="hidden" name="available" value="1"> <input
					type="submit" name="Submit" value=" Next " id ="submitBTNID"> <input
					type="button" name="Cancel" value="Cancel" onClick="window.close()">
				</div>
				</td>
			</tr>
		</table>
		<p>
		<p>&nbsp;</p>
		</center>
		</td>
	</tr>
</table>


</form>
</body>
</html>
