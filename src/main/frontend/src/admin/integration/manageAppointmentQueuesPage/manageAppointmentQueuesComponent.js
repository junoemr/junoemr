import {JUNO_STYLE, LABEL_POSITION} from "../../../common/components/junoComponentConstants";

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
				ctrl.componentStyle = JUNO_STYLE.GREY;

				ctrl.sortMode = "id";
				ctrl.queueList = [];
				ctrl.onDemandQueueHours = [];
				ctrl.onDemandAssignedQueue = null;
				ctrl.onDemandQueueSelectOptions = [];

				ctrl.LABEL_POSITION = LABEL_POSITION;

				ctrl.$onInit = function ()
				{
					ctrl.loadQueuesList();
					ctrl.loadOnDemandQueueHours();
					ctrl.updateQueueSelectOptions();
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
					// TODO call api delete
					// remove queue from queue list
					ctrl.queueList = ctrl.queueList.filter((obj) =>
					{
						return obj.id !== queue.id;
					});
					ctrl.updateQueueSelectOptions();
				}

				ctrl.openQueueModal = (queue, editMode) =>
				{
					try
					{
						$uibModal.open(
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
						).result.then(
							(updatedQueue) =>
							{
								if (editMode)
								{
									// update local copy with updated data
									angular.copy(updatedQueue, queue);
								}
								else
								{
									ctrl.queueList.push(updatedQueue);
								}
								ctrl.updateQueueSelectOptions();
							},
							(error) =>
							{
								// on canceled
							});
					}
					catch(err)
					{
						// user pressed ESC key
					}
				}

				ctrl.updateQueueSelectOptions = () =>
				{
					ctrl.onDemandQueueSelectOptions = ctrl.queueList.map((queue) =>
					{
						return {
							value: queue,
							label: queue.name,
						}
					});
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
				}
				ctrl.loadOnDemandQueueHours = () =>
				{
					const sampleStart = moment({hour: 8, minute: 0});
					const sampleEnd = moment({hour: 16, minute: 30});
					ctrl.onDemandQueueHours = [
						{
							name: 'Monday',
							enabled: true,
							start: sampleStart,
							end: sampleEnd,
						},
						{
							name: 'Tuesday',
							enabled: true,
							start: sampleStart,
							end: sampleEnd,
						},
						{
							name: 'Wednesday',
							enabled: true,
							start: sampleStart,
							end: sampleEnd,
						},
						{
							name: 'Thursday',
							enabled: true,
							start: sampleStart,
							end: sampleEnd,
						},
						{
							name: 'Friday',
							enabled: true,
							start: sampleStart,
							end: sampleEnd,
						},
						{
							name: 'Saturday',
							enabled: false,
							start: sampleStart,
							end: sampleEnd,
						},
						{
							name: 'Sunday',
							enabled: false,
							start: sampleStart,
							end: sampleEnd,
						},
					];
				}
			}]
	});