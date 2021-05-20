import {RosterServiceApi} from "../../../../generated";
import {
    JUNO_BUTTON_COLOR,
    JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE,
    LABEL_POSITION
} from "../../../common/components/junoComponentConstants";

angular.module('Admin').component('rosterStatusManagement',
    {
        templateUrl: "src/admin/section/rosterStatus/rosterStatusManagement.jsp",
        bindings: {},
        controller: [
            "$scope",
            "$http",
            "$httpParamSerializer",
            "NgTableParams",
            "$uibModal",
            function (
                $scope,
                $http,
                $httpParamSerializer,
                NgTableParams,
                $uibModal)
            {
                let ctrl = this;

                $scope.LABEL_POSITION = LABEL_POSITION;
                $scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
                $scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
                ctrl.componentStyle = JUNO_STYLE.DEFAULT;

                ctrl.rosterStatuses = [];
                const rosterApi = new RosterServiceApi($http, $httpParamSerializer, '../ws/rs');

                ctrl.$onInit = () =>
                {
                    ctrl.tableParams = new NgTableParams(
                        {
                            page: 1,
                            count: -1,
                        }
                    );
                    ctrl.getStatuses();
                }

                ctrl.getStatuses = async () =>
                {
                    await rosterApi.getRosterStatuses().then(
                        (data) =>
                        {
                            ctrl.rosterStatuses = data.data.body.map((status) =>
                            {
                                return {
                                    id: status.id,
                                    rosterStatus: status.rosterStatus,
                                    statusDescription: status.statusDescription,
                                    createdAt: Juno.Common.Util.formatMomentDateTimeNoTimezone(
                                        Juno.Common.Util.getDatetimeNoTimezoneMoment(status.createdAt)),
                                    updatedAt: Juno.Common.Util.formatMomentDateTimeNoTimezone(
                                        Juno.Common.Util.getDatetimeNoTimezoneMoment(status.updatedAt)),
                                    deletedAt: status.active ? null :
                                        Juno.Common.Util.formatMomentDateTimeNoTimezone(
                                            Juno.Common.Util.getDatetimeNoTimezoneMoment(status.deletedAt)),
                                    updatedBy: status.updatedBy,
                                    updatedByProviderName: status.updatedByProviderName,
                                    systemManaged: status.systemManaged,
                                    rostered: status.rostered,
                                    terminated: status.terminated,
                                    active: status.active,
                                }
                            });
                        }
                    );
                }

                ctrl.editStatus = async (status) =>
                {
                    await $uibModal.open(
                        {
                            component: 'editStatusModal',
                            backdrop: 'static',
                            windowClass: 'juno-modal sml',
                            resolve: {
                                status: status,
                            }
                        },
                    )
                }

                ctrl.addStatus = async () =>
                {
                    let loaded = $uibModal.open(
                        {
                            component: 'editStatusModal',
                            backdrop: 'static',
                            windowClass: 'juno-modal sml',
                            resolve: {

                            }
                        }
                    )
                    loaded.result.then(() =>
                    {
                        ctrl.getStatuses();
                    })
                }
            }

        ]
    });