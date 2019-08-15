// functions that perform batch operations based on drill down results
BatchOperations =
	{
		// ====================== general functionality ======================= //
		OPERATION_TYPES:
		{
			DEACTIVATE_DEMO: 	"../../../ws/rs/batch/deactivate_demographics",
			ACTIVATE_DEMO: 		"../../../ws/rs/batch/activate_demographics",
			ASSIGN_DX_CODE: 	"../../../ws/rs/batch/set_dx_code"
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

		// get demographics an confirm the run with the user.
		// returns null if the run is to be aborted, else the demographic number are returned
		getDemographics: function (all, actionMsg)
		{
			let demos = [];
			if (all)
			{
				demos = BatchOperations.getAllFilteredDemographics();
			}
			else
			{
				demos = BatchOperations.getCheckedDemographics();
			}

			if (demos.length > 0)
			{
				let ok = false;
				if (all)
				{
					ok = confirm("All " + demos.length + " Demographics " + actionMsg + " with respect to the currently applied filter\nAre you Sure?");
				}
				else
				{
					ok = confirm("Demographics: \n" + demos + "\n" + actionMsg +" \nAre you Sure?");
				}
				return ok ? demos : null;
			}
			else
			{
				alert("Please select at least one demographic");
				return null;
			}
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

		// helper to keep dx code assignment names consistent
		dxAssignNameTemplate: function (codeName, all)
		{
			if (all)
			{
				return "Assign " + codeName + "To All"
			}
			else
			{
				return "Assign " + codeName + " To Selected"
			}
		},

		// ======================== batch operations ========================== //

		// batch activate / deactivate demographics
		activateDeactivateDemographics: function(deactivate, demos)
		{
			if (deactivate)
			{
				let promise = BatchOperations.doBatchOperation(BatchOperations.OPERATION_TYPES.DEACTIVATE_DEMO, {
					operation: "deactivate",
					demographicNumbers: demos
				});
				promise.always(function (data) {
					BatchOperations.showMessageForResponse(data);
				});
			}
			else
			{
				let promise = BatchOperations.doBatchOperation(BatchOperations.OPERATION_TYPES.ACTIVATE_DEMO, {
					operation: "activate",
					demographicNumbers: demos
				});
				promise.always(function (data) {
					BatchOperations.showMessageForResponse(data);
				});
			}
		},

		// generates a function closure for activateDeactivateDemographics
		activateDeactivateDemographicsGenerator: function (deactivate, all)
		{
			return function () {
				let demos = BatchOperations.getDemographics(all, "Will be set to status, " + (deactivate ? 'inactive' : 'active'));
				if (demos != null)
				{
					BatchOperations.activateDeactivateDemographics(deactivate, demos);
				}
			}
		},

		// batch assign the specified dx code
		assignDxCodeToDemographics: function (code, codingSystem, demos)
		{
			let promise = BatchOperations.doBatchOperation(BatchOperations.OPERATION_TYPES.ASSIGN_DX_CODE, {
				operation: "assign Dx code",
				demographicNumbers: demos,
				dxCode: code,
				dxCodingSystem: codingSystem
			});
			promise.always(function (data){
				BatchOperations.showMessageForResponse(data);
			})
		},

		// generates a function closure for assignDxCodeToDemographics
		assignDxCodeGenerator: function(code, codingSystem, all)
		{
			return function () {
				let demos = BatchOperations.getDemographics(all, "Will be assigned Dx Code [" + code + "]");
				if (demos != null)
				{
					BatchOperations.assignDxCodeToDemographics(code, codingSystem, demos);
				}
			}
		}
	};


$(window).load(function ()
{
	// populate the report name -> action mapping
	BatchOperations.REPORT_NAME_ACTION_MAPPING = {
		"Active Patients": [
			{func: BatchOperations.activateDeactivateDemographicsGenerator(true,false), 		name: "Deactivate Selected"},
			{func: BatchOperations.activateDeactivateDemographicsGenerator(false, false), 	name: "Activate Selected"},
			{func: BatchOperations.activateDeactivateDemographicsGenerator(true,true), 		name: "Deactivate All"},
			{func: BatchOperations.activateDeactivateDemographicsGenerator(false, true), 		name: "Activate All"}
		],

		// CDM
		"Consider Diabetes": [
			{func: BatchOperations.assignDxCodeGenerator('250', 'icd9', false), 		name: BatchOperations.dxAssignNameTemplate("Diabetes", false)},
			{func: BatchOperations.assignDxCodeGenerator('250', 'icd9', true), 		name: BatchOperations.dxAssignNameTemplate("Diabetes", true)}
		],
		"Consider Hypertension": [
			{func: BatchOperations.assignDxCodeGenerator('401', 'icd9', false), 		name: BatchOperations.dxAssignNameTemplate("Hypertension", false)},
			{func: BatchOperations.assignDxCodeGenerator('401', 'icd9', true), 		name: BatchOperations.dxAssignNameTemplate("Hypertension", true)}
		],
		"Consider Heart Failure": [
			{func: BatchOperations.assignDxCodeGenerator('428', 'icd9', false), 		name: BatchOperations.dxAssignNameTemplate("Heart Failure", false)},
			{func: BatchOperations.assignDxCodeGenerator('428', 'icd9', true), 		name: BatchOperations.dxAssignNameTemplate("Heart Failure", true)}
		],
		"Consider Chronic Pain": [
			{func: BatchOperations.assignDxCodeGenerator('338.2', 'icd9', false), 	name: BatchOperations.dxAssignNameTemplate("Chronic pain", false)},
			{func: BatchOperations.assignDxCodeGenerator('338.2', 'icd9', true), 		name: BatchOperations.dxAssignNameTemplate("Chronic pain", true)}
		],
		"Consider COPD": [
			{func: BatchOperations.assignDxCodeGenerator('unknown', '???', false),	name: BatchOperations.dxAssignNameTemplate("COPD", false)},
			{func: BatchOperations.assignDxCodeGenerator('unknown', '???', true), 	name: BatchOperations.dxAssignNameTemplate("COPD", true)}
		]
	};

	BatchOperations.populateBatchActionListItems();
})