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
			componentStyle: "<?",
			total: "<",
			processed: "<",
			message: "@?",
		},
		controller: [
			function ()
			{
				let ctrl = this;

				ctrl.getProgressStyle = () =>
				{
					// make sure the values are valid / exist etc.
					let processed = (ctrl.processed && ctrl.processed >= 0) ? ctrl.processed : 0;
					let total = (ctrl.total && ctrl.total > 0) ? ctrl.total : 1;

					return {
						width: Math.round((processed / total) * 100) +"%",
					}
				}

				ctrl.getComponentClasses = () =>
				{
					return [ctrl.componentStyle]
				}
			}]
	});