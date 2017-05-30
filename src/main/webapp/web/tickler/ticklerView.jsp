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
	<button type="button" class="close" ng-click="close()" aria-label="Close">&times;</button>
	<h3 class="modal-title p-inline"><bean:message key="tickler.view.title" bundle="ui"/>: <span><p class="blue-text">{{ticklerUpdate.demographicName}}</p></span></h3>
		
</div>
<div class="modal-body" id="tickler-edit-body">
  <div class="row">
  	<div class="col-sm-10 col-sm-offset-1">
		<form>
			<div class="form-group col-sm-12">
			<label>Message</label>
  			<textarea rows="5" ng-model="ticklerUpdate.message" class="form-control" ng-readonly="!ticklerWriteAccess">{{ticklerUpdate.message}}</textarea>
			</div>
			<div class="form-group col-sm-6">
				<label><bean:message key="tickler.view.assignTo" bundle="ui"/></label>
				<input type="text" ng-click="editTaskAssignedTo()" ng-model="ticklerUpdate.taskAssignedToName" 
					placeholder="<bean:message key="tickler.view.provider.placeholder" bundle="ui"/>" 
					uib-typeahead="pt.name for pt in searchProviders($viewValue)" 
					typeahead-on-select="updateTaskAssignedTo($item, $model, $label)"
					class="form-control"></input>
			</div>
			
			<div class="form-group col-sm-6">
				<label><bean:message key="tickler.view.priority" bundle="ui"/></label>

				<select ng-model="ticklerUpdate.priority"
					ng-options="p for p in priorities"
					ng-change="updatePriority(ticklerUpdate.priority)" 
					class="form-control ng-pristine ng-valid ng-not-empty ng-touched">
				</select>


			</div>	

			<div class="form-group col-sm-6">
				<label><bean:message key="tickler.view.lastUpdated" bundle="ui"/></label>
				<input disabled type="text" class="form-control" ng-model="ticklerUpdate.updateDate " ></input>
				<%--<p>{{ticklerUpdate.updateDate | date :'yyyy-MM-dd HH:mm'}}</p>--%>

			</div>

			<div class="form-group col-sm-6">
				<label><bean:message key="tickler.view.serviceDate" bundle="ui"/></label>
				<%--<span class="input-group">
					<input  ng-model="ticklerUpdate.serviceDate" type="text" class="form-control">
					<input  ng-model="ticklerUpdate.serviceTime" type="text" class="form-control">
				</span>
				<button class="btn btn-xs btn-default" ng-click="cancelServiceDateAndTimeUpdate()" ><bean:message key="global.cancel" bundle="ui"/></button>
				<button class="btn btn-xs btn-success" ng-click="updateServiceDateAndTime()" ><bean:message key="tickler.view.date.set" bundle="ui"/></button>--%>
				<juno-datepicker-popup juno-model="ticklerUpdate.serviceDate"> </juno-datepicker-popup>

			</div>

			<div class="form-group col-sm-6">
				<label><bean:message key="tickler.view.serviceTime" bundle="ui"/></label>
				<%--<juno-timepicker-popup juno-model="ticklerUpdate.serviceTime"></juno-timepicker-popup>--%>
				<%--<div class="input-group bootstrap-timepicker timepicker">
					<input id="timepicker" class="form-control" data-provide="timepicker" data-template="modal" data-minute-step="1" data-modal-backdrop="true" type="text"/>
				</div>--%>
				<div class="input-group bootstrap-timepicker timepicker">
					<input id="timepicker1" type="text" class="form-control input-small" data-provide="timepicker" ng-model="ticklerUpdate.serviceTime">
					<span class="input-group-addon">
						<i class="glyphicon glyphicon-time"></i>
					</span>
					
				</div>
			</div>

			<div class="form-group col-sm-6">
				<label><bean:message key="tickler.view.status" bundle="ui"/></label>

				<select ng-model="selectedStatus"
					ng-options="s.label for s in statuses"
					ng-change="updateStatus(selectedStatus)" 
					class="form-control ng-pristine ng-valid ng-not-empty ng-touched">
				</select>
			</div>	
		</form>
  	</div>
  </div>
    
	<div class="row">
		<div class="col-xs-12">
			<div class="tickler-edit-comments-header">
				<strong ng-click="showComments = !showComments">
					<bean:message key="tickler.view.comments" bundle="ui"/>
					({{ticklerUpdate.ticklerComments != null && ticklerUpdate.ticklerComments.length || 0}}) 
					<%--<span class="glyphicon glyphicon-pencil" ng-show="ticklerWriteAccess" ng-click="addComment()"></span>--%>
					<button class="btn btn-xs btn-success" ng-show="ticklerWriteAccess" ng-click="addComment()">Add</button>
				</strong>
			</div>
			<div class="row" ng-if="showComments">	
				<div class="form-group" ng-show="showCommentFormControl">
					<div class="input-group">
						<div class="input-group-addon"><span class="glyphicon glyphicon-remove" ng-click="cancelCommentUpdate()"></span></div>
						<div class="input-group-addon"><span class="glyphicon glyphicon-ok" ng-click="saveComment()"></span></div>
						<input  type="text" ng-model="ticklerUpdate.comment" class="form-control"/>			
					</div>
				</div>	  		
				<hr ng-if="ticklerUpdate.ticklerComments == null || ticklerUpdate.ticklerComments.length == 0"/>
				<table ng-if="ticklerUpdate.ticklerComments != null && ticklerUpdate.ticklerComments.length > 0" class="table">
					<tr ng-repeat="tc in ticklerUpdate.ticklerComments">
						<td>{{tc.updateDate | date : 'yyyy-MM-dd'}}</td>
						<td>{{tc.providerName}}</td>
						<td>{{tc.message}}</td>
					</tr>
				</table>
			</div>
		</div>
	</div>

  
    <div class="row">
		<div class="col-xs-12">
			<strong ng-click="showUpdates = !showUpdates"><bean:message key="tickler.view.updates" bundle="ui"/> ({{ticklerUpdate.ticklerUpdates != null && ticklerUpdate.ticklerUpdates.length || 0}})</strong>
			<div ng-if="showUpdates">
				<hr ng-if="ticklerUpdate.ticklerUpdates == null || ticklerUpdate.ticklerUpdates.length == 0"/>
				<table ng-if="ticklerUpdate.ticklerUpdates != null && ticklerUpdate.ticklerUpdates.length > 0" class="table">
					<tr ng-repeat="tc in ticklerUpdate.ticklerUpdates">
						<td>{{tc.updateDate | date : 'yyyy-MM-dd HH:mm'}}</td>
						<td>{{tc.providerName}}</td>
					</tr>
				</table>
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
		<button class="btn btn-warning" ng-click="completeTickler()" ng-show="ticklerWriteAccess"><bean:message key="tickler.view.complete" bundle="ui"/></button>
		<button class="btn btn-danger" ng-click="deleteTickler()" ng-show="ticklerWriteAccess"><bean:message key="global.delete" bundle="ui"/></button>
	</div>
	<button class="btn btn-default" ng-click="close()"><bean:message key="global.close" bundle="ui"/></button>
	<button class="btn btn-primary" ng-click="printTickler()"><bean:message key="global.print" bundle="ui"/></button>
	<button class="btn btn-success" ng-click="saveChanges()" ng-show="ticklerWriteAccess"><bean:message key="tickler.view.save" bundle="ui"/></button>
</div>


