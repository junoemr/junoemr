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

<%@ page import="java.io.UnsupportedEncodingException" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.LocalTime" %>
<%@ page import="java.time.Duration" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="java.util.SortedMap" %>

<%@ page import="oscar.OscarProperties" %>
<%@ page import="oscar.util.UtilDateUtilities" %>
<%@ page import="oscar.util.ConversionUtils" %>
<%@ page import="org.oscarehr.appointment.model.AppointmentStatusList" %>
<%@ page import="org.oscarehr.common.dao.MyGroupDao" %>
<%@ page import="org.oscarehr.common.dao.MyGroupAccessRestrictionDao" %>
<%@ page import="org.oscarehr.common.dao.ProviderSiteDao" %>
<%@ page import="org.oscarehr.common.dao.SiteDao" %>
<%@ page import="org.oscarehr.common.dao.UserPropertyDAO" %>
<%@ page import="org.oscarehr.common.model.LookupList" %>
<%@ page import="org.oscarehr.common.model.LookupListItem" %>
<%@ page import="org.oscarehr.common.model.MyGroup" %>
<%@ page import="org.oscarehr.common.model.MyGroupAccessRestriction" %>
<%@ page import="org.oscarehr.common.model.Provider" %>
<%@ page import="org.oscarehr.common.model.ProviderPreference"%>
<%@ page import="org.oscarehr.common.model.Site" %>
<%@ page import="org.oscarehr.common.model.UserProperty" %>
<%@ page import="org.oscarehr.managers.AppointmentManager" %>
<%@ page import="org.oscarehr.managers.SecurityInfoManager" %>
<%@ page import="org.oscarehr.managers.LookupListManager" %>
<%@ page import="org.oscarehr.PMmodule.dao.ProviderDao" %>
<%@ page import="org.oscarehr.provider.model.ProviderPreventionManager" %>
<%@ page import="org.oscarehr.schedule.dao.ScheduleDateDao" %>
<%@ page import="org.oscarehr.schedule.dto.AppointmentDetails" %>
<%@ page import="org.oscarehr.schedule.dto.ResourceSchedule" %>
<%@ page import="org.oscarehr.schedule.dto.ScheduleSlot" %>
<%@ page import="org.oscarehr.schedule.dto.UserDateSchedule" %>
<%@ page import="org.oscarehr.schedule.model.ScheduleDate" %>
<%@ page import="org.oscarehr.util.LoggedInInfo" %>
<%@ page import="org.oscarehr.util.MiscUtils" %>
<%@ page import="org.oscarehr.util.SessionConstants" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.web.admin.ProviderPreferencesUIBean" %>
<%@ page import="org.springframework.transaction.support.DefaultTransactionDefinition" %>
<%@ page import="org.springframework.transaction.TransactionDefinition" %>

<jsp:useBean id="providerBean" class="java.util.Properties" scope="session" />
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
private HashMap<String,String> siteBgColor = new HashMap<String,String>();

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

private String getScheduleUrl(boolean isWeekView, LocalDate date, int view, String currentProvider,
		String currentProviderName, String providerNo, String viewAll) throws UnsupportedEncodingException
{
	String viewString = "0";
	if(view != 0)
	{
		viewString = "1&curProvider=" + currentProvider + "&curProviderName="+URLEncoder.encode(currentProviderName,"UTF-8");
	}

	String providerString = "";
	if(isWeekView)
	{
		providerString = "&provider_no=" + providerNo;
	}

	return "providercontrol.jsp?" +
			"year=" + date.getYear() +
			"&month=" + date.getMonthValue() +
			"&day=" + date.getDayOfMonth() +
			"&view=" + viewString +
			"&displaymode=day" +
			"&dboperation=searchappointmentday" +
			providerString +
			"&viewall=" + viewAll;
}


