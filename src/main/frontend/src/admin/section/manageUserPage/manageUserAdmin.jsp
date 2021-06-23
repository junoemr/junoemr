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
<div class="manage-user-page-admin">
	<div class="col-sm-12">
		<panel>
			<panel-header>
				<h6 class="d-inline-block">Search Filters</h6>
				<btn class="btn btn-primary float-right lg-padding-left lg-padding-right sm-margin-top" ng-click="$ctrl.toAddUser()">Add User</btn>
			</panel-header>
			<panel-body class="flex-row align-items-end">
				<div class="col-sm-4 no-float">
					<ca-field-text
									ca-name="keywordSearch"
									ca-title="Keyword"
									ca-model="$ctrl.keyword"
									ca-rows="1"
									ca-text-placeholder="Search..."
					>
					</ca-field-text>
				</div>
				<div class="col-sm-4 no-float">
					<ca-field-select
									class="juno-modal no-padding"
									ca-template="label"
									ca-name="type"
									ca-title="Provider Type"
									ca-model="$ctrl.providerType"
									ca-options="$ctrl.providerTypeOptions"
									ca-empty-option="true"
									ca-text-placeholder="All"
					>
					</ca-field-select>
				</div>
				<div class="col-sm-4 no-float">
					<ca-field-select
									class="juno-modal no-padding"
									ca-template="label"
									ca-name="status"
									ca-title="Provider Status"
									ca-model="$ctrl.providerStatus"
									ca-options="$ctrl.providerStatusOptions"
					>
					</ca-field-select>
				</div>
			</panel-body>
		</panel>
	</div>

	<div class="col-sm-12 lg-margin-top">
		<table ng-table="$ctrl.tableParams" class="table table-striped table-bordered">
			<tbody>
				<tr ng-repeat="provider in $ctrl.providerList | filter:$ctrl.filterProviders  | orderBy:$ctrl.sortMode">
					<td data-title="'Provider No'" sortable="'providerNo'">
						{{provider.providerNo}}
					</td>
					<td data-title="'Last Name'" sortable="'lastName'">
						{{provider.lastName}}
					</td>
					<td data-title="'First Name'" sortable="'firstName'">
						{{provider.firstName}}
					</td>
					<td data-title="'Type'" sortable="'providerType'">
						{{provider.providerType}}
					</td>
					<td data-title="'Speciality'" sortable="'specialty'">
						{{provider.specialty}}
					</td>
					<td data-title="'Team'" sortable="'team'">
						{{provider.team}}
					</td>
					<td data-title="'Status'" sortable="'enabled'">
						<div ng-if="provider.enabled">Active</div>
						<div ng-if="!provider.enabled">Inactive</div>
					</td>
					<td class="provider-button-column flex-row justify-content-center">
						<button class="btn btn-primary btn-active-hover sm-margin-left sm-margin-right"
										title="View Provider"
										ng-click="$ctrl.toViewUser(provider.providerNo)"
						>
							<i class="icon icon-view"></i>
						</button>
						<button
										class="btn btn-warning btn-active-hover sm-margin-left sm-margin-right"
										title="Edit Provider"
										ng-click="$ctrl.toEditUser(provider.providerNo)"
						>
							<i class="icon icon-write"></i>
						</button>
						<button
										class="btn btn-danger  btn-active-hover sm-margin-left sm-margin-right"
										title="Delete Provider"
										ng-click="$ctrl.changeProviderStatus(provider.providerNo, false)"
										ng-if="provider.enabled"
						>
							<i class="icon icon-delete"></i>
						</button>
						<button
										class="btn btn-success  btn-active-hover sm-margin-left sm-margin-right"
										title="Enable Provider"
										ng-click="$ctrl.changeProviderStatus(provider.providerNo, true)"
										ng-if="!provider.enabled"
						>
							<i class="icon icon-check"></i>
						</button>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>