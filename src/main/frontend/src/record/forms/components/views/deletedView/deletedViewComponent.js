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
angular.module('Record.Forms').component('deletedViewComponent', {
	templateUrl: 'src/record/forms/components/views/deletedView/deletedView.jsp',
	bindings: {
		demographicNo: '<',
		formList: '=',
		filterForms: '&',
	},
	controller: ['formService', function (formService)
	{
		let ctrl = this;

		ctrl.sortMode = FORM_CONTROLLER_SORT_MODES.FORM_NAME;
		ctrl.SORT_MODES = FORM_CONTROLLER_SORT_MODES;
		ctrl.reverseSort = false;

		ctrl.openForm = function (id)
		{
			formService.openEFormInstancePopup(ctrl.demographicNo, id);
		};

		ctrl.restoreForm = function (id, type)
		{
			let ok = confirm("Are you sure you want to restore this eform?");
			if (ok)
			{
				formService.restoreForm(id, type).then(
					function success(result)
					{
						ctrl.formList.splice(ctrl.formList.findIndex(function (form)
						{
							return form.id === id && form.type === type;
						}),1);
					},
					function error (result)
					{
						console.error("Failed to restore form: " + id);
					}
				)
			}
		};

		ctrl.doFilterForms = function(form, index, array)
		{
			return ctrl.filterForms({form:form, index:index, array:array});
		};

		ctrl.doSort = function(mode)
		{
			if (mode === ctrl.sortMode)
			{
				ctrl.reverseSort = !ctrl.reverseSort;
			}
			else
			{
				ctrl.reverseSort = false;
			}
			ctrl.sortMode = mode;
		};
	}]
});