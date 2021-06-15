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

import MhaConfigService from "../../../../../lib/integration/myhealthaccess/service/MhaConfigService";
import {
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN,
	JUNO_STYLE
} from "../../../../../common/components/junoComponentConstants";

angular.module('Admin.Integration').component('integrationList',
	{
		templateUrl: 'src/admin/integration/mhaConfig/components/integrationList/integrationList.jsp',
		bindings: {
			componentStyle: "<?"
		},
		controller: [
			'$scope',
			'$uibModal',
			function (
				$scope,
				$uibModal)
			{
				const ctrl = this;
				const mhaConfigService = new MhaConfigService();

				$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
				$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;

				ctrl.selectedIntegration = null;
				ctrl.integrations = [];

				ctrl.$onInit = async () =>
				{
					ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;

					ctrl.integrations = await mhaConfigService.getMhaIntegrations();
					$scope.$apply();
				}

				ctrl.deleteIntegration = async (integration) =>
				{
					const ok = await Juno.Common.Util.confirmationDialog(
						$uibModal,
						"Delete Integration?",
						`Are you sure you want to delete the MHA integration for site [${integration.siteName}]?`);

					if (ok)
					{
						try
						{
							await mhaConfigService.deleteIntegration(integration);
							ctrl.integrations = ctrl.integrations.filter((integrationItem) => integrationItem.integration !== integration);
							$scope.$apply();
						}
						catch(error)
						{
							Juno.Common.Util.errorAlert($uibModal,
								"Error deleting integration!",
								`Failed to delete integration [${integration.siteName}]`);
							throw error;
						}
					}
				}

			}]
	});