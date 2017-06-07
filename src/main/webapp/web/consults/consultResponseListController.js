angular.module('Consults').controller('Consults.ConsultResponseListController', [

	'$scope',
	'$timeout',
	'$state',
	'$location',
	'NgTableParams',
	'consultService',
	'providerService',
	'demographicService',
	'securityService',
	'staticDataService',

	function(
		$scope,
		$timeout,
		$state,
		$location,
		NgTableParams,
		consultService,
		providerService,
		demographicService,
		securityService,
		staticDataService)
	{

		var controller = this;

		//get access rights
		securityService.hasRight("_con", "r").then(
			function success(results)
			{
				controller.consultReadAccess = results;
			},
			function error(errors)
			{
				console.log(errors);
			});
		securityService.hasRight("_con", "u").then(
			function success(results)
			{
				controller.consultUpdateAccess = results; //to be used with batch operations (not yet implemented)
			},
			function error(errors)
			{
				console.log(errors);
			});
		securityService.hasRight("_con", "w").then(
			function success(results)
			{
				controller.consultWriteAccess = results;
			},
			function error(errors)
			{
				console.log(errors);
			});

		//set search statuses
		controller.statuses = staticDataService.getConsultResponseStatuses();

		//get urgencies list
		controller.urgencies = staticDataService.getConsultUrgencies();

		controller.lastResponse = "";
		controller.teams = [];
		controller.consult = {};

		var allTeams = "All Teams";
		controller.search = {
			team: allTeams,
			startIndex: 0,
			numToReturn: 10
		};

		providerService.getActiveTeams().then(
			function success(results)
			{
				controller.teams = results;
				controller.teams.unshift(allTeams);
				console.log(JSON.stringify(results));
			},
			function error(errors)
			{
				console.log(errors);
			});

		controller.searchPatients = function searchPatients(term)
		{
			var search = {
				type: 'Name',
				'term': term,
				active: true,
				integrator: false,
				outofdomain: true
			};
			return demographicService.search(search, 0, 25).then(
				function success(results)
				{
					var resp = [];
					for (var x = 0; x < results.content.length; x++)
					{
						resp.push(
						{
							demographicNo: results.content[x].demographicNo,
							name: results.content[x].lastName + ', ' + results.content[x].firstName
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
			if (item != null)
			{
				controller.search.demographicNo = item.demographicNo;
				controller.consult.demographicName = item.name;
			}
			else
			{
				demographicService.getDemographic(model).then(
					function success(results)
					{
						controller.search.demographicNo = results.demographicNo;
						controller.consult.demographicName = results.lastName + ", " + results.firstName;
						controller.doSearch(true);
					},
					function error(errors)
					{
						console.log(errors);
					});
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
			var url = "/record/" + controller.search.demographicNo + "/consultResponse/new";
			if (consult != "new") url = "/record/" + consult.demographic.demographicNo + "/consultResponse/" + consult.id;

			$location.path(url).search(controller.searchParams);
		};

		controller.addConsult = function addConsult()
		{
			if (!controller.consultWriteAccess)
			{
				alert("You don't have right to create new consult response");
				return false;
			}

			controller.editConsult("new");
		};

		controller.removeDemographicAssignment = function removeDemographicAssignment()
		{
			controller.search.demographicNo = null;
			controller.consult.demographicName = null;
		};

		controller.removeMrpAssignment = function removeMrpAssignment()
		{
			controller.search.mrpNo = null;
			controller.consult.mrpName = null;
		};

		controller.clear = function clear()
		{
			controller.removeDemographicAssignment();
			controller.removeMrpAssignment();
			controller.search = {
				team: allTeams,
				startIndex: 0,
				numToReturn: 10,
				demographicNo: parseInt($state.params.demographicNo)
			};
			controller.doSearch();
		};

		controller.doSearch = function doSearch(init)
		{
			if (init)
			{
				controller.tableParams.reload();
			}
			else
			{
				controller.toPage = 1;
				controller.tableParams.reload();
				controller.toPage = null;
			}
		};

		//retain search & filters for users to go back
		controller.setSearchParams = function setSearchParams()
		{
			controller.searchParams = {};
			if ($state.$current == "record.consultResponses") controller.searchParams.list = "patient";
			else if (controller.search.demographicNo != null) controller.searchParams.srhDemoNo = controller.search.demographicNo;

			if (controller.search.mrpNo != null) controller.searchParams.srhMrpNo = controller.search.mrpNo;
			if (controller.search.status != null) controller.searchParams.srhStatus = controller.search.status;
			if (controller.search.team != allTeams) controller.searchParams.srhTeam = controller.search.team;
			if (controller.search.referralStartDate != null) controller.searchParams.srhRefStartDate = controller.search.referralStartDate.getTime();
			if (controller.search.referralEndDate != null) controller.searchParams.srhRefEndDate = controller.search.referralEndDate.getTime();
			if (controller.search.appointmentStartDate != null) controller.searchParams.srhApptStartDate = controller.search.appointmentStartDate.getTime();
			if (controller.search.appointmentEndDate != null) controller.searchParams.srhApptEndDate = controller.search.appointmentEndDate.getTime();

			if (controller.tableParams.$params.page > 1) controller.searchParams.srhToPage = controller.tableParams.$params.page;
			if (controller.tableParams.$params.count > 10) controller.searchParams.srhCountPerPage = controller.tableParams.$params.count;
			if (controller.tableParams.$params.sorting["ReferralDate"] != "desc")
			{
				controller.searchParams.srhSortMode = Object.keys(controller.tableParams.$params.sorting);
				controller.searchParams.srhSortDir = controller.tableParams.$params.sorting[controller.searchParams.srhSortMode];
			}
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
			total: 0, // length of data
			getData: function($defer, params)
			{
				if (controller.toPage != null) controller.tableParams.$params.page = controller.toPage;
				if (controller.countPerPage != null) controller.tableParams.$params.count = controller.countPerPage;
				if (controller.sortMode != null) controller.tableParams.$params.sorting = controller.sortMode;
				controller.setSearchParams();

				var count = params.url().count;
				var page = params.url().page;

				controller.search.startIndex = ((page - 1) * count);
				controller.search.numToReturn = parseInt(count);

				var search1 = angular.copy(controller.search);
				search1.params = params.url();

				if (search1.team === allTeams)
				{
					search1.team = null;
				}

				consultService.searchResponses(search1).then(
					function success(results)
					{
						params.total(results.total);
						$defer.resolve(results.content);

						for (var i = 0; i < results.content.length; i++)
						{
							var consult = results.content[i];

							//add statusDescription
							for (var j = 0; j < controller.statuses.length; j++)
							{
								if (consult.status == controller.statuses[j].value)
								{
									consult.statusDescription = controller.statuses[j].name;
									break;
								}
							}

							//add urgencyDescription
							for (var j = 0; j < controller.urgencies.length; j++)
							{
								if (consult.urgency == controller.urgencies[j].value)
								{
									consult.urgencyDescription = controller.urgencies[j].name;
									break;
								}
							}

							//add urgencyColor if consult urgency=Urgent(1)
							if (consult.urgency == 1)
							{
								consult.urgencyColor = "text-danger"; //= red text
							}
						}
						controller.lastResponse = results.content;

					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		});

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
		if ($location.search().srhRefStartDate != null) controller.search.referralStartDate = new Date(Number($location.search().srhRefStartDate));
		if ($location.search().srhRefEndDate != null) controller.search.referralEndDate = new Date(Number($location.search().srhRefEndDate));
		if ($location.search().srhApptStartDate != null) controller.search.appointmentStartDate = new Date(Number($location.search().srhApptStartDate));
		if ($location.search().srhApptEndDate != null) controller.search.appointmentEndDate = new Date(Number($location.search().srhApptEndDate));
		if ($location.search().srhStatus != null) controller.search.status = Number($location.search().srhStatus);
		if ($location.search().srhTeam != null) controller.search.team = $location.search().srhTeam;
		if ($location.search().srhCountPerPage != null) controller.countPerPage = $location.search().srhCountPerPage;
		if ($location.search().srhToPage != null) controller.toPage = $location.search().srhToPage;
		if ($location.search().srhSortMode != null && $location.search().srhSortDir != null)
		{
			controller.sortMode = {};
			controller.sortMode[$location.search().srhSortMode] = $location.search().srhSortDir;
		}
		controller.doSearch(true);
		controller.countPerPage = null;
		controller.toPage = null;
		controller.sortMode = null;

	}
]);