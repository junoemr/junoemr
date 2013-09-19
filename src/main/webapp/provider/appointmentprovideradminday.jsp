
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
<%@page import="org.oscarehr.phr.util.MyOscarUtils"%>
<%@page import="org.oscarehr.common.model.Appointment.BookingSource"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@page import="org.oscarehr.common.model.Provider,org.oscarehr.common.model.BillingONCHeader1"%>
<%@page import="org.oscarehr.common.model.ProviderPreference"%>
<%@page import="org.oscarehr.web.admin.ProviderPreferencesUIBean"%>
<%@page import="oscar.OscarProperties"%>
<%@page import="org.oscarehr.common.dao.UserPropertyDAO, org.oscarehr.common.dao.DemographicDao, org.oscarehr.common.model.Demographic, org.oscarehr.common.model.UserProperty" %>
<%@ page import="org.oscarehr.common.dao.MyGroupAccessRestrictionDao" %>
<%@ page import="org.oscarehr.common.model.MyGroupAccessRestriction" %>
<%@ page import="org.oscarehr.PMmodule.dao.ProviderDao" %>
<%@ page import="org.oscarehr.common.model.Provider" %>
<%
			
	if(session.getAttribute("userrole") == null )  response.sendRedirect("../logout.jsp");
	String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");

    boolean isSiteAccessPrivacy=false;
    boolean isTeamAccessPrivacy=false;

    MyGroupAccessRestrictionDao myGroupAccessRestrictionDao = SpringUtils.getBean(MyGroupAccessRestrictionDao.class);
%>
<security:oscarSec objectName="_site_access_privacy" roleName="<%=roleName$%>" rights="r" reverse="false">
	<%isSiteAccessPrivacy=true; %>
</security:oscarSec>
<security:oscarSec objectName="_team_access_privacy" roleName="<%=roleName$%>" rights="r" reverse="false">
	<%isTeamAccessPrivacy=true; %>
</security:oscarSec>

<%!
//multisite starts =====================
private boolean bMultisites = org.oscarehr.common.IsPropertiesOn.isMultisitesEnable();
private JdbcApptImpl jdbc = new JdbcApptImpl();
private List<Site> sites = new ArrayList<Site>();
private List<Site> curUserSites = new ArrayList<Site>();
private List<String> siteProviderNos = new ArrayList<String>();
private List<String> siteGroups = new ArrayList<String>();
private String selectedSite = null;
private HashMap<String,String> siteBgColor = new HashMap<String,String>();
private HashMap<String,String> CurrentSiteMap = new HashMap<String,String>();

%>

<%
if (bMultisites) {
	SiteDao siteDao = (SiteDao)WebApplicationContextUtils.getWebApplicationContext(application).getBean("siteDao");
	sites = siteDao.getAllActiveSites();
	selectedSite = (String)session.getAttribute("site_selected");

	if (selectedSite != null) {
		//get site provider list
		siteProviderNos = siteDao.getProviderNoBySiteLocation(selectedSite);
		siteGroups = siteDao.getGroupBySiteLocation(selectedSite);
	}

	if (isSiteAccessPrivacy || isTeamAccessPrivacy) {
		String siteManagerProviderNo = (String) session.getAttribute("user");
		curUserSites = siteDao.getActiveSitesByProviderNo(siteManagerProviderNo);
		if (selectedSite==null) {
			siteProviderNos = siteDao.getProviderNoBySiteManagerProviderNo(siteManagerProviderNo);
			siteGroups = siteDao.getGroupBySiteManagerProviderNo(siteManagerProviderNo);
		}
	}
	else {
		curUserSites = sites;
	}

	for (Site s : curUserSites) {
		CurrentSiteMap.put(s.getName(),"Y");
	}

	//get all sites bgColors
	for (Site st : sites) {
		siteBgColor.put(st.getName(),st.getBgColor());
	}
}
//multisite ends =======================
%>

<!-- add by caisi -->
<%@ taglib uri="http://www.caisi.ca/plugin-tag" prefix="plugin" %>
<%@ taglib uri="/WEB-INF/caisi-tag.tld" prefix="caisi" %>
<%@ taglib uri="/WEB-INF/special_tag.tld" prefix="special" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/indivo-tag.tld" prefix="myoscar" %>
<%@ taglib uri="/WEB-INF/phr-tag.tld" prefix="phr" %>

<!-- add by caisi end<style>* {border:1px solid black;}</style> -->

<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>
<%
	long loadPage = System.currentTimeMillis();
    if(session.getAttribute("userrole") == null )  response.sendRedirect("../logout.jsp");
    //String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_appointment" rights="r" reverse="<%=true%>" >
<%response.sendRedirect("../logout.jsp");%>
</security:oscarSec>

<!-- caisi infirmary view extension add -->
<caisi:isModuleLoad moduleName="caisi">
<%
if (request.getParameter("year")!=null && request.getParameter("month")!=null && request.getParameter("day")!=null)
	{
		java.util.Date infirm_date=new java.util.GregorianCalendar(Integer.valueOf(request.getParameter("year")).intValue(), Integer.valueOf(request.getParameter("month")).intValue()-1, Integer.valueOf(request.getParameter("day")).intValue()).getTime();
		session.setAttribute("infirmaryView_date",infirm_date);

	}else
	{
		session.setAttribute("infirmaryView_date",null);
	}
	String reqstr =request.getQueryString();
	if (reqstr == null)
	{
		//Hack:: an unknown bug of struts or JSP causing the queryString to be null
		String year_q = request.getParameter("year");
	    String month_q =request.getParameter("month");
	    String day_q = request.getParameter("day");
	    String view_q = request.getParameter("view");
	    String displayMode_q = request.getParameter("displaymode");
	    reqstr = "year=" + year_q + "&month=" + month_q
           + "&day="+ day_q + "&view=" + view_q + "&displaymode=" + displayMode_q;
	}
	session.setAttribute("infirmaryView_OscarQue",reqstr);
	String absurl="/infirm.do?action=showProgram";
%>
<c:import url="/infirm.do?action=showProgram" />
</caisi:isModuleLoad>
<!-- caisi infirmary view extension add end -->

<%@ page import="java.util.*, java.text.*,java.sql.*, java.net.*, oscar.*, oscar.util.*, org.oscarehr.provider.model.PreventionManager" %>

<%@ page import="org.apache.commons.lang.*" %>

<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>
<jsp:useBean id="apptMainBean" class="oscar.AppointmentMainBean" scope="session" />
<jsp:useBean id="providerBean" class="java.util.Properties" scope="session" />
<jsp:useBean id="as" class="oscar.appt.ApptStatusData" scope="page" />
<jsp:useBean id="dateTimeCodeBean" class="java.util.Hashtable" scope="page" />
<% Properties oscarVariables = OscarProperties.getInstance(); %>
<%@ include file="/common/webAppContextAndSuperMgr.jsp"%>

<!-- Struts for i18n -->
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<% PreventionManager prevMgr = (PreventionManager)webApplicationContext.getBean("preventionMgr");%>
<%!
/**
Checks if the schedule day is patients birthday
**/
public boolean isBirthday(String schedDate,String demBday){
	return schedDate.equals(demBday);
}
public boolean patientHasOutstandingPrivateBills(String demographicNo){
	oscar.oscarBilling.ca.bc.MSP.MSPReconcile msp = new oscar.oscarBilling.ca.bc.MSP.MSPReconcile();
	return msp.patientHasOutstandingPrivateBill(demographicNo);
}
%>
<%
    if(session.getAttribute("user") == null )
        response.sendRedirect("../logout.jsp");

	String curUser_no = (String) session.getAttribute("user");
	oscar.oscarSecurity.CookieSecurity cs = new oscar.oscarSecurity.CookieSecurity();
    response.addCookie(cs.GiveMeACookie(oscar.oscarSecurity.CookieSecurity.providerCookie));

    ProviderPreference providerPreference2=(ProviderPreference)session.getAttribute(SessionConstants.LOGGED_IN_PROVIDER_PREFERENCE);

    String mygroupno = providerPreference2.getMyGroupNo();
    if(mygroupno == null){
    	mygroupno = ".default";
    }
    String caisiView = null;
    caisiView = request.getParameter("GoToCaisiViewFromOscarView");
    if(caisiView!=null && "true".equals(caisiView)) {
    	mygroupno = ".default";
    }
    String userfirstname = (String) session.getAttribute("userfirstname");
    String userlastname = (String) session.getAttribute("userlastname");
    String prov= (oscarVariables.getProperty("billregion","")).trim().toUpperCase();

    int startHour=providerPreference2.getStartHour();
    int endHour=providerPreference2.getEndHour();
    int everyMin=providerPreference2.getEveryMin();
    String defaultServiceType = (String) session.getAttribute("default_servicetype");
	ProviderPreference providerPreference=ProviderPreferencesUIBean.getLoggedInProviderPreference();
    if( defaultServiceType == null && providerPreference!=null) {
    	defaultServiceType = providerPreference.getDefaultServiceType();
    }

    if( defaultServiceType == null ) {
        defaultServiceType = "";
    }

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
    
    boolean prescriptionQrCodes = providerPreference2.isPrintQrCodeOnPrescriptions();
    boolean erx_enable = providerPreference2.isERxEnabled();
    boolean erx_training_mode = providerPreference2.isERxTrainingMode();

    boolean bShortcutIntakeForm = oscarVariables.getProperty("appt_intake_form", "").equalsIgnoreCase("on") ? true : false;

    String newticklerwarningwindow=null;
    String default_pmm=null;
    String programId_oscarView=null;
	String ocanWarningWindow=null;
	String caisiBillingPreferenceNotDelete = null;

	if (org.oscarehr.common.IsPropertiesOn.isCaisiEnable() && org.oscarehr.common.IsPropertiesOn.propertiesOn("OCAN_warning_window") ) {
        ocanWarningWindow = (String)session.getAttribute("ocanWarningWindow");
	}

if (org.oscarehr.common.IsPropertiesOn.isCaisiEnable() && org.oscarehr.common.IsPropertiesOn.isTicklerPlusEnable()){
	newticklerwarningwindow = (String) session.getAttribute("newticklerwarningwindow");
	default_pmm = (String)session.getAttribute("default_pmm");

	caisiBillingPreferenceNotDelete = String.valueOf(session.getAttribute("caisiBillingPreferenceNotDelete"));
    if(caisiBillingPreferenceNotDelete==null) {
    	ProviderPreference pp = ProviderPreferencesUIBean.getProviderPreferenceByProviderNo(curUser_no);
    	if(pp!=null) {
    		caisiBillingPreferenceNotDelete = String.valueOf(pp.getDefaultDoNotDeleteBilling());
    	}

    	//List<Map> prefBillingList = oscarSuperManager.find("providerDao", "search_pref_defaultbill", new Object[] {curUser_no});
        //if (prefBillingList.size() > 0) {
       //     caisiBillingPreferenceNotDelete = String.valueOf(prefBillingList.get(0).get("defaultDoNotDeleteBilling"));
    	//}
    }

	//Disable schedule view associated with the program
	//Made the default program id "0";
	//programId_oscarView= (String)session.getAttribute("programId_oscarView");
	programId_oscarView = "0";
} else {
	programId_oscarView="0";
	session.setAttribute("programId_oscarView",programId_oscarView);
}
    int lenLimitedL=11; //L - long
    if(OscarProperties.getInstance().getProperty("APPT_SHOW_FULL_NAME","").equalsIgnoreCase("true")) {
    	lenLimitedL = 25;
    }
    int lenLimitedS=3; //S - short
    int len = lenLimitedL;
    int view = request.getParameter("view")!=null ? Integer.parseInt(request.getParameter("view")) : 0; //0-multiple views, 1-single view
    //// THIS IS THE VALUE I HAVE BEEN LOOKING FOR!!!!!
	boolean bDispTemplatePeriod = ( oscarVariables.getProperty("receptionist_alt_view") != null && oscarVariables.getProperty("receptionist_alt_view").equals("yes") ); // true - display as schedule template period, false - display as preference
	
%>
<%

    String tickler_no="", textColor="", tickler_note="";
    String ver = "", roster="";
    String yob = "";
    String mob = "";
    String dob = "";
    String demBday = "";
    StringBuffer study_no=null, study_link=null,studyDescription=null;
	String studySymbol = "#", studyColor = "red";

    String resourcebaseurl =  oscarVariables.getProperty("resource_base_url");

    List<Map<String,Object>> resultList = oscarSuperManager.find("providerDao", "search_resource_baseurl", new Object[] {"resource_baseurl"});
    for (Map url : resultList) {
            resourcebaseurl = (String)url.get("value");
    }


    boolean isWeekView = false;
    String provNum = request.getParameter("provider_no");
    if (provNum != null) {
        isWeekView = true;
    }
    if(caisiView!=null && "true".equals(caisiView)) {
    	isWeekView = false;
    }
int nProvider;

boolean caseload = "1".equals(request.getParameter("caseload"));

GregorianCalendar cal = new GregorianCalendar();
int curYear = cal.get(Calendar.YEAR);
int curMonth = (cal.get(Calendar.MONTH)+1);
int curDay = cal.get(Calendar.DAY_OF_MONTH);

int year = Integer.parseInt(request.getParameter("year"));
int month = Integer.parseInt(request.getParameter("month"));
int day = Integer.parseInt(request.getParameter("day"));

//verify the input date is really existed
cal = new GregorianCalendar(year,(month-1),day);

