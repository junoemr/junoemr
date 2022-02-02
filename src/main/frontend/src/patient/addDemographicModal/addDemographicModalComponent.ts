import {Demographic, DemographicTo1, SystemPreferenceApi} from "../../../generated";
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, LABEL_POSITION, JUNO_STYLE} from "../../common/components/junoComponentConstants";
import {ProvidersServiceApi} from "../../../generated";
import ToastService from "../../lib/alerts/service/ToastService";

enum PHONE_TYPE {
	HOME = "HOME",
	WORK = "WORK",
	CELL = "CELL"
}

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
		'$stateParams',
		'staticDataService',
		'demographicService',
		'providerService',
		function (
			$scope,
			$http,
			$httpParamSerializer,
			$timeout,
			$stateParams,
			staticDataService,
			demographicService,
			providerService,
		)
		{
			let ctrl = this;

			ctrl.LABEL_POSITION = LABEL_POSITION.TOP;
			ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
			ctrl.COMPONENT_STYLE = JUNO_STYLE.DEFAULT;

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
				alternativePhone: "",
				hin: "",
				ver: "",
				hcType: "BC",
				providerNo: "",
				extras: [],
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

			ctrl.preferredPhoneType = PHONE_TYPE.HOME;
			ctrl.preferredPhone = null;

			ctrl.preferredPhoneOptions = [
				{
					value: PHONE_TYPE.HOME,
					label: "Home Phone",
					shortLabel: "Home",

				},
				{
					value: PHONE_TYPE.CELL,
					label: "Mobile Phone",
					shortLabel: "Mobile",
				},
				{
					value: PHONE_TYPE.WORK,
					label: "Work Phone",
					shortLabel: "Work",
				}
			]

			ctrl.$onInit = () =>
			{
				ctrl.resetDemographic();
				ctrl.preferredPhone = "";
				ctrl.preferredPhoneType = PHONE_TYPE.HOME;

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
				console.log("hc changed");
				ctrl.newDemographicData.hcType = value;
				console.log(ctrl.newDemographicData.hcType);
			}

			ctrl.onMRPChange = (value) =>
			{
				ctrl.newDemographicData.mrp = value;
			}

			ctrl.onPreferredPhoneTypeChange = (value) =>
			{
				console.log("phone number changed");
				ctrl.preferredPhoneType = value;
				console.log(ctrl.preferredPhoneType);
			}

			ctrl.onCancel = () =>
			{
				ctrl.modalInstance.dismiss("cancel");
			};

			ctrl.finalizePhoneNumber = () =>
			{
				const preferredPhone = ctrl.preferredPhone + "*";

				switch (ctrl.preferredPhoneType)
				{
					case PHONE_TYPE.HOME:
						ctrl.newDemographicData.phone = preferredPhone;
						break;
					case PHONE_TYPE.WORK:
						ctrl.newDemographicData.alternativePhone = preferredPhone;
						break;
					case PHONE_TYPE.CELL:
						ctrl.newDemographicData.extras.push({
							name: "demo_cell",
							value: preferredPhone,
							providerNo: $stateParams.providerNo,
							demographicNo: null
						})
				}
			}

			ctrl.finalizeHin = () =>
			{
				ctrl.newDemographicData.hin = ctrl.newDemographicData.hin.replace(/[\W_]/gi, '');
			}

			ctrl.finalizeStatusDates = () =>
			{
				const now = Juno.Common.Util.getDateMoment(new Date());
				ctrl.newDemographicData.dateJoined = now;
				ctrl.newDemographicData.patientStatusDate = now;
			}

			ctrl.onAdd = function ()
			{
				ctrl.buttonClicked = true;

				ctrl.finalizeStatusDates();

				if (ctrl.newDemographicData.preferredPhone)
				{
					ctrl.finalizePhoneNumber();
				}

				if (ctrl.newDemographicData.hin)
				{
					ctrl.finalizeHin();
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
