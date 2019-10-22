
angular.module('Patient').component('addDemographicModal', {
	templateUrl: 'src/patient/addDemographicModal/addDemographicModal.jsp',
	bindings: {
		modalInstance: "<",
		resolve: "<",
	},
	controller:[ '$scope', 'staticDataService', function ($scope, staticDataService)
	{
		let ctrl = this;
		ctrl.genders = staticDataService.getGenders();
		ctrl.provinces = staticDataService.getProvinces();
		ctrl.provincesCA = staticDataService.getCanadaProvinces();
		ctrl.newDemographicData = {};

		// personal data
		ctrl.newDemographicData.lastName = "";
		ctrl.newDemographicData.firstName = "";
		ctrl.newDemographicData.gender = ctrl.genders ? ctrl.genders[0] : "U";
		ctrl.newDemographicData.dob = "";
		ctrl.newDemographicData.hin = "";
		ctrl.newDemographicData.hinVer = "";
		ctrl.newDemographicData.hinType = "BC";

		// address data
		ctrl.newDemographicData.address = "";
		ctrl.newDemographicData.city = "";
		ctrl.newDemographicData.province = "BC";
		ctrl.newDemographicData.postalCode = "";
		ctrl.newDemographicData.email = "";
		ctrl.newDemographicData.phone = "";

		ctrl.saveDemographic = function ()
		{
			// some thing.
		};

		ctrl.onCancel = function()
		{
			ctrl.modalInstance.dismiss("cancel");
		};

		ctrl.onAdd = function ()
		{
			ctrl.saveDemographic();
			ctrl.modalInstance.close(ctrl.newDemographicData);
		}

	}]
});