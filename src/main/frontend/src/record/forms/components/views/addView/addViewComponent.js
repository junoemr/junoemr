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

angular.module('Record.Forms').component('addViewComponent', {
	templateUrl: 'src/record/forms/components/views/addView/addView.jsp',
	bindings: {
		demographicNo: '<',
		providerNo: '<',
		appointmentNo: '<',
		formList: '=',
		filterForms: '&',
	},
	controller:[ 'formService', '$scope', 'NgTableParams', function (formService, $scope, NgTableParams)
	{
		let ctrl = this;

		$scope.FORM_CONTROLLER_FORM_TYPES = FORM_CONTROLLER_FORM_TYPES;
		ctrl.sortMode = FORM_CONTROLLER_SORT_MODES.FORM_NAME;

		ctrl.openEForm = function (id)
		{
			formService.openEFormPopup(ctrl.demographicNo, id);
		};

		ctrl.openForm = function (url)
		{
			formService.openFormPopup(ctrl.providerNo, ctrl.demographicNo, ctrl.appointmentNo, url);
		};

		ctrl.doFilterForms = function(form, index, array)
		{
			return ctrl.filterForms({form:form, index:index, array:array});
		};

		ctrl.tableParams = new NgTableParams(
		{
			page: 1, // show first page
			count: -1, // unlimited
			sorting:
				{
					name: 'asc',
				}
		},
		{
			// called when sort order changes
			getData: function(params) {
				ctrl.sortMode = params.orderBy();
			}
		});
	}]
});