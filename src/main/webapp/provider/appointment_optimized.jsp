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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="org.oscarehr.provider.controller.MenuBar" %>

<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ResourceBundle" %>

<%@ page import="oscar.OscarProperties" %>
<%@ page import="oscar.util.UtilDateUtilities" %>
<%@ page import="oscar.util.ConversionUtils" %>
<%@ page import="org.oscarehr.common.dao.DemographicDao" %>
<%@ page import="org.oscarehr.common.dao.OscarAppointmentDao" %>
<%@ page import="org.oscarehr.common.dao.MyGroupDao" %>
<%@ page import="org.oscarehr.common.dao.MyGroupAccessRestrictionDao" %>
<%@ page import="org.oscarehr.common.dao.ProviderSiteDao" %>
<%@ page import="org.oscarehr.common.dao.ScheduleDateDao" %>
<%@ page import="org.oscarehr.common.dao.ScheduleTemplateCodeDao" %>
<%@ page import="org.oscarehr.common.dao.SiteDao" %>
<%@ page import="org.oscarehr.common.dao.UserPropertyDAO" %>
<%@ page import="org.oscarehr.common.model.Dashboard" %>
<%@ page import="org.oscarehr.common.model.MyGroup" %>
<%@ page import="org.oscarehr.common.model.MyGroupAccessRestriction" %>
<%@ page import="org.oscarehr.common.model.Provider" %>
<%@ page import="org.oscarehr.common.model.ProviderPreference"%>
<%@ page import="org.oscarehr.common.model.ScheduleDate" %>
<%@ page import="org.oscarehr.common.model.ScheduleTemplate" %>
<%@ page import="org.oscarehr.common.model.ScheduleTemplateCode" %>
<%@ page import="org.oscarehr.common.model.Site" %>
<%@ page import="org.oscarehr.common.model.UserProperty" %>
<%@ page import="org.oscarehr.managers.AppManager" %>
<%@ page import="org.oscarehr.managers.DashboardManager" %>
<%@ page import="org.oscarehr.PMmodule.dao.ProviderDao" %>
<%@ page import="org.oscarehr.schedule.service.Schedule" %>
<%@ page import="org.oscarehr.schedule.dto.ResourceSchedule" %>
<%@ page import="org.oscarehr.util.LoggedInInfo" %>
<%@ page import="org.oscarehr.util.MiscUtils" %>
<%@ page import="org.oscarehr.util.SessionConstants" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.common.model.Appointment" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="org.oscarehr.managers.ProgramManager2" %>
<%@ page import="org.oscarehr.PMmodule.model.ProgramProvider" %>
<%@ page import="org.oscarehr.common.model.Demographic" %>
<%@ page import="oscar.util.UtilMisc" %>
<%@ page import="org.oscarehr.common.model.Tickler" %>
<%@ page import="org.oscarehr.common.model.DemographicCust" %>
<%@ page import="org.oscarehr.managers.SecurityInfoManager" %>
<%@ page import="org.oscarehr.managers.TicklerManager" %>
<%@ page import="org.oscarehr.common.dao.DemographicCustDao" %>
<%@ page import="org.oscarehr.common.model.DemographicStudy" %>
<%@ page import="org.oscarehr.common.dao.DemographicStudyDao" %>
<%@ page import="org.oscarehr.common.model.Study" %>
<%@ page import="org.oscarehr.common.dao.StudyDao" %>
<%@ page import="org.oscarehr.common.model.LookupListItem" %>
<%@ page import="org.oscarehr.common.model.LookupList" %>
<%@ page import="org.oscarehr.managers.LookupListManager" %>
<%@ page import="oscar.appt.ApptStatusData" %>
<%@ page import="org.oscarehr.provider.model.PreventionManager" %>
<%@ page import="oscar.MyDateFormat" %>
<%@ page import="oscar.SxmlMisc" %>
<%@ page import="org.oscarehr.web.admin.ProviderPreferencesUIBean" %>
<%@ page import="org.springframework.transaction.support.DefaultTransactionDefinition" %>
<%@ page import="org.springframework.transaction.TransactionDefinition" %>
<%@ page import="org.springframework.transaction.TransactionStatus" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.LocalTime" %>
<%@ page import="org.oscarehr.schedule.dto.UserDateSchedule" %>
<%@ page import="org.oscarehr.schedule.dto.ScheduleSlot" %>
<%@ page import="org.oscarehr.schedule.dto.AppointmentDetails" %>
<%@ page import="java.time.Duration" %>
<%@ page import="java.util.SortedMap" %>

<jsp:useBean id="providerBean" class="java.util.Properties" scope="session" />
<jsp:useBean id="dateTimeCodeBean" class="java.util.Hashtable" scope="page" />
<jsp:useBean id="txManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager" scope="page" />
<jsp:useBean id="appointmentInfo" class="org.oscarehr.appointment.AppointmentDisplayController" scope="page" />

<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.caisi.ca/plugin-tag" prefix="plugin" %>
<%!


private boolean bMultisites = org.oscarehr.common.IsPropertiesOn.isMultisitesEnable();
//private List<Site> sites = new ArrayList<Site>();
private List<Site> curUserSites = new ArrayList<Site>();
private List<String> siteProviderNos = new ArrayList<String>();
private List<String> siteGroups = new ArrayList<String>();
private String selectedSite = null;
private HashMap<String,String> siteBgColor = new HashMap<String,String>();
private HashMap<String,String> CurrentSiteMap = new HashMap<String,String>();

public boolean isWeekView(ServletRequest request)
{
	String provNum = request.getParameter("provider_no");

	if (provNum == null) {
		return false;
	}

	return true;
}

public boolean checkRestriction(List<MyGroupAccessRestriction> restrictions, String name)
{
	for(MyGroupAccessRestriction restriction:restrictions)
	{
		if(restriction.getMyGroupNo().equals(name))
		{
			return true;
		}
	}
	return false;
}

public boolean patientHasOutstandingPrivateBills(String demographicNo)
{
	oscar.oscarBilling.ca.bc.MSP.MSPReconcile msp = new oscar.oscarBilling.ca.bc.MSP.MSPReconcile();
	return msp.patientHasOutstandingPrivateBill(demographicNo);
}

public boolean isBirthday(String schedDate,String demBday)
{
	return schedDate.equals(demBday);
}


%>
<%
	int MINUTES_IN_DAY = (24 * 60);
	org.oscarehr.schedule.service.Schedule scheduleService = SpringUtils.getBean(org.oscarehr.schedule.service.Schedule.class);

	DefaultTransactionDefinition def = new DefaultTransactionDefinition();
// explicitly setting the transaction name is something that can only be done programmatically
	def.setName("SomeTxName");
	def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

	//TransactionStatus txStatus = txManager.getTransaction(def);


	OscarAppointmentDao appointmentDao = SpringUtils.getBean(OscarAppointmentDao.class);

	MenuBar menuBarController = new MenuBar(request, session);
	pageContext.setAttribute("menuBarController", menuBarController);

	// Required for menu bar
	LoggedInInfo loggedInInfo1=LoggedInInfo.getLoggedInInfoFromSession(request);

	AppManager appManager = SpringUtils.getBean(AppManager.class);

	OscarProperties oscarProperties = OscarProperties.getInstance();

	String resourceBaseUrl =  oscarProperties.getProperty("resource_base_url");

	GregorianCalendar cal = new GregorianCalendar();
	int curYear = cal.get(Calendar.YEAR);
	int curMonth = (cal.get(Calendar.MONTH)+1);
	int curDay = cal.get(Calendar.DAY_OF_MONTH);

	int year = Integer.parseInt(request.getParameter("year"));
	int month = Integer.parseInt(request.getParameter("month"));
	int day = Integer.parseInt(request.getParameter("day"));

	String roleName$ = session.getAttribute("userrole") + "," + session.getAttribute("user");
	String curUser_no = (String) session.getAttribute("user");
	String userFirstName = (String) session.getAttribute("userfirstname");
	String userLastName = (String) session.getAttribute("userlastname");
	String prov = oscarProperties.getBillingTypeUpperCase();


	// Additional things required for schedule

	MyGroupDao myGroupDao = SpringUtils.getBean(MyGroupDao.class);
	MyGroupAccessRestrictionDao myGroupAccessRestrictionDao = SpringUtils.getBean(MyGroupAccessRestrictionDao.class);
	ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
	ProviderSiteDao providerSiteDao = SpringUtils.getBean(ProviderSiteDao.class);
	ScheduleDateDao scheduleDateDao = SpringUtils.getBean(ScheduleDateDao.class);
	ScheduleTemplateCodeDao scheduleTemplateCodeDao = SpringUtils.getBean(ScheduleTemplateCodeDao.class);
	SiteDao siteDao = SpringUtils.getBean(SiteDao.class);
	UserPropertyDAO userPropertyDao = SpringUtils.getBean(UserPropertyDAO.class);

	int view = request.getParameter("view")!=null ? Integer.parseInt(request.getParameter("view")) : 0; //0-multiple views, 1-single view
	String provNum = request.getParameter("provider_no");

	SimpleDateFormat inform = new SimpleDateFormat ("yyyy-MM-dd", request.getLocale());
	//String strDate = year + "-" + month + "-" + day;

	LocalDate selectedDate = LocalDate.of(year, month, day);
	String strDate = selectedDate.toString();

	String formatDate;
	try {
		java.util.ResourceBundle prop = ResourceBundle.getBundle("oscarResources", request.getLocale());
		formatDate = UtilDateUtilities.DateToString(inform.parse(strDate), prop.getString("date.EEEyyyyMMdd"),request.getLocale());
	} catch (Exception e) {
		MiscUtils.getLogger().error("Error", e);
		formatDate = UtilDateUtilities.DateToString(inform.parse(strDate), "EEE, yyyy-MM-dd");
	}

	int week = cal.get(Calendar.WEEK_OF_YEAR);

	String strYear=""+year;
	String strMonth=month>9?(""+month):("0"+month);
	String strDay=day>9?(""+day):("0"+day);

	String monthDay = String.format("%02d", month) + "-" + String.format("%02d", day);

	String viewall = request.getParameter("viewall");
	if( viewall == null ) {
		viewall = "0";
	}

	int numProvider=0, numAvailProvider=0;
	String [] curProvider_no;
	String [] curProviderName;
