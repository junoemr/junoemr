'use strict';

if (!window.Juno) window.Juno = {};
if (!Juno.OscarEncounter) Juno.OscarEncounter = {};
if (!Juno.OscarEncounter.JunoEncounter) Juno.OscarEncounter.JunoEncounter = function JunoEncounter(pageData, pageState)
{
	this.pageData = pageData;
	this.pageState = pageState;

	var openWindows = {};
	var measurementWindows = [];

	this.monkeyPatches = function monkeyPatches()
	{
		// This was messing up the serialization of arrays to JSON so I removed it.
		delete Array.prototype.toJSON;

		// Monkey Patch from https://stackoverflow.com/a/16208232
		if (typeof junoJQuery.when.all === 'undefined')
		{
			junoJQuery.when.all = function (deferreds)
			{
				return junoJQuery.Deferred(function (def)
				{
					junoJQuery.when.apply(jQuery, deferreds).then(
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

	// Set the initial width of the page and set up a monitor to make sure it doesn't get smaller
	this.initNavBarMonitor = function initNavBarMonitor()
	{
	    this.emulateMinWidth(null);
		Element.observe(window, "resize", this.emulateMinWidth);
	}

	// Keep the encounter window from getting smaller
	this.emulateMinWidth = function emulateMinWidth(e)
	{
		var win = pageWidth();
		var main = Element.getWidth("body");

		if (e == null)
		{
			this.pageState.minMain = Math.round(main * this.pageState.minDelta);
			this.pageState.minWin = Math.round(win * this.pageState.minDelta);
		}

		if (main < this.pageState.minMain)
		{
			$("body").style.width = this.pageState.minMain + "px";
		}
		else if (win >= this.pageState.minWin && main == this.pageState.minMain)
		{
			$("body").style.width = "100%";
		}

	}

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

	this.popupPage = function popupPage(vheight, vwidth, windowName, varpage)
	{
		return this.popupPageAndReload(vheight, vwidth, windowName, varpage, null);
	}

	this.popupPageAndReload = function popupPageBase(vheight, vwidth, windowName, varpage, sectionToReload)
	{
		if (varpage == null || varpage === -1)
		{
			return false;
		}

		if (varpage.indexOf("..") === 0)
		{
			varpage = this.pageData.contextPath + varpage.substr(2);
		}

		var page = "" + varpage;
		var windowprops = "height=" + vheight + ",width=" + vwidth
				+ ",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=600,screenY=200,top=0,left=0";

		var windowProxy = window.open(page, windowName, windowprops);

		if (sectionToReload !== null)
		{
			this.reloadSectionOnWindowClose(windowName, windowProxy, sectionToReload)
		}

		return false;
	}

	// Create an interval that watches for windows being closed.  After they are
	// closed, reload the required section of the page and stop watching that
	// window.
	this.reloadSectionOnWindowClose = function reloadSectionOnWindowClose(windowName, windowProxy, sectionToReload)
	{
		this.pageState.openWindows[name] = {
			windowProxy: windowProxy,
			sectionToReload: sectionToReload,
		}

		if(this.pageState.reloadSectionTimer === null)
		{
			var me = this;
			this.pageState.reloadSectionTimer = setInterval(function()
			{
				if(me.checkLengthOfObject(me.pageState.openWindows) === 0)
				{
					clearInterval(me.pageState.reloadSectionTimer);
					me.pageState.reloadSectionTimer = null;
					return;
				}

				for(var name in me.pageState.openWindows)
				{
					var windowInfo = me.pageState.openWindows[name];

					if(windowInfo.windowProxy.closed)
					{
						me.getSectionRemote(windowInfo.sectionToReload, false, true)
						delete me.pageState.openWindows[name];
					}
				}
			}, 1000);
		}
	}

	this.getAssignedIssueArray = function getAssignedIssueArray(issueIdArray, async)
	{
		var deferred = junoJQuery.Deferred();

		var deferredArray = [];

		for (var i = 0; i < issueIdArray.length; i++)
		{
			var issueId = issueIdArray[i];

			var ajaxPromise = junoJQuery.ajax({
				async: async,
				type: "POST",
				url: "../ws/rs/notes/getIssueById/" + issueId
			});

			deferredArray.push(ajaxPromise);
		}

		junoJQuery.when.all(deferredArray).then(function (response)
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
		junoJQuery.ajax({
			type: "GET",
			contentType: "application/json",
			dataType: "json",
			url: encounterNote.getEncounterSectionUrl(sectionName, demographicNo, appointmentNo, limit, offset),
			success: function (response)
			{
				var containerDiv = junoJQuery('#' + sectionName + 'list');

				containerDiv.empty();

				junoJQuery.each(response.body.notes, function (index, note)
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

					note.textLineArray = [];
					if(note.text)
					{
						note.textLineArray = note.text.split(/\r?\n/);
					}

					var newNode;
					if (me.isCppSection(sectionName))
					{
						newNode = junoJQuery('#sectionCppNoteTemplate').tmpl(note);
					} else
					{
						newNode = junoJQuery('#sectionNoteTemplate').tmpl(note);
					}

					return newNode.appendTo(containerDiv);
				});
			}
		});
	};
/*

	this.fillJQueryTemplate = function fillJQueryTemplate(templateElement, templateData)
	{
		if(templateElement.length && !junoJQuery.isFunction(templateElement.tmpl))
		{
			junoJQuery.ajax({
				url: "../share/documentUploader/jquery.tmpl.min.js",
				cache: true,
				dataType: "script",
				success: function(response)
				{

				}
			});
		}

		return templateElement.tmpl(templateData);
	};

*/

	this.initOceanToolbar = function initOceanToolbar()
	{
		if(this.pageData.cmeJs == 'ocean_toolbar')
		{
			junoJQuery.ajax({ url: "../eform/displayImage.do?imagefile=oceanToolbar.js", cache: true, dataType: "script" });
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
	 * @param calculatorMenu junoJQuery element referencing a select with urls as option values
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
			junoJQuery(document).ready(function(){window.resizeTo(screen.width,screen.height);});
		}
		else if(this.pageData.encounterWindowCustomSize)
		{
			window.resizeTo(pageData.encounterWindowWidth,pageData.encounterWindowHeight);
		}
	};

	this.configureCalculator = function configureCalculator()
	{
		var calculatorMenu = junoJQuery('#calculators_menu');
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


		junoJQuery("#enTemplate").autocomplete({
			source: function(request, response)
			{
				junoJQuery.getJSON(searchAutocompleteUrl + request.term + appointmentQueryString, function(data)
				{
					response(junoJQuery.map(data.body, function(section, index)
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
				junoJQuery("#enTemplate").val(ui.item.label);
			},
			minLength: 2,
			delay: 100
		}).data("ui-autocomplete")._renderItem = function( ul, item )
		{
			// HACK: override the _renderItem() method to highlight the search string
			let txt = String(item.label).replace(new RegExp(this.term, "gi"),"<b>$&</b>");
			return junoJQuery("<li></li>")
				.data("ui-autocomplete-item", item)
				.append("<a>" + txt + "</a>")
				.appendTo(ul);
		};
	};

	this.openTemplatePage = function openTemplatePage(selectedValue)
	{
	    if(selectedValue !== "-1")
		{
			popupPage(700,700,'Templates',selectedValue);
		}
	};

	this.grabEnter = function grabEnter(id, event)
	{
		var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
		if (keyCode == 13)
		{
			$(id).click();
			return false;
		}

		return true;
	}
};