if (isWeekView) {
cal.add(Calendar.DATE, -(cal.get(Calendar.DAY_OF_WEEK)-1)); // change the day to the current weeks initial sunday
}

int week = cal.get(Calendar.WEEK_OF_YEAR);
year = cal.get(Calendar.YEAR);
month = (cal.get(Calendar.MONTH)+1);
day = cal.get(Calendar.DAY_OF_MONTH);

String strDate = year + "-" + month + "-" + day;
String monthDay = String.format("%02d", month) + "-" + String.format("%02d", day);
SimpleDateFormat inform = new SimpleDateFormat ("yyyy-MM-dd", request.getLocale());
String formatDate;
try {
java.util.ResourceBundle prop = ResourceBundle.getBundle("oscarResources", request.getLocale());
formatDate = UtilDateUtilities.DateToString(inform.parse(strDate), prop.getString("date.EEEyyyyMMdd"),request.getLocale());
} catch (Exception e) {
	MiscUtils.getLogger().error("Error", e);
formatDate = UtilDateUtilities.DateToString(inform.parse(strDate), "EEE, yyyy-MM-dd");
}
String strYear=""+year;
String strMonth=month>9?(""+month):("0"+month);
String strDay=day>9?(""+day):("0"+day);
   
UserPropertyDAO userPropertyDao = (UserPropertyDAO) SpringUtils.getBean("UserPropertyDAO");
DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographicDao");

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
%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="oscar.util.*"%>
<%@page import="oscar.oscarDB.*"%>

<%@page import="oscar.appt.JdbcApptImpl"%>
<%@page import="oscar.appt.ApptUtil"%>
<%@page import="org.oscarehr.common.dao.SiteDao"%>
<%@page import="org.oscarehr.common.model.Site"%>
<%@page import="org.oscarehr.web.admin.ProviderPreferencesUIBean"%>
<%@page import="org.oscarehr.common.model.ProviderPreference"%>
<%@page import="org.oscarehr.web.AppointmentProviderAdminDayUIBean"%>
<%@page import="org.oscarehr.common.model.EForm"%><html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title><%=WordUtils.capitalize(userlastname + ", " +  org.apache.commons.lang.StringUtils.substring(userfirstname, 0, 1)) + "-"%><bean:message key="provider.appointmentProviderAdminDay.title"/></title>

<!-- Determine which stylesheet to use: mobile-optimized or regular -->
<% boolean isMobileOptimized = session.getAttribute("mobileOptimized") != null;
if (isMobileOptimized) { %>
    <meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no, width=device-width"/>
    <link rel="stylesheet" href="../mobile/receptionistapptstyle.css" type="text/css">
<% } else {%>
<link rel="stylesheet" href="../receptionist/receptionistapptstyle.css" type="text/css">
<% } %>

<% if (!caseload) { %>
<c:if test="${empty sessionScope.archiveView or sessionScope.archiveView != true}">
<%! String refresh = oscar.OscarProperties.getInstance().getProperty("refresh.appointmentprovideradminday.jsp", "-1"); %>
<%="-1".equals(refresh)?"":"<meta http-equiv=\"refresh\" content=\""+refresh+";\">"%>
</c:if>
<% } %>

<script language="javascript" type="text/javascript" src="../share/javascript/Oscar.js" ></script>
<script type="text/javascript" src="../share/javascript/prototype.js"></script>
<script type="text/javascript" src="../phr/phr.js"></script>

<script src="<c:out value="../js/jquery.js"/>"></script>
<script>
	jQuery.noConflict();
</script>

<oscar:customInterface section="main"/>


<script language="JavaScript">
function storeApptNo(apptNo) {
	var url = "storeApptInSession.jsp?appointment_no="+apptNo;
	new Ajax.Request(url, {method:'get'});
}

function showPersonal()  {
    var url = "<%=request.getContextPath()%>/Provider/showPersonal.do";

    toggleReason();
    new Ajax.Request(
        url,
        { method: 'post',
          postBody: "method=toggle"
        }
    );

    return false;
}

function getElementsByClass(searchClass,node,tag) {
        var classElements = new Array();
        if ( node == null )
                node = document;
        if ( tag == null )
                tag = '*';
        var els = document.getElementsByTagName(tag);
        var elsLen = els.length;
        var pattern = new RegExp("(^|\\s)"+searchClass+"(\\s|$)");
        for (i = 0, j = 0; i < elsLen; i++) {
                if ( pattern.test(els[i].className) ) {
                        classElements[j] = els[i];
                        j++;
                }
        }
        return classElements;
}
<%
    Boolean showPersonal = (Boolean)session.getAttribute("showPersonal");
    if( showPersonal == null ) {
        showPersonal = false;
    }
%>
var display = <%=showPersonal%>;
function toggleReason() {
    var reasons = getElementsByClass("reason","span");
    display = !display;
    for( var idx = 0; idx < reasons.length; ++idx ) {

        if( display ) {
            reasons[idx].style.display = "inline";
        }
        else {
            reasons[idx].style.display = "none";
        }
    }
}
function confirmPopupPage(height, width, queryString, doConfirm, allowDay, allowWeek){
        if (doConfirm == "Yes") {
                if (confirm("<bean:message key="provider.appointmentProviderAdminDay.confirmBooking"/>")){
                 popupPage(height, width, queryString);
                }
        }
        else if (doConfirm == "Day"){
                if (allowDay == "No") {
                        alert("<bean:message key="provider.appointmentProviderAdminDay.sameDay"/>");
                }
                else {
                        popupPage(height, width, queryString);
                }
        }
        else if (doConfirm == "Wk"){
                if (allowWeek == "No") {
                        alert("<bean:message key="provider.appointmentProviderAdminDay.sameWeek"/>");
                }
                else {
                        popupPage2(queryString, 'appointment', height, width);
                }
        }
        else {
                popupPage2(queryString, 'appointment', height, width);
        }
}

function setfocus() {
this.focus();
}

function popupPage(vheight,vwidth,varpage) {
var page = "" + varpage;
windowprops = "height="+vheight+",width="+vwidth+",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=50,screenY=50,top=0,left=0";
var popup=window.open(page, "<bean:message key="provider.appointmentProviderAdminDay.apptProvider"/>", windowprops);
if (popup != null) {
if (popup.opener == null) {
popup.opener = self;
}
popup.focus();
}
}

function popUpEncounter(vheight,vwidth,varpage) {
   var page = "" + varpage;
    windowprops = "height="+vheight+",width="+vwidth+",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=50,screenY=50,top=0,left=0";
    var popup=window.open(page, "Encounter", windowprops);

    if (popup != null) {
    if (popup.opener == null) {
        popup.opener = self;
    }
        popup.focus();
    }
}

function popupPageOfChangePassword(){
<%
	Integer ed;
	String expired_days="";
	if(session.getAttribute("expired_days")!=null){
		expired_days = (String)session.getAttribute("expired_days");
	}
	if(!(expired_days.equals(" ")||expired_days.equals("")||expired_days==null)) {
%>

window.open("changePassword.jsp","changePassword","resizable=yes,scrollbars=yes,width=400,height=300");
changePassword.moveTo(0,0);
<%
	}
%>
}
function popupInboxManager(varpage){
    var page = "" + varpage;
    var windowname="apptProviderSearch";
    windowprops = "height=700,width=1100,location=no,"
    + "scrollbars=yes,menubars=no,toolbars=no,resizable=yes,top=10,left=0";
    var popup = window.open(page, windowname, windowprops);
    if (popup != null) {
        if (popup.opener == null) {
            popup.opener = self;
        }
        popup.focus();
    }
}

function popupPage2(varpage) {
popupPage2(varpage, "apptProviderSearch");
}

function popupPage2(varpage, windowname) {
popupPage2(varpage, windowname, 700, 1024);
}

function popupPage2(varpage, windowname, vheight, vwidth) {
// Provide default values for windowname, vheight, and vwidth incase popupPage2
// is called with only 1 or 2 arguments (must always specify varpage)
windowname  = typeof(windowname)!= 'undefined' ? windowname : 'apptProviderSearch';
vheight     = typeof(vheight)   != 'undefined' ? vheight : '700px';
vwidth      = typeof(vwidth)    != 'undefined' ? vwidth : '1024px';
var page = "" + varpage;
windowprops = "height="+vheight+",width="+vwidth+",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=50,screenY=50,top=0,left=0";
var popup = window.open(page, windowname, windowprops);
if (popup != null) {
	if (popup.opener == null) {
  		popup.opener = self;
	}
	popup.focus();
	}
}

<!--oscarMessenger code block-->
function popupOscarRx(vheight,vwidth,varpage) {
var page = varpage;
windowprops = "height="+vheight+",width="+vwidth+",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=0,screenY=0,top=0,left=0";
var popup=window.open(varpage, "<bean:message key="global.oscarRx"/>_appt", windowprops);
if (popup != null) {
if (popup.opener == null) {
popup.opener = self;
}
popup.focus();
}
}

function popupWithApptNo(vheight,vwidth,varpage,name,apptNo) {
	if (apptNo) storeApptNo(apptNo);
	if (name=='master')
		popup(vheight,vwidth,varpage,name);
	else if (name=='encounter')
		popUpEncounter(vheight, vwidth, varpage);
	else
		popupOscarRx(vheight,vwidth,varpage);
}

function review(key) {
  if(self.location.href.lastIndexOf("?") > 0) {
	var a = self.location.href;
    if(a.lastIndexOf("&viewall=") > 0 ) a = a.substring(0,self.location.href.lastIndexOf("&viewall="));
    if(a.lastIndexOf("&provider_no=") > 0 ) a = a.substring(0,self.location.href.lastIndexOf("&provider_no="));
  } else {
    a="providercontrol.jsp?year="+document.jumptodate.year.value+"&month="+document.jumptodate.month.value+"&day="+document.jumptodate.day.value+"&view=0&displaymode=day&dboperation=searchappointmentday&site=" + "<%=(selectedSite==null? "none" : selectedSite)%>";
  }
  self.location.href = a + "&viewall="+key ;
}

function refresh() {
document.location.reload();
}

function refresh1() {
var u = self.location.href;
if(u.lastIndexOf("view=1") > 0) {
self.location.href = u.substring(0,u.lastIndexOf("view=1")) + "view=0" + u.substring(eval(u.lastIndexOf("view=1")+6));
} else {
document.location.reload();
}
}

function onUnbilled(url) {
if(confirm("<bean:message key="provider.appointmentProviderAdminDay.onUnbilled"/>")) {
popupPage(700,720, url);
}
}

function onUpdatebill(url) {
    popupPage(700,720, url);
}


