/*

	Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
	This software is published under the GPL GNU General Public License.
	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

	This software was written for
	CloudPractice Inc.
	Victoria, British Columbia
	Canada

 */

import Demographic from "../lib/demographic/model/Demographic";

angular.module('Patient').component('demographicCard', {
	bindings: {
		demographicModel: '<?',
		disabled: '<?',
	},
	templateUrl: "src/patient/demographicCard.jsp",
	controller: [function ()
	{
		const ctrl = this;

		ctrl.model = {
			demographicNo: null,
			// data stores the raw demographic model
			data: {},
			// display data is built using the raw data model
			displayData: {
				birthDate: null,
				fullName: null,
				addressLine: null,
				hasPhoto: false,
				patientPhotoUrl: '/imageRenderingServlet?source=local_client&clientId=0',
			},
		};
		ctrl.$onInit = function()
		{
			ctrl.fillDisplayData(ctrl.demographicModel);
			ctrl.disabled = ctrl.disabled || false;
		};

		ctrl.$onChanges = function(bindingHash: any)
		{
			// bindingsHash only has data for changed bindings, so check for object existance
			// @ts-ignore
			if(Juno.Common.Util.exists(bindingHash.demographicModel))
			{
				ctrl.fillDisplayData(bindingHash.demographicModel.currentValue);
			}
			// @ts-ignore

			if(Juno.Common.Util.exists(bindingHash.disabled))
			{
				ctrl.disabled = bindingHash.disabled.currentValue;
			}
		};

		ctrl.fillDisplayData = function fillDisplayData(demographicDataModel: Demographic)
		{
			// @ts-ignore
			if(Juno.Common.Util.exists(demographicDataModel))
			{
				ctrl.model.data = demographicDataModel;

				ctrl.model.demographicNo = demographicDataModel.id;
				ctrl.model.displayData.fullName = Juno.Common.Util.formatName(demographicDataModel.firstName, demographicDataModel.lastName);
				ctrl.model.displayData.patientPhotoUrl = '/imageRenderingServlet?source=local_client&clientId=' +
					(demographicDataModel.id ? demographicDataModel.id : 0);
				ctrl.model.displayData.birthDate = demographicDataModel.displayDateOfBirth;

				if (Juno.Common.Util.exists(demographicDataModel.address))
				{
					ctrl.model.displayData.addressLine =
						Juno.Common.Util.noNull(demographicDataModel.address.addressLine1) + ' ' +
						Juno.Common.Util.noNull(demographicDataModel.address.addressLine2) + ' ' +
						Juno.Common.Util.noNull(demographicDataModel.address.city) + ' ' +
						Juno.Common.Util.noNull(demographicDataModel.address.regionCode) + ' ' +
						Juno.Common.Util.noNull(demographicDataModel.address.postalCode);
				}
			}
			else //clear the data model
			{
				ctrl.model = {
					demographicNo: null,
					data: {},
					displayData: {
						birthDate: null,
						fullName: null,
						addressLine: null,
						hasPhoto: false,
						patientPhotoUrl: '/imageRenderingServlet?source=local_client&clientId=0',
					},
				};
			}
		};
	}]
});