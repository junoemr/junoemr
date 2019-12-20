<%--
  ~ Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.                
  ~ This software is published under the GPL GNU General Public License.            
  ~ This program is free software; you can redistribute it and/or                   
  ~ modify it under the terms of the GNU General Public License                     
  ~ as published by the Free Software Foundation; either version 2                  
  ~ of the License, or (at your option) any later version.                          
  ~                                                                                 
  ~ This program is distributed in the hope that it will be useful,                 
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of                  
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the                    
  ~ GNU General Public License for more details.                                    
  ~                                                                                 
  ~ You should have received a copy of the GNU General Public License               
  ~ along with this program; if not, write to the Free Software                     
  ~ Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.      
  ~                                                                                 
  ~ This software was written for                                                   
  ~ CloudPractice Inc.                                                              
  ~ Victoria, British Columbia                                                      
  ~ Canada      
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

<fmt:parseDate value="${junoEncounterForm.header.encounterNoteHideBeforeDate}"
			   pattern="EEE MMM dd HH:mm:ss z y"
			   var="encounterNoteHideBeforeDateParsed"/>

<fmt:formatDate value="${encounterNoteHideBeforeDateParsed}"
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

		<script type="text/javascript">



			<%-- ============================================================================== --%>
			<%-- Transfer data from Java to Javascript                                          --%>
			<%-- ============================================================================== --%>

			function getAppointmentNo()
			{
				<c:if test="${not empty junoEncounterForm.header.appointmentNo}">
				var appointmentNo = <c:out value="${junoEncounterForm.header.appointmentNo}" />;
				</c:if>
				<c:if test="${empty junoEncounterForm.header.appointmentNo}">
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

			var pageData = {
				contextPath: "<c:out value='${pageContext.request.contextPath}' />",
				demographicNo: "<c:out value='${junoEncounterForm.header.demographicNo}' />",
				providerNo: "<c:out value='${junoEncounterForm.header.providerNo}' />",
				appointmentNo: getAppointmentNo(),
				encounterNoteHideBeforeDate: "<c:out value='${encounterNoteHideBeforeDateFormatted}' />",
				defaultEncounterType: "<c:out value='${junoEncounterForm.encType}' />",
				reason: "<c:out value='${junoEncounterForm.reason}' />",
				appointmentDate: "<c:out value='${junoEncounterForm.appointmentDate}' />",
				cmeJs: "<c:out value='${junoEncounterForm.header.cmeJs}' />",
				billingUrl: "<c:out value='${junoEncounterForm.header.billingUrl}' />",
				encounterTypeArray: getEncounterTypeArray(),
				imagePresentPlaceholderUrl: "<c:out value='${fn:escapeXml(junoEncounterForm.header.imagePresentPlaceholderUrl)}' />",
				editUnsignedMsg: "<bean:message key="oscarEncounter.editUnsignedNote.msg"/>",
				printDateMsg: "<bean:message key="oscarEncounter.printDate.msg"/>",
				printDateOrderMsg: "<bean:message key="oscarEncounter.printDateOrder.msg"/>",
				notesIncrement: <%= OscarProperties.getNumLoadedNotes(20) %>,
				assignedIssuesTitle: "<bean:message key="oscarEncounter.assignedIssues.title"/>",
				referenceResolvedIssuesTitle: "<bean:message key="oscarEncounter.referenceResolvedIssues.title"/>",
				referenceUnresolvedIssuesTitle: "<bean:message key="oscarEncounter.referenceUnresolvedIssues.title"/>",
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
			};


			var eChartUUID = "${junoEncounterForm.header.echartUuid}";

			<%@ include file="js/JunoEncounter.js" %>
			var junoEncounter = new Juno.OscarEncounter.JunoEncounter(pageData);

			<%@ include file="js/JunoEncounter/CppNote.js" %>
			var cppNote = new Juno.OscarEncounter.JunoEncounter.CppNote(pageData, junoEncounter);

			<%@ include file="js/JunoEncounter/EncounterNote.js" %>
			var encounterNote = new Juno.OscarEncounter.JunoEncounter.EncounterNote(pageData, pageState);

			<%@ include file="js/JunoEncounter/CaseManagementIssue.js" %>
			var caseManagementIssue = new Juno.OscarEncounter.JunoEncounter.CaseManagementIssue(pageData, pageState);

			<%@ include file="js/JunoEncounter/PrintNotes.js" %>
			var printNotes = new Juno.OscarEncounter.JunoEncounter.PrintNotes(pageData);

			<%@ include file="js/JunoEncounter/NoteFilter.js" %>
			var noteFilter = new Juno.OscarEncounter.JunoEncounter.NoteFilter(pageData, pageState, encounterNote);


			<%-- ============================================================================== --%>
			<%-- API Functions                                                                  --%>
			<%-- ============================================================================== --%>

			// These methods are used by external pages so they shouldn't be changed.

			// This method is called by child windows.  Please don't move or rename it.
			function getEChartUUID()
			{
				return eChartUUID;
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

			function onClosing()
			{
				var noteId = jQuery("input#editNoteId").val();

				// Prepare data
				var noteData = encounterNote.getNoteDataById(noteId);

				// Save unfinished note on exit. The temp save stuff added in Oscar15 is too fragile
				// to depend on

				// Trim the notes because that happens when the note is saved
				if(pageState.currentNoteData.note.trim() != noteData.note.trim())
				{
					encounterNote.saveEncounterNote(false, false, true, false, false);
				}

				// Tell child measurement windows that we're leaving
				junoEncounter.cleanUpWindows();

				return null;
			}


			<%-- ============================================================================== --%>
			<%-- Autocomplete helper functions                                                  --%>
			<%-- ============================================================================== --%>

			function saveIssueId(txtField, listItem)
			{
				jQuery('input#issueSearchSelectedId').val(listItem.id);
				jQuery('input#issueSearchSelected').val(listItem.innerHTML);
			}

			function addIssueToCPP(txtField, listItem)
			{
				var nodeId = listItem.id;
				var issueDescription = listItem.innerHTML;
				caseManagementIssue.addIssue("frmIssueNotes", "issueIdList", "issueAutocompleteCPP", nodeId, issueDescription);

				$("issueChange").value = true;
			}

			function autoCompleteShowMenuCPP(element, update)
			{
				Effect.Appear($("issueListCPP"), {duration: 0.15});
				Effect.Appear(update, {duration: 0.15});
			}

			function autoCompleteHideMenuCPP(element, update)
			{
				new Effect.Fade(update, {duration: 0.15});
				new Effect.Fade($("issueListCPP"), {duration: 0.15});
			}

			function autoCompleteShowMenu(element, update)
			{
				$("issueList").style.left = $("mainContent").style.left;
				$("issueList").style.top = $("mainContent").style.top;
				$("issueList").style.width = $("issueAutocompleteList").style.width;

				Effect.Appear($("issueList"), {duration: 0.15});
				Effect.Appear($("issueTable"), {duration: 0.15});
				Effect.Appear(update, {duration: 0.15});
			}

			function autoCompleteHideMenu(element, update)
			{
				new Effect.Fade(update, {duration: 0.15});
				new Effect.Fade($("issueTable"), {duration: 0.15});
				new Effect.Fade($("issueList"), {duration: 0.15});
			}



			<%-- ============================================================================== --%>
			<%-- init                                                                           --%>
			<%-- ============================================================================== --%>

			function init()
			{
				var oceanHeight = 0;
				if(junoEncounter.showOceanToolbar())
				{
					jQuery.ajax({ url: "../eform/displayImage.do?imagefile=oceanToolbar.js", cache: true, dataType: "script" });
					oceanHeight = 28;
				}

				if (parseInt(navigator.appVersion) > 3)
				{
					var windowHeight = 750;
					if (navigator.appName == "Netscape")
					{
						windowHeight = window.innerHeight;
					}
					if (navigator.appName.indexOf("Microsoft") != -1)
					{
						windowHeight = document.body.offsetHeight;
					}

					// XXX: this seems like it generally won't work (ocean, etc.)
					var divHeight = windowHeight - (320 + oceanHeight);
					$("encMainDiv").style.height = divHeight + 'px';
				}

				// This was messing up the serialization of arrays to JSON so I removed it.
				delete Array.prototype.toJSON;

				// Monkey Patch from https://stackoverflow.com/a/16208232
				if (typeof jQuery.when.all === 'undefined')
				{
					jQuery.when.all = function (deferreds)
					{
						return jQuery.Deferred(function (def)
						{
							jQuery.when.apply(jQuery, deferreds).then(
								function ()
								{
									def.resolveWith(this, [Array.prototype.slice.call(arguments)]);
								},
								function ()
								{
									def.rejectWith(this, [Array.prototype.slice.call(arguments)]);
								});
						});
					}
				}


				Date.prototype.toJSON = function ()
				{
					return moment(this).format();
				}

				if (!NiftyCheck())
				{
					return;
				}

				Rounded("div.showEdContent", "all", "transparent", "#CCCCCC", "big border #000000");
				Rounded("div.printOps", "all", "transparent", "#CCCCCC", "big border #000000");
				Calendar.setup({
					inputField: "printStartDate",
					ifFormat: "%d-%b-%Y",
					showsTime: false,
					button: "printStartDate_cal",
					singleClick: true,
					step: 1
				});
				Calendar.setup({
					inputField: "printEndDate",
					ifFormat: "%d-%b-%Y",
					showsTime: false,
					button: "printEndDate_cal",
					singleClick: true,
					step: 1
				});

				<c:url value="/CaseManagementEntry.do" var="issueURLCPP">
				<c:param name="method" value="issueList"/>
				<c:param name="demographicNo" value="${junoEncounterForm.header.demographicNo}" />
				<c:param name="providerNo" value="${junoEncounterForm.header.providerNo}" />
				<c:param name="all" value="true" />
				</c:url>

				<nested:notEmpty name="DateError">
				alert("<nested:write name="DateError"/>");
				</nested:notEmpty>

				var calculatorMenu = jQuery('#calculators_menu');
				junoEncounter.bindCalculatorListener(calculatorMenu);

				// Click handlers for the resolved/unresolved issue buttons.  They pass in the
				// jQuery object from this context because it wouldn't work with the local context
				// inside the handler.  I don't know why, but this made it work.
				jQuery('#displayResolvedIssuesButton').click({jQuery: jQuery}, function(event)
				{
					caseManagementIssue.displayResolvedIssues(event.data);
				});
				jQuery('#displayUnresolvedIssuesButton').click({jQuery: jQuery}, function(event)
				{
					caseManagementIssue.displayUnresolvedIssues(event.data);
				});

				var issueAutoCompleterCPP = new Ajax.Autocompleter(
					"issueAutocompleteCPP",
					"issueAutocompleteListCPP",
					"<c:out value="${issueURLCPP}" />",
					{
						minChars: 3,
						indicator: 'busy2',
						afterUpdateElement: addIssueToCPP,
						onShow: autoCompleteShowMenuCPP,
						onHide: autoCompleteHideMenuCPP
					}
				);

				var issueURL = pageData.contextPath + "/CaseManagementEntry.do" +
					"?method=issueList" +
					"&demographicNo=" + this.pageData.demographicNo +
					"&providerNo=" + this.pageData.providerNo;
				issueAutoCompleter = new Ajax.Autocompleter("issueAutocomplete", "issueAutocompleteList", issueURL, {
					minChars: 3,
					indicator: 'busy',
					afterUpdateElement: saveIssueId,
					onShow: autoCompleteShowMenu,
					onHide: autoCompleteHideMenu
				});

				//var calculatorMenu = jQuery('#calculators_menu');

				//notesIncrement = parseInt("<%=OscarProperties.getInstance().getProperty("num_loaded_notes", "20") %>");

				var demographicNo = this.pageData.demographicNo;

				// Load a few extra notes initially, hopefully fill up the page
				encounterNote.notesLoader(pageData.contextPath, 0, pageData.notesIncrement * 2, demographicNo, true).then(function ()
				{
					pageState.notesOffset += (pageData.notesIncrement * 2);
					pageState.notesScrollCheckInterval = setInterval(function ()
					{
						encounterNote.notesIncrementAndLoadMore(demographicNo)
					}, 50);
				});

				// Multi-search autocomplete
				var searchAutocompleteUrl = "../ws/rs/encounterSections/" + this.pageData.demographicNo + "/autocomplete/";
				jQuery("#enTemplate").autocomplete({
					source: function(request, response)
					{
						jQuery.getJSON(searchAutocompleteUrl + request.term, function(data)
						{
							response(jQuery.map(data.body, function(section, index)
							{
								return {
									label: section.text,
									value: section.onClick
								};
							}));
						});
					},
					select: function(event, ui)
					{
						new Function(ui.item.value)();
						event.preventDefault();
					},
					minLength: 2,
					delay: 100
				});

				window.onbeforeunload = onClosing;
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
				color: ${fn:escapeXml(junoEncounterForm.header.inverseUserColour)};
			<%--<bean:write name="junoEncounterForm" property="header.inverseUserColour" />;--%> background-color: <bean:write name="junoEncounterForm" property="header.userColour" />;
			}

			div.encounterHeaderContainer span.Header {
				color: <bean:write name="junoEncounterForm" property="header.inverseUserColour" />;
				background-color: <bean:write name="junoEncounterForm" property="header.userColour" />;
			}

			div.encounterHeaderContainer span.familyDoctorInfo {
				border-bottom: medium solid<bean:write name="junoEncounterForm" property="header.familyDoctorColour" />;
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
		<div class="encounterHeaderContainer" id="encounterHeader">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td>
						<security:oscarSec roleName="${junoEncounterForm.header.roleName}"
										   objectName="_newCasemgmt.doctorName" rights="r">
								<span class="familyDoctorInfo">
									<bean:message key="oscarEncounter.Index.msgMRP"/>
									&nbsp;&nbsp;<bean:write name="junoEncounterForm"
															property="header.formattedFamilyDoctorName"/>
								</span>
						</security:oscarSec>
						<span class="Header">
								<%

								%>
								<a href="#"
								   onClick="popupPage(700,1000,
										   '${junoEncounterForm.header.windowName}',
										   '${ctx}${junoEncounterForm.header.demographicUrl}'
										   ); return false;"
								   title="<bean:message key="provider.appointmentProviderAdminDay.msgMasterFile"/>"
								>
									${junoEncounterForm.header.formattedPatientName}
								</a> ${junoEncounterForm.header.formattedPatientInfo}
								<c:if test="${junoEncounterForm.header.echartAdditionalPatientInfoEnabled}">
									<bean:write name="junoEncounterForm" property="header.patientBirthdate"/>
								</c:if>

								&nbsp;<oscar:phrverification
								demographicNo="${junoEncounterForm.header.demographicNo}"><bean:message
								key="phr.verification.link"/></oscar:phrverification> &nbsp;<bean:write
								name="junoEncounterForm" property="header.patientPhone"/>
								<span id="encounterHeaderExt"></span>
								<security:oscarSec roleName="${junoEncounterForm.header.roleName}"
												   objectName="_newCasemgmt.apptHistory" rights="r">
									<a href="javascript:popupPage(400,850,'ApptHist','<c:out value="${ctx}"/><bean:write name="junoEncounterForm" property="header.demographicAdditionalInfoUrl" />')"
									   style="font-size: 11px;text-decoration:none;"
									   title="<bean:message key="oscarEncounter.Header.nextApptMsg"/>"><span
											style="margin-left:20px;"><bean:message
											key="oscarEncounter.Header.nextAppt"/>: <oscar:nextAppt
											demographicNo="${junoEncounterForm.header.demographicNo}"/></span></a>
								</security:oscarSec>
								&nbsp;&nbsp;

								<c:if test="${junoEncounterForm.header.echartAdditionalPatientInfoEnabled}">
									<bean:write name="junoEncounterForm"
												property="header.referringDoctorName"/>
									<bean:write name="junoEncounterForm"
												property="header.referringDoctorNumber"/>
									&nbsp;&nbsp;
									<c:if test="${junoEncounterForm.header.rosterDateEnabled}">
										Referral date:
										<bean:write name="junoEncounterForm"
													property="header.rosterDateString"/>
									</c:if>
								</c:if>

								<c:if test="${junoEncounterForm.header.incomingRequestorSet}">
									<a href="javascript:void(0)"
									   onClick="popupPage(600,175,'Calculators','${fn:escapeXml(junoEncounterForm.header.diseaseListUrl)}'); return false;"><bean:message
											key="oscarEncounter.Header.OntMD"/></a>
								</c:if>
								<c:out value="${junoEncounterForm.header.echartLinks}"/>
								&nbsp;&nbsp;

							</span>
					</td>
					<td align=right>
							<span class="HelpAboutLogout">
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
					<c:when test="${not empty requestScope.image_exists}">
						<img style="cursor: pointer;" id="ci"
							 src="${fn:escapeXml(junoEncounterForm.header.imagePresentPlaceholderUrl)}"
							 alt="id_photo" height="100" title="Click to upload new photo."
							 OnMouseOver="document.getElementById('ci').src='../imageRenderingServlet?source=local_client&clientId=${fn:escapeXml(junoEncounterForm.header.demographicNo)}'"
							 OnMouseOut="junoEncounter.delay(5000); window.status='Click to upload new photo'; return true;"
							 onClick="junoEncounter.popupUploadPage('uploadimage.jsp',${fn:escapeXml(junoEncounterForm.header.demographicNo)});return false;" />
					</c:when>
					<c:otherwise>
						<img style="cursor: pointer;"
							 src="${fn:escapeXml(junoEncounterForm.header.imageMissingPlaceholderUrl)}"
							 alt="No_Id_Photo" height="100" title="Click to upload new photo."
							 OnMouseOver="window.status='Click to upload new photo';return true"
							 onClick="junoEncounter.popupUploadPage('../casemgmt/uploadimage.jsp',${fn:escapeXml(junoEncounterForm.header.demographicNo)});return false;"/>
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

							<c:set var="section" scope="page"
								   value="${junoEncounterForm.sections[sectionName]}"/>

							<c:forEach items="${section.notes}" var="note" varStatus="loop">

								<li class="encounterNote ${loop.index % 2 == 0 ? 'encounterNoteEven' : 'encounterNoteOdd'}">

									<%-- Expand arrows if neccessary --%>
									<c:choose>
										<c:when test="${ section.remainingNotes > 0 && loop.last }">
											<a href="#"
											   class="expandCasemgmtSidebar encounterNoteTitle"
											   onclick="junoEncounter.getSectionRemote('${sectionName}', true, false); return false;"
											   title="${section.remainingNotes} more items">
												<img id="img${sectionName}5"
													 src="graphics/expand.gif"/>&nbsp;&nbsp;
											</a>
										</c:when>
										<c:otherwise>
											<a border="0"
											   class="expandCasemgmtSidebar encounterNoteTitle">
												<img id="img${sectionName}1"
													 src="/images/clear.gif"/>&nbsp;&nbsp;
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
												title="Flu=Influenza vaccine"
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
										<span class="encounterNoteDate">
											...<a
												class="links"
												style="margin-right: 2px; color: ${note.colour};"
												onmouseover="this.className='linkhover'"
												onmouseout="this.className='links'"
												href="#"
												onclick="${note.onClick};return false;"
												title="DTaP=Diphtheria, Tetanus, Acellular Pertussis - pediatric"
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

		<div id="content"
			 style="display: inline; float: left; width: 60%; background-color: #CCCCFF;">

					<%
			// =================================================================================
			// CPP boxes (four boxes at the top)
			// =================================================================================
			%>
			<div id="cppBoxes">

				<c:forEach items="${junoEncounterForm.cppNoteSections}" var="sectionName"
						   varStatus="loop">

					<c:set var="section" scope="page"
						   value="${junoEncounterForm.sections[sectionName]}"/>

				<!-- show div on 1 -->
				<c:if test="${loop.index == 0}">
				<div id="divR1"
					 style="width: 100%; height: 75px; margin: 0; background-color: #FFFFFF;">
					</c:if>

					<!-- show div on 3 -->
					<c:if test="${loop.index == 2}">
					<div id="divR2"
						 style="width: 100%; height: 75px; margin-top: 0; background-color: #FFFFFF;">
						</c:if>


						<!--Ongoing Concerns cell -->
						<c:if test="${loop.index == 0}">
						<div id="divR1I1" class="topBox"
							 style="float: left; width: 49%; margin-left: 3px; height: inherit;">
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
													<li class="${noteLoop.index % 2 == 0 ? 'encounterNoteEven' : 'encounterNoteOdd'}">
											<span id="spanListNote${fn:escapeXml(noteLoop.index)}">
												<a class="topLinks"
												   onmouseover="this.className='topLinkhover'"
												   onmouseout="this.className='topLinks'"
												   title="Rev:${note.revision} - Last update:${updateDate}"
												   id="listNote${note.id}"
												   href="#"
												   onclick="${note.onClick}"
												   style="width:100%;overflow:scroll;">
														${fn:escapeXml(note.text)}
												</a>
											</span>
													</li>
												</c:forEach>
											</ul>
											<br>
										</div>

									</div>

									<!-- show div on 2 and 4 -->
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
									<%--
									<html:hidden property="demographicNo"
												 value="${junoEncounterForm.header.demographicNo}"/>
									<html:hidden property="providerNo"
												 value="${junoEncounterForm.header.providerNo}"/>
									<html:hidden property="tab" value="Current Issues"/>
									<html:hidden property="hideActiveIssue"/>
									<html:hidden property="ectWin.rowOneSize" styleId="rowOneSize"/>
									<html:hidden property="ectWin.rowTwoSize" styleId="rowTwoSize"/>
									<input type="hidden" name="chain" value="list">
									<input type="hidden" name="method" value="view">
									<input type="hidden" id="check_issue" name="check_issue">
									--%>
									<%-- TODO: fix later
									<input type="hidden" id="serverDate" value="<%=strToday%>">
									--%>
									<input type="hidden" id="resetFilter" name="resetFilter"
										   value="false">
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

												<%--
												<nested:notEmpty name="caseManagementViewForm"
																 property="filter_providers">
													<div style="float: left; margin-left: 10px; margin-top: 0px;">
														<u><bean:message
																key="oscarEncounter.providers.title"/>:</u><br>
														<nested:iterate type="String" id="filter_provider"
																		property="filter_providers">
															<c:choose>
																<c:when test="${filter_provider == 'a'}">All</c:when>
																<c:otherwise>
																	<nested:iterate id="provider" name="providers">
																		<c:if test="${filter_provider==provider.providerNo}">
																			<nested:write name="provider" property="formattedName"/>
																			<br>
																		</c:if>
																	</nested:iterate>
																</c:otherwise>
															</c:choose>
														</nested:iterate>
													</div>
												</nested:notEmpty>

												<nested:notEmpty name="caseManagementViewForm"
																 property="filter_roles">
													<div style="float: left; margin-left: 10px; margin-top: 0px;">
														<u><bean:message
																key="oscarEncounter.roles.title"/>:</u><br>
														<nested:iterate type="String" id="filter_role"
																		property="filter_roles">
															<c:choose>
																<c:when test="${filter_role == 'a'}">All</c:when>
																<c:otherwise>
																	<nested:iterate id="role" name="roles">
																		<c:if test="${filter_role==role.id}">
																			<nested:write name="role"
																						  property="name"/>
																			<br>
																		</c:if>
																	</nested:iterate>
																</c:otherwise>
															</c:choose>
														</nested:iterate>
													</div>
												</nested:notEmpty>

												<nested:notEmpty name="caseManagementViewForm"
																 property="note_sort">
													<div style="float: left; margin-left: 10px; margin-top: 0px;">
														<u><bean:message
																key="oscarEncounter.sort.title"/>:</u><br>
														<nested:write property="note_sort"/><br>
													</div>
												</nested:notEmpty>

												<nested:notEmpty name="caseManagementViewForm"
																 property="filter_issues">
													<div style="float: left; margin-left: 10px; margin-top: 0px;">
														<u><bean:message key="oscarEncounter.issues.title"/>:</u><br>
														<nested:iterate type="String" id="filter_issue"
																		property="filter_issues">
															<c:choose>
																<c:when test="${filter_issue == 'a'}">All</c:when>
																<c:otherwise>
																	<nested:iterate id="issue"
																					name="cme_issues">
																		<c:if test="${filter_issue==issue.issue.id}">
																			<nested:write name="issue"
																						  property="issueDisplay.description"/>
																			<br>
																		</c:if>
																	</nested:iterate>
																</c:otherwise>
															</c:choose>
														</nested:iterate>
													</div>
												</nested:notEmpty>
												--%>



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

																<c:forEach items="${junoEncounterForm.header.providers}"
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

																<c:forEach items="${junoEncounterForm.header.roles}"
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

																<c:forEach items="${junoEncounterForm.header.caseManagementIssues}"
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
												<%--
												onkeypress="encounterMultiSearch(event); return false;">
												--%>

												<div class="enTemplate_name_auto_complete"
													 id="enTemplate_list"
													 style="z-index: 1; display: none">&nbsp;
												</div>

												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;


												<input type="text" id="keyword" name="keyword"
													   value=""
													   onkeypress="return grabEnter('searchButton',event)">
												<input type="button" id="searchButton" name="button"
													   value="<bean:message key="oscarEncounter.Index.btnSearch"/>"
													   onClick="junoEncounter.channelSearch(); return false;">

												<div style="display:inline-block; text-align: left;">
													<%
														// TODO: make this work (parts missing)
										/*
										if (privateConsentEnabled && showPopup && showConsentsThisTime) {
										}

										 */
													%>

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
														<option value="tripsearch.jsp?searchterm=">
															Trip Database
														</option>
														<option value="macplussearch.jsp?searchterm=">
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

												<security:oscarSec roleName="${junoEncounterForm.header.roleName}" objectName="_newCasemgmt.calculators" rights="r" reverse="false">
													<%
														String patientAge = junoEncounterForm.getHeader().getPatientAgeInYears();
														String patientSex = junoEncounterForm.getHeader().getPatientSex();
													%>
													<%@include file="../casemgmt/calculatorsSelectList.jspf" %>
												</security:oscarSec>

												<security:oscarSec roleName="${junoEncounterForm.header.roleName}" objectName="_newCasemgmt.templates" rights="r">
													<select style="width:100px;" onchange="javascript:popupPage(700,700,'Templates',this.value);">
														<option value="-1"><bean:message key="oscarEncounter.Header.Templates"/></option>
														<option value="-1">------------------</option>
														<security:oscarSec roleName="${junoEncounterForm.header.roleName}" objectName="_newCasemgmt.templates" rights="w">
															<option value="${ctx}/admin/providertemplate.jsp">New / Edit Template</option>
															<option value="-1">------------------</option>
														</security:oscarSec>
														<c:forEach items="${junoEncounterForm.header.encounterTemplates}" var="encounterTemplate">
															<option value="${ctx}/admin/providertemplate.jsp?dboperation=Edit&name=${encounterTemplate.encounterTemplateName}">
																	${encounterTemplate.encounterTemplateName}
															</option>
														</c:forEach>
													</select>
												</security:oscarSec>



													<%--
													<script>
														function updateMYOSCAR(){
															jQuery.getScript('phrLinks.jsp?demographicNo=<%=demographicNo%>');
														}
														updateMYOSCAR();
													</script>
													--%>

											</div>
										</div>
									</div>
								</html:form>
								<%
									String oscarMsgType = (String) request.getParameter("msgType");
									String OscarMsgTypeLink = (String) request.getParameter("OscarMsgTypeLink");
								%>
								<nested:form action="/CaseManagementEntry"
											 style="display:inline; margin-top:0; margin-bottom:0; position: relative;">
									<%--
									// TODO: make this work (parts missing)
									<html:hidden property="demographicNo" value="<%=demographicNo%>" />
									--%>
									<html:hidden property="includeIssue" value="off"/>
									<input type="hidden" name="OscarMsgType"
										   value="<%=oscarMsgType%>"/>
									<input type="hidden" name="OscarMsgTypeLink"
										   value="<%=OscarMsgTypeLink%>"/>
									<%
										String apptNo = request.getParameter("appointmentNo");
										if (apptNo == null || apptNo.equals("") || apptNo.equals("null"))
										{
											apptNo = "0";
										}

										String apptDate = request.getParameter("appointmentDate");
										if (apptDate == null || apptDate.equals("") || apptDate.equals("null"))
										{
											apptDate = oscar.util.UtilDateUtilities.getToday("yyyy-MM-dd");
										}

										String startTime = request.getParameter("start_time");
										if (startTime == null || startTime.equals("") || startTime.equals("null"))
										{
											startTime = "00:00:00";
										}

										String apptProv = request.getParameter("apptProvider");
										if (apptProv == null || apptProv.equals("") || apptProv.equals("null"))
										{
											apptProv = "none";
										}

										String provView = request.getParameter("providerview");
										if (provView == null || provView.equals("") || provView.equals("null"))
										{
											// TODO: make this work (parts missing)
											//provView = provNo;
										}
									%>

									<html:hidden property="appointmentNo" value="<%=apptNo%>"/>
									<html:hidden property="appointmentDate" value="<%=apptDate%>"/>
									<html:hidden property="start_time" value="<%=startTime%>"/>
									<html:hidden property="billRegion"
												 value="<%=(OscarProperties.getInstance().getBillingType()).trim().toUpperCase()%>"/>
									<html:hidden property="apptProvider" value="<%=apptProv%>"/>
									<html:hidden property="providerview" value="<%=provView%>"/>
									<input type="hidden" name="toBill" id="toBill" value="false">
									<input type="hidden" name="deleteId" value="0">
									<input type="hidden" name="lineId" value="0">
									<input type="hidden" name="from" value="casemgmt">
									<input type="hidden" name="method" value="save">
									<input type="hidden" name="change_diagnosis"
										   value="<c:out value="${change_diagnosis}"/>">
									<input type="hidden" name="change_diagnosis_id"
										   value="<c:out value="${change_diagnosis_id}"/>">
									<input type="hidden" name="newIssueId" id="newIssueId">
									<input type="hidden" name="newIssueName" id="newIssueName">
									<input type="hidden" name="ajax" value="false">
									<input type="hidden" name="chain" value="">
									<%--
									// TODO: make this work (parts missing)
									<input type="hidden" name="caseNote.program_no" value="<%=pId%>">--%>
									<input type="hidden" name="noteId" value="0">
									<input type="hidden" name="note_edit" value="new">
									<input type="hidden" name="sign" value="off">
									<input type="hidden" name="verify" value="off">
									<input type="hidden" name="forceNote" value="false">
									<input type="hidden" name="newNoteIdx" value="">
									<input type="hidden" name="notes2print" id="notes2print"
										   value="">
									<input type="hidden" name="printCPP" id="printCPP"
										   value="false">
									<input type="hidden" name="printRx" id="printRx" value="false">
									<input type="hidden" name="printLabs" id="printLabs"
										   value="false">
									<input type="hidden" name="encType" id="encType" value="">
									<input type="hidden" name="pStartDate" id="pStartDate" value="">
									<input type="hidden" name="pEndDate" id="pEndDate" value="">
									<input type="hidden" id="annotation_attribname"
										   name="annotation_attribname" value="">
									<%
										if (OscarProperties.getInstance().getBooleanProperty("note_program_ui_enabled", "true"))
										{
									%>
									<input type="hidden" name="_note_program_no" value=""/>
									<input type="hidden" name="_note_role_id" value=""/>
									<% } %>

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
										<script type="text/javascript">

											/*
											if (parseInt(navigator.appVersion) > 3)
											{
												var windowHeight = 750;
												if (navigator.appName == "Netscape")
												{
													windowHeight = window.innerHeight;
												}
												if (navigator.appName.indexOf("Microsoft") != -1)
												{
													windowHeight = document.body.offsetHeight;
												}

												// XXX: this seems like it generally won't work (ocean, etc.)
												var divHeight = windowHeight - 320;
												$("encMainDiv").style.height = divHeight + 'px';
											}

											 */
										</script>
										<div id='save'
											 style="width: 99%; background-color: #CCCCFF; padding-top: 5px; margin-left: 2px; border-left: thin solid #000000; border-right: thin solid #000000; border-bottom: thin solid #000000;">
		<span style="float: right; margin-right: 5px;">

		<div class="encounter_timer_container">
			<div style="display: inline-block; position:relative;">
				<input id="encounter_timer" title="Paste timer data" type="button"
					   onclick="encounterTimer.putEncounterTimeInNote()" value="00:00"/>
			</div>
			<input id="encounter_timer_pause" class="encounter_timer_control" type="button"
				   onclick="encounterTimer.toggleEncounterTimer('#encounter_timer_pause', '#encounter_timer_play')"
				   value="||"/>
			<input id="encounter_timer_play" class="encounter_timer_control" type="button"
				   onclick="encounterTimer.toggleEncounterTimer('#encounter_timer_pause', '#encounter_timer_play')"
				   value="&gt;"/>
		</div>
			<%--
		<%

			// TODO: make this work (parts missing)
			if(facility.isEnableGroupNotes()) {
		%>
			<input tabindex="16" type='image' src="<c:out value="${ctx}/oscarEncounter/graphics/group-gnote.png"/>" id="groupNoteImg" onclick="Event.stop(event);return selectGroup(document.forms['caseManagementEntryForm'].elements['caseNote.program_no'].value,document.forms['caseManagementEntryForm'].elements['demographicNo'].value);" title='<bean:message key="oscarEncounter.Index.btnGroupNote"/>'>&nbsp;
		<%  }
			if(facility.isEnablePhoneEncounter()) {
		%>
			<input tabindex="25" type='image' src="<c:out value="${ctx}/oscarEncounter/graphics/attach.png"/>" id="attachNoteImg" onclick="Event.stop(event);return assign(document.forms['caseManagementEntryForm'].elements['caseNote.program_no'].value,document.forms['caseManagementEntryForm'].elements['demographicNo'].value);" title='<bean:message key="oscarEncounter.Index.btnAttachNote"/>'>&nbsp;
		<%  } %>
		--%>
			<input tabindex="17" type='image'
				   src="<c:out value="${ctx}/oscarEncounter/graphics/media-floppy.png"/>"
				   id="saveImg"
				   onclick="Event.stop(event);return encounterNote.saveEncounterNote(false, false, false, true, false);"
				   title='<bean:message key="oscarEncounter.Index.btnSave"/>'>&nbsp;
			<input tabindex="18" type='image'
				   src="<c:out value="${ctx}/oscarEncounter/graphics/document-new.png"/>"
				   id="newNoteImg" onclick="newNote(event); return false;"
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
			<c:if test="${junoEncounterForm.header.source == null}">
				<input tabindex="21" type='image'
					   src="<c:out value="${ctx}/oscarEncounter/graphics/dollar-sign-icon.png"/>"
					   onclick="Event.stop(event);return encounterNote.saveEncounterNote(true, false, true, true, true);"
					   title='<bean:message key="oscarEncounter.Index.btnBill"/>'>&nbsp;
			</c:if>
	    	<input tabindex="23" type='image'
				   src="<c:out value="${ctx}/oscarEncounter/graphics/system-log-out.png"/>"
				   onclick='closeEnc(event);return false;'
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
				   size="30">&nbsp;
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
				   onclick="caseManagementIssue.changeIssue({jQuery: jQuery}); return false;"
				   value="<bean:message key="oscarEncounter.change.title"/>">
			<span id="busy" style="display: none">
	    		<img style="position: absolute;"
					 src="<c:out value="${ctx}/oscarEncounter/graphics/busy.gif"/>"
					 alt="<bean:message key="oscarEncounter.Index.btnWorking" />">
	    	</span>
		</div>
		<div style="padding-top: 3px;">
			<button type="button" id="displayResolvedIssuesButton"/>
<%--
					onclick="return showHideIssues(event, true);">
--%>
				<bean:message
						key="oscarEncounter.Index.btnDisplayResolvedIssues"/></button>
			&nbsp;
			<button type="button" id="displayUnresolvedIssuesButton"/>
<%--
					onclick="return showHideIssues(event, false);">
--%>
				<bean:message
						key="oscarEncounter.Index.btnDisplayUnresolvedIssues"/></button>
			&nbsp;
			<button type="button"
					onclick="javascript:spellCheck();">Spell
				Check
			</button> &nbsp;
			<button type="button"
					onclick="javascript:toggleFullViewForAll(this.form);">
				<bean:message key="eFormGenerator.expandAll"/>
				<bean:message
						key="Appointment.formNotes"/></button>
			<button type="button"
					onclick="javascript:popupPage(500,200,'noteBrowser${junoEncounterForm.header.demographicNo}','../casemgmt/noteBrowser.jsp?demographic_no=${junoEncounterForm.header.demographicNo}&FirstTime=1');">
				<bean:message
						key="oscarEncounter.Index.BrowseNotes"/></button>
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

									<%--
									<input type="hidden" id="reloadUrl" name="reloadUrl" value="">
									<input type="hidden" id="containerDiv" name="containerDiv" value="">
									--%>
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
						   <%--onclick="this.focus();$('channel').style.visibility ='visible';$('showEditNote').style.display='none';return false;">--%>
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
												 src='<c:out value="${ctx}"/>/oscarEncounter/graphics/printer.png'>&nbsp;<bean:message
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
												 src='<c:out value="${ctx}"/>/oscarEncounter/graphics/printer.png'>&nbsp;<bean:message
												key="oscarEncounter.Rx.title"/></td>
									</tr>
									<tr>
										<td></td>
										<td><img style="cursor: pointer;"
												 title="<bean:message key="oscarEncounter.print.title"/>"
												 id='imgPrintLabs'
												 alt="<bean:message key="oscarEncounter.togglePrintLabs.title"/>"
												 onclick="return printNotes.printInfo(this, 'printLabs');"
												 src='<c:out value="${ctx}"/>/oscarEncounter/graphics/printer.png'>&nbsp;<bean:message
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

								<%
									if (OscarProperties.getInstance().getBooleanProperty("note_program_ui_enabled", "true"))
									{
								%>
								<span class="popup" style="display: none;" id="_program_popup">
					<div class="arrow"></div>
					<div class="contents">
						<div class="selects">
							<select class="selectProgram"></select> <select class="role"></select>
						</div>
						<div class="under">
							<div class="errorMessage"></div>
							<input type="button" class="scopeBtn" value="View Note Scope"/>
							<input type="button" class="closeBtn" value="Close"/> <input
								type="button" class="saveBtn" value="Save"/>
						</div>
					</div>
				</span>

								<div id="_program_scope" class="_program_screen"
									 style="display: none;">
									<div class="_scopeBox">
										<div class="boxTitle">
											<span class="text">Note Permission Summary</span><span
												class="uiBigBarBtn"><span
												class="text">x</span></span>
										</div>
										<table class="details">
											<tr>
												<th>Program Name (of this note)</th>
												<td class="programName">...</td>
											</tr>
											<tr>
												<th>Role Name (of this note)</th>
												<td class="roleName">...</td>
											</tr>
										</table>
										<div class="explanation">The following is a summary of what
											kind of access providers in the above program have to
											this note.
										</div>
										<div class="loading">Loading...</div>
										<table class="permissions"></table>
									</div>
								</div>
								<%
									}
								%>
							</form>
						</div>


						<script type="text/javascript">
							/*
							document.observe('dom:loaded', function(){
								init();
							});
							*/

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
