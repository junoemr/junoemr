import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../../../../common/components/junoComponentConstants";
import {ProvidersServiceApi, RosterServiceApi} from "../../../../../generated";

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
        "referralDoctorsService",
        function($scope,
                 $uibModal,
                 $http,
                 $httpParamSerializer,
                 staticDataService,
                 referralDoctorsService)
        {
            let ctrl = this;
            let rosterApi = new RosterServiceApi($http, $httpParamSerializer, '../ws/rs');
            let providersServiceApi = new ProvidersServiceApi($http, $httpParamSerializer, "../ws/rs");

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

            ctrl.updateFamilyDoctors = (docSearchString, docReferralNo) =>
            {
                referralDoctorsService.searchReferralDoctors(docSearchString, docReferralNo, 1, 10).then(
                    function success(results)
                    {
                        let familyDoctors = new Array(results.length);

                        for (let i = 0; i < results.length; i++)
                        {
                            let displayName = results[i].lastName + ', ' + results[i].firstName;
                            familyDoctors[i] = {
                                label: displayName,
                                value: displayName,
                                referralNo: results[i].referralNo
                            };
                            if (results[i].specialtyType != null && results[i].specialtyType !== "")
                            {
                                familyDoctors[i].label += " [" + results[i].referralNo + "]";
                            }
                        }

                        ctrl.familyDoctors = familyDoctors;
                    },
                    function failure(errors)
                    {
                        console.log(errors);
                    }
                ).then(
                    providersServiceApi.getActive().then(
                        function success(results)
                        {
                            for (let provider of results.data.body)
                            {
                                let fullName = provider.lastName + ", " + provider.firstName;
                                let displayName = provider.lastName + ", " + provider.firstName;
                                if (provider.ohipNo !== "")
                                {
                                    displayName += " [" + provider.ohipNo + "]";
                                }
                                ctrl.familyDoctors.push({
                                    label: displayName,
                                    value: fullName,
                                    referralNo: provider.ohipNo
                                });
                            }
                        },
                        function failure(errors)
                        {
                            console.error(errors);
                        }
                    )
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
                            component: 'rosteredHistoryModal',
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