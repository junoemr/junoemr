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
			function (
				$scope,
				$location,
				$uibModal,
				staticDataService,
				NgTableParams,
			)
			{
				let ctrl = this;

				ctrl.sortMode = "id";
				ctrl.queueList = [];

				ctrl.$onInit = function ()
				{
					ctrl.loadQueuesList();
					ctrl.tableParams = new NgTableParams(
						{
							page: 1, // show first page
							count: -1, // unlimited
							sorting:
								{
									id: 'desc',
								}
						},
						{
							// called when sort order changes
							getData: function (params)
							{
								ctrl.sortMode = params.orderBy();
							}
						}
					);
				};

				ctrl.addQueue = () =>
				{
					console.info("add queue");
				}

				ctrl.editQueue = (queue) =>
				{
					console.info("edit queue", queue);
				}

				ctrl.deleteQueue = (queue) =>
				{
					console.info("delete queue", queue);
				}

				ctrl.loadQueuesList = () =>
				{
					//TODO replace with backend call
					ctrl.queueList = [
						{
							id: 1,
							name: "Queue name 1",
							data: "asdasd",
						},
						{
							id: 2,
							name: "Queue name 2",
							data: "asdasd",
						},
						{
							id: 3,
							name: "Queue name 3",
							data: "asdasd",
						}
					];
				}
			}]
	});