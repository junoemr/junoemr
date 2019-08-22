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
			let action = BatchOperations.REPORT_NAME_ACTION_MAPPING[reportName];
			if (action !== undefined)
			{
				return action;
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

		// get demographics
		// returns null if the run is to be aborted, else the demographic numbers are returned
		getDemographics: function (all)
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
				return demos;
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
				BatchOperations.showErrorMessage("You do not have sufficient privileges to execute that action. Please contact an administrator.");
			}
			else if (data.status === 500)
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
				let demos = BatchOperations.getDemographics(all);
				if (demos != null)
				{
					let ok = confirm(demos.length + " Patients will have their status set to " + (deactivate ? "inactive" : "active") +". \nYou cannot undo this action. Are you sure you would like to proceed?");
					if (ok)
					{
						BatchOperations.activateDeactivateDemographics(deactivate, demos);
					}
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
				let demos = BatchOperations.getDemographics(all);
				if (demos != null)
				{
					let ok = confirm(demos.length + " Patients will be assigned Dx Code [" + code +"]\nYou cannot undo this action. Are you sure you would like to proceed?");
					if (ok)
					{
						BatchOperations.assignDxCodeToDemographics(code, codingSystem, demos);
					}
				}
			}
		}
	};


$(window).load(function ()
{
	// populate the report name -> action mapping
	BatchOperations.REPORT_NAME_ACTION_MAPPING = {

		// Population - Panel 1
		"Active Patients": [
			{func: BatchOperations.activateDeactivateDemographicsGenerator(true,false), 		name: "Deactivate Selected"},
			{func: BatchOperations.activateDeactivateDemographicsGenerator(false, false), 	name: "Activate Selected"},
			{func: BatchOperations.activateDeactivateDemographicsGenerator(true,true), 		name: "Deactivate All"},
			{func: BatchOperations.activateDeactivateDemographicsGenerator(false, true), 		name: "Activate All"}
		],

		// CDM - Panel 1
		"Consider Diabetes": [
			{func: BatchOperations.assignDxCodeGenerator('250', 'icd9', false), 	name: BatchOperations.dxAssignNameTemplate("Diabetes", false)},
			{func: BatchOperations.assignDxCodeGenerator('250', 'icd9', true), 	name: BatchOperations.dxAssignNameTemplate("Diabetes", true)}
		],
		"Consider Hypertension": [
			{func: BatchOperations.assignDxCodeGenerator('401', 'icd9', false), 	name: BatchOperations.dxAssignNameTemplate("Hypertension", false)},
			{func: BatchOperations.assignDxCodeGenerator('401', 'icd9', true), 	name: BatchOperations.dxAssignNameTemplate("Hypertension", true)}
		],
		"Consider Heart Failure": [
			{func: BatchOperations.assignDxCodeGenerator('428', 'icd9', false), 	name: BatchOperations.dxAssignNameTemplate("Heart Failure", false)},
			{func: BatchOperations.assignDxCodeGenerator('428', 'icd9', true), 	name: BatchOperations.dxAssignNameTemplate("Heart Failure", true)}
		],
		"Consider Chronic Pain": [
			{func: BatchOperations.assignDxCodeGenerator('338.2', 'icd9', false), name: BatchOperations.dxAssignNameTemplate("Chronic pain", false)},
			{func: BatchOperations.assignDxCodeGenerator('338.2', 'icd9', true), 	name: BatchOperations.dxAssignNameTemplate("Chronic pain", true)}
		],
		"Consider COPD": [
			{func: BatchOperations.assignDxCodeGenerator('496', 'icd9', false),	name: BatchOperations.dxAssignNameTemplate("COPD", false)},
			{func: BatchOperations.assignDxCodeGenerator('496', 'icd9', true), 	name: BatchOperations.dxAssignNameTemplate("COPD", true)}
		],

		// CDM - Panel 2
		"Consider Chronic Liver Disease": [
			{func: BatchOperations.assignDxCodeGenerator('571', 'icd9', false), 	name: BatchOperations.dxAssignNameTemplate("Chronic Liver Disease", false)},
			{func: BatchOperations.assignDxCodeGenerator('571', 'icd9', true), 	name: BatchOperations.dxAssignNameTemplate("Chronic Liver Disease", true)}
		],
		"Consider Ischemic Heart Disease": [
			{func: BatchOperations.assignDxCodeGenerator('414', 'icd9', false), 	name: BatchOperations.dxAssignNameTemplate("Ischemic Heart Disease", false)},
			{func: BatchOperations.assignDxCodeGenerator('414', 'icd9', true), 	name: BatchOperations.dxAssignNameTemplate("Ischemic Heart Disease", true)}
		],
		"Consider Advanced Care Planning": [
			{func: BatchOperations.assignDxCodeGenerator('V66', 'icd9', false), 	name: BatchOperations.dxAssignNameTemplate("Advanced Care Planning", false)},
			{func: BatchOperations.assignDxCodeGenerator('V66', 'icd9', true), 	name: BatchOperations.dxAssignNameTemplate("Advanced Care Planning", true)}
		],
		"Consider Frailty": [
			{func: BatchOperations.assignDxCodeGenerator('V15', 'icd9', false), 	name: BatchOperations.dxAssignNameTemplate("Frailty", false)},
			{func: BatchOperations.assignDxCodeGenerator('V15', 'icd9', true), 	name: BatchOperations.dxAssignNameTemplate("Frailty", true)}
		],
		"Consider Chronic Kidney Disease": [
			{func: BatchOperations.assignDxCodeGenerator('585', 'icd9', false), 	name: BatchOperations.dxAssignNameTemplate("Chronic Kidney Disease", false)},
			{func: BatchOperations.assignDxCodeGenerator('585', 'icd9', true), 	name: BatchOperations.dxAssignNameTemplate("Chronic Kidney Disease", true)}
		],
		"Consider Osteoarthritis": [
			{func: BatchOperations.assignDxCodeGenerator('715', 'icd9', false), 	name: BatchOperations.dxAssignNameTemplate("Osteoarthritis", false)},
			{func: BatchOperations.assignDxCodeGenerator('715', 'icd9', true), 	name: BatchOperations.dxAssignNameTemplate("Osteoarthritis", true)}
		],

		// CDM - Panel 3
		"Consider Alcohol Dependence Syndrome": [
			{func: BatchOperations.assignDxCodeGenerator('303', 'icd9', false), 	name: BatchOperations.dxAssignNameTemplate("Alcohol Dependence Syndrome", false)},
			{func: BatchOperations.assignDxCodeGenerator('303', 'icd9', true), 	name: BatchOperations.dxAssignNameTemplate("Alcohol Dependence Syndrome", true)}
		],
		"Consider Depression": [
			{func: BatchOperations.assignDxCodeGenerator('311', 'icd9', false), 	name: BatchOperations.dxAssignNameTemplate("Depression", false)},
			{func: BatchOperations.assignDxCodeGenerator('311', 'icd9', true), 	name: BatchOperations.dxAssignNameTemplate("Depression", true)}
		],
		"Consider Anxiety": [
			{func: BatchOperations.assignDxCodeGenerator('3000', 'icd9', false), 	name: BatchOperations.dxAssignNameTemplate("Anxiety", false)},
			{func: BatchOperations.assignDxCodeGenerator('3000', 'icd9', true), 	name: BatchOperations.dxAssignNameTemplate("Anxiety", true)}
		],
		"Consider Dementia": [
			{func: BatchOperations.assignDxCodeGenerator('290', 'icd9', false), 	name: BatchOperations.dxAssignNameTemplate("Dementia", false)},
			{func: BatchOperations.assignDxCodeGenerator('290', 'icd9', true), 	name: BatchOperations.dxAssignNameTemplate("Dementia", true)}
		],

		// CDM - PSP-Reports
		"Consider Drug Dependence Syndrome": [
			{func: BatchOperations.assignDxCodeGenerator('304', 'icd9', false), 	name: BatchOperations.dxAssignNameTemplate("Drug Dependence Syndrome", false)},
			{func: BatchOperations.assignDxCodeGenerator('304', 'icd9', true), 	name: BatchOperations.dxAssignNameTemplate("Drug Dependence Syndrome", true)}
		],
	};

	BatchOperations.populateBatchActionListItems();
})