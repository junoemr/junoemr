/*
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

import MhaConfigService from "../../lib/integration/myhealthaccess/service/MhaConfigService";
import MhaPatientService from "../../lib/integration/myhealthaccess/service/MhaPatientService";
import MessagingServiceFactory from "../../lib/messaging/factory/MessagingServiceFactory";
import {MessagingServiceType} from "../../lib/messaging/model/MessagingServiceType";
import {MessageGroup} from "../../lib/messaging/model/MessageGroup";
import {SecurityPermissions} from "../../common/security/securityConstants";

angular.module('Record.Nav').component('recordNav', {
	templateUrl: 'src/record/nav/recordNav.jsp',
	bindings: {
	},
	controller: [
		'$scope',
		'$state',
		'$stateParams',
		'uxService',
		'securityRolesService',
		function ($scope,
		          $state,
		          $stateParams,
		          uxService,
		          securityRolesService)
	{
		const ctrl = this;

		ctrl.PATIENT_MESSENGER_NAV_ID = 432543;
		ctrl.recordTabs = [];

		ctrl.$onInit = () =>
		{
			ctrl.fillMenu();
		}

		ctrl.fillMenu = () =>
		{
			uxService.menu($stateParams.demographicNo).then(
				function success(results)
				{
					ctrl.recordTabs = results;

					if(securityRolesService.hasSecurityPrivileges(SecurityPermissions.MessageRead))
					{
						ctrl.addMessengerToMenu(ctrl.recordTabs);
					}
				},
				function error(errors)
				{
					console.log(errors);
				});
		};


		/**
		 * load messenger nav item & add it to the menu.
		 * Cannot be done on the backend because checking MHA status in a blocking manner (on the backend)
		 * will slow page load.
		 * @param navItems - the nav item array
		 */
		ctrl.addMessengerToMenu = async (navItems) =>
		{
			const mhaConfigService = new MhaConfigService();
			const mhaPatientService = new MhaPatientService();

			const navItem = {
				id: navItems[navItems.length - 1].id + 1,
				label: "Messenger",
				state: ["record.messaging.view", "record.messaging.view.message"],
				dropdown: true,
				dropdownItems: [],
			};

			// add patient messenger item only if the patient is verified.
			if (await mhaConfigService.mhaEnabled())
			{
				const mhaProfiles = await mhaPatientService.profilesForDemographic($stateParams.demographicNo);
				const verified = mhaProfiles.reduce((verified, profile) => verified || profile.isVerified, false);

				if (verified)
				{
					const verifiedProfile = mhaProfiles.find((profile) => profile.isVerified);
					const messagingService = MessagingServiceFactory.build(MessagingServiceType.MHA_CLINIC);

					navItem.dropdownItems.push({
						id: ctrl.PATIENT_MESSENGER_NAV_ID,
						label: "Patient Messenger",
						popup: false,
						openNewWindow: false,
						custom_state: {
							state: "record.messaging.view",
							params: {
								backend: MessagingServiceType.MHA_CLINIC,
								source: (await messagingService.getDefaultMessageSource()).id,
								group: MessageGroup.Received,
								messageableId: verifiedProfile.id,
								recordPageEmbedded: true,
							}
						},
					})
				}
			}

			navItem.dropdownItems.push({
				id: 1,
				label: "Internal Messenger",
				url: `../oscarMessenger/DisplayDemographicMessages.do?orderby=date&boxType=3&demographic_no=${$stateParams.demographicNo}`
			});

			navItems.push(navItem);
			$scope.$apply();
		}

		ctrl.changeTab = function changeTab(temp)
		{
			ctrl.currenttab2 = ctrl.recordTabs[temp.id];

			if (Juno.Common.Util.isDefinedAndNotNull(temp.state))
			{
				if(Juno.Common.Util.isDefinedAndNotNull(temp.demoId)){
					$state.go(temp.state[0],
						{
							demographicNo: temp.demoId
						});
				}
				else
				{
					$state.go(temp.state[0]);
				}
			}
			else
			{
				switch (temp.id)
				{
					case ctrl.PATIENT_MESSENGER_NAV_ID:
						$state.go(temp.custom_state.state, temp.custom_state.params);
						break;
					default:
						if (angular.isDefined(temp.url))
						{
							var win;
							if (temp.label === "Rx")
							{
								win = temp.label + ctrl.demographicNo;
							}
							else
							{
								var rnd = Math.round(Math.random() * 1000);
								win = "win" + rnd;
							}
							window.open(temp.url, win, "scrollbars=yes, location=no, width=1000, height=600");
						}
						break;
				}
			}
		};

		ctrl.isActive = (tab) =>
		{
			if (Juno.Common.Util.isDefinedAndNotNull($state.current.name) &&
				Juno.Common.Util.isDefinedAndNotNull(tab.state))
			{
				return (tab.state.includes($state.current.name));
			}

			return false;
		};

	}]
});