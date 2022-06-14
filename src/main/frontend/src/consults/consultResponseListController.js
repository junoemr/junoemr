import {SecurityPermissions} from "../common/security/securityConstants";

angular.module('Consults').controller('Consults.ConsultResponseListController', [

	'$scope',
	'$timeout',
	'$state',
	'$location',
	'NgTableParams',
	'consultService',
	'providerService',
	'demographicsService',
	'securityRolesService',
	'staticDataService',

	function(
		$scope,
		$timeout,
		$state,
		$location,
		NgTableParams,
		consultService,
		providerService,
		demographicsService,
		securityRolesService,
		staticDataService)
	{

		var controller = this;

		controller.onRecordPage = false;
		controller.demographicNo = null;
		controller.statuses = staticDataService.getConsultResponseStatuses(); 	//set search statuses
		controller.urgencies = staticDataService.getConsultUrgencies(); //get urgencies list
		controller.lastResponse = "";
		controller.teams = [];
		controller.consult = {};

		var allTeams = "All Teams";
		controller.search = {
			team: allTeams,
			startIndex: 0,
			numToReturn: 10
		};

		controller.SecurityPermissions = SecurityPermissions;

		// Initialize the controller
		controller.$onInit = () =>
		{
			if($state.params.demographicNo){
				controller.demographicNo = parseInt($state.params.demographicNo);
				controller.onRecordPage = true;
				controller.search.list = "patient";
			}

			controller.getTeams();
		};

		controller.canEditConsults = () =>
		{
			return securityRolesService.hasSecurityPrivileges(SecurityPermissions.ConsultationUpdate);
		}
		controller.canCreateConsults = () =>
		{
			return securityRolesService.hasSecurityPrivileges(SecurityPermissions.ConsultationCreate);
		}
		controller.canAccessDemographics = () =>
		{
			return securityRolesService.hasSecurityPrivileges(SecurityPermissions.DemographicRead);
		}

		controller.getTeams = function getTeams()
		{
			providerService.getActiveTeams().then(
				function success(results)
				{
					controller.teams = results;
					controller.teams.unshift(allTeams);
					controller.search.team = allTeams;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.searchPatients = function searchPatients(term)
		{
			var search = {
				type: demographicsService.SEARCH_MODE.Name,
				term: term,
				status: demographicsService.STATUS_MODE.ACTIVE,
				integrator: false,
				outofdomain: true
			};
			return demographicsService.search(search, 0, 25).then(
				function success(results)
				{
					var resp = [];
					for (var x = 0; x < results.data.length; x++)
					{
						resp.push(
						{
							demographicNo: results.data[x].demographicNo,
							name: results.data[x].lastName + ', ' + results.data[x].firstName
						});
					}
					return resp;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.searchMrps = function searchMrps(term)
		{
			var search = {
				searchTerm: term,
				active: true
			};
			return providerService.searchProviders(search).then(
				function success(results)
				{
					var resp = [];
					for (var x = 0; x < results.length; x++)
					{
						resp.push(
						{
							mrpNo: results[x].providerNo,
							name: results[x].name
						});
					}
					return resp;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.updateMrpNo = function updateMrpNo(model)
		{
			if (Juno.Common.Util.exists(model))
			{
				controller.search.mrpNo = Number(model.mrpNo);
				controller.consult.mrpName = model.name;
			}
			else
			{
				providerService.getProvider(model).then(
					function success(results)
					{
						controller.search.mrpNo = Number(model);
						controller.consult.mrpName = results.lastName + ", " + results.firstName;
						controller.doSearch(true);
					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		};

		controller.updateDemographicNo = function updateDemographicNo(item, model, label)
		{
			if(item !== null)
			{
				controller.demographicName = item.name;
				controller.demographicNo = item.demographicNo;
				controller.tableParams.reload();
			}
		};

		controller.checkAll = function checkAll()
		{
			angular.forEach(controller.lastResponse, function(item)
			{
				item.checked = true;
			});
		};

		controller.checkNone = function checkNone()
		{
			angular.forEach(controller.lastResponse, function(item)
			{
				item.checked = false;
			});
		};

		controller.editConsult = function editConsult(consult)
		{
			if(consult)
			{
				url = "/record/" + consult.demographic.demographicNo + "/consultResponse/" + consult.id;
				$location.path(url).search(controller.search);
				return true;
			}

			alert("Something went wrong");
			return false;
		};

		controller.addConsult = function addConsult()
		{
			if (!controller.canCreateConsults())
			{
				alert("You don't have right to create new consult response");
				return false;
			}

			// User shouldn't be able to access this function unless demoNo is populated, but check just in case
			if(controller.demographicNo)
			{
				var url = "/record/" + controller.demographicNo + "/consultResponse/new";
				$location.path(url).search(controller.search);
				return true;
			}

			alert("Something went wrong");
			return false;
		};

		controller.removeDemographicAssignment = function removeDemographicAssignment()
		{
			controller.demographicNo = null;
			controller.demographicName = null;
			controller.tableParams.reload();
		};

		controller.removeMrpAssignment = function removeMrpAssignment()
		{
			controller.search.mrpNo = null;
			controller.consult.mrpName = null;
			controller.tableParams.reload();
		};

		controller.clear = function clear()
		{
			if(!controller.onRecordPage)
				controller.removeDemographicAssignment();
			controller.removeMrpAssignment();
			controller.search = {
				team: allTeams,
				startIndex: 0,
				numToReturn: 10,
			};

			controller.doSearch();
		};

		controller.doSearch = function doSearch(init)
		{
			$location.search(controller.search);
			controller.tableParams.reload();
		};

		controller.tableParams = new NgTableParams(
		{
			page: 1, // show first page
			count: 10, // initial count per page
			sorting:
			{
				ReferralDate: 'desc' // initial sorting
			}
		},
		{
			// total: 0, // length of data
			getData: function(params)
			{
				controller.getSavedSearchParams();
		
				var count = params._params.count;
				var page = params.url().page;

				controller.search.startIndex = ((page - 1) * count);
				controller.search.numToReturn = parseInt(count);

				controller.search.page = params.url().page;
				controller.search.perPage = params.url().count;

				var myRegexp = /sorting\[(\w+)\]/g;
				for(var key in params.url()) {
					var match = myRegexp.exec(String(key));
					if(match) {
						controller.search.sortColumn = match[1];
						controller.search.sortDirection = params.url()[String(key)];
					}
				}

				var tmpSearch = angular.copy(controller.search);

				if (tmpSearch.team === allTeams)
				{
					tmpSearch.team = null;
				}

				if(controller.demographicNo)
				{
					tmpSearch.demographicNo = controller.demographicNo;
				}

				return consultService.searchResponses(tmpSearch).then(
					function success(results)
					{
						params.total(parseInt(results.meta.total[0]));

						for (var i = 0; i < results.data.length; i++)
						{
							var consult = results.data[i];

							//add statusDescription
							for (var j = 0; j < controller.statuses.length; j++)
							{
								if (consult.status === controller.statuses[j].value)
								{
									consult.statusDescription = controller.statuses[j].label;
									break;
								}
							}

							//add urgencyDescription
							for (var j = 0; j < controller.urgencies.length; j++)
							{
								if (consult.urgency === controller.urgencies[j].value)
								{
									consult.urgencyDescription = controller.urgencies[j].label;
									break;
								}
							}

							//add urgencyColor if consult urgency=Urgent(1)
							if (consult.urgency === 1)
							{
								consult.urgencyColor = "text-danger"; //= red text
							}
						}

						controller.lastResponse = results.data;
						return results.data;
					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		});

		controller.getSavedSearchParams = function getSavedSearchParams()
		{
			//process search parameters
			if ($state.params.demographicNo != null)
			{
				controller.hideSearchPatient = true;
				controller.updateDemographicNo(null, $state.params.demographicNo);
			}
			else if ($location.search().srhDemoNo != null)
			{
				controller.updateDemographicNo(null, $location.search().srhDemoNo);
			}
			if ($location.search().srhMrpNo != null) controller.updateMrpNo($location.search().srhMrpNo);
			if ($location.search().referralStartDate) controller.search.referralStartDate = new Date($location.search().referralStartDate);
			if ($location.search().referralEndDate != null) controller.search.referralEndDate = new Date($location.search().referralEndDate);
			if ($location.search().appointmentStartDate != null) controller.search.appointmentStartDate = new Date($location.search().appointmentStartDate);
			if ($location.search().appointmentEndDate != null) controller.search.appointmentEndDate = new Date($location.search().appointmentEndDate);
			if ($location.search().status != null) controller.search.status = Number($location.search().status);
			if ($location.search().team != null) controller.search.team = $location.search().team;
			if ($location.search().countPerPage != null) controller.countPerPage = $location.search().countPerPage;
			if ($location.search().toPage != null) controller.toPage = $location.search().toPage;
			if ($location.search().sortColumn != null && $location.search().sortDirection != null)
			{
				controller.search.sortColumn = $location.search().sortColumn;
				controller.search.sortDirection = $location.search().sortDirection;
			}
		};
	}
]);