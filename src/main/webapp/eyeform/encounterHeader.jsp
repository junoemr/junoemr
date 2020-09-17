
<%--


    Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
    Centre for Research on Inner City Health, St. Michael's Hospital,
    Toronto, Ontario, Canada

--%>


 <%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
 <%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>
 <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
 <%@ page import="oscar.oscarEncounter.data.*, oscar.oscarProvider.data.*, oscar.util.UtilDateUtilities" %>
<%@page import="org.oscarehr.util.LoggedInInfo"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
      String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
      boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_eChart" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_eChart");%>
</security:oscarSec>
<%
if(!authed) {
	return;
}
%>


<%
    oscar.oscarEncounter.pageUtil.EctSessionBean bean = null;
    if((bean=(oscar.oscarEncounter.pageUtil.EctSessionBean)request.getSession().getAttribute("EctSessionBean"))==null) {
        response.sendRedirect("error.jsp");
        return;
    }

    String demoNo = bean.demographicNo;
    EctPatientData.Patient pd = new EctPatientData().getPatient(LoggedInInfo.getLoggedInInfoFromSession(request) , demoNo);
    String famDocName, famDocSurname, famDocColour, inverseUserColour, userColour;
    String user = (String) session.getAttribute("user");
    ProviderColourUpdater colourUpdater = new ProviderColourUpdater(user);
    userColour = colourUpdater.getColour();
    if(userColour == null || userColour.length() == 0)
    {
        userColour = ProviderColourUpdater.DEFAULT_COLOUR_BLUE;   //default blue if no preference set
    }
    //we calculate inverse of provider colour for text
    inverseUserColour = ProviderColourUpdater.getInverseColour(userColour);

    if(bean.familyDoctorNo.equals("")) {
        famDocName = "";
        famDocSurname = "";
        famDocColour = "";
    }
    else {
        EctProviderData.Provider prov = new EctProviderData().getProvider(bean.familyDoctorNo);
        famDocName = prov.getFirstName();
        famDocSurname = prov.getSurname();
        colourUpdater = new ProviderColourUpdater(bean.familyDoctorNo);
        famDocColour = colourUpdater.getColour();
		if(famDocColour == null || famDocColour.length() == 0)
		{
			famDocColour = "#CCCCFF";
		}
	}

    String patientName = pd.getFirstName()+" "+pd.getSurname();
    String patientAge = pd.getAge();
    String patientSex = pd.getSex();
    String pAge = Integer.toString(UtilDateUtilities.calcAge(bean.yearOfBirth,bean.monthOfBirth,bean.dateOfBirth));

    java.util.Locale vLocale =(java.util.Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY);
    
    //referring doctor
    org.oscarehr.common.dao.DemographicDao dao = (org.oscarehr.common.dao.DemographicDao)org.oscarehr.util.SpringUtils.getBean("demographicDao");
	org.oscarehr.common.model.Demographic d= dao.getDemographic(demoNo);
	String familyDoctorXml = d.getFamilyDoctor();
	String rd = "";
	if(familyDoctorXml != null) {
		rd = oscar.SxmlMisc.getXmlContent(familyDoctorXml,"rd");
	}
	
	//appointment reason
	String apptNo = request.getParameter("appointmentNo");
	String reason = new String();
	if(apptNo != null && apptNo.length()>0) {
		org.oscarehr.common.dao.OscarAppointmentDao appointmentDao = org.oscarehr.util.SpringUtils.getBean(org.oscarehr.common.dao.OscarAppointmentDao.class);
		org.oscarehr.common.model.Appointment a = appointmentDao.find(Integer.parseInt(apptNo));
		if(a != null) {
			reason = a.getReason();
		}
	}
    %>

    <c:set var="ctx" value="${pageContext.request.contextPath}" scope="request"/>
   
    <span class="Header" style="color:<%=inverseUserColour%>; background-color:<%=userColour%>; font-weight:normal;">
        <%
            String winName = "Master" + bean.demographicNo;
            String url = "/demographic/demographiccontrol.jsp?demographic_no=" + bean.demographicNo + "&amp;displaymode=edit&amp;dboperation=search_detail";
        %>
        <span style="font-weight:bold;">
        	<a href="#" onClick="popupPage(700,1000,'<%=winName%>','<c:out value="${ctx}"/><%=url%>'); return false;" title="<bean:message key="provider.appointmentProviderAdminDay.msgMasterFile"/>"><%=bean.patientLastName %>, <%=bean.patientFirstName%></a> <%=bean.patientSex%> <%=bean.patientAge%>
       	</span>  
	<bean:message key="oscarEncounter.Index.msgMRP"/>:&nbsp;<span style="font-weight:bold;"><%=famDocName%> <%=famDocSurname%></span> 
	REF:&nbsp;<span style="font-weight:bold;"><%=rd%></span>  
 	REASON:&nbsp;<span style="font-weight:bold;"><%=reason%></span>
 </span>
