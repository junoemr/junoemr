<%--

    Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
    CloudPractice Inc.
    Victoria, British Columbia
    Canada

--%>

<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>

<%@ include file="/casemgmt/taglibs.jsp" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page errorPage="/casemgmt/error.jsp" %>
<%@ page import="java.util.Enumeration, org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.oscarehr.casemgmt.common.Colour" %>
<%@ page import="org.oscarehr.casemgmt.web.formbeans.*, org.oscarehr.casemgmt.model.CaseManagementNote" %>
<%@ page import="org.oscarehr.common.dao.UserPropertyDAO, oscar.OscarProperties" %>
<%@ page import="org.oscarehr.common.model.UserProperty" %>
<%@ page import="org.oscarehr.eform.dao.EFormDao" %>
<%@ page import="org.oscarehr.provider.dao.ProviderDataDao" %>
<%@ page import="org.oscarehr.provider.model.ProviderData" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.util.LoggedInInfo" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="oscar.util.StringUtils" %>
<%@ page import="org.oscarehr.sharingcenter.SharingCenterUtil" %>
<%@ page import="java.util.UUID"%>

<jsp:useBean id="junoEncounterForm" scope="request"
			 type="org.oscarehr.casemgmt.web.formbeans.JunoEncounterFormBean"/>

<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request"/>

