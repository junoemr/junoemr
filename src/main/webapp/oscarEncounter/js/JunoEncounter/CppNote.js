'use strict';

if (!window.Juno) window.Juno = {};
if (!Juno.OscarEncounter) Juno.OscarEncounter = {};
if (!Juno.OscarEncounter.JunoEncounter) Juno.OscarEncounter.JunoEncounter = {};

if (!Juno.OscarEncounter.JunoEncounter.CppNote) Juno.OscarEncounter.JunoEncounter.CppNote = function CppNote(pageData, junoEncounter)
{
	this.pageData = pageData;
	this.junoEncounter = junoEncounter;

	var cppIssues = new Array(7);
	cppIssues[0] = "SocHistory";
	cppIssues[1] = "MedHistory";
	cppIssues[2] = "FamHistory";
	cppIssues[3] = "Concerns";
	cppIssues[4] = "RiskFactors";
	cppIssues[5] = "Reminders";
	cppIssues[6] = "OMeds";

	var cppNames = new Array(7);
	cppNames[0] = "Social History";
	cppNames[1] = "Medical History";
	cppNames[2] = "Family History";
	cppNames[3] = "Ongoing Concerns";
	cppNames[4] = "Risk Factors";
	cppNames[5] = "Reminders";
	cppNames[6] = "Other Meds";

	var exFields = new Array(11);
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

	var exKeys = new Array(11);
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

	this.getFormData = function getFormData($form)
	{
		var unindexed_array = $form.serializeArray();
		var indexed_array = {};

		junoJQuery.map(unindexed_array, function (n, i)
		{
			indexed_array[n['name']] = n['value'];
		});

		return indexed_array;
	};

	this.getCPPObjectFromForm = function getCPPObjectFromForm(form, issueIdArray)
	{
		var deferred = junoJQuery.Deferred();

		var noteId = 0;
		if (form.noteEditId)
		{
			noteId = form.noteEditId;
		}

		var me = this;
		this.junoEncounter.getAssignedIssueArray(issueIdArray, true).then(function (assignedIssueArray)
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
					"startDate": form.startdate,
					"resolutionDate": form.resolutiondate,
					"procedureDate": form.proceduredate,
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
	};

	this.updateCPPNote = function updateCPPNote()
	{
		var demographicNo = this.pageData.demographicNo;
		var form = junoJQuery('#frmIssueNotes');
		var formData = this.getFormData(form);

		var issueIdArray = [];
		junoJQuery("#issueIdList input:checkbox[name=issue_id]:checked").each(function ()
		{
			issueIdArray.push(junoJQuery(this).val());
		});

		var me = this;
		this.getCPPObjectFromForm(formData, issueIdArray).then(function (restData)
		{
			var jsonString = JSON.stringify(restData);

			junoJQuery.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: me.getSaveCPPNoteUrl(demographicNo),
				data: jsonString,
				success: function (response)
				{
					if (response.status === "SUCCESS")
					{
						// Close window
						me.hideEdit();

						// Reload note list
						me.junoEncounter.getSectionRemote(formData.noteSummaryCode, true, true);

						return false;
					} else
					{
						// Show error
						junoJQuery('#editNoteError').html(response.error.message);
					}
				}
			});
		});

		return false;
	};

	this.hideEdit = function hideEdit()
	{
		$('showEditNote').style.display = 'none';
	};

	this.showEdit = function showEdit(e, summaryCode, title, numNotes, noteIssues, demoNo, noteJsonString)
	{
		// Gather data
		var note = JSON.parse(noteJsonString);

		if (note != null)
		{
			var me = this;
			junoJQuery.ajax({
				type: "GET",
				contentType: "application/json",
				dataType: "json",
				url: "../ws/rs/notes/getIssueNote/" + note.noteId,
				success: function (response)
				{
					return me.showEditBox(e, summaryCode, title, numNotes, response.body.assignedCMIssues, demoNo, note)
				}
			});
		}
		else
		{
			return this.showEditBox(e, summaryCode, title, numNotes, [], demoNo, null)
		}

		return false;
	};

	this.showEditBox = function showEditBox(e, summaryCode, title, numNotes_remove, assignedCMIssues, demoNo, note)
	{
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
				startdate: this.junoEncounter.getFormattedDate(note.extStartDate),
				resolutiondate: this.junoEncounter.getFormattedDate(note.extResolutionDate),
				proceduredate: this.junoEncounter.getFormattedDate(note.extProcedureDate),
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
			position = 0;
			revision = "";
			uuid = "";
			extraFields = {};
		}


		var editElement = "showEditNote";

		// Clear Errors
		junoJQuery('#editNoteError').html("");


		// Set hidden data values

		$("noteUuid").value = uuid;
		$("noteEditTxt").value = noteText;
		$("noteSummaryCode").value = summaryCode;
		$("noteEditId").value = noteId;
		$("noteRevision").value = revision;


		// Set editors
		// XXX: Make this work better for new notes
		var editorUl = "<ul style='list-style: none outside none; margin:0;'>";
		var editorSpan = "";

		if (editors != null && editors.length > 0)
		{
			editorSpan = "<span style='float:left;'>Editors: </span>";

			var editorArray = editors.split(";");
			var idx;
			for (idx = 0; idx < editorArray.length; ++idx)
			{
				if (idx % 2 === 0)
				{
					editorUl += "<li>" + editorArray[idx];
				}
				else
				{
					editorUl += "; " + editorArray[idx] + "</li>";
				}
			}

			if (idx % 2 === 0)
			{
				editorUl += "</li>";
			}
		}
		editorUl += "</ul>";


		// Set issue list
		var noteIssueUl = "<ul id='issueIdList' style='list-style: none outside none; margin:0;'>";

		if (Array.isArray(assignedCMIssues) && assignedCMIssues.length > 0)
		{
			var idx;
			for(idx = 0; idx < assignedCMIssues.length; idx++)
			{
				var assignedIssue = assignedCMIssues[idx];

				if (idx % 2 === 0)
				{
					noteIssueUl += "<li>";
				}
				else
				{
					noteIssueUl += "&nbsp;";
				}

				noteIssueUl += "<input type='checkbox' id='issueId' name='issue_id' checked value='" + assignedIssue.issue_id + "' \>" + assignedIssue.issue.description;
			}

			if (idx % 2 === 0)
			{
				noteIssueUl += "</li>";
			}
		}
		noteIssueUl += "</ul>";

		var noteInfo = "<div style='float:right;'>"
				+ "<i>Encounter Date:&nbsp;" + date + "&nbsp;rev"
				+ "<a href='#' onclick='return junoEncounter.showHistory(\"" + noteId + "\",event);'>" + revision + "</a>"
				+ "</i>"
				+ "</div>"
				+ editorSpan
				+ "<div>" + editorUl + noteIssueUl + "</div>"
				+ "<br style='clear:both;'>";

		$("issueNoteInfo").update(noteInfo);


		//Prepare Annotation Window & Extra Fields
		var now = new Date();
		document.getElementById('annotation_attrib').value = "anno" + now.getTime();
		//var obj = {};
		Element.observe('anno', 'click', this.junoEncounter.openAnnotation.bindAsEventListener(this, noteId, "issue", demoNo));

		this.prepareExtraFields(title, extraFields);


		// Build position dropdown

		// Remove any existing options
		junoJQuery("#position").children("option").remove();

		// Add options for this list
		var numNotes = junoJQuery("#" + summaryCode + "list li.cpp").length;
		for(var j = 1; j <= numNotes + 1; j++)
		{
			var optId = "popt" + j;
			if ($(optId) == null)
			{
				var opttxt = j;

				var option = document.createElement("OPTION");
				option.id = optId;
				option.text = "" + opttxt;
				option.value = j;
				junoJQuery("#position").append(option);
				if(position === j)
				{
					option.selected = "selected";
				}
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
	};

	this.prepareExtraFields = function prepareExtraFields(cpp, extObj)
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
			}
		}
	};

	this.getSaveCPPNoteUrl = function getSaveCPPNoteUrl(demographicNo)
	{
		return "../ws/rs/notes/" + demographicNo + "/saveIssueNote";
	};

	this.showIssueHistory = function showIssueHistory(demoNo, issueIds)
	{
		var rnd = Math.round(Math.random() * 1000);
		win = "win" + rnd;

		var url = this.pageData.contextPath + "/CaseManagementEntry.do" +
			"?method=issuehistory" +
			"&demographicNo=" + encodeURIComponent(this.pageData.demographicNo) +
			"&issueIds=" + issueIds;

		window.open(url, win, "scrollbars=yes, location=no, width=647, height=600", "");

		return false;
	};
};

