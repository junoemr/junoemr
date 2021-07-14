import {
    JUNO_BUTTON_COLOR,
    JUNO_BUTTON_COLOR_PATTERN,
    LABEL_POSITION
} from "../../../../common/components/junoComponentConstants";
import {DemographicApi} from "../../../../../generated";

angular.module('Record.Details').component('externalContactsModal', {
    templateUrl: 'src/record/details/components/externalContacts/externalContactsModal.jsp',
    bindings: {
        modalInstance: "<",
        resolve: "<",
    },
    controller: [
        "$scope",
        "$http",
        "$httpParamSerializer",
        "NgTableParams",
        function (
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
            ctrl.contact = null;
            ctrl.contactType = null;
            ctrl.demo = null;

            ctrl.$onInit = () =>
            {
                ctrl.contact = ctrl.resolve.demoContact;

                if (ctrl.contact.type === 1)
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
        }]
});