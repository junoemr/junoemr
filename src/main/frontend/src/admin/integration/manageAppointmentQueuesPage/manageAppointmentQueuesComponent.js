angular.module('Admin.Integration').component('manageAppointmentQueuesAdmin',
	{
		templateUrl: 'src/admin/integration/manageAppointmentQueuesPage/manageAppointmentQueues.jsp',
		bindings: {},
		controller: [
			'$scope',
			'$location',
			'$uibModal',
			'staticDataService',
			'NgTableParams',
			'providerService',
			function (
				$scope,
				$location,
				$uibModal,
				staticDataService,
				NgTableParams,
				providerService,
			)
			{
				let ctrl = this;

			}]
	});