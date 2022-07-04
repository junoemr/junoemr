'use strict';

if (!window.Juno) window.Juno = {};
if (!Juno.OscarEncounter) Juno.OscarEncounter = {};
if (!Juno.OscarEncounter.JunoEncounter) Juno.OscarEncounter.JunoEncounter = {};

if (!Juno.OscarEncounter.JunoEncounter.EncounterNote) Juno.OscarEncounter.JunoEncounter.EncounterNote =
	function EncounterNote(pageData, pageState, junoEncounter)
{
	this.pageData = pageData;
	this.pageState = pageState;
	this.junoEncounter = junoEncounter;

	var AUTO_SAVE_DELAY = 5000;

	var numChars = 0;
	var lastTmpSaveNote = null;
	var autoSaveTimer = null;
	var notesRetrieveOk = false;

	this.loadNotes = function loadNotes()
	{
		var demographicNo = this.pageData.demographicNo;

		this.pageState.notesOffset = 0;

		// Load a few extra notes initially, hopefully fill up the page
		var me = this;
		this.notesLoader(
			this.pageData.contextPath,
			0,
			this.pageData.notesIncrement * 2,
			demographicNo,
			true
		).then(function ()
		{
			me.pageState.notesOffset += (pageData.notesIncrement * 2);
			me.pageState.notesScrollCheckInterval = setInterval(function ()
			{
				me.notesIncrementAndLoadMore(demographicNo)
			}, 50);
		});
	};

	this.pasteToEncounterNote = function pasteToEncounterNote(txt)
	{
		var currentlyEditedNoteId = junoJQuery('input#editNoteId').val();
		var currentTextAreaId = "caseNote_note" + currentlyEditedNoteId;

		$(currentTextAreaId).value += "\n" + txt;
		this.adjustCaseNote();
		this.setCaretPosition($(currentTextAreaId), $(currentTextAreaId).value.length);

	};

	this.putEncounterTimeInNote = function putEncounterTimeInNote()
	{
		var timer = jQuery("#encounter_timer");
		if (timer.length)
		{
			let endTime = new Date();
			let timeStr = timer.val();

			if (timeStr.split(":").length < 3)
			{
				timeStr = "00:" + timeStr;
			}

			var noteTxt = "Start time: " + encounterTimer.startTime.getHours()
				+ ":" + ("0" + encounterTimer.startTime.getMinutes()).slice(-2)
				+ "\n" +
				"End time: " + endTime.getHours() + ":" + ("0"
					+ endTime.getMinutes()).slice(-2) + "\n" +
				"Duration: " + timeStr + "\n";

			this.pasteToEncounterNote(noteTxt);
		}
	};

	this.updateNoteInPageState = function updateNoteInPageState(noteData, assignedIssueArray)
	{
		pageState.currentNoteData = junoJQuery.extend(true, {}, noteData);
		pageState.currentAssignedCMIssues = junoJQuery.extend(true, [], assignedIssueArray);
	};

	this.createNewNote = function createNewNote()
	{
		var currentNoteId = junoJQuery("input#editNoteId").val();
		var noteData = this.getNoteDataById(currentNoteId);

		if(parseInt(noteData.noteId) === 0 && noteData.note.trim() === pageState.currentNoteData.note.trim())
		{
			// do nothing, this is already an new, empty note
			return false;
		}

		return this.editEncounterNote(null, 0);
	}

	this.editEncounterNote = function editEncounterNote(event, noteId)
	{
		var currentNoteId = junoJQuery("input#editNoteId").val();
		var noteData = this.getNoteDataById(currentNoteId);

		this.unobserveTextArea();

		if(noteData.note.trim() !== pageState.currentNoteData.note.trim())
		{
			if(confirm(this.pageData.unsavedNoteWarningMsg))
			{
				// Save note and refresh note list
				var me = this;
				this.saveEncounterNote(
					false,
					false,
					false,
					false,
					false
				).then(
					function(response)
					{
						// Set the current note data from the results of the saved note
						pageState.currentNoteData = me.buildNote(
							response.noteId,
							response.uuid,
							response.observationDate,
							response.providerNo,
							response.encounterType,
							false,
							false,
							response.appointmentNo,
							response.note
						)
					},
					function(response)
					{
						console.log("Error saving encounter note when changing edited note.");
					}
				);
			}
			else
			{
				return;
			}
		}


		// Show a warning and offer to save the note if
		var demographicNo = this.pageData.demographicNo;

		if(noteId === 0)
		{
		    // If there is a note with noteId 0, just create and select it, it's not a real note yet
			var emptyNote = this.getEmptyNote(this.pageData.providerNo, this.pageData.appointmentNo);
			var blankIssues = [];
			var noteDiv = junoJQuery('div#n' + noteId);
			if(!noteDiv.length)
			{
				var containerDiv = junoJQuery('div#encMainDiv');
				var newNoteNode = this.appendNoteEntry(containerDiv, noteId, emptyNote, blankIssues, demographicNo, false);
				newNoteNode[0].scrollIntoView();
			}
			this.enableEditMode(noteId, demographicNo, emptyNote, blankIssues);
		}
		else
		{
			var me = this;

			junoJQuery.ajax({
				type: "GET",
				contentType: "application/json",
				dataType: "json",
				url: "../ws/rs/notes/" + demographicNo + "/getNoteToEdit/" + noteId,
				success: function (result)
				{
					var note = me.getEmptyNote(me.pageData.providerNo, me.pageData.appointmentNo);
					var issues = [];

					if(result.body !== null)
					{
						note = result.body.encounterNote;
						issues = result.body.assignedCMIssues;


						// Show a warning if an unsigned note was created by a different provider
						var editWarn = (!note.isSigned && note.providerNo !== me.pageData.providerNo);

						if (editWarn && !confirm(pageData.editUnsignedMsg))
						{
							return false;
						}
					}

					me.enableEditMode(noteId, demographicNo, note, issues);
				}
			});
		}

		return false;
	};

	this.enableEditMode = function enableEditMode(noteId, demographicNo, note, issues)
	{
		// Disable any notes currently being edited
		var currentlyEditedNoteId = junoJQuery('input#editNoteId').val();
		var currentlyEditedNoteDiv = junoJQuery('div#n' + currentlyEditedNoteId).parent();


		this.replaceNoteEntry(currentlyEditedNoteDiv, pageState.currentNoteData, null, demographicNo, false);

		// Make the note editable
		var noteDiv = junoJQuery('div#n' + noteId).parent();

		this.replaceNoteEntry(noteDiv, note, issues, demographicNo, true);
		this.updateNoteInPageState(note, issues);

		this.adjustCaseNote();
		this.observeTextArea();
		this.focusTextArea();
		this.setSaveButtonVisibility();
	};

	this.observeTextArea = function observeTextArea()
	{
		var textAreaName = this.getEditTextAreaName();

		if(textAreaName != null && $(textAreaName) != undefined)
		{
			Element.observe(textAreaName, 'keyup', this.adjustCaseNote);
		}
	};

	this.unobserveTextArea = function unobserveTextArea()
	{
		var textAreaName = this.getEditTextAreaName();

		if(textAreaName != null && $(textAreaName) != undefined)
		{
			Element.stopObserving(textAreaName, 'keyup', this.adjustCaseNote);
		}
	};

	this.focusTextArea = function focusTextArea()
	{
		var textAreaName = this.getEditTextAreaName();

		if(textAreaName != null && $(textAreaName) != undefined)
		{
			this.setCaretPosition($(textAreaName), $(textAreaName).value.length);
		}
	};

	this.prependNoteEntry = function prependNoteEntry(containerDiv, index, note, issues, demographicNo, enableEdit, tmpSave)
	{
		var newNode = this.buildNoteEntry(index, note, issues, demographicNo, enableEdit, tmpSave);
		var returnNode = newNode.prependTo(containerDiv);

		if(enableEdit)
		{
			this.enableCalendar(note.noteId);
		}

		return returnNode;
	};

	this.appendNoteEntry = function appendNoteEntry(containerDiv, index, note, issues, demographicNo, enableEdit, tmpSave)
	{
		var newNode = this.buildNoteEntry(index, note, issues, demographicNo, enableEdit, tmpSave);
		var returnNode = newNode.appendTo(containerDiv);

		if(enableEdit)
		{
			this.enableCalendar(note.noteId);
		}

		return returnNode;
	};

	this.replaceNoteEntry = function replaceNoteEntry(nodeToReplace, note, issues, demographicNo, enableEdit)
	{
		// Make sure the node being replaced has the right id format
		var elementId = nodeToReplace.attr('id');
		if (/^n\d*$/.test(elementId))
		{
			return null;
		}

		var index = elementId.substring(2);
		var newNode = this.buildNoteEntry(index, note, issues, demographicNo, enableEdit);
		var returnNode = nodeToReplace.replaceWith(newNode);

		if(enableEdit)
		{
			this.enableCalendar(note.noteId);
		}

		return returnNode;
	};

	//resize case note text area to contain all text
	this.adjustCaseNote = function adjustCaseNote()
	{
		var caseNote = 'caseNote_note' + pageState.currentNoteData.noteId;

		if($(caseNote) == null)
		{
			return;
		}

		var MAXCHARS = 78;
		var payload = $(caseNote).value;
		var numLines = 0;
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

		numChars = $(caseNote).value.length;
	};

	this.isEncounterNote = function isEncounterNote(note)
	{
		return !(
			note.document ||
			note.eformData ||
			note.encounterForm ||
			note.invoice ||
			(note.cpp && !note.ticklerNote)
		);
	};

	this.getNoteColour = function getNoteColour(note)
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

		return '#CCCCFF';
	};

	this.getEncounterSectionUrl = function getEncounterSectionUrl(sectionName, demographicNo, appointmentNo, limit, offset, eChartUUID)
	{
		var limitString = "";
		var offsetString = "";
		var echartUuidString = "";

		if (limit !== null)
		{
			limitString = "&limit=" + limit;
		}

		if (offset !== null)
		{
			offsetString = "&offset=" + offset;
		}

		if(eChartUUID !== null)
		{
			echartUuidString = '&eChartUUID=' + eChartUUID;
		}

		return "../ws/rs/encounterSections/" + demographicNo + "/section/" + sectionName + "/?appointmentNo=" +
			appointmentNo + limitString + offsetString + echartUuidString;
	};

	this.displayNotes = function displayNotes(demographicNo, noteArray, noteToEdit, tmpSave, issues, scrollToBottom, offset)
	{
		var containerDiv = junoJQuery('div#encMainDiv');

		var noteToEditUuid = null;
		if (noteToEdit != null)
		{
			noteToEditUuid = noteToEdit.uuid;
		}

		var firstNoteNode = null;
		var foundNoteToEdit = false;
		var me = this;
		junoJQuery.each(noteArray, function (index, note)
		{
			var noteNode = null;

			if (me.isEncounterNote(note))
			{
				var noteData = note;
				var noteIssues = [];
				var editThisNote = false;
				if (offset === 0 && note.uuid === noteToEditUuid)
				{
					foundNoteToEdit = true;
					editThisNote = true;
					noteData = noteToEdit;
					noteIssues = issues;
				}

				if(tmpSave && tmpSave.noteId === note.noteId)
				{
					foundNoteToEdit = true;
					editThisNote = true;
				}

				noteNode = me.prependNoteEntry(containerDiv, index + offset + 1, noteData, noteIssues, demographicNo, editThisNote, tmpSave);
			}
			else
			{
				noteNode = me.prependNonNoteEntry(containerDiv, index + offset, note, demographicNo);
			}

			if (firstNoteNode === null)
			{
				firstNoteNode = noteNode;
			}
		});

		// If this is the first page, possibly show an extra note to edit
		if (offset === 0)
		{
			if (!foundNoteToEdit)
			{
				// Add an extra, editable note to the end
				var noteNode = this.appendNoteEntry(containerDiv, noteToEdit.noteId, noteToEdit, issues, demographicNo, true, tmpSave);
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
		else if(firstNoteNode !== null)
		{
			firstNoteNode[0].scrollIntoView();
		}
	};


	this.prependNonNoteEntry = function prependNonNoteEntry(containerDiv, index, note, demographicNo)
	{
		var appointmentNo = this.pageData.appointmentNo;

		var date = moment(note.observationDate);

		var winName = "junoEncounterFormWindow";


		// Eform link
		var onClickString = "";
		if (note.eformData)
		{
			onClickString = "popupPage(700,800,'" + winName + "','" + this.pageData.contextPath + "/eform/efmshowform_data.jsp" +
				"?appointment=" + encodeURIComponent(appointmentNo) +
				"&amp;fdid=" + encodeURIComponent(note.eformDataId) + "');";
		} else if (note.encounterForm)
		{

			var url = "../form/forwardshortcutname.jsp" +
				"?formname=" + encodeURIComponent(note.note || "") +
				"&demographic_no=" + encodeURIComponent(demographicNo || "") +
				"&appointmentNo=" + encodeURIComponent(appointmentNo || "") +
				"&formId=" + encodeURIComponent(note.noteId || "");

			onClickString = "popupPage(700,800,'" + winName + "','" + url + "');";
		}

		// Document link
		var documentWinName = "docs" + demographicNo;
		var documentStatus = note.documentStatus;
		if (documentStatus === 'A')
		{
			documentStatus = "active";
		}
		var documentUrl = this.pageData.contextPath + "/dms/documentGetFile.jsp" +
			"?document=" + encodeURIComponent(note.documentFilename) +
			"&type=" + encodeURIComponent(documentStatus) +
			"&doc_no=" + encodeURIComponent(note.documentId);
		var documentOnClickString = "popupPage(700,800,'" + documentWinName + "', '" + documentUrl + "'); return false;";


		// Prescription link
		var prescriptionWinName = "rx" + demographicNo;
		var regionalIdentifier = "";
		if(note.regionalIdentifier !== null)
		{
			regionalIdentifier = note.regionalIdentifier;
		}
		var customName = "";
		if(note.customName !== null)
		{
			customName = note.customName;
		}
		var prescriptionUrl = this.pageData.contextPath + "/oscarRx/StaticScript2.jsp" +
			"?demographicNo=" + encodeURIComponent(demographicNo) +
			"&regionalIdentifier=" + encodeURIComponent(regionalIdentifier) +
			"&cn=" + encodeURIComponent(customName);
		var prescriptionOnClickString = "popupPage(700,800,'" + prescriptionWinName + "', '" + prescriptionUrl + "'); return false;";


		// Template fields
		var templateParameters = {
			index: index,
			note: note,
			colour: this.getNoteColour(note),
			noteLineArray: note.note.split("\n"),
			formattedObservationDate: date.format('DD-MMM-YYYY H:mm'),
			onClickString: onClickString,
			documentOnClickString: documentOnClickString,
			prescriptionOnClickString: prescriptionOnClickString
		};


		var newNode = junoJQuery('#encounterNonNoteTemplate').tmpl(templateParameters);

		return newNode.prependTo(containerDiv);
	};


	this.buildNoteEntry = function buildNoteEntry(index, note, issues, demographicNo, enableEdit, tmpSave)
	{
	  if(enableEdit)
		{
			this.updateNoteInPageState(note, issues);
		}

	  if(tmpSave && tmpSave.noteId === note.noteId)
		{
			note.note = tmpSave.note
		}

		var date = moment(note.observationDate);
		var formattedDate = "";
		var formattedDateTime = "";
		if (date.isValid())
		{
			formattedDate = date.format('DD-MMM-YYYY');
			formattedDateTime = date.format('DD-MMM-YYYY H:mm');
		}
		var hideBeforeMoment = moment(this.pageData.encounterNoteHideBeforeDate);
		var observationMoment = moment(note.observationDate);

		// Make annotation url
		var annotationLabel = "anno" + moment().unix();
		var annotationUrl = this.pageData.contextPath + "/annotation/annotation.jsp" +
			"?atbname=" + encodeURIComponent(annotationLabel) +
			"&table_id=" + encodeURIComponent(note.noteId) +
			"&display=EChartNote" +
			"&demo=" + encodeURIComponent(URL);

		var encounterTypeArray = getEncounterTypeArray();

		var selectedEncounterType = this.pageData.defaultEncounterType;
		if(note.encounterType)
		{
			selectedEncounterType = note.encounterType;
		}

		var noteColour = this.getNoteColour(note);

		var templateParameters = {
			index: index,
			contextPath: this.pageData.contextPath,
			note: note,
			issues: (issues == null ? [] : issues),
			noteLineArray: note.note.split("\n"),
			escapedNote: note.note.escapeHTML(),
			formattedObservationDate: formattedDate,
			formattedObservationDateTime: formattedDateTime,
			collapseNote: hideBeforeMoment.isAfter(observationMoment),
			edit: enableEdit,
			annotationLabel: annotationLabel,
			annotationUrl: annotationUrl,
			encounterTypeArray: encounterTypeArray,
			selectedEncounterType: selectedEncounterType,
			assignedIssuesTitle: this.pageData.assignedIssuesTitle,
			referenceResolvedIssuesTitle: this.pageData.referenceResolvedIssuesTitle,
			referenceUnresolvedIssuesTitle: this.pageData.referenceUnresolvedIssuesTitle,
			colour: noteColour,
		};

		return junoJQuery('#encounterNoteTemplate').tmpl(templateParameters);
	};

	this.getEditTextAreaName = function getEditTextAreaName()
	{
		if(!pageState.currentNoteData || pageState.currentNoteData.noteId == null)
		{
			return null;
		}

		return 'caseNote_note' + pageState.currentNoteData.noteId;
	};

	this.getNoteDataById = function getNoteDataById(noteId)
	{
		var uuid = junoJQuery("input#uuid" + noteId).val();
		var providerNo = junoJQuery("input#providerNo" + noteId).val();
		var observationDate = junoJQuery("input#observationDateInput" + noteId).val();
		var encounterType = junoJQuery("select#encounterTypeSelect" + noteId).val();
		var isSigned = junoJQuery("input#isSigned" + noteId).val();
		var isVerified = junoJQuery("input#isVerified" + noteId).val();
		var appointmentNo = junoJQuery("input#appointmentNo" + noteId).val();
		var noteText = junoJQuery("textarea#caseNote_note" + noteId).val();

		var observationMoment = moment();
		if(observationDate)
		{
			observationMoment = moment(observationDate, "DD-MMM-YYYY HH:mm");
		}

		return {
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
	};

	this.buildNote = function buildNote(
		noteId,
		uuid,
		observationDate,
		providerNo,
		encounterType,
		isSigned,
		isVerified,
		appointmentNo,
		noteText
	)
	{
		return {
			noteId: noteId,
			uuid: uuid,
			observationDate: observationDate,
			providerNo: providerNo,
			encounterType: encounterType,
			isSigned: isSigned,
			isVerified: isVerified,
			appointmentNo: appointmentNo,
			note: noteText
		};
	};

	this.getEmptyNote = function getEmptyNote(providerNo, appointmentNo)
	{
		return {
			noteId: 0,
			uuid: null,
			observationDate: null,
			providerNo: providerNo,
			encounterType: null,
			isSigned: false,
			isVerified: false,
			appointmentNo: appointmentNo,
			note: this.getFormattedReason()
		};
	};

	this.saveTmpSave = function saveTmpSave()
	{
		// Gather information to save the note
		var demographicNo = this.pageData.demographicNo;
		var noteId = junoJQuery("input#editNoteId").val();


		// Prepare data
		var noteData = this.getNoteDataById(noteId);
		if(noteId === 0)
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

		var me = this;
		junoJQuery.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "../ws/rs/demographic/" + demographicNo + "/note/temp",
			data: JSON.stringify({
				noteId: noteId,
				note: noteData.note,
				observationDate: moment(noteData.observationDate),
				encounterType: noteData.encounterType,
			}),
			success: function (response)
			{
				me.setNoteStatus("Draft saved " + moment().format("DD-MMM-YYYY HH:mm:ss"));
			},
			error: function (response)
			{
				me.setNoteError("Error saving note");
			}
		});
	};

	this.checkNoteChanged = function checkNoteChanged()
	{
		if(lastTmpSaveNote == null)
		{
			if(pageState.currentNoteData && pageState.currentNoteData.note)
			{
				lastTmpSaveNote = pageState.currentNoteData.note;
			}
			else
			{
				lastTmpSaveNote = "";
			}
		}

		// Prepare data
		var noteId = junoJQuery("input#editNoteId").val();
		var noteData = this.getNoteDataById(noteId);

		// Trim the notes because that happens when the note is saved
		if(noteData.note.trim() !== lastTmpSaveNote.trim())
		{
			this.saveTmpSave();
			lastTmpSaveNote = noteData.note;
		}

		this.setTmpSaveTimer();
	};

	this.setTmpSaveTimer = function setTmpSaveTimer()
	{
		var me = this;
		var timeoutFunction = function()
		{
			me.checkNoteChanged();
		};

		autoSaveTimer = setTimeout(timeoutFunction, AUTO_SAVE_DELAY);
	};

	this.clearTmpSaveTimer = function clearTmpSaveTimer()
	{
		clearTimeout(autoSaveTimer);
	};

	/*
	this.buildBillingUrl = function buildBillingUrl(assignedIssueArray)
	{
		var billingUrl = this.pageData.billingUrl;

		// Add dx codes to it from the issue array
		for(var i = 0; i < assignedIssueArray.length; i++)
		{
			var dxCode = assignedIssueArray[i].issue.code;
			var codeNumber = i;
			if(codeNumber == 0)
			{
				codeNumber = "";
			}

			billingUrl += "&dxCode" + codeNumber + "=" + encodeURIComponent(dxCode.substring(0,3));
		}

		return this.pageData.contextPath + billingUrl
	};
	 */

	this.saveEncounterNote = function saveEncounterNote(signNote, verifyNote, exitAfterSaving, async, redirectToBilling)
	{
		var deferred = junoJQuery.Deferred();

		if(this.pageState.savingNote)
		{
			deferred.resolve();
			return deferred.promise();
		}

		this.pageState.savingNote = true;

		// Clear state
		this.clearNoteError();
		this.clearNoteStatus();

		// Gather information to save the note
		var demographicNo = this.pageData.demographicNo;
		var noteId = junoJQuery("input#editNoteId").val();

		// Prepare data
		var noteData = this.getNoteDataById(noteId);
		//
		if(noteId === 0)
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
		junoJQuery(
			"#noteIssueIdList input:checkbox[name=issue_id]:checked, #noteIssues input:checkbox[name=issue_id]:checked"
		).each(function()
		{
			issueIdArray.push(junoJQuery(this).val());
		});

		var me = this;
		junoEncounter.getAssignedIssueArray(issueIdArray, async).then(function(assignedIssueArray)
		{
			noteData.assignedIssues = assignedIssueArray;

			junoJQuery.ajax({
				async: async,
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "../ws/rs/notes/" + demographicNo + "/save?deleteTmpSave=true",
				data: JSON.stringify(noteData),
				success: function (response)
				{
					if (response.status !== "SUCCESS")
					{
						me.setNoteError(response.error.message);
					}
					else
					{
						// Set the saved note as the current note
						me.updateNoteInPageState(response.body, assignedIssueArray);
						var currentlyEditedNoteDiv = junoJQuery('div#n' + noteId).parent();
						me.replaceNoteEntry(currentlyEditedNoteDiv, pageState.currentNoteData, assignedIssueArray, demographicNo, true);

						lastTmpSaveNote = null;
						me.checkNoteChanged();

						if(redirectToBilling)
						{
							window.location.replace(me.pageData.billingUrl);
							me.maximizeWindow();
						}
						else if (exitAfterSaving)
						{
							window.close();
						}

						me.setNoteStatus("Note successfully saved");

						me.pageState.savingNote = false;
						deferred.resolve(response.body);
					}
				},
				error: function (response)
				{
					me.setNoteError("Error saving note");

					me.pageState.savingNote = false;
					deferred.reject();
				}
			});
		});

		return deferred.promise();
	};

	this.maximizeWindow = function maximizeWindow()
	{
		window.onunload = function()
		{
			window.resizeTo(screen.width,screen.height);
		};
	}

	this.setCaretPosition = function setCaretPosition(input, pos)
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
	};

	this.setNoteError = function setNoteError(errorMessage)
	{
		junoJQuery("div#noteSaveErrorMessage").text(errorMessage);
	};

	this.clearNoteError = function clearNoteError()
	{
		this.setNoteError("");
	};

	this.setNoteStatus = function setNoteStatus(message)
	{
		junoJQuery("div#noteSaveStatusMessage").text(message);
	};

	this.clearNoteStatus = function clearNoteStatus()
	{
		this.setNoteStatus("");
	};

	this.notesIncrementAndLoadMore = function notesIncrementAndLoadMore(demographicNo)
	{
		var mainDiv = $("encMainDiv");

		if (notesRetrieveOk && mainDiv.scrollTop <= 100)
		{
			notesRetrieveOk = false;

			var me = this;
			this.notesLoader(
				this.pageData.contextPath,
				this.pageState.notesOffset,
				this.pageData.notesIncrement,
				demographicNo,
				false
			).then(function ()
			{
				me.pageState.notesOffset += me.pageData.notesIncrement;
			});
		}
	};

	this.buildNoteLoaderUrl = function buildNoteLoaderUrl(demographicNo, numToReturn, offset)
	{

		var url = "../ws/rs/notes/" + demographicNo + "/all?numToReturn=" + numToReturn +
		"&offset=" + offset;

		if(junoJQuery.isArray(this.pageState.filteredProviders))
		{
			//for(var i = 0; i < filteredProviders.length; i++)
			junoJQuery.each(this.pageState.filteredProviders, function(index, value)
			{
				url += "&providerNoFilter=" + encodeURIComponent(value);
			});
		}

		if(junoJQuery.isArray(this.pageState.filteredRoles))
		{
			junoJQuery.each(this.pageState.filteredRoles, function(index, value)
			{
				url += "&roleNoFilter=" + encodeURIComponent(value);
			});
		}

		if(junoJQuery.isArray(this.pageState.filteredIssues))
		{
			//for(var i = 0; i < filteredIssues.length; i++)
			junoJQuery.each(this.pageState.filteredIssues, function(index, value)
			{
				url += "&issueFilter=" + encodeURIComponent(value);
			});
		}

		if(this.pageState.filterSort)
		{
			url += "&sortType=" + encodeURIComponent(this.pageState.filterSort);
		}

		return url;
	};

	this.clearNotes = function clearNotes()
	{
		junoJQuery('div#encMainDiv').empty();
	};

	this.notesLoader = function notesLoader(ctx, offset, numToReturn, demographicNo, scrollToBottom)
	{
		var deferred = junoJQuery.Deferred();
		$("notesLoading").style.display = "inline";

		var noteToEditDeferred = junoJQuery.ajax({
			type: "GET",
			contentType: "application/json",
			dataType: "json",
			url: "../ws/rs/notes/" + demographicNo + "/noteToEdit/latest"
		});

		var noteListDeferred = junoJQuery.ajax({
			type: "GET",
			contentType: "application/json",
			dataType: "json",
			url: this.buildNoteLoaderUrl(demographicNo, numToReturn, offset),
		});

		var tmpSaveDeferred = junoJQuery.ajax({
			type: "GET",
			contentType: "application/json",
			dataType: "json",
			url: "../ws/rs/demographic/" + demographicNo + "/note/temp",
		});

		var me = this;
		junoJQuery.when(noteListDeferred, noteToEditDeferred, tmpSaveDeferred).done(
			function (noteListResponse, noteToEditResponse, tmpSaveResponse)
			{
				// XXX: handle error (check response[1] = 'success')

				var response = noteListResponse[0];

				var tmpSave = "";
				if(tmpSaveResponse[1] === "success")
				{
					tmpSave = tmpSaveResponse[0].body;
					if(tmpSave)
					{
						tmpSave.noteId = tmpSave.noteId || 0; //handle null note id
					}
				}

				var noteToEdit = null;
				var issues = [];
				if (
					noteToEditResponse[1] === "success" &&
					noteToEditResponse[0].body &&
						// Edit new note if tmpSave is for note id 0
						(
							!tmpSave || tmpSave.noteId !== 0
						)
				)
				{
					noteToEdit = noteToEditResponse[0].body.encounterNote;
					issues = noteToEditResponse[0].body.assignedCMIssues;
				}
				else
				{
					noteToEdit = me.getEmptyNote(me.pageData.providerNo, me.pageData.appointmentNo);
				}

				me.updateNoteInPageState(noteToEdit, issues);

				$("notesLoading").style.display = "none";
				me.displayNotes(demographicNo, response.body.notelist, noteToEdit, tmpSave, issues,
					scrollToBottom, offset);

				me.adjustCaseNote();
				me.observeTextArea();
				me.setSaveButtonVisibility();
				me.setTmpSaveTimer();
				me.focusTextArea();

				if (typeof response !== undefined && 'body' in response)
				{
					notesRetrieveOk = response.body.moreNotes;
				}

				if (!notesRetrieveOk)
				{
					clearInterval(pageState.notesScrollCheckInterval);
				}

				deferred.resolve();
			});

		return deferred.promise();
	};

	this.getFormattedReason = function getFormattedReason()
	{
		var formattedReason = "";
		var reason = this.pageData.reason;
		var appointmentDate = this.pageData.appointmentDate;

		if(reason == null)
		{
			reason = "";
		}

		if( appointmentDate == null || appointmentDate === "" || appointmentDate.toLowerCase() === "null")
		{
			formattedReason = "\n[" + moment().format("DD-MMM-YYYY") + " .: " + reason + "] \n";
		}
		else
		{
			var appointmentMoment = moment(appointmentDate);
			formattedReason = "\n[" + appointmentMoment.format("DD-MMM-YYYY") + " .: " + reason + "]\n";
		}

		return formattedReason;
	};

	this.setSaveButtonVisibility = function setSaveButtonVisibility()
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
	};


	this.enableCalendar = function enableCalendar(noteId)
	{
		Calendar.setup({
			inputField : "observationDateInput" + noteId,
			ifFormat : "%d-%b-%Y %H:%M ",
			showsTime :true,
			button : "observationDate_cal",
			singleClick : true,
			step : 1
		});
	};

	this.minView = function minView(e, nodeId)
	{
		this.toggleShrunkNote(e, nodeId, true);
	};

	this.maxView = function maxView(e, nodeId)
	{
		this.toggleShrunkNote(e, nodeId, false);
	};

	this.expandAllNotes = function expandAllNotes()
	{
		var shrunkNotes = junoJQuery("[id^=minimizedNote]").filter(":visible");

		var me = this;
		shrunkNotes.each(function()
		{
			if (/^minimizedNote\d*$/.test(this.id))
			{
				var noteId = this.id.substring(13);
				me.toggleShrunkNote(null, noteId, false)
			}
		});
	};

	this.toggleShrunkNote = function toggleShrunkNote(e, nodeId, shrink)
	{
		//var txt = Event.element(e).parentNode.id;
		var noteDivId = "#n" + nodeId;
		var minimizedNoteDivId = "#minimizedNote" + nodeId;

		if (shrink)
		{
			junoJQuery(noteDivId).hide();
			junoJQuery(minimizedNoteDivId).show();
		}
		else
		{
			junoJQuery(minimizedNoteDivId).hide();
			junoJQuery(noteDivId).show();
		}
	};

	this.copyCppToCurrentNote = function copyCppToCurrentNote()
	{
		var noteId = junoJQuery("input#editNoteId").val();
		var noteData = this.getNoteDataById(noteId);

		var currentNoteText = noteData.note;

		currentNoteText += "\n";
		currentNoteText += junoJQuery("#noteEditTxt").val();

		junoJQuery("#caseNote_note" + noteId).val(currentNoteText);
	};

	this.spellCheck = function spellCheck()
	{
		var noteId = junoJQuery("input#editNoteId").val();
		var caseNote = "caseNote_note" + noteId;

		// Build an array of form elements (not there values)
		var elements = new Array(0);

		// Your form elements that you want to have spell checked
		elements[elements.length] = document.getElementById(caseNote);

		// Start the spell checker
		startSpellCheck(this.pageData.contextPath + '/jspspellcheck/',elements);
	};

	this.onClosing = function onClosing()
	{
		var noteId = junoJQuery("input#editNoteId").val();

		// Prepare data
		var noteData = this.getNoteDataById(noteId);

		// Save unfinished note on exit. The temp save stuff added in Oscar15 is too fragile
		// to depend on

		// Trim the notes because that happens when the note is saved
		if(pageState.currentNoteData.note.trim() != noteData.note.trim())
		{
			this.saveEncounterNote(false, false, true, false, false);
		}

		// Tell child measurement windows that we're leaving
		this.junoEncounter.cleanUpWindows();

		return null;
	};

	this.closeEnc = function closeEnc(e)
	{
		Event.stop(e);

		var noteId = junoJQuery("input#editNoteId").val();
		var noteData = this.getNoteDataById(noteId);

		if(
				pageState.currentNoteData.note.trim() === noteData.note.trim() ||
				confirm(pageData.closeWithoutSaveMsg))
		{
			window.close();
		}

		return false;
	};
};
