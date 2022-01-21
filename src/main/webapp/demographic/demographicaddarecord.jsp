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
	String roleName$ = session.getAttribute("userrole") + "," + session.getAttribute("user");
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
<%@ page import="org.oscarehr.PMmodule.service.AdmissionManager, org.oscarehr.PMmodule.service.ProgramManager, org.oscarehr.PMmodule.web.GenericIntakeEditAction, org.oscarehr.common.OtherIdManager" errorPage="errorpage.jsp"%>
<%@ page import="org.oscarehr.common.dao.DemographicArchiveDao"%>
<%@ page import="org.oscarehr.demographic.dao.DemographicCustDao" %>
<%@ page import="org.oscarehr.common.dao.DemographicDao"%>

<%@ page import="org.oscarehr.demographic.dao.DemographicExtArchiveDao" %>

<%@ page import="org.oscarehr.demographic.dao.DemographicExtDao" %>
<%@ page import="org.oscarehr.common.model.ConsentType" %>

<%@ page import="org.oscarehr.common.model.Demographic" %>
<%@ page import="org.oscarehr.demographic.entity.DemographicCust" %>
<%@ page import="org.oscarehr.demographic.entity.DemographicExt" %>
<%@ page import="org.oscarehr.demographic.entity.DemographicExtArchive" %>

<%@page import="org.oscarehr.managers.PatientConsentManager" %>
<%@page import="org.oscarehr.provider.service.RecentDemographicAccessService" %>
<%@page import="org.oscarehr.util.LoggedInInfo" %>

<%@page import="org.oscarehr.util.SpringUtils" %>
<%@page import="oscar.MyDateFormat" %>
<%@page import="oscar.OscarProperties" %>

<%@page import="oscar.log.LogAction" %>
<%@page import="oscar.log.LogConst" %>
<%@page import="oscar.oscarWaitingList.util.WLWaitingListUtil" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.oscarehr.util.MiscUtils" %>
<%@ page import="static oscar.util.StringUtils.filterControlCharacters" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/caisi-tag.tld" prefix="caisi"%>

<%
	OscarProperties oscarVariables = oscar.OscarProperties.getInstance();

	ProgramManager pm = SpringUtils.getBean(ProgramManager.class);
	AdmissionManager am = SpringUtils.getBean(AdmissionManager.class);
	DemographicExtDao demographicExtDao = SpringUtils.getBean(DemographicExtDao.class);
	DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographicDao");
	DemographicCustDao demographicCustDao = (DemographicCustDao)SpringUtils.getBean("demographicCustDao");

	DemographicExtArchiveDao demographicExtArchiveDao = SpringUtils.getBean(DemographicExtArchiveDao.class);
	DemographicArchiveDao demographicArchiveDao = (DemographicArchiveDao)SpringUtils.getBean("demographicArchiveDao");

	RecentDemographicAccessService recentDemographicAccessService = SpringUtils.getBean(RecentDemographicAccessService.class);

%>

