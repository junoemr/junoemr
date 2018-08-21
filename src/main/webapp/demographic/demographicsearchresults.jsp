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
<security:oscarSec roleName="<%=roleName$%>" objectName="_search" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect(request.getContextPath() + "/securityError.jsp?type=_search");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="org.oscarehr.util.MiscUtils"%>
<%@page import="org.oscarehr.util.LoggedInInfo" %>
<%@page import="org.oscarehr.caisi_integrator.ws.CachedProvider"%>
<%@page import="org.oscarehr.caisi_integrator.ws.FacilityIdStringCompositePk"%>
<%@page import="org.oscarehr.PMmodule.caisi_integrator.CaisiIntegratorManager"%>
<%@page import="org.apache.commons.lang.time.DateFormatUtils"%>
<%@page import="org.oscarehr.caisi_integrator.ws.DemographicTransfer"%>
<%@page import="org.oscarehr.ws.rest.to.model.DemographicSearchRequest"%>
<%@page import="org.oscarehr.ws.rest.to.model.DemographicSearchRequest.SEARCHMODE"%>
<%@page import="org.oscarehr.ws.rest.to.model.DemographicSearchRequest.SORTMODE"%>
<%@page import="org.oscarehr.ws.rest.to.model.DemographicSearchRequest.STATUSMODE"%>
<%@page import="org.oscarehr.ws.rest.to.model.DemographicSearchResult"%>
<%@page import="org.oscarehr.caisi_integrator.ws.MatchingDemographicTransferScore"%>
<%@page import="org.oscarehr.casemgmt.service.CaseManagementManager"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/caisi-tag.tld" prefix="caisi" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<%
	OscarProperties oscarProps = OscarProperties.getInstance();
	Boolean isMobileOptimized = session.getAttribute("mobileOptimized") != null;

     LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
     
 	GregorianCalendar now=new GregorianCalendar();
 	int curYear = now.get(Calendar.YEAR);
 	int curMonth = (now.get(Calendar.MONTH)+1);
 	int curDay = now.get(Calendar.DAY_OF_MONTH);
 	String curProvider_no = (String) session.getAttribute("user");

%>

<%@ page import="java.util.*, java.net.URLEncoder, oscar.*" errorPage="errorpage.jsp" %>
<%@page import="org.oscarehr.util.SpringUtils" %>
<%@page import="org.oscarehr.common.dao.DemographicDao" %>
<%@ page import="oscar.oscarDemographic.data.DemographicMerged" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>

<jsp:useBean id="providerBean" class="java.util.Properties"	scope="session" />

<%
	String strOffset = "0";
	String strLimit = "18";
	String deepColor = "#CCCCFF", weakColor = "#EEEEFF";

	if (request.getParameter("limit1") != null)
	{
		strOffset = request.getParameter("limit1");
	}
	if (request.getParameter("limit2") != null)
	{
		strLimit = request.getParameter("limit2");
	}

	int offset = Integer.parseInt(strOffset);
	int limit = Integer.parseInt(strLimit);

	String displayMode = request.getParameter("displaymode");
	String dboperation = request.getParameter("dboperation");
	String keyword = request.getParameter("keyword");
	String orderBy = request.getParameter("orderby");
	String ptStatus = request.getParameter("ptstatus");

	java.util.ResourceBundle oscarResources = ResourceBundle.getBundle("oscarResources", request.getLocale());
    String noteReason = oscarResources.getString("oscarEncounter.noteReason.TelProgress");

	if (OscarProperties.getInstance().getProperty("disableTelProgressNoteTitleInEncouterNotes") != null 
			&& OscarProperties.getInstance().getProperty("disableTelProgressNoteTitleInEncouterNotes").equals("yes")) {
		noteReason = "";
	}

%>
<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<script type="text/javascript" src="<c:out value="${ctx}/share/javascript/Oscar.js"/>"></script>
<title><bean:message key="demographic.demographicsearchresults.title" /></title>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
   <script>
     jQuery.noConflict();
   </script>
<oscar:customInterface section="demoSearch"/>

<% if (isMobileOptimized) { %>
   <meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no, width=device-width" />
   <link rel="stylesheet" type="text/css" href="../mobile/searchdemographicstyle.css">
<% } else { %>
   <link rel="stylesheet" type="text/css" media="all" href="../demographic/searchdemographicstyle.css"  />
   <link rel="stylesheet" type="text/css" media="all" href="../share/css/searchBox.css"  />
   <style type="text/css"> .deep { background-color: <%= deepColor %>; } .weak { background-color: <%= weakColor %>; } </style>
<% } %>

