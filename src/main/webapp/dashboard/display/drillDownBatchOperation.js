// functions that perform batch operations based on drill down results
BatchOperations =
	{
		// ====================== general functionality ======================= //
		OPERATION_TYPES:
		{
			DEACTIVATE_DEMO: "../../../ws/rs/batch/deactivate_demographics",
			ACTIVATE_DEMO: "../../../ws/rs/batch/activate_demographics"
		},

		// maps report name to batch operation functions
		REPORT_NAME_ACTION_MAPPING:
		{
			// populated in the onload handler at bottom of file
		},

		// user message banner
		BANNER_SUCCESS_COLOR: '#468847',
		BANNER_SUCCESS_BG_COLOR: 'rgb(169, 225, 174)',
		BANNER_ERROR_COLOR: '#fff6fb',
		BANNER_ERROR_BG_COLOR: '#b93e37',
		BANNER_FLASH_TIME: 4000,

		// post batch operation to server and return promise
		doBatchOperation: function (url, data)
		{
			if (url !== undefined)
			{
				return $.ajax({
					url: url,
					type: "POST",
					data: JSON.stringify(data),
					contentType: "application/json",
					dataType: 'json'
				});
			}
		},

		getReportActions: function (reportName)
		{
			let actionName = BatchOperations.REPORT_NAME_ACTION_MAPPING[reportName];
			if (actionName !== undefined)
			{
				return actionName;
			} else
			{
				return undefined;
			}
		},

		// populate the batch action list for this report
		populateBatchActionListItems: function ()
		{
			let reportTitle = $("#drill_down_report_title").html().trim();
			let batchOpMenu = $("#batch_operations_menu");
			let listItemTemplate = $("#batch_op_list_item_template");

			let actions = BatchOperations.getReportActions(reportTitle);

			if (actions !== undefined)
			{
				actions.forEach(function (action)
				{
					let newListItem = listItemTemplate.clone();
					newListItem.find("a").html(action.name);
					newListItem.find("a").click(action.func);
					newListItem.css("display", "block");
					batchOpMenu.append(newListItem);
				});
			} else
			{
				console.log("No action mappings for this report: '" + reportTitle + "'");
			}
		},

		// get a list of selected demographic numbers
		getCheckedDemographics: function ()
		{
			var demographics = [];
			$("input:checkbox.ticklerChecked").each(function ()
			{
				if (this.checked)
				{
					demographics.push(this.id);
				}
			});
			return demographics;
		},

		// get all demographics in report while respecting the current filter.
		getAllFilteredDemographics()
		{
			let demographicNoList = [];
			let idRows = $('#drilldownTable').DataTable().columns({
				order: 'current',
				page: 'all',
				search: 'applied'
			}).data()[1];
			idRows.forEach(function (idColumn)
			{
				demographicNoList.push($($.parseHTML(idColumn)).attr('id'));
			});

			return demographicNoList;
		},

		// show an success message to the user
		showSuccessMessage: function ()
		{
			let batchBanner = $("#batch_operation_banner");
			batchBanner.css("display", "flex");
			batchBanner.css("opacity", "1.0");
			batchBanner.css("color", BatchOperations.BANNER_SUCCESS_COLOR);
			batchBanner.css("background-color", BatchOperations.BANNER_SUCCESS_BG_COLOR);
			batchBanner.find("h4").html("Batch Operation Success!");

			BatchOperations.hideMessageBanner(batchBanner);
		},

		// show an error message to the user
		showErrorMessage: function (msg)
		{
			let batchBanner = $("#batch_operation_banner");
			batchBanner.css("display", "flex");
			batchBanner.css("opacity", "1.0");
			batchBanner.css("color", BatchOperations.BANNER_ERROR_COLOR);
			batchBanner.css("background-color", BatchOperations.BANNER_ERROR_BG_COLOR);
			batchBanner.find("h4").html("Error: </br>" + msg);

			BatchOperations.hideMessageBanner(batchBanner);
		},

		hideMessageBanner: function (batchBanner)
		{
			window.setTimeout(function () {
				batchBanner.css("opacity", "0.0");
			}, BatchOperations.BANNER_FLASH_TIME);

			window.setTimeout(function () {
				batchBanner.css("display", "none");
			}, BatchOperations.BANNER_FLASH_TIME + 2000);
		},

		showMessageForResponse: function (data)
		{
			if (data.status === "SUCCESS")
			{
				BatchOperations.showSuccessMessage();
			}
			else if (data.status === "ERROR")
			{
				BatchOperations.showErrorMessage(data.error.message);
			}
			else if (data.status === 403)
			{
				BatchOperations.showErrorMessage(JSON.parse(data.responseText).error.message);
			}
			else
			{
				BatchOperations.showErrorMessage("Unknown status returned from server: " + data.status);
			}
		},

		// ======================== batch operations ========================== //

		deactivateSelectedDemographics: function ()
		{
			return BatchOperations.activateDeactivateSelectedDemographics(true, BatchOperations.OPERATION_TYPES.DEACTIVATE_DEMO);
		},

		activateSelectedDemographics: function ()
		{
			return BatchOperations.activateDeactivateSelectedDemographics(false, BatchOperations.OPERATION_TYPES.ACTIVATE_DEMO);
		},

		// deactivate all selected demographics
		activateDeactivateSelectedDemographics: function (deactivate, url)
		{
			// do some thing
			let deactivateDemographics = BatchOperations.getCheckedDemographics();

			if (deactivateDemographics.length === 0)
			{
				alert("Please select at least one demographic");
			}
			else
			{
				let ok = confirm("Demographics: \n" + deactivateDemographics + "\nwill be set to " + (deactivate ? "inactive" : "active") + " \nAre you Sure?");
				if (ok)
				{
					let promise = BatchOperations.doBatchOperation(url, {
						operation: deactivate ? "deactivate" : "activate",
						demographicNumbers: deactivateDemographics
					});
					promise.always(function (data) {
						BatchOperations.showMessageForResponse(data);
					});
				}
			}
		},

		deactivateAllDemographics: function ()
		{
			BatchOperations.activateDeactivateAllDemographics(true, BatchOperations.OPERATION_TYPES.DEACTIVATE_DEMO);
		},

		activateAllDemographics: function ()
		{
			BatchOperations.activateDeactivateAllDemographics(false, BatchOperations.OPERATION_TYPES.ACTIVATE_DEMO);
		},

		activateDeactivateAllDemographics: function (deactivate, url)
		{
			let deactivateDemographics = BatchOperations.getAllFilteredDemographics();
			let ok = confirm("This will set, " + (deactivate ? "inactive" : "active") + " ALL, " + deactivateDemographics.length + " demographics in this report with respect to the currently applied filter\nAre you Sure?");
			if (ok)
			{
				let promise = BatchOperations.doBatchOperation(url, {
					operation: deactivate ? "deactivate" : "activate",
					demographicNumbers: deactivateDemographics
				});
				promise.always(function (data) {
					BatchOperations.showMessageForResponse(data);
				});
			}
		}
	};


$(window).load(function ()
{
	// populate the report name -> action mapping
	BatchOperations.REPORT_NAME_ACTION_MAPPING = {
		"Active Patients": [
			{func: BatchOperations.deactivateSelectedDemographics, 	name: "Deactivate Selected"},
			{func: BatchOperations.activateSelectedDemographics, 	name: "Activate Selected"},
			{func: BatchOperations.deactivateAllDemographics, 		name: "Deactivate All"},
			{func: BatchOperations.activateAllDemographics, 		name: "Activate All"}
		]
	}

	BatchOperations.populateBatchActionListItems();
})