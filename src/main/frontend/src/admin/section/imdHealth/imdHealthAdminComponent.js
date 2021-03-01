/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../../../common/components/junoComponentConstants";
import {IMDHealthApi} from "../../../../generated";

angular.module('Admin.Section').component('imdHealthAdmin',
    {
        templateUrl: 'src/admin/section/imdHealth/imdHealthAdmin.jsp',
        bindings: {},
        controller: ['$scope', '$http', '$httpParamSerializer', '$state', '$uibModal', function ($scope, $http, $httpParamSerializer, $state, $uibModal)
        {
            let ctrl = this;
            let imdHealthWebService = new IMDHealthApi ($http, $httpParamSerializer,
                '../ws/rs');

            ctrl.credentials =
            {
                clientId: "",
                clientSecret: ""
            };

            ctrl.imdIntegrations = [];

            ctrl.LABEL_POSITION = LABEL_POSITION;
            ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
            ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
            ctrl.componentStyle = JUNO_STYLE.GREY;

            ctrl.$onInit = () => {
                ctrl.fetchIntegrations();
            };

            ctrl.onSave = () =>
            {
                console.log("updating credentials: " + ctrl.credentials);
                imdHealthWebService.updateIntegration(ctrl.credentials)
                    .then((response) =>
                    {
                        const result = response.data.body;
                        ctrl.fetchIntegrations();
                    })
                    .catch((error) =>
                    {
                        Juno.Common.Util.errorAlert($uibModal, "Error", "Could not update integration");
                        console.log(error);
                    });

            };


            ctrl.fetchIntegrations = () =>
            {
                imdHealthWebService.getIMDHealthIntegrations()
                    .then((response) =>
                    {
                        ctrl.imdIntegrations = response.data.body;
                        ctrl.credentials.clientId = "";
                        ctrl.credentials.clientSecret = "";
                    })
                    .catch((error) =>
                    {
                        console.log(error);
                    })
            };

            ctrl.removeIntegration = (integrationId) =>
            {
                if (confirm("Are you sure you want to delete this integration?"))
                {
                    imdHealthWebService.deleteIntegration(integrationId)
                        .then((response) =>
                        {
                            ctrl.fetchIntegrations();
                        })
                        .catch((error) =>
                        {
                            Juno.Common.Util.errorAlert($uibModal, "Error", "Could not remove integration");
                            console.log(error);
                        })
                }
            };

             ctrl.syncIntegrations = (integrationId) =>
             {
                  imdHealthWebService.syncIntegrations(integrationId)
                      .then((response ) =>
                      {
                          let failedIntegrations = response.data.body;

                          if (failedIntegrations.length < 1)
                          {
                              Juno.Common.Util.successAlert($uibModal,"Success", "Successfully synced");
                          }
                          else
                          {
                              Juno.Common.Util.errorAlert($uibModal, "Failure", "Could not sync integrations");
                              console.log(failedIntegrations.join(' | '));
                          }
                      })
                      .catch((error) =>
                      {
                        console.log(error);
                      })
             }

            ctrl.testSSO = (integrationId) =>
            {
                imdHealthWebService.testIntegration(integrationId)
                    .then((response) =>
                    {
                        const payload = response.data.body;

                        if (payload)
                        {
                            Juno.Common.Util.successAlert($uibModal,"Success", "This iMD Health integration is valid");
                        }
                        else
                        {
                            Juno.Common.Util.errorAlert($uibModal, "Failure", "This iMD Health integration is invalid");
                        }
                    })
                    .catch((error) =>
                    {
                        console.log(error);
                    })
            }
        }]
    });
