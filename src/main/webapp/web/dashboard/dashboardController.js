/*

    Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for the
    Department of Family Medicine
    McMaster University
    Hamilton
    Ontario, Canada

*/
angular.module('Dashboard').controller('Dashboard.DashboardController', [

	'$scope',
	'$uibModal',
	'NgTableParams',
	'providerService',
	'ticklerService',
	'messageService',
	'inboxService',
	'k2aService',
	'noteService',
	'securityService',
	'personaService',

	function(
		$scope,
		$uibModal,
		NgTableParams,
		providerService,
		ticklerService,
		messageService,
		inboxService,
		k2aService,
		noteService,
		securityService,
		personaService)
	{

		//header
		$scope.displayDate = function displayDate()
		{
			return new Date();
		};
		console.log('TABLE PARAMS', NgTableParams);
		$scope.me = null;

		$scope.busyLoadingData = false;

		personaService.getDashboardPreferences().then(
			function success(results)
			{
				$scope.prefs = results.dashboardPreferences;
			},
			function error(errors)
			{
				console.log(errors);
			});

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
		}).then(
			function success(results)
			{
				if (results.content != null && results.content.length == 2)
				{
					$scope.ticklerWriteAccess = results.content[0];
					$scope.ticklerReadAccess = results.content[1];
				}
			},
			function error(errors)
			{
				console.log(errors);
			});

		$scope.inboxTableParams = new NgTableParams(
		{
			page: 1, // show first page
			count: 10
		},
		{
			// total: 0, // length of data
			getData: function(params)
			{


				return inboxService.getDashboardItems(params.count()).then(
					function success(results)
					{
						console.log('INBOX DATA: ', results);
						params.total(results.total); // recal. page nav controls
						return results.content;
					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		});

		$scope.openInbox = function openInbox()
		{
			newwindow = window.open('../dms/inboxManage.do?method=prepareForIndexPage', 'inbox', 'height=700,width=1000');
			if (window.focus)
			{
				newwindow.focus();
			}
		};

		// Remove? 
		// $scope.loadMoreK2aFeed = function()
		// {
		// 	$scope.updateFeed($scope.k2afeed.length, 10);
		// };

		// $scope.authenticateK2A = function(id)
		// {
		// 	window.open('../apps/oauth1.jsp?id=' + id, 'appAuth', 'width=700,height=450');
		// };

		// $scope.agreeWithK2aPost = function(item)
		// {
		// 	if (item.agree)
		// 	{
		// 		k2aService.removeK2AComment(item.agreeId).then(
		// 			function(response)
		// 			{
		// 				item.agree = false;
		// 				item.agreeCount--;
		// 				item.agreeId = '';
		// 			},
		// 			function(reason)
		// 			{
		// 				alert(reason);
		// 			});
		// 	}
		// 	else if (!(item.agree || item.disagree))
		// 	{
		// 		if (typeof item.newComment === 'undefined')
		// 		{
		// 			item.newComment = {};
		// 		}
		// 		item.newComment.agree = true;
		// 		item.newComment.body = '';

		// 		$scope.commentOnK2aPost(item);
		// 	}
		// };

		// $scope.disagreeWithK2aPost = function(item)
		// {
		// 	if (item.disagree)
		// 	{
		// 		k2aService.removeK2AComment(item.agreeId).then(
		// 			function(response)
		// 			{
		// 				item.disagree = false;
		// 				item.disagreeCount--;
		// 				item.agreeId = '';
		// 			},
		// 			function(reason)
		// 			{
		// 				alert(reason);
		// 			});
		// 	}
		// 	if (!(item.agree || item.disagree))
		// 	{
		// 		if (typeof item.newComment === 'undefined')
		// 		{
		// 			item.newComment = {};
		// 		}
		// 		item.newComment.agree = false;
		// 		item.newComment.body = '';

		// 		$scope.commentOnK2aPost(item);
		// 	}
		// };

		// $scope.commentOnK2aPost = function(item)
		// {
		// 	item.newComment.postId = item.id;
		// 	k2aService.postK2AComment(item.newComment).then(
		// 		function(response)
		// 		{
		// 			item.newComment.body = '';
		// 			item.newComment.agree = '';
		// 			item.agreeId = response.agreeId;
		// 			if (!(typeof response.post[0].agree === 'undefined'))
		// 			{
		// 				if (response.post[0].agree)
		// 				{
		// 					item.agree = true;
		// 					item.agreeId = response.post[0].agreeId;
		// 					item.agreeCount++;
		// 				}
		// 				else
		// 				{
		// 					item.disagree = true;
		// 					item.agreeId = response.post[0].agreeId;
		// 					item.disagreeCount++;
		// 				}
		// 			}
		// 			else
		// 			{
		// 				item.commentCount++;
		// 				item.comments.unshift(response.post[0]);
		// 			}
		// 		},
		// 		function(reason)
		// 		{
		// 			alert(reason);
		// 		});
		// };

		$scope.updateTicklers = function updateTicklers()
		{
			//consider the option to have overdue only or not
			ticklerService.search(
			{
				priority: '',
				status: 'A',
				assignee: $scope.me.providerNo,
				overdueOnly: 'property'
			}, 0, 6).then(
				function success(results)
				{
					$scope.totalTicklers = results.total;
					if (results.content == null)
					{
						return;
					}

					if (results.content instanceof Array)
					{
						$scope.ticklers = results.content;
					}
					else
					{
						var arr = new Array();
						arr[0] = results.content;
						$scope.ticklers = arr;
					}
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		$scope.updateMessages = function updateMessages()
		{
			messageService.getUnread(6).then(
				function success(results)
				{
					$scope.totalMessages = results.total;

					if (results.content == null)
					{
						return;
					}

					if (results.content instanceof Array)
					{
						$scope.messages = results.content;
					}
					else
					{
						var arr = new Array();
						arr[0] = results.content;
						$scope.messages = arr;
					}
				},
				function error(errors)
				{
					console.log(errors);
				});

		};

		$scope.updateReports = function updateReports()
		{
			//TODO: changed to return 5 since that is all we are using at the moment
			inboxService.getDashboardItems(5).then(
				function success(results)
				{
					if (results.content == null)
					{
						return;
					}

					if (results.content instanceof Array)
					{
						$scope.inbox = results.content;
					}
					else
					{
						var arr = new Array();
						arr[0] = results.content;
						$scope.inbox = arr;
					}
					$scope.totalInbox = results.total;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		// Remove? 
		// $scope.updateFeed = function updateFeed(startPoint, numberOfRows)
		// {
		// 	if ($scope.busyLoadingData) return;
		// 	$scope.busyLoadingData = true;
		// 	k2aService.getK2aFeed(startPoint, numberOfRows).then(
		// 		function(response)
		// 		{
		// 			if (response.post == null)
		// 			{
		// 				return;
		// 			}

		// 			if (response.post instanceof Array)
		// 			{
		// 				for (var i = 0; i < response.post.length; i++)
		// 				{
		// 					if (!Array.isArray(response.post[i].comments))
		// 					{
		// 						var arr = new Array();
		// 						arr[0] = response.post[i].comments;
		// 						response.post[i].comments = arr;
		// 					}
		// 				}
		// 				if (typeof $scope.k2afeed === 'undefined')
		// 				{
		// 					$scope.k2afeed = response.post;
		// 				}
		// 				else
		// 				{
		// 					$scope.k2afeed = $scope.k2afeed.concat(response.post);
		// 				}
		// 				$scope.busyLoadingData = false;
		// 			}
		// 			else
		// 			{
		// 				if (response.post.authenticatek2a)
		// 				{
		// 					$scope.authenticatek2a = response.post.description;
		// 				}
		// 				else
		// 				{
		// 					var arr = new Array();
		// 					arr[0] = response.post;
		// 					$scope.k2afeed = arr;
		// 				}
		// 			}
		// 		},
		// 		function(reason)
		// 		{
		// 			alert(reason);
		// 			$scope.busyLoadingData = false;
		// 		});
		// };

		$scope.updateDashboard = function updateDashboard()
		{
			$scope.updateTicklers();
			$scope.updateMessages();
			$scope.updateReports();
			$scope.updateFeed(0, 10);

		};

		$scope.$watch(function()
		{
			return securityService.getUser();
		}, function(newVal)
		{
			$scope.me = newVal;

			if (newVal != null)
			{
				$scope.updateDashboard();
			}
		}, true);


		$scope.isTicklerExpiredOrHighPriority = function isTicklerExpiredOrHighPriority(tickler)
		{
			var ticklerDate = Date.parse(tickler.serviceDate);
			var now = new Date();
			var result = false;
			if (ticklerDate < now)
			{
				result = true;
			}
			if (tickler.priority == 'High')
			{
				result = true;
			}

			return result;
		};

		$scope.isTicklerHighPriority = function isTicklerHighPriority(tickler)
		{
			var ticklerDate = Date.parse(tickler.serviceDate);
			var now = new Date();
			var result = false;

			if (tickler.priority == 'High')
			{
				result = true;
			}

			return result;
		};

		$scope.openClassicMessenger = function openClassicMessenger()
		{
			if ($scope.me != null)
			{
				window.open('../oscarMessenger/DisplayMessages.do?providerNo=' + $scope.me.providerNo, 'msgs', 'height=700,width=1024,scrollbars=1');
			}
		};

		$scope.viewMessage = function viewMessage(message)
		{
			window.open('../oscarMessenger/ViewMessage.do?messageID=' + message.id + '&boxType=0', 'msg' + message.id, 'height=700,width=1024,scrollbars=1');
		};

		$scope.viewTickler = function viewTickler(tickler)
		{
			var modalInstance = $uibModal.open(
			{
				templateUrl: 'tickler/ticklerView.jsp',
				controller: 'Tickler.TicklerViewCtrl',
				backdrop: false,
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
						return $scope.ticklerWriteAccess;
					},
					me: function()
					{
						return $scope.me;
					}
				}
			});

			modalInstance.result.then(
				function success(results)
				{
					//console.log('data from modalInstance '+data);
					if (results != null && results == true)
					{
						$scope.updateTicklers();
					}
				},
				function error(errors)
				{
					console.log(errors);
				});

		};

		$scope.configureTicklers = function configureTicklers()
		{
			var modalInstance = $uibModal.open(
			{
				templateUrl: 'tickler/configureDashboard.jsp',
				controller: 'TicklerConfigureController',
				backdrop: false,
				size: 'lg',
				resolve:
				{
					prefs: function()
					{
						return personaService.getDashboardPreferences();
					}
				}
			});

			modalInstance.result.then(
				function success(results)
				{
					if (results == true)
					{
						$scope.updateTicklers();
						personaService.getDashboardPreferences().then(
							function(results)
							{
								$scope.prefs = results.dashboardPreferences;
							});
					}
				},
				function error(errors)
				{
					console.log(errors);
				});

		};
	}
]);