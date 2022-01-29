import {SystemPreferenceApi} from "../../../generated/api/SystemPreferenceApi";
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, LABEL_POSITION, JUNO_STYLE} from "../../common/components/junoComponentConstants";
import {ProvidersServiceApi} from "../../../generated";
import ToastService from "../../lib/alerts/service/ToastService";

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
		'$timeout',
		'staticDataService',
		'demographicService',
		'providerService',
		function (
			$scope,
			$http,
			$httpParamSerializer,
			$timeout,
			staticDataService,
			demographicService,
			providerService,
		)
		{
			let ctrl = this;

			ctrl.LABEL_POSITION = LABEL_POSITION.TOP;
			ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

			ctrl.systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer,
				'../ws/rs');
			ctrl.providersServiceApi = new ProvidersServiceApi($http, $httpParamSerializer,
				"../ws/rs");
			ctrl.toastService = new ToastService();

			ctrl.genders = staticDataService.getGenders();
			ctrl.provinces = staticDataService.getProvinces();
			ctrl.provincesCA = staticDataService.getCanadaProvinces();
			ctrl.mrpOptions = [];

			// personal data
			ctrl.emptyDemographicData = {
				lastName: "",
				firstName: "",
				sex: "",
				dateOfBirth: "",
				address: {
					address: "",
					city: "",
					province: "BC",
					postal: "",
				},
				email: "",
				phone: "",
				hin: "",
				ver: "",
				hcType: "BC",
				providerNo: "",
				dateJoined: Juno.Common.Util.getDateMoment(new Date()),
				patientStatusDate: Juno.Common.Util.getDateMoment(new Date())
			}

			ctrl.newDemographicData = {};
			ctrl.isCreateAnotherEnabled = false;

			// validation
			ctrl.invalidLastName = false;
			ctrl.invalidFirstName = false;
			ctrl.invalidSex = false;
			ctrl.invalidDob = false;

			ctrl.buttonClicked = false;

			ctrl.hcTypeProvs = ctrl.provincesCA.map(prov =>
			{
				return {
					value: prov.value,
					label: prov.label,
					shortLabel: prov.value
				}
			});

			ctrl.$onInit = () =>
			{
				ctrl.resetDemographic();

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

				ctrl.providersServiceApi.getBySecurityRole("doctor").then(
					function success(data) {
						ctrl.mrpOptions = data.data.body.map((doc) => {return {label: doc.name, value: doc.providerNo}});
						ctrl.mrpOptions.push({label: "--", value: ""})
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

			ctrl.resetDemographic = () =>
			{
				ctrl.newDemographicData = angular.copy(ctrl.emptyDemographicData);
			}

			ctrl.resetFocus = () =>
			{
				$timeout(() =>
				{
					ctrl.formRef.find(":input:visible:first").focus();
				});
			}

			ctrl.validateDemographic = function ()
			{
				let dateOfBirthValid = Juno.Common.Util.getDateMoment(ctrl.newDemographicData.dateOfBirth).isValid();

				return dateOfBirthValid &&
					ctrl.newDemographicData.lastName &&
					ctrl.newDemographicData.firstName &&
					ctrl.newDemographicData.sex
			};

			ctrl.onHcTypeChange = (value) =>
			{
				ctrl.newDemographicData.hcType = value;
			}

			ctrl.onMRPChange = (value) =>
			{
				ctrl.newDemographicData.mrp = value;
			}

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
						ctrl.toastService.successToast("Demographic Saved");

						if (ctrl.isCreateAnotherEnabled)
						{
							ctrl.resetDemographic();
						}
						else
						{
							ctrl.modalInstance.close(results);
						}
					})
					.catch((errors) =>
					{
						ctrl.toastService.errorToast("Unable to save demographic");
						console.error(errors);
					})
					.finally(() =>
					{
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