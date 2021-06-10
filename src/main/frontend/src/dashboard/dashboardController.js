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
    '$interval',
    'NgTableParams',
    'providerService',
    'ticklerService',
    'messageService',
    'inboxService',
    'k2aService',
    'noteService',
    'securityService',
    'personaService',

    function($scope,
             $uibModal,
             $interval,
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

        var controller = this;

        // Intervals for periodic updates
        controller.dashboardInterval = undefined;
        controller.dashboardMessageInterval = undefined;

        // Interval takes update times in ms, so 60s * 1000 * num_minutes
        controller.intervalLengthOneMinute = 60000;
        controller.intervalLengthFiveMinutes = 60000 * 5;

        //header
        controller.displayDate = function displayDate()
        {
            return new Date();
        };
        controller.me = null;
        controller.k2aActive = false;
	    controller.k2aFeedActive = false;

	    controller.busyLoadingData = false;

        personaService.getDashboardPreferences().then(
            function success(results)
            {
                controller.prefs = results.dashboardPreferences;
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
                    controller.ticklerWriteAccess = results.content[0];
                    controller.ticklerReadAccess = results.content[1];
                }
            },
            function error(errors)
            {
                console.log(errors);
            });

        controller.inboxTableParams = new NgTableParams(
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

        controller.openInbox = function openInbox()
        {
            newwindow = window.open('../dms/inboxManage.do?method=prepareForIndexPage', 'inbox', 'height=700,width=1000');
            if (window.focus)
            {
                newwindow.focus();
            }
        };
        
	    controller.updateK2aActive = async () =>
	    {
	    	controller.k2aActive = await k2aService.isK2AInit();
	    };
	    
	    controller.loadMoreK2aFeed = function ()
	    {
		    controller.updateFeed(controller.k2afeed.length, 10);
	    };

	    controller.authenticateK2A = function (id)
	    {
		    window.open('../apps/oauth1.jsp?id=' + id, 'appAuth', 'width=700,height=450');
	    };

	    controller.agreeWithK2aPost = function (item)
	    {
		    if (item.agree)
		    {
			    k2aService.removeK2AComment(item.agreeId).then(
				    function (response)
				    {
					    item.agree = false;
					    item.agreeCount--;
					    item.agreeId = '';
				    },
				    function (reason)
				    {
					    alert(reason);
				    });
		    }
		    else if (!(item.agree || item.disagree))
		    {
			    if (typeof item.newComment === 'undefined')
			    {
				    item.newComment = {};
			    }
			    item.newComment.agree = true;
			    item.newComment.body = '';

			    controller.commentOnK2aPost(item);
		    }
	    };

	    controller.disagreeWithK2aPost = function (item)
	    {
		    if (item.disagree)
		    {
			    k2aService.removeK2AComment(item.agreeId).then(
				    function (response)
				    {
					    item.disagree = false;
					    item.disagreeCount--;
					    item.agreeId = '';
				    },
				    function (reason)
				    {
					    alert(reason);
				    });
		    }
		    if (!(item.agree || item.disagree))
		    {
			    if (typeof item.newComment === 'undefined')
			    {
				    item.newComment = {};
			    }
			    item.newComment.agree = false;
			    item.newComment.body = '';

			    controller.commentOnK2aPost(item);
		    }
	    };

	    controller.commentOnK2aPost = function (item)
	    {
		    item.newComment.postId = item.id;
		    k2aService.postK2AComment(item.newComment).then(
			    function (response)
			    {
				    item.newComment.body = '';
				    item.newComment.agree = '';
				    item.agreeId = response.agreeId;
				    if (!(typeof response.post[0].agree === 'undefined'))
				    {
					    if (response.post[0].agree)
					    {
						    item.agree = true;
						    item.agreeId = response.post[0].agreeId;
						    item.agreeCount++;
					    }
					    else
					    {
						    item.disagree = true;
						    item.agreeId = response.post[0].agreeId;
						    item.disagreeCount++;
					    }
				    }
				    else
				    {
					    item.commentCount++;
					    item.comments.unshift(response.post[0]);
				    }
			    },
			    function (reason)
			    {
				    alert(reason);
			    });
	    };

        controller.updateTicklers = function updateTicklers()
        {
            //consider the option to have overdue only or not
            ticklerService.search(
                {
                    status: 'A',
                    assignee: controller.me.providerNo,
                    overdueOnly: 'property'
                }, 0, 6).then(
                function success(results)
                {
                    controller.totalTicklers = results.total;
                    if (results.content == null)
                    {
                        return;
                    }

                    if (results.content instanceof Array)
                    {
                        controller.ticklers = results.content;
                    }
                    else
                    {
                        var arr = new Array();
                        arr[0] = results.content;
                        controller.ticklers = arr;
                    }
                },
                function error(errors)
                {
                    console.log(errors);
                });
        };

        controller.updateMessages = function updateMessages()
        {
            messageService.getUnread(6).then(
                function success(results)
                {
                    controller.totalMessages = results.total;

                    if (results.content == null)
                    {
                        return;
                    }

                    if (results.content instanceof Array)
                    {
                        controller.messages = results.content;
                    }
                    else
                    {
                        var arr = new Array();
                        arr[0] = results.content;
                        controller.messages = arr;
                    }
                },
                function error(errors)
                {
                    console.log(errors);
                });

        };

        controller.updateReports = function updateReports()
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
                        controller.inbox = results.content;
                    }
                    else
                    {
                        var arr = new Array();
                        arr[0] = results.content;
                        controller.inbox = arr;
                    }
                    controller.totalInbox = results.total;
                },
                function error(errors)
                {
                    console.log(errors);
                });
        };

        controller.updateFeed = function updateFeed(startPoint, numberOfRows)
        {
            if (!controller.k2aActive || controller.busyLoadingData)
            {
            	return;
            }
            
            controller.busyLoadingData = true;
            k2aService.getK2aFeed(startPoint, numberOfRows).then(
                function(response)
                {
	                if (response.content instanceof Array)
	                {
	                	var content = response.content;
		                for (var i = 0; i < content.length; i++)
		                {
			                if (!Array.isArray(content[i].comments))
			                {
				                var arr = new Array();
				                arr[0] = content[i].comments;
				                content[i].comments = arr;
			                }
		                }
		                if (typeof controller.k2afeed === 'undefined')
		                {
			                controller.k2afeed = content;
		                }
		                else
		                {
			                controller.k2afeed = controller.k2afeed.concat(content);
		                }
		                controller.k2aFeedActive = true;
		                controller.busyLoadingData = false;
	                }
	                else
	                {
		                if (response.content.authenticatek2a)
		                {
			                controller.authenticatek2a = response.content.description;
		                }
		                else
		                {
			                var arr = new Array();
			                arr[0] = response.content;
			                controller.k2afeed = arr;
			                controller.k2aFeedActive = true;
		                }
	                }

                },
                function(reason)
                {
                    alert(reason);
                    controller.busyLoadingData = false;
                });
        };

        controller.updateDashboard = function updateDashboard()
        {
            controller.updateTicklers();
            controller.updateReports();
	        controller.updateK2aActive();
	
	        if (controller.k2aActive)
	        {
		        controller.updateFeed(0, 10);
	        }
        };

        $scope.$watch(function()
        {
            return securityService.getUser();
        }, function(newVal)
        {
            controller.me = newVal;

            if (newVal != null)
            {
                controller.updateDashboard();
                controller.updateMessages();
            }

            if (!angular.isDefined(controller.dashboardInterval))
            {
                controller.dashboardInterval = $interval(function()
                {
                    controller.updateDashboard();
                }, controller.intervalLengthFiveMinutes);
            }

            if (!angular.isDefined(controller.dashboardMessageInterval))
            {
                controller.dashboardMessageInterval = $interval(function()
                {
                    controller.updateMessages();
                }, controller.intervalLengthOneMinute);
            }

        }, true);


        controller.isTicklerExpiredOrHighPriority = function isTicklerExpiredOrHighPriority(tickler)
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

        controller.isTicklerHighPriority = function isTicklerHighPriority(tickler)
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

        controller.openClassicMessenger = function openClassicMessenger()
        {
            if (controller.me != null)
            {
                window.open('../oscarMessenger/DisplayMessages.do?providerNo=' + controller.me.providerNo, 'msgs', 'height=700,width=1024,scrollbars=1');
            }
        };

        controller.viewMessage = function viewMessage(message)
        {
            window.open('../oscarMessenger/ViewMessage.do?messageID=' + message.id + '&boxType=0', 'msg' + message.id, 'height=700,width=1024,scrollbars=1');
        };

        controller.viewTickler = function viewTickler(tickler)
        {
            var modalInstance = $uibModal.open(
                {
                    templateUrl: 'src/tickler/ticklerView.jsp',
                    controller: 'Tickler.TicklerViewController as  ticklerViewCtrl',
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
                                return controller.me;
                            }
                        }
                });

            modalInstance.result.then(
                function success(results)
                {
                    //console.log('data from modalInstance '+data);
                    if (results != null && results == true)
                    {
                        controller.updateTicklers();
                    }
                },
                function error(errors)
                {
                    console.log(errors);
                });

        };

        controller.configureTicklers = function configureTicklers()
        {
            var modalInstance = $uibModal.open(
                {
                    templateUrl: 'src/tickler/configureDashboard.jsp',
                    controller: 'Dashboard.TicklerConfigureController as ticklerConfigureCtrl',
                    backdrop: 'static',
                    size: 'md',
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
                        controller.updateTicklers();
                        personaService.getDashboardPreferences().then(
                            function(results)
                            {
                                controller.prefs = results.dashboardPreferences;
                            });
                    }
                },
                function error(errors)
                {
                    console.log(errors);
                });

        };

        // Destroy interval before controller closes to ensure background updates don't occur
        $scope.$on('$destroy', function()
        {
            if (angular.isDefined(controller.dashboardInterval))
            {
                $interval.cancel(controller.dashboardInterval);
                controller.dashboardInterval = undefined;
            }

            if (angular.isDefined(controller.dashboardMessageInterval))
            {
                $interval.cancel(controller.dashboardMessageInterval);
                controller.dashboardMessageInterval = undefined;
            }
        })
    }
]);