import {SystemPreferenceApi} from "../../../generated/api/SystemPreferenceApi";
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN} from "../../common/components/junoComponentConstants";

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
		ctrl.newDemographicData.dateJoined = Juno.Common.Util.getDateMoment(new Date());
		ctrl.newDemographicData.patientStatusDate = Juno.Common.Util.getDateMoment(new Date());

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

		ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
		ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
		ctrl.buttonClicked = false;
		ctrl.focusField = "lastName";

		ctrl.$onInit = () =>
		{
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
			ctrl.systemPreferenceApi.getPreferenceValue("phone_prefix", "").then(
				function success(results)
				{
					ctrl.newDemographicData.phone = results.data.body;
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
					ctrl.newDemographicData.sex = result.defaultSex;

					// If the user doesn't have a HC type pre-set, pull from system-wide setting
					if (result.defaultHcType === "")
					{
						ctrl.systemPreferenceApi.getPropertyValue("hctype", "BC").then(
							function success(results)
							{
								ctrl.newDemographicData.address.province = results.data.body;
							},
							function error(errors)
							{
								console.log("Failed to fetch system properties with error:" + errors);
							}
						)
					}
					else
					{
						ctrl.newDemographicData.hcType = result.defaultHcType;
						ctrl.newDemographicData.address.province = result.defaultHcType;
					}
				},
				function error(errors)
				{
					console.error("Failed to fetch Provider settings with error: " + errors);
				}
			);
		}

		ctrl.validateDemographic = function ()
		{
			let dateOfBirthValid = Juno.Common.Util.getDateMoment(ctrl.newDemographicData.dateOfBirth).isValid();

			ctrl.invalidLastName = !ctrl.newDemographicData.lastName;
			ctrl.invalidFirstName = !ctrl.newDemographicData.firstName;
			ctrl.invalidSex = !ctrl.newDemographicData.sex;
			ctrl.invalidDob = !dateOfBirthValid;

			return !(ctrl.invalidLastName || ctrl.invalidFirstName || ctrl.invalidSex || ctrl.invalidDob);
		};

		ctrl.onCancel = function()
		{
			ctrl.modalInstance.dismiss("cancel");
		};

		ctrl.onAdd = function ()
		{
			ctrl.buttonClicked = true;

			if (Juno.Common.Util.exists(ctrl.newDemographicData.hin))
			{
				ctrl.newDemographicData.hin = ctrl.newDemographicData.hin.replace(/[\W_]/gi, '');
			}

			if (ctrl.validateDemographic())
			{
				demographicService.saveDemographic(ctrl.newDemographicData)
					.then((results) =>
					{
						ctrl.modalInstance.close(results);
					})
					.catch((errors) =>
					{
						alert(errors);
						console.error(errors);
						ctrl.buttonClicked = false;
					})
			}
			else // Need this to reset button if validation fails
			{
				ctrl.buttonClicked = false;
			}
		}
	}]
});