function changeGroup(s) {
var newGroupNo = s.options[s.selectedIndex].value;
if(newGroupNo.indexOf("_grp_") != -1) {
  newGroupNo = s.options[s.selectedIndex].value.substring(5);
}else{
  newGroupNo = s.options[s.selectedIndex].value;
}
<%if (org.oscarehr.common.IsPropertiesOn.isCaisiEnable() && org.oscarehr.common.IsPropertiesOn.isTicklerPlusEnable()){
%>
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
self.location.href = "providercontrol.jsp?year=<%=year%>&month=<%=month%>&day=<%=day%>&view=0&displaymode=day&dboperation=searchappointmentday&provider_no="+s;
}
function goZoomView(s, n) {
self.location.href = "providercontrol.jsp?year=<%=strYear%>&month=<%=strMonth%>&day=<%=strDay%>&view=1&curProvider="+s+"&curProviderName="+n+"&displaymode=day&dboperation=searchappointmentday" ;
}
function findProvider(p,m,d) {
popupPage(300,400, "receptionistfindprovider.jsp?pyear=" +p+ "&pmonth=" +m+ "&pday=" +d+ "&providername="+ document.findprovider.providername.value );
}
function goSearchView(s) {
	popupPage(600,650,"../appointment/appointmentsearch.jsp?provider_no="+s);
}

//popup a new tickler warning window
function load() {
	var ocan = "<%=ocanWarningWindow%>";
	if(ocan!="null") {
		alert(ocan);
	}
	if ("<%=newticklerwarningwindow%>"=="enabled") {
		if (IsPopupBlocker()) {
		    alert("You have a popup blocker, so you can not see the new tickler warning window. Please disable the pop blocker in your google bar, yahoo bar or IE ...");
		} else{
				var pu=window.open("../UnreadTickler.do",'viewUnreadTickler',"height=120,width=250,location=no,scrollbars=no,menubars=no,toolbars=no,resizable=yes,top=500,left=700");
				if(window.focus)
					pu.focus();
			}
	}

	popupPageOfChangePassword();
	refreshAllTabAlerts();
}

function IsPopupBlocker() {
var oWin = window.open("","testpopupblocker","width=100,height=50,top=5000,left=5000");
if (oWin==null || typeof(oWin)=="undefined") {
	return true;
} else {
	oWin.close();
	return false;
}
}

<%-- Refresh tab alerts --%>
function refreshAllTabAlerts() {
refreshTabAlerts("oscar_new_lab");
refreshTabAlerts("oscar_new_msg");
refreshTabAlerts("oscar_new_tickler");
refreshTabAlerts("oscar_aged_consults");
refreshTabAlerts("oscar_scratch");
}

function callRefreshTabAlerts(id) {
setTimeout("refreshTabAlerts('"+id+"')", 10);
}

function refreshTabAlerts(id) {
var url = "../provider/tabAlertsRefresh.jsp";
var pars = "id=" + id;

var myAjax = new Ajax.Updater(id, url, {method: 'get', parameters: pars});
}

function refreshSameLoc(mypage) {
 var X =  (window.pageXOffset?window.pageXOffset:window.document.body.scrollLeft);
 var Y =  (window.pageYOffset?window.pageYOffset:window.document.body.scrollTop);
 window.location.href = mypage+"&x="+X+"&y="+Y;
}

function scrollOnLoad() {
  var X = getParameter("x");
  var Y = getParameter("y");
  if(X!=null && Y!=null) {
    window.scrollTo(parseInt(X),parseInt(Y));
  }
}

function getParameter(paramName) {
  var searchString = window.location.search.substring(1);
  var i,val;
  var params = searchString.split("&");

  for (i=0;i<params.length;i++) {
    val = params[i].split("=");
    if (val[0] == paramName) {
      return val[1];
    }
  }
  return null;
}
</script>

<style type="text/css">
.reason {
    display: <%=showPersonal ? "inline" : "none"%>;
}
</style>

<% if (OscarProperties.getInstance().getBooleanProperty("indivica_hc_read_enabled", "true")) { %>
<script language="javascript" src="<%=request.getContextPath() %>/hcHandler/hcHandler.js"></script>
<script language="javascript" src="<%=request.getContextPath() %>/hcHandler/hcHandlerAppointment.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath() %>/hcHandler/hcHandler.css" type="text/css" />
<% } %>

</head>
<%if (org.oscarehr.common.IsPropertiesOn.isCaisiEnable()){%>
<body bgcolor="#EEEEFF" onload="load();" topmargin="0" leftmargin="0" rightmargin="0">
<%}else{%>
<body bgcolor="#EEEEFF" onLoad="refreshAllTabAlerts();scrollOnLoad();" topmargin="0" leftmargin="0" rightmargin="0">
<%}%>

<% boolean isTeamScheduleOnly = false; %>
<security:oscarSec roleName="<%=roleName$%>"
	objectName="_team_schedule_only" rights="r" reverse="false">
<% isTeamScheduleOnly = true; %>
</security:oscarSec>
<%
int numProvider=0, numAvailProvider=0;
String [] curProvider_no;
String [] curProviderName;
//initial provider bean for all the application
if(providerBean.isEmpty()) {
   resultList = oscarSuperManager.find("providerDao", "searchallprovider", new Object[] {});
   for (Map provider : resultList) {
      providerBean.setProperty(String.valueOf(provider.get("provider_no")), provider.get("last_name")+","+provider.get("first_name"));
   }
 }

String viewall = request.getParameter("viewall");
if( viewall == null ) {
    viewall = "0";
}
String _scheduleDate = strYear+"-"+strMonth+"-"+strDay;

if(mygroupno != null && providerBean.get(mygroupno) != null) { //single appointed provider view
     numProvider=1;
     curProvider_no = new String [numProvider];
     curProviderName = new String [numProvider];
     curProvider_no[0]=mygroupno;
     {
    	 ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
    	    
     	curProviderName[0]=providerDao.getProvider(mygroupno).getFullName();
     }
} else {
	if(view==0) { //multiple views
	   if (selectedSite!=null) {
		   	resultList = oscarSuperManager.find("providerDao", "site_searchmygroupcount", new Object[] {mygroupno,selectedSite});
	   }
	   else {
			resultList = oscarSuperManager.find("providerDao", "searchmygroupcount", new Object[] {mygroupno});
	   }
	   for (Map count : resultList) {
	        numProvider = ((Long)(count.get(count.keySet().toArray()[0]))).intValue();
	   }
       String [] param3 = new String [2];
       param3[0] = mygroupno;
       param3[1] = strDate; //strYear +"-"+ strMonth +"-"+ strDay ;
       if (selectedSite!=null) {
    	   	resultList = oscarSuperManager.find("providerDao", "site_search_numgrpscheduledate", new Object[]{mygroupno,strDate,selectedSite});
       }
       else {
       		resultList = oscarSuperManager.find("providerDao", "search_numgrpscheduledate", param3);
       }
       for (Map count : resultList) {
         numAvailProvider = ((Long)(count.get(count.keySet().toArray()[0]))).intValue();
	   }
     // _team_schedule_only does not support groups
     // As well, the mobile version only shows the schedule of the login provider.
     if(numProvider==0 || isTeamScheduleOnly || isMobileOptimized) {
       numProvider=1; //the login user
       curProvider_no = new String []{curUser_no};  //[numProvider];
       curProviderName = new String []{(userlastname+", "+userfirstname)}; //[numProvider];
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
    	 resultList = oscarSuperManager.find("providerDao", "site_searchmygroupprovider", new Object[] {mygroupno,selectedSite});
     }
     else {
     	 resultList = oscarSuperManager.find("providerDao", "searchmygroupprovider", new Object[] {mygroupno});
     }
     for (Map provider : resultList) {
       curProvider_no[iTemp] = String.valueOf(provider.get("provider_no"));
       {
      	 ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
      	 
      	 Provider p = providerDao.getProvider((String)provider.get("provider_no"));
      	 if (p!=null) {
      		 curProviderName[iTemp]=p.getFullName();
      	 } else {
      		curProviderName[iTemp]=null;
      	 }
       }
       
       iTemp++;
     }
    }
   } else { //single view
     numProvider=1;
     curProvider_no = new String [numProvider];
     curProviderName = new String [numProvider];
     curProvider_no[0]=request.getParameter("curProvider");
     curProviderName[0]=request.getParameter("curProviderName");
   }
}
//set timecode bean
String bgcolordef = "#486ebd" ;
String [] param3 = new String[2];
param3[0] = strDate; //strYear+"-"+strMonth+"-"+strDay;
for(nProvider=0;nProvider<numProvider;nProvider++) {
     param3[1] = curProvider_no[nProvider];
     resultList = oscarSuperManager.find("providerDao", "search_appttimecode", param3);
     for (Map provider : resultList) {
       dateTimeCodeBean.put(String.valueOf(provider.get("provider_no")), String.valueOf(provider.get("timecode")));
     }
}

   resultList = oscarSuperManager.find("providerDao", "search_timecode", new Object[] {});
   for (Map appt : resultList) {
     dateTimeCodeBean.put("description"+appt.get("code"), appt.get("description"));
     dateTimeCodeBean.put("duration"+appt.get("code"), appt.get("duration"));
     dateTimeCodeBean.put("color"+appt.get("code"), (appt.get("color")==null || "".equals(appt.get("color")))?bgcolordef:appt.get("color"));
     dateTimeCodeBean.put("confirm" + appt.get("code"), appt.get("confirm"));
   }

java.util.Locale vLocale =(java.util.Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY);

%>


<table BORDER="0" CELLPADDING="0" CELLSPACING="0" WIDTH="100%" id="firstTable">
<tr>
<td id="firstMenu">
<ul id="navlist">

<caisi:isModuleLoad moduleName="TORONTO_RFQ" reverse="true">
<security:oscarSec roleName="<%=roleName$%>" objectName="_day" rights="r">
<li id="today">
    <a class="rightButton top" href="providercontrol.jsp?year=<%=curYear%>&month=<%=curMonth%>&day=<%=curDay%>&view=<%=view==0?"0":("1&curProvider="+request.getParameter("curProvider")+"&curProviderName="+request.getParameter("curProviderName") )%>&displaymode=day&dboperation=searchappointmentday&viewall=<%=request.getParameter("viewall")%>" TITLE='<bean:message key="provider.appointmentProviderAdminDay.viewDaySched"/>' OnMouseOver="window.status='<bean:message key="provider.appointmentProviderAdminDay.viewDaySched"/>' ; return true"><bean:message key="global.today"/></a>
</li>
</security:oscarSec>
<security:oscarSec roleName="<%=roleName$%>" objectName="_month" rights="r">
 <li>
    <a href="providercontrol.jsp?year=<%=year%>&month=<%=month%>&day=1&view=<%=view==0?"0":("1&curProvider="+request.getParameter("curProvider")+"&curProviderName="+request.getParameter("curProviderName") )%>&displaymode=month&dboperation=searchappointmentmonth&viewall=<%=request.getParameter("viewall")%>" TITLE='<bean:message key="provider.appointmentProviderAdminDay.viewMonthSched"/>' OnMouseOver="window.status='<bean:message key="provider.appointmentProviderAdminDay.viewMonthSched"/>' ; return true"><bean:message key="global.month"/></a>
 </li>
 </security:oscarSec>
 <security:oscarSec roleName="<%=roleName$%>" objectName="_resource" rights="r">
 <li>
    <a href="#" ONCLICK ="popupPage2('<%=resourcebaseurl%>');return false;" title="<bean:message key="provider.appointmentProviderAdminDay.viewResources"/>" onmouseover="window.status='<bean:message key="provider.appointmentProviderAdminDay.viewResources"/>';return true"><bean:message key="oscarEncounter.Index.clinicalResources"/></a>
 </li>
 </security:oscarSec>
</caisi:isModuleLoad>

 <% if (isMobileOptimized) { %>
        <!-- Add a menu button for mobile version, which opens menu contents when clicked on -->
        <li id="menu"><a class="leftButton top" onClick="showHideItem('navlistcontents');">
                <bean:message key="global.menu" /></a>
            <ul id="navlistcontents" style="display:none;">
<% } %>
<security:oscarSec roleName="<%=roleName$%>" objectName="_search" rights="r">
 <li id="search">
    <caisi:isModuleLoad moduleName="caisi">
       <a HREF="../PMmodule/ClientSearch2.do" TITLE='<bean:message key="global.searchPatientRecords"/>' OnMouseOver="window.status='<bean:message key="global.searchPatientRecords"/>' ; return true"><bean:message key="provider.appointmentProviderAdminDay.search"/></a>
    </caisi:isModuleLoad>
    <caisi:isModuleLoad moduleName="caisi" reverse="true">
       <a HREF="#" ONCLICK ="popupPage2('../demographic/search.jsp');return false;"  TITLE='<bean:message key="global.searchPatientRecords"/>' OnMouseOver="window.status='<bean:message key="global.searchPatientRecords"/>' ; return true"><bean:message key="provider.appointmentProviderAdminDay.search"/></a>
    </caisi:isModuleLoad>
</li>
</security:oscarSec>
<caisi:isModuleLoad moduleName="TORONTO_RFQ" reverse="true">
<security:oscarSec roleName="<%=roleName$%>" objectName="_report" rights="r">
<li>
    <a HREF="#" ONCLICK ="popupPage2('../report/reportindex.jsp','reportPage');return false;"   TITLE='<bean:message key="global.genReport"/>' OnMouseOver="window.status='<bean:message key="global.genReport"/>' ; return true"><bean:message key="global.report"/></a>
</li>
</security:oscarSec>
<oscar:oscarPropertiesCheck property="NOT_FOR_CAISI" value="no" defaultVal="true">

<security:oscarSec roleName="<%=roleName$%>" objectName="_billing" rights="r">
<li>
     <%
     OscarProperties oscarProps = OscarProperties.getInstance();
     String clinicaid_link = "";
     if(Boolean.parseBoolean(oscarProps.getProperty("clinicaid_billing", "")) && prov.equals("AB")){
    	 clinicaid_link = "../billing/billingClinicAid.jsp?billing_action=invoice_reports";
    	 //Force this link to go to BC billing report
    	 %>
    	 <a HREF="#" ONCLICK ="popupPage2('../billing/CA/BC/billingReportCenter.jsp?displaymode=billreport&providerview=<%=curUser_no%>');return false;" TITLE='<bean:message key="global.genBillReport"/>' onMouseOver="window.status='<bean:message key="global.genBillReport"/>';return true"><bean:message key="global.billing"/></a>
    	 <%
     }else{
     
     if (vLocale.getCountry().equals("BR")) { %>
       <a HREF="#" ONCLICK ="popupPage2('../oscar/billing/consultaFaturamentoMedico/init.do');return false;" TITLE='<bean:message key="global.genBillReport"/>' onMouseOver="window.status='<bean:message key="global.genBillReport"/>';return true"><bean:message key="global.billing"/></a>
     <% } else {%>
	<a HREF="#" ONCLICK ="popupPage2('../billing/CA/<%=prov%>/billingReportCenter.jsp?displaymode=billreport&providerview=<%=curUser_no%>');return false;" TITLE='<bean:message key="global.genBillReport"/>' onMouseOver="window.status='<bean:message key="global.genBillReport"/>';return true"><bean:message key="global.billing"/></a>
	<% }
	}%>
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

 </caisi:isModuleLoad>
 <caisi:isModuleLoad moduleName="TORONTO_RFQ" reverse="true">
 	<security:oscarSec roleName="<%=roleName$%>" objectName="_msg" rights="r">
     <li>
	 <a HREF="#" ONCLICK ="popupOscarRx(600,1024,'../oscarMessenger/DisplayMessages.do?providerNo=<%=curUser_no%>&userName=<%=URLEncoder.encode(userfirstname+" "+userlastname)%>')" title="<bean:message key="global.messenger"/>">
	 <span id="oscar_new_msg"><bean:message key="global.msg"/></span></a>
     </li>
   	</security:oscarSec>
 </caisi:isModuleLoad>
<caisi:isModuleLoad moduleName="TORONTO_RFQ" reverse="true">
<security:oscarSec roleName="<%=roleName$%>" objectName="_con" rights="r">
<li id="con">
 <a HREF="#" ONCLICK ="popupOscarRx(625,1024,'../oscarEncounter/IncomingConsultation.do?providerNo=<%=curUser_no%>&userName=<%=URLEncoder.encode(userfirstname+" "+userlastname)%>')" title="<bean:message key="provider.appointmentProviderAdminDay.viewConReq"/>">
 <span id="oscar_aged_consults"><bean:message key="global.con"/></span></a>
