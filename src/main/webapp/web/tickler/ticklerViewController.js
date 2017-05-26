angular.module('Tickler').controller('Tickler.TicklerViewController', [

	'$scope',
	'$filter',
	'$uibModalInstance',
	'$timeout',
	'tickler',
	'ticklerService',
	'ticklerNote',
	'ticklerWriteAccess',
	'providerService',
	'me',

	function(
		$scope,
		$filter,
		$uibModalInstance,
		$timeout,
		tickler,
		ticklerService,
		ticklerNote,
		ticklerWriteAccess,
		providerService,
		me)
	{

		$scope.ticklerUpdate = angular.copy(tickler);
		console.log("TICKLER", tickler);
		if (Juno.Common.Util.exists($scope.ticklerUpdate.serviceDate))
		{
			$scope.ticklerUpdate.serviceDate = new Date($scope.ticklerUpdate.serviceDate);
		}

		if (Juno.Common.Util.exists($scope.ticklerUpdate.updateDate))
		{
			$scope.ticklerUpdate.updateDate = new Date($scope.ticklerUpdate.updateDate);
		}



		$scope.me = me;
		$scope.ticklerWriteAccess = ticklerWriteAccess;

		//this object keeps track of the changes being made
		// $scope.ticklerUpdate = {
		// 	message: $scope.tickler.message,

		// 	taskAssignedTo: $scope.tickler.taskAssignedTo,
		// 	taskAssignedToName: $scope.tickler.taskAssignedName,
		// 	serviceDate: $filter('date')($scope.tickler.serviceDate, 'yyyy-MM-dd'),
		// 	serviceTime: $filter('date')($scope.tickler.serviceDate, 'HH:mm'),

		// };
		// $scope.ticklerUpdate.message = "TESSST";
		// console.log("TicklerUpdate", $scope.ticklerUpdate.message);
		// console.log("Tickler message", tickler.message);
		$scope.needsUpdate = false;


		$scope.showUpdates = false;
		$scope.showComments = true;

		// console.log("Tickler Update status", $scope.tickler.taskAssignedTo);
		// console.log("Tickler Update status", $scope.tickler.priority);

		// $scope.taskAssignedToName = $scope.tickler.taskAssignedName;
		$scope.priorities = ['Low', 'Normal', 'High'];
		$scope.statuses = [
		{
			id: 'A',
			label: 'Active'
		},
		{
			id: 'C',
			label: 'Completed'
		},
		{
			id: 'D',
			label: 'Deleted'
		}];

		if (ticklerNote != null)
		{
			$scope.ticklerNote = ticklerNote.ticklerNote;
		}

		// Keep track of the current status as an object containing id and label
		if (tickler != null)
		{
			$scope.selectedStatus = $scope.statuses.find(function(status)
			{
				return status.id == tickler.status;
			});
		}

		$scope.$watch('ticklerUpdate.serviceDate',
			function(new_value)
			{
				console.log('change', new_value);

				if ($scope.ticklerUpdate.serviceDate instanceof Date && $scope.ticklerUpdate.serviceTime instanceof Date)
				{

					$scope.ticklerUpdate.serviceDate = new Date($scope.ticklerUpdate.serviceDate.getTime() + $scope.ticklerUpdate.serviceTime.getTime());

					console.log('new val', $scope.ticklerUpdate.serviceDate);
				}


			}
		);

		$scope.$watch('ticklerUpdate.serviceTime',
			function(new_value)
			{
				console.log('change', new_value);

				if ($scope.ticklerUpdate.serviceDate instanceof Date && $scope.ticklerUpdate.serviceTime instanceof Date)
				{
					$scope.ticklerUpdate.serviceDate = new Date($scope.ticklerUpdate.serviceDate.getTime() + $scope.ticklerUpdate.serviceTime.getTime());
				}


			}
		);

		$scope.close = function()
		{
			if ($scope.needsUpdate)
			{
				if (confirm("You have unsaved changes, are you sure?"))
				{
					$uibModalInstance.close(false);
				}
			}
			else
			{
				$scope.ticklerUpdate = undefined;
				$uibModalInstance.close(false);
			}

		};

		$scope.save = function()
		{
			$uibModalInstance.close("Someone Saved Me");
		};

		$scope.editTaskAssignedTo = function()
		{
			$scope.showTaskAssignedToFormControl = true;
			// $scope.ticklerUpdate.taskAssignedTo = $scope.tickler.taskAssignedTo;
			// $scope.ticklerUpdate.taskAssignedToName = $scope.tickler.taskAssignedName;
		};

		$scope.updateTaskAssignedTo = function(item, model, label)
		{
			$scope.needsUpdate = true;
			$scope.tickler.taskAssignedTo = model;
			$scope.tickler.taskAssignedToName = label;
			$scope.showTaskAssignedToFormControl = false;
		};

		$scope.cancelTaskAssignedToUpdate = function()
		{
			$scope.ticklerUpdate.taskAssignedTo = null;
			$scope.ticklerUpdate.taskAssignedToName = null;

			$scope.showTaskAssignedToFormControl = false;

		};

		$scope.editServiceDateAndTime = function()
		{
			// $scope.ticklerUpdate.serviceDate = $filter('date')($scope.tickler.serviceDate, 'yyyy-MM-dd');
			// $scope.ticklerUpdate.serviceTime = $filter('date')($scope.tickler.serviceDate, 'HH:mm');

			$scope.showServiceDateAndTimeFormControl = true;
		};


		$scope.updateServiceDateAndTime = function()
		{
			var dp = $scope.ticklerUpdate.serviceDate.split("-");
			var tp = $scope.ticklerUpdate.serviceTime.split(":");

			if (dp.length != 3 || tp.length != 2)
			{
				alert('Invalid Date/time. Please use yyyy-MM-dd and HH:mm formats');
				return;
			}

			var d = new Date(dp[0], parseInt(dp[1]) - 1, dp[2], tp[0], tp[1], 0, 0);

			if (d == null || isNaN(d.getTime()))
			{
				alert('Invalid Date/time. Please use yyyy-MM-dd and HH:mm formats');
				return;
			}
			$scope.needsUpdate = true;
			$scope.tickler.serviceDate = d;
			$scope.showServiceDateAndTimeFormControl = false;
		};

		$scope.cancelServiceDateAndTimeUpdate = function()
		{
			$scope.ticklerUpdate.serviceDate = null;
			$scope.showServiceDateAndTimeFormControl = false;
		};

		$scope.editPriority = function()
		{
			// $scope.ticklerUpdate.priority = $scope.tickler.priority;
			$scope.priorities = ['Low', 'Normal', 'High'];

			$scope.showPriorityFormControl = true;
		};

		$scope.updatePriority = function(newPriority)
		{
			console.log('updatePriority: ', newPriority);
			console.log('TicklerUpdate ', $scope.ticklerUpdate);
			$scope.needsUpdate = true;
			// $scope.ticklerUpdate.priority = newPriority; // Don't need this anymore?
			$scope.showPriorityFormControl = false;
		};


		$scope.cancelPriorityUpdate = function()
		{
			$scope.ticklerUpdate.priority = null;
			$scope.showPriorityFormControl = false;
		};

		$scope.editStatus = function()
		{
			// $scope.ticklerUpdate.statusName = $scope.tickler.statusName;
			// $scope.ticklerUpdate.status = $scope.tickler.statusName;
			$scope.showStatusFormControl = true;
		};

		$scope.updateStatus = function(selectedStatus)
		{
			console.log('STATUS: ', selectedStatus);
			console.log('TicklerUpdate ', $scope.ticklerUpdate);
			$scope.needsUpdate = true;
			$scope.ticklerUpdate.status = selectedStatus.id;
			$scope.ticklerUpdate.statusName = selectedStatus.label;
			$scope.showStatusFormControl = false;
		};

		$scope.cancelStatusUpdate = function()
		{
			$scope.ticklerUpdate.statusName = null;
			$scope.ticklerUpdate.status = null;

			$scope.showStatusFormControl = false;

		};

		$scope.addComment = function()
		{
			// $scope.ticklerUpdate.comment = '';
			$scope.showCommentFormControl = true;

		};

		$scope.saveComment = function()
		{
			console.log('Comment: ', $scope.ticklerUpdate.comment);
			$scope.needsUpdate = true;
			if ($scope.ticklerUpdate.ticklerComments == null)
			{
				$scope.ticklerUpdate.ticklerComments = [];
			}
			var comment = {
				message: $scope.ticklerUpdate.comment,
				providerName: me.formattedName,
				providerNo: me.providerNo,
				updateDate: new Date(),
				newComment: true
			};
			$scope.ticklerUpdate.ticklerComments.unshift(comment);
			$scope.showCommentFormControl = false;
			$scope.showComments = true;
		};

		$scope.cancelCommentUpdate = function()
		{
			$scope.ticklerUpdate.comment = null;
			$scope.showCommentFormControl = false;
		};


		$scope.searchProviders = function(val)
		{
			var search = {
				searchTerm: val,
				active: true
			};
			return providerService.searchProviders(search, 0, 10).then(function(response)
			{
				var resp = [];
				for (var x = 0; x < response.length; x++)
				{
					resp.push(
					{
						providerNo: response[x].providerNo,
						name: response[x].firstName + ' ' + response[x].lastName
					});
				}
				return resp;
			});
		};

		$scope.saveChanges = function()
		{
			if (tickler.message != $scope.ticklerUpdate.message)
			{
				$scope.needsUpdate = true;
			}
			if (tickler.serviceDate != $scope.ticklerUpdate.serviceDate.getTime())
			{
				$scope.needsUpdate = true;
			}
			if (tickler.serviceTime != $scope.ticklerUpdate.serviceTime)
			{
				console.log('updating time. old time: ', tickler.serviceTime);
				$scope.needsUpdate = true;
			}
			if ($scope.needsUpdate)
			{
				var postData = angular.copy($scope.ticklerUpdate);

				if (Juno.Common.Util.exists(postData.serviceDate))
				{
					postData.serviceDate = postData.serviceDate.getTime();
				}

				console.log('Post DATA: ', postData);

				ticklerService.update(postData).then(function(data)
				{
					$uibModalInstance.close(true);
				});
			}
			else
			{
				$uibModalInstance.close(false);
			}

		};


		$scope.completeTickler = function()
		{
			ticklerService.setCompleted([tickler.id]).then(function(data)
			{
				$uibModalInstance.close(true);
			}, function(reason)
			{
				alert(reason);
			});

		};

		$scope.deleteTickler = function()
		{
			ticklerService.setDeleted([tickler.id]).then(function(data)
			{
				$uibModalInstance.close(true);
			}, function(reason)
			{
				alert(reason);
			});

		};

		$scope.printTickler = function()
		{
			window.open('../Tickler.do?method=print&id=' + tickler.id);

		};
	}
]);