<%
	String ptstatus = request.getParameter("ptstatus") == null ? "active" : request.getParameter("ptstatus");

	OscarProperties props = OscarProperties.getInstance();
%>

<script language="JavaScript">

	function showHideItem(id) {
		if (document.getElementById(id).style.display == 'inline')
			document.getElementById(id).style.display = 'none';
		else
			document.getElementById(id).style.display = 'inline';
	}
	function setfocus() {
		document.titlesearch.keyword.focus();
		document.titlesearch.keyword.select();
	}

	function checkTypeIn() {
		var dob = document.titlesearch.keyword;
		typeInOK = true;

		if (dob.value.indexOf('%b610054') == 0 && dob.value.length > 18) {
			document.titlesearch.keyword.value = dob.value.substring(8, 18);
			document.titlesearch.search_mode[4].checked = true;
		}
		if (document.titlesearch.search_mode[0].checked) {
			var keyword = document.titlesearch.keyword.value;
			var keywordLowerCase = keyword.toLowerCase();
			document.titlesearch.keyword.value = keywordLowerCase;
		}
		if (document.titlesearch.search_mode[2].checked) {
			if (dob.value.length == 8) {
				dob.value = dob.value.substring(0, 4) + "-"
						+ dob.value.substring(4, 6) + "-"
						+ dob.value.substring(6, 8);
			}
			if (dob.value.length != 10) {
				alert("<bean:message key="demographic.search.msgWrongDOB"/>");
				typeInOK = false;
			}

			return typeInOK;
		} else {
			return true;
		}
	}

	function popup(vheight, vwidth, varpage) {
		var page = varpage;
		windowprops = "height="
				+ vheight
				+ ",width="
				+ vwidth
				+ ",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=0,screenY=0,top=0,left=0";
		var popup = window.open(varpage, "<bean:message key="global.oscarRx"/>_________________$tag________________________________demosearch",	windowprops);
		if (popup != null) {
			if (popup.opener == null) {
				popup.opener = self;
			}
			popup.focus();
		}
	}

	function popupEChart(vheight,vwidth,varpage) { //open a new popup window
		  var page = "" + varpage;
		  windowprops = "height="+vheight+",width="+vwidth+",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=50,screenY=50,top=20,left=20";
		  var popup=window.open(page, "encounter", windowprops);
		  if (popup != null) {
		    if (popup.opener == null) {
		      popup.opener = self;
		    }
		    popup.focus();
		  }
		}
</SCRIPT>
</head>
	

<body onLoad="setfocus()" topmargin="0" leftmargin="0" rightmargin="0" bottommargin="0">

<div id="demographicSearch">
    <a href="#" onclick="showHideItem('demographicSearch');" id="cancelButton" class="leftButton top"> <bean:message key="global.btnCancel" /> </a>
	<%@ include file="zdemographicfulltitlesearch.jsp"%>
</div>