</li>
</security:oscarSec>
</caisi:isModuleLoad>
<security:oscarSec roleName="<%=roleName$%>" objectName="_pref" rights="r">
<li>    <!-- remove this and let providerpreference check -->
    <caisi:isModuleLoad moduleName="ticklerplus">
	<a href=# onClick ="popupPage(715,680,'providerpreference.jsp?provider_no=<%=curUser_no%>&start_hour=<%=startHour%>&end_hour=<%=endHour%>&every_min=<%=everyMin%>&mygroup_no=<%=mygroupno%>&new_tickler_warning_window=<%=newticklerwarningwindow%>&default_pmm=<%=default_pmm%>&caisiBillingPreferenceNotDelete=<%=caisiBillingPreferenceNotDelete%>');return false;" TITLE='<bean:message key="provider.appointmentProviderAdminDay.msgSettings"/>' OnMouseOver="window.status='<bean:message key="provider.appointmentProviderAdminDay.msgSettings"/>' ; return true"><bean:message key="global.pref"/></a>
    </caisi:isModuleLoad>
    <caisi:isModuleLoad moduleName="ticklerplus" reverse="true">
	<a href=# onClick ="popupPage(715,680,'providerpreference.jsp?provider_no=<%=curUser_no%>&start_hour=<%=startHour%>&end_hour=<%=endHour%>&every_min=<%=everyMin%>&mygroup_no=<%=mygroupno%>');return false;" TITLE='<bean:message key="provider.appointmentProviderAdminDay.msgSettings"/>' OnMouseOver="window.status='<bean:message key="provider.appointmentProviderAdminDay.msgSettings"/>' ; return true"><bean:message key="global.pref"/></a>
    </caisi:isModuleLoad>
</li>
</security:oscarSec>
 <caisi:isModuleLoad moduleName="TORONTO_RFQ" reverse="true">
<security:oscarSec roleName="<%=roleName$%>" objectName="_edoc" rights="r">
<li>
   <a HREF="#" onclick="popup('700', '1024', '../dms/documentReport.jsp?function=provider&functionid=<%=curUser_no%>&curUser=<%=curUser_no%>', 'edocView');" TITLE='<bean:message key="provider.appointmentProviderAdminDay.viewEdoc"/>'><bean:message key="global.edoc"/></a>
</li>
</security:oscarSec>
 </caisi:isModuleLoad>
 <security:oscarSec roleName="<%=roleName$%>" objectName="_tickler" rights="r">
<li>
   <caisi:isModuleLoad moduleName="ticklerplus" reverse="true">
    <a HREF="#" ONCLICK ="popupPage2('../tickler/ticklerMain.jsp','<bean:message key="global.tickler"/>');return false;" TITLE='<bean:message key="global.tickler"/>'>
	<span id="oscar_new_tickler"><bean:message key="global.btntickler"/></span></a>
   </caisi:isModuleLoad>
   <caisi:isModuleLoad moduleName="ticklerplus">
    <a HREF="#" ONCLICK ="popupPage2('../Tickler.do?filter.assignee=<%=curUser_no%>&filter.demographic_no=&filter.demographic_webName=','<bean:message key="global.tickler"/>');return false;" TITLE='<bean:message key="global.tickler"/>'+'+'>
	<span id="oscar_new_tickler"><bean:message key="global.btntickler"/></span></a>
   </caisi:isModuleLoad>
</li>
</security:oscarSec>
<oscar:oscarPropertiesCheck property="OSCAR_LEARNING" value="yes">
<li>
    <a HREF="#" ONCLICK ="popupPage2('../oscarLearning/CourseView.jsp','<bean:message key="global.courseview"/>');return false;" TITLE='<bean:message key="global.courseview"/>'>
	<span id="oscar_courseview"><bean:message key="global.btncourseview"/></span></a>
</li>
</oscar:oscarPropertiesCheck>

<oscar:oscarPropertiesCheck property="WORKFLOW" value="yes">
   <li><a href="javascript: function myFunction() {return false; }" onClick="popup(700,1024,'../oscarWorkflow/WorkFlowList.jsp','<bean:message key="global.workflow"/>')"><bean:message key="global.btnworkflow"/></a></li>
</oscar:oscarPropertiesCheck>
 <oscar:oscarPropertiesCheck property="MY_OSCAR" value="yes">
    <myoscar:indivoRegistered provider="<%=curUser_no%>">
	<%MyOscarUtils.attemptMyOscarAutoLoginIfNotAlreadyLoggedInAsynchronously(LoggedInInfo.loggedInInfo.get());%>
    <li>
	<a HREF="#" ONCLICK ="popup('600', '1024','../phr/PhrMessage.do?method=viewMessages','INDIVOMESSENGER2<%=curUser_no%>')" title='<bean:message key="global.myoscar"/>'><phr:setColor><bean:message key="global.btnmyoscar"/></phr:setColor></a>
    </li>
    </myoscar:indivoRegistered>
</oscar:oscarPropertiesCheck>
<caisi:isModuleLoad moduleName="TORONTO_RFQ" reverse="true">
	<security:oscarSec roleName="<%=roleName$%>" objectName="_admin,_admin.userAdmin,_admin.schedule,_admin.billing,_admin.resource,_admin.reporting,_admin.backup,_admin.messenger,_admin.eform,_admin.encounter,_admin.misc" rights="r">
  <li id="admin">
     <a HREF="#" ONCLICK ="popupOscarRx(700,687,'../admin/admin.jsp');return false;"><bean:message key="global.admin"/></a>
  </li>
  <!-- Added logout link for mobile version -->
  <li id="logoutMobile">
      <a href="../logout.jsp"><bean:message key="global.btnLogout"/></a>
  </li>
</security:oscarSec>
	</caisi:isModuleLoad>
<!-- plugins menu extension point add -->
<%int pluginMenuTagNumber=0; %>
<plugin:pageContextExtension serviceName="oscarMenuExtension" stemFromPrefix="Oscar"/>
<logic:iterate name="oscarMenuExtension.points" id="pt" scope="page" type="oscar.caisi.OscarMenuExtension">
<%if (oscar.util.plugin.IsPropertiesOn.propertiesOn(pt.getName().toLowerCase())) {
	pluginMenuTagNumber++;
%>

       <li><a href='<html:rewrite page="<%= pt.getLink() %>"/>'>
       <%= pt.getName() %></a></li>
<%} %>
</logic:iterate>

<!-- plugin menu extension point add end-->

<%int menuTagNumber=0; %>
<caisi:isModuleLoad moduleName="caisi">
   <li>
     <a href='<html:rewrite page="/PMmodule/ProviderInfo.do"/>'>Program</a>
     <% menuTagNumber++ ; %>
   </li>
</caisi:isModuleLoad>
<% if (isMobileOptimized) { %>
    </ul></li> <!-- end menu list for mobile-->
<% } %>
</ul>  <!--- old TABLE -->

</td>

<form method="post" name="findprovider" onSubmit="findProvider(<%=year%>,<%=month%>,<%=day%>);return false;" target="apptReception" action="receptionistfindprovider.jsp">
<td align="right" valign="bottom">
<caisi:isModuleLoad moduleName="TORONTO_RFQ" reverse="true">
<INPUT TYPE="text" NAME="providername" VALUE="" WIDTH="2" HEIGHT="10" border="0" size="10" maxlength="10">
<INPUT TYPE="SUBMIT" NAME="Go" VALUE='<bean:message key="receptionist.appointmentreceptionistadminday.btnGo"/>' onClick="findProvider(<%=year%>,<%=month%>,<%=day%>);return false;">
&nbsp;&nbsp;
</caisi:isModuleLoad>

  	| <a href="javascript: function myFunction() {return false; }" onClick="popup(700,1024,'../scratch/index.jsp','scratch')"><span id="oscar_scratch"></span></a>&nbsp;

	<caisi:isModuleLoad moduleName="TORONTO_RFQ" reverse="true">
	| <a href=# onClick ="popupPage(600,750,'<%=resourcebaseurl%>')"><bean:message key="global.help"/></a>
	</caisi:isModuleLoad>

	| <a href="../logout.jsp"><bean:message key="global.btnLogout"/>&nbsp;</a>

</td>
</form>

</tr>
</table>

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

<div id="system_message"></div>
<div id="facility_message"></div>
<%
if (caseload) {
%>
<%@ include file="caseload.jsp" %>
<%
} else {
%>
<form name="appointmentForm">
<table BORDER="0" CELLPADDING="1" CELLSPACING="0" WIDTH="100%" BGCOLOR="#C0C0C0">
<tr id="ivoryBar">
<td id="dateAndCalendar" BGCOLOR="ivory" width="33%">
 <a class="redArrow" href="providercontrol.jsp?year=<%=year%>&month=<%=month%>&day=<%=isWeekView?(day-7):(day-1)%>&view=<%=view==0?"0":("1&curProvider="+request.getParameter("curProvider")+"&curProviderName="+request.getParameter("curProviderName") )%>&displaymode=day&dboperation=searchappointmentday<%=isWeekView?"&provider_no="+provNum:""%>&viewall=<%=viewall%>">
 &nbsp;&nbsp;<img src="../images/previous.gif" WIDTH="10" HEIGHT="9" BORDER="0" ALT="<bean:message key="provider.appointmentProviderAdminDay.viewPrevDay"/>" vspace="2"></a>
 <b><span class="dateAppointment"><% if (isWeekView) { %><bean:message key="provider.appointmentProviderAdminDay.week"/> <%=week%><% } else { %><%=formatDate%><% } %></span></b>
 <a class="redArrow" href="providercontrol.jsp?year=<%=year%>&month=<%=month%>&day=<%=isWeekView?(day+7):(day+1)%>&view=<%=view==0?"0":("1&curProvider="+request.getParameter("curProvider")+"&curProviderName="+request.getParameter("curProviderName") )%>&displaymode=day&dboperation=searchappointmentday<%=isWeekView?"&provider_no="+provNum:""%>&viewall=<%=viewall%>">
 <img src="../images/next.gif" WIDTH="10" HEIGHT="9" BORDER="0" ALT="<bean:message key="provider.appointmentProviderAdminDay.viewNextDay"/>" vspace="2">&nbsp;&nbsp;</a>
<a id="calendarLink" href=# onClick ="popupPage(425,430,'../share/CalendarPopup.jsp?urlfrom=../provider/providercontrol.jsp&year=<%=strYear%>&month=<%=strMonth%>&param=<%=URLEncoder.encode("&view=0&displaymode=day&dboperation=searchappointmentday&viewall="+viewall,"UTF-8")%><%=isWeekView?URLEncoder.encode("&provider_no="+provNum, "UTF-8"):""%>')"><bean:message key="global.calendar"/></a>
<%
	boolean anonymousEnabled = LoggedInInfo.loggedInInfo.get().currentFacility.isEnableAnonymous();
	if(anonymousEnabled) {
%>
&nbsp;&nbsp;(<a href="#" onclick="popupPage(710, 1024,'<html:rewrite page="/PMmodule/createAnonymousClient.jsp"/>?programId=<%=(String)session.getAttribute(SessionConstants.CURRENT_PROGRAM_ID)%>');return false;">New Anon Client</a>)
<% } %>
</td>

<td class="title" ALIGN="center"  BGCOLOR="ivory" width="33%">

<% if (isWeekView) {
for(int provIndex=0;provIndex<numProvider;provIndex++) {
if (curProvider_no[provIndex].equals(provNum)) { %>
<bean:message key="provider.appointmentProviderAdminDay.weekView"/>: <%=curProviderName[provIndex]%>
<% } } } else { if (view==1) {%>
<a href='providercontrol.jsp?year=<%=strYear%>&month=<%=strMonth%>&day=<%=strDay%>&view=0&displaymode=day&dboperation=searchappointmentday'><bean:message key="provider.appointmentProviderAdminDay.grpView"/></a>
<% } else { %>
<% if (!isMobileOptimized) { %> <bean:message key="global.hello"/> <% } %>
<% out.println( userfirstname+" "+userlastname); %><a id="expandReason" href="#" onclick="return showPersonal();" title="<bean:message key="provider.appointmentProviderAdminDay.expandreason"/>">*</a>
</td>
<% } } %>
<td id="group" ALIGN="RIGHT" BGCOLOR="Ivory">

<% if (isWeekView) { %>
<bean:message key="provider.appointmentProviderAdminDay.doctor"/>:
<select name="provider_select" onChange="goWeekView(this.options[this.selectedIndex].value)">
<% for (nProvider=0;nProvider<numProvider;nProvider++) { %>
<option value="<%=curProvider_no[nProvider]%>"<%=curProvider_no[nProvider].equals(provNum)?" selected":""%>><%=curProviderName[nProvider]%></option>
<% } %>

</select>

&nbsp;|&nbsp;
<% if(request.getParameter("viewall")!=null && request.getParameter("viewall").equals("1") ) { %>
         <a href=# onClick = "review('0')" title="<bean:message key="provider.appointmentProviderAdminDay.viewProvAval"/>"><bean:message key="provider.appointmentProviderAdminDay.schedView"/></a>
<% } else {  %>
         <a href=# onClick = "review('1')" title="<bean:message key="provider.appointmentProviderAdminDay.viewAllProv"/>"><bean:message key="provider.appointmentProviderAdminDay.viewAll"/></a>
<% } %>

<% } else { %>

<!-- caisi infirmary view extension add ffffffffffff-->
<caisi:isModuleLoad moduleName="caisi">
<table><tr><td align="right">
    <caisi:ProgramExclusiveView providerNo="<%=curUser_no%>" value="appointment">
	<% session.setAttribute("infirmaryView_isOscar", "true"); %>
    </caisi:ProgramExclusiveView>
    <caisi:ProgramExclusiveView providerNo="<%=curUser_no%>" value="case-management">
	<% session.setAttribute("infirmaryView_isOscar", "false"); %>
    </caisi:ProgramExclusiveView>
</caisi:isModuleLoad>

<caisi:isModuleLoad moduleName="TORONTO_RFQ">
	<% session.setAttribute("infirmaryView_isOscar", "false"); %>
</caisi:isModuleLoad>

<caisi:isModuleLoad moduleName="oscarClinic">
	<% session.setAttribute("infirmaryView_isOscar", "true"); %>
</caisi:isModuleLoad>
<!-- caisi infirmary view extension add end ffffffffffffff-->


<logic:notEqual name="infirmaryView_isOscar" value="false">

<%
//session.setAttribute("case_program_id", null);
%>
	<!--  multi-site , add site dropdown list -->
 <%if (bMultisites) { %>
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
    		<option value="<%= curUserSites.get(i).getName() %>" style="background-color:<%= curUserSites.get(i).getBgColor() %>"
    				<%=(curUserSites.get(i).getName().equals(selectedSite)) ? " selected " : "" %> >
    			<%= curUserSites.get(i).getName() %>
    		</option>
    	<% } %>
    	</select>
<%} %>
  <span><bean:message key="global.group"/>:</span>

<%
	List<MyGroupAccessRestriction> restrictions = myGroupAccessRestrictionDao.findByProviderNo(curUser_no);
%>
  <select id="mygroup_no" name="mygroup_no" onChange="changeGroup(this)">
  <option value=".<bean:message key="global.default"/>">.<bean:message key="global.default"/></option>


<security:oscarSec roleName="<%=roleName$%>" objectName="_team_schedule_only" rights="r" reverse="false">
<%
	String provider_no = curUser_no;
	resultList = oscarSuperManager.find("providerDao", "searchloginteam", new Object[]{provider_no, provider_no});
	for (Map provider : resultList) {
		boolean skip = checkRestriction(restrictions,(String)provider.get("provider_no"));
		if(!skip) {

%>
<option value="<%=provider.get("provider_no")%>" <%=mygroupno.equals(provider.get("provider_no"))?"selected":""%>>
		<%=provider.get("last_name")+", "+provider.get("first_name")%></option>
<%
	} }
%>

</security:oscarSec>
<security:oscarSec roleName="<%=roleName$%>" objectName="_team_schedule_only" rights="r" reverse="true">
<%
	request.getSession().setAttribute("archiveView","false");
	resultList = oscarSuperManager.find("providerDao", "searchmygroupno", new Object[] {});
	for (Map group : resultList) {
		boolean skip = checkRestriction(restrictions,(String)group.get("mygroup_no"));

		if (!skip && (!bMultisites || siteGroups == null || siteGroups.size() == 0 || siteGroups.contains(group.get("mygroup_no")))) {
%>
  <option value="<%="_grp_"+group.get("mygroup_no")%>"
		<%=mygroupno.equals(group.get("mygroup_no"))?"selected":""%>><%=group.get("mygroup_no")%></option>
<%
		}
	}

	resultList = oscarSuperManager.find("providerDao", "searchprovider", new Object[] {});
	for (Map provider : resultList) {
		boolean skip = checkRestriction(restrictions,(String)provider.get("provider_no"));

		if (!skip && (!bMultisites || siteProviderNos  == null || siteProviderNos.size() == 0 || siteProviderNos.contains(provider.get("provider_no")))) {
%>
  <option value="<%=provider.get("provider_no")%>" <%=mygroupno.equals(provider.get("provider_no"))?"selected":""%>>
		<%=provider.get("last_name")+", "+provider.get("first_name")%></option>
<%
		}
	}
%>
</security:oscarSec>
</select>

&nbsp;|&nbsp;
<% if(request.getParameter("viewall")!=null && request.getParameter("viewall").equals("1") ) { %>
         <a href=# onClick = "review('0')" title="<bean:message key="provider.appointmentProviderAdminDay.viewProvAval"/>"><bean:message key="provider.appointmentProviderAdminDay.schedView"/></a>
<% } else {  %>
         <a href=# onClick = "review('1')" title="<bean:message key="provider.appointmentProviderAdminDay.viewAllProv"/>"><bean:message key="provider.appointmentProviderAdminDay.viewAll"/></a>
<% } %>
</logic:notEqual>

<logic:equal name="infirmaryView_isOscar" value="false">
<%
	WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
%>
&nbsp;&nbsp;&nbsp;&nbsp;
</logic:equal>

<% } %>


