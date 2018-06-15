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
<div class="modal-content" id="new-patient-page">
	<div class="modal-header">
		<button type="button" class="close" ng-click="newPatientCtrl.cancel()" aria-label="Close">&times;</button>
		<h3 class="modal-title"><bean:message key="modal.newPatient.title" bundle="ui"/></h3>
	</div>

	<div class="modal-body" ng-hide="newPatientCtrl.hasRight">
		<bean:message key="modal.newPatient.noRights" bundle="ui"/>
	</div>

	<div class="modal-body" ng-show="newPatientCtrl.hasRight">
		<div class="row">
			<div class="col-sm-6 col-sm-offset-3">
				<form name="newDemographic">
					<div class="form-group col-sm-12">
						<label for="lastName" class="control-label"><bean:message key="modal.newPatient.lastName" bundle="ui"/></label>
						<input type="text" class="form-control" 
							id="lastName" 
							placeholder="<bean:message key="modal.newPatient.lastName" bundle="ui"/>" 
							name="lastName" 
							ng-model="newPatientCtrl.demographic.lastName" 
							ng-change="newPatientCtrl.capName()" 
							required>
					</div>
					<div class="form-group col-sm-12">
						<label class="control-label"><bean:message key="modal.newPatient.firstName" bundle="ui"/></label>
						<input type="text" class="form-control" placeholder="<bean:message key="modal.newPatient.firstName" bundle="ui"/>" ng-model="newPatientCtrl.demographic.firstName" ng-change="newPatientCtrl.capName()" required>
					</div>
					<div class="form-group col-sm-12">
						<label for="birthYear" class="control-label" ><bean:message key="modal.newPatient.birth" bundle="ui"/></label>
						<div class="input-group">
							<input type="text" id="new-patient-dob-month"
								class="form-control" 
								placeholder="MM" 
								ng-model="newPatientCtrl.demographic.dobMonth" 
								required
								maxlength="2">
							<input type="text" id="new-patient-dob-day"
								class="form-control" 
								placeholder="DD" 
								ng-model="newPatientCtrl.demographic.dobDay" 
								required
								maxlength="2">
							<input type="text"  id="new-patient-dob-year"
								class="form-control" 
								id="birthYear" 
								placeholder="YYYY" 
								ng-model="newPatientCtrl.demographic.dobYear" 
								required
								maxlength="4">
						</div>
					</div> 
					<div class="form-group col-sm-12">
						<label class="control-label"><bean:message key="modal.newPatient.gender" bundle="ui"/></label>
						<select class="form-control form-control-details" title="Sex" 
							ng-model="newPatientCtrl.demographic.sex" 
							ng-options="sexes.value as sexes.label for sexes in newPatientCtrl.genders" 
							required/>
					</div> 
					<div class="form-group col-sm-12" ng-show="newPatientCtrl.programs.length>1">
						<label class="control-label"><bean:message key="modal.newPatient.program" bundle="ui"/></label>
						<select class="form-control form-control-details" title="Program" 
							ng-model="newPatientCtrl.demographic.admissionProgramId" 
							ng-options="pg.id as pg.name for pg in newPatientCtrl.programs" 
							required/>
					</div> 
				</form>
			</div>
		</div>
	</div>
	
	<div class="modal-footer">
		<button ng-click="newPatientCtrl.cancel()" type="button" class="btn btn-default">
			<bean:message key="modal.newPatient.cancel" bundle="ui"/>
		</button>
		<button ng-show="newPatientCtrl.hasRight" ng-click="newPatientCtrl.save(newDemographic)" ng-disabled="newPatientCtrl.submitDisabled"
				type="button" class="btn btn-success">
			<bean:message key="modal.newPatient.submit" bundle="ui"/>
		</button>
	</div>
</div>

	
