import {SecurityPermissions} from "../common/security/securityConstants";

import {TicklerAttachmentType} from "../lib/tickler/model/TicklerAttachmentType";
import ToastService from "../lib/alerts/service/ToastService";
import TicklerAttachment from "../lib/tickler/model/TicklerAttachment";

angular.module('Tickler').component('ticklerListController', {
	templateUrl: 'src/tickler/ticklerList.jsp',
	bindings: {
		modalInstance: "<",
		resolve: "<",
	},
	controller: [
		'$scope',
		'$state',
		'$timeout',
		'$resource',
		'$uibModal',
		'$http',
		'$stateParams',
		'$filter',
		'NgTableParams',
		'securityService',
		'securityRolesService',
		'ticklerService',
		'noteService',
		'providerService',

		function(
			$scope,
			$state,
			$timeout,
			$resource,
			$uibModal,
			$http,
			$stateParams,
			$filter,
			NgTableParams,
			securityService,
			securityRolesService,
			ticklerService,
			noteService,
			providerService)
		{
			const controller = this;

			const ticklerAPI = $resource('../ws/rs/tickler/ticklers');
			controller.toastService = new ToastService();

			controller.user = null;
			controller.providers = [];
			controller.SecurityPermissions = SecurityPermissions;
			controller.tableParams = null;

			//object which represents all the filters, initialize status.
			controller.search = {
				status: 'A',
			};

			controller.$onInit = async () =>
			{
				if(securityRolesService.hasSecurityPrivileges(SecurityPermissions.TicklerRead))
				{
					if ($state.current.name === 'ticklers')
					{
						// only default to current day for serviceEndDate on the global tickler page.
						controller.search.serviceEndDate = moment().endOf('day').toDate();
					}

					let resultList = await Promise.all([
						providerService.getMe(),
						providerService.getSettings(),
						providerService.searchProviders({active: true}),
					]);
					controller.user = resultList[0];
					let settings = resultList[1];
					controller.providers = resultList[2];

					if (settings.ticklerViewOnlyMine)
					{
						controller.search.taskAssignedTo = controller.user.providerNo;
					}
					controller.loadTable();
				}
			}

			controller.doSearch = function()
			{
				controller.tableParams.reload();
			};

			controller.clear = function()
			{
				controller.search = angular.copy(
				{
					status: 'A'
				});
				controller.tableParams.reload();
			};

			controller.checkAll = function checkAll(data)
			{
				angular.forEach(data, function(tickler)
				{
					tickler.checked = true;
				});
			};

			controller.checkNone = function checkNone(data)
			{
				angular.forEach(data, function(tickler)
				{
					tickler.checked = false;
				});
			};

			controller.loadTable = function()
			{
				controller.tableParams = new NgTableParams(
						{
							page: 1, // show first page
							count: 10,// initial count per page
							sorting: {
								ServiceDate: "desc"
							},
	                        paginationMinBlocks: 2
						},
						{
							// total: 0, // length of data
							getData: function(params)
							{
								// ajax request to api
								controller.search.count = params.url().count;
								controller.search.page = params.url().page;
								controller.search.includeLinks = 'true';
								controller.search.includeComments = 'true';
								controller.search.includeUpdates = 'true';
								controller.search.includeProgram = true;

								// need to parse out the ng-tables sort column/direction values
								// for use in our get parameters.
								var myRegexp = /sorting\[(\w+)\]/g;
								for(var key in params.url()) {
									var match = myRegexp.exec(String(key));
									if(match) {
										controller.search.sortColumn = match[1];
										controller.search.sortDirection = params.url()[String(key)];
									}
								}

								if (angular.isDefined($stateParams.demographicNo))
								{
									controller.search.demographicNo = $stateParams.demographicNo;
								}

								return ticklerAPI.get(controller.search).$promise.then(function(data)
								{
									params.total(data.total); // recal. page nav controls
									var ticklerList = data.content;
									// Grab URLs for tickler links
									for (var i = 0; i < ticklerList.length; i++){
										if (ticklerList[i].ticklerLinks.length > 0 )
										{
											let attachment = new TicklerAttachment(
												ticklerList[i].ticklerLinks[0].tableName,
												ticklerList[i].ticklerLinks[0].tableId,
												ticklerList[i].ticklerLinks[0].meta,
												);
											ticklerList[i].ticklerLinkUrl = attachment.getLinkUrl($state);
										}
									}

									return data.content;
								});
							}
						});
			}

			controller.completeTicklers = function()
			{
				var selectedTicklers = [];
				angular.forEach(controller.tableParams.data, function(item)
				{
					if (item.checked)
					{
						selectedTicklers.push(item.id);
					}
				});

				ticklerService.setCompleted(selectedTicklers).then(
					function (data)
					{
						controller.tableParams.reload();
					},
					function (reason)
					{
						console.error(reason);
						controller.toastService.errorToast("Error setting ticklers as completed", true);
					});
			};

			controller.deleteTicklers = function()
			{
				var selectedTicklers = [];
				angular.forEach(controller.tableParams.data, function(item)
				{
					if (item.checked)
					{
						selectedTicklers.push(item.id);
					}
				});

				ticklerService.setDeleted(selectedTicklers).then(
					function (data)
					{
						controller.tableParams.reload();
					},
					function (reason)
					{
						console.error(reason);
						controller.toastService.errorToast("Error setting ticklers as deleted", true);
					});

			};

			controller.addTickler = function()
			{
				var modalInstance = $uibModal.open(
				{
					component: "ticklerAddComponent",
					backdrop: 'static',
					size: 'lg',
					resolve: {
						attachment: () => null,
						presetDemographicNo: () => null,
					}
				});

				modalInstance.result.then(
					(data) =>
					{
						if (data != null && data == true)
						{
							controller.tableParams.reload();
						}
					},
					(reason) =>
					{
						console.error(reason);
						controller.toastService.errorToast("An unknown error occurred", true);
					});
			};

			controller.editTickler = function(tickler)
			{
				var modalInstance = $uibModal.open(
				{
					templateUrl: 'src/tickler/ticklerView.jsp',
					controller: 'Tickler.TicklerViewController as ticklerViewCtrl',
					backdrop: 'static',
					size: 'lg',
					resolve:
					{
						tickler: function()
						{
							return tickler;
						},
						ticklerNote: function()
						{
							return noteService.getTicklerNote(tickler.id);
						},
						me: function()
						{
							return controller.user;
						}
					}
				});

				modalInstance.result.then(
					function (data)
					{
						if (data != null && data == true)
						{
							controller.tableParams.reload();
						}
					},
					function (reason)
					{
						console.error(reason);
						controller.toastService.errorToast("An unknown error occurred", true);
					});

			};

			controller.editNote2 = function(tickler)
			{

				noteService.getTicklerNote(tickler.id).then(
					function (data)
					{
						if (data.ticklerNote != null)
						{
							controller.ticklerNote = data.ticklerNote;
						}
						else
						{
							controller.ticklerNote = {
								"editor": "you",
								"note": "",
								"noteId": 0,
								"observationDate": "now",
								"revision": 0
							};

						}
						var modalInstance = $uibModal.open(
							{
								templateUrl: 'src/tickler/ticklerNote.jsp',
								controller: 'Tickler.TicklerNoteController as ticklerNoteCtrl',
								backdrop: 'static',
								resolve:
									{
										ticklerNote: function ()
										{
											return controller.ticklerNote;
										},
										tickler: function ()
										{
											return tickler;
										}
									}
							});
					},
					function (reason)
					{
						console.error(reason);
						controller.toastService.errorToast("Error fetching tickler notes", true);
					});

			};

			controller.showComments = function(tickler)
			{
				controller.tickler = tickler;
				var modalInstance = $uibModal.open(
				{
					templateUrl: 'src/tickler/ticklerComments.jsp',
					controller: 'Tickler.TicklerCommentController as ticklerCommentCtrl',
					backdrop: 'static',
					resolve:
					{
						tickler: function()
						{
							return controller.tickler;
						}
					}
				});
			};

			controller.printArea = function()
			{
				window.print();
			};

			controller.inDemographicView = function()
			{
				return ($state.params.demographicNo != null);
			}

			controller.canEdit = () =>
			{
				return securityRolesService.hasSecurityPrivileges(SecurityPermissions.TicklerUpdate);
			}
			controller.canCreate = () =>
			{
				return securityRolesService.hasSecurityPrivileges(SecurityPermissions.TicklerCreate);
			}
			controller.canDelete = () =>
			{
				return securityRolesService.hasSecurityPrivileges(SecurityPermissions.TicklerDelete);
			}
		}
]});