%>
<%

	// Additional things required for schedule
	String roleName$ = session.getAttribute("userrole") + "," + session.getAttribute("user");

	MyGroupDao myGroupDao = SpringUtils.getBean(MyGroupDao.class);
	MyGroupAccessRestrictionDao myGroupAccessRestrictionDao = SpringUtils.getBean(MyGroupAccessRestrictionDao.class);
	ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
	ProviderSiteDao providerSiteDao = SpringUtils.getBean(ProviderSiteDao.class);
	ScheduleDateDao scheduleDateDao = SpringUtils.getBean(ScheduleDateDao.class);
	SiteDao siteDao = SpringUtils.getBean(SiteDao.class);
	UserPropertyDAO userPropertyDao = SpringUtils.getBean(UserPropertyDAO.class);

	// XXX: do we need these?
	List<Site> curUserSites = new ArrayList<Site>();
	List<String> siteProviderNos = new ArrayList<String>();
	List<String> siteGroups = new ArrayList<String>();
	HashMap<String,String> currentSiteMap = new HashMap<String,String>();
	boolean isSiteAccessPrivacy = false;
	boolean isTeamAccessPrivacy = false;

	String selectedSite = null;

	// XXX: convert these to java queries
	%>
	<security:oscarSec objectName="_site_access_privacy" roleName="<%=roleName$%>" rights="r" reverse="false">
	<%
		isSiteAccessPrivacy=true;
	%>
	</security:oscarSec>

	<security:oscarSec objectName="_team_access_privacy" roleName="<%=roleName$%>" rights="r" reverse="false">
	<%
		isTeamAccessPrivacy=true;
	%>
	</security:oscarSec>
	<%

	if (bMultisites)
	{
		List<Site> sites = siteDao.getAllActiveSites();
		selectedSite = (String) session.getAttribute("site_selected");

		if (selectedSite != null)
		{
			//get site provider list
			siteProviderNos = siteDao.getProviderNoBySiteLocation(selectedSite);
			siteGroups = siteDao.getGroupBySiteLocation(selectedSite);
		}

		if (isSiteAccessPrivacy || isTeamAccessPrivacy)
		{
			String siteManagerProviderNo = (String) session.getAttribute("user");
			curUserSites = siteDao.getActiveSitesByProviderNo(siteManagerProviderNo);
			if (selectedSite==null)
			{
				siteProviderNos = siteDao.getProviderNoBySiteManagerProviderNo(siteManagerProviderNo);
				siteGroups = siteDao.getGroupBySiteManagerProviderNo(siteManagerProviderNo);
			}
		}
		else
		{
			curUserSites = sites;
		}

		for (Site s : curUserSites)
		{
			currentSiteMap.put(s.getName(),"Y");
		}

		//get all sites bgColors
		for (Site st : sites)
		{
			siteBgColor.put(st.getName(),st.getBgColor());
		}
	}


	int MINUTES_IN_DAY = (24 * 60);
	org.oscarehr.schedule.service.Schedule scheduleService = SpringUtils.getBean(org.oscarehr.schedule.service.Schedule.class);

	DefaultTransactionDefinition def = new DefaultTransactionDefinition();

	// explicitly setting the transaction name is something that can only be done programmatically
	def.setName("SomeTxName");
	def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);


	MenuBar menuBarController = new MenuBar(request, session);
	pageContext.setAttribute("menuBarController", menuBarController);

	// Required for menu bar
	LoggedInInfo loggedInInfo1=LoggedInInfo.getLoggedInInfoFromSession(request);


	OscarProperties oscarProperties = OscarProperties.getInstance();

	String resourceBaseUrl =  oscarProperties.getProperty("resource_base_url");

	GregorianCalendar cal = new GregorianCalendar();
	int curYear = cal.get(Calendar.YEAR);
	int curMonth = (cal.get(Calendar.MONTH)+1);
	int curDay = cal.get(Calendar.DAY_OF_MONTH);

	int year = Integer.parseInt(request.getParameter("year"));
	int month = Integer.parseInt(request.getParameter("month"));
	int day = Integer.parseInt(request.getParameter("day"));

	String curUser_no = (String) session.getAttribute("user");
	String userFirstName = (String) session.getAttribute("userfirstname");
	String userLastName = (String) session.getAttribute("userlastname");
	String prov = oscarProperties.getBillingTypeUpperCase();


	int view = request.getParameter("view")!=null ? Integer.parseInt(request.getParameter("view")) : 0; //0-multiple views, 1-single view
	String provNum = request.getParameter("provider_no");

	SimpleDateFormat inform = new SimpleDateFormat ("yyyy-MM-dd", request.getLocale());

	LocalDate selectedDate = LocalDate.of(year, month, day);
	String strDate = selectedDate.toString();

	String formatDate;
	try
	{
		java.util.ResourceBundle prop = ResourceBundle.getBundle("oscarResources", request.getLocale());
		formatDate = UtilDateUtilities.DateToString(inform.parse(strDate), prop.getString("date.EEEyyyyMMdd"),request.getLocale());
	}
	catch (Exception e)
	{
		MiscUtils.getLogger().error("Error", e);
		formatDate = UtilDateUtilities.DateToString(inform.parse(strDate), "EEE, yyyy-MM-dd");
	}

	int week = cal.get(Calendar.WEEK_OF_YEAR);

	String strYear = "" + year;
	String strMonth = month > 9 ? (""+month) : ("0"+month);
	String strDay = day > 9 ? (""+day) : ("0"+day);

	String viewall = request.getParameter("viewall");
	if( viewall == null )
	{
		viewall = "0";
	}

	int numProvider=0, numAvailProvider=0;
	String [] curProvider_no;
	String [] curProviderName;

	//initial provider bean for all the application
	if(providerBean.isEmpty())
	{
		for(Provider p : providerDao.getActiveProviders())
		{
			providerBean.setProperty(p.getProviderNo(),p.getFormattedName());
		}
	}

	ProviderPreference providerPreference2=(ProviderPreference)session.getAttribute(SessionConstants.LOGGED_IN_PROVIDER_PREFERENCE);
	String mygroupno = providerPreference2.getMyGroupNo();

	int startHour = providerPreference2.getStartHour();
	int endHour = providerPreference2.getEndHour();
	int everyMin = providerPreference2.getEveryMin();

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

	//ResourceSchedule resourceScheduleDTO = null;

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
					if(numProvider >= 5) {lenLimitedL = 2; lenLimitedS = 3; len = 2; }
				} else {
					if(numAvailProvider >= 5) {lenLimitedL = 2; lenLimitedS = 3; len = 2; }
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
		}
		else
		{
			//single view
			numProvider=1;
			curProvider_no = new String [numProvider];
			curProviderName = new String [numProvider];
			curProvider_no[0]=request.getParameter("curProvider");
			curProviderName[0]=request.getParameter("curProviderName");
		}
	}

	String newticklerwarningwindow=null;
	String default_pmm=null;


	// Schedule display variables

	SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

	LookupListManager lookupListManager = SpringUtils.getBean(LookupListManager.class);
	LookupList reasonCodes = lookupListManager.findLookupListByName(loggedInInfo1, "reasonCode");
	ProviderPreventionManager prevMgr = (ProviderPreventionManager)SpringUtils.getBean("preventionMgr");
	UserPropertyDAO propDao =(UserPropertyDAO)SpringUtils.getBean("UserPropertyDAO");

	Map<Integer,LookupListItem> reasonCodesMap = new  HashMap<Integer,LookupListItem>();
	for(LookupListItem lli:reasonCodes.getItems()) {
		reasonCodesMap.put(lli.getId(),lli);
	}

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

	boolean showOldEchartLink = true;
	UserProperty oldEchartLink = propDao.getProp(curUser_no, UserProperty.HIDE_OLD_ECHART_LINK_IN_APPT);
	if (oldEchartLink!=null && "Y".equals(oldEchartLink.getValue())) showOldEchartLink = false;

	String caisiBillingPreferenceNotDelete = null;

	String defaultServiceType = (String) session.getAttribute("default_servicetype");
	ProviderPreference providerPreference = ProviderPreferencesUIBean.getProviderPreference(loggedInInfo1.getLoggedInProviderNo());
	if( defaultServiceType == null && providerPreference!=null) {
		defaultServiceType = providerPreference.getDefaultServiceType();
	}

	if( defaultServiceType == null ) {
		defaultServiceType = "";
	}

	boolean prescriptionQrCodes = providerPreference2.isPrintQrCodeOnPrescriptions();

	boolean erx_enable = providerPreference2.isERxEnabled();
	boolean erx_training_mode = providerPreference2.isERxTrainingMode();

	Collection<Integer> eforms = providerPreference2.getAppointmentScreenEForms();
	StringBuilder eformIds = new StringBuilder();
	for( Integer eform : eforms ) {
		eformIds = eformIds.append("&eformId=" + eform);
	}

	Collection<String> forms = providerPreference2.getAppointmentScreenForms();
	StringBuilder ectFormNames = new StringBuilder();
	for( String formName : forms ) {
		ectFormNames = ectFormNames.append("&encounterFormName=" + formName);
	}

