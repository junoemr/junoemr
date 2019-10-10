/*
* Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
* This software is published under the GPL GNU General Public License.
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
*
* This software was written for
* CloudPractice Inc.
* Victoria, British Columbia
* Canada
*/
import {FORM_CONTROLLER_SPECIAL_GROUPS} from '../../formsConstants'

angular.module('Record.Forms').component('groupSelectorComponent', {
	templateUrl: 'src/record/forms/components/groupSelector/groupSelector.jsp',
	bindings: {
		groupChange: '&',
		groupSelection: '<'
	},
	controller: ['formService', function (formService) {
		let ctrl = this;

		ctrl.FORM_CONTROLLER_SPECIAL_GROUPS = FORM_CONTROLLER_SPECIAL_GROUPS;

		ctrl.onGroupChange = function (groupId, selectedItems)
		{
			ctrl.groupChange({groupId: groupId, selectedForms: selectedItems});
		};

		ctrl.groups = [];
		formService.getFormGroups().then(
			function success(results) {
				ctrl.groups = results;
			},
			function error(results) {
				console.error("Failed to fetch groups");
			}
		);
	}]
});