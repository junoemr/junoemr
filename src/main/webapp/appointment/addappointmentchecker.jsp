<%--

    Copyright (c) 2005-2012. OscarHost Inc. All Rights Reserved.
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

    This software was written for
    OscarHost, a Division of Cloud Practice Inc.

--%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="org.oscarehr.util.SpringUtils" %>
<%@page import="org.oscarehr.util.AppointmentUtil" %>
<%@page import="org.oscarehr.common.model.Demographic" %>
<%@page import="org.oscarehr.common.dao.DemographicDao" %>
<%@page import="org.oscarehr.common.model.DemographicCust" %>
<%@page import="org.oscarehr.common.model.DemographicExt" %>
<%@page import="org.oscarehr.common.dao.DemographicExtDao" %>
<%@page import="org.oscarehr.util.MiscUtils" %>
<%@ page contentType="application/json" %>
<%
String demo_no = request.getParameter("demographic_no");
if(demo_no == null){
	return;
}

DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographicDao");
Demographic demo = demographicDao.getDemographic(demo_no);
Date last_update_date = demo.getLastUpdateDate();
SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");

String last_appointment_date_string = AppointmentUtil.getLastAppointment(demo_no);
String result = "update_required";
String message = "";

Date last_appointment_date = null;

Date today_date = new Date();
String today_string = dt.format(today_date);
Date today = dt.parse(today_string);

// no appointment ever for this demographic
if(last_appointment_date_string.equals("(none)")){
	result="update_required";
	message="No appointments found";
}else{
	last_appointment_date = dt.parse(last_appointment_date_string);
	if(today != null){
		message += "Today: "+today.toString()+ ". ";
	}
	if(last_appointment_date != null){
		message +="Last appointment was on: "+last_appointment_date.toString()+". ";
	}
	if(last_update_date != null){
		message +="Last demographic update was on: "+last_update_date.toString()+".";
	}
}

// Demographic was updated today
if( last_update_date != null &&
	today_date != null &&
    last_update_date.compareTo(today) == 0)
{
	result="success";
	message = "Updated today";
}

Calendar cal = Calendar.getInstance();
cal.add(Calendar.MONTH, -3);
Date three_months_ago = cal.getTime();

// Check if last appointment is less than three months ago
if(last_appointment_date != null &&
   last_appointment_date.after(three_months_ago))
{
	result = "success";	
	message = "Last appointment was on "+last_appointment_date.toString();

}

// If we're all good in checking if the last appointment is less than 3 months ago,
// Let's check if they have missing demographic info
if(result.equals("success")){
	// Check if demographic has email and cellphone
	
	DemographicExtDao demographicExtDao = SpringUtils.getBean(DemographicExtDao.class);
	DemographicExt demoext_cellphone = demographicExtDao.getLatestDemographicExt(Integer.parseInt(demo_no),"demo_cell");
	String cellphone = "";
	if(demoext_cellphone != null){
		cellphone = demoext_cellphone.getValue();
	}
	
	if(cellphone.length() == 0){
		result = "update_required";
		message = "No cellphone";
	}
	
	String email = demo.getEmail();
	if(email == null || email.length() == 0){
		result = "update_required";
		message = "No email";
	}
}
%>
{"result":"<%=result%>", "message":"<%=message%>"}