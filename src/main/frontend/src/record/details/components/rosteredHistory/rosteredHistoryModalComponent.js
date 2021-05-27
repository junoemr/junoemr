import {
    JUNO_BUTTON_COLOR,
    JUNO_BUTTON_COLOR_PATTERN,
    LABEL_POSITION
} from "../../../../common/components/junoComponentConstants";
import {DemographicApi} from "../../../../../generated";

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

        const demographicApi = new DemographicApi($http, $httpParamSerializer, '../ws/rs');

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

            demographicApi.getRosteredHistory(ctrl.demographic.demographicNo).then(
                (data) => {
                    ctrl.rosteredHistory = data.data.body.map((entry) => {
                        return {
                            statusDescription: entry.rosterStatus.statusDescription,
                            rosteredPhysician: entry.rosteredPhysician,
                            rosterDate: entry.rosterDate == null? "" :
                                Juno.Common.Util.formatMomentDate(
                                    Juno.Common.Util.getDatetimeNoTimezoneMoment(entry.rosterDate)),
                            rosterTerminationDate: entry.rosterTerminationDate == null ? "" :
                                Juno.Common.Util.formatMomentDate(
                                    Juno.Common.Util.getDatetimeNoTimezoneMoment(entry.rosterTerminationDate)),
                            rosterTerminationDescription: entry.rosterTerminationDescription,
                            addedAt: Juno.Common.Util.getDatetimeNoTimezoneMoment(entry.addedAt),
                        }
                    });
                });
        }

        ctrl.onCancel = () =>
        {
            ctrl.modalInstance.close();
        }
    }]
});