<div id="searchResults">
<a href="#" onclick="showHideItem('demographicSearch');" id="searchPopUpButton" class="rightButton top">Search</a>
<br>
<i><bean:message key="demographic.demographicsearchresults.msgSearchKeys" /></i> : <%=request.getParameter("keyword")%>

	<%
		String basicQueryString = "?fromMessenger=" + fromMessenger +
			"&keyword=" + StringEscapeUtils.escapeHtml(request.getParameter("keyword")) +
			"&ptstatus=" + request.getParameter("ptstatus") +
			"&displaymode=" + request.getParameter("displaymode") +
			"&search_mode=" + request.getParameter("search_mode") +
			"&dboperation=" + request.getParameter("dboperation") +
			"&limit1=0" +
			"&limit2=" + strLimit;
	%>

    <table>
        <tr class="tableHeadings deep">
		<td class="demoIdSearch">
			<a href="demographiccontrol.jsp<%=basicQueryString%>&orderby=demographic_no">
				<bean:message key="demographic.demographicsearchresults.btnDemoNo" />
			</a>
        </td>
		<% if (!fromMessenger) { %>
		<td class="links">
			<bean:message key="demographic.demographicsearchresults.module" />
		</td>
		<% } %>
		<td class="name">
			<a href="demographiccontrol.jsp<%=basicQueryString%>&orderby=last_name">
				<bean:message key="demographic.demographicsearchresults.btnDemoName"/></a>
        </td>
		<td class="chartNo">
			<a href="demographiccontrol.jsp<%=basicQueryString%>&orderby=chart_no">
				<bean:message key="demographic.demographicsearchresults.btnChart" />
			</a>
        </td>
		<td class="sex">
			<a href="demographiccontrol.jsp<%=basicQueryString%>&orderby=sex">
				<bean:message key="demographic.demographicsearchresults.btnSex" />
			</a>
        </td>
		<td class="dob">
			<a href="demographiccontrol.jsp<%=basicQueryString%>&orderby=dob">
				<bean:message key="demographic.demographicsearchresults.btnDOB" />
				<span class="dateFormat"><bean:message key="demographic.demographicsearchresults.btnDOBFormat" /></span>
			</a>
        </td>
        <td class="hin">
			<a href="demographiccontrol.jsp<%=basicQueryString%>&orderby=hin">
				<bean:message key="demographic.demographicsearchresults.btnHIN" />
			</a>
        </td>
		<td class="doctor">
			<a href="demographiccontrol.jsp<%=basicQueryString%>&orderby=provider_name">
				<bean:message key="demographic.demographicsearchresults.btnDoctor" />
			</a>
        </td>
        <td class="rosterStatus">
			<a href="demographiccontrol.jsp<%=basicQueryString%>&orderby=roster_status">
				<bean:message key="demographic.demographicsearchresults.btnRosSta" />
			</a>
        </td>
		<td class="patientStatus">
			<a href="demographiccontrol.jsp<%=basicQueryString%>&orderby=patient_status">
				<bean:message key="demographic.demographicsearchresults.btnPatSta" />
			</a>
        </td>
        <td class="phone">
			<a href="demographiccontrol.jsp<%=basicQueryString%>&orderby=phone">
				<bean:message key="demographic.demographicsearchresults.btnPhone" />
			</a>
        </td>
	</tr>
	<%
	DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographicDao");
	CaseManagementManager caseManagementManager=(CaseManagementManager)SpringUtils.getBean("caseManagementManager");
	String providerNo = loggedInInfo.getLoggedInProviderNo();
	boolean outOfDomain = true;
	if(OscarProperties.getInstance().getProperty("ModuleNames","").indexOf("Caisi") != -1) {
		if(!"true".equals(OscarProperties.getInstance().getProperty("pmm.client.search.outside.of.domain.enabled","true"))) {
			outOfDomain=false;
		}
		if(request.getParameter("outofdomain")!=null && request.getParameter("outofdomain").equals("true")) {
			outOfDomain=true;
		}
	}
	
	

	if (searchMode == null)
		searchMode = "search_name";
	if (orderBy == null)
		orderBy = "last_name";
	
	
	List<DemographicSearchResult> demoList = null;
	
	demoList = doSearch(demographicDao,loggedInInfo, searchMode, ptstatus, keyword, limit, offset, orderBy, outOfDomain);
	
	
	boolean toggleLine = false;
	boolean firstPageShowIntegratedResults = request.getParameter("firstPageShowIntegratedResults") != null && "true".equals(request.getParameter("firstPageShowIntegratedResults"));
	int nItems=0;

	if(demoList==null) {
		out.println("Your Search Returned No Results!!!");
	} 
	else {
		@SuppressWarnings("unchecked")
		  List<MatchingDemographicTransferScore> integratorSearchResults=(List<MatchingDemographicTransferScore>)request.getAttribute("integratorSearchResults");
		  
		  
		  if (integratorSearchResults!=null) {
		      firstPageShowIntegratedResults = true;
			  for (MatchingDemographicTransferScore matchingDemographicTransferScore : integratorSearchResults) {
			      if( isLocal(matchingDemographicTransferScore, demoList)) {
				  	continue;
			      }
				  
				  DemographicTransfer demographicTransfer=matchingDemographicTransferScore.getDemographicTransfer();
		%>
				   <tr class="<%=toggleLine?"even":"odd"%>">
				   <td class="demoIdSearch">
				   	<a title="Import" href="#"  onclick="popup(700,1027,'../appointment/copyRemoteDemographic.jsp?remoteFacilityId=<%=demographicTransfer.getIntegratorFacilityId()%>&demographic_no=<%=String.valueOf(demographicTransfer.getCaisiDemographicId())%>&originalPage=../demographic/demographiceditdemographic.jsp&provider_no=<%=curProvider_no%>')" >Import</a></td>
				   <td class="links">Remote</td>
				   <td class="name"><%=Misc.toUpperLowerCase(demographicTransfer.getLastName())%>, <%=Misc.toUpperLowerCase(demographicTransfer.getFirstName())%></td>
				   <td class="chartNo"></td>
				   <td class="sex"><%=demographicTransfer.getGender()%></td>
				   <td class="dob"><%=demographicTransfer.getBirthDate() != null ?  DateFormatUtils.ISO_DATE_FORMAT.format(demographicTransfer.getBirthDate()) : ""%></td>
				   <td class="hin"><%=demographicTransfer.getHin()%></td>
				   <td class="doctor">
				   
		<% 
		   		FacilityIdStringCompositePk providerPk=new FacilityIdStringCompositePk();
		   		providerPk.setIntegratorFacilityId(demographicTransfer.getIntegratorFacilityId());
		   		providerPk.setCaisiItemId(demographicTransfer.getCaisiProviderId());
		   		CachedProvider cachedProvider=CaisiIntegratorManager.getProvider(loggedInInfo, loggedInInfo.getCurrentFacility(), providerPk);
		   		MiscUtils.getLogger().debug("Cached provider, pk="+providerPk.getIntegratorFacilityId()+","+providerPk.getCaisiItemId()+", cachedProvider="+cachedProvider);
		   		
		   		String providerName="";
		   		
		   		if (cachedProvider!=null)
		   		{
		   			providerName=cachedProvider.getLastName()+", "+cachedProvider.getFirstName();
		   		}
		%>
		        	<%=providerName%>
					</td>
					<td class="rosterStatus"></td>
					<td class="patientStatus"></td>
					<td class="phone"><%=demographicTransfer.getPhone1()%></td>
				</tr>
		<%	  
					toggleLine = !toggleLine;
					nItems++;
				}
		 	}
		
		

		DemographicMerged dmDAO = new DemographicMerged();

		for(DemographicSearchResult demo : demoList) {

			String dem_no = demo.getDemographicNo().toString();
			String head = dmDAO.getHead(dem_no);

%>
	<tr class="<%=toggleLine?"even":"odd"%>">
	<td class="demoIdSearch">

	<%

		if (fromMessenger) {
	%>
		<a href="demographiccontrol.jsp?keyword=<%=StringEscapeUtils.escapeJavaScript(Misc.toUpperLowerCase(demo.getLastName()+", "+demo.getFirstName()))%>&demographic_no=<%= dem_no %>&displaymode=linkMsg2Demo&dboperation=search_detail" ><%=demo.getDemographicNo()%></a></td>
	<%	
		} else { 
	%>
		<a title="Master Demographic File" href="#"  onclick="popup(700,1027,'demographiccontrol.jsp?demographic_no=<%=head%>&displaymode=edit&dboperation=search_detail')" ><%=dem_no%></a></td>
	
		<!-- Rights -->
		<td class="links"><security:oscarSec roleName="<%=roleName$%>"
			objectName="_eChart" rights="r">
			<a class="encounterBtn" title="Encounter" href="#"
				onclick="popupEChart(710,1024,'<c:out value="${ctx}"/>/oscarEncounter/IncomingEncounter.do?providerNo=<%=curProvider_no%>&appointmentNo=&demographicNo=<%=dem_no%>&curProviderNo=&reason=<%=URLEncoder.encode(noteReason)%>&encType=&curDate=<%=""+curYear%>-<%=""+curMonth%>-<%=""+curDay%>&appointmentDate=&startTime=&status=');return false;">E</a>
		</security:oscarSec> <!-- Rights --> <security:oscarSec roleName="<%=roleName$%>"
			objectName="_rx" rights="r">
			<a class="rxBtn" title="Prescriptions" href="#" onclick="popup(700,1027,'../oscarRx/choosePatient.do?providerNo=<%=demo.getProviderNo()%>&demographicNo=<%=dem_no%>')">Rx</a>
		</security:oscarSec></td>

	<%	
		}
		if (OscarProperties.getInstance().isPropertyActive("new_eyeform_enabled")) { 
	%>
		<security:oscarSec roleName="<%=roleName$%>" objectName="_eChart" rights="r">
			<a title="Eyeform" href="#" onclick="popup(800, 1280, '../eyeform/eyeform.jsp?demographic_no=<%=dem_no %>&reason=')">EF</a>
		</security:oscarSec>
	<% 
		} 
	%>
		<caisi:isModuleLoad moduleName="caisi">
		<td class="name"><a href="#" onclick="location.href='<%= request.getContextPath() %>/PMmodule/ClientManager.do?id=<%=dem_no%>'"><%=Misc.toUpperLowerCase(demo.getLastName())%>, <%=Misc.toUpperLowerCase(demo.getFirstName())%></a></td>
		</caisi:isModuleLoad>
		<caisi:isModuleLoad moduleName="caisi" reverse="true">
		<td class="name"><%=Misc.toUpperLowerCase(demo.getLastName())%>, <%=Misc.toUpperLowerCase(demo.getFirstName())%></td>
		</caisi:isModuleLoad>
		<td class="chartNo"><%=demo.getChartNo()==null||demo.getChartNo().equals("")?"&nbsp;":demo.getChartNo()%></td>
		<td class="sex"><%=demo.getSex()%></td>
		<td class="dob"><%=demo.getFormattedDOB()%></td>
		<td class="hin"><%=StringUtils.trimToEmpty(demo.getHin())%></td>
		<td class="doctor"><%=Misc.getShortStr(providerBean.getProperty(demo.getProviderNo() == null ? "" : demo.getProviderNo()),"_",12 )%></td>
		<td class="rosterStatus"><%=demo.getRosterStatus()==null||demo.getRosterStatus().equals("")?"&nbsp;":demo.getRosterStatus()%></td>
		<td class="patientStatus"><%=demo.getPatientStatus()==null||demo.getPatientStatus().equals("")?"&nbsp;":demo.getPatientStatus()%></td>
		<td class="phone"><%=demo.getPhone()==null||demo.getPhone().equals("")?"&nbsp;":(demo.getPhone().length()==10?(demo.getPhone().substring(0,3)+"-"+demo.getPhone().substring(3)):demo.getPhone())%></td>
	</tr>
	<%
		
	toggleLine = !toggleLine;
	nItems++; //to calculate if it is the end of records
		}
	}