<fmt:formatDate value="${junoEncounterForm.pageData.encounterNoteHideBeforeDate}"
			   pattern="yyyy-MM-dd'T'HH:mm"
			   var="encounterNoteHideBeforeDateFormatted"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html:html locale="true">
	<head>
		<link rel="stylesheet" href="<c:out value="${ctx}"/>/css/casemgmt.css" type="text/css">
		<link rel="stylesheet" href="<c:out value="${ctx}"/>/js/jquery_css/smoothness/jquery-ui-1.10.2.custom.min.css" type="text/css">
		<link rel="stylesheet" href="<c:out value="${ctx}"/>/oscarEncounter/encounterStyles.css"
			  type="text/css">
		<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}"/>/css/print.css"
			  media="print">

		<script src="<c:out value="${ctx}/js/jquery-1.7.1.min.js"/>"></script>
		<script src="<c:out value="${ctx}/js/jquery-ui-1.10.2.custom.min.js"/>"></script>
		<script src="<c:out value="${ctx}/share/documentUploader/jquery.tmpl.min.js"/>"></script>
		<script language="javascript">
			jQuery.noConflict();
		</script>

		<script src="<c:out value="${ctx}"/>/share/javascript/prototype.js"
				type="text/javascript"></script>
		<script src="<c:out value="${ctx}"/>/share/javascript/scriptaculous.js"
				type="text/javascript"></script>
		<script src="<c:out value="${ctx}"/>/library/moment.js" type="text/javascript"></script>

		<script type="text/javascript"
				src="<c:out value="${ctx}"/>/js/messenger/messenger.js"></script>
		<script type="text/javascript"
				src="<c:out value="${ctx}"/>/js/messenger/messenger-theme-future.js"></script>
		<link rel="stylesheet" type="text/css"
			  href="<c:out value="${ctx}"/>/js/messenger/messenger.css"></link>
		<link rel="stylesheet" type="text/css"
			  href="<c:out value="${ctx}"/>/js/messenger/messenger-theme-future.css"></link>

		<%-- for popup menu of forms --%>
		<script src="<c:out value="${ctx}"/>/share/javascript/popupmenu.js"
				type="text/javascript"></script>
		<script src="<c:out value="${ctx}"/>/share/javascript/menutility.js"
				type="text/javascript"></script>

		<!-- library for rounded elements -->
		<link rel="stylesheet" type="text/css"
			  href="<c:out value="${ctx}/share/css/niftyCorners.css" />">
		<script type="text/javascript"
				src="<c:out value="${ctx}/share/javascript/nifty.js"/>"></script>

		<!-- calendar stylesheet -->
		<link rel="stylesheet" type="text/css" media="all"
			  href="<c:out value="${ctx}"/>/share/calendar/calendar.css" title="win2k-cold-1">

		<!-- main calendar program -->
		<script type="text/javascript"
				src="<c:out value="${ctx}"/>/share/calendar/calendar.js"></script>

		<!-- language for the calendar -->
		<script type="text/javascript"
				src="<c:out value="${ctx}"/>/share/calendar/lang/<bean:message key="global.javascript.calendar"/>"></script>

		<!-- the following script defines the Calendar.setup helper function, which makes adding a calendar a matter of 1 or 2 lines of code. -->
		<script type="text/javascript"
				src="<c:out value="${ctx}"/>/share/calendar/calendar-setup.js"></script>

		<!-- js window size utility funcs since prototype's funcs are buggy in ie6 -->
		<script type="text/javascript"
				src="<c:out value="${ctx}/share/javascript/screen.js"/>"></script>

		<!-- scriptaculous based select box -->
		<script type="text/javascript"
				src="<c:out value="${ctx}/share/javascript/select.js"/>"></script>

		<!-- phr popups -->
		<script type="text/javascript" src="<c:out value="${ctx}/phr/phr.js"/>"></script>

		<script type="text/javascript"
				src="<c:out value="${ctx}/casemgmt/EncounterTimer.js"/>"></script>
		<script type="text/javascript" src="<c:out value="${ctx}"/>/jspspellcheck/spellcheck-caller.js"></script>

		<script type="text/javascript">



			<%-- ============================================================================== --%>
			<%-- Transfer data from Java to Javascript                                          --%>
			<%-- ============================================================================== --%>

			function getAppointmentNo()
			{
				<c:if test="${not empty junoEncounterForm.pageData.appointmentNo}">
				var appointmentNo = parseInt("<spring:escapeBody htmlEscape='false' javaScriptEscape='true'>${junoEncounterForm.pageData.appointmentNo}</spring:escapeBody>");
				</c:if>
				<c:if test="${empty junoEncounterForm.pageData.appointmentNo}">
				var appointmentNo = null;
				</c:if>

				return appointmentNo;
			}

			function getEncounterTypeArray()
			{
				return [
					"<bean:message key="oscarEncounter.faceToFaceEnc.title"/>",
					"<bean:message key="oscarEncounter.telephoneEnc.title"/>",
					"<bean:message key="oscarEncounter.emailEnc.title"/>",
					"<bean:message key="oscarEncounter.noClientEnc.title"/>"
				]
			}

			// A renamed jQuery.  See the init() method for more info.
			var junoJQuery = null;

			var pageData = {
				contextPath: "<spring:escapeBody htmlEscape='false' javaScriptEscape='true'>${pageContext.request.contextPath}</spring:escapeBody>", //**
				demographicNo: "<spring:escapeBody htmlEscape='false' javaScriptEscape='true'>${junoEncounterForm.pageData.demographicNo}</spring:escapeBody>", //**
				providerNo: "<spring:escapeBody htmlEscape='false' javaScriptEscape='true'>${junoEncounterForm.pageData.providerNo}</spring:escapeBody>", //**
				appointmentNo: getAppointmentNo(),
				encounterNoteHideBeforeDate: "<spring:escapeBody htmlEscape='false' javaScriptEscape='true'>${encounterNoteHideBeforeDateFormatted}</spring:escapeBody>", //**
				defaultEncounterType: "<spring:escapeBody htmlEscape='false' javaScriptEscape='true'>${junoEncounterForm.encType}</spring:escapeBody>", //**
				reason: "<spring:escapeBody htmlEscape='false' javaScriptEscape='true'>${junoEncounterForm.reason}</spring:escapeBody>", //**
				appointmentDate: "<spring:escapeBody htmlEscape='false' javaScriptEscape='true'>${junoEncounterForm.appointmentDate}</spring:escapeBody>", //**
				cmeJs: "<spring:escapeBody htmlEscape='false' javaScriptEscape='true'>${junoEncounterForm.pageData.cmeJs}</spring:escapeBody>",
				billingUrl: "<spring:escapeBody htmlEscape='false' javaScriptEscape='true'>${junoEncounterForm.pageData.billingUrl}</spring:escapeBody>",
				encounterWindowCustomSize: ("<spring:escapeBody htmlEscape='false' javaScriptEscape='true'>${junoEncounterForm.pageData.encounterWindowCustomSize}</spring:escapeBody>" == "true"), //**
				encounterWindowHeight: "<spring:escapeBody htmlEscape='false' javaScriptEscape='true'>${junoEncounterForm.pageData.encounterWindowHeight}</spring:escapeBody>", //**
				encounterWindowWidth: "<spring:escapeBody htmlEscape='false' javaScriptEscape='true'>${junoEncounterForm.pageData.encounterWindowWidth}</spring:escapeBody>", //**
				encounterWindowMaximize: ("<spring:escapeBody htmlEscape='false' javaScriptEscape='true'>${junoEncounterForm.pageData.encounterWindowMaximize}</spring:escapeBody>" == "true"), //**
				imagePresentPlaceholderUrl: "<spring:escapeBody htmlEscape='false' javaScriptEscape='true'>${fn:escapeXml(junoEncounterForm.pageData.imagePresentPlaceholderUrl)}</spring:escapeBody>", //**
				encounterTypeArray: getEncounterTypeArray(),
				editUnsignedMsg: "<bean:message key="oscarEncounter.editUnsignedNote.msg"/>",
				unsavedNoteWarningMsg: "<bean:message key="oscarEncounter.unsavedNoteWarning.msg"/>",
				printDateMsg: "<bean:message key="oscarEncounter.printDate.msg"/>",
				printDateOrderMsg: "<bean:message key="oscarEncounter.printDateOrder.msg"/>",
				notesIncrement: <%= OscarProperties.getNumLoadedNotes(20) %>,
				assignedIssuesTitle: "<bean:message key="oscarEncounter.assignedIssues.title"/>",
				referenceResolvedIssuesTitle: "<bean:message key="oscarEncounter.referenceResolvedIssues.title"/>",
				referenceUnresolvedIssuesTitle: "<bean:message key="oscarEncounter.referenceUnresolvedIssues.title"/>",
				closeWithoutSaveMsg: "<bean:message key="oscarEncounter.closeWithoutSave.msg"/>",
			};


			<%-- ============================================================================== --%>
			<%-- Page State                                                                     --%>
			<%-- ============================================================================== --%>

			// These values are for state shared between the different javascript modules
			var pageState = {
				currentNoteData: null,
				notesOffset: 0,
				notesScrollCheckInterval: null,
				currentAssignedCMIssues: [],
				openWindows: new Object(),
				savingNote: false,
				filterShows: false,
				filterSort: null,
				filteredProviders: [],
				filteredRoles: [],
				filteredIssues: [],
				minDelta: 0.93,
				minMain: null,
				minWin: null,
				openWindows: {},
				reloadSectionTimer: null,
			};


			var eChartUUID = "${junoEncounterForm.pageData.echartUuid}";

			<%@ include file="js/JunoEncounter.js" %>
			var junoEncounter = new Juno.OscarEncounter.JunoEncounter(pageData, pageState);

			<%@ include file="js/JunoEncounter/CppNote.js" %>
			var cppNote = new Juno.OscarEncounter.JunoEncounter.CppNote(pageData, junoEncounter);

			<%@ include file="js/JunoEncounter/EncounterNote.js" %>
			var encounterNote = new Juno.OscarEncounter.JunoEncounter.EncounterNote(pageData, pageState, junoEncounter);

			<%@ include file="js/JunoEncounter/CaseManagementIssue.js" %>
			var caseManagementIssue = new Juno.OscarEncounter.JunoEncounter.CaseManagementIssue(pageData, pageState);

			<%@ include file="js/JunoEncounter/PrintNotes.js" %>
			var printNotes = new Juno.OscarEncounter.JunoEncounter.PrintNotes(pageData, pageState);

			<%@ include file="js/JunoEncounter/NoteFilter.js" %>
			var noteFilter = new Juno.OscarEncounter.JunoEncounter.NoteFilter(pageData, pageState, encounterNote);


			<%-- ============================================================================== --%>
			<%-- API Functions                                                                  --%>
			<%-- ============================================================================== --%>

			// These methods are called by child windows.  Please don't move or rename them.
			function getEChartUUID()
			{
				return eChartUUID;
			}

			function pasteToEncounterNote(txt)
			{
				return encounterNote.pasteToEncounterNote(txt);
			}

			function reloadNav(sectionName)
			{
				junoEncounter.getSectionRemote(sectionName, false, false, getEChartUUID());
			}


			<%-- ============================================================================== --%>
			<%-- Local functions                                                                --%>
			<%-- ============================================================================== --%>

			// This is being left here becuase it is used in a lot of generated links and I don't
			// want to find them all and update them.
			function popupPage(vheight, vwidth, name, varpage)
			{
				return junoEncounter.popupPage(vheight, vwidth, name, varpage);
			}



			<%-- ============================================================================== --%>
			<%-- init                                                                           --%>
			<%-- ============================================================================== --%>

			<%-- this runs on $(document).ready() --%>
			function init()
			{
				// Save a copy of the initially-loaded jQuery before the Ocean Toolbar can replace it.
				// Use this for query calls, especially ones that use plugins, like templating, because they don't get
				// loaded by the Ocean-toolbar version of jQuery.
				junoJQuery = jQuery;

				junoEncounter.monkeyPatches();

				junoEncounter.initOceanToolbar();

				junoEncounter.resizeContent();

				junoEncounter.initNavBarMonitor();

				if(!junoEncounter.configureNifty())
				{
					return;
				}

				junoEncounter.configureCalendar();

				junoEncounter.configureCalculator();

				caseManagementIssue.configureIssueButtons();

				caseManagementIssue.configureIssueAutocompleteCPP();
				caseManagementIssue.configureIssueAutocomplete();

				encounterNote.loadNotes();

				junoEncounter.configureMultiSearchAutocomplete();

				window.onbeforeunload = junoEncounter.onClosing;

				junoEncounter.setWindowSize();
			}

		</script>


		<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}/css/oscarRx.css" />">

		<style type="text/css">

			html {
				overflow-y: scroll;
			}

			.encTypeCombo /* look&feel of scriptaculous select box*/
			{
				margin: 0px; /* 5px 10px 0px;*/
				font-family: Verdana, Geneva, Arial, Helvetica, sans-serif;
				font-size: 9px;
				width: 200px;
				text-align: left;
				vertical-align: middle;
				/*background: #FFFFFF url('<c:out value="${ctx}"/>/images/downarrow_inv.gif') no-repeat right;*/
				background: #FFFFFF; /*url('<c:out value="${ctx}"/>/images/downarrow_inv.gif') no-repeat right;*/
				height: 18px;
				cursor: pointer;
				border: 1px solid #ccc;
				color: #000000;
			}

			div.encounterHeaderContainer {
				float: left;
				width: 100%;
				padding-left: 2px;
				text-align: left;
				font-size: 12px;
				color: ${fn:escapeXml(junoEncounterForm.pageData.inverseUserColour)};
			<%--<bean:write name="junoEncounterForm" property="header.inverseUserColour" />;--%> background-color: <bean:write name="junoEncounterForm" property="pageData.userColour" />;
			}

			div.encounterHeaderContainer span.Header {
				color: <bean:write name="junoEncounterForm" property="pageData.inverseUserColour" />;
				background-color: <bean:write name="junoEncounterForm" property="pageData.userColour" />;
			}

			div.encounterHeaderContainer span.familyDoctorInfo {
				border-bottom: medium solid<bean:write name="junoEncounterForm" property="pageData.familyDoctorColour" />;
			}

		</style>


		<script id="sectionNoteTemplate" type="text/x-jquery-tmpl">
			<%@include file="templates/junoEncounter/sectionNoteTemplate.html"%>
		</script>


		<script id="sectionCppNoteTemplate" type="text/x-jquery-tmpl">
			<%@include file="templates/junoEncounter/sectionCppNoteTemplate.html"%>

		</script>

		<script id="existingIssueTemplate" type="text/x-jquery-tmpl">
			<%@include file="templates/junoEncounter/existingIssueTemplate.html"%>
		</script>

		<script id="encounterNonNoteTemplate" type="text/x-jquery-tmpl">
			<%@include file="templates/junoEncounter/encounterNonNoteTemplate.html"%>
		</script>

		<script id="encounterNoteTemplate" type="text/x-jquery-tmpl">
			<%@include file="templates/junoEncounter/encounterNoteTemplate.html"%>
		</script>


		<html:base/>
		<title><bean:message key="oscarEncounter.Index.title"/> - <oscar:nameage
				demographicNo="<%=request.getParameter(\"demographicNo\")%>"/></title>
	</head>
	<body id="body" style="margin: 0px;">

	<div id="header">
		<div class="encounterHeaderContainer" id="encounterPageData">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td>
						<security:oscarSec roleName="${junoEncounterForm.pageData.roleName}"
										   objectName="_newCasemgmt.doctorName" rights="r">
								<span class="familyDoctorInfo">
									<bean:message key="oscarEncounter.Index.msgMRP"/>
									&nbsp;&nbsp;<bean:write name="junoEncounterForm"
															property="pageData.formattedFamilyDoctorName"/>
								</span>
						</security:oscarSec>
						<span class="Header">
								<%

								%>
								<a href="#"
								   onClick="popupPage(700,1000,
										   '${junoEncounterForm.pageData.windowName}',
										   '${junoEncounterForm.pageData.demographicUrl}'
										   ); return false;"
								   title="<bean:message key="provider.appointmentProviderAdminDay.msgMasterFile"/>"
								>
									${junoEncounterForm.pageData.formattedPatientName}
								</a> ${junoEncounterForm.pageData.formattedPatientInfo}
								<c:if test="${junoEncounterForm.pageData.echartAdditionalPatientInfoEnabled}">
									<bean:write name="junoEncounterForm" property="pageData.patientBirthdate"/>
								</c:if>

								&nbsp;<oscar:phrverification
								demographicNo="${junoEncounterForm.pageData.demographicNo}"><bean:message
								key="phr.verification.link"/></oscar:phrverification> &nbsp;<bean:write
								name="junoEncounterForm" property="pageData.patientPhone"/>
								<span id="encounterHeaderExt"></span>
								<security:oscarSec roleName="${junoEncounterForm.pageData.roleName}"
												   objectName="_newCasemgmt.apptHistory" rights="r">
									<a href="javascript:popupPage(400,850,'ApptHist','<bean:write name="junoEncounterForm" property="pageData.demographicAdditionalInfoUrl" />')"
									   style="font-size: 11px;text-decoration:none;"
									   title="<bean:message key="oscarEncounter.Header.nextApptMsg"/>"><span
											style="margin-left:20px;"><bean:message
											key="oscarEncounter.Header.nextAppt"/>: <oscar:nextAppt
											demographicNo="${junoEncounterForm.pageData.demographicNo}"/></span></a>
								</security:oscarSec>
								&nbsp;&nbsp;

								<c:if test="${junoEncounterForm.pageData.echartAdditionalPatientInfoEnabled}">
									<bean:write name="junoEncounterForm"
												property="pageData.referringDoctorName"/>
									<bean:write name="junoEncounterForm"
												property="pageData.referringDoctorNumber"/>
									&nbsp;&nbsp;
									<c:if test="${junoEncounterForm.pageData.rosterDateEnabled}">
										Referral date:
										<bean:write name="junoEncounterForm"
													property="pageData.rosterDateString"/>
									</c:if>
								</c:if>

								<c:if test="${junoEncounterForm.pageData.incomingRequestorSet}">
									<a href="javascript:void(0)"
									   onClick="popupPage(600,175,'Calculators','${fn:escapeXml(junoEncounterForm.pageData.diseaseListUrl)}'); return false;"><bean:message
											key="oscarEncounter.Header.OntMD"/></a>
								</c:if>
								<c:out value="${junoEncounterForm.pageData.echartLinks}"/>
								&nbsp;&nbsp;

							</span>
					</td>
					<td align=right>
							<span class="HelpAboutLogout">
                                				<c:if test="${junoEncounterForm.pageData.imdHealthEnabled}">
                                    					<script src="../integration/imdHealth/imdHealthUtils.js"></script>
                                    					<a style="font-size:10px;font-style:normal;"  href="javascript:void(0)" onclick="Juno.Integration.iMDHealth.openIMDHealth()">
                                        				<b>Patient Education</b>
                                    					</a> |
                                				</c:if>
                                				<c:if test="${junoEncounterForm.pageData.careConnectEnabled}">
                                    					<a style="font-size:10px;font-style:normal;"  href="javascript:void(0)" onclick="window.open('../integration/careConnect/careConnectForm.jsp?demoNo=<c:out value="${junoEncounterForm.pageData.demographicNo}"/>', 'CareConnectPopup', 'width=1200,height=800');">CareConnect</a> |
                                				</c:if>
								<c:if test="${junoEncounterForm.pageData.linkToOldEncounterPageEnabled}">
									<a style="font-size:10px;font-style:normal;"
									   href="javascript:void(0)"
									   onClick="popupPage(700,1024, 'Encounter', 'IncomingEncounter.do?<c:out value="${requestScope['javax.servlet.forward.query_string']}" />&old_encounter=1'); return false;">
										Open Old Encounter</a> |
								</c:if>
								<oscar:help
										keywords="&Title=Chart+Interface&portal_type%3Alist=Document"
										key="app.top1" style="font-size:10px;font-style:normal;"/>&nbsp;|
								<a style="font-size:10px;font-style:normal;"
								   href="<%=request.getContextPath()%>/oscarEncounter/About.jsp"
								   target="_new"><bean:message key="global.about"/></a>
							</span>
					</td>
				</tr>
			</table>
		</div>
	</div>

	<c:set var="navbarSides" value="${fn:split('right,left', ',')}"></c:set>
	<c:forEach items="${navbarSides}" var="navbarSide">

		<%-- Show different preamble for left and right sides --%>

		<%-- Left --%>
	<c:if test="${navbarSide == 'left'}">
	<c:set var="noteSections" value="${junoEncounterForm.leftNoteSections}"/>
	<div id="leftNavBar" style="display: inline; float: left; width: 20%;">
		</c:if>

			<%-- Right --%>
		<c:if test="${navbarSide == 'right'}">
			<c:set var="noteSections" value="${junoEncounterForm.rightNoteSections}"/>
		<div id="rightNavBar" style="display: inline; float: right; width: 20%; margin-left: -3px;">
			<%
				String demo = request.getParameter("demographicNo");
				String roleName$ = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");

				// Do we want the sharing center?  If so put this in the action form.
				// MARC-HI's Sharing Center
				boolean isSharingCenterEnabled = SharingCenterUtil.isEnabled();

			%>

			<!--dummmy div to force browser to allocate space -->
			<security:oscarSec roleName="<%=roleName$%>" objectName="_newCasemgmt.photo" rights="r">
				<c:choose>
					<c:when test="${junoEncounterForm.pageData.clientImagePresent}">
						<img style="cursor: pointer;" id="ci"
							 src="${fn:escapeXml(junoEncounterForm.pageData.imagePresentPlaceholderUrl)}"
							 alt="id_photo" height="100" title="Click to upload new photo."
							 OnMouseOver="document.getElementById('ci').src='../imageRenderingServlet?source=local_client&clientId=${fn:escapeXml(junoEncounterForm.pageData.demographicNo)}'"
							 OnMouseOut="junoEncounter.delay(5000); window.status='Click to upload new photo'; return true;"
							 onClick="junoEncounter.popupUploadPage('${ctx}/casemgmt/uploadimage.jsp',${fn:escapeXml(junoEncounterForm.pageData.demographicNo)});return false;" />
					</c:when>
					<c:otherwise>
						<img style="cursor: pointer;"
							 src="${fn:escapeXml(junoEncounterForm.pageData.imageMissingPlaceholderUrl)}"
							 alt="No_Id_Photo" height="100" title="Click to upload new photo."
							 OnMouseOver="window.status='Click to upload new photo';return true"
							 onClick="junoEncounter.popupUploadPage('${ctx}/casemgmt/uploadimage.jsp',${fn:escapeXml(junoEncounterForm.pageData.demographicNo)});return false;"/>
					</c:otherwise>
				</c:choose>
			</security:oscarSec>
			<!-- MARC-HI's Sharing Center -->
			<% if (isSharingCenterEnabled)
			{ %>
			<div>
				<button type="button"
						onclick="window.open('${ctx}/sharingcenter/documents/demographicExport.jsp?demographic_no=<%=demo%>');">
					Export Patient Demographic
				</button>
			</div>
			<% } %>
			</c:if>


			<div class="encounterNavBar" id="${navbarSide}ColLoader" style="width: 100%;">
				<%
					// =================================================================================
					// Sidebar display
					// =================================================================================
				%>
				<c:forEach items="${noteSections}" var="sectionName" varStatus="loop">

					<c:set var="section" scope="page"
						   value="${junoEncounterForm.sections[sectionName]}"/>

					<div class="leftBox" id="${sectionName}" style="display: block;">

						<div id='menuTitle${sectionName}'
							 style="width: 10%; float: right; text-align: center;">
							<h3 style="padding:0px; background-color: ${section.colour};">
								<a href="javascript:void(0);"
								   onclick="${section.onClickPlus};return false;"
								   onmouseover="return !junoEncounter.showMenu('${section.menuId}', event);"
								>+</a>
							</h3>
						</div>

						<%-- Popup Menu --%>
						<c:if test="${not empty section.menuId}">
							<c:set var="useColumns" scope="page"
								   value="${fn:length(section.menuItems) > 40}"/>
							<c:set var="menuWidth" scope="page"
								   value="${useColumns ? '250' : '125'}"/>
							<div id='menu${section.menuId}' class='menu'
								 style='width: ${menuWidth}px' onclick='event.cancelBubble = true;'>
								<h3 style='text-align: center'><bean:message
										key="${section.menuHeaderKey}"/>
								</h3>
								<c:forEach items="${section.menuItems}" var="menuItem"
										   varStatus="loop">
									<a href="#"
									   class="menuItem${useColumns && loop.index % 2 == 1 ? 'right' : 'left' }"
									   onmouseover='this.style.color="black"'
									   onmouseout='this.style.color="white"'
									   onclick="${menuItem.onClick}; return false;">
										<c:if test="${not empty menuItem.textKey}"><bean:message
												key="${menuItem.textKey}"/></c:if>
										<c:if test="${empty menuItem.textKey}">${menuItem.text}</c:if>
									</a>
									<c:if test="${(!useColumns || loop.index % 2 == 1)}"><br></c:if>
								</c:forEach>
								<br>
							</div>
						</c:if>

						<%-- Section title --%>
						<div style="clear: left; float: left; width: 90%;">
							<h3 style="width:100%; background-color: ${section.colour}">
								<a href="#" onclick="${section.onClickTitle};return false;">
									<bean:message key="${section.titleKey}"/>
								</a>
							</h3>
						</div>

						<%-- List of links in the section --%>
						<ul id="${sectionName}list">

							<c:if test="${fn:length(section.notes) == 0}">
								<li>&nbsp;</li>
							</c:if>

							<c:set var="section" scope="page"
								   value="${junoEncounterForm.sections[sectionName]}"/>

							<c:forEach items="${section.notes}" var="note" varStatus="loop">

								<%-- ============================================================================== --%>
								<%-- NOTE: This template is duplicated in sectionNoteTemplate.html and any          --%>
								<%--       modifications will need to be done in both places.  This should be       --%>
								<%--       changed at some point to share a template.                               --%>
								<%-- ============================================================================== --%>
								<li class="encounterNote ${loop.index % 2 == 0 ? 'encounterNoteEven' : 'encounterNoteOdd'}">

									<%-- Expand arrows if neccessary --%>
									<c:choose>
										<c:when test="${ section.remainingNotes > 0 && loop.last }">
											<a href="#"
											   class="expandCasemgmtSidebar encounterNoteTitle"
											   onclick="junoEncounter.getSectionRemote('${sectionName}', true, false, getEChartUUID()); return false;"
											   title="${section.remainingNotes} more items">
												<img id="img${sectionName}5"
													 src="${ctx}/oscarEncounter/graphics/expand.gif"/>&nbsp;&nbsp;
											</a>
										</c:when>
										<c:otherwise>
											<a border="0"
											   class="expandCasemgmtSidebar encounterNoteTitle">
												<img id="img${sectionName}1"
													 src="${ctx}/images/clear.gif"/>&nbsp;&nbsp;
											</a>
										</c:otherwise>
									</c:choose>

										<%-- Link title --%>
									<span class="encounterNoteTitle">
										<a
												class="links ${fn:join(note.titleClasses, ' ')}"
												<c:if test="${note.colour != null}">
													style="color: ${note.colour};"
												</c:if>
												onmouseover="this.className='linkhover ${fn:join(note.titleClasses, ' ')}'"
												onmouseout="this.className='links ${fn:join(note.titleClasses, ' ')}'"
												href="#"
												onclick="${note.onClick};return false;"
												title="${note.title}"
										>
											<c:out value="${note.text}"/>
										</a>
									</span>

										<%-- Link date --%>
									<fmt:parseDate value="${note.updateDate}"
												   pattern="yyyy-MM-dd'T'HH:mm"
												   var="parsedUpdateDate"/>
									<fmt:formatDate value="${parsedUpdateDate}"
													pattern="dd-MMM-yyyy" var="updateDate"/>
									<c:if test="${not empty updateDate}">
										<span class="encounterNoteDate ${loop.index % 2 == 0 ? 'encounterNoteEven' : 'encounterNoteOdd'}">
											...<a
												class="links"
												style="margin-right: 2px; color: ${note.colour};"
												onmouseover="this.className='linkhover'"
												onmouseout="this.className='links'"
												href="#"
												onclick="${note.onClick};return false;"
												title="${note.title}"
										>
												<c:out value="${note.value}"/>
												<c:out value="${updateDate}"/>
											</a>
										</span>
									</c:if>
								</li>
							</c:forEach>
						</ul>
					</div>
				</c:forEach>
			</div>
		</div>
		</c:forEach>

		<div id="content" style="display: inline; float: left; width: 60%; background-color: #CCCCFF;">

			<%
			// =================================================================================
			// CPP boxes (four boxes at the top)
			// =================================================================================
			%>
			<div id="cppBoxes">

				<c:forEach items="${junoEncounterForm.cppNoteSections}" var="sectionName" varStatus="loop">

					<c:set var="section" scope="page" value="${junoEncounterForm.sections[sectionName]}"/>

					<!-- show div on 1 -->
					<c:if test="${loop.index == 0}">
						<div id="divR1" style="width: 100%; height: 75px; margin: 0; background-color: #FFFFFF;">
					</c:if>

					<!-- show div on 3 -->
					<c:if test="${loop.index == 2}">
						<div id="divR2" style="width: 100%; height: 75px; margin-top: 0; background-color: #FFFFFF;">
					</c:if>

					<!--Ongoing Concerns cell -->
					<c:if test="${loop.index == 0}">
						<div id="divR1I1" class="topBox" style="float: left; width: 49%; margin-left: 3px; height: inherit;">
					</c:if>

					<c:if test="${loop.index == 1}">
						<div id="divR1I2" class="topBox"
							 style="float: right; width: 49%; margin-right: 3px; height: inherit;">
					</c:if>

					<c:if test="${loop.index == 2}">
						<div id="divR2I1" class="topBox"
							 style="clear: left; float: left; width: 49%; margin-left: 3px; height: inherit;">
					</c:if>

					<c:if test="${loop.index == 3}">
						<div id="divR2I2" class="topBox"
							 style="clear: right; float: right; width: 49%; margin-right: 3px; height: inherit;">
					</c:if>


					<div style="width: 10%; float: right; text-align: center;">
						<h3 style="padding:0px; background-color: ${section.colour}">
							<a href="javascript:void(0);"
							   onclick="${section.onClickPlus};return false;"
							>+</a>
						</h3>
					</div>
					<div style="clear: left; float: left; width: 90%;">
						<h3 style="width:100%; background-color: ${section.colour}">
							<a href="javascript:void(0);"
							   onclick="${section.onClickTitle};return false;">
								<bean:message key="${section.titleKey}"/>
							</a>
						</h3>
					</div>
					<div style="clear: both; height: calc(100% - 10px); overflow: auto;">
						<ul id="${sectionName}list" style="margin-left: 5px;">
							<c:forEach items="${section.notes}" var="note"
									   varStatus="noteLoop">
								<fmt:parseDate value="${note.updateDate}"
											   pattern="yyyy-MM-dd'T'HH:mm"
											   var="parsedUpdateDate"/>
								<fmt:formatDate value="${parsedUpdateDate}"
												pattern="dd-MMM-yyyy"
												var="updateDate"/>
								<fmt:parseDate value="${note.observationDate}"
											   pattern="yyyy-MM-dd'T'HH:mm"
											   var="parsedObservationDate"/>
								<fmt:formatDate value="${parsedObservationDate}"
												pattern="dd-MMM-yyyy"
												var="observationDate"/>
								<%-- ============================================================================== --%>
								<%-- NOTE: This template is duplicated in sectionCPPNoteTemplate.html and any       --%>
								<%--       modifications will need to be done in both places.  This should be       --%>
								<%--       changed at some point to share a template.                               --%>
								<%-- ============================================================================== --%>
								<li class="cpp ${noteLoop.index % 2 == 0 ? 'encounterNoteEven' : 'encounterNoteOdd'}">
									<span id="spanListNote${fn:escapeXml(noteLoop.index)}">
										<a class="topLinks"
										   onmouseover="this.className='topLinkhover'"
										   onmouseout="this.className='topLinks'"
										   title="${note.title}"
										   id="listNote${note.id}"
										   href="#"
										   onclick="${note.onClick}"
										   style="width:100%;overflow:scroll;">
											<c:forEach items="${note.textLineArray}" var="noteLine">
												<c:out value="${noteLine}" /><br />
											</c:forEach>
										</a>
									</span>
								</li>
							</c:forEach>
						</ul>
						<br />
					</div>

					</div>

					<!-- show end div on 1 and 3 -->
					<c:if test="${loop.index == 1 or loop.index == 3}">
						</div>
					</c:if>

				</c:forEach>

				<span style="visibility:hidden">test</span>
			</div>


			<%
				// =================================================================================
				// Case Notes form and border etc.
				// =================================================================================
			%>
			<div id="notCPP" style="height: 70%; margin-left: 2px; background-color: #FFFFFF;">

				<html:form action="/CaseManagementView" method="post">
					<div id="topContent"
						 style="float: left; width: 100%; margin-right: -2px; padding-bottom: 1px; background-color: #CCCCFF; font-size: 10px;">

						<div id="appliedFiltersProviders" style="float: left; margin-left: 10px; margin-top: 0px; display: none;">
							<u><bean:message key="oscarEncounter.providers.title"/>:</u><br>

							<div id="appliedFiltersProvidersContent">

							</div>
						</div>

						<div id="appliedFiltersRoles" style="float: left; margin-left: 10px; margin-top: 0px; display: none;">
							<u><bean:message key="oscarEncounter.roles.title"/>:</u><br>

							<div id="appliedFiltersRolesContent">

							</div>
						</div>

						<div id="appliedFiltersSort" style="float: left; margin-left: 10px; margin-top: 0px; display: none;">
							<u><bean:message key="oscarEncounter.sort.title"/>:</u><br>

							<div id="appliedFiltersSortContent">

							</div>
						</div>
						<div id="appliedFiltersIssues" style="float: left; margin-left: 10px; margin-top: 0px; display: none;">
							<u><bean:message key="oscarEncounter.issues.title"/>:</u><br>

							<div id="appliedFiltersIssuesContent">

							</div>
						</div>

						<div id="filter"
							 style="display:none;background-color:#ddddff;padding:8px">
							<input type="button"
								   value="<bean:message key="oscarEncounter.showView.title" />"
								   onclick="return noteFilter.filter(false);"/>
							<input type="button"
								   value="<bean:message key="oscarEncounter.resetFilter.title" />"
								   onclick="return noteFilter.filter(true);"/>

							<table style="border-collapse:collapse;width:100%;margin-left:auto;margin-right:auto">
								<tr>
									<td style="font-size:inherit;background-color:#bbbbff;font-weight:bold">
										<bean:message
												key="oscarEncounter.providers.title"/>
									</td>
									<td style="font-size:inherit;background-color:#bbbbff;border-left:solid #ddddff 4px;border-right:solid #ddddff 4px;font-weight:bold">
										Role
									</td>
									<td style="font-size:inherit;background-color:#bbbbff;font-weight:bold">
										<bean:message
												key="oscarEncounter.sort.title"/>
									</td>
									<td style="font-size:inherit;background-color:#bbbbff;font-weight:bold">
										<bean:message
												key="oscarEncounter.issues.title"/>
									</td>
								</tr>
								<tr>
									<td style="font-size:inherit;background-color:#ccccff">
										<div style="height:150px;overflow:auto">
											<ul style="padding:0px;margin:0px;list-style:none inside none">
												<li>
													<input type="checkbox"
														   name="filter_providers"
														   value="a"
														   onclick="noteFilter.filterCheckBox(this);" />
													<bean:message key="oscarEncounter.sortAll.title"/>
												</li>

												<c:forEach items="${junoEncounterForm.pageData.providers}"
														   var="provider"
														   varStatus="loop">
													<li>
														<input type="checkbox"
															   name="filter_providers"
															   value="${provider.providerNo}"
															   onclick="noteFilter.filterCheckBox(this);" />
														<span id="filter_provider_name${provider.providerNo}">
															<c:out value="${provider.formattedName}" />
														</span>
													</li>

												</c:forEach>
											</ul>
										</div>
									</td>
									<td style="font-size:inherit;background-color:#ccccff;border-left:solid #ddddff 4px;border-right:solid #ddddff 4px">
										<div style="height:150px;overflow:auto">
											<ul style="padding:0px;margin:0px;list-style:none inside none">
												<li>
													<input type="checkbox"
														   name="filter_roles"
														   value="a"
														   onclick="noteFilter.filterCheckBox(this);">
													<bean:message key="oscarEncounter.sortAll.title"/>
												</li>

												<c:forEach items="${junoEncounterForm.pageData.roles}"
														   var="role"
														   varStatus="loop">
													<li>
														<input type="checkbox"
																name="filter_roles"
																value="${role.id}"
																onclick="noteFilter.filterCheckBox(this);" />
														<span id="filter_role_name${role.id}">
															<c:out value="${role.name}" />
														</span>
													</li>

												</c:forEach>
											</ul>
										</div>
									</td>
									<td style="font-size:inherit;background-color:#ccccff">
										<div style="height:150px;overflow:auto">
											<ul style="padding:0px;margin:0px;list-style:none inside none">
												<li><html:radio property="note_sort"
																value="observation_date_asc">
													<span id="filter_sort_nameobservation_date_asc">
														<bean:message
															key="oscarEncounter.sortDateAsc.title"/>
													</span>
												</html:radio></li>
												<li>
													<html:radio property="note_sort"
																value="observation_date_desc">
													<span id="filter_sort_nameobservation_date_desc">
														<bean:message
															key="oscarEncounter.sortDateDesc.title"/>
													</span>
												</html:radio></li>
												<li><html:radio property="note_sort"
																value="providerName">
													<span id="filter_sort_nameproviderName">
														<bean:message
															key="oscarEncounter.provider.title"/>
													</span>
												</html:radio></li>
												<%--
												<li><html:radio property="note_sort"
																value="programName">
													<bean:message
															key="oscarEncounter.program.title"/>
												</html:radio></li>
												<li><html:radio property="note_sort"
																value="roleName">
													<bean:message
															key="oscarEncounter.role.title"/>
												</html:radio></li>
												--%>
											</ul>
										</div>
									</td>
									<td style="font-size:inherit;background-color:#ccccff;border-left:solid #ddddff 4px;border-right:solid #ddddff 4px">
										<div style="height:150px;overflow:auto">
											<ul style="padding:0px;margin:0px;list-style:none inside none">
												<li>
													<input type="checkbox"
														   name="filter_issues"
														   value="a"
														   onclick="noteFilter.filterCheckBox(this);" />
													<bean:message key="oscarEncounter.sortAll.title"/>
												</li>

												<c:forEach items="${junoEncounterForm.pageData.caseManagementIssues}"
														   var="issue"
														   varStatus="loop">
													<li>
														<input type="checkbox"
															   name="filter_issues"
															   value="${issue.issue.id}"
															   onclick="noteFilter.filterCheckBox(this);" />
														<span id="filter_issue_name${issue.issue.id}">
															<c:if test="${issue.resolved}">* </c:if> <c:out value="${issue.issue.description}" />
														</span>
													</li>

												</c:forEach>
											</ul>
										</div>
									</td>
								</tr>
							</table>
						</div>

						<div style="float: left; clear: both; margin-top: 5px; margin-bottom: 3px; width: 100%; text-align: center;">
							<div style="display:inline-block">
								<img alt="<bean:message key="oscarEncounter.msgFind"/>"
									 src="<c:out value="${ctx}/oscarEncounter/graphics/edit-find.png"/>">
								<input id="enTemplate" tabindex="6" size="16"
									   type="text" value="">
								<div class="enTemplate_name_auto_complete"
									 id="enTemplate_list"
									 style="z-index: 1; display: none">&nbsp;
								</div>
								<input type="text" id="keyword" name="keyword"
									   value=""
									   onkeypress="return junoEncounter.grabEnter('searchButton',event);">
								<input type="button" id="searchButton" name="button"
									   value="<bean:message key="oscarEncounter.Index.btnSearch"/>"
									   onClick="junoEncounter.channelSearch(); return false;">

								<div style="display:inline-block; text-align: left;">

									<!-- channel -->
									<select id="channel">
										<option value="http://www.google.com/search?q=">
											<bean:message
													key="global.google"/></option>
										<option value="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?SUBMIT=y&amp;CDM=Search&amp;DB=PubMed&amp;term=">
											<bean:message
													key="global.pubmed"/></option>
										<option value="http://search.nlm.nih.gov/medlineplus/query?DISAMBIGUATION=true&amp;FUNCTION=search&amp;SERVER2=server2&amp;SERVER1=server1&amp;PARAMETER=">
											<bean:message
													key="global.medlineplus"/></option>
										<option value="../casemgmt/tripsearch.jsp?searchterm=">
											Trip Database
										</option>
										<option value="../casemgmt/macplussearch.jsp?searchterm=">
											MacPlus Database
										</option>
									</select>
								</div>

							</div>
							&nbsp;&nbsp;
							<div style="display:inline-block;text-align: left;" id="toolbar">
								<input type="button"
									   value="<bean:message key="oscarEncounter.Filter.title"/>"
									   onclick="noteFilter.showFilter();"/>

								<security:oscarSec roleName="${junoEncounterForm.pageData.roleName}" objectName="_newCasemgmt.calculators" rights="r" reverse="false">
									<%
										String patientAge = junoEncounterForm.getPageData().getPatientAgeInYears();
										String patientSex = junoEncounterForm.getPageData().getPatientSex();
									%>
									<%@include file="../casemgmt/calculatorsSelectList.jspf" %>
								</security:oscarSec>

								<security:oscarSec roleName="${junoEncounterForm.pageData.roleName}" objectName="_newCasemgmt.templates" rights="r">
									<select style="width:100px;" onchange="junoEncounter.openTemplatePage(this.value); return false;">
										<option value="-1"><bean:message key="oscarEncounter.Header.Templates"/></option>
										<option value="-1">------------------</option>
										<security:oscarSec roleName="${junoEncounterForm.pageData.roleName}" objectName="_newCasemgmt.templates" rights="w">
											<option value="${ctx}/admin/providertemplate.jsp">New / Edit Template</option>
											<option value="-1">------------------</option>
										</security:oscarSec>
										<c:forEach items="${junoEncounterForm.pageData.encounterTemplates}" var="encounterTemplate">
											<option value="${ctx}/admin/providertemplate.jsp?dboperation=Edit&name=${encounterTemplate.encounterTemplateName}">
													${encounterTemplate.encounterTemplateName}
											</option>
										</c:forEach>
									</select>
								</security:oscarSec>
							</div>
						</div>
					</div>
				</html:form>
				<nested:form action="/CaseManagementEntry"
							 style="display:inline; margin-top:0; margin-bottom:0; position: relative;">

					<span id="notesLoading">
						<img src="<c:out value="${ctx}/images/DMSLoader.gif" />">Loading Notes...
					</span>

					<div id="mainContent"
						 style="background-color: #FFFFFF; width: 100%; margin-right: -2px; display: inline; float: left;">
						<div id="issueList"
							 style="background-color: #FFFFFF; height: 440px; width: 350px; position: absolute; z-index: 1; display: none; overflow: auto;">
							<table id="issueTable"
								   class="enTemplate_name_auto_complete"
								   style="position: relative; left: 0px; display: none;">
								<tr>
									<td style="height: 430px; vertical-align: bottom;">
										<div class="enTemplate_name_auto_complete"
											 id="issueAutocompleteList"
											 style="position: relative; left: 0px; display: none;"></div>
									</td>
								</tr>
							</table>
						</div>

						<div id="encMainDiv" class="encMainDiv">

						</div>

						<div id='save' style="width: 99%; background-color: #CCCCFF; padding-top: 5px; margin-left: 2px; border-left: thin solid #000000; border-right: thin solid #000000; border-bottom: thin solid #000000;">
							<span style="float: right; margin-right: 5px;">
								<input type="hidden" name="notes2print" id="notes2print" value="">
								<input type="hidden" name="printCPP" id="printCPP" value="false">
								<input type="hidden" name="printRx" id="printRx" value="false">
								<input type="hidden" name="printLabs" id="printLabs" value="false">

								<div class="encounter_timer_container">
									<div style="display: inline-block; position:relative;">
										<input id="encounter_timer" title="Paste timer data" type="button"
											   onclick="encounterNote.putEncounterTimeInNote()" value="00:00"/>
									</div>
									<input id="encounter_timer_pause" class="encounter_timer_control" type="button"
										   onclick="encounterTimer.toggleEncounterTimer('#encounter_timer_pause', '#encounter_timer_play')"
										   value="||"/>
									<input id="encounter_timer_play" class="encounter_timer_control" type="button"
										   onclick="encounterTimer.toggleEncounterTimer('#encounter_timer_pause', '#encounter_timer_play')"
										   value="&gt;"/>
								</div>
								<input tabindex="17" type='image'
									   src="<c:out value="${ctx}/oscarEncounter/graphics/media-floppy.png"/>"
									   id="saveImg"
									   onclick="Event.stop(event);return encounterNote.saveEncounterNote(false, false, false, true, false);"
									   title='<bean:message key="oscarEncounter.Index.btnSave"/>'>&nbsp;
								<input tabindex="18" type='image'
									   src="<c:out value="${ctx}/oscarEncounter/graphics/document-new.png"/>"
									   id="newNoteImg" onclick="Event.stop(event); return encounterNote.createNewNote(); return false;"
									   title='<bean:message key="oscarEncounter.Index.btnNew"/>'>&nbsp;
								<input tabindex="19" type='image'
									   src="<c:out value="${ctx}/oscarEncounter/graphics/note-save.png"/>"
									   id="signSaveImg"
									   onclick="Event.stop(event);return encounterNote.saveEncounterNote(true, false, true, true, false);"
									   title='<bean:message key="oscarEncounter.Index.btnSignSave"/>'>&nbsp;
								<input tabindex="20" type='image'
									   src="<c:out value="${ctx}/oscarEncounter/graphics/verify-sign.png"/>"
									   id="signVerifyImg"
									   onclick="Event.stop(event);return encounterNote.saveEncounterNote(true, true, true, true, false);"
									   title='<bean:message key="oscarEncounter.Index.btnSign"/>'>&nbsp;
								<c:if test="${junoEncounterForm.pageData.source == null}">
									<input tabindex="21" type='image'
										   src="<c:out value="${ctx}/oscarEncounter/graphics/dollar-sign-icon.png"/>"
										   onclick="Event.stop(event);return encounterNote.saveEncounterNote(true, false, true, true, true);"
										   title='<bean:message key="oscarEncounter.Index.btnBill"/>'>&nbsp;
								</c:if>
								<input tabindex="23" type='image'
									   src="<c:out value="${ctx}/oscarEncounter/graphics/system-log-out.png"/>"
									   onclick='encounterNote.closeEnc(event);return false;'
									   title='<bean:message key="global.btnExit"/>'>&nbsp;
								<input tabindex="24" type='image'
									   src="<c:out value="${ctx}/oscarEncounter/graphics/document-print.png"/>"
									   onclick="return printNotes.printSetup(event);"
									   title='<bean:message key="oscarEncounter.Index.btnPrint"/>'
									   id="imgPrintEncounter">
							</span>
							<div id="assignIssueSection">
								<!-- input type='image' id='toggleIssue' onclick="return showIssues(event);" src="<c:out value="${ctx}/oscarEncounter/graphics/issues.png"/>" title='<bean:message key="oscarEncounter.Index.btnDisplayIssues"/>'>&nbsp; -->
								<input tabindex="8" type="text"
									   id="issueAutocomplete" name="issueSearch"
									   style="z-index: 2;"
									   onkeypress="return caseManagementIssue.submitIssue(event);"
									   size="24">&nbsp;
								<input type="hidden"
									   id="issueSearchSelectedId"
									   name="issueSearchSelectedId">
								<input type="hidden"
									   id="issueSearchSelected"
									   name="issueSearchSelected">
								<input tabindex="9"
									   type="button"
									   id="asgnIssues"
									   onclick="caseManagementIssue.addIssueToCurrentNote(event); return false;"
									   value="<bean:message key="oscarEncounter.assign.title"/>">
								<input tabindex="9"
									   type="button"
									   id="changeIssues"
									   style="display: none;"
									   onclick="caseManagementIssue.changeIssue({junoJQuery: junoJQuery}); return false;"
									   value="<bean:message key="oscarEncounter.change.title"/>">
								<span id="busy" style="display: none">
									<img style="position: absolute;"
										 src="<c:out value="${ctx}/oscarEncounter/graphics/busy.gif"/>"
										 alt="<bean:message key="oscarEncounter.Index.btnWorking" />">
								</span>
							</div>
							<div style="padding-top: 3px;">
								<button type="button" id="displayResolvedIssuesButton">
									<bean:message key="oscarEncounter.Index.btnDisplayResolvedIssues"/>
								</button>
								&nbsp;
								<button type="button" id="displayUnresolvedIssuesButton">
									<bean:message key="oscarEncounter.Index.btnDisplayUnresolvedIssues"/>
								</button>
								&nbsp;
								<button type="button" onclick="encounterNote.spellCheck();">
									Spell Check
								</button> &nbsp;
								<button type="button" onclick="encounterNote.expandAllNotes();">
									<bean:message key="eFormGenerator.expandAll"/>
									<bean:message key="Appointment.formNotes"/>
								</button>
								<button type="button"
										onclick="javascript:popupPage(500,200,'noteBrowser${junoEncounterForm.pageData.demographicNo}','../casemgmt/noteBrowser.jsp?demographic_no=${junoEncounterForm.pageData.demographicNo}&FirstTime=1');">
									<bean:message key="oscarEncounter.Index.BrowseNotes"/>
								</button>
								&nbsp;
							</div>
						</div>

					</div>
					</nested:form>
				<%--
				// =================================================================================
				// End of Case Notes
				// =================================================================================
				--%>

		</div>

	</div>

	<!-- hovering divs -->
	<div id="showEditNote" class="showEdContent">
		<form
				id="frmIssueNotes"
				action=""
				method="post"
				onsubmit="return cppNote.updateCPPNote();">

			<input type="hidden" id="noteUuid" name="noteUuid" value=""/>
			<input type="hidden" id="noteSummaryCode" name="noteSummaryCode"
				   value=""/>
			<input type="hidden" id="noteEditId" name="noteEditId" value=""/>
			<input type="hidden" id="noteRevision" name="noteRevision"
				   value=""/>
			<input type="hidden" id="issueChange" name="issueChange" value=""/>
			<input type="hidden" id="archived" name="archived" value="false"/>
			<input type="hidden" id="annotation_attrib" name="annotation_attrib"/>
			<div id="winTitle"></div>
			<div id="editNoteError" class="error editNoteError"></div>
			<textarea style="margin: 10px;" cols="50" rows="15" id="noteEditTxt"
					  name="value"></textarea>
			<br>

			<table>
				<tr id="Itemproblemdescription">
					<td><bean:message
							key="oscarEncounter.problemdescription.title"/>:
					</td>
					<td><input type="text" id="problemdescription"
							   name="problemdescription" value=""></td>
				</tr>
				<tr id="Itemstartdate">
					<td><bean:message key="oscarEncounter.startdate.title"/>:
					</td>
					<td><input type="text" id="startdate" name="startdate"
							   value="" size="12"> (YYYY-MM-DD)
					</td>
				</tr>
				<tr id="Itemresolutiondate">
					<td><bean:message
							key="oscarEncounter.resolutionDate.title"/>:
					</td>
					<td><input type="text" id="resolutiondate"
							   name="resolutiondate" value="" size="12">
						(YYYY-MM-DD)
					</td>
				</tr>
				<tr id="Itemageatonset">
					<td><bean:message key="oscarEncounter.ageAtOnset.title"/>:
					</td>
					<td><input type="text" id="ageatonset" name="ageatonset"
							   value="" size="2"></td>
				</tr>

				<tr id="Itemproceduredate">
					<td><bean:message key="oscarEncounter.procedureDate.title"/>:
					</td>
					<td><input type="text" id="proceduredate"
							   name="proceduredate"
							   value="" size="12"> (YYYY-MM-DD)
					</td>
				</tr>
				<tr id="Itemtreatment">
					<td><bean:message key="oscarEncounter.treatment.title"/>:
					</td>
					<td><input type="text" id="treatment" name="treatment"
							   value=""></td>
				</tr>
				<tr id="Itemproblemstatus">
					<td><bean:message key="oscarEncounter.problemStatus.title"/>:
					</td>
					<td><input type="text" id="problemstatus"
							   name="problemstatus"
							   value="" size="8"> <bean:message
							key="oscarEncounter.problemStatusExample.msg"/></td>
				</tr>
				<tr id="Itemexposuredetail">
					<td><bean:message
							key="oscarEncounter.exposureDetail.title"/>:
					</td>
					<td><input type="text" id="exposuredetail"
							   name="exposuredetail" value=""></td>
				</tr>
				<tr id="Itemrelationship">
					<td><bean:message key="oscarEncounter.relationship.title"/>:
					</td>
					<td><input type="text" id="relationship" name="relationship"
							   value=""></td>
				</tr>
				<tr id="Itemlifestage">
					<td><bean:message key="oscarEncounter.lifestage.title"/>:
					</td>
					<td><select name="lifestage" id="lifestage">
						<option value="">
							<bean:message
									key="oscarEncounter.lifestage.opt.notset"/>
						</option>
						<option value="N">
							<bean:message
									key="oscarEncounter.lifestage.opt.newborn"/>
						</option>
						<option value="I">
							<bean:message
									key="oscarEncounter.lifestage.opt.infant"/>
						</option>
						<option value="C">
							<bean:message
									key="oscarEncounter.lifestage.opt.child"/>
						</option>
						<option value="T">
							<bean:message
									key="oscarEncounter.lifestage.opt.adolescent"/>
						</option>
						<option value="A">
							<bean:message
									key="oscarEncounter.lifestage.opt.adult"/>
						</option>
					</select></td>
				</tr>
				<tr id="Itemhidecpp">
					<td><bean:message key="oscarEncounter.hidecpp.title"/>:</td>
					<td><select id="hidecpp" name="hidecpp">
						<option value="0">No</option>
						<option value="1">Yes</option>
					</select></td>
				</tr>
			</table>
			<br>
			<span style="float: right; margin-right: 10px;">
				<input type="image"
					   src="<c:out value="${ctx}/oscarEncounter/graphics/copy.png"/>"
					   title='<bean:message key="oscarEncounter.Index.btnCopy"/>'
					   onclick="encounterNote.copyCppToCurrentNote(); return false;">
				<input type="image"
					   src="<c:out value="${ctx}/oscarEncounter/graphics/annotation.png"/>"
					   title='<bean:message key="oscarEncounter.Index.btnAnnotation"/>'
					   id="anno" style="padding-right: 10px;">
				<input type="image"
					   src="<c:out value="${ctx}/oscarEncounter/graphics/edit-cut.png"/>"
					   title='<bean:message key="oscarEncounter.Index.btnArchive"/>'
					   onclick="$('archived').value='true';"
					   style="padding-right: 10px;">
				<input type="image"
					   src="<c:out value="${ctx}/oscarEncounter/graphics/note-save.png"/>"
					   title='<bean:message key="oscarEncounter.Index.btnSignSave"/>'
					   onclick="$('archived').value='false';" style="padding-right: 10px;">
				<input type="image"
					   src="<c:out value="${ctx}/oscarEncounter/graphics/system-log-out.png"/>"
					   title='<bean:message key="global.btnExit"/>'
					   onclick="this.focus();cppNote.hideEdit();return false;">
			</span>
			<bean:message key="oscarEncounter.Index.btnPosition"/>
			<select id="position" name="position">
			</select>
			<div id="issueNoteInfo"
				 style="clear: both; text-align: left;"></div>
			<div id="issueListCPP"
				 style="background-color: #FFFFFF; height: 200px; width: 350px; position: absolute; z-index: 1; display: none; overflow: auto;">
				<div class="enTemplate_name_auto_complete"
					 id="issueAutocompleteListCPP"
					 style="position: relative; left: 0px; display: none;"></div>
			</div>
			<bean:message key="oscarEncounter.Index.assnIssue"/>
			&nbsp;<input tabindex="100" type="text" id="issueAutocompleteCPP"
						 name="issueSearch" style="z-index: 2;" size="25">&nbsp;
			<span
					id="busy2" style="display: none"><img
					style="position: absolute;"
					src="<c:out value="${ctx}/oscarEncounter/graphics/busy.gif"/>"
					alt="<bean:message key="oscarEncounter.Index.btnWorking"/>"></span>

		</form>
	</div>

	<div id="printOps" class="printOps">
		<h3 style="margin-bottom: 5px; text-align: center;">
			<bean:message key="oscarEncounter.Index.PrintDialog"/>
		</h3>
		<form id="frmPrintOps" action="" onsubmit="return false;">
			<table id="printElementsTable">
				<tr>
					<td><input type="radio" id="printopSelected" name="printop"
							   value="selected">
						<bean:message key="oscarEncounter.Index.PrintSelect"/>
					</td>
					<td>
						<%
							String roleName = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
						%> <security:oscarSec roleName="<%=roleName%>"
											  objectName="_newCasemgmt.cpp"
											  rights="r" reverse="false">
						<img style="cursor: pointer;"
							 title="<bean:message key="oscarEncounter.print.title"/>"
							 id='imgPrintCPP'
							 alt="<bean:message key="oscarEncounter.togglePrintCPP.title"/>"
							 onclick="return printNotes.printInfo(this,'printCPP');"
							 src='<c:out value="${ctx}/oscarEncounter/graphics/printer.png"/>'>&nbsp;<bean:message
							key="oscarEncounter.cpp.title"/>
					</security:oscarSec>
					</td>
				</tr>
				<tr>
					<td><input type="radio" id="printopAll" name="printop"
							   value="all">
						<bean:message key="oscarEncounter.Index.PrintAll"/></td>
					<td><img style="cursor: pointer;"
							 title="<bean:message key="oscarEncounter.print.title"/>"
							 id='imgPrintRx'
							 alt="<bean:message key="oscarEncounter.togglePrintRx.title"/>"
							 onclick="return printNotes.printInfo(this, 'printRx');"
							 src='<c:out value="${ctx}/oscarEncounter/graphics/printer.png"/>'>&nbsp;<bean:message
							key="oscarEncounter.Rx.title"/></td>
				</tr>
				<tr>
					<td></td>
					<td><img style="cursor: pointer;"
							 title="<bean:message key="oscarEncounter.print.title"/>"
							 id='imgPrintLabs'
							 alt="<bean:message key="oscarEncounter.togglePrintLabs.title"/>"
							 onclick="return printNotes.printInfo(this, 'printLabs');"
							 src='<c:out value="${ctx}/oscarEncounter/graphics/printer.png"/>'>&nbsp;<bean:message
							key="oscarEncounter.Labs.title"/></td>
				</tr>
				<!--  extension point -->
				<tr id="printDateRow">
					<td><input type="radio" id="printopDates" name="printop"
							   value="dates">
						<bean:message key="oscarEncounter.Index.PrintDates"/>&nbsp;<a
								style="font-variant: small-caps;" href="#"
								onclick="return printNotes.printToday(event);"><bean:message
								key="oscarEncounter.Index.PrintToday"/></a></td>
					<td></td>
				</tr>
			</table>

			<div style="float: left; margin-left: 5px; width: 30px;">
				<bean:message key="oscarEncounter.Index.PrintFrom"/>
				:
			</div>
			<img src="<c:out value="${ctx}/images/cal.gif" />"
				 id="printStartDate_cal" alt="calendar">&nbsp;<input
				type="text" id="printStartDate" name="printStartDate"
				ondblclick="this.value='';"
				style="font-style: italic; border: 1px solid #7682b1; width: 125px; background-color: #FFFFFF;"
				readonly value=""><br>
			<div style="float: left; margin-left: 5px; width: 30px;">
				<bean:message key="oscarEncounter.Index.PrintTo"/>
				:
			</div>
			<img src="<c:out value="${ctx}/images/cal.gif" />"
				 id="printEndDate_cal" alt="calendar">&nbsp;<input type="text"
																   id="printEndDate"
																   name="printEndDate"
																   ondblclick="this.value='';"
																   style="font-style: italic; border: 1px solid #7682b1; width: 125px; background-color: #FFFFFF;"
																   readonly
																   value=""><br>
			<div style="margin-top: 5px; text-align: center">
				<input type="submit" id="printOp"
					   style="border: 1px solid #7682b1;"
					   value="Print" onclick="return printNotes.printNotes();">

				<indivo:indivoRegistered
						demographic="<%=(String) request.getAttribute(\"demographicNo\")%>"
						provider="<%=(String) request.getSession().getAttribute(\"user\")%>">
					<input type="submit" id="sendToPhr"
						   style="border: 1px solid #7682b1;"
						   value="Send To Phr"
						   onclick="return sendToPhrr();">
				</indivo:indivoRegistered>
				<input type="submit" id="cancelprintOp"
					   style="border: 1px solid #7682b1;" value="Cancel"
					   onclick="$('printOps').style.display='none';"> <input
					type="submit" id="clearprintOp"
					style="border: 1px solid #7682b1;"
					value="Clear"
					onclick="$('printOps').style.display='none'; return printNotes.clearAll(event);">
			</div>
		</form>
	</div>


	<script type="text/javascript">

		(function ($)
		{
			$(document).ready(function ()
			{
				init();
			});
		})(jQuery);

	</script>

</body>
</html:html>
