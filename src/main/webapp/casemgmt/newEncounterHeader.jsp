
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
<%@ page import="org.oscarehr.util.MiscUtils"%>
<%@ page import="org.oscarehr.PMmodule.caisi_integrator.CaisiIntegratorManager"%>
<%@ page import="org.oscarehr.util.LoggedInInfo" %>
<%@ page import="org.oscarehr.common.model.Facility" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="org.oscarehr.preferences.service.SystemPreferenceService" %>
<%@ page import="org.oscarehr.common.model.UserProperty" %>
<%@ page import="oscar.oscarProvider.data.ProviderColourUpdater" %>
<%@ page import="oscar.oscarEncounter.data.EctProviderData" %>
<%@ page import="org.oscarehr.PMmodule.utility.UtilDateUtilities" %>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>
<%
	SystemPreferenceService systemPreferenceService = SpringUtils.getBean(SystemPreferenceService.class);
	LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

	oscar.oscarEncounter.pageUtil.EctSessionBean bean = (oscar.oscarEncounter.pageUtil.EctSessionBean)request.getSession().getAttribute("EctSessionBean");
	if (bean == null)
	{
		response.sendRedirect("error.jsp");
		return;
	}

	Facility facility = loggedInInfo.getCurrentFacility();

	String demoNo = bean.demographicNo;
	String famDocName;
	String famDocSurname;
	String famDocColour;
	String user = (String) session.getAttribute("user");
	String roleName$ = session.getAttribute("userrole") + "," + session.getAttribute("user");
	ProviderColourUpdater colourUpdater = new ProviderColourUpdater(user);
	String userColour = colourUpdater.getColour();
	//default blue if no preference set
	if (userColour == null || userColour.isEmpty())
	{
		userColour = ProviderColourUpdater.DEFAULT_COLOUR_BLUE;
	}

	//we calculate inverse of provider colour for text
	String inverseUserColour = ProviderColourUpdater.getInverseColour(userColour);

	if(bean.familyDoctorNo == null || bean.familyDoctorNo.isEmpty())
	{
		famDocName = "";
		famDocSurname = "";
		famDocColour = "";
	}
	else
	{
		EctProviderData.Provider prov = new EctProviderData().getProvider(bean.familyDoctorNo);
		famDocName =  prov == null || prov.getFirstName() == null ? "" : prov.getFirstName();
		famDocSurname = prov == null || prov.getSurname() == null ? "" : prov.getSurname();
		colourUpdater = new ProviderColourUpdater(bean.familyDoctorNo);
		famDocColour = colourUpdater.getColour();
		if (famDocColour == null || famDocColour.isEmpty())
		{
			famDocColour = ProviderColourUpdater.DEFAULT_COLOUR_BLUE;
		}
	}

    String pAge = Integer.toString(UtilDateUtilities.calcAge(bean.yearOfBirth,bean.monthOfBirth,bean.dateOfBirth));
    %>

    <c:set var="ctx" value="${pageContext.request.contextPath}" scope="request"/>
    
