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

<div id="patient-search-page" ng-init="patientSearchCtrl.init()">

	<div class="patient-search-header vertical-align">
		<div class="col-lg-12">
			<h3>
				<bean:message key="patientsearch.title" bundle="ui"/>
			</h3>
		</div>
	</div>

	<juno-security-check show-placeholder="true" permissions="patientSearchCtrl.SecurityPermissions.DEMOGRAPHIC_READ">
		<div class="col-lg-12 patient-search-content">
			<form role="form"
				  id="search-form"
				  ng-submit="patientSearchCtrl.searchPatients()">
				<div class="form-group">
					<div class="row search-filters">
						<div class="col-sm-3 col-xs-12">
							<label>Search By</label>
							<select ng-model="patientSearchCtrl.search.type"
									ng-change="patientSearchCtrl.clearParams(patientSearchCtrl.search.type)"
									class="form-control">
								<option value="{{patientSearchCtrl.SEARCH_MODE.Name}}">
									<bean:message key="patientsearch.type.name" bundle="ui"/>
								</option>
								<option value="{{patientSearchCtrl.SEARCH_MODE.Phone}}">
									<bean:message key="patientsearch.type.phone" bundle="ui"/>
								</option>
								<option value="{{patientSearchCtrl.SEARCH_MODE.DOB}}">
									<bean:message key="patientsearch.type.dob" bundle="ui"/>
								</option>
								<option value="{{patientSearchCtrl.SEARCH_MODE.Address}}">
									<bean:message key="patientsearch.type.address" bundle="ui"/>
								</option>
								<option value="{{patientSearchCtrl.SEARCH_MODE.Hin}}">
									<bean:message key="patientsearch.type.hin" bundle="ui"/>
								</option>
								<option value="{{patientSearchCtrl.SEARCH_MODE.Email}}">
									<bean:message key="patientsearch.type.email" bundle="ui"/>
								</option>
								<option value="{{patientSearchCtrl.SEARCH_MODE.ChartNo}}">
									<bean:message key="patientsearch.type.chartNo" bundle="ui"/>
								</option>
								<option value="{{patientSearchCtrl.SEARCH_MODE.DemographicNo}}">
									<bean:message key="patientsearch.type.demographicNo" bundle="ui"/>
								</option>
							</select>
						</div>

						<div class="col-sm-3 col-xs-12">
							<label>Search Term</label>
							<input ng-model="patientSearchCtrl.search.term"
								   type="text"
								   class="form-control"
								   placeholder="{{patientSearchCtrl.searchTermPlaceHolder}}"/>
						</div>

						<div class="col-sm-3 col-xs-12">
							<label>What to Show</label>
							<select ng-model="patientSearchCtrl.search.status" class="form-control">
								<option value="{{patientSearchCtrl.STATUS_MODE.ALL}}">
									<bean:message key="patientsearch.showAll" bundle="ui"/>
								</option>
								<option value="{{patientSearchCtrl.STATUS_MODE.ACTIVE}}">
									<bean:message key="patientsearch.showActiveOnly" bundle="ui"/>
								</option>
								<option value="{{patientSearchCtrl.STATUS_MODE.INACTIVE}}">
									<bean:message key="patientsearch.showInactiveOnly" bundle="ui"/>
								</option>
							</select>
						</div>

						<div class="col-sm-2 col-xs-3">
							<label>Options</label>
							<div class="col-xs-12 demo-search-options">
								<a class="btn dropdown-toggle" data-toggle="dropdown">
									<span class="glyphicon glyphicon-cog"></span>
									<span class="caret"></span>
								</a>
								<ul class="dropdown-menu">
									<li>
										<a ng-click="patientSearchCtrl.toggleParam('integrator'); $event.stopPropagation();">
											<input ng-model="patientSearchCtrl.search.integrator"
												   type="checkbox"
												   ng-click="$event.stopPropagation();"/>
											<bean:message key="patientsearch.includeIntegrator" bundle="ui"/>
										</a>
									</li>
									<li>
										<a ng-click="patientSearchCtrl.toggleParam('outofdomain'); $event.stopPropagation();">
											<input ng-model="patientSearchCtrl.search.outofdomain"
												   type="checkbox"
												   ng-click="$event.stopPropagation();"/>
											<bean:message key="patientsearch.outOfDomain" bundle="ui"/>
										</a>
									</li>
								</ul>
							</div>
						</div>
					</div>
					<div class="row search-buttons">
						<div class="col-xs-12">
							<button class="btn btn-primary"
									type="submit">
								<bean:message key="global.search" bundle="ui"/>
							</button>
							<button class="btn btn-default"
									type="button"
									ng-click="patientSearchCtrl.clearParams()">
								<bean:message key="global.clear" bundle="ui"/>
							</button>
						</div>
					</div>
				</div>
			</form>
			<div class="col-xs-6">
				<button class="btn btn-warning"
						ng-show="patientSearchCtrl.integratorResults != null && patientSearchCtrl.integratorResults.total > 0"
						ng-click="patientSearchCtrl.showIntegratorResults()"><span
						class="glyphicon glyphicon-exclamation-sign"></span>
					<bean:message key="patientsearch.remoteMatches" bundle="ui"/>
				</button>
			</div>

			<table ng-table="patientSearchCtrl.tableParams"
				   show-filter="false"
				   class="table table-hover table-striped table-bordered"
				   id="patient-search-table">
				<tbody>
				<tr ng-repeat="patient in $data"
					ng-mouseover="patient.$selected=true"
					ng-mouseout="patient.$selected=false"
					ng-class="{'active': patient.$selected}"
					ng-click="patientSearchCtrl.loadRecord(patient.demographicNo)">

					<td data-title="'<bean:message key="patientsearch.header.id" bundle="ui"/>'"
						sortable="'DemographicNo'">
						{{patient.demographicNo}}
					</td>
					<td data-title="'<bean:message key="patientsearch.header.name" bundle="ui"/>'"
						sortable="'DemographicName'">
						{{patient.lastName}}, {{patient.firstName}}
					</td>
					<td data-title="'<bean:message key="patientsearch.header.chartNo" bundle="ui"/>'"
						sortable="'ChartNo'">
						{{patient.chartNo}}
					</td>
					<td data-title="'<bean:message key="patientsearch.header.gender" bundle="ui"/>'"
						class="text-center"
						sortable="'Sex'">
						{{patient.sex}}
					</td>
					<td data-title="'<bean:message key="patientsearch.header.dob" bundle="ui"/>'"
						class="text-center"
						sortable="'DOB'">
						{{patient.formattedDOB}}
					<td data-title="'<bean:message key="patientsearch.header.doctor" bundle="ui"/>'"
						sortable="'ProviderName'">
						{{patient.providerName}}
					</td>
					<td data-title="'<bean:message key="patientsearch.header.rosterStatus" bundle="ui"/>'"
						class="text-center"
						sortable="'RosterStatus'">
						{{patient.rosterStatus}}
					</td>
					<td data-title="'<bean:message key="patientsearch.header.patientStatus" bundle="ui"/>'"
						class="text-center"
						sortable="'Status'">
						{{patient.patientStatus}}
					</td>
					<td data-title="'<bean:message key="patientsearch.header.phone" bundle="ui"/>'"
						sortable="'Phone'">
						{{patient.phone}}
					</td>
				</tr>
				</tbody>

			</table>
		</div>
	</juno-security-check>
</div>
