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
<%@ page import="org.oscarehr.casemgmt.web.formbeans.*, org.oscarehr.casemgmt.model.CaseManagementNote"%>
<%@ page import="org.oscarehr.common.dao.UserPropertyDAO, oscar.OscarProperties" %>
<%@ page import="org.oscarehr.common.model.UserProperty" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.util.LoggedInInfo" %>
<%@ page import="org.oscarehr.casemgmt.common.Colour" %>
<%@ page import="org.oscarehr.provider.dao.ProviderDataDao" %>
<%@ page import="org.oscarehr.provider.model.ProviderData"%>
<%@ page import="java.util.List"%>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="oscar.util.StringUtils" %>

<jsp:useBean id="junoEncounterForm" scope="request" type="org.oscarehr.casemgmt.web.formbeans.JunoEncounterFormBean"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
	//
	////oscar.oscarEncounter.pageUtil.EctSessionBean bean = null;
	//String beanName = "casemgmt_oscar_bean" + (String) request.getAttribute("demographicNo");

	//pageContext.setAttribute("providerNo",request.getParameter("providerNo"), PageContext.PAGE_SCOPE);
	//pageContext.setAttribute("demographicNo",request.getParameter("demographicNo"), PageContext.PAGE_SCOPE);

	//org.oscarehr.casemgmt.model.CaseManagementNoteExt cme = new org.oscarehr.casemgmt.model.CaseManagementNoteExt();

	//String frmName = "caseManagementEntryForm" + request.getParameter("demographicNo");
	//CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean)session.getAttribute(frmName);

	//String encTimeMandatoryValue = OscarProperties.getInstance().getProperty("ENCOUNTER_TIME_MANDATORY","false");

%>

<html:html locale="true">
	<head>
		<c:set var="ctx" value="${pageContext.request.contextPath}"	scope="request" />

		<link rel="stylesheet" href="<c:out value="${ctx}"/>/css/casemgmt.css" type="text/css">
		<link rel="stylesheet" href="<c:out value="${ctx}"/>/oscarEncounter/encounterStyles.css" type="text/css">
		<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}"/>/css/print.css" media="print">

		<script src="<c:out value="${ctx}/js/jquery-1.7.1.min.js"/>"></script>
		<script language="javascript">
			jQuery.noConflict();
		</script>

		<script src="<c:out value="${ctx}"/>/share/javascript/prototype.js" type="text/javascript"></script>
		<script src="<c:out value="${ctx}"/>/share/javascript/scriptaculous.js" type="text/javascript"></script>

		<script type="text/javascript" src="<c:out value="${ctx}"/>/js/messenger/messenger.js"> </script>
		<script type="text/javascript" src="<c:out value="${ctx}"/>/js/messenger/messenger-theme-future.js"> </script>
		<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}"/>/js/messenger/messenger.css"> </link>
		<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}"/>/js/messenger/messenger-theme-future.css"> </link>