%>
</table>
<%

  
  int nLastPage=0,nNextPage=0;
  nNextPage=Integer.parseInt(strLimit)+Integer.parseInt(strOffset);
  nLastPage=Integer.parseInt(strOffset)-Integer.parseInt(strLimit);
  if(nLastPage>=0) {
%> 
	<a href="demographiccontrol.jsp?keyword=<%=URLEncoder.encode(keyword,"UTF-8")%>&search_mode=<%=searchMode%>&displaymode=<%=displayMode%>&dboperation=<%=dboperation%>&orderby=<%=orderBy%>&limit1=<%=nLastPage%>&limit2=<%=strLimit%>&ptstatus=<%=ptStatus%>&firstPageShowIntegratedResults=<%=firstPageShowIntegratedResults%><%=nLastPage==0 && firstPageShowIntegratedResults?"&includeIntegratedResults=true":""%>">
	<bean:message key="demographic.demographicsearchresults.btnLastPage" /></a> <%
  }
  if(nItems>=Integer.parseInt(strLimit)) {
      if (nLastPage>=0) {
	%> | <%    } %> 
	<a href="demographiccontrol.jsp?keyword=<%=URLEncoder.encode(keyword,"UTF-8")%>&search_mode=<%=searchMode%>&displaymode=<%=displayMode%>&dboperation=<%=dboperation%>&orderby=<%=orderBy%>&limit1=<%=nNextPage%>&limit2=<%=strLimit%>&ptstatus=<%=ptStatus%>&firstPageShowIntegratedResults=<%=firstPageShowIntegratedResults%>">
	<bean:message key="demographic.demographicsearchresults.btnNextPage" /></a>
<%
}
%>
<br> 
<div class="createNew">
<a href="demographicaddarecordhtm.jsp?search_mode=<%=searchMode%>&keyword=<%=StringEscapeUtils.escapeHtml(keyWord)%>" title="<bean:message key="demographic.search.btnCreateNewTitle" />">
<bean:message key="demographic.search.btnCreateNew" />
</a>
	<% if (!oscarProps.isPropertyActive("hide_quickform")) { %>
		<br/>
		<a href="demographicaddrecordcustom.jsp"><bean:message
				key="demographic.search.btnQuickCreateNew" /></a>
	<% } %>
