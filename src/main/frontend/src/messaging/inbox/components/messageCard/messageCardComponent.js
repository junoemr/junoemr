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

import {JUNO_STYLE} from "../../../../common/components/junoComponentConstants";

angular.module("Messaging.Components").component('messageCard', {
	templateUrl: 'src/messaging/inbox/components/messageCard/messageCard.jsp',
	bindings: {
		componentStyle: "<?",
		message: "<",
		selected: "<?",
		click: "&?"
	},
	controller: [
		function ()
		{
			const ctrl = this;

			ctrl.$onInit = () =>
			{
				ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
				ctrl.selected = ctrl.selected || false;
			};

			ctrl.formatMessageDate = (date) =>
			{
				return date.format(Juno.Common.Util.settings.message_date_format);
			}

			ctrl.onClick = () =>
			{
				if (ctrl.click)
				{
					ctrl.click({});
				}
			}

		}],
});