<div style="float:left; width: 100%; padding-left:2px; text-align:left; font-size: 12px; color:<%=inverseUserColour%>; background-color:<%=userColour%>" id="encounterHeader">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
<td>
    <security:oscarSec roleName="<%=roleName$%>" objectName="_newCasemgmt.doctorName" rights="r">
    <span style="border-bottom: medium solid <%=famDocColour%>"><bean:message key="oscarEncounter.Index.msgMRP"/>&nbsp;&nbsp;
    <%=famDocName.toUpperCase()%> <%=famDocSurname.toUpperCase()%>  </span>
	</security:oscarSec>
    <span class="Header" style="color:<%=inverseUserColour%>; background-color:<%=userColour%>">
        <%
        
            String appointmentNo = request.getParameter("appointmentNo");
            String winName = "Master" + bean.demographicNo;
            String url = "/demographic/demographiccontrol.jsp?demographic_no=" + bean.demographicNo + "&amp;displaymode=edit&amp;dboperation=search_detail&appointment="+appointmentNo;
        %>
        <a href="#" onClick="popupPage(700,1000,'<%=winName%>','<c:out value="${ctx}"/><%=url%>'); return false;" title="<bean:message key="provider.appointmentProviderAdminDay.msgMasterFile"/>"><%=bean.patientLastName %>, <%=bean.patientFirstName%></a> <%=bean.patientSex%> <%=bean.patientAge%>
		<% if (oscar.OscarProperties.getInstance().isEChartAdditionalPatientInfoEnabled())
		{%>
            <%=bean.patientBirthdate%>
        <%}%>
        &nbsp;<oscar:phrverification demographicNo="<%=demoNo%>"><bean:message key="phr.verification.link"/></oscar:phrverification> &nbsp;<%=StringUtils.trimToEmpty(bean.phone)%>
		<span id="encounterHeaderExt"></span>
		<security:oscarSec roleName="<%=roleName$%>" objectName="_newCasemgmt.apptHistory" rights="r">
		<a href="javascript:popupPage(400,850,'ApptHist','<c:out value="${ctx}"/>/demographic/demographiccontrol.jsp?demographic_no=<%=bean.demographicNo%>&amp;last_name=<%=bean.patientLastName.replaceAll("'", "\\\\'")%>&amp;first_name=<%=bean.patientFirstName.replaceAll("'", "\\\\'")%>&amp;orderby=appointment_date&amp;displaymode=appt_history&amp;dboperation=appt_history&amp;limit1=0&amp;limit2=25')" style="font-size: 11px;text-decoration:none;" title="<bean:message key="oscarEncounter.Header.nextApptMsg"/>"><span style="margin-left:20px;"><bean:message key="oscarEncounter.Header.nextAppt"/>: <oscar:nextAppt demographicNo="<%=bean.demographicNo%>"/></span></a>
		</security:oscarSec>
        &nbsp;&nbsp;

		<% if (oscar.OscarProperties.getInstance().isEChartAdditionalPatientInfoEnabled())
		{%>
            <%=bean.referringDoctorName%>
            <%=bean.referringDoctorNumber%>
            &nbsp;&nbsp;
           <% if (bean.hasRosterDate())
		   { %>
	            Referral date:
	            <%=bean.rosterDate.toString()%>
	    	<%}%>
        <%}%>
		
        <% if(oscar.OscarProperties.getInstance().hasProperty("ONTARIO_MD_INCOMINGREQUESTOR")){%>
           <a href="javascript:void(0)" onClick="popupPage(600,175,'Calculators','<c:out value="${ctx}"/>/common/omdDiseaseList.jsp?sex=<%=bean.patientSex%>&age=<%=pAge%>'); return false;" ><bean:message key="oscarEncounter.Header.OntMD"/></a>
        <%}%>
        <%=getEChartLinks() %>
        &nbsp;&nbsp;
        
		<%
		if (facility.isIntegratorEnabled()){
			int secondsTillConsideredStale = -1;
			try{
				secondsTillConsideredStale = Integer.parseInt(oscar.OscarProperties.getInstance().getProperty("seconds_till_considered_stale"));
			}catch(Exception e){
				MiscUtils.getLogger().error("OSCAR Property: seconds_till_considered_stale did not parse to an int",e);
			}
			
			boolean allSynced = true;
			
			try{
				allSynced  = CaisiIntegratorManager.haveAllRemoteFacilitiesSyncedIn(loggedInInfo, loggedInInfo.getCurrentFacility(), secondsTillConsideredStale,false); 
				CaisiIntegratorManager.setIntegratorOffline(session, false);	
			}catch(Exception remoteFacilityException){
				MiscUtils.getLogger().error("Error checking Remote Facilities Sync status",remoteFacilityException);
				CaisiIntegratorManager.checkForConnectionError(session, remoteFacilityException);
			}
			if(secondsTillConsideredStale == -1){  
				allSynced = true; 
			}
		%>
			<%if (CaisiIntegratorManager.isIntegratorOffline(session)) {%>
    			<div style="background: none repeat scroll 0% 0% red; color: white; font-weight: bold; padding-left: 10px; margin-bottom: 2px;"><bean:message key="oscarEncounter.integrator.NA"/></div>
    		<%}else if(!allSynced) {%>
    			<div style="background: none repeat scroll 0% 0% orange; color: white; font-weight: bold; padding-left: 10px; margin-bottom: 2px;"><bean:message key="oscarEncounter.integrator.outOfSync"/>
    			&nbsp;&nbsp;
				<a href="javascript:void(0)" onClick="popupPage(233,600,'ViewICommun','<c:out value="${ctx}"/>/admin/viewIntegratedCommunity.jsp'); return false;" >Integrator</a>
    			</div>
	    	<%}else{%>
	    		<a href="javascript:void(0)" onClick="popupPage(233,600,'ViewICommun','<c:out value="${ctx}"/>/admin/viewIntegratedCommunity.jsp'); return false;" >I</a>
	    	<%}%>
	  <%}%>    
   </span>
</td>
<td align=right>
	<span class="HelpAboutLogout">
		<%
            if (systemPreferenceService.isPreferenceEnabled(UserProperty.INTEGRATION_SUPERADMIN_IMDHEALTH_ENABLED, false))
            {
        %>
                <script src="../integration/imdHealth/imdHealthUtils.js"></script>
                <a style="font-size:10px;font-style:normal;"  href="javascript:void(0)" onclick="Juno.Integration.iMDHealth.openIMDHealth()"><b>Patient Education</b></a> |
		<%
            }
			if (systemPreferenceService.isPreferenceEnabled(UserProperty.CARE_CONNECT_ENABLED, false))
			{
		%>
				<a style="font-size:10px;font-style:normal;"  href="javascript:void(0)" onclick="window.open('../integration/careConnect/careConnectForm.jsp?demoNo=' + '<%=demoNo%>', 'CareConnectPopup', 'width=1200,height=800');">CareConnect</a> |
		<%
			}
		%>
		<oscar:help keywords="&Title=Chart+Interface&portal_type%3Alist=Document" key="app.top1" style="font-size:10px;font-style:normal;"/>&nbsp;|
		<a style="font-size:10px;font-style:normal;" href="<%=request.getContextPath()%>/oscarEncounter/About.jsp" target="_new"><bean:message key="global.about" /></a>
	</span>
</td>
</tr>
</table>
</div>
<%!

String getEChartLinks(){
	String str = oscar.OscarProperties.getInstance().getProperty("ECHART_LINK");
		if (str == null){
			return "";
		}
		try{
			String[] httpLink = str.split("\\|"); 
 			return "<a target=\"_blank\" href=\""+httpLink[1]+"\">"+httpLink[0]+"</a>";
		}catch(Exception e){
			MiscUtils.getLogger().error("ECHART_LINK is not in the correct format. title|url :"+str, e);
		}
		return "";
}
%>
