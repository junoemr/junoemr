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
<div id="ice-fall-admin-activity">
	<div class="col-sm-12 juno-modal flex flex-row align-items-end">
		<ca-field-select
						class="col-sm-3 no-padding-left"
						ca-name="status"
						ca-title="Status"
						ca-template="label"
						ca-model="$ctrl.statusFilter"
						ca-options="$ctrl.filterStatuses"
		>
		</ca-field-select>

		<div class="col-sm-3" ng-class="{'required': !$ctrl.startDate}">
			<ca-field-date
							ca-title="Start Date"
							ca-date-picker-id="startDate"
							ca-name="startDate"
							ca-model="$ctrl.startDate"
							ca-orientation="auto"
							ca-required-field="true"
			>
			</ca-field-date>
		</div>

		<div class="col-sm-3" ng-class="{'required': !$ctrl.endDate}">
			<ca-field-date
							ca-title="End Date"
							ca-date-picker-id="endDate"
							ca-name="endDate"
							ca-model="$ctrl.endDate"
							ca-orientation="auto"
							ca-required-field="true"
			>
			</ca-field-date>
		</div>

		<div class="flex-row flex-grow justify-content-right">
			<button class="btn btn-primary" ng-click="$ctrl.tableParams.reload()">
				Search
			</button>
		</div>
	</div>
	<div class="col-sm-12 md-margin-top">
		<table ng-table="$ctrl.tableParams" show-filter="false" class="table table-striped table-bordered">
			<tbody>
				<tr ng-repeat="log in  $data">
					<td data-title="'Date Sent'" sortable="'DATE_SENT'">
						{{log.dateSent | date:'yyyy-MM-dd'}}
					</td>
					<td data-title="'Status'" sortable="'STATUS'">
						{{log.status}}
					</td>
					<td data-title="'Message'" sortable="'MESSAGE'">
						{{log.message}}
					</td>
					<td data-title="'Sending Provider'" sortable="'PROVIDER_NO'">
						{{log.providerNo}}
					</td>
					<td data-title="'Document'" sortable="'DATE_SENT'">
						<div class="text-center" ng-click="$ctrl.doEformPopup(log)"><i class="icon icon-sm icon-file hand-hover"></i></div>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>