<!-- caisi infirmary view extension add fffffffffffff-->
<caisi:isModuleLoad moduleName="caisi">

	<%@ include file="infirmaryviewprogramlist.jspf" %>

</caisi:isModuleLoad>
<!-- caisi infirmary view extension add end fffffffffffff-->

| <a href='providercontrol.jsp?year=<%=curYear%>&month=<%=curMonth%>&day=<%=curDay%>&view=0&displaymode=day&dboperation=searchappointmentday&caseload=1&clProv=<%=curUser_no%>&viewall=<%=request.getParameter("viewall")%>'>Caseload</a>
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
<% bShowDocLink = true; %>
</security:oscarSec>
<security:oscarSec roleName="<%=roleName$%>" objectName="_eChart" rights="r">
<% bShowEncounterLink = true; %>
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

boolean userAvail = true;
int me = -1;
for(nProvider=0;nProvider<numProvider;nProvider++) {
	if(curUser_no.equals(curProvider_no[nProvider]) ) {
       //userInGroup = true;
		me = nProvider; break;
	}
}

   // set up the iterator appropriately (today - for each doctor; this week - for each day)
   int iterMax;
   if (isWeekView) {
      iterMax=7;
      // find the nProvider value that corresponds to provNum
      for(int provIndex=0;provIndex<numProvider;provIndex++) {
         if (curProvider_no[provIndex].equals(provNum)) {
            nProvider=provIndex;
         }
      }
   } else {
      iterMax=numProvider;
   }

   StringBuffer hourmin = null;
   String [] param1 = new String[2];

   for(int iterNum=0;iterNum<iterMax;iterNum++) {

     if (isWeekView) {
        // get the appropriate datetime objects for the current day in this week
        year = cal.get(Calendar.YEAR);
        month = (cal.get(Calendar.MONTH)+1);
        day = cal.get(Calendar.DAY_OF_MONTH);

        strDate = year + "-" + month + "-" + day;
        monthDay = String.format("%02d", month) + "-" + String.format("%02d", day);

        inform = new SimpleDateFormat ("yyyy-MM-dd", request.getLocale());
        try {
           java.util.ResourceBundle prop = ResourceBundle.getBundle("oscarResources", request.getLocale());
           formatDate = UtilDateUtilities.DateToString(inform.parse(strDate), prop.getString("date.EEEyyyyMMdd"),request.getLocale());
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
     resultList = oscarSuperManager.find("providerDao", "search_appttimecode", param3);
     for (Map provider : resultList) {
       dateTimeCodeBean.put(String.valueOf(provider.get("provider_no")), String.valueOf(provider.get("timecode")));
     }

     resultList = oscarSuperManager.find("providerDao", "search_timecode", new Object[] {});
     for (Map appt : resultList) {
       dateTimeCodeBean.put("description"+appt.get("code"), appt.get("description"));
       dateTimeCodeBean.put("duration"+appt.get("code"), appt.get("duration"));
       dateTimeCodeBean.put("color"+appt.get("code"), (appt.get("color")==null || "".equals(appt.get("color")))?bgcolordef:appt.get("color"));
       dateTimeCodeBean.put("confirm" + appt.get("code"), appt.get("confirm"));
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
     resultList = oscarSuperManager.find("providerDao", "search_scheduledate_single", param1);

     //viewall function
     if(request.getParameter("viewall")==null || request.getParameter("viewall").equals("0") ) {
         if(resultList.size()==0 || "0".equals(resultList.get(0).get("available")) ) {
             if(nProvider!=me ) continue;
             else userAvail = false;
         }
     }
     bColor=bColor?false:true;
 %>
            <td valign="top" width="<%=isWeekView?100/7:100/numProvider%>%"> <!-- for the first provider's schedule -->

        <table border="0" cellpadding="0" bgcolor="#486ebd" cellspacing="0" width="100%"><!-- for the first provider's name -->
          <tr><td class="infirmaryView" NOWRAP ALIGN="center" BGCOLOR="<%=bColor?"#bfefff":"silver"%>">
 <!-- caisi infirmary view extension modify ffffffffffff-->
  <logic:notEqual name="infirmaryView_isOscar" value="false">

      <% if (isWeekView) { %>
          <b><a href="providercontrol.jsp?year=<%=year%>&month=<%=month%>&day=<%=day%>&view=0&displaymode=day&dboperation=searchappointmentday"><%=formatDate%></a></b>
      <% } else { %>
  <b><input type='button' value="<bean:message key="provider.appointmentProviderAdminDay.weekLetter"/>" name='weekview' onClick=goWeekView('<%=curProvider_no[nProvider]%>') title="<bean:message key="provider.appointmentProviderAdminDay.weekView"/>" style="color:black">
	  <input type='button' value="<bean:message key="provider.appointmentProviderAdminDay.searchLetter"/>" name='searchview' onClick=goSearchView('<%=curProvider_no[nProvider]%>') title="<bean:message key="provider.appointmentProviderAdminDay.searchView"/>" style="color:black">
          <b><input type='radio' name='flipview' onClick="goFilpView('<%=curProvider_no[nProvider]%>')" title="Flip view"  >
          <a href=# onClick="goZoomView('<%=curProvider_no[nProvider]%>','<%=curProviderName[nProvider]%>')" onDblClick="goFilpView('<%=curProvider_no[nProvider]%>')" title="<bean:message key="provider.appointmentProviderAdminDay.zoomView"/>" >
          <!--a href="providercontrol.jsp?year=<%=strYear%>&month=<%=strMonth%>&day=<%=strDay%>&view=1&curProvider=<%=curProvider_no[nProvider]%>&curProviderName=<%=curProviderName[nProvider]%>&displaymode=day&dboperation=searchappointmentday" title="<bean:message key="provider.appointmentProviderAdminDay.zoomView"/>"-->
          <%=curProviderName[nProvider]%></a></b>
      <% } %>

          <% if (!userAvail) {%>
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
<!-- caisi infirmary view extension modify end ffffffffffffffff-->
</td></tr>
          <tr><td valign="top">

<!-- caisi infirmary view exteion add -->
<!--  fffffffffffffffffffffffffffffffffffffffffff-->
<caisi:isModuleLoad moduleName="caisi">
<%@ include file="infirmarydemographiclist.jspf" %>
</caisi:isModuleLoad>
<logic:notEqual name="infirmaryView_isOscar" value="false">
<!-- caisi infirmary view exteion add end ffffffffffffffffff-->
<!-- =============== following block is the original oscar code. -->
        <!-- table for hours of day start -->
        <table id="providerSchedule" border="0" cellpadding="0" bgcolor="<%=userAvail?"#486ebd":"silver"%>" cellspacing="0" width="100%">
<%
        bFirstTimeRs=true;
        bFirstFirstR=true;
        String [] param0 = new String[3];

                param0[0]=curProvider_no[nProvider];
                param0[1]=year+"-"+month+"-"+day;//e.g."2001-02-02";
				param0[2]=programId_oscarView;
                List<Map<String,Object>> appointmentList = oscarSuperManager.find("providerDao", strsearchappointmentday, param0);
                Iterator<Map<String,Object>> it = appointmentList.iterator();

                Map appointment = null;

			    for(ih=startHour*60; ih<=(endHour*60+(60/depth-1)*depth); ih+=depth) { // use minutes as base
            hourCursor = ih/60;
            minuteCursor = ih%60;
            bColorHour=minuteCursor==0?true:false; //every 00 minute, change color

            //templatecode
            if((dateTimeCodeBean.get(curProvider_no[nProvider]) != null)&&(dateTimeCodeBean.get(curProvider_no[nProvider]) != "")) {
	            int nLen = 24*60 / ((String) dateTimeCodeBean.get(curProvider_no[nProvider]) ).length();
	            int ratio = (hourCursor*60+minuteCursor)/nLen;
              hourmin = new StringBuffer(dateTimeCodeBean.get(curProvider_no[nProvider])!=null?((String) dateTimeCodeBean.get(curProvider_no[nProvider])).substring(ratio,ratio+1):" " );
            } else { hourmin = new StringBuffer(); }
        %>
          <tr>
            <td align="RIGHT" class="<%=bColorHour?"scheduleTime00":"scheduleTimeNot00"%>" NOWRAP>
             <a href=# onClick="confirmPopupPage(400,780,'../appointment/addappointment.jsp?provider_no=<%=curProvider_no[nProvider]%>&bFirstDisp=<%=true%>&year=<%=strYear%>&month=<%=strMonth%>&day=<%=strDay%>&start_time=<%=(hourCursor>9?(""+hourCursor):("0"+hourCursor))+":"+ (minuteCursor<10?"0":"") +minuteCursor %>&end_time=<%=(hourCursor>9?(""+hourCursor):("0"+hourCursor))+":"+(minuteCursor+depth-1)%>&duration=<%=dateTimeCodeBean.get("duration"+hourmin.toString())%>','<%= dateTimeCodeBean.get("confirm"+hourmin.toString()) %>','<%= allowDay %>','<%= allowWeek %>');return false;"
  title='<%=MyDateFormat.getTimeXX_XXampm(hourCursor +":"+ (minuteCursor<10?"0":"")+minuteCursor)%> - <%=MyDateFormat.getTimeXX_XXampm(hourCursor +":"+((minuteCursor+depth-1)<10?"0":"")+(minuteCursor+depth-1))%>' class="adhour">
             <%=(hourCursor<10?"0":"") +hourCursor+ ":"%><%=(minuteCursor<10?"0":"")+minuteCursor%>&nbsp;</a></td>
            <td class="hourmin" width='1%' <%=dateTimeCodeBean.get("color"+hourmin.toString())!=null?("bgcolor="+dateTimeCodeBean.get("color"+hourmin.toString()) ):""%> title='<%=dateTimeCodeBean.get("description"+hourmin.toString())%>'><font color='<%=(dateTimeCodeBean.get("color"+hourmin.toString())!=null && !dateTimeCodeBean.get("color"+hourmin.toString()).equals(bgcolordef) )?"black":"white" %>'><%=hourmin.toString() %></font></td>
<%
        while (bFirstTimeRs?it.hasNext():true) { //if it's not the first time to parse the standard time, should pass it by
                  appointment = bFirstTimeRs?it.next():appointment;
                  if(OscarProperties.getInstance().getProperty("APPT_ALWAYS_SHOW_LONG_NAME","false").equals("false")) {
                	len = bFirstTimeRs&&!bFirstFirstR?lenLimitedS:lenLimitedL;
                  }
                  
                  iS=Integer.parseInt(String.valueOf(appointment.get("start_time")).substring(0,2));
                  iSm=Integer.parseInt(String.valueOf(appointment.get("start_time")).substring(3,5));
                  iE=Integer.parseInt(String.valueOf(appointment.get("end_time")).substring(0,2));
              iEm=Integer.parseInt(String.valueOf(appointment.get("end_time")).substring(3,5));

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

         	    //get time format: 00:00am/pm
                    //String startTime = (iS>12?("0"+(iS-12)):String.valueOf(appointment.get("start_time")).substring(0,2))+":"+String.valueOf(appointment.get("start_time")).substring(3,5)+am_pm ;
                    //String endTime   = (iE>12?("0"+(iE-12)):String.valueOf(appointment.get("end_time")).substring(0,2))  +":"+String.valueOf(appointment.get("end_time")).substring(3,5)+(iE<12?"am":"pm");

                    int demographic_no = (Integer)appointment.get("demographic_no");

                  //Pull the appointment name from the demographic information if the appointment is attached to a specific demographic.
                  //Otherwise get the name associated with the appointment from the appointment information
                  StringBuilder nameSb = new StringBuilder();
                  if ((demographic_no != 0)&& (demographicDao != null)) {
                        Demographic demo = demographicDao.getDemographic(String.valueOf(demographic_no));
                        nameSb.append(demo.getLastName())
                              .append(",")
                              .append(demo.getFirstName());
                  }
                  else {
                        nameSb.append(String.valueOf(appointment.get("name")));
                  }
                  String name = UtilMisc.toUpperLowerCase(nameSb.toString());

                  paramTickler[0]=String.valueOf(demographic_no);
                  paramTickler[1]=MyDateFormat.getSysDate(strDate); //year+"-"+month+"-"+day;//e.g."2001-02-02";
                  tickler_no = "";
                  tickler_note="";
                      List<Map<String,Object>> ticklerList = oscarSuperManager.find("providerDao", "search_tickler", paramTickler);
                          for (Map tickler : ticklerList) {
                                tickler_no = String.valueOf(tickler.get("tickler_no"));
                                tickler_note = tickler.get("message")==null?tickler_note:tickler_note + "\n" + tickler.get("message");
                  }

                          ver = "";
                  roster = "";
                      List<Map<String,Object>> demoList = oscarSuperManager.find("providerDao", "search_demograph", new Object[] {demographic_no});
                  for (Map demo : demoList) {
                    ver = (String)demo.get("ver");
                    roster = (String)demo.get("roster_status");

                    int intMob = 0;
                    int intDob = 0;

                    mob = String.valueOf(demo.get("month_of_birth"));
                    if(mob.length()>0 && !mob.equals("null"))
                    	intMob = Integer.parseInt(mob);

                    dob = String.valueOf(demo.get("date_of_birth"));
                    if(dob.length()>0 && !dob.equals("null"))
                    	intDob = Integer.parseInt(dob);


                    demBday = mob + "-" + dob;

                    if (roster == null ) { //|| !(roster.equalsIgnoreCase("FS") || roster.equalsIgnoreCase("NR") || roster.equalsIgnoreCase("PL"))) {
                        roster = "";
                    }
                  }
                  study_no = new StringBuffer("");
                  study_link = new StringBuffer("");
				  studyDescription = new StringBuffer("");

				  int numStudy = 0;
                      List<Map<String,Object>> studyList = oscarSuperManager.find("providerDao", "search_studycount", new Object[] {demographic_no});
                          for (Map study : studyList) {
                                  numStudy = ((Long)(study.get(study.keySet().toArray()[0]))).intValue();
                          }
				  if (numStudy == 1) {
                              studyList = oscarSuperManager.find("providerDao", "search_study", new Object[] {demographic_no});
                                  for (Map study : studyList) {
                          study_no = new StringBuffer(String.valueOf(study.get("study_no")));
                          study_link = new StringBuffer(String.valueOf(study.get("study_link")));
                          studyDescription = new StringBuffer(String.valueOf(study.get("description")));
                      }
				  } else if (numStudy > 1) {
                      study_no = new StringBuffer("0");
                      study_link = new StringBuffer("formstudy.jsp");
				      studyDescription = new StringBuffer("Form Studies");
				  }

                          String reason = String.valueOf(appointment.get("reason")).trim();
                  String notes = String.valueOf(appointment.get("notes")).trim();
                  String status = String.valueOf(appointment.get("status")).trim();
          	  String sitename = String.valueOf(appointment.get("location")).trim();
          	  String urgency = (String)appointment.get("urgency");
          	  String apptType = (String)appointment.get("type");

          	  bFirstTimeRs=true;
			    as.setApptStatus(status);

			 //multi-site. if a site have been selected, only display appointment in that site
			 if (!bMultisites || (selectedSite == null && CurrentSiteMap.get(sitename) != null) || sitename.equals(selectedSite)) {
        %>
            <td class="appt" bgcolor='<%=as.getBgColor()%>' rowspan="<%=iRows%>" <%-- =view==0?(len==lenLimitedL?"nowrap":""):"nowrap"--%> nowrap>
			<%
				if (BookingSource.MYOSCAR_SELF_BOOKING.name().equals(appointment.get("bookingSource")))
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
			    if (as.getNextStatus() != null && !as.getNextStatus().equals("")) {
            %>
            <a class="apptStatus" href=# onclick="refreshSameLoc('providercontrol.jsp?appointment_no=<%=appointment.get("appointment_no")%>&provider_no=<%=curProvider_no[nProvider]%>&status=&statusch=<%=as.getNextStatus()%>&year=<%=year%>&month=<%=month%>&day=<%=day%>&view=<%=view==0?"0":("1&curProvider="+request.getParameter("curProvider")+"&curProviderName="+request.getParameter("curProviderName") )%>&displaymode=addstatus&dboperation=updateapptstatus&viewall=<%=request.getParameter("viewall")==null?"0":(request.getParameter("viewall"))%><%=isWeekView?"&viewWeek=1":""%>');" title="<%=as.getTitle()%> " >
            <%
				}
			    if (as.getNextStatus() != null) {
            %>
            <img src="../images/<%=as.getImageName()%>" border="0" height="10" title="<%=as.getTitle()%>"></a>
            <%
                } else {
	                out.print("&nbsp;");
                }

            if(urgency != null && urgency.equals("critical")) {
            %>
            	<img src="../images/warning-icon.png" border="0" width="14" height="14" title="Critical Appointment"/>
            <% } %>
<%--|--%>
        <%
        			if(demographic_no==0) {
        				//MARC
        %>
        	<!--  caisi  -->
        	<% if (tickler_no.compareTo("") != 0) {%>
	        	<caisi:isModuleLoad moduleName="ticklerplus" reverse="true">
        			<a href="#" onClick="popupPage(700,1024, '../tickler/ticklerDemoMain.jsp?demoview=0');return false;" title="<bean:message key="provider.appointmentProviderAdminDay.ticklerMsg"/>: <%=UtilMisc.htmlEscape(tickler_note)%>"><font color="red">!</font></a>
    			</caisi:isModuleLoad>
    			<caisi:isModuleLoad moduleName="ticklerplus">
    				<a href="../ticklerPlus/index.jsp" title="<bean:message key="provider.appointmentProviderAdminDay.ticklerMsg"/>: <%=UtilMisc.htmlEscape(tickler_note)%>"><font color="red">!</font></a>
    			</caisi:isModuleLoad>
    		<%} //end of tickler %>
<a href=# onClick ="popupPage(535,860,'../appointment/appointmentcontrol.jsp?appointment_no=<%=appointment.get("appointment_no")%>&provider_no=<%=curProvider_no[nProvider]%>&year=<%=year%>&month=<%=month%>&day=<%=day%>&start_time=<%=iS+":"+iSm%>&demographic_no=0&displaymode=edit&dboperation=search');return false;" title="<%=iS+":"+(iSm>10?"":"0")+iSm%>-<%=iE+":"+iEm%>
<%=name%>
<bean:message key="provider.appointmentProviderAdminDay.reason"/>: <%=UtilMisc.htmlEscape(reason)%>
<bean:message key="provider.appointmentProviderAdminDay.notes"/>: <%=UtilMisc.htmlEscape(notes)%>" >

            .<%=(view==0&&numAvailProvider!=1)?(name.length()>len?name.substring(0,len).toUpperCase():name.toUpperCase()):name.toUpperCase()%>
            </font></a><!--Inline display of reason -->
        <% if(OscarProperties.getInstance().getProperty("APPT_MULTILINE", "false").equals("true") || OscarProperties.getInstance().getProperty("APPT_THREE_LINE", "true").equals("true")) { %>
	      	<br/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<%if((apptType != null && apptType.length()>0) && (reason != null && reason.length()>0) ) { %>	
			<%=UtilMisc.htmlEscape(apptType)%>&nbsp;|&nbsp;<%=UtilMisc.htmlEscape(reason)%>
		<% } %>
		<%if((apptType != null && apptType.length()>0) && (reason == null || reason.length() == 0) ) { %>	
			<%=UtilMisc.htmlEscape(apptType)%>
		<% } %>
		<%if((apptType == null || apptType.length()==0) && (reason != null && reason.length() > 0) ) { %>	
			<%=UtilMisc.htmlEscape(reason)%>
		<% } %>
		
		<%if(OscarProperties.getInstance().getProperty("APPT_THREE_LINE", "true").equals("true")) { %>
		<br/>
		<% } %>
	<% } %>
	
      <oscar:oscarPropertiesCheck property="SHOW_APPT_REASON_TOOLTIP" value="yes" defaultVal="true"><span class="reason"><bean:message key="provider.appointmentProviderAdminDay.Reason"/>:<%=UtilMisc.htmlEscape(reason)%></span></oscar:oscarPropertiesCheck></td>
        <%
        			} else {
				%>	<% if (tickler_no.compareTo("") != 0) {%>
			        	<caisi:isModuleLoad moduleName="ticklerplus" reverse="true">
                                        <a href="#" onClick="popupPage(700,1024, '../tickler/ticklerDemoMain.jsp?demoview=<%=demographic_no%>');return false;" title="<bean:message key="provider.appointmentProviderAdminDay.ticklerMsg"/>: <%=UtilMisc.htmlEscape(tickler_note)%>"><font color="red">!</font></a>
    					</caisi:isModuleLoad>
    					<caisi:isModuleLoad moduleName="ticklerplus">
		    				<!--  <a href="../Tickler.do?method=filter&filter.client=<%=demographic_no %>" title="<bean:message key="provider.appointmentProviderAdminDay.ticklerMsg"/>: <%=UtilMisc.htmlEscape(tickler_note)%>"><font color="red">!</font></a> -->
    						<a href="#" onClick="popupPage(700,1024, '../Tickler.do?method=filter&filter.client=<%=demographic_no %>');return false;" title="<bean:message key="provider.appointmentProviderAdminDay.ticklerMsg"/>: <%=UtilMisc.htmlEscape(tickler_note)%>"><font color="red">!</font></a>
    					</caisi:isModuleLoad>
					<%} %>

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

		String warning = prevMgr.getWarnings(String.valueOf(demographic_no));
		warning = PreventionManager.checkNames(warning);

		String htmlWarning = "";

		if( !warning.equals("")) {
			  htmlWarning = "<img src=\"../images/stop_sign.png\" height=\"14\" width=\"14\" title=\"" + warning +"\">&nbsp;";
		}

		out.print(htmlWarning);

}
}
%>
<a class="apptLink" href=# onClick ="popupPage(535,860,'../appointment/appointmentcontrol.jsp?appointment_no=<%=appointment.get("appointment_no")%>&provider_no=<%=curProvider_no[nProvider]%>&year=<%=year%>&month=<%=month%>&day=<%=day%>&start_time=<%=iS+":"+iSm%>&demographic_no=<%=demographic_no%>&displaymode=edit&dboperation=search');return false;"  <oscar:oscarPropertiesCheck property="SHOW_APPT_REASON_TOOLTIP" value="yes" defaultVal="true"> title="<%=name%>
&nbsp; reason: <%=UtilMisc.htmlEscape(reason)%>
&nbsp; notes: <%=UtilMisc.htmlEscape(notes)%>"</oscar:oscarPropertiesCheck>   ><%=(view==0)?(name.length()>len?name.substring(0,len):name):name%></a>
<%if(OscarProperties.getInstance().getProperty("APPT_THREE_LINE","false").equals("true")){  %>
	<%if((apptType != null && apptType.length()>0) || (reason != null && reason.length() > 0)) {%>
	<br/>
	<%} %>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<%if((apptType != null && apptType.length()>0) && (reason != null && reason.length()>0) ) { %>	
			<%=UtilMisc.htmlEscape(apptType)%>&nbsp;|&nbsp;<%=UtilMisc.htmlEscape(reason)%>
		<% } %>
		<%if((apptType != null && apptType.length()>0) && (reason == null || reason.length() == 0) ) { %>	
			<%=UtilMisc.htmlEscape(apptType)%>
		<% } %>
		<%if((apptType == null || apptType.length()==0) && (reason != null && reason.length() > 0) ) { %>	
			<%=UtilMisc.htmlEscape(reason)%>
		<% } %>
	<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<% } %>
<% if(len==lenLimitedL || view!=0 || numAvailProvider==1 || OscarProperties.getInstance().getProperty("APPT_ALWAYS_SHOW_LINKS", "false").equals("true") ) {%>

<security:oscarSec roleName="<%=roleName$%>" objectName="_eChart" rights="r">
<oscar:oscarPropertiesCheck property="eform_in_appointment" value="yes">
	<b><a href="#" onclick="popupPage(500,1024,'../eform/efmformslistadd.jsp?parentAjaxId=eforms&demographic_no=<%=demographic_no%>&appointment=<%=appointment.get("appointment_no")%>'); return false;"
		  title="eForms">|e</a></b>
</oscar:oscarPropertiesCheck>
</security:oscarSec>

<!-- doctor code block 3 -->
<% if(bShowEncounterLink && !isWeekView) { %>
<% String  eURL = "../oscarEncounter/IncomingEncounter.do?providerNo="+curUser_no+"&appointmentNo="+appointment.get("appointment_no")+"&demographicNo="+demographic_no+"&curProviderNo="+curProvider_no[nProvider]+"&reason="+URLEncoder.encode(reason)+"&encType="+URLEncoder.encode("face to face encounter with client","UTF-8")+"&userName="+URLEncoder.encode( userfirstname+" "+userlastname)+"&curDate="+curYear+"-"+curMonth+"-"+curDay+"&appointmentDate="+year+"-"+month+"-"+day+"&startTime="+iS+":"+iSm+"&status="+status + "&apptProvider_no=" + curProvider_no[nProvider] + "&providerview=" + curProvider_no[nProvider];%>
<a href=# class="encounterBtn" onClick="popupWithApptNo(710, 1024,'<%=eURL%>','encounter',<%=appointment.get("appointment_no")%>);return false;" title="<bean:message key="global.encounter"/>">
            <%if(OscarProperties.getInstance().getProperty("APPT_THREE_LINE","false").equals("false")){  %>| <%} %>
            <bean:message key="provider.appointmentProviderAdminDay.btnE"/></a>
<% } %>


<%= (bShortcutIntakeForm) ? "| <a href='#' onClick='popupPage(700, 1024, \"formIntake.jsp?demographic_no="+demographic_no+"\")' title='Intake Form'>In</a>" : "" %>

<!--  eyeform open link -->
<% if (oscar.OscarProperties.getInstance().isPropertyActive("new_eyeform_enabled") && !isWeekView) { %>
	| <a href="#" onClick='popupPage(800, 1280, "../eyeform/eyeform.jsp?demographic_no=<%=demographic_no %>&appointment_no=<%=appointment.get("appointment_no")%>");return false;' title="EyeForm">EF</a>
<% } %>

<!-- billing code block -->
<%
	// Get the default service type to use for the billing page. (Service type = Billing form)
	// If the patients provider has a default selected in thier preferences use it. Otherwise use the 
	// global default defined in the properties file. (Note: The preferences for the patients provider
	// are used, not the preference for the active user)
	WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
	Demographic demographic = demographicDao.getDemographic(Integer.toString(demographic_no));
	String billingServiceType = URLEncoder.encode(oscarVariables.getProperty("default_view"));
	List<Map<java.lang.String,java.lang.Object>> providerPreferences = oscarSuperManager.find("providerDao", "search_pref_defaultbill", new Object[] {demographic.getProviderNo()});
	if (providerPreferences.size() > 0) 
	{
		if ( !String.valueOf(providerPreferences.get(0).get("defaultServiceType")).equals( "no" ) )
		{
			billingServiceType = URLEncoder.encode(String.valueOf(providerPreferences.get(0).get("defaultServiceType")));
		}
	}
	
	
	if (!isWeekView) { %>
	<security:oscarSec roleName="<%=roleName$%>" objectName="_billing" rights="r">
	<%
	OscarProperties oscarProps = OscarProperties.getInstance();
	String clinicaid_link = "";
	if(Boolean.parseBoolean(oscarProps.getProperty("clinicaid_billing", "")) && prov.equals("AB")){
		clinicaid_link = "../billing/billingClinicAid.jsp?demographic_no="+demographic_no+
						"&service_start_date="+URLEncoder.encode(strYear+"-"+strMonth+"-"+strDay, "UTF-8")+
						"&appointment_no="+appointment.get("appointment_no")+
						"&appointment_provider_no="+appointment.get("provider_no")+
						"&chart_no="+
						"&appointment_start_time="+appointment.get("start_time");
						
	}
	%>
             <% if(status.indexOf('B')==-1) {
                //java.util.Locale vLocale =(java.util.Locale)session.getAttribute(org.apache.struts.action.Action.LOCALE_KEY);
                if (vLocale.getCountry().equals("BR")) { %>
               <a href=# onClick='popupPage(700,1024, "../oscar/billing/procedimentoRealizado/init.do?appId=<%=appointment.get("appointment_no")%>");return false;' title="Faturamento">|FAT|</a>
             <% } else if(Boolean.parseBoolean(oscarProps.getProperty("clinicaid_billing", "")) && prov.equals("AB")){%>
           	   <a href="<%=clinicaid_link+"&billing_action=create_invoice"%>" target="_blank" title="<bean:message key="global.billing"/>">|<bean:message key="provider.appointmentProviderAdminDay.btnB"/></a>
             <% } else {%>
              <a href=# onClick='popupPage(755,1200, "../billing.do?billRegion=<%=URLEncoder.encode(prov)%>&billForm=<%=billingServiceType%>&hotclick=<%=URLEncoder.encode("")%>&appointment_no=<%=appointment.get("appointment_no")%>&demographic_name=<%=URLEncoder.encode(name)%>&status=<%=status%>&demographic_no=<%=demographic_no%>&providerview=<%=curProvider_no[nProvider]%>&user_no=<%=curUser_no%>&apptProvider_no=<%=curProvider_no[nProvider]%>&appointment_date=<%=year+"-"+month+"-"+day%>&start_time=<%=iS+":"+iSm%>&bNewForm=1");return false;' title="<bean:message key="global.billingtag"/>">|<bean:message key="provider.appointmentProviderAdminDay.btnB"/></a>
             <% } %>
<% } else {%>
	 <%if(caisiBillingPreferenceNotDelete!=null && caisiBillingPreferenceNotDelete.equals("1")) {%>
         <a href=# onClick='onUpdatebill("../billing/CA/ON/billingEditWithApptNo.jsp?billRegion=<%=URLEncoder.encode(prov)%>&billForm=<%=billingServiceType%>&hotclick=<%=URLEncoder.encode("")%>&appointment_no=<%=appointment.get("appointment_no")%>&demographic_name=<%=URLEncoder.encode(name)%>&status=<%=status%>&demographic_no=<%=demographic_no%>&providerview=<%=curProvider_no[nProvider]%>&user_no=<%=curUser_no%>&apptProvider_no=<%=curProvider_no[nProvider]%>&appointment_date=<%=year+"-"+month+"-"+day%>&start_time=<%=iS+":"+iSm%>&bNewForm=1");return false;' title="<bean:message key="global.billingtag"/>">|=<bean:message key="provider.appointmentProviderAdminDay.btnB"/></a>
	     <% } else if(Boolean.parseBoolean(oscarProps.getProperty("clinicaid_billing", "")) && prov.equals("AB")){%>
	     	<% /*<a href="<%=clinicaid_link+"&billing_action=delete_invoice&appointment_number"+appointment.get("appointment_no")%->" target="_blank" title="<bean:message key="global.billing"/>">|-<bean:message key="provider.appointmentProviderAdminDay.btnB"/></a> */%>
	     	<a href="#" title="<bean:message key="global.billing"/>">|-<bean:message key="provider.appointmentProviderAdminDay.btnB"/></a>
     <% } else { %>
    <a href=# onClick='onUnbilled("../billing/CA/<%=prov%>/billingDeleteWithoutNo.jsp?status=<%=status%>&appointment_no=<%=appointment.get("appointment_no")%>");return false;' title="<bean:message key="global.billingtag"/>">|-<bean:message key="provider.appointmentProviderAdminDay.btnB"/></a>
	<% } %>
<%} %>

<!--/security:oscarSec-->
	  </security:oscarSec>
<% } %>
<!-- billing code block -->
<security:oscarSec roleName="<%=roleName$%>" objectName="_masterLink" rights="r">
    <% if (vLocale.getCountry().equals("BR")) {%>
    <a class="masterBtn" href="javascript: function myFunction() {return false; }" onClick="popupWithApptNo(700,1024,'../demographic/demographiccontrol.jsp?demographic_no=<%=demographic_no%>&apptProvider=<%=curProvider_no[nProvider]%>&appointment=<%=appointment.get("appointment_no")%>&displaymode=edit&dboperation=search_detail_ptbr','master',<%=appointment.get("appointment_no")%>)"
    title="<bean:message key="provider.appointmentProviderAdminDay.msgMasterFile"/>">|<bean:message key="provider.appointmentProviderAdminDay.btnM"/></a>
    <%}else{%>
    <a class="masterBtn" href="javascript: function myFunction() {return false; }" onClick="popupWithApptNo(700,1024,'../demographic/demographiccontrol.jsp?demographic_no=<%=demographic_no%>&apptProvider=<%=curProvider_no[nProvider]%>&appointment=<%=appointment.get("appointment_no")%>&displaymode=edit&dboperation=search_detail','master',<%=appointment.get("appointment_no")%>)"
    title="<bean:message key="provider.appointmentProviderAdminDay.msgMasterFile"/>">|<bean:message key="provider.appointmentProviderAdminDay.btnM"/></a>
    <%}%>
</security:oscarSec>
      <% if (!vLocale.getCountry().equals("BR") && !isWeekView) { %>

<!-- doctor code block 4 -->

	  <security:oscarSec roleName="<%=roleName$%>" objectName="_appointment.doctorLink" rights="r">
      <a href=# onClick="popupWithApptNo(700,1027,'../oscarRx/choosePatient.do?providerNo=<%=curUser_no%>&demographicNo=<%=demographic_no%>','rx',<%=appointment.get("appointment_no")%>)" title="<bean:message key="global.prescriptions"/>">|<bean:message key="global.rx"/>
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
	  |<b style="color:#FF0000">$</b>
	  <%}}%>
      <oscar:oscarPropertiesCheck property="SHOW_APPT_REASON" value="yes">| <b><%=reason%></b></oscar:oscarPropertiesCheck>
	  </security:oscarSec>

	  <!-- add one link to caisi Program Management Module -->
	  <caisi:isModuleLoad moduleName="caisi">
                <%-- <a href=# onClick="popupPage(700, 1048,'../PMmodule/ClientManager.do?id=<%=demographic_no%>')" title="Program Management">|P</a>--%>
	  	<a href='../PMmodule/ClientManager.do?id=<%=demographic_no%>' title="Program Management">|P</a>
    </caisi:isModuleLoad>
          <%
     //out.print(monthDay + " " + demBday);
      if(isBirthday(monthDay,demBday)){%>
       	| <img src="../images/cake.gif" height="20" alt="Happy Birthday"/>

      <%}%>

      <%String appointment_no=appointment.get("appointment_no").toString();%>
	  <%@include file="appointmentFormsLinks.jspf" %>

	<oscar:oscarPropertiesCheck property="appt_pregnancy" value="true" defaultVal="false">

		<c:set var="demographicNo" value="<%=demographic_no %>" />
	   <jsp:include page="appointmentPregnancy.jspf" >
	   	<jsp:param value="${demographicNo}" name="demographicNo"/>
	   </jsp:include>

	</oscar:oscarPropertiesCheck>

      <!--Inline display of reason -->
      <oscar:oscarPropertiesCheck property="SHOW_APPT_REASON_TOOLTIP" value="yes" defaultVal="true"><span class="reason"><bean:message key="provider.appointmentProviderAdminDay.Reason"/>:<%=UtilMisc.htmlEscape(reason)%></span></oscar:oscarPropertiesCheck>
      <% } %>
      <% if(OscarProperties.getInstance().getProperty("APPT_MULTILINE", "false").equals("true")) { %>
	      	<br/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<%if((apptType != null && apptType.length()>0) && (reason != null && reason.length()>0) ) { %>	
			<%=UtilMisc.htmlEscape(apptType)%>&nbsp;|&nbsp;<%=UtilMisc.htmlEscape(reason)%>
		<% } %>
		<%if((apptType != null && apptType.length()>0) && (reason == null || reason.length() == 0) ) { %>	
			<%=UtilMisc.htmlEscape(apptType)%>
		<% } %>
		<%if((apptType == null || apptType.length()==0) && (reason != null && reason.length() > 0) ) { %>	
			<%=UtilMisc.htmlEscape(reason)%>
		<% } %>
	<% } %>
<% } else {%>
	<!-- no links -->
	<% if(OscarProperties.getInstance().getProperty("APPT_MULTILINE", "false").equals("true")) { %>
	<br/>&nbsp;
	<% } %>
	
	<%}%>
	

        		</font></td>
        <%
        			}
        			}
        			bFirstFirstR = false;
        			
          	} 
                  
            //out.println("<td width='1'>&nbsp;</td></tr>"); give a grid display
            out.println("<td class='noGrid' width='1'></td></tr>"); //no grid display
          } 
         
				%>

          </table> <!-- end table for each provider schedule display -->
<!-- caisi infirmary view extension add fffffffffff-->
</logic:notEqual>
<!-- caisi infirmary view extension add end fffffffffffffff-->
	
		
		
         </td></tr>
          <tr><td class="infirmaryView" ALIGN="center" BGCOLOR="<%=bColor?"#bfefff":"silver"%>">
<!-- caisi infirmary view extension modify fffffffffffffffffff-->
<logic:notEqual name="infirmaryView_isOscar" value="false">

      <% if (isWeekView) { %>
          <b><a href="providercontrol.jsp?year=<%=year%>&month=<%=month%>&day=<%=day%>&view=0&displaymode=day&dboperation=searchappointmentday"><%=formatDate%></a></b>
      <% } else { %>
          <b><input type='button' value="<bean:message key="provider.appointmentProviderAdminDay.weekLetter"/>" name='weekview' onClick=goWeekView('<%=curProvider_no[nProvider]%>') title="<bean:message key="provider.appointmentProviderAdminDay.weekView"/>" style="color:black">
          <input type='button' value="<bean:message key="provider.appointmentProviderAdminDay.searchLetter"/>" name='searchview' onClick=goSearchView('<%=curProvider_no[nProvider]%>') title="<bean:message key="provider.appointmentProviderAdminDay.searchView"/>" style="color:black">
          <b><input type='radio' name='flipview' onClick="goFilpView('<%=curProvider_no[nProvider]%>')" title="Flip view"  >
          <a href=# onClick="goZoomView('<%=curProvider_no[nProvider]%>','<%=curProviderName[nProvider]%>')" onDblClick="goFilpView('<%=curProvider_no[nProvider]%>')" title="<bean:message key="provider.appointmentProviderAdminDay.zoomView"/>" >
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
<% } // end caseload view %>
        </table>        <!-- end table for the whole schedule row display -->




        </td>
      </tr>

      <tr><td colspan="3">
              <table BORDER="0" CELLPADDING="0" CELLSPACING="0" WIDTH="100%">
                  <tr>
                      <td BGCOLOR="ivory" width="60%">
                          <a href="providercontrol.jsp?year=<%=year%>&month=<%=month%>&day=<%=isWeekView ? (day - 7) : (day - 1)%>&view=<%=view == 0 ? "0" : ("1&curProvider=" + request.getParameter("curProvider") + "&curProviderName=" + request.getParameter("curProviderName"))%>&displaymode=day&dboperation=searchappointmentday<%=isWeekView ? "&provider_no=" + provNum : ""%>">
                              &nbsp;&nbsp;<img src="../images/previous.gif" WIDTH="10" HEIGHT="9" BORDER="0" ALT="<bean:message key="provider.appointmentProviderAdminDay.viewPrevDay"/>" vspace="2"></a>
                          <b><span class="dateAppointment"><% if (isWeekView) {%><bean:message key="provider.appointmentProviderAdminDay.week"/> <%=week%><% } else {%><%=formatDate%><% }%></span></b>
                          <a href="providercontrol.jsp?year=<%=year%>&month=<%=month%>&day=<%=isWeekView ? (day + 7) : (day + 1)%>&view=<%=view == 0 ? "0" : ("1&curProvider=" + request.getParameter("curProvider") + "&curProviderName=" + request.getParameter("curProviderName"))%>&displaymode=day&dboperation=searchappointmentday<%=isWeekView ? "&provider_no=" + provNum : ""%>">
                              <img src="../images/next.gif" WIDTH="10" HEIGHT="9" BORDER="0" ALT="<bean:message key="provider.appointmentProviderAdminDay.viewNextDay"/>" vspace="2">&nbsp;&nbsp;</a>
                          <a id="calendarLinkBottom" href=# onClick ="popupPage(425,430,'../share/CalendarPopup.jsp?urlfrom=../provider/providercontrol.jsp&year=<%=strYear%>&month=<%=strMonth%>&param=<%=URLEncoder.encode("&view=0&displaymode=day&dboperation=searchappointmentday", "UTF-8")%><%=isWeekView ? URLEncoder.encode("&provider_no=" + provNum, "UTF-8") : ""%>')"><bean:message key="global.calendar"/></a></td>
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
<!-- key shortcut hotkey block added by phc -->
<script language="JavaScript">

// popup blocking for the site must be off!
// developed on Windows FF 2, 3 IE 6 Linux FF 1.5
// FF on Mac and Opera on Windows work but will require shift or control with alt and Alpha
// to fire the altKey + Alpha combination - strange

// Modification Notes:
//     event propagation has not been blocked beyond returning false for onkeydown (onkeypress may or may not fire depending)
//     keyevents have not been even remotely standardized so test mods across agents/systems or something will break!
//     use popupOscarRx so that this codeblock can be cut and pasted to appointmentprovideradminmonth.jsp

// Internationalization Notes:
//     underlines should be added to the labels to prompt/remind the user and should correspond to
//     the actual key whose keydown fires, which is also stored in the oscarResources.properties files
//     if you are using the keydown/up event the value stored is the actual key code
//     which, at least with a US keyboard, also is the uppercase utf-8 code, ie A keyCode=65

document.onkeydown=function(e){
	evt = e || window.event;  // window.event is the IE equivalent
	if (evt.altKey) {
		//use (evt.altKey || evt.metaKey) for Mac if you want Apple+A, you will probably want a seperate onkeypress handler in that case to return false to prevent propagation
		switch(evt.keyCode) {
			case <bean:message key="global.adminShortcut"/> : popupOscarRx(700,687,'../admin/admin.jsp');  return false;  //run code for 'A'dmin
			case <bean:message key="global.billingShortcut"/> : popupOscarRx(600,1024,'../billing/CA/<%=prov%>/billingReportCenter.jsp?displaymode=billreport&providerview=<%=curUser_no%>');return false;  //code for 'B'illing
			case <bean:message key="global.calendarShortcut"/> : popupOscarRx(425,430,'../share/CalendarPopup.jsp?urlfrom=../provider/providercontrol.jsp&year=<%=strYear%>&month=<%=strMonth%>&param=<%=URLEncoder.encode("&view=0&displaymode=day&dboperation=searchappointmentday","UTF-8")%>');  return false;  //run code for 'C'alendar
			case <bean:message key="global.edocShortcut"/> : popupOscarRx('700', '1024', '../dms/documentReport.jsp?function=provider&functionid=<%=curUser_no%>&curUser=<%=curUser_no%>', 'edocView');  return false;  //run code for e'D'oc
			case <bean:message key="global.resourcesShortcut"/> : popupOscarRx(550,687,'<%=resourcebaseurl%>'); return false; // code for R'e'sources
 			case <bean:message key="global.helpShortcut"/> : popupOscarRx(600,750,'<%=resourcebaseurl%>');  return false;  //run code for 'H'elp
			case <bean:message key="global.ticklerShortcut"/> : {
				<caisi:isModuleLoad moduleName="ticklerplus" reverse="true">
					popupOscarRx(700,1024,'../tickler/ticklerMain.jsp','<bean:message key="global.tickler"/>') //run code for t'I'ckler
				</caisi:isModuleLoad>
				<caisi:isModuleLoad moduleName="ticklerplus">
					popupOscarRx(700,1024,'../Tickler.do','<bean:message key="global.tickler"/>'); //run code for t'I'ckler+
				</caisi:isModuleLoad>
				return false;
			}
			case <bean:message key="global.labShortcut"/> : popupOscarRx(600,1024,'../dms/inboxManage.do?method=prepareForIndexPage&providerNo=<%=curUser_no%>', '<bean:message key="global.lab"/>');  return false;  //run code for 'L'ab
			case <bean:message key="global.msgShortcut"/> : popupOscarRx(600,1024,'../oscarMessenger/DisplayMessages.do?providerNo=<%=curUser_no%>&userName=<%=URLEncoder.encode(userfirstname+" "+userlastname)%>'); return false;  //run code for 'M'essage
			case <bean:message key="global.monthShortcut"/> : window.open("providercontrol.jsp?year=<%=year%>&month=<%=month%>&day=1&view=<%=view==0?"0":("1&curProvider="+request.getParameter("curProvider")+"&curProviderName="+request.getParameter("curProviderName") )%>&displaymode=month&dboperation=searchappointmentmonth","_self"); return false ;  //run code for Mo'n'th
			case <bean:message key="global.conShortcut"/> : popupOscarRx(625,1024,'../oscarEncounter/IncomingConsultation.do?providerNo=<%=curUser_no%>&userName=<%=URLEncoder.encode(userfirstname+" "+userlastname)%>');  return false;  //run code for c'O'nsultation
			case <bean:message key="global.reportShortcut"/> : popupOscarRx(650,1024,'../report/reportindex.jsp','reportPage');  return false;  //run code for 'R'eports
			case <bean:message key="global.prefShortcut"/> : {
				    <caisi:isModuleLoad moduleName="ticklerplus">
					popupOscarRx(715,680,'providerpreference.jsp?provider_no=<%=curUser_no%>&start_hour=<%=startHour%>&end_hour=<%=endHour%>&every_min=<%=everyMin%>&mygroup_no=<%=mygroupno%>&caisiBillingPreferenceNotDelete=<%=caisiBillingPreferenceNotDelete%>&new_tickler_warning_window=<%=newticklerwarningwindow%>&default_pmm=<%=default_pmm%>'); //run code for tickler+ 'P'references
					return false;
				    </caisi:isModuleLoad>
			            <caisi:isModuleLoad moduleName="ticklerplus" reverse="true">
					popupOscarRx(715,680,'providerpreference.jsp?provider_no=<%=curUser_no%>&start_hour=<%=startHour%>&end_hour=<%=endHour%>&every_min=<%=everyMin%>&mygroup_no=<%=mygroupno%>'); //run code for 'P'references
					return false;
			            </caisi:isModuleLoad>
			}
			case <bean:message key="global.searchShortcut"/> : popupOscarRx(550,687,'../demographic/search.jsp');  return false;  //run code for 'S'earch
			case <bean:message key="global.dayShortcut"/> : window.open("providercontrol.jsp?year=<%=curYear%>&month=<%=curMonth%>&day=<%=curDay%>&view=<%=view==0?"0":("1&curProvider="+request.getParameter("curProvider")+"&curProviderName="+request.getParameter("curProviderName") )%>&displaymode=day&dboperation=searchappointmentday","_self") ;  return false;  //run code for 'T'oday
			case <bean:message key="global.viewShortcut"/> : {
				<% if(request.getParameter("viewall")!=null && request.getParameter("viewall").equals("1") ) { %>
				         review('0');  return false; //scheduled providers 'V'iew
				<% } else {  %>
				         review('1');  return false; //all providers 'V'iew
				<% } %>
			}
			case <bean:message key="global.workflowShortcut"/> : popupOscarRx(700,1024,'../oscarWorkflow/WorkFlowList.jsp','<bean:message key="global.workflow"/>'); return false ; //code for 'W'orkflow
			case <bean:message key="global.myoscarShortcut"/> : popupOscarRx('600', '1024','../phr/PhrMessage.do?method=viewMessages','INDIVOMESSENGER2<%=curUser_no%>')
			default : return;
               }
	}
	if (evt.ctrlKey) {
               switch(evt.keyCode || evt.charCode) {
			case <bean:message key="global.btnLogoutShortcut"/> : window.open('../logout.jsp','_self');  return false;  // 'Q'uit/log out
			default : return;
               }

        }
}

</script>
<!-- end of keycode block -->
<% if (OscarProperties.getInstance().getBooleanProperty("indivica_hc_read_enabled", "true")) { %>
<%@include file="/hcHandler/hcHandler.html" %>
<% } %>
</html:html>

<%!
        public boolean checkRestriction(List<MyGroupAccessRestriction> restrictions, String name) {
                for(MyGroupAccessRestriction restriction:restrictions) {
                        if(restriction.getMyGroupNo().equals(name))
                                return true;
                }
                return false;
        }
%>

