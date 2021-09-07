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
import {FORM_CONTROLLER_STATES, FORM_CONTROLLER_FORM_TYPES, FORM_CONTROLLER_SPECIAL_GROUPS} from "./formsConstants";
import {SecurityPermissions} from "../../common/security/securityConstants";

angular.module('Record.Forms').controller('Record.Forms.FormController', [

	'$scope',
	'$http',
	'$location',
	'$stateParams',
	'$state',
	'demographicService',
	'formService',
	'user',
	'securityRolesService',

	function(
		$scope,
		$http,
		$location,
		$stateParams,
		$state,
		demographicService,
		formService,
		user,
		securityRolesService)
	{
		var controller = this;

		$scope.FORM_CONTROLLER_STATES = FORM_CONTROLLER_STATES;
		controller.SecurityPermissions = SecurityPermissions;

		controller.demographicNo = $stateParams.demographicNo;
		controller.providerNo = user.providerNo;
		controller.appointmentNo = $stateParams.appointmentNo;

		controller.viewState = $stateParams.viewState;
		controller.formSearchStr = "";

		controller.groupSelection = FORM_CONTROLLER_SPECIAL_GROUPS.SELECT_ALL;
		controller.groupSelectedForms = null;

		// form display list
		$scope.displayFormList = [];

		controller.$onInit = () =>
		{
			if(securityRolesService.hasSecurityPrivileges(SecurityPermissions.EformRead, SecurityPermissions.FormRead))
			{
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
		}

		// fill form list with completed forms
		controller.getCompletedForms = function ()
		{
			formService.getCompletedEncounterForms($stateParams.demographicNo).then(
				function success(results)
				{
					$scope.displayFormList = Juno.Common.Util.toArray(results.list);
					controller.ensureSubjectNotNull($scope.displayFormList);
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
					controller.ensureSubjectNotNull($scope.displayFormList);
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
					controller.ensureSubjectNotNull($scope.displayFormList);
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
					controller.ensureSubjectNotNull($scope.displayFormList);
				},
				function error(errors)
				{
					console.error(errors);
				}
			);
		};

		// null subject values do no sort well. force them to empty string
		controller.ensureSubjectNotNull = function (formList)
		{
			formList.forEach(function (form)
			{
				if (form.subject === null)
				{
					form.subject = '';
				}
			});
		};

		// called on mode change
		controller.onModeChange = function (mode)
		{
			controller.viewState = mode;
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
		controller.onGroupChange = function (groupId, selectedForms)
		{
			controller.groupSelection = groupId;
			controller.groupSelectedForms = selectedForms;
		};

		// filter forms for display
		$scope.onFilterForms = function (form, index, array)
		{
			// filter on group
			let foundInGroup = true;

			switch(controller.groupSelection)
			{
				case FORM_CONTROLLER_SPECIAL_GROUPS.SELECT_ALL:
					foundInGroup = true;
					break;
				case FORM_CONTROLLER_SPECIAL_GROUPS.SELECT_FORM:
					foundInGroup = form.type === FORM_CONTROLLER_FORM_TYPES.FORM;
					break;
				case FORM_CONTROLLER_SPECIAL_GROUPS.SELECT_EFORM:
					foundInGroup = form.type === FORM_CONTROLLER_FORM_TYPES.EFORM;
					break;
				default:
					let found = controller.groupSelectedForms.find(function (selectedItem) {
						return selectedItem.id === form.formId
					});
					foundInGroup = (found !== undefined && found !== null);
					break;
			}

			// filter on search string
			let foundInSearch = true;
			if (controller.formSearchStr.length > 0)
			{
				foundInSearch = form.name.toUpperCase().search(".*" + controller.formSearchStr.toUpperCase()+".*") !== -1;
			}

			return foundInGroup && foundInSearch;
		};

		controller.openManageForms = function ()
		{
			window.open("../administration/?show=Forms"
				,'popUpWindow','height=700,width=1200,left=100,top=100,resizable=yes,scrollbars=yes,toolbar=no,menubar=no,location=no,directories=no');
		};

		controller.showEditPopup = function()
		{
			window.open("../administration/?show=Forms&load=Groups"
				,'popUpWindow','height=700,width=1200,left=100,top=100,resizable=yes,scrollbars=yes,toolbar=no,menubar=no,location=no,directories=no');
		};
	}
]);
