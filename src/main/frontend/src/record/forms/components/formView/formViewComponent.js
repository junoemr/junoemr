/*
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
*/
import {FORM_CONTROLLER_FORM_TYPES, FORM_CONTROLLER_SORT_MODES, FORM_CONTROLLER_STATES} from "../../formsConstants";
import {SecurityPermissions} from "../../../../common/security/securityConstants";

angular.module('Record.Forms').component('formViewComponent', {
    templateUrl: 'src/record/forms/components/formView/formView.jsp',
    bindings: {
        formList: '=',

        providerNo: '<',
        viewState: '<', //FORM_CONTROLLER_STATES
        instancedForms: '<', // true if forms are instance types

        filterForms: '&',
        reloadForms: '&'
    },
    controller:[
        'formService',
        '$scope',
        '$stateParams',
        'NgTableParams',
        'securityRolesService',
        function (
            formService,
            $scope,
            $stateParams,
            NgTableParams,
            securityRolesService)
    {
        let ctrl = this;

        $scope.FORM_CONTROLLER_FORM_TYPES = FORM_CONTROLLER_FORM_TYPES;
        ctrl.sortMode = FORM_CONTROLLER_SORT_MODES.FORM_NAME;
        ctrl.SecurityPermissions = SecurityPermissions;

        ctrl.canOpenForm = (form) =>
        {
            return (form.id || !form.id && securityRolesService.hasSecurityPrivileges(SecurityPermissions.FORM_CREATE))
        }
        ctrl.canOpenEForm = (eform) =>
        {
            return (eform.id || !eform.id && securityRolesService.hasSecurityPrivileges(SecurityPermissions.EFORM_CREATE))
        }

        ctrl.openEForm = function (form)
        {
            if(ctrl.canOpenEForm(form))
            {
                if (ctrl.instancedForms)
                {
                    formService.openEFormInstancePopup($stateParams.demographicNo, form.id).then(function (val)
                    {
                        ctrl.reloadForms({});
                    });
                }
                else
                {
                    formService.openEFormPopup($stateParams.demographicNo, form.formId).then(function (val)
                    {
                        ctrl.reloadForms({});
                    });
                }
            }
        };

        ctrl.openForm = function (form)
        {
            if(ctrl.canOpenForm(form))
            {
                if (ctrl.instancedForms)
                {
                    formService.openFormInstancePopup(form.name, $stateParams.demographicNo, $stateParams.appointmentNo, form.id).then(function (val)
                    {
                        ctrl.reloadForms({});
                    });
                }
                else
                {
                    formService.openFormPopup(ctrl.providerNo, $stateParams.demographicNo, $stateParams.appointmentNo, form.subject).then(function (val)
                    {
                        ctrl.reloadForms({});
                    });
                }
            }
        };

        ctrl.doFilterForms = function(form, index, array)
        {
            return ctrl.filterForms({form:form, index:index, array:array});
        };

        ctrl.canDeleteForm = (type) =>
        {
            if(type === FORM_CONTROLLER_FORM_TYPES.EFORM)
            {
                return securityRolesService.hasSecurityPrivileges(SecurityPermissions.EFORM_DELETE);
            }
            else if(type === FORM_CONTROLLER_FORM_TYPES.FORM)
            {
                return securityRolesService.hasSecurityPrivileges(SecurityPermissions.FORM_DELETE);
            }
            return false;
        }

        ctrl.deleteForm = function (id, type)
        {
            let ok = confirm("Are you sure you want to delete this eform?");
            if (ok)
            {
                formService.deleteForm(id, type).then(
                    function success (result) {
                        ctrl.formList.splice(ctrl.formList.findIndex(function (form)
                        {
                            return form.id === id && form.type === type;
                        }),1);
                    },
                    function error (result) {
                        console.error("Failed to delete Form, id: " + id);
                    }
                )
            }
        };

        ctrl.canRestoreForm = (type) =>
        {
            if(type === FORM_CONTROLLER_FORM_TYPES.EFORM)
            {
                return securityRolesService.hasSecurityPrivileges(SecurityPermissions.EFORM_UPDATE);
            }
            else if(type === FORM_CONTROLLER_FORM_TYPES.FORM)
            {
                return securityRolesService.hasSecurityPrivileges(SecurityPermissions.FORM_UPDATE);
            }
            return false;
        }

        ctrl.restoreForm = function (id, type)
        {
            let ok = confirm("Are you sure you want to restore this eform?");
            if (ok)
            {
                formService.restoreForm(id, type).then(
                    function success(result)
                    {
                        ctrl.formList.splice(ctrl.formList.findIndex(function (form)
                        {
                            return form.id === id && form.type === type;
                        }),1);
                    },
                    function error (result)
                    {
                        console.error("Failed to restore form: " + id);
                    }
                )
            }
        };

        ctrl.$onInit = function () {
            // configure ngTable
            ctrl.tableParams = new NgTableParams(
                {
                    page: 1, // show first page
                    count: -1, // unlimited
                    sorting:
                        {
                            name: 'asc',
                        }
                },
                {
                    // called when sort order changes
                    getData: function (params) {
                        ctrl.sortMode = params.orderBy();
                    }
                });
            ctrl.disabled = ctrl.disabled || false;

            ctrl.cols = [
                {title: 'Form Name', field: 'name', visible: true, sortable: 'name', displayClass: 'col-md-3'},
                {title: 'Additional Information', field: 'subject', visible: true, sortable: 'subject', displayClass: 'col-md-5'},
                {title: 'Modified Date', field: 'date', visible: true, sortable: 'date', displayClass: 'col-md-2'}
            ];

            switch (ctrl.viewState) {
                case FORM_CONTROLLER_STATES.COMPLETED:
                    ctrl.cols.push({title: 'Action', field: 'delete', visible: true, displayClass: 'col-md-1'})
                    break;
                case FORM_CONTROLLER_STATES.REVISION:
                    ctrl.cols.push({title: 'Creation Date', field: 'createDate', sortable: 'createDate', visible: true, displayClass: 'col-md-2'})
                    break;
                case FORM_CONTROLLER_STATES.DELETED:
                    ctrl.cols.push({title: 'Action', field: 'restore', visible: true, displayClass: 'col-md-1'})
                    break;

            }
        };
    }]
});