</div>

<div class="goBackToSchedule">
<a href="../provider/providercontrol.jsp" title="<bean:message key="demographic.search.btnReturnToSchedule" />">
<bean:message key="demographic.search.btnReturnToSchedule" />
</a>
</div>


</div>

</body>
</html:html>
<%!

Boolean isLocal(MatchingDemographicTransferScore matchingDemographicTransferScore, List<DemographicSearchResult> demoList) {
    String hin = matchingDemographicTransferScore.getDemographicTransfer().getHin(); 
    for( DemographicSearchResult demo : demoList ) {
		
		if( hin != null && hin.equals(demo.getHin()) ) {
		    return true;
		}
    }
    
    return false;
    
}

List<DemographicSearchResult> doSearch(DemographicDao demographicDao,LoggedInInfo loggedInInfo, String searchMode, String searchStatus, String searchKeyword, int limit, int offset, String orderBy, boolean outOfDomain) {
	List<DemographicSearchResult> demoList = null;

	DemographicSearchRequest searchRequest = new DemographicSearchRequest();

	searchRequest.setOutOfDomain(outOfDomain);
	searchRequest.setKeyword(searchKeyword);

	// Set Status Mode
	if (("active").equals(searchStatus))
	{
		searchRequest.setStatusMode(STATUSMODE.active);
	}
	else if (("inactive").equals(searchStatus))
	{
		searchRequest.setStatusMode(STATUSMODE.inactive);
	}
	else
	{
	    searchRequest.setStatusMode(STATUSMODE.all);
	}

	// Set Search Mode
	if ("search_name".equals(searchMode))
	{
		searchRequest.setMode(SEARCHMODE.Name);
	}
	else if ("search_phone".equals(searchMode))
	{
		searchRequest.setMode(SEARCHMODE.Phone);
	}
	else if ("search_dob".equals(searchMode))
	{
		searchRequest.setMode(SEARCHMODE.DOB);
	}
	else if ("search_address".equals(searchMode))
	{
		searchRequest.setMode(SEARCHMODE.Address);
	}
	else if ("search_hin".equals(searchMode))
	{
		searchRequest.setMode(SEARCHMODE.HIN);
	}
	else if ("search_chart_no".equals(searchMode))
	{
		searchRequest.setMode(SEARCHMODE.ChartNo);
	}
	else if ("search_demographic_no".equals(searchMode))
	{
		searchRequest.setMode(SEARCHMODE.DemographicNo);
	}
	else
	{
	    MiscUtils.getLogger().error("Invalid search mode set [" + searchMode + "], defaulting to name");
	    searchRequest.setMode(SEARCHMODE.Name);
	}

	// Set Order By
	if ("demographic_no".equals(orderBy))
	{
		searchRequest.setSortMode(SORTMODE.DemographicNo);
	}
	else if ("last_name".equals(orderBy))
	{
		searchRequest.setSortMode(SORTMODE.Name);
	}
	else if ("chart_no".equals(orderBy))
	{
		searchRequest.setSortMode(SORTMODE.ChartNo);
	}
	else if ("dob".equals(orderBy))
	{
		searchRequest.setSortMode(SORTMODE.DOB);
	}
	else if ("sex".equals(orderBy))
	{
		searchRequest.setSortMode(SORTMODE.Sex);
	}
	else if ("patient_status".equals(orderBy))
	{
		searchRequest.setSortMode(SORTMODE.PatientStatus);
	}
	else if ("roster_status".equals(orderBy))
	{
		searchRequest.setSortMode(SORTMODE.RosterStatus);
	}
	else if ("phone".equals(orderBy))
	{
		searchRequest.setSortMode(SORTMODE.Phone);
	}
	else if ("provider_name".equals(orderBy))
	{
		searchRequest.setSortMode(SORTMODE.ProviderName);
	}
	else if ("hin".equals(orderBy))
	{
	    searchRequest.setSortMode(SORTMODE.HIN);
	}
	else
	{
	    searchRequest.setSortMode(SORTMODE.Name);
	}

	demoList = demographicDao.searchPatients(loggedInInfo, searchRequest, offset, limit);
	return demoList;
}
%>
