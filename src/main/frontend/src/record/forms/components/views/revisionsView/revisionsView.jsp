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
			<h4>Form Name</h4>
		</div>
		<div class="col-md-5" style="background-color: #2f83ff;">
			<h4>Additional Information</h4>
		</div>
		<div class="col-md-2" style="background-color: #38a1bb;">
			<h4>Modified Date</h4>
		</div>
		<div class="col-md-2" style="background-color: #2f83ff;">
			<h4>Creation Date</h4>
		</div>
	</div>

	<div class="row">
		<hr>
	</div>

	<!--list-->
	<div class="row content-row" ng-repeat=" form in $ctrl.formList">
		<div class="col-md-3" style="background-color: #38a1bb;">
			<a href="javascript:;" ng-click="$ctrl.openForm(form.id)">{{form.name}}</a>
		</div>
		<div class="col-md-5" style="background-color: #2f83ff;">
			{{form.subject}}
		</div>
		<div class="col-md-2" style="background-color: #38a1bb;">
			{{form.date}}
		</div>
		<div class="col-md-2" style="background-color: #2f83ff;">
			{{form.date}}
		</div>
	</div>
</div>