%>
<html>
<head>
	<script>

		// copied from Google Analytics Snippet, adapted for Prometheus Aggregator
		(function(i,s,o,g,r,a,m){i['PrometheusAggregatorObjectName']=r;i[r]=i[r]||function(){
			(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
			m=s.getElementsByTagName(o)[0];a.async=1;a.src=(
			"<%=request.getContextPath()%>/js/userMetrics.js");m.parentNode.insertBefore(a,m);
			i[r].aggregatorServerRoot = g;
		})(window,document,'script',
			"<%=request.getContextPath()%>/ws/rs/user_metrics",'prometheusAggregator');

		prometheusAggregator('increment', 'app_load_succeeded', { app: 'student'}, 1);

	</script>

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


	<script type="text/javascript">

		function changeGroup(s) {
			var newGroupNo = s.options[s.selectedIndex].value;
			if(newGroupNo.indexOf("_grp_") != -1) {
				newGroupNo = s.options[s.selectedIndex].value.substring(5);
			}else{
				newGroupNo = s.options[s.selectedIndex].value;
			}
			<%if (org.oscarehr.common.IsPropertiesOn.isCaisiEnable() && org.oscarehr.common.IsPropertiesOn.isTicklerPlusEnable()){%>
			//Disable schedule view associated with the program
			//Made the default program id "0";
			//var programId = document.getElementById("bedprogram_no").value;
			var programId = 0;
			var programId_forCME = document.getElementById("bedprogram_no").value;

			popupPage(10,10, "providercontrol.jsp?provider_no=<%=curUser_no%>&start_hour=<%=startHour%>&end_hour=<%=endHour%>&every_min=<%=everyMin%>&caisiBillingPreferenceNotDelete=<%=caisiBillingPreferenceNotDelete%>&new_tickler_warning_window=<%=newticklerwarningwindow%>&default_pmm=<%=default_pmm%>&color_template=deepblue&dboperation=updatepreference&displaymode=updatepreference&default_servicetype=<%=defaultServiceType%>&prescriptionQrCodes=<%=prescriptionQrCodes%>&erx_enable=<%=erx_enable%>&erx_training_mode=<%=erx_training_mode%>&mygroup_no="+newGroupNo+"&programId_oscarView="+programId+"&case_program_id="+programId_forCME + "<%=eformIds.toString()%><%=ectFormNames.toString()%>");
			<%}else {%>
			var programId=0;
			popupPage(10,10, "providercontrol.jsp?provider_no=<%=curUser_no%>&start_hour=<%=startHour%>&end_hour=<%=endHour%>&every_min=<%=everyMin%>&color_template=deepblue&dboperation=updatepreference&displaymode=updatepreference&default_servicetype=<%=defaultServiceType%>&prescriptionQrCodes=<%=prescriptionQrCodes%>&erx_enable=<%=erx_enable%>&erx_training_mode=<%=erx_training_mode%>&mygroup_no="+newGroupNo+"&programId_oscarView="+programId + "<%=eformIds.toString()%><%=ectFormNames.toString()%>");
			<%}%>
		}

		function ts1(s) {
			popupPage(360,780,('../appointment/addappointment.jsp?'+s));
		}
		function tsr(s) {
			popupPage(360,780,('../appointment/appointmentcontrol.jsp?displaymode=edit&dboperation=search&'+s));
		}
		function goFilpView(s) {
			self.location.href = "../schedule/scheduleflipview.jsp?originalpage=../provider/providercontrol.jsp&startDate=<%=year+"-"+month+"-"+day%>" + "&provider_no="+s ;
		}
		function goWeekView(s) {
			self.location.href = "providercontrol.jsp?year=<%=year%>&month=<%=month%>&day=<%=day%>&view=0&displaymode=day&dboperation=searchappointmentday&viewall=1&provider_no="+s;
		}
		function goZoomView(s, n) {
			self.location.href = "providercontrol.jsp?year=<%=strYear%>&month=<%=strMonth%>&day=<%=strDay%>&view=1&curProvider="+s+"&curProviderName="+encodeURIComponent(n)+"&displaymode=day&dboperation=searchappointmentday" ;
		}
		function findProvider(p,m,d) {
			popupPage(300,400, "receptionistfindprovider.jsp?pyear=" +p+ "&pmonth=" +m+ "&pday=" +d+ "&providername="+ document.findprovider.providername.value );
		}
		function goSearchView(s) {
			popupPage(600,650,"../appointment/appointmentsearch.jsp?provider_no="+s);
		}

		function review(key) {
			if(self.location.href.lastIndexOf("?") > 0) {
				if(self.location.href.lastIndexOf("&viewall=") > 0 ) a = self.location.href.substring(0,self.location.href.lastIndexOf("&viewall="));
				else a = self.location.href;
			} else {
				a="providercontrol.jsp?year="+document.jumptodate.year.value+"&month="+document.jumptodate.month.value+"&day="+document.jumptodate.day.value+"&view=0&displaymode=day&dboperation=searchappointmentday&site=" + "<%=(selectedSite==null? "none" : selectedSite)%>";
			}
			self.location.href = a + "&viewall="+key ;
		}


	</script>

	<style type="text/css">

		<% // NavBar always visible when scrolling down page
		if (oscar.OscarProperties.getInstance().isPropertyActive("navigation_always_on_top")) { %>

		#firstTable{
			position: fixed;
			background-color: #EEEEFF;
		}
		#appointmentTable {
			padding-top:17px;
		}
		<% } %>
	</style>


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


	LocalDate previousDate;
	LocalDate nextDate;

	if(isWeekView(request))
	{
		previousDate = selectedDate.minusDays(7);
		nextDate = selectedDate.plusDays(7);
	}
	else
	{
		previousDate = selectedDate.minusDays(1);
		nextDate = selectedDate.plusDays(1);
	}

	String previousScheduleUrl = getScheduleUrl(
			isWeekView(request),
			previousDate,
			view,
			request.getParameter("curProvider"),
			request.getParameter("curProviderName"),
			provNum,
			viewall
	);

	String nextScheduleUrl = getScheduleUrl(
			isWeekView(request),
			nextDate,
			view,
			request.getParameter("curProvider"),
			request.getParameter("curProviderName"),
			provNum,
			viewall
	);