<%--		XXX: Removed this because I want to avoid stuff like this if I can--%>
<%--		<script type="text/javascript" src="newEncounterLayout.js.jsp"> </script>--%>

			<%-- for popup menu of forms --%>
		<script src="<c:out value="${ctx}"/>/share/javascript/popupmenu.js" type="text/javascript"></script>
		<script src="<c:out value="${ctx}"/>/share/javascript/menutility.js" type="text/javascript"></script>

		<!-- library for rounded elements -->
		<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}/share/css/niftyCorners.css" />">
		<script type="text/javascript" src="<c:out value="${ctx}/share/javascript/nifty.js"/>"></script>

		<!-- calendar stylesheet -->
		<link rel="stylesheet" type="text/css" media="all" href="<c:out value="${ctx}"/>/share/calendar/calendar.css" title="win2k-cold-1">

		<!-- main calendar program -->
		<script type="text/javascript" src="<c:out value="${ctx}"/>/share/calendar/calendar.js"></script>

		<!-- language for the calendar -->
		<script type="text/javascript" src="<c:out value="${ctx}"/>/share/calendar/lang/<bean:message key="global.javascript.calendar"/>"></script>

		<!-- the following script defines the Calendar.setup helper function, which makes adding a calendar a matter of 1 or 2 lines of code. -->
		<script type="text/javascript" src="<c:out value="${ctx}"/>/share/calendar/calendar-setup.js"></script>

		<!-- js window size utility funcs since prototype's funcs are buggy in ie6 -->
		<script type="text/javascript" src="<c:out value="${ctx}/share/javascript/screen.js"/>"></script>

		<!-- scriptaculous based select box -->
		<script type="text/javascript" src="<c:out value="${ctx}/share/javascript/select.js"/>"></script>

		<!-- phr popups -->
		<script type="text/javascript" src="<c:out value="${ctx}/phr/phr.js"/>"></script>

		<script type="text/javascript">

			var cppIssues = new Array(7);
			var cppNames = new Array(7);
			cppIssues[0] = "SocHistory";
			cppIssues[1] = "MedHistory";
			cppIssues[2] = "FamHistory";
			cppIssues[3] = "Concerns";
			cppIssues[4] = "RiskFactors";
			cppIssues[5] = "Reminders";
			cppIssues[6] = "OMeds";
			cppNames[0] = "Social History";
			cppNames[1] = "Medical History";
			cppNames[2] = "Family History";
			cppNames[3] = "Ongoing Concerns";
			cppNames[4] = "Risk Factors";
			cppNames[5] = "Reminders";
			cppNames[6] = "Other Meds";

			function getCPP(issueCode)
			{
				for (var i = 0; i < cppIssues.length; i++)
				{
					if (issueCode == cppIssues[i])
					{
						return cppNames[i];
					}
				}
				return "";
			}

			var exFields = new Array(11);
			var exKeys = new Array(11);
			exFields[0] = "startdate";
			exFields[1] = "resolutiondate";
			exFields[2] = "proceduredate";
			exFields[3] = "ageatonset";
			exFields[4] = "treatment";
			exFields[5] = "problemstatus";
			exFields[6] = "exposuredetail";
			exFields[7] = "relationship";
			exFields[8] = "lifestage";
			exFields[9] = "hidecpp";
			exFields[10] = "problemdescription";
			exKeys[0] = "Start Date";
			exKeys[1] = "Resolution Date";
			exKeys[2] = "Procedure Date";
			exKeys[3] = "Age at Onset";
			exKeys[4] = "Treatment";
			exKeys[5] = "Problem Status";
			exKeys[6] = "Exposure Details";
			exKeys[7] = "Relationship";
			exKeys[8] = "Life Stage";
			exKeys[9] = "Hide Cpp";
			exKeys[10] = "Problem Description";

			function prepareExtraFields(cpp, exts)
			{
				//commented out..this causes a problem in Firefox
				//console.log("prepare Extra Fields");
				var rowIDs = new Array(10);
				for (var i = 2; i < exFields.length; i++)
				{
					rowIDs[i] = "Item" + exFields[i];
					$(rowIDs[i]).hide();
				}
				if (cpp == cppNames[1]) $(rowIDs[2], rowIDs[4], rowIDs[8], rowIDs[9]).invoke("show");
				if (cpp == cppNames[2]) $(rowIDs[3], rowIDs[4], rowIDs[7], rowIDs[8], rowIDs[9]).invoke("show");
				if (cpp == cppNames[3]) $(rowIDs[5], rowIDs[8], rowIDs[9], rowIDs[10]).invoke("show");
				if (cpp == cppNames[4]) $(rowIDs[3], rowIDs[6], rowIDs[8], rowIDs[9]).invoke("show");

				for (var i = 0; i < exFields.length; i++)
				{
					$(exFields[i]).value = "";
				}

				var extsArr = exts.split(";");
				for (var i = 0; i < extsArr.length; i += 2)
				{
					for (var j = 0; j < exFields.length; j++)
					{
						if (extsArr[i] == exKeys[j])
						{
							$(exFields[j]).value = extsArr[i + 1];
							continue;
						}
					}
				}
			}

			// TODO: Put all of this somewhere else?  It seems like it might be duplicated all
			//       over the place, but I don't know if I can do anytyhing about that.
			function checkLengthofObject(o)
			{
				var c = 0;
				for (var attr in o)
				{
					if (o.hasOwnProperty(attr))
					{
						++c;
					}
				}

				return c;
			}

			//open a new popup window
			function popupPage(vheight, vwidth, name, varpage)
			{
				var openWindows = {};
				var reloadWindows = {};
				var updateDivTimer = null;
				if (varpage == null || varpage == -1)
				{
					return false;
				}
				if (varpage.indexOf("..") == 0)
				{
					varpage = ctx + varpage.substr(2);
				}
				var page = "" + varpage;
				var windowprops = "height=" + vheight + ",width=" + vwidth + ",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=600,screenY=200,top=0,left=0";
				openWindows[name] = window.open(page, name, windowprops);

				if (openWindows[name] != null)
				{
					if (openWindows[name].opener == null)
					{
						openWindows[name].opener = self;
					}
					openWindows[name].focus();
					if (updateDivTimer == null)
					{
						updateDivTimer = setInterval(
							function()
							{

								if (checkLengthofObject(openWindows) > 0)
								{
									for (var name in openWindows)
									{
										if (openWindows[name].closed && reloadWindows[name] != undefined)
										{
											var reloadDivUrl = reloadWindows[name];
											var reloadDiv = reloadWindows[name + "div"];

											loadDiv(reloadDiv, reloadDivUrl, 0);

											delete reloadWindows[name];
											var divName = name + "div";
											delete reloadWindows[divName];
											delete openWindows[name];
										}

									}

								}
								if (checkLengthofObject(openWindows) == 0)
								{
									clearInterval(updateDivTimer);
									updateDivTimer = null;
								}

							}, 1000);
					}
				}

			}

			//This object stores the key -> cmd value passed to action class and the id of the created div
			// and the value -> URL of the action class

			function popupUploadPage(varpage,dn)
			{
				var page = "" + varpage+"?demographicNo="+dn;
				windowprops = "height=500,width=500,location=no,"
					+ "scrollbars=no,menubars=no,toolbars=no,resizable=yes,top=50,left=50";
				var popup=window.open(page, "", windowprops);
				popup.focus();

			}

			function delay(time)
			{
				string="document.getElementById('ci').src='${fn:escapeXml(junoEncounterForm.header.imagePresentPlaceholderUrl)}'";
				setTimeout(string,time);
			}

			function openAnnotation()
			{
				var atbname = document.getElementById('annotation_attrib').value;
				var data = $A(arguments);
				var addr = ctx + "/annotation/annotation.jsp?atbname=" + atbname + "&table_id=" + data[1] + "&display=" + data[2] + "&demo=" + data[3];
				window.open(addr, "anwin", "width=400,height=500");
				Event.stop(data[0]);
			}

			function showEdit(e, title, noteId, editors, date, revision, note, url, numNotes, position, reloadUrl, noteIssues, noteExts, demoNo)
			{

				//var limit = containerDiv + "threshold";
				var editElem = "showEditNote";
				//var pgHeight = pageHeight();

/*				var coords = null;
				if (document.getElementById("measurements_div") == null)
				{
					coords = Position.page($("topContent"));
				}
				else
				{
					var coords = Position.positionedOffset($("cppBoxes"));
				}*/

				var coords = Position.positionedOffset($("cppBoxes"));

				var top = Math.max(coords[1], 0);
				var right = Math.round(coords[0] / 0.66);
				//var height = $("showEditNote").getHeight();
				var gutterMargin = 150;

				if (right < gutterMargin)
					right = gutterMargin;


				$("noteEditTxt").value = note;

				var editorUl = "<ul style='list-style: none outside none; margin:0px;'>";

				if (editors.length > 0)
				{
					var editorArray = editors.split(";");
					var idx;
					for (idx = 0; idx < editorArray.length; ++idx)
					{
						if (idx % 2 == 0)
							editorUl += "<li>" + editorArray[idx];
						else
							editorUl += "; " + editorArray[idx] + "</li>";
					}

					if (idx % 2 == 0)
						editorUl += "</li>";
				}
				editorUl += "</ul>";

				var noteIssueUl = "<ul id='issueIdList' style='list-style: none outside none; margin:0px;'>";

				if (noteIssues.length > 0)
				{
					var issueArray = noteIssues.split(";");
					var idx, rows;
					var cppDisplay = "";
					for (idx = 0, rows = 0; idx < issueArray.length; idx += 3, ++rows)
					{
						if (rows % 2 == 0)
							noteIssueUl += "<li><input type='checkbox' id='issueId' name='issue_id' checked value='" + issueArray[idx] + "'>" + issueArray[idx + 2];
						else
							noteIssueUl += "&nbsp; <input type='checkbox' id='issueId' name='issue_id' checked value='" + issueArray[idx] + "'>" + issueArray[idx + 2] + "</li>";

						if (cppDisplay == "") cppDisplay = getCPP(issueArray[idx + 1]);
					}

					if (rows % 2 == 0)
						noteIssueUl += "</li>";
				}
				noteIssueUl += "</ul>";

				var noteInfo = "<div style='float:right;'><i>Encounter Date:&nbsp;" + date + "&nbsp;rev<a href='#' onclick='return showHistory(\"" + noteId + "\",event);'>" + revision + "</a></i></div>" +
					"<div><span style='float:left;'>Editors: </span>" + editorUl + noteIssueUl + "</div><br style='clear:both;'>";

				$("issueNoteInfo").update(noteInfo);
				$("frmIssueNotes").action = url;
				$("reloadUrl").value = reloadUrl;
				$("containerDiv").value = containerDiv;
				$("winTitle").update(title);

				$(editElem).style.right = right + "px";
				$(editElem).style.top = top + "px";
				// XXX: don't show integrator stuff
				//$("showIntegratedNote").style.display = "none";
				if (Prototype.Browser.IE)
				{
					//IE6 bug of showing select box
					$("channel").style.visibility = "hidden";
					$(editElem).style.display = "block";
				}
				else
				{
					$(editElem).style.display = "table";
				}

				//Prepare Annotation Window & Extra Fields
				var now = new Date();
				document.getElementById('annotation_attrib').value = "anno" + now.getTime();
				var obj = {};
				Element.observe('anno', 'click', openAnnotation.bindAsEventListener(obj, noteId, cppDisplay, demoNo));
				prepareExtraFields(cppDisplay, noteExts);

				//Set note position order
				//var elementNum = containerDiv + "num";
				//var numNotes = $F(elementNum);
				//var positionElement = containerDiv + noteId;
				//var position;
				//if (noteId == "")
				//{
				//	position = 0;
				//}
				//else
				//{
				//	position = $F(positionElement);
				//}

				var curElem;
				var numOptions = $("position").length;
				var max = numNotes > numOptions ? numNotes : numOptions;
				var optId;
				var option;
				var opttxt;

				for (curElem = 0; curElem < max; ++curElem)
				{

					optId = "popt" + curElem;
					if ($(optId) == null)
					{
						option = document.createElement("OPTION");
						option.id = optId;
						opttxt = curElem + 1;
						option.text = "" + opttxt;
						option.value = curElem;
						$("position").options.add(option, curElem);
					}

					if (position == curElem)
					{
						$(optId).selected = true;
					}
				}

				if (max == numNotes)
				{
					optId = "popt" + max;
					if ($(optId) == null)
					{
						option = document.createElement("OPTION");
						option.id = optId;
						opttxt = 1 * max + 1;
						option.text = "" + opttxt;
						option.value = max;
						$("position").options.add(option, max);
					}

				}

				for (curElem = max - 1; curElem > 0; --curElem)
				{

					optId = "popt" + curElem;
					if (curElem > numNotes)
					{
						Element.remove(optId);
					}
				}


				$("noteEditTxt").focus();

				return false;
			}

			function assembleMainChartParams(displayFullChart) {

				var params = "method=edit&ajaxview=ajaxView&fullChart=" + displayFullChart;
				<%
				  Enumeration<String>enumerator = request.getParameterNames();
				  String paramName, paramValue;
				  while( enumerator.hasMoreElements() ) {
					 paramName = enumerator.nextElement();
					 if( paramName.equals("method") || paramName.equals("fullChart") ) {
						 continue;
					 }

					 paramValue = request.getParameter(paramName);

				 %>
				params += "&<%=paramName%>=<%=URLEncoder.encode(StringUtils.transformNullInEmptyString(paramValue), "UTF-8")%>";
				<%
				 }
			   %>

				return params;
			}

			function scrollDownInnerBar()
			{
				$("encMainDiv").scrollTop = $("encMainDiv").scrollHeight;
			}

			function viewFullChart(ctx, displayFullChart)
			{

				var url = ctx + "/CaseManagementEntry.do";
				var params = assembleMainChartParams(displayFullChart);

				if (displayFullChart)
				{
					fullChart = "true";
				}
				else
				{
					fullChart = "false";
				}

				$("notCPP").update("Loading...");
				var objAjax = new Ajax.Request(
					url,
					{
						method: 'post',
						postBody: params,
						evalScripts: true,
						onSuccess: function(request)
						{
							$("notCPP").update(request.responseText);
							$("notCPP").style.height = "50%";
							if (displayFullChart)
							{
								$("quickChart").innerHTML = quickChartMsg;
								$("quickChart").onclick = function()
								{
									return viewFullChart(false);
								}
								scrollDownInnerBar();

							}
							else
							{
								$("quickChart").innerHTML = fullChartMsg;
								$("quickChart").onclick = function()
								{
									return viewFullChart(true);
								}
								scrollDownInnerBar();
							}
						},
						onFailure: function(request)
						{
							$("notCPP").update("Error: " + request.status + request.responseText);
						}
					}
				);
				return false;
			}

			function init() {
				var ctx = '<c:out value="${ctx}"/>';


				// XXX: This is required to set some session state to make saving a note work.
				//      I feel like this is a terrible idea and should be removed with predjudice.
				//viewFullChart(ctx, false);

				/*
				showIssueNotes();

				var navBars = new navBarLoader();
				navBars.load();

				monitorNavBars(null);

				Element.observe(window, "resize", monitorNavBars);
				*/

				if(!NiftyCheck()) {
					return;
				}

				Rounded("div.showEdContent","all","transparent","#CCCCCC","big border #000000");
				Rounded("div.printOps","all","transparent","#CCCCCC","big border #000000");
				Calendar.setup({ inputField : "printStartDate", ifFormat : "%d-%b-%Y", showsTime :false, button : "printStartDate_cal", singleClick : true, step : 1 });
				Calendar.setup({ inputField : "printEndDate", ifFormat : "%d-%b-%Y", showsTime :false, button : "printEndDate_cal", singleClick : true, step : 1 });

				<%--
				<c:url value="/CaseManagementEntry.do" var="issueURLCPP">
				<c:param name="method" value="issueList"/>
				<c:param name="demographicNo" value="${demographicNo}" />
				<c:param name="providerNo" value="${providerNo}" />
				<c:param name="all" value="true" />
				</c:url>
				var issueAutoCompleterCPP = new Ajax.Autocompleter("issueAutocompleteCPP", "issueAutocompleteListCPP", "<c:out value="${issueURLCPP}" />", {minChars: 3, indicator: 'busy2', afterUpdateElement: addIssue2CPP, onShow: autoCompleteShowMenuCPP, onHide: autoCompleteHideMenuCPP});

				<nested:notEmpty name="DateError">
				alert("<nested:write name="DateError"/>");
				</nested:notEmpty>
				--%>
			}