//initial provider bean for all the application
	if(providerBean.isEmpty()) {
		for(Provider p : providerDao.getActiveProviders()) {
			providerBean.setProperty(p.getProviderNo(),p.getFormattedName());
		}
	}

	String _scheduleDate = strYear+"-"+strMonth+"-"+strDay;

	List<Map<String,Object>> resultList = null;

	ProviderPreference providerPreference= ProviderPreferencesUIBean.getProviderPreference(loggedInInfo1.getLoggedInProviderNo());
	ProviderPreference providerPreference2=(ProviderPreference)session.getAttribute(SessionConstants.LOGGED_IN_PROVIDER_PREFERENCE);
	String mygroupno = providerPreference2.getMyGroupNo();

	int startHour=providerPreference2.getStartHour();
	int endHour=providerPreference2.getEndHour();
	int everyMin=providerPreference2.getEveryMin();

	boolean isTeamScheduleOnly = false;
	%>
	<security:oscarSec roleName="<%=roleName$%>" objectName="_team_schedule_only" rights="r"
		reverse="false">
	<%
		isTeamScheduleOnly = true;
	%>
	</security:oscarSec>
	<%

	int lenLimitedL=11; //L - long
	if(OscarProperties.getInstance().getProperty("APPT_SHOW_FULL_NAME","").equalsIgnoreCase("true")) {
		lenLimitedL = 25;
	}
	int lenLimitedS=3; //S - short
	int len = lenLimitedL;

	int nProvider;

	if(mygroupno != null && providerBean.get(mygroupno) != null) { //single appointed provider view
		numProvider=1;
		curProvider_no = new String [numProvider];
		curProviderName = new String [numProvider];
		curProvider_no[0]=mygroupno;

		curProviderName[0]=providerDao.getProvider(mygroupno).getFullName();

	} else {
		if(view==0) { //multiple views
			if (selectedSite!=null) {
				numProvider = siteDao.site_searchmygroupcount(mygroupno, selectedSite).intValue();
			}
			else {
				numProvider = myGroupDao.getGroupByGroupNo(mygroupno).size();
			}


			String [] param3 = new String [2];
			param3[0] = mygroupno;
			param3[1] = strDate; //strYear +"-"+ strMonth +"-"+ strDay ;
			numAvailProvider = 0;
			if (selectedSite!=null) {
				List<String> siteProviders = providerSiteDao.findByProviderNoBySiteName(selectedSite);
				List<ScheduleDate> results = scheduleDateDao.search_numgrpscheduledate(mygroupno, ConversionUtils.fromDateString(strDate));

				for(ScheduleDate result:results) {
					if(siteProviders.contains(result.getProviderNo())) {
						numAvailProvider++;
					}
				}
			}
			else {
				numAvailProvider = scheduleDateDao.search_numgrpscheduledate(mygroupno, ConversionUtils.fromDateString(strDate)).size();

			}

			// _team_schedule_only does not support groups
			// As well, the mobile version only shows the schedule of the login provider.
			if(numProvider==0 || isTeamScheduleOnly) {
				numProvider=1; //the login user
				curProvider_no = new String []{curUser_no};  //[numProvider];
				curProviderName = new String []{(userLastName+", "+userFirstName)}; //[numProvider];
			} else {
				if(request.getParameter("viewall")!=null && request.getParameter("viewall").equals("1") ) {
					if(numProvider >= 5) {lenLimitedL = 2; lenLimitedS = 3; }
				} else {
					if(numAvailProvider >= 5) {lenLimitedL = 2; lenLimitedS = 3; }
					if(numAvailProvider == 2) {lenLimitedL = 20; lenLimitedS = 10; len = 20;}
					if(numAvailProvider == 1) {lenLimitedL = 30; lenLimitedS = 30; len = 30; }
				}
				UserProperty uppatientNameLength = userPropertyDao.getProp(curUser_no, UserProperty.PATIENT_NAME_LENGTH);
				int NameLength=0;

				if ( uppatientNameLength != null && uppatientNameLength.getValue() != null) {
					try {
						NameLength=Integer.parseInt(uppatientNameLength.getValue());
					} catch (NumberFormatException e) {
						NameLength=0;
					}

					if(NameLength>0) {
						len=lenLimitedS= lenLimitedL = NameLength;
					}
				}
				curProvider_no = new String [numProvider];
				curProviderName = new String [numProvider];

				int iTemp = 0;
				if (selectedSite!=null) {
					List<String> siteProviders = providerSiteDao.findByProviderNoBySiteName(selectedSite);
					List<MyGroup> results = myGroupDao.getGroupByGroupNo(mygroupno);
					for(MyGroup result:results) {
						if(siteProviders.contains(result.getId().getProviderNo())) {
							curProvider_no[iTemp] = String.valueOf(result.getId().getProviderNo());

							Provider p = providerDao.getProvider(curProvider_no[iTemp]);
							if (p!=null) {
								curProviderName[iTemp] = p.getFullName();
							}
							iTemp++;
						}
					}
				}
				else {
					List<MyGroup> results = myGroupDao.getGroupByGroupNo(mygroupno);
					Collections.sort(results,MyGroup.MyGroupNoViewOrderComparator);

					for(MyGroup result:results) {
						curProvider_no[iTemp] = String.valueOf(result.getId().getProviderNo());

						Provider p = providerDao.getProvider(curProvider_no[iTemp]);
						if (p!=null) {
							curProviderName[iTemp] = p.getFullName();
						}
						iTemp++;
					}
				}


			}
		} else { //single view
			numProvider=1;
			curProvider_no = new String [numProvider];
			curProviderName = new String [numProvider];
			curProvider_no[0]=request.getParameter("curProvider");
			curProviderName[0]=request.getParameter("curProviderName");
		}
		String bgcolordef = "#486ebd" ;
		String [] param3 = new String[2];
		param3[0] = strDate;
		for(nProvider=0;nProvider<numProvider;nProvider++) {
			param3[1] = curProvider_no[nProvider];
			List<Object[]> results = scheduleDateDao.search_appttimecode(ConversionUtils.fromDateString(strDate), curProvider_no[nProvider]);
			for(Object[] result:results) {
				ScheduleTemplate st = (ScheduleTemplate)result[0];
				ScheduleDate sd = (ScheduleDate)result[1];
				dateTimeCodeBean.put(sd.getProviderNo(), st.getTimecode());
			}

		}
	}

	String newticklerwarningwindow=null;
	String default_pmm=null;


	// Schedule display variables

	SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	TicklerManager ticklerManager= SpringUtils.getBean(TicklerManager.class);

	DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographicDao");
	DemographicCustDao demographicCustDao = SpringUtils.getBean(DemographicCustDao.class);
	DemographicStudyDao demographicStudyDao = SpringUtils.getBean(DemographicStudyDao.class);
	StudyDao studyDao = SpringUtils.getBean(StudyDao.class);
	LookupListManager lookupListManager = SpringUtils.getBean(LookupListManager.class);
	LookupList reasonCodes = lookupListManager.findLookupListByName(loggedInInfo1, "reasonCode");
	PreventionManager prevMgr = (PreventionManager)SpringUtils.getBean("preventionMgr");
	UserPropertyDAO propDao =(UserPropertyDAO)SpringUtils.getBean("UserPropertyDAO");

	Map<Integer,LookupListItem> reasonCodesMap = new  HashMap<Integer,LookupListItem>();
	for(LookupListItem lli:reasonCodes.getItems()) {
		reasonCodesMap.put(lli.getId(),lli);
	}

	String [] param3 = new String [2];
	String bgcolordef = "#486ebd" ;
	boolean bDispTemplatePeriod = ( oscarProperties.getProperty("receptionist_alt_view") != null && oscarProperties.getProperty("receptionist_alt_view").equals("yes") ); // true - display as schedule template period, false - display as preference
	String programId_oscarView="0";
	session.setAttribute("programId_oscarView",programId_oscarView);

	Calendar apptDate = Calendar.getInstance();
	apptDate.set(year, month-1 , day);
	Calendar minDate = Calendar.getInstance();
	minDate.set( minDate.get(Calendar.YEAR), minDate.get(Calendar.MONTH), minDate.get(Calendar.DATE) );
	String allowDay = "";
	if (apptDate.equals(minDate)) {
		allowDay = "Yes";
	} else {
		allowDay = "No";
	}
	minDate.add(Calendar.DATE, 7);
	String allowWeek = "";
	if (apptDate.before(minDate)) {
		allowWeek = "Yes";
	} else {
		allowWeek = "No";
	}

	String tickler_no="", textColor="", tickler_note="";
	String ver = "", roster="";
	String mob = "";
	String dob = "";
	String demBday = "";
	StringBuffer study_no=null, study_link=null,studyDescription=null;
	String studySymbol = "\u03A3", studyColor = "red";

	String resourcebaseurl =  oscarProperties.getProperty("resource_base_url");

	UserProperty rbu = userPropertyDao.getProp("resource_baseurl");
	if(rbu != null) {
		resourcebaseurl = rbu.getValue();
	}

	String resourcehelpHtml = "";
	UserProperty rbuHtml = userPropertyDao.getProp("resource_helpHtml");
	if(rbuHtml != null) {
		resourcehelpHtml = rbuHtml.getValue();
	}

	boolean showOldEchartLink = true;
	UserProperty oldEchartLink = propDao.getProp(curUser_no, UserProperty.HIDE_OLD_ECHART_LINK_IN_APPT);
	if (oldEchartLink!=null && "Y".equals(oldEchartLink.getValue())) showOldEchartLink = false;

	boolean bShortcutIntakeForm = oscarProperties.getProperty("appt_intake_form", "").equalsIgnoreCase("on") ? true : false;

	String caisiBillingPreferenceNotDelete = null;


%>
<html>
<head>
	<title>Title</title>

	<link rel="stylesheet" href="../css/receptionistapptstyle.css" type="text/css">
	<link rel="stylesheet" href="../css/helpdetails.css" type="text/css">

	<script type="text/javascript" src="../share/javascript/Oscar.js" ></script>
	<script type="text/javascript" src="../share/javascript/prototype.js"></script>
	<script type="text/javascript" src="../phr/phr.js"></script>

	<script src="<c:out value="../js/jquery.js"/>"></script>
	<script>
		jQuery.noConflict();
	</script>

	<script type="text/javascript" src="schedulePage.js.jsp"></script>
</head>
<body bgcolor="#EEEEFF" onLoad="refreshAllTabAlerts();scrollOnLoad();" topmargin="0" leftmargin="0" rightmargin="0">




<script>
	jQuery(document).ready(function(){
		jQuery.get("<%=request.getContextPath()%>/SystemMessage.do?method=view","html",function(data,textStatus){
			jQuery("#system_message").html(data);
		});
		jQuery.get("<%=request.getContextPath()%>/FacilityMessage.do?method=view","html",function(data,textStatus){
			jQuery("#facility_message").html(data);
		});
	});
</script>



<%

// =================================================================================================
// START OF MENU BAR
// =================================================================================================

%>

