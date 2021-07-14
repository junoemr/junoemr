import {
    JUNO_BUTTON_COLOR,
    JUNO_BUTTON_COLOR_PATTERN,
    LABEL_POSITION,
    JUNO_STYLE,
} from "../../../../common/components/junoComponentConstants";

angular.module('Record.Details').component('demographicContactsModal', {
    templateUrl: 'src/record/details/components/demographicContactsModal/demographicContactsModal.jsp',
    bindings: {
        modalInstance: "<",
        resolve: "<",
    },
    controller: [
        "$scope",
        "$state",
        "uxService",
        function (
            $scope,
            $state,
            uxService,
        )
        {
            const ctrl = this;

            const PROVIDER = 0;
            const INTERNAL = 1;
            const EXTERNAL = 2;
            const PROFESSIONALSPECIALIST = 3;
            ctrl.LABEL_POSITION = LABEL_POSITION.TOP;
            ctrl.JUNO_STYLE = JUNO_STYLE.GREY;
            ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
            ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
            ctrl.contact = null;
            ctrl.contactType = null;
            ctrl.demo = null;

            ctrl.$onInit = () =>
            {
                ctrl.contact = ctrl.resolve.demoContact;

                if (ctrl.contact.type === INTERNAL)
                {
                    ctrl.contactType = "Internal Contact";
                } else
                {
                    ctrl.contactType = "External Contact";
                }
            }

            ctrl.onCancel = () =>
            {
                ctrl.modalInstance.close();
            }

            ctrl.edit = () =>
            {
                if (ctrl.contact.type === INTERNAL)
                {
                    ctrl.getTabs(ctrl.contact.contactId)
                }
                else
                {
                    //not yet implemented
                }
            }
            ctrl.getTabs = function getTabs(contactId)
            {
                uxService.menu(contactId).then(
                    function success(results)
                    {
                        ctrl.tab = results[0];
                        ctrl.tab.demoId = contactId;
                        ctrl.changeTab(ctrl.tab);
                    },
                    function error(errors)
                    {
                        console.log(errors);
                    });
            };

            ctrl.changeTab = function changeTab(tab)
            {
                if (Juno.Common.Util.isDefinedAndNotNull(tab.state))
                {
                    if (Juno.Common.Util.isDefinedAndNotNull(tab.demoId))
                    {
                        $state.go(tab.state[0],
                            {
                                demographicNo: tab.demoId
                            });
                        ctrl.modalInstance.close();
                    } else
                    {
                        $state.go(tab.state[0]);
                    }
                }
            };

        }]
});