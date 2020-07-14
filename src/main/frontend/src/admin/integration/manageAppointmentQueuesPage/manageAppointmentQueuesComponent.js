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

				ctrl.onDemandBookingEnabled = true;

				ctrl.sortMode = "id";
				ctrl.queueList = [];
				ctrl.onDemandQueueHours = [];
				ctrl.onDemandAssignedQueue = null;
				ctrl.onDemandQueueSelectOptions = [];

				ctrl.$onInit = function ()
				{
					ctrl.loadQueuesList();
					ctrl.loadOnDemandQueueHours();
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
					ctrl.hoursTableParams = new NgTableParams(
						{
							page: 1, // show first page
							count: -1, // unlimited
						}
					);

					//TODO assign correctly
					ctrl.onDemandAssignedQueue = ctrl.queueList[0];
				};

				ctrl.addQueue = () =>
				{
					ctrl.openQueueModal(null, false);
				}

				ctrl.editQueue = (queue) =>
				{
					ctrl.openQueueModal(queue, true);
				}

				ctrl.deleteQueue = (queue) =>
				{
					console.info("delete queue", queue);
				}

				ctrl.openQueueModal = async (queue, editMode) =>
				{
					try
					{
						ctrl.inviteSent = await $uibModal.open(
							{
								component: 'appointmentQueueModal',
								backdrop: 'static',
								windowClass: "juno-modal sml",
								resolve: {
									style: () => ctrl.componentStyle,
									queue: () => queue,
									editMode: editMode,
								}
							}
						).result;
					}
					catch(err)
					{
						// user pressed ESC key
					}
				}

				ctrl.loadQueuesList = () =>
				{
					//TODO replace with backend call
					ctrl.queueList = [
						{
							id: 1,
							name: "Queue name 1",
							limit: 25,
						},
						{
							id: 2,
							name: "Queue name 2",
							limit: 10,
						},
						{
							id: 3,
							name: "Queue name 3",
							limit: 10,
						}
					];

					ctrl.onDemandQueueSelectOptions = ctrl.queueList.map((queue) =>
					{
						return {
							value: queue,
							label: queue.name,
						}
					});
				}
				ctrl.loadOnDemandQueueHours = () =>
				{
					ctrl.onDemandQueueHours = [
						{
							name: 'Monday',
							enabled: true,
							start: moment(),
							end: moment(),
						},
						{
							name: 'Tuesday',
							enabled: true,
							start: moment(),
							end: moment(),
						},
						{
							name: 'Wednesday',
							enabled: true,
							start: moment(),
							end: moment(),
						},
						{
							name: 'Thursday',
							enabled: true,
							start: moment(),
							end: moment(),
						},
						{
							name: 'Friday',
							enabled: true,
							start: moment(),
							end: moment(),
						},
						{
							name: 'Saturday',
							enabled: false,
							start: moment(),
							end: moment(),
						},
						{
							name: 'Sunday',
							enabled: false,
							start: moment(),
							end: moment(),
						},
					];
				}
			}]
	});