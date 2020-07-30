import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../../../common/components/junoComponentConstants";
import {AqsQueuesApi} from "../../../../generated";

angular.module('Admin.Integration').component('manageAppointmentQueuesAdmin',
	{
		templateUrl: 'src/admin/integration/manageAppointmentQueuesPage/manageAppointmentQueues.jsp',
		bindings: {},
		controller: [
			'$q',
			'$http',
			'$httpParamSerializer',
			'$scope',
			'$location',
			'$uibModal',
			'staticDataService',
			'NgTableParams',
			function (
				$q,
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

					ctrl.loadQueuesList().then(() =>
					{
						ctrl.loadOnDemandQueueHours();
					});

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

				ctrl.deleteQueue = async (queue) =>
				{
					const userOk = await Juno.Common.Util.confirmationDialog($uibModal, "Delete Queue?",
						"Are you sure you want to delete this queue?", ctrl.componentStyle);
					if (userOk)
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
					const deferred = $q.defer();
					aqsQueuesApi.getAppointmentQueues().then(
						(response) =>
						{
							ctrl.queueList = response.data.body;
							ctrl.updateQueueSelectOptions();
							deferred.resolve();
						}
					).catch((error) =>
					{
						console.error(error);
						alert("Failed to load appointment queue list");
						deferred.reject(error);
					});
					return deferred.promise;
				}
				ctrl.loadOnDemandQueueHours = () =>
				{
					const deferred = $q.defer();
					aqsQueuesApi.getOnDemandBookingSettings().then(
						(response) =>
						{
							ctrl.onDemandQueueHours = response.data.body.bookingHours.map((transfer) =>
							{
								return {
									dayOfWeek: transfer.dayOfWeek,
									enabled: transfer.enabled,
									startTime: moment(transfer.startTime, "HH:mm:ss"),
									endTime: moment(transfer.endTime, "HH:mm:ss"),
								}
							});

							// set the selected on-demand queue based on settings data
							ctrl.onDemandAssignedQueue = ctrl.queueList.find((queue) => queue.id === response.data.body.queueId);
							deferred.resolve();
						}
					).catch(
						(error) =>
						{
							console.error(error);
							alert("Failed to load on-demand booking settings");
							deferred.reject(error);
						}
					);
					return deferred.promise;
				}
			}]
	});