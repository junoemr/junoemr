'use strict';

if (!window.Juno) window.Juno = {};
if (!Juno.OscarEncounter) Juno.OscarEncounter = {};
if (!Juno.OscarEncounter.JunoEncounter) Juno.OscarEncounter.JunoEncounter = {};

if (!Juno.OscarEncounter.JunoEncounter.NoteFilter) Juno.OscarEncounter.JunoEncounter.NoteFilter =
	function NoteFilter(pageData, pageState, encounterNote)
{
	this.pageData = pageData;
	this.pageState = pageState;
	this.encounterNote = encounterNote;

	//var filterShows = false;
	//var filterSort = null;
	//var filteredProviders = [];
	//var filteredRoles = [];
	//var filteredIssues = [];

	this.showFilter = function showFilter()
	{

		if (this.pageState.filterShows)
		{
			new Effect.BlindUp('filter');
		}
		else
		{
			new Effect.BlindDown('filter');
		}

		this.pageState.filterShows = !this.pageState.filterShows;
	};

	this.hideFilter = function hideFilter()
	{
		if(this.pageState.filterShows)
		{
			junoJQuery('#filter').hide();
			this.pageState.filterShows = false;
		}
	};

	// Clears other boxes if 'All' is checked and vice-versa
	this.filterCheckBox = function filterCheckBox(checkbox)
	{
		var checks = document.getElementsByName(checkbox.name);

		if (checkbox.value == "a" && checkbox.checked)
		{

			for (var idx = 0; idx < checks.length; ++idx)
			{
				if (checks[idx] != checkbox)
					checks[idx].checked = false;
			}
		}
		else
		{
			for (var idx = 0; idx < checks.length; ++idx)
			{
				if (checks[idx].value == "a")
				{
					if (checks[idx].checked)
						checks[idx].checked = false;

					break;
				}
			}
		}

	};

	this.resetInputElements = function resetInputElements(element)
	{
		if (Object.prototype.toString.call(element) == "[object RadioNodeList]")
		{
			var size = element.length;
			for (var i = 0; i < size; i++)
			{
				element[i].checked = false;
			}
		}
		else
		{
			element.checked = false;
		}
	};

	this.filter = function filter(reset)
	{
		if (reset)
		{
			this.resetInputElements(document.forms["caseManagementViewForm"].filter_providers);
			this.resetInputElements(document.forms["caseManagementViewForm"].filter_roles);
			this.resetInputElements(document.forms["caseManagementViewForm"].note_sort);
			this.resetInputElements(document.forms["caseManagementViewForm"].filter_issues);
		}


		// Clear filters
		this.pageState.filterSort = null;
		this.pageState.filteredProviders = [];
		this.pageState.filteredRoles = [];
		this.pageState.filteredIssues = [];
		this.pageState.notesOffset = 0;

		var me = this;
		junoJQuery("input[name=filter_providers]:checked").each(function(index, object)
		{
			if(object.value === 'a')
			{
				return;
			}

			me.pageState.filteredProviders.push(object.value);
		});

		junoJQuery("input[name=filter_roles]:checked").each(function(index, object)
		{
			if(object.value === 'a')
			{
				return;
			}

			me.pageState.filteredRoles.push(object.value);
		});

		junoJQuery("input[name=filter_issues]:checked").each(function(index, object)
		{
			if(object.value === 'a')
			{
				return;
			}

			me.pageState.filteredIssues.push(object.value);
		});

		var note_sort = junoJQuery("input[name=note_sort]:checked");

		if(note_sort.val())
		{
			me.pageState.filterSort = note_sort.val();
		}

		// Clear current encounter notes
		this.encounterNote.clearTmpSaveTimer();
		this.encounterNote.clearNotes();

		if(pageState.notesScrollCheckInterval != null)
		{
			clearInterval(pageState.notesScrollCheckInterval);
		}

		// Load updated encounter notes
		var me = this;
		this.encounterNote.notesLoader(pageData.contextPath, 0, this.pageData.notesIncrement * 2, pageData.demographicNo, true).then(function ()
		{
			pageState.notesOffset += (me.pageData.notesIncrement * 2);

			pageState.notesScrollCheckInterval = setInterval(function ()
			{
				me.encounterNote.notesIncrementAndLoadMore(pageData.demographicNo)
			}, 50);

			me.displaySelectedFilters();

			me.hideFilter();
		});

		return false;
	};

	this.displaySelectedFilters = function displaySelectedFilters()
	{
		this.showFiltersOfType(
			"appliedFiltersProviders",
			this.pageState.filteredProviders,
			"#filter_provider_name"
		);
		this.showFiltersOfType(
			"appliedFiltersRoles",
			this.pageState.filteredRoles,
			"#filter_role_name"
		);
		this.showFiltersOfType(
			"appliedFiltersSort",
			[this.pageState.filterSort],
			"#filter_sort_name"
		);
		this.showFiltersOfType(
			"appliedFiltersIssues",
			this.pageState.filteredIssues,
			"#filter_issue_name"
		);
	};

	this.showFiltersOfType = function showFiltersOfType(containerDivId, valueArray, textIdPrefix)
	{
		// Clear existing filters
		var containerDiv = junoJQuery("#" + containerDivId + "Content");

		containerDiv.empty();

		if(!junoJQuery.isArray(valueArray) || valueArray.length === 0 ||
			(valueArray.length === 1 && !valueArray[0])
		)
		{
			junoJQuery("#" + containerDivId).hide();
			return;
		}

		junoJQuery("#" + containerDivId).show();

		junoJQuery.each(valueArray, function(index, value)
		{
			var value = junoJQuery(textIdPrefix + value).text().trim();

			containerDiv.append(value + "<br />");
		});

	};

	this.selectIssueFilterValue = function selectIssueFilterValue(value)
	{
		junoJQuery("input[name='filter_issues'][value='" + value + "']").prop("checked", true);
	}
};
