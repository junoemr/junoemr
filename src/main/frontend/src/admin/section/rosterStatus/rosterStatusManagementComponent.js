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

                // for drag n drop
                ctrl.dragStartY = 0;

                ctrl.$onInit = () =>
                {
                    ctrl.tableParams = new NgTableParams(
                        {
                            page: 1,
                            count: -1,
                            sorting:
                                {
                                    order: 'asc',
                                }
                        },
                        {
                            // called when sort order changes
                            getData: (params) =>
                            {
                                ctrl.sortMode = params.orderBy();
                            }
                        }
                    );
                    ctrl.getStatuses();
                }

                ctrl.getStatuses = async () =>
                {
                    await rosterApi.getRosterStatuses().then(
                        (data) =>
                        {
                            ctrl.rosterStatuses = data.data.body;
                        }
                    );
                }

                ctrl.editStatus = async (status) =>
                {
                    await $uibModal.open(
                        {
                            component: 'editStatusModal',
                            backdrop: 'static',
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