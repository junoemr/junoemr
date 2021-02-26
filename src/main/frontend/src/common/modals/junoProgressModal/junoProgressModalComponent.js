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

import {JUNO_STYLE} from "../../components/junoComponentConstants";

angular.module('Common.Components').component('junoProgressModalComponent',
	{
		templateUrl: 'src/common/modals/junoProgressModal/junoProgressModal.jsp',
		bindings: {
			modalInstance: "<",
			resolve: "<",
		},
		controller: [function ()
		{
			let ctrl = this;

			ctrl.total = 1;
			ctrl.processed = 0;
			ctrl.message = "Initializing...";

			ctrl.$onInit = () =>
			{
				ctrl.resolve.style = ctrl.resolve.style || JUNO_STYLE.DEFAULT;
				ctrl.resolve.deferral.promise.then(
					function success(response)
					{
						// wait a little before closing the modal just for visual reasons.
						// this lets the user see the progress bar hit the 100% mark before it disappears
						setTimeout(() =>
						{
							ctrl.modalInstance.close(response);
						}, 1500);
					},
					function failure(error)
					{
						ctrl.modalInstance.dismiss(error);
					},
					function notify(data)
					{
						ctrl.total = data.total;
						ctrl.processed = data.processed;
						ctrl.message = data.message;
					},
				)
			}

			ctrl.getComponentClasses = () =>
			{
				return [ctrl.resolve.style, ctrl.resolve.style + "-background"]
			}
		}]
	});