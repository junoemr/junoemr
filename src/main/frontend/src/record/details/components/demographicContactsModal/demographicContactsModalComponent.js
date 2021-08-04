import {
    JUNO_BUTTON_COLOR,
    JUNO_BUTTON_COLOR_PATTERN,
    LABEL_POSITION,
} from "../../../../common/components/junoComponentConstants";
import {DemographicApi} from "../../../../../generated";

angular.module('Record.Details').component('demographicContactsModal', {
    templateUrl: 'src/record/details/components/demographicContactsModal/demographicContactsModal.jsp',
    bindings: {
        modalInstance: "<",
        title: "<",
        resolve: "<",
        componentStyle: "<",
    },
    controller: [
        "$scope",
        "$http",
        "$httpParamSerializer",
        "$uibModal",
        "$state",
        "uxService",
        "demographicService",
        function (
            $scope,
            $http,
            $httpParamSerializer,
            $uibModal,
            $state,
            uxService,
            demographicService,
        )
        {
            const ctrl = this;
            const demographicApi = new DemographicApi($http, $httpParamSerializer, "../ws/rs");
            const EDIT_TITLE = "Edit contact";
            const NO_EDIT_TITLE = "Editing is currently only available for internal contacts";

            ctrl.types = {
                PROVIDER: 0,
                INTERNAL: 1,
                EXTERNAL: 2,
                PROFESSIONAL_SPECIALIST: 3,
            };

            ctrl.typesText = {
                PROVIDER_TEXT: "Provider Contact",
                INTERNAL_TEXT: "Internal Contact",
                EXTERNAL_TEXT: "External Contact",
                PROFESSIONAL_SPECIALIST_TEXT: "Professional Specialist Contact"
            };

            ctrl.LABEL_POSITION = LABEL_POSITION;
            ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
            ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

            ctrl.contact = null;
            ctrl.contactType = null;
            ctrl.demo = null;

            ctrl.$onInit = () =>
            {
                ctrl.dataChanged = false;
                ctrl.contact = ctrl.resolve.demoContact;
                ctrl.demographic = ctrl.resolve.demographic;
                ctrl.editable = false;

                switch(ctrl.contact.type)
                {
                    case ctrl.types.INTERNAL:
                        ctrl.title = EDIT_TITLE;
                        ctrl.contactType = ctrl.typesText.INTERNAL_TEXT;
                        break;
                    case ctrl.types.EXTERNAL:
                        ctrl.title = NO_EDIT_TITLE;
                        ctrl.contactType = ctrl.typesText.EXTERNAL_TEXT;
                        break;
                    case ctrl.types.PROVIDER:
                        ctrl.title = NO_EDIT_TITLE;
                        ctrl.contactType = ctrl.typesText.PROVIDER_TEXT;
                        break;
                    case ctrl.types.PROFESSIONAL_SPECIALIST:
                        ctrl.title = NO_EDIT_TITLE;
                        ctrl.contactType = ctrl.typesText.PROFESSIONAL_SPECIALIST_TEXT;
                }
            };

            ctrl.onCancel = () =>
            {
                ctrl.modalInstance.close();
            };

            ctrl.edit = () =>
            {
                if (ctrl.contact.type === ctrl.types.INTERNAL)
                {
                    ctrl.getTabs(ctrl.contact.contactId);
                }
                else
                {
                    ctrl.externalContactEdit();
                }
            };

            ctrl.externalContactEdit = function externalContactsEdit()
            {
                ctrl.editable = true;
                ctrl.dataChanged = false;
            };

            ctrl.save = function save()
            {
                ctrl.saving = true;

                demographicApi.updateExternalContact(ctrl.demographic, ctrl.contact).then(
                    (data) => {
                        console.log(data);
                        ctrl.onCancel();
                    });
            }

            ctrl.resetEditState = function resetEditState()
		{
			ctrl.saving = false;
			ctrl.dataChanged = false;
		};

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
                        Juno.Common.Util.alert("Unable to open tab.", errors);
                    });
            };

            ctrl.changeTab = function changeTab(tab)
            {
                if (tab.state)
                {
                    if (tab.demoId)
                    {
                        $state.go(tab.state[0],
                            {
                                demographicNo: tab.demoId
                            });
                        ctrl.modalInstance.close();
                    }
                    else
                    {
                        $state.go(tab.state[0]);
                    }
                }
            };

		//monitor data changed
		$scope.$watch(function()
		{
			return ctrl.contact;
		}, function(newValue, oldValue)
		{
			if (newValue !== oldValue && angular.isDefined(oldValue) && angular.isDefined(newValue))
			{
				ctrl.dataChanged = true;
				console.log(ctrl.dataChanged);
			}

		}, true);
        }]
});