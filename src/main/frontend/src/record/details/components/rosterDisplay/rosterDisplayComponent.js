import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../../../../common/components/junoComponentConstants";
import {RosterServiceApi} from "../../../../../generated";

angular.module('Record.Details').component('rosterDisplaySection', {
    templateUrl: 'src/record/details/components/rosterDisplay/rosterDisplay.jsp',
    bindings: {
        ngModel: "=",
        validations: "=",
        componentStyle: "<?"
    },
    controller: [ "$scope",
        "$uibModal",
        "$http",
        "$httpParamSerializer",
        "staticDataService",
        function($scope,
                 $uibModal,
                 $http,
                 $httpParamSerializer,
                 staticDataService)
        {
            let ctrl = this;
            let rosterApi = new RosterServiceApi($http, $httpParamSerializer, '../ws/rs');

            $scope.LABEL_POSITION = LABEL_POSITION;
            $scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
            $scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

            ctrl.rosterTermReasons = staticDataService.getRosterTerminationReasons();

            ctrl.rosterDateValid = true;
            ctrl.terminationDateValid = true;

            ctrl.$onInit = () =>
            {
                ctrl.validations["rosterDate"] = Juno.Validations.validationCustom(() => ctrl.rosterDateValid);
                ctrl.validations["terminationDate"] = Juno.Validations.validationCustom(() => ctrl.terminationDateValid);

                ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT

                rosterApi.getActiveRosterStatuses().then(
                    (data) =>
                    {
                        ctrl.rosterStatusList = data.data.body.map((entry) => {
                            return {
                                label: entry.rosterStatus + " - " + entry.statusDescription,
                                value: entry.rosterStatus
                            }
                        });
                    }
                );
            }

            ctrl.openRosteredHistoryModal = async () =>
            {
                await $uibModal.open(
                    {
                        component: 'rosteredHistoryModal',
                        backdrop: 'static',
                        windowClass: "juno-modal lg",
                        resolve: {
                            demographic: ctrl.ngModel,
                        }
                    }
                ).catch((_reason) =>
                {
                    // do nothing on cancel
                });
            }
        }
    ]
});