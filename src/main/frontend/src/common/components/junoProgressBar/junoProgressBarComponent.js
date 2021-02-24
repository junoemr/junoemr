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

angular.module('Common.Components').component('junoProgressBar',
	{
		templateUrl: 'src/common/components/junoProgressBar/junoProgressBar.jsp',
		bindings: {
			updateCallback: "&", // must return a specific object
			onComplete: "&",
			componentStyle: "<?",
		},
		controller: [
			'$scope',
			'$interval',
			function (
				$scope,
				$interval)
			{
				let ctrl = this;

				// the callback method must return an object with these properties
				ctrl.data = {
					total: 1,
					processed: 0,
					message: "Initializing...",
					complete: false,
				};
				ctrl.pollingPromise = null;

				ctrl.$onInit = () =>
				{
					ctrl.startPolling();
				}

				ctrl.$onDestroy = () =>
				{
					ctrl.stopPolling();
				};

				ctrl.startPolling = () =>
				{
					ctrl.stopPolling();
					if (ctrl.updateCallback)
					{
						ctrl.pollingPromise = $interval(async () =>
						{
							ctrl.data = await ctrl.updateCallback();
							if (ctrl.data.complete)
							{
								ctrl.stopPolling();
								if(ctrl.onComplete)
								{
									ctrl.onComplete();
								}
							}
						}, 1000, 0, true);
					}
				}
				ctrl.stopPolling = () =>
				{
					if(ctrl.pollingPromise)
					{
						$interval.cancel(ctrl.pollingPromise);
						ctrl.pollingPromise = null;
					}
				}

				ctrl.getProgressStyle = () =>
				{
					return {
						width: Math.round((ctrl.data.processed / ctrl.data.total) * 100) +"%",
					}
				}
				ctrl.getProgressLabel = () =>
				{
					return ctrl.data.message + " " + Math.round((ctrl.data.processed / ctrl.data.total) * 100) +"%";
				}

				ctrl.getComponentClasses = () =>
				{
					return [ctrl.componentStyle]
				}
			}]
	});