/*			$(document).ready(function()
			{
				init();
			});

			document.observe('dom:loaded', function(){
				init();
			});

 */

		</script>


		<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}/css/oscarRx.css" />">

<%--

		XXX: Not sure what this is
		<oscar:customInterface section="cme" />
--%>

		<style type="text/css">

			/*CPP Format */
			li.cpp {
				color: #000000;
				font-family: arial, sans-serif;
				text-overflow: ellipsis;
				overflow: hidden;
			}

			/*Note format */
			div.newNote {
				color: #000000;
				font-family: arial, sans-serif;
				font-size: 0.8em;
				margin: 5px 0px 5px 5px;
				float: left;
				width: 98%;
			}

			div.newNote pre {
				color: #000000;
				font-family: arial, sans-serif;
				margin: 0px 3px 0px 3px;
				width: 100%;
				clear: left;
			}

			div.note {
				color: #000000;
				font-family: arial, sans-serif;
				margin: 3px 0px 3px 5px;
				float: left;
				width: 98%;
			}

			div.note pre {
				color: #000000;
				font-family: arial, sans-serif;
				margin: 0px 3px 0px 3px;
				width: 100%;
				clear: left;
			}

			.sig {
				background-color: #CCCCFF;
				color: #000000;
				width: 100%;
				font-size: 9px;
			}

			.txtArea {
				font-family: arial, sans-serif;
				font-size: 1.0em;
				width: 99%;
				rows: 10;
				overflow: hidden;
				border: none;
				font-family: arial, sans-serif;
				margin: 0px 3px 0px 3px;
			}

			p.passwd {
				margin: 0px 3px 0px 3px;
			}

			/* span formatting for measurements div found in ajax call */
			span.measureCol1 {
				float: left;
				width: 50px;
			}

			span.measureCol2 {
				float: left;
				width: 55px;
			}

			span.measureCol3 {
				float: left;
			}

			.topLinks {
				color: black;
				text-decoration: none;
				font-size: 9px;
			}

			.topLinkhover {
				color: blue;
				text-decoration: underline;
			}

			/* formatting for navbar */
			.links {
				color: blue;
				text-decoration: none;
				font-size: 9px;
			}

			.linkhover {
				color: black;
				text-decoration: underline;
			}

			/* template styles*/
			.enTemplate_name_auto_complete {
				width: 350px;
				background: #fff;
				font-size: 9px;
				text-align: left;
			}

			.enTemplate_name_auto_complete ul {
				border: 1px solid #888;
				margin: 0;
				padding: 0;
				width: 100%;
				list-style-type: square;
				list-style-position: inside;
			}

			.enTemplate_name_auto_complete ul li {
				margin: 0;
				padding: 3px;
			}

			.enTemplate_name_auto_complete ul li.selected {
				background-color: #ffb;
				text-decoration: underline;
			}

			.enTemplate_name_auto_complete ul strong.highlight {
				color: #800;
				margin: 0;
				padding: 0;
			}

			/* CPP textareas */
			.rowOne {
				height: <%--<nested:write name="rowOneSize"/>--%>10px;
				width: 97%;
				overflow: auto;
			}

			.rowTwo {
				height: <%--<nested:write name="rowTwoSize"/>--%>10px;
				width: 97%;
				margin-left: 4px;
				overflow: auto;
			}

			/* Encounter type select box */
			div.autocomplete {
				position: absolute;
				width: 400px;
				background-color: white;
				border: 1px solid #ccc;
				margin: 0px;
				padding: 0px;
				font-size: 9px;
				text-align: left;
				max-height: 200px;
				overflow: auto;
			}

			div.autocomplete ul {
				list-style-type: none;
				margin: 0px;
				padding: 0px;
			}

			div.autocomplete ul li.selected {
				background-color: #EAF2FB;
			}

			div.autocomplete ul li {
				list-style-type: none;
				display: block;
				margin: 0;
				padding: 2px;
				cursor: pointer;
			}

			.encTypeCombo /* look&feel of scriptaculous select box*/ {
				margin: 0px; /* 5px 10px 0px;*/
				font-family: Verdana, Geneva, Arial, Helvetica, sans-serif;
				font-size: 9px;
				width: 200px;
				text-align: left;
				vertical-align: middle;
				background: #FFFFFF
				url('<c:out value="${ctx}"/>/images/downarrow_inv.gif') no-repeat
				right;
				height: 18px;
				cursor: pointer;
				border: 1px solid #ccc;
				color: #000000;
			}

			.printOps {
				background-color: #CCCCFF;
				font-size: 9px;
				position: absolute;
				display: none;
				z-index: 1;
				width: 200px;
				right: 100px;
				bottom: 200px;
			}

			.showEdContainer {
				position: absolute;
				display: none;
				z-index: 100;
				right: 100px;
				bottom: 200px;
				background-color: transparent;
				font-size: 8px;
				/*border: thin ridge black;*/
				text-align: center;
			}

			.showEdPosition {
				display: table-cell;
				vertical-align: middle;
			}

			.showEdContent { /*border: thin ridge black;*/
				background-color: #CCCCFF;
				font-size: 9px;
				position: absolute;
				display: none;
				z-index: 200;
				right: 100px;
				bottom: 200px;
				text-align: center;
			}

			.showResident {
				left: 0;
				top: 0;
				/*transform: translate(100%, 100%);*/
				min-width: 100%;
				min-height: 100%;
				background: rgba(239,250,250,0.6);

				position: absolute;
				display: none;
				z-index: 300;
				text-align: center;
				border-style: ridge;
			}

			.showResidentBorder {
				background: rgba(239,250,250,1);
				border-style: ridge;
				text-align: center;
				width: 45%;
				height:auto;
				margin: 40% auto;
				position:relative;
			}

			.showResidentContent {
				background: rgba(13,117,173,1);
				text-align: center;
				width:auto;
				height: auto;
				margin: 2% auto;
				border-style: inset;
				position: relative;
			}

			.residentText {
				font-family: "Times New Roman", Times, serif;
				font-style: italic;
			}

			.supervisor {
			}

			.reviewer {
			}

			div.encounterHeaderContainer {
				float:left;
				width: 100%;
				padding-left:2px;
				text-align:left;
				font-size: 12px;
				color: ${fn:escapeXml(junoEncounterForm.header.inverseUserColour)}; <%--<bean:write name="junoEncounterForm" property="header.inverseUserColour" />;--%>
				background-color: <bean:write name="junoEncounterForm" property="header.userColour" />;
			}

			div.encounterHeaderContainer span.Header {
				color: <bean:write name="junoEncounterForm" property="header.inverseUserColour" />;
				background-color: <bean:write name="junoEncounterForm" property="header.userColour" />;
			}

			div.encounterHeaderContainer span.familyDoctorInfo {
				border-bottom: medium solid <bean:write name="junoEncounterForm" property="header.familyDoctorColour" />;
			}
		</style>

		<html:base />
		<title><bean:message key="oscarEncounter.Index.title" /> - <oscar:nameage
				demographicNo="<%=request.getParameter(\"demographicNo\")%>" /></title>
	</head>
	<body id="body" style="margin: 0px;">

		<div id="header">
			<div class="encounterHeaderContainer" id="encounterHeader">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>
							<security:oscarSec roleName="${junoEncounterForm.header.roleName}" objectName="_newCasemgmt.doctorName" rights="r">
								<span class="familyDoctorInfo">
									<bean:message key="oscarEncounter.Index.msgMRP"/>
									&nbsp;&nbsp;<bean:write name="junoEncounterForm" property="header.formattedFamilyDoctorName" />
								</span>
							</security:oscarSec>
							<span class="Header">
								<%

								%>
								<a href="#" onClick="popupPage(700,1000,'<bean:write name="junoEncounterForm" property="header.windowName" />','<c:out value="${ctx}"/><bean:write name="junoEncounterForm" property="header.demographicUrl" />'); return false;" title="<bean:message key="provider.appointmentProviderAdminDay.msgMasterFile"/>"><bean:write name="junoEncounterForm" property="header.formattedPatientName" /></a> <bean:write name="junoEncounterForm" property="header.formattedPatientInfo" />
								<c:if test="${junoEncounterForm.header.echartAdditionalPatientInfoEnabled}">
									<bean:write name="junoEncounterForm" property="header.patientBirthdate" />
								</c:if>

								<% //if (oscar.OscarProperties.getInstance().isEChartAdditionalPatientInfoEnabled())
								//{%>
								<%//}%>

								&nbsp;<oscar:phrverification demographicNo="${junoEncounterForm.header.demographicNo}"><bean:message key="phr.verification.link"/></oscar:phrverification> &nbsp;<bean:write name="junoEncounterForm" property="header.patientPhone" />
								<span id="encounterHeaderExt"></span>
								<security:oscarSec roleName="${junoEncounterForm.header.roleName}" objectName="_newCasemgmt.apptHistory" rights="r">
									<a href="javascript:popupPage(400,850,'ApptHist','<c:out value="${ctx}"/><bean:write name="junoEncounterForm" property="header.demographicAdditionalInfoUrl" />')" style="font-size: 11px;text-decoration:none;" title="<bean:message key="oscarEncounter.Header.nextApptMsg"/>"><span style="margin-left:20px;"><bean:message key="oscarEncounter.Header.nextAppt"/>: <oscar:nextAppt demographicNo="${junoEncounterForm.header.demographicNo}"/></span></a>
								</security:oscarSec>
								&nbsp;&nbsp;

								<c:if test="${junoEncounterForm.header.echartAdditionalPatientInfoEnabled}">
									<bean:write name="junoEncounterForm" property="header.referringDoctorName" />
									<bean:write name="junoEncounterForm" property="header.referringDoctorNumber" />
									&nbsp;&nbsp;
									<c:if test="${junoEncounterForm.header.rosterDateEnabled}">
										Referral date:
										<bean:write name="junoEncounterForm" property="header.rosterDateString" />
									</c:if>
								</c:if>

								<c:if test="${junoEncounterForm.header.incomingRequestorSet}">
									<a href="javascript:void(0)" onClick="popupPage(600,175,'Calculators','${fn:escapeXml(junoEncounterForm.header.diseaseListUrl)}'); return false;" ><bean:message key="oscarEncounter.Header.OntMD"/></a>
								</c:if>
								<c:out value="${junoEncounterForm.header.echartLinks}" />
								&nbsp;&nbsp;

							</span>
						</td>
						<td align=right>
							<span class="HelpAboutLogout">
								<oscar:help keywords="&Title=Chart+Interface&portal_type%3Alist=Document" key="app.top1" style="font-size:10px;font-style:normal;"/>&nbsp;|
								<a style="font-size:10px;font-style:normal;" href="<%=request.getContextPath()%>/oscarEncounter/About.jsp" target="_new"><bean:message key="global.about" /></a>
							</span>
						</td>
					</tr>
				</table>
			</div>
		</div>



		<div id="rightNavBar" style="display: inline; float: right; width: 20%; margin-left: -3px;">
			<%
			String demo=request.getParameter("demographicNo");
			String roleName$ = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");

			// XXX: Do we want the sharing center?
			// MARC-HI's Sharing Center
			//boolean isSharingCenterEnabled = SharingCenterUtil.isEnabled();

			%>

			<!--dummmy div to force browser to allocate space -->
			<security:oscarSec roleName="<%=roleName$%>" objectName="_newCasemgmt.photo" rights="r">
				<c:choose>
					<c:when test="${not empty requestScope.image_exists}">
						<img style="cursor: pointer;" id="ci" src="${fn:escapeXml(junoEncounterForm.header.imagePresentPlaceholderUrl)}" alt="id_photo" height="100" title="Click to upload new photo."
							 OnMouseOver="document.getElementById('ci').src='../imageRenderingServlet?source=local_client&clientId=${fn:escapeXml(junoEncounterForm.header.demographicNo)}'"
							 OnMouseOut="delay(5000)" window.status='Click to upload new photo'; return true;"
						onClick="popupUploadPage('uploadimage.jsp',${fn:escapeXml(junoEncounterForm.header.demographicNo)});return false;" />
					</c:when>
					<c:otherwise>
						<img style="cursor: pointer;" src="${fn:escapeXml(junoEncounterForm.header.imageMissingPlaceholderUrl)}" alt="No_Id_Photo" height="100" title="Click to upload new photo." OnMouseOver="window.status='Click to upload new photo';return true"
							 onClick="popupUploadPage('../casemgmt/uploadimage.jsp',${fn:escapeXml(junoEncounterForm.header.demographicNo)});return false;" />
					</c:otherwise>
				</c:choose>
			</security:oscarSec>

			<!-- MARC-HI's Sharing Center -->
