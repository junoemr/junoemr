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
<security:oscarSec roleName="<%=roleName$%>" objectName="_demographic" rights="w" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect(request.getContextPath() + "/securityError.jsp?type=_demographic");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>

<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.oscarehr.PMmodule.model.Program"%>
<%@page import="org.oscarehr.PMmodule.service.AdmissionManager"%>
<%@page import="org.oscarehr.PMmodule.service.ProgramManager"%>
<%@page import="org.oscarehr.PMmodule.web.GenericIntakeEditAction"%>
<%@page import="org.oscarehr.common.OtherIdManager"%>
<%@page import="org.oscarehr.common.dao.DemographicArchiveDao"%>
<%@page import="org.oscarehr.common.dao.DemographicDao"%>
<%@page import="org.oscarehr.common.model.ConsentType" %>
<%@page import="org.oscarehr.common.model.Demographic" %>
<%@page import="org.oscarehr.demographic.dao.DemographicCustDao" %>
<%@page import="org.oscarehr.demographic.model.DemographicCust" %>
<%@page import="org.oscarehr.demographic.model.DemographicExt" %>
<%@page import="org.oscarehr.managers.DemographicManager" %>
<%@page import="org.oscarehr.managers.PatientConsentManager" %>
<%@page import="org.oscarehr.provider.model.ProviderPreventionManager" %>
<%@page import="org.oscarehr.provider.service.RecentDemographicAccessService" %>
<%@page import="org.oscarehr.util.LoggedInInfo" %>
<%@page import="org.oscarehr.util.MiscUtils" %>
<%@page import="org.oscarehr.util.SpringUtils" %>
<%@page import="oscar.MyDateFormat" %>
<%@page import="oscar.OscarProperties" %>
<%@page import="oscar.log.LogAction" %>
<%@page import="oscar.log.LogConst" %>
<%@page import="oscar.oscarWaitingList.util.WLWaitingListUtil" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.HashSet" %>
<%@page import="java.util.List" %>
<%@page import="java.util.Set" %>
<%@page import="java.util.Date" %>
<%@page import="static oscar.util.StringUtils.filterControlCharacters" %>
<%@page import="org.oscarehr.demographic.service.DemographicService" %>
<%@page errorPage="errorpage.jsp"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<%
	OscarProperties oscarVariables = oscar.OscarProperties.getInstance();

	DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographicDao");
	DemographicArchiveDao demographicArchiveDao = (DemographicArchiveDao)SpringUtils.getBean("demographicArchiveDao");
	DemographicCustDao demographicCustDao = (DemographicCustDao)SpringUtils.getBean("demographicCustDao");
	DemographicManager demographicManager = SpringUtils.getBean(DemographicManager.class);
	RecentDemographicAccessService recentDemographicAccessService = SpringUtils.getBean(RecentDemographicAccessService.class);
	DemographicService demographicService = (DemographicService) SpringUtils.getBean("demographic.service.DemographicService");

	LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
	
%>

<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script></head>

<body>
<center>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr bgcolor="#486ebd">
		<th align="CENTER"><font face="Helvetica" color="#FFFFFF">
		UPDATE demographic RECORD</font></th>
	</tr>
