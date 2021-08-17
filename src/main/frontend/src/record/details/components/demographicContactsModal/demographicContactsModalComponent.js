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
        function (
            $scope,
            $http,
            $httpParamSerializer,
            $uibModal,
            $state,
            uxService,
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

            ctrl.phoneNumberRegex = /^[\d-\s()]*$/;
            ctrl.contact = null;
            ctrl.contactType = null;
            ctrl.demo = null;

            ctrl.valid = {
                email : true,
                postal: true
            }

            ctrl.$onInit = () =>
            {
                ctrl.dataChanged = false;
                ctrl.contact = angular.copy(ctrl.resolve.demoContact);
                ctrl.contactOnStart = ctrl.resolve.demoContact;
                ctrl.demographic = ctrl.resolve.demographic;
                ctrl.editable = false;

                switch(ctrl.contact.type)
                {
                    case ctrl.types.INTERNAL:
                        ctrl.title = EDIT_TITLE;
                        ctrl.contactType = ctrl.typesText.INTERNAL_TEXT;
                        break;
                    case ctrl.types.EXTERNAL:
                        ctrl.title = EDIT_TITLE;
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

            ctrl.onCancel = (data) =>
            {
                ctrl.modalInstance.close(data);
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
                ctrl.valid.postal = true;
                ctrl.valid.email = true;

                if (!ctrl.validate())
			{
				Juno.Common.Util.errorAlert($uibModal, 'Error', 'Some fields are invalid, Please correct the highlighted fields');
				return;
			}

                ctrl.saving = true;
                demographicApi.updateExternalContact(ctrl.demographic, ctrl.contact.contactId, ctrl.contact).then(
                    (data) => {
                        ctrl.onCancel(data);
                    },
                    () => {
                        Juno.Common.Util.errorAlert($uibModal, 'Error', 'Could not update contacts');
                    });
            };

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
                    function error()
                    {
                        Juno.Common.Util.errorAlert($uibModal, "Error", "Unable to open tab.");
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

            ctrl.validate = function validate()
            {
                if (ctrl.contact.email && (ctrl.contact.email === "" || !ctrl.contact.email.match(/^[^@ ]+@([A-z0-9-]+\.)+[A-z0-9-]+$/)))
		        {
		            ctrl.valid.email = false;
			        return false;
		        }
                if (ctrl.contact.postal && (ctrl.contact.postal === "" || !ctrl.contact.postal.match(/^[A-Za-z]\d[A-Za-z][ -]?\d[A-Za-z]\d$/)))
                {
                    ctrl.valid.postal = false;
			        return false;
                }
                else
                {
                    return true;
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
		    	}

	    	}, true);
        }]
});