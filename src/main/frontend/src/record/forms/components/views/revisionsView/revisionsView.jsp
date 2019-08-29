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
<div id="revisions-view-component" class="container-fluid">
	<!--header-->
	<div class="row">
		<div class="col-md-3" style="background-color: #38a1bb;">
			<h4><a href="javascript:" ng-click="$ctrl.doSort(SORT_MODES.FORM_NAME)">Form Name</a></h4>
		</div>
		<div class="col-md-5" style="background-color: #2f83ff;">
			<h4><a href="javascript:" ng-click="$ctrl.doSort(SORT_MODES.ADDITIONAL)">Additional Information</a></h4>
		</div>
		<div class="col-md-2" style="background-color: #38a1bb;">
			<h4><a href="javascript:" ng-click="$ctrl.doSort(SORT_MODES.MOD_DATE)">Modified Date</a></h4>
		</div>
		<div class="col-md-2" style="background-color: #2f83ff;">
			<h4><a href="javascript:" ng-click="$ctrl.doSort(SORT_MODES.CREATE_DATE)">Creation Date</a></h4>
		</div>
	</div>

	<div class="row">
		<hr>
	</div>

	<!--list-->
	<div class="row content-row" ng-repeat=" form in $ctrl.formList | filter:$ctrl.doFilterForms | orderBy:$ctrl.sortMode:$ctrl.reverseSort">
		<div class="col-md-3" style="background-color: #38a1bb;">
			<a href="javascript:;" ng-if="form.type === FORM_CONTROLLER_FORM_TYPES.EFORM" ng-click="$ctrl.openEForm(form.id)">{{form.name}}</a>
			<a href="javascript:" ng-if="form.type === FORM_CONTROLLER_FORM_TYPES.FORM" ng-click="$ctrl.openForm(form.name, form.id)">{{form.name}}</a>
		</div>
		<div class="col-md-5" style="background-color: #2f83ff;">
			{{form.subject}}
		</div>
		<div class="col-md-2" style="background-color: #38a1bb;">
			{{form.date | date:'yyyy-MM-dd'}}
		</div>
		<div class="col-md-2" style="background-color: #2f83ff;">
			{{form.createDate | date:'yyyy-MM-dd'}}
		</div>
	</div>
</div>