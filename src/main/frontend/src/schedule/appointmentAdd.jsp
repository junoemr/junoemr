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
        <button type="button" class="close" ng-click="appointmentAddCtrl.close()" aria-label="Close">&times;</button>
        <h3 class="modal-title">Create an appointment</h3>
    </div>
    <div class="modal-body">
        <div class="row margin-bottom" ng-if="appointmentAddCtrl.appointment.demographic != null">
            <div class="col-sm-10 col-sm-offset-1">
                <div class="col-sm-2 pull-right">
                    <img class="img-rounded" 
                        ng-src="../imageRenderingServlet?source=local_client&clientId={{appointmentAddCtrl.appointment.demographic.demographicNo}}"/>
                </div>
                <div class="col-sm-2 text-right pull-right">
                    <div class="row blue-text"><h4 class="no-margin">{{appointmentAddCtrl.appointment.demographic.lastName}},
                        {{appointmentAddCtrl.appointment.demographic.firstName}}</h4>
                    </div>
                    <div class="row">{{appointmentAddCtrl.appointment.demographic.hin}}</div>
                    <div class="row">{{appointmentAddCtrl.appointment.demographic.dateOfBirth | date :
                        'yyyy-MM-dd'}}
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-xs-12">
                <ul>
                    <li class="text-danger" ng-repeat="error in errors">{{error}}</li>
                </ul>
            </div>
        </div>

        <div class="row">
            <div class="col-sm-10 col-sm-offset-1">
                <form>
                    <div class="form-group col-sm-6">
                        <label class="control-label">Patient</label>
                        <input type="text"
                            ng-model="appointmentAddCtrl.appointment.demographicName" placeholder="Patient"
                            uib-typeahead="pt.demographicNo as pt.name for pt in appointmentAddCtrl.searchPatients($viewValue)"
                            typeahead-on-select="appointmentAddCtrl.updateDemographicNo($item, $model, $label)"
                            class="form-control form-control-details">
                    </div>

                    <div class="form-group col-sm-6">
                        <label class="control-label">Provider</label> 
                        <input type="text"
                            ng-model="appointmentAddCtrl.appointment.providerName"
                            placeholder="Provider"
                            uib-typeahead="pt.providerNo as pt.name for pt in appointmentAddCtrl.searchProviders($viewValue)"
                            typeahead-on-select="appointmentAddCtrl.updateProviderNo($item, $model, $label)"
                            class="form-control">
                    </div>

                    <div class="form-group col-sm-6">
                        <label class="control-label">Start Time</label>
						<ca-field-time
                                class="form-control form-control-details"
                                ca-template="no_button"
                                ca-name="startTime"
                                ca-model="appointmentAddCtrl.appointment.startTime"
                                placeholder="Start Time" />
                        <%--<div class="bootstrap-timepicker"> --%>
