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

<%@ page import="java.util.*, java.sql.*, oscar.*, java.text.*, oscar.login.*,java.net.*" errorPage="../appointment/errorpage.jsp"%>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.common.dao.AppointmentArchiveDao" %>
<%@ page import="org.oscarehr.common.dao.OscarAppointmentDao" %>
<%@ page import="org.oscarehr.common.model.Appointment" %>

<%@ page import="org.oscarehr.common.model.MyGroup"%>
<%@ page import="org.oscarehr.common.dao.MyGroupDao"%>

<%@ page import="org.oscarehr.provider.model.ProviderData"%>
<%@ page import="org.oscarehr.provider.dao.ProviderDataDao"%>
<%@ page import="org.oscarehr.appointment.dao.AppointmentStatusDao" %>
<%@ page import="org.oscarehr.common.model.AppointmentStatus" %>
<%@ page import="org.oscarehr.util.MiscUtils" %>
<%@ page import="org.apache.log4j.Logger" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<jsp:useBean id="daySheetBean" class="oscar.AppointmentMainBean" scope="page" />
<jsp:useBean id="myGroupBean" class="java.util.Properties" scope="page" />
<jsp:useBean id="providerBean" class="java.util.Properties" scope="session" />

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%

	Logger logger = MiscUtils.getLogger();

	String curProviderNo = (String) session.getAttribute("user");
	String orderby = request.getParameter("orderby")!=null?request.getParameter("orderby"):("start_time") ;
    
    java.util.Properties oscarVariables = oscar.OscarProperties.getInstance();
    java.util.Locale vLocale =(java.util.Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY);

    SimpleDateFormat dayFormatter = new SimpleDateFormat("yyyy-MM-dd");
    String deepColor = "#CCCCFF", weakColor = "#EEEEFF" ;
    int count = 0;	

	AppointmentArchiveDao appointmentArchiveDao = (AppointmentArchiveDao)SpringUtils.getBean("appointmentArchiveDao");
	AppointmentStatusDao appointmentStatusDao = (AppointmentStatusDao)SpringUtils.getBean("appointmentStatusDao");
	OscarAppointmentDao appointmentDao = (OscarAppointmentDao)SpringUtils.getBean("oscarAppointmentDao");
	MyGroupDao myGroupDao = SpringUtils.getBean(MyGroupDao.class);
	ProviderDataDao providerDataDao = SpringUtils.getBean(ProviderDataDao.class);


	String [][] dbQueries;
    dbQueries=new String[][] {
		{"search_daysheetall",       "select concat(d.year_of_birth,'/',d.month_of_birth,'/',d.date_of_birth)as dob, d.family_doctor, a.appointment_date, a.provider_no, a.start_time, a.end_time, a.reason, a.name,a.bookingSource, p.last_name, p.first_name, d.sex, d.hin, d.ver, d.family_doctor, d.provider_no as doc_no, d.phone, d.roster_status, p2.last_name as doc_last_name, p2.first_name as doc_first_name, d.chart_no from (appointment a, provider p) left join demographic d on a.demographic_no=d.demographic_no left join provider p2 on d.provider_no=p2.provider_no where a.appointment_date>=? and a.appointment_date<=? and a.start_time>=? and a.end_time<? and a.provider_no=p.provider_no and BINARY a.status not like 'C%' order by p.last_name, p.first_name, a.appointment_date, "+orderby },
		{"search_daysheetsingleproviderall", "select concat(d.year_of_birth,'/',d.month_of_birth,'/',d.date_of_birth)as dob, d.family_doctor, a.appointment_date, a.provider_no, a.start_time, a.end_time, a.reason, a.name,a.bookingSource, p.last_name, p.first_name, d.sex, d.hin, d.ver, d.family_doctor, d.provider_no as doc_no, d.phone, d.roster_status, p2.last_name as doc_last_name, p2.first_name as doc_first_name, d.chart_no  from (appointment a, provider p )left join demographic d on a.demographic_no=d.demographic_no left join provider p2 on d.provider_no=p2.provider_no where a.appointment_date>=? and a.appointment_date<=? and a.start_time>=? and a.end_time<? and a.provider_no=? and BINARY a.status not like 'C%' and a.provider_no=p.provider_no order by a.appointment_date,"+orderby },
		{"search_daysheetnew",       "select concat(d.year_of_birth,'/',d.month_of_birth,'/',d.date_of_birth)as dob, d.family_doctor, a.appointment_date, a.provider_no, a.start_time, a.end_time, a.reason, a.name,a.bookingSource, p.last_name, p.first_name, d.sex, d.hin, d.ver, d.family_doctor, d.provider_no as doc_no, d.phone, d.roster_status, p2.last_name as doc_last_name, p2.first_name as doc_first_name, d.chart_no  from (appointment a, provider p) left join demographic d on a.demographic_no=d.demographic_no left join provider p2 on d.provider_no=p2.provider_no where a.appointment_date=? and a.provider_no=p.provider_no and a.status like binary 't' order by p.last_name, p.first_name, a.appointment_date,"+orderby },
		{"search_daysheetsingleprovidernew", "select concat(d.year_of_birth,'/',d.month_of_birth,'/',d.date_of_birth)as dob, d.family_doctor, a.appointment_date, a.provider_no, a.start_time, a.end_time, a.reason, a.name,a.bookingSource, p.last_name, p.first_name, d.sex, d.hin, d.ver, d.family_doctor, d.provider_no as doc_no, d.phone, d.roster_status, p2.last_name as doc_last_name, p2.first_name as doc_first_name, d.chart_no  " +
				"from (appointment a, provider p) left join demographic d on a.demographic_no=d.demographic_no left join provider p2 on d.provider_no=p2.provider_no " +
				"where a.appointment_date=? and a.provider_no=? and a.status like binary 't' and a.provider_no=p.provider_no order by a.appointment_date,"+orderby },
		{"search_daysheetsingleapptnew", "select concat(d.year_of_birth,'/',d.month_of_birth,'/',d.date_of_birth)as dob, d.family_doctor, a.appointment_date, a.provider_no, a.start_time, a.end_time, a.reason, a.name,a.bookingSource, p.last_name, p.first_name, d.sex, d.hin, d.ver, d.family_doctor, d.provider_no as doc_no, d.phone, d.roster_status, p2.last_name as doc_last_name, p2.first_name as doc_first_name, d.chart_no  " +
				"from (appointment a, provider p) left join demographic d on a.demographic_no=d.demographic_no left join provider p2 on d.provider_no=p2.provider_no " +
				"where a.appointment_date=? and a.provider_no=? and a.status like binary 't' and a.appointment_no=? and a.provider_no=p.provider_no order by a.appointment_date,"+orderby }
	};
   

    daySheetBean.doConfigure(dbQueries);

    boolean isSiteAccessPrivacy=false;
    boolean isTeamAccessPrivacy=false;
