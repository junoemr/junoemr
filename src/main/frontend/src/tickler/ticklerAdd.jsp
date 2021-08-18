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
<form name="ticklerAddForm" ng-submit="$ctrl.save()" ng-init="$ctrl.init()" novalidate>
	<div class="modal-header">
		<button type="button" class="close" ng-click="$ctrl.close()" aria-label="Close">&times;</button>	
		<h3><bean:message key="tickler.add.title" bundle="ui"/></h3>
	</div>
	<div class="modal-body">
		<div class="row">
			<div class="col-sm-10 col-sm-offset-1">
				<div class="row" ng-show="$ctrl.showErrors === true">
					<div class="col-xs-12">
						<ul>
							<li class="text-danger" ng-repeat="error in $ctrl.errors">{{error}}</li>
						</ul>
					</div>
				</div>

				<div class="row">
					<div class="col-xs-7">
						<div class="form-group">
							<label>Patient:</label>
							<%--<juno-patient-typeahead
									juno-model="$ctrl.demographicSearch"
									juno-placeholder="<bean:message key="tickler.add.patient" bundle="ui"/>">
							</juno-patient-typeahead>--%>
							<input type="text" ng-model="$ctrl.demographicSearch" placeholder="<bean:message key="tickler.add.patient" bundle="ui"/>"
							       ng-ref="$ctrl.demographicSearchRef"
							       uib-typeahead="pt.name for pt in $ctrl.searchPatients($viewValue)"
							       typeahead-on-select="$ctrl.updateDemographicNo($item, $model, $label)"
							       class="form-control">
						</div>

					</div>
					<div class="col-xs-5">
						<div class="row" ng-if="$ctrl.tickler.demographic != null">
							<div class="col-xs-3">
								<img width="60px" ng-src="../imageRenderingServlet?source=local_client&clientId={{$ctrl.tickler.demographic.demographicNo}}"/>
							</div>
							<div class="col-xs-9">
								<div class="blue-text">
									<h4 class="no-margin">{{$ctrl.tickler.demographic.lastName}}, {{$ctrl.tickler.demographic.firstName}}</h4>
								</div>
								<div>{{$ctrl.tickler.demographic.hin}}</div>
								<div>{{$ctrl.tickler.demographic.dateOfBirth | date : 'yyyy-MM-dd'}}</div>
							</div>
						</div>

					</div>
				</div>

				<div class="row">
					<div class="col-xs-7">
						<div class="form-group">
							<label>Assign to:</label>
							<input type="text" ng-model="$ctrl.tickler.taskAssignedToName" placeholder="<bean:message key="tickler.add.provider" bundle="ui"/>"
								uib-typeahead="pt.providerNo as pt.name for pt in $ctrl.searchProviders($viewValue)"
								typeahead-on-select="$ctrl.updateProviderNo($item, $model, $label)"
								class="form-control"
							>
						</div>

					</div>

					<div class="col-xs-5">
						<div class="form-group">
							<label><bean:message key="tickler.add.priority" bundle="ui"/>:</label>
							<select ng-model="$ctrl.tickler.priority" ng-init="$ctrl.tickler.priority='Normal'" 
								ng-options="p for p in $ctrl.priorities" 
								class="form-control">
							</select>
						</div>
					</div>
				</div>

				<div class="row">
					<div class="col-xs-12">
						<label><bean:message key="tickler.add.serviceDate" bundle="ui"/>:</label>
					</div>
				</div>


				<div class="row vertical-align">
					<div class="form-group col-xs-7">
						<juno-datepicker-popup juno-model="$ctrl.tickler.serviceDateDate"
											   show-icon="true"
											   type="Input">
						</juno-datepicker-popup>
					</div>

					<div class="form-group col-xs-2">
						<a class="" ng-click="$ctrl.addMonthsFromNow(6)"><bean:message key="tickler.ticklerEdit.add6month"/></a>
					</div>
					<div class="form-group col-xs-2">
						<a class="" ng-click="$ctrl.addMonthsFromNow(12)"><bean:message key="tickler.ticklerEdit.add1year"/></a>
					</div>
				</div>


				<div class="row">
					<div class="form-group col-xs-7">
						<label><bean:message key="tickler.view.serviceTime" bundle="ui"/>:</label>
						<div class="input-group bootstrap-timepicker timepicker">
							<input ng-model="$ctrl.tickler.serviceDateTime"
								   type="text"
								   class="form-control input-small" id="timepicker">
							<span class="input-group-addon">
								<i class="glyphicon glyphicon-time"></i>
							</span>
						</div>
					</div>
				</div>


				<div class="row">
					<div class="col-xs-12">
							<div class="form-group">
							<label><bean:message key="tickler.add.templates" bundle="ui"/>:</label>
							<select ng-model="$ctrl.tickler.suggestedTextId" 
								ng-change="$ctrl.setSuggestedText()" 
								ng-options="a.id as a.suggestedText for a in $ctrl.textSuggestions" 
								class="form-control">
							</select>
							</div>
					</div>
				</div>

				<div class="row">
					<div class="col-xs-12">
							<textarea ng-model="$ctrl.tickler.message" class="form-control" rows="6" required></textarea>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<button class="btn btn-default" type="button" ng-click="$ctrl.close()"><bean:message key="global.close" bundle="ui"/></button>
		<button class="btn btn-success" type="button" ng-click="$ctrl.save()" ng-disabled="$ctrl.isDisabled"><bean:message key="global.save" bundle="ui"/></button>
		<button class="btn btn-success" type="button" ng-click="$ctrl.saveWithEncounter()" ng-disabled="$ctrl.isDisabled"><bean:message key="tickler.add.saveWithEncounter" bundle="ui"/></button>

	</div>
</form>





