angular.module("Admin.Integration").controller('Admin.Integration.k2aConfigController', [
	"$scope",
	"k2aService",
	function ($scope,
	          k2aService)
	{
		var controller = this;
		controller.checkStatus = function () {
			console.info("Checking status");
			k2aService.isK2AInit().then(
				function success(data) {
					console.log("k2aActive", data);
					controller.k2aActive = data;
					console.log(controller.k2aActive);
					if (controller.k2aActive) {
						controller.getPreventionRulesList();
						controller.getCurrentPreventionRulesVersion();
					}
				},
				function failure(error) {
					console.error(error);
					alert("Failed to verify K2A Status");
				}
			);
		};
		controller.checkStatus();

		controller.availablePreventionRuleSets = [];
		controller.currentPreventionRulesSet = [];

		controller.getPreventionRulesList = function () {
			k2aService.preventionRulesList().then(
				function success(data) {
					controller.availablePreventionRuleSets = data;
					console.log("prev rules ", controller.availablePreventionRuleSets);
				},
				function failure(error) {
					console.error(error);
				}
			);
		};

		controller.getCurrentPreventionRulesVersion = function () {
			k2aService.getCurrentPreventionRulesVersion().then(
				function success(data) {
					console.log("currentPreventionRulesSet", data);
					controller.currentPreventionRulesSet = data;
				},
				function failure(error) {
					console.error(error);
				}
			);
		};

		controller.loadPreventionRuleById = function (prevSet) {

			// if (confirm("<bean:message key="admin.k2a.confirmation"/>")) {
				console.log("loadPreventionRuleById", prevSet);
				// prevSet.agreement = "<bean:message key="admin.k2a.confirmation"/>";
				k2aService.loadPreventionRuleById(prevSet).then(function (data) {
					console.log("data coming back", data);
					k2aService.getCurrentPreventionRulesVersion();
					console.log("prev rules ", controller.availablePreventionRuleSets);
				});
			// }
		};

		controller.PrevListQuantity = 10;

		controller.increasePrevListQuantity = function () {
			controller.PrevListQuantity = controller.availablePreventionRuleSets.length;
		};

		controller.initK2A = function () {
			console.log("init k2a function", $scope.clinicName);
			k2aService.initK2A($scope.clinicName).then(
				function success(response) {
					console.info("Init complete, check status", response);
					controller.checkStatus();
				},
				function failure(error) {
					console.log(error);
					alert("Failed to initialize K2A");
				}
			);
		}
	}
]);