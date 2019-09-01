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
angular.module('Record.Forms').controller('Record.Forms.AddFormsModalController', [

    '$scope',
    '$http',
    '$location',
    '$stateParams',
    '$state',
    'formService',
    'providerNo',
    '$uibModal',
    '$uibModalInstance',

    function(
        $scope,
        $http,
        $location,
        $stateParams,
        $state,
        formService,
        providerNo,
        $uibModal,
        $uibModalInstance)
    {
        let controller = this;

        $scope.displayFormList = []

        controller.providerNo = providerNo;
        controller.groupSelection = FORM_CONTROLLER_SPECIAL_GROUPS.SELECT_ALL;
        controller.groupSelectedForms = null;
        controller.formSearchStr = '';

        // fill form list with all forms (so that the user can add them to the patients chart).
        controller.getFormsToAdd = function ()
        {
            formService.getAddForms($stateParams.demographicNo).then(
                function success(results)
                {
                    $scope.displayFormList = Juno.Common.Util.toArray(results.list);
                    controller.ensureSubjectNotNull($scope.displayFormList);
                },
                function error(errors)
                {
                    console.error(errors);
                }
            );
        };

        // null subject values do no sort well. force them to empty string
        controller.ensureSubjectNotNull = function (formList)
        {
            formList.forEach(function (form)
            {
                if (form.subject === null)
                {
                    form.subject = '';
                }
            });
        };

        // called on group change
        controller.onGroupChange = function (groupId, selectedForms)
        {
            controller.groupSelection = groupId;
            controller.groupSelectedForms = selectedForms;
        };

        // filter forms for display
        controller.onFilterForms = function (form, index, array)
        {
            // filter on group
            let foundInGroup = true;

            switch(controller.groupSelection)
            {
                case FORM_CONTROLLER_SPECIAL_GROUPS.SELECT_ALL:
                    foundInGroup = true;
                    break;
                case FORM_CONTROLLER_SPECIAL_GROUPS.SELECT_FORM:
                    foundInGroup = form.type === FORM_CONTROLLER_FORM_TYPES.FORM;
                    break;
                case FORM_CONTROLLER_SPECIAL_GROUPS.SELECT_EFORM:
                    foundInGroup = form.type === FORM_CONTROLLER_FORM_TYPES.EFORM;
                    break;
                default:
                    let found = controller.groupSelectedForms.find(function (selectedItem) {
                        return selectedItem.id === form.formId
                    });
                    foundInGroup = (found !== undefined && found !== null);
                    break;
            }

            // filter on search string
            let foundInSearch = true;
            if (controller.formSearchStr.length > 0)
            {
                foundInSearch = form.name.toUpperCase().search("^" + controller.formSearchStr.toUpperCase()+".*") !== -1;
            }

            return foundInGroup && foundInSearch;
        };

        controller.close = function cancel()
        {
            $uibModalInstance.close(null);
        };

        controller.getFormsToAdd();
    }
]);