'use strict';

if (!window.Juno) window.Juno = {};
if (!Juno.OscarEncounter) Juno.OscarEncounter = {};
if (!Juno.OscarEncounter.JunoEncounter) Juno.OscarEncounter.JunoEncounter = function JunoEncounter(pageData)
{
	this.pageData = pageData;

	this.checkLengthOfObject = function checkLengthOfObject(o)
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
	};

	this.getUTCDateFromString = function getUTCDateFromString(dateString)
	{
		if (dateString == null || dateString === "")
		{
			return null;
		}

		var dateMoment = moment(dateString);

		if (!dateMoment.isValid())
		{
			return null;
		}

		return dateMoment.toDate();
	};

	this.getFormattedDate = function getFormattedDate(date)
	{
		if (date == null || date === "")
		{
			return null;
		}

		return moment(date).format("YYYY-MM-DD");
	};

	this.openAnnotation = function openAnnotation()
	{
		var atbname = document.getElementById('annotation_attrib').value;
		var data = $A(arguments);
		var addr = ctx + "/annotation/annotation.jsp?atbname=" + atbname + "&table_id=" + data[1] + "&display=" + data[2] + "&demo=" + data[3];
		window.open(addr, "anwin", "width=400,height=500");
		Event.stop(data[0]);
	};

	this.popupUploadPage = function popupUploadPage(varpage, dn)
	{
		var page = "" + varpage + "?demographicNo=" + dn;
		windowprops = "height=500,width=500,location=no,"
			+ "scrollbars=no,menubars=no,toolbars=no,resizable=yes,top=50,left=50";
		var popup = window.open(page, "", windowprops);
		popup.focus();

	};

	this.delay = function delay(time)
	{
		var string = "document.getElementById('ci').src='" + this.pageData.imagePresentPlaceholderUrl + "'";
		setTimeout(string, time);
	};

	this.showHistory = function showHistory(noteId, event)
	{
		Event.stop(event);
		var rnd = Math.round(Math.random() * 1000);
		win = "win" + rnd;
		var url = this.pageData.contextPath + "/CaseManagementEntry.do?method=notehistory&noteId=" + noteId;
		window.open(url, win, "scrollbars=yes, location=no, width=647, height=600", "");
		return false;
	};

	this.popupPage = function popupPage(vheight, vwidth, name, varpage)
	{
		var openWindows = {};
		var reloadWindows = {};
		var updateDivTimer = null;
		if (varpage == null || varpage === -1)
		{
			return false;
		}
		if (varpage.indexOf("..") === 0)
		{
			varpage = this.pageData.contextPath + varpage.substr(2);
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
				var me = this;
				updateDivTimer = setInterval(
					function ()
					{
						if (me.checkLengthOfObject(openWindows) > 0)
						{
							for (var name in openWindows)
							{
								if (openWindows[name].closed && reloadWindows[name] !== undefined)
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

						if (me.checkLengthOfObject(openWindows) === 0)
						{
							clearInterval(updateDivTimer);
							updateDivTimer = null;
						}

					}, 1000);
			}
		}
	};

	this.getAssignedIssueArray = function getAssignedIssueArray(issueIdArray)
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
			if (deferredArray.length === 1)
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
			}

			deferred.resolve(assignedIssueArray);
		});

		return deferred.promise();
	};

	this.isCppSection = function isCppSection(sectionName)
	{
		return ["SocHistory", "MedHistory", "Concerns", "Reminders"].indexOf(sectionName) !== -1;
	};

	this.getSectionRemote = function getSectionRemote(sectionName, getAll, disableExpand)
	{
		var appointmentNo = this.pageData.appointmentNo;
		var demographicNo = this.pageData.demographicNo;

		var limit = null;
		var offset = null;
		if (!getAll)
		{
			limit = 6;
			offset = 0;
		}

		var me = this;
		jQuery.ajax({
			type: "GET",
			contentType: "application/json",
			dataType: "json",
			url: encounterNote.getEncounterSectionUrl(sectionName, demographicNo, appointmentNo, limit, offset),
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
					if (index % 2 === 0)
					{
						note.rowClass = "encounterNoteEven";
					}

					// Show the close arrow on the first and last row
					if (!disableExpand && getAll && (index === 0 || index === response.body.notes.length - 1))
					{
						note.showCollapse = true;
					} else if (!disableExpand && !getAll && index === response.body.notes.length - 1)
					{
						note.showExpand = true;
					}

					var newNode;
					if (me.isCppSection(sectionName))
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
	};
};
