import {TicklerAttachmentType} from "../lib/tickler/model/TicklerAttachmentType";

angular.module('Tickler').controller('Tickler.TicklerListController', [

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
	'ticklerService',
	'noteService',
	'providerService',
	'providers',


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
		ticklerService,
		noteService,
		providerService,
		providers)
	{

		var controller = this;

		var ticklerAPI = $resource('../ws/rs/tickler/ticklers');

		controller.lastResponse = ""; // Can be removed?
		controller.providers = providers;

		securityService.hasRights(
		{
			items: [
			{
				objectName: '_tickler',
				privilege: 'w'
			},
			{
				objectName: '_tickler',
				privilege: 'r'
			}]
		}).then(function(result)
		{
			if (result.content != null && result.content.length == 2)
			{
				controller.ticklerWriteAccess = result.content[0];
				controller.ticklerReadAccess = result.content[1];

				if (controller.ticklerReadAccess)
				{

					//object which represents all the filters, initialize status.
					controller.search = {
						status: 'A',
					};

					if ($state.current.name === 'ticklers')
					{// only default to current day for serviceEndDate on the global tickler page.
						controller.search.serviceEndDate = moment().endOf('day').toDate();
					}

					providerService.getSettings().then(
						function(settings)
						{
							if (settings.ticklerViewOnlyMine)
							{
								providerService.getMe().then(
										function(user)
										{
											controller.search.taskAssignedTo = user.providerNo;
											controller.loadTable();
										}
								)
							}
							else
							{
								controller.loadTable();
							}
						}
					);
				}
			}
			else
			{
				alert('failed to load rights');
			}
		}, function(reason)
		{
			alert(reason);
		});

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

		// controller.checkAll = function()
		// {

		// 	angular.forEach(controller.lastResponse, function(item)
		// 	{
		// 		item.checked = true;
		// 	});
		// };

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
										ticklerList[i].ticklerLinkUrl = controller.getLinkUrl(ticklerList[i].ticklerLinks[0]);
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

			ticklerService.setCompleted(selectedTicklers).then(function(data)
			{
				controller.tableParams.reload();
			}, function(reason)
			{
				alert(reason);
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

			ticklerService.setDeleted(selectedTicklers).then(function(data)
			{
				controller.tableParams.reload();
			}, function(reason)
			{
				alert(reason);
			});

		};

		controller.addTickler = function()
		{
			var windowProps = "height=400,width=600,location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes";
			//window.open('../tickler/ticklerAdd.jsp','ticklerAdd',windowProps);

			var modalInstance = $uibModal.open(
			{
				templateUrl: 'src/tickler/ticklerAdd.jsp',
				controller: 'Tickler.TicklerAddController as ticklerAddCtrl',
				backdrop: 'static',
				size: 'lg',
				resolve: {
					attachment: () => null,
					presetDemographicNo: () => null,
				}
			});

			modalInstance.result.then(function(data)
			{
				console.log('data from modalInstance ' + data);
				if (data != null && data == true)
				{
					controller.tableParams.reload();
				}
			}, function(reason)
			{
				alert(reason);
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
					ticklerWriteAccess: function()
					{
						return controller.ticklerWriteAccess;
					},
					me: function()
					{
						return providerService.getMe();
					}
				}
			});

			modalInstance.result.then(function(data)
			{
				console.log('data from modalInstance ' + data);
				if (data != null && data == true)
				{
					controller.tableParams.reload();
				}
			}, function(reason)
			{
				alert(reason);
			});

		};

		controller.editNote2 = function(tickler)
		{

			noteService.getTicklerNote(tickler.id).then(function(data)
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
						ticklerNote: function()
						{
							return controller.ticklerNote;
						},
						tickler: function()
						{
							return tickler;
						}
					}
				});
			}, function(reason)
			{
				alert(reason);
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

		controller.getLinkUrl = function getLinkUrl(input)
		{
			if (input !== null && input.id !== null)
			{
				var url = "";
				if (input.tableName === TicklerAttachmentType.Cml)
				{
					url = "../lab/CA/ON/CMLDisplay.jsp?segmentID=" + input.tableId;
				}
				else if (input.tableName === TicklerAttachmentType.Mds)
				{
					url = "../oscarMDS/SegmentDisplay.jsp?segmentID=" + input.tableId;
				}
				else if (input.tableName === TicklerAttachmentType.Hl7)
				{
					url = "../lab/CA/ALL/labDisplay.jsp?segmentID=" + input.tableId;
				}
				else if (input.tableName === TicklerAttachmentType.Doc)
				{
					url = "../dms/ManageDocument.do?method=display&doc_no=" + input.tableId;
				}
				else if (input.tableName === TicklerAttachmentType.Message)
				{
					const meta = JSON.parse(input.meta);
					url = $state.href("messaging.view.message", {
						messageId: input.tableId,
						backend: meta.messagingBackend,
						source: meta.source,
						group: meta.group,
					});
				}
				return url;
			}
		};
		controller.inDemographicView = function()
		{
			return ($state.params.demographicNo != null);
		}
	}
]);