<%--			<% if (isSharingCenterEnabled) { %>
			<div>
				<button type="button" onclick="window.open('${ctx}/sharingcenter/documents/demographicExport.jsp?demographic_no=<%=demo%>');">
					Export Patient Demographic
				</button>
			</div>
			<% } %>--%>

			<div id="rightColLoader" style="width: 100%;">

				<c:forEach items="${junoEncounterForm.rightNoteSections}" var="sectionName" varStatus="loop">

					<c:set var="section" scope="page" value="${junoEncounterForm.sections[sectionName]}" />

					<div class="leftBox" id="${sectionName}" style="display: block;">

						<form style="display: none;" name="dummyForm" action="">
							<input type="hidden" id="reloadDiv" name="reloadDiv" value="none" onchange="updateDiv();">
						</form>

						<div id='menuTitle${sectionName}' style="width: 10%; float: right; text-align: center;">
							<h3 style="padding:0px; background-color: ${section.colour};">
								<a href="javascript:void(0);" onclick="return false;">+</a>
							</h3>
						</div>

						<div style="clear: left; float: left; width: 90%;">
							<h3 style="width:100%; background-color: ${section.colour}">
								<a href="#" onclick="return false;">
									${section.title}
								</a>
							</h3>
						</div>

						<ul id="${sectionName}list">

							<c:set var="section" scope="page" value="${junoEncounterForm.sections[sectionName]}" />

							<c:forEach items="${section.notes}" var="note" varStatus="loop">

								<li style="overflow: hidden; clear:both; position:relative; display:block; white-space:nowrap; ">
									<a border="0" style="text-decoration:none; width:7px; z-index: 100; background-color: white; position:relative; margin: 0px; padding-bottom: 0px;  vertical-align: bottom; display: inline; float: right; clear:both;"><img id="img${sectionName}1" src="/images/clear.gif">&nbsp;&nbsp;</a>
									<span style=" z-index: 1; position:absolute; margin-right:10px; width:90%; overflow:hidden;  height:1.2em; white-space:nowrap; float:left; text-align:left; ">
									<a
											class="links"
											style="color: ${note.colour};"
											onmouseover="this.className='linkhover'"
											onmouseout="this.className='links'"
											href="#" onclick=""
											title="Flu=Influenza vaccine"
									>
										<c:choose>
											<c:when test="${note.colouredTitle}">
												<span class="${fn:join(note.titleClasses, ' ')}">
													<c:out value="${note.text}" />
												</span>
											</c:when>
											<c:otherwise>
												<c:out value="${note.text}" />
											</c:otherwise>
										</c:choose>
									</a>
								</span>
									<span style="z-index: 100; background-color: #f3f3f3; overflow:hidden;   position:relative; height:1.2em; white-space:nowrap; float:right; text-align:right;">
									<fmt:parseDate value="${note.updateDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedUpdateDate" />
									<fmt:formatDate value="${parsedUpdateDate}" pattern="dd-MMM-yyyy" var="updateDate" />
									...<a
											class="links"
											style="margin-right: 2px; color: ${note.colour};"
											onmouseover="this.className='linkhover'"
											onmouseout="this.className='links'"
											href="#"
											onclick="reloadWindows['prevention148'] = '/oscarEncounter/displayPrevention.do?hC=009999&amp;reloadURL=%2FoscarEncounter%2FdisplayPrevention.do%3FhC%3D009999&amp;numToDisplay=6&amp;cmd=preventions&amp;cmd=preventions';reloadWindows['prevention148div'] = 'preventions';popupPage(700,960,'prevention148', '/oscarPrevention/index.jsp?demographic_no=148');return false;; return false;"
											title="DTaP=Diphtheria, Tetanus, Acellular Pertussis - pediatric"
									>
										<c:out value="${updateDate}" />
									</a>
								</span>
								</li>

							</c:forEach>
						</ul>
					</div>
				</c:forEach>



			</div>
		</div>

		<div id="leftNavBar" style="display: inline; float: left; width: 20%;">

<%--			<div class="leftBox" id="preventions" style="display: block;">--%>

				<!--dummmy div to force browser to allocate space -->
				<%--
				<div id="leftColLoader" class="leftBox" style="width: 100%">
					<h3 style="width: 100%; background-color: #996633;">
						<a href="#" onclick="return false;"><bean:message key="oscarEncounter.LeftNavBar.msgLoading"/></a>
					</h3>
				</div>
				--%>

<%--				<form style="display: none;" name="dummyForm" action="">
					<input type="hidden" id="reloadDiv" name="reloadDiv" value="none" onchange="updateDiv();">
				</form>

				<div id='menuTitlepreventions' style="width: 10%; float: right; text-align: center;">
					<h3 style="padding:0px; background-color: #009999;">
						<a href="javascript:void(0);" onclick="return false;">+</a>
					</h3>
				</div>

				<div style="clear: left; float: left; width: 90%;">
					<h3 style="width:100%; background-color: #009999">
						<a href="#" onclick="return false;">
							Preventions
						</a>
					</h3>
				</div>

				<ul id="preventionslist">--%>
					<%--
					// Example
					<li style="overflow: hidden; clear:both; position:relative; display:block; white-space:nowrap; ">
						<a border="0" style="text-decoration:none; width:7px; z-index: 100; background-color: white; position:relative; margin: 0px; padding-bottom: 0px;  vertical-align: bottom; display: inline; float: right; clear:both;"><img id="imgpreventions1" src="/images/clear.gif">&nbsp;&nbsp;</a>
						<span style=" z-index: 1; position:absolute; margin-right:10px; width:90%; overflow:hidden;  height:1.2em; white-space:nowrap; float:left; text-align:left; ">
						<a class="links" style="" onmouseover="this.className='linkhover'" onmouseout="this.className='links'" href="#" onclick="reloadWindows['prevention148'] = '/oscarEncounter/displayPrevention.do?hC=009999&amp;reloadURL=%2FoscarEncounter%2FdisplayPrevention.do%3FhC%3D009999&amp;numToDisplay=6&amp;cmd=preventions&amp;cmd=preventions';reloadWindows['prevention148div'] = 'preventions';popupPage(700,960,'prevention148', '/oscarPrevention/index.jsp?demographic_no=148');return false;; return false;" title="Flu=Influenza vaccine">
							Flu
						</a>
						</span>
					</li>
					--%>

