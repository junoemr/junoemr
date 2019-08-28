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

// view states that the form controller can be in
FORM_CONTROLLER_STATES = {
	ADD: 		0,
	COMPLETED: 	1,
	REVISION: 	2,
	DELETED: 	3,
	MANAGE: 	4,
}

FORM_CONTROLLER_GROUP_SELECT_ALL = -1;

angular.module('Record.Forms').controller('Record.Forms.FormController', [

	'$scope',
	'$http',
	'$location',
	'$stateParams',
	'$state',
	'demographicService',
	'demo',
	'formService',
	'user',
	'securityService',

	function(
		$scope,
		$http,
		$location,
		$stateParams,
		$state,
		demographicService,
		demo,
		formService,
		user,
		securityService)
	{
		var controller = this;

		console.log("form ctrl ", $stateParams, $state);

		controller.demographicNo = $stateParams.demographicNo;
		controller.providerNo = user.providerNo;

		controller.viewState = $stateParams.viewState;

		controller.groupSelection = FORM_CONTROLLER_GROUP_SELECT_ALL;
		controller.groupSelectedForms = null;

		$scope.viewState = $stateParams.viewState;
		$scope.FORM_CONTROLLER_STATES = FORM_CONTROLLER_STATES;
		$scope.demographicNo = $stateParams.demographicNo;
		$scope.formSearchStr = "";

		console.log("Loading Form Controller in state: " + controller.viewState);

		securityService.hasRights(
		{
			items: [
			{
				objectName: '_admin',
				privilege: 'w'
			},
			{
				objectName: '_admin.eform',
				privilege: 'w'
			}]
		}).then(
			function success(results)
			{
				controller.adminAccess = results.content[0];
				controller.adminEformAccess = results.content[1];
				if (results.content != null && results.content.length === 2)
				{
					if (controller.adminAccess || controller.adminEformAccess)
					{
						controller.hasAdminAccess = true;
					}
				}
				else
				{
					alert('failed to load rights');
				}
			},
			function error(errors)
			{
				console.log(errors);
		});

		// fill form list with completed forms
		controller.getCompletedForms = function ()
		{
			formService.getCompletedEncounterForms($stateParams.demographicNo).then(
				function success(results)
				{
					$scope.displayFormList = Juno.Common.Util.toArray(results.list);
					console.log(results);
				},
				function error(errors)
				{
					console.error(errors);
				}
			);
		};

		// fill form list with all forms (so that the user can add them to the patients chart).
		controller.getFormsToAdd = function ()
		{
			formService.getAddForms($stateParams.demographicNo).then(
				function success(results)
				{
					$scope.displayFormList = Juno.Common.Util.toArray(results.list);
					console.log(results);
				},
				function error(errors)
				{
					console.error(errors);
				}
			);
		};

		// fill form list with revisions of all completed forms
		controller.getFormRevisions = function ()
		{
			formService.getRevisionForms($stateParams.demographicNo).then(
				function success(results)
				{
					$scope.displayFormList = Juno.Common.Util.toArray(results.list);
					console.log(results);
				},
				function error(errors)
				{
					console.error(errors);
				}
			);
		};

		// fill form list with revisions of all completed forms
		controller.getDeletedForms = function ()
		{
			formService.getDeletedForms($stateParams.demographicNo).then(
				function success(results)
				{
					$scope.displayFormList = Juno.Common.Util.toArray(results.list);
					console.log(results);
				},
				function error(errors)
				{
					console.error(errors);
				}
			);
		};

		// called on mode change
		$scope.onModeChange = function (mode)
		{
			console.log("new mode: " + mode);
			$scope.viewState = mode;

			switch (mode)
			{
				case FORM_CONTROLLER_STATES.ADD:
					$location.url("/record/" + controller.demographicNo + "/forms/add");
					controller.getFormsToAdd();
					break;
				case FORM_CONTROLLER_STATES.COMPLETED:
					$location.url("/record/" + controller.demographicNo + "/forms/completed");
					controller.getCompletedForms();
					break;
				case FORM_CONTROLLER_STATES.REVISION:
					$location.url("/record/" + controller.demographicNo + "/forms/revisions");
					controller.getFormRevisions();
					break;
				case FORM_CONTROLLER_STATES.DELETED:
					$location.url("/record/" + controller.demographicNo + "/forms/deleted");
					controller.getDeletedForms();
					break;
				default:
					break;
			}
		};

		// called on group change
		$scope.onGroupChange = function (groupId, selectedForms)
		{
			console.log("new group: " + groupId);
			controller.groupSelection = groupId;
			controller.groupSelectedForms = selectedForms;
		};

		// filter forms for display
		$scope.onFilterForms = function (form, index, array)
		{
			// filter on group
			let foundInGroup = true;
			if (controller.groupSelection !== FORM_CONTROLLER_GROUP_SELECT_ALL)
			{
				let found = controller.groupSelectedForms.find(function (selectedItem) {
					return selectedItem.id === form.formId
				});
				foundInGroup = (found !== undefined && found !== null);
			}

			// filter on search string
			let foundInSearch = true;
			if ($scope.formSearchStr.length > 0)
			{
				foundInSearch = form.name.search("^" + $scope.formSearchStr+".*") !== -1;
			}

			return foundInGroup && foundInSearch;
		};

		// form display list
		$scope.displayFormList = [];

		switch (controller.viewState)
		{
			case FORM_CONTROLLER_STATES.ADD:
				controller.getFormsToAdd();
				break;
			case FORM_CONTROLLER_STATES.COMPLETED:
				controller.getCompletedForms();
				break;
			case FORM_CONTROLLER_STATES.REVISION:
				controller.getFormRevisions();
				break;
			case FORM_CONTROLLER_STATES.DELETED:
				controller.getDeletedForms();
				break;
		}
	}
]);