<html:html locale="true">
	<head>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
		<link rel="stylesheet" href="../web.css" />
	</head>

	<body onload="start()" bgproperties="fixed" topmargin="0" leftmargin="0" rightmargin="0">
	<center>
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr bgcolor="#486ebd">
				<th align="CENTER"><font face="Helvetica" color="#FFFFFF">
					<bean:message key="demographic.demographicaddarecord.title" /></font></th>
			</tr>
		</table>
		<form method="post" name="addappt">
			<%
				LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
				//If this is from adding appointment screen, then back to there
				String fromAppt = request.getParameter("fromAppt");
				String originalPage2 = request.getParameter("originalPage");
				String provider_no2 = request.getParameter("provider_no");
				String bFirstDisp2 = request.getParameter("bFirstDisp");
				String year2 = request.getParameter("year");
				String month2 = request.getParameter("month");
				String day2 = request.getParameter("day");
				String start_time2 = request.getParameter("start_time");
				String end_time2 = request.getParameter("end_time");
				String duration2 = request.getParameter("duration");

				String dem = null;
				String year, month, day;
				String curUser_no = (String)session.getAttribute("user");

				Demographic demographic = new Demographic();
				demographic.setLastName(request.getParameter("last_name").trim());
				demographic.setFirstName(request.getParameter("first_name").trim());
				demographic.setAddress(request.getParameter("address"));
				demographic.setCity(request.getParameter("city"));
				demographic.setProvince(request.getParameter("province"));
				demographic.setPostal(request.getParameter("postal"));
				demographic.setPhone(filterControlCharacters(request.getParameter("phone")));
				demographic.setPhone2(filterControlCharacters(request.getParameter("phone2")));
				demographic.setEmail(request.getParameter("email"));
				demographic.setMyOscarUserName(StringUtils.trimToNull(request.getParameter("myOscarUserName")));
				demographic.setYearOfBirth(request.getParameter("year_of_birth"));
				demographic.setMonthOfBirth(request.getParameter("month_of_birth")!=null && request.getParameter("month_of_birth").length()==1 ? "0"+request.getParameter("month_of_birth") : request.getParameter("month_of_birth"));
				demographic.setDateOfBirth(request.getParameter("date_of_birth")!=null && request.getParameter("date_of_birth").length()==1 ? "0"+request.getParameter("date_of_birth") : request.getParameter("date_of_birth"));

                String hin = request.getParameter("hin");
                if (hin != null)
                {
                    hin = hin.replaceAll("[^0-9a-zA-Z]", "");
                }
                demographic.setHin(StringUtils.trimToNull(hin));
                
				demographic.setVer(request.getParameter("ver"));
				demographic.setRosterStatus(request.getParameter("roster_status"));
				demographic.setPatientStatus(request.getParameter("patient_status"));
				demographic.setDateJoined(MyDateFormat.getSysDate(request.getParameter("date_joined_year")+"-"+request.getParameter("date_joined_month")+"-"+request.getParameter("date_joined_date")));
				demographic.setChartNo(request.getParameter("chart_no"));
				demographic.setProviderNo(StringUtils.trimToNull(request.getParameter("staff")));
				demographic.setSex(request.getParameter("sex"));
				demographic.setNameOfMother(StringUtils.trimToNull(request.getParameter("nameOfMother")));
				demographic.setNameOfFather(StringUtils.trimToNull(request.getParameter("nameOfFather")));

				year = StringUtils.trimToNull(request.getParameter("end_date_year"));
				month = StringUtils.trimToNull(request.getParameter("end_date_month"));
				day = StringUtils.trimToNull(request.getParameter("end_date_date"));
				if (year!=null && month!=null && day!=null) {
					demographic.setEndDate(MyDateFormat.getSysDate(year + "-" + month + "-" + day));
				} else {
					demographic.setEndDate(null);
				}

				year = StringUtils.trimToNull(request.getParameter("eff_date_year"));
				month = StringUtils.trimToNull(request.getParameter("eff_date_month"));
				day = StringUtils.trimToNull(request.getParameter("eff_date_date"));
				if (year!=null && month!=null && day!=null) {
					demographic.setEffDate(MyDateFormat.getSysDate(year + "-" + month + "-" + day));
				} else {
					demographic.setEffDate(null);
				}

				demographic.setPcnIndicator(request.getParameter("pcn_indicator"));
				demographic.setHcType(request.getParameter("hc_type"));

				year = StringUtils.trimToNull(request.getParameter("roster_date_year"));
				month = StringUtils.trimToNull(request.getParameter("roster_date_month"));
				day = StringUtils.trimToNull(request.getParameter("roster_date_date"));
				if (year!=null && month!=null && day!=null) {
					demographic.setRosterDate(MyDateFormat.getSysDate( year + "-" + month + "-" + day));
				} else {
					demographic.setRosterDate(null);
				}

				year = StringUtils.trimToNull(request.getParameter("hc_renew_date_year"));
				month = StringUtils.trimToNull(request.getParameter("hc_renew_date_month"));
				day = StringUtils.trimToNull(request.getParameter("hc_renew_date_date"));
				if (year!=null && month!=null && day!=null) {
					demographic.setHcRenewDate(MyDateFormat.getSysDate( year + "-" + month + "-" + day));
				} else {
					demographic.setHcRenewDate(null);
				}

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
				demographic.setLastUpdateUser(curUser_no);
				demographic.setLastUpdateDate(new java.util.Date());
				demographic.setPatientStatusDate(new java.util.Date());



				StringBuilder bufChart = null, bufName = null, bufNo = null, bufDoctorNo = null;
				// add checking hin duplicated record, if there is a HIN number
				// added check to see if patient has a bc health card and has a version code of 66, in this case you are aloud to have dup hin
				boolean hinDupCheckException = false;
				String hcType = request.getParameter("hc_type");
				String ver  = request.getParameter("ver");
				if (hcType != null && ver != null && hcType.equals("BC") && ver.equals("66")){
					hinDupCheckException = true;
				}

				String paramNameHin = request.getParameter("hin");
				if(paramNameHin!=null && paramNameHin.length()>5 && !hinDupCheckException) {
					//oscar.oscarBilling.ca.on.data.BillingONDataHelp dbObj = new oscar.oscarBilling.ca.on.data.BillingONDataHelp();
					//String sql = "select demographic_no from demographic where hin=? and year_of_birth=? and month_of_birth=? and date_of_birth=?";
					List<Demographic> demographics = demographicDao.searchByHealthCard(paramNameHin.trim());
					if(demographics.size()>0){
			%>
			***<font color='red'><bean:message key="demographic.demographicaddarecord.msgDuplicatedHIN" /></font>***<br><br>
			<a href=# onClick="history.go(-1);return false;"><b>&lt;-<bean:message key="global.btnBack" /></b></a>
			<%
						return;
					}
				}

				bufName = new StringBuilder(request.getParameter("last_name")+ ","+ request.getParameter("first_name") );
				bufNo = new StringBuilder( (StringUtils.trimToEmpty("demographic_no")) );
				bufChart = new StringBuilder(StringUtils.trimToEmpty("chart_no"));
				bufDoctorNo = new StringBuilder( StringUtils.trimToEmpty("provider_no") );

				if(oscarVariables.isPropertyActive("demographic_veteran_no")) {
					demographic.setVeteranNo(StringUtils.trimToNull(request.getParameter("veteranNo")));
				}

				demographicDao.save(demographic);

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

				GenericIntakeEditAction gieat = new GenericIntakeEditAction();
				gieat.setAdmissionManager(am);
				gieat.setProgramManager(pm);
				String residentialStatus = request.getParameter("rps");
				Integer programId;
				if (residentialStatus == null || residentialStatus.trim().isEmpty()) {
					programId = pm.getProgramIdByProgramName("OSCAR"); //Default to the oscar program
				}
				else {
					programId = Integer.parseInt(residentialStatus);
				}
				gieat.admitBedCommunityProgram(demographic.getDemographicNo(),loggedInInfo.getLoggedInProviderNo(),programId,"","",null);

				String[] servP = request.getParameterValues("sp");
				if(servP!=null&&servP.length>0){
					Set<Integer> s = new HashSet<Integer>();
					for(String _s:servP) s.add(Integer.parseInt(_s));
					gieat.admitServicePrograms(demographic.getDemographicNo(),loggedInInfo.getLoggedInProviderNo(),s,"",null);
				}


				//add democust record for alert
				String[] param2 =new String[6];
				param2[0]=demographic.getDemographicNo().toString();

				DemographicCust demographicCust = new DemographicCust();
				demographicCust.setResident(request.getParameter("cust2"));
				demographicCust.setNurse(request.getParameter("cust1"));
				demographicCust.setAlert(request.getParameter("cust3"));
				demographicCust.setMidwife(request.getParameter("cust4"));
				demographicCust.setNotes("<unotes>"+ request.getParameter("content")+"</unotes>");
				demographicCust.setId(demographic.getDemographicNo());
				demographicCustDao.persist(demographicCust);

				dem = demographic.getDemographicNo().toString();

				// Save the patient consent values.
				if( OscarProperties.getInstance().getBooleanProperty("USE_NEW_PATIENT_CONSENT_MODULE", "true") ) {

					PatientConsentManager patientConsentManager = SpringUtils.getBean( PatientConsentManager.class );
					List<ConsentType> consentTypes = patientConsentManager.getConsentTypes();
					String consentTypeId = null;

					for( ConsentType consentType : consentTypes ) {
						consentTypeId = request.getParameter( consentType.getType() );
						// checked box means add or edit consent.
						if( consentTypeId != null ) {
							patientConsentManager.addConsent(loggedInInfo, demographic.getDemographicNo(), Integer.parseInt( consentTypeId ) );
						}
					}
				}

				String proNo = (String) session.getValue("user");
				demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "hPhoneExt", filterControlCharacters(request.getParameter("hPhoneExt")), "");
				demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "wPhoneExt", filterControlCharacters(request.getParameter("wPhoneExt")), "");
				demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "demo_cell", filterControlCharacters(request.getParameter("demo_cell")), "");
				demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "aboriginal", request.getParameter("aboriginal"), "");
				demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "cytolNum",  request.getParameter("cytolNum"),  "");
				demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "ethnicity",     request.getParameter("ethnicity"),     "");
				demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "area",          request.getParameter("area"),          "");
				demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "statusNum",     request.getParameter("statusNum"),     "");
				demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "fNationCom",    request.getParameter("fNationCom"),    "");
				demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "given_consent", request.getParameter("given_consent"), "");
				demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "rxInteractionWarningLevel", request.getParameter("rxInteractionWarningLevel"), "");
				demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "primaryEMR", request.getParameter("primaryEMR"), "");
				demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "aboriginal", request.getParameter("aboriginal"), "");
				demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "phoneComment", filterControlCharacters(request.getParameter("phoneComment")), "");
				demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "usSigned", request.getParameter("usSigned"), "");
				demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "privacyConsent", request.getParameter("privacyConsent"), "");
				demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "informedConsent", request.getParameter("informedConsent"), "");
				//for the IBD clinic
				OtherIdManager.saveIdDemographic(dem, "meditech_id", request.getParameter("meditech_id"));

				// customized key
				if(oscarVariables.getProperty("demographicExt") != null) {
					String [] propDemoExt = oscarVariables.getProperty("demographicExt","").split("\\|");
					for(int k=0; k<propDemoExt.length; k++) {
						demographicExtDao.addKey(proNo,demographic.getDemographicNo(),propDemoExt[k],request.getParameter(propDemoExt[k].replace(' ','_')),"");
					}
				}
				// customized key

				// add log
				String ip = request.getRemoteAddr();
				LogAction.addLogEntry(curUser_no, demographic.getDemographicNo(), LogConst.ACTION_ADD, LogConst.CON_DEMOGRAPHIC, LogConst.STATUS_SUCCESS, param2[0], ip);
				recentDemographicAccessService.updateAccessRecord(Integer.parseInt(curUser_no), demographic.getDemographicNo());

				//archive the original too
				Long archiveId = demographicArchiveDao.archiveRecord(demographicDao.getDemographic(dem));
				List<DemographicExt> extensions = demographicExtDao.getDemographicExtByDemographicNo(Integer.parseInt(dem));
				for (DemographicExt extension : extensions) {
					DemographicExtArchive archive = new DemographicExtArchive(extension);
					archive.setArchiveId(archiveId);
					archive.setValue(request.getParameter(archive.getKey()));
					demographicExtArchiveDao.saveEntity(archive);
				}

				// Assign the patient to a waitlist if necessary
				String waitListIdStr = request.getParameter("list_id");
				if(waitListIdStr != null) {
					int waitingListID = Integer.parseInt(waitListIdStr);
					WLWaitingListUtil.addToWaitingList(waitingListID, demographic.getDemographicNo(), request.getParameter("waiting_list_referral_date"), request.getParameter("waiting_list_note"));
				}

				if(start_time2!=null && !start_time2.equals("null")) {
			%>
			<script language="JavaScript">
				<!--
				document.addappt.action="../appointment/addappointment.jsp?user_id=<%=request.getParameter("creator")%>&provider_no=<%=provider_no2%>&bFirstDisp=<%=bFirstDisp2%>&appointment_date=<%=request.getParameter("appointment_date")%>&year=<%=year2%>&month=<%=month2%>&day=<%=day2%>&start_time=<%=start_time2%>&end_time=<%=end_time2%>&duration=<%=duration2%>&name=<%=URLEncoder.encode(bufName.toString())%>&chart_no=<%=URLEncoder.encode(bufChart.toString())%>&bFirstDisp=false&demographic_no=<%=dem.toString()%>&messageID=<%=request.getParameter("messageId")%>&doctor_no=<%=bufDoctorNo.toString()%>&notes=<%=request.getParameter("notes")%>&reason=<%=request.getParameter("reason")%>&location=<%=request.getParameter("location")%>&resources=<%=request.getParameter("resources")%>&type=<%=request.getParameter("type")%>&style=<%=request.getParameter("style")%>&billing=<%=request.getParameter("billing")%>&status=<%=request.getParameter("status")%>&createdatetime=<%=request.getParameter("createdatetime")%>&creator=<%=request.getParameter("creator")%>&remarks=<%=request.getParameter("remarks")%>";
				document.addappt.submit();
				//-->
			</SCRIPT>
			<% } %>
		</form>



		<p>
		<h2><bean:message key="demographic.demographicaddarecord.msgSuccessful" /></h2>

		<a href="demographiccontrol.jsp?demographic_no=<%=dem%>&displaymode=edit&dboperation=search_detail"><bean:message key="demographic.demographicaddarecord.goToRecord"/></a>

		<caisi:isModuleLoad moduleName="caisi">
			<br/>
			<a href="../PMmodule/ClientManager.do?id=<%=dem%>"><bean:message key="demographic.demographicaddarecord.goToCaisiRecord"/> (<a href="#"  onclick="popup(700,1027,'demographiccontrol.jsp?demographic_no=<%=dem%>&displaymode=edit&dboperation=search_detail')">New Window</a>)</a>
		</caisi:isModuleLoad>


		<caisi:isModuleLoad moduleName="caisi">
			<br/>
			<a href="../PMmodule/ClientManager.do?id=<%=dem%>"><bean:message key="demographic.demographicaddarecord.goToCaisiRecord"/></a>
		</caisi:isModuleLoad>


		<p></p>
		<%@ include file="footer.jsp"%></center>
	</body>
</html:html>