<%--					<c:set var="sectionPrevention" scope="page" value="${junoEncounterForm.sections['Preventions']}" />

					<c:forEach items="${sectionPrevention.notes}" var="note" varStatus="loop">

						<li style="overflow: hidden; clear:both; position:relative; display:block; white-space:nowrap; ">
							<a border="0" style="text-decoration:none; width:7px; z-index: 100; background-color: white; position:relative; margin: 0px; padding-bottom: 0px;  vertical-align: bottom; display: inline; float: right; clear:both;"><img id="imgpreventions1" src="/images/clear.gif">&nbsp;&nbsp;</a>
							<span style=" z-index: 1; position:absolute; margin-right:10px; width:90%; overflow:hidden;  height:1.2em; white-space:nowrap; float:left; text-align:left; ">
								<a class="links" style="" onmouseover="this.className='linkhover'" onmouseout="this.className='links'" href="#" onclick="" title="Flu=Influenza vaccine">
									<c:out value="${note.text}" />
								</a>
							</span>
							<c:if test="${note.updateDate != null}">
								<fmt:parseDate value="${note.updateDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedUpdateDate" />
								<fmt:formatDate value="${parsedUpdateDate}" pattern="dd-MMM-yyyy" var="updateDate" />
								<span style="z-index: 100; background-color: #f3f3f3; overflow:hidden;   position:relative; height:1.2em; white-space:nowrap; float:right; text-align:right;">
									...<a class="links" style="margin-right: 2px; color: ${note.colour}" onmouseover="this.className='linkhover'" onmouseout="this.className='links'" href="#" onclick="reloadWindows['prevention148'] = '/oscarEncounter/displayPrevention.do?hC=009999&amp;reloadURL=%2FoscarEncounter%2FdisplayPrevention.do%3FhC%3D009999&amp;numToDisplay=6&amp;cmd=preventions&amp;cmd=preventions';reloadWindows['prevention148div'] = 'preventions';popupPage(700,960,'prevention148', '/oscarPrevention/index.jsp?demographic_no=148');return false;; return false;" title="DTaP=Diphtheria, Tetanus, Acellular Pertussis - pediatric">
										<c:out value="${updateDate}" />
									</a>
								</span>
							</c:if>
						</li>

					</c:forEach>
				</ul>
			</div>--%>


			<%--
			<div class="leftBox" id="ticklers" style="display: block;">

				<form style="display: none;" name="dummyForm" action="">
					<input type="hidden" id="reloadDiv" name="reloadDiv" value="none" onchange="updateDiv();">
				</form>

				<div id='menuTitleticklers' style="width: 10%; float: right; text-align: center;">
					<h3 style="padding:0px; background-color: #FF6600;">
						<a href="javascript:void(0);" onclick="return false;">+</a>
					</h3>
				</div>

				<div style="clear: left; float: left; width: 90%;">
					<h3 style="width:100%; background-color: #FF6600">
						<a href="#" onclick="return false;">
							Tickler
						</a>
					</h3>
				</div>

				<ul id="ticklerslist">

					<c:set var="section" scope="page" value="${junoEncounterForm.sections['Tickler']}" />

					<c:forEach items="${section.notes}" var="note" varStatus="loop">

						<li style="overflow: hidden; clear:both; position:relative; display:block; white-space:nowrap; ">
							<a border="0" style="text-decoration:none; width:7px; z-index: 100; background-color: white; position:relative; margin: 0px; padding-bottom: 0px;  vertical-align: bottom; display: inline; float: right; clear:both;"><img id="imgticklers1" src="/images/clear.gif">&nbsp;&nbsp;</a>
							<span style=" z-index: 1; position:absolute; margin-right:10px; width:90%; overflow:hidden;  height:1.2em; white-space:nowrap; float:left; text-align:left; ">
								<a
									class="links"
									style="color: ${note.colour};"
									onmouseover="this.className='linkhover'"
									onmouseout="this.className='links'"
									href="#" onclick=""
									title="Flu=Influenza vaccine"
								>
									<c:out value="${note.text}" />
								</a>
							</span>
							<span style="z-index: 100; background-color: #f3f3f3; overflow:hidden;   position:relative; height:1.2em; white-space:nowrap; float:right; text-align:right;">
								<fmt:parseDate value="${note.updateDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedUpdateDate" />
								<fmt:formatDate value="${parsedUpdateDate}" pattern="dd-MMM-yyyy" var="updateDate" />
								...<a
									class="links"
									style="margin-right: 2px; color: ${note.colour};"
									onmouseover="this.className='linkhover'"
									onmouseout="this.className='links'"
									href="#"
									onclick="reloadWindows['prevention148'] = '/oscarEncounter/displayPrevention.do?hC=009999&amp;reloadURL=%2FoscarEncounter%2FdisplayPrevention.do%3FhC%3D009999&amp;numToDisplay=6&amp;cmd=preventions&amp;cmd=preventions';reloadWindows['prevention148div'] = 'preventions';popupPage(700,960,'prevention148', '/oscarPrevention/index.jsp?demographic_no=148');return false;; return false;"
									title="DTaP=Diphtheria, Tetanus, Acellular Pertussis - pediatric"
								   >
									<c:out value="${updateDate}" />
								</a>
							</span>
						</li>

					</c:forEach>
				</ul>
			</div>
			--%>

			<c:forEach items="${junoEncounterForm.leftNoteSections}" var="sectionName" varStatus="loop">

				<c:set var="section" scope="page" value="${junoEncounterForm.sections[sectionName]}" />

				<div class="leftBox" id="${sectionName}" style="display: block;">

					<form style="display: none;" name="dummyForm" action="">
						<input type="hidden" id="reloadDiv" name="reloadDiv" value="none" onchange="updateDiv();">
					</form>

					<div id='menuTitle${sectionName}' style="width: 10%; float: right; text-align: center;">
						<h3 style="padding:0px; background-color: ${section.colour};">
							<a href="javascript:void(0);" onclick="return false;">+</a>
						</h3>
					</div>

					<div style="clear: left; float: left; width: 90%;">
						<h3 style="width:100%; background-color: ${section.colour}">
							<a href="#" onclick="return false;">
								${section.title}
							</a>
						</h3>
					</div>

					<ul id="${sectionName}list">

						<c:set var="section" scope="page" value="${junoEncounterForm.sections[sectionName]}" />

						<c:forEach items="${section.notes}" var="note" varStatus="loop">

							<li style="overflow: hidden; clear:both; position:relative; display:block; white-space:nowrap; ">
								<a border="0" style="text-decoration:none; width:7px; z-index: 100; background-color: white; position:relative; margin: 0px; padding-bottom: 0px;  vertical-align: bottom; display: inline; float: right; clear:both;"><img id="img${sectionName}1" src="/images/clear.gif">&nbsp;&nbsp;</a>
								<span style=" z-index: 1; position:absolute; margin-right:10px; width:90%; overflow:hidden;  height:1.2em; white-space:nowrap; float:left; text-align:left; ">
									<a
										class="links"
										style="color: ${note.colour};"
										onmouseover="this.className='linkhover'"
										onmouseout="this.className='links'"
										href="#" onclick=""
										title="Flu=Influenza vaccine"
									>
										<c:out value="${note.text}" />
									</a>
								</span>
								<span style="z-index: 100; background-color: #f3f3f3; overflow:hidden;   position:relative; height:1.2em; white-space:nowrap; float:right; text-align:right;">
									<fmt:parseDate value="${note.updateDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedUpdateDate" />
									<fmt:formatDate value="${parsedUpdateDate}" pattern="dd-MMM-yyyy" var="updateDate" />
									...<a
										class="links"
										style="margin-right: 2px; color: ${note.colour};"
										onmouseover="this.className='linkhover'"
										onmouseout="this.className='links'"
										href="#"
										onclick="reloadWindows['prevention148'] = '/oscarEncounter/displayPrevention.do?hC=009999&amp;reloadURL=%2FoscarEncounter%2FdisplayPrevention.do%3FhC%3D009999&amp;numToDisplay=6&amp;cmd=preventions&amp;cmd=preventions';reloadWindows['prevention148div'] = 'preventions';popupPage(700,960,'prevention148', '/oscarPrevention/index.jsp?demographic_no=148');return false;; return false;"
										title="DTaP=Diphtheria, Tetanus, Acellular Pertussis - pediatric"
									>
										<c:out value="${updateDate}" />
									</a>
								</span>
							</li>

						</c:forEach>
					</ul>
				</div>
			</c:forEach>
		</div>

		<div id="content" style="display: inline; float: left; width: 60%; background-color: #CCCCFF;">

			<div id="cppBoxes">

				<c:forEach items="${junoEncounterForm.cppNoteSections}" var="sectionName" varStatus="loop">

					<c:set var="section" scope="page" value="${junoEncounterForm.sections[sectionName]}" />

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
							<div id="divR1I2" class="topBox" style="float: right; width: 49%; margin-right: 3px; height: inherit;">
						</c:if>

						<c:if test="${loop.index == 2}">
							<div id="divR2I1" class="topBox" style="clear: left; float: left; width: 49%; margin-left: 3px; height: inherit;">
						</c:if>

						<c:if test="${loop.index == 3}">
							<div id="divR2I2" class="topBox" style="clear: right; float: right; width: 49%; margin-right: 3px; height: inherit;">
						</c:if>


							<div style="width: 10%; float: right; text-align: center;">
								<h3 style="padding:0px; background-color: ${section.colour}">
									<a href="#" title='Add Item' onclick="return showEdit(
										event,
										'${fn:escapeXml(section.title)}',
										'',
										0,
										'',
										'',
										'',
										'${fn:escapeXml(section.addUrl)}0',
										${fn:length(section.notes)},
										0,
										'${fn:escapeXml(section.identUrl)}',
										'${fn:escapeXml(section.cppIssues)}',
										'',
										'${fn:escapeXml(junoEncounterForm.header.demographicNo
									)}');">+</a>
								</h3>
							</div>
							<div style="clear: left; float: left; width: 90%;">
								<h3 style="width:100%; background-color: ${section.colour}">
										<c:out value="${section.title}" />
								</h3>
							</div>
							<div style="clear: both; height: calc(100% - 10px); overflow: auto;">
								<ul style="margin-left: 5px;">
									<c:forEach items="${section.notes}" var="note" varStatus="noteLoop">
										<fmt:parseDate value="${note.updateDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedUpdateDate" />
										<fmt:formatDate value="${parsedUpdateDate}" pattern="dd-MMM-yyyy" var="updateDate" />
										<fmt:parseDate value="${note.observationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedObservationDate" />
										<fmt:formatDate value="${parsedObservationDate}" pattern="dd-MMM-yyyy" var="observationDate" />
										<li>
											<span id="spanListNote${fn:escapeXml(noteLoop.index)}">
												<a class="topLinks"
												   onmouseover="this.className='topLinkhover'"
												   onmouseout="this.className='topLinks'"
												   title="Rev:${note.revision} - Last update:${updateDate}"
												   id="listNote${note.id}"
												   href="#"
												   onclick="showEdit(
														   event,
														   '<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${section.title}</spring:escapeBody>',
														   '<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${note.id}</spring:escapeBody>',
														   '<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${note.editors}</spring:escapeBody>',
														   '${observationDate}',
														   '<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${note.revision}</spring:escapeBody>',
														   '<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${note.text}</spring:escapeBody>',
														   '<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${section.addUrl}${note.id}</spring:escapeBody>',
													       ${fn:length(section.notes)},
														   ${noteLoop.index},
														   '<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${section.identUrl}</spring:escapeBody>',
														   '<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${note.noteIssuesString}</spring:escapeBody>',
														   '<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${note.noteExtsString}</spring:escapeBody>',
														   '<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${param.demographicNo}</spring:escapeBody>',
														   );return false;"
												   style="width:100%;overflow:scroll;" >
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