</table>
<%

	String loggedInProviderNo = loggedInInfo.getLoggedInProviderNo();
	String currentUserNoStr = (String) session.getAttribute("user");
	String demographicNoStr = request.getParameter("demographic_no");
	int demographicNo = Integer.parseInt(demographicNoStr);

	/* Prepare the demographic model */
	Demographic demographic = demographicDao.getDemographic(demographicNoStr);

	String hin = request.getParameter("hin").replaceAll("[^0-9a-zA-Z]", "");
	String ver = request.getParameter("ver");
	String hcType = request.getParameter("hc_type");
	String previousPatientStatus = demographic.getPatientStatus();

	demographic.setLastName(request.getParameter("last_name").trim());
	demographic.setFirstName(request.getParameter("first_name").trim());
	demographic.setAddress(request.getParameter("address"));
	demographic.setCity(request.getParameter("city"));
	demographic.setProvince(request.getParameter("province"));
	demographic.setPostal(request.getParameter("postal"));
	demographic.setPhone(filterControlCharacters(request.getParameter("phone")));
	demographic.setPhone2(filterControlCharacters(request.getParameter("phone2")));
	demographic.setEmail(StringUtils.trimToNull(request.getParameter("email")));
	demographic.setMyOscarUserName(StringUtils.trimToNull(request.getParameter("myOscarUserName")));
	demographic.setYearOfBirth(request.getParameter("year_of_birth"));
	demographic.setMonthOfBirth(request.getParameter("month_of_birth")!=null && request.getParameter("month_of_birth").length()==1 ? "0"+request.getParameter("month_of_birth") : request.getParameter("month_of_birth"));
	demographic.setDateOfBirth(request.getParameter("date_of_birth")!=null && request.getParameter("date_of_birth").length()==1 ? "0"+request.getParameter("date_of_birth") : request.getParameter("date_of_birth"));
	demographic.setHin(hin);
	demographic.setVer(ver);
	demographic.setRosterStatus(request.getParameter("roster_status"));
	demographic.setPatientStatus(request.getParameter("patient_status"));
	demographic.setChartNo(request.getParameter("chart_no"));
	demographic.setProviderNo(StringUtils.trimToNull(request.getParameter("provider_no")));
	demographic.setSex(request.getParameter("sex"));
	demographic.setPcnIndicator(request.getParameter("pcn_indicator"));
	demographic.setHcType(hcType);
	demographic.setFamilyDoctor("<rdohip>" + StringUtils.trimToEmpty(request.getParameter("referral_doctor_no")) + "</rdohip>" +
			"<rd>" + StringUtils.trimToEmpty(request.getParameter("referral_doctor_name")) + "</rd>");
	demographic.setFamilyDoctor2("<fd>" + StringUtils.trimToEmpty(request.getParameter("family_doctor_no")) + "</fd>" +
			"<fdname>" + StringUtils.trimToEmpty(request.getParameter("family_doctor_name")) + "</fdname>");
	demographic.setCountryOfOrigin(request.getParameter("countryOfOrigin"));
	demographic.setNewsletter(request.getParameter("newsletter"));
	demographic.setSin(request.getParameter("sin"));
	demographic.setTitle(request.getParameter("title"));
	demographic.setOfficialLanguage(request.getParameter("official_lang"));
	demographic.setSpokenLanguage(request.getParameter("spoken_lang"));
	demographic.setRosterTerminationReason(request.getParameter("roster_termination_reason"));
	demographic.setLastUpdateUser(currentUserNoStr);
	demographic.setLastUpdateDate(new java.util.Date());
	demographic.setNameOfMother(StringUtils.trimToNull(request.getParameter("nameOfMother")));
	demographic.setNameOfFather(StringUtils.trimToNull(request.getParameter("nameOfFather")));

	if(oscarVariables.isPropertyActive("demographic_veteran_no"))
	{
		demographic.setVeteranNo(StringUtils.trimToNull(request.getParameter("veteranNo")));
	}

	String yearTmp = StringUtils.trimToNull(request.getParameter("date_joined_year"));
	String monthTmp = StringUtils.trimToNull(request.getParameter("date_joined_month"));
	String dayTmp = StringUtils.trimToNull(request.getParameter("date_joined_date"));
	if(yearTmp != null && monthTmp != null && dayTmp != null)
	{
		demographic.setDateJoined(MyDateFormat.getSysDate(yearTmp + '-' + monthTmp + '-' + dayTmp));
	}

	yearTmp = StringUtils.trimToNull(request.getParameter("end_date_year"));
	monthTmp = StringUtils.trimToNull(request.getParameter("end_date_month"));
	dayTmp = StringUtils.trimToNull(request.getParameter("end_date_date"));
	if(yearTmp != null && monthTmp != null && dayTmp != null)
	{
		demographic.setEndDate(MyDateFormat.getSysDate(yearTmp + '-' + monthTmp + '-' + dayTmp));
	}

	yearTmp = StringUtils.trimToNull(request.getParameter("eff_date_year"));
	monthTmp = StringUtils.trimToNull(request.getParameter("eff_date_month"));
	dayTmp = StringUtils.trimToNull(request.getParameter("eff_date_date"));
	if(yearTmp != null && monthTmp != null && dayTmp != null)
	{
		demographic.setEffDate(MyDateFormat.getSysDate(yearTmp + '-' + monthTmp + '-' + dayTmp));
	}

	yearTmp = StringUtils.trimToNull(request.getParameter("hc_renew_date_year"));
	monthTmp = StringUtils.trimToNull(request.getParameter("hc_renew_date_month"));
	dayTmp = StringUtils.trimToNull(request.getParameter("hc_renew_date_date"));
	if(yearTmp != null && monthTmp != null && dayTmp != null)
	{
		demographic.setHcRenewDate(MyDateFormat.getSysDate(yearTmp + '-' + monthTmp + '-' + dayTmp));
	}

	yearTmp = StringUtils.trimToNull(request.getParameter("roster_date_year"));
	monthTmp = StringUtils.trimToNull(request.getParameter("roster_date_month"));
	dayTmp = StringUtils.trimToNull(request.getParameter("roster_date_day"));

	if(yearTmp != null && monthTmp != null && dayTmp != null)
	{
		demographic.setRosterDate(MyDateFormat.getSysDate(yearTmp+'-'+monthTmp+'-'+dayTmp));
	}

	yearTmp=StringUtils.trimToNull(request.getParameter("roster_termination_date_year"));
	monthTmp=StringUtils.trimToNull(request.getParameter("roster_termination_date_month"));
	dayTmp=StringUtils.trimToNull(request.getParameter("roster_termination_date_day"));

	if(yearTmp != null && monthTmp != null && dayTmp != null)
	{
		demographic.setRosterTerminationDate(MyDateFormat.getSysDate(yearTmp + '-' + monthTmp + '-' + dayTmp));
	}

	/* Set patient status date */
	if (!(demographic.getPatientStatus().equals(previousPatientStatus)))
	{
		demographic.setPatientStatusDate(new Date());
	}

	/* patient consent */
	if(OscarProperties.getInstance().getBooleanProperty("USE_NEW_PATIENT_CONSENT_MODULE", "true"))
	{
		// Retrieve and set patient consents.
		PatientConsentManager patientConsentManager = SpringUtils.getBean(PatientConsentManager.class);
		List<ConsentType> consentTypes = patientConsentManager.getConsentTypes();
		String consentTypeId = null;
		int patientConsentIdInt = 0;

		for(ConsentType consentType : consentTypes)
		{
			consentTypeId = request.getParameter(consentType.getType());
			String patientConsentId = request.getParameter(consentType.getType() + "_id");
			if(patientConsentId != null)
			{
				patientConsentIdInt = Integer.parseInt(patientConsentId);
			}

			// checked box means add or edit consent. 
			if(consentTypeId != null)
			{
				patientConsentManager.addConsent(loggedInInfo, demographic.getDemographicNo(), Integer.parseInt(consentTypeId));
				// unchecked and patientConsentId > 0 could mean the patient opted out.
			}
			else if(patientConsentIdInt > 0)
			{
				patientConsentManager.optoutConsent(loggedInInfo, patientConsentIdInt);
			}
		}
	}
	
	/* Prepare demographic extension models */
	List<DemographicExt> extensions = new ArrayList<DemographicExt>();

	extensions.add(new DemographicExt(request.getParameter("demo_cell_id"), currentUserNoStr, demographicNo, "demo_cell", request.getParameter("demo_cell")));
	extensions.add(new DemographicExt(request.getParameter("aboriginal_id"), currentUserNoStr, demographicNo, "aboriginal", request.getParameter("aboriginal")));
	extensions.add(new DemographicExt(request.getParameter("hPhoneExt_id"), currentUserNoStr, demographicNo, "hPhoneExt", request.getParameter("hPhoneExt")));
	extensions.add(new DemographicExt(request.getParameter("wPhoneExt_id"), currentUserNoStr, demographicNo, "wPhoneExt", request.getParameter("wPhoneExt")));
	extensions.add(new DemographicExt(request.getParameter("cytolNum_id"), currentUserNoStr, demographicNo, "cytolNum",  request.getParameter("cytolNum")));
	extensions.add(new DemographicExt(request.getParameter("ethnicity_id"), currentUserNoStr, demographicNo, "ethnicity",  request.getParameter("ethnicity")));
	extensions.add(new DemographicExt(request.getParameter("area_id"), currentUserNoStr, demographicNo, "area", request.getParameter("area")));
	extensions.add(new DemographicExt(request.getParameter("statusNum_id"), currentUserNoStr, demographicNo, "statusNum",  request.getParameter("statusNum")));
	extensions.add(new DemographicExt(request.getParameter("fNationCom_id"), currentUserNoStr, demographicNo, "fNationCom", request.getParameter("fNationCom")));
	extensions.add(new DemographicExt(request.getParameter("given_consent_id"), currentUserNoStr, demographicNo, "given_consent", request.getParameter("given_consent")));
	extensions.add(new DemographicExt(request.getParameter("rxInteractionWarningLevel_id"), currentUserNoStr, demographicNo, "rxInteractionWarningLevel", request.getParameter("rxInteractionWarningLevel")));
	extensions.add(new DemographicExt(request.getParameter("primaryEMR_id"), currentUserNoStr, demographicNo, "primaryEMR", request.getParameter("primaryEMR")));
	extensions.add(new DemographicExt(request.getParameter("phoneComment_id"), currentUserNoStr, demographicNo, "phoneComment", request.getParameter("phoneComment")));
	extensions.add(new DemographicExt(request.getParameter("usSigned_id"), currentUserNoStr, demographicNo, "usSigned", request.getParameter("usSigned")));
	extensions.add(new DemographicExt(request.getParameter("privacyConsent_id"), currentUserNoStr, demographicNo, "privacyConsent", request.getParameter("privacyConsent")));
	extensions.add(new DemographicExt(request.getParameter("informedConsent_id"), currentUserNoStr, demographicNo, "informedConsent", request.getParameter("informedConsent")));
	extensions.add(new DemographicExt(request.getParameter("paper_chart_archived_id"), currentUserNoStr, demographicNo, "paper_chart_archived", request.getParameter("paper_chart_archived")));
	extensions.add(new DemographicExt(request.getParameter("paper_chart_archived_date_id"), currentUserNoStr, demographicNo, "paper_chart_archived_date", request.getParameter("paper_chart_archived_date")));
	extensions.add(new DemographicExt(request.getParameter("paper_chart_archived_program_id"), currentUserNoStr, demographicNo, "paper_chart_archived_program", request.getParameter("paper_chart_archived_program")));
	
	// customized key
	if(oscarVariables.getProperty("demographicExt") != null)
	{
		String[] propDemoExt = oscarVariables.getProperty("demographicExt", "").split("\\|");
		for(int k = 0; k < propDemoExt.length; k++)
		{
			extensions.add(new DemographicExt(request.getParameter(propDemoExt[k].replace(' ', '_') + "_id"), currentUserNoStr,
					demographicNo, propDemoExt[k].replace(' ', '_'), request.getParameter(propDemoExt[k].replace(' ', '_'))));
		}
	}

	// added check to see if patient has a bc health card and has a version code of 66, in this case you are aloud to have dup hin
	boolean hinDupCheckException = false;

	if(hcType != null && ver != null && hcType.equals("BC") && ver.equals("66"))
	{
		hinDupCheckException = true;
	}

	if(request.getParameter("hin") != null && request.getParameter("hin").length() > 5 && !hinDupCheckException)
	{
		boolean isUnique = demographicManager.isUniqueHealthCard(loggedInInfo, hin, ver, hcType, demographicNo);

		if (!isUnique)
		{
%>
				***<font color='red'><bean:message key="demographic.demographicaddarecord.msgDuplicatedHIN" /></font>
				***<br><br><a href=# onClick="history.go(-1);return false;"><b>&lt;-<bean:message key="global.btnBack" /></b></a>
<%
				return;
		}
	}

	//save
	demographicService.updateLegacyDemographicRecord(demographic, extensions, loggedInInfo);

	// save custom licensed producer if enabled
	if(oscarVariables.isPropertyActive("show_demographic_licensed_producers")) {
		try {
			int licensedProducerID = Integer.parseInt(request.getParameter("licensed_producer"));
			int licensedProducerID2 = Integer.parseInt(request.getParameter("licensed_producer2"));
			int licensedProducerAddressID = Integer.parseInt(request.getParameter("licensed_producer_address"));
			demographicDao.saveDemographicLicensedProducer(demographic.getDemographicNo(), licensedProducerID, licensedProducerID2, licensedProducerAddressID);
		}
		catch(NumberFormatException e) {
			// unable to save licensed producer info
			MiscUtils.getLogger().warn(
					String.format("Failed to save licensed producer for demographic %d.", demographic.getDemographicNo())
			);
		}
	}

	// for the IBD clinic
	OtherIdManager.saveIdDemographic(demographicNo, "meditech_id", request.getParameter("meditech_id"));
    
    try{
    	oscar.oscarDemographic.data.DemographicNameAgeString.resetDemographic(demographicNoStr);
    }catch(Exception nameAgeEx){
    	MiscUtils.getLogger().error("ERROR RESETTING NAME AGE", nameAgeEx);
    }

    //find the democust record for update
    DemographicCust demographicCust = demographicCustDao.find(demographicNo);
    if(demographicCust != null) {
    	demographicCust.setResident(request.getParameter("resident"));
    	demographicCust.setNurse(request.getParameter("nurse"));
    	demographicCust.setAlert(request.getParameter("alert"));
    	demographicCust.setMidwife(request.getParameter("midwife"));
    	demographicCust.setNotes("<unotes>"+ request.getParameter("notes")+"</unotes>");
    	demographicCustDao.merge(demographicCust);
    } else {
    	demographicCust = new DemographicCust();
    	demographicCust.setResident(request.getParameter("resident"));
    	demographicCust.setNurse(request.getParameter("nurse"));
    	demographicCust.setAlert(request.getParameter("alert"));
    	demographicCust.setMidwife(request.getParameter("midwife"));
    	demographicCust.setNotes("<unotes>"+ request.getParameter("notes")+"</unotes>");
    	demographicCust.setId(demographicNo);
    	demographicCustDao.persist(demographicCust);
    }

    //update admission information
    GenericIntakeEditAction gieat = new GenericIntakeEditAction();
    ProgramManager pm = SpringUtils.getBean(ProgramManager.class);
	AdmissionManager am = SpringUtils.getBean(AdmissionManager.class);
    gieat.setAdmissionManager(am);
    gieat.setProgramManager(pm);

	String bedComProgramId = request.getParameter("rps");
	if(bedComProgramId != null && bedComProgramId.length() > 0)
	{
		try
		{
			gieat.admitBedCommunityProgram(demographic.getDemographicNo(), currentUserNoStr, Integer.parseInt(bedComProgramId), "", "(Master record change)", new java.util.Date());
		}
		catch(Exception e)
		{

		}
	}

	String[] servP = request.getParameterValues("sp");
	if(servP != null && servP.length > 0)
	{
		Set<Integer> s = new HashSet<Integer>();
		for(String _s : servP)
		{
			s.add(Integer.parseInt(_s));
		}
		try
		{
			gieat.admitServicePrograms(demographic.getDemographicNo(), currentUserNoStr, s, "(Master record change)", new java.util.Date());
		}
		catch(Exception e)
		{
		}
	}

	Set<Program> pset = gieat.getActiveProviderProgramsInFacility(loggedInInfo, loggedInProviderNo, loggedInInfo.getCurrentFacility().getId());
	List<Program> allServiceProgramsShown = gieat.getServicePrograms(pset, loggedInProviderNo);
	for(Program p : allServiceProgramsShown)
	{
		if(!isFound(servP, p.getId().toString()))
		{
			try
			{
				am.processDischarge(p.getId(), demographic.getDemographicNo(), "(Master record change)", "0");
			}
			catch(org.oscarehr.PMmodule.exception.AdmissionException e)
			{
			}
		}
	}

	//add to waiting list if the waiting_list parameter in the property file is set to true
	oscar.oscarWaitingList.WaitingList wL = oscar.oscarWaitingList.WaitingList.getInstance();
	if(wL.getFound())
	{
		String waitListId = request.getParameter("list_id");
		String waitListDate = request.getParameter("waiting_list_referral_date");
		String waitListNote = request.getParameter("waiting_list_note");

		if(waitListId != null && !waitListId.equals("0"))
		{
			WLWaitingListUtil.updateWaitingListRecord(waitListId, waitListNote, demographicNoStr, waitListDate);
		}
	}
	response.sendRedirect("demographiccontrol.jsp?demographic_no=" + demographicNoStr + "&displaymode=edit&dboperation=search_detail");
%>

<h2>Update a Provider Record Successfully !
	<p><a href="demographiccontrol.jsp?demographic_no=<%= demographicNoStr %>&displaymode=edit&dboperation=search_detail"><%= demographicNoStr %></a></p>
</h2>

<%
	ProviderPreventionManager prevMgr = (ProviderPreventionManager) SpringUtils.getBean("preventionMgr");
	prevMgr.removePrevention(demographicNoStr);

	LogAction.addLogEntry(currentUserNoStr, demographicNo, LogConst.ACTION_UPDATE, LogConst.CON_DEMOGRAPHIC, LogConst.STATUS_SUCCESS, demographicNoStr, request.getRemoteAddr());
	recentDemographicAccessService.updateAccessRecord(Integer.parseInt(currentUserNoStr), demographicNo);
%>
<p></p>

</center>
</body>
</html:html>

<%!
	public boolean isFound(String[] vals, String val)
	{
		if(vals != null)
		{
			for(String t : vals)
			{
				if(t.equals(val))
				{
					return true;
				}
			}
		}
		return false;
	}
%>
