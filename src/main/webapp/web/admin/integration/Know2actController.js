angular.module("Admin.Integration").controller('Admin.Integration.k2aController', [
	"$scope",
	"k2aService",
	function ($scope,
	          k2aService)
	{
		var controller = this;
		controller.checkStatus = function () {
			k2aService.isK2AInit().then(function (data) {
				console.log("data coming back", data);
				$scope.k2aActive = data.success;
				console.log($scope.k2aActive);
				if ($scope.k2aActive) {
					k2aService.getPreventionRulesList();
					k2aService.getCurrentPreventionRulesVersion();
				}
			});
		};
		console.log("Checking status");
		controller.checkStatus();

		$scope.availablePreventionRuleSets = [];
		$scope.currentPreventionRulesSet = "";

		controller.getPreventionRulesList = function () {
			k2aService.preventionRulesList().then(function (data) {
				console.log("data coming back", data);
				$scope.availablePreventionRuleSets = data;
				console.log("prev rules ", $scope.availablePreventionRuleSets);
			});
		};

		controller.getCurrentPreventionRulesVersion = function () {
			k2aService.getCurrentPreventionRulesVersion().then(function (data) {
				console.log("data coming back", data);
				$scope.currentPreventionRulesSet = data;
				console.log("prev rules ", $scope.availablePreventionRuleSets);
			});
		};

		$scope.loadPreventionRuleById = function (prevSet) {

			// if (confirm("<bean:message key="admin.k2a.confirmation"/>")) {
				console.log("prev", prevSet);
				// prevSet.agreement = "<bean:message key="admin.k2a.confirmation"/>";
				// k2aService.loadPreventionRuleById(prevSet).then(function (data) {
				// 	console.log("data coming back", data);
				// 	k2aService.getCurrentPreventionRulesVersion();
				// 	console.log("prev rules ", $scope.availablePreventionRuleSets);
				// });
			// }
		};

		$scope.PrevListQuantity = 10;

		$scope.increasePrevListQuantity = function () {
			$scope.PrevListQuantity = $scope.availablePreventionRuleSets.length;
		};

		$scope.initK2A = function () {
			// console.log($scope.clinicName);
			console.log("init k2a function");
			var clinic = {};
			// clinic.name = $scope.clinicName;
			k2aService.initK2A(clinic).then(function (data) {
				checkStatus();
			});
		}
	}
]);