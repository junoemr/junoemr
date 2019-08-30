<%--
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
--%>
<div id="group-selector-component">
	<h3>View Group:</h3>
	<div class="group-list-item" ng-class="$ctrl.styleListItems($ctrl.FORM_CONTROLLER_SPECIAL_GROUPS.SELECT_ALL)">
		<a href="javascript:" ng-click="$ctrl.onGroupChange($ctrl.FORM_CONTROLLER_SPECIAL_GROUPS.SELECT_ALL, null)">Show All</a>
		<i class="icon icon-chevron-right" ng-if="$ctrl.groupSelection === $ctrl.FORM_CONTROLLER_SPECIAL_GROUPS.SELECT_ALL"></i>
	</div>
	<div class="group-list-item" ng-class="$ctrl.styleListItems($ctrl.FORM_CONTROLLER_SPECIAL_GROUPS.SELECT_EFORM)">
		<a href="javascript:" ng-click="$ctrl.onGroupChange($ctrl.FORM_CONTROLLER_SPECIAL_GROUPS.SELECT_EFORM, null)">Show eForms</a>
		<i class="icon icon-chevron-right" ng-if="$ctrl.groupSelection === $ctrl.FORM_CONTROLLER_SPECIAL_GROUPS.SELECT_EFORM"></i>
	</div>
	<div class="group-list-item" ng-class="$ctrl.styleListItems($ctrl.FORM_CONTROLLER_SPECIAL_GROUPS.SELECT_FORM)">
		<a href="javascript:" ng-click="$ctrl.onGroupChange($ctrl.FORM_CONTROLLER_SPECIAL_GROUPS.SELECT_FORM, null)">Show Forms</a>
		<i class="icon icon-chevron-right" ng-if="$ctrl.groupSelection === $ctrl.FORM_CONTROLLER_SPECIAL_GROUPS.SELECT_FORM"></i>
	</div>
	<div class="container-fluid user-groups-section">
		<div class="row group-list-item" ng-repeat="group in $ctrl.groups">
			<div ng-class="$ctrl.styleListItems(group.id)">
				<a href="javascript:" ng-click="$ctrl.onGroupChange(group.id, group.summaryItem)">{{group.displayName}}</a>
				<i class="icon icon-chevron-right" ng-if="$ctrl.groupSelection === group.id"></i>
			</div>
		</div>
	</div>
	<div class="group-list-item"><a href="javascript:" ng-click="$ctrl.showEditPopup()">Edit Groups</a></div>
</div>