import {SystemPreferenceApi} from "../../../generated/api/SystemPreferenceApi";

angular.module('Patient').component('addDemographicModal', {
	templateUrl: 'src/patient/addDemographicModal/addDemographicModal.jsp',
	bindings: {
		modalInstance: "<",
		resolve: "<",
	},
	controller:[
		'$scope',
		'$http',
		'$httpParamSerializer',
		'staticDataService',
		'demographicService',
		'programService',
		'providerService',
		function (
			$scope,
			$http,
			$httpParamSerializer,
			staticDataService,
			demographicService,
			programService,
			providerService)
	{
		let ctrl = this;
		ctrl.genders = staticDataService.getGenders();
		ctrl.provinces = staticDataService.getProvinces();
		ctrl.provincesCA = staticDataService.getCanadaProvinces();
		ctrl.newDemographicData = {};

		ctrl.systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer,
			'../ws/rs');

		// personal data
		ctrl.newDemographicData.lastName = "";
		ctrl.newDemographicData.firstName = "";
		ctrl.newDemographicData.sex = "";
		ctrl.newDemographicData.dateOfBirth = "";
		ctrl.newDemographicData.hin = "";
		ctrl.newDemographicData.ver = "";
		ctrl.newDemographicData.hcType = "BC";

		// address data
		ctrl.newDemographicData.address = {
			address: "",
			city: "",
			province: "BC",
			postal: "",
		};
		ctrl.newDemographicData.email = "";
		ctrl.newDemographicData.phone = "";

		// validation
		ctrl.invalidLastName = false;
		ctrl.invalidFirstName = false;
		ctrl.invalidSex = false;
		ctrl.invalidDob = false;

		//get programs to be selected
		programService.getPrograms().then(
			function success(results)
			{
				ctrl.programs = results;
				if (ctrl.programs.length === 1)
				{
					ctrl.newDemographicData.admissionProgramId = ctrl.programs[0].id;
				}
			},
			function error(errors)
			{
				console.log(errors);
			}
		);

		// Pull phone prefix from Oscar Properties file
		ctrl.systemPreferenceApi.getPropertyValue("phoneprefix", "").then(
			function success(results)
			{
				ctrl.newDemographicData.phone = results.data.body;
			},
			function error(errors)
			{
				console.log("errors::" + errors);
			}
		);

		// Pull default province, priority is user defined property then system-wide default HC if no property set
		ctrl.systemPreferenceApi.getPreferenceValue("HC_Type", "").then(
			function success(results)
			{
				if (results.data.body !== "")
				{
					ctrl.newDemographicData.address.province = results.data.body;
				}
				else
				{
					ctrl.systemPreferenceApi.getPropertyValue("hctype", "BC").then(
						function success(results)
						{
							ctrl.newDemographicData.address.province = results.data.body;
						},
						function error(errors)
						{
							console.log("errors::" + errors);
						}
					)
				}
			},
			function error(errors)
			{
				console.log("errors::" + errors);
			}
		);
		// set defaults based on provider settings
		providerService.getSettings().then(
				function success(result)
				{
					console.log(result);
					ctrl.newDemographicData.hcType = result.defaultHcType;
					ctrl.newDemographicData.sex = result.defaultSex;
				},
				function error(errors)
				{
					console.error("Failed to fetch Provider settings with error: " + errors);
				}
		);

		ctrl.validateDemographic = function ()
		{
			let valid = true;

			if (!ctrl.newDemographicData.lastName)
			{
				valid = false;
				ctrl.invalidLastName = true;
			}
			else
			{
				ctrl.invalidLastName = false;
			}

			if (!ctrl.newDemographicData.firstName )
			{
				valid = false;
				ctrl.invalidFirstName = true;
			}
			else
			{
				ctrl.invalidFirstName = false;
			}

			if (!ctrl.newDemographicData.sex)
			{
				valid = false;
				ctrl.invalidSex = true;
			}
			else
			{
				ctrl.invalidSex = false;
			}

			if (!ctrl.newDemographicData.dateOfBirth)
			{
				valid = false;
				ctrl.invalidDob = true;
			}
			else
			{
				ctrl.invalidDob = false;
			}

			return valid;
		};

		ctrl.onCancel = function()
		{
			ctrl.modalInstance.dismiss("cancel");
		};

		ctrl.onAdd = function ()
		{
			if (ctrl.validateDemographic())
			{
				ctrl.newDemographicData.dateOfBirth += "T00:00:00" + Juno.Common.Util.getUserISOTimezoneOffset();
				demographicService.saveDemographic(ctrl.newDemographicData).then(
					function success(results)
					{
						ctrl.modalInstance.close(results);
					},
					function error(errors)
					{
						console.error(errors);
					}
				);
			}
		}

	}]
});