<%--
				<div id="divR1" style="width: 100%; height: 75px; margin: 0; background-color: #FFFFFF;">
					<!-- social history -->
					<input type="hidden" id="divR1I1num" value="${fn:length(junoEncounterForm.sections['SocHistory'].notes)}">
					<div id="divR1I1" class="topBox"
						 style="float: left; width: 49%; margin-left: 3px; height: inherit;">

						<div style="width: 10%; float: right; text-align: center;">
							<h3 style="padding:0px; background-color:#996633">
								<a href="#" title='Add Item' onclick="return showEdit(event,'${junoEncounterForm.sections['SocHistory'].title}','',0,'','','','${junoEncounterForm.sections['SocHistory'].addUrl}0', 'divR1I1','${junoEncounterForm.sections['SocHistory'].identUrl}','${junoEncounterForm.sections['SocHistory'].cppIssues}','','${junoEncounterForm.header.demographicNo}');">+</a>
							</h3>
						</div>
						<div style="clear: left; float: left; width: 90%;">
							<h3 style="width:100%; background-color:#996633">
								${junoEncounterForm.sections['SocHistory'].title}
							</h3>
						</div>

						--%>

<%--						<div style="width: 10%; float: right; text-align: center;">
							<h3 style="padding:0px; background-color:#<c:out value="${param.hc}"/>">
								<%
									LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
									com.quatro.service.security.SecurityManager securityManager = new com.quatro.service.security.SecurityManager();
									if(securityManager.hasWriteAccess("_" + request.getParameter("issue_code"),roleName$)) {
								%>
								<a href="#" title='Add Item' onclick="return showEdit(event,'<bean-el:message key="${param.title}" />','',0,'','','','<%=request.getAttribute("addUrl")%>0', '<c:out value="${param.cmd}"/>','<%=request.getAttribute("identUrl")%>','<%=request.getAttribute("cppIssue")%>','','<c:out value="${param.demographicNo}"/>');">+</a>
								<% } else { %>
								&nbsp;
								<% } %>
							</h3>
						</div>--%>
<%--						<div style="clear: left; float: left; width: 90%;">
							<h3 style="width:100%; background-color:#<c:out value="${param.hc}"/>"><a
									href="#"
									onclick="return showIssueHistory('<c:out value="${param.demographicNo}"/>','<%=request.getAttribute("issueIds")%>');"><bean-el:message key="${param.title}" /></a></h3>
						</div>--%>

							<%--
						<div style="clear: both; height: calc(100% - 10px); overflow: auto;">
							<ul style="margin-left: 5px;">

								<c:forEach items="${junoEncounterForm.sections['SocHistory'].notes}" var="note">
									<li>${note.text}</li>
								</c:forEach>
								--%>
<%--									<% List<CaseManagementNoteExt> noteExts = (List<CaseManagementNoteExt>)request.getAttribute("NoteExts"); %>--%>

<%--									<nested:iterate indexId="noteIdx" id="note" name="Notes"
													type="org.oscarehr.casemgmt.model.CaseManagementNote">
										<input type="hidden" id="<c:out value="${param.cmd}"/><nested:write name="note" property="id"/>" value="<nested:write name="noteIdx"/>">
										<% if( noteIdx % 2 == 0 ) { %>
										<li class="cpp"	style="clear: both; whitespace: nowrap; background-color: #F3F3F3;">
										<%}else {%>
										<li class="cpp" style="clear: both; whitespace: nowrap;">
										<%}%>
										<span id="spanListNote<nested:write name="note" property="id"/>">--%>


<%--			<c:choose>
            <c:when test='${param.title == "oscarEncounter.oMeds.title" || param.title == "oscarEncounter.riskFactors.title" || param.title == "oscarEncounter.famHistory.title"|| param.noheight == "true"}'>
                <a class="links" onmouseover="this.className='linkhover'"	onmouseout="this.className='links'" title="Rev:<nested:write name="note" property="revision"/> - Last update:<nested:write name="note" property="update_date" format="dd-MMM-yyyy"/>" id="listNote<nested:write name="note" property="id"/>" href="#" onclick="showEdit(event,'<bean-el:message key="${param.title}" />','<nested:write name="note" property="id"/>','<%=StringEscapeUtils.escapeJavaScript(editors.toString())%>','<nested:write name="note" property="observation_date" format="dd-MMM-yyyy"/>','<nested:write name="note" property="revision"/>','<%=noteTxt%>', '<%=request.getAttribute("addUrl")%><nested:write name="note" property="id"/>', '<c:out value="${param.cmd}"/>','<%=request.getAttribute("identUrl")%>','<%=strNoteIssues.toString()%>','<%=strNoteExts%>','<c:out value="${param.demographicNo}"/>');return false;"  style="width:100%;overflow:scroll;" >
            </c:when>
            <c:otherwise>
                <a class="topLinks" onmouseover="this.className='topLinkhover'"	onmouseout="this.className='topLinks'" title="Rev:<nested:write name="note" property="revision"/> - Last update:<nested:write name="note" property="update_date" format="dd-MMM-yyyy"/>" id="listNote<nested:write name="note" property="id"/>" href="#" onclick="showEdit(event,'<bean-el:message key="${param.title}" />','<nested:write name="note" property="id"/>','<%=StringEscapeUtils.escapeJavaScript(editors.toString())%>','<nested:write name="note" property="observation_date" format="dd-MMM-yyyy"/>','<nested:write name="note" property="revision"/>','<%=noteTxt%>', '<%=request.getAttribute("addUrl")%><nested:write name="note" property="id"/>', '<c:out value="${param.cmd}"/>','<%=request.getAttribute("identUrl")%>','<%=strNoteIssues.toString()%>','<%=strNoteExts%>','<c:out value="${param.demographicNo}"/>');return false;"  style="width:100%;overflow:scroll;" >
            </c:otherwise>
        	</c:choose>--%>

<%--											<%=htmlNoteTxt%></a>
										</span></li>
									</nested:iterate>--%>

