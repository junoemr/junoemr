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
import {FORM_CONTROLLER_STATES} from "../../formsConstants";

angular.module('Record.Forms').component('modeSelectorComponent', {
	templateUrl: 'src/record/forms/components/modeSelector/modeSelector.jsp',
	bindings: {
		modeChange: '&',
		viewState: '<'
	},
	controller: function () {
		let ctrl = this;

		ctrl.FORM_CONTROLLER_STATES = FORM_CONTROLLER_STATES;
		ctrl.onModeChange = function (newMode)
		{
			ctrl.modeChange({mode: newMode});
		};
	}
});