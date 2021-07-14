import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../../../../common/components/junoComponentConstants";
import {ReferralDoctorsApi, RosterServiceApi} from "../../../../../generated";

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
            let referralDoctorsApi = new ReferralDoctorsApi($http, $httpParamSerializer, "../ws/rs");

            $scope.LABEL_POSITION = LABEL_POSITION;
            $scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
            $scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

            ctrl.numberRegex=/^\d*$/
            ctrl.familyDoctors = [{value: "", label: "--"}];
            ctrl.rosterTermReasons = staticDataService.getRosterTerminationReasons();

            ctrl.rosterDateValid = true;
            ctrl.terminationDateValid = true;

            ctrl.$onInit = () =>
            {
                ctrl.validations["rosterDate"] = Juno.Validations.validationCustom(() => ctrl.rosterDateValid);
                ctrl.validations["terminationDate"] = Juno.Validations.validationCustom(() => ctrl.terminationDateValid);

                ctrl.validations["rosterTerminationReason"] = Juno.Validations.validationFieldOr(
                    Juno.Validations.validationCustom(() => ctrl.ngModel.rosterStatus !== "TE"),
                    Juno.Validations.validationFieldRequired(ctrl, "ngModel.rosterTerminationReason")
                );

                ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT

                rosterApi.getRosterStatuses(true).then(
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

            ctrl.updateFamilyDoctors = async (docSearchString, docReferralNo) =>
            {
                referralDoctorsApi.searchEnrolledDoctors(docSearchString, docReferralNo, 1, 10).then(
                    function success(results)
                    {
                        let familyDoctors = [];
                        if (!results.data.body || results.data.body.length === 0)
                        {
                            familyDoctors.push({value: "", label: "--"})
                        }

                        for (let i = 0; i < results.data.body.length; i++)
                        {
                            let familyDoctor = results.data.body[i];
                            let displayName = familyDoctor.lastName + ', ' + familyDoctor.firstName;
                            let label = familyDoctor.lastName + ', ' + familyDoctor.firstName;

                            if (familyDoctor.referralNo != null && familyDoctor.referralNo !== "")
                            {
                                label += "[" + familyDoctor.referralNo + "]";
                            }

                            familyDoctors.push({
                                label: label,
                                value: displayName,
                                referralNo: familyDoctor.referralNo
                            });
                        }
                        ctrl.familyDoctors = familyDoctors;
                    },
                    function failure(errors)
                    {
                        console.error(errors);
                    }
                );
            }

            ctrl.updateFamilyDocNo = (value) =>
            {
                ctrl.ngModel.scrFamilyDocNo = value.referralNo;
            }

            ctrl.openRosteredHistoryModal = async () =>
            {
                try
                {
                    await $uibModal.open(
                        {
                            component: 'externalContactsModal',
                            backdrop: 'static',
                            windowClass: "juno-modal lg",
                            resolve: {
                                demographic: ctrl.ngModel,
                            }
                        })
                }
                catch(_reason)
                {
                    // do nothing on cancel
                }
            }
        }
    ]
});