%>

<security:oscarSec objectName="_site_access_privacy" roleName="<%=roleName$%>" rights="r" reverse="false"> <%isSiteAccessPrivacy=true; %></security:oscarSec>
<security:oscarSec objectName="_team_access_privacy" roleName="<%=roleName$%>" rights="r" reverse="false"> <%isTeamAccessPrivacy =true;%></security:oscarSec>

<%
List<ProviderData> pdList = null;
HashMap<String,String> providerMap = new HashMap<String,String>();

//multisites function
if (isSiteAccessPrivacy || isTeamAccessPrivacy) {

	if (isSiteAccessPrivacy) 
		pdList = providerDataDao.findByProviderSite(curProviderNo);
	
	if (isTeamAccessPrivacy) 
		pdList = providerDataDao.findByProviderTeam(curProviderNo);

	for(ProviderData providerData : pdList) {
		providerMap.put(providerData.getId(), "true");
	}
}
%>
<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/jquery.js"></script>
<title><bean:message key="report.reportdaysheet.title" /></title>
<link rel="stylesheet" href="../web.css">
<style> td {font-size: 16px;}</style>

<script type="text/javascript" src="<c:out value="${ctx}/share/javascript/prototype.js"/>"></script>
<script language="JavaScript">

function hideOnSource(){
	var selfBooked = document.getElementById('onlySelfBooked');
	var list = $$('tr.oscar');
	if(selfBooked.checked){
		list.invoke('hide');   
	}else{
		list.invoke('show');
	}
}

