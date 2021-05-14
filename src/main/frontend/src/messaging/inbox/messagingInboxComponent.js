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

import MessagingServiceFactory from "../../lib/messaging/factory/MessagingServiceFactory";
import MessageSource from "../../lib/messaging/model/MessageSource";
import {MessageGroup} from "../../lib/messaging/model/MessageGroup";

angular.module("Messaging").component('messagingInbox', {
	templateUrl: 'src/messaging/inbox/messagingInbox.jsp',
	bindings: {
	},
	controller: [
		"$scope",
		"$stateParams",
		function (
			$scope,
			$stateParams
		)
	{
		let ctrl = this;

		ctrl.backend = $stateParams.backend;
		ctrl.messagingService = MessagingServiceFactory.build(ctrl.backend);

		ctrl.$onInit = async () =>
		{
			ctrl.stream = await ctrl.messagingService.searchMessagesAsStream(new MessageSource("1", "pants"), {});
			console.log(ctrl.stream);
			let read = 10;
			while (read > 0 )
			{
				const start = performance.now();
				read = await ctrl.stream.load(50);
				console.log(performance.now() - start);
				console.log(ctrl.stream);
			}
		}
	}],
});