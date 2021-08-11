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
import {saveAs} from "file-saver";
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN} from "../../../../../../common/components/junoComponentConstants";
import {MessageableLocalType} from "../../../../../../lib/messaging/model/MessageableLocalType";
import RoutingUtil from "../../../../../../lib/util/RoutingUtil";

angular.module("Messaging.Components.View.Components").component('message', {
	templateUrl: 'src/messaging/inbox/components/messageView/components/message/message.jsp',
	bindings: {
		message: "<",
		messagingService: "<",
		compact: "<",
		indentLevel: "<?"
	},
	controller: [
		"$scope",
		"$state",
		function (
			$scope,
			$state)
		{
			const ctrl = this;
			ctrl.demographicMapping = new Map();
			ctrl.loading = false;

			ctrl.$onInit = async () =>
			{
				ctrl.compact = ctrl.compact || false;
				ctrl.indentLevel = ctrl.indentLevel || 0;

				try
				{
					ctrl.loading = true;
					await ctrl.loadDemographicMapping();
				}
				finally
				{
					ctrl.loading = false;
					$scope.$apply();
				}
			};

			ctrl.getComponentClasses = () =>
			{
				return {
					'compact': ctrl.compact,
					'indent-level-1': ctrl.indentLevel === 1,
					'indent-level-2': ctrl.indentLevel === 2,
					'indent-level-3': ctrl.indentLevel >= 3,
				}
			}

			/**
			 * setup the messageable id to demographic id mapping.
			 */
			ctrl.loadDemographicMapping = async () =>
			{
				ctrl.demographicMapping = new Map();
				const messageables = ctrl.message.recipients.concat([ctrl.message.sender]);

				for (const messageable of messageables)
				{
					if (await messageable.hasLocalMapping() && (await messageable.localType()) === MessageableLocalType.DEMOGRAPHIC)
					{
						ctrl.demographicMapping.set(messageable.id, await messageable.localId());
					}
				}
			};

			ctrl.formattedMessageDate = () =>
			{
				return ctrl.message.createdAtDateTime.format(Juno.Common.Util.settings.message_date_long_format);
			};

			ctrl.toDemographicSummary = (demographicNo) =>
			{
				RoutingUtil.goNewTab($state, "record.summary", {
					demographicNo: demographicNo,
				});
			};
		}],
});
