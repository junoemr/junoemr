<%--

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

--%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<div class="modal-header">
	<button type="button" class="close" ng-click="ticklerViewCtrl.close()" aria-label="Close">
		&times;
	</button>
	<h3 class="modal-title p-inline">
		<bean:message key="tickler.view.title" bundle="ui"/>:
		<span><p class="blue-text">{{ticklerViewCtrl.ticklerUpdate.demographicName}}</p></span>
	</h3>

</div>
<div class="modal-body" id="tickler-edit-body">
	<div class="row">
		<div class="col-sm-10 col-sm-offset-1">
			<form>
				<div class="form-group col-sm-12">
					<label>Message</label>
					<textarea rows="5"
							  ng-value="ticklerViewCtrl.ticklerUpdate.message"
							  class="form-control"
                              readonly>
					</textarea>
				</div>
				<div class="form-group col-sm-6">
					<label><bean:message key="tickler.view.assignTo" bundle="ui"/></label>
					<input type="text"
						   ng-model="ticklerViewCtrl.ticklerUpdate.taskAssignedToName"
						   placeholder="<bean:message key="tickler.view.provider.placeholder" bundle="ui"/>"
						   uib-typeahead="pt.providerNo as pt.name for pt in ticklerViewCtrl.searchProviders($viewValue)"
						   typeahead-on-select="ticklerViewCtrl.updateTaskAssignedTo($item, $model, $label)"
						   class="form-control">
					</input>
				</div>

				<div class="form-group col-sm-6">
					<label><bean:message key="tickler.view.priority" bundle="ui"/></label>

					<select ng-model="ticklerViewCtrl.ticklerUpdate.priority"
							ng-options="p for p in ticklerViewCtrl.priorities"
							ng-change="ticklerViewCtrl.updatePriority(ticklerViewCtrl.ticklerUpdate.priority)"
							class="form-control ng-pristine ng-valid ng-not-empty ng-touched">
					</select>


				</div>

				<div class="form-group col-sm-6">
					<label><bean:message key="tickler.view.serviceDate" bundle="ui"/></label>
					<%--<span class="input-group">
						<input  ng-model="ticklerUpdate.serviceDate" type="text" class="form-control">
						<input  ng-model="ticklerUpdate.serviceTime" type="text" class="form-control">
					</span>
					<button class="btn btn-xs btn-default" ng-click="cancelServiceDateAndTimeUpdate()" ><bean:message key="global.cancel" bundle="ui"/></button>
					<button class="btn btn-xs btn-success" ng-click="updateServiceDateAndTime()" ><bean:message key="tickler.view.date.set" bundle="ui"/></button>--%>
					<juno-datepicker-popup juno-model="ticklerViewCtrl.serviceDateInput"
										   show-icon="true"
										   type="Input"></juno-datepicker-popup>

				</div>

				<div class="form-group col-sm-6">
					<label><bean:message key="tickler.view.lastUpdated" bundle="ui"/></label>
					<input disabled type="text" class="form-control"
						   value="{{ticklerViewCtrl.ticklerUpdate.updateDate | date:'yyyy-MM-dd'}}"></input>
					<%--<p>{{ticklerUpdate.updateDate | date :'yyyy-MM-dd HH:mm'}}</p>--%>

				</div>

				<div class="form-group col-sm-6">
					<label><bean:message key="tickler.view.serviceTime" bundle="ui"/></label>
					<%--<juno-timepicker-popup juno-model="ticklerUpdate.serviceTime"></juno-timepicker-popup>--%>
					<%--<div class="input-group bootstrap-timepicker timepicker">
						<input id="timepicker" class="form-control" data-provide="timepicker" data-template="modal" data-minute-step="1" data-modal-backdrop="true" type="text"/>
					</div>--%>
					<div class="input-group bootstrap-timepicker timepicker">
						<input ng-model="ticklerViewCtrl.serviceTimeInput" id="timepicker1"
							   type="text"
							   class="form-control input-small" data-provide="timepicker">
						<span class="input-group-addon">
						<i class="glyphicon glyphicon-time"></i>
					</span>

					</div>
				</div>

				<div class="form-group col-sm-6">
					<label><bean:message key="tickler.view.status" bundle="ui"/></label>

					<select ng-model="ticklerViewCtrl.selectedStatus"
							ng-options="s.label for s in ticklerViewCtrl.statuses"
							ng-change="ticklerViewCtrl.updateStatus(ticklerViewCtrl.selectedStatus)"
							class="form-control ng-pristine ng-valid ng-not-empty ng-touched">
					</select>
				</div>
			</form>
		</div>
	</div>
	<div class="row">
		<div class="col-sm-10 col-sm-offset-1">
			<div class="col-sm-12">
				<div class="tickler-edit-comments-header">
					<strong ng-click="ticklerViewCtrl.showComments = !ticklerViewCtrl.showComments"
							title="Toggle show/hide comments">
						<bean:message key="tickler.view.comments" bundle="ui"/>
						({{ticklerViewCtrl.ticklerUpdate.ticklerComments.length}})
					</strong>
					<a class="btn btn-xs btn-default"
					   ng-click="ticklerViewCtrl.showComments = !ticklerViewCtrl.showComments">
						<i class="fa fa-chevron-down" ng-show="!ticklerViewCtrl.showComments"
						   aria-hidden="true"></i>
						<i class="fa fa-chevron-up" ng-show="ticklerViewCtrl.showComments"
						   aria-hidden="true"></i>
					</a>
					<button class="btn btn-xs btn-success"
							ng-show="ticklerViewCtrl.ticklerWriteAccess"
							ng-click="ticklerViewCtrl.addComment()">Add
					</button>
				</div>
				<div class="form-group" ng-show="ticklerViewCtrl.showCommentFormControl">
					<div class="input-group">
							<span class="input-group-btn">
								<button class="btn btn-regular"
										ng-click="ticklerViewCtrl.cancelCommentUpdate()">
									<span class="glyphicon glyphicon-remove"></span>
								</button>
								<button class="btn btn-success"
										ng-click="ticklerViewCtrl.saveComment()">
									<span class="glyphicon glyphicon-ok"></span>
								</button>
							</span>
						<input type="text" ng-model="ticklerViewCtrl.ticklerUpdate.comment"
							   class="form-control"/>
					</div>
				</div>
				<div ng-if="ticklerViewCtrl.showComments">
					<hr ng-if="ticklerViewCtrl.ticklerUpdate.ticklerComments == null || ticklerViewCtrl.ticklerUpdate.ticklerComments.length == 0"/>
					<table ng-if="ticklerViewCtrl.ticklerUpdate.ticklerComments != null && ticklerViewCtrl.ticklerUpdate.ticklerComments.length > 0"
						   class="table">
						<tr ng-repeat="tc in ticklerViewCtrl.ticklerUpdate.ticklerComments">
							<td>{{tc.updateDate | date : 'yyyy-MM-dd'}}</td>
							<td>{{tc.providerName}}</td>
							<td>{{tc.message}}</td>
						</tr>
					</table>
				</div>
			</div>
			<div class="col-sm-12">
				<strong ng-click="ticklerViewCtrl.showUpdates = !ticklerViewCtrl.showUpdates">
					<bean:message key="tickler.view.updates" bundle="ui"/>
					({{ticklerViewCtrl.ticklerUpdate.ticklerUpdates.length}})
				</strong>
				<a class="btn btn-xs btn-default"
				   ng-click="ticklerViewCtrl.showUpdates = !ticklerViewCtrl.showUpdates">
					<i class="fa fa-chevron-down" ng-show="!ticklerViewCtrl.showUpdates"
					   aria-hidden="true"></i>
					<i class="fa fa-chevron-up" ng-show="ticklerViewCtrl.showUpdates"
					   aria-hidden="true"></i>
				</a>
				<div ng-if="ticklerViewCtrl.showUpdates">
					<hr ng-if="ticklerViewCtrl.ticklerUpdate.ticklerUpdates == null || ticklerViewCtrl.ticklerUpdate.ticklerUpdates.length == 0"
					/>
					<table ng-if="ticklerViewCtrl.ticklerUpdate.ticklerUpdates != null && ticklerViewCtrl.ticklerUpdate.ticklerUpdates.length > 0"
						   class="table">
						<tr ng-repeat="tc in ticklerViewCtrl.ticklerUpdate.ticklerUpdates">
							<td>{{tc.updateDate | date : 'yyyy-MM-dd HH:mm'}}</td>
							<td>{{tc.providerName}}</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
	</div>

	<!--
   <pre>{{tickler}}</pre>
   -->
