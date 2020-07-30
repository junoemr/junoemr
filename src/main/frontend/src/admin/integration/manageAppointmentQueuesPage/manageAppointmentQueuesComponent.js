import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../../../common/components/junoComponentConstants";
import {AqsQueuesApi} from "../../../../generated";

angular.module('Admin.Integration').component('manageAppointmentQueuesAdmin',
	{
		templateUrl: 'src/admin/integration/manageAppointmentQueuesPage/manageAppointmentQueues.jsp',
		bindings: {},
		controller: [
			'$http',
			'$httpParamSerializer',
			'$scope',
			'$location',
			'$uibModal',
			'staticDataService',
			'NgTableParams',
			function (
				$http,
				$httpParamSerializer,
				$scope,
				$location,
				$uibModal,
				staticDataService,
				NgTableParams,
			)
			{
				let ctrl = this;

				// load appointment queue api
				let aqsQueuesApi = new AqsQueuesApi($http, $httpParamSerializer, '../ws/rs');

				ctrl.onDemandBookingEnabled = true;
				ctrl.componentStyle = JUNO_STYLE.GREY;

				ctrl.sortMode = "id";
				ctrl.queueList = [];
				ctrl.onDemandQueueHours = [];
				ctrl.onDemandAssignedQueue = null;
				ctrl.onDemandQueueSelectOptions = [];

				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

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
					aqsQueuesApi.getAppointmentQueue("1");
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
					if (window.confirm("Are you sure you want to delete this queue?"))
					{
						aqsQueuesApi.deleteAppointmentQueue(queue.id).then(
							(response) =>
							{
								// remove queue from queue list
								ctrl.queueList = ctrl.queueList.filter((obj) =>
								{
									return obj.id !== queue.id;
								});
								ctrl.updateQueueSelectOptions();
							}
						).catch((error) =>
						{
							console.error(error);
							alert("Failed to delete the queue");
						});
					}
				}

				ctrl.openQueueModal = (queue, editMode) =>
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
						(dismissReason) =>
						{
							// modal dismissed
						});
				}

				ctrl.updateQueueSelectOptions = () =>
				{
					ctrl.onDemandQueueSelectOptions = ctrl.queueList.map((queue) =>
					{
						return {
							value: queue,
							label: queue.queueName,
						}
					});
				}

				ctrl.loadQueuesList = () =>
				{
					aqsQueuesApi.getAppointmentQueues().then(
						(response) =>
						{
							ctrl.queueList = response.data.body;
							ctrl.updateQueueSelectOptions();

							//TODO assign correctly
							ctrl.onDemandAssignedQueue = ctrl.queueList[0];
						}
					).catch((error) =>
					{
						console.error(error);
						alert("Failed to load appointment queue list");
					});
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