<table BORDER="0" CELLPADDING="0" CELLSPACING="0" WIDTH="100%" id="firstTable" class="noprint">
	<tr>
		<td align="center" >
			<a href="../web/" title="OSCAR EMR"><img src="<%=request.getContextPath()%>/images/oscar_small.png" border="0"></a>
		</td>
		<td id="firstMenu">
			<ul id="navlist">
				<c:if test="${menuBarController.isInfirmaryOscarView()}">
					<c:choose>
						<c:when test="${menuBarController.isViewAll()}">
							<li>
								<a href=# onClick = "review('0')" title="<bean:message key="provider.appointmentProviderAdminDay.viewProvAval"/>"><bean:message key="provider.appointmentProviderAdminDay.schedView"/></a>
							</li>
						</c:when>
						<c:otherwise>
							<li>
								<a href='providercontrol.jsp?year=<%=menuBarController.getCurrentYear()%>&month=<%=menuBarController.getCurrentMonth()%>&day=<%=menuBarController.getCurrentDay()%>&view=0&displaymode=day&dboperation=searchappointmentday&viewall=1'><bean:message key="provider.appointmentProviderAdminDay.schedView"/></a>
							</li>
						</c:otherwise>
					</c:choose>
				</c:if>

				<li>
					<a href='providercontrol.jsp?year=<%=curYear%>&month=<%=curMonth%>&day=<%=curDay%>&view=0&displaymode=day&dboperation=searchappointmentday&caseload=1&clProv=<%=curUser_no%>'><bean:message key="global.caseload"/></a>
				</li>

				<security:oscarSec roleName="<%=roleName$%>" objectName="_search" rights="r">
					<li id="search">
						<a HREF="#" ONCLICK ="popupPage2('../demographic/search.jsp');return false;"  TITLE='<bean:message key="global.searchPatientRecords"/>' OnMouseOver="window.status='<bean:message key="global.searchPatientRecords"/>' ; return true"><bean:message key="provider.appointmentProviderAdminDay.search"/></a>
					</li>
				</security:oscarSec>

				<security:oscarSec roleName="<%=roleName$%>" objectName="_report" rights="r">
					<li>
						<a HREF="#" ONCLICK ="popupPage2('../report/reportindex.jsp','reportPage');return false;"   TITLE='<bean:message key="global.genReport"/>' OnMouseOver="window.status='<bean:message key="global.genReport"/>' ; return true"><bean:message key="global.report"/></a>
					</li>
				</security:oscarSec>
				<oscar:oscarPropertiesCheck property="NOT_FOR_CAISI" value="no" defaultVal="true">

					<security:oscarSec roleName="<%=roleName$%>" objectName="_billing" rights="r">
						<li>
							<a HREF="#" ONCLICK ="popupPage2('../billing/CA/<%=prov%>/billingReportCenter.jsp?displaymode=billreport&providerview=<%=curUser_no%>');return false;" TITLE='<bean:message key="global.genBillReport"/>' onMouseOver="window.status='<bean:message key="global.genBillReport"/>';return true"><bean:message key="global.billing"/></a>
						</li>
					</security:oscarSec>

					<security:oscarSec roleName="<%=roleName$%>" objectName="_appointment.doctorLink" rights="r">
						<li>
							<a HREF="#" ONCLICK ="popupInboxManager('../dms/inboxManage.do?method=prepareForIndexPage&providerNo=<%=curUser_no%>', 'Lab');return false;" TITLE='<bean:message key="provider.appointmentProviderAdminDay.viewLabReports"/>'>
								<span id="oscar_new_lab"><bean:message key="global.lab"/></span>
							</a>
							<oscar:newUnclaimedLab>
								<a class="tabalert" HREF="#" ONCLICK ="popupInboxManager('../dms/inboxManage.do?method=prepareForIndexPage&providerNo=0&searchProviderNo=0&status=N&lname=&fname=&hnum=&pageNum=1&startIndex=0', 'Lab');return false;" TITLE='<bean:message key="provider.appointmentProviderAdminDay.viewLabReports"/>'>*</a>
							</oscar:newUnclaimedLab>
						</li>
					</security:oscarSec>
				</oscar:oscarPropertiesCheck>


				<security:oscarSec roleName="<%=roleName$%>" objectName="_msg" rights="r">
					<li>
						<a HREF="#" ONCLICK ="popupOscarRx(600,1024,'../oscarMessenger/DisplayMessages.do?providerNo=<%=curUser_no%>&userName=<%=URLEncoder.encode(userFirstName+" "+userLastName)%>')" title="<bean:message key="global.messenger"/>">
							<span id="oscar_new_msg"><bean:message key="global.msg"/></span></a>
					</li>
				</security:oscarSec>
				<security:oscarSec roleName="<%=roleName$%>" objectName="_con" rights="r">
					<li id="con">
						<a HREF="#" ONCLICK ="popupOscarRx(625,1024,'../oscarEncounter/IncomingConsultation.do?providerNo=<%=curUser_no%>&userName=<%=URLEncoder.encode(userFirstName+" "+userLastName)%>')" title="<bean:message key="provider.appointmentProviderAdminDay.viewConReq"/>">
							<span id="oscar_aged_consults"><bean:message key="global.con"/></span></a>
					</li>
				</security:oscarSec>
				<security:oscarSec roleName="<%=roleName$%>" objectName="_pref" rights="r">
					<li>    <!-- remove this and let providerpreference check -->
						<a href=# onClick ="popupPage(715,680,'providerpreference.jsp');return false;" TITLE='<bean:message key="provider.appointmentProviderAdminDay.msgSettings"/>' OnMouseOver="window.status='<bean:message key="provider.appointmentProviderAdminDay.msgSettings"/>' ; return true"><bean:message key="global.pref"/></a>
					</li>
				</security:oscarSec>
				<security:oscarSec roleName="<%=roleName$%>" objectName="_edoc" rights="r">
					<li>
						<a HREF="#" onclick="popup('700', '1024', '../dms/documentReport.jsp?function=provider&functionid=<%=curUser_no%>&curUser=<%=curUser_no%>', 'edocView');" TITLE='<bean:message key="provider.appointmentProviderAdminDay.viewEdoc"/>'><bean:message key="global.edoc"/></a>
					</li>
				</security:oscarSec>
				<security:oscarSec roleName="<%=roleName$%>" objectName="_tickler" rights="r">
					<li>
						<a HREF="#" ONCLICK ="popupPage2('../tickler/ticklerMain.jsp','<bean:message key="global.tickler"/>');return false;" TITLE='<bean:message key="global.tickler"/>'>
							<span id="oscar_new_tickler"><bean:message key="global.btntickler"/></span></a>
					</li>
				</security:oscarSec>

				<oscar:oscarPropertiesCheck property="referral_menu" value="yes">
					<security:oscarSec roleName="<%=roleName$%>" objectName="_admin,_admin.misc" rights="r">
						<li id="ref">
							<a href="#" onclick="popupPage(550,800,'../admin/ManageBillingReferral.do');return false;"><bean:message key="global.manageReferrals"/></a>
						</li>
					</security:oscarSec>
				</oscar:oscarPropertiesCheck>

				<oscar:oscarPropertiesCheck property="WORKFLOW" value="yes">
					<li><a href="javascript: function myFunction() {return false; }" onClick="popup(700,1024,'../oscarWorkflow/WorkFlowList.jsp','<bean:message key="global.workflow"/>')"><bean:message key="global.btnworkflow"/></a></li>
				</oscar:oscarPropertiesCheck>

				<c:if test="${menuBarController.isK2AEnabled()}">
					<li>
						<a href="javascript:void(0);" id="K2ALink">K2A<span><sup id="k2a_new_notifications"></sup></span></a>
						<script type="text/javascript">
							function getK2AStatus(){
								jQuery.get("../ws/rs/resources/notifications/number",
									function (data) {
										var returnVal = JSON.parse(data).body;
										if (returnVal === "-") { //If user is not logged in
											jQuery("#K2ALink").click(function () {
												var win = window.open('../apps/oauth1.jsp?id=K2A', 'appAuth', 'width=700,height=450,scrollbars=1');
												win.focus();
											});
										} else {
											jQuery("#k2a_new_notifications").text(returnVal);
											jQuery("#K2ALink").click(function () {
												var win = window.open("<%=request.getContextPath()%>/web/#!/k2aNotification",
													'appAuth', 'width=450,height=700,scrollbars=1');
												win.focus();
											});
										}
									}, "json");
							}
							getK2AStatus();
						</script>
					</li>
				</c:if>

				<security:oscarSec roleName="<%=roleName$%>" objectName="_admin,_admin.userAdmin,_admin.schedule,_admin.billing,_admin.resource,_admin.reporting,_admin.backup,_admin.messenger,_admin.eform,_admin.encounter,_admin.consult,_admin.misc,_admin.fax" rights="r">

					<li id="admin2">
						<a href="javascript:void(0)" id="admin-panel" TITLE='Administration Panel' onclick="newWindow('<%=request.getContextPath()%>/administration/','admin')">Administration</a>
					</li>

					<security:oscarSec roleName="<%=roleName$%>" objectName="_dashboardDisplay" rights="r">
						<c:if test="menuBarController.hasDashboards()">
							<li id="dashboardList">
								<div class="dropdown">
									<a href="#" class="dashboardBtn">Dashboard</a>
									<div class="dashboardDropdown">
										<c:forEach items="${ menuBarController.getDashboards() }" var="dashboard" >
											<a href="javascript:void(0)" onclick="newWindow('<%=request.getContextPath()%>/web/dashboard/display/DashboardDisplay.do?method=getDashboard&dashboardId=${ dashboard.id }','admin')">
												<c:out value="${ dashboard.name }" />
											</a>
										</c:forEach>
									</div>
								</div>
							</li>
						</c:if>

					</security:oscarSec>

					<!-- Added logout link for mobile version -->
					<li id="logoutMobile">
						<a href="../logout.jsp"><bean:message key="global.btnLogout"/></a>
					</li>
				</security:oscarSec>

				<!-- plugins menu extension point add -->
				<%
					int pluginMenuTagNumber=0;
				%>
				<plugin:pageContextExtension serviceName="oscarMenuExtension" stemFromPrefix="Oscar"/>
				<logic:iterate name="oscarMenuExtension.points" id="pt" scope="page" type="oscar.caisi.OscarMenuExtension">
					<%
						if (oscar.util.plugin.IsPropertiesOn.propertiesOn(pt.getName().toLowerCase())) {
							pluginMenuTagNumber++;
					%>

					<li><a href='<html:rewrite page="<%=pt.getLink()%>"/>'>
						<%=pt.getName()%></a></li>
					<%
						}
					%>
				</logic:iterate>

				<!-- plugin menu extension point add end-->

				<%
					int menuTagNumber=0;
				%>


			</ul>  <!--- old TABLE -->

		</td>


		<td align="right" valign="bottom" >
			<a href="javascript: function myFunction() {return false; }" onClick="popup(700,1024,'../scratch/index.jsp','scratch')"><span id="oscar_scratch"></span></a>&nbsp;

			<a href=# onClick="popupPage(700,1024,'<%=resourceBaseUrl%>')">
				<img src="<%=request.getContextPath()%>/images/life-buoy-icon-small.png" border="0" align="absbottom" title="Help"></a>

			<a id="helpLink" href="javascript:void(0)" onClick ="popupPage(600,750,'<%=resourceBaseUrl%>')"><bean:message key="global.help"/></a>

			| <a href="../logout.jsp"><bean:message key="global.btnLogout"/>&nbsp;</a>

		</td>


	</tr>
</table>

<%

// =================================================================================================
// END OF MENU BAR
// =================================================================================================

%>

<div id="system_message"></div>
<div id="facility_message"></div>


