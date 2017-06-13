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
<div class="modal-content">
	<div class="modal-header">
		<button type="button" class="close" ng-click="newPatientCtrl.cancel()" aria-label="Close">&times;</button>
		<h3 class="modal-title"><bean:message key="modal.newPatient.title" bundle="ui"/></h3>
	</div>

	<div class="modal-body" ng-hide="newPatientCtrl.hasRight">
		<bean:message key="modal.newPatient.noRights" bundle="ui"/>
	</div>

	<div class="modal-body" ng-show="newPatientCtrl.hasRight">
		<div class="container-fluid">
			<form class="form-horizontal" role="form" name="newDemographic">
				<div class="form-group">
					<label for="lastName" class="col-sm-3 control-label"><bean:message key="modal.newPatient.lastName" bundle="ui"/></label>
					<div class="col-sm-8">
						<input type="text" class="form-control" 
							id="lastName" 
							placeholder="<bean:message key="modal.newPatient.lastName" bundle="ui"/>" 
							name="lastName" 
							ng-model="newPatientCtrl.demographic.lastName" 
							ng-change="newPatientCtrl.capName()" 
							required>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label"><bean:message key="modal.newPatient.firstName" bundle="ui"/></label>
					<div class="col-sm-8">
						<input type="text" class="form-control" placeholder="<bean:message key="modal.newPatient.firstName" bundle="ui"/>" ng-model="newPatientCtrl.demographic.firstName" ng-change="newPatientCtrl.capName()" required>
					</div>
				</div>
				<div class="form-group">
					<label for="birthYear" class="col-sm-3 control-label"><bean:message key="modal.newPatient.birth" bundle="ui"/></label>
					<div class="col-sm-2">
						<input type="text" class="form-control" id="birthYear" placeholder="<bean:message key="modal.newPatient.year" bundle="ui"/>" ng-model="newPatientCtrl.demographic.dobYear" required>
					</div>
					<div class="col-sm-2">
						<input type="text" class="form-control" placeholder="<bean:message key="modal.newPatient.month" bundle="ui"/>" ng-model="newPatientCtrl.demographic.dobMonth" required>
					</div>
					<div class="col-sm-2">
						<input type="text" class="form-control" placeholder="<bean:message key="modal.newPatient.day" bundle="ui"/>" ng-model="newPatientCtrl.demographic.dobDay" required>
					</div>
			
				</div> 
				<div class="form-group">
					<label class="col-sm-3 control-label"><bean:message key="modal.newPatient.gender" bundle="ui"/></label>
					<div class="col-sm-5">
					<select class="form-control form-control-details" title="Sex" ng-model="newPatientCtrl.demographic.sex" ng-options="sexes.value as sexes.label for sexes in newPatientCtrl.genders" required/>
					</div>
				</div> 
				<div class="form-group" ng-show="programs.length>1">
					<label class="col-sm-3 control-label"><bean:message key="modal.newPatient.program" bundle="ui"/></label>
					<div class="col-sm-5">
					<select class="form-control form-control-details" title="Program" ng-model="newPatientCtrl.demographic.admissionProgramId" ng-options="pg.id as pg.name for pg in newPatientCtrl.programs" required/>
					</div>
				</div> 
			</form>
		</div>
	</div>
	
	<div class="modal-footer">
		<button ng-click="newPatientCtrl.cancel()" type="button" class="btn btn-default">
			<bean:message key="modal.newPatient.cancel" bundle="ui"/>
		</button>
		<button ng-show="newPatientCtrl.hasRight" ng-click="newPatientCtrl.saver(newDemographic)" type="button" class="btn btn-success">
			<bean:message key="modal.newPatient.submit" bundle="ui"/>
		</button>
	</div>
</div>

	
