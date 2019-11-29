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
<%@ page
		import="org.oscarehr.casemgmt.web.formbeans.*, org.oscarehr.casemgmt.model.CaseManagementNote" %>
<%@ page import="org.oscarehr.common.dao.UserPropertyDAO, oscar.OscarProperties" %>
<%@ page import="org.oscarehr.common.model.UserProperty" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.util.LoggedInInfo" %>
<%@ page import="org.oscarehr.casemgmt.common.Colour" %>
<%@ page import="org.oscarehr.provider.dao.ProviderDataDao" %>
<%@ page import="org.oscarehr.provider.model.ProviderData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="oscar.util.StringUtils" %>
<%@ page import="org.oscarehr.sharingcenter.SharingCenterUtil" %>
<%@ page import="java.util.UUID"%>

<jsp:useBean id="junoEncounterForm" scope="request"
			 type="org.oscarehr.casemgmt.web.formbeans.JunoEncounterFormBean"/>

<fmt:parseDate value="${junoEncounterForm.header.encounterNoteHideBeforeDate}"
			   pattern="EEE MMM dd HH:mm:ss z y"
			   var="encounterNoteHideBeforeDateParsed"/>

<fmt:formatDate value="${encounterNoteHideBeforeDateParsed}"
			   pattern="yyyy-MM-dd'T'HH:mm"
			   var="encounterNoteHideBeforeDateFormatted"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
	//oscar.oscarEncounter.pageUtil.EctSessionBean bean = null;
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
		<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request"/>

		<link rel="stylesheet" href="<c:out value="${ctx}"/>/css/casemgmt.css" type="text/css">
		<link rel="stylesheet" href="<c:out value="${ctx}"/>/oscarEncounter/encounterStyles.css"
			  type="text/css">
		<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}"/>/css/print.css"
			  media="print">

		<script src="<c:out value="${ctx}/js/jquery-1.7.1.min.js"/>"></script>
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

			<%--		XXX: Removed this because I want to avoid stuff like this if I can--%>
			<%--		<script type="text/javascript" src="newEncounterLayout.js.jsp"> </script>--%>

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

			var pageState = {
				currentNoteData: null,
				numChars: 0,
				//flag for determining if we want to submit case management entry form with enter key pressed in auto completer text box
				submitIssues: false,
				lastTmpSaveNote: null,
				autoSaveTimer: null,
			};

			var ctx = '<c:out value="${ctx}"/>';

			var autoSaveDelay = 5000;

			var notesOffset = 0;
			var notesIncrement = <%= OscarProperties.getNumLoadedNotes(20) %>;
			var notesRetrieveOk = false;
			var notesCurrentTop = null;
			var notesScrollCheckInterval = null;
			var measurementWindows = new Array();
			var openWindows = new Object();

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

			var eChartUUID = "${junoEncounterForm.header.echartUuid}";

			function getEChartUUID()
			{
				console.log(eChartUUID);
				return eChartUUID;
			}

			function pasteToEncounterNote(txt)
			{
				var currentlyEditedNoteId = jQuery('input#editNoteId').val();
				var currentTextAreaId = "caseNote_note" + currentlyEditedNoteId;

				$(currentTextAreaId).value += "\n" + txt;
				adjustCaseNote();
				setCaretPosition($(currentTextAreaId), $(currentTextAreaId).value.length);

			}

			function measurementLoaded(name)
			{
				measurementWindows.push(openWindows[name]);
			}

			function prepareExtraFields(cpp, extObj)
			{
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

				for (var j = 0; j < exFields.length; j++)
				{
					if (extObj.hasOwnProperty(exFields[j]))
					{
						$(exFields[j]).value = extObj[exFields[j]];
						continue;
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
							function ()
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

			function popupUploadPage(varpage, dn)
			{
				var page = "" + varpage + "?demographicNo=" + dn;
				windowprops = "height=500,width=500,location=no,"
					+ "scrollbars=no,menubars=no,toolbars=no,resizable=yes,top=50,left=50";
				var popup = window.open(page, "", windowprops);
				popup.focus();

			}

			function delay(time)
			{
				string = "document.getElementById('ci').src='${fn:escapeXml(junoEncounterForm.header.imagePresentPlaceholderUrl)}'";
				setTimeout(string, time);
			}

			function openAnnotation()
			{
				var atbname = document.getElementById('annotation_attrib').value;
				var data = $A(arguments);
				var addr = ctx + "/annotation/annotation.jsp?atbname=" + atbname + "&table_id=" + data[1] + "&display=" + data[2] + "&demo=" + data[3];
				window.open(addr, "anwin", "width=400,height=500");
				Event.stop(data[0]);
			}

			function showHistory(noteId, event)
			{
				Event.stop(event);
				var rnd = Math.round(Math.random() * 1000);
				win = "win" + rnd;
				var url = ctx + "/CaseManagementEntry.do?method=notehistory&noteId=" + noteId;
				window.open(url, win, "scrollbars=yes, location=no, width=647, height=600", "");
				return false;
			}

			function getFormattedDate(date)
			{
				if (date == null || date == "")
				{
					return null;
				}

				return moment(date).format("YYYY-MM-DD");
			}


			//function showEdit(e, summaryCode, title, noteId, uuid, editors, date, revision, note, numNotes, position, noteIssues, noteExts, demoNo)
			function showEdit(e, summaryCode, title, numNotes, noteIssues, demoNo, noteJsonString)
			{
				// Gather data
				var note = JSON.parse(noteJsonString);

				if (note != null)
				{
					jQuery.ajax({
						type: "GET",
						contentType: "application/json",
						dataType: "json",
						url: "../ws/rs/notes/getIssueNote/" + note.noteId,
						success: function (response)
						{
							return showEditBox(e, summaryCode, title, numNotes, response.body.assignedCMIssues, demoNo, note)
						}
					});
				} else
				{
					return showEditBox(e, summaryCode, title, numNotes, [], demoNo, null)
				}
				return false;
			}

			function showEditBox(e, summaryCode, title, numNotes, assignedCMIssues, demoNo, note)
			{
				// Gather data
				//var note = JSON.parse(noteJsonString);

				var noteId, uuid, editors, date, revision, noteText, position, extraFields;
				if (note != null)
				{
					noteId = note.noteId;
					uuid = note.uuid;
					editors = note.editors;
					revision = note.revision;
					noteText = note.note;
					position = note.position;

					// Fill extra field data
					extraFields = {
						startdate: getFormattedDate(note.extStartDate),
						resolutiondate: getFormattedDate(note.extResolutionDate),
						proceduredate: getFormattedDate(note.extProcedureDate),
						ageatonset: note.extAgeAtOnset,
						treatment: note.extTreatment,
						problemstatus: note.extProblemStatus,
						exposuredetail: note.extExposureDetail,
						relationship: note.extRelationship,
						lifestage: note.extLifeStage,
						hidecpp: note.extHideCpp,
						problemdescription: note.extProblemDescription
					};
				} else
				{
					// New note defaults
					editors = null;
					noteId = "";
					noteText = "";
					position = 1;
					revision = "";
					uuid = "";
					extraFields = {};
				}


				var editElement = "showEditNote";

				// Clear Errors
				jQuery('#editNoteError').html("");


				// Set hidden data values

				$("noteUuid").value = uuid;
				$("noteEditTxt").value = noteText;
				$("noteSummaryCode").value = summaryCode;
				$("noteEditId").value = noteId;
				$("noteRevision").value = revision;


				// Set editors
				// XXX: Make this work better for new notes
				var editorUl = "<ul style='list-style: none outside none; margin:0px;'>";
				var editorSpan = "";

				if (editors != null && editors.length > 0)
				{
					editorSpan = "<span style='float:left;'>Editors: </span>";

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
					{
						editorUl += "</li>";
					}
				}
				editorUl += "</ul>";


				// Set issue list
				var noteIssueUl = "<ul id='issueIdList' style='list-style: none outside none; margin:0px;'>";

				if (Array.isArray(assignedCMIssues) && assignedCMIssues.length > 0)
				{
					//var issueArray = noteIssues.split(";");
					var idx;
					var cppDisplay = "";
					for (idx = 0; idx < assignedCMIssues.length; idx++)
					{
						var assignedIssue = assignedCMIssues[idx];

						if (idx % 2 == 0)
						{
							noteIssueUl += "<li>";
						} else
						{
							noteIssueUl += "&nbsp;";
						}

						noteIssueUl += "<input type='checkbox' id='issueId' name='issue_id' checked value='" + assignedIssue.issue_id + "' \>" + assignedIssue.issue.description;

						/* XXX: don't show CPP issues
						if (cppDisplay == "")
						{
							cppDisplay = getCPP(issueArray[idx + 1]);
						}
						*/
					}

					if (idx % 2 == 0)
					{
						noteIssueUl += "</li>";
					}
				}
				noteIssueUl += "</ul>";

				var noteInfo = "<div style='float:right;'><i>Encounter Date:&nbsp;" + date + "&nbsp;rev<a href='#' onclick='return showHistory(\"" + noteId + "\",event);'>" + revision + "</a></i></div>" +
					editorSpan + "<div>" + editorUl + noteIssueUl + "</div><br style='clear:both;'>";

				$("issueNoteInfo").update(noteInfo);


				//Prepare Annotation Window & Extra Fields
				var now = new Date();
				document.getElementById('annotation_attrib').value = "anno" + now.getTime();
				var obj = {};
				Element.observe('anno', 'click', openAnnotation.bindAsEventListener(obj, noteId, "issue", demoNo));

				prepareExtraFields(cppDisplay, extraFields);


				// Build position dropdown

				var curElem;
				var numOptions = $("position").length;
				var max = numNotes > numOptions ? numNotes : numOptions;
				var optId;
				var option;
				var opttxt;

				for (curElem = 2; curElem <= max; ++curElem)
				{
					optId = "popt" + curElem;
					if ($(optId) == null)
					{
						option = document.createElement("OPTION");
						option.id = optId;
						opttxt = curElem;
						option.text = "" + opttxt;
						option.value = curElem;
						$("position").options.add(option, curElem);
					}

					if (position == curElem)
					{
						$(optId).selected = true;
					}
				}

				// XXX: What does this do?
				// XXX: also make sure adding the last note works
				for (curElem = max; curElem > 0; --curElem)
				{
					optId = "popt" + curElem;
					if (curElem > numNotes)
					{
						Element.remove(optId);
					}
				}


				// Position the modal
				var coords = null;
				if (document.getElementById("measurements_div") == null)
				{
					coords = Position.page($("topContent"));
				} else
				{
					coords = Position.positionedOffset($("cppBoxes"));
				}

				var top = Math.max(coords[1], 0);
				var right = Math.round(coords[0] / 0.66);
				var gutterMargin = 150;

				if (right < gutterMargin)
				{
					right = gutterMargin;
				}

				$(editElement).style.right = right + "px";
				$(editElement).style.top = top + "px";


				// Display the modal
				$("winTitle").update(title);

				if (Prototype.Browser.IE)
				{
					//IE6 bug of showing select box
					$("channel").style.visibility = "hidden";
					$(editElement).style.display = "block";
				} else
				{
					$(editElement).style.display = "table";
				}

				$("noteEditTxt").focus();

				return false;
			}

			function getFormData($form)
			{
				var unindexed_array = $form.serializeArray();
				var indexed_array = {};

				jQuery.map(unindexed_array, function (n, i)
				{
					indexed_array[n['name']] = n['value'];
				});

				return indexed_array;
			}

			function getAssignedIssueArray(issueIdArray)
			{
				var deferred = jQuery.Deferred();

				var deferredArray = [];

				for (var i = 0; i < issueIdArray.length; i++)
				{
					var issueId = issueIdArray[i];

					var ajaxPromise = jQuery.ajax({
						type: "POST",
						url: "../ws/rs/notes/getIssueById/" + issueId
					});

					deferredArray.push(ajaxPromise);
				}

				jQuery.when.all(deferredArray).then(function (response)
				{
					var adjustedArray = response;
					if (deferredArray.length == 1)
					{
						adjustedArray = [response];
					}

					var assignedIssueArray = [];

					for (var j = 0; j < adjustedArray.length; j++)
					{
						var result = adjustedArray[j][0];

						var assignedIssue = {
							acute: false,
							certain: false,
							demographic_no: null,
							id: null,
							issue: result,
							issue_id: result.id,
							major: false,
							program_id: null,
							resolved: false,
							type: null,
							unchecked: false,
							unsaved: true,
							update_date: new Date()
						};

						assignedIssueArray.push(assignedIssue);
						//assignedIssueArray.push(adjustedArray[j][0]);
					}

					deferred.resolve(assignedIssueArray);
				});

				return deferred.promise();
			}

			function getCPPObjectFromForm(form, issueIdArray)
			{
				var deferred = jQuery.Deferred();

				var noteId = 0;
				if (form.noteEditId)
				{
					noteId = form.noteEditId;
				}

				getAssignedIssueArray(issueIdArray).then(function (assignedIssueArray)
				{
					var result = {
						"assignedCMIssues": assignedIssueArray,
						"encounterNote": {
							"noteId": noteId,
							"uuid": form.noteUuid,
							"position": form.position,
							"note": form.value,
							"archived": form.archived,
							"cpp": true,
							"editable": true,
							"isSigned": true,
							"observationDate": new Date(),
							//"observationDate":"2019-10-08T21:23:49.441Z",
							"encounterType": "",
							"encounterTime": "",
							"assignedIssues": assignedIssueArray,
							"summaryCode": form.noteSummaryCode,
							"revision": form.noteRevision
						},
						"annotation_attrib": form.annotation_attrib,
						"groupNoteExt": {
							"noteId": noteId,
							"hideCpp": form.hidecpp,
							"startDate": getUTCDateFromString(form.startdate),
							"resolutionDate": getUTCDateFromString(form.resolutiondate),
							"procedureDate": getUTCDateFromString(form.proceduredate),
							"ageAtOnset": form.ageatonset,
							"treatment": form.treatment,
							"problemStatus": form.problemstatus,
							"exposureDetail": form.exposuredetail,
							"relationship": form.relationship,
							"lifeStage": form.lifestage,
							"problemDesc": form.problemdescription
						}
					};

					deferred.resolve(result);
				});

				return deferred.promise();
			}

			function getUTCDateFromString(dateString)
			{
				if (dateString == null || dateString == "")
				{
					return null;
				}

				var dateMoment = moment(dateString);

				if (!dateMoment.isValid())
				{
					return null;
				}

				return dateMoment.toDate();
			}

			function updateCPPNote(cppType)
			{
				var demographicNo = getDemographicNo();
				var form = jQuery('#frmIssueNotes');
				var formData = getFormData(form);

				var issueIdArray = [];
				jQuery("#issueIdList input:checkbox[name=issue_id]:checked").each(function ()
				{
					issueIdArray.push(jQuery(this).val());
				});

				getCPPObjectFromForm(formData, issueIdArray).then(function (restData)
				{
					var jsonString = JSON.stringify(restData);

					jQuery.ajax({
						type: "POST",
						contentType: "application/json",
						dataType: "json",
						url: getSaveCPPNoteUrl(demographicNo),
						data: jsonString,
						success: function (response)
						{
							if (response.status == "SUCCESS")
							{
								// Close window
								hideEdit();

								// Reload note list
								getSectionRemote(formData.noteSummaryCode, true, true);

								return false;
							} else
							{
								// Show error
								jQuery('#editNoteError').html(response.error.message);
							}
						}
					});
				});

				return false;
			}

			function hideEdit()
			{
				$('showEditNote').style.display = 'none';
			}

			function assembleMainChartParams(displayFullChart)
			{

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
				} else
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
						onSuccess: function (request)
						{
							$("notCPP").update(request.responseText);
							$("notCPP").style.height = "50%";
							if (displayFullChart)
							{
								$("quickChart").innerHTML = quickChartMsg;
								$("quickChart").onclick = function ()
								{
									return viewFullChart(ctx, false);
								}
								scrollDownInnerBar();

							} else
							{
								$("quickChart").innerHTML = fullChartMsg;
								$("quickChart").onclick = function ()
								{
									return viewFullChart(ctx, true);
								}
								scrollDownInnerBar();
							}
						},
						onFailure: function (request)
						{
							$("notCPP").update("Error: " + request.status + request.responseText);
						}
					}
				);
				return false;
			}

			function editEncounterNote(event, noteId)
			{
				// Get the note to edit
				//var note = getNoteDataById(noteId);

				var demographicNo = getDemographicNo();

				jQuery.ajax({
					type: "GET",
					contentType: "application/json",
					dataType: "json",
					url: "../ws/rs/notes/" + demographicNo + "/getNoteToEdit/" + noteId,
					success: function (result)
					{
						var note = result.body.encounterNote;
						var issues = result.body.assignedCMIssues;

						// Show a warning if an unsigned note was created by a different provider
						var editWarn = (!note.isSigned && note.providerNo != getProviderNo());
						var editUnsignedMsg = "<bean:message key="oscarEncounter.editUnsignedNote.msg"/>";

						if (editWarn && !confirm(editUnsignedMsg))
						{
							return false;
						}

						// Disable any notes currently being edited
						var currentlyEditedNoteId = jQuery('input#editNoteId').val();
						var currentlyEditedNoteDiv = jQuery('div#n' + currentlyEditedNoteId).parent();

						unobserveTextArea();

						replaceNoteEntry(currentlyEditedNoteDiv, pageState.currentNoteData, null, demographicNo, false);


						// Make the note editable
						var noteDiv = jQuery('div#n' + noteId).parent();

						replaceNoteEntry(noteDiv, note, issues, demographicNo, true);
						pageState.currentNoteData = Object.clone(note);

						adjustCaseNote();
						observeTextArea();
						setSaveButtonVisibility();
					}
				});

				return false;
			}

			function observeTextArea()
			{
				var textAreaName = getEditTextAreaName();

				if(textAreaName != null)
				{
					Element.observe(textAreaName, 'keyup', monitorCaseNote);
				}
			}

			function unobserveTextArea()
			{
				var textAreaName = getEditTextAreaName();

				if(textAreaName != null)
				{
					Element.stopObserving(textAreaName, 'keyup', monitorCaseNote);
				}
			}

			function getEditTextAreaName()
			{
				if(!pageState.currentNoteData || pageState.currentNoteData.noteId == null)
				{
					return null;
				}

				return 'caseNote_note' + pageState.currentNoteData.noteId;
			}

			function getNoteDataById(noteId)
			{
				var uuid = jQuery("input#uuid" + noteId).val();
				var providerNo = jQuery("input#providerNo" + noteId).val();
				var observationDate = jQuery("input#observationDateInput" + noteId).val();
				var encounterType = jQuery("select#encounterTypeSelect" + noteId).val();
				var isSigned = jQuery("input#isSigned" + noteId).val();
				var isVerified = jQuery("input#isVerified" + noteId).val();
				var appointmentNo = jQuery("input#appointmentNo" + noteId).val();
				var noteText = jQuery("textarea#caseNote_note" + noteId).val();

				var observationMoment = moment();
				if(observationDate)
				{
					observationMoment = moment(observationDate, "DD-MMM-YYYY HH:mm");
				}

				var noteData = {
					noteId: noteId,
					uuid: uuid,
					observationDate: observationMoment.toDate(),
					providerNo: providerNo,
					encounterType: encounterType,
					isSigned: isSigned,
					isVerified: isVerified,
					appointmentNo: appointmentNo,
					note: noteText
				};

				return noteData;
			}

			function getEmptyNote(providerNo, appointmentNo)
			{
				var noteData = {
					noteId: 0,
					uuid: null,
					observationDate: null,
					providerNo: providerNo,
					encounterType: null,
					isSigned: false,
					isVerified: false,
					appointmentNo: appointmentNo,
					note: ""
				};

				return noteData;
			}

			function saveTmpSave()
			{
				// Gather information to save the note
				var demographicNo = getDemographicNo();
				var noteId = jQuery("input#editNoteId").val();


				// Prepare data
				var noteData = getNoteDataById(noteId);
				if(noteId == 0)
				{
					// New note, save accordingly
					noteData.noteId = null;
					noteData.uuid = null;
					if(!noteData.observationDate)
					{
						noteData.observationDate = new Date();
					}
					noteData.updateDate = noteData.observationDate;
				}

				jQuery.ajax({
					type: "POST",
					contentType: "application/json",
					dataType: "json",
					url: "../ws/rs/notes/" + demographicNo + "/tmpSave",
					data: JSON.stringify(noteData),
					success: function (response)
					{
						setNoteStatus("Draft saved " + moment().format("DD-MMM-YYYY HH:mm:ss"));
					},
					error: function (response)
					{
						setNoteError("Error saving note");
					}
				});
			}

			function checkNoteChanged()
			{
				if(pageState.lastTmpSaveNote == null)
				{
					if(pageState.currentNoteData && pageState.currentNoteData.note)
					{
						pageState.lastTmpSaveNote = pageState.currentNoteData.note;
					}
					else
					{
						pageState.lastTmpSaveNote = "";
					}
				}

				// Prepare data
				var noteId = jQuery("input#editNoteId").val();
				var noteData = getNoteDataById(noteId);

				// Trim the notes because that happens when the note is saved
				if(noteData.note.trim() != pageState.lastTmpSaveNote.trim())
				{
					saveTmpSave();
					pageState.lastTmpSaveNote = noteData.note;
				}

				setTmpSaveTimer();
			}

			function setTmpSaveTimer()
			{
				pageState.autoSaveTimer = setTimeout(checkNoteChanged, autoSaveDelay);
			}

			function clearTmpSaveTimer()
			{
				clearTimeout(pageState.autoSaveTimer);
			}

			function signSaveBillEncounterNote(event)
			{
				document.forms['caseManagementEntryForm'].sign.value='on';
				document.forms['caseManagementEntryForm'].toBill.value='true';

				var saved = savePage('saveAndExit', '');

				// savePage always returns false, but we can check note differences before redirecting
				if (origCaseNote === $F(caseNote))
				{
					Event.stop(event);
					maximizeWindow();
				}

				return saved;
			}

			function saveEncounterNote(signNote, verifyNote, exitAfterSaving, async, bill)
			{
				// Clear state
				clearNoteError();
				clearNoteStatus();

				// Gather information to save the note
				var demographicNo = getDemographicNo();
				var noteId = jQuery("input#editNoteId").val();

				// Prepare data
				var noteData = getNoteDataById(noteId);
				//
				if(noteId == 0)
				{
					// New note, save accordingly
					noteData.noteId = null;
					noteData.uuid = null;
					if(!noteData.observationDate)
					{
						noteData.observationDate = new Date();
					}
					noteData.updateDate = noteData.observationDate;
				}

				if (signNote)
				{
					noteData.isSigned = true;
				}

				if (verifyNote)
				{
					noteData.isVerified = true;
				}

				var issueIdArray = [];
				jQuery("#noteIssueIdList input:checkbox[name=issue_id]:checked").each(function ()
				{
					issueIdArray.push(jQuery(this).val());
				});

				// XXX: position, issues flag

				getAssignedIssueArray(issueIdArray).then(function(assignedIssueArray)
				{
					noteData.assignedIssues = assignedIssueArray;

					jQuery.ajax({
						async: async,
						type: "POST",
						contentType: "application/json",
						dataType: "json",
						url: "../ws/rs/notes/" + demographicNo + "/save?deleteTmpSave=true",
						data: JSON.stringify(noteData),
						success: function (response)
						{
							if (response.status != "SUCCESS")
							{
								setNoteError(response.error.message);
							}
							else
							{
								// Set the saved note as the current note
								pageState.currentNoteData = Object.clone(noteData);
								pageState.lastTmpSaveNote = null;
								checkNoteChanged();

								if (exitAfterSaving)
								{
									window.close();
								}

								setNoteStatus("Note successfully saved");
							}
						},
						error: function (response)
						{
							console.log(response);
							setNoteError("Error saving note");
						}
					});
				});
			}

			function monitorCaseNote(e)
			{
				var caseNote = 'caseNote_note' + pageState.currentNoteData.noteId;

				var MAXCHARS = 78;
				var MINCHARS = -10;
				var newChars = $(caseNote).value.length - pageState.numChars;
				var newline = false;

				if (e.keyCode == 13)
					newline = true;

				if (newline)
				{
					adjustCaseNote();
				}
				else if (newChars >= MAXCHARS)
				{
					adjustCaseNote();
				}
				else if (newChars <= MINCHARS)
				{
					adjustCaseNote();
				}

			}

			function setCaretPosition(input, pos)
			{
				if (input.setSelectionRange)
				{
					input.focus();
					input.setSelectionRange(pos, pos);
				}
				else if (input.createTextRange)
				{
					var range = input.createTextRange();
					range.collapse(true);
					range.moveEnd('character', pos);
					range.moveStart('character', pos);
					range.select();
				}
			}

			//resize case note text area to contain all text
			function adjustCaseNote()
			{
				var caseNote = 'caseNote_note' + pageState.currentNoteData.noteId;

				if($(caseNote) == null)
				{
					return;
				}


				var MAXCHARS = 78;
				var payload = $(caseNote).value;
				var numLines = 0;
				var spacing = Prototype.Browser.IE == true ? 1.08 : Prototype.Browser.Gecko == true ? 1.11 : 1.2;
				var fontSize = $(caseNote).getStyle('font-size');
				var lHeight = $(caseNote).getStyle('line-height');
				var lineHeight = lHeight.substr(0, lHeight.indexOf('e'));
				var arrLines = payload.split("\n");

				//we count each new line char and add a line for lines longer than max length
				for (var idx = 0; idx < arrLines.length; ++idx)
				{

					if (arrLines[idx].length >= MAXCHARS)
					{
						numLines += Math.ceil(arrLines[idx].length / MAXCHARS);
					}
					else
						++numLines;

				}

				//add a buffer
				numLines += 2;
				var noteHeight = Math.ceil(lineHeight * numLines);
				noteHeight += 'em';
				$(caseNote).style.height = noteHeight;

				pageState.numChars = $(caseNote).value.length;
			}

			function setNoteError(errorMessage)
			{
				jQuery("div#noteSaveErrorMessage").text(errorMessage);
			}

			function clearNoteError()
			{
				setNoteError("");
			}

			function setNoteStatus(message)
			{
				jQuery("div#noteSaveStatusMessage").text(message);
			}

			function clearNoteStatus()
			{
				setNoteStatus("");
			}

			function notesIncrementAndLoadMore(demographicNo)
			{
				if (notesRetrieveOk && $("encMainDiv").scrollTop <= 100)
				{
					notesRetrieveOk = false;
					notesCurrentTop = $("encMainDiv").children[0].id;
					notesLoader(ctx, notesOffset, notesIncrement, demographicNo, false).then(function ()
					{
						notesOffset += notesIncrement;
					});
				}
			}

			function notesLoader(ctx, offset, numToReturn, demographicNo, scrollToBottom)
			{
				var deferred = jQuery.Deferred();
				$("notesLoading").style.display = "inline";

				var noteToEditDeferred = jQuery.ajax({
					type: "GET",
					contentType: "application/json",
					dataType: "json",
					url: "../ws/rs/notes/" + demographicNo + "/noteToEdit/latest"
				});

				var noteListDeferred = jQuery.ajax({
					type: "GET",
					contentType: "application/json",
					dataType: "json",
					url: "../ws/rs/notes/" + demographicNo + "/all?numToReturn=" + numToReturn + "&offset=" + offset,
				});

				var tmpSaveDeferred = jQuery.ajax({
					type: "GET",
					contentType: "application/json",
					dataType: "json",
					url: "../ws/rs/notes/" + demographicNo + "/tmpSave",
				});

				jQuery.when(noteListDeferred, noteToEditDeferred, tmpSaveDeferred).done(
					function (noteListResponse, noteToEditResponse, tmpSaveResponse)
					{
						// XXX: handle error (check response[1] = 'success')

						var response = noteListResponse[0];

						var tmpSave = "";
						if(tmpSaveResponse[1] == "success")
						{
							tmpSave = tmpSaveResponse[0].body;
						}

						var noteToEdit = null;
						var issues = null;
						if (
							noteToEditResponse[1] == "success" &&
							noteToEditResponse[0].body
						)
						{
							noteToEdit = noteToEditResponse[0].body.encounterNote;
							issues = noteToEditResponse[0].body.assignedCMIssues;
						}
						else
						{
							noteToEdit = getEmptyNote(getProviderNo(), getAppointmentNo());
							noteToEdit.note = getFormattedReason();
						}

						if(tmpSave)
						{
							noteToEdit.note = tmpSave;
						}

						pageState.currentNoteData = Object.clone(noteToEdit);

						$("notesLoading").style.display = "none";
						displayNotes(demographicNo, response.body.notelist, noteToEdit, issues,
							scrollToBottom, offset);

						adjustCaseNote();
						observeTextArea();
						setSaveButtonVisibility();
						setTmpSaveTimer();

						if (typeof response !== undefined && 'body' in response)
						{
							notesRetrieveOk = response.body.moreNotes;
						}

						if (!notesRetrieveOk)
						{
							clearInterval(notesScrollCheckInterval);
						}

						deferred.resolve();
					});

				return deferred.promise();
			}

			function getFormattedReason()
			{
				var formattedReason = "";
				var reason = getReason();
				var appointmentDate = getAppointmentDate();

				if(reason == null)
				{
					reason = "";
				}

				if( appointmentDate == null || appointmentDate == "" || appointmentDate.toLowerCase() == "null")
				{
					formattedReason = "\n[" + moment().format("DD-MMM-YYYY") + " .: " + reason + "] \n";
				}
				else
				{
					var appointmentMoment = moment(appointmentDate);
					formattedReason = "\n[" + appointmentMoment.format("DD-MMM-YYYY") + " .: " + reason + "]\n";
				}

				return formattedReason;
			}

			function setSaveButtonVisibility()
			{
				var note = pageState.currentNoteData;

				if (note != null && note.isSigned)
				{
					$("saveImg").style.visibility = "hidden";
				}
				else
				{
					$("saveImg").style.visibility = "visible";
				}
			}

			function displayNotes(demographicNo, noteArray, noteToEdit, issues, scrollToBottom, offset)
			{
				var containerDiv = jQuery('div#encMainDiv');

				var noteToEditUuid = null;
				if (noteToEdit != null)
				{
					noteToEditUuid = noteToEdit.uuid;
				}

				var firstNoteNode = null;
				var foundNoteToEdit = false;
				jQuery.each(noteArray, function (index, note)
				{
					var noteNode = null;

					if (isEncounterNote(note))
					{
						var noteData = note;
						var noteIssues = null;
						var editThisNote = (offset == 0 && note.uuid == noteToEditUuid);
						if (editThisNote)
						{
							foundNoteToEdit = true;
							noteData = noteToEdit;
							noteIssues = issues;
						}
						noteNode = prependNoteEntry(containerDiv, index + offset + 1, noteData, noteIssues, demographicNo, editThisNote);
					}
					else
					{
						noteNode = buildNonNoteEntry(containerDiv, index + offset, note, null, demographicNo, editThisNote);
					}

					if (firstNoteNode === null)
					{
						firstNoteNode = noteNode;
					}
				});

				// If this is the first page, possibly show an extra note to edit
				if (offset == 0)
				{
					if (!foundNoteToEdit)
					{
						// Add an extra, editable note to the end
						var noteNode = appendNoteEntry(containerDiv, noteToEdit.noteId, noteToEdit, issues, demographicNo, true);
					}

					if (firstNoteNode === null)
					{
						firstNoteNode = noteNode;
					}
				}

				if (scrollToBottom)
				{
					containerDiv.scrollTop(containerDiv.prop("scrollHeight"));
				}
				else
				{
					firstNoteNode[0].scrollIntoView();
				}
			}

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

			function getContextPath()
			{
				return "${pageContext.request.contextPath}";
			}

			function getDemographicNo()
			{
				var demographicNo = ${junoEncounterForm.header.demographicNo};

				return demographicNo;
			}

			function getProviderNo()
			{
				var providerNo = ${junoEncounterForm.header.providerNo};

				return providerNo;
			}

			function getEncounterNoteHideBeforeDate()
			{

				return '${encounterNoteHideBeforeDateFormatted}';
			}

			function getDefaultEncounterType()
			{
				return '${junoEncounterForm.encType}';
			}

			function getReason()
			{
				return '${junoEncounterForm.reason}';
			}

			function getAppointmentDate()
			{
				return '${junoEncounterForm.appointmentDate}'
			}

			function getEncounterTypeArray()
			{
				return [
					'<bean:message key="oscarEncounter.faceToFaceEnc.title"/>',
					'<bean:message key="oscarEncounter.telephoneEnc.title"/>',
					'<bean:message key="oscarEncounter.emailEnc.title"/>',
					'<bean:message key="oscarEncounter.noClientEnc.title"/>'
				]
			}


			function buildNonNoteEntry(containerDiv, index, note, demographicNo)
			{
				var appointmentNo = getAppointmentNo();

				var date = moment(note.observationDate);

				var winName = "junoEncounterFormWindow";

				var onClickString = "";
				if (note.eformData)
				{
					onClickString = "popupPage(700,800,'" + winName + "','/eform/efmshowform_data.jsp" +
						"?appointment=" + encodeURIComponent(appointmentNo) +
						"&amp;fdid=" + encodeURIComponent(note.eformDataId) + "');";
				} else if (note.encounterForm)
				{

					var url = "../form/forwardshortcutname.jsp" +
						"?formname=" + encodeURIComponent(note.note) +
						"&demographic_no=" + encodeURIComponent(demographicNo) +
						"&appointmentNo=" + encodeURIComponent(appointmentNo) +
						"&formId=" + encodeURIComponent(note.noteId);

					onClickString = "popupPage(700,800,'" + winName + "','" + url + "');";
				}

				var templateParameters = {
					index: index,
					note: note,
					colour: getNoteColor(note),
					noteLineArray: note.note.split("\n"),
					formattedObservationDate: date.format('DD-MMM-YYYY H:mm'),
					onClickString: onClickString
				};


				var newNode = jQuery('#encounterNonNoteTemplate').tmpl(templateParameters);

				return newNode.prependTo(containerDiv);
			}


			function buildNoteEntry(index, note, issues, demographicNo, enableEdit)
			{
				var date = moment(note.observationDate);
				var formattedDate = "";
				if (date.isValid())
				{
					formattedDate = date.format('DD-MMM-YYYY H:mm');
				}
				var hideBeforeMoment = moment(getEncounterNoteHideBeforeDate());
				var observationMoment = moment(note.observationDate);

				if (hideBeforeMoment.isAfter(observationMoment))
				{
					var minimizeStyles = 'overflow: hidden; height: 1.1em;';
					minimizeStyles: minimizeStyles
				}

				// Make annotation url
				var annotationLabel = "anno" + moment().unix();
				var annotationUrl = getContextPath() + "/annotation/annotation.jsp" +
					"?atbname=" + encodeURIComponent(annotationLabel) +
					"&table_id=" + encodeURIComponent(note.noteId) +
					"&display=EChartNote" +
					"&demo=" + encodeURIComponent(URL);

				var encounterTypeArray = getEncounterTypeArray();

				var selectedEncounterType = getDefaultEncounterType();
				if(note.encounterType)
				{
					selectedEncounterType = note.encounterType;
				}

				var templateParameters = {
					index: index,
					contextPath: getContextPath(),
					note: note,
					issues: (issues == null ? [] : issues),
					noteLineArray: note.note.split("\n"),
					escapedNote: note.note.escapeHTML(),
					formattedObservationDate: formattedDate,
					collapseNote: hideBeforeMoment.isAfter(observationMoment),
					edit: enableEdit,
					annotationLabel: annotationLabel,
					annotationUrl: annotationUrl,
					encounterTypeArray: encounterTypeArray,
					selectedEncounterType: selectedEncounterType
				};

				return jQuery('#encounterNoteTemplate').tmpl(templateParameters);
			}

			function enableCalendar(noteId)
			{
				Calendar.setup({
					inputField : "observationDateInput" + noteId,
					ifFormat : "%d-%b-%Y %H:%M ",
					showsTime :true,
					button : "observationDate_cal",
					singleClick : true,
					step : 1
				});
			}

			function prependNoteEntry(containerDiv, index, note, issues, demographicNo, enableEdit)
			{
				var newNode = buildNoteEntry(index, note, issues, demographicNo, enableEdit);
				var returnNode = newNode.prependTo(containerDiv);

				if(enableEdit)
				{
					enableCalendar(note.noteId);
				}

				return returnNode;
			}

			function appendNoteEntry(containerDiv, index, note, issues, demographicNo, enableEdit)
			{
				var newNode = buildNoteEntry(index, note, issues, demographicNo, enableEdit);
				var returnNode = newNode.appendTo(containerDiv);

				if(enableEdit)
				{
					enableCalendar(note.noteId);
				}

				return returnNode;
			}

			function replaceNoteEntry(nodeToReplace, note, issues, demographicNo, enableEdit)
			{
				// Make sure the node being replaced has the right id format
				var elementId = nodeToReplace.attr('id');
				if (/^n\d*$/.test(elementId))
				{
					return null;
				}

				var index = elementId.substring(1);
				var newNode = buildNoteEntry(index, note, issues, demographicNo, enableEdit);
				var returnNode = nodeToReplace.replaceWith(newNode);

				if(enableEdit)
				{
					enableCalendar(note.noteId);
				}

				return returnNode;
			}

			function isEncounterNote(note)
			{
				if (note.document || note.rxAnnotation || note.eformData || note.encounterForm ||
					note.invoice || note.ticklerNote || note.cpp)
				{
					return false;
				}

				return true;
			}

			function getNoteColor(note)
			{
				if (note.eformData)
				{
					return '#008000';
				} else if (note.document)
				{
					return '#476BB3';
				} else if (note.rxAnnotation)
				{
					return '#7D2252';
				} else if (note.encounterForm)
				{
					return '#917611';
				} else if (note.invoice)
				{
					return '#254117';
				} else if (note.ticklerNote)
				{
					return '#FF6600';
				} else if (note.cpp)
				{
					if (note.issueDescriptions.indexOf('Family History as part of cpp') > -1)
					{
						return '#006600';
					} else if (note.issueDescriptions.indexOf('Other Meds as part of cpp') > -1)
					{
						return '#306754';
					} else if (note.issueDescriptions.indexOf('Risk Factors as part of cpp') > -1)
					{
						return '#993333';
					}

					return '#996633';
				}

				return '#000000';
			}

			function getEncounterSectionUrl(sectionName, demographicNo, appointmentNo, limit, offset)
			{
				var limitString = "";
				var offsetString = "";

				if (limit !== null)
				{
					limitString = "&limit=" + limit;
				}

				if (offset !== null)
				{
					offsetString = "&offset=" + offset;
				}

				return "../ws/rs/encounterSections/" + demographicNo + "/" + sectionName + "/?appointmentNo=" +
					appointmentNo + limitString + offsetString;
			}

			function getSaveCPPNoteUrl(demographicNo)
			{
				return "../ws/rs/notes/" + demographicNo + "/saveIssueNote";
			}

			function isCppSection(sectionName)
			{
				return ["SocHistory", "MedHistory", "Concerns", "Reminders"].indexOf(sectionName) != -1;
			}

			function getSectionRemote(sectionName, getAll, disableExpand)
			{
				var appointmentNo = getAppointmentNo();
				var demographicNo = getDemographicNo();

				var limit = null;
				var offset = null;
				if (!getAll)
				{
					limit = 6;
					offset = 0;
				}

				jQuery.ajax({
					type: "GET",
					contentType: "application/json",
					dataType: "json",
					url: getEncounterSectionUrl(sectionName, demographicNo, appointmentNo, limit, offset),
					success: function (response)
					{
						var containerDiv = jQuery('#' + sectionName + 'list');

						containerDiv.empty();

						jQuery.each(response.body.notes, function (index, note)
						{
							note.sectionName = sectionName;
							note.index = index;
							note.updateDateFormatted = "";
							if (note.updateDate !== null)
							{
								var updateMoment = moment(note.updateDate);
								note.updateDateFormatted = updateMoment.format("DD-MMM-YYYY");
							}

							note.rowClass = "encounterNoteOdd";
							if (index % 2 == 0)
							{
								note.rowClass = "encounterNoteEven";
							}

							// Show the close arrow on the first and last row
							if (!disableExpand && getAll && (index == 0 || index == response.body.notes.length - 1))
							{
								note.showCollapse = true;
							} else if (!disableExpand && !getAll && index == response.body.notes.length - 1)
							{
								note.showExpand = true;
							}

							var newNode;
							if (isCppSection(sectionName))
							{
								newNode = jQuery('#sectionCppNoteTemplate').tmpl(note);
							} else
							{
								newNode = jQuery('#sectionNoteTemplate').tmpl(note);
							}

							return newNode.appendTo(containerDiv);
						});
					}
				});


			}

			function showMenu(menuNumber, eventObj)
			{
				var menuId = 'menu' + menuNumber;
				return showPopup(menuId, eventObj);
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

			function copyCppToCurrentNote()
			{
				// Enable this when encounter notes work
				/*
				var currentNoteId = jQuery("input[name='noteId']").val();
				var currentNoteText = jQuery("#caseNote_note" + currentNoteId).val();
				currentNoteText += "\n";
				currentNoteText += jQuery("#noteEditTxt").val();
				jQuery("#caseNote_note" + currentNoteId).val(currentNoteText);
				*/
			}

			function addIssueToCPP(txtField, listItem)
			{
				var nodeId = listItem.id;
				var issueDescription = listItem.innerHTML;
				addIssue("frmIssueNotes", "issueIdList", "issueAutocompleteCPP", nodeId, issueDescription);

				$("issueChange").value = true;
			}

			function addIssueToCurrentNote(event)
			{
				var nodeId = jQuery('input#issueSearchSelectedId').val();
				var issueDescription = jQuery('input#issueSearchSelected').val();

				if(!nodeId)
				{
					return false;
				}

				addIssue("caseManagementEntryForm", "noteIssueIdList", "issueAutocomplete", nodeId, issueDescription);

				jQuery('input#issueSearchSelectedId').val("");
				jQuery('input#issueSearchSelected').val("");
			}

			function addIssue(formName, parentNodeId, autocompleteId, nodeId, issueDescription)
			{

				var size = 0;
				var found = false;
				var curItems = document.forms[formName].elements["issueId"];

				if (curItems && typeof curItems.length != "undefined")
				{
					size = curItems.length;

					for (var idx = 0; idx < size; ++idx)
					{
						if (curItems[idx].value == nodeId)
						{
							found = true;
							break;
						}
					}
				}
				else if (curItems && typeof curItems.value != "undefined")
				{
					found = curItems.value == nodeId;
				}

				if (!found)
				{
					var node = document.createElement("LI");

					var html = "<input type='checkbox' id='issueId' name='issue_id' checked value='" + nodeId + "'>" + issueDescription;
					new Insertion.Top(node, html);

					$(parentNodeId).appendChild(node);
					$(autocompleteId).value = "";
				}
			}

			function minView(e, nodeId)
			{
				toggleShrunkNote(e, nodeId, true);
			}

			function maxView(e, nodeId)
			{
				toggleShrunkNote(e, nodeId, false);
			}

			function toggleShrunkNote(e, nodeId, shrink)
			{
				//var txt = Event.element(e).parentNode.id;
				var noteDivId = "n" + nodeId;
				var noteTxtId = "txt" + nodeId;

				if (shrink)
				{
					Element.remove("quitImg" + nodeId);

					$(noteTxtId).addClassName("collapse");

					var maximizeImageTag = "<img title='Maximize Display' alt='Maximize Display' id='xpImg" + nodeId + "' name='expandViewTrigger' onclick='maxView(event, \"" + nodeId + "\")' style='float:right; margin-right:5px; margin-top: 2px;' src='" + ctx + "/oscarEncounter/graphics/triangle_down.gif'>";
					new Insertion.Top(noteDivId, maximizeImageTag);
				} else
				{
					Element.remove("xpImg" + nodeId);

					$(noteTxtId).removeClassName("collapse");

					var minimizeImageTag = "<img id='quitImg" + nodeId + "' onclick='minView(event, \"" + nodeId + "\")' style='float:right; margin-right:5px; margin-top: 2px;' src='" + ctx + "/oscarEncounter/graphics/triangle_up.gif'>";
					new Insertion.Top(noteDivId, minimizeImageTag);
				}
			}

			function submitIssue(event)
			{
				var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
				if (keyCode == 13)
				{
					if (pageState.submitIssues)
					{
						$("asgnIssues").click();
					}

					return false;
				}
			}

			function togglePrint(noteId, e)
			{
				var selected = ctx + "/oscarEncounter/graphics/printerGreen.png";
				var unselected = ctx + "/oscarEncounter/graphics/printer.png";
				var imgId = "print" + noteId;
				var idx;
				var idx2;
				var tmp = "";

				//see whether we're called in a click event or not
				if (e != null)
					Event.stop(e);

				//if selected note has been inserted into print queue, remove it and update image src
				//else insert note into print queue
				idx = noteIsQeued(noteId);
				if (idx >= 0)
				{
					$(imgId).src = unselected;

					//if we're slicing first note off list
					if (idx == 0)
					{
						idx2 = $F("notes2print").indexOf(",");
						if (idx2 > 0)
							tmp = $F("notes2print").substring(idx2 + 1);
					}
					//or we're slicing after first element
					else
					{
						idx2 = $F("notes2print").indexOf(",", idx);
						//are we in the middle of the list?
						if (idx2 > 0)
						{
							tmp = $F("notes2print").substring(0, idx);
							tmp += $F("notes2print").substring(idx2 + 1);
						}
						//or are we at the end of the list; don't copy comma
						else
							tmp = $F("notes2print").substring(0, idx - 1);

					}

					$("notes2print").value = tmp;
				}
				else
				{
					$(imgId).src = selected;
					if ($F("notes2print").length > 0)
						$("notes2print").value += "," + noteId;
					else
						$("notes2print").value = noteId;
				}

				return false;
			}

			function clearAll(e)
			{
				var idx;
				var noteId;
				var notesDiv;
				var pos;
				var imgId;

				Event.stop(e);

				//cycle through container divs for each note
				for (idx = 1; idx <= notesOffset; ++idx)
				{
					if ($("nc" + idx) == null) continue;

					notesDiv = $("nc" + idx).down('div');
					noteId = notesDiv.id.substr(1);  //get note id
					imgId = "print" + noteId;

					//if print img present, add note to print queue if not already there
					if ($(imgId) != null)
					{
						pos = noteIsQeued(noteId);
						if (pos >= 0)
							removePrintQueue(noteId, pos);
					}
				}

				if ($F("printCPP") == "true")
					printInfo("imgPrintCPP", "printCPP");

				if ($F("printRx") == "true")
					printInfo("imgPrintRx", "printRx");

				return false;
			}

			function noteIsQeued(noteId)
			{
				var foundIdx = -1;
				var curpos = 0;
				var arrNoteIds = $F("notes2print").split(",");

				for (var idx = 0; idx < arrNoteIds.length; ++idx)
				{
					if (arrNoteIds[idx] == noteId)
					{
						foundIdx = curpos;
						break;
					}
					curpos += arrNoteIds[idx].length + 1;
				}


				return foundIdx;
			}

			function printToday(e)
			{
				clearAll(e);

				var today = moment().format("DD-MMM-YYYY");
				$("printStartDate").value = today;
				$("printEndDate").value = $F("printStartDate");
				$("printopDates").checked = true;
			}

			function printInfo(img, item)
			{
				var selected = ctx + "/oscarEncounter/graphics/printerGreen.png";
				var unselected = ctx + "/oscarEncounter/graphics/printer.png";

				if ($F(item) == "true")
				{
					$(img).src = unselected;
					$(item).value = "false";
				}
				else
				{
					$(img).src = selected;
					$(item).value = "true";
				}

				return false;
			}

			function getPrintDates()
			{
				var startDate = $F("printStartDate");
				var endDate = $F("printEndDate");

				if(startDate.length == 0 || endDate.length == 0)
				{
					alert("<bean:message key="oscarEncounter.printDate.msg"/>");
					return null;
				}

				var startMoment = moment(startDate, "DD-MMM-YYYY");
				var endMoment = moment(endDate, "DD-MMM-YYYY");

				if(startMoment.isAfter(endMoment))
				{
					alert("<bean:message key="oscarEncounter.printDateOrder.msg"/>");
					return null;
				}

				var dateObject = {
					start: startMoment.toDate(),
					end: endMoment.toDate()
				};

				return dateObject;
			}

			function removePrintQueue(noteId, idx)
			{
				var unselected = ctx + "/oscarEncounter/graphics/printer.png";
				var imgId = "print" + noteId;
				var tmp = "";
				var idx2;

				$(imgId).src = unselected; //imgPrintgrey.src;

				//if we're slicing first note off list
				if (idx == 0)
				{
					idx2 = $F("notes2print").indexOf(",");
					if (idx2 > 0)
						tmp = $F("notes2print").substring(idx2 + 1);
				}
				//or we're slicing after first element
				else
				{
					idx2 = $F("notes2print").indexOf(",", idx);
					//are we in the middle of the list?
					if (idx2 > 0)
					{
						tmp = $F("notes2print").substring(0, idx);
						tmp += $F("notes2print").substring(idx2 + 1);
					}
					//or are we at the end of the list; don't copy comma
					else
						tmp = $F("notes2print").substring(0, idx - 1);

				}

				$("notes2print").value = tmp;
			}

			function printSetup(e)
			{
				if ($F("notes2print").length > 0)
				{
					$("printopSelected").checked = true;
				}
				else
				{
					$("printopAll").checked = true;
				}

				$("printOps").style.right = (pageWidth() - Event.pointerX(e)) + "px";
				$("printOps").style.bottom = (pageHeight() - Event.pointerY(e)) + "px";
				$("printOps").style.display = "block";

				return false;
			}

			function printNotes()
			{
				var printType = null;
				var dateObject = null;

				if ($("printopAll").checked)
				{
					printType = "all";
				}
				else if ($("printopDates").checked)
				{
					dateObject = getPrintDates();

					if(dateObject == null)
					{
						return false;
					}

					printType = "dates";
				}

				var selectedNoteCsv = $F("notes2print");

				var noteArray = [];
				if(selectedNoteCsv.length > 0)
				{
					noteArray = selectedNoteCsv.split(",");
				}

				var printConfig = {
					printType: printType,
					dates: dateObject,
					cpp: $F("printCPP"),
					rx: $F("printRx"),
					labs: $F("printLabs"),
					selectedList: noteArray
				};

				var jsonString = JSON.stringify(printConfig);

				var url = "../ws/rs/recordUX/" + getDemographicNo() + "/print?printOps=" + encodeURIComponent(jsonString);

				window.open(url, '_blank');

				return false;
			}

			function onClosing()
			{
				var noteId = jQuery("input#editNoteId").val();

				// Prepare data
				var noteData = getNoteDataById(noteId);

				// Save unfinished note on exit. The temp save stuff added in Oscar15 is too fragile
				// to depend on

				// Trim the notes because that happens when the note is saved
				if(pageState.currentNoteData.note.trim() != noteData.note.trim())
				{
					saveEncounterNote(false, false, true, false, false);
				}

				/*
				// XXX: make this work, whatever it is
				for (var idx = 0; idx < measurementWindows.length; ++idx)
				{
					if (!measurementWindows[idx].closed)
					{
						measurementWindows[idx].parentChanged = true;
					}
				}
				*/

				return null;
			}

			function init()
			{
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

				var issueURL = ctx + "/CaseManagementEntry.do" +
					"?method=issueList" +
					"&demographicNo=" + getDemographicNo() +
					"&providerNo=" + getProviderNo();
				issueAutoCompleter = new Ajax.Autocompleter("issueAutocomplete", "issueAutocompleteList", issueURL, {
					minChars: 3,
					indicator: 'busy',
					afterUpdateElement: saveIssueId,
					onShow: autoCompleteShowMenu,
					onHide: autoCompleteHideMenu
				});

				//var calculatorMenu = jQuery('#calculators_menu');

				//notesIncrement = parseInt("<%=OscarProperties.getInstance().getProperty("num_loaded_notes", "20") %>");

				var demographicNo = getDemographicNo();

				// Load a few extra notes initially, hopefully fill up the page
				notesLoader(ctx, 0, notesIncrement * 2, demographicNo, true).then(function ()
				{
					notesOffset += (notesIncrement * 2);
					notesScrollCheckInterval = setInterval(function ()
					{
						notesIncrementAndLoadMore(demographicNo)
					}, 50);
				});


				//bindCalculatorListener(calculatorMenu);
				window.onbeforeunload = onClosing;
			}

			// XXX: this is here to allow the old notes display to run
			var showIssue = false;
			var autoCompleted = new Object();
			var autoCompList = new Array();
			var itemColours = new Object();
			var changeIssueFunc;

			function setupNotes()
			{
			}

			function getActiveText(e)
			{
			}

			function fullView(e)
			{
			}

			function saveIssueId(txtField, listItem)
			{
				jQuery('input#issueSearchSelectedId').val(listItem.id);
				jQuery('input#issueSearchSelected').val(listItem.innerHTML);
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

			function updateIssues(e)
			{
			}

			function menuAction()
			{
			}


		</script>


		<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}/css/oscarRx.css" />">

			<%--

					XXX: Not sure what this is
					<oscar:customInterface section="cme" />
			--%>

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

			<li class="encounterNote \${rowClass}">
				{{if showExpand }}
				<a href="#" class="expandCasemgmtSidebar" onclick="getSectionRemote('\${sectionName}', true, false); return false;" title="\${remainingNotes} more items">
					<img id="imgpreventions5" src="graphics/expand.gif" />&nbsp;&nbsp;
				</a>
				{{else showCollapse }}
				<a href="#" class="expandCasemgmtSidebar" onclick="getSectionRemote('\${sectionName}', false, false); return false;" title="\${remainingNotes} more items">
					<img id="imgpreventions5" src="../oscarMessenger/img/collapse.gif" />&nbsp;&nbsp;
				</a>
				{{else}}
				<a border="0" class="expandCasemgmtSidebar">
					<img id="img\${sectionName}1" src="/images/clear.gif" />&nbsp;&nbsp;
				</a>
				{{/if}}
				<span class="encounterNoteTitle">
					<a
						class="links"
						style="color: \${colour};"
						onmouseover="this.className='linkhover'"
						onmouseout="this.className='links'"
						href="#"
						onclick="\${onClick};return false;"
						title="Flu=Influenza vaccine"
					>
						\${text}
					</a>
				</span>
				<span class="encounterNoteDate">
					{{if updateDateFormatted}}...{{/if}}<a
						class="links"
						style="margin-right: 2px; color: \${colour};"
						onmouseover="this.className='linkhover'"
						onmouseout="this.className='links'"
						href="#"
						onclick="\${onClick};return false;"
						title="DTaP=Diphtheria, Tetanus, Acellular Pertussis - pediatric"
					>
						\${value}
						\${updateDateFormatted}
					</a>
				</span>
			</li>

		</script>


		<script id="sectionCppNoteTemplate" type="text/x-jquery-tmpl">
			<li class="\${rowClass}">
				<span id="spanListNote\${index}">
					<a class="topLinks"
					   onmouseover="this.className='topLinkhover'"
					   onmouseout="this.className='topLinks'"
					   title="Rev:\${revision} - Last update:\${updateDateFormatted}"
					   id="listNote\${noteId}"
					   href="#"
					   onclick="\${onClick};return false;"
					   style="width:100%;overflow:scroll;" >
							\${text}
					</a>
				</span>
			</li>

		</script>

		<script id="encounterNonNoteTemplate" type="text/x-jquery-tmpl">
			<div id="nc\${index}" style="display:block;" class="note">

				<div id="n\${note.noteId}">

					<div id="wrapper\${note.noteId}" style="color:#FFFFFF;background-color:\${colour};color:white;font-size:10px;">

						<div id="txt\${note.noteId}" style="display:inline-block;overflow-wrap:break-word;word-wrap:break-word;max-width:60%;">
							\${note.note}
						</div>
						<div id="observation671898" style="display:inline-block;font-size: 11px; float: right; margin-right: 3px;">
							Encounter Date:&nbsp;
							<span id="obs">\${formattedObservationDate}</span>
							&nbsp;
							{{if note.revision}}
								 Rev

								<a style="color:#ddddff;" href="#" onclick="return showHistory('\${note.noteId}', event);">\${note.revision}</a>
							{{/if}}
							{{if note.eformData || note.encounterForm}}
								<a class="links" title="View eForm" id="viewEFORM122582" href="#"
									onclick="\${onClickString};return false;"
									style="float: right; margin-right: 5px; font-size: 10px;"> View </a>
							{{/if}}
						</div>
					</div>
				</div>
			</div>

		</script>

		<script id="encounterNoteTemplate" type="text/x-jquery-tmpl">

			<div id="nc\${index}" style="" class="note noteRounded _nifty junoEncounterNote">
				<b class="artop" style="background-color: transparent;">
					<b class="re1" style="background-color: rgb(204, 204, 204); border-color: rgb(0, 0, 0);"></b>
					<b class="re2" style="background-color: rgb(204, 204, 204); border-color: rgb(0, 0, 0);"></b>
					<b class="re3" style="background-color: rgb(204, 204, 204); border-color: rgb(0, 0, 0);"></b>
					<b class="re4" style="background-color: rgb(204, 204, 204); border-color: rgb(0, 0, 0);"></b>
				</b>

				<input type="hidden" id="signed\${note.noteId}" value="\${note.isSigned}" />
				<%--
				 XXX: I don't think this gets sent
				<input type="hidden" id="full\${note.noteId}" value="\${note.}" />
				<input type="hidden" id="bgColour\${note.noteId}" value="\${note.}" />
				<input type="hidden" id="editWarn\${note.noteId}" value="\${note.}" />
				--%>

				<div id="n\${note.noteId}" style="border-left: 1px solid rgb(0, 0, 0); border-right: 1px solid rgb(0, 0, 0);">

					{{if !edit}}
						{{if collapseNote}}
							<img title="Maximize Display"
								alt="Maximize Display"
								id="xpImg\${note.noteId}"
								name="expandViewTrigger"
								onclick="maxView(event, '\${note.noteId}')"
								style="float:right; margin-right:5px; margin-top: 2px;"
								src="\${context}/oscarEncounter/graphics/triangle_down.gif" />
						{{else}}
							<img title="Minimize Display"
								id="quitImg\${note.noteId}"
								alt="Minimize Display"
								onclick="minView(event, '\${note.noteId}')"
								style="float: right; margin-right: 5px; margin-bottom: 3px; margin-top: 2px;"
								src="\${context}/oscarEncounter/graphics/triangle_up.gif" />
						{{/if}}

						<img
							title="Print"
							id="print\${note.noteId}"
							alt="Toggle Print Note"
							onclick="togglePrint('\${note.noteId}', event)"
							style="float: right; margin-right: 5px; margin-top: 2px;"
							src="\${context}/oscarEncounter/graphics/printer.png" />
					{{/if}}

					<input type="hidden" id="uuid\${note.noteId}" value="\${note.uuid}" />
					<input type="hidden" id="observationDate\${note.noteId}" value="\${note.observationDate}" />
					<input type="hidden" id="providerNo\${note.noteId}" value="\${note.providerNo}" />
					<input type="hidden" id="encounterType\${note.noteId}" value="\${note.encounterType}" />
					<input type="hidden" id="isSigned\${note.noteId}" value="\${note.isSigned}" />
					<input type="hidden" id="isVerified\${note.noteId}" value="\${note.isVerified}" />
					<input type="hidden" id="appointmentNo\${note.noteId}" value="\${note.appointmentNo}" />
					{{if edit}}
						<input type="hidden" id="editNoteId" value="\${note.noteId}" />
						<div class="error" id="noteSaveErrorMessage"></div>
						<textarea
							tabindex="7"
							cols="84"
							rows="10"
							class="txtArea"
							wrap="soft"
							style="line-height: 1.1em;"
							name="caseNote_note"
							id="caseNote_note\${note.noteId}">
{{html escapedNote}}</textarea>

						<div class="sig" style="display:inline;" id="sig\${note.noteId}">
							<%--
							<%@ include file="../casemgmt/noteIssueList.jsp"%>
							--%>
						</div>
					{{else}}
						<a
							title="Edit"
							id="edit\${note.noteId}"
							href="#"
							onclick="editEncounterNote(event, '\${note.noteId}');return false;"
							style="float: right; margin-right: 5px; font-size: 10px;">

							Edit
						</a>

						<a
							href=""
							onclick="window.open('/lab/CA/ALL/sendOruR01.jsp?noteId=\${note.noteId}', 'eSend');return(false);"
							title="Send Electronically" style="float: right; margin-right: 5px; font-size: 10px;">

							eSend
						</a>

						<input
							type="image"
							id="anno\${note.noteId}"
							src="/oscarEncounter/graphics/annotation.png"
							title="Annotation"
							style="float: right; margin-right: 5px; margin-bottom: 3px; height:10px;width:10px"
							onclick="window.open('\${annotationUrl}','anwin','width=400,height=500');$('annotation_attribname').value='\${annotationLabel}'; return false;"
						>

						<div id="wrapper\${note.noteId}" style="clear:right;">

							<div
								id="txt\${note.noteId}"
								{{if collapseNote}}
									class="collapse"
								{{/if}}
								style="display:inline-block;overflow-wrap:break-word;word-wrap:break-word;max-width:100%;">

								{{each noteLineArray}}
									\${$value}<br>
								{{/each}}
							</div>
						</div>
					{{/if}}

					<div id="sig\${note.noteId}" class="sig" style="clear:both;color:#000000;background-color:#CCCCFF;">
						<div id="sumary\${note.noteId}">
							<div id="observation\${note.noteId}" style="font-size: 11px; float: right; margin-right: 3px;">
								Encounter Date:&nbsp;
								{{if !edit}}
									<span id="obs\${note.noteId}">\${formattedObservationDate}</span>&nbsp;
								{{else}}
									&nbsp;<img src="\${contextPath}/images/cal.gif" id="observationDate_cal" alt="calendar">&nbsp;
									<input type="text"
										id="observationDateInput\${note.noteId}"
										name="observation_date"
										ondblclick="this.value='';"
										class="observationDate"
										readonly="readonly"
										value="\${formattedObservationDate}">
								{{/if}}
								Rev

								<a href="#" onclick="return showHistory('\${note.noteId}', event);">\${note.revision}</a>

							</div>

							<div style="font-size: 11px;">
								<span style="float: left;">Editors:</span>
								<ul style="list-style: none inside none; margin: 0px;">
									{{each note.editorNames}}
										<li>\${$value};</li>
									{{/each}}
								</ul>
							</div>
							<div style="font-size: 11px; clear: right; margin-right: 3px; float: right;">
								Enc Type:&nbsp;
								{{if !edit}}
									<span id="encType\${note.noteId}">\${note.encounterType}</span>
								{{else}}
									<select
										id="encounterTypeSelect\${note.noteId}"
										class="encTypeCombo"
										name="caseManagementEntryForm">

										<option value=""></option>
										{{each encounterTypeArray}}
											<option
												value="\${$value}"
												{{if $value == selectedEncounterType}}
													selected="selected"
												{{/if}}
											>\${$value}</option>
										{{/each}}
									</select>
								{{/if}}
							</div>

							{{if edit}}
								<div style="margin: 0px 0px 0px 3px; font-size: 11px;">
									<span style="float: left;"><bean:message key="oscarEncounter.assignedIssues.title"/></span>
									<ul id='noteIssueIdList' style='float: left; list-style: circle inside; margin: 0px;'></ul>
									{{each issues}}
										<li>

											<input type='checkbox' id='issueId' name='issue_id' checked value='\${$value.issue_id}'>
											\${$value.issue.description}
										</li>
									{{/each}}
									<%--
									XXX: load notes on page load
									<nested:iterate id="noteIssue" property="caseNote.issues"
													name="caseManagementEntryForm">
										<li><c:out value="${noteIssue.issue.description}" /></li>
									</nested:iterate>
									--%>
									</ul>
									<br style="clear: both;">
								</div>
								<div class="noteStatus" id="noteSaveStatusMessage"></div>
							{{else}}
								<div style="display: block; font-size: 11px;">
									<span style="float: left;">Assigned Issues</span>
									<ul style="float: left; list-style: circle inside none; margin: 0px;">
										{{each note.issueDescriptions}}
											<li>\${$value}</li>
										{{/each}}
									</ul>
									<br style="clear: both;">
								</div>
							{{/if}}
						</div>
					</div>
				</div>
				<b class="artop" style="background-color: transparent;">
					<b class="re4" style="background-color: rgb(204, 204, 204); border-color: rgb(0, 0, 0);"></b>
					<b class="re3" style="background-color: rgb(204, 204, 204); border-color: rgb(0, 0, 0);"></b>
					<b class="re2" style="background-color: rgb(204, 204, 204); border-color: rgb(0, 0, 0);"></b>
					<b class="re1" style="background-color: rgb(204, 204, 204); border-color: rgb(0, 0, 0);"></b>
				</b>
			</div>


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
								   onClick="popupPage(700,1000,'<bean:write name="junoEncounterForm"
																			property="header.windowName"/>','
									   <c:out value="${ctx}"/>
									   <bean:write name="junoEncounterForm"
												   property="header.demographicUrl"/>'); return false;"
								   title="<bean:message key="provider.appointmentProviderAdminDay.msgMasterFile"/>"><bean:write
										name="junoEncounterForm"
										property="header.formattedPatientName"/></a> <bean:write
								name="junoEncounterForm" property="header.formattedPatientInfo"/>
								<c:if test="${junoEncounterForm.header.echartAdditionalPatientInfoEnabled}">
									<bean:write name="junoEncounterForm"
												property="header.patientBirthdate"/>
								</c:if>

								<% //if (oscar.OscarProperties.getInstance().isEChartAdditionalPatientInfoEnabled())
									//{%>
								<%//}%>

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

				// XXX: Do we want the sharing center?  If so put this in the action form.
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
							 OnMouseOut="delay(5000)" window.status='Click to upload new photo' ;
							 return true;"
						onClick="popupUploadPage('uploadimage.jsp',${fn:escapeXml(junoEncounterForm.header.demographicNo)});return false;" />
					</c:when>
					<c:otherwise>
						<img style="cursor: pointer;"
							 src="${fn:escapeXml(junoEncounterForm.header.imageMissingPlaceholderUrl)}"
							 alt="No_Id_Photo" height="100" title="Click to upload new photo."
							 OnMouseOver="window.status='Click to upload new photo';return true"
							 onClick="popupUploadPage('../casemgmt/uploadimage.jsp',${fn:escapeXml(junoEncounterForm.header.demographicNo)});return false;"/>
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
					// XXX: Maybe replace this with JSON/Javascript code
				%>
				<c:forEach items="${noteSections}" var="sectionName" varStatus="loop">

					<c:set var="section" scope="page"
						   value="${junoEncounterForm.sections[sectionName]}"/>

					<div class="leftBox" id="${sectionName}" style="display: block;">

						<form style="display: none;" name="dummyForm" action="">
							<input type="hidden" id="reloadDiv" name="reloadDiv" value="none"
								   onchange="updateDiv();">
						</form>

						<div id='menuTitle${sectionName}'
							 style="width: 10%; float: right; text-align: center;">
							<h3 style="padding:0px; background-color: ${section.colour};">
								<a href="javascript:void(0);"
								   onclick="${section.onClickPlus};return false;"
								   onmouseover="return !showMenu('${section.menuId}', event);"
								>+</a>
							</h3>
						</div>

							<%-- Popup Menu --%>
						<c:if test="${not empty section.menuId}">
							<%-- XXX: put the 40 + 125 in a constant --%>
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
										<%-- XXX: Remove this once all titles are localized --%>
									<c:if test="${not empty section.titleKey}">
										<bean:message key="${section.titleKey}"/>
									</c:if>
									<c:if test="${empty section.titleKey}">
										${section.title}
									</c:if>
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
											   onclick="getSectionRemote('${sectionName}', true, false); return false;"
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
													<%--
													onclick="showEdit(
															event,
															'<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${section.title}</spring:escapeBody>',
															'<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${note.id}</spring:escapeBody>',
															'<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${note.editors}</spring:escapeBody>',
															'${observationDate}',
															'<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${note.revision}</spring:escapeBody>',
															'<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${note.text}</spring:escapeBody>',
															${fn:length(section.notes)},
															${noteLoop.index},
															'<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${note.noteIssuesString}</spring:escapeBody>',
															'<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${note.noteExtsString}</spring:escapeBody>',
															'<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${param.demographicNo}</spring:escapeBody>',
															);return false;"
													 --%>
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
							<div id="notCPP"
								 style="height: 70%; margin-left: 2px; background-color: #FFFFFF;">

								<html:form action="/CaseManagementView" method="post">
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
									<%-- TODO: fix later
									<input type="hidden" id="serverDate" value="<%=strToday%>">
									--%>
									<input type="hidden" id="resetFilter" name="resetFilter"
										   value="false">
									<div id="topContent"
										 style="float: left; width: 100%; margin-right: -2px; padding-bottom: 1px; background-color: #CCCCFF; font-size: 10px;">
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
															<nested:iterate id="provider"
																			name="providers">
																<c:if test="${filter_provider==provider.providerNo}">
																	<nested:write name="provider"
																				  property="formattedName"/>
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
														 property="issues">
											<div style="float: left; margin-left: 10px; margin-top: 0px;">
												<u><bean:message key="oscarEncounter.issues.title"/>:</u><br>
												<nested:iterate type="String" id="filter_issue"
																property="issues">
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
										<div id="filter"
											 style="display:none;background-color:#ddddff;padding:8px">
											<input type="button"
												   value="<bean:message key="oscarEncounter.showView.title" />"
												   onclick="return filter(false);"/>
											<input type="button"
												   value="<bean:message key="oscarEncounter.resetFilter.title" />"
												   onclick="return filter(true);"/>

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
																<li><html:multibox
																		property="filter_providers"
																		value="a"
																		onclick="filterCheckBox(this)"></html:multibox><bean:message
																		key="oscarEncounter.sortAll.title"/></li>
																<%
																	/*
																	// TODO: make this work (parts missing)
																	@SuppressWarnings("unchecked")
																	Set<Provider> providers = (Set<Provider>)request.getAttribute("providers");

																	String providerNo;
																	Provider prov;
																	Iterator<Provider> iter = providers.iterator();
																	while (iter.hasNext())
																	{
																		prov = iter.next();
																		providerNo = prov.getProviderNo();
																<li><html:multibox property="filter_providers" value="providerNo" onclick="filterCheckBox(this)"></html:multibox>prov.getFormattedName()</li>
																	}
																	*/
																%>
															</ul>
														</div>
													</td>
													<td style="font-size:inherit;background-color:#ccccff;border-left:solid #ddddff 4px;border-right:solid #ddddff 4px">
														<div style="height:150px;overflow:auto">
															<ul style="padding:0px;margin:0px;list-style:none inside none">
																<li><html:multibox
																		property="filter_roles"
																		value="a"
																		onclick="filterCheckBox(this)"></html:multibox><bean:message
																		key="oscarEncounter.sortAll.title"/></li>
																<%
																	/*
																	// TODO: make this work (parts missing)
																	@SuppressWarnings("unchecked")
																	List roles = (List)request.getAttribute("roles");
																	for (int num = 0; num < roles.size(); ++num)
																	{
																		Secrole role = (Secrole)roles.get(num);
																	}
																	*/
																%>
															</ul>
														</div>
													</td>
													<td style="font-size:inherit;background-color:#ccccff">
														<div style="height:150px;overflow:auto">
															<ul style="padding:0px;margin:0px;list-style:none inside none">
																<li><html:radio property="note_sort"
																				value="observation_date_asc">
																	<bean:message
																			key="oscarEncounter.sortDateAsc.title"/>
																</html:radio></li>
																<li><html:radio property="note_sort"
																				value="observation_date_desc">
																	<bean:message
																			key="oscarEncounter.sortDateDesc.title"/>
																</html:radio></li>
																<li><html:radio property="note_sort"
																				value="providerName">
																	<bean:message
																			key="oscarEncounter.provider.title"/>
																</html:radio></li>
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
															</ul>
														</div>
													</td>
													<td style="font-size:inherit;background-color:#ccccff;border-left:solid #ddddff 4px;border-right:solid #ddddff 4px">
														<div style="height:150px;overflow:auto">
															<ul style="padding:0px;margin:0px;list-style:none inside none">
																<li><html:multibox property="issues"
																				   value="a"
																				   onclick="filterCheckBox(this)"></html:multibox><bean:message
																		key="oscarEncounter.sortAll.title"/></li>
																<%
																	// TODO: make this work (parts missing)
													/*
													@SuppressWarnings("unchecked")
													List issues = (List)request.getAttribute("cme_issues");
													for (int num = 0; num < issues.size(); ++num)
													{
														CheckBoxBean issue_checkBoxBean = (CheckBoxBean)issues.get(num);
													}

													 */
																%>
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
													   type="text" value=""
													   onkeypress="return grabEnterGetTemplate(event)">

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
													   onClick="popupPage(600,800,'<bean:message
															   key="oscarEncounter.Index.popupSearchPageWindow"/>',$('channel').options[$('channel').selectedIndex].value+urlencode($F('keyword')) ); return false;">

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
											<div style="display:inline-block;text-align: left;"
												 id="toolbar">
												<input type="button"
													   value="<bean:message key="oscarEncounter.Filter.title"/>"
													   onclick="showFilter();"/>
												<%
													// TODO: make this work (parts missing)
									/*
									String roleName = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
									String pAge = Integer.toString(UtilDateUtilities.calcAge(bean.yearOfBirth,bean.monthOfBirth,bean.dateOfBirth));
									 */
													// TODO: make this work (parts missing)
												%>
													<%--<%@include file="calculatorsSelectList.jspf" %>--%>
													<%--<security:oscarSec roleName="<%=roleName%>" objectName="_admin.templates" rights="r"> --%>
													<%--
													<security:oscarSec roleName="<%=roleName%>" objectName="_newCasemgmt.templates" rights="r">
														<select style="width:100px;" onchange="javascript:popupPage(700,700,'Templates',this.value);">
															<option value="-1"><bean:message key="oscarEncounter.Header.Templates"/></option>
															<option value="-1">------------------</option>
															<security:oscarSec roleName="<%=roleName%>" objectName="_newCasemgmt.templates" rights="w">
																<option value="<%=request.getContextPath()%>/admin/providertemplate.jsp">New / Edit Template</option>
																<option value="-1">------------------</option>
															</security:oscarSec>
															<%
																EncounterTemplateDao encounterTemplateDao=(EncounterTemplateDao)SpringUtils.getBean("encounterTemplateDao");
																List<EncounterTemplate> allTemplates=encounterTemplateDao.findAll();

																for (EncounterTemplate encounterTemplate : allTemplates)
																{
																	String templateName=StringEscapeUtils.escapeHtml(encounterTemplate.getEncounterTemplateName());
															%>
															<option value="<%=request.getContextPath()+"/admin/providertemplate.jsp?dboperation=Edit&name="+templateName%>"><%=templateName%></option>
															<%
																}
															%>
														</select>
													</security:oscarSec>

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
				   onclick="Event.stop(event);return saveEncounterNote(false, false, false, true, false);"
				   title='<bean:message key="oscarEncounter.Index.btnSave"/>'>&nbsp;
			<input tabindex="18" type='image'
				   src="<c:out value="${ctx}/oscarEncounter/graphics/document-new.png"/>"
				   id="newNoteImg" onclick="newNote(event); return false;"
				   title='<bean:message key="oscarEncounter.Index.btnNew"/>'>&nbsp;
			<input tabindex="19" type='image'
				   src="<c:out value="${ctx}/oscarEncounter/graphics/note-save.png"/>"
				   id="signSaveImg"
				   onclick="Event.stop(event);return saveEncounterNote(true, false, true, true, false);"
				   title='<bean:message key="oscarEncounter.Index.btnSignSave"/>'>&nbsp;
			<input tabindex="20" type='image'
				   src="<c:out value="${ctx}/oscarEncounter/graphics/verify-sign.png"/>"
				   id="signVerifyImg"
				   onclick="Event.stop(event);return saveEncounterNote(true, true, true, true, false);"
				   title='<bean:message key="oscarEncounter.Index.btnSign"/>'>&nbsp;
			<c:if test="${junoEncounterForm.header.source == null}">
				<input tabindex="21" type='image'
					   src="<c:out value="${ctx}/oscarEncounter/graphics/dollar-sign-icon.png"/>"
					   onclick="Event.stop(event);return saveEncounterNote(true, false, true, true, true);"
					   title='<bean:message key="oscarEncounter.Index.btnBill"/>'>&nbsp;
			</c:if>
	    	<input tabindex="23" type='image'
				   src="<c:out value="${ctx}/oscarEncounter/graphics/system-log-out.png"/>"
				   onclick='closeEnc(event);return false;'
				   title='<bean:message key="global.btnExit"/>'>&nbsp;
	    	<input tabindex="24" type='image'
				   src="<c:out value="${ctx}/oscarEncounter/graphics/document-print.png"/>"
				   onclick="return printSetup(event);"
				   title='<bean:message key="oscarEncounter.Index.btnPrint"/>'
				   id="imgPrintEncounter">
    	</span>
		<div id="assignIssueSection">
			<!-- input type='image' id='toggleIssue' onclick="return showIssues(event);" src="<c:out value="${ctx}/oscarEncounter/graphics/issues.png"/>" title='<bean:message key="oscarEncounter.Index.btnDisplayIssues"/>'>&nbsp; -->
			<input tabindex="8" type="text"
				   id="issueAutocomplete" name="issueSearch"
				   style="z-index: 2;"
				   onkeypress="return submitIssue(event);"
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
				onclick="addIssueToCurrentNote(event); return false;"
				value="<bean:message key="oscarEncounter.assign.title"/>">
			<span id="busy" style="display: none">
	    		<img style="position: absolute;"
					 src="<c:out value="${ctx}/oscarEncounter/graphics/busy.gif"/>"
					 alt="<bean:message key="oscarEncounter.Index.btnWorking" />">
	    	</span>
		</div>
		<div style="padding-top: 3px;">
			<button type="button"
					onclick="return showHideIssues(event, 'noteIssues-resolved');">
				<bean:message
						key="oscarEncounter.Index.btnDisplayResolvedIssues"/></button>
			&nbsp;
			<button type="button"
					onclick="return showHideIssues(event, 'noteIssues-unresolved');">
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
					onclick="javascript:popupPage(500,200,'noteBrowser<%//TODO: Fix; bean.demographicNo%>','noteBrowser.jsp?demographic_no=<% //TODO: fix; bean.demographicNo%>&FirstTime=1');">
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
									onsubmit="return updateCPPNote();">

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
																				   onclick="$('archived').value='true';"
																				   style="padding-right: 10px;">
					<input type="image"
						   src="<c:out value="${ctx}/oscarEncounter/graphics/note-save.png"/>"
						   title='<bean:message key="oscarEncounter.Index.btnSignSave"/>'
						   onclick="$('archived').value='false';" style="padding-right: 10px;">
					<input type="image"
						   src="<c:out value="${ctx}/oscarEncounter/graphics/system-log-out.png"/>"
						   title='<bean:message key="global.btnExit"/>'
						   onclick="this.focus();hideEdit();return false;">
						   <%--onclick="this.focus();$('channel').style.visibility ='visible';$('showEditNote').style.display='none';return false;">--%>
				</span>
								<bean:message key="oscarEncounter.Index.btnPosition"/>
								<select id="position" name="position">
									<option id="popt0" value="1">1</option>
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
												 onclick="return printInfo(this,'printCPP');"
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
												 onclick="return printInfo(this, 'printRx');"
												 src='<c:out value="${ctx}"/>/oscarEncounter/graphics/printer.png'>&nbsp;<bean:message
												key="oscarEncounter.Rx.title"/></td>
									</tr>
									<tr>
										<td></td>
										<td><img style="cursor: pointer;"
												 title="<bean:message key="oscarEncounter.print.title"/>"
												 id='imgPrintLabs'
												 alt="<bean:message key="oscarEncounter.togglePrintLabs.title"/>"
												 onclick="return printInfo(this, 'printLabs');"
												 src='<c:out value="${ctx}"/>/oscarEncounter/graphics/printer.png'>&nbsp;<bean:message
												key="oscarEncounter.Labs.title"/></td>
									</tr>
									<!--  extension point -->
									<tr id="printDateRow">
										<td><input type="radio" id="printopDates" name="printop"
												   value="dates">
											<bean:message key="oscarEncounter.Index.PrintDates"/>&nbsp;<a
													style="font-variant: small-caps;" href="#"
													onclick="return printToday(event);"><bean:message
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
										   value="Print" onclick="return printNotes();">

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
										onclick="$('printOps').style.display='none'; return clearAll(event);">
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
