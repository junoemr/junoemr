import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../../../common/components/junoComponentConstants";
import {AqsQueuesApi} from "../../../../generated";

angular.module('Admin.Section').component('manageAppointmentQueuesAdmin',
	{
		templateUrl: 'src/admin/section/manageAppointmentQueuesPage/manageAppointmentQueues.jsp',
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

				ctrl.componentStyle = JUNO_STYLE.GREY;

				ctrl.sortMode = "id";
				ctrl.queueList = [];

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

					ctrl.loadQueuesList();
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
					const userOk = await Juno.Common.Util.confirmationDialog(
						$uibModal,
						"Delete Queue?",
						"Are you sure you want to delete this queue?",
						ctrl.componentStyle);
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
							windowClass: "juno-modal",
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
						},
						(dismissReason) =>
						{
							// modal dismissed
						});
				}

				ctrl.loadQueuesList = () =>
				{
					const deferred = $q.defer();
					aqsQueuesApi.getAppointmentQueues().then(
						(response) =>
						{
							ctrl.queueList = response.data.body;
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
			}]
	});