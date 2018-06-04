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
<%@ page import="org.oscarehr.demographic.model.DemographicCust" %>
<%@ page import="org.oscarehr.demographic.model.DemographicExt" %>
<%@ page import="org.oscarehr.demographic.model.DemographicExtArchive" %>

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
<%@ page import="java.util.Hashtable" %>
<%@ page import="org.apache.commons.fileupload.FileUploadException" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.FileItem" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.IOException" %>
<%@ page import="org.oscarehr.document.model.Document" %>
<%@ page import="org.oscarehr.document.service.DocumentService" %>
<%@ page import="org.apache.commons.io.FilenameUtils" %>
<%@ page import="java.util.ResourceBundle" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/caisi-tag.tld" prefix="caisi"%>

<% 
	OscarProperties oscarVariables = oscar.OscarProperties.getInstance();

	java.util.ResourceBundle oscarResources = ResourceBundle.getBundle("oscarResources", request.getLocale());

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
<%!
	static InputStream fileContent = null;
	public static Hashtable getParamsFromMultipartForm(HttpServletRequest req) throws FileUploadException, IOException
	{
        Hashtable<String,String> ret = new Hashtable<String,String>();
        List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);
        for (FileItem item : items) {
            if (item.isFormField()) {
                ret.put(item.getFieldName(), item.getString());
            }
            else {
				ret.put(item.getFieldName(), FilenameUtils.getName(item.getName()));
            	fileContent = item.getInputStream();
			}
        }
        return ret;
    }