<%--                            <input ng-model="appointmentAddCtrl.appointment.startTime" id="startTime"
                                placeholder="Start Time" class="form-control form-control-details"/>--%>
                        <%--</div>--%>
                    </div>

                    <div class="form-group col-sm-6">
                        <label class="control-label">Duration</label> 
                        <input ng-model="appointmentAddCtrl.appointment.duration"
                            placeholder="Duration" class="form-control"/>
                    </div>

                    <div class="form-group col-sm-6">
                        <label class="control-label">Type</label>
                        <select ng-model="appointmentAddCtrl.appointment.type" ng-init="appointmentAddCtrl.appointment.type=''"
                            ng-options="p.name as p.name for p in appointmentAddCtrl.types" class="form-control" ng-change="appointmentAddCtrl.selectType()">
                        </select>
                    </div>
                    <div class="form-group col-sm-6">
                        <label class="control-label">Critical</label>
                        <select ng-model="appointmentAddCtrl.appointment.critical" ng-options="p.value as p.label for p in appointmentAddCtrl.urgencies"
                            class="form-control">
                        </select>
                    </div>

                    <div class="form-group col-sm-6">
                        <label class="control-label">Reason</label>
                        <textarea ng-model="appointmentAddCtrl.appointment.reason" placeholder="Reason"
                            class="form-control" rows="5">
                        </textarea>
                    </div>

                    <div class="form-group col-sm-6">
                        <label class="control-label">Notes</label>
                        <textarea ng-model="appointmentAddCtrl.appointment.notes" type="text"
                            placeholder="Notes" class="form-control" rows="5">
                        </textarea>
                    </div>

                    <div class="form-group col-sm-6">
                        <label class="control-label">Location</label> 
                        <input ng-model="appointmentAddCtrl.appointment.location"
                            placeholder="Location" class="form-control"/>
                    </div>

                    <div class="form-group col-sm-6">
                        <label class="control-label">Resources</label>
                        <input ng-model="appointmentAddCtrl.appointment.resources"
                            type="text" placeholder="Resources" class="form-control"/>
                    </div>
                </form>

                <%--Old horizontal form layout--%>
                <%--<form class="form-horizontal">
                    <div class="row" ng-show="showErrors === true">
                        <div class="col-xs-12">
                            <ul>
                                <li class="text-danger" ng-repeat="error in errors">{{error}}</li>
                            </ul>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label class="col-sm-2 control-label">Patient</label>
                        <div class="col-sm-4">
                            <input type="text"
                                ng-model="appointment.demographicName" placeholder="Patient"
                                uib-typeahead="pt.demographicNo as pt.name for pt in searchPatients($viewValue)"
                                typeahead-on-select="updateDemographicNo($item, $model, $label)"
                                class="form-control form-control-details">
                        </div>

                        <label class="col-sm-2 control-label">Provider</label> 
                        <div class="col-sm-4">
                            <input type="text"
                                ng-model="appointment.providerName"
                                placeholder="Provider"
                                uib-typeahead="pt.providerNo as pt.name for pt in searchProviders($viewValue)"
                                typeahead-on-select="updateProviderNo($item, $model, $label)"
                                class="form-control">
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-sm-2 control-label">Start Time</label>
                        <div class="col-sm-4" class="bootstrap-timepicker"> 
                            <input ng-model="appointment.startTime" id="startTime"
                                placeholder="Start Time" class="form-control form-control-details"/>
                        </div>

                        <label class="col-sm-2 control-label">Duration</label> 
                        <div class="col-sm-4">
                            <input ng-model="appointment.duration"
                                placeholder="Duration" class="form-control"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-sm-2 control-label">Type</label>
                        <div class="col-sm-4">
                            <select ng-model="appointment.type" ng-init="appointment.type=''"
                                ng-options="p.name as p.name for p in types" class="form-control" ng-change="selectType()">
                            </select>
                        </div>

                        <label class="col-sm-2 control-label">Critical</label>
                        <div class="col-sm-4">
                            <select ng-model="appointment.critical" ng-options="p.value as p.label for p in urgencies"
                                class="form-control">
                            </select>

                        </div>
                    </div>


                    <div class="form-group">
                        <label class="col-sm-2 control-label">Reason</label>
                        <div class="col-sm-4">
                            <textarea ng-model="appointment.reason" placeholder="Reason"
                                class="form-control" rows="5">
                            </textarea>
                        </div>

                        <label class="col-sm-2 control-label">Notes</label>
                        <div class="col-sm-4">
                            <textarea ng-model="appointment.notes" type="text"
                                placeholder="Notes" class="form-control" rows="5">
                            </textarea>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-sm-2 control-label">Location</label> 
                        <div class="col-sm-4">
                            <input ng-model="appointment.location"
                                placeholder="Location" class="form-control"/>
                        </div>

                        <label class="col-sm-2 control-label">Resources</label> 
                        <div class="col-sm-4">
                            <input ng-model="appointment.resources"
                                type="text" placeholder="Resources" class="form-control"/>
                        </div>
                    </div>
                </form>--%>
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <button class="btn btn-default" ng-click="appointmentAddCtrl.close()">
            <bean:message key="global.cancel" bundle="ui"/>
        </button>
        <button class="btn btn-success" ng-click="appointmentAddCtrl.save()">Save</button>
    </div>
</div>

<script>
/*    $(document).ready(function () {
        $("#startTime").timepicker();
    });*/
</script>