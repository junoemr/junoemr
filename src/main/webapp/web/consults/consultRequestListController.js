angular.module('Consults').controller('Consults.ConsultRequestListController', [

	'$scope',
	'$timeout',
	'$state',
	'$location',
	// '$defer',
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
		// $defer,
		NgTableParams,
		consultService,
		providerService,
		demographicService,
		securityService,
		staticDataService)
	{

		//get access rights
		securityService.hasRight("_con", "r").then(
			function success(results)
			{
				$scope.consultReadAccess = results;
			},
			function error(errors)
			{
				console.log(errors);
			});
		securityService.hasRight("_con", "u").then(
			function success(results)
			{
				$scope.consultUpdateAccess = results; //to be used with batch operations (not yet implemented)
			},
			function error(errors)
			{
				console.log(errors);
			});
		securityService.hasRight("_con", "w").then(
			function success(results)
			{
				$scope.consultWriteAccess = results;
			},
			function error(errors)
			{
				console.log(errors);
			});

		//set search statuses
		$scope.statuses = staticDataService.getConsultRequestStatuses();

		//get urgencies list
		$scope.urgencies = staticDataService.getConsultUrgencies();

		$scope.lastResponse = "";
		$scope.teams = [];
		$scope.consult = {};

		var allTeams = "All Teams";
		$scope.search = {
			team: allTeams,
			startIndex: 0,
			numToReturn: 10
		};

		providerService.getActiveTeams().then(
			function success(results)
			{
				$scope.teams = results;
				$scope.teams.unshift(allTeams);
				console.log(JSON.stringify(results));
			},
			function error(errors)
			{
				console.log(errors);
			});

		$scope.searchPatients = function searchPatients(term)
		{
			var search = {
				type: 'Name',
				'term': term,
				active: true,
				integrator: false,
				outofdomain: true
			};
			return demographicService.search(search, 0, 25).then(
				function success(response)
				{
					var resp = [];
					for (var x = 0; x < response.content.length; x++)
					{
						resp.push(
						{
							demographicNo: response.content[x].demographicNo,
							name: response.content[x].lastName + ', ' + response.content[x].firstName
						});
					}
					return resp;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		$scope.searchMrps = function searchMrps(term)
		{
			var search = {
				searchTerm: term,
				active: true
			};
			return providerService.searchProviders(search).then(
				function success(response)
				{
					var resp = [];
					for (var x = 0; x < response.length; x++)
					{
						resp.push(
						{
							mrpNo: response[x].providerNo,
							name: response[x].name
						});
					}
					return resp;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		$scope.updateMrpNo = function updateMrpNo(model)
		{
			if (model instanceof Object)
			{ //mrp set in search box
				$scope.search.mrpNo = Number(model.mrpNo);
				$scope.consult.mrpName = model.name;
			}
			else
			{ //mrp specified in url (come back from another consults) 
				providerService.getProvider(model).then(
					function success(results)
					{
						$scope.consult.mrpName = results.lastName + ", " + results.firstName;
					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		};

		$scope.updateDemographicNo = function updateDemographicNo(item, model)
		{
			if (item != null)
			{ //demo set in search box
				$scope.search.demographicNo = item.demographicNo;
				$scope.consult.demographicName = item.name;
			}
			else
			{ //demo specified in url (come back from another consults)
				demographicService.getDemographic(model).then(
					function success(results)
					{
						$scope.consult.demographicName = results.lastName + ", " + results.firstName;
					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		};

		$scope.checkAll = function checkAll()
		{
			angular.forEach($scope.lastResponse, function(item)
			{
				item.checked = true;
			});
		};

		$scope.checkNone = function checkNone()
		{
			angular.forEach($scope.lastResponse, function(item)
			{
				item.checked = false;
			});
		};

		$scope.editConsult = function editConsult(consult)
		{
			var url = "/record/" + $scope.search.demographicNo + "/consult/new";
			if (consult != "new") url = "/record/" + consult.demographic.demographicNo + "/consult/" + consult.id;

			$location.path(url).search($scope.searchParams);
		};

		$scope.addConsult = function addConsult()
		{
			if (!$scope.consultWriteAccess)
			{
				alert("You don't have right to create new consult");
				return false;
			}

			$scope.editConsult("new");
		};

		$scope.removeDemographicAssignment = function removeDemographicAssignment()
		{
			$scope.search.demographicNo = null;
			$scope.consult.demographicName = null;
		};

		$scope.removeMrpAssignment = function removeMrpAssignment()
		{
			$scope.search.mrpNo = null;
			$scope.consult.mrpName = null;
		};

		$scope.doSearch = function doSearch()
		{
			$scope.tableParams.page = 1;
			$scope.tableParams.reload();
		};

		$scope.clear = function clear()
		{
			$scope.removeDemographicAssignment();
			$scope.removeMrpAssignment();

			var searchDemoNo = $state.params.demographicNo == null ? null : Number($state.params.demographicNo);
			$scope.search = {
				team: allTeams,
				startIndex: 0,
				numToReturn: 10,
				demographicNo: searchDemoNo
			};
			$scope.doSearch();
		};

		//retain search & filters for users to come back
		$scope.setSearchParams = function setSearchParams()
		{
			$scope.searchParams = {};
			if ($state.$current == "record.consultRequests") $scope.searchParams.list = "patient";
			else if ($scope.search.demographicNo != null) $scope.searchParams.srhDemoNo = $scope.search.demographicNo;

			if ($scope.search.mrpNo != null) $scope.searchParams.srhMrpNo = $scope.search.mrpNo;
			if ($scope.search.status != null) $scope.searchParams.srhStatus = $scope.search.status;
			if ($scope.search.team != allTeams) $scope.searchParams.srhTeam = $scope.search.team;
			if ($scope.search.referralStartDate != null) $scope.searchParams.srhRefStartDate = $scope.search.referralStartDate.getTime();
			if ($scope.search.referralEndDate != null) $scope.searchParams.srhRefEndDate = $scope.search.referralEndDate.getTime();
			if ($scope.search.appointmentStartDate != null) $scope.searchParams.srhApptStartDate = $scope.search.appointmentStartDate.getTime();
			if ($scope.search.appointmentEndDate != null) $scope.searchParams.srhApptEndDate = $scope.search.appointmentEndDate.getTime();

			if ($scope.tableParams.page > 1) $scope.searchParams.srhToPage = $scope.tableParams.$params.page;
			if ($scope.tableParams.count > 10) $scope.searchParams.srhCountPerPage = $scope.tableParams.$params.count;
			if ($scope.tableParams.sorting["ReferralDate"] != "desc")
			{
				$scope.searchParams.srhSortMode = Object.keys($scope.tableParams.sorting);
				$scope.searchParams.srhSortDir = $scope.tableParams.sorting[$scope.searchParams.srhSortMode];
			}
		};

		$scope.justOpen = true;

		$scope.tableParams = new NgTableParams(
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
			getData: function(params)
			{
				if ($scope.justOpen)
				{
					//process demographicNo in url, run only once
					if ($state.params.demographicNo != null)
					{
						//called from patient record
						$scope.hideSearchPatient = true;
						$scope.search.demographicNo = Number($state.params.demographicNo);
					}
					else if ($location.search().srhDemoNo != null)
					{
						//come back from another consults
						$scope.search.demographicNo = Number($location.search().srhDemoNo);
						$scope.updateDemographicNo(null, $location.search().srhDemoNo);
					}

					//process other search parameters in url
					if ($location.search().srhMrpNo != null)
					{
						$scope.search.mrpNo = Number($location.search().srhMrpNo);
						$scope.updateMrpNo($location.search().srhMrpNo);
					}
					if ($location.search().srhRefStartDate != null) $scope.search.referralStartDate = new Date(Number($location.search().srhRefStartDate));
					if ($location.search().srhRefEndDate != null) $scope.search.referralEndDate = new Date(Number($location.search().srhRefEndDate));
					if ($location.search().srhApptStartDate != null) $scope.search.appointmentStartDate = new Date(Number($location.search().srhApptStartDate));
					if ($location.search().srhApptEndDate != null) $scope.search.appointmentEndDate = new Date(Number($location.search().srhApptEndDate));
					if ($location.search().srhStatus != null) $scope.search.status = Number($location.search().srhStatus);
					if ($location.search().srhTeam != null) $scope.search.team = $location.search().srhTeam;

					if ($location.search().srhCountPerPage != null) $scope.tableParams.$params.count = $location.search().srhCountPerPage;
					if ($location.search().srhToPage != null) $scope.tableParams.$params.page = $location.search().srhToPage;

					if ($location.search().srhSortMode != null && $location.search().srhSortDir != null)
					{
						$scope.sortMode = {};
						$scope.sortMode[$location.search().srhSortMode] = $location.search().srhSortDir;
						$scope.tableParams.$params.sorting = $scope.sortMode;
					}
					$scope.justOpen = false;
				}

				$scope.setSearchParams();

				var count = params.url().count;
				var page = params.url().page;

				$scope.search.startIndex = ((page - 1) * count);
				$scope.search.numToReturn = parseInt(count);

				var search1 = angular.copy($scope.search);
				search1.params = params.url();

				if (search1.team === allTeams)
				{
					search1.team = null;
				}

				consultService.searchRequests(search1).then(
					function success(result)
					{
						params.total(result.total);
						// $defer.resolve(result.content);

						for (var i = 0; i < result.content.length; i++)
						{
							var consult = result.content[i];

							//add statusDescription
							for (var j = 0; j < $scope.statuses.length; j++)
							{
								if (consult.status == $scope.statuses[j].value)
								{
									consult.statusDescription = $scope.statuses[j].name;
									break;
								}
							}

							//add urgencyDescription
							for (var j = 0; j < $scope.urgencies.length; j++)
							{
								if (consult.urgency == $scope.urgencies[j].value)
								{
									consult.urgencyDescription = $scope.urgencies[j].name;
									break;
								}
							}

							//add urgencyColor if consult urgency=Urgent(1)
							if (consult.urgency == 1)
							{
								consult.urgencyColor = "text-danger"; //= red text
							}

							//add notification if outstanding (incomplete requests > 1 month)
							if (consult.status != 4 && consult.status != 5 && consult.status != 7)
							{
								var dp = consult.referralDate.split("-");
								var rDate = new Date(dp[0], dp[1] - 1, dp[2]);
								rDate.setMonth(rDate.getMonth() + 1);
								if ((new Date()) >= rDate) consult.outstanding = true;
							}
						}
						$scope.lastResponse = result.content;

					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		});

		$scope.popup = function popup(vheight, vwidth, varpage, winname)
		{
			var page = varpage;
			windowprops = "height=" + vheight + ",width=" + vwidth + ",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=0,screenY=0,top=0,left=0";
			var popup = window.open(varpage, winname, windowprops);
			if (popup != null)
			{
				if (popup.opener == null)
				{
					popup.opener = self;
				}
			}
		};

	}
]);