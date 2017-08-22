angular.module('Tickler').controller('Tickler.TicklerListController', [

	'$scope',
	'$timeout',
	'$resource',
	'$uibModal',
	'$http',
	'$stateParams',
	'NgTableParams',
	'securityService',
	'ticklerService',
	'noteService',
	'providerService',
	'providers',


	function(
		$scope,
		$timeout,
		$resource,
		$uibModal,
		$http,
		$stateParams,
		NgTableParams,
		securityService,
		ticklerService,
		noteService,
		providerService,
		providers)
	{

		var controller = this;

		var ticklerAPI = $resource('../ws/rs/tickler/ticklers');

		console.log("Params: ", $resource);

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
						status: 'A'
					};


					controller.tableParams = new NgTableParams(
					{
						page: 1, // show first page
						count: 10
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

							if (angular.isDefined($stateParams.demographicNo))
							{
								controller.search.demographicNo = $stateParams.demographicNo;
							}

							return ticklerAPI.get(controller.search).$promise.then(function(data)
							{
								params.total(data.total); // recal. page nav controls
								return data.content;
							});

							// ticklerAPI.get(controller.search, function(data)
							// {
							// 	console.log("DATA: ", data);
							// 	$timeout(function()
							// 	{

							// 		// update table params
							// 		params.total(data.total);
							// 		// set new data
							// 		// $defer.resolve(data.tickler);

							// 		controller.lastResponse = data.tickler;
							// 	}, 500);
							// });


						}
					});
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
				templateUrl: 'tickler/ticklerAdd.jsp',
				controller: 'Tickler.TicklerAddController as ticklerAddCtrl',
				backdrop: 'static',
				size: 'lg'
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
				templateUrl: 'tickler/ticklerView.jsp',
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
					templateUrl: 'tickler/ticklerNote.jsp',
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
				templateUrl: 'tickler/ticklerComments.jsp',
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


	}
]);