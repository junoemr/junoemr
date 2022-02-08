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
		'$uibModal',
		'securityService',
		'staticDataService',
		'demographicService',
		'providerService',
		function (
			$scope,
			$http,
			$httpParamSerializer,
			$timeout,
			$uibModal,
			securityService,
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
				providerNo: null,
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
					label: prov.value
				}
			});

			// User configurable defaults.
			ctrl.defaultProvince = null;
			ctrl.defaultSex = null;
			ctrl.defaultPhoneNumber = null;

			ctrl.preferredPhoneType = PHONE_TYPE.CELL;
			ctrl.preferredPhoneNumber = "";

			ctrl.preferredPhoneOptions = [
				{
					value: PHONE_TYPE.HOME,
					label: "Home",
				},
				{
					value: PHONE_TYPE.CELL,
					label: "Mobile",
				},
				{
					value: PHONE_TYPE.WORK,
					label: "Work",
				}
			]

			ctrl.firstNamePristine = true;
			ctrl.lastNamePristine = true;
			ctrl.genderPristine = true;

			ctrl.$onInit = () :void =>
			{
				// Pull phone prefix from Oscar Properties file
				ctrl.systemPreferenceApi.getPreferenceValue("phone_prefix", "").then(
					function success(results)
					{
						ctrl.defaultPhoneNumber = results.data.body;
					},
					function error(errors)
					{
						console.error("errors::" + errors);
					}
				);

				ctrl.providersServiceApi.getBySecurityRole("doctor").then(
					function success(data) {
						ctrl.mrpOptions = data.data.body.map((doc) => {return {label: doc.name, value: doc.providerNo}});
						ctrl.mrpOptions.push({label: "--", value: null})
					}
				);

				// set defaults based on provider settings
				providerService.getSettings().then(
					function success(result)
					{
						ctrl.defaultSex = result.defaultSex;

						// If the user doesn't have a HC type pre-set, pull from system-wide setting
						if (result.defaultHcType === "")
						{
							ctrl.systemPreferenceApi.getPropertyValue("hctype", "BC").then(
								function success(results)
								{
									ctrl.defaultProvince = results.data.body;
									ctrl.resetToDefaults();
								},
								function error(errors)
								{
									console.error("Failed to fetch system properties with error:" + errors);
								}
							)
						}
						else
						{
							ctrl.defaultProvince = result.defaultHcType;
							ctrl.resetToDefaults();
						}
					},
					function error(errors)
					{
						console.error("Failed to fetch Provider settings with error: " + errors);
					}
				);
			}

			ctrl.resetToDefaults = () :void =>
			{
				ctrl.newDemographicData = angular.copy(ctrl.emptyDemographicData);

				ctrl.firstNamePristine = true;
				ctrl.lastNamePristine = true;
				ctrl.genderPristine = true;

				ctrl.newDemographicData.sex = ctrl.defaultSex;
				ctrl.newDemographicData.hcType = ctrl.defaultProvince;
				ctrl.newDemographicData.address.province = ctrl.defaultProvince;

				ctrl.preferredPhoneNumber = ctrl.defaultPhoneNumber;
				ctrl.preferredPhoneType = PHONE_TYPE.CELL;
			}

			ctrl.resetFocus = () :void =>
			{
				$timeout(() =>
				{
					ctrl.firstColumnRef.find(":input:visible:first").focus();
				});
			}

			ctrl.validateDemographic = () :void =>
			{
				let dateOfBirthValid = Juno.Common.Util.getDateMoment(ctrl.newDemographicData.dateOfBirth).isValid();

				return dateOfBirthValid &&
					ctrl.newDemographicData.lastName &&
					ctrl.newDemographicData.firstName &&
					ctrl.newDemographicData.sex
			};

			ctrl.firstNameValid = () :boolean =>
			{
				return ctrl.newDemographicData.firstName || ctrl.firstNamePristine;
			}


			ctrl.lastNameValid = () :boolean =>
			{
				return ctrl.newDemographicData.lastName || ctrl.lastNamePristine;
			}


			ctrl.genderValid = () :boolean =>
			{
				return ctrl.newDemographicData.sex || ctrl.genderPristine;
			}

			ctrl.onFirstNameChange = () :void =>
			{
				ctrl.firstNamePristine = false;
			}

			ctrl.onLastNameChange = () :void =>
			{
				ctrl.lastNamePristine = false;
			}

			ctrl.onGenderChange = (value :object) :void =>
			{
				ctrl.newDemographicData.sex = value;
				ctrl.genderPristine = false;
			}

			ctrl.onHcTypeChange = (value :object) :void =>
			{
				ctrl.newDemographicData.hcType = value;
			}

			ctrl.onMRPChange = (value :object) :void =>
			{
				ctrl.newDemographicData.providerNo = value;
			}

			ctrl.onPreferredPhoneTypeChange = (value :object) :void =>
			{
				ctrl.preferredPhoneType = value;
			}

			ctrl.onCancel = () :void =>
			{
				ctrl.modalInstance.dismiss("cancel");
			};

			ctrl.finalizePhoneNumber = () :void =>
			{
				// Reset the all phone numbers in case the first attempt to save failed
				ctrl.newDemographicData.phone = "";
				ctrl.newDemographicData.alternativePhone = "";
				ctrl.newDemographicData.extras = [];

				const preferredPhone = ctrl.preferredPhoneNumber + "*";

				switch (ctrl.preferredPhoneType) {
					case PHONE_TYPE.HOME:
						ctrl.newDemographicData.phone = preferredPhone;
						break;
					case PHONE_TYPE.WORK:
						ctrl.newDemographicData.alternativePhone = preferredPhone;
						break;
					case PHONE_TYPE.CELL:
						const demoExt = {
							id: null,
							key: "demo_cell",
							value: preferredPhone,
							providerNo: securityService.getUser().providerNo,
							dateCreated: Juno.Common.Util.getDateMoment(new Date()),
							demographicNo: null
						}
						ctrl.newDemographicData.extras.push(demoExt);
						break;
				}
			}

			ctrl.finalizeHin = () :void =>
			{
				ctrl.newDemographicData.hin = ctrl.newDemographicData.hin.replace(/[\W_]/gi, '');
			}

			ctrl.finalizeStatusDates = ()  :void =>
			{
				const now = Juno.Common.Util.getDateMoment(new Date());
				ctrl.newDemographicData.dateJoined = now;
				ctrl.newDemographicData.patientStatusDate = now;
			}

			ctrl.openSwipecardModal = () :void =>
			{
				var modalInstance = $uibModal.open(
						{
							templateUrl: 'src/record/details/swipecard.jsp',
							controller: 'Record.Details.SwipecardController as swipecardController',
							backdrop: 'static',
							windowClass: 'juno-modal',
						});
				modalInstance.result.then(
						// the object passed back on closing
						function success(cardInfo)
						{
							ctrl.fillDataFromSwipecard(cardInfo.data);
						});
			}

			ctrl.fillDataFromSwipecard = (data :any) :void =>
			{
				if (data.address)
				{
					ctrl.newDemographicData.address.address = data.address;
				}

				if (data.city)
				{
					ctrl.newDemographicData.address.city = data.city;
				}

				if (data.province)
				{
					ctrl.newDemographicData.address.province = data.province;
					ctrl.hcType = data.province;
				}

				if (data.postal)
				{
					ctrl.newDemographicData.address.postal = data.postal;
				}

				if (data.firstName)
				{
					ctrl.newDemographicData.firstName = data.firstName;
				}

				if (data.lastName)
				{
					ctrl.newDemographicData.lastName = data.lastName;
				}

				if (data.sex)
				{
					ctrl.newDemographicData.sex = data.sex;
				}

				if (data.hin)
				{
					ctrl.newDemographicData.hin = data.hin;
				}

				if (data.versionCode)
				{
					ctrl.newDemographicData.ver = data.versionCode;
				}

				let dateOfBirth = Juno.Common.Util.getDateMomentFromComponents(data.dobYear, data.dobMonth, data.dobDay)
				if (dateOfBirth.isValid())
				{
					ctrl.newDemographicData.dateOfBirth = dateOfBirth.format('YYYY-MM-DD');
				}
			}

			ctrl.submitOnCtrlEnter = ($event :any) :void =>
			{
				if ($event.ctrlKey && $event.keyCode === 13)
				{
					if (!ctrl.buttonClicked)
					{
						ctrl.onAdd();
					}
				}
			}

			ctrl.onAdd = () :void =>
			{
				ctrl.buttonClicked = true;

				ctrl.finalizeStatusDates();

				if (ctrl.preferredPhoneNumber)
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
							ctrl.resetToDefaults();
							ctrl.resetFocus();
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
					ctrl.toastService.errorToast("Demographic is missing required fields (*)");
					ctrl.buttonClicked = false;
				}
			}
		}]
});