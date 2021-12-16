import {
    JUNO_BUTTON_COLOR,
    JUNO_BUTTON_COLOR_PATTERN,
    LABEL_POSITION,
} from "../../../../common/components/junoComponentConstants";

import {DemographicApi} from "../../../../../generated";
import ToastService from "../../../../lib/alerts/service/ToastService";

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
        "$state",
        function (
            $scope,
            $http,
            $httpParamSerializer,
            $state,
        )
        {
            const ctrl = this;
            ctrl.demographicApi = new DemographicApi($http, $httpParamSerializer, "../ws/rs");
            ctrl.EDIT_TITLE = "Edit contact";
            ctrl.NO_EDIT_TITLE = "Editing is currently only available for internal contacts";
            ctrl.toastService = new ToastService();

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
                ctrl.demographic = ctrl.resolve.demographic;
                ctrl.editable = false;

                switch(ctrl.contact.type)
                {
                    case ctrl.types.INTERNAL:
                        ctrl.title = ctrl.EDIT_TITLE;
                        ctrl.contactType = ctrl.typesText.INTERNAL_TEXT;
                        break;
                    case ctrl.types.EXTERNAL:
                        ctrl.title = ctrl.EDIT_TITLE;
                        ctrl.contactType = ctrl.typesText.EXTERNAL_TEXT;
                        break;
                    case ctrl.types.PROVIDER:
                        ctrl.title = ctrl.NO_EDIT_TITLE;
                        ctrl.contactType = ctrl.typesText.PROVIDER_TEXT;
                        break;
                    case ctrl.types.PROFESSIONAL_SPECIALIST:
                        ctrl.title = ctrl.NO_EDIT_TITLE;
                        ctrl.contactType = ctrl.typesText.PROFESSIONAL_SPECIALIST_TEXT;
                }
            };

            ctrl.onCancel = () =>
            {
                ctrl.closeModal();
            };

            ctrl.closeModal = (updatedContact = null) =>
            {
                ctrl.modalInstance.close(updatedContact);
            };


            ctrl.edit = () =>
            {
                if (ctrl.contact.type === ctrl.types.INTERNAL)
                {
                    $state.go("record.details",
                        {
                            demographicNo: ctrl.contact.contactId,
                        });
                    ctrl.closeModal();
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
                    ctrl.toastService.notificationToast('Some fields are invalid, Please correct the highlighted fields');
                    return;
                }

                ctrl.saving = true;
                ctrl.demographicApi.updateExternalContact(ctrl.demographic, ctrl.contact.contactId, ctrl.contact).then(
                    (response) =>
                    {
                        ctrl.closeModal(response.data.body);
                    },
                    () =>
                    {
                        ctrl.toastService.errorToast('Could not update contacts', true);
                    });
            };

            ctrl.resetEditState = function resetEditState()
		    {
			    ctrl.saving = false;
			    ctrl.dataChanged = false;
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