</div>
<div class="modal-footer">
	<!--
    <button class="btn" ng-click="save()">Save</button>
	-->
	<div class="pull-left">
		<button class="btn btn-warning" ng-click="ticklerViewCtrl.completeTickler()"
				ng-disabled="!ticklerViewCtrl.canEdit()">
			<bean:message key="tickler.view.complete" bundle="ui"/>
		</button>
		<button class="btn btn-danger" ng-click="ticklerViewCtrl.deleteTickler()"
		        ng-disabled="!ticklerViewCtrl.canDelete()">
			<bean:message key="global.delete" bundle="ui"/>
		</button>
	</div>
	<button class="btn btn-default" ng-click="ticklerViewCtrl.close()">
		<bean:message key="global.close" bundle="ui"/>
	</button>
	<button class="btn btn-primary" ng-click="ticklerViewCtrl.printTickler()">
		<bean:message key="global.print" bundle="ui"/>
	</button>
	<button class="btn btn-success" ng-click="ticklerViewCtrl.saveChanges()"
	        ng-disabled="!ticklerViewCtrl.canEdit()">
		<bean:message key="tickler.view.save" bundle="ui"/>
	</button>
	<button class="btn btn-success" ng-click="ticklerViewCtrl.saveChangesAndWriteEncounter()"
	        ng-disabled="!ticklerViewCtrl.canEdit()">
		<bean:message key="tickler.view.saveWithEncounter" bundle="ui"/>
	</button>
</div>


