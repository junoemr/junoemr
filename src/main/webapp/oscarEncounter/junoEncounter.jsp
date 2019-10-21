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
<%@ page import="org.oscarehr.sharingcenter.SharingCenterUtil" %>

<jsp:useBean id="junoEncounterForm" scope="request" type="org.oscarehr.casemgmt.web.formbeans.JunoEncounterFormBean"/>

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
		<c:set var="ctx" value="${pageContext.request.contextPath}"	scope="request" />

		<link rel="stylesheet" href="<c:out value="${ctx}"/>/css/casemgmt.css" type="text/css">
		<link rel="stylesheet" href="<c:out value="${ctx}"/>/oscarEncounter/encounterStyles.css" type="text/css">
		<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}"/>/css/print.css" media="print">

		<script src="<c:out value="${ctx}/js/jquery-1.7.1.min.js"/>"></script>
		<script src="<c:out value="${ctx}/share/documentUploader/jquery.tmpl.min.js"/>"></script>
		<script language="javascript">
			jQuery.noConflict();
		</script>

		<script src="<c:out value="${ctx}"/>/share/javascript/prototype.js" type="text/javascript"></script>
		<script src="<c:out value="${ctx}"/>/share/javascript/scriptaculous.js" type="text/javascript"></script>
		<script src="<c:out value="${ctx}"/>/library/moment.js" type="text/javascript"></script>

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

		<script type="text/javascript" src="<c:out value="${ctx}/casemgmt/EncounterTimer.js"/>"></script>

		<script type="text/javascript">

			var ctx = '<c:out value="${ctx}"/>';

			var notesOffset = 0;
			var notesIncrement = <%= OscarProperties.getNumLoadedNotes(20) %>;
			var notesRetrieveOk = false;
			var notesCurrentTop = null;
			var notesScrollCheckInterval = null;

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

			function prepareExtraFields(cpp, extObj)
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

				//var extsArr = exts.split(";");
				//for (var i = 0; i < extsArr.length; i += 2)
				//{
					console.log(extObj);
					for (var j = 0; j < exFields.length; j++)
					{
						console.log(exFields[j]);
						if (extObj.hasOwnProperty(exFields[j]))
						{
							$(exFields[j]).value = extObj[exFields[j]];
							continue;
						}
						/*
						if (extsArr[i] == exKeys[j])
						{
							$(exFields[j]).value = extsArr[i + 1];
							continue;
						}
						 */
					}
				//}
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
				if(date == null || date == "")
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

				if(note != null)
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
				}
				else
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
				if(note != null)
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
				}
				else
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
						}
						else
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
				}
				else
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
				}
				else
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

				jQuery.map(unindexed_array, function(n, i)
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

				jQuery.when.all(deferredArray).then(function(response)
				{
					var adjustedArray = response;
					if(deferredArray.length == 1)
					{
						adjustedArray = [response];
					}

					var assignedIssueArray = [];

					for(var j = 0; j < adjustedArray.length; j++)
					{
						assignedIssueArray.push(adjustedArray[j][0]);
					}

					deferred.resolve(assignedIssueArray);
				});

				return deferred.promise();
			}

			function getCPPObjectFromForm(form, issueIdArray)
			{
				var deferred = jQuery.Deferred();

				var noteId = 0;
				if(form.noteEditId)
				{
					noteId = form.noteEditId;
				}

				getAssignedIssueArray(issueIdArray).then(function(result)
				{
					var assignedIssueArray = [];
					for(var i = 0; i < result.length; i++)
					{

						var assignedIssue = {
							acute: false,
							certain: false,
							demographic_no: null,
							id: null,
							issue: result[i],
							issue_id: result[i].id,
							major: false,
							program_id: null,
							resolved: false,
							type: null,
							unchecked: false,
							unsaved: true,
							update_date: new Date()
						};

						assignedIssueArray.push(assignedIssue);
					}

					var result = {
						"assignedCMIssues": assignedIssueArray,
						"encounterNote":{
							"noteId": noteId,
							"uuid": form.noteUuid,
							"position":form.position,
							"note": form.value,
							"archived": form.archived,
							"cpp":true,
							"editable":true,
							"isSigned":true,
							"observationDate": new Date(),
							//"observationDate":"2019-10-08T21:23:49.441Z",
							"encounterType":"",
							"encounterTime":"",
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
							"procedureDate":getUTCDateFromString(form.proceduredate),
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
				if(dateString == null || dateString == "")
				{
					return null;
				}

				var dateMoment = moment(dateString);

				if(!dateMoment.isValid())
				{
					return null;
				}

				return dateMoment.toDate();
			}

			function updateCPPNote(cppType)
			{
				var demographicNo = <c:out value="${junoEncounterForm.header.demographicNo}" />;
				var form = jQuery('#frmIssueNotes');
				var formData = getFormData(form);

				var issueIdArray = [];
				jQuery("input:checkbox[name=issue_id]:checked").each(function(){
					issueIdArray.push(jQuery(this).val());
				});

				getCPPObjectFromForm(formData, issueIdArray).then(function(restData)
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
							if(response.status == "SUCCESS")
							{
								// Close window
								hideEdit();

								// Reload note list
								getSectionRemote(formData.noteSummaryCode, true, true);

								return false;
							}
							else
							{
								// Show error
								jQuery('#editNoteError').html(response.error.message);
							}
						}
					});
				});

				return false;
			}

			// XXX: NO!!  NO NO NO!
			<%--
			function updateCPPNote()
			{
				var url = $("frmIssueNotes").action;
				var reloadUrl = $("reloadUrl").value;
				var div = $("containerDiv").value;

				$('channel').style.visibility = 'visible';
				$('showEditNote').style.display = 'none';

				var curItems = document.forms["frmIssueNotes"].elements["issueId"];
				if (typeof curItems.length != "undefined")
				{
					size = curItems.length;

					for (var idx = 0; idx < size; ++idx)
					{
						if (!curItems[idx].checked)
						{
							$("issueChange").value = true;
							break;
						}
					}
				}
				else
				{
					$("issueChange").value = true;
				}

				$("reloadUrl").value = "method=junoEncounterSaveSuccess";

				var params = $("frmIssueNotes").serialize();
				var sigId = "sig" + caseNote.substr(13);
				var objAjax = new Ajax.Request(
					url,
					{
						method: 'post',
						evalScripts: true,
						postBody: params,
						onSuccess: function(request)
						{
							if (request.responseText.length > 0)
							{
								$(div).update(request.responseText);
							}
							if ($("issueChange").value == "true")
							{
								ajaxUpdateIssues("edit", sigId);
								$("issueChange").value = false;
							}

							notifyDivLoaded($(div).id);
						},
						onFailure: function(request)
						{
							$(div).innerHTML = "<h3>" + div + "<\/h3>Error: " + request.status;
						}
					}
				);
				return false;

			}
			--%>

			function hideEdit()
			{
				$('showEditNote').style.display='none';
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
									return viewFullChart(ctx, false);
								}
								scrollDownInnerBar();

							}
							else
							{
								$("quickChart").innerHTML = fullChartMsg;
								$("quickChart").onclick = function()
								{
									return viewFullChart(ctx, true);
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

			function notesIncrementAndLoadMore(demographicNo)
			{
				if (notesRetrieveOk && $("encMainDiv").scrollTop == 0)
				{
					notesRetrieveOk = false;
					notesCurrentTop = $("encMainDiv").children[0].id;
					notesLoader(ctx, notesOffset, notesIncrement, demographicNo, false).then(function()
					{
						notesOffset += notesIncrement;
					});
				}
			}

			function notesLoader(ctx, offset, numToReturn, demoNo, scrollToBottom)
			{
				var deferred = jQuery.Deferred();
				$("notesLoading").style.display = "inline";

				jQuery.ajax({
					type: "GET",
					contentType: "application/json",
					dataType: "json",
					url: "../ws/rs/notes/" + demoNo + "/all?numToReturn=" + numToReturn+ "&offset=" + offset,
					success: function(response)
					{

						$("notesLoading").style.display = "none";
						displayNotes(response.body.notelist, scrollToBottom, offset);


						if(typeof response !== undefined && 'body' in response)
						{
							notesRetrieveOk = response.body.moreNotes;
						}

						if (!notesRetrieveOk)
						{
							clearInterval(scrollCheckInterval);
						}

						deferred.resolve();
					}
				});

				return deferred.promise();
			}

			function displayNotes(noteArray, scrollToBottom, offset)
			{
				if(!jQuery.isArray(noteArray) || noteArray.length == 0)
				{
					return;
				}

				var containerDiv = jQuery('div#encMainDiv');

				var firstNoteNode = null;
				jQuery.each(noteArray, function(index, note)
				{
					var noteNode = null;

					if(isEncounterNote(note))
					{
						noteNode = buildNoteEntry(containerDiv, index + offset, note);
					}
					else
					{
						noteNode = buildNonNoteEntry(containerDiv, index + offset, note);
					}

					if(firstNoteNode === null)
					{
						firstNoteNode = noteNode;
					}
				});

				if(scrollToBottom)
				{
					containerDiv.scrollTop(containerDiv.prop("scrollHeight"));
				}
				else
				{
					console.log(firstNoteNode);
					firstNoteNode[0].scrollIntoView();
				}



				// XXX: this needs to work when regular notes are added
				<%--
				//display last saved note for editing
				if (note.getNoteId() != null && !"".equals(note.getNoteId()) && note.getNoteId().intValue() == savedId )
				{
					found = true;
					%>
					<script>
					savedNoteId=<%=note.getNoteId()%>;
					</script>
						<%
					if (OscarProperties.getInstance().getBooleanProperty("note_program_ui_enabled", "true")) {
				%>
				<script>
					_setupNewNote();
				</script>
						<% } %>

				<img title="<bean:message key="oscarEncounter.print.title"/>" id='print<%=globalNoteId%>' alt="<bean:message key="oscarEncounter.togglePrintNote.title"/>" onclick="togglePrint(<%=globalNoteId%>, event)" style='float: right; margin-right: 5px;' src='<%=ctx %>/oscarEncounter/graphics/printer.png' />
				<textarea tabindex="7" cols="84" rows="10" class="txtArea" wrap="soft" style="line-height: 1.1em;" name="caseNote_note" id="caseNote_note<%=savedId%>"><%=caseNote_note%></textarea>

				<div class="sig" style="display:inline;<%=bgColour%>" id="sig<%=globalNoteId%>">
					<%@ include file="noteIssueList.jsp"%>
				</div>
			}
			--%>
				console.log('========================================================');
				console.log(noteArray);
				console.log('========================================================');
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

			function buildNonNoteEntry(containerDiv, index, note)
			{
				var appointmentNo = getAppointmentNo();

				var date = moment(note.observationDate);

				var onClickString = "";
				if(note.eformData)
				{
					onClickString = "popupPage(700,800,'1689724085','/eform/efmshowform_data.jsp" +
						"?appointment=" + appointmentNo +
						"&amp;fdid=" + note.eformDataId + "');";
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

				/*
				var noteDiv = jQuery('<div id="nc' + index + '" style="display:block;" class="note" />')
					.prependTo(containerDiv);

				var noteDiv2 = jQuery('<div id="n' + note.noteId + '" />')
					.appendTo(noteDiv);

				var noteDiv3 = jQuery('<div id="wrapper' + note.noteId + '" style="color:#FFFFFF;background-color:' + color + ';color:white;font-size:10px;">')
					.appendTo(noteDiv2);


				var noteDiv4 = jQuery('<div id="txt' + note.noteId + '" style="display:inline-block;overflow-wrap:break-word;word-wrap:break-word;max-width:60%;">')
					.append(note.note)
					.appendTo(noteDiv3);


				var noteDiv5 = jQuery('<div id="observation671898" style="display:inline-block;font-size: 11px; float: right; margin-right: 3px;">')
					.append(" Encounter Date:&nbsp; ")
					.appendTo(noteDiv3);

				var noteSpan = jQuery('<span id="obs671898" />')
					.append(date.format('DD-MMM-YYYY H:mm'))
					.appendTo(noteDiv5);

				noteDiv5.append(" &nbsp; Rev ");

				noteDiv5.append('<a style="color:#ddddff;" href="#" onclick="return showHistory(\'' + note.noteId +'\', event);">' + note.revision + '</a>');


				return noteDiv;
				 */
			}

			function buildNoteEntry(containerDiv, index, note)
			{
				var date = moment(note.observationDate);
				var templateParameters = {
					index: index,
					note: note,
					noteLineArray: note.note.split("\n"),
					formattedObservationDate: date.format('DD-MMM-YYYY H:mm')
				};

				var newNode = jQuery('#encounterNoteTemplate').tmpl(templateParameters);

				return newNode.prependTo(containerDiv);
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
				}
				else if (note.document)
				{
					return '#476BB3';
				}
				else if (note.rxAnnotation)
				{
					return '#7D2252';
				}
				else if (note.encounterForm)
				{
					return '#917611';
				}
				else if (note.invoice)
				{
					return '#254117';
				}
				else if (note.ticklerNote)
				{
					return '#FF6600';
				}
				else if (note.cpp)
				{
					if(note.issueDescriptions.indexOf('Family History as part of cpp') > -1)
					{
						return '#006600';
					}
					else if(note.issueDescriptions.indexOf('Other Meds as part of cpp') > -1)
					{
						return '#306754';
					}
					else if(note.issueDescriptions.indexOf('Risk Factors as part of cpp') > -1)
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

				if(limit !== null)
				{
					limitString = "&limit=" + limit;
				}

				if(offset !== null)
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
				var demographicNo = <c:out value="${junoEncounterForm.header.demographicNo}" />;

				var limit = null;
				var offset = null;
				if(!getAll)
				{
					limit = 6;
					offset = 0;
				}

				jQuery.ajax({
					type: "GET",
					contentType: "application/json",
					dataType: "json",
					url: getEncounterSectionUrl(sectionName, demographicNo, appointmentNo, limit, offset),
					success: function(response)
					{
						var containerDiv = jQuery('#' + sectionName + 'list');

						containerDiv.empty();

						jQuery.each(response.body.notes, function(index, note)
						{
							note.sectionName = sectionName;
							note.index = index;
							note.updateDateFormatted = "";
							if(note.updateDate !== null)
							{
								var updateMoment = moment(note.updateDate);
								note.updateDateFormatted = updateMoment.format("DD-MMM-YYYY");
							}

							note.rowClass = "encounterNoteOdd";
							if(index % 2 == 0)
							{
								note.rowClass = "encounterNoteEven";
							}

							// Show the close arrow on the first and last row
							if(!disableExpand && getAll && (index == 0 || index == response.body.notes.length - 1))
							{
								note.showCollapse = true;
							}
							else if(!disableExpand && !getAll && index == response.body.notes.length - 1)
							{
								note.showExpand = true;
							}

							var newNode;
							if(isCppSection(sectionName))
							{
								newNode = jQuery('#sectionCppNoteTemplate').tmpl(note);
							}
							else
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

			function addIssue2CPP(txtField, listItem)
			{
				var nodeId = listItem.id;
				var size = 0;
				var found = false;
				var curItems = document.forms["frmIssueNotes"].elements["issueId"];

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
				else if(curItems && typeof curItems.value != "undefined")
				{
					found = curItems.value == nodeId;
				}

				if (!found)
				{
					var node = document.createElement("LI");

					var html = "<input type='checkbox' id='issueId' name='issue_id' checked value='" + nodeId + "'>" + listItem.innerHTML;
					new Insertion.Top(node, html);

					$("issueIdList").appendChild(node);
					$("issueAutocompleteCPP").value = "";

				}

				$("issueChange").value = true;
			}


			function init()
			{
				// This was messing up the serialization of arrays to JSON so I removed it.
				delete Array.prototype.toJSON

				// Monkey Patch from https://stackoverflow.com/a/16208232
				if (typeof jQuery.when.all === 'undefined') {
					jQuery.when.all = function (deferreds) {
						return jQuery.Deferred(function (def) {
							jQuery.when.apply(jQuery, deferreds).then(
								function () {
									def.resolveWith(this, [Array.prototype.slice.call(arguments)]);
								},
								function () {
									def.rejectWith(this, [Array.prototype.slice.call(arguments)]);
								});
						});
					}
				}


				Date.prototype.toJSON = function(){ return moment(this).format(); }

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
						afterUpdateElement: addIssue2CPP,
						onShow: autoCompleteShowMenuCPP,
						onHide: autoCompleteHideMenuCPP
					}
				);

				//var calculatorMenu = jQuery('#calculators_menu');

				//notesIncrement = parseInt("<%=OscarProperties.getInstance().getProperty("num_loaded_notes", "20") %>");

				var demographicNo = ${junoEncounterForm.header.demographicNo};

				// Load a few extra notes initially, hopefully fill up the page
				notesLoader(ctx, 0, notesIncrement * 2, demographicNo, true).then(function()
				{
					notesOffset += (notesIncrement * 2);
					notesScrollCheckInterval = setInterval(function()
					{
						notesIncrementAndLoadMore(demographicNo)
					}, 500);
				});


				//bindCalculatorListener(calculatorMenu);
			}

/*			$(document).ready(function()
			{
				init();
			});

			document.observe('dom:loaded', function(){
				init();
			});

 */

			// XXX: this is here to allow the old notes display to run
			var showIssue = false;
			var autoCompleted = new Object();
			var autoCompList = new Array();
			var itemColours = new Object();
			var changeIssueFunc;
			function setupNotes()
			{
			}
			function monitorCaseNote(e)
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
			}
			function autoCompleteShowMenu(element, update)
			{
			}
			function autoCompleteHideMenu(element, update)
			{
			}
			function updateIssues(e)
			{
			}
			function menuAction()
			{
			}
			function setTimer()
			{
			}


		</script>


		<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}/css/oscarRx.css" />">

<%--

		XXX: Not sure what this is
		<oscar:customInterface section="cme" />
--%>

			<style type="text/css">

			html {overflow-y: scroll; }

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
							{{if note.eformData}}
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

			<div id="nc\${index}" style="display: block; padding-top: 0px; padding-bottom: 0px;" class="note noteRounded _nifty">
				<b class="artop" style="background-color: transparent;">
					<b class="re1" style="background-color: rgb(204, 204, 204); border-color: rgb(0, 0, 0);"></b>
					<b class="re2" style="background-color: rgb(204, 204, 204); border-color: rgb(0, 0, 0);"></b>
					<b class="re3" style="background-color: rgb(204, 204, 204); border-color: rgb(0, 0, 0);"></b>
					<b class="re4" style="background-color: rgb(204, 204, 204); border-color: rgb(0, 0, 0);"></b>
				</b>

				<input type="hidden" id="signed\${index}" value="true" style="border-left: 1px solid rgb(0, 0, 0); border-right: 1px solid rgb(0, 0, 0);">
				<input type="hidden" id="full\${index}" value="true" style="border-left: 1px solid rgb(0, 0, 0); border-right: 1px solid rgb(0, 0, 0);">
				<input type="hidden" id="bgColour\${index}" value="color:#000000;background-color:#CCCCFF;" style="border-left: 1px solid rgb(0, 0, 0); border-right: 1px solid rgb(0, 0, 0);">
				<input type="hidden" id="editWarn\${index}" value="false" style="border-left: 1px solid rgb(0, 0, 0); border-right: 1px solid rgb(0, 0, 0);">

				<div id="n\${index}" style="border-left: 1px solid rgb(0, 0, 0); border-right: 1px solid rgb(0, 0, 0);">

					<img title="Minimize Display" id="quitImg\${index}" alt="Minimize Display" onclick="minView(event)" style="float: right; margin-right: 5px; margin-bottom: 3px; margin-top: 2px;" src="/oscarEncounter/graphics/triangle_up.gif" />

					<img title="Print" id="print\${index}" alt="Toggle Print Note" onclick="togglePrint('671920'   , event)" style="float: right; margin-right: 5px; margin-top: 2px;" src="/oscarEncounter/graphics/printer.png" />

					<a title="Edit" id="edit\${index}" href="#" onclick="editNote(event) ;return false;" style="float: right; margin-right: 5px; font-size: 10px;">
						Edit
					</a>

					<a href="" onclick="window.open('/lab/CA/ALL/sendOruR01.jsp?noteId=\${index}', 'eSend');return(false);" title="Send Electronically" style="float: right; margin-right: 5px; font-size: 10px;">eSend</a>

					<input type="image" id="anno\${index}" src="/oscarEncounter/graphics/annotation.png" title="Annotation" style="float: right; margin-right: 5px; margin-bottom: 3px; height:10px;width:10px" onclick="window.open('/annotation/annotation.jsp?atbname=anno1567018421988&amp;table_id=671920&amp;display=EChartNote&amp;demo=148','anwin','width=400,height=500');$('annotation_attribname').value='anno1567018421988'; return false;">


					<div id="wrapper\${index}" style="clear:right;">


						<div id="txt\${index}" style="display:inline-block;overflow-wrap:break-word;word-wrap:break-word;max-width:100%;">
							{{each noteLineArray}}
								\${$value}<br>
							{{/each}}
						</div>

					</div>

					<div id="sig\${index}" class="sig" style="clear:both;color:#000000;background-color:#CCCCFF;">
						<div id="sumary\${index}">
							<div id="observation\${index}" style="font-size: 11px; float: right; margin-right: 3px;">
								Encounter Date:&nbsp;
								<span id="obs\${index}">\${formattedObservationDate}</span>&nbsp;
								Rev

								<a href="#" onclick="return showHistory('\${index}', event);">2</a>

							</div>



							<div style="font-size: 11px;">
								<span style="float: left;">Editors:</span>
								<ul style="list-style: none inside none; margin: 0px;">
									<li>Host, Oscar; </li>
								</ul>
							</div>
							<div style="font-size: 11px; clear: right; margin-right: 3px; float: right;">
								Enc Type:&nbsp;
								<span id="encType\${index}">\${encounterType}</span>
							</div>


							<div style="display: block; font-size: 11px;">
								<span style="float: left;">Assigned Issues</span>

								<br style="clear: both;">
							</div>
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

		<c:set var="navbarSides" value="${fn:split('right,left', ',')}"></c:set>
		<c:forEach items="${navbarSides}" var="navbarSide">

			<%-- Show different preamble for left and right sides --%>

			<%-- Left --%>
			<c:if test="${navbarSide == 'left'}">
				<c:set var="noteSections" value="${junoEncounterForm.leftNoteSections}" />
				<div id="leftNavBar" style="display: inline; float: left; width: 20%;">
			</c:if>

			<%-- Right --%>
			<c:if test="${navbarSide == 'right'}">
				<c:set var="noteSections" value="${junoEncounterForm.rightNoteSections}" />
				<div id="rightNavBar" style="display: inline; float: right; width: 20%; margin-left: -3px;">
					<%
						String demo=request.getParameter("demographicNo");
						String roleName$ = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");

						// XXX: Do we want the sharing center?  If so put this in the action form.
						// MARC-HI's Sharing Center
						boolean isSharingCenterEnabled = SharingCenterUtil.isEnabled();

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
					<% if (isSharingCenterEnabled) { %>
					<div>
						<button type="button" onclick="window.open('${ctx}/sharingcenter/documents/demographicExport.jsp?demographic_no=<%=demo%>');">
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

					<c:set var="section" scope="page" value="${junoEncounterForm.sections[sectionName]}" />

					<div class="leftBox" id="${sectionName}" style="display: block;">

						<form style="display: none;" name="dummyForm" action="">
							<input type="hidden" id="reloadDiv" name="reloadDiv" value="none" onchange="updateDiv();">
						</form>

						<div id='menuTitle${sectionName}' style="width: 10%; float: right; text-align: center;">
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
							<c:set var="useColumns" scope="page" value="${fn:length(section.menuItems) > 40}" />
							<c:set var="menuWidth" scope="page" value="${useColumns ? '250' : '125'}" />
							<div id='menu${section.menuId}' class='menu' style='width: ${menuWidth}px' onclick='event.cancelBubble = true;'>
								<h3 style='text-align: center'><bean:message key="${section.menuHeaderKey}" />
								</h3>
								<c:forEach items="${section.menuItems}" var="menuItem" varStatus="loop">
									<a href="#" class="menuItem${useColumns && loop.index % 2 == 1 ? 'right' : 'left' }"
										  onmouseover='this.style.color="black"'
										  onmouseout='this.style.color="white"'
										  onclick="${menuItem.onClick}; return false;">
										<c:if test="${not empty menuItem.textKey}"><bean:message key="${menuItem.textKey}" /></c:if>
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

							<c:set var="section" scope="page" value="${junoEncounterForm.sections[sectionName]}" />

							<c:forEach items="${section.notes}" var="note" varStatus="loop">

								<li class="encounterNote ${loop.index % 2 == 0 ? 'encounterNoteEven' : 'encounterNoteOdd'}">

									<%-- Expand arrows if neccessary --%>
									<c:choose>
										<c:when test="${ section.remainingNotes > 0 && loop.last }">
											<a href="#" class="expandCasemgmtSidebar encounterNoteTitle" onclick="getSectionRemote('${sectionName}', true, false); return false;" title="${section.remainingNotes} more items">
												<img id="img${sectionName}5" src="graphics/expand.gif" />&nbsp;&nbsp;
											</a>
										</c:when>
										<c:otherwise>
											<a border="0" class="expandCasemgmtSidebar encounterNoteTitle">
												<img id="img${sectionName}1" src="/images/clear.gif" />&nbsp;&nbsp;
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
											<c:out value="${note.text}" />
										</a>
									</span>

									<%-- Link date --%>
									<fmt:parseDate value="${note.updateDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedUpdateDate" />
									<fmt:formatDate value="${parsedUpdateDate}" pattern="dd-MMM-yyyy" var="updateDate" />
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
												<c:out value="${note.value}" />
												<c:out value="${updateDate}" />
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
									<c:forEach items="${section.notes}" var="note" varStatus="noteLoop">
										<fmt:parseDate value="${note.updateDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedUpdateDate" />
										<fmt:formatDate value="${parsedUpdateDate}" pattern="dd-MMM-yyyy" var="updateDate" />
										<fmt:parseDate value="${note.observationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedObservationDate" />
										<fmt:formatDate value="${parsedObservationDate}" pattern="dd-MMM-yyyy" var="observationDate" />
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

				<span style="visibility:hidden">test</span>
			</div>


			<%
			// =================================================================================
			// Case Notes form and border etc.
			// =================================================================================
			%>
			<div id="notCPP" style="height: 70%; margin-left: 2px; background-color: #FFFFFF;">

				<html:form action="/CaseManagementView" method="post">
					<html:hidden property="demographicNo" value="${junoEncounterForm.header.demographicNo}" />
					<html:hidden property="providerNo" value="${junoEncounterForm.header.providerNo}" />
					<html:hidden property="tab" value="Current Issues" />
					<html:hidden property="hideActiveIssue" />
					<html:hidden property="ectWin.rowOneSize" styleId="rowOneSize" />
					<html:hidden property="ectWin.rowTwoSize" styleId="rowTwoSize" />
					<input type="hidden" name="chain" value="list" >
					<input type="hidden" name="method" value="view" >
					<input type="hidden" id="check_issue" name="check_issue">
					<%-- TODO: fix later
					<input type="hidden" id="serverDate" value="<%=strToday%>">
					--%>
					<input type="hidden" id="resetFilter" name="resetFilter" value="false">
					<div id="topContent" style="float: left; width: 100%; margin-right: -2px; padding-bottom: 1px; background-color: #CCCCFF; font-size: 10px;">
						<nested:notEmpty name="caseManagementViewForm" property="filter_providers">
							<div style="float: left; margin-left: 10px; margin-top: 0px;"><u><bean:message key="oscarEncounter.providers.title" />:</u><br>
								<nested:iterate type="String" id="filter_provider" property="filter_providers">
									<c:choose>
										<c:when test="${filter_provider == 'a'}">All</c:when>
										<c:otherwise>
											<nested:iterate id="provider" name="providers">
												<c:if test="${filter_provider==provider.providerNo}">
													<nested:write name="provider" property="formattedName" />
													<br>
												</c:if>
											</nested:iterate>
										</c:otherwise>
									</c:choose>
								</nested:iterate>
							</div>
						</nested:notEmpty>

						<nested:notEmpty name="caseManagementViewForm" property="filter_roles">
							<div style="float: left; margin-left: 10px; margin-top: 0px;"><u><bean:message key="oscarEncounter.roles.title" />:</u><br>
								<nested:iterate type="String" id="filter_role" property="filter_roles">
									<c:choose>
										<c:when test="${filter_role == 'a'}">All</c:when>
										<c:otherwise>
											<nested:iterate id="role" name="roles">
												<c:if test="${filter_role==role.id}">
													<nested:write name="role" property="name" />
													<br>
												</c:if>
											</nested:iterate>
										</c:otherwise>
									</c:choose>
								</nested:iterate>
							</div>
						</nested:notEmpty>

						<nested:notEmpty name="caseManagementViewForm" property="note_sort">
							<div style="float: left; margin-left: 10px; margin-top: 0px;"><u><bean:message key="oscarEncounter.sort.title" />:</u><br>
								<nested:write property="note_sort" /><br>
							</div>
						</nested:notEmpty>

						<nested:notEmpty name="caseManagementViewForm" property="issues">
							<div style="float: left; margin-left: 10px; margin-top: 0px;"><u><bean:message key="oscarEncounter.issues.title" />:</u><br>
								<nested:iterate type="String" id="filter_issue" property="issues">
									<c:choose>
										<c:when test="${filter_issue == 'a'}">All</c:when>
										<c:otherwise>
											<nested:iterate id="issue" name="cme_issues">
												<c:if test="${filter_issue==issue.issue.id}">
													<nested:write name="issue" property="issueDisplay.description" />
													<br>
												</c:if>
											</nested:iterate>
										</c:otherwise>
									</c:choose>
								</nested:iterate>
							</div>
						</nested:notEmpty>
						<div id="filter" style="display:none;background-color:#ddddff;padding:8px">
							<input type="button" value="<bean:message key="oscarEncounter.showView.title" />" onclick="return filter(false);" />
							<input type="button" value="<bean:message key="oscarEncounter.resetFilter.title" />" onclick="return filter(true);" />

							<table style="border-collapse:collapse;width:100%;margin-left:auto;margin-right:auto">
								<tr>
									<td style="font-size:inherit;background-color:#bbbbff;font-weight:bold">
										<bean:message key="oscarEncounter.providers.title" />
									</td>
									<td style="font-size:inherit;background-color:#bbbbff;border-left:solid #ddddff 4px;border-right:solid #ddddff 4px;font-weight:bold">
										Role
									</td>
									<td style="font-size:inherit;background-color:#bbbbff;font-weight:bold">
										<bean:message key="oscarEncounter.sort.title" />
									</td>
									<td style="font-size:inherit;background-color:#bbbbff;font-weight:bold">
										<bean:message key="oscarEncounter.issues.title" />
									</td>
								</tr>
								<tr>
									<td style="font-size:inherit;background-color:#ccccff">
										<div style="height:150px;overflow:auto">
											<ul style="padding:0px;margin:0px;list-style:none inside none">
												<li><html:multibox property="filter_providers" value="a" onclick="filterCheckBox(this)"></html:multibox><bean:message key="oscarEncounter.sortAll.title" /></li>
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
												<li><html:multibox property="filter_roles" value="a" onclick="filterCheckBox(this)"></html:multibox><bean:message key="oscarEncounter.sortAll.title" /></li>
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
												<li><html:radio property="note_sort" value="observation_date_asc">
													<bean:message key="oscarEncounter.sortDateAsc.title" />
												</html:radio></li>
												<li><html:radio property="note_sort" value="observation_date_desc">
													<bean:message key="oscarEncounter.sortDateDesc.title" />
												</html:radio></li>
												<li><html:radio property="note_sort" value="providerName">
													<bean:message key="oscarEncounter.provider.title" />
												</html:radio></li>
												<li><html:radio property="note_sort" value="programName">
													<bean:message key="oscarEncounter.program.title" />
												</html:radio></li>
												<li><html:radio property="note_sort" value="roleName">
													<bean:message key="oscarEncounter.role.title" />
												</html:radio></li>
											</ul>
										</div>
									</td>
									<td style="font-size:inherit;background-color:#ccccff;border-left:solid #ddddff 4px;border-right:solid #ddddff 4px">
										<div style="height:150px;overflow:auto">
											<ul style="padding:0px;margin:0px;list-style:none inside none">
												<li><html:multibox property="issues" value="a" onclick="filterCheckBox(this)"></html:multibox><bean:message key="oscarEncounter.sortAll.title" /></li>
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
								<img alt="<bean:message key="oscarEncounter.msgFind"/>" src="<c:out value="${ctx}/oscarEncounter/graphics/edit-find.png"/>">
								<input id="enTemplate" tabindex="6" size="16" type="text" value="" onkeypress="return grabEnterGetTemplate(event)">

								<div class="enTemplate_name_auto_complete" id="enTemplate_list" style="z-index: 1; display: none">&nbsp;</div>

								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;




								<input type="text" id="keyword" name="keyword" value="" onkeypress="return grabEnter('searchButton',event)">
								<input type="button" id="searchButton" name="button" value="<bean:message key="oscarEncounter.Index.btnSearch"/>" onClick="popupPage(600,800,'<bean:message key="oscarEncounter.Index.popupSearchPageWindow"/>',$('channel').options[$('channel').selectedIndex].value+urlencode($F('keyword')) ); return false;">

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
										<option value="http://www.google.com/search?q="><bean:message key="global.google" /></option>
										<option value="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?SUBMIT=y&amp;CDM=Search&amp;DB=PubMed&amp;term="><bean:message key="global.pubmed" /></option>
										<option value="http://search.nlm.nih.gov/medlineplus/query?DISAMBIGUATION=true&amp;FUNCTION=search&amp;SERVER2=server2&amp;SERVER1=server1&amp;PARAMETER="><bean:message key="global.medlineplus" /></option>
										<option value="tripsearch.jsp?searchterm=">Trip Database</option>
										<option value="macplussearch.jsp?searchterm=">MacPlus Database</option>
									</select>
								</div>

							</div>
							&nbsp;&nbsp;
							<div style="display:inline-block;text-align: left;" id="toolbar">
								<input type="button" value="<bean:message key="oscarEncounter.Filter.title"/>" onclick="showFilter();" />
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
					String oscarMsgType = (String)request.getParameter("msgType");
					String OscarMsgTypeLink = (String)request.getParameter("OscarMsgTypeLink");
				%>
				<nested:form action="/CaseManagementEntry" style="display:inline; margin-top:0; margin-bottom:0; position: relative;">
					<%--
					// TODO: make this work (parts missing)
					<html:hidden property="demographicNo" value="<%=demographicNo%>" />
					--%>
					<html:hidden property="includeIssue" value="off" />
					<input type="hidden" name="OscarMsgType" value="<%=oscarMsgType%>"/>
					<input type="hidden" name="OscarMsgTypeLink" value="<%=OscarMsgTypeLink%>"/>
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

					<html:hidden property="appointmentNo" value="<%=apptNo%>" />
					<html:hidden property="appointmentDate" value="<%=apptDate%>" />
					<html:hidden property="start_time" value="<%=startTime%>" />
					<html:hidden property="billRegion" value="<%=(OscarProperties.getInstance().getBillingType()).trim().toUpperCase()%>" />
					<html:hidden property="apptProvider" value="<%=apptProv%>" />
					<html:hidden property="providerview" value="<%=provView%>" />
					<input type="hidden" name="toBill" id="toBill" value="false">
					<input type="hidden" name="deleteId" value="0">
					<input type="hidden" name="lineId" value="0">
					<input type="hidden" name="from" value="casemgmt">
					<input type="hidden" name="method" value="save">
					<input type="hidden" name="change_diagnosis" value="<c:out value="${change_diagnosis}"/>">
					<input type="hidden" name="change_diagnosis_id" value="<c:out value="${change_diagnosis_id}"/>">
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
					<input type="hidden" name="notes2print" id="notes2print" value="">
					<input type="hidden" name="printCPP" id="printCPP" value="false">
					<input type="hidden" name="printRx" id="printRx" value="false">
					<input type="hidden" name="printLabs" id="printLabs" value="false">
					<input type="hidden" name="encType" id="encType" value="">
					<input type="hidden" name="pStartDate" id="pStartDate" value="">
					<input type="hidden" name="pEndDate" id="pEndDate" value="">
					<input type="hidden" id="annotation_attribname" name="annotation_attribname" value="">
					<%
						if (OscarProperties.getInstance().getBooleanProperty("note_program_ui_enabled", "true")) {
					%>
					<input type="hidden" name="_note_program_no" value="" />
					<input type="hidden" name="_note_role_id" value="" />
					<% } %>

					<span id="notesLoading">
						<img src="<c:out value="${ctx}/images/DMSLoader.gif" />">Loading Notes...
					</span>

					<div id="mainContent" style="background-color: #FFFFFF; width: 100%; margin-right: -2px; display: inline; float: left;">
						<div id="issueList" style="background-color: #FFFFFF; height: 440px; width: 350px; position: absolute; z-index: 1; display: none; overflow: auto;">
							<table id="issueTable" class="enTemplate_name_auto_complete" style="position: relative; left: 0px; display: none;">
								<tr>
									<td style="height: 430px; vertical-align: bottom;">
										<div class="enTemplate_name_auto_complete" id="issueAutocompleteList" style="position: relative; left: 0px; display: none;"></div>
									</td>
								</tr>
							</table>
						</div>
						<div id="encMainDiv" class="encMainDiv">

						</div>
						<script type="text/javascript">

							if (parseInt(navigator.appVersion)>3) {
								var windowHeight=750;
								if (navigator.appName=="Netscape") {
									windowHeight = window.innerHeight;
								}
								if (navigator.appName.indexOf("Microsoft")!=-1) {
									windowHeight = document.body.offsetHeight;
								}

								// XXX: this seems like it generally won't work (ocean, etc.)
								var divHeight = windowHeight - 320;
								$("encMainDiv").style.height = divHeight+'px';
							}
						</script>
						<div id='save' style="width: 99%; background-color: #CCCCFF; padding-top: 5px; margin-left: 2px; border-left: thin solid #000000; border-right: thin solid #000000; border-bottom: thin solid #000000;">
		<span style="float: right; margin-right: 5px;">

		<div class="encounter_timer_container">
			<div style="display: inline-block; position:relative;">
				<input id="encounter_timer" title="Paste timer data" type="button" onclick="encounterTimer.putEncounterTimeInNote()" value="00:00"/>
			</div>
			<input id="encounter_timer_pause" class="encounter_timer_control" type="button" onclick="encounterTimer.toggleEncounterTimer('#encounter_timer_pause', '#encounter_timer_play')" value="||"/>
			<input id="encounter_timer_play" class="encounter_timer_control" type="button" onclick="encounterTimer.toggleEncounterTimer('#encounter_timer_pause', '#encounter_timer_play')" value="&gt;"/>
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
			<input tabindex="17" type='image' src="<c:out value="${ctx}/oscarEncounter/graphics/media-floppy.png"/>" id="saveImg" onclick="Event.stop(event);return saveNoteAjax('save', 'list');" title='<bean:message key="oscarEncounter.Index.btnSave"/>'>&nbsp;
			<input tabindex="18" type='image' src="<c:out value="${ctx}/oscarEncounter/graphics/document-new.png"/>" id="newNoteImg" onclick="newNote(event); return false;" title='<bean:message key="oscarEncounter.Index.btnNew"/>'>&nbsp;
			<input tabindex="19" type='image' src="<c:out value="${ctx}/oscarEncounter/graphics/note-save.png"/>" id="signSaveImg" onclick="document.forms['caseManagementEntryForm'].sign.value='on';Event.stop(event);return savePage('saveAndExit', '');" title='<bean:message key="oscarEncounter.Index.btnSignSave"/>'>&nbsp;
			<input tabindex="20" type='image' src="<c:out value="${ctx}/oscarEncounter/graphics/verify-sign.png"/>" id="signVerifyImg" onclick="document.forms['caseManagementEntryForm'].sign.value='on';document.forms['caseManagementEntryForm'].verify.value='on';Event.stop(event);return savePage('saveAndExit', '');" title='<bean:message key="oscarEncounter.Index.btnSign"/>'>&nbsp;
			<%--
			// TODO: make this work (parts missing)
			<%
				if(bean.source == null)  {
			%>
					<input tabindex="21" type='image' src="<c:out value="${ctx}/oscarEncounter/graphics/dollar-sign-icon.png"/>" onclick="signSaveBill(event);" title='<bean:message key="oscarEncounter.Index.btnBill"/>'>&nbsp;
				<%
					}
				%>
		--%>



	    	<input tabindex="23" type='image' src="<c:out value="${ctx}/oscarEncounter/graphics/system-log-out.png"/>" onclick='closeEnc(event);return false;' title='<bean:message key="global.btnExit"/>'>&nbsp;
	    	<input tabindex="24" type='image' src="<c:out value="${ctx}/oscarEncounter/graphics/document-print.png"/>" onclick="return printSetup(event);" title='<bean:message key="oscarEncounter.Index.btnPrint"/>' id="imgPrintEncounter">
    	</span>
							<div id="assignIssueSection">
								<!-- input type='image' id='toggleIssue' onclick="return showIssues(event);" src="<c:out value="${ctx}/oscarEncounter/graphics/issues.png"/>" title='<bean:message key="oscarEncounter.Index.btnDisplayIssues"/>'>&nbsp; -->
								<input tabindex="8" type="text" id="issueAutocomplete" name="issueSearch" style="z-index: 2;" onkeypress="return submitIssue(event);" size="30">&nbsp; <input tabindex="9" type="button" id="asgnIssues" value="<bean:message key="oscarEncounter.assign.title"/>">
								<span id="busy" style="display: none">
	    		<img style="position: absolute;" src="<c:out value="${ctx}/oscarEncounter/graphics/busy.gif"/>" alt="<bean:message key="oscarEncounter.Index.btnWorking" />">
	    	</span>
							</div>
							<div style="padding-top: 3px;">
								<button type="button" onclick="return showHideIssues(event, 'noteIssues-resolved');"><bean:message key="oscarEncounter.Index.btnDisplayResolvedIssues"/></button> &nbsp;
								<button type="button" onclick="return showHideIssues(event, 'noteIssues-unresolved');"><bean:message key="oscarEncounter.Index.btnDisplayUnresolvedIssues"/></button> &nbsp;
								<button type="button" onclick="javascript:spellCheck();">Spell Check</button> &nbsp;
								<button type="button" onclick="javascript:toggleFullViewForAll(this.form);"><bean:message key="eFormGenerator.expandAll"/> <bean:message key="Appointment.formNotes"/></button>
								<button type="button" onclick="javascript:popupPage(500,200,'noteBrowser<%//TODO: Fix; bean.demographicNo%>','noteBrowser.jsp?demographic_no=<% //TODO: fix; bean.demographicNo%>&FirstTime=1');"><bean:message key="oscarEncounter.Index.BrowseNotes"/></button> &nbsp;
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
				<input type="hidden" id="noteUuid" name="noteUuid" value="" />
				<input type="hidden" id="noteSummaryCode" name="noteSummaryCode" value="" />
				<input type="hidden" id="noteEditId" name="noteEditId" value="" />
				<input type="hidden" id="noteRevision" name="noteRevision" value="" />
				<input type="hidden" id="issueChange" name="issueChange" value="" />
				<input type="hidden" id="archived" name="archived" value="false" />
				<input type="hidden" id="annotation_attrib" name="annotation_attrib" />
				<div id="winTitle"></div>
				<div id="editNoteError" class="error editNoteError"></div>
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
						   onclick="this.focus();hideEdit();return false;">
						   <%--onclick="this.focus();$('channel').style.visibility ='visible';$('showEditNote').style.display='none';return false;">--%>
				</span>
				<bean:message key="oscarEncounter.Index.btnPosition" />
				<select id="position" name="position">
					<option id="popt0" value="1">1</option>
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
