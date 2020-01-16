'use strict';

if (!window.Juno) window.Juno = {};
if (!Juno.OscarEncounter) Juno.OscarEncounter = {};
if (!Juno.OscarEncounter.JunoEncounter) Juno.OscarEncounter.JunoEncounter = function JunoEncounter(pageData, pageState)
{
	this.pageData = pageData;

	var openWindows = {};
	var measurementWindows = [];

	this.monkeyPatches = function monkeyPatches()
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
		};
	};

	this.resizeContent = function resizeContent()
	{
		// Resize the content to fit the window
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

			var divHeight = windowHeight - 280;
			$("encMainDiv").style.height = divHeight + 'px';
		}
	};

	this.configureNifty = function configureNifty()
	{
		// Configure nifty
		if (!NiftyCheck())
		{
			return false;
		}

		Rounded("div.showEdContent", "all", "transparent", "#CCCCCC", "big border #000000");
		Rounded("div.printOps", "all", "transparent", "#CCCCCC", "big border #000000");

		return true;
	};

	this.configureCalendar = function configureCalendar()
	{
		// Calendar configuration
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
	};

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
		var addr = this.pageData.contextPath + "/annotation/annotation.jsp?atbname=" + atbname + "&table_id=" + data[1] + "&display=" + data[2] + "&demo=" + data[3];
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

	this.measurementLoaded = function measurementLoaded(name)
	{
		measurementWindows.push(openWindows[name]);
	};

	this.cleanUpWindows = function cleanUpWindows()
	{
		for (var idx = 0; idx < measurementWindows.length; ++idx)
		{
			if (!measurementWindows[idx].closed)
			{
				measurementWindows[idx].parentChanged = true;
			}
		}
	};

	this.popupPage = function popupPage(vheight, vwidth, name, varpage)
	{
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
					note.contextPath = me.pageData.contextPath;
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


	this.initOceanToolbar = function initOceanToolbar()
	{
		if(this.pageData.cmeJs == 'ocean_toolbar')
		{
			jQuery.ajax({ url: "../eform/displayImage.do?imagefile=oceanToolbar.js", cache: true, dataType: "script" });
		}
	};

	this.writeToEncounterNote = function writeToEncounterNote(request)
	{
		var text = request.responseText;

		text = text.replace(/\\u000A/g, "\u000A");
		text = text.replace(/\\u000D/g, "");
		text = text.replace(/\\u003E/g, "\u003E");
		text = text.replace(/\\u003C/g, "\u003C");
		text = text.replace(/\\u005C/g, "\u005C");
		text = text.replace(/\\u0022/g, "\u0022");
		text = text.replace(/\\u0027/g, "\u0027");


		encounterNote.pasteToEncounterNote(text);
	};

	this.ajaxInsertTemplate = function ajaxInsertTemplate(varpage)
	{
		//fetch template

		if (varpage != 'null')
		{
			var me = this;
			var page = this.pageData.contextPath + "/oscarEncounter/InsertTemplate.do";
			var params = "templateName=" + varpage + "&version=2";
			new Ajax.Request(page, {
					method: 'post',
					postBody: params,
					evalScripts: true,
					onSuccess: me.writeToEncounterNote,
					onFailure: function()
					{
						alert(insertTemplateError);
					}
				}
			);
		}

	};

	this.channelSearch = function channelSearch()
	{
		var url = $('channel').options[$('channel').selectedIndex].value +
			encodeURIComponent($F('keyword'));

		popupPage(600,800,'<bean:message key="oscarEncounter.Index.popupSearchPageWindow"/>', url);

		return false;
	};

	/**
	 * Allows calculators to be opened by clicking on them in a select menu.  This is needed for cross-platform
	 * functionality to achieve an effect similar to onClick for a select option element.
	 * (onClick on the option element doesn't work in Chrome (or IE), and onClick on the select doesn't work in FireFox)
	 *
	 * @param calculatorMenu jQuery element referencing a select with urls as option values
	 */
	this.bindCalculatorListener = function bindCalculatorListener(calculatorMenu)
	{
		var me = this;
		calculatorMenu.change(
			function()
			{
				var x_size = calculatorMenu.attr('x_size'),
					y_size = calculatorMenu.attr('y_size');

				me.popperup(x_size, y_size, calculatorMenu.val(), calculatorMenu.text());

				// Since we are listening for the change event, we need to account for the same calculator
				// selected twice in a row.  A side effect is that the UI will be updated when we reset the
				// value of the select menu to the default.  Here we're using the value "none" over a -1 index
				// because this is the key to a disabled "title" element, whereas -1 will display an empty
				// select menu.
				calculatorMenu.val("none");
			});
	};

	this.popperup = function popperup(vheight, vwidth, varpage, pageName)
	{
		//open a new popup window
		var windowprops = "height=" + vheight + ",width=" + vwidth + ",status=yes,location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=0,screenY=0,top=100,left=100";
		var popup = window.open(varpage, pageName, windowprops);
		popup.pastewin = opener;
		popup.focus();
	};

	this.showMenu = function showMenu(menuNumber, eventObj)
	{
		var menuId = 'menu' + menuNumber;
		return showPopup(menuId, eventObj);
	};

	this.setWindowSize = function setWindowSize()
	{
		if(this.pageData.encounterWindowMaximize)
		{
			jQuery(document).ready(function(){window.resizeTo(screen.width,screen.height);});
		}
		else if(this.pageData.encounterWindowCustomSize)
		{
			window.resizeTo(pageData.encounterWindowWidth,pageData.encounterWindowHeight);
		}
	};

	this.configureCalculator = function configureCalculator()
	{
		var calculatorMenu = jQuery('#calculators_menu');
		junoEncounter.bindCalculatorListener(calculatorMenu);
	};

	this.configureMultiSearchAutocomplete = function configureMultiSearchAutocomplete()
	{
		// Multi-search autocomplete
		var searchAutocompleteUrl = "../ws/rs/encounterSections/" + this.pageData.demographicNo + "/autocomplete/";

		var appointmentQueryString = "";
		if(this.pageData.appointmentNo)
		{
			appointmentQueryString = "?appointmentNo=" + this.pageData.appointmentNo;
		}


		jQuery("#enTemplate").autocomplete({
			source: function(request, response)
			{
				jQuery.getJSON(searchAutocompleteUrl + request.term + appointmentQueryString, function(data)
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
			focus: function(event, ui)
			{
				event.preventDefault();
			},
			select: function(event, ui)
			{
				event.preventDefault();
				new Function(ui.item.value)();
				jQuery("#enTemplate").val(ui.item.label);
			},
			minLength: 2,
			delay: 100
		});
	};
};