%>

	<%
	Hashtable<String,String> multipartParams = getParamsFromMultipartForm(request);

	LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);

        //If this is from adding appointment screen, then back to there
        String fromAppt = multipartParams.get("fromAppt");
        String originalPage2 = multipartParams.get("originalPage");
        String provider_no2 = multipartParams.get("provider_no");
        String bFirstDisp2 = multipartParams.get("bFirstDisp");
        String year2 = multipartParams.get("year");
        String month2 = multipartParams.get("month");
        String day2 = multipartParams.get("day");
        String start_time2 = multipartParams.get("start_time");
        String end_time2 = multipartParams.get("end_time");
        String duration2 = multipartParams.get("duration");

    String dem = null;
	String year, month, day;
    String curUser_no = (String)session.getAttribute("user");
	Demographic demographic = new Demographic();
	demographic.setLastName(multipartParams.get("last_name").trim());
	demographic.setFirstName(multipartParams.get("first_name").trim());
	demographic.setAddress(multipartParams.get("address"));
	demographic.setCity(multipartParams.get("city"));
	demographic.setProvince(multipartParams.get("province"));
	demographic.setPostal(multipartParams.get("postal"));
	demographic.setPhone(multipartParams.get("phone"));
	demographic.setPhone2(multipartParams.get("phone2"));
	demographic.setEmail(multipartParams.get("email"));
	demographic.setMyOscarUserName(StringUtils.trimToNull(multipartParams.get("myOscarUserName")));
	demographic.setYearOfBirth(multipartParams.get("year_of_birth"));
	demographic.setMonthOfBirth(multipartParams.get("month_of_birth")!=null && multipartParams.get("month_of_birth").length()==1 ? "0"+multipartParams.get("month_of_birth") : multipartParams.get("month_of_birth"));
	demographic.setDateOfBirth(multipartParams.get("date_of_birth")!=null && multipartParams.get("date_of_birth").length()==1 ? "0"+multipartParams.get("date_of_birth") : multipartParams.get("date_of_birth"));
	demographic.setHin(multipartParams.get("hin"));
	demographic.setVer(multipartParams.get("ver"));
	demographic.setRosterStatus(multipartParams.get("roster_status"));
	demographic.setPatientStatus(multipartParams.get("patient_status"));
	demographic.setDateJoined(MyDateFormat.getSysDate(multipartParams.get("date_joined_year")+"-"+multipartParams.get("date_joined_month")+"-"+multipartParams.get("date_joined_date")));
	demographic.setChartNo(multipartParams.get("chart_no"));
	demographic.setProviderNo(multipartParams.get("staff"));
	demographic.setSex(multipartParams.get("sex"));

	year = StringUtils.trimToNull(multipartParams.get("end_date_year"));
	month = StringUtils.trimToNull(multipartParams.get("end_date_month"));
	day = StringUtils.trimToNull(multipartParams.get("end_date_date"));
	if (year!=null && month!=null && day!=null) {
	 		demographic.setEndDate(MyDateFormat.getSysDate(year + "-" + month + "-" + day));
	} else {
		demographic.setEndDate(null);
	}
	
	year = StringUtils.trimToNull(multipartParams.get("eff_date_year"));
	month = StringUtils.trimToNull(multipartParams.get("eff_date_month"));
	day = StringUtils.trimToNull(multipartParams.get("eff_date_date"));
	if (year!=null && month!=null && day!=null) {
		demographic.setEffDate(MyDateFormat.getSysDate(year + "-" + month + "-" + day));
	} else {
		demographic.setEffDate(null);
	}

	demographic.setPcnIndicator(multipartParams.get("pcn_indicator"));
	demographic.setHcType(multipartParams.get("hc_type"));
	
	year = StringUtils.trimToNull(multipartParams.get("roster_date_year"));
	month = StringUtils.trimToNull(multipartParams.get("roster_date_month"));
	day = StringUtils.trimToNull(multipartParams.get("roster_date_date"));
	if (year!=null && month!=null && day!=null) {
		demographic.setRosterDate(MyDateFormat.getSysDate( year + "-" + month + "-" + day));
	} else {
		demographic.setRosterDate(null);
	}
	          
	year = StringUtils.trimToNull(multipartParams.get("hc_renew_date_year"));
	month = StringUtils.trimToNull(multipartParams.get("hc_renew_date_month"));
	day = StringUtils.trimToNull(multipartParams.get("hc_renew_date_date"));
	if (year!=null && month!=null && day!=null) {
		demographic.setHcRenewDate(MyDateFormat.getSysDate( year + "-" + month + "-" + day));
	} else {
		demographic.setHcRenewDate(null);
	}
	         
	demographic.setFamilyDoctor("<rdohip>" + multipartParams.get("referral_doctor_no") + "</rdohip>" + "<rd>" + multipartParams.get("referral_doctor_name") + "</rd>"+ (multipartParams.get("family_doc")!=null? ("<family_doc>" + multipartParams.get("family_doc") + "</family_doc>") : ""));
	demographic.setFamilyDoctor2("<fd>" + multipartParams.get("family_doctor_no") + "</fd>" + (multipartParams.get("family_doctor_name")!=null? ("<fdname>" + multipartParams.get("family_doctor_name") + "</fdname>") : ""));
	demographic.setCountryOfOrigin(multipartParams.get("countryOfOrigin"));
	demographic.setNewsletter(multipartParams.get("newsletter"));
	demographic.setSin(multipartParams.get("sin"));
	demographic.setTitle(multipartParams.get("title"));
	demographic.setOfficialLanguage(multipartParams.get("official_lang"));
	demographic.setSpokenLanguage(multipartParams.get("spoken_lang"));
	demographic.setLastUpdateUser(curUser_no);
	demographic.setLastUpdateDate(new java.util.Date());
	demographic.setPatientStatusDate(new java.util.Date());
	
	
	
	StringBuilder bufChart = null, bufName = null, bufNo = null, bufDoctorNo = null;
    // add checking hin duplicated record, if there is a HIN number
    // added check to see if patient has a bc health card and has a version code of 66, in this case you are aloud to have dup hin
    boolean hinDupCheckException = false;
     String hcType = multipartParams.get("hc_type");
     String ver  = multipartParams.get("ver");
     if (hcType != null && ver != null && hcType.equals("BC") && ver.equals("66")){
        hinDupCheckException = true;
     }

	String paramNameHin = multipartParams.get("hin");
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
    
    bufName = new StringBuilder(multipartParams.get("last_name")+ ","+ multipartParams.get("first_name") );
    bufNo = new StringBuilder( (StringUtils.trimToEmpty("demographic_no")) );
    bufChart = new StringBuilder(StringUtils.trimToEmpty("chart_no"));
    bufDoctorNo = new StringBuilder( StringUtils.trimToEmpty("provider_no") );

	if(oscarVariables.isPropertyActive("demographic_veteran_no")) {
		demographic.setVeteranNo(StringUtils.trimToNull(multipartParams.get("veteranNo")));
	}

    demographicDao.save(demographic);

	// save custom licensed producer if enabled
	if(oscarVariables.isPropertyActive("show_demographic_licensed_producers")) {
		try {
			int licensedProducerID = Integer.parseInt(multipartParams.get("licensed_producer"));
			int licensedProducerID2 = Integer.parseInt(multipartParams.get("licensed_producer2"));
			int licensedProducerAddressID = Integer.parseInt(multipartParams.get("licensed_producer_address"));
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
	String residentialStatus = multipartParams.get("rps");
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
       	demographicCust.setResident(multipartParams.get("cust2"));
    	demographicCust.setNurse(multipartParams.get("cust1"));
    	demographicCust.setAlert(multipartParams.get("cust3"));
    	demographicCust.setMidwife(multipartParams.get("cust4"));
    	demographicCust.setNotes("<unotes>"+ multipartParams.get("content")+"</unotes>");
    	demographicCust.setId(demographic.getDemographicNo());
    	demographicCustDao.persist(demographicCust);

       dem = demographic.getDemographicNo().toString();
       
       // Save the patient consent values.
	   if( OscarProperties.getInstance().getBooleanProperty("USE_NEW_PATIENT_CONSENT_MODULE", "true") ) {
	
			PatientConsentManager patientConsentManager = SpringUtils.getBean( PatientConsentManager.class );
			List<ConsentType> consentTypes = patientConsentManager.getConsentTypes();
			String consentTypeId = null;

			for( ConsentType consentType : consentTypes ) {
				consentTypeId = multipartParams.get( consentType.getType() );
				// checked box means add or edit consent. 
				if( consentTypeId != null ) {		
					patientConsentManager.addConsent(loggedInInfo, demographic.getDemographicNo(), Integer.parseInt( consentTypeId ) );
				} 	
			}
		}

       String proNo = (String) session.getValue("user");
       demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "hPhoneExt", multipartParams.get("hPhoneExt"), "");
       demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "wPhoneExt", multipartParams.get("wPhoneExt"), "");
       demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "demo_cell", multipartParams.get("demo_cell"), "");
       demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "aboriginal", multipartParams.get("aboriginal"), "");
       demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "cytolNum",  multipartParams.get("cytolNum"),  "");
       demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "ethnicity",     multipartParams.get("ethnicity"),     "");
       demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "area",          multipartParams.get("area"),          "");
       demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "statusNum",     multipartParams.get("statusNum"),     "");
       demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "fNationCom",    multipartParams.get("fNationCom"),    "");
       demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "given_consent", multipartParams.get("given_consent"), "");
       demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "rxInteractionWarningLevel", multipartParams.get("rxInteractionWarningLevel"), "");
       demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "primaryEMR", multipartParams.get("primaryEMR"), "");
       demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "aboriginal", multipartParams.get("aboriginal"), "");
       demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "phoneComment", multipartParams.get("phoneComment"), "");
       demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "usSigned", multipartParams.get("usSigned"), "");
       demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "privacyConsent", multipartParams.get("privacyConsent"), "");
       demographicExtDao.addKey(proNo, demographic.getDemographicNo(), "informedConsent", multipartParams.get("informedConsent"), "");
       //for the IBD clinic
		OtherIdManager.saveIdDemographic(dem, "meditech_id", multipartParams.get("meditech_id"));

       // customized key
       if(oscarVariables.getProperty("demographicExt") != null) {
	       String [] propDemoExt = oscarVariables.getProperty("demographicExt","").split("\\|");
	       for(int k=0; k<propDemoExt.length; k++) {
	    	   demographicExtDao.addKey(proNo,demographic.getDemographicNo(),propDemoExt[k],multipartParams.get(propDemoExt[k].replace(' ','_')),"");
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
			archive.setValue(multipartParams.get(archive.getKey()));
			demographicExtArchiveDao.saveEntity(archive);	
		}	
        
        // Assign the patient to a waitlist if necessary
        String waitListIdStr = multipartParams.get("list_id");
        if(waitListIdStr != null) {
        	int waitingListID = Integer.parseInt(waitListIdStr);
            WLWaitingListUtil.addToWaitingList(waitingListID, demographic.getDemographicNo(), multipartParams.get("waiting_list_referral_date"), multipartParams.get("waiting_list_note"));
        }

        //Attach Document
		if(fileContent != null && fileContent.available() != 0)
		{
			Document document = new Document();
			document.setDocdesc(multipartParams.get("docDesc"));
			document.setDocfilename(multipartParams.get("docFile"));
			document.setDoccreator(loggedInInfo.getLoggedInProviderNo());
			document.setResponsible(loggedInInfo.getLoggedInProviderNo());
			DocumentService documentService = SpringUtils.getBean(org.oscarehr.document.service.DocumentService.class);
			documentService.uploadNewDocument(document, fileContent, Integer.parseInt(dem));
		}

	if (multipartParams.get("submitType").equals(oscarResources.getString("demographic.demographicaddrecordhtm.btnAddDocs")))
	{
	%>
		<script language="JavaScript">
			window.open('../dms/documentReport.jsp?function=demographic&doctype=lab&functionid=<%=dem%>&curUser=<%=provider_no2%>&mode=add&parentAjaxId=docs', 'height=700', 'width=1027');
		</script>
	<%
	}

        if(start_time2!=null && !start_time2.equals("null")) {
	%>
	<script language="JavaScript">
	<!--
	document.addappt.action="../appointment/addappointment.jsp?user_id=<%=multipartParams.get("creator")%>&provider_no=<%=provider_no2%>&bFirstDisp=<%=bFirstDisp2%>&appointment_date=<%=multipartParams.get("appointment_date")%>&year=<%=year2%>&month=<%=month2%>&day=<%=day2%>&start_time=<%=start_time2%>&end_time=<%=end_time2%>&duration=<%=duration2%>&name=<%=URLEncoder.encode(bufName.toString())%>&chart_no=<%=URLEncoder.encode(bufChart.toString())%>&bFirstDisp=false&demographic_no=<%=dem.toString()%>&messageID=<%=multipartParams.get("messageId")%>&doctor_no=<%=bufDoctorNo.toString()%>&notes=<%=multipartParams.get("notes")%>&reason=<%=multipartParams.get("reason")%>&location=<%=multipartParams.get("location")%>&resources=<%=multipartParams.get("resources")%>&type=<%=multipartParams.get("type")%>&style=<%=multipartParams.get("style")%>&billing=<%=multipartParams.get("billing")%>&status=<%=multipartParams.get("status")%>&createdatetime=<%=multipartParams.get("createdatetime")%>&creator=<%=multipartParams.get("creator")%>&remarks=<%=multipartParams.get("remarks")%>";
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
