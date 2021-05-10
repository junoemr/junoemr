import {
    JUNO_BUTTON_COLOR,
    JUNO_BUTTON_COLOR_PATTERN,
    LABEL_POSITION
} from "../../../../common/components/junoComponentConstants";
import {RosterServiceApi} from "../../../../../generated";

angular.module('Record.Details').component('rosteredHistoryModal', {
    templateUrl: 'src/record/details/components/rosteredHistory/rosteredHistoryModal.jsp',
    bindings: {
        modalInstance: "<",
        resolve: "<",
    },
    controller: [
        "$scope",
        "$http",
        "$httpParamSerializer",
        "NgTableParams",
        function(
            $scope,
            $http,
            $httpParamSerializer,
            NgTableParams
        )
    {
        const ctrl = this;

        ctrl.LABEL_POSITION = LABEL_POSITION;
        ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
        ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

        const rosterApi = new RosterServiceApi($http, $httpParamSerializer, '../ws/rs');

        ctrl.rosteredHistory = [];
        ctrl.tableParams = new NgTableParams(
            {
                page: 1,
                count: -1,
            }
        );

        ctrl.$onInit = async () =>
        {
            ctrl.demographic = ctrl.resolve.demographic;

            rosterApi.getRosteredHistory(ctrl.demographic.demographicNo).then(
                (data) => {
                    ctrl.rosteredHistory = data.data.body;
                });
        }

        ctrl.onCancel = () =>
        {
            ctrl.modalInstance.close();
        }
    }]
});