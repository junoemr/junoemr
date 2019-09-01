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
<juno-modal id="add-forms-modal">
    <modal-title>
        <h2>Add Forms</h2>
    </modal-title>
    <modal-ctl-buttons>
        <button type="button" class="btn btn-icon" aria-label="Close"
                ng-click="addFormsModalCtrl.close()"
                title="Cancel">
            <i class="icon icon-modal-ctl icon-close"></i>
        </button>
    </modal-ctl-buttons>
    <modal-body>
        <div class="flex-row">
            <div class="container-fluid group-select-panel-placeholder">
                <div class="container-fluid group-select-panel">
                    <input type="text" class="form-control search-query" ng-model="addFormsModalCtrl.formSearchStr" placeholder="Filter Forms">
                    <group-selector-component group-change="addFormsModalCtrl.onGroupChange(groupId, selectedForms)"
                                              group-selection="addFormsModalCtrl.groupSelection"></group-selector-component>
                </div>
            </div>
            <div class="col-md-10">
                <form-view-component form-list="displayFormList"
                                     provider-no="addFormsModalCtrl.providerNo" filter-forms="addFormsModalCtrl.onFilterForms(form, index, array)"
                                     view-state="FORM_CONTROLLER_STATES.ADD" instanced-forms="false"></form-view-component>
            </div>
        </div>
    </modal-body>
    <modal-footer>
        <button class="btn btn-primary" ng-click="addFormsModalCtrl.close()">Done</button>
    </modal-footer>
</juno-modal>