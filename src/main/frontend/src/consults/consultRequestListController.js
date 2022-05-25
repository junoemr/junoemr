import {ProviderPreferenceApi} from "../../generated";
import {SecurityPermissions} from "../common/security/securityConstants";

angular.module('Consults').controller('Consults.ConsultRequestListController', [
	'$http',
	'$httpParamSerializer',
	'$scope',
	'$timeout',
	'$state',
	'$location',
	'NgTableParams',
	'consultService',
	'providerService',
	'demographicService',
	'demographicsService',
	'securityRolesService',
	'staticDataService',

	function(
		$http,
		$httpParamSerializer,
		$scope,
		$timeout,
		$state,
		$location,
		NgTableParams,
		consultService,
		providerService,
		demographicService,
		demographicsService,
		securityRolesService,
		staticDataService)
	{

		var controller = this;
		let providerPreferenceApi = new ProviderPreferenceApi($http, $httpParamSerializer, '../ws/rs');


		//set search statuses
		controller.statuses = staticDataService.getConsultRequestStatuses();

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

		controller.SecurityPermissions = SecurityPermissions;

		controller.$onInit = () =>
		{
			providerService.getActiveTeams().then(
				function success(results)
				{
					controller.teams = results;
					controller.teams.unshift(allTeams);
				},
				function error(errors)
				{
					alert(errors);
					console.log(errors);
				});
		}

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
		controller.canAccessConsultConfig = () =>
		{
			return securityRolesService.hasSecurityPrivileges(SecurityPermissions.ConfigureConsultRead);
		}

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
				function success(response)
				{
					var resp = [];
					for (var x = 0; x < response.data.length; x++)
					{
						resp.push(
						{
							demographicNo: response.data[x].demographicNo,
							name: response.data[x].lastName + ', ' + response.data[x].firstName
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

		controller.updateMrpNo = function updateMrpNo(model)
		{
			if (model instanceof Object)
			{ //mrp set in search box
				controller.search.mrpNo = Number(model.mrpNo);
				controller.consult.mrpName = model.name;
			}
			else
			{ //mrp specified in url (come back from another consults) 
				providerService.getProvider(model).then(
					function success(results)
					{
						controller.consult.mrpName = results.lastName + ", " + results.firstName;
					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		};

		controller.updateDemographicNo = function updateDemographicNo(item, model)
		{
			if (item != null)
			{ //demo set in search box
				controller.search.demographicNo = item.demographicNo;
				controller.consult.demographicName = item.name;
			}
			else
			{ //demo specified in url (come back from another consults)
				demographicService.getDemographic(model).then(
					function success(results)
					{
						controller.consult.demographicName = results.displayName;
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
			var url = "/record/" + consult.demographic.demographicNo + "/consult/" + consult.id;
			$location.path(url).search(controller.searchParams);
		};

		controller.addConsult = function addConsult()
		{
			if (!controller.canCreateConsults())
			{
				alert("You don't have right to create new consult");
				return false;
			}
			var url = "/record/" + controller.search.demographicNo + "/consult/new";
			$location.path(url).search(controller.searchParams);
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

		controller.doSearch = function doSearch()
		{
			controller.tableParams.reload();
		};

		controller.clear = function clear()
		{
			controller.removeDemographicAssignment();
			controller.removeMrpAssignment();

			var searchDemoNo = $state.params.demographicNo == null ? null : Number($state.params.demographicNo);
			controller.search = {
				team: allTeams,
				startIndex: 0,
				numToReturn: 10,
				demographicNo: searchDemoNo
			};
			controller.doSearch();
		};

		//retain search & filters for users to come back
		controller.setSearchParams = function setSearchParams()
		{
			controller.searchParams = {};

			if ($state.$current.name === "record.consultRequests")
				controller.searchParams.list = "patient";
			else if (controller.search.demographicNo != null) controller.searchParams.srhDemoNo = controller.search.demographicNo;

			if (controller.search.mrpNo != null) controller.searchParams.srhMrpNo = controller.search.mrpNo;
			if (controller.search.status != null) controller.searchParams.srhStatus = controller.search.status;
			if (controller.search.team != allTeams) controller.searchParams.srhTeam = controller.search.team;
			if (controller.search.referralStartDate != null) controller.searchParams.srhRefStartDate = controller.search.referralStartDate.getTime();
			if (controller.search.referralEndDate != null) controller.searchParams.srhRefEndDate = controller.search.referralEndDate.getTime();
			if (controller.search.appointmentStartDate != null) controller.searchParams.srhApptStartDate = controller.search.appointmentStartDate.getTime();
			if (controller.search.appointmentEndDate != null) controller.searchParams.srhApptEndDate = controller.search.appointmentEndDate.getTime();

			if (controller.search.page != null) controller.searchParams.srhToPage = controller.search.page;
			if (controller.search.perPage != null) controller.searchParams.srhCountPerPage = controller.search.perPage;
			if (controller.search.sortDirection != null)
			{
				controller.searchParams.srhSortMode = controller.search.sortColumn;
				controller.searchParams.srhSortDir = controller.search.sortDirection;
			}
		};

		controller.justOpen = true;

		// default parameters
		controller.search.sortColumn="ReferralDate";
		controller.search.sortDirection="desc";

		controller.tableParams = new NgTableParams(
		{
			page: 1, // show first page
			count: 10, // initial count per page
			sorting: {
				ReferralDate: 'desc'
			}
		},
		{
			getData: function(params)
			{
				if (controller.justOpen)
				{
					controller.getSavedSearchVals();
					controller.justOpen = false;
				}

				controller.setSearchParams();

				var count = params.url().count;
				var page = params.url().page;

				// shouldn't need these anymore
				//TODO-legacy refactor out of all uses here (I think these are used elsewhere).
				controller.search.startIndex = ((page - 1) * count);
				controller.search.numToReturn = parseInt(count);

				controller.search.page = params.url().page;
				controller.search.perPage = params.url().count;

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

				// copy to the get parameters hash
				var search1 = angular.copy(controller.search);

				if (search1.team === allTeams)
				{
					search1.team = null;
				}

				return consultService.searchRequests(search1).then(
					async function success(result)
					{

						params.total(parseInt(result.meta.total));
						let numMonthsOutstanding = 1;
						let providerPreferences = await providerPreferenceApi.getAllProviderSettings();

						if (providerPreferences.data.content[0].consultationTimePeriodWarning
							&& Juno.Common.Util.isIntegerString(providerPreferences.data.content[0].consultationTimePeriodWarning))
						{
							numMonthsOutstanding = parseInt(providerPreferences.data.content[0].consultationTimePeriodWarning);
						}
						else
						{
							console.warn("Following is set as consultationTimePeriodWarning but cannot be parsed: "
								+ providerPreferences.data.content[0].consultationTimePeriodWarning);
						}

						for (var i = 0; i < result.data.length; i++)
						{
							var consult = result.data[i];


							//add statusDescription
							for (var j = 0; j < controller.statuses.length; j++)
							{
								if (consult.status == controller.statuses[j].value)
								{
									consult.statusDescription = controller.statuses[j].label;
									break;
								}
							}

							//add urgencyDescription
							for (var j = 0; j < controller.urgencies.length; j++)
							{
								if (consult.urgency == controller.urgencies[j].value)
								{
									consult.urgencyDescription = controller.urgencies[j].label;
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
								let refDate = moment(consult.referralDate).toDate();
								refDate.setMonth(refDate.getMonth() + numMonthsOutstanding);
								if ((new Date()) >= refDate)
								{
									consult.outstanding = true;
								}
							}
						}
						controller.lastResponse = result.data;
						return result.data;
					},
					function error(errors)
					{
						alert(errors);
						console.log(errors);
					});
			}

		});

		controller.getSavedSearchVals = function getSavedSearchVals()
		{
			//process demographicNo in url, run only once
			if ($state.params.demographicNo != null)
			{
				//called from patient record
				controller.hideSearchPatient = true;
				controller.search.demographicNo = Number($state.params.demographicNo);
			}
			else if ($location.search().srhDemoNo != null)
			{
				//come back from another consults
				controller.search.demographicNo = Number($location.search().srhDemoNo);
				controller.updateDemographicNo(null, $location.search().srhDemoNo);
			}

			//process other search parameters in url
			if ($location.search().srhMrpNo != null)
			{
				controller.search.mrpNo = Number($location.search().srhMrpNo);
				controller.updateMrpNo($location.search().srhMrpNo);
			}
			if ($location.search().srhRefStartDate != null) controller.search.referralStartDate = new Date(Number($location.search().srhRefStartDate));
			if ($location.search().srhRefEndDate != null) controller.search.referralEndDate = new Date(Number($location.search().srhRefEndDate));
			if ($location.search().srhApptStartDate != null) controller.search.appointmentStartDate = new Date(Number($location.search().srhApptStartDate));
			if ($location.search().srhApptEndDate != null) controller.search.appointmentEndDate = new Date(Number($location.search().srhApptEndDate));
			if ($location.search().srhStatus != null) controller.search.status = Number($location.search().srhStatus);
			if ($location.search().srhTeam != null) controller.search.team = $location.search().srhTeam;
			if ($location.search().srhCountPerPage != null) controller.search.perPage = $location.search().srhCountPerPage;

			if ($location.search().srhToPage != null) controller.search.page = $location.search().srhToPage;
			if ($location.search().srhSortMode != null && $location.search().srhSortDir != null)
			{
				controller.search.sortColumn = $location.search().sortMode;
				controller.search.sortDirection = $location.search().srhSortDir;
			}
		};

		controller.popup = function popup(vheight, vwidth, varpage, winname)
		{
			let windowprops = "height=" + vheight + ",width=" + vwidth + ",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=0,screenY=0,top=0,left=0";
			let popup = window.open(varpage, winname, windowprops);
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