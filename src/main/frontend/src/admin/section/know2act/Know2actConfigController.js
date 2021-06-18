angular.module("Admin.Section.Know2act").controller('Admin.Section.Know2act.k2aConfigController', [
	"k2aService",
	function (k2aService)
	{
		var controller = this;

		controller.PrevListQuantity = 10;
		controller.availablePreventionRuleSets = [];
		controller.currentPreventionRulesSet = [];
		controller.clinicName = "";
		controller.k2aActive = false;

		controller.checkStatus = function ()
		{
			k2aService.isK2AInit().then(
				function success(data)
				{
					controller.k2aActive = data;
					if (controller.k2aActive)
					{
						controller.getPreventionRulesList();
						controller.getCurrentPreventionRulesVersion();
					}
				},
				function failure(error)
				{
					console.error(error);
					alert("Failed to verify K2A Status");
				}
			);
		};
		controller.checkStatus();

		controller.getPreventionRulesList = function ()
		{
			k2aService.preventionRulesList().then(
				function success(data)
				{
					controller.availablePreventionRuleSets = data;
					console.log("availablePreventionRuleSets", controller.availablePreventionRuleSets);
				},
				function failure(error)
				{
					console.error(error);
				}
			);
		};

		controller.getCurrentPreventionRulesVersion = function ()
		{
			k2aService.getCurrentPreventionRulesVersion().then(
				function success(data)
				{
					controller.currentPreventionRulesSet = data;
					console.log("currentPreventionRulesSet", data);
				},
				function failure(error)
				{
					console.error(error);
				}
			);
		};

		controller.loadPreventionRuleById = function (prevSet)
		{

			// if (confirm("<bean:message key="admin.k2a.confirmation"/>")) {
			// prevSet.agreement = "<bean:message key="admin.k2a.confirmation"/>";
			k2aService.loadPreventionRuleById(prevSet).then(function (data)
			{
				k2aService.getCurrentPreventionRulesVersion();
			});
			// }
		};

		controller.increasePrevListQuantity = function ()
		{
			controller.PrevListQuantity = controller.availablePreventionRuleSets.length;
		};

		controller.initK2A = function ()
		{
			k2aService.initK2A(controller.clinicName).then(
				function success(response)
				{
					console.info("Init complete, check status", response);
					controller.checkStatus();
				},
				function failure(error)
				{
					console.log(error);
					alert("Failed to initialize K2A");
				}
			);
		}
	}
]);