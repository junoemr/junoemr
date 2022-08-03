import {
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN,
	JUNO_STYLE,
	LABEL_POSITION
} from "../../../common/components/junoComponentConstants";
import {AqsQueuesApi} from "../../../../generated";
import {SystemPreferences} from "../../../common/services/systemPreferenceServiceConstants";
import {API_BASE_PATH} from "../../../lib/constants/ApiConstants";
import ToastErrorHandler from "../../../lib/error/handler/ToastErrorHandler";
import {SecurityPermissions} from "../../../common/security/securityConstants";
import ToastService from "../../../lib/alerts/service/ToastService";

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
			'securityService',
			'securityRolesService',
			'systemPreferenceService',
			function (
				$q,
				$http,
				$httpParamSerializer,
				$scope,
				$location,
				$uibModal,
				staticDataService,
				NgTableParams,
				securityService,
				securityRolesService,
				systemPreferenceService,
			)
			{
				const ctrl = this;

				// load appointment queue api
				ctrl.aqsQueuesApi = new AqsQueuesApi($http, $httpParamSerializer, API_BASE_PATH);
				ctrl.errorHandler = new ToastErrorHandler();
				ctrl.toastService = new ToastService();

				ctrl.componentStyle = JUNO_STYLE.GREY;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
				ctrl.SecurityPermissions = SecurityPermissions;

				ctrl.sortMode = "id";
				ctrl.queueList = [];
				ctrl.userIsSuperAdmin = securityService.getUser().superAdmin;
				ctrl.showAdvancedOptions = false;
				ctrl.organizationName = null;
				ctrl.organizationSecret = null;

				ctrl.$onInit = async function ()
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

					try
					{
						if(securityRolesService.hasSecurityPrivileges(ctrl.SecurityPermissions.AqsQueueConfigRead))
						{
							ctrl.organizationName = await systemPreferenceService.getPreference(SystemPreferences.AqsOrganizationId, null);
							if(ctrl.organizationName)
							{
								ctrl.loadQueuesList();
							}
						}
					}
					catch(e)
					{
						ctrl.errorHandler.handleError(e);
					}
				};

				ctrl.addQueue = () =>
				{
					ctrl.openQueueModal(null, false);
				}

				ctrl.editQueue = (queue) =>
				{
					ctrl.openQueueModal(queue, true);
				}

				ctrl.deleteQueue = async (queue): Promise<void> =>
				{
					const userOk: boolean = await Juno.Common.Util.confirmationDialog(
						$uibModal,
						"Delete Queue?",
						"Are you sure you want to delete this queue?",
						ctrl.componentStyle);
					if (userOk)
					{
						try
						{
							await ctrl.aqsQueuesApi.deleteAppointmentQueue(queue.id);

							// remove queue from queue list
							ctrl.queueList = ctrl.queueList.filter((obj) =>
							{
								return obj.id !== queue.id;
							});
						}
						catch(error)
						{
							ctrl.errorHandler.handleError(error);
						}
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

				// toggle advanced options display
				ctrl.toggleAdvanced = () =>
				{
					ctrl.showAdvancedOptions = !ctrl.showAdvancedOptions;
				}

				// update AQS credentials
				ctrl.updateAqsCredentials = async () =>
				{
					try
					{
						if(ctrl.organizationName)
						{
							await systemPreferenceService.setPreference(SystemPreferences.AqsOrganizationId, ctrl.organizationName);
						}

						if(ctrl.organizationSecret)
						{
							await systemPreferenceService.setPreference(SystemPreferences.AqsOrganizationSecret, ctrl.organizationSecret);
						}

						// reload queues.
						ctrl.queueList = [];
						await ctrl.loadQueuesList();
					}
					catch(e)
					{
						ctrl.errorHandler.handleError(e);
					}
				}

				ctrl.loadQueuesList = async (): Promise<void> =>
				{
					ctrl.queueList = (await ctrl.aqsQueuesApi.getAppointmentQueues()).data.body;
				}
			}]
	});