<table id="appointmentTable" BORDER="0" CELLPADDING="1" CELLSPACING="0" WIDTH="100%" BGCOLOR="#C0C0C0">

	<!-- START IVORY BAR -->
	<tr id="ivoryBar">
		<td id="dateAndCalendar" BGCOLOR="ivory" width="33%">
			<a class="redArrow" href="providercontrol.jsp?year=<%=year%>&month=<%=month%>&day=<%=isWeekView(request)?(day-7):(day-1)%>&view=<%=view==0?"0":("1&curProvider="+request.getParameter("curProvider")+"&curProviderName="+URLEncoder.encode(request.getParameter("curProviderName"),"UTF-8") )%>&displaymode=day&dboperation=searchappointmentday<%=isWeekView(request)?"&provider_no="+provNum:""%>&viewall=<%=viewall%>">
				&nbsp;&nbsp;<img src="../images/previous.gif" WIDTH="10" HEIGHT="9" BORDER="0" class="noprint" ALT="<bean:message key="provider.appointmentProviderAdminDay.viewPrevDay"/>" vspace="2"></a>
			<b><span class="dateAppointment"><%
				if (isWeekView(request)) {
			%><bean:message key="provider.appointmentProviderAdminDay.week"/> <%=week%><%
			} else {
			%><%=formatDate%><%
				}
			%></span></b>
			<a class="redArrow" href="providercontrol.jsp?year=<%=year%>&month=<%=month%>&day=<%=isWeekView(request)?(day+7):(day+1)%>&view=<%=view==0?"0":("1&curProvider="+request.getParameter("curProvider")+"&curProviderName="+URLEncoder.encode(request.getParameter("curProviderName"),"UTF-8") )%>&displaymode=day&dboperation=searchappointmentday<%=isWeekView(request)?"&provider_no="+provNum:""%>&viewall=<%=viewall%>">
				<img src="../images/next.gif" WIDTH="10" HEIGHT="9" BORDER="0" class="noprint" ALT="<bean:message key="provider.appointmentProviderAdminDay.viewNextDay"/>" vspace="2">&nbsp;&nbsp;</a>
			<a id="calendarLink" href=# onClick ="popupPage(425,430,'../share/CalendarPopup.jsp?urlfrom=../provider/providercontrol.jsp&year=<%=strYear%>&month=<%=strMonth%>&param=<%=URLEncoder.encode("&view=0&displaymode=day&dboperation=searchappointmentday&viewall="+viewall,"UTF-8")%><%=isWeekView(request)?URLEncoder.encode("&provider_no="+provNum, "UTF-8"):""%>')"><bean:message key="global.calendar"/></a>

			<logic:notEqual name="infirmaryView_isOscar" value="false">
				| <% if(request.getParameter("viewall")!=null && request.getParameter("viewall").equals("1") ) { %>
				<!-- <span style="color:#333"><bean:message key="provider.appointmentProviderAdminDay.viewAll"/></span> -->
				<u><a href=# onClick = "review('0')" title="<bean:message key="provider.appointmentProviderAdminDay.viewAllProv"/>"><bean:message key="provider.appointmentProviderAdminDay.schedView"/></a></u>

				<%}else{%>
				<u><a href=# onClick = "review('1')" title="<bean:message key="provider.appointmentProviderAdminDay.viewAllProv"/>"><bean:message key="provider.appointmentProviderAdminDay.viewAll"/></a></u>
				<%}%>
			</logic:notEqual>

			<security:oscarSec roleName="<%=roleName$%>" objectName="_day" rights="r">
				| <a class="rightButton top" href="providercontrol.jsp?year=<%=curYear%>&month=<%=curMonth%>&day=<%=curDay%>&view=<%=view==0?"0":("1&curProvider="+request.getParameter("curProvider")+"&curProviderName="+URLEncoder.encode(request.getParameter("curProviderName"),"UTF-8") )%>&displaymode=day&dboperation=searchappointmentday&viewall=<%=viewall%>" TITLE='<bean:message key="provider.appointmentProviderAdminDay.viewDaySched"/>' OnMouseOver="window.status='<bean:message key="provider.appointmentProviderAdminDay.viewDaySched"/>' ; return true"><bean:message key="global.today"/></a>
			</security:oscarSec>
			<security:oscarSec roleName="<%=roleName$%>" objectName="_month" rights="r">

				| <a href="providercontrol.jsp?year=<%=year%>&month=<%=month%>&day=1&view=<%=view==0?"0":("1&curProvider="+request.getParameter("curProvider")+"&curProviderName="+URLEncoder.encode(request.getParameter("curProviderName"),"UTF-8") )%>&displaymode=month&dboperation=searchappointmentmonth&viewall=<%=viewall%>" TITLE='<bean:message key="provider.appointmentProviderAdminDay.viewMonthSched"/>' OnMouseOver="window.status='<bean:message key="provider.appointmentProviderAdminDay.viewMonthSched"/>' ; return true"><bean:message key="global.month"/></a>

			</security:oscarSec>


			<%
				boolean anonymousEnabled = false;
				if (loggedInInfo1.getCurrentFacility() != null) {
					anonymousEnabled = loggedInInfo1.getCurrentFacility().isEnableAnonymous();
				}
				if(anonymousEnabled) {
			%>
			&nbsp;&nbsp;(<a href="#" onclick="popupPage(710, 1024,'<html:rewrite page="/PMmodule/createAnonymousClient.jsp"/>?programId=<%=(String)session.getAttribute(SessionConstants.CURRENT_PROGRAM_ID)%>');return false;">New Anon Client</a>)
			<%
				}
			%>
			<%
				boolean epe = false;
				if (loggedInInfo1.getCurrentFacility() != null) {
					epe = loggedInInfo1.getCurrentFacility().isEnablePhoneEncounter();
				}
				if(epe) {
			%>
			&nbsp;&nbsp;(<a href="#" onclick="popupPage(710, 1024,'<html:rewrite page="/PMmodule/createPEClient.jsp"/>?programId=<%=(String)session.getAttribute(SessionConstants.CURRENT_PROGRAM_ID)%>');return false;">Phone Encounter</a>)
			<%
				}
			%>
		</td>

		<td class="title noprint" ALIGN="center"  BGCOLOR="ivory" width="33%">

			<%
				if (isWeekView(request)) {
					for(int provIndex=0;provIndex<numProvider;provIndex++) {
						if (curProvider_no[provIndex].equals(provNum)) {
			%>
			<bean:message key="provider.appointmentProviderAdminDay.weekView"/>: <%=curProviderName[provIndex]%>
			<%
					} } } else { if (view==1) {
			%>
			<a href='providercontrol.jsp?year=<%=strYear%>&month=<%=strMonth%>&day=<%=strDay%>&view=0&displaymode=day&dboperation=searchappointmentday'><bean:message key="provider.appointmentProviderAdminDay.grpView"/></a>
			<% } else { %>
			<bean:message key="global.hello"/>
			<% out.println( userFirstName+" "+userLastName); %>
		</td>
		<% } } %>

		<td id="group" ALIGN="RIGHT" BGCOLOR="Ivory">

			<caisi:isModuleLoad moduleName="TORONTO_RFQ" reverse="true">
				<form method="post" name="findprovider" onSubmit="findProvider(<%=year%>,<%=month%>,<%=day%>);return false;" target="apptReception" action="receptionistfindprovider.jsp" style="display:inline;margin:0px;padding:0px;padding-right:10px">
					<INPUT TYPE="text" NAME="providername" VALUE="" WIDTH="2" HEIGHT="10" border="0" size="10" maxlength="10" class="noprint" title="Find a Provider" placeholder="Enter Lastname">
					<INPUT TYPE="SUBMIT" NAME="Go" VALUE='<bean:message key="provider.appointmentprovideradminmonth.btnGo"/>' class="noprint" onClick="findProvider(<%=year%>,<%=month%>,<%=day%>);return false;">
				</form>
			</caisi:isModuleLoad>

			<form name="appointmentForm" style="display:inline;margin:0px;padding:0px;">
					<% if (isWeekView(request)) { %>
				<bean:message key="provider.appointmentProviderAdminDay.provider"/>:
				<select name="provider_select" onChange="goWeekView(this.options[this.selectedIndex].value)">
					<%
						for (nProvider=0;nProvider<numProvider;nProvider++) {
					%>
					<option value="<%=curProvider_no[nProvider]%>"<%=curProvider_no[nProvider].equals(provNum)?" selected":""%>><%=curProviderName[nProvider]%></option>
					<%
						}
					%>

				</select>

					<%
	} else {
%>

				<!-- caisi infirmary view extension add ffffffffffff-->

					<caisi:isModuleLoad moduleName="oscarClinic">
						<%
							session.setAttribute("infirmaryView_isOscar", "true");
						%>
					</caisi:isModuleLoad>
					<!-- caisi infirmary view extension add end ffffffffffffff-->


					<logic:notEqual name="infirmaryView_isOscar" value="false">

						<%
							//session.setAttribute("case_program_id", null);
						%>
						<!--  multi-site , add site dropdown list -->
						<%
							if (bMultisites) {
						%>
						<script>
							function changeSite(sel) {
								sel.style.backgroundColor=sel.options[sel.selectedIndex].style.backgroundColor;
								var siteName = sel.options[sel.selectedIndex].value;
								var newGroupNo = "<%=(mygroupno == null ? ".default" : mygroupno)%>";
								<%if (org.oscarehr.common.IsPropertiesOn.isCaisiEnable() && org.oscarehr.common.IsPropertiesOn.isTicklerPlusEnable()){%>
								popupPage(10,10, "providercontrol.jsp?provider_no=<%=curUser_no%>&start_hour=<%=startHour%>&end_hour=<%=endHour%>&every_min=<%=everyMin%>&new_tickler_warning_window=<%=newticklerwarningwindow%>&default_pmm=<%=default_pmm%>&color_template=deepblue&dboperation=updatepreference&displaymode=updatepreference&mygroup_no="+newGroupNo+"&site="+siteName);
								<%}else {%>
								popupPage(10,10, "providercontrol.jsp?provider_no=<%=curUser_no%>&start_hour=<%=startHour%>&end_hour=<%=endHour%>&every_min=<%=everyMin%>&color_template=deepblue&dboperation=updatepreference&displaymode=updatepreference&mygroup_no="+newGroupNo+"&site="+siteName);
								<%}%>
							}
						</script>

						<select id="site" name="site" onchange="changeSite(this)" style="background-color: <%=( selectedSite == null || siteBgColor.get(selectedSite) == null ? "#FFFFFF" : siteBgColor.get(selectedSite))%>">
							<option value="none" style="background-color:white">---all clinic---</option>
							<%
								for (int i=0; i<curUserSites.size(); i++) {
							%>
							<option value="<%=curUserSites.get(i).getName()%>" style="background-color:<%=curUserSites.get(i).getBgColor()%>"
									<%=(curUserSites.get(i).getName().equals(selectedSite)) ? " selected " : ""%> >
								<%=curUserSites.get(i).getName()%>
							</option>
							<%
								}
							%>
						</select>
						<%
							}
						%>
						<span><bean:message key="global.group"/>:</span>

						<%
							List<MyGroupAccessRestriction> restrictions = myGroupAccessRestrictionDao.findByProviderNo(curUser_no);
						%>
						<select id="mygroup_no" name="mygroup_no" onChange="changeGroup(this)">
							<option value=".<bean:message key="global.default"/>">.<bean:message key="global.default"/></option>


							<security:oscarSec roleName="<%=roleName$%>" objectName="_team_schedule_only" rights="r" reverse="false">
								<%
									String provider_no = curUser_no;
									for(Provider p : providerDao.getActiveProviders()) {
										boolean skip = checkRestriction(restrictions,p.getProviderNo());
										if(!skip) {
								%>
								<option value="<%=p.getProviderNo()%>" <%=mygroupno.equals(p.getProviderNo())?"selected":""%>>
									<%=p.getFormattedName()%></option>
								<%
										} }
								%>

							</security:oscarSec>
							<security:oscarSec roleName="<%=roleName$%>" objectName="_team_schedule_only" rights="r" reverse="true">
								<%
									request.getSession().setAttribute("archiveView","false");
									for(MyGroup g : myGroupDao.searchmygroupno()) {

										boolean skip = checkRestriction(restrictions,g.getId().getMyGroupNo());

										if (!skip && (!bMultisites || siteGroups == null || siteGroups.size() == 0 || siteGroups.contains(g.getId().getMyGroupNo()))) {
								%>
								<option value="<%="_grp_"+g.getId().getMyGroupNo()%>"
										<%=mygroupno.equals(g.getId().getMyGroupNo())?"selected":""%>><%=g.getId().getMyGroupNo()%></option>
								<%
										}
									}

									for(Provider p : providerDao.getActiveProvidersByType("doctor")) {
										boolean skip = checkRestriction(restrictions,p.getProviderNo());

										if (!skip && (!bMultisites || siteProviderNos  == null || siteProviderNos.size() == 0 || siteProviderNos.contains(p.getProviderNo()))) {
								%>
								<option value="<%=p.getProviderNo()%>" <%=mygroupno.equals(p.getProviderNo())?"selected":""%>>
									<%=p.getFormattedName()%></option>
								<%
										}
									}
								%>
							</security:oscarSec>
						</select>

					</logic:notEqual>

					<logic:equal name="infirmaryView_isOscar" value="false">
						&nbsp;&nbsp;&nbsp;&nbsp;
					</logic:equal>

					<%
						}
					%>

				</td>
	</tr>
	<!-- END IVORY BAR -->


	<!-- ======================================================================================= -->
	<!-- ======================================================================================= -->
	<!-- ======================================================================================= -->



	<!-- START Schedule page -->
	<tr>
		<td colspan="3">
			<table border="0" cellpadding="0" bgcolor="#486ebd" cellspacing="0" width="100%">
				<tr>
					<%

						boolean headerColor = true;
						boolean showApptCountForProvider = OscarProperties.getInstance().isPropertyActive("schedule.show_appointment_count");
						boolean userAvail = true;

						// Get schedules
						//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						//LocalDate selectedDate = LocalDate.parse(strDate);
						ResourceSchedule resourceScheduleDTO = scheduleService.getResourceSchedule(mygroupno, selectedDate);

						List<UserDateSchedule> schedules = resourceScheduleDTO.getSchedules();

						for(UserDateSchedule schedule: schedules)
						{
							Integer providerNo = schedule.getProviderNo();

							headerColor = !headerColor;



					%>
					<td valign="top" width="<%=isWeekView(request)?100/7:100/numProvider%>%"> <!-- for the first provider's schedule -->

						<table border="0" cellpadding="0" bgcolor="#486ebd" cellspacing="0" width="100%"><!-- for the first provider's name -->
							<tr><td class="infirmaryView" NOWRAP ALIGN="center" BGCOLOR="<%=headerColor?"#bfefff":"silver"%>">
								<!-- caisi infirmary view extension modify ffffffffffff-->
								<%
								if (showApptCountForProvider) {
									int appointmentCount = 0;
									for(List<AppointmentDetails> appointmentDetailsList: schedule.getAppointments().values())
									{
										appointmentCount += appointmentDetailsList.size();
									}
								%>
								<span style="padding-right: 3px;">(<%= appointmentCount %>)</span>
								<%
								}
								%>
								<logic:notEqual name="infirmaryView_isOscar" value="false">

								<%
								if (isWeekView(request)) {
								%>
								<b><a href="providercontrol.jsp?year=<%=year%>&month=<%=month%>&day=<%=day%>&view=0&displaymode=day&dboperation=searchappointmentday"><%=formatDate%></a></b>
								<%
								} else {
								%>
								<b><input type='button' value="<bean:message key="provider.appointmentProviderAdminDay.weekLetter"/>" name='weekview' onClick=goWeekView('<%= providerNo %>') title="<bean:message key="provider.appointmentProviderAdminDay.weekView"/>" style="color:black" class="noprint">
									<input type='button' value="<bean:message key="provider.appointmentProviderAdminDay.searchLetter"/>" name='searchview' onClick=goSearchView('<%= providerNo %>') title="<bean:message key="provider.appointmentProviderAdminDay.searchView"/>" style="color:black" class="noprint">
									<b><input type='radio' name='flipview' class="noprint" onClick="goFilpView('<%= providerNo %>')" title="Flip view"  >
										<a href=# onClick="goZoomView('<%= providerNo %>','<%=StringEscapeUtils.escapeJavaScript(schedule.getFullName())%>')" onDblClick="goFilpView('<%= providerNo %>')" title="<bean:message key="provider.appointmentProviderAdminDay.zoomView"/>" >
											<%=schedule.getFullName()%></a>
										<oscar:oscarPropertiesCheck value="yes" property="TOGGLE_REASON_BY_PROVIDER" defaultVal="true">
											<a id="expandReason" href="#" onclick="return toggleReason('<%=providerNo%>');"
											   title="<bean:message key="provider.appointmentProviderAdminDay.expandreason"/>">*</a>
											<%-- Default is to hide inline reasons. --%>
											<c:set value="true" var="hideReason" />
										</oscar:oscarPropertiesCheck>
									</b>
								<%
								}
								%>

								<%
          						if (!userAvail) {
          						%>
									[<bean:message key="provider.appointmentProviderAdminDay.msgNotOnSched"/>]
								<%
          						}
          						%>
									</logic:notEqual>
									<logic:equal name="infirmaryView_isOscar" value="false">
								<%
								String prID="1";
								%>
								<logic:present name="infirmaryView_programId">
								<%
								prID=(String)session.getAttribute(SessionConstants.CURRENT_PROGRAM_ID);
								%>
								</logic:present>
								<logic:iterate id="pb" name="infirmaryView_programBeans" type="org.apache.struts.util.LabelValueBean">
								<%
						  		if (pb.getValue().equals(prID)) {
	  							%>
									<b><label><%=pb.getLabel()%></label></b>
								<%
								}
								%>
								</logic:iterate>
								</logic:equal>
								<!-- caisi infirmary view extension modify end ffffffffffffffff-->
							</td></tr>
							<tr><td valign="top">
								<table id="providerSchedule" border="0" cellpadding="0" bgcolor="<%=userAvail?"#486ebd":"silver"%>" cellspacing="0" width="100%">
								<%

								int slotLengthInMinutes = everyMin;

								if(bDispTemplatePeriod)
								{
									slotLengthInMinutes = (MINUTES_IN_DAY/schedule.getScheduleSlots().asMapOfRanges().size());
								}

								LocalTime startTime = LocalTime.of(startHour, 0);
								LocalTime endTime = LocalTime.of(endHour + 1, 0);
								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
								DateTimeFormatter titleFormatter = DateTimeFormatter.ofPattern("h:mm a");

								for(LocalTime slotTime = startTime; slotTime.isBefore(endTime); slotTime = slotTime.plusMinutes(slotLengthInMinutes))
								{
									LocalTime slotEndTime = slotTime.plusMinutes(slotLengthInMinutes);

									boolean isExactHour = false;
									if(slotTime.getMinute() == 0)
									{
										isExactHour = true;
									}

									ScheduleSlot slot = schedule.getScheduleSlots().get(slotTime);
									String durationString = "";
									String colorString = "";
									String bgColorString = "";
									String confirmString = "";
									String descriptionString = "";
									String codeString = "";
									if(slot != null)
									{
										if(slot.getDurationMinutes() != null)
										{
											durationString = slot.getDurationMinutes().toString();
										}

										if(slot.getColor() != null)
										{
											colorString = slot.getColor();
											bgColorString = "bgcolor=\"" + slot.getColor() + "\"";
										}

										if(slot.getConfirm() != null)
										{
											confirmString = slot.getConfirm();
										}

										if(slot.getDescription() != null)
										{
											descriptionString = slot.getDescription();
										}

										if(slot.getCode() != null)
										{
											codeString = slot.getCode();
										}
									}

									String url = "../appointment/addappointment.jsp" +
											"?provider_no=" + providerNo +
											"&bFirstDisp=true" +
											"&year=" + strYear +
											"&month=" + strMonth +
											"&day=" + strDay +
											"&start_time=" + slotTime.format(formatter) +
											"&end_time=" + slotTime.plusMinutes(slotLengthInMinutes - 1) +
											"&duration=" + durationString;

									String timeTitle = slotTime.format(titleFormatter) + " - " + slotEndTime.format(titleFormatter);


									// XXX: need to get all appointments before the end of this appointment

									%>

									<tr>


										<%
										// ----------------------------------------------------------------------------------
										// Build time slot
										// ----------------------------------------------------------------------------------
										%>

										<td align="RIGHT" class="<%=isExactHour?"scheduleTime00":"scheduleTimeNot00"%>" NOWRAP>
											<a
													href=#
													onClick="confirmPopupPage(400,780,'<%= url %>','<%= confirmString %>','<%=allowDay%>','<%=allowWeek%>');return false;"
													title='<%= timeTitle %>' class="adhour"
											>
												<%= slotTime.format(formatter) %>&nbsp;
											</a></td>
										<td
												class="hourmin"
												width='1%'
												<% //= bgColorString %>
												title='<%= descriptionString %>'
										>

											<font color='<%= colorString.equals(bgcolordef) ? "black":"white" %>'><%= codeString %></font>
										</td>


										<%
										// ----------------------------------------------------------------------------------
										// Build appointments
										// ----------------------------------------------------------------------------------

										SortedMap<LocalTime, List<AppointmentDetails>> appointmentLists =
											schedule.getAppointments().subMap(slotTime, slotEndTime);

										for(List<AppointmentDetails> appointments: appointmentLists.values())
										{
											Collections.reverse(appointments);
											for(AppointmentDetails appointment: appointments)
											{
												long appointmentLengthInMinutes = Duration
														.between(appointment.getStartTime(),
																appointment.getEndTime())
														.toMinutes();
												long appointmentRowSpan = (long) Math
														.ceil((double) appointmentLengthInMinutes / slotLengthInMinutes);

												String sitename = String.valueOf(appointment.getLocation()).trim();

												DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

												ApptStatusData apptStatusData = new ApptStatusData(appointment.getStatus());

											%>

											<td class="appt" bgcolor='<%= appointment.getColor() %>' rowspan="<%= appointmentRowSpan %>" nowrap>
												<c:if test="${appointmentInfo.trueTest}">
													<div>${appointmentInfo.testString}</div>
												</c:if>

												<c:if test="${appointmentInfo.falseTest}"><div>TESTING FALSE</div></c:if>

												<!-- #### Self booking notice -->
												<%
													if (Appointment.BookingSource.MYOSCAR_SELF_BOOKING.equals(appointment.getBookingSource()))
													{
												%>
												<c:if test="${appointmentInfo.selfBooked}">
													<bean:message key="provider.appointmentProviderAdminDay.SelfBookedMarker"/>
												</c:if>
												<%
													}
												%>


												<!-- #### multisites : add colour-coded to the "location" value of that appointment. -->
												<%if (bMultisites) {%>
												<span title="<%= sitename %>" style="background-color:<%=siteBgColor.get(sitename)%>;">&nbsp;</span>|
												<%} %>


												<!-- Appointment status image -->
												<%
													if (apptStatusData.getNextStatus() != null) {

														if(OscarProperties.getInstance().getProperty("APPT_SHOW_SHORT_LETTERS", "false") != null
																&& OscarProperties.getInstance().getProperty("APPT_SHOW_SHORT_LETTERS", "false").equals("true"))
														{
															String colour = appointment.getShortLetterColour();
															if(colour == null){
																colour = "#FFFFFF";
															}

												%>
												<span
														class='short_letters'
														style='color:<%= colour%>;border:0;height:10'>
													[<%=UtilMisc.htmlEscape(appointment.getShortLetters())%>]
												</span>
												<%
														}
														else
														{
												%>

												<img src="../images/<%= appointment.getIconImage() %>" border="0" height="10" title="<%= appointment.getStatusTitle() %>" />

												<%
														}
													}
													else
													{
														out.print("&nbsp;");
													}

												%>



												<!-- Demographic name and link to edit appointment  -->
												<%
													/*
													String linkClass = "";
													if(appointment.getDemographicNo() == 0)
													{
														linkClass = " class=\"apptLink\"";
													}

													String reason = appointment.getReason();

													String reasonCodeName = null;
													if(appointment.getReasonCode() != null)    {
														LookupListItem lli  = reasonCodesMap.get(appointment.getReasonCode());
														if(lli != null) {
															reasonCodeName = lli.getLabel();
														}
													}

													if ( "yes".equalsIgnoreCase(OscarProperties.getInstance().getProperty("SHOW_APPT_TYPE_WITH_REASON")) ) {
														reasonCodeName = ( appointment.getType() + " : " + reasonCodeName );
													}
													//String name = UtilMisc.toUpperLowerCase(appointment.getName();
													String nameString = name.toUpperCase()
													if(view == 0 && numAvailProvider != 1 && name.length() > len)
													{
														nameString = name.substring(0,len).toUpperCase();
													}
													*/
												%>









											</td>



											<%
											}
										}


										// ----------------------------------------------------------------------------------
										// End Build appointments
										// ----------------------------------------------------------------------------------
										%>





										<td class="noGrid" width="1">
										</td>
									</tr>
									<%
								}
								%>
								</table>
							</td></tr>
						</table>
					</td>
						<%
						}
						%>
				</tr>
			</table>
		</td>
	</tr>

	<tr><td colspan="3">
			<table border="0" cellpadding="0" bgcolor="#486ebd" cellspacing="0" width="100%">
							<tr>
								<%
									boolean bShowDocLink = false;
									boolean bShowEncounterLink = false;
								%>
								<security:oscarSec roleName="<%=roleName$%>" objectName="_appointment.doctorLink" rights="r">
									<%
										bShowDocLink = true;
									%>
								</security:oscarSec>
								<security:oscarSec roleName="<%=roleName$%>" objectName="_eChart" rights="r">
									<%
										bShowEncounterLink = true;
									%>
								</security:oscarSec>


								<%




									int hourCursor=0, minuteCursor=0, depth=everyMin; //depth is the period, e.g. 10,15,30,60min.
									String am_pm=null;
									boolean bColor=true, bColorHour=true; //to change color

									int iCols=0, iRows=0, iS=0,iE=0,iSm=0,iEm=0; //for each S/E starting/Ending hour, how many events
									int ih=0, im=0, iSn=0, iEn=0 ; //hour, minute, nthStartTime, nthEndTime, rowspan
									boolean bFirstTimeRs=true;
									boolean bFirstFirstR=true;
									Object[] paramTickler = new Object[2];
									String[] param = new String[2];
									String strsearchappointmentday=request.getParameter("dboperation");

									int me = -1;
									for(nProvider=0;nProvider<numProvider;nProvider++) {
										if(curUser_no.equals(curProvider_no[nProvider]) ) {
											//userInGroup = true;
											me = nProvider; break;
										}
									}

									// set up the iterator appropriately (today - for each doctor; this week - for each day)
									int iterMax;
									if (isWeekView(request)) {
										iterMax=7;
										// find the nProvider value that corresponds to provNum
										if(numProvider == 1) {
											nProvider = 0;
										}
										else {
											for(int provIndex=0;provIndex<numProvider;provIndex++) {
												if (curProvider_no[provIndex].equals(provNum)) {
													nProvider=provIndex;
												}
											}
										}
									} else {
										iterMax=numProvider;
									}

									StringBuffer hourmin = null;
									String [] param1 = new String[2];

									java.util.ResourceBundle wdProp = ResourceBundle.getBundle("oscarResources", request.getLocale());


									// =====================================================
									// Loop through schedules (days, doctors, etc)
									// =====================================================
									for(int iterNum=0;iterNum<iterMax;iterNum++) {

										if (isWeekView(request)) {
											// get the appropriate datetime objects for the current day in this week
											year = cal.get(Calendar.YEAR);
											month = (cal.get(Calendar.MONTH)+1);
											day = cal.get(Calendar.DAY_OF_MONTH);

											strDate = year + "-" + month + "-" + day;
											monthDay = String.format("%02d", month) + "-" + String.format("%02d", day);

											inform = new SimpleDateFormat ("yyyy-MM-dd", request.getLocale());
											try {
												formatDate = UtilDateUtilities.DateToString(inform.parse(strDate), wdProp.getString("date.EEEyyyyMMdd"),request.getLocale());
											} catch (Exception e) {
												MiscUtils.getLogger().error("Error", e);
												formatDate = UtilDateUtilities.DateToString(inform.parse(strDate), "EEE, yyyy-MM-dd");
											}
											strYear=""+year;
											strMonth=month>9?(""+month):("0"+month);
											strDay=day>9?(""+day):("0"+day);

											// Reset timecode bean for this day
											param3[0] = strDate; //strYear+"-"+strMonth+"-"+strDay;
											param3[1] = curProvider_no[nProvider];
											dateTimeCodeBean.put(String.valueOf(provNum), "");

											List<Object[]> results = scheduleDateDao.search_appttimecode(ConversionUtils.fromDateString(strDate), curProvider_no[nProvider]);
											for(Object[] result : results) {
												ScheduleTemplate st = (ScheduleTemplate)result[0];
												ScheduleDate sd = (ScheduleDate)result[1];
												dateTimeCodeBean.put(sd.getProviderNo(), st.getTimecode());
											}


											for(ScheduleTemplateCode stc : scheduleTemplateCodeDao.findAll()) {

												dateTimeCodeBean.put("description"+stc.getCode(), stc.getDescription());
												dateTimeCodeBean.put("duration"+stc.getCode(), stc.getDuration());
												dateTimeCodeBean.put("color"+stc.getCode(), (stc.getColor()==null || "".equals(stc.getColor()))?bgcolordef:stc.getColor());
												dateTimeCodeBean.put("confirm" + stc.getCode(), stc.getConfirm());
											}

											// move the calendar forward one day
											cal.add(Calendar.DATE, 1);
										} else {
											nProvider = iterNum;
										}

										userAvail = true;
										int timecodeLength = dateTimeCodeBean.get(curProvider_no[nProvider])!=null?((String) dateTimeCodeBean.get(curProvider_no[nProvider]) ).length() : 4*24;

										if (timecodeLength == 0){
											timecodeLength = 4*24;
										}

										depth = bDispTemplatePeriod ? (24*60 / timecodeLength) : everyMin; // add function to display different time slot
										param1[0] = strDate; //strYear+"-"+strMonth+"-"+strDay;
										param1[1] = curProvider_no[nProvider];

										ScheduleDate sd = scheduleDateDao.findByProviderNoAndDate(curProvider_no[nProvider],ConversionUtils.fromDateString(strDate));

										List<Appointment> appointments = appointmentDao.searchappointmentday(curProvider_no[nProvider], ConversionUtils.fromDateString(year+"-"+month+"-"+day),ConversionUtils.fromIntString(programId_oscarView));
										Iterator<Appointment> it = appointments.iterator();


										//viewall function
										if(request.getParameter("viewall")==null || request.getParameter("viewall").equals("0") ) {
											if(sd == null|| "0".equals(String.valueOf(sd.getAvailable())) ) {
												if(nProvider!=me ) continue;
												else userAvail = false;
											}
										}
										bColor=bColor?false:true;
								%>
								<td valign="top" width="<%=isWeekView(request)?100/7:100/numProvider%>%"> <!-- for the first provider's schedule -->

									<table border="0" cellpadding="0" bgcolor="#486ebd" cellspacing="0" width="100%"><!-- for the first provider's name -->
										<tr><td class="infirmaryView" NOWRAP ALIGN="center" BGCOLOR="<%=bColor?"#bfefff":"silver"%>">
											<!-- caisi infirmary view extension modify ffffffffffff-->
											<%
												if (showApptCountForProvider) {
											%>
											<span style="padding-right: 3px;">(<%= appointments.size() %>)</span>
											<%
												}
											%>
											<logic:notEqual name="infirmaryView_isOscar" value="false">

											<%
												if (isWeekView(request)) {
											%>
											<b><a href="providercontrol.jsp?year=<%=year%>&month=<%=month%>&day=<%=day%>&view=0&displaymode=day&dboperation=searchappointmentday"><%=formatDate%></a></b>
											<%
											} else {
											%>
											<b><input type='button' value="<bean:message key="provider.appointmentProviderAdminDay.weekLetter"/>" name='weekview' onClick=goWeekView('<%=curProvider_no[nProvider]%>') title="<bean:message key="provider.appointmentProviderAdminDay.weekView"/>" style="color:black" class="noprint">
												<input type='button' value="<bean:message key="provider.appointmentProviderAdminDay.searchLetter"/>" name='searchview' onClick=goSearchView('<%=curProvider_no[nProvider]%>') title="<bean:message key="provider.appointmentProviderAdminDay.searchView"/>" style="color:black" class="noprint">
												<b><input type='radio' name='flipview' class="noprint" onClick="goFilpView('<%=curProvider_no[nProvider]%>')" title="Flip view"  >
													<a href=# onClick="goZoomView('<%=curProvider_no[nProvider]%>','<%=StringEscapeUtils.escapeJavaScript(curProviderName[nProvider])%>')" onDblClick="goFilpView('<%=curProvider_no[nProvider]%>')" title="<bean:message key="provider.appointmentProviderAdminDay.zoomView"/>" >
														<!--a href="providercontrol.jsp?year=<%=strYear%>&month=<%=strMonth%>&day=<%=strDay%>&view=1&curProvider=<%=curProvider_no[nProvider]%>&curProviderName=<%=curProviderName[nProvider]%>&displaymode=day&dboperation=searchappointmentday" title="<bean:message key="provider.appointmentProviderAdminDay.zoomView"/>"-->
														<%=curProviderName[nProvider]%></a>
													<oscar:oscarPropertiesCheck value="yes" property="TOGGLE_REASON_BY_PROVIDER" defaultVal="true">
														<a id="expandReason" href="#" onclick="return toggleReason('<%=curProvider_no[nProvider]%>');"
														   title="<bean:message key="provider.appointmentProviderAdminDay.expandreason"/>">*</a>
														<%-- Default is to hide inline reasons. --%>
														<c:set value="true" var="hideReason" />
													</oscar:oscarPropertiesCheck>
												</b>
														<% } %>

														<%
          	if (!userAvail) {
          %>
												[<bean:message key="provider.appointmentProviderAdminDay.msgNotOnSched"/>]
														<%
          	}
          %>
												</logic:notEqual>
												<logic:equal name="infirmaryView_isOscar" value="false">
														<%
		String prID="1";
	%>
												<logic:present name="infirmaryView_programId">
														<%
		prID=(String)session.getAttribute(SessionConstants.CURRENT_PROGRAM_ID);
	%>
												</logic:present>
												<logic:iterate id="pb" name="infirmaryView_programBeans" type="org.apache.struts.util.LabelValueBean">
														<%
	  		if (pb.getValue().equals(prID)) {
	  	%>
												<b><%=pb.getLabel()%></label></b>
														<%
			}
		%>
												</logic:iterate>
												</logic:equal>
												<!-- caisi infirmary view extension modify end ffffffffffffffff-->
										</td></tr>
										<tr><td valign="top">

											<!-- caisi infirmary view exteion add -->
											<!--  fffffffffffffffffffffffffffffffffffffffffff-->
											<logic:notEqual name="infirmaryView_isOscar" value="false">
												<!-- caisi infirmary view exteion add end ffffffffffffffffff-->
												<!-- =============== following block is the original oscar code. -->
												<!-- table for hours of day start -->
												<table id="providerSchedule" border="0" cellpadding="0" bgcolor="<%=userAvail?"#486ebd":"silver"%>" cellspacing="0" width="100%">
													<%
														bFirstTimeRs=true;
														bFirstFirstR=true;

														String useProgramLocation = OscarProperties.getInstance().getProperty("useProgramLocation");
														String moduleNames = OscarProperties.getInstance().getProperty("ModuleNames");
														boolean caisiEnabled = moduleNames != null && org.apache.commons.lang.StringUtils.containsIgnoreCase(moduleNames, "Caisi");
														boolean locationEnabled = caisiEnabled && (useProgramLocation != null && useProgramLocation.equals("true"));

														int length = locationEnabled ? 4 : 3;

														String [] param0 = new String[length];

														param0[0]=curProvider_no[nProvider];
														param0[1]=year+"-"+month+"-"+day;//e.g."2001-02-02";
														param0[2]=programId_oscarView;
														if (locationEnabled) {


															ProgramManager2 programManager2 = SpringUtils.getBean(ProgramManager2.class);
															ProgramProvider programProvider = programManager2.getCurrentProgramInDomain(loggedInInfo1,loggedInInfo1.getLoggedInProviderNo());
															if(programProvider!=null && programProvider.getProgram() != null) {
																programProvider.getProgram().getName();
															}
															param0[3]=request.getParameter("programIdForLocation");
															strsearchappointmentday = "searchappointmentdaywithlocation";
														}

														Appointment appointment = null;
														String router = "";
														String record = "";
														String module = "";
														String newUxUrl = "";
														String inContextStyle = "";

														if(request.getParameter("record")!=null){
															record=request.getParameter("record");
														}

														if(request.getParameter("module")!=null){
															module=request.getParameter("module");
														}
														List<Object[]> confirmTimeCode = scheduleDateDao.search_appttimecode(ConversionUtils.fromDateString(strDate), curProvider_no[nProvider]);


														// =========================================================================
														// Loop through times for creating each schedule
														// =========================================================================
														for(ih=startHour*60; ih<=(endHour*60+(60/depth-1)*depth); ih+=depth) { // use minutes as base
															hourCursor = ih/60;
															minuteCursor = ih%60;
															bColorHour=minuteCursor==0?true:false; //every 00 minute, change color

															//templatecode
															if((dateTimeCodeBean.get(curProvider_no[nProvider]) != null)&&(dateTimeCodeBean.get(curProvider_no[nProvider]) != "") && confirmTimeCode.size()!=0) {
																int nLen = 24*60 / ((String) dateTimeCodeBean.get(curProvider_no[nProvider]) ).length();
																int ratio = (hourCursor*60+minuteCursor)/nLen;
																hourmin = new StringBuffer(dateTimeCodeBean.get(curProvider_no[nProvider])!=null?((String) dateTimeCodeBean.get(curProvider_no[nProvider])).substring(ratio,ratio+1):" " );
															} else { hourmin = new StringBuffer(); }
													%>
													<tr>
														<td align="RIGHT" class="<%=bColorHour?"scheduleTime00":"scheduleTimeNot00"%>" NOWRAP>
															<a href=# onClick="confirmPopupPage(400,780,'../appointment/addappointment.jsp?provider_no=<%=curProvider_no[nProvider]%>&bFirstDisp=<%=true%>&year=<%=strYear%>&month=<%=strMonth%>&day=<%=strDay%>&start_time=<%=(hourCursor>9?(""+hourCursor):("0"+hourCursor))+":"+ (minuteCursor<10?"0":"") +minuteCursor%>&end_time=<%=(hourCursor>9?(""+hourCursor):("0"+hourCursor))+":"+(minuteCursor+depth-1)%>&duration=<%=dateTimeCodeBean.get("duration"+hourmin.toString())%>','<%=dateTimeCodeBean.get("confirm"+hourmin.toString())%>','<%=allowDay%>','<%=allowWeek%>');return false;"
															   title='<%=MyDateFormat.getTimeXX_XXampm(hourCursor +":"+ (minuteCursor<10?"0":"")+minuteCursor)%> - <%=MyDateFormat.getTimeXX_XXampm(hourCursor +":"+((minuteCursor+depth-1)<10?"0":"")+(minuteCursor+depth-1))%>' class="adhour">
																<%=(hourCursor<10?"0":"") +hourCursor+ ":"%><%=(minuteCursor<10?"0":"")+minuteCursor%>&nbsp;</a></td>
														<td class="hourmin" width='1%' <%=dateTimeCodeBean.get("color"+hourmin.toString())!=null?("bgcolor="+dateTimeCodeBean.get("color"+hourmin.toString()) ):""%> title='<%=dateTimeCodeBean.get("description"+hourmin.toString())%>'><font color='<%=(dateTimeCodeBean.get("color"+hourmin.toString())!=null && !dateTimeCodeBean.get("color"+hourmin.toString()).equals(bgcolordef) )?"black":"white"%>'><%=hourmin.toString()%></font></td>
																<%
	while (bFirstTimeRs?it.hasNext():true) { //if it's not the first time to parse the standard time, should pass it by
                  appointment = bFirstTimeRs?it.next():appointment;
                  len = bFirstTimeRs&&!bFirstFirstR?lenLimitedS:lenLimitedL;
                  String strStartTime = ConversionUtils.toTimeString(appointment.getStartTime());
                  String strEndTime = ConversionUtils.toTimeString(appointment.getEndTime());

                  iS=Integer.parseInt(String.valueOf(strStartTime).substring(0,2));
                  iSm=Integer.parseInt(String.valueOf(strStartTime).substring(3,5));
                  iE=Integer.parseInt(String.valueOf(strEndTime).substring(0,2));
              	  iEm=Integer.parseInt(String.valueOf(strEndTime).substring(3,5));

          	  if( (ih < iS*60+iSm) && (ih+depth-1)<iS*60+iSm ) { //iS not in this period (both start&end), get to the next period
          	  	//out.println("<td width='10'>&nbsp;</td>"); //should be comment
          	  	bFirstTimeRs=false;
          	  	break;
          	  }
          	  if( (ih > iE*60+iEm) ) { //appt before this time slot (both start&end), get to the next period
          	  	//out.println("<td width='10'>&nbsp;</td>"); //should be comment
          	  	bFirstTimeRs=true;
          	  	continue;
          	  }
         	    iRows=((iE*60+iEm)-ih)/depth+1; //to see if the period across an hour period
         	    //iRows=(iE-iS)*60/depth+iEm/depth-iSm/depth+1; //to see if the period across an hour period


                    int demographic_no = appointment.getDemographicNo();

                  //Pull the appointment name from the demographic information if the appointment is attached to a specific demographic.
                  //Otherwise get the name associated with the appointment from the appointment information
                  StringBuilder nameSb = new StringBuilder();
                  Boolean active_medical_coverage = false;
                  if ((demographic_no != 0)&& (demographicDao != null)) {
                        Demographic demo = demographicDao.getDemographic(String.valueOf(demographic_no));

                        // Check for active medical coverage
                        java.util.Date hc_renew_date = demo.getHcRenewDate();
                        java.util.Date todays_date = new java.util.Date();
                        if( hc_renew_date != null &&
                            hc_renew_date.getYear() == todays_date.getYear() &&
                            hc_renew_date.getMonth() == todays_date.getMonth())
                        {
                            active_medical_coverage = true;
                        }

                        nameSb.append(demo.getLastName())
                              .append(",")
                              .append(demo.getFirstName());
                  }
                  else {
                        nameSb.append(String.valueOf(appointment.getName()));
                  }
                  String name = UtilMisc.toUpperLowerCase(nameSb.toString());

                  paramTickler[0]=String.valueOf(demographic_no);
                  paramTickler[1]=MyDateFormat.getSysDate(strDate); //year+"-"+month+"-"+day;//e.g."2001-02-02";
                  tickler_no = "";
                  tickler_note="";

                 if(securityInfoManager.hasPrivilege(loggedInInfo1, "_tickler", "r", demographic_no)) {
	                  for(Tickler t: ticklerManager.search_tickler(loggedInInfo1, demographic_no,MyDateFormat.getSysDate(strDate))) {
	                	  tickler_no = t.getId().toString();
	                      tickler_note = t.getMessage()==null?tickler_note:tickler_note + "\n" + t.getMessage();
	                  }
                 }

                  //alerts and notes
                  DemographicCust dCust = demographicCustDao.find(demographic_no);


                  ver = "";
                  roster = "";
                  Demographic demographic = demographicDao.getDemographicById(demographic_no);
                  if(demographic != null) {

                    ver = demographic.getVer();
                    roster = demographic.getRosterStatus();

                    int intMob = 0;
                    int intDob = 0;

                    mob = String.valueOf(demographic.getMonthOfBirth());
                    if(mob.length()>0 && !mob.equals("null"))
                    	intMob = Integer.parseInt(mob);

                    dob = String.valueOf(demographic.getDateOfBirth());
                    if(dob.length()>0 && !dob.equals("null"))
                    	intDob = Integer.parseInt(dob);


                    demBday = mob + "-" + dob;

                    if (roster == null ) {
                        roster = "";
                    }
                  }
                  study_no = new StringBuffer("");
                  study_link = new StringBuffer("");
		  studyDescription = new StringBuffer("");

		  int numStudy = 0;

		  for(DemographicStudy ds:demographicStudyDao.findByDemographicNo(demographic_no)) {
			  Study study = studyDao.find(ds.getId().getStudyNo());
			  if(study != null && study.getCurrent1() == 1) {
				  numStudy++;
				  if(numStudy == 1) {
					  study_no = new StringBuffer(String.valueOf(study.getId()));
	                          study_link = new StringBuffer(String.valueOf(study.getStudyLink()));
	                          studyDescription = new StringBuffer(String.valueOf(study.getDescription()));
				  } else {
					  study_no = new StringBuffer("0");
		                      study_link = new StringBuffer("formstudy.jsp");
				      studyDescription = new StringBuffer("Form Studies");
				  }
			  }
		  }

                  //String reason = org.apache.commons.lang.StringEscapeUtils.escapeJavaScript(String.valueOf(appointment.getReason()).trim());
                  //String notes = org.apache.commons.lang.StringEscapeUtils.escapeJavaScript(String.valueOf(appointment.getNotes()).trim());
                  String reason = String.valueOf(appointment.getReason()).trim();
                  String notes = String.valueOf(appointment.getNotes()).trim();
                  String status = String.valueOf(appointment.getStatus()).trim();
          	      String sitename = String.valueOf(appointment.getLocation()).trim();
          	      String type = appointment.getType();
          	      String urgency = appointment.getUrgency();
          	      String reasonCodeName = null;
          	      if(appointment.getReasonCode() != null)    {
          	    	LookupListItem lli  = reasonCodesMap.get(appointment.getReasonCode());
          	    	if(lli != null) {
          	    		reasonCodeName = lli.getLabel();
          	    	}
          	      }
				if ( "yes".equalsIgnoreCase(OscarProperties.getInstance().getProperty("SHOW_APPT_TYPE_WITH_REASON")) ) {
					reasonCodeName = ( type + " : " + reasonCodeName );
				}

          	  bFirstTimeRs=true;
          	  ApptStatusData apptStatusData = new ApptStatusData(status);

	 //multi-site. if a site have been selected, only display appointment in that site
	 if (!bMultisites || (selectedSite == null && CurrentSiteMap.get(sitename) != null) || sitename.equals(selectedSite)) {
														//-===========================================================
														// Create appointment
														//-===========================================================
%>
														<td class="appt" bgcolor='<%=apptStatusData.getBgColor()%>' rowspan="<%=iRows%>" <%-- =view==0?(len==lenLimitedL?"nowrap":""):"nowrap"--%> nowrap>
															<%
																if (Appointment.BookingSource.MYOSCAR_SELF_BOOKING == appointment.getBookingSource())
																{
															%>
															<bean:message key="provider.appointmentProviderAdminDay.SelfBookedMarker"/>
															<%
																}
															%>
															<!-- multisites : add colour-coded to the "location" value of that appointment. -->
															<%if (bMultisites) {%>
															<span title="<%= sitename %>" style="background-color:<%=siteBgColor.get(sitename)%>;">&nbsp;</span>|
															<%} %>

															<%
																if (apptStatusData.getNextStatus() != null && !apptStatusData.getNextStatus().equals("")) {
															%>
															<!-- Short letters -->
															<a class="apptStatus" href=#
															   onclick="refreshSameLoc('providercontrol.jsp?appointment_no=<%=appointment.getId()%>&provider_no=<%=curProvider_no[nProvider]%>&status=&statusch=<%=apptStatusData.getNextStatus()%>&year=<%=year%>&month=<%=month%>&day=<%=day%>&view=<%=view==0?"0":("1&curProvider="+request.getParameter("curProvider")+"&curProviderName="+URLEncoder.encode(request.getParameter("curProviderName"),"UTF-8") )%>&displaymode=addstatus&dboperation=updateapptstatus&viewall=<%=request.getParameter("viewall")==null?"0":(request.getParameter("viewall"))%><%=isWeekView(request)?"&viewWeek=1":""%>');" title="<%=apptStatusData.getTitleString(request.getLocale())%> " >
																<%
																	}
																	if (apptStatusData.getNextStatus() != null) {

																		String statusTitle = apptStatusData.getTitleString(request.getLocale());

																		if(OscarProperties.getInstance().getProperty("APPT_SHOW_SHORT_LETTERS", "false") != null
																				&& OscarProperties.getInstance().getProperty("APPT_SHOW_SHORT_LETTERS", "false").equals("true")){
																			String colour = apptStatusData.getShortLetterColour();
																			if(colour == null){
																				colour = "#FFFFFF";
																			}

																%>
																<span
																		class='short_letters'
																		style='color:<%= colour%>;border:0;height:10'>
											[<%=UtilMisc.htmlEscape(apptStatusData.getShortLetters())%>]
									</span>
																<%
																}else{
																%>

																<img src="../images/<%=apptStatusData.getImageName()%>" border="0" height="10" title="<%= statusTitle %>">

																<%
																		}
																	} else {
																		out.print("&nbsp;");
																	}

																%>
															</a>
															<%
																if(urgency != null && urgency.equals("critical")) {
															%>
															<img src="../images/warning-icon.png" border="0" width="14" height="14" title="Critical Appointment"/>
															<% } %>
																<%--|--%>
															<%
																if(demographic_no==0) {
															%>
															<!--  caisi  -->
															<security:oscarSec roleName="<%=roleName$%>" objectName="_tickler" rights="r">
																<% if (tickler_no.compareTo("") != 0) {%>
																	<a href="#" onClick="popupPage(700,1024, '../tickler/ticklerMain.jsp?demoview=0');return false;" title="<bean:message key="provider.appointmentProviderAdminDay.ticklerMsg"/>: <%=UtilMisc.htmlEscape(tickler_note)%>"><font color="red">!</font></a>
																<%} %>
															</security:oscarSec>

															<!--  alerts -->
															<% if(OscarProperties.getInstance().getProperty("displayAlertsOnScheduleScreen", "").equals("true")){ %>
															<% if(dCust != null && dCust.getAlert() != null && !dCust.getAlert().isEmpty()) { %>
															<a href="#" onClick="return false;" title="<%=StringEscapeUtils.escapeHtml(dCust.getAlert())%>">A</a>
															<%} }%>

															<!--  notes -->
															<% if(OscarProperties.getInstance().getProperty("displayNotesOnScheduleScreen", "").equals("true")){ %>
															<% if(dCust != null && dCust.getNotes() != null && !SxmlMisc.getXmlContent(dCust.getNotes(), "<unotes>", "</unotes>").isEmpty()) { %>
															<a href="#" onClick="return false;" title="<%=StringEscapeUtils.escapeHtml(SxmlMisc.getXmlContent(dCust.getNotes(), "<unotes>", "</unotes>"))%>">N</a>
															<%} }%>


															<a href=# onClick ="popupPage(535,860,'../appointment/appointmentcontrol.jsp?appointment_no=<%=appointment.getId()%>&provider_no=<%=curProvider_no[nProvider]%>&year=<%=year%>&month=<%=month%>&day=<%=day%>&start_time=<%=iS+":"+iSm%>&demographic_no=0&displaymode=edit&dboperation=search');return false;" title="<%=iS+":"+(iSm>10?"":"0")+iSm%>-<%=iE+":"+iEm%>
<%=name%>
	<%=type != null ? "type: " + type : "" %>
	reason: <%=reasonCodeName!=null?reasonCodeName:""%> <%if(reason!=null && !reason.isEmpty()){%>- <%=UtilMisc.htmlEscape(reason)%>
<%}%>	<bean:message key="provider.appointmentProviderAdminDay.notes"/>: <%=UtilMisc.htmlEscape(notes)%>" >
																.<%=(view==0&&numAvailProvider!=1)?(name.length()>len?name.substring(0,len).toUpperCase():name.toUpperCase()):name.toUpperCase()%>
																</font></a><!--Inline display of reason -->
															<oscar:oscarPropertiesCheck property="SHOW_APPT_REASON" value="yes" defaultVal="true">
																<span class="reason reason_<%=curProvider_no[nProvider]%> ${ hideReason ? "hideReason" : "" }"><bean:message key="provider.appointmentProviderAdminDay.Reason"/>:<%=UtilMisc.htmlEscape(reason)%></span>
															</oscar:oscarPropertiesCheck></td>
																<%
        			} else {
				%>	<% if (tickler_no.compareTo("") != 0) {%>
														<a href="#" onClick="popupPage(700,1024, '../tickler/ticklerMain.jsp?demoview=<%=demographic_no%>');return false;" title="<bean:message key="provider.appointmentProviderAdminDay.ticklerMsg"/>: <%=UtilMisc.htmlEscape(tickler_note)%>"><font color="red">!</font></a>
																<%} %>

														<!--  alerts -->
																<% if(OscarProperties.getInstance().getProperty("displayAlertsOnScheduleScreen", "").equals("true")) {%>
																<% if(dCust != null && dCust.getAlert() != null && !dCust.getAlert().isEmpty()) { %>
														<a href="#" onClick="return false;" title="<%=StringEscapeUtils.escapeHtml(dCust.getAlert())%>">A</a>
																<%} } %>

														<!--  notes -->
																<% if(OscarProperties.getInstance().getProperty("displayNotesOnScheduleScreen", "").equals("true")) {%>
																<% if(dCust != null && dCust.getNotes() != null && !SxmlMisc.getXmlContent(dCust.getNotes(), "<unotes>", "</unotes>").isEmpty()) { %>
														<a href="#" onClick="return false;" title="<%=StringEscapeUtils.escapeHtml(SxmlMisc.getXmlContent(dCust.getNotes(), "<unotes>", "</unotes>"))%>">N</a>
																<%} }%>

														<!-- doctor code block 1 -->
																<% if(bShowDocLink) { %>
														<!-- security:oscarSec roleName="<%--=roleName$--%>" objectName="_appointment.doctorLink" rights="r" -->
																<% if ("".compareTo(study_no.toString()) != 0) {%>	<a href="#" onClick="popupPage(700,1024, '../form/study/forwardstudyname.jsp?study_link=<%=study_link.toString()%>&demographic_no=<%=demographic_no%>&study_no=<%=study_no%>');return false;" title="<bean:message key="provider.appointmentProviderAdminDay.study"/>: <%=UtilMisc.htmlEscape(studyDescription.toString())%>"><%="<font color='"+studyColor+"'>"+studySymbol+"</font>"%></a><%} %>

																<% if (ver!=null && ver!="" && "##".compareTo(ver.toString()) == 0){%><a href="#" title="<bean:message key="provider.appointmentProviderAdminDay.versionMsg"/> <%=UtilMisc.htmlEscape(ver)%>"> <font color="red">*</font></a><%}%>

																<% if (roster!="" && "FS".equalsIgnoreCase(roster)){%> <a href="#" title="<bean:message key="provider.appointmentProviderAdminDay.rosterMsg"/> <%=UtilMisc.htmlEscape(roster)%>"><font color="red">$</font></a><%}%>

																<% if ("NR".equalsIgnoreCase(roster) || "PL".equalsIgnoreCase(roster)){%> <a href="#" title="<bean:message key="provider.appointmentProviderAdminDay.rosterMsg"/> <%=UtilMisc.htmlEscape(roster)%>"><font color="red">#</font></a><%}%>
														<!-- /security:oscarSec -->
																<% } %>
														<!-- doctor code block 2 -->
																<%

boolean disableStopSigns = PreventionManager.isDisabled();
boolean propertyExists = PreventionManager.isCreated();
if(disableStopSigns!=true){
if( OscarProperties.getInstance().getProperty("SHOW_PREVENTION_STOP_SIGNS","false").equals("true") || propertyExists==true) {

		String warning = prevMgr.getWarnings(loggedInInfo1, String.valueOf(demographic_no));
		warning = PreventionManager.checkNames(warning);

		String htmlWarning = "";

		if( !warning.equals("")) {
			  htmlWarning = "<img src=\"../images/stop_sign.png\" height=\"14\" width=\"14\" title=\"" + warning +"\">&nbsp;";
		}

		out.print(htmlWarning);

}
}

String start_time = "";
if( iS < 10 ) {
	 	start_time = "0";
}
start_time +=  iS + ":";
if( iSm < 10 ) {
	 	start_time += "0";
}

start_time += iSm + ":00";
%>

														<a class="apptLink" href=# onClick ="popupPage(535,860,'../appointment/appointmentcontrol.jsp?appointment_no=<%=appointment.getId()%>&provider_no=<%=curProvider_no[nProvider]%>&year=<%=year%>&month=<%=month%>&day=<%=day%>&start_time=<%=iS+":"+iSm%>&demographic_no=<%=demographic_no%>&displaymode=edit&dboperation=search');return false;"
																<oscar:oscarPropertiesCheck property="SHOW_APPT_REASON_TOOLTIP" value="yes" defaultVal="true">
																	title="<%=name%>
																	type: <%=type != null ? type : "" %>
																	reason: <%=reasonCodeName!=null? reasonCodeName:""%> <%if(reason!=null && !reason.isEmpty()){%>- <%=UtilMisc.htmlEscape(reason)%><%}%>
																	notes: <%=notes%>"
																</oscar:oscarPropertiesCheck> >


															<oscar:oscarPropertiesCheck property="show_hc_eligibility" value="true" defaultVal="false">
																<%=active_medical_coverage?"+&nbsp":""%></oscar:oscarPropertiesCheck>

															<%=(view==0) ? (name.length()>len?name.substring(0,len) : name) :name%></a>

																<% if(len==lenLimitedL || view!=0 || numAvailProvider==1 || oscar.OscarProperties.getInstance().isPropertyActive("APPT_ALWAYS_SHOW_LINKS")) {%>

														<security:oscarSec roleName="<%=roleName$%>" objectName="_eChart" rights="r">
														<oscar:oscarPropertiesCheck property="eform_in_appointment" value="yes">
														&#124;<b><a href="#" onclick="popupPage(500,1024,'../eform/efmformslistadd.jsp?parentAjaxId=eforms&demographic_no=<%=demographic_no%>&appointment=<%=appointment.getId()%>'); return false;"
																	title="eForms">e</a></b>
														</oscar:oscarPropertiesCheck>
														</security:oscarSec>

														<!-- doctor code block 3 -->
																<% if(bShowEncounterLink && !isWeekView(request)) { %>
																<% if (oscar.OscarProperties.getInstance().isPropertyActive("SINGLE_PAGE_CHART")) {

	newUxUrl = "../web/#/record/" + demographic_no + "/";

	if(String.valueOf(demographic_no).equals(record) && !module.equals("summary")){
		newUxUrl =  newUxUrl + module;
		inContextStyle = "style='color: blue;'";
	}else{
		newUxUrl =  newUxUrl + "summary?appointmentNo=" + appointment.getId() + "&encType=face%20to%20face%20encounter%20with%20client";
		inContextStyle = "";
	}
%>
														&#124; <a href="<%=newUxUrl%>" <%=inContextStyle %>><bean:message key="provider.appointmentProviderAdminDay.btnE"/>2</a>
																<%}%>

																<% String  eURL = "../oscarEncounter/IncomingEncounter.do?providerNo="
	+curUser_no+"&appointmentNo="
	+appointment.getId()
	+"&demographicNo="
	+demographic_no
	+"&curProviderNo="
	+curProvider_no[nProvider]
	+"&reason="
	+URLEncoder.encode(reason)
	+"&encType="
	+URLEncoder.encode("face to face encounter with client","UTF-8")
	+"&userName="
	+URLEncoder.encode( userFirstName+" "+userLastName)
	+"&curDate="+curYear+"-"+curMonth+"-"
	+curDay+"&appointmentDate="+year+"-"
	+month+"-"+day+"&startTime="
	+ start_time + "&status="+status
	+ "&apptProvider_no="
	+ curProvider_no[nProvider]
			+ "&providerview="
	+ curProvider_no[nProvider];%>

																<% if (showOldEchartLink) { %>
														&#124; <a href=# class="encounterBtn" onClick="popupWithApptNo(710, 1024,'<%=eURL%>','encounter',<%=appointment.getId()%>);return false;" title="<bean:message key="global.encounter"/>">
														<bean:message key="provider.appointmentProviderAdminDay.btnE"/></a>
																<% }} %>

																<%= (bShortcutIntakeForm) ? "| <a href='#' onClick='popupPage(700, 1024, \"formIntake.jsp?demographic_no="+demographic_no+"\")' title='Intake Form'>In</a>" : "" %>
														<!--  eyeform open link -->
																<% if (oscar.OscarProperties.getInstance().isPropertyActive("new_eyeform_enabled") && !isWeekView(request)) { %>
														&#124; <a href="#" onClick='popupPage(800, 1280, "../eyeform/eyeform.jsp?demographic_no=<%=demographic_no %>&appointment_no=<%=appointment.getId()%>");return false;' title="EyeForm">EF</a>
																<% } %>

														<!-- billing code block -->
																<% if (!isWeekView(request)) { %>
														<security:oscarSec roleName="<%=roleName$%>" objectName="_billing" rights="r">
																<%
	if(status.indexOf('B')==-1)
	{
	%>
														&#124; <a
															href="../billing.do?billRegion=<%=URLEncoder.encode(prov)%>&billForm=<%=URLEncoder.encode(oscarProperties.getProperty("default_view"))%>&hotclick=<%=URLEncoder.encode("")%>&appointment_no=<%=appointment.getId()%>&demographic_name=<%=URLEncoder.encode(name)%>&status=<%=status%>&demographic_no=<%=demographic_no%>&providerview=<%=curProvider_no[nProvider]%>&user_no=<%=curUser_no%>&apptProvider_no=<%=curProvider_no[nProvider]%>&appointment_date=<%=year+"-"+month+"-"+day%>&start_time=<%=start_time%>&bNewForm=1"
															target="_blank"
															title="<bean:message key="global.billingtag"/>"><bean:message key="provider.appointmentProviderAdminDay.btnB"/></a>
																<%
	}
	else
	{
	%>
														&#124; <a href=# onClick='onUnbilled("../billing/CA/<%=prov%>/billingDeleteWithoutNo.jsp?status=<%=status%>&appointment_no=<%=appointment.getId()%>");return false;' title="<bean:message key="global.billingtag"/>">-<bean:message key="provider.appointmentProviderAdminDay.btnB"/></a>
	<%
	}
	%>

														<!--/security:oscarSec-->
														</security:oscarSec>
																<% } %>
														<!-- billing code block -->
														<security:oscarSec roleName="<%=roleName$%>" objectName="_masterLink" rights="r">

														&#124; <a class="masterBtn" href="javascript: function myFunction() {return false; }" onClick="popupWithApptNo(700,1024,'../demographic/demographiccontrol.jsp?demographic_no=<%=demographic_no%>&apptProvider=<%=curProvider_no[nProvider]%>&appointment=<%=appointment.getId()%>&displaymode=edit&dboperation=search_detail','master',<%=appointment.getId()%>)"
																  title="<bean:message key="provider.appointmentProviderAdminDay.msgMasterFile"/>"><bean:message key="provider.appointmentProviderAdminDay.btnM"/></a>

														</security:oscarSec>
																<% if (!isWeekView(request)) { %>

														<!-- doctor code block 4 -->

														<security:oscarSec roleName="<%=roleName$%>" objectName="_appointment.doctorLink" rights="r">
														&#124; <a href=# onClick="popupWithApptNo(700,1027,'../oscarRx/choosePatient.do?providerNo=<%=curUser_no%>&demographicNo=<%=demographic_no%>','rx',<%=appointment.getId()%>)" title="<bean:message key="global.prescriptions"/>"><bean:message key="global.rx"/>
													</a>


														<!-- doctor color -->
														<oscar:oscarPropertiesCheck property="ENABLE_APPT_DOC_COLOR" value="yes">
																<%
                String providerColor = null;
                if(view == 1 && demographicDao != null && userPropertyDao != null) {
                        String providerNo = (demographicDao.getDemographic(String.valueOf(demographic_no))==null?null:demographicDao.getDemographic(String.valueOf(demographic_no)).getProviderNo());
                        UserProperty property = userPropertyDao.getProp(providerNo, UserPropertyDAO.COLOR_PROPERTY);
                        if(property != null) {
                                providerColor = property.getValue();
                        }
                }
        %>
																<%= (providerColor != null ? "<span style=\"background-color:"+providerColor+";width:5px\">&nbsp;</span>" : "") %>
														</oscar:oscarPropertiesCheck>

																<%
	  if("bc".equalsIgnoreCase(prov)){
	  if(patientHasOutstandingPrivateBills(String.valueOf(demographic_no))){
	  %>
														&#124;<b style="color:#FF0000">$</b>
																<%}}%>
														<oscar:oscarPropertiesCheck property="SHOW_APPT_REASON" value="yes" defaultVal="true">
														<span class="reason_<%=curProvider_no[nProvider]%> ${ hideReason ? "hideReason" : "" }">
     			<strong>&#124;<%=reasonCodeName==null?"":"&nbsp;" + reasonCodeName + " -"%><%=reason==null?"":"&nbsp;" + reason%></strong>
     		</span>
														</oscar:oscarPropertiesCheck>

														</security:oscarSec>

														<!-- add one link to caisi Program Management Module -->
																<%

      if(isBirthday(monthDay,demBday)){%>
														&#124; <img src="../images/cake.gif" height="20" alt="Happy Birthday"/>
																<%}%>

																<%String appointment_no=appointment.getId().toString();%>
															<%@include file="appointmentFormsLinks.jspf" %>

														<oscar:oscarPropertiesCheck property="appt_pregnancy" value="true" defaultVal="false">

															<c:set var="demographicNo" value="<%=demographic_no %>" />
														<jsp:include page="appointmentPregnancy.jspf" >
															<jsp:param value="${demographicNo}" name="demographicNo"/>
														</jsp:include>

														</oscar:oscarPropertiesCheck>

																<% }} %>
														</font></td>
																<%
        			}
        		}
        			bFirstFirstR = false;
          	}
            //out.println("<td width='1'>&nbsp;</td></tr>"); give a grid display
            out.println("<td class='noGrid' width='1'></td></tr>"); //no grid display
          }

          // ===============================================================================
		  // End Loop through times for creating each schedule
          // ===============================================================================
				%>

												</table> <!-- end table for each provider schedule display -->
												<!-- caisi infirmary view extension add fffffffffff-->
											</logic:notEqual>
											<!-- caisi infirmary view extension add end fffffffffffffff-->

										</td></tr>
										<tr><td class="infirmaryView" ALIGN="center" BGCOLOR="<%=bColor?"#bfefff":"silver"%>">
											<!-- caisi infirmary view extension modify fffffffffffffffffff-->
											<logic:notEqual name="infirmaryView_isOscar" value="false">

												<% if (isWeekView(request)) { %>
												<b><a href="providercontrol.jsp?year=<%=year%>&month=<%=month%>&day=<%=day%>&view=0&displaymode=day&dboperation=searchappointmentday"><%=formatDate%></a></b>
												<% } else { %>
												<b><input type='button' value="<bean:message key="provider.appointmentProviderAdminDay.weekLetter"/>" name='weekview' onClick="goWeekView('<%=curProvider_no[nProvider]%>');" title="<bean:message key="provider.appointmentProviderAdminDay.weekView"/>" style="color:black" class="noprint"></b>
												<input type='button' value="<bean:message key="provider.appointmentProviderAdminDay.searchLetter"/>" name='searchview' onClick="goSearchView('<%=curProvider_no[nProvider]%>');" title="<bean:message key="provider.appointmentProviderAdminDay.searchView"/>" style="color:black" class="noprint">
												<b><input type='radio' name='flipview' class="noprint" onClick="goFilpView('<%=curProvider_no[nProvider]%>')" title="Flip view"  >
													<a href=# onClick="goZoomView('<%=curProvider_no[nProvider]%>','<%=StringEscapeUtils.escapeJavaScript(curProviderName[nProvider])%>')" onDblClick="goFilpView('<%=curProvider_no[nProvider]%>')" title="<bean:message key="provider.appointmentProviderAdminDay.zoomView"/>" >
														<!--a href="providercontrol.jsp?year=<%=strYear%>&month=<%=strMonth%>&day=<%=strDay%>&view=1&curProvider=<%=curProvider_no[nProvider]%>&curProviderName=<%=curProviderName[nProvider]%>&displaymode=day&dboperation=searchappointmentday" title="<bean:message key="provider.appointmentProviderAdminDay.zoomView"/>"-->
														<%=curProviderName[nProvider]%></a></b>
												<% } %>

												<% if(!userAvail) { %>
												[<bean:message key="provider.appointmentProviderAdminDay.msgNotOnSched"/>]
												<% } %>
											</logic:notEqual>
											<logic:equal name="infirmaryView_isOscar" value="false">
												<%String prID="1"; %>
												<logic:present name="infirmaryView_programId">
													<%prID=(String)session.getAttribute(SessionConstants.CURRENT_PROGRAM_ID); %>
												</logic:present>
												<logic:iterate id="pb" name="infirmaryView_programBeans" type="org.apache.struts.util.LabelValueBean">
													<%if (pb.getValue().equals(prID)) {%>
													<b><%=pb.getLabel()%></label></b>
													<%} %>
												</logic:iterate>
											</logic:equal>
											<!-- caisi infirmary view extension modify end ffffffffffffffffff-->
										</td></tr>

									</table><!-- end table for each provider name -->

								</td>
								<%
									} //end of display team a, etc.

								%>


							</tr>
						</table>        <!-- end table for the whole schedule row display -->




					</td>
					</tr>

					<tr><td colspan="3">
						<table BORDER="0" CELLPADDING="0" CELLSPACING="0" WIDTH="100%" class="noprint">
							<tr>
								<td BGCOLOR="ivory" width="60%">
									<a href="providercontrol.jsp?year=<%=year%>&month=<%=month%>&day=<%=isWeekView(request) ? (day - 7) : (day - 1)%>&view=<%=view == 0 ? "0" : ("1&curProvider=" + request.getParameter("curProvider") + "&curProviderName=" + URLEncoder.encode(request.getParameter("curProviderName"),"UTF-8"))%>&displaymode=day&dboperation=searchappointmentday<%=isWeekView(request) ? "&provider_no=" + provNum : ""%>">
										&nbsp;&nbsp;<img src="../images/previous.gif" WIDTH="10" HEIGHT="9" BORDER="0" ALT="<bean:message key="provider.appointmentProviderAdminDay.viewPrevDay"/>" vspace="2"></a>
									<b><span class="dateAppointment"><% if (isWeekView(request)) {%><bean:message key="provider.appointmentProviderAdminDay.week"/> <%=week%><% } else {%><%=formatDate%><% }%></span></b>
									<a href="providercontrol.jsp?year=<%=year%>&month=<%=month%>&day=<%=isWeekView(request) ? (day + 7) : (day + 1)%>&view=<%=view == 0 ? "0" : ("1&curProvider=" + request.getParameter("curProvider") + "&curProviderName=" + URLEncoder.encode(request.getParameter("curProviderName"),"UTF-8"))%>&displaymode=day&dboperation=searchappointmentday<%=isWeekView(request) ? "&provider_no=" + provNum : ""%>">
										<img src="../images/next.gif" WIDTH="10" HEIGHT="9" BORDER="0" ALT="<bean:message key="provider.appointmentProviderAdminDay.viewNextDay"/>" vspace="2">&nbsp;&nbsp;</a>
									<a id="calendarLinkBottom" href=# onClick ="popupPage(425,430,'../share/CalendarPopup.jsp?urlfrom=../provider/providercontrol.jsp&year=<%=strYear%>&month=<%=strMonth%>&param=<%=URLEncoder.encode("&view=0&displaymode=day&dboperation=searchappointmentday", "UTF-8")%><%=isWeekView(request) ? URLEncoder.encode("&provider_no=" + provNum, "UTF-8") : ""%>')"><bean:message key="global.calendar"/></a></td>
								<td ALIGN="RIGHT" BGCOLOR="Ivory">
									| <a href="../logout.jsp"><bean:message key="global.btnLogout"/> &nbsp;</a>
								</td>
							</tr>
						</table>
					</td></tr>

				</table>
		</td></tr>
</table>




</body>
</html>
