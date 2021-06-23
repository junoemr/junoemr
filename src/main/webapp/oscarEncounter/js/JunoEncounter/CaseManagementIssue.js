'use strict';

if (!window.Juno) window.Juno = {};
if (!Juno.OscarEncounter) Juno.OscarEncounter = {};
if (!Juno.OscarEncounter.JunoEncounter) Juno.OscarEncounter.JunoEncounter = {};
if (!Juno.OscarEncounter.JunoEncounter.CaseManagementIssue) Juno.OscarEncounter.JunoEncounter.CaseManagementIssue =
	function CaseManagementIssue(pageData, pageState, encounterNote)
{
	this.pageData = pageData;
	this.encounterNote = encounterNote;

	var issueIdToChange = null;
	var changeModeResolved = null;

	this.displayResolvedIssues = function displayResolvedIssues(clickContext)
	{
		this.showIssues(
			clickContext,
			'resolvedIssues',
			'noteIssues-resolved',
			'<bean:message key="oscarEncounter.referenceResolvedIssues.title"/>',
			true
		);
	};

	this.displayUnresolvedIssues = function displayUnresolvedIssues(clickContext)
	{
		this.showIssues(
			clickContext,
			'unresolvedIssues',
			'noteIssues-unresolved',
			'<bean:message key="oscarEncounter.referenceUnresolvedIssues.title"/>',
			false
		);
	};

	this.showIssues = function showIssues(clickContext, method, divId, title, resolved)
	{
		var me = this;

		junoJQuery.ajax({
			type: "GET",
			contentType: "application/json",
			dataType: "json",
			url: "../ws/rs/demographic/" + this.pageData.demographicNo + "/" + method,
			success: function (response)
			{
				if(!response || !junoJQuery.isArray(response.body))
				{
					return false;
				}

				junoJQuery('#' + divId).empty();
				me.displayExistingIssueList(clickContext, response.body, divId, title, resolved);
			}
		});
	};

	this.getIssueIdArray = function getIssueIdArray(assignedCMIssues)
	{
		var issueIdArray = [];

		if(!junoJQuery.isArray(assignedCMIssues))
		{
			return issueIdArray;
		}

		for(var i = 0; i < assignedCMIssues.length; i++)
		{
			issueIdArray.push(assignedCMIssues[i].issue.id);
		}

		return issueIdArray;
	};

	this.removeMatchingIssueCheckboxes = function removeMatchingIssueCheckboxes(issueIdArray)
	{
		junoJQuery("#noteIssueIdList input:checkbox[name=issue_id]").each(function ()
		{
			var issueId = parseInt(junoJQuery(this).val());

			if(junoJQuery.inArray(issueId, issueIdArray) >= 0)
			{
				junoJQuery(this).remove();
			}
		});
	};

	this.displayExistingIssueList = function displayExistingIssueList(clickContext, issueArray, divId, title, resolved)
	{
		var assignedIssueIdArray = this.getIssueIdArray(pageState.currentAssignedCMIssues);

		this.removeMatchingIssueCheckboxes(this.getIssueIdArray(issueArray));

		var templateParameters = {
			title: title,
			issueArray: issueArray,
			assignedIssueArray: assignedIssueIdArray,
			resolved: (resolved ? 'true' : 'false'),
			propertyArray: [
				{
					name: "acute",
					label: "acute",
					fieldName: "acute",
					fieldValue: "true",
					class: ""
				},
				{
					name: "chronic",
					label: "chronic",
					fieldName: "acute",
					fieldValue: "false",
					class: "setIssueListControlRight"
				},
				{
					name: "certain",
					label: "certain",
					fieldName: "certain",
					fieldValue: "true",
					class: ""
				},
				{
					name: "uncertain",
					label: "uncertain",
					fieldName: "certain",
					fieldValue: "false",
					class: "setIssueListControlRight"
				},
				{
					name: "major",
					label: "major",
					fieldName: "major",
					fieldValue: "true",
					class: ""
				},
				{
					name: "not_major",
					label: "not major",
					fieldName: "major",
					fieldValue: "false",
					class: "setIssueListControlRight"
				},
				{
					name: "resolved",
					label: "resolved",
					fieldName: "resolved",
					fieldValue: "true",
					class: ""
				},
				{
					name: "unresolved",
					label: "unresolved",
					fieldName: "resolved",
					fieldValue: "false",
					class: "setIssueListControlRight"
				},
			]
		};

		clickContext.junoJQuery('#existingIssueTemplate').tmpl(templateParameters).appendTo('#' + divId);

		Element.show(divId);
	};

	this.getSaveIssueUrl = function getSaveIssueUrl(issueId)
	{
		return "../ws/rs/demographic" +
			"/" + this.pageData.demographicNo +
			"/caseManagementIssue" +
			"/" + encodeURIComponent(issueId) +
			"/updateProperty"
	};

	this.saveIssueProperty = function saveIssueProperty(clickContext, issueId, propertyName, propertyValue)
	{
		var postData = {
			propertyName: propertyName,
			propertyValue: propertyValue
		};

		var jsonString = JSON.stringify(postData);

		var me = this;

		junoJQuery.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: this.getSaveIssueUrl(issueId),
			data: jsonString,
			success: function (response)
			{
				if(propertyName === 'resolved')
				{
					// Update both lists if resolvedness changes
					me.displayResolvedIssues(clickContext);
					me.displayUnresolvedIssues(clickContext);
				}
			}
		});
	};

	this.toggleIssueWidget = function toggleIssueWidget(issueId)
	{
		var widget = junoJQuery('#setIssueListWidget' + issueId);
		var widgetContainer = junoJQuery('#issueListWidgetContainer' + issueId);

		if(widget.is(":visible"))
		{
			widgetContainer.css('background-color', '');
			widgetContainer.css('border', '');
			widget.hide();
		}
		else
		{
			var me = this;
			junoJQuery.ajax({
				type: "GET",
				url: "../ws/rs/demographic/" + this.pageData.demographicNo + "/caseManagementIssue/" + issueId,
				success: function(response)
				{
					if(response.status !== "SUCCESS")
					{
						return;
					}

					var issue = response.body;

					widgetContainer.css('background-color', '#dde3eb');
					widgetContainer.css('border', '1px solid #464f5a');
					widget.show();


					me.checkIssueItem(issueId, 'acute', 'chronic', issue.acute);
					me.checkIssueItem(issueId, 'certain', 'uncertain', issue.certain);
					me.checkIssueItem(issueId, 'major', 'not_major', issue.major);
					me.checkIssueItem(issueId, 'resolved', 'unresolved', issue.resolved);
				}
			});
		}
	};

	this.checkIssueItem = function checkIssueItem(issueId, trueField, falseField, value)
	{
		var field = falseField;
		if(value)
		{
			field = trueField;
		}

		junoJQuery('#issueCheckList' + issueId + '\\.' + field).prop('checked', true);
	};

	this.addIssueToCurrentNote = function addIssueToCurrentNote(event)
	{
		var nodeId = junoJQuery('input#issueSearchSelectedId').val();
		var issueDescription = junoJQuery('input#issueSearchSelected').val();

		if(!nodeId)
		{
			return false;
		}

		this.addIssue("caseManagementEntryForm", "noteIssueIdList", "issueAutocomplete", nodeId, issueDescription);

		junoJQuery('input#issueSearchSelectedId').val("");
		junoJQuery('input#issueSearchSelected').val("");
	};

	this.addIssue = function addIssue(formName, parentNodeId, autocompleteId, nodeId, issueDescription)
	{

		var size = 0;
		var found = false;
		var form = document.forms[formName];
		var curItems = null;

		if(form)
		{
			curItems = form.elements["issueId"];
		}

		if(curItems && typeof curItems.length != "undefined")
		{
			size = curItems.length;

			for(var idx = 0; idx < size; ++idx)
			{
				if (curItems[idx].value === nodeId)
				{
					found = true;
					break;
				}
			}
		}
		else if(curItems && typeof curItems.value != "undefined")
		{
			found = curItems.value === nodeId;
		}

		if(!found)
		{
			var node = document.createElement("LI");

			var html = "<input type='checkbox' id='issueId' name='issue_id' checked value='" + nodeId + "'>" + issueDescription;
			new Insertion.Top(node, html);

			$(parentNodeId).appendChild(node);
			$(autocompleteId).value = "";
		}
	};

	this.submitIssue = function submitIssue(event)
	{
		var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
		if (keyCode === 13)
		{
			return false;
		}
	};

	this.getChangeIssueUrl = function getChangeIssueUrl(issueId, newIssueId)
	{
		return "../ws/rs/demographic" +
			"/" + this.pageData.demographicNo +
			"/caseManagementIssue" +
			"/" + encodeURIComponent(issueId) +
			"/updateIssue";
	};

	this.changeIssue = function changeIssue(clickContext)
	{
		var newIssueId = junoJQuery('input#issueSearchSelectedId').val();

		if(!newIssueId)
		{
			return false;
		}

		var postData = {
			newIssueId: newIssueId
		};

		var jsonString = JSON.stringify(postData);

		var me = this;

		junoJQuery.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: this.getChangeIssueUrl(issueIdToChange, newIssueId),
			data: jsonString,
			success: function (response)
			{
				if(changeModeResolved)
				{
					me.displayResolvedIssues(clickContext);
				}
				else
				{
					me.displayUnresolvedIssues(clickContext);
				}

				junoJQuery('input#issueSearchSelectedId').val("");
				junoJQuery('input#issueSearchSelected').val("");
			}
		});
	};

	this.enableChangeMode = function enableChangeMode(issueId, resolved)
	{
		issueIdToChange = issueId;
		changeModeResolved = resolved;

		junoJQuery("#asgnIssues").hide();
		junoJQuery("#changeIssues").show();
	};




	this.saveIssueId = function saveIssueId(txtField, listItem)
	{
		junoJQuery('input#issueSearchSelectedId').val(listItem.id);
		junoJQuery('input#issueSearchSelected').val(listItem.innerHTML);
	};

	this.addIssueToCPP = function addIssueToCPP(txtField, listItem)
	{
		var nodeId = listItem.id;
		var issueDescription = listItem.innerHTML;
		caseManagementIssue.addIssue(
			"frmIssueNotes",
			"issueIdList",
			"issueAutocompleteCPP",
			nodeId,
			issueDescription
		);

		$("issueChange").value = true;
	};

	this.autoCompleteShowMenuCPP = function autoCompleteShowMenuCPP(element, update)
	{
		Effect.Appear($("issueListCPP"), {duration: 0.15});
		Effect.Appear(update, {duration: 0.15});
	};

	this.autoCompleteHideMenuCPP = function autoCompleteHideMenuCPP(element, update)
	{
		new Effect.Fade(update, {duration: 0.15});
		new Effect.Fade($("issueListCPP"), {duration: 0.15});
	};

	this.autoCompleteShowMenu = function autoCompleteShowMenu(element, update)
	{
		$("issueList").style.left = $("mainContent").style.left;
		$("issueList").style.top = $("mainContent").style.top;
		$("issueList").style.width = $("issueAutocompleteList").style.width;

		Effect.Appear($("issueList"), {duration: 0.15});
		Effect.Appear($("issueTable"), {duration: 0.15});
		Effect.Appear(update, {duration: 0.15});
	};

	this.autoCompleteHideMenu = function autoCompleteHideMenu(element, update)
	{
		new Effect.Fade(update, {duration: 0.15});
		new Effect.Fade($("issueTable"), {duration: 0.15});
		new Effect.Fade($("issueList"), {duration: 0.15});
	};

	this.configureIssueAutocompleteCPP = function configureIssueAutocompleteCPP()
	{
		var issueURL = pageData.contextPath + "/CaseManagementEntry.do" +
			"?method=issueList" +
			"&demographicNo=" + this.pageData.demographicNo +
			"&providerNo=" + this.pageData.providerNo +
			"&all=true";

		var issueAutoCompleterCPP = new Ajax.Autocompleter(
			"issueAutocompleteCPP",
			"issueAutocompleteListCPP",
			issueURL,
			{
				minChars: 3,
				indicator: 'busy2',
				afterUpdateElement: this.addIssueToCPP,
				onShow: this.autoCompleteShowMenuCPP,
				onHide: this.autoCompleteHideMenuCPP
			}
		);
	};

	this.configureIssueAutocomplete = function configureIssueAutocomplete()
	{
		var issueURL = pageData.contextPath + "/CaseManagementEntry.do" +
			"?method=issueList" +
			"&demographicNo=" + this.pageData.demographicNo +
			"&providerNo=" + this.pageData.providerNo;

		issueAutoCompleter = new Ajax.Autocompleter(
			"issueAutocomplete",
			"issueAutocompleteList",
			issueURL,
			{
				minChars: 3,
				indicator: 'busy',
				afterUpdateElement: this.saveIssueId,
				onShow: this.autoCompleteShowMenu,
				onHide: this.autoCompleteHideMenu
			}
		);
	};

	this.configureIssueButtons = function configureIssueButtons()
	{
		// Click handlers for the resolved/unresolved issue buttons.  They pass in the
		// junoJQuery object from this context because it wouldn't work with the local context
		// inside the handler.  I don't know why, but this made it work.

		var me = this;
		junoJQuery('#displayResolvedIssuesButton').click({junoJQuery: junoJQuery}, function(event)
		{
			me.displayResolvedIssues(event.data);
		});

		junoJQuery('#displayUnresolvedIssuesButton').click({junoJQuery: junoJQuery}, function(event)
		{
			me.displayUnresolvedIssues(event.data);
		});
	};
};
