angular.module("Admin.Integration.Fax").controller('Admin.Integration.Fax.FaxConfigurationController', [
	"$uibModal",
	"faxConfigService",
	function ($uibModal,
	          faxConfigService)
	{
		var controller = this;
		controller.faxAccountList = [];

		controller.initialize = function()
		{
			faxConfigService.listAccounts().then(
				function success(response)
				{
					controller.faxAccountList = response;
				},
				function error(error)
				{
					console.error(error);
				}
			)
		};

		controller.editNewFaxAccount = function editNewFaxAccount()
		{
			controller.editFaxAccount();
		};
		controller.editFaxAccount = function editFaxAccount(faxAccount)
		{
			let isNewAcct = true;
			if(faxAccount)
			{
				isNewAcct = false;
			}

			var modalInstance = $uibModal.open(
				{
					templateUrl: 'admin/integration/fax/faxConfigurationEdit.jsp',
					controller: 'Admin.Integration.Fax.FaxConfigurationEditController as faxConfigEditController',
					backdrop: 'static',
					windowClass: 'faxEditModal',
					resolve:
						{
							faxAccount: function()
							{
								return faxAccount;
							}
						}
				});

			modalInstance.result.then(
				// the object passed back on closing
				function success(updatedAccount)
				{
					if(isNewAcct)
					{
						// new accounts get added to the account list
						controller.faxAccountList.push(updatedAccount);
					}
					else
					{
						// clear the existing properties and replace with the updated ones
						angular.copy(updatedAccount, faxAccount);
					}
				},
				function error(errors)
				{
					// do nothing on dismissal
				});
		};

		controller.initialize();
	}
]);