function printDaysheet() {
	jQuery.ajax({
		type: "post",
		data: "print=yes",
		success: function(msg){
			window.print();
		}
	});
}
</script>
</head>
<%!
	public void setAppointmentStatus(String status, Appointment appt, String user, OscarAppointmentDao appointmentDao)
	{
		appt.setStatus(status);
		appt.setLastUpdateUser(user);
		appt.setUpdateDateTime(new java.util.Date());
		appointmentDao.merge(appt);
	}
%>
<%
	boolean bDob = oscarVariables.getProperty("daysheet_dob", "").equalsIgnoreCase("true") ? true : false;

    GregorianCalendar now=new GregorianCalendar();
    String createtime = now.get(Calendar.YEAR) +"-" +(now.get(Calendar.MONTH)+1) +"-"+now.get(Calendar.DAY_OF_MONTH) +" "+now.get(Calendar.HOUR_OF_DAY)+":"+now.get(Calendar.MINUTE) ;
    now.add(now.DATE, 1);
    int curYear = now.get(Calendar.YEAR);
    int curMonth = (now.get(Calendar.MONTH)+1);
    int curDay = now.get(Calendar.DAY_OF_MONTH);

    String sdate = request.getParameter("sdate")!=null?request.getParameter("sdate"):(curYear+"-"+curMonth+"-"+curDay) ;
    String edate = request.getParameter("edate")!=null?request.getParameter("edate"):"" ;
    String sTime = request.getParameter("sTime")!=null? (request.getParameter("sTime")+":00:00") : "00:00:00" ;
    String eTime = request.getParameter("eTime")!=null? (request.getParameter("eTime")+":00:00") : "24:00:00" ;
    String providerNo = request.getParameter("provider_no")!=null?request.getParameter("provider_no"):"175" ;
	String appointmentNo = request.getParameter("appointment_no")!=null?request.getParameter("appointment_no"):"NULL" ;
	String print = request.getParameter("print")!=null?request.getParameter("print"):"no" ;
	String dsmode = request.getParameter("dsmode");
    ResultSet rsdemo = null ;
    boolean bodd = false;

    //initial myGroupBean if neccessary
    if(providerNo.startsWith("_grp_")) {
    	List<MyGroup> myGroups = myGroupDao.getGroupByGroupNo(providerNo.substring(5));
        for(MyGroup myGroup:myGroups) {
        	myGroupBean.setProperty(myGroup.getId().getProviderNo(),"true");
        }
    }
%>
<body bgproperties="fixed" onLoad="setfocus()" topmargin="0" leftmargin="0" rightmargin="0">

<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr bgcolor="<%=deepColor%>">
		<th><bean:message key="report.reportdaysheet.msgMainLabel" />
			<input type="checkbox" onclick="hideOnSource();" id="onlySelfBooked"/><bean:message key="report.reportdaysheet.msgSelfBookedCheck"/>
		</th>
		<th width="10%" nowrap><%=createtime%> <input type="button"
			name="Button"
			value="<bean:message key="report.reportdaysheet.btnPrint"/>"
			onClick="printDaysheet();"><input type="button" name="Button"
			value="<bean:message key="global.btnExit"/>" onClick="window.close()"></th>
	</tr>
</table>

