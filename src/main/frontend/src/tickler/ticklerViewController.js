import {SecurityPermissions} from "../common/security/securityConstants";

angular.module('Tickler').controller('Tickler.TicklerViewController', [

	'$scope',
	'$filter',
	'$uibModalInstance',
	'$timeout',
	'tickler',
	'ticklerService',
	'ticklerNote',
	'providerService',
	'securityRolesService',
	'me',

	function(
		$scope,
		$filter,
		$uibModalInstance,
		$timeout,
		tickler,
		ticklerService,
		ticklerNote,
		providerService,
		securityRolesService,
		me)
	{

		const controller = this;

		controller.ticklerUpdate = angular.copy(tickler);

		controller.serviceDateInput = moment(controller.ticklerUpdate.serviceDate).toDate();
		controller.serviceTimeInput = moment(controller.ticklerUpdate.serviceDate).format("hh:mm A");

		controller.me = me;

		//this object keeps track of the changes being made
		// controller.ticklerUpdate = {
		// 	message: controller.tickler.message,

		// 	taskAssignedTo: controller.tickler.taskAssignedTo,
		// 	taskAssignedToName: controller.tickler.taskAssignedName,
		// 	serviceDate: $filter('date')(controller.tickler.serviceDate, 'yyyy-MM-dd'),
		// 	serviceTime: $filter('date')(controller.tickler.serviceDate, 'HH:mm'),

		// };

		controller.needsUpdate = false;
		controller.showUpdates = false;
		controller.showComments = false;
		controller.showCommentFormControl = false;

		// controller.taskAssignedToName = controller.tickler.taskAssignedName;
		controller.priorities = ['Low', 'Normal', 'High'];
		controller.statuses = [
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
			controller.ticklerNote = ticklerNote.ticklerNote;
		}

		// Keep track of the current status as an object containing id and label
		if (tickler != null)
		{
			controller.selectedStatus = controller.statuses.find(function(status)
			{
				return status.id == tickler.status;
			});
		}

		// $scope.$watch('controller.ticklerUpdate.serviceDate',
		// 	function(new_value)
		// 	{
		// 		console.log('change', new_value);

		// 		if (controller.ticklerUpdate.serviceDate instanceof Date && controller.ticklerUpdate.serviceTime instanceof Date)
		// 		{

		// 			controller.ticklerUpdate.serviceDate = new Date(controller.ticklerUpdate.serviceDate.getTime() + controller.ticklerUpdate.serviceTime.getTime());

		// 			console.log('new val', controller.ticklerUpdate.serviceDate);
		// 		}


		// 	}
		// );

		// Watches the date input and updates serviceDate when a change is made
		$scope.$watch(function()
			{
				return controller.serviceDateInput;
			},
			function(newValue)
			{
				var newDate = moment(newValue.getTime()).format("MM-DD-YYYY");
				var finalVal = moment(newDate + "-" + controller.serviceTimeInput, 'MM-DD-YYYY-hh:mm A').toDate().getTime();

				controller.ticklerUpdate.serviceDate = finalVal;
			}
		);

		// Watches the time input and updates serviceDate when a change is made
		$scope.$watch(function()
			{
				return controller.serviceTimeInput;
			},
			function(newValue)
			{
				var date = moment(controller.serviceDateInput.getTime()).format("MM-DD-YYYY");
				var finalVal = moment(date + "-" + newValue, 'MM-DD-YYYY-hh:mm A').toDate().getTime();

				controller.ticklerUpdate.serviceDate = finalVal;
			}
		);

		controller.close = function()
		{
			if (controller.needsUpdate)
			{
				if (confirm("You have unsaved changes, are you sure?"))
				{
					$uibModalInstance.close(false);
				}
			}
			else
			{
				controller.ticklerUpdate = undefined;
				$uibModalInstance.close(false);
			}

		};

		controller.save = function()
		{
			$uibModalInstance.close("Someone Saved Me");
		};

		controller.updateTaskAssignedTo = function(item, model, label)
		{
			controller.needsUpdate = true;
			controller.ticklerUpdate.taskAssignedTo = model;
			controller.ticklerUpdate.taskAssignedToName = label;
			controller.showTaskAssignedToFormControl = false;
		};

		controller.cancelTaskAssignedToUpdate = function()
		{
			controller.ticklerUpdate.taskAssignedTo = null;
			controller.ticklerUpdate.taskAssignedToName = null;

			controller.showTaskAssignedToFormControl = false;

		};

		controller.editServiceDateAndTime = function()
		{
			// controller.ticklerUpdate.serviceDate = $filter('date')(controller.tickler.serviceDate, 'yyyy-MM-dd');
			// controller.ticklerUpdate.serviceTime = $filter('date')(controller.tickler.serviceDate, 'HH:mm');

			controller.showServiceDateAndTimeFormControl = true;
		};


		controller.updateServiceDateAndTime = function()
		{
			var dp = controller.ticklerUpdate.serviceDate.split("-");
			var tp = controller.ticklerUpdate.serviceTime.split(":");

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
			controller.needsUpdate = true;
			controller.tickler.serviceDate = d;
			controller.showServiceDateAndTimeFormControl = false;
		};

		controller.cancelServiceDateAndTimeUpdate = function()
		{
			controller.ticklerUpdate.serviceDate = null;
			controller.showServiceDateAndTimeFormControl = false;
		};

		controller.editPriority = function()
		{
			// controller.ticklerUpdate.priority = controller.tickler.priority;
			controller.priorities = ['Low', 'Normal', 'High'];

			controller.showPriorityFormControl = true;
		};

		controller.updatePriority = function(newPriority)
		{
			controller.needsUpdate = true;
			// controller.ticklerUpdate.priority = newPriority; // Don't need this anymore?
			controller.showPriorityFormControl = false;
		};


		controller.cancelPriorityUpdate = function()
		{
			controller.ticklerUpdate.priority = null;
			controller.showPriorityFormControl = false;
		};

		controller.editStatus = function()
		{
			// controller.ticklerUpdate.statusName = controller.tickler.statusName;
			// controller.ticklerUpdate.status = controller.tickler.statusName;
			controller.showStatusFormControl = true;
		};

		controller.updateStatus = function(selectedStatus)
		{
			controller.needsUpdate = true;
			controller.ticklerUpdate.status = selectedStatus.id;
			controller.ticklerUpdate.statusName = selectedStatus.label;
			controller.showStatusFormControl = false;
		};

		controller.cancelStatusUpdate = function()
		{
			controller.ticklerUpdate.statusName = null;
			controller.ticklerUpdate.status = null;

			controller.showStatusFormControl = false;

		};

		controller.addComment = function()
		{
			// controller.ticklerUpdate.comment = '';
			controller.showCommentFormControl = true;

		};

		controller.saveComment = function()
		{
			controller.needsUpdate = true;
			if (controller.ticklerUpdate.ticklerComments == null)
			{
				controller.ticklerUpdate.ticklerComments = [];
			}
			var comment = {
				message: controller.ticklerUpdate.comment,
				providerName: me.formattedName,
				providerNo: me.providerNo,
				updateDate: new Date(),
				newComment: true
			};
			controller.ticklerUpdate.ticklerComments.unshift(comment);
			controller.showCommentFormControl = false;
			controller.showComments = true;
		};

		controller.cancelCommentUpdate = function()
		{
			controller.ticklerUpdate.comment = null;
			controller.showCommentFormControl = false;
		};


		controller.searchProviders = function(val)
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

		controller.saveChangesAndWriteEncounter = function()
		{
			return controller.saveChanges(true);
		}
		controller.saveChanges = function(writeEncounter = false)
		{
			if (tickler.message != controller.ticklerUpdate.message)
			{
				controller.needsUpdate = true;
			}
			// if (tickler.serviceDate != controller.ticklerUpdate.serviceDate.getTime())
			// {
			// 	controller.needsUpdate = true;
			// }

			if (tickler.serviceDate != controller.ticklerUpdate.serviceDate)
			{
				controller.needsUpdate = true;
			}

			if (controller.needsUpdate)
			{
				var postData = angular.copy(controller.ticklerUpdate);
				// if (Juno.Common.Util.exists(postData.serviceDate))
				// {
				// 	// postData.serviceDate = postData.serviceDate.getTime();
				// 	console.log('POST DATA: ', postData);
				// }

				console.log('Post DATA: ', postData);

				ticklerService.update(postData, writeEncounter).then(function(data)
				{
					$uibModalInstance.close(true);
				});
			}
			else
			{
				$uibModalInstance.close(false);
			}

		};


		controller.completeTickler = function()
		{
			ticklerService.setCompleted([tickler.id]).then(function(data)
			{
				$uibModalInstance.close(true);
			}, function(reason)
			{
				alert(reason);
			});

		};

		controller.deleteTickler = function()
		{
			ticklerService.setDeleted([tickler.id]).then(function(data)
			{
				$uibModalInstance.close(true);
			}, function(reason)
			{
				alert(reason);
			});

		};

		controller.printTickler = function()
		{
			window.open('../Tickler.do?method=print&id=' + tickler.id);
		};

		controller.canEdit = () =>
		{
			return securityRolesService.hasSecurityPrivileges(SecurityPermissions.TicklerUpdate);
		}
		controller.canDelete = () =>
		{
			return securityRolesService.hasSecurityPrivileges(SecurityPermissions.TicklerDelete);
		}
	}
]);