import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../../../../common/components/junoComponentConstants";
import {RosterServiceApi} from "../../../../../generated";

angular.module('Admin').component('editStatusModal', {
    templateUrl: 'src/admin/section/rosterStatus/modals/editRosterStatusModal.jsp',
    bindings: {
        modalInstance: "<",
        resolve: "<",
    },
    controller: [
        "$scope",
        "$http",
        "$httpParamSerializer",
        "$uibModal",
        function(
            $scope,
            $http,
            $httpParamSerializer,
            $uibModal
        )
        {
            let ctrl = this;
            const rosterApi = new RosterServiceApi($http, $httpParamSerializer, "../ws/rs");

            $scope.LABEL_POSITION = LABEL_POSITION;
            $scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
            $scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
            ctrl.componentStyle = JUNO_STYLE.GREY;

            ctrl.editMode = false;
            ctrl.$onInit = () =>
            {
                ctrl.status = ctrl.resolve.status;
                if (ctrl.status)
                {
                    ctrl.editMode = true;
                }
                else
                {
                    ctrl.status = {
                        active: true,
                        rostered: true,
                    };
                }
            }

            ctrl.close = () =>
            {
                ctrl.modalInstance.close();
            }

            ctrl.onCancel = () =>
            {
                ctrl.close();
            }

            ctrl.onSave = () =>
            {
                if (ctrl.editMode === true)
                {
                    rosterApi.editStatus(ctrl.status.id, ctrl.status).then(
                        () =>
                        {
                            ctrl.close();
                        }
                    )
                }
                else
                {
                    rosterApi.addStatus(ctrl.status).then(
                        function success()
                        {
                            ctrl.close();
                        },
                        function error(error)
                        {
                            console.log(error);
                            Juno.Common.Util.errorAlert($uibModal, "Error",
                                "An error occurred while saving. " +
                                "Ensure the status code you use does not share the same code as another roster status.");
                        }
                    )
                }
            }
        }
    ]
});