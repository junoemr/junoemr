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
<div class="manage-appointment-queues-page-admin">
	<div class="col-sm-12">
		<panel>
			<panel-header>
				<div class="row queues-header">
					<div class="col-md-10">
						<h6 class="d-inline-block">Appointment Queues</h6>
					</div>
					<div class="col-md-2 pull-right">
						<juno-button ng-click="$ctrl.addQueue()">Add New Queue</juno-button>
					</div>
				</div>
			</panel-header>
			<panel-body>
				<table ng-table="$ctrl.tableParams" class="table table-striped table-bordered">
					<tbody>
					<tr ng-repeat="queue in $ctrl.queueList | orderBy:$ctrl.sortMode">
						<td data-title="'Queue Name'" sortable="'name'">
							{{queue.name}}
						</td>
						<td data-title="'Queue data'">
							{{queue.data}}
						</td>
						<td data-title="'Actions'" class="actions-column">
							<div class="row">
								<div class="col-md-6">
									<juno-button
											title="Edit Queue"
											component-style=" JUNO_STYLE.GREY"
											button-color="JUNO_BUTTON_COLOR.BASE"
											ng-click="$ctrl.editQueue(queue)">
										<i class="icon icon-write"></i>Edit
									</juno-button>
								</div>
								<div class="col-md-6">
									<juno-button
											title="Delete Queue"
											button-color="JUNO_BUTTON_COLOR.BASE"
											ng-click="$ctrl.deleteQueue(queue)">
										<i class="icon icon-delete"></i>Delete
									</juno-button>
								</div>
							</div>
						</td>
					</tr>
					</tbody>
				</table>
			</panel-body>
		</panel>
	</div>
	<div class="col-sm-12 lg-margin-top">

	</div>
</div>