%>

<div id="system_message"></div>
<div id="facility_message"></div>


<table id="appointmentTable" BORDER="0" CELLPADDING="1" CELLSPACING="0" WIDTH="100%" BGCOLOR="#C0C0C0">

	<!-- START IVORY BAR -->
	<tr id="ivoryBar">
		<td id="dateAndCalendar" BGCOLOR="ivory" width="33%">
			<a class="redArrow" href="<%= previousScheduleUrl %>">
				&nbsp;&nbsp;<img src="../images/previous.gif" WIDTH="10" HEIGHT="9" BORDER="0" class="noprint" ALT="<bean:message key="provider.appointmentProviderAdminDay.viewPrevDay"/>" vspace="2"></a>
			<b><span class="dateAppointment">
				<%
				if (isWeekView(request))
				{
					%><bean:message key="provider.appointmentProviderAdminDay.week"/> <%=week%><%
				}
				else
				{
					%><%=formatDate%><%
				}
				%>
			</span></b>
			<a class="redArrow" href="<%= nextScheduleUrl %>">
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
				<%
					if (isWeekView(request))
					{
				%>
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
					}
					else
					{
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

	<%

		boolean showApptCountForProvider = OscarProperties.getInstance().isPropertyActive("schedule.show_appointment_count");
	%>


	<!-- START Schedule page -->
	<tr>
		<td colspan="3">
			<table border="0" cellpadding="0" bgcolor="#486ebd" cellspacing="0" width="100%">
				<tr>
					<%

						boolean headerColor = true;
						boolean showDocumentLink = securityInfoManager.hasPrivilege(loggedInInfo1, "_tickler", "r", null);
						boolean showEncounterLink = securityInfoManager.hasPrivilege(loggedInInfo1, "_eChart", "r", null);
						boolean showTicklers = securityInfoManager.hasPrivilege(loggedInInfo1, "_tickler", "r", null);
						boolean showDoctorLink = securityInfoManager.hasPrivilege(loggedInInfo1, "_appointment.doctorLink", "r", null);
						boolean showMasterLink = securityInfoManager.hasPrivilege(loggedInInfo1, "_masterLink", "r", null);
						boolean showBilling = securityInfoManager.hasPrivilege(loggedInInfo1, "_billing", "r", null);
						boolean showEChart = securityInfoManager.hasPrivilege(loggedInInfo1, "_eChart", "r", null);

						// Show the prevention stop signs in appointments
						boolean enablePreventionAppointmentWarnings = (
							!ProviderPreventionManager.isDisabled() && (
								ProviderPreventionManager.isCreated() ||
								OscarProperties.getInstance().isPropertyActive("SHOW_PREVENTION_STOP_SIGNS")
							)
						);

						ResourceSchedule resourceScheduleDTO;




						// This section decides which schedules to show on the page.  Here is the
						// logic behind what gets shown for a provider/date combination (it applies
						// to single providers, the .default provider and to providers in a selected
						// group):

						// - If the viewall parameter is set, show the schedule for all providers.
						// - Otherwise, use these rules:
						//   - Show a regular schedule for anyone with a scheduledate entry
						//   - Show a greyed out schedule for the current user if there is no
						//     scheduledate entry for that provider.
						//   - Don't show a schedule for anyone else

						boolean viewAllBoolean = ("1".equals(viewall));

						if(isWeekView(request))
						{
							resourceScheduleDTO = scheduleService
								.getWeekScheduleByProvider(provNum, selectedDate, selectedSite);
						}
						else if(mygroupno != null && providerBean.get(mygroupno) != null)
						{
							// Get schedules (mygroupno is provider number)
							String actuallyProviderNo = mygroupno;

							boolean showForSure =
								(viewAllBoolean || actuallyProviderNo.equals(curUser_no));

							resourceScheduleDTO = scheduleService
								.getResourceScheduleByProvider(mygroupno, selectedDate,
									selectedSite, showForSure);
						}
						else if(".default".equals(mygroupno))
						{
							// Always show the schedule because it will
							resourceScheduleDTO = scheduleService
								.getResourceScheduleByProvider(curUser_no, selectedDate,
									selectedSite, true);
						}
						else
						{
							resourceScheduleDTO = scheduleService
								.getResourceScheduleByGroup(mygroupno, selectedDate, selectedSite,
									viewAllBoolean, Integer.parseInt(curUser_no));
						}

						AppointmentManager appointmentManager = SpringUtils.getBean(AppointmentManager.class);
						AppointmentStatusList appointmentStatusList =
								AppointmentStatusList.factory(appointmentManager);

						List<UserDateSchedule> schedules = resourceScheduleDTO.getSchedules();

						for(UserDateSchedule schedule: schedules)
						{
							Integer providerNo = schedule.getProviderNo();

							headerColor = !headerColor;

							boolean notOnSchedule = false;
							if(!viewall.equals("1") && providerNo == Integer.parseInt(curUser_no) && !schedule.hasSchedule())
							{
								notOnSchedule = true;
							}


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
								if (isWeekView(request))
								{
									String dayUrl = "providercontrol.jsp" +
											"?year=" + schedule.getScheduleDate().getYear() +
											"&month=" + schedule.getScheduleDate().getMonthValue() +
											"&day=" + schedule.getScheduleDate().getDayOfMonth() +
											"&view=0" +
											"&displaymode=day" +
											"&dboperation=searchappointmentday";
									%>
									<b><a href="<%= dayUrl %>">
										<%=schedule.getScheduleDate().format(DateTimeFormatter.ISO_LOCAL_DATE)%>
									</a></b>
									<%
								}
								else
								{
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
											</oscar:oscarPropertiesCheck>
										</b>
									<%
								}
								%>

								<%
          						if (notOnSchedule) {
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
								<table id="providerSchedule" border="0" cellpadding="0" bgcolor="<%=notOnSchedule?"silver":"#486ebd"%>" cellspacing="0" width="100%">
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
									String fontColor = "white";
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
											bgColorString = "bgcolor=\"" + slot.getColor() + "\"";
										}

										if(slot.getColor() != null && !slot.getColor().equals(bgcolordef))
										{
											fontColor = "black";
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
												<%= bgColorString %>
												title='<%= descriptionString %>'
										>

											<font color='<%= fontColor %>'><%= codeString %></font>
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

												// Preventions are very complicated.  To keep from running a query per appointment it
												// would require a complicated query.
												// XXX: if fixing, probably run one query for all appointments and create one prevention
												//      object per demographic.
												String preventionWarnings = "";
												if(enablePreventionAppointmentWarnings)
												{
													preventionWarnings = prevMgr.getWarnings(loggedInInfo1, appointment.getDemographicNo().toString());
													preventionWarnings = ProviderPreventionManager.checkNames(preventionWarnings);
												}

												String record = "";
												String module = "";

												if(request.getParameter("record") != null)
												{
													record = request.getParameter("record");
												}

												if(request.getParameter("module") != null)
												{
													module = request.getParameter("module");
												}

												// Load appointmentInfo bean for this appointment
												appointmentInfo.init(
													appointment,
													bMultisites,
													siteBgColor,
													appointmentStatusList,
													request.getLocale(),
													isWeekView(request),
													view,
													numAvailProvider,
													len,
													lenLimitedL,
													request.getParameter("curProvider"),
													request.getParameter("curProviderName"),
													request.getParameter("viewall"),
													providerNo,
													reasonCodesMap,
													showDocumentLink,
													showEncounterLink,
													showOldEchartLink,
													enablePreventionAppointmentWarnings,
													preventionWarnings,
													record,
													module,
													userFirstName,
													userLastName,
													curUser_no,
													showTicklers,
													showDoctorLink,
													showMasterLink,
													showBilling,
													showEChart
												);

												// These are for the appointmentFormLinks.jspf
												String demographic_no = appointment.getDemographicNo().toString();
												String appointment_no = appointment.getAppointmentNo().toString();

											%>

											<td class="appt" bgcolor='<%= appointment.getColor() %>' rowspan="<%= appointmentRowSpan %>" nowrap>

												<!-- Self booking notice -->
												<c:if test="${appointmentInfo.selfBooked}">
													<bean:message key="provider.appointmentProviderAdminDay.SelfBookedMarker"/>
												</c:if>


												<!-- multisites : add colour-coded to the "location" value of that appointment. -->
												<c:if test="${appointmentInfo.multisitesEnabled}">
													<span title="${appointmentInfo.siteName}" style="background-color:${appointmentInfo.siteColour};">&nbsp;</span>|
												</c:if>


												<!-- Short letters -->
												<a
													class="apptStatus"
													href=#
													onclick="refreshSameLoc('${appointmentInfo.refreshURL}');"
													title="${appointmentInfo.appointmentTitle} " >

													<c:choose>
														<c:when test="${appointmentInfo.showShortLetters}">
															<!-- Appointment status image -->
															<span
																class='short_letters'
																style='color:${appointmentInfo.colour};border:0;height:10'>
																[${appointmentInfo.shortLetters}]
															</span>
														</c:when>
														<c:otherwise>
															<img src="../images/${appointmentInfo.iconImage}" border="0" height="10" title="${appointmentInfo.statusTitle}" />
														</c:otherwise>
													</c:choose>

												</a>


												<c:if test="${appointmentInfo.criticalUrgency}">
													<img src="../images/warning-icon.png" border="0" width="14" height="14" title="Critical Appointment"/>
												</c:if>

												<%--|--%>
												<c:choose>
													<c:when test="${appointmentInfo.emptyDemographic}">

														<!--  caisi  -->
														<c:if test="${appointmentInfo.showTickler}">
															<a href="#" onClick="popupPage(700,1024, '../tickler/ticklerMain.jsp?demoview=0');return false;" title="<bean:message key="provider.appointmentProviderAdminDay.ticklerMsg"/>: ${appointmentInfo.ticklerNote}"><font color="red">!</font></a>
														</c:if>

														<!--  alerts -->
														<c:if test="${appointmentInfo.displayAlerts}">
															<a href="#" onClick="return false;" title="${appointmentInfo.alert}">A</a>
														</c:if>

														<!--  notes -->
														<c:if test="${appointmentInfo.displayNotes}">
															<a href="#" onClick="return false;" title="${appointmentInfo.notes}">N</a>
														</c:if>


														<a href=# onClick ="popupPage(535,860,'${appointmentInfo.appointmentURL}');return false;" title="${appointmentInfo.appointmentLinkTitle}" >
															.${appointmentInfo.truncatedUpperName}
														</a><!--Inline display of reason -->
														<oscar:oscarPropertiesCheck property="SHOW_APPT_REASON" value="yes" defaultVal="true">
															<span class="${appointmentInfo.reasonToggleableClass} reason reason_${appointmentInfo.providerNo} ${appointmentInfo.hideReasonClass}">
																<bean:message key="provider.appointmentProviderAdminDay.Reason"/>:${appointmentInfo.reason}
															</span>
														</oscar:oscarPropertiesCheck></td>

													</c:when>
													<c:otherwise>

														<c:if test="${appointmentInfo.showTickler}">
															<a href="#" onClick="popupPage(700,1024, '../tickler/ticklerMain.jsp?demoview=${appointmentInfo.demographicNo}');return false;" title="<bean:message key="provider.appointmentProviderAdminDay.ticklerMsg"/>: ${appointmentInfo.ticklerNote}"><font color="red">!</font></a>
														</c:if>

														<!--  alerts -->
														<c:if test="${appointmentInfo.displayAlerts}">
															<a href="#" onClick="return false;" title="${appointmentInfo.alert}">A</a>
														</c:if>

														<!--  notes -->
														<c:if test="${appointmentInfo.displayNotes}">
															<a href="#" onClick="return false;" title="${appointmentInfo.notes}">N</a>
														</c:if>

														<!-- doctor code block 1 -->
														<c:if test="${appointmentInfo.showDocumentLink}">

															<c:if test="${appointmentInfo.showVerLink}">
																<a href="#" title="<bean:message key="provider.appointmentProviderAdminDay.versionMsg"/> ${appointmentInfo.ver}">
																	<font color="red">*</font>
																</a>
															</c:if>

															<c:if test="${appointmentInfo.showFSRosterLink}">
																<a href="#" title="<bean:message key="provider.appointmentProviderAdminDay.rosterMsg"/> ${appointmentInfo.rosterStatus}">
																	<font color="red">$</font>
																</a>
															</c:if>

															<c:if test="${appointmentInfo.showNRorPLRosterLink}">
																<a href="#" title="<bean:message key="provider.appointmentProviderAdminDay.rosterMsg"/> ${appointmentInfo.rosterStatus}">
																	<font color="red">#</font>
																</a>
															</c:if>

														</c:if>

														<!-- doctor code block 2 -->

														<c:if test="${appointmentInfo.showPreventionWarnings}">
															<img src="../images/stop_sign.png" height="14" width="14" title="${appointmentInfo.preventionWarnings}" />&nbsp;

														</c:if>

														<a class="apptLink" href=# onClick ="popupPage(535,860,'${appointmentInfo.appointmentURL}');return false;"

															<oscar:oscarPropertiesCheck property="SHOW_APPT_REASON_TOOLTIP" value="yes" defaultVal="true">
																${appointmentInfo.appointmentLinkTitle}
															</oscar:oscarPropertiesCheck>
														>

															<oscar:oscarPropertiesCheck property="show_hc_eligibility" value="true" defaultVal="false">
																<c:if test="${appointmentInfo.activeMedicalCoverage}">+&nbsp</c:if>
															</oscar:oscarPropertiesCheck>

															${appointmentInfo.truncatedName}
														</a>


														<c:if test="${appointmentInfo.showAppointmentLinks}">

															<c:if test="${appointmentInfo.showEChart}">
																<oscar:oscarPropertiesCheck property="eform_in_appointment" value="yes">
																	&#124;<b><a href="#" onclick="popupPage(500,1024,'${appointmentInfo.eformURL}'); return false;" title="eForms">e</a></b>
																</oscar:oscarPropertiesCheck>
															</c:if>

															<!-- doctor code block 3 -->
															<c:if test="${appointmentInfo.showEncounterLink}">

																<c:if test="${appointmentInfo.singlePageChart}">
																	&#124; <a href="${appointmentInfo.singlePageChartURL}" ${appointmentInfo.singlePageChartStyle}>
																		<bean:message key="provider.appointmentProviderAdminDay.btnE"/>2</a>
																</c:if>

																<c:if test="${appointmentInfo.showOldEchartLink}">
																	&#124; <a href=# class="encounterBtn" onClick="popupWithApptNo(710, 1024,'${appointmentInfo.incomingEncounterURL}','encounter',${appointmentInfo.appointmentNo});return false;" title="<bean:message key="global.encounter"/>">
																		<bean:message key="provider.appointmentProviderAdminDay.btnE"/></a>
																</c:if>
															</c:if>

															<c:if test="${appointmentInfo.showIntakeFormLink}">
																&#124; <a href='#' onClick='popupPage(700, 1024, "formIntake.jsp?demographic_no=${appointmentInfo.demographicNo}")' title='Intake Form'>In</a>
															</c:if>

															<!--  eyeform open link -->
															<c:if test="${appointmentInfo.showEyeformLink}">
																&#124; <a href="#" onClick='popupPage(800, 1280, "../eyeform/eyeform.jsp?demographic_no=${appointmentInfo.demographicNo}&appointment_no=${appointmentInfo.appointmentNo}");return false;' title="EyeForm">EF</a>
															</c:if>

															<!-- billing code block -->
															<c:if test="${!appointmentInfo.weekView}">
																<c:if test="${appointmentInfo.showBilling}">
																	<c:choose>
																		<c:when test="${appointmentInfo.billed}">
																			&#124; <a
																				href=#
																				onClick='onUnbilled("${appointmentInfo.unbillURL}");return false;'
																				title="<bean:message key="global.billingtag"/>"
																			>
																				-<bean:message key="provider.appointmentProviderAdminDay.btnB"/>
																			</a>
																		</c:when>
																		<c:otherwise>
																			&#124; <a
																				href="${appointmentInfo.billLink}"
																				target="_blank"
																				title="<bean:message key="global.billingtag"/>"
																			>
																				<bean:message key="provider.appointmentProviderAdminDay.btnB"/>
																			</a>
																		</c:otherwise>
																	</c:choose>
																</c:if>
															</c:if>
															<!-- billing code block -->

															<c:if test="${appointmentInfo.showMasterLink}">
																&#124; <a class="masterBtn" href="javascript: function myFunction() {return false; }" onClick="popupWithApptNo(700,1024,'../demographic/demographiccontrol.jsp?demographic_no=${appointmentInfo.demographicNo}&apptProvider=${appointmentInfo.currentProviderNo}&appointment=${appointmentInfo.appointmentNo}&displaymode=edit&dboperation=search_detail','master',${appointmentInfo.appointmentNo})"
																		  title="<bean:message key="provider.appointmentProviderAdminDay.msgMasterFile"/>"><bean:message key="provider.appointmentProviderAdminDay.btnM"/></a>
															</c:if>

															<c:if test="${!appointmentInfo.weekView}">

																<!-- doctor code block 4 -->
																<c:if test="${appointmentInfo.showDoctorLink}">
																	&#124; <a href=# onClick="popupWithApptNo(700,1027,'../oscarRx/choosePatient.do?providerNo=${appointmentInfo.providerNo}&demographicNo=${appointmentInfo.demographicNo}','rx',${appointmentInfo.appointmentNo})" title="<bean:message key="global.prescriptions"/>">
																		<bean:message key="global.rx"/>
																	</a>


																	<!-- doctor color -->
																	<oscar:oscarPropertiesCheck property="ENABLE_APPT_DOC_COLOR" value="yes">
																		<c:if test="${appointmentInfo.hasProviderColor}">
																			<span style="background-color:${appointmentInfo.providerColor};width:5px">&nbsp;</span>
																		</c:if>
																	</oscar:oscarPropertiesCheck>

																	<c:if test="${appointmentInfo.showDollarSign}">
																		&#124;<b style="color:#FF0000">$</b>
																	</c:if>
																	<oscar:oscarPropertiesCheck property="SHOW_APPT_REASON" value="yes" defaultVal="true">
																		<span class="toggleable reason_${appointmentInfo.providerNo} ${appointmentInfo.hideReasonClass}">
																			<strong>&#124;${appointmentInfo.formattedReason}</strong>
																		</span>
																	</oscar:oscarPropertiesCheck>

																</c:if>

																<!-- add one link to caisi Program Management Module -->
																<c:if test="${appointmentInfo.birthday}">
																	&#124; <img src="../images/cake.gif" height="20" alt="Happy Birthday"/>
																</c:if>

																<%@include file="appointmentFormsLinks.jspf" %>

																<oscar:oscarPropertiesCheck property="appt_pregnancy" value="true" defaultVal="false">

																	<c:set var="demographicNo" value="${appointmentInfo.demographicNo}" />
																	<jsp:include page="appointmentPregnancy.jspf" >
																		<jsp:param value="${appointmentInfo.demographicNo}" name="demographicNo"/>
																	</jsp:include>

																</oscar:oscarPropertiesCheck>


															</c:if>
														</c:if>
														</font></td>
													</c:otherwise>
												</c:choose>


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


							<tr><td class="infirmaryView" NOWRAP ALIGN="center" BGCOLOR="<%=headerColor?"#bfefff":"silver"%>">
								<!-- caisi infirmary view extension modify ffffffffffff-->
								<%
								if (showApptCountForProvider)
								{
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
									if (isWeekView(request))
									{
										String dayUrl = "providercontrol.jsp" +
												"?year=" + schedule.getScheduleDate().getYear() +
												"&month=" + schedule.getScheduleDate().getMonthValue() +
												"&day=" + schedule.getScheduleDate().getDayOfMonth() +
												"&view=0" +
												"&displaymode=day" +
												"&dboperation=searchappointmentday";
										%>
										<b><a href="<%= dayUrl %>">
											<%=schedule.getScheduleDate().format(DateTimeFormatter.ISO_LOCAL_DATE)%>
										</a></b>
										<%
									}
									else
									{
										%>
										<b><input type='button' value="<bean:message key="provider.appointmentProviderAdminDay.weekLetter"/>" name='weekview' onClick=goWeekView('<%= providerNo %>') title="<bean:message key="provider.appointmentProviderAdminDay.weekView"/>" style="color:black" class="noprint">
											<input type='button' value="<bean:message key="provider.appointmentProviderAdminDay.searchLetter"/>" name='searchview' onClick=goSearchView('<%= providerNo %>') title="<bean:message key="provider.appointmentProviderAdminDay.searchView"/>" style="color:black" class="noprint">
											<b><input type='radio' name='flipview' class="noprint" onClick="goFilpView('<%= providerNo %>')" title="Flip view"  >
												<a href=# onClick="goZoomView('<%= providerNo %>','<%=StringEscapeUtils.escapeJavaScript(schedule.getFullName())%>')" onDblClick="goFilpView('<%= providerNo %>')" title="<bean:message key="provider.appointmentProviderAdminDay.zoomView"/>" >
													<%=schedule.getFullName()%></a>
											</b>
										<%
									}
									%>

									<%
									if (notOnSchedule) {
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
										if (pb.getValue().equals(prID))
										{
											%>
											<b><label><%=pb.getLabel()%></label></b>
											<%
										}
										%>
									</logic:iterate>
								</logic:equal>
								<!-- caisi infirmary view extension modify end ffffffffffffffff-->
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

	<!-- Bottom ivory bar -->

	<tr><td colspan="3">
		<table BORDER="0" CELLPADDING="0" CELLSPACING="0" WIDTH="100%" class="noprint">
			<tr>
				<td BGCOLOR="ivory" width="60%">
					<a class="redArrow" href="<%= previousScheduleUrl %>">
						&nbsp;&nbsp;<img src="../images/previous.gif" WIDTH="10" HEIGHT="9" BORDER="0" ALT="<bean:message key="provider.appointmentProviderAdminDay.viewPrevDay"/>" vspace="2"></a>
					<b><span class="dateAppointment"><% if (isWeekView(request)) {%><bean:message key="provider.appointmentProviderAdminDay.week"/> <%=week%><% } else {%><%=formatDate%><% }%></span></b>
					<a class="redArrow" href="<%= nextScheduleUrl %>">
						<img src="../images/next.gif" WIDTH="10" HEIGHT="9" BORDER="0" ALT="<bean:message key="provider.appointmentProviderAdminDay.viewNextDay"/>" vspace="2">&nbsp;&nbsp;</a>
					<a id="calendarLinkBottom" href=# onClick ="popupPage(425,430,'../share/CalendarPopup.jsp?urlfrom=../provider/providercontrol.jsp&year=<%=strYear%>&month=<%=strMonth%>&param=<%=URLEncoder.encode("&view=0&displaymode=day&dboperation=searchappointmentday", "UTF-8")%><%=isWeekView(request) ? URLEncoder.encode("&provider_no=" + provNum, "UTF-8") : ""%>')"><bean:message key="global.calendar"/></a></td>
				<td ALIGN="RIGHT" BGCOLOR="Ivory">
					| <a href="../logout.jsp"><bean:message key="global.btnLogout"/> &nbsp;</a>
				</td>
			</tr>
		</table>
	</td></tr>

</table>


</body>
</html>