<%
	boolean bFistL = true; //first line in a table for TH
	String strTemp = "";
	String dateTemp = "";

	/*
		Generate the daysheet
	 */
	if ("all".equals(dsmode))
	{
		//Get all appointments
		if (!providerNo.equals("*") && !providerNo.startsWith("_grp_"))
		{
			//Get all the appointments for the selected provider on the selected date.
			rsdemo = daySheetBean.queryResults(new String[]{sdate, edate, sTime, eTime, providerNo}, "search_daysheetsingleproviderall");
		} else
		{
			//Get all the appointments for all providers on the selected date.
			rsdemo = daySheetBean.queryResults(new String[]{sdate, edate, sTime, eTime}, "search_daysheetall");
		}
	} else if ("new".equals(dsmode))
	{
		//Get only the 'new' appointments. New appointments have status code 't'.
		if (!providerNo.equals("*") && !providerNo.startsWith("_grp_"))
		{
			//Get the new appointments for the selected provider on the selected date
			rsdemo = daySheetBean.queryResults(new String[]{sdate, providerNo}, "search_daysheetsingleprovidernew");
		} else
		{
			//Get the new appointments for all providers on the selected date
			rsdemo = daySheetBean.queryResults(curProviderNo, "search_daysheetnew");
		}
	} else if ("newappt".equals(dsmode))
	{
		//Get a single new appointment for the selected provider
		rsdemo = daySheetBean.queryResults(new String[]{sdate, providerNo, appointmentNo}, "search_daysheetsingleapptnew");
	}

	/*
		Update statues if the print button was pressed and status code T is still Daysheet Printed and enabled
	 */
	AppointmentStatus daysheetPrintedStatus = appointmentStatusDao.findByStatus(AppointmentStatus.APPOINTMENT_STATUS_DAYSHEET_PRINTED);

	if (print.equals("yes") && daysheetPrintedStatus.getDescription().equals("Daysheet Printed") && daysheetPrintedStatus.getActive() == 1)
	{
		//If dsmode is equal to 'new' we're coming from the daysheet report function. If 'newappt' we're coming from the add appointment and print preview function
		if ("new".equals(dsmode))
		{
			//Update all new appointments for the selected provider
			if (!providerNo.equals("*") && !providerNo.startsWith("_grp_"))
			{
				//Archive the appointments for the selected provider on the selected day before updating the status
				try
				{
					List<Appointment> appts = appointmentDao.findByProviderDayAndStatus(providerNo, dayFormatter.parse(sdate), AppointmentStatus.APPOINTMENT_STATUS_NEW);
					for (Appointment appt : appts)
					{
						appointmentArchiveDao.archiveAppointment(appt);
					}
				} catch (java.text.ParseException e)
				{
					logger.error("Cannot archive appt", e);
				}

				for (Appointment a : appointmentDao.findByProviderDayAndStatus(providerNo, dayFormatter.parse(sdate), AppointmentStatus.APPOINTMENT_STATUS_NEW))
				{
					setAppointmentStatus(AppointmentStatus.APPOINTMENT_STATUS_DAYSHEET_PRINTED, a, curProviderNo, appointmentDao);
				}
			} else
			{
				//Update all new appointments for all providers. Archive the appointments for all providers on the selected day before updating the status
				try
				{
					List<Appointment> appts = appointmentDao.findByProviderDayAndStatus(providerNo, dayFormatter.parse(sdate), AppointmentStatus.APPOINTMENT_STATUS_NEW);
					for (Appointment appt : appts)
					{
						appointmentArchiveDao.archiveAppointment(appt);
					}
				} catch (java.text.ParseException e)
				{
					logger.error("Cannot archive appt", e);
				}

				for (Appointment a : appointmentDao.findByDayAndStatus(dayFormatter.parse(sdate), AppointmentStatus.APPOINTMENT_STATUS_NEW))
				{
					setAppointmentStatus(AppointmentStatus.APPOINTMENT_STATUS_DAYSHEET_PRINTED, a, curProviderNo, appointmentDao);
				}
			}
		} else if ("newappt".equals(dsmode))
		{
			//We're updating a single new appointment
			Appointment appt = appointmentDao.find(Integer.parseInt(appointmentNo));

			appointmentArchiveDao.archiveAppointment(appt);

			if (appt != null)
			{
				setAppointmentStatus(AppointmentStatus.APPOINTMENT_STATUS_DAYSHEET_PRINTED, appt, curProviderNo, appointmentDao);
			}
		}
	}

  while (rsdemo.next()) {
    //if it is a group and a group member
	if(!myGroupBean.isEmpty()) {
	  if(myGroupBean.getProperty(rsdemo.getString("provider_no"))==null) continue;
	}

    //multisites. skip record if not belong to same site/team
    if (isSiteAccessPrivacy || isTeamAccessPrivacy) {
    	if(providerMap.get(rsdemo.getString("provider_no"))== null)  continue;
    }

  bodd = bodd?false:true;
	if(!strTemp.equals(rsdemo.getString("provider_no")) || !dateTemp.equals(rsdemo.getString("appointment_date")) ) { //new provider for a new table
	  strTemp = rsdemo.getString("provider_no") ;
          dateTemp = rsdemo.getString("appointment_date");
	  bFistL = true;
	  out.println("</table> <p>") ;
	}
	if(bFistL) {
	  bFistL = false;
    bodd = false ;
%>
<table width="480" border="0" cellspacing="1" cellpadding="0">
	<tr>
		<td><%=providerBean.getProperty(rsdemo.getString("provider_no")) + " - " +dateTemp + (request.getParameter("sTime")!=null? (" " + sTime + "-" + eTime) : "") %>
		</td>
		<td align="right"></td>
	</tr>
</table>
<table width="100%" border="1" bgcolor="#ffffff" cellspacing="0"
	cellpadding="1">
	<tr bgcolor="#CCCCFF" align="center">
		<!--<TH width="14%"><b><a href="reportdaysheet.jsp?provider_no=<%=providerNo%>&sdate=<%=sdate%>&edate=<%=edate%>&orderby=appointment_date"><bean:message key="report.reportdaysheet.msgAppointmentDate"/></a></b></TH>-->
		<TH width="6%"><b><a
			href="reportdaysheet.jsp?provider_no=<%=providerNo%>&sdate=<%=sdate%>&edate=<%=edate%>&orderby=start_time<%=request.getParameter("dsmode")==null?"":"&dsmode="+request.getParameter("dsmode")%>"><bean:message
			key="report.reportdaysheet.msgAppointmentTime" /></a></b></TH>
		<TH width="15%"><b><a
			href="reportdaysheet.jsp?provider_no=<%=providerNo%>&sdate=<%=sdate%>&edate=<%=edate%>&orderby=name<%=request.getParameter("dsmode")==null?"":"&dsmode="+request.getParameter("dsmode")%>"><bean:message
			key="report.reportdaysheet.msgPatientLastName" /></a> </b></TH>
		<!--<TH width="20%"><b><a href="reportdaysheet.jsp?provider_no=<%=providerNo%>&sdate=<%=sdate%>&edate=<%=edate%>&orderby=p_first_name"><bean:message key="report.reportdaysheet.msgPatientFirstName"/></a> </b></TH>-->

 		<TH width="10%"><b><a
                        href="reportdaysheet.jsp?provider_no=<%=providerNo%>&sdate=<%=sdate%>&edate=<%=edate%>&orderby=phone<%=request.getParameter("dsmode")==null?"":"&dsmode="+request.getParameter("dsmode")%>">
                        Phone</a></b></TH>
         <TH width="3%"><b><a
                        href="reportdaysheet.jsp?provider_no=<%=providerNo%>&sdate=<%=sdate%>&edate=<%=edate%>&orderby=sex<%=request.getParameter("dsmode")==null?"":"&dsmode="+request.getParameter("dsmode")%>">
                        Gender </a></b></TH>
         <TH width="9%"><b><a
                        href="reportdaysheet.jsp?provider_no=<%=providerNo%>&sdate=<%=sdate%>&edate=<%=edate%>&orderby=hin<%=request.getParameter("dsmode")==null?"":"&dsmode="+request.getParameter("dsmode")%>">
                        Health Card </a></b></TH>
         <TH width="5%"><b><a
                        href="reportdaysheet.jsp?provider_no=<%=providerNo%>&sdate=<%=sdate%>&edate=<%=edate%>&orderby=ver<%=request.getParameter("dsmode")==null?"":"&dsmode="+request.getParameter("dsmode")%>">
                        Version </a></b></TH>

		<TH width="6%"><b><a
			href="reportdaysheet.jsp?provider_no=<%=providerNo%>&sdate=<%=sdate%>&edate=<%=edate%>&orderby=chart_no<%=request.getParameter("dsmode")==null?"":"&dsmode="+request.getParameter("dsmode")%>"><bean:message
			key="report.reportdaysheet.msgChartNo" /></a></b></TH>
                <!--<TH width="6%"><b><a
			href="reportdaysheet.jsp?provider_no=<%=providerNo%>&sdate=<%=sdate%>&edate=<%=edate%>&orderby=hin<%=request.getParameter("dsmode")==null?"":"&dsmode="+request.getParameter("dsmode")%>"><bean:message
			key="oscarEncounter.search.demographicSearch.msgHin" /></a></b></TH>-->
		<% if(!bDob) {%>
		<TH width="6%"><b><a
			href="reportdaysheet.jsp?provider_no=<%=providerNo%>&sdate=<%=sdate%>&edate=<%=edate%>&orderby=roster_status<%=request.getParameter("dsmode")==null?"":"&dsmode="+request.getParameter("dsmode")%>"><bean:message
			key="report.reportdaysheet.msgRosterStatus" /></a></b></TH>
		<% } else {%>
		<TH width="10%"><b>DOB</b></TH>
		<% }%>
		
		
		<th><bean:message key="report.reportdaysheet.msgBookingStatus" /></th>
		
		<TH width="30%"><b><bean:message
			key="report.reportdaysheet.msgComments" /></b></TH>
	</tr>
	<%
    }
count++;
%>
<tr bgcolor="<%=bodd?"#EEEEFF":"white"%>"  class="<%=rsdemo.getString("bookingSource")==null?"oscar":"self"%>"    id="r<%=count %>" >
      <!--<td align="center" nowrap><%=rsdemo.getString("appointment_date")%></td>-->
      <td align="center" nowrap title="<%="End Time: "+rsdemo.getString("end_time")%>"><%=rsdemo.getString("start_time").substring(0,5)%></td>
      <td align="left"><%=rsdemo.getString("name")==null?".":""%><%=Misc.toUpperLowerCase(rsdemo.getString("name"))%></td>
      <td align="center">&nbsp;<%=rsdemo.getString("phone")==null?"":rsdemo.getString("phone")%>&nbsp;</td>
      <td align="center">&nbsp;<%=rsdemo.getString("sex")==null?"":rsdemo.getString("sex")%>&nbsp;</td>
      <td align="center">&nbsp;<%=rsdemo.getString("hin")==null?"":rsdemo.getString("hin")%>&nbsp;</td>
      <td align="center">&nbsp;<%=rsdemo.getString("ver")==null?"":rsdemo.getString("ver")%>&nbsp;</td>
      <td align="center">&nbsp;<%=rsdemo.getString("chart_no")==null?"":rsdemo.getString("chart_no")%>&nbsp;</td>
      <!--<td align="center">&nbsp;<%=rsdemo.getString("family_doctor")==null?"":rsdemo.getString("family_doctor")%>&nbsp;</td>-->

<% if(!bDob) {%>
      <td align="center">&nbsp;<%=rsdemo.getString("roster_status")==null?"":rsdemo.getString("roster_status")%>&nbsp;</td>
<% } else {
		String dob = rsdemo.getString("dob");
%>
		<td align="center">&nbsp;<%=dob==null?"":dob%></td>
		<% }%>
		
		<td>
			<%if(rsdemo.getString("bookingSource")==null){%>
			&nbsp;
			<%}else{%>
			<bean:message key="report.reportdaysheet.msgSelfBooked"/>
			<%}%>
	    </td>
		<td>
		<% if ( rsdemo.getString("doc_no") != null && ! daySheetBean.getString(rsdemo,"doc_no").equals("") && ! daySheetBean.getString(rsdemo,"doc_no").equals(daySheetBean.getString(rsdemo,"provider_no")) ) {

                    String doc_first_name = daySheetBean.getString(rsdemo,"doc_first_name");
                    char initial = 0x20;
                    if( doc_first_name.length() > 0 ) {
                        initial = doc_first_name.charAt(0);
                    }
%>
		[<%=daySheetBean.getString(rsdemo,"doc_last_name")%>, <%=initial%>]
		&nbsp; <% } %> <% if ( bDob && daySheetBean.getString(rsdemo,"family_doctor") != null) {
              String rd = SxmlMisc.getXmlContent(daySheetBean.getString(rsdemo,"family_doctor"),"rd");
              rd = rd !=null ? rd : "" ;
          %> [<%=rd%>]&nbsp; <% } %> <%=daySheetBean.getString(rsdemo,"reason")%>&nbsp;</td>
	</tr>
	<%
  }
%>

</table>
</body>
</html:html>
