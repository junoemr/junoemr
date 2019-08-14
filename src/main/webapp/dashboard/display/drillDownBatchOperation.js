// functions that perform batch operations based on drill down results
BatchOperations =
	{
		// ====================== general functionality ======================= //
		OPERATION_TYPES:
		{
			DEACTIVATE_DEMO: "../../../ws/rs/batch/activate_deactivate_demographics"
		},

		// maps report name to batch operation functions
		REPORT_NAME_ACTION_MAPPING:
		{
			// populated in the onload handler at bottom of file
		},

		// post batch operation to server and return promise
		doBatchOperation: function (url, data)
		{
			if (url !== undefined)
			{
				$.ajax({
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

		// ======================== batch operations ========================== //

		deactivateSelectedDemographics: function ()
		{
			return BatchOperations.activateDeactivateSelectedDemographics(true);
		},

		activateSelectedDemographics: function ()
		{
			return BatchOperations.activateDeactivateSelectedDemographics(false);
		},

		// deactivate all selected demographics
		activateDeactivateSelectedDemographics: function (deactivate)
		{
			// do some thing
			let deactivateDemographics = BatchOperations.getCheckedDemographics();

			if (deactivateDemographics.length === 0)
			{
				alert("Please select at least one demographic");
			}
			else
			{
				let ok = confirm("Demographics: \n" + deactivateDemographics + "\n will be set to " + (deactivate ? "inactive" : "active"));
				if (ok)
				{
					BatchOperations.doBatchOperation(BatchOperations.OPERATION_TYPES.DEACTIVATE_DEMO, {
						operation: deactivate ? "deactivate" : "activate",
						demographicNumbers: deactivateDemographics
					})
				}
			}
		},

		deactivateAllDemographics: function ()
		{
			BatchOperations.activateDeactivateAllDemographics(true);
		},

		activateAllDemographics: function ()
		{
			BatchOperations.activateDeactivateAllDemographics(false);
		},

		activateDeactivateAllDemographics: function (deactivate)
		{
			let deactivateDemographics = BatchOperations.getAllFilteredDemographics();
			let ok = confirm("This will set, " + (deactivate ? "inactive" : "active") + " ALL demographics in this report with respect to the currently applied filter");
			if (ok)
			{
				BatchOperations.doBatchOperation(BatchOperations.OPERATION_TYPES.DEACTIVATE_DEMO, {
					operation: deactivate ? "deactivate" : "activate",
					demographicNumbers: deactivateDemographics
				});
			}
		}
	}


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