<%--
									<%
										List<NoteDisplay>remoteNotes = (List<NoteDisplay>)request.getAttribute("remoteNotes");
										String htmlText;
										int noteIdx = 0;
										if( remoteNotes != null ) {
											for( NoteDisplay remoteNote : remoteNotes) {
												htmlText = remoteNote.getNote();
												htmlText = htmlText.replaceAll("\n", "<br>");
												if( noteIdx % 2 == 0 ) {
									%>
									<li class="cpp" style="clear: both; whitespace: nowrap; background-color: #FFCCCC;">
												<%
				}
				else {
				    %>
									<li class="cpp" style="clear: both; whitespace: nowrap; background-color: #CCA3A3">
										<%
											}
										%>
										<a class="links" onmouseover="this.className='linkhover'"	onmouseout="this.className='links'" title="<%=remoteNote.getLocation()%> by <%=remoteNote.getProviderName()%> on <%=ConversionUtils.toTimestampString(remoteNote.getObservationDate())%>" href="javascript:void(0)" onclick="showIntegratedNote('<bean-el:message key="${param.title}" />',<%=htmlText%>,<%=remoteNote.getLocation()%>, <%=remoteNote.getProviderName()%>, <%=ConversionUtils.toTimestampString(remoteNote.getObservationDate())%>);">
											<%=htmlText%>
										</a>
									</li>
									<%
											}

										}
									%>--%>


								<%--
							</ul>
							<br>
						</div>
					</div>

					<!-- This is the Medical History cell ...mh...-->
					<div id="divR1I2" class="topBox" style="float: right; width: 49%; margin-right: 3px; height: inherit;">
						<input type="hidden" id="divR1I2num" value="${fn:length(junoEncounterForm.sections['MedHistory'].notes)}">

						<div style="width: 10%; float: right; text-align: center;">
							<h3 style="padding:0px; background-color:#996633">
									+
							</h3>
						</div>
						<div style="clear: left; float: left; width: 90%;">
							<h3 style="width:100%; background-color:#996633">
								${junoEncounterForm.sections['MedHistory'].title}
							</h3>
						</div>
						<div style="clear: both; height: calc(100% - 10px); overflow: auto;">
							<ul style="margin-left: 5px;">

								<c:forEach items="${junoEncounterForm.sections['MedHistory'].notes}" var="note">
								<li>${note.text}</li>
								</c:forEach>
							</ul>
							<br>
						</div>

					</div>
				</div>

				<div id="divR2" style="width: 100%; height: 75px; margin-top: 0; background-color: #FFFFFF;">
					<!--Ongoing Concerns cell -->
					<div id="divR2I1" class="topBox"
						 style="clear: left; float: left; width: 49%; margin-left: 3px; height: inherit;">
						<input type="hidden" id="divR2I1num" value="${fn:length(junoEncounterForm.sections['Concerns'].notes)}">

						<div style="width: 10%; float: right; text-align: center;">
							<h3 style="padding:0px; background-color:#996633">
									+
							</h3>
						</div>
						<div style="clear: left; float: left; width: 90%;">
							<h3 style="width:100%; background-color:#996633">
								${junoEncounterForm.sections['Concerns'].title}
							</h3>
						</div>
						<div style="clear: both; height: calc(100% - 10px); overflow: auto;">
							<ul style="margin-left: 5px;">

								<c:forEach items="${junoEncounterForm.sections['Concerns'].notes}" var="note">
									<li>${note.text}</li>
								</c:forEach>
							</ul>
							<br>
						</div>

					</div>
					<!--Reminders cell -->
					<div id="divR2I2" class="topBox"
						 style="clear: right; float: right; width: 49%; margin-right: 3px; height: inherit;">
						<input type="hidden" id="divR2I2num" value="${fn:length(junoEncounterForm.sections['Reminders'].notes)}">

						<div style="width: 10%; float: right; text-align: center;">
							<h3 style="padding:0px; background-color:#996633">
									+
							</h3>
						</div>
						<div style="clear: left; float: left; width: 90%;">
							<h3 style="width:100%; background-color:#996633">
								${junoEncounterForm.sections['Reminders'].title}
							</h3>
						</div>
						<div style="clear: both; height: calc(100% - 10px); overflow: auto;">
							<ul style="margin-left: 5px;">

								<c:forEach items="${junoEncounterForm.sections['Reminders'].notes}" var="note">
									<li>${note.text}</li>
								</c:forEach>
							</ul>
							<br>
						</div>

					</div>
				</div>
				--%>

				<span style="visibility:hidden">test</span>
			</div>

			<div id="notCPP" style="height: 70%; margin-left: 2px; background-color: #FFFFFF;"></div>

		</div>

<%-- XXX: don't do integrator stuff

		<!-- Display Integrated Data -->
		<div id="showIntegratedNote" class="showEdContent" style="height:325px;">
			<div id="integratedNoteWrapper" style="position:relative;width:99.5%;height:320px">
				<div id="integratedNoteTitle"></div>
				<textarea style="margin: 10px;" cols="50" rows="15" id="integratedNoteTxt" name="integratedNoteTxt" readonly></textarea>
				<div id="integratedNoteDetails" style="text-align:left;padding-left:4px;font-size:10px;"></div>

				<span style="position:absolute;right:10px;">
						<input type="image"
							   src="<c:out value="${ctx}/oscarEncounter/graphics/system-log-out.png"/>"
							   title='<bean:message key="global.btnExit"/>'
							   onclick="this.focus();$('channel').style.visibility ='visible';$('showIntegratedNote').style.display='none';return false;">
				</span>
			</div><!-- integratedNoteWrapper -->
		</div><!-- showIntegratedNote -->
--%>

		<!-- hovering divs -->
		<div id="showEditNote" class="showEdContent">
			<form id="frmIssueNotes" action="" method="post"
				  onsubmit="return updateCPPNote();">
				<input type="hidden" id="reloadUrl" name="reloadUrl" value="">
				<input type="hidden" id="containerDiv" name="containerDiv" value="">
				<input type="hidden" id="issueChange" name="issueChange" value="">
				<input type="hidden" id="archived" name="archived" value="false">
				<input type="hidden" id="annotation_attrib" name="annotation_attrib">
				<div id="winTitle"></div>
				<textarea style="margin: 10px;" cols="50" rows="15" id="noteEditTxt"
						  name="value"></textarea>
				<br>

				<table>
					<tr id="Itemproblemdescription">
						<td><bean:message
								key="oscarEncounter.problemdescription.title" />:</td>
						<td><input type="text" id="problemdescription"
								   name="problemdescription" value=""></td>
					</tr>
					<tr id="Itemstartdate">
						<td><bean:message key="oscarEncounter.startdate.title" />:</td>
						<td><input type="text" id="startdate" name="startdate"
								   value="" size="12"> (YYYY-MM-DD)</td>
					</tr>
					<tr id="Itemresolutiondate">
						<td><bean:message key="oscarEncounter.resolutionDate.title" />:
						</td>
						<td><input type="text" id="resolutiondate"
								   name="resolutiondate" value="" size="12"> (YYYY-MM-DD)</td>
					</tr>
					<tr id="Itemageatonset">
						<td><bean:message key="oscarEncounter.ageAtOnset.title" />:</td>
						<td><input type="text" id="ageatonset" name="ageatonset"
								   value="" size="2"></td>
					</tr>

					<tr id="Itemproceduredate">
						<td><bean:message key="oscarEncounter.procedureDate.title" />:
						</td>
						<td><input type="text" id="proceduredate" name="proceduredate"
								   value="" size="12"> (YYYY-MM-DD)</td>
					</tr>
					<tr id="Itemtreatment">
						<td><bean:message key="oscarEncounter.treatment.title" />:</td>
						<td><input type="text" id="treatment" name="treatment"
								   value=""></td>
					</tr>
					<tr id="Itemproblemstatus">
						<td><bean:message key="oscarEncounter.problemStatus.title" />:
						</td>
						<td><input type="text" id="problemstatus" name="problemstatus"
								   value="" size="8"> <bean:message
								key="oscarEncounter.problemStatusExample.msg" /></td>
					</tr>
					<tr id="Itemexposuredetail">
						<td><bean:message key="oscarEncounter.exposureDetail.title" />:
						</td>
						<td><input type="text" id="exposuredetail"
								   name="exposuredetail" value=""></td>
					</tr>
					<tr id="Itemrelationship">
						<td><bean:message key="oscarEncounter.relationship.title" />:
						</td>
						<td><input type="text" id="relationship" name="relationship"
								   value=""></td>
					</tr>
					<tr id="Itemlifestage">
						<td><bean:message key="oscarEncounter.lifestage.title" />:</td>
						<td><select name="lifestage" id="lifestage">
							<option value="">
								<bean:message key="oscarEncounter.lifestage.opt.notset" />
							</option>
							<option value="N">
								<bean:message key="oscarEncounter.lifestage.opt.newborn" />
							</option>
							<option value="I">
								<bean:message key="oscarEncounter.lifestage.opt.infant" />
							</option>
							<option value="C">
								<bean:message key="oscarEncounter.lifestage.opt.child" />
							</option>
							<option value="T">
								<bean:message key="oscarEncounter.lifestage.opt.adolescent" />
							</option>
							<option value="A">
								<bean:message key="oscarEncounter.lifestage.opt.adult" />
							</option>
						</select></td>
					</tr>
					<tr id="Itemhidecpp">
						<td><bean:message key="oscarEncounter.hidecpp.title" />:</td>
						<td><select id="hidecpp" name="hidecpp">
							<option value="0">No</option>
							<option value="1">Yes</option>
						</select></td>
					</tr>
				</table>
				<br> <span style="float: right; margin-right: 10px;"> <input
					type="image"
					src="<c:out value="${ctx}/oscarEncounter/graphics/copy.png"/>"
					title='<bean:message key="oscarEncounter.Index.btnCopy"/>'
					onclick="copyCppToCurrentNote(); return false;"> <input
					type="image"
					src="<c:out value="${ctx}/oscarEncounter/graphics/annotation.png"/>"
					title='<bean:message key="oscarEncounter.Index.btnAnnotation"/>'
					id="anno" style="padding-right: 10px;"> <input type="image"
																   src="<c:out value="${ctx}/oscarEncounter/graphics/edit-cut.png"/>"
																   title='<bean:message key="oscarEncounter.Index.btnArchive"/>'
																   onclick="$('archived').value='true';" style="padding-right: 10px;">
					<input type="image"
						   src="<c:out value="${ctx}/oscarEncounter/graphics/note-save.png"/>"
						   title='<bean:message key="oscarEncounter.Index.btnSignSave"/>'
						   onclick="$('archived').value='false';" style="padding-right: 10px;">
					<input type="image"
						   src="<c:out value="${ctx}/oscarEncounter/graphics/system-log-out.png"/>"
						   title='<bean:message key="global.btnExit"/>'
						   onclick="this.focus();$('showEditNote').style.display='none';return false;">
						   <%--onclick="this.focus();$('channel').style.visibility ='visible';$('showEditNote').style.display='none';return false;">--%>
				</span>
				<bean:message key="oscarEncounter.Index.btnPosition" />
				<select id="position" name="position"><option id="popt0"
															  value="0">1</option>
				</select>
				<div id="issueNoteInfo" style="clear: both; text-align: left;"></div>
				<div id="issueListCPP"
					 style="background-color: #FFFFFF; height: 200px; width: 350px; position: absolute; z-index: 1; display: none; overflow: auto;">
					<div class="enTemplate_name_auto_complete"
						 id="issueAutocompleteListCPP"
						 style="position: relative; left: 0px; display: none;"></div>
				</div>
				<bean:message key="oscarEncounter.Index.assnIssue" />
				&nbsp;<input tabindex="100" type="text" id="issueAutocompleteCPP"
							 name="issueSearch" style="z-index: 2;" size="25">&nbsp; <span
					id="busy2" style="display: none"><img
					style="position: absolute;"
					src="<c:out value="${ctx}/oscarEncounter/graphics/busy.gif"/>"
					alt="<bean:message key="oscarEncounter.Index.btnWorking"/>"></span>

			</form>
		</div>

		<div id="printOps" class="printOps">
			<h3 style="margin-bottom: 5px; text-align: center;">
				<bean:message key="oscarEncounter.Index.PrintDialog" />
			</h3>
			<form id="frmPrintOps" action="" onsubmit="return false;">
				<table id="printElementsTable">
					<tr>
						<td><input type="radio" id="printopSelected" name="printop"
								   value="selected">
							<bean:message key="oscarEncounter.Index.PrintSelect" /></td>
						<td>
							<%
								String roleName = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
							%> <security:oscarSec roleName="<%=roleName%>"
												  objectName="_newCasemgmt.cpp" rights="r" reverse="false">
							<img style="cursor: pointer;"
								 title="<bean:message key="oscarEncounter.print.title"/>"
								 id='imgPrintCPP'
								 alt="<bean:message key="oscarEncounter.togglePrintCPP.title"/>"
								 onclick="return printInfo(this,'printCPP');"
								 src='<c:out value="${ctx}"/>/oscarEncounter/graphics/printer.png'>&nbsp;<bean:message
								key="oscarEncounter.cpp.title" />
						</security:oscarSec>
						</td>
					</tr>
					<tr>
						<td><input type="radio" id="printopAll" name="printop"
								   value="all">
							<bean:message key="oscarEncounter.Index.PrintAll" /></td>
						<td><img style="cursor: pointer;"
								 title="<bean:message key="oscarEncounter.print.title"/>"
								 id='imgPrintRx'
								 alt="<bean:message key="oscarEncounter.togglePrintRx.title"/>"
								 onclick="return printInfo(this, 'printRx');"
								 src='<c:out value="${ctx}"/>/oscarEncounter/graphics/printer.png'>&nbsp;<bean:message
								key="oscarEncounter.Rx.title" /></td>
					</tr>
					<tr>
						<td></td>
						<td><img style="cursor: pointer;"
								 title="<bean:message key="oscarEncounter.print.title"/>"
								 id='imgPrintLabs'
								 alt="<bean:message key="oscarEncounter.togglePrintLabs.title"/>"
								 onclick="return printInfo(this, 'printLabs');"
								 src='<c:out value="${ctx}"/>/oscarEncounter/graphics/printer.png'>&nbsp;<bean:message
								key="oscarEncounter.Labs.title" /></td>
					</tr>
					<!--  extension point -->
					<tr id="printDateRow">
						<td><input type="radio" id="printopDates" name="printop"
								   value="dates">
							<bean:message key="oscarEncounter.Index.PrintDates" />&nbsp;<a
									style="font-variant: small-caps;" href="#"
									onclick="return printToday(event);"><bean:message
									key="oscarEncounter.Index.PrintToday" /></a></td>
						<td></td>
					</tr>
				</table>

				<div style="float: left; margin-left: 5px; width: 30px;">
					<bean:message key="oscarEncounter.Index.PrintFrom" />
					:
				</div>
				<img src="<c:out value="${ctx}/images/cal.gif" />"
					 id="printStartDate_cal" alt="calendar">&nbsp;<input
					type="text" id="printStartDate" name="printStartDate"
					ondblclick="this.value='';"
					style="font-style: italic; border: 1px solid #7682b1; width: 125px; background-color: #FFFFFF;"
					readonly value=""><br>
				<div style="float: left; margin-left: 5px; width: 30px;">
					<bean:message key="oscarEncounter.Index.PrintTo" />
					:
				</div>
				<img src="<c:out value="${ctx}/images/cal.gif" />"
					 id="printEndDate_cal" alt="calendar">&nbsp;<input type="text"
																	   id="printEndDate" name="printEndDate" ondblclick="this.value='';"
																	   style="font-style: italic; border: 1px solid #7682b1; width: 125px; background-color: #FFFFFF;"
																	   readonly value=""><br>
				<div style="margin-top: 5px; text-align: center">
					<input type="submit" id="printOp" style="border: 1px solid #7682b1;"
						   value="Print" onclick="return printNotes();">

					<indivo:indivoRegistered
							demographic="<%=(String) request.getAttribute(\"demographicNo\")%>"
							provider="<%=(String) request.getSession().getAttribute(\"user\")%>">
						<input type="submit" id="sendToPhr"
							   style="border: 1px solid #7682b1;" value="Send To Phr"
							   onclick="return sendToPhrr();">
					</indivo:indivoRegistered>
					<input type="submit" id="cancelprintOp"
						   style="border: 1px solid #7682b1;" value="Cancel"
						   onclick="$('printOps').style.display='none';"> <input
						type="submit" id="clearprintOp" style="border: 1px solid #7682b1;"
						value="Clear"
						onclick="$('printOps').style.display='none'; return clearAll(event);">
				</div>

				<%
					if (OscarProperties.getInstance().getBooleanProperty("note_program_ui_enabled", "true")) {
				%>
				<span class="popup" style="display: none;" id="_program_popup">
					<div class="arrow"></div>
					<div class="contents">
						<div class="selects">
							<select class="selectProgram"></select> <select class="role"></select>
						</div>
						<div class="under">
							<div class="errorMessage"></div>
							<input type="button" class="scopeBtn" value="View Note Scope" />
							<input type="button" class="closeBtn" value="Close" /> <input
								type="button" class="saveBtn" value="Save" />
						</div>
					</div>
				</span>

				<div id="_program_scope" class="_program_screen"
					 style="display: none;">
					<div class="_scopeBox">
						<div class="boxTitle">
							<span class="text">Note Permission Summary</span><span
								class="uiBigBarBtn"><span class="text">x</span></span>
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
							kind of access providers in the above program have to this note.</div>
						<div class="loading">Loading...</div>
						<table class="permissions"></table>
					</div>
				</div>
				<%
					}
				%>
			</form>
		</div>

<%--	XXX: This is used for resident review ("resident_review" property)
			<%
			String apptNo = request.getParameter("appointmentNo");
			if(
					OscarProperties.getInstance().getProperty("resident_review", "false").equalsIgnoreCase("true") &&
					loggedInInfo.getLoggedInProvider().getProviderType().equals("resident") &&
					!"null".equalsIgnoreCase(apptNo) &&
					!"".equalsIgnoreCase(apptNo)
			)
			{
				ProviderDataDao providerDao = SpringUtils.getBean(ProviderDataDao.class);
				List<ProviderData> providerList = providerDao.findAllBilling("1");
		%>
		<div id="showResident" class="showResident">

			<div class="showResidentBorder residentText">
				Resident Check List

				<form action="" id="resident" name="resident" onsubmit="return false;">
					<input type="hidden" name="residentMethod" id="residentMethod" value="">
					<input type="hidden" name="residentChain" id="residentChain" value="">
					<table class="showResidentContent">
						<tr>
							<td>
								Was this encounter reviewed?
							</td>
							<td>
								Yes <input type="radio" value="true" name="reviewed">&nbsp;No <input type="radio" value="false" name="reviewed">
							</td>
						</tr>
						<tr class="reviewer" style="display:none">
							<td class="residentText">
								Who did you review the encounter with?
							</td>
							<td>
								<select id="reviewer" name="reviewer">
									<option value="">Choose Reviewer</option>
									<%
										for( ProviderData p : providerList ) {
									%>
									<option value="<%=p.getId()%>"><%=p.getLastName() + ", " + p.getFirstName()%></option>
									<%
										}
									%>
								</select>
							</td>
						</tr>
						<tr class="supervisor" style="display:none">
							<td class="residentText">
								Who is your Supervisor/Monitor for this encounter?
							</td>
							<td>
								<select id="supervisor" name="supervisor">
									<option value="">Choose Supervisor</option>
									<%
										for( ProviderData p : providerList ) {
									%>
									<option value="<%=p.getId()%>"><%=p.getLastName() + ", " + p.getFirstName()%></option>
									<%
										}
									%>
								</select>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<input id="submitResident" value="Continue" name="submitResident" type="submit" onclick="return subResident();"/>
								<input id="submitResident" value="Return to Chart" name="submitResident" type="submit" onclick="return cancelResident();"/>
							</td>
						</tr>
					</table>
				</form>
			</div>

		</div>
		<%}%>--%>

		<script type="text/javascript">
			/*
			document.observe('dom:loaded', function(){
				init();
			});
			*/

			(function($)
			{
				$(document).ready(function()
				{
					init();
				});
			})(jQuery